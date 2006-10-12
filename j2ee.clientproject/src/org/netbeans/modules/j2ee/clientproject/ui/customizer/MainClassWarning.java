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

package org.netbeans.modules.j2ee.clientproject.ui.customizer;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import org.openide.filesystems.FileObject;

/** Shows a warning that no main class is set and allows choose a main class.
 *
 * @author  Jiri Rechtacek
 */
public class MainClassWarning extends JPanel {
   
    private String message;
    private FileObject[] sourcesRoots;

    /** Creates new form LibrariesChooser */
    public MainClassWarning (String message, FileObject[] sourcesRoots) {
        this.sourcesRoots = sourcesRoots;
        this.message = message;
        initComponents();
        // add MainClassChooser
    }
    
    /** Returns the selected main class.
     *
     * @return name of class or null if no class with the main method is selected
     */ 
    public String getSelectedMainClass () {
        return ((MainClassChooser)jPanel1).getSelectedMainClass ();
    }

    public void addChangeListener (ChangeListener l) {
        ((MainClassChooser)jPanel1).addChangeListener (l);
    }
    
    public void removeChangeListener (ChangeListener l) {
        ((MainClassChooser)jPanel1).removeChangeListener (l);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new MainClassChooser (sourcesRoots, org.openide.util.NbBundle.getBundle(MainClassWarning.class).getString("CTL_SelectAvaialableMainClasses"));

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(MainClassWarning.class).getString("AD_MainClassWarning"));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, this.message);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 6, 12);
        add(jLabel1, gridBagConstraints);

        jScrollPane1.setBorder(null);
        jScrollPane1.setViewportView(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables


}
