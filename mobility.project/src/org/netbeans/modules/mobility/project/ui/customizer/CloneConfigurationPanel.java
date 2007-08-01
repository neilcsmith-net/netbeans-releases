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

/*
 * NewConfigurationPanel.java
 *
 * Created on February 11, 2004, 2:44 PM
 */
package org.netbeans.modules.mobility.project.ui.customizer;

import java.util.Collection;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.mobility.project.J2MEProjectUtils;
import org.openide.DialogDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.HelpCtx;

/**
 *
 * @author  gc149856
 */
public class CloneConfigurationPanel extends JPanel implements DocumentListener {
    
    private DialogDescriptor dialogDescriptor;
    final private Collection<String> allNames;
    
    /** Creates new form NewConfigurationPanel */
    public CloneConfigurationPanel(Collection<String> allNames) {
        this.allNames = allNames;
        initComponents();
        initAccessibility();
    }
    
    public String getName() {
        return jTextFieldName.getText();
    }
    
    public void setDialogDescriptor(final DialogDescriptor dd) {
        assert dialogDescriptor == null : "Set the dialog descriptor only once!"; //NOI18N
        dialogDescriptor = dd;
        dd.setHelpCtx(new HelpCtx(NewConfigurationPanel.class));
        jTextFieldName.getDocument().addDocumentListener(this);
        changedUpdate(null);
    }
    
    public boolean isValid() {
        final String name = jTextFieldName.getText();
        if (J2MEProjectUtils.ILEGAL_CONFIGURATION_NAMES.contains(name)) {
            errorPanel.setErrorBundleMessage("ERR_AddCfg_ReservedWord"); //NOI18N
            return false;
        }
        if (!Utilities.isJavaIdentifier(name)) {
            errorPanel.setErrorBundleMessage("ERR_AddCfg_MustBeJavaIdentifier"); //NOI18N
            return false;
        }
        if (allNames.contains(name)) {
            errorPanel.setErrorBundleMessage("ERR_AddCfg_NameExists"); //NOI18N
            return false;
        }
        errorPanel.setErrorBundleMessage(null);
        return true;
    }
    
    public void changedUpdate(@SuppressWarnings("unused")
	final DocumentEvent e) {
        if (dialogDescriptor != null) {
            dialogDescriptor.setValid(isValid());
        }
    }
    
    public void insertUpdate(final DocumentEvent e) {
        changedUpdate(e);
    }
    
    public void removeUpdate(final DocumentEvent e) {
        changedUpdate(e);
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jTextFieldName = new javax.swing.JTextField();
        errorPanel = new org.netbeans.modules.mobility.project.ui.customizer.ErrorPanel();

        setPreferredSize(new java.awt.Dimension(400, 100));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setLabelFor(jTextFieldName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(CloneConfigurationPanel.class, "LBL_NewConfigPanel_ConfigurationName")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 0, 12);
        add(jTextFieldName, gridBagConstraints);
        jTextFieldName.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CloneConfigurationPanel.class, "ACSD_CloneCfg_CfgName")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(errorPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    private void initAccessibility() {
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(NewConfigurationPanel.class, "ACSN_CloneConfigPanel"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(NewConfigurationPanel.class, "ACSD_CloneConfigPanel"));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.mobility.project.ui.customizer.ErrorPanel errorPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextFieldName;
    // End of variables declaration//GEN-END:variables
    
}
