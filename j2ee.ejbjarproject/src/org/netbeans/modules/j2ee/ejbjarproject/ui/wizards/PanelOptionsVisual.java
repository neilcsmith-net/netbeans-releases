/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbjarproject.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModuleContainer;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eePlatform;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

public class PanelOptionsVisual extends javax.swing.JPanel {
    
//    private static boolean lastMainClassCheck = false; // XXX Store somewhere
    
    private PanelConfigureProject panel;
    
    private java.util.List serverInstanceIDs;
    
    private List earProjects;
    
    private static final String J2EE_SPEC_14_LABEL = NbBundle.getMessage(PanelOptionsVisual.class, "J2EESpecLevel_14"); //NOI18N
//    private String j2eeLevel;
    
    /** Creates new form PanelOptionsVisual */
    public PanelOptionsVisual(PanelConfigureProject panel) {
        initComponents();
        this.panel = panel;
        initServerInstances();
        initEnterpriseApplications();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setAsMainCheckBox = new javax.swing.JCheckBox();
        j2eeSpecLabel = new javax.swing.JLabel();
        j2eeSpecComboBox = new javax.swing.JComboBox();
        serverInstanceLabel = new javax.swing.JLabel();
        serverInstanceComboBox = new javax.swing.JComboBox();
        addToAppLabel = new javax.swing.JLabel();
        addToAppComboBox = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        setAsMainCheckBox.setMnemonic(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_NWP1_SetAsMain_CheckBoxMnemonic").charAt(0));
        setAsMainCheckBox.setSelected(true);
        setAsMainCheckBox.setText(NbBundle.getBundle("org/netbeans/modules/j2ee/ejbjarproject/ui/wizards/Bundle").getString("LBL_NWP1_SetAsMain_CheckBox"));
        setAsMainCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(setAsMainCheckBox, gridBagConstraints);
        setAsMainCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACS_LBL_NWP1_SetAsMain_A11YDesc"));

        j2eeSpecLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_NWP1_J2EESpecLevel_CheckBoxMnemonic").charAt(0));
        j2eeSpecLabel.setLabelFor(j2eeSpecComboBox);
        j2eeSpecLabel.setText(NbBundle.getBundle("org/netbeans/modules/j2ee/ejbjarproject/ui/wizards/Bundle").getString("LBL_NWP1_J2EESpecLevel_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 24, 11);
        add(j2eeSpecLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 24, 0);
        add(j2eeSpecComboBox, gridBagConstraints);
        j2eeSpecComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACS_LBL_NPW1_J2EESpecLevel_A11YDesc"));

        serverInstanceLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_NWP1_Server_LabelMnemonic").charAt(0));
        serverInstanceLabel.setLabelFor(serverInstanceComboBox);
        serverInstanceLabel.setText(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_NWP1_Server_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 11);
        add(serverInstanceLabel, gridBagConstraints);

        serverInstanceComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serverInstanceComboBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(serverInstanceComboBox, gridBagConstraints);
        serverInstanceComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACS_NEJB_Server_ComboBox_A11YDesc"));

        addToAppLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("LBL_NWP1_AddToEApp_CheckBoxMnemonic").charAt(0));
        addToAppLabel.setLabelFor(addToAppComboBox);
        addToAppLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbjarproject/ui/wizards/Bundle").getString("LBL_NWP1_AddToEApp_CheckBox"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        add(addToAppLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(addToAppComboBox, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void serverInstanceComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverInstanceComboBoxActionPerformed
        String prevSelectedItem = (String)j2eeSpecComboBox.getSelectedItem();
        String servInsID = (String)serverInstanceIDs.get(serverInstanceComboBox.getSelectedIndex());
        J2eePlatform j2eePlatform = Deployment.getDefault().getJ2eePlatform(servInsID);
        Set supportedVersions = j2eePlatform.getSupportedSpecVersions();
        j2eeSpecComboBox.removeAllItems();
        if (supportedVersions.contains(J2eeModule.J2EE_14)) j2eeSpecComboBox.addItem(J2EE_SPEC_14_LABEL);
        if (prevSelectedItem != null) {
            j2eeSpecComboBox.setSelectedItem(prevSelectedItem);
        }
    }//GEN-LAST:event_serverInstanceComboBoxActionPerformed
    
    boolean valid(WizardDescriptor wizardDescriptor) {
        if (getSelectedServer() == null) {
            String errMsg = NbBundle.getMessage(PanelOptionsVisual.class, "MSG_NoServer");
            wizardDescriptor.putProperty( "WizardPanel_errorMessage", errMsg); // NOI18N
            return false;
        }
        return true;
    }

    void store(WizardDescriptor d) {
        d.putProperty(WizardProperties.SET_AS_MAIN, setAsMainCheckBox.isSelected() ? Boolean.TRUE : Boolean.FALSE );
        d.putProperty(WizardProperties.SERVER_INSTANCE_ID, getSelectedServer());
        d.putProperty(WizardProperties.J2EE_LEVEL, getSelectedJ2eeSpec());
        d.putProperty(WizardProperties.EAR_APPLICATION, getSelectedEarApplication());
    }
    
    void read(WizardDescriptor d) {
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox addToAppComboBox;
    private javax.swing.JLabel addToAppLabel;
    private javax.swing.JComboBox j2eeSpecComboBox;
    private javax.swing.JLabel j2eeSpecLabel;
    private javax.swing.JComboBox serverInstanceComboBox;
    private javax.swing.JLabel serverInstanceLabel;
    private javax.swing.JCheckBox setAsMainCheckBox;
    // End of variables declaration//GEN-END:variables

    private void initServerInstances() {
        String[] servInstIDs = Deployment.getDefault().getServerInstanceIDs();
        serverInstanceIDs = new ArrayList();
        for (int i = 0; i < servInstIDs.length; i++) {
            J2eePlatform j2eePlat = Deployment.getDefault().getJ2eePlatform(servInstIDs[i]);
            String servInstDisplayName = Deployment.getDefault().getServerInstanceDisplayName(servInstIDs[i]);
            if (servInstDisplayName != null && !servInstDisplayName.equals("")
                && j2eePlat != null && j2eePlat.getSupportedModuleTypes().contains(J2eeModule.EJB)) {
                serverInstanceIDs.add(servInstIDs[i]);
                serverInstanceComboBox.addItem(servInstDisplayName);
            }
        }
        if (serverInstanceIDs.size() > 0) {
            serverInstanceComboBox.setSelectedIndex(0);
        } else {
            serverInstanceComboBox.setEnabled(false);
            j2eeSpecComboBox.setEnabled(false);
        }
    }
    
    private String getSelectedJ2eeSpec() {
        Object item = j2eeSpecComboBox.getSelectedItem();
        return item == null ? null
                            : item.equals(J2EE_SPEC_14_LABEL) ? J2eeModule.J2EE_14 : J2eeModule.J2EE_13;
    }
    
    private String getSelectedServer() {
        int idx = serverInstanceComboBox.getSelectedIndex();
        return idx == -1 ? null 
                         : (String)serverInstanceIDs.get(idx);
    }
    
    private Project getSelectedEarApplication() {
        int idx = addToAppComboBox.getSelectedIndex();
        return (idx <= 0) ? null : (Project) earProjects.get(idx - 1);
    }
    
    private void initEnterpriseApplications() {
        addToAppComboBox.addItem(NbBundle.getMessage(PanelOptionsVisual.class, "LBL_NWP1_AddToEApp_None"));
        addToAppComboBox.setSelectedIndex(0);
        
        Project[] allProjects = OpenProjects.getDefault().getOpenProjects();
        earProjects = new ArrayList();
        for (int i = 0; i < allProjects.length; i++) {
            J2eeModuleContainer container = (J2eeModuleContainer) allProjects[i].getLookup().lookup(J2eeModuleContainer.class);
            ProjectInformation projectInfo = (ProjectInformation) allProjects[i].getLookup().lookup(ProjectInformation.class);
            if (container != null && projectInfo != null) {
                earProjects.add(projectInfo.getProject());
                addToAppComboBox.addItem(projectInfo.getDisplayName());
            }
        }
        if (earProjects.size() <= 0) {
            addToAppComboBox.setEnabled(false);
        }
    }
}

