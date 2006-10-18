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

import org.netbeans.api.debugger.jpda.ClassLoadUnloadBreakpoint;
import org.netbeans.modules.debugger.jpda.ui.EditorContextBridge;
import org.netbeans.spi.debugger.ui.Controller;
import org.openide.util.NbBundle;

/**
 * @author  Jan Jancura
 */
// <RAVE>
// Implement HelpCtx.Provider interface to provide help ids for help system
// public class ClassBreakpointPanel extends JPanel implements Controller {
// ====
public class ClassBreakpointPanel extends JPanel implements Controller, org.openide.util.HelpCtx.Provider {
// </RAVE>
    
    private ActionsPanel                actionsPanel; 
    private ClassLoadUnloadBreakpoint   breakpoint;
    private boolean                     createBreakpoint = false;
    
    
    private static ClassLoadUnloadBreakpoint creteBreakpoint () {
        ClassLoadUnloadBreakpoint mb = ClassLoadUnloadBreakpoint.create (
            EditorContextBridge.getCurrentClassName (),
            false, 
            ClassLoadUnloadBreakpoint.TYPE_CLASS_LOADED_UNLOADED
        );
        mb.setPrintText (
            NbBundle.getBundle (ClassBreakpointPanel.class).getString 
                ("CTL_Class_Breakpoint_Print_Text")
        );
        return mb;
    }
    
    
    /** Creates new form LineBreakpointPanel */
    public ClassBreakpointPanel () {
        this (creteBreakpoint ());
        createBreakpoint = true;
    }
    
    /** Creates new form LineBreakpointPanel */
    public ClassBreakpointPanel (ClassLoadUnloadBreakpoint b) {
        breakpoint = b;
        initComponents ();
        
        String[] cf = b.getClassExclusionFilters ();
        String className = "";
        if (cf.length > 0) {
            cbExclusionFilter.setSelected (true);
            className = cf [0];
        } else {
            cbExclusionFilter.setSelected (false);
            cf = b.getClassFilters ();
            if (cf.length > 0)
                className = cf [0];
        }
        int i = className.lastIndexOf ('.');
        if (i < 0) {
            tfPackageName.setText ("");
            tfClassName.setText (className);
        } else {
            tfPackageName.setText (className.substring (0, i));
            tfClassName.setText (className.substring (i + 1, className.length ()));
        }
        cbBreakpointType.addItem (java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("LBL_Class_Breakpoint_Type_Prepare"));
        cbBreakpointType.addItem (java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("LBL_Class_Breakpoint_Type_Unload"));
        cbBreakpointType.addItem (java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle").getString("LBL_Class_Breakpoint_Type_Prepare_or_Unload"));
        switch (b.getBreakpointType ()) {
            case ClassLoadUnloadBreakpoint.TYPE_CLASS_LOADED:
                cbBreakpointType.setSelectedIndex (0);
                break;
            case ClassLoadUnloadBreakpoint.TYPE_CLASS_UNLOADED:
                cbBreakpointType.setSelectedIndex (1);
                break;
            case ClassLoadUnloadBreakpoint.TYPE_CLASS_LOADED_UNLOADED:
                cbBreakpointType.setSelectedIndex (2);
                break;
        }
        
        actionsPanel = new ActionsPanel (b);
        pActions.add (actionsPanel, "Center");
        // <RAVE>
        // The help IDs for the AddBreakpointPanel panels have to be different from the
        // values returned by getHelpCtx() because they provide different help
        // in the 'Add Breakpoint' dialog and when invoked in the 'Breakpoints' view
        putClientProperty("HelpID_AddBreakpointPanel", "debug.add.breakpoint.java.class"); // NOI18N
        // </RAVE>
    }

    // <RAVE>
    // Implement getHelpCtx() with the correct helpID
    public org.openide.util.HelpCtx getHelpCtx() {
       return new org.openide.util.HelpCtx("NetbeansDebuggerBreakpointClassJPDA"); // NOI18N
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tfPackageName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tfClassName = new javax.swing.JTextField();
        cbExclusionFilter = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        cbBreakpointType = new javax.swing.JComboBox();
        pActions = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        pSettings.setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle"); // NOI18N
        pSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("L_Class_Breakpoint_BorderTitle"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, bundle.getString("L_Class_Breakpoint_filter_hint")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(jLabel1, gridBagConstraints);
        jLabel1.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_L_Class_Breakpoint_filter_hint")); // NOI18N

        jLabel2.setLabelFor(tfPackageName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, bundle.getString("L_Class_Breakpoint_Package_Name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(jLabel2, gridBagConstraints);
        jLabel2.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_L_Class_Breakpoint_Package_Name")); // NOI18N

        tfPackageName.setToolTipText(bundle.getString("TTT_TF_Class_Breakpoint_Package_Name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(tfPackageName, gridBagConstraints);
        tfPackageName.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_TF_Class_Breakpoint_Package_Name")); // NOI18N

        jLabel3.setLabelFor(tfClassName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, bundle.getString("L_Class_Breakpoint_Class_Name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(jLabel3, gridBagConstraints);
        jLabel3.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_L_Class_Breakpoint_Class_Name")); // NOI18N

        tfClassName.setToolTipText(bundle.getString("TTT_TF_Class_Breakpoint_Class_Name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(tfClassName, gridBagConstraints);
        tfClassName.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_TF_Class_Breakpoint_Class_Name")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cbExclusionFilter, bundle.getString("CB_Class_Breakpoint_Exclusion_Filter")); // NOI18N
        cbExclusionFilter.setToolTipText(bundle.getString("TTT_CB_Class_Breakpoint_Exclusion_Filter")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(cbExclusionFilter, gridBagConstraints);
        cbExclusionFilter.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_CB_Class_Breakpoint_Exclusion_Filter")); // NOI18N

        jLabel4.setLabelFor(cbBreakpointType);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, bundle.getString("L_Class_Breakpoint_Type")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(jLabel4, gridBagConstraints);
        jLabel4.getAccessibleContext().setAccessibleDescription(bundle.getString("ASCD_L_Class_Breakpoint_Type")); // NOI18N

        cbBreakpointType.setToolTipText(bundle.getString("TTT_CB_Class_Breakpoint_Type")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        pSettings.add(cbBreakpointType, gridBagConstraints);
        cbBreakpointType.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_CB_Class_Breakpoint_Type")); // NOI18N

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

    
    // Controller implementation ...............................................
    
    /**
     * Called when "Ok" button is pressed.
     *
     * @return whether customizer can be closed
     */
    public boolean ok () {
        if (! isFilled()) {
            JOptionPane.showMessageDialog(this,
                java.util.ResourceBundle.getBundle("org/netbeans/modules/debugger/jpda/ui/breakpoints/Bundle")
                    .getString("MSG_No_Class_Name_Spec"));
            return false;
        }
        actionsPanel.ok ();
        String className = tfPackageName.getText ().trim ();
        if (className.length () > 0)
            className += '.';
        className += tfClassName.getText ().trim ();
        if (cbExclusionFilter.isSelected ()) {
            breakpoint.setClassFilters (new String [0]);
            breakpoint.setClassExclusionFilters (new String [] {className});
        } else {
            breakpoint.setClassFilters (new String [] {className});
            breakpoint.setClassExclusionFilters (new String [0]);
        }
        
        switch (cbBreakpointType.getSelectedIndex ()) {
            case 0:
                breakpoint.setBreakpointType (ClassLoadUnloadBreakpoint.TYPE_CLASS_LOADED);
                break;
            case 1:
                breakpoint.setBreakpointType (ClassLoadUnloadBreakpoint.TYPE_CLASS_UNLOADED);
                break;
            case 2:
                breakpoint.setBreakpointType (ClassLoadUnloadBreakpoint.TYPE_CLASS_LOADED_UNLOADED);
                break;
        }
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
        if (tfClassName.getText().trim ().length() > 0)
            return true;
        return false;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbBreakpointType;
    private javax.swing.JCheckBox cbExclusionFilter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel pActions;
    private javax.swing.JPanel pSettings;
    private javax.swing.JTextField tfClassName;
    private javax.swing.JTextField tfPackageName;
    // End of variables declaration//GEN-END:variables
    
}
