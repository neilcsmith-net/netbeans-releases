/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.project.ui.customizer;

import java.awt.Dialog;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 * Represents <em>Libraries</em> panel in Netbeans Module customizer.
 *
 * @author mkrauskopf
 */
public class CustomizerLibraries extends JPanel {
    
    private ComponentFactory.ModuleListModel subModulesModel;
    private ComponentFactory.ModuleListModel universeModulesModel;
    
    /** Creates new form CustomizerLibraries */
    public CustomizerLibraries(
            ComponentFactory.ModuleListModel subModules,
            ComponentFactory.ModuleListModel universeModules) {
        initComponents();
        this.subModulesModel = subModules;
        this.universeModulesModel = universeModules;
        dependencyList.setModel(subModules);
        dependencyList.setCellRenderer(ComponentFactory.getModuleCellRenderer());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        modDepLabel = new javax.swing.JLabel();
        depButtonPanel = new javax.swing.JPanel();
        addDepButton = new javax.swing.JButton();
        removeDepButton = new javax.swing.JButton();
        dependencySP = new javax.swing.JScrollPane();
        dependencyList = new javax.swing.JList();

        setLayout(new java.awt.GridBagLayout());

        modDepLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/customizer/Bundle").getString("LBL_ModuleDependencies_Mnem").charAt(0));
        modDepLabel.setLabelFor(dependencyList);
        modDepLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/customizer/Bundle").getString("LBL_ModuleDependencies"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(modDepLabel, gridBagConstraints);

        depButtonPanel.setLayout(new java.awt.GridLayout(2, 1));

        addDepButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/customizer/Bundle").getString("CTL_AddButton_Mnem").charAt(0));
        addDepButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/customizer/Bundle").getString("CTL_AddButton"));
        addDepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addModuleDependency(evt);
            }
        });

        depButtonPanel.add(addDepButton);

        removeDepButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/customizer/Bundle").getString("CTL_RemoveButton_Mnem").charAt(0));
        removeDepButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/apisupport/project/ui/customizer/Bundle").getString("CTL_RemoveButton"));
        removeDepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeModuleDependency(evt);
            }
        });

        depButtonPanel.add(removeDepButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(depButtonPanel, gridBagConstraints);

        dependencySP.setViewportView(dependencyList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(dependencySP, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void removeModuleDependency(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeModuleDependency
        subModulesModel.removeModule(Arrays.asList(dependencyList.getSelectedValues()));
        dependencyList.clearSelection();
    }//GEN-LAST:event_removeModuleDependency
    
    private void addModuleDependency(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addModuleDependency
        Set modulesToAdd = new TreeSet(universeModulesModel.getSubModules());
        modulesToAdd.removeAll(subModulesModel.getSubModules());
        ComponentFactory.ModuleListModel model =
                ComponentFactory.createModuleListModel(modulesToAdd);
        AddModulePanel addPanel = new AddModulePanel(model);
        DialogDescriptor descriptor = new DialogDescriptor(addPanel,
                NbBundle.getMessage(CustomizerLibraries.class,
                "CTL_AddModuleDependencyTitle")); // NOI18N
        Dialog d = DialogDisplayer.getDefault().createDialog(descriptor);
        d.setVisible(true);
        if (descriptor.getValue().equals(DialogDescriptor.OK_OPTION)) {
            subModulesModel.addModule(addPanel.getSelectedModule());
        }
        d.dispose();
    }//GEN-LAST:event_addModuleDependency
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addDepButton;
    private javax.swing.JPanel depButtonPanel;
    private javax.swing.JList dependencyList;
    private javax.swing.JScrollPane dependencySP;
    private javax.swing.JLabel modDepLabel;
    private javax.swing.JButton removeDepButton;
    // End of variables declaration//GEN-END:variables
    
}
