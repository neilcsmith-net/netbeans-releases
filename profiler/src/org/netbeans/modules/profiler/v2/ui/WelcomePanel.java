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

package org.netbeans.modules.profiler.v2.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.netbeans.lib.profiler.ui.UIUtils;
import org.netbeans.lib.profiler.ui.components.HTMLTextArea;
import org.netbeans.modules.profiler.v2.ProfilerFeature;

/**
 *
 * @author Jiri Sedlacek
 */
public abstract class WelcomePanel extends JPanel {
    
    public WelcomePanel(Set<ProfilerFeature> features) {
        
        Color background = UIUtils.getProfilerResultsBackground();
        
        JPanel pp = new JPanel(new GridBagLayout());
        pp.setOpaque(true);
        pp.setBackground(background);
        
        int y = 0;
        
        HTMLTextArea a1 = new HTMLTextArea("<font size='+1'>Configure Profiling Session</font>");
        a1.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, a1.getForeground()));
        a1.setBackground(background);
        if (UIUtils.isNimbus()) a1.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = y++;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(20, 20, 0, 20);
        pp.add(a1, c);
        
        HTMLTextArea a2 = new HTMLTextArea("Choose the feature to be profiled using the <b><a href='#'>Profile</a></b> dropdown in the above toolbar:") {
            protected void showURL(URL url) { showDropdown(); }
        };
        a2.setBackground(background);
        if (UIUtils.isNimbus()) a2.setOpaque(false);
        c = new GridBagConstraints();
        c.gridy = y++;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(20, 20, 10, 20);
        pp.add(a2, c);
        
        for (ProfilerFeature feature : features) {
        
            JLabel l1 = new JLabel(feature.getName(), feature.getIcon(), JLabel.LEADING);
            l1.setFont(l1.getFont().deriveFont(Font.BOLD));
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = y;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(3, 40, 10, 10);
            pp.add(l1, c);

            JLabel l2 = new JLabel(feature.getDescription());
            l2.setEnabled(false);
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = y++;
            c.gridwidth = 1;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(3, 0, 10, 20);
            pp.add(l2, c);
        
        }
        
        HTMLTextArea a3 = new HTMLTextArea("To profile multiple features simultaneously, select the <b>Profile multiple features</b> choice. Note that profiling <b>Methods</b> and <b>Objects</b> is mutually exclusive.");
        a3.setBackground(background);
        if (UIUtils.isNimbus()) a3.setOpaque(false);
        c = new GridBagConstraints();
        c.gridy = y++;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 20, 0, 20);
        pp.add(a3, c);
        
        HTMLTextArea a4 = new HTMLTextArea("Some of the features can be configured using their settings toolbar. Selected features and their settings will be remembered for successive profiling sessions.");
        a4.setBackground(background);
        if (UIUtils.isNimbus()) a4.setOpaque(false);
        c = new GridBagConstraints();
        c.gridy = y++;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 20, 0, 20);
        pp.add(a4, c);
        
        HTMLTextArea a5 = new HTMLTextArea("To start a new profiling session immediately on invoking <b>Profile Project</b>, select the <b>Start profiling immediately</b> option in <b>Tools | Options | Java | Profiler</b>.");
        a5.setBackground(background);
        if (UIUtils.isNimbus()) a5.setOpaque(false);
        c = new GridBagConstraints();
        c.gridy = y++;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 20, 20, 20);
        pp.add(a5, c);
        
        int w = pp.getMinimumSize().width;
        
        a1.setSize(w, Integer.MAX_VALUE);
        a1.setPreferredSize(new Dimension(w, a1.getPreferredSize().height));
        
        a2.setSize(w, Integer.MAX_VALUE);
        a2.setPreferredSize(new Dimension(w, a2.getPreferredSize().height));
        
        a3.setSize(w, Integer.MAX_VALUE);
        a3.setPreferredSize(new Dimension(w, a3.getPreferredSize().height));
        
        a4.setSize(w, Integer.MAX_VALUE);
        a4.setPreferredSize(new Dimension(w, a4.getPreferredSize().height));
        
        a5.setSize(w, Integer.MAX_VALUE);
        a5.setPreferredSize(new Dimension(w, a5.getPreferredSize().height));
        
        
        setLayout(new GridBagLayout());
        setOpaque(true);
        setBackground(UIUtils.getProfilerResultsBackground());
        GridBagConstraints x = new GridBagConstraints();
        x.gridx = 0;
        x.gridy = 0;
        x.weightx = 1;
        x.weighty = 1;
        x.fill = GridBagConstraints.NONE;
        add(pp, x);
        
    }
    
    public abstract void showDropdown();
    
}