/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
/*
 * ExportDataSourcesDialog.java
 *
 * Created on March 8, 2004, 12:09 PM
 */

package org.netbeans.modules.visualweb.ejb.ui;

import java.awt.BorderLayout;
import java.io.File;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;


/**
 * A panle to allow the user to export EJB datasources to a jar file
 *
 * @author dongmei cao
 */
public class ImportEjbDataSourcesPanel extends JPanel{
    
    private EjbDataSourcesSelectionPanel ejbDataSourceSelectionPanel;
    private EjbDataSourcePropertiesPanel propsPanel;
    private PortableEjbDataSource[] ejbDataSources;
    private boolean textFieldChanged = false;
    
    public ImportEjbDataSourcesPanel()
    {
        initComponents();
        
        propsPanel = new EjbDataSourcePropertiesPanel();
        ejbDataSourceSelectionPanel = new EjbDataSourcesSelectionPanel( propsPanel );
        
        selectionPanel.add( ejbDataSourceSelectionPanel, BorderLayout.CENTER );
        propertiesPanel.add( propsPanel, BorderLayout.CENTER );
        
        fileNameTextField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                textFieldChanged = true;
            }

            public void removeUpdate(DocumentEvent e) {
                textFieldChanged = true;
            }

            public void changedUpdate(DocumentEvent e) {
                textFieldChanged = true;
            }
        });
        
    }
    
    public ImportEjbDataSourcesPanel(PortableEjbDataSource[] ejbDataSources)
    {
        this();
    }
    
    public void setImportFilePath( String filePath )
    {
        fileNameTextField.setText( filePath );
    }
    
    public String getImportFilePath()
    {
        return fileNameTextField.getText().trim();
    }
    
    public void setEjbDataSources( PortableEjbDataSource[] ejbDataSources )
    {
        this.ejbDataSources = ejbDataSources;
        ejbDataSourceSelectionPanel.setEjbDataSources( ejbDataSources );
    }
    
    public PortableEjbDataSource[] getEjbDataSources()
    {
        return this.ejbDataSources;
    }
    
    public boolean saveChange()
    {
        return propsPanel.saveChange();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        title = new javax.swing.JLabel();
        selectionPanel = new javax.swing.JPanel();
        filePanel = new javax.swing.JPanel();
        fileNameLabel = new javax.swing.JLabel();
        fileNameTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        propertiesPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        title.setLabelFor(selectionPanel);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/visualweb/ejb/ui/Bundle"); // NOI18N
        title.setText(bundle.getString("IMPORT_EJB_DATASOURCES_LABEL")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        add(title, gridBagConstraints);
        title.getAccessibleContext().setAccessibleName(bundle.getString("IMPORT_EJB_DATASOURCES_LABEL")); // NOI18N
        title.getAccessibleContext().setAccessibleDescription(bundle.getString("IMPORT_EJB_DATASOURCES")); // NOI18N

        selectionPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 10, 10));
        selectionPanel.setVerifyInputWhenFocusTarget(false);
        selectionPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(selectionPanel, gridBagConstraints);

        filePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 10, 10));
        filePanel.setLayout(new java.awt.BorderLayout(5, 1));

        fileNameLabel.setLabelFor(fileNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(fileNameLabel, org.openide.util.NbBundle.getMessage(ImportEjbDataSourcesPanel.class, "FILE_NAME")); // NOI18N
        filePanel.add(fileNameLabel, java.awt.BorderLayout.WEST);
        fileNameLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("IMPORT_FILE_NAME_DESC")); // NOI18N

        fileNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNameTextFieldActionPerformed(evt);
            }
        });
        filePanel.add(fileNameTextField, java.awt.BorderLayout.CENTER);
        fileNameTextField.getAccessibleContext().setAccessibleDescription(bundle.getString("IMPORT_FILE_NAME_DESC")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(ImportEjbDataSourcesPanel.class, "BROWSE_IMPORT_FILE_BUTTON_LABEL")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });
        filePanel.add(browseButton, java.awt.BorderLayout.EAST);
        browseButton.getAccessibleContext().setAccessibleDescription(bundle.getString("BROWSE_IMPORT_FILE_BUTTON_DESC")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(17, 0, 0, 0);
        add(filePanel, gridBagConstraints);

        propertiesPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 17, 0, 12);
        add(propertiesPanel, gridBagConstraints);

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ImportEjbDataSourcesPanel.class, "IMPORT_EJB_DATASOURCES")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ImportEjbDataSourcesPanel.class, "IMPORT_EJB_DATASOURCES")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void fileNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNameTextFieldActionPerformed
        if( !(new File(getImportFilePath())).exists() ) {
            String msg = NbBundle.getMessage(ImportEjbDataSourcesPanel.class, "IMPORT_FILE_NOT_FOUND", getImportFilePath() );
            NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            return;
        }
        else {
            if (!textFieldChanged) {
                return;
            }
            
            textFieldChanged = false;
            // This file will be the default file the file chooser
            ImportExportFileChooser.setCurrentFilePath( getImportFilePath() );
            
            // start a new thread to read in the data
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    PortableEjbDataSource[] ejbDataSources = ImportEjbDataSourcesHelper.readDataSourceImports( getImportFilePath() );
                    if( ejbDataSources != null )
                        setEjbDataSources( ejbDataSources );
                    else
                        return;
                }
            });
        }
    }//GEN-LAST:event_fileNameTextFieldActionPerformed
    
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        
        // Chooser a file to import from
        
        ImportExportFileChooser fileChooser = new ImportExportFileChooser( this );
        String selectedFile = fileChooser.getImportFile();
        
        if( selectedFile != null )
        {
            if( !(new File(selectedFile)).exists() ) 
            {
                String msg = NbBundle.getMessage(ImportEjbDataSourcesPanel.class, "IMPORT_FILE_NOT_FOUND", selectedFile );
                NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
                return;
            }
            
            fileChooser.setCurrentFilePath( selectedFile );
            fileNameTextField.setText(selectedFile);
            
            // No need to check file existence here because it is done in the file chooser
            
            // start a new thread to read in the data
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    PortableEjbDataSource[] ejbDataSources = ImportEjbDataSourcesHelper.readDataSourceImports( getImportFilePath() );
                    if( ejbDataSources != null )
                        setEjbDataSources( ejbDataSources );
                    else
                        return;
                }
            });
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JTextField fileNameTextField;
    private javax.swing.JPanel filePanel;
    private javax.swing.JPanel propertiesPanel;
    private javax.swing.JPanel selectionPanel;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables
    
}
