/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.xml.tax.beans.customizer;

import java.beans.PropertyChangeEvent;

import org.netbeans.tax.TreeException;
import org.netbeans.tax.TreeDocumentType;

/**
 *
 * @author  Libor Kramolis
 * @version 0.1
 */
public class TreeDocumentTypeCustomizer extends AbstractTreeCustomizer {

    /** Serial Version UID */
    private static final long serialVersionUID = -6111125131099262050L;

    
    //
    // init
    //

    /** */
    public TreeDocumentTypeCustomizer () {
	super();

        initComponents();
        nameLabel.setDisplayedMnemonic(Util.getChar("MNE_xmlName")); // NOI18N
        systemLabel.setDisplayedMnemonic(Util.getChar("MNE_xmlSystemID")); // NOI18N
        publicLabel.setDisplayedMnemonic(Util.getChar("MNE_xmlPublicID")); // NOI18N
    }


    //
    // itself
    //

    /**
     */
    protected final TreeDocumentType getDocumentType () {
        return (TreeDocumentType)getTreeObject();
    }

    /**
     */
    protected final void safePropertyChange (PropertyChangeEvent pche) {
        if (initializing)
	    return;

        super.safePropertyChange (pche);
        
	if (pche.getPropertyName().equals (TreeDocumentType.PROP_ELEMENT_NAME)) {
	    updateNameComponent();
	} else if (pche.getPropertyName().equals (TreeDocumentType.PROP_PUBLIC_ID)) {
	    updatePublicIdComponent();
	} else if (pche.getPropertyName().equals (TreeDocumentType.PROP_SYSTEM_ID)) {
	    updateSystemIdComponent();
	}
    }

    /**
     */
    protected final void updateDocumentTypeName () {
	try {
	    getDocumentType().setElementName (nameField.getText());
	} catch (TreeException exc) {
	    updateNameComponent();
	    Util.notifyTreeException (exc);
	}
    }
    
    /**
     */
    protected final void updateNameComponent () {
	nameField.setText (getDocumentType().getElementName());
    }
    
    /**
     */
    protected final void updateDocumentTypePublicId () {
	try {
	    getDocumentType().setPublicId (text2null (publicField.getText()));
	} catch (TreeException exc) {
	    updatePublicIdComponent();
	    Util.notifyTreeException (exc);
	}
    }
    
    /**
     */
    protected final void updatePublicIdComponent () {
	publicField.setText (null2text (getDocumentType().getPublicId()));
    }

    /**
     */
    protected final void updateDocumentTypeSystemId () {
	try {
            String systemId = systemField.getText();
            if ( ( getDocumentType().getPublicId() == null ) &&
                 ( "".equals (systemId) == false ) ) { // NOI18N
                systemId = text2null (systemId);
            }
	    getDocumentType().setSystemId (systemId);
	} catch (TreeException exc) {
	    updateSystemIdComponent();
	    Util.notifyTreeException (exc);
	}
    }
    
    /**
     */
    protected final void updateSystemIdComponent () {
	systemField.setText (null2text (getDocumentType().getSystemId()));
    }


    /**
     */
    protected final void initComponentValues () {
        updateNameComponent();
        updatePublicIdComponent();
        updateSystemIdComponent();
    }

    /**
     */
    protected void updateReadOnlyStatus (boolean editable) {
        nameField.setEditable (editable);
        publicField.setEditable (editable);
        systemField.setEditable (editable);
    }    


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        systemLabel = new javax.swing.JLabel();
        systemField = new javax.swing.JTextField();
        publicLabel = new javax.swing.JLabel();
        publicField = new javax.swing.JTextField();
        fillPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        nameLabel.setText(Util.getString ("PROP_xmlName"));
        nameLabel.setLabelFor(nameField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(nameLabel, gridBagConstraints);

        nameField.setColumns(23);
        nameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameFieldActionPerformed(evt);
            }
        });

        nameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                nameFieldFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(nameField, gridBagConstraints);

        systemLabel.setText(Util.getString ("PROP_xmlSystemID"));
        systemLabel.setLabelFor(systemField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(systemLabel, gridBagConstraints);

        systemField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                systemFieldActionPerformed(evt);
            }
        });

        systemField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                systemFieldFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(systemField, gridBagConstraints);

        publicLabel.setText(Util.getString ("PROP_xmlPublicID"));
        publicLabel.setLabelFor(publicField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(publicLabel, gridBagConstraints);

        publicField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                publicFieldActionPerformed(evt);
            }
        });

        publicField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                publicFieldFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints.weightx = 1.0;
        add(publicField, gridBagConstraints);

        fillPanel.setPreferredSize(new java.awt.Dimension(0, 0));
        fillPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(fillPanel, gridBagConstraints);

    }//GEN-END:initComponents

    /**
     */
    private void publicFieldFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_publicFieldFocusLost
	// Add your handling code here:
        updateDocumentTypePublicId();
    }//GEN-LAST:event_publicFieldFocusLost

    /**
     */
    private void systemFieldFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_systemFieldFocusLost
	// Add your handling code here:
        updateDocumentTypeSystemId();
    }//GEN-LAST:event_systemFieldFocusLost

    /**
     */
    private void nameFieldFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameFieldFocusLost
	// Add your handling code here:
        updateDocumentTypeName();
    }//GEN-LAST:event_nameFieldFocusLost

    /**
     */
    private void publicFieldActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publicFieldActionPerformed
	// Add your handling code here:
        updateDocumentTypePublicId();
    }//GEN-LAST:event_publicFieldActionPerformed

    /**
     */
    private void systemFieldActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_systemFieldActionPerformed
	// Add your handling code here:
        updateDocumentTypeSystemId();
    }//GEN-LAST:event_systemFieldActionPerformed

    /**
     */
    private void nameFieldActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFieldActionPerformed
	// Add your handling code here:
        updateDocumentTypeName();
    }//GEN-LAST:event_nameFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel publicLabel;
    private javax.swing.JTextField nameField;
    private javax.swing.JTextField publicField;
    private javax.swing.JLabel systemLabel;
    private javax.swing.JTextField systemField;
    private javax.swing.JPanel fillPanel;
    // End of variables declaration//GEN-END:variables
    
}
