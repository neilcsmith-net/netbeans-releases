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
package org.netbeans.modules.versioning.ui.options;

/**
 *
 * @author Tomas Stupka
 */
public class HistoryOptionsPanel extends javax.swing.JPanel {
    
    /** Creates new form SvnOptionsPanel */
    public HistoryOptionsPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();

        olderThanDaysTextField.setText(org.openide.util.NbBundle.getMessage(HistoryOptionsPanel.class, "HistoryOptionsPanel.olderThanDaysTextField.text")); // NOI18N

        warningLabel.setForeground(java.awt.Color.red);
        warningLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/versioning/ui/resources/icons/error.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(warningLabel, org.openide.util.NbBundle.getMessage(HistoryOptionsPanel.class, "HistoryOptionsPanel.warningLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(noLabelCleanupCheckBox, org.openide.util.NbBundle.getMessage(HistoryOptionsPanel.class, "HistoryOptionsPanel.noLabelCleanupCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(HistoryOptionsPanel.class, "HistoryOptionsPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(HistoryOptionsPanel.class, "HistoryOptionsPanel.jLabel2.text")); // NOI18N

        daysIncrementTextField.setText(org.openide.util.NbBundle.getMessage(HistoryOptionsPanel.class, "HistoryOptionsPanel.daysIncrementTextField.text")); // NOI18N

        buttonGroup1.add(loadAllRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(loadAllRadioButton, org.openide.util.NbBundle.getMessage(HistoryOptionsPanel.class, "HistoryOptionsPanel.loadAllRadioButton.text")); // NOI18N

        buttonGroup1.add(loadIncrementsRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(loadIncrementsRadioButton, org.openide.util.NbBundle.getMessage(HistoryOptionsPanel.class, "HistoryOptionsPanel.loadIncrementsRadioButton.text")); // NOI18N

        buttonGroup2.add(removeOlderRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(removeOlderRadioButton, org.openide.util.NbBundle.getMessage(HistoryOptionsPanel.class, "HistoryOptionsPanel.removeOlderRadioButton.text")); // NOI18N

        buttonGroup2.add(keepForeverRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(keepForeverRadioButton, org.openide.util.NbBundle.getMessage(HistoryOptionsPanel.class, "HistoryOptionsPanel.keepForeverRadioButton.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(loadAllRadioButton)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(loadIncrementsRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(daysIncrementTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(noLabelCleanupCheckBox))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(removeOlderRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(olderThanDaysTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 160, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1))
                    .addComponent(keepForeverRadioButton)
                    .addComponent(warningLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(olderThanDaysTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeOlderRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noLabelCleanupCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(keepForeverRadioButton)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(daysIncrementTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(loadIncrementsRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loadAllRadioButton)
                .addGap(18, 18, 18)
                .addComponent(warningLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        olderThanDaysTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(HistoryOptionsPanel.class, "ACSN_LocalHistoryOptionsPanel.daysTextField.text")); // NOI18N
        olderThanDaysTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(HistoryOptionsPanel.class, "ACSD_LocalHistoryOptionsPanel.daysTextField.text")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    final javax.swing.JTextField daysIncrementTextField = new javax.swing.JTextField();
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    final javax.swing.JRadioButton keepForeverRadioButton = new javax.swing.JRadioButton();
    final javax.swing.JRadioButton loadAllRadioButton = new javax.swing.JRadioButton();
    final javax.swing.JRadioButton loadIncrementsRadioButton = new javax.swing.JRadioButton();
    final javax.swing.JCheckBox noLabelCleanupCheckBox = new javax.swing.JCheckBox();
    final javax.swing.JTextField olderThanDaysTextField = new javax.swing.JTextField();
    final javax.swing.JRadioButton removeOlderRadioButton = new javax.swing.JRadioButton();
    final javax.swing.JLabel warningLabel = new javax.swing.JLabel();
    // End of variables declaration//GEN-END:variables
    
}
