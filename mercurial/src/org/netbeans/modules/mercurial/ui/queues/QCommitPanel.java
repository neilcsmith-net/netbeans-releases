/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.mercurial.ui.queues;

import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import org.netbeans.modules.mercurial.FileInformation;
import org.netbeans.modules.mercurial.FileStatusCache;
import org.netbeans.modules.mercurial.HgException;
import org.netbeans.modules.mercurial.HgModuleConfig;
import org.netbeans.modules.mercurial.HgProgressSupport;
import org.netbeans.modules.mercurial.Mercurial;
import org.netbeans.modules.mercurial.ui.diff.MultiDiffPanel;
import org.netbeans.modules.mercurial.ui.log.HgLogMessage;
import org.netbeans.modules.mercurial.ui.log.HgLogMessage.HgRevision;
import org.netbeans.modules.mercurial.util.HgCommand;
import org.netbeans.modules.mercurial.util.HgUtils;
import org.netbeans.modules.versioning.hooks.HgQueueHook;
import org.netbeans.modules.versioning.hooks.HgQueueHookContext;
import org.netbeans.modules.versioning.hooks.VCSHookContext;
import org.netbeans.modules.versioning.hooks.VCSHooks;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.versioning.util.Utils;
import org.netbeans.modules.versioning.util.common.VCSCommitPanelModifier;
import org.netbeans.modules.versioning.util.common.VCSCommitDiffProvider;
import org.netbeans.modules.versioning.util.common.VCSCommitFilter;
import org.netbeans.modules.versioning.util.common.VCSCommitPanel;
import org.netbeans.modules.versioning.util.common.VCSCommitParameters.DefaultCommitParameters;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;

/**
 *
 * @author Tomas Stupka
 */
public class QCommitPanel extends VCSCommitPanel<QFileNode> {

    private final Collection<HgQueueHook> hooks;
    private final File[] roots;
    private final File repository;
    private final NodesProvider nodesProvider;
    private final HelpCtx helpCtx;

    private QCommitPanel(QCommitTable table, final File[] roots, final File repository, DefaultCommitParameters parameters, Preferences preferences, Collection<HgQueueHook> hooks, 
            VCSHookContext hooksContext, VCSCommitDiffProvider diffProvider, NodesProvider nodesProvider, HelpCtx helpCtx) {
        super(table, parameters, preferences, hooks, hooksContext, Collections.<VCSCommitFilter>emptyList(), diffProvider);
        this.roots = roots;
        this.repository = repository;
        this.hooks = hooks;
        this.nodesProvider = nodesProvider;
        this.helpCtx = helpCtx;
    }

    public static QCommitPanel createNewPanel (final File[] roots, final File repository, String commitMessage, String helpCtxId) {
        Preferences preferences = HgModuleConfig.getDefault().getPreferences();
        
        DefaultCommitParameters parameters = new QCreatePatchParameters(preferences, commitMessage, null); //NOI18N
        
        Collection<HgQueueHook> hooks = VCSHooks.getInstance().getHooks(HgQueueHook.class);
        HgQueueHookContext hooksCtx = new HgQueueHookContext(roots, null, null);
        
        DiffProvider diffProvider = new DiffProvider();
        VCSCommitPanelModifier modifier = RefreshPanelModifier.getDefault("create"); //NOI18N
        return new QCommitPanel(new QCommitTable(modifier), roots, repository, parameters, preferences, hooks, hooksCtx, diffProvider, new ModifiedNodesProvider(),
                new HelpCtx(helpCtxId));
    }

    public static QCommitPanel createRefreshPanel (final File[] roots, final File repository, String commitMessage, QPatch patch, HgRevision parentRevision, String helpCtxId) {
        Preferences preferences = HgModuleConfig.getDefault().getPreferences();
        
        DefaultCommitParameters parameters = new QCreatePatchParameters(preferences, commitMessage, patch); //NOI18N
        
        Collection<HgQueueHook> hooks = VCSHooks.getInstance().getHooks(HgQueueHook.class);
        HgQueueHookContext hooksCtx = new HgQueueHookContext(roots, null, patch.getId());
        
        // own diff provider, displays qdiff instead of regular diff
        DiffProvider diffProvider = new QDiffProvider(parentRevision);
        VCSCommitPanelModifier msgProvider = RefreshPanelModifier.getDefault("refresh"); //NOI18N
        // own node computer, displays files not modified in cache but files returned by qdiff
        return new QCommitPanel(new QCommitTable(msgProvider), roots, repository, parameters, preferences, hooks, hooksCtx, diffProvider, new QRefreshNodesProvider(parentRevision),
                new HelpCtx(helpCtxId));
    }
    
    @Override
    public QCreatePatchParameters getParameters() {
        return (QCreatePatchParameters) super.getParameters();
    }

    public Collection<HgQueueHook> getHooks() {
        return hooks;
    }

    @Override
    protected void computeNodes() {      
        computeNodesIntern();
    }
    
    HelpCtx getHelpContext () {
        return helpCtx;
    }

    @Override
    public boolean open (VCSContext context, HelpCtx helpCtx) {
        // synchronize access to this static field
        assert EventQueue.isDispatchThread();
        boolean ok = super.open(context, helpCtx);
        HgProgressSupport supp = support;
        if (supp != null) {
            supp.cancel();
        }
        return ok;
    }
    
    /** used by unit tests */
    HgProgressSupport support;
    RequestProcessor.Task computeNodesIntern() {      
        final boolean refreshFinnished[] = new boolean[] { false };
        RequestProcessor rp = Mercurial.getInstance().getRequestProcessor(repository);

        HgProgressSupport supp = this.support;
        if (supp != null) {
            supp.cancel();
        }
        support = getProgressSupport(refreshFinnished);
        final String preparingMessage = NbBundle.getMessage(QCommitPanel.class, "Progress_Preparing_Commit"); //NOI18N
        setupProgress(preparingMessage, support.getProgressComponent());
        Task task = support.start(rp, repository, preparingMessage);
        
        // do not show progress in dialog if task finnished early        
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!(refreshFinnished[0])) {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            showProgress();                            
                        }
                    });                     
                }
            }
        }, 1000);
        return task;
    }

    // merge-type commit dialog can hook into this method
    protected HgProgressSupport getProgressSupport (final boolean[] refreshFinished) {
        return new HgProgressSupport() {
            @Override
            public void perform() {
                try {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            getCommitTable().setNodes(new QFileNode[0]);
                        }
                    });
                    final QFileNode[] nodes = nodesProvider.getNodes(repository, roots, refreshFinished);
                    if (nodes != null) {
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                getCommitTable().setNodes(nodes);
                            }
                        });
                    }
                } finally {
                    refreshFinished[0] = true;
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            stopProgress();
                        }
                    });
                }
            }
        };
    }

    private static class DiffProvider extends VCSCommitDiffProvider {

        final Map<File, MultiDiffPanel> panels = new HashMap<File, MultiDiffPanel>();

        @Override
        public Set<File> getModifiedFiles () {
            return getSaveCookiesPerFile().keySet();
        }

        private Map<File, SaveCookie> getSaveCookiesPerFile () {
            Map<File, SaveCookie> modifiedFiles = new HashMap<File, SaveCookie>();
            for (Map.Entry<File, MultiDiffPanel> e : panels.entrySet()) {
                SaveCookie[] cookies = e.getValue().getSaveCookies(false);
                if (cookies.length > 0) {
                    modifiedFiles.put(e.getKey(), cookies[0]);
                }
            }
            return modifiedFiles;
        }

        @Override
        public JComponent createDiffComponent (File file) {
            MultiDiffPanel panel = new MultiDiffPanel(file, HgRevision.BASE, HgRevision.CURRENT, false);
            panels.put(file, panel);
            return panel;
        }

        /**
         * Returns save cookies available for files in the commit table
         * @return
         */
        @Override
        protected SaveCookie[] getSaveCookies () {
            return getSaveCookiesPerFile().values().toArray(new SaveCookie[0]);
        }

        /**
         * Returns editor cookies available for modified and not open files in the commit table
         * @return
         */
        @Override
        protected EditorCookie[] getEditorCookies () {
            LinkedList<EditorCookie> allCookies = new LinkedList<EditorCookie>();
            for (Map.Entry<File, MultiDiffPanel> e : panels.entrySet()) {
                EditorCookie[] cookies = e.getValue().getEditorCookies(true);
                if (cookies.length > 0) {
                    allCookies.add(cookies[0]);
                }
            }
            return allCookies.toArray(new EditorCookie[allCookies.size()]);
        }        
    }
    
    private static class QDiffProvider extends DiffProvider {
        private final HgRevision parent;
        
        QDiffProvider (HgRevision parent) {
            this.parent = parent;
        }
        
        @Override
        public JComponent createDiffComponent (File file) {
            MultiDiffPanel panel = new MultiDiffPanel(file, parent, HgRevision.CURRENT, false);
            panels.put(file, panel);
            return panel;
        }
    }
    
    private static interface NodesProvider {
        QFileNode[] getNodes (File repository, File[] roots, boolean[] refreshFinished);
    }
    
    /**
     * Used in qnew panel, provides files modified in cache
     */
    private static final class ModifiedNodesProvider implements NodesProvider {

        @Override
        public QFileNode[] getNodes (File repository, File[] roots, boolean[] refreshFinished) {
            // Ensure that cache is uptodate
            FileStatusCache cache = Mercurial.getInstance().getFileStatusCache();
            cache.refreshAllRoots(Collections.<File, Set<File>>singletonMap(repository, new HashSet<File>(Arrays.asList(roots))));
            // the realy time consuming part is over;
            // no need to show the progress component,
            // which only makes the dialog flicker
            refreshFinished[0] = true;
            File[][] split = Utils.splitFlatOthers(roots);
            List<File> fileList = new ArrayList<File>();
            for (int c = 0; c < split.length; c++) {
                File[] splitRoots = split[c];
                boolean recursive = c == 1;
                if (recursive) {
                    File[] files = cache.listFiles(splitRoots, FileInformation.STATUS_LOCAL_CHANGE);
                    for (int i = 0; i < files.length; i++) {
                        for(int r = 0; r < splitRoots.length; r++) {
                            if(Utils.isAncestorOrEqual(splitRoots[r], files[i]))
                            {
                                if(!fileList.contains(files[i])) {
                                    fileList.add(files[i]);
                                }
                            }
                        }
                    }
                } else {
                    File[] files = HgUtils.flatten(splitRoots, FileInformation.STATUS_LOCAL_CHANGE);
                    for (int i= 0; i<files.length; i++) {
                        if(!fileList.contains(files[i])) {
                            fileList.add(files[i]);
                        }
                    }
                }
            }
            if(fileList.isEmpty()) {
                return null;
            }

            ArrayList<QFileNode> nodesList = new ArrayList<QFileNode>(fileList.size());

            for (Iterator<File> it = fileList.iterator(); it.hasNext();) {
                File file = it.next();
                QFileNode node = new QFileNode(repository, file);
                nodesList.add(node);
            }
            return nodesList.toArray(new QFileNode[fileList.size()]);
        }
        
    }

    /**
     * Used in qrefresh panel, provides also files that are already part of a patch
     * So it displays:
     * - all local modifications against the WC parent (qtip)
     * - plus all files already in the patch, i.e. difference between qtip and parent revision
     */
    private static final class QRefreshNodesProvider implements NodesProvider {
        private final HgRevision parent;

        private QRefreshNodesProvider (HgRevision parentRevision) {
            this.parent = parentRevision;
        }

        @Override
        public QFileNode[] getNodes (File repository, File[] roots, boolean[] refreshFinished) {
            try {
                if (parent != null && parent != HgLogMessage.HgRevision.EMPTY) {
                    Map<File, FileInformation> patchChanges = HgCommand.getStatus(repository, Collections.singletonList(repository), parent.getRevisionNumber(), QPatch.TAG_QTIP);
                    FileStatusCache cache = Mercurial.getInstance().getFileStatusCache();
                    Set<File> toRefresh = new HashSet<File>(Arrays.asList(roots));
                    toRefresh.addAll(patchChanges.keySet());
                    cache.refreshAllRoots(Collections.<File, Set<File>>singletonMap(repository, toRefresh));
                    
                    Map<File, FileInformation> statuses = getLocalChanges(roots, cache);
                    statuses.keySet().retainAll(HgUtils.flattenFiles(roots, statuses.keySet()));
                    Set<File> patchChangesUnderSelection = getPatchChangesUnderSelection(patchChanges, roots);
                    
                    for (Map.Entry<File, FileInformation> e : patchChanges.entrySet()) {
                        if (patchChangesUnderSelection.contains(e.getKey())) {
                            if (!statuses.containsKey(e.getKey())) {
                                statuses.put(e.getKey(), new FileInformation(FileInformation.STATUS_VERSIONED_UPTODATE, null, false));
                            }
                        } else {
                            FileInformation info = cache.getCachedStatus(e.getKey());
                            if ((info.getStatus() & FileInformation.STATUS_LOCAL_CHANGE) != 0) {
                                statuses.put(e.getKey(), info);
                            }
                        }
                    }

                    refreshFinished[0] = true;

                    if(statuses.isEmpty()) {
                        return null;
                    }

                    ArrayList<QFileNode> nodesList = new ArrayList<QFileNode>(statuses.size());
                    for (Map.Entry<File, FileInformation> e : statuses.entrySet()) {
                        File f = e.getKey();
                        FileInformation fi = e.getValue();
                        if ((fi.getStatus() & FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY) != 0 && HgUtils.isIgnored(f)) {
                            // do not include not sharable files
                            continue;
                        }
                        QFileNode node = new QFileNode(repository, f, fi);
                        nodesList.add(node);
                    }
                    return nodesList.toArray(new QFileNode[nodesList.size()]);
                }
            } catch (HgException.HgCommandCanceledException ex) {
                //
            } catch (HgException ex) {
                Mercurial.LOG.log(Level.INFO, null, ex);
            }
            return null;
        }

        // should contain only patch changes that apply to current selection
        private Set<File> getPatchChangesUnderSelection(Map<File, FileInformation> patchChanges, File[] roots) {
            Set<File> patchChangesUnderSelection = new HashSet<File>(patchChanges.keySet());
            for (Iterator<File> it = patchChangesUnderSelection.iterator(); it.hasNext(); ) {
                File f = it.next();
                boolean isUnderRoots = false;
                for (File root : roots) {
                    if (Utils.isAncestorOrEqual(root, f)) {
                        isUnderRoots = true;
                        break;
                    }
                }
                if (!isUnderRoots) {
                    it.remove();
                }
            }
            patchChangesUnderSelection = HgUtils.flattenFiles(roots, patchChangesUnderSelection);
            return patchChangesUnderSelection;
        }

        private Map<File, FileInformation> getLocalChanges (File[] roots, FileStatusCache cache) {
            File[] files = cache.listFiles(roots, FileInformation.STATUS_LOCAL_CHANGE);
            Map<File, FileInformation> retval = new HashMap<File, FileInformation>(files.length);
            for (File file : files) {
                retval.put(file, cache.getCachedStatus(file));
            }
            return retval;
        }
        
    }
}
