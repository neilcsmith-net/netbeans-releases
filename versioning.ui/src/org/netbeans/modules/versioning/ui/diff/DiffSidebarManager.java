/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.versioning.ui.diff;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.modules.editor.errorstripe.privatespi.MarkProvider;
import org.netbeans.modules.versioning.core.util.Utils;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiDataObject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.CookieSet;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.Document;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.openide.util.NbPreferences;

/**
 * Central place of diff integration into editor and errorstripe.
 * 
 * @author Maros Sandor
 */
public class DiffSidebarManager implements PreferenceChangeListener, PropertyChangeListener {

    static final String SIDEBAR_ENABLED = "diff.sidebarEnabled"; // NOI18N

    private static DiffSidebarManager instance;

    /**
     * Temporary top folder for diffsidebar in a single session
     */
    private static File tempDir;
    
    /**
     * Request processor for long running tasks.
     */
    private static final RequestProcessor blockingRequestProcessor = new RequestProcessor("Diffsidebar long tasks", 1, false, false);
    static final Logger LOG = Logger.getLogger(DiffSidebarManager.class.getName());
    private static final boolean LOG_STACKTRACE = Boolean.getBoolean("versioning.diffsidebar.refresh.logstacktrace"); //NOI18N
    
    public static synchronized DiffSidebarManager getInstance() {
        if (instance == null) {
            instance = new DiffSidebarManager();
            Utils.addPropertyChangeListener(instance);
        }
        return instance;
    }

    private boolean sidebarEnabled;

    /**
     * Holds created sidebars (to be able to show/hide them all at once). It is just a set, values are always null.
     */
    private final Map<DiffSidebar, Object> sideBars = new WeakHashMap<DiffSidebar, Object>();

    private DiffSidebarManager() {
        sidebarEnabled = getPreferences().getBoolean(SIDEBAR_ENABLED, true); 
        getPreferences().addPreferenceChangeListener(this);
    }

    public void refreshSidebars(final Set<VCSFileProxy> proxies) {
        // pushing the change ... we may as well listen for changes in versioning manager
        Set<FileObject> fileObjects = null;
        if (proxies != null) {
            fileObjects = new HashSet<FileObject>(proxies.size());
            for (VCSFileProxy file : proxies) {
                fileObjects.add(file.toFileObject());
            }
            fileObjects.remove(null);
        }
        if (LOG_STACKTRACE && (fileObjects == null || !fileObjects.isEmpty())) {
            LOG.log(Level.INFO, "Refreshing: " + fileObjects, new Exception());
        }
        final Set<FileObject> fileObjectsToRefresh = fileObjects;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                synchronized (sideBars) {
                    for (DiffSidebar bar : sideBars.keySet()) {
                        if (matches(bar, fileObjectsToRefresh)) {
                            bar.refresh();
                        }
                    }
                }
            }
        });
    }
        
    private boolean matches(DiffSidebar sidebar, Set<FileObject> fileObjects) {
        if (fileObjects == null) return true;
        for (FileObject fileObject : fileObjects) {
            if (fileObject.equals(sidebar.getFileObject())) return true;
        }
        return false;
    }

    Preferences getPreferences() {
        return NbPreferences.forModule(DiffSidebarManager.class);
    }    
    /**
     * Creates a new task needed by a diff sidebar to update its structures (compute diff). 
     * 
     * @param runnable a runnable task
     * @return RP task
     */
    RequestProcessor.Task createDiffSidebarTask(Runnable runnable) {
        return blockingRequestProcessor.create(runnable);
    }

    JComponent createSideBar(JTextComponent target) {
        return getSideBar(target);
    }

    private DiffSidebar getSideBar(JTextComponent target) {
        synchronized(sideBars) {
            DiffSidebar sideBar = null;
            for (DiffSidebar bar : sideBars.keySet()) {
                if (bar.getTextComponent() == target) {
                    sideBar = bar;
                    break;
                }
            }
            if (sideBar == null) {
                Document doc = target.getDocument();
                FileObject file = fileForDocument(doc);
                if (file == null || !file.isValid()) {
                    LOG.log(Level.FINE, "no valid file");
                    return null;
                }
                LOG.log(Level.FINE, "requested sidebar for {0}", file.getPath());
    
                sideBar = new DiffSidebar(target, file);
                sideBars.put(sideBar, null);
                sideBar.setSidebarVisible(sidebarEnabled);
            }
            return sideBar;
        }
    }

    private FileObject fileForDocument(Document doc) {
        DataObject dobj = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
        LOG.log(Level.FINEST, "document {0} returns {1} for property=" + Document.StreamDescriptionProperty, new Object[] {doc, dobj});
        if (dobj == null) return null;
        LOG.log(Level.FINER, "looking up file for {0}", dobj);
        if (dobj instanceof MultiDataObject) {
            return fileForDataobject(doc, (MultiDataObject) dobj);
        } else if (dobj != null) {
            return dobj.getPrimaryFile();
        } else {
            return null;
        }
    }

    private FileObject fileForDataobject(Document doc, MultiDataObject dobj) {
        for (MultiDataObject.Entry entry : dobj.secondaryEntries()) {
            if (entry instanceof CookieSet.Factory) {
                CookieSet.Factory factory = (CookieSet.Factory) entry;
                EditorCookie ec = factory.createCookie(EditorCookie.class);
                Document entryDocument = ec.getDocument();
                if (entryDocument == doc) {
                    return entry.getFile();
                }
            }
        }
        return dobj.getPrimaryFile();
    }

    private void setSidebarEnabled(boolean enable) {
        synchronized(sideBars) {
            for (DiffSidebar sideBar : sideBars.keySet()) {
                sideBar.setSidebarVisible(enable);
            }
            sidebarEnabled = enable;
        }
    }

    MarkProvider createMarkProvider(JTextComponent target) {
        DiffSidebar sideBar = getSideBar(target);
        return (sideBar != null) ? sideBar.getMarkProvider() : null;
    }

    DiffSidebar t9y_getSidebar() {
        return sideBars.keySet().iterator().next();
    }

    synchronized File getMainTempDir () {
        if (tempDir == null) {
            File tmpDir = new File(System.getProperty("java.io.tmpdir"));   // NOI18N
            for (;;) {
                File dir = new File(tmpDir, "vcs-" + Long.toString(System.currentTimeMillis())); // NOI18N
                if (!dir.exists() && dir.mkdirs()) {
                    tempDir = FileUtil.normalizeFile(dir);
                    tempDir.deleteOnExit();
                    break;
                }
            }
        }
        return tempDir;
    }

    public void preferenceChange(PreferenceChangeEvent evt) {
        if (evt.getKey().equals(SIDEBAR_ENABLED)) {
            setSidebarEnabled(Boolean.valueOf(evt.getNewValue()));
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(Utils.EVENT_STATUS_CHANGED)) {
            refreshSidebars((Set<VCSFileProxy>)evt.getNewValue());
        }
    }
}
