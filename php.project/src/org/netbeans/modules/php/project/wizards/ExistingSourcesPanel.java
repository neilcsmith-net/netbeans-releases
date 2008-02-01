/*
 * SourcesPanelVisual.java
 *
 * Created on 21 Август 2007 г., 16:17
 */

package org.netbeans.modules.php.project.wizards;

import java.awt.Color;
import java.io.File;
import java.text.MessageFormat;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.php.project.ui.SourceRootsUi;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author  avk
 */
public class ExistingSourcesPanel extends javax.swing.JPanel {

    private static final String TIP_FULL_SOURCE_PATH = "TIP_SourcePath"; // NOI18N
    private static final String LBL_SELECT_SOURCE_FOLDER = "LBL_Select_Source_Folder_Title"; // NOI18N
    private static final String BROWSE = "BROWSE"; // NOI18N

    /** Creates new form SourcesPanelVisual */
    ExistingSourcesPanel(PhpSourcesConfigurePanel panel) {
        myPanel = panel;

        initComponents();

        init(panel);
    }

    boolean dataIsValid(WizardDescriptor wizardDescriptor) {
        return validate(wizardDescriptor);
    }

    void store(WizardDescriptor descriptor) {
        if (myLastUsedSourceDir != null) {
            descriptor.putProperty(NewPhpProjectWizardIterator.SOURCE_ROOT, myLastUsedSourceDir);
        }
    }

    void read(WizardDescriptor settings) {
        File root = (File) settings.getProperty(NewPhpProjectWizardIterator.SOURCE_ROOT);

        myProjectLocation = (File) settings.
                getProperty(NewPhpProjectWizardIterator.PROJECT_DIR);

        viewSourcesRoot(root);
    }

    private void init(PhpSourcesConfigurePanel panel) {
        myPanel = panel;
        // Register listener on the textFields to make the automatic updates
        myListener = new Listener();
        mySourceFolder.getDocument().addDocumentListener(myListener);

        //text field is not editable. But we set it's BG color to look as it is editable.
        // To show that it can ber changed (at least using button)
        mySourceFolder.setBackground(getTextFieldBgColor());
    }
        
    private boolean validate(WizardDescriptor wizardDescriptor) {
        if (!validateSourceRoot(wizardDescriptor)) {
            return false;
        }
        wizardDescriptor.putProperty(
                NewPhpProjectWizardIterator.WIZARD_PANEL_ERROR_MESSAGE, "");
        return true;
    }

    private boolean validateSourceRoot(WizardDescriptor wizardDescriptor) {
        // TODO validate
        return true;
    }

    PhpSourcesConfigurePanel getPanel() {
        return myPanel;
    }

    private void viewSourcesRoot(File sourceRoot) {
        if (sourceRoot == null) {
            mySourceFolder.setText("");
            return;
        }
        String projectPath = myProjectLocation.getAbsolutePath();
        String sourcePath = sourceRoot.getAbsolutePath();

        if (projectPath.equalsIgnoreCase(sourcePath)) {
            mySourceFolder.setText("."); // NOI18N
        } else if (sourcePath.startsWith(projectPath + File.separator)) {
            String name = sourcePath.substring(projectPath.length() + 1);
            mySourceFolder.setText(name);
        } else {
            mySourceFolder.setText(sourcePath);
        }

        String message = NbBundle.getMessage(ExistingSourcesPanel.class, TIP_FULL_SOURCE_PATH);
        String tip = MessageFormat.format(message, sourcePath);
        mySourceFolder.setToolTipText(tip);
    }

    private String getMessage(String key, Object... args) {
        String message = null;
        if (args.length > 0) {
            message = MessageFormat.format(NbBundle.getMessage(ExistingSourcesPanel.class, key), args);
        } else {
            message = NbBundle.getMessage(ExistingSourcesPanel.class, key);
        }
        return message;
    }

    private void performUpdate(DocumentEvent event) {
        getPanel().fireChangeEvent(); // Notify that the panel changed
    }

    private class Listener implements DocumentListener {

        public void changedUpdate(DocumentEvent event) {
            performUpdate(event);
        }

        public void insertUpdate(DocumentEvent event) {
            performUpdate(event);
        }

        public void removeUpdate(DocumentEvent event) {
            performUpdate(event);
        }
    }

    private Color getTextFieldBgColor(){
        JTextField tf = new JTextField();
        tf.setEditable(true);
        tf.setEnabled(true);
        return tf.getBackground();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        mySourceFolderLabel = new javax.swing.JLabel();
        mySourceFolder = new javax.swing.JTextField();
        myBrowse = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        mySourceFolderLabel.setLabelFor(mySourceFolder);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/php/project/wizards/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(mySourceFolderLabel, bundle.getString("LBL_Source_Folder")); // NOI18N

        mySourceFolder.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(myBrowse, org.openide.util.NbBundle.getMessage(ExistingSourcesPanel.class, "LBL_Browse_Btn")); // NOI18N
        myBrowse.setActionCommand(BROWSE);
        myBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myBrowsedoBrowse(evt);
            }
        });

        jLabel1.setText(org.openide.util.NbBundle.getMessage(ExistingSourcesPanel.class, "ExistingSourcesPanel.jLabel1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(mySourceFolderLabel)
                        .add(19, 19, 19)
                        .add(mySourceFolder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(myBrowse, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 81, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mySourceFolderLabel)
                    .add(mySourceFolder, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(myBrowse))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mySourceFolderLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ExistingSourcesPanel.class, "A11_Source_Folder_Lbl")); // NOI18N
        mySourceFolder.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ExistingSourcesPanel.class, "A11_Source_Folder")); // NOI18N
        myBrowse.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ExistingSourcesPanel.class, "A11_Browse_Btn")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void myBrowsedoBrowse(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myBrowsedoBrowse
        String command = evt.getActionCommand();

        if (BROWSE.equals(command)) {
            JFileChooser chooser = new JFileChooser();
            FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
            chooser.setDialogTitle(getMessage(LBL_SELECT_SOURCE_FOLDER));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        File projectDir = myProjectLocation;
        File curDir = null;
        if (myLastUsedSourceDir != null) {
            curDir = myLastUsedSourceDir;
        }
        if (curDir == null) {
            curDir = projectDir;
        }
        if (curDir != null) {
            chooser.setCurrentDirectory(curDir);
        }

        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File sourceDir = chooser.getSelectedFile();
            if (sourceDir != null) {
                File normSourceDir = FileUtil.normalizeFile(sourceDir);
                File normProjectDir = FileUtil.normalizeFile(projectDir);
                if (SourceRootsUi.isRootNotOccupied(normSourceDir, normProjectDir)) {
                    this.myLastUsedSourceDir = normSourceDir;
                    viewSourcesRoot(myLastUsedSourceDir);
                } else {
                    SourceRootsUi.showSourceUsedDialog(normSourceDir);
                }
            }
        }
            
            
            getPanel().fireChangeEvent();
        }
    }//GEN-LAST:event_myBrowsedoBrowse


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton myBrowse;
    private javax.swing.JTextField mySourceFolder;
    private javax.swing.JLabel mySourceFolderLabel;
    // End of variables declaration//GEN-END:variables
    private PhpSourcesConfigurePanel myPanel;
    private DocumentListener myListener;
    private File myProjectLocation;
    private File myLastUsedSourceDir;
}
