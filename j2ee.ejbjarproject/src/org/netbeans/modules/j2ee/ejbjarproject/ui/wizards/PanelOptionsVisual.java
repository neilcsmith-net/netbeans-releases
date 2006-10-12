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
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbjarproject.ui.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModuleContainer;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eePlatform;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ServerManager;
import org.netbeans.modules.j2ee.ejbjarproject.ui.FoldersListSettings;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

public class PanelOptionsVisual extends javax.swing.JPanel {
    
//    private static boolean lastMainClassCheck = false; // XXX Store somewhere
    
    private PanelConfigureProject panel;
    private J2eeVersionWarningPanel warningPanel;
    private final DefaultComboBoxModel serversModel = new DefaultComboBoxModel();
    
    private List earProjects;
    
    private static final String J2EE_SPEC_15_LABEL = NbBundle.getMessage(PanelOptionsVisual.class, "J2EESpecLevel_15"); //NOI18N
    private static final String J2EE_SPEC_14_LABEL = NbBundle.getMessage(PanelOptionsVisual.class, "J2EESpecLevel_14"); //NOI18N
//    private String j2eeLevel;
    
    /** Creates new form PanelOptionsVisual */
    public PanelOptionsVisual(PanelConfigureProject panel) {
        initComponents();
        this.panel = panel;
        setJ2eeVersionWarningPanel();
        initServers(FoldersListSettings.getDefault().getLastUsedServer());
        // preselect the first item in the j2ee spec combo
        if (j2eeSpecComboBox.getModel().getSize() > 0) {
            j2eeSpecComboBox.setSelectedIndex(0);
        }
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
        warningPlaceHolderPanel = new javax.swing.JPanel();
        manageServersButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setAsMainCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(setAsMainCheckBox, NbBundle.getBundle("org/netbeans/modules/j2ee/ejbjarproject/ui/wizards/Bundle").getString("LBL_NWP1_SetAsMain_CheckBox"));
        setAsMainCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(setAsMainCheckBox, gridBagConstraints);
        setAsMainCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACS_LBL_NWP1_SetAsMain_A11YDesc"));

        j2eeSpecLabel.setLabelFor(j2eeSpecComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(j2eeSpecLabel, NbBundle.getBundle("org/netbeans/modules/j2ee/ejbjarproject/ui/wizards/Bundle").getString("LBL_NWP1_J2EESpecLevel_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 11);
        add(j2eeSpecLabel, gridBagConstraints);

        j2eeSpecComboBox.setPrototypeDisplayValue("MMMMMMMMM" /* "Java EE 5" */);
        j2eeSpecComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                j2eeSpecComboBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(j2eeSpecComboBox, gridBagConstraints);
        j2eeSpecComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACS_LBL_NPW1_J2EESpecLevel_A11YDesc"));

        serverInstanceLabel.setLabelFor(serverInstanceComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(serverInstanceLabel, org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_NWP1_Server_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 11);
        add(serverInstanceLabel, gridBagConstraints);

        serverInstanceComboBox.setModel(serversModel);
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
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 6);
        add(serverInstanceComboBox, gridBagConstraints);
        serverInstanceComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACS_NEJB_Server_ComboBox_A11YDesc"));

        addToAppLabel.setLabelFor(addToAppComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(addToAppLabel, java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbjarproject/ui/wizards/Bundle").getString("LBL_NWP1_AddToEApp_CheckBox"));
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
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 11, 0);
        add(addToAppComboBox, gridBagConstraints);

        warningPlaceHolderPanel.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(warningPlaceHolderPanel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(manageServersButton, org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "LBL_ManageServers"));
        manageServersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageServersButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(manageServersButton, gridBagConstraints);
        manageServersButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ASCN_ManageServers"));
        manageServersButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ASCD_ManageServers"));

    }// </editor-fold>//GEN-END:initComponents

    private void manageServersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageServersButtonActionPerformed
        ServerInstanceWrapper serverInstanceWrapper = (ServerInstanceWrapper) serversModel.getSelectedItem();
        String lastSelectedServerInstanceID = null;
        if (serverInstanceWrapper != null) {
            lastSelectedServerInstanceID = serverInstanceWrapper.getServerInstanceID();
        }
        ServerManager.showCustomizer(lastSelectedServerInstanceID);
        String lastSelectedJ2eeSpecLevel = (String) j2eeSpecComboBox.getSelectedItem();
        // refresh the list of servers
        initServers(lastSelectedServerInstanceID);
        if (lastSelectedJ2eeSpecLevel != null) {
            j2eeSpecComboBox.setSelectedItem(lastSelectedJ2eeSpecLevel);
        }
    }//GEN-LAST:event_manageServersButtonActionPerformed
    
    private void j2eeSpecComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_j2eeSpecComboBoxActionPerformed
        setJ2eeVersionWarningPanel();
    }//GEN-LAST:event_j2eeSpecComboBoxActionPerformed

    private void serverInstanceComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverInstanceComboBoxActionPerformed
        String prevSelectedItem = (String) j2eeSpecComboBox.getSelectedItem();
        // update the j2ee spec list according to the selected server
        ServerInstanceWrapper serverInstanceWrapper = (ServerInstanceWrapper) serversModel.getSelectedItem();
        if (serverInstanceWrapper != null) {
            J2eePlatform j2eePlatform = Deployment.getDefault().getJ2eePlatform(serverInstanceWrapper.getServerInstanceID());
            Set supportedVersions = j2eePlatform.getSupportedSpecVersions(J2eeModule.EJB);
            j2eeSpecComboBox.removeAllItems();
            if (supportedVersions.contains(J2eeModule.JAVA_EE_5)) {
                j2eeSpecComboBox.addItem(J2EE_SPEC_15_LABEL);
            }
            if (supportedVersions.contains(J2eeModule.J2EE_14)) {
                j2eeSpecComboBox.addItem(J2EE_SPEC_14_LABEL);
            }
            if (prevSelectedItem != null) {
                j2eeSpecComboBox.setSelectedItem(prevSelectedItem);
            }
        } else {
            j2eeSpecComboBox.removeAllItems();
        }
        // revalidate the form
        panel.fireChangeEvent();
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
        if (warningPanel != null && warningPanel.getWarningType() != null && warningPanel.getDowngradeAllowed()) {
            d.putProperty(WizardProperties.JAVA_PLATFORM, warningPanel.getSuggestedJavaPlatformName());
            
            String j2ee = getSelectedJ2eeSpec();
            if (j2ee != null) {
                String warningType = J2eeVersionWarningPanel.findWarningType(j2ee);
                FoldersListSettings fls = FoldersListSettings.getDefault();
                String srcLevel = "1.6"; //NOI18N
                if (warningType.equals(J2eeVersionWarningPanel.WARN_SET_SOURCE_LEVEL_14) && fls.isAgreedSetSourceLevel14())
                    srcLevel = "1.4"; //NOI18N
                else if (warningType.equals(J2eeVersionWarningPanel.WARN_SET_SOURCE_LEVEL_15) && fls.isAgreedSetSourceLevel15())
                    srcLevel = "1.5"; //NOI18N
                
                d.putProperty(WizardProperties.SOURCE_LEVEL, srcLevel);
            }            
        } else
            d.putProperty(WizardProperties.SOURCE_LEVEL, null);
    }
    
    void read(WizardDescriptor d) {
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox addToAppComboBox;
    private javax.swing.JLabel addToAppLabel;
    private javax.swing.JComboBox j2eeSpecComboBox;
    private javax.swing.JLabel j2eeSpecLabel;
    private javax.swing.JButton manageServersButton;
    private javax.swing.JComboBox serverInstanceComboBox;
    private javax.swing.JLabel serverInstanceLabel;
    private javax.swing.JCheckBox setAsMainCheckBox;
    private javax.swing.JPanel warningPlaceHolderPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Init servers model
     * @param selectedServerInstanceID preselected instance or null if non is preselected
     */
    private void initServers(String selectedServerInstanceID) {
        // init the list of server instances
        serversModel.removeAllElements();
        Set<ServerInstanceWrapper> servers = new TreeSet<ServerInstanceWrapper>();
        ServerInstanceWrapper selectedItem = null;
        boolean sjasFound = false;
        for (String serverInstanceID : Deployment.getDefault().getServerInstanceIDs()) {
            String displayName = Deployment.getDefault().getServerInstanceDisplayName(serverInstanceID);
            J2eePlatform j2eePlatform = Deployment.getDefault().getJ2eePlatform(serverInstanceID);
            if (displayName != null && j2eePlatform != null && j2eePlatform.getSupportedModuleTypes().contains(J2eeModule.EJB)) {
                ServerInstanceWrapper serverWrapper = new ServerInstanceWrapper(serverInstanceID, displayName);
                // decide whether this server should be preselected
                if (selectedItem == null || !sjasFound) {
                    if (selectedServerInstanceID != null) {
                        if (selectedServerInstanceID.equals(serverInstanceID)) {
                            selectedItem = serverWrapper;
                        }
                    } else {
                        // preselect the best server ;)
                        String shortName = Deployment.getDefault().getServerID(serverInstanceID);
                        if ("J2EE".equals(shortName)) { // NOI18N
                            selectedItem = serverWrapper;
                            sjasFound = true;
                        }
                        else
                        if ("JBoss4".equals(shortName)) { // NOI18N
                            selectedItem = serverWrapper;
                        }
                    }
                }
                servers.add(serverWrapper);
            }
        }
        for (ServerInstanceWrapper item : servers) {
            serversModel.addElement(item);
        }
        if (selectedItem != null) {
            // set the preselected item
            serversModel.setSelectedItem(selectedItem);
        } else if (serversModel.getSize() > 0) {
            // set the first item
            serversModel.setSelectedItem(serversModel.getElementAt(0));
        }
    }
    
    private String getSelectedJ2eeSpec() {
        Object item = j2eeSpecComboBox.getSelectedItem();
        return item == null ? null
                            : item.equals(J2EE_SPEC_14_LABEL) ? J2eeModule.J2EE_14 
                            : item.equals(J2EE_SPEC_15_LABEL) ? J2eeModule.JAVA_EE_5 : J2eeModule.J2EE_13;
    }
    
    private String getSelectedServer() {
        ServerInstanceWrapper serverInstanceWrapper = (ServerInstanceWrapper) serversModel.getSelectedItem();
        if (serverInstanceWrapper == null) {
            return null;
        }
        return serverInstanceWrapper.getServerInstanceID();
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
            ProjectInformation projectInfo = ProjectUtils.getInformation(allProjects[i]);
            if (container != null) {
                earProjects.add(projectInfo.getProject());
                addToAppComboBox.addItem(projectInfo.getDisplayName());
            }
        }
        if (earProjects.size() <= 0) {
            addToAppComboBox.setEnabled(false);
        }
    }
    
    private void setJ2eeVersionWarningPanel() {
        String j2ee = getSelectedJ2eeSpec();
        if (j2ee == null)
            return;
        String warningType = J2eeVersionWarningPanel.findWarningType(j2ee);
        if (warningType == null && warningPanel == null)
            return;
        if (warningPanel == null) {
            warningPanel = new J2eeVersionWarningPanel(warningType);
            warningPlaceHolderPanel.add(warningPanel, java.awt.BorderLayout.CENTER);
            warningPanel.setWarningType(warningType);
        } else {
            warningPanel.setWarningType(warningType);
        }
    }
    
    
    /**
     * Server instance wrapper represents server instances in the servers combobox.
     * @author sherold
     */
    private static class ServerInstanceWrapper implements Comparable {

        private final String serverInstanceID;
        private final String displayName;

        ServerInstanceWrapper(String serverInstanceID, String displayName) {
            this.serverInstanceID = serverInstanceID;
            this.displayName = displayName;
        }

        public String getServerInstanceID() {
            return serverInstanceID;
        }

        public String toString() {
            return displayName;
        }

        public int compareTo(Object o) {
            return toString().compareTo(o.toString());
        }
    }
}

