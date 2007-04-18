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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbcore.ui.logicalview.entres;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import org.netbeans.modules.j2ee.deployment.common.api.Datasource;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.ejbcore.ui.EJBPreferences;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;


/**
 * Provide an interface to support datasource reference selection.
 * @author  Chris Webster
 */
public class SelectDatabasePanel extends javax.swing.JPanel {
    
    public static final String IS_VALID = "SelectDatabasePanel_isValid"; //NOI18N
    
    private Color nbErrorForeground;
    
    private Node driverNode;
    protected static final String PROTOTYPE_VALUE = "jdbc:pointbase://localhost/sample [pbpublic on PBPUBLIC] "; //NOI18N
    private final ServiceLocatorStrategyPanel slPanel;
    private final EJBPreferences ejbPreferences;
    private final J2eeModuleProvider provider;
    private final Map<String, Datasource> references;
    private final Set<Datasource> moduleDatasources;
    private final Set<Datasource> serverDatasources;
    
    public SelectDatabasePanel(J2eeModuleProvider provider, String lastLocator, Map<String, Datasource> references,
            Set<Datasource> moduleDatasources, Set<Datasource> serverDatasources) {
        initComponents();
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SelectDatabasePanel.class, "ACSD_ChooseDatabase"));
        ejbPreferences = new EJBPreferences();
        this.provider = provider;
        this.references = references;
        this.moduleDatasources = moduleDatasources;
        this.serverDatasources = serverDatasources;
        
        dsRefCombo.setRenderer(new ReferenceListCellRenderer());
        dsRefCombo.setPrototypeDisplayValue(PROTOTYPE_VALUE);
        populateReferences();
        
        dsRefCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                checkDatasourceReference();
            }
        });
        
        slPanel = new ServiceLocatorStrategyPanel(lastLocator);
        serviceLocatorPanel.add(slPanel, BorderLayout.CENTER);
        createResourcesCheckBox.setSelected(ejbPreferences.isAgreedCreateServerResources());
        slPanel.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ServiceLocatorStrategyPanel.IS_VALID)) {
                    Object newvalue = evt.getNewValue();
                    if (newvalue instanceof Boolean) {
                        boolean isServiceLocatorOk = ((Boolean)newvalue).booleanValue();
                        if (isServiceLocatorOk) {
                            checkDatasourceReference();
                        } else {
                            firePropertyChange(IS_VALID, true, false);
                        }
                    }
                }
            }
        });
    }
    
    public String getDatasourceReference() {
        return (String) dsRefCombo.getSelectedItem();
    }

    public String getServiceLocator() {
        return slPanel.classSelected();
    }
    
    public boolean createServerResources() {
        return createResourcesCheckBox.isSelected();
    }
    
    public Datasource getDatasource() {
        return references.get(getDatasourceReference());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        dsRefLabel = new javax.swing.JLabel();
        serviceLocatorPanel = new javax.swing.JPanel();
        createResourcesCheckBox = new javax.swing.JCheckBox();
        errorLabel = new javax.swing.JLabel();
        dsRefCombo = new javax.swing.JComboBox();
        buttonAdd = new javax.swing.JButton();

        dsRefLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "LBL_ConnectionMnemonic").charAt(0));
        dsRefLabel.setLabelFor(dsRefCombo);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ui/logicalview/entres/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(dsRefLabel, bundle.getString("LBL_DsReference")); // NOI18N

        serviceLocatorPanel.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(createResourcesCheckBox, org.openide.util.NbBundle.getBundle(SelectDatabasePanel.class).getString("LBL_CreateServerResources")); // NOI18N
        createResourcesCheckBox.setToolTipText(org.openide.util.NbBundle.getBundle(SelectDatabasePanel.class).getString("ToolTip_CreateServerResources")); // NOI18N
        createResourcesCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        createResourcesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        createResourcesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createResourcesCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, " ");

        org.openide.awt.Mnemonics.setLocalizedText(buttonAdd, org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "LBL_Add")); // NOI18N
        buttonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(errorLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 377, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(dsRefLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(dsRefCombo, 0, 341, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(buttonAdd))))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(createResourcesCheckBox))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(serviceLocatorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dsRefLabel)
                    .add(dsRefCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(buttonAdd))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createResourcesCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(serviceLocatorPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(25, 25, 25)
                .add(errorLabel))
        );

        dsRefLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "LBL_DsReference")); // NOI18N
        dsRefLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "ACSD_DsReference")); // NOI18N
        dsRefCombo.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "LBL_DsReference")); // NOI18N
        dsRefCombo.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SelectDatabasePanel.class, "ACSD_DsRefCombo")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void buttonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddActionPerformed
        DataSourceReferencePanel referencePanel = new DataSourceReferencePanel(provider, references.keySet(), moduleDatasources, serverDatasources);
        final DialogDescriptor dialogDescriptor = new DialogDescriptor(
                referencePanel,
                NbBundle.getMessage(SelectDatabasePanel.class, "LBL_AddDataSourceReference"), //NOI18N
                true,
                DialogDescriptor.OK_CANCEL_OPTION,
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.DEFAULT_ALIGN,
                HelpCtx.DEFAULT_HELP,
                null);
        
        // register listener
        referencePanel.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (DataSourceReferencePanel.IS_VALID.equals(evt.getPropertyName())) {
                    Object newvalue = evt.getNewValue();
                    if ((newvalue != null) && (newvalue instanceof Boolean)) {
                        dialogDescriptor.setValid(((Boolean) newvalue).booleanValue());
                    }
                }
            }
        });
        
        // initial invalidation
        dialogDescriptor.setValid(false);
        
        // show and eventually save
        Object option = DialogDisplayer.getDefault().notify(dialogDescriptor);
        if (option == NotifyDescriptor.OK_OPTION) {
            final String refName = referencePanel.getReferenceName();
            // TMYSIK: how to save datasource reference?
            references.put(refName, referencePanel.getDataSource());
            if (referencePanel.copyDataSourceToProject()) {
                // TMYSIK how to copy it?
            }
            
            // update gui (needed because of sorting)
            populateReferences();
            // Ensure that the correct item is selected before listeners like FocusListener are called.
            // ActionListener.actionPerformed() is not called if this method is already called from
            // actionPerformed(), in that case selectItemLater should be set to true and setSelectedItem()
            // below is called asynchronously so that the actionPerformed() is called
            dsRefCombo.setSelectedItem(refName);
            
            boolean selectItemLater = false;
            if (selectItemLater) {
                SwingUtilities.invokeLater(new Runnable() { // postpone item selection to enable event firing from JCombobox.setSelectedItem()
                    public void run() {
                        dsRefCombo.setSelectedItem(refName);
                    }
                });
            }
            
        }
    }//GEN-LAST:event_buttonAddActionPerformed
    
    
        
    private void createResourcesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createResourcesCheckBoxActionPerformed
        ejbPreferences.setAgreedCreateServerResources(createResourcesCheckBox.isSelected());
    }//GEN-LAST:event_createResourcesCheckBoxActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAdd;
    private javax.swing.JCheckBox createResourcesCheckBox;
    private javax.swing.JComboBox dsRefCombo;
    private javax.swing.JLabel dsRefLabel;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JPanel serviceLocatorPanel;
    // End of variables declaration//GEN-END:variables
    
    protected void checkDatasourceReference() {
        // TMYSIK is this necessary? or should be any check here (valid reference etc.)?
        if (dsRefCombo.getSelectedItem() instanceof String) {
            firePropertyChange(IS_VALID, false, true);
        } else {
            firePropertyChange(IS_VALID, true, false);
        }
    }

    private void populateReferences() {
        
        SortedSet<String> refNames = new TreeSet<String>(references.keySet());
        
        dsRefCombo.removeAllItems();
        for (String s : refNames) {
            dsRefCombo.addItem(s);
        }
    }
    
    private class ReferenceListCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            if (value instanceof String) {
                String refName = (String) value;
                Datasource ds = references.get(refName);
                StringBuilder sb = new StringBuilder(refName);
                if (ds != null) {
                    sb.append(" ["); // NOI18N
                    sb.append(ds.getUrl());
                    sb.append("] "); // NOI18N
                }
                setText(sb.toString());
            } else {
                // should not get here
                setText(value != null ? value.toString() : ""); // NOI18N
            }
            setToolTipText(""); // NOI18N
            
            return this;
        }

    }
}
