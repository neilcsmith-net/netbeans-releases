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

package org.netbeans.modules.web.jsf.palette.items;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.web.jsf.api.facesmodel.ManagedBean;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 * @author  Pavel Buzek
 */
public class JsfTableCustomizer extends javax.swing.JPanel implements DocumentListener {
    
    private Dialog dialog = null;
    private DialogDescriptor descriptor = null;
    private boolean dialogOK = false;

    private final boolean hasModuleJsf;
    JsfTable jsfTable;
    JTextComponent target;
            
    public JsfTableCustomizer(JsfTable jsfTable, JTextComponent target) {
        this.jsfTable = jsfTable;
        this.target = target;
        
        initComponents();
        errorField.setForeground(UIManager.getColor("nb.errorForeground")); //NOI18N
        hasModuleJsf = JsfForm.hasModuleJsf(target);
        classTextField.getDocument().addDocumentListener(this);
    }
    
    public boolean showDialog() {
        
        dialogOK = false;
        
        String displayName = NbBundle.getMessage(JsfTableCustomizer.class, "NAME_jsp-JsfTable"); // NOI18N
        
        descriptor = new DialogDescriptor
                (this, NbBundle.getMessage(JsfTableCustomizer.class, "LBL_Customizer_InsertPrefix") + " " + displayName, true,
                 DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION,
                 new ActionListener() {
                     public void actionPerformed(ActionEvent event) {
                        if (descriptor.getValue().equals(DialogDescriptor.OK_OPTION)) {
                            evaluateInput();
                            dialogOK = true;
                        }
                        dialog.dispose();
		     }
		 } 
                );
        
        checkStatus();
        dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        dialog.setVisible(true);
        repaint();
        
        return dialogOK;
    }
    
    private void evaluateInput() {
        
        String entityClass = classTextField.getText();
        jsfTable.setBean(entityClass);
        
        jsfTable.setVariable("arrayOrCollectionOf" + entityClass);
        int formType = empty.isSelected() ? 0 : 1;
        jsfTable.setFormType(formType);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jFileChooser1 = new javax.swing.JFileChooser();
        populate = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        classTextField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        empty = new javax.swing.JRadioButton();
        fromBean = new javax.swing.JRadioButton();
        errorField = new javax.swing.JLabel();

        jFileChooser1.setCurrentDirectory(null);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/palette/items/Bundle").getString("LBL_GetProperty_Bean"));

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/palette/items/Bundle").getString("LBL_Browse"));
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        populate.add(empty);
        empty.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(empty, java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/palette/items/Bundle").getString("LBL_Empty_Table"));
        empty.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        empty.setMargin(new java.awt.Insets(0, 0, 0, 0));
        empty.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                emptyItemStateChanged(evt);
            }
        });

        populate.add(fromBean);
        org.openide.awt.Mnemonics.setLocalizedText(fromBean, java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/palette/items/Bundle").getString("LBL_Table_From_Entity"));
        fromBean.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fromBean.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fromBean.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                emptyItemStateChanged(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(errorField, java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/palette/items/Bundle").getString("MSG_No_Managed_Beans"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(empty)
                    .add(fromBean)
                    .add(layout.createSequentialGroup()
                        .add(17, 17, 17)
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(classTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton1))
                    .add(errorField))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(empty)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fromBean)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(classTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(errorField)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
//TODO: RETOUCHE FQN search
//FQNSearch.showFastOpen(classTextField);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void emptyItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_emptyItemStateChanged
        checkStatus();
    }//GEN-LAST:event_emptyItemStateChanged

    private void checkStatus() {
        if (empty.isSelected()) {
            classTextField.setEnabled(false);
            jButton1.setEnabled(false);
        } else {
            classTextField.setEnabled(true);
            jButton1.setEnabled(true);
        }
        boolean validClassName = false;
        try {
            validClassName = empty.isSelected() || JsfFormCustomizer.classExists(classTextField);
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ioe);
        }
        descriptor.setValid(hasModuleJsf && validClassName);
        errorField.setText(hasModuleJsf ? 
                (validClassName ? "" : java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/palette/items/Bundle").getString("MSG_InvalidClassName")) :
                java.util.ResourceBundle.getBundle("org/netbeans/modules/web/jsf/palette/items/Bundle").getString("MSG_NoJSF"));
    }

    public void insertUpdate(DocumentEvent event) {
        checkStatus();
    }

    public void removeUpdate(DocumentEvent event) {
        checkStatus();
    }

    public void changedUpdate(DocumentEvent event) {
        checkStatus();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField classTextField;
    private javax.swing.JRadioButton empty;
    private javax.swing.JLabel errorField;
    private javax.swing.JRadioButton fromBean;
    private javax.swing.JButton jButton1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.ButtonGroup populate;
    // End of variables declaration//GEN-END:variables
    
    public static class ManagedBeanRenderer extends JLabel implements ListCellRenderer {
        
        public ManagedBeanRenderer() {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
        }
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String text = "" + value;
            if (value instanceof ManagedBean) {
                ManagedBean bean = (ManagedBean) value;
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
                text = bean.getManagedBeanName() + "(" + bean.getManagedBeanClass() + ")"; //NOI18N
            }
            setFont(list.getFont());
            setText(text);
            return this;
        }
    }
}
