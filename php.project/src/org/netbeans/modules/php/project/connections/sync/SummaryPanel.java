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
package org.netbeans.modules.php.project.connections.sync;

import java.awt.Dialog;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

/**
 * Synchronization confirmation panel.
 */
public class SummaryPanel extends JPanel {

    private static final long serialVersionUID = 176831576846546L;

    private static final Logger LOGGER = Logger.getLogger(SummaryPanel.class.getName());


    public SummaryPanel(int uploadNumber, int downloadNumber, int deleteNumber, int noopNumber) {
        initComponents();
        setNumber(uploadNumberLabel, uploadNumber);
        setNumber(downloadNumberLabel, downloadNumber);
        setNumber(deleteNumberLabel, deleteNumber);
        setNumber(noopNumberLabel, noopNumber);
    }

    @NbBundle.Messages("SummaryPanel.title=Summary")
    public boolean open() {
        assert SwingUtilities.isEventDispatchThread();
        DialogDescriptor descriptor = new DialogDescriptor(
                this,
                Bundle.SummaryPanel_title(),
                true,
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.OK_OPTION,
                null);
        final Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        try {
            dialog.setVisible(true);
        } finally {
            dialog.dispose();
        }
        return descriptor.getValue() == NotifyDescriptor.OK_OPTION;
    }

    public void decreaseUploadNumber() {
        decreaseNumber(uploadNumberLabel);
    }

    public void decreaseDownloadNumber() {
        decreaseNumber(downloadNumberLabel);
    }

    public void decreaseNoopNumber() {
        decreaseNumber(noopNumberLabel);
    }

    public void resetDeleteNumber() {
        deleteNumberLabel.setText(String.valueOf(0));
    }

    private void setNumber(JLabel numberLabel, int number) {
        numberLabel.setText(String.valueOf(number));
    }

    @NbBundle.Messages("SummaryPanel.na=N/A")
    private void decreaseNumber(JLabel numberLabel) {
        try {
            int number = Integer.parseInt(numberLabel.getText());
            number--;
            numberLabel.setText(String.valueOf(number));
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, null, ex);
            numberLabel.setText(Bundle.SummaryPanel_na());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        uploadLabel = new JLabel();
        downloadLabel = new JLabel();
        deleteLabel = new JLabel();
        noopLabel = new JLabel();
        uploadNumberLabel = new JLabel();
        downloadNumberLabel = new JLabel();
        deleteNumberLabel = new JLabel();
        noopNumberLabel = new JLabel();
        Mnemonics.setLocalizedText(uploadLabel, NbBundle.getMessage(SummaryPanel.class, "SummaryPanel.uploadLabel.text")); // NOI18N
        Mnemonics.setLocalizedText(downloadLabel, NbBundle.getMessage(SummaryPanel.class, "SummaryPanel.downloadLabel.text")); // NOI18N
        Mnemonics.setLocalizedText(deleteLabel, NbBundle.getMessage(SummaryPanel.class, "SummaryPanel.deleteLabel.text")); // NOI18N
        Mnemonics.setLocalizedText(noopLabel, NbBundle.getMessage(SummaryPanel.class, "SummaryPanel.noopLabel.text")); // NOI18N
        Mnemonics.setLocalizedText(uploadNumberLabel, "0"); // NOI18N
        Mnemonics.setLocalizedText(downloadNumberLabel, "0"); // NOI18N
        Mnemonics.setLocalizedText(deleteNumberLabel, "0"); // NOI18N
        Mnemonics.setLocalizedText(noopNumberLabel, "0"); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
                .addContainerGap()

                .addGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
                        .addComponent(downloadLabel)

                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(downloadNumberLabel)).addGroup(layout.createSequentialGroup()
                        .addComponent(uploadLabel)

                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(uploadNumberLabel)).addGroup(layout.createSequentialGroup()
                        .addComponent(deleteLabel)

                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(deleteNumberLabel)).addGroup(layout.createSequentialGroup()
                        .addComponent(noopLabel)

                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(noopNumberLabel))).addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
                .addContainerGap()

                .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(uploadLabel).addComponent(uploadNumberLabel)).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(downloadLabel).addComponent(downloadNumberLabel)).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(deleteLabel).addComponent(deleteNumberLabel)).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(noopLabel).addComponent(noopNumberLabel)).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel deleteLabel;
    private JLabel deleteNumberLabel;
    private JLabel downloadLabel;
    private JLabel downloadNumberLabel;
    private JLabel noopLabel;
    private JLabel noopNumberLabel;
    private JLabel uploadLabel;
    private JLabel uploadNumberLabel;
    // End of variables declaration//GEN-END:variables
}
