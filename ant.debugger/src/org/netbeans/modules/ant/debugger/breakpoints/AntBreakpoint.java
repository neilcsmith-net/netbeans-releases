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

package org.netbeans.modules.ant.debugger.breakpoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerAdapter;
import org.netbeans.api.debugger.DebuggerManagerListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.ant.debugger.AntDebugger;
import org.openide.filesystems.FileObject;
import org.openide.text.Line;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;



/**
 *
 * @author  Honza
 */
public class AntBreakpoint extends Breakpoint {

    private boolean enabled = true;
    private Line    line;


    AntBreakpoint (Line line) {
        this.line = line;
    }

    public Line getLine () {
        return line;
    }

    /**
     * Test whether the breakpoint is enabled.
     *
     * @return <code>true</code> if so
     */
    @Override
    public boolean isEnabled () {
        return enabled;
    }
    
    /**
     * Disables the breakpoint.
     */
    @Override
    public void disable () {
        if (!enabled) return;
        enabled = false;
        firePropertyChange (PROP_ENABLED, Boolean.TRUE, Boolean.FALSE);
    }
    
    /**
     * Enables the breakpoint.
     */
    @Override
    public void enable () {
        if (enabled) return;
        enabled = true;
        firePropertyChange (PROP_ENABLED, Boolean.FALSE, Boolean.TRUE);
    }

    @Override
    public GroupProperties getGroupProperties() {
        return new AntGroupProperties();
    }

    private final class AntGroupProperties extends GroupProperties {

        private AntEngineListener engineListener;

        @Override
        public String getLanguage() {
            return "ANT";
        }

        @Override
        public String getType() {
            return NbBundle.getMessage(AntBreakpoint.class, "LineBrkp_Type");
        }

        private FileObject getFile() {
            return line.getLookup().lookup(FileObject.class);
        }

        @Override
        public FileObject[] getFiles() {
            FileObject fo = getFile();
            if (fo != null) {
                return new FileObject[] { fo };
            } else {
                return null;
            }
        }

        @Override
        public Project[] getProjects() {
            FileObject f = getFile();
            while (f != null) {
                f = f.getParent();
                if (f != null && ProjectManager.getDefault().isProject(f)) {
                    break;
                }
            }
            if (f != null) {
                try {
                    return new Project[] { ProjectManager.getDefault().findProject(f) };
                } catch (IOException ex) {
                } catch (IllegalArgumentException ex) {
                }
            }
            return null;
        }

        @Override
        public DebuggerEngine[] getEngines() {
            if (engineListener == null) {
                engineListener = new AntEngineListener();
                DebuggerManager.getDebuggerManager().addDebuggerListener(
                        WeakListeners.create(DebuggerManagerListener.class,
                                             engineListener,
                                             DebuggerManager.getDebuggerManager()));
            }
            DebuggerEngine[] engines = DebuggerManager.getDebuggerManager().getDebuggerEngines();
            if (engines.length == 0) {
                return null;
            }
            if (engines.length == 1) {
                if (isAntEngine(engines[0])) {
                    return engines;
                } else {
                    return null;
                }
            }
            // Several running sessions
            List<DebuggerEngine> antEngines = null;
            for (DebuggerEngine e : engines) {
                if (isAntEngine(e)) {
                    if (antEngines == null) {
                        antEngines = new ArrayList<DebuggerEngine>();
                    }
                    antEngines.add(e);
                }
            }
            if (antEngines == null) {
                return null;
            } else {
                return antEngines.toArray(new DebuggerEngine[]{});
            }
        }

        private boolean isAntEngine(DebuggerEngine e) {
            return e.lookupFirst(null, AntDebugger.class) != null;
        }

        @Override
        public boolean isHidden() {
            return false;
        }

        private final class AntEngineListener extends DebuggerManagerAdapter {

            @Override
            public void engineAdded(DebuggerEngine engine) {
                if (isAntEngine(engine)) {
                    firePropertyChange(PROP_GROUP_PROPERTIES, null, AntGroupProperties.this);
                }
            }

            @Override
            public void engineRemoved(DebuggerEngine engine) {
                if (isAntEngine(engine)) {
                    firePropertyChange(PROP_GROUP_PROPERTIES, null, AntGroupProperties.this);
                }
            }

        }
        
    }
    
}
