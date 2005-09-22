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

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.SortedSet;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.modules.apisupport.project.ui.UIUtil;
import org.netbeans.modules.apisupport.project.universe.LocalizedBundleInfo;

/**
 * Represents <em>Display</em> panel in Netbeans Module customizer.
 *
 * @author mkrauskopf
 */
final class CustomizerDisplay extends NbPropertyPanel.Single {
    
    private boolean disabled;
    
    /** Creates new form CustomizerDisplay */
    CustomizerDisplay(final SingleModuleProperties props) {
        super(props, CustomizerDisplay.class);
        initComponents();
        refresh();
    }
    
    void refresh() {
        this.disabled = getBundle() == null;
        if (disabled) {
            nameValue.setEnabled(false);
            categoryValue.setEnabled(false);
            shortDescValue.setEnabled(false);
            longDescValue.setEnabled(false);
        } else {
            readFromProperties();
        }
    }
    
    public void store() {
        if (!disabled) {
            getBundle().setDisplayName(nameValue.getText());
            getBundle().setCategory(getSelectedCategory());
            getBundle().setShortDescription(shortDescValue.getText());
            getBundle().setLongDescription(longDescValue.getText());
        }
    }
    
    private LocalizedBundleInfo getBundle() {
        return getProperties().getBundleInfo();
    }
    
    private void readFromProperties() {
        UIUtil.setText(nameValue, getBundle().getDisplayName());
        UIUtil.setText(shortDescValue, getBundle().getShortDescription());
        longDescValue.setText(getBundle().getLongDescription());
        fillUpCategoryValue();
    }
    
    private void fillUpCategoryValue() {
        categoryValue.setEnabled(false);
        categoryValue.setModel(ComponentFactory.COMBO_WAIT_MODEL);
        categoryValue.setSelectedItem(ComponentFactory.WAIT_VALUE);
        ModuleProperties.RP.post(new Runnable() {
            public void run() {
                final SortedSet moduleCategories = getProperties().getModuleCategories();
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        DefaultComboBoxModel model = new DefaultComboBoxModel();
                        categoryValue.removeAllItems();
                        for (Iterator it = moduleCategories.iterator(); it.hasNext(); ) {
                            model.addElement(it.next());
                        }
                        if (!moduleCategories.contains(getCategory())) {
                            // put module's own category at the beginning
                            model.insertElementAt(getCategory(), 0);
                        }
                        categoryValue.setModel(model);
                        categoryValue.setSelectedItem(getCategory());
                        categoryValue.setEnabled(true);
                    }
                });
            }
        });
    }
    
    private String getCategory() {
        String category = getBundle().getCategory();
        return category != null ? category : ""; // NOI18N
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        if (SingleModuleProperties.NB_PLATFORM_PROPERTY == evt.getPropertyName()) {
            fillUpCategoryValue();
        }
    }
    
    private String getSelectedCategory() {
        String cat = (String) categoryValue.getSelectedItem();
        return ComponentFactory.WAIT_VALUE == cat ? getCategory() : cat;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        name = new javax.swing.JLabel();
        nameValue = new javax.swing.JTextField();
        category = new javax.swing.JLabel();
        categoryValue = new javax.swing.JComboBox();
        shortDesc = new javax.swing.JLabel();
        shortDescValue = new javax.swing.JTextField();
        longDesc = new javax.swing.JLabel();
        hackPanel = new javax.swing.JPanel();
        longDescValueSP = new javax.swing.JScrollPane();
        longDescValue = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        name.setLabelFor(nameValue);
        org.openide.awt.Mnemonics.setLocalizedText(name, org.openide.util.NbBundle.getMessage(CustomizerDisplay.class, "LBL_DisplayName"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 6);
        add(name, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 6);
        add(nameValue, gridBagConstraints);

        category.setLabelFor(categoryValue);
        org.openide.awt.Mnemonics.setLocalizedText(category, org.openide.util.NbBundle.getMessage(CustomizerDisplay.class, "LBL_DisplayCategory"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 6);
        add(category, gridBagConstraints);

        categoryValue.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 6);
        add(categoryValue, gridBagConstraints);

        shortDesc.setLabelFor(shortDescValue);
        org.openide.awt.Mnemonics.setLocalizedText(shortDesc, org.openide.util.NbBundle.getMessage(CustomizerDisplay.class, "LBL_ShortDescription"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 6);
        add(shortDesc, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 6);
        add(shortDescValue, gridBagConstraints);

        longDesc.setLabelFor(longDescValue);
        org.openide.awt.Mnemonics.setLocalizedText(longDesc, org.openide.util.NbBundle.getMessage(CustomizerDisplay.class, "LBL_LongDescription"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 6);
        add(longDesc, gridBagConstraints);

        hackPanel.setLayout(new java.awt.BorderLayout());

        longDescValue.setLineWrap(true);
        longDescValue.setRows(10);
        longDescValue.setWrapStyleWord(true);
        longDescValueSP.setViewportView(longDescValue);

        hackPanel.add(longDescValueSP, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(hackPanel, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel category;
    private javax.swing.JComboBox categoryValue;
    private javax.swing.JPanel hackPanel;
    private javax.swing.JLabel longDesc;
    private javax.swing.JTextArea longDescValue;
    private javax.swing.JScrollPane longDescValueSP;
    private javax.swing.JLabel name;
    private javax.swing.JTextField nameValue;
    private javax.swing.JLabel shortDesc;
    private javax.swing.JTextField shortDescValue;
    // End of variables declaration//GEN-END:variables
}
