/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.webkit.tooling.networkmonitor;

import java.lang.ref.WeakReference;
import javax.swing.SwingUtilities;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.webkit.debugging.api.network.Network;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

public class NetworkMonitor implements Network.Listener {

    private static WeakReference<NetworkMonitor> lastNetworkMonitor = new WeakReference<>(null);
    
    private final Model model;
    private final Project project;
    private volatile NetworkMonitorTopComponent component;
    private volatile boolean debuggingSession;

    private NetworkMonitor(Lookup projectContext, NetworkMonitorTopComponent comp, boolean debuggingSession) {
        this.component = comp;
        this.model = new Model(projectContext);
        this.debuggingSession = debuggingSession;
        project = projectContext.lookup(Project.class);
        lastNetworkMonitor = new WeakReference<>(this);
    }

    boolean isConnected() {
        return debuggingSession;
    }

    void open() {
        final boolean show = NetworkMonitorTopComponent.canReopenNetworkComponent();
        if (show) {
            // active model if NetworkMonitor is going to be shown:
            model.activate();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (component == null) {
                    component = new NetworkMonitorTopComponent(model, isConnected());
                    if (show) {
                        component.open();
                        component.requestActive();
                    }
                } else {
                    component.setModel(model, isConnected());
                }
            }
        });
    }

    private void resetComponent() {
        this.component = null;
    }

    public static NetworkMonitor createNetworkMonitor(Lookup projectContext) {
        NetworkMonitorTopComponent component = findNetworkMonitorTC();
        // reuse TopComponent if it is open; but always create a new model for new monitoring session
        NetworkMonitor nm = new NetworkMonitor(projectContext, component, true);
        nm.open();
        return nm;
    }

    public static void reopenNetworkMonitor() {
        NetworkMonitorTopComponent component = findNetworkMonitorTC();
        if (component != null) {
            component.requestActive();
        } else {
            NetworkMonitor nm = lastNetworkMonitor.get();
            if (nm != null) {
                // reuse model from last user NetworkMonitor but create a new UI:
                nm.resetComponent();
            } else {
                // open blank NetworkMonitor:
                nm = new NetworkMonitor(Lookup.EMPTY, null, false);
            }
            nm.open();
        }
    }

    private static NetworkMonitorTopComponent findNetworkMonitorTC() {
        for (TopComponent tc : TopComponent.getRegistry().getOpened()) {
            if (tc instanceof NetworkMonitorTopComponent) {
                return (NetworkMonitorTopComponent)tc;
            }
        }
        return null;
    }

    public void close() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                NetworkMonitorTopComponent cmp = component;
                if (cmp != null && cmp.isOpened()) {
                    cmp.close();
                    // reopen automatically NetworkMonitor next time:
                    NetworkMonitorTopComponent.setReopenNetworkComponent(true);
                }
            }
        });
        debuggingSession = false;
    }

    // Implementation of Network.Listener

    @Override
    public void networkRequest(Network.Request request) {
        model.add(request);
        DependentFileQueryImpl.networkRequest(project, request);
    }

    @Override
    public void webSocketRequest(Network.WebSocketRequest request) {
        model.add(request);
    }

}
