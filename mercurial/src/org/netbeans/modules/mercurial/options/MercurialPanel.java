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

package org.netbeans.modules.mercurial.options;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.netbeans.modules.mercurial.HgModuleConfig;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import org.netbeans.modules.mercurial.Mercurial;
import org.openide.util.NbBundle;
import static org.netbeans.modules.mercurial.options.Bundle.*;
import org.netbeans.spi.options.OptionsPanelController;

@OptionsPanelController.Keywords(keywords={"hg", "mercurial", "#MercurialPanel.kw1", "#MercurialPanel.kw2", "#MercurialPanel.kw3"},
        location="Team", tabTitle="#CTL_OptionsPanel.title")
@NbBundle.Messages({
    "CTL_OptionsPanel.title=Versioning",
    "MercurialPanel.kw1=status labels",
    "MercurialPanel.kw2=extensions",
    "MercurialPanel.kw3=exclude from commit"
})
final class MercurialPanel extends javax.swing.JPanel {
    
    private final MercurialOptionsPanelController controller;
    private final DocumentListener listener;
    private final ActionListener actionListener;
    private String initialUserName;
    private String[] keywords;
    
    MercurialPanel(MercurialOptionsPanelController controller) {
        this.controller = controller;
        this.listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { nameChange(); }
            public void removeUpdate(DocumentEvent e) { nameChange(); }
            public void changedUpdate(DocumentEvent e) { nameChange(); }
        };
        actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameChange();
            }
        };
        initComponents();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        userNameTextField.getDocument().addDocumentListener(listener);
        executablePathTextField.getDocument().addDocumentListener(listener);
        exportFilenameTextField.getDocument().addDocumentListener(listener);
        annotationTextField.getDocument().addDocumentListener(listener);
        cbOpenOutputWindow.addActionListener(actionListener);
        cbAskBeforeCommitAfterMerge.addActionListener(actionListener);
        cbInternalMergeToolEnabled.addActionListener(actionListener);
        excludeNewFiles.addActionListener(actionListener);
        pullWithUpdate.addActionListener(actionListener);
    }

    @Override
    public void removeNotify() {
        userNameTextField.getDocument().removeDocumentListener(listener);
        super.removeNotify();
    }

    Collection<String> getKeywords () {
        if (keywords == null) {
            keywords = new String[] {
                "HG",
                "MERCURIAL",
                Bundle.MercurialPanel_kw1().toUpperCase(),
                Bundle.MercurialPanel_kw2().toUpperCase(),
                Bundle.MercurialPanel_kw3().toUpperCase()
            };
        }
        return Collections.unmodifiableList(Arrays.asList(keywords));
    }

        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        exportFilename = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextPane1 = new javax.swing.JTextPane();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        cbOpenOutputWindow = new javax.swing.JCheckBox();
        cbAskBeforeCommitAfterMerge = new javax.swing.JCheckBox();
        cbInternalMergeToolEnabled = new javax.swing.JCheckBox();
        lblWarning = new javax.swing.JLabel();

        jLabel1.setLabelFor(userNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.jLabel1.text")); // NOI18N

        jLabel2.setLabelFor(executablePathTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(execPathBrowseButton, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.browseButton.text")); // NOI18N

        exportFilename.setLabelFor(exportFilenameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(exportFilename, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.ExportFilename.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(exportFilenameBrowseButton, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.browseButton2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.jLabel5.text")); // NOI18N

        jLabel3.setLabelFor(annotationTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.addButton.text")); // NOI18N

        annotationTextField.setText(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.annotationTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.jLabel6.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(manageButton, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.manageButton.text")); // NOI18N

        jTextPane1.setBackground(jLabel1.getBackground());
        jTextPane1.setText(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.jTextPane1.text")); // NOI18N

        cbOpenOutputWindow.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(cbOpenOutputWindow, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.cbOpenOutputWindow.text")); // NOI18N
        cbOpenOutputWindow.setToolTipText(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "ACSD_cbOpenOutputWindow")); // NOI18N
        cbOpenOutputWindow.setBorder(null);

        cbAskBeforeCommitAfterMerge.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(cbAskBeforeCommitAfterMerge, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.cbAskBeforeCommitAfterMerge.text")); // NOI18N
        cbAskBeforeCommitAfterMerge.setToolTipText(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.cbAskBeforeCommitAfterMerge.toolTipText")); // NOI18N
        cbAskBeforeCommitAfterMerge.setBorder(null);

        cbInternalMergeToolEnabled.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(cbInternalMergeToolEnabled, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.cbInternalMergeToolEnabled.text")); // NOI18N
        cbInternalMergeToolEnabled.setToolTipText(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.cbInternalMergeToolEnabled.toolTipText")); // NOI18N
        cbInternalMergeToolEnabled.setBorder(null);

        org.openide.awt.Mnemonics.setLocalizedText(excludeNewFiles, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.excludeNewFiles.text")); // NOI18N
        excludeNewFiles.setToolTipText(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.excludeNewFiles.toolTipText")); // NOI18N
        excludeNewFiles.setBorder(null);
        excludeNewFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excludeNewFilesActionPerformed(evt);
            }
        });

        lblWarning.setForeground(javax.swing.UIManager.getDefaults().getColor("nb.errorForeground"));
        org.openide.awt.Mnemonics.setLocalizedText(lblWarning, " "); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(pullWithUpdate, org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.pullWithUpdate.text")); // NOI18N
        pullWithUpdate.setToolTipText(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "MercurialPanel.pullWithUpdate.toolTipText")); // NOI18N
        pullWithUpdate.setBorder(null);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(449, 449, 449))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(exportFilename))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(executablePathTextField)
                            .addComponent(exportFilenameTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(exportFilenameBrowseButton)
                            .addComponent(execPathBrowseButton)))
                    .addComponent(userNameTextField)))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(annotationTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(jSeparator2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jTextPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(manageButton))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblWarning)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbOpenOutputWindow)
                            .addComponent(cbAskBeforeCommitAfterMerge)
                            .addComponent(cbInternalMergeToolEnabled)
                            .addComponent(excludeNewFiles)
                            .addComponent(pullWithUpdate)))
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(userNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(executablePathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(execPathBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exportFilename)
                    .addComponent(exportFilenameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exportFilenameBrowseButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(annotationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(manageButton)
                    .addComponent(jTextPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(cbOpenOutputWindow)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbAskBeforeCommitAfterMerge)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbInternalMergeToolEnabled)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(excludeNewFiles)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(lblWarning))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pullWithUpdate))))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {addButton, annotationTextField, jLabel3});

        userNameTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "ACSD_userNameTextField")); // NOI18N
        executablePathTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "ACSD_executablePathTextField")); // NOI18N
        execPathBrowseButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "ACSD_execPathBrowseButton")); // NOI18N
        exportFilenameTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "ACSD_exportFileNameTextField")); // NOI18N
        exportFilenameBrowseButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "ACSD_exportFilenameBrowseButton")); // NOI18N
        addButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "ACSD_addButton")); // NOI18N
        manageButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "ACSD_manageButton")); // NOI18N
        cbOpenOutputWindow.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MercurialPanel.class, "ACSD_cbOpenOutputWindow")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void excludeNewFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excludeNewFilesActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_excludeNewFilesActionPerformed
    
    private void nameChange() {
        HgModuleConfig config = HgModuleConfig.getDefault();
        boolean isChanged = (userNameTextField.isEnabled() && !initialUserName.equals(userNameTextField.getText()))
                || !config.getExecutableBinaryPath().equals(executablePathTextField.getText())
                || !config.getExportFilename().equals(exportFilenameTextField.getText())
                || !config.getAnnotationFormat().equals(annotationTextField.getText())
                || config.getAutoOpenOutput() != cbOpenOutputWindow.isSelected()
                || config.getConfirmCommitAfterMerge() != cbAskBeforeCommitAfterMerge.isSelected()
                || config.isInternalMergeToolEnabled() != cbInternalMergeToolEnabled.isSelected()
                || config.isPullWithUpdate() != pullWithUpdate.isSelected()
                || config.getExludeNewFiles() != excludeNewFiles.isSelected();
        controller.changed(isChanged);
    }

    @NbBundle.Messages("CTL_UsernameLoading=Loading...")
    void load() {
        // TODO read settings and initialize GUI
        // Example:
        // someCheckBox.setSelected(Preferences.userNodeForPackage(MercurialPanel.class).getBoolean("someFlag", false)); // NOI18N
        // or for org.openide.util with API spec. version >= 7.4:
        // someCheckBox.setSelected(NbPreferences.forModule(MercurialPanel.class).getBoolean("someFlag", false)); // NOI18N
        // or:
        // someTextField.setText(SomeSystemOption.getDefault().getSomeStringProperty());
        userNameTextField.setEnabled(false);
        userNameTextField.setText(CTL_UsernameLoading());
        final HgModuleConfig config = HgModuleConfig.getDefault();
        Mercurial.getInstance().getParallelRequestProcessor().post(new Runnable() {
            @Override
            public void run () {
                initialUserName = config.getSysUserName();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run () {
                        userNameTextField.setText(initialUserName);
                        userNameTextField.setEnabled(true);
                    }
                });
            }
        });
        executablePathTextField.setText(config.getExecutableBinaryPath());
        exportFilenameTextField.setText(config.getExportFilename());
        annotationTextField.setText(config.getAnnotationFormat());
        cbOpenOutputWindow.setSelected(config.getAutoOpenOutput());
        cbAskBeforeCommitAfterMerge.setSelected(config.getConfirmCommitAfterMerge());
        cbInternalMergeToolEnabled.setSelected(config.isInternalMergeToolEnabled());
        excludeNewFiles.setSelected(config.getExludeNewFiles());
        pullWithUpdate.setSelected(config.isPullWithUpdate());
    }
    
    void store() {
        HgModuleConfig config = HgModuleConfig.getDefault();
        // TODO store modified settings
        // Example:
        // Preferences.userNodeForPackage(MercurialPanel.class).putBoolean("someFlag", someCheckBox.isSelected()); // NOI18N
        // or for org.openide.util with API spec. version >= 7.4:
        // NbPreferences.forModule(MercurialPanel.class).putBoolean("someFlag", someCheckBox.isSelected()); // NOI18N
        // or:
        // SomeSystemOption.getDefault().setSomeStringProperty(someTextField.getText());
        if(userNameTextField.isEnabled() && !initialUserName.equals(userNameTextField.getText())) {
            try {
                config.setUserName(userNameTextField.getText());
            } catch (IOException ex) {
                HgModuleConfig.notifyParsingError();
            }
        }
        config.setExecutableBinaryPath(executablePathTextField.getText());
	Mercurial.getInstance().asyncInit();
        config.setExportFilename(exportFilenameTextField.getText());
        config.setAnnotationFormat(annotationTextField.getText());
        config.setAutoOpenOutput(cbOpenOutputWindow.isSelected());
        config.setConfirmCommitAfterMerge(cbAskBeforeCommitAfterMerge.isSelected());
        config.setInternalMergeToolEnabled(cbInternalMergeToolEnabled.isSelected());
        config.setExcludeNewFiles(excludeNewFiles.isSelected());
        config.setPullWithUpdate(pullWithUpdate.isSelected());
    }
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JButton addButton = new javax.swing.JButton();
    final javax.swing.JTextField annotationTextField = new javax.swing.JTextField();
    private javax.swing.JCheckBox cbAskBeforeCommitAfterMerge;
    private javax.swing.JCheckBox cbInternalMergeToolEnabled;
    private javax.swing.JCheckBox cbOpenOutputWindow;
    final javax.swing.JCheckBox excludeNewFiles = new javax.swing.JCheckBox();
    final javax.swing.JButton execPathBrowseButton = new javax.swing.JButton();
    final javax.swing.JTextField executablePathTextField = new javax.swing.JTextField();
    private javax.swing.JLabel exportFilename;
    final javax.swing.JButton exportFilenameBrowseButton = new javax.swing.JButton();
    final javax.swing.JTextField exportFilenameTextField = new javax.swing.JTextField();
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JLabel lblWarning;
    final javax.swing.JButton manageButton = new javax.swing.JButton();
    final javax.swing.JCheckBox pullWithUpdate = new javax.swing.JCheckBox();
    final javax.swing.JTextField userNameTextField = new javax.swing.JTextField();
    // End of variables declaration//GEN-END:variables

    void showError (String message) {
        if (message == null) {
            lblWarning.setText(" "); //NOI18N
        } else {
            lblWarning.setText(message);
        }
    }
    
}
