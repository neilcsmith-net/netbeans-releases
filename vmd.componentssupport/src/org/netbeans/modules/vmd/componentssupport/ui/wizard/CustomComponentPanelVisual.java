/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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


package org.netbeans.modules.vmd.componentssupport.ui.wizard;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

class CustomComponentPanelVisual extends JPanel implements DocumentListener {

    public static final String PROP_PROJECT_NAME = "projectName";
    public static final String BROWSE = "BROWSE";
    public static final String LBL_SELECT_LOCATION_DLG = "LBL_SelectProjectLocation";
    //messages
    public static final String MSG_ILLEGAL_FOLDER_NAME = "MSG_IllegalFolderName";
    public static final String MSG_IS_NOT_DIRECTORY = "MSG_IsNotAFolder";
    public static final String MSG_CANT_CREATE_FOLDER = "MSG_CanNotCreateFolder";
    public static final String MSG_ILLEGAL_FOLDER_PATH = "MSG_IllegalFolderPath";
    public static final String MSG_FOLDER_EXISTS = "MSG_ProjectFolderExists";
    //default values
    public static final String TXT_DEFAULT_PROJECT_NAME = "TXT_DefaultProjectName";

    private CustomComponentWizardPanel panel;

    public CustomComponentPanelVisual(CustomComponentWizardPanel panel) {
        initComponents();
        this.panel = panel;
        // Register listener on the textFields to make the automatic updates
        projectNameTextField.getDocument().addDocumentListener(this);
        projectLocationTextField.getDocument().addDocumentListener(this);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        //same problem as in 31086, initial focus on Cancel button
        projectNameTextField.requestFocus();
    }

    // Implementation of DocumentListener --------------------------------------
    public void changedUpdate(DocumentEvent e) {
        updateTexts(e);
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange(PROP_PROJECT_NAME, 
                    null, this.projectNameTextField.getText());
        }
    }

    public void insertUpdate(DocumentEvent e) {
        updateTexts(e);
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange(PROP_PROJECT_NAME, 
                    null, this.projectNameTextField.getText());
        }
    }

    public void removeUpdate(DocumentEvent e) {
        updateTexts(e);
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange(PROP_PROJECT_NAME, 
                    null, this.projectNameTextField.getText());
        }
    }
    // -------------
    
    void store(WizardDescriptor d) {
        String name = projectNameTextField.getText().trim();
        String folder = createdFolderTextField.getText().trim();

        d.putProperty(CustomComponentWizardIterator.PROJECT_DIR, new File(folder));
        d.putProperty(CustomComponentWizardIterator.PROJECT_NAME, name);
    }

    void read(WizardDescriptor settings) {

        this.projectLocationTextField.setText(
                getProjectLocation(settings).getAbsolutePath());
        
        this.projectNameTextField.setText(getProjectName(settings));
        this.projectNameTextField.selectAll();
    }
    
    boolean valid(WizardDescriptor wizardDescriptor) {

        if (projectNameTextField.getText().length() == 0) {
            String message = NbBundle.getMessage(CustomComponentPanelVisual.class,
                    MSG_ILLEGAL_FOLDER_NAME );
            wizardDescriptor.putProperty(
                    CustomComponentWizardIterator.WIZARD_PANEL_ERROR_MESSAGE, message);
            return false; // Display name not specified

        }
        File f = FileUtil.normalizeFile(
                new File(projectLocationTextField.getText()).getAbsoluteFile());
        if (!f.isDirectory()) {
            String message = NbBundle.getMessage(CustomComponentPanelVisual.class,
                    MSG_IS_NOT_DIRECTORY );
            wizardDescriptor.putProperty(
                    CustomComponentWizardIterator.WIZARD_PANEL_ERROR_MESSAGE, message);
            return false;
        }
        final File destFolder = FileUtil.normalizeFile(
                new File(createdFolderTextField.getText()).getAbsoluteFile());

        File projLoc = destFolder;
        while (projLoc != null && !projLoc.exists()) {
            projLoc = projLoc.getParentFile();
        }
        if (projLoc == null || !projLoc.canWrite()) {
            String message = NbBundle.getMessage(CustomComponentPanelVisual.class,
                    MSG_CANT_CREATE_FOLDER );
            wizardDescriptor.putProperty(
                    CustomComponentWizardIterator.WIZARD_PANEL_ERROR_MESSAGE, message);
            return false;
        }

        if (FileUtil.toFileObject(projLoc) == null) {
            String message = NbBundle.getMessage(CustomComponentPanelVisual.class,
                    MSG_ILLEGAL_FOLDER_PATH );
            wizardDescriptor.putProperty(
                    CustomComponentWizardIterator.WIZARD_PANEL_ERROR_MESSAGE, message);
            return false;
        }

        File[] kids = destFolder.listFiles();
        if (destFolder.exists() && kids != null && kids.length > 0) {
            // Folder exists and is not empty
            String message = NbBundle.getMessage(CustomComponentPanelVisual.class,
                    MSG_FOLDER_EXISTS );
            wizardDescriptor.putProperty(
                    CustomComponentWizardIterator.WIZARD_PANEL_ERROR_MESSAGE, message);
            return false;
        }
        wizardDescriptor.putProperty(
                CustomComponentWizardIterator.WIZARD_PANEL_ERROR_MESSAGE, "");
        return true;
    }

    /**
     * Returns project location value stored in WizardDescriptor, or
     * default value if it wasn't stored yet
     * @param settings WizardDescriptor
     * @return File Directory that will contain project folder
     */
    File getProjectLocation(WizardDescriptor settings){
        File projectLocation = (File) settings
                .getProperty(CustomComponentWizardIterator.PROJECT_DIR);
        // project directory
        if (projectLocation == null 
                || projectLocation.getParentFile() == null 
                || !projectLocation.getParentFile().isDirectory()) 
        {
            projectLocation = ProjectChooser.getProjectsFolder();
        } else {
            projectLocation = projectLocation.getParentFile();
        }
        return projectLocation;
    }

    /**
     * Returns project name value stored in WizardDescriptor, or
     * default value if it wasn't stored yet
     * @param settings WizardDescriptor
     * @return String project name loaded from WizardDescriptor or default 
     * name wich is not used as directory name in project location directory yet.
     */
    String getProjectName(WizardDescriptor settings){
        String projectName = (String) settings
                .getProperty(CustomComponentWizardIterator.PROJECT_NAME);
        // project name
        if (projectName == null) {
            projectName = getDefaultFreeName(getProjectLocation(settings));
        }
        return projectName;
    }
    
    /* 
     * is invoked from panel.validate()
     * which implements WizardDescriptor.ValidatingPanel 
     */
    void validate(WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectNameLabel = new javax.swing.JLabel();
        projectNameTextField = new javax.swing.JTextField();
        projectLocationLabel = new javax.swing.JLabel();
        projectLocationTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        createdFolderLabel = new javax.swing.JLabel();
        createdFolderTextField = new javax.swing.JTextField();

        projectNameLabel.setLabelFor(projectNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectNameLabel, org.openide.util.NbBundle.getMessage(CustomComponentPanelVisual.class, "LBL_ProjectName")); // NOI18N

        projectLocationLabel.setLabelFor(projectLocationTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectLocationLabel, org.openide.util.NbBundle.getMessage(CustomComponentPanelVisual.class, "LBL_ProjectLocation")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(CustomComponentPanelVisual.class, "LBL_Browse_Button")); // NOI18N
        browseButton.setActionCommand(BROWSE);
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        createdFolderLabel.setLabelFor(createdFolderTextField);
        org.openide.awt.Mnemonics.setLocalizedText(createdFolderLabel, org.openide.util.NbBundle.getMessage(CustomComponentPanelVisual.class, "LBL_ProjectFolder")); // NOI18N

        createdFolderTextField.setEditable(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(projectNameLabel)
                    .add(projectLocationLabel)
                    .add(createdFolderLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, projectNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, projectLocationTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, createdFolderTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(browseButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectNameLabel)
                    .add(projectNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectLocationLabel)
                    .add(projectLocationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(browseButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(createdFolderLabel)
                    .add(createdFolderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(213, Short.MAX_VALUE))
        );

        projectNameLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(CustomComponentPanelVisual.class, "ACSN_ProjectName")); // NOI18N
        projectNameLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomComponentPanelVisual.class, "ACSD_ProjectName")); // NOI18N
        projectLocationLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(CustomComponentPanelVisual.class, "ACSN_ProjectLocation")); // NOI18N
        projectLocationLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomComponentPanelVisual.class, "ACSD_ProjectLocation")); // NOI18N
        browseButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(CustomComponentPanelVisual.class, "ACSN_Browse_Button")); // NOI18N
        browseButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomComponentPanelVisual.class, "ACSN_Browse_Button")); // NOI18N
        createdFolderLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(CustomComponentPanelVisual.class, "ACSN_ProjectFolder")); // NOI18N
        createdFolderLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomComponentPanelVisual.class, "ACSD_ProjectFolder")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        String command = evt.getActionCommand();
        if (BROWSE.equals(command)) {
            JFileChooser chooser = new JFileChooser();
            FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
            chooser.setDialogTitle(LBL_SELECT_LOCATION_DLG);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            String path = this.projectLocationTextField.getText();
            if (path.length() > 0) {
                File f = new File(path);
                if (f.exists()) {
                    chooser.setSelectedFile(f);
                }
            }
            if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
                File projectDir = chooser.getSelectedFile();
                projectLocationTextField.setText(
                        FileUtil.normalizeFile(projectDir).getAbsolutePath());
            }
            panel.fireChangeEvent();
        }

    }//GEN-LAST:event_browseButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel createdFolderLabel;
    private javax.swing.JTextField createdFolderTextField;
    private javax.swing.JLabel projectLocationLabel;
    private javax.swing.JTextField projectLocationTextField;
    private javax.swing.JLabel projectNameLabel;
    private javax.swing.JTextField projectNameTextField;
    // End of variables declaration//GEN-END:variables

    private String getDefaultFreeName(File projectLocation) {
        int i = 1;
        String projectName;
        do {
            projectName = validFreeProjectName(projectLocation, i++);
        } while (projectName == null);
        return projectName;
    }

    private String validFreeProjectName(File parentFolder, int index) {
        String name = NbBundle.getMessage(CustomComponentPanelVisual.class,
                    TXT_DEFAULT_PROJECT_NAME, new Object[] {index} );
        File file = new File(parentFolder, name);
        if (file.exists()) {
            return null;
        }
        return name;
    }

    /** Handles changes in the Project name and project directory, */
    private void updateTexts(DocumentEvent e) {

        Document doc = e.getDocument();

        if (doc == projectNameTextField.getDocument() 
                || doc == projectLocationTextField.getDocument()) 
        {
            // Change in the project name

            String projectName = projectNameTextField.getText();
            String projectFolder = projectLocationTextField.getText();

            //if (projectFolder.trim().length() == 0 || projectFolder.equals(oldName)) {
            createdFolderTextField.setText(projectFolder + File.separatorChar + projectName);
        //}

        }
        panel.fireChangeEvent(); // Notify that the panel changed

    }
}
