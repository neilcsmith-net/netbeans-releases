/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

/*
 * ShellValidationErrors.java
 *
 * Created on 29.10.2009, 16:51:22
 */
package org.netbeans.modules.nativeexecution.support.ui;

import java.awt.event.ActionListener;
import org.netbeans.modules.nativeexecution.api.util.Shell;
import org.openide.util.NbBundle;

/**
 *
 * @author ak119685
 */
public class ShellValidationStatusPanel extends javax.swing.JPanel {
    private ActionListener actionListener = null;

    /** Creates new form ShellValidationErrors */
    public ShellValidationStatusPanel() {
        this(null, null, null);
    }

    public ShellValidationStatusPanel(Shell shell) {
        this(null, null, shell);
    }
    
    public ShellValidationStatusPanel(String header, String footer, Shell shell) {
        initComponents();

        lblHeader.setText(header);
        lblFooter.setText(footer);

        if (shell != null) {
            lblDescription.setText(loc("ShellValidationStatusPanel.lblDescription.text", // NOI18N
                    shell.type.name(), shell.bindir.getAbsolutePath()));

            StringBuilder errorsText = new StringBuilder();

            for (String error : shell.getValidationStatus().getErrors()) {
                errorsText.append(loc("ShellValidationStatusPanel.error.text") + " " + error).append('\n'); // NOI18N
            }

            for (String error : shell.getValidationStatus().getWarnings()) {
                errorsText.append(loc("ShellValidationStatusPanel.warning.text") + " " + error).append('\n'); // NOI18N
            }

            errorsArea.setText(errorsText.toString());
        }
    }

    public boolean isRememberDecision() {
        return cbRememberChoice.isSelected();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cbRememberChoice = new javax.swing.JCheckBox();
        lblDescription = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        errorsArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblHeader = new javax.swing.JLabel();
        lblFooter = new javax.swing.JLabel();

        cbRememberChoice.setText(org.openide.util.NbBundle.getMessage(ShellValidationStatusPanel.class, "ShellValidationStatusPanel.cbRememberChoice.text")); // NOI18N
        cbRememberChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRememberChoiceActionPerformed(evt);
            }
        });

        lblDescription.setText(org.openide.util.NbBundle.getMessage(ShellValidationStatusPanel.class, "ShellValidationStatusPanel.lblDescription.text")); // NOI18N

        errorsArea.setColumns(20);
        errorsArea.setEditable(false);
        errorsArea.setRows(5);
        errorsArea.setFocusable(false);
        errorsArea.setRequestFocusEnabled(false);
        jScrollPane1.setViewportView(errorsArea);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(ShellValidationStatusPanel.class, "ShellValidationStatusPanel.jLabel1.text")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(ShellValidationStatusPanel.class, "ShellValidationStatusPanel.jLabel2.text")); // NOI18N

        lblHeader.setText(org.openide.util.NbBundle.getMessage(ShellValidationStatusPanel.class, "ShellValidationStatusPanel.lblHeader.text")); // NOI18N

        lblFooter.setText(org.openide.util.NbBundle.getMessage(ShellValidationStatusPanel.class, "ShellValidationStatusPanel.lblFooter.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblHeader)
                    .add(lblDescription)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                    .add(jLabel1)
                    .add(jLabel2)
                    .add(cbRememberChoice)
                    .add(lblFooter))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblHeader)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblDescription)
                .add(18, 18, 18)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .add(18, 18, 18)
                .add(cbRememberChoice)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblFooter))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbRememberChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRememberChoiceActionPerformed
        if (actionListener != null) {
            actionListener.actionPerformed(evt);
        }
    }//GEN-LAST:event_cbRememberChoiceActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbRememberChoice;
    private javax.swing.JTextArea errorsArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblFooter;
    private javax.swing.JLabel lblHeader;
    // End of variables declaration//GEN-END:variables

    private static String loc(String key, String... params) {
        return NbBundle.getMessage(ShellValidationStatusPanel.class, key, params);
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }
}
