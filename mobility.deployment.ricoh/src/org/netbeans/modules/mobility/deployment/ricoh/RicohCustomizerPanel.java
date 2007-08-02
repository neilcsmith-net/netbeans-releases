/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. 
 * Portions Copyrighted 2006 Ricoh Corporation 
 * All Rights Reserved.
 */

package org.netbeans.modules.mobility.deployment.ricoh;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import javax.swing.text.JTextComponent;
import org.netbeans.api.mobility.project.ui.customizer.ProjectProperties;
import org.openide.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.Set;
import org.netbeans.modules.mobility.project.DefaultPropertiesDescriptor;
import org.netbeans.spi.mobility.project.ui.customizer.CustomizerPanel;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.PropertyUtils;

/**
 * Customizes the deployment configuration GUI for 
 *
 * @author  esanchez
 */
public class RicohCustomizerPanel extends javax.swing.JPanel implements CustomizerPanel
{    

    private final EditableProperties ep;        
    private File deployKeyContainingDirectory = null;
    
    ProjectProperties actProps;
    String            actConfig;
    
    final private ActionListener fieldListener;
            
    public static final String DEPLOYMENT_PREFIX = "deployments."; //NOI18N            
    
    RicohCustomizerPanel()
    {
        initComponents();
        ep = PropertyUtils.getGlobalProperties();
        DeploymentPanels panel=new DeploymentPanels();
        mainConfigPanel.add(panel);            
        fieldListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                updateTooltips();
            }
        };
        addTextFieldListeners();
    }
    
    private void updateTooltips()
    {
        //tooltips for signing keys
        this.devKeyAliasTextField.setToolTipText(devKeyAliasTextField.getText());
        this.devKeyManifestTextField.setToolTipText(devKeyManifestTextField.getText());
        this.devKeyPathTextField.setToolTipText(devKeyPathTextField.getText());
    }
    
    public void addTextFieldListeners()
    {
        //tooltips for signing keys
        this.devKeyAliasTextField.addActionListener(fieldListener);
        this.devKeyManifestTextField.addActionListener(fieldListener);
        this.devKeyPathTextField.addActionListener(fieldListener);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        mainConfigPanel = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        devKeyPathTextField = new javax.swing.JTextField();
        browseSigningKeyButton = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        devKeyManifestTextField = new javax.swing.JTextField();
        browseManifestButton = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        devKeyPasswordField = new javax.swing.JPasswordField();
        jLabel27 = new javax.swing.JLabel();
        devKeyAliasTextField = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        deployMethodComboBox = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();

        setPreferredSize(new java.awt.Dimension(600, 400));
        getAccessibleContext().setAccessibleDescription(null);
        mainConfigPanel.setLayout(new java.awt.BorderLayout());

        jLabel28.setText(org.openide.util.NbBundle.getMessage(RicohCustomizerPanel.class, "LBL_SecurityCertification"));
        jLabel28.getAccessibleContext().setAccessibleName(null);
        jLabel28.getAccessibleContext().setAccessibleDescription(null);

        jLabel25.setLabelFor(devKeyPathTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel25, java.util.ResourceBundle.getBundle("org/netbeans/modules/mobility/deployment/ricoh/Bundle").getString("LBL_SignatureKey"));
        jLabel25.getAccessibleContext().setAccessibleName(null);
        jLabel25.getAccessibleContext().setAccessibleDescription(null);

        devKeyPathTextField.setName(RicohDeploymentProperties.PROP_RICOH_SIGN_KEYFILE);
        devKeyPathTextField.getAccessibleContext().setAccessibleDescription(null);

        org.openide.awt.Mnemonics.setLocalizedText(browseSigningKeyButton, java.util.ResourceBundle.getBundle("org/netbeans/modules/mobility/deployment/ricoh/Bundle").getString("LBL_BrowseSignature"));
        browseSigningKeyButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                browseSigningKeyButtonActionPerformed(evt);
            }
        });

        browseSigningKeyButton.getAccessibleContext().setAccessibleName(null);
        browseSigningKeyButton.getAccessibleContext().setAccessibleDescription(null);

        jLabel29.setLabelFor(devKeyManifestTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel29, java.util.ResourceBundle.getBundle("org/netbeans/modules/mobility/deployment/ricoh/Bundle").getString("LBL_Manifest"));
        jLabel29.getAccessibleContext().setAccessibleName(null);
        jLabel29.getAccessibleContext().setAccessibleDescription(null);

        devKeyManifestTextField.setName(RicohDeploymentProperties.PROP_RICOH_SIGN_MANIFEST);
        devKeyManifestTextField.getAccessibleContext().setAccessibleDescription(null);

        org.openide.awt.Mnemonics.setLocalizedText(browseManifestButton, java.util.ResourceBundle.getBundle("org/netbeans/modules/mobility/deployment/ricoh/Bundle").getString("LBL_BrowseManifest"));
        browseManifestButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                browseManifestButtonActionPerformed(evt);
            }
        });

        browseManifestButton.getAccessibleContext().setAccessibleName(null);
        browseManifestButton.getAccessibleContext().setAccessibleDescription(null);

        jLabel26.setLabelFor(devKeyPasswordField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel26, java.util.ResourceBundle.getBundle("org/netbeans/modules/mobility/deployment/ricoh/Bundle").getString("LBL_KeyPassword"));
        jLabel26.getAccessibleContext().setAccessibleName(null);
        jLabel26.getAccessibleContext().setAccessibleDescription(null);

        devKeyPasswordField.setName(RicohDeploymentProperties.PROP_RICOH_SIGN_KEYPASS);
        devKeyPasswordField.getAccessibleContext().setAccessibleDescription(null);

        jLabel27.setLabelFor(devKeyAliasTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel27, java.util.ResourceBundle.getBundle("org/netbeans/modules/mobility/deployment/ricoh/Bundle").getString("LBL_KeyAlias"));
        jLabel27.getAccessibleContext().setAccessibleName(null);
        jLabel27.getAccessibleContext().setAccessibleDescription(null);

        devKeyAliasTextField.setName(RicohDeploymentProperties.PROP_RICOH_SIGN_ALIAS);
        devKeyAliasTextField.getAccessibleContext().setAccessibleDescription(null);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel31, NbBundle.getMessage(RicohCustomizerPanel.class, "LBL_DeploymentMethod"));

        deployMethodComboBox.setModel(new DeploymentComboBoxModel(DeploymentComboBoxModel.deployPropStr));
        deployMethodComboBox.setName(RicohDeploymentProperties.PROP_RICOH_DEPLOY_METHOD);
        deployMethodComboBox.setRenderer(new DeploymentComboBoxModel.DeployMethodRenderer());
        deployMethodComboBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                deployMethodComboBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, mainConfigPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(jLabel31)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(deployMethodComboBox, 0, 385, Short.MAX_VALUE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(jLabel26)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(devKeyPasswordField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel27)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(devKeyAliasTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel28)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(jLabel25)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(devKeyPathTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                                    .add(devKeyManifestTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(jLabel29)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 444, Short.MAX_VALUE)))
                        .add(12, 12, 12)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(browseSigningKeyButton)
                            .add(browseManifestButton))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel28)
                .add(7, 7, 7)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel25)
                    .add(browseSigningKeyButton)
                    .add(devKeyPathTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel29)
                    .add(devKeyManifestTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(browseManifestButton))
                .add(6, 6, 6)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel26)
                    .add(devKeyPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel27)
                    .add(devKeyAliasTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(12, 12, 12)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel31)
                    .add(deployMethodComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainConfigPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {devKeyAliasTextField, devKeyManifestTextField, devKeyPasswordField, devKeyPathTextField}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents

    private void deployMethodComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_deployMethodComboBoxActionPerformed
    {//GEN-HEADEREND:event_deployMethodComboBoxActionPerformed
        updateDeployment();
    }//GEN-LAST:event_deployMethodComboBoxActionPerformed

    private void browseManifestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseManifestButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
        chooser.setDialogTitle(NbBundle.getMessage(RicohCustomizerPanel.class, "TITLE_ManifestSelect")); //NOI18N
        int answer;
        
        //select file in chooser dialog if already has an entry
        if (this.devKeyManifestTextField.getText().trim().equals("") == false)
        {
            File currentSelectedMF = new File(devKeyManifestTextField.getText());
            chooser.setCurrentDirectory(currentSelectedMF.getParentFile());
            chooser.setSelectedFile(currentSelectedMF);
        }
        else
        if (this.deployKeyContainingDirectory != null)
            chooser.setCurrentDirectory(this.deployKeyContainingDirectory);
        
        //show the modal choose dialog and process the outcome
        answer = chooser.showOpenDialog(((JButton)evt.getSource()).getParent());
        if (answer == JFileChooser.APPROVE_OPTION)
        {
            this.devKeyManifestTextField.setText(chooser.getSelectedFile().getAbsolutePath());  
            this.deployKeyContainingDirectory = chooser.getSelectedFile().getParentFile();
        }
    }//GEN-LAST:event_browseManifestButtonActionPerformed

    private void browseSigningKeyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseSigningKeyButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
        chooser.setDialogTitle(NbBundle.getMessage(RicohCustomizerPanel.class, "TITLE_SigningKeySelect")); //NOI18N
        int answer;
        
        //select file in chooser dialog if already has an entry
        if (devKeyPathTextField.getText().trim().equals("") == false)
        {
            File currentSelectedKeyFile = new File(devKeyPathTextField.getText());
            chooser.setCurrentDirectory(currentSelectedKeyFile.getParentFile());
            chooser.setSelectedFile(currentSelectedKeyFile);
        }
        else
        // if a keyfile or manifest had been chosen previously (usually in same dir), navigate first to that directory
        if (deployKeyContainingDirectory != null)
            chooser.setCurrentDirectory(this.deployKeyContainingDirectory);
        
        //show the modal choose dialog and process the outcome
        answer = chooser.showOpenDialog(((JButton)evt.getSource()).getParent());
        if (answer == JFileChooser.APPROVE_OPTION)
        {
            this.devKeyPathTextField.setText(chooser.getSelectedFile().getAbsolutePath());  //can't check validity now, will do it later
            this.deployKeyContainingDirectory = chooser.getSelectedFile().getParentFile();
        }
    }//GEN-LAST:event_browseSigningKeyButtonActionPerformed

    private void updateDeployment()
    {
        String deployment = ((DeploymentComboBoxModel)deployMethodComboBox.getModel()).getSelectedItem().toString();
        
        JPanel panel=(JPanel)mainConfigPanel.getComponent(0);        
        ((CardLayout)panel.getLayout()).show(panel,deployment);            
        this.repaint();   
    }
    
    public void initValues(ProjectProperties props, String configuration)
    {
        actConfig=configuration;
        actProps =props;
        JPanel panel=(JPanel)mainConfigPanel.getComponent(0);        
        updateDeployment();    
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseManifestButton;
    private javax.swing.JButton browseSigningKeyButton;
    private javax.swing.JComboBox deployMethodComboBox;
    private javax.swing.JTextField devKeyAliasTextField;
    private javax.swing.JTextField devKeyManifestTextField;
    private javax.swing.JPasswordField devKeyPasswordField;
    private javax.swing.JTextField devKeyPathTextField;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    javax.swing.JLabel jLabel31;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel mainConfigPanel;
    // End of variables declaration//GEN-END:variables

   
}
