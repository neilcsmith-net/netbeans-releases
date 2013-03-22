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
package org.netbeans.modules.php.editor.verification;

import java.util.prefs.Preferences;
import org.netbeans.modules.php.editor.verification.TooManyLinesHint.ClassLinesHint;

/**
 *
 * @author Ondrej Brejla <obrejla@netbeans.org>
 */
public class ClassLinesCustomizer extends javax.swing.JPanel {
    private final Preferences preferences;
    private final ClassLinesHint classLinesHint;


    public ClassLinesCustomizer(Preferences preferences, ClassLinesHint classLinesHint) {
        this.preferences = preferences;
        this.classLinesHint = classLinesHint;
        initComponents();
        maxAllowedLinesSpinner.getModel().setValue(classLinesHint.getMaxAllowedLines(preferences));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        maxAllowedLinesLabel = new javax.swing.JLabel();
        maxAllowedLinesSpinner = new javax.swing.JSpinner();

        org.openide.awt.Mnemonics.setLocalizedText(maxAllowedLinesLabel, org.openide.util.NbBundle.getMessage(ClassLinesCustomizer.class, "ClassLinesCustomizer.maxAllowedLinesLabel.text")); // NOI18N

        maxAllowedLinesSpinner.setModel(new javax.swing.SpinnerNumberModel(100, 1, 1000, 1));
        maxAllowedLinesSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                maxAllowedLinesSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(maxAllowedLinesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(maxAllowedLinesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxAllowedLinesLabel)
                    .addComponent(maxAllowedLinesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void maxAllowedLinesSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_maxAllowedLinesSpinnerStateChanged
        classLinesHint.setMaxAllowedLines(preferences, (Integer) maxAllowedLinesSpinner.getValue());
    }//GEN-LAST:event_maxAllowedLinesSpinnerStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel maxAllowedLinesLabel;
    private javax.swing.JSpinner maxAllowedLinesSpinner;
    // End of variables declaration//GEN-END:variables
}
