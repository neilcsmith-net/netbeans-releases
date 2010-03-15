/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.debugger.jpda.breakpoints;

import java.beans.PropertyChangeEvent;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.netbeans.api.debugger.Breakpoint;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerListener;
import org.netbeans.api.debugger.LazyDebuggerManagerListener;
import org.netbeans.api.debugger.Properties;
import org.netbeans.api.debugger.Properties.Reader;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.Watch;
import org.netbeans.api.debugger.jpda.JPDABreakpoint;
import org.netbeans.api.debugger.jpda.LineBreakpoint;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.util.RequestProcessor;

/**
 * Listens on DebuggerManager and:
 * - loads all breakpoints. Watches are loaded by debuggercore's PersistentManager.
 * - listens on all changes of breakpoints and saves new values
 *
 * @author Jan Jancura
 */
public class PersistenceManager implements LazyDebuggerManagerListener {

    private static Reference<PersistenceManager> instanceRef = new WeakReference<PersistenceManager>(null);

    private Breakpoint[] breakpoints;
    private RequestProcessor.Task saveTask;

    public PersistenceManager() {
        instanceRef = new WeakReference<PersistenceManager>(this);
    }
    
    public synchronized Breakpoint[] initBreakpoints () {
        Properties p = Properties.getDefault ().getProperties ("debugger").
            getProperties (DebuggerManager.PROP_BREAKPOINTS);
        Breakpoint[] breakpoints = (Breakpoint[]) p.getArray (
            "jpda", 
            new Breakpoint [0]
        );
        for (int i = 0; i < breakpoints.length; i++) {
            if (breakpoints[i] instanceof LineBreakpoint) {
                LineBreakpoint lb = (LineBreakpoint) breakpoints[i];
                try {
                    FileObject fo = URLMapper.findFileObject(new URL(lb.getURL()));
                    if (fo == null) {
                        // The file is gone - we should remove the breakpoint as well.
                        Breakpoint[] breakpoints2 = new Breakpoint[breakpoints.length - 1];
                        if (i > 0) {
                            System.arraycopy(breakpoints, 0, breakpoints2, 0, i);
                        }
                        if (i < breakpoints2.length) {
                            System.arraycopy(breakpoints, i + 1, breakpoints2, i, breakpoints2.length - i);
                        }
                        breakpoints = breakpoints2;
                        i--;
                        continue;
                    }
                } catch (MalformedURLException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
            breakpoints[i].addPropertyChangeListener(this);
        }
        this.breakpoints = breakpoints;
        return breakpoints;
    }
    
    public synchronized Breakpoint[] unloadBreakpoints() {
        Breakpoint[] bpts = DebuggerManager.getDebuggerManager().getBreakpoints();
        ArrayList<Breakpoint> unloaded = new ArrayList<Breakpoint>();
        for (Breakpoint b : bpts) {
            if (b instanceof JPDABreakpoint) {
                unloaded.add(b);
                b.removePropertyChangeListener(this);
            }
        }
        this.breakpoints = null;
        return unloaded.toArray(new Breakpoint[0]);
    }
    
    public void initWatches () {
    }
    
    public String[] getProperties () {
        return new String [] {
            DebuggerManager.PROP_BREAKPOINTS_INIT,
            DebuggerManager.PROP_BREAKPOINTS,
        };
    }
    
    public void breakpointAdded (Breakpoint breakpoint) {
        if (breakpoint instanceof JPDABreakpoint &&
                !((JPDABreakpoint) breakpoint).isHidden ()) {
            synchronized (this) {
                if (breakpoints == null) {
                    return ;
                }
                int n = breakpoints.length;
                Breakpoint[] newBreakpoints = new Breakpoint[n + 1];
                System.arraycopy(breakpoints, 0, newBreakpoints, 0, n);
                newBreakpoints[n] = breakpoint;
                breakpoints = newBreakpoints;
                storeTheBreakpoints();
            }
            breakpoint.addPropertyChangeListener(this);
        }
    }

    public void breakpointRemoved (Breakpoint breakpoint) {
        if (breakpoint instanceof JPDABreakpoint &&
                !((JPDABreakpoint) breakpoint).isHidden ()) {
            synchronized (this) {
                if (breakpoints == null) {
                    return ;
                }
                int n = breakpoints.length;
                for (int i = 0; i < n; i++) {
                    if (breakpoints[i] == breakpoint) {
                        Breakpoint[] newBreakpoints = new Breakpoint[n - 1];
                        if (i > 0) {
                            System.arraycopy(breakpoints, 0, newBreakpoints, 0, i);
                        }
                        if (i < (n-1)) {
                            System.arraycopy(breakpoints, i+1, newBreakpoints, i, n - 1 - i);
                        }
                        n--;
                        breakpoints = newBreakpoints;
                    }
                }
                storeTheBreakpoints();
            }
            breakpoint.removePropertyChangeListener(this);
        }
    }
    public void watchAdded (Watch watch) {
    }
    
    public void watchRemoved (Watch watch) {
    }
    
    public void propertyChange (PropertyChangeEvent evt) {
        if (evt.getSource() instanceof JPDABreakpoint) {
            if (LineBreakpoint.PROP_LINE_NUMBER.equals(evt.getPropertyName())) {
                BreakpointsReader r = findBreakpointsReader();
                if (r != null) {
                    // Reset the class name, which might change
                    r.storeCachedClassName((JPDABreakpoint) evt.getSource(), null);
                }
            }
            if (Breakpoint.PROP_VALIDITY.equals(evt.getPropertyName())) {
                return ;
            }
            storeTheBreakpoints();
        }
    }
    
    static BreakpointsReader findBreakpointsReader() {
        BreakpointsReader breakpointsReader = null;
        Iterator i = DebuggerManager.getDebuggerManager().lookup (null, Reader.class).iterator ();
        while (i.hasNext ()) {
            Reader r = (Reader) i.next ();
            String[] ns = r.getSupportedClassNames ();
            if (ns.length == 1 && JPDABreakpoint.class.getName().equals(ns[0])) {
                breakpointsReader = (BreakpointsReader) r;
                break;
            }
        }
        return breakpointsReader;
    }

    static void storeBreakpoints() {
        PersistenceManager pm = instanceRef.get();
        if (pm != null) {
            pm.storeTheBreakpoints();
        }
    }

    private synchronized void storeTheBreakpoints() {
        if (saveTask == null) {
            saveTask = new RequestProcessor("Debugger JPDA Breakpoints storage", 1).create(new SaveTask());
        }
        saveTask.schedule(100);
    }
    
    public void sessionAdded (Session session) {}
    public void sessionRemoved (Session session) {}
    public void engineAdded (DebuggerEngine engine) {}
    public void engineRemoved (DebuggerEngine engine) {}
    
    
    private final class SaveTask implements Runnable {

        @Override
        public void run() {
            synchronized (PersistenceManager.this) {
                if (breakpoints == null) {
                    return ;
                }
                Properties.getDefault ().getProperties ("debugger").
                    getProperties (DebuggerManager.PROP_BREAKPOINTS).setArray (
                        "jpda",
                        breakpoints
                    );
            }
        }
        
    }

}
