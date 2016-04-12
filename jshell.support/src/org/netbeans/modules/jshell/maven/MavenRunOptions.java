/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.jshell.maven;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ChangeEvent;
import org.netbeans.api.project.Project;
import org.netbeans.modules.jshell.project.JShellOptions2;
import org.netbeans.modules.maven.api.customizer.ModelHandle2;
import org.netbeans.modules.maven.execute.model.ActionToGoalMapping;
import org.netbeans.modules.maven.execute.model.NetbeansActionMapping;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.HelpCtx;

/**
 *
 * @author sdedic
 */
public class MavenRunOptions extends javax.swing.JPanel implements HelpCtx.Provider {
    private final Project       project;
    private final ModelHandle2  handle;
    private final Category      category;
    private JShellOptions2      nestedOptions;
    
    private NetbeansActionMapping run;
    private NetbeansActionMapping debug;
    /**
     * Creates new form MavenRunOptions
     */
    public MavenRunOptions(Project project, Category category, ModelHandle2 handle) {
        this.project = project;
        this.category = category;
        this.handle = handle;
        
        initComponents();

        cbConfiguration.setEditable(false);
        cbConfiguration.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component com = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (com instanceof JLabel) {
                    if (value == handle.getActiveConfiguration()) {
                        com.setFont(com.getFont().deriveFont(Font.BOLD));
                    }
                }
                return com;
            }
        });
        
        nestedOptions = (JShellOptions2)pOptions;
        
        setupConfigurations();
        
        loadOptions(null);
        
        nestedOptions.setConfigChangeListener(this::optionsChanged);
        cbConfiguration.addActionListener(this::loadOptions);
    }
    
    private void loadOptions(ActionEvent e) {
        ActionToGoalMapping mapp = handle.getActionMappings((ModelHandle2.Configuration) cbConfiguration.getSelectedItem());
        List<NetbeansActionMapping> lst = mapp.getActions();
        for (NetbeansActionMapping m : lst) {
            if (ActionProvider.COMMAND_RUN.equals(m.getActionName())) {
                run = m;
            }
            if (ActionProvider.COMMAND_DEBUG.equals(m.getActionName())) {
                debug = m;
            }
        }
        if (run == null) {
            run = ModelHandle2.getDefaultMapping(ActionProvider.COMMAND_RUN, project);
        }
        if (debug == null) {
            debug = ModelHandle2.getDefaultMapping(ActionProvider.COMMAND_DEBUG, project);
        }
        nestedOptions.readOptions(run.getProperties());
    }
    
    private boolean updateMessage() {
         if (!nestedOptions.isPanelValid()) {
            category.setErrorMessage(nestedOptions.getErrorMessage());
            return true;
        } else {
            category.setErrorMessage(null);
            return false;
         }
    }
    
    private void optionsChanged(ChangeEvent e) {
        if (updateMessage()) {
            return;
        }
        ActionToGoalMapping a2gm = handle.getActionMappings((ModelHandle2.Configuration) cbConfiguration.getSelectedItem());
        changeConfiguration(run, a2gm);
        changeConfiguration(debug, a2gm);
        handle.markAsModified(a2gm);
    }
    
    private void changeConfiguration(NetbeansActionMapping am, ActionToGoalMapping a2gm) {
        if (am == null) {
            return;
        }
        Map<String, String> opts = nestedOptions.getChangedOptions();
        for (String k : opts.keySet()) {
            String v = opts.get(k);
            if (v != null) {
                am.addProperty(k, v);
            } else {
                am.getProperties().remove(k);
            }
        }
        ModelHandle2.setUserActionMapping(am, a2gm);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("maven_jshell_run");
    }

    private void setupConfigurations() {
        DefaultComboBoxModel comModel = new DefaultComboBoxModel();
        for (ModelHandle2.Configuration conf : handle.getConfigurations()) {
            comModel.addElement(conf);
        }
        cbConfiguration.setModel(comModel);
        cbConfiguration.setSelectedItem(handle.getActiveConfiguration());
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cbConfiguration = new javax.swing.JComboBox();
        pOptions = new JShellOptions2(project);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MavenRunOptions.class, "MavenRunOptions.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbConfiguration, 0, 378, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbConfiguration;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel pOptions;
    // End of variables declaration//GEN-END:variables
}
