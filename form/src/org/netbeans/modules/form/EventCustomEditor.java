/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.form;

import java.util.Vector;
import javax.swing.JList;
import javax.swing.DefaultListModel;

import org.openide.TopManager;
import org.openide.NotifyDescriptor;

/**
 *
 * @author  Pavel Buzek
 */
public class EventCustomEditor extends javax.swing.JPanel {

    static final long serialVersionUID =-4825059521634962952L;

    /** Creates new form EventCustomEditor */
    public EventCustomEditor(EventProperty eventProperty) {
        this.eventProperty = eventProperty;
        changes = eventProperty.new HandlerSetChange();

        initComponents();
        enableButtons();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jScrollPane1 = new javax.swing.JScrollPane();
        Vector h = eventProperty.event.getHandlers();
        for (int i=0, n=h.size (); i < n; i++) {
            handlersModel.addElement(((EventHandler) (h.get(i))).getName());
        }
        handlersList = new javax.swing.JList();
        handlersList.setModel(handlersModel);
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        setPreferredSize(new java.awt.Dimension(300, 300));
        
        
        handlersList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
              public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                  handlersListValueChanged(evt);
              }
          }
          );
          jScrollPane1.setViewportView(handlersList);
          
          
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridheight = 4;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(8, 8, 8, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 0.9;
        gridBagConstraints1.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints1);
        
        
        addButton.setText(FormEditor.getFormBundle ().getString ("CTL_EE_ADD"));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        }
        );
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(8, 8, 0, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints1.weightx = 0.1;
        add(addButton, gridBagConstraints1);
        
        
        removeButton.setText(FormEditor.getFormBundle ().getString ("CTL_EE_REMOVE"));
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        }
        );
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(8, 8, 0, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints1.weightx = 0.1;
        add(removeButton, gridBagConstraints1);
        
        
        editButton.setText(FormEditor.getFormBundle ().getString ("CTL_EE_RENAME"));
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        }
        );
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(8, 8, 0, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints1.weightx = 0.1;
        add(editButton, gridBagConstraints1);
        
    }//GEN-END:initComponents

    private void handlersListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_handlersListValueChanged
        enableButtons();
    }//GEN-LAST:event_handlersListValueChanged

    private void enableButtons() {
        if (handlersList.isSelectionEmpty()) {
            removeButton.setEnabled(false);
        } else {
            removeButton.setEnabled(true);
        }
        editButton.setEnabled(handlersList.getSelectedIndices().length == 1);
    }
        
    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        // Add your handling code here:
        int i = handlersList.getSelectedIndex();
        if (i >= 0) {
            String oldName = (String) handlersModel.get(i);
            NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine(FormEditor.getFormBundle().getString("CTL_EE_RENAME_LABEL"),
                                                   FormEditor.getFormBundle().getString("CTL_EE_RENAME_CAPTION"));
            nd.setInputText(oldName);

            if (TopManager.getDefault().notify(nd).equals(NotifyDescriptor.OK_OPTION)) {
                String newName = nd.getInputText();
                if (newName.equals(oldName)) return; // no change

                if (!org.openide.util.Utilities.isJavaIdentifier(newName)) { // invalid name
                    NotifyDescriptor.Message msg = new NotifyDescriptor.Message(FormEditor.getFormBundle().getString("CTL_EE_NOT_IDENTIFIER"), NotifyDescriptor.ERROR_MESSAGE);
                    TopManager.getDefault().notify(msg);
                    return;
                }

                if (handlersModel.indexOf(newName) >= 0) { // already exists
                    NotifyDescriptor.Message msg = new NotifyDescriptor.Message(FormEditor.getFormBundle().getString("CTL_EE_ALREADY_EXIST"), NotifyDescriptor.INFORMATION_MESSAGE);
                    TopManager.getDefault().notify(msg);
                    return;
                }

                int ii = changes.getAdded().indexOf(oldName);
                if (ii >= 0) { // a newly added handler was renamed
                    changes.getAdded().set(ii,newName);
                }
                else {
                    ii = changes.getRenamedNewNames().indexOf(oldName);
                    if (ii >= 0) // this handler has been already renamed
                        changes.getRenamedNewNames().set(ii, newName);
                    else {
                        changes.getRenamedOldNames().add(oldName);
                        changes.getRenamedNewNames().add(newName);
                    }
                }

                handlersModel.set(i,newName);
                handlersList.setSelectedIndex(i);
                enableButtons();
            }
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        Object[] handlers = handlersList.getSelectedValues();
        for (int i=0; i < handlers.length; i++) {
            int ii = changes.getAdded().indexOf(handlers[i]);
            if (ii >= 0) { // the handler was previously added - cancel it
                changes.getAdded().remove(ii);
            }
            else {
                ii = changes.getRenamedNewNames().indexOf(handlers[i]);
                String toRemove;
                if (ii >= 0) { // the handler was previously renamed - cancel it
                    changes.getRenamedNewNames().remove(ii);
                    toRemove = (String) changes.getRenamedOldNames().get(ii);
                    changes.getRenamedOldNames().remove(ii);
                }
                else toRemove = (String) handlers[i];

                changes.getRemoved().add(toRemove);
            }
            handlersModel.removeElement(handlers[i]);
            enableButtons();
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine(FormEditor.getFormBundle().getString("CTL_EE_ADD_LABEL"), FormEditor.getFormBundle().getString("CTL_EE_ADD_CAPTION"));
        if (TopManager.getDefault().notify(nd).equals(NotifyDescriptor.OK_OPTION)) {
            String newHandler = nd.getInputText();
            if (!org.openide.util.Utilities.isJavaIdentifier(newHandler)) {
                NotifyDescriptor.Message msg = new NotifyDescriptor.Message(FormEditor.getFormBundle().getString("CTL_EE_NOT_IDENTIFIER"), NotifyDescriptor.ERROR_MESSAGE);
                TopManager.getDefault().notify(msg);
                return;
            }

            if (handlersModel.indexOf(newHandler) >= 0) {
                NotifyDescriptor.Message msg = new NotifyDescriptor.Message(FormEditor.getFormBundle().getString("CTL_EE_ALREADY_EXIST"), NotifyDescriptor.INFORMATION_MESSAGE);
                TopManager.getDefault().notify(msg);
                return;
            }
                
            int ir = changes.getRemoved().indexOf(newHandler);
            if (ir >= 0) {
                changes.getRemoved().remove(ir);
            }
            else {
                changes.getAdded().add(newHandler);
            }
            handlersModel.addElement(newHandler);
            handlersList.setSelectedIndex(handlersModel.size() - 1);
            enableButtons();
        }
    }//GEN-LAST:event_addButtonActionPerformed

    public void doChanges() {
        try {
            eventProperty.setValue(changes);
        } catch (Exception e) { // should not happen
            e.printStackTrace();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList handlersList;
    private javax.swing.JButton addButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton editButton;
    // End of variables declaration//GEN-END:variables

    EventProperty eventProperty;
    DefaultListModel handlersModel = new DefaultListModel();
    EventProperty.HandlerSetChange changes;
}
