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
import java.awt.event.ActionEvent;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.util.RequestProcessor;

/**
 * Represents panel for adding new dependency for a module. Shown after
 * <em>Add</em> button on the <code>CustomizerLibraries</code> panel has been
 * pushed.
 *
 * @author  mkrauskopf
 */
final class AddModulePanel extends JPanel {
    
    private ComponentFactory.DependencyListModel universeModules;
    private RequestProcessor.Task filterTask;
    private AddModuleFilter filterer;
    
    AddModulePanel(ComponentFactory.DependencyListModel universeModules) {
        this.universeModules = universeModules;
        initComponents();
        moduleList.setModel(universeModules);
        moduleList.setCellRenderer(ComponentFactory.getDependencyCellRenderer(true));
        moduleList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                showDescription();
            }
        });
        filterValue.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                search();
            }
            public void removeUpdate(DocumentEvent e) {
                search();
            }
            public void changedUpdate(DocumentEvent e) {}
        });
        // Make basic navigation commands from the list work from the text field.
        String[] listNavCommands = {
            "selectPreviousRow", // NOI18N
            "selectNextRow", // NOI18N
            "selectFirstRow", // NOI18N
            "selectLastRow", // NOI18N
            "scrollUp", // NOI18N
            "scrollDown", // NOI18N
        };
        InputMap listBindings = moduleList.getInputMap();
        KeyStroke[] listBindingKeys = listBindings.allKeys();
        ActionMap listActions = moduleList.getActionMap();
        InputMap textBindings = filterValue.getInputMap();
        ActionMap textActions = filterValue.getActionMap();
        for (int i = 0; i < listNavCommands.length; i++) {
            String command = listNavCommands[i];
            final Action orig = listActions.get(command);
            if (orig == null) {
                continue;
            }
            textActions.put(command, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    orig.actionPerformed(new ActionEvent(moduleList, e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers()));
                }
            });
            for (int j = 0; j < listBindingKeys.length; j++) {
                if (listBindings.get(listBindingKeys[j]).equals(command)) {
                    textBindings.put(listBindingKeys[j], command);
                }
            }
        }
    }
    
    private void showDescription() {
        ModuleDependency dep = getSelectedDependency();
        descValue.setText(dep == null ? "" : // NOI18N
            dep.getModuleEntry().getLongDescription());
    }
    
    ModuleDependency getSelectedDependency() {
        Object o = moduleList.getSelectedValue();
        if (o == ComponentFactory.PLEASE_WAIT) {
            return null;
        } else {
            return (ModuleDependency) o;
        }
    }
    
    private void search() {
        if (filterTask != null) {
            filterTask.cancel();
            filterTask = null;
        }
        final String text = filterValue.getText();
        if (text.length() == 0) {
            moduleList.setModel(universeModules);
            moduleList.setSelectedIndex(0);
            moduleList.ensureIndexIsVisible(0);
        } else {
            DefaultListModel dummy = new DefaultListModel();
            dummy.addElement(ComponentFactory.PLEASE_WAIT);
            moduleList.setModel(dummy);
            filterTask = RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    if (filterer == null) {
                        filterer = new AddModuleFilter(universeModules.getDependencies());
                    }
                    final Set/*<ModuleDependency>*/ matches = filterer.getMatches(text);
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            moduleList.setModel(ComponentFactory.createDependencyListModel(matches));
                            int index = matches.isEmpty() ? -1 : 0;
                            moduleList.setSelectedIndex(index);
                            moduleList.ensureIndexIsVisible(index);
                        }
                    });
                    filterTask = null;
                }
            });
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        moduleLabel = new javax.swing.JLabel();
        moduleSP = new javax.swing.JScrollPane();
        moduleList = new javax.swing.JList();
        descLabel = new javax.swing.JLabel();
        hackPanel = new javax.swing.JPanel();
        descValueSP = new javax.swing.JScrollPane();
        descValue = new javax.swing.JTextArea();
        filter = new javax.swing.JLabel();
        filterValue = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(6, 6, 6, 6)));
        setPreferredSize(new java.awt.Dimension(400, 300));
        moduleLabel.setLabelFor(moduleList);
        org.openide.awt.Mnemonics.setLocalizedText(moduleLabel, org.openide.util.NbBundle.getMessage(AddModulePanel.class, "LBL_Module"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(24, 0, 0, 0);
        add(moduleLabel, gridBagConstraints);

        moduleList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        moduleSP.setViewportView(moduleList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(moduleSP, gridBagConstraints);

        descLabel.setLabelFor(descValue);
        org.openide.awt.Mnemonics.setLocalizedText(descLabel, org.openide.util.NbBundle.getMessage(AddModulePanel.class, "LBL_Description"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(24, 0, 0, 0);
        add(descLabel, gridBagConstraints);

        hackPanel.setLayout(new java.awt.BorderLayout());

        descValue.setEditable(false);
        descValue.setLineWrap(true);
        descValue.setRows(4);
        descValue.setWrapStyleWord(true);
        descValue.setDisabledTextColor(java.awt.Color.black);
        descValueSP.setViewportView(descValue);

        hackPanel.add(descValueSP, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(hackPanel, gridBagConstraints);

        filter.setLabelFor(filterValue);
        org.openide.awt.Mnemonics.setLocalizedText(filter, org.openide.util.NbBundle.getMessage(AddModulePanel.class, "LBL_Filter"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(filter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(filterValue, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel descLabel;
    private javax.swing.JTextArea descValue;
    private javax.swing.JScrollPane descValueSP;
    private javax.swing.JLabel filter;
    private javax.swing.JTextField filterValue;
    private javax.swing.JPanel hackPanel;
    private javax.swing.JLabel moduleLabel;
    private javax.swing.JList moduleList;
    private javax.swing.JScrollPane moduleSP;
    // End of variables declaration//GEN-END:variables
    
}
