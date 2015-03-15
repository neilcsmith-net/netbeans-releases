/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2014 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.modules.profiler.v2.features;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import org.netbeans.lib.profiler.common.Profiler;
import org.netbeans.lib.profiler.ui.components.ProfilerToolbar;
import org.netbeans.lib.profiler.ui.monitor.MonitorView;
import org.netbeans.lib.profiler.ui.swing.GrayLabel;
import org.netbeans.modules.profiler.api.icons.GeneralIcons;
import org.netbeans.modules.profiler.api.icons.Icons;
import org.openide.util.NbBundle;

/**
 *
 * @author Jiri Sedlacek
 */
@NbBundle.Messages({
    "MonitorFeatureUI_graphs=Graphs:"
})
abstract class MonitorFeatureUI extends FeatureUI {
    
    private ProfilerToolbar toolbar;
    private MonitorView monitorView;
    
    
    // --- External implementation ---------------------------------------------
    
    abstract Profiler getProfiler();
    
    
    // --- API implementation --------------------------------------------------
    
    ProfilerToolbar getToolbar() {
        if (toolbar == null) initUI();
        return toolbar;
    }

    JPanel getResultsUI() {
        if (monitorView == null) initUI();
        return monitorView;
    }
    
    
    void sessionStateChanged(int sessionState) {
        refreshToolbar(sessionState);
    }
    
    
    // --- UI ------------------------------------------------------------------
    
    private JLabel grLabel;
    private JButton grZoomInButton;
    private JButton grZoomOutButton;
    private JToggleButton grFitWidthButton;
    
    
    private void initUI() {
        
        assert SwingUtilities.isEventDispatchThread();
        
        // --- Results ---------------------------------------------------------
        
        monitorView = new MonitorView(getProfiler().getVMTelemetryManager());
        
        monitorView.putClientProperty("HelpCtx.Key", "ProfileTelemetry.HelpCtx"); // NOI18N
        
        
        // --- Toolbar ---------------------------------------------------------
        
        grLabel = new GrayLabel(Bundle.MonitorFeatureUI_graphs());
            
        grZoomInButton = new JButton(Icons.getIcon(GeneralIcons.ZOOM_IN));
        grZoomInButton.setEnabled(false);

        grZoomOutButton = new JButton(Icons.getIcon(GeneralIcons.ZOOM_OUT));
        grZoomOutButton.setEnabled(false);

        grFitWidthButton = new JToggleButton(Icons.getIcon(GeneralIcons.SCALE_TO_FIT));
        grFitWidthButton.setEnabled(false);

        toolbar = ProfilerToolbar.create(true);

        toolbar.addSpace(2);
        toolbar.addSeparator();
        toolbar.addSpace(5);

        toolbar.add(grLabel);
        toolbar.addSpace(2);
        toolbar.add(grZoomInButton);
        toolbar.add(grZoomOutButton);
        toolbar.add(grFitWidthButton);
        
        
        // --- Sync UI ---------------------------------------------------------
        
        sessionStateChanged(getSessionState());
        
    }
    
    private void refreshToolbar(final int state) {
//        if (toolbar != null) SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//            }
//        });
    }
    
}
