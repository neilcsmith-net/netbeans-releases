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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

/*
 * AccidentalAssignmentCustomizer.java
 *
 * Created on Sep 16, 2008, 11:44:39 AM
 */

package org.netbeans.modules.php.editor.verification;

import java.util.prefs.Preferences;

/**
 *
 * @author Tomasz.Slota@Sun.COM
 */
public class AccidentalAssignmentCustomizer extends javax.swing.JPanel {
    private final AccidentalAssignmentHint accidentalAssignmentHint;
    private final Preferences preferences;

    /* Creates new form AccidentalAssignmentCustomizer */
    public AccidentalAssignmentCustomizer(Preferences preferences, AccidentalAssignmentHint accidentalAssignmentHint) {
        this.preferences = preferences;
        this.accidentalAssignmentHint = accidentalAssignmentHint;
        initComponents();
        checkAssignmentsInWhileStatementsCheckBox.setSelected(accidentalAssignmentHint.checkAssignmentsInWhileStatements(preferences));
        checkAssignmentsInSubStatementsCheckBox.setSelected(accidentalAssignmentHint.checkAssignmentsInSubStatements(preferences));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        checkAssignmentsInWhileStatementsCheckBox = new javax.swing.JCheckBox();
        checkAssignmentsInSubStatementsCheckBox = new javax.swing.JCheckBox();

        setFocusTraversalPolicy(null);

        checkAssignmentsInWhileStatementsCheckBox.setMnemonic('a');
        checkAssignmentsInWhileStatementsCheckBox.setText(org.openide.util.NbBundle.getMessage(AccidentalAssignmentCustomizer.class, "AccidentalAssignmentCustomizer.includeWhileCB.text")); // NOI18N
        checkAssignmentsInWhileStatementsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAssignmentsInWhileStatementsCheckBoxActionPerformed(evt);
            }
        });

        checkAssignmentsInSubStatementsCheckBox.setMnemonic('C');
        checkAssignmentsInSubStatementsCheckBox.setText(org.openide.util.NbBundle.getMessage(AccidentalAssignmentCustomizer.class, "AccidentalAssignmentCustomizer.checkAssignmentsInSubStatementsCheckBox.text")); // NOI18N
        checkAssignmentsInSubStatementsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAssignmentsInSubStatementsCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkAssignmentsInSubStatementsCheckBox)
                    .addComponent(checkAssignmentsInWhileStatementsCheckBox))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkAssignmentsInSubStatementsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkAssignmentsInWhileStatementsCheckBox)
                .addContainerGap(238, Short.MAX_VALUE))
        );

        checkAssignmentsInWhileStatementsCheckBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AccidentalAssignmentCustomizer.class, "AccidentalAssignmentCustomizer.cbIncludeWhile.AccessibleContext.accessibleName")); // NOI18N
        checkAssignmentsInWhileStatementsCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AccidentalAssignmentCustomizer.class, "AccidentalAssignmentCustomizer.cbIncludeWhile.AccessibleContext.accessibleDescription")); // NOI18N
        checkAssignmentsInSubStatementsCheckBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AccidentalAssignmentCustomizer.class, "AccidentalAssignmentCustomizer.chkTopLvlStmtOnly.AccessibleContext.accessibleName")); // NOI18N
        checkAssignmentsInSubStatementsCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AccidentalAssignmentCustomizer.class, "AccidentalAssignmentCustomizer.chkTopLvlStmtOnly.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AccidentalAssignmentCustomizer.class, "AccidentalAssignmentCustomizer.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AccidentalAssignmentCustomizer.class, "AccidentalAssignmentCustomizer.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void checkAssignmentsInWhileStatementsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkAssignmentsInWhileStatementsCheckBoxActionPerformed
        accidentalAssignmentHint.setCheckAssignmentsInWhileStatements(preferences, checkAssignmentsInWhileStatementsCheckBox.isSelected());
    }//GEN-LAST:event_checkAssignmentsInWhileStatementsCheckBoxActionPerformed

    private void checkAssignmentsInSubStatementsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkAssignmentsInSubStatementsCheckBoxActionPerformed
        accidentalAssignmentHint.setCheckAssignmentsInSubStatements(preferences, checkAssignmentsInSubStatementsCheckBox.isSelected());
}//GEN-LAST:event_checkAssignmentsInSubStatementsCheckBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkAssignmentsInSubStatementsCheckBox;
    private javax.swing.JCheckBox checkAssignmentsInWhileStatementsCheckBox;
    // End of variables declaration//GEN-END:variables

}
