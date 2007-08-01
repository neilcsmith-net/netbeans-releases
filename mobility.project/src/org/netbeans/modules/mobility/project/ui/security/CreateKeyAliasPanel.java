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
 * CreateKeyAliasPanel.java
 *
 * Created on June 2, 2004, 2:45 PM
 */
package org.netbeans.modules.mobility.project.ui.security;

import org.netbeans.modules.mobility.project.security.KeyStoreRepository;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 *
 * @author  David Kaspar
 */
public class CreateKeyAliasPanel extends javax.swing.JPanel implements DocumentListener {
    
    private DialogDescriptor dd;
    private static final Dimension PREFERRED_SIZE = new Dimension(400, 350);
    final private KeyStoreRepository.KeyStoreBean bean;
    
    /** Creates new form CreateKeyAliasPanel */
    public CreateKeyAliasPanel(KeyStoreRepository.KeyStoreBean bean) {
        initComponents();
        initAccessibility();
        this.bean = bean;
        
        tAlias.getDocument().addDocumentListener(this);
        tCommon.getDocument().addDocumentListener(this);
        tOrgUnit.getDocument().addDocumentListener(this);
        tOrg.getDocument().addDocumentListener(this);
        tLocality.getDocument().addDocumentListener(this);
        tState.getDocument().addDocumentListener(this);
        tCountry.getDocument().addDocumentListener(this);
        tPassword.getDocument().addDocumentListener(this);
        tPasswordConfirm.getDocument().addDocumentListener(this);
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
        tAlias = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tCommon = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tOrgUnit = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tOrg = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tLocality = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tState = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        tCountry = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        tPassword = new javax.swing.JPasswordField();
        jLabel9 = new javax.swing.JLabel();
        tPasswordConfirm = new javax.swing.JPasswordField();
        jPanel2 = new javax.swing.JPanel();
        lError = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setLabelFor(tAlias);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CreateKeyAliasPanel.class, "LBL_CreateKey_Alias")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(tAlias, gridBagConstraints);
        tAlias.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CreateKeyAliasPanel.class, "ACSD_CreateKey_Alias")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CreateKeyAliasPanel.class, "LBL_CreateKey_Details"))); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setLabelFor(tCommon);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CreateKeyAliasPanel.class, "LBL_CreateKey_CommonName")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        jPanel1.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        jPanel1.add(tCommon, gridBagConstraints);
        tCommon.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CreateKeyAliasPanel.class, "ACSD_CreateKey_Common")); // NOI18N

        jLabel3.setLabelFor(tOrgUnit);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(CreateKeyAliasPanel.class, "LBL_CreateKey_OrgUnit")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        jPanel1.add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        jPanel1.add(tOrgUnit, gridBagConstraints);
        tOrgUnit.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CreateKeyAliasPanel.class, "ACSD_CreateKey_OrgUnit")); // NOI18N

        jLabel4.setLabelFor(tOrg);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(CreateKeyAliasPanel.class, "LBL_CreateKey_OrgName")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        jPanel1.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        jPanel1.add(tOrg, gridBagConstraints);
        tOrg.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CreateKeyAliasPanel.class, "ACSD_CreateKey_Org")); // NOI18N

        jLabel5.setLabelFor(tLocality);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(CreateKeyAliasPanel.class, "LBL_CreateKey_Locality")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        jPanel1.add(jLabel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        jPanel1.add(tLocality, gridBagConstraints);
        tLocality.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CreateKeyAliasPanel.class, "ACSD_CreateKey_Locality")); // NOI18N

        jLabel6.setLabelFor(tState);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(CreateKeyAliasPanel.class, "LBL_CreateKey_State")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        jPanel1.add(jLabel6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        jPanel1.add(tState, gridBagConstraints);
        tState.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CreateKeyAliasPanel.class, "ACSD_CreateKey_State")); // NOI18N

        jLabel7.setLabelFor(tCountry);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(CreateKeyAliasPanel.class, "LBL_CreateKey_Country")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        jPanel1.add(jLabel7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        jPanel1.add(tCountry, gridBagConstraints);
        tCountry.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CreateKeyAliasPanel.class, "ACSD_CreateKey_Country")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(jPanel1, gridBagConstraints);

        jLabel8.setLabelFor(tPassword);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(CreateKeyAliasPanel.class, "LBL_CreateKey_Password")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(jLabel8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(tPassword, gridBagConstraints);
        tPassword.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CreateKeyAliasPanel.class, "ACSD_CreateKey_Password")); // NOI18N

        jLabel9.setLabelFor(tPasswordConfirm);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(CreateKeyAliasPanel.class, "LBL_CreateKey_ConfirmPassword")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(jLabel9, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(tPasswordConfirm, gridBagConstraints);
        tPasswordConfirm.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CreateKeyAliasPanel.class, "ACSD_CreateKey_Password2")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lError, " ");
        lError.setForeground(new java.awt.Color(89, 79, 191));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(lError, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    private void initAccessibility() {
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(CreateKeyAliasPanel.class, "ACSN_CreateKey"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CreateKeyAliasPanel.class, "ACSD_CreateKey"));
    }
    
    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }
    
    protected void setDialogDescriptor(final DialogDescriptor desc) {
        this.dd = desc;
        dd.setHelpCtx(new HelpCtx(AddKeystorePanel.class));
        checkErrors();
    }
    
    private boolean isEmpty(final JTextField field) {
        final String text = field.getText();
        return text == null  ||  "".equals(text); // NOI18N
    }
    
    public String getErrorMessage() {
        final String text = tAlias.getText();
        if (isEmpty(tAlias))
            return "ERR_EmptyAliasName"; // NOI18N
        if (bean.getAlias(text) != null)
            return "ERR_AliasAlreadyExists"; // NOI18N
        if (isEmpty(tCommon)  &&  isEmpty(tOrgUnit)  &&  isEmpty(tOrg)  &&  isEmpty(tLocality)  &&  isEmpty(tState)  &&  isEmpty(tCountry))
            return "ERR_EmptyInfo"; // NOI18N
        final int passwordLength = tPassword.getPassword().length;
        if (passwordLength > 0  &&  passwordLength < 6)
            return "ERR_PasswordSmall"; // NOI18N
        if (!new String(tPassword.getPassword()).equals(new String(tPasswordConfirm.getPassword())))
            return "ERR_PasswordsNotEqual"; // NOI18N
        return null;
    }
    
    public void checkErrors() {
        final String errorMessage = getErrorMessage();
        lError.setText(errorMessage != null ? NbBundle.getMessage(CreateKeyAliasPanel.class, errorMessage) : ""); // NOI18N
        final boolean valid = errorMessage == null;
        if (dd != null && valid != dd.isValid())
            dd.setValid(valid);
    }
    
    public void changedUpdate(@SuppressWarnings("unused")
	final DocumentEvent e) {
        checkErrors();
    }
    
    public void insertUpdate(@SuppressWarnings("unused")
	final DocumentEvent e) {
        checkErrors();
    }
    
    public void removeUpdate(@SuppressWarnings("unused")
	final DocumentEvent e) {
        checkErrors();
    }
    
    private String getValue(final JTextField field) {
        final String val = field.getText().trim();
        if (val.length() == 0)
            return null;
        
        final StringBuffer sb = new StringBuffer(val);
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == ',') {//NOI18N
                sb.insert(i, '\\');//NOI18N
                i++;
            }
        }
        return val;
    }
    
    public String getDName() {
        final StringBuffer sb = new StringBuffer();
        String value = getValue(tCommon);
        if (value != null) {
            sb.append("CN="); //NOI18N
            sb.append(value);
        }
        value = getValue(tOrgUnit);
        if (value != null) {
            if (sb.length() > 0) sb.append(", ");//NOI18N
            sb.append("OU=");//NOI18N
            sb.append(value);
        }
        value = getValue(tOrg);
        if (value != null) {
            if (sb.length() > 0) sb.append(", ");//NOI18N
            sb.append("O=");//NOI18N
            sb.append(value);
        }        value = getValue(tLocality);
        if (value != null) {
            if (sb.length() > 0) sb.append(", ");//NOI18N
            sb.append("L=");//NOI18N
            sb.append(value);
        }
        value = getValue(tState);
        if (value != null) {
            if (sb.length() > 0) sb.append(", ");//NOI18N
            sb.append("S=");//NOI18N
            sb.append(value);
        }
        value = getValue(tCountry);
        if (value != null) {
            if (sb.length() > 0) sb.append(", ");//NOI18N
            sb.append("C=");//NOI18N
            sb.append(value);
        }
        return sb.toString();
    }
    
    public static KeyStoreRepository.KeyStoreBean.KeyAliasBean showCreateKeyAliasPanel(final KeyStoreRepository.KeyStoreBean bean) {
        if (bean == null)
            return null;
        final CreateKeyAliasPanel create = new CreateKeyAliasPanel(bean);
        final DialogDescriptor dd = new DialogDescriptor(create, NbBundle.getMessage(CreateKeyAliasPanel.class, "TITLE_CreateKeyPair"), true, null); // NOI18N
        create.setDialogDescriptor(dd);
        create.checkErrors();
        final Dialog d = DialogDisplayer.getDefault().createDialog(dd);
        d.setVisible(true);
        if (dd.getValue() != NotifyDescriptor.OK_OPTION)
            return null;
        final String dname = create.getDName();
        try {
            final String keyAliasPassword = new String(create.tPassword.getPassword());
            final KeyStoreRepository.KeyStoreBean.KeyAliasBean alias = bean.addKeyToStore(create.tAlias.getText(), dname, keyAliasPassword, -1);
            if (alias != null) {
                alias.setPassword((keyAliasPassword == null  ||  "".equals(keyAliasPassword)) ? bean.getPassword() : keyAliasPassword);
                alias.open();
            }
            return alias;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lError;
    private javax.swing.JTextField tAlias;
    private javax.swing.JTextField tCommon;
    private javax.swing.JTextField tCountry;
    private javax.swing.JTextField tLocality;
    private javax.swing.JTextField tOrg;
    private javax.swing.JTextField tOrgUnit;
    private javax.swing.JPasswordField tPassword;
    private javax.swing.JPasswordField tPasswordConfirm;
    private javax.swing.JTextField tState;
    // End of variables declaration//GEN-END:variables
    
}
