/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.tasks.ui.settings;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;

/**
 *
 * @author jpeska
 */
public class DashboardOptions extends javax.swing.JPanel implements DocumentListener {

    PropertyChangeSupport support = new PropertyChangeSupport(this);
    private boolean dataValid;

    /**
     * Creates new form DashboardOptions
     */
    public DashboardOptions() {
        initComponents();
        lblError.setVisible(false);
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
        jSeparator1 = new javax.swing.JSeparator();
        txtSync = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        txtLimitNumber = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lblError = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cbLimit = new javax.swing.JCheckBox();
        cbLimitCategory = new javax.swing.JCheckBox();
        cbLimitQuery = new javax.swing.JCheckBox();
        cbSync = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(DashboardOptions.class, "DashboardOptions.jLabel1.text")); // NOI18N

        txtSync.setMinimumSize(new java.awt.Dimension(30, 20));
        txtSync.setPreferredSize(new java.awt.Dimension(40, 20));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(DashboardOptions.class, "DashboardOptions.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(DashboardOptions.class, "DashboardOptions.jLabel4.text")); // NOI18N

        txtLimitNumber.setMinimumSize(new java.awt.Dimension(30, 20));
        txtLimitNumber.setPreferredSize(new java.awt.Dimension(40, 20));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(DashboardOptions.class, "DashboardOptions.jLabel5.text")); // NOI18N

        lblError.setForeground(java.awt.Color.red);
        lblError.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/tasks/ui/resources/error.png"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(DashboardOptions.class, "DashboardOptions.jLabel6.text")); // NOI18N

        cbLimit.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(cbLimit, org.openide.util.NbBundle.getMessage(DashboardOptions.class, "DashboardOptions.cbLimit.text")); // NOI18N
        cbLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbLimitActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cbLimitCategory, org.openide.util.NbBundle.getMessage(DashboardOptions.class, "DashboardOptions.cbLimitCategory.text")); // NOI18N

        cbLimitQuery.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(cbLimitQuery, org.openide.util.NbBundle.getMessage(DashboardOptions.class, "DashboardOptions.cbLimitQuery.text")); // NOI18N

        cbSync.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(cbSync, org.openide.util.NbBundle.getMessage(DashboardOptions.class, "DashboardOptions.cbSync.text")); // NOI18N
        cbSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSyncActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cbLimit)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtLimitNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel5))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cbSync)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtSync, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel3))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(21, 21, 21)
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cbLimitQuery)
                                            .addComponent(cbLimitCategory)))))
                            .addComponent(lblError))
                        .addGap(0, 39, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSync, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cbSync))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLimitNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(cbLimit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbLimitCategory)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbLimitQuery)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblError)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbSyncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSyncActionPerformed
        txtSync.setEnabled(cbSync.isSelected());
        validate(true);
    }//GEN-LAST:event_cbSyncActionPerformed

    private void cbLimitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbLimitActionPerformed
        txtLimitNumber.setEnabled(cbLimit.isSelected());
        cbLimitCategory.setEnabled(cbLimit.isSelected());
        cbLimitQuery.setEnabled(cbLimit.isSelected());
        validate(true);
    }//GEN-LAST:event_cbLimitActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbLimit;
    private javax.swing.JCheckBox cbLimitCategory;
    private javax.swing.JCheckBox cbLimitQuery;
    private javax.swing.JCheckBox cbSync;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblError;
    private javax.swing.JTextField txtLimitNumber;
    private javax.swing.JTextField txtSync;
    // End of variables declaration//GEN-END:variables

    void update() {
        txtLimitNumber.getDocument().removeDocumentListener(this);
        txtSync.getDocument().removeDocumentListener(this);
        loadSettings();
        txtLimitNumber.getDocument().addDocumentListener(this);
        txtSync.getDocument().addDocumentListener(this);
        validate(false);
    }

    void applyChanges() {
        final DashboardSettings settings = DashboardSettings.getInstance();
        boolean autoSync = cbSync.isSelected();
        settings.setAutoSync(autoSync, false);
        settings.setAutoSyncValue(Integer.parseInt(txtSync.getText().trim()), true);

        boolean tasksLimit = cbLimit.isSelected();
        settings.setTasksLimit(tasksLimit, false);
        settings.setTasksLimitValue(Integer.parseInt(txtLimitNumber.getText().trim()), false);
        settings.setTasksLimitCategory(cbLimitCategory.isSelected(), false);
        settings.setTasksLimitQuery(cbLimitQuery.isSelected(), true);
    }

    boolean isDataValid() {
        validate(false);
        return dataValid;
    }

    boolean isChanged() {
        final DashboardSettings settings = DashboardSettings.getInstance();
        return cbSync.isSelected() != settings.isAutoSync()
                || !txtSync.getText().trim().equals(settings.getAutoSyncValue() + "")
                || !cbLimit.isSelected() == settings.isTasksLimit()
                || !txtLimitNumber.getText().trim().equals(settings.getTasksLimitValue() + "")
                || !cbLimitCategory.isSelected() == settings.isTasksLimitCategory()
                || !cbLimitQuery.isSelected() == settings.isTasksLimitQuery();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        validate(true);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        validate(true);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        validate(true);
    }

    private void validate(boolean fireEvents) {
        boolean oldValid = dataValid;
        lblError.setVisible(false);
        lblError.setText("");                                           // NOI18N

        String limit = txtLimitNumber.getText().trim();
        String sync = txtSync.getText().trim();

        dataValid = (isValidNumberValue(limit) || !cbLimit.isSelected()) && (isValidNumberValue(sync) || !cbSync.isSelected());
        lblError.setVisible(!dataValid);

        if (fireEvents && oldValid != dataValid) {
            support.firePropertyChange(new PropertyChangeEvent(this, OptionsPanelController.PROP_VALID, oldValid, dataValid));
        }
    }

    private boolean isValidNumberValue(String text) {
        try {
            Integer.parseInt(text);
        } catch (NumberFormatException numberFormatException) {
            lblError.setText(NbBundle.getMessage(DashboardOptions.class, "LBL_INVALID_VALUE"));
            return false;
        }
        return true;
    }

    private void loadSettings() {
        final DashboardSettings settings = DashboardSettings.getInstance();
        boolean autoSync = settings.isAutoSync();
        cbSync.setSelected(autoSync);
        txtSync.setText(settings.getAutoSyncValue() + "");
        txtSync.setEnabled(autoSync);

        boolean tasksLimit = settings.isTasksLimit();
        cbLimit.setSelected(tasksLimit);
        txtLimitNumber.setText(settings.getTasksLimitValue() + "");
        txtLimitNumber.setEnabled(tasksLimit);
        cbLimitCategory.setSelected(settings.isTasksLimitCategory());
        cbLimitCategory.setEnabled(tasksLimit);
        cbLimitQuery.setSelected(settings.isTasksLimitQuery());
        cbLimitQuery.setEnabled(tasksLimit);
    }
}
