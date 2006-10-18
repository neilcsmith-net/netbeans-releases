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

package org.netbeans.modules.debugger.jpda.ui.breakpoints;

import javax.swing.JPanel;
import javax.swing.JOptionPane;
import org.netbeans.api.debugger.DebuggerManager;

import org.netbeans.api.debugger.jpda.MethodBreakpoint;
import org.netbeans.modules.debugger.jpda.ui.EditorContextBridge;
import org.netbeans.spi.debugger.ui.Controller;
import org.openide.util.NbBundle;

/**
 * @author  Jan Jancura
 */
// <RAVE>
// Implement HelpCtx.Provider interface to provide help ids for help system
// public class MethodBreakpointPanel extends JPanel implements Controller {
// ====
public class MethodBreakpointPanel extends JPanel implements Controller, org.openide.util.HelpCtx.Provider {
// </RAVE>
    
    private ActionsPanel                actionsPanel; 
    private MethodBreakpoint            breakpoint;
    private boolean                     createBreakpoint = false;
    
    
    private static MethodBreakpoint createBreakpoint () {
        MethodBreakpoint mb = MethodBreakpoint.create (
            EditorContextBridge.getCurrentClassName (),
            EditorContextBridge.getCurrentMethodName ()
        );
        mb.setPrintText (
            NbBundle.getBundle (MethodBreakpointPanel.class).getString 
                ("CTL_Method_Breakpoint_Print_Text")
        );
        return mb;
    }
    
    
    /** Creates new form LineBreakpointPanel */
    public MethodBreakpointPanel () {
        this (createBreakpoint ());
        createBreakpoint = true;
    }
    
    /** Creates new form LineBreakpointPanel */
    public MethodBreakpointPanel (MethodBreakpoint b) {
        breakpoint = b;
        initComponents ();
        
        String className = "";
        String[] fs = b.getClassFilters ();
        if (fs.length > 0) className = fs [0];
        int i = className.lastIndexOf ('.');
        if (i < 0) {
            tfPackageName.setText ("");
            tfClassName.setText (className);
        } else {
            tfPackageName.setText (className.substring (0, i));
            tfClassName.setText (className.substring (i + 1, className.length ()));
        }
        if ("".equals (b.getMethodName ())) {
            tfMethodName.setText (org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "Method_Breakpoint_ALL_METHODS"));
            cbAllMethods.setSelected (true);
            tfMethodName.setEnabled (false);
        } else {
            if (b.getMethodName().equals(tfClassName.getText()))
                tfMethodName.setText ("<init>");
            else
                tfMethodName.setText (b.getMethodName ());
        }
        cbStopOnEntry.setSelected((b.getBreakpointType() & b.TYPE_METHOD_ENTRY) != 0);
        cbStopOnExit.setSelected((b.getBreakpointType() & b.TYPE_METHOD_EXIT) != 0);
        
        tfCondition.setText (b.getCondition ());
        
        actionsPanel = new ActionsPanel (b);
        pActions.add (actionsPanel, "Center");
        // <RAVE>
        // The help IDs for the AddBreakpointPanel panels have to be different from the
        // values returned by getHelpCtx() because they provide different help
        // in the 'Add Breakpoint' dialog and when invoked in the 'Breakpoints' view
        putClientProperty("HelpID_AddBreakpointPanel", "debug.add.breakpoint.java.method"); // NOI18N
        // </RAVE>
    }
    
    // <RAVE>
    // Implement getHelpCtx() with the correct helpID
    public org.openide.util.HelpCtx getHelpCtx() {
        return new org.openide.util.HelpCtx("NetbeansDebuggerBreakpointMethodJPDA"); // NOI18N
    }
    // </RAVE>
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pSettings = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tfCondition = new javax.swing.JTextField();
        tfPackageName = new javax.swing.JTextField();
        tfClassName = new javax.swing.JTextField();
        cbAllMethods = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        tfMethodName = new javax.swing.JTextField();
        cbStopOnEntry = new javax.swing.JCheckBox();
        cbStopOnExit = new javax.swing.JCheckBox();
        pActions = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        pSettings.setLayout(new java.awt.GridBagLayout());

        pSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "L_Method_Breakpoint_BorderTitle"))); // NOI18N
        jLabel2.setLabelFor(tfPackageName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "L_Method_Breakpoint_Package_Name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(jLabel2, gridBagConstraints);
        jLabel2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "ACSD_L_Method_Breakpoint_Package_Name")); // NOI18N

        jLabel3.setLabelFor(tfClassName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "L_Method_Breakpoint_Class_Name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(jLabel3, gridBagConstraints);
        jLabel3.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "ACSD_L_Method_Breakpoint_Class_Name")); // NOI18N

        jLabel5.setLabelFor(tfCondition);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "L_Method_Breakpoint_Condition")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(jLabel5, gridBagConstraints);
        jLabel5.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "ACSD_L_Method_Breakpoint_Condition")); // NOI18N

        tfCondition.setToolTipText(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "TTT_TF_Method_Breakpoint_Condition")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(tfCondition, gridBagConstraints);
        tfCondition.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "ACSD_TF_Method_Breakpoint_Condition")); // NOI18N

        tfPackageName.setToolTipText(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "TTT_TF_Method_Breakpoint_Package_Name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(tfPackageName, gridBagConstraints);
        tfPackageName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "ACSD_TF_Method_Breakpoint_Package_Name")); // NOI18N

        tfClassName.setToolTipText(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "TTT_TF_Method_Breakpoint_Class_Name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(tfClassName, gridBagConstraints);
        tfClassName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "ACSD_TF_Method_Breakpoint_Class_Name")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cbAllMethods, org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "CB_Method_Breakpoint_All_Methods")); // NOI18N
        cbAllMethods.setToolTipText(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "TTT_CB_Method_Breakpoint_All_Methods")); // NOI18N
        cbAllMethods.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbAllMethods.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAllMethodsActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(cbAllMethods, gridBagConstraints);
        cbAllMethods.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "ACSD_CB_Method_Breakpoint_All_Methods")); // NOI18N

        jLabel1.setLabelFor(tfMethodName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "L_Method_Breakpoint_Method_Name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(jLabel1, gridBagConstraints);
        jLabel1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "ACSD_L_Method_Breakpoint_Method_Name")); // NOI18N

        tfMethodName.setToolTipText(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "TTT_TF_Method_Breakpoint_Method_Name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(tfMethodName, gridBagConstraints);
        tfMethodName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "ACSD_TF_Method_Breakpoint_Method_Name")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cbStopOnEntry, org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "LBL_CB_MethodEntry")); // NOI18N
        cbStopOnEntry.setToolTipText(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "TTT_CB_MethodEntry")); // NOI18N
        cbStopOnEntry.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbStopOnEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbStopOnEntryActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(cbStopOnEntry, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(cbStopOnExit, org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "LBL_CB_MethodExit")); // NOI18N
        cbStopOnExit.setToolTipText(org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "TTT_CB_MethodExit")); // NOI18N
        cbStopOnExit.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbStopOnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbStopOnExitActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(cbStopOnExit, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(pSettings, gridBagConstraints);

        pActions.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(pActions, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void cbStopOnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbStopOnExitActionPerformed
        // Assure that at least one checkbox is selected
        if (!cbStopOnExit.isSelected() && !cbStopOnEntry.isSelected()) {
            cbStopOnEntry.setSelected(true);
        }
    }//GEN-LAST:event_cbStopOnExitActionPerformed

    private void cbStopOnEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbStopOnEntryActionPerformed
        // Assure that at least one checkbox is selected
        if (!cbStopOnEntry.isSelected() && !cbStopOnExit.isSelected()) {
            cbStopOnExit.setSelected(true);
        }
    }//GEN-LAST:event_cbStopOnEntryActionPerformed

    private void cbAllMethodsActionPerformed (java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbAllMethodsActionPerformed
    {//GEN-HEADEREND:event_cbAllMethodsActionPerformed
        if (cbAllMethods.isSelected ()) {
            tfMethodName.setText (org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "Method_Breakpoint_ALL_METHODS"));
            tfMethodName.setEnabled (false);
        } else {
            tfMethodName.setText ("");
            tfMethodName.setEnabled (true);
        }
    }//GEN-LAST:event_cbAllMethodsActionPerformed

    
    // Controller implementation ...............................................
    
    /**
     * Called when "Ok" button is pressed.
     *
     * @return whether customizer can be closed
     */
    public boolean ok () {
        if (! isFilled()) {
            JOptionPane.showMessageDialog(this,
                org.openide.util.NbBundle.getMessage(MethodBreakpointPanel.class, "MSG_No_Class_or_Method_Name_Spec"));
            return false;
        }
        actionsPanel.ok ();
        String className = ((String) tfPackageName.getText ()).trim ();
        if (className.length () > 0)
            className += '.';
        className += tfClassName.getText ().trim ();
        breakpoint.setClassFilters (new String[] {className});
        if (!cbAllMethods.isSelected ())
            breakpoint.setMethodName (tfMethodName.getText ().trim ());
        else
            breakpoint.setMethodName ("");
        breakpoint.setCondition (tfCondition.getText ());
        int bpType = 0;
        if (cbStopOnEntry.isSelected()) {
            bpType |= breakpoint.TYPE_METHOD_ENTRY;
        }
        if (cbStopOnExit.isSelected()) {
            bpType |= breakpoint.TYPE_METHOD_EXIT;
        }
        breakpoint.setBreakpointType(bpType);
        
        if (createBreakpoint) 
            DebuggerManager.getDebuggerManager ().addBreakpoint (breakpoint);
        return true;
    }
    
    /**
     * Called when "Cancel" button is pressed.
     *
     * @return whether customizer can be closed
     */
    public boolean cancel () {
        return true;
    }
    
    /**
     * Return <code>true</code> whether value of this customizer 
     * is valid (and OK button can be enabled).
     *
     * @return <code>true</code> whether value of this customizer 
     * is valid
     */
    public boolean isValid () {
        return true;
    }
    
     boolean isFilled () {
        if (tfClassName.getText().trim().length() > 0 &&
                (tfMethodName.getText().trim().length() > 0 ||
                    cbAllMethods.isSelected()))
            return true;
        return false;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbAllMethods;
    private javax.swing.JCheckBox cbStopOnEntry;
    private javax.swing.JCheckBox cbStopOnExit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel pActions;
    private javax.swing.JPanel pSettings;
    private javax.swing.JTextField tfClassName;
    private javax.swing.JTextField tfCondition;
    private javax.swing.JTextField tfMethodName;
    private javax.swing.JTextField tfPackageName;
    // End of variables declaration//GEN-END:variables
    
}
