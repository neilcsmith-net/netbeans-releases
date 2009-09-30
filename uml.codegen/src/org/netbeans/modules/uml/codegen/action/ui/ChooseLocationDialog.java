/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.uml.codegen.action.ui;

import java.io.File;

/**
 *
 * @author  Craig Conover, craig.conover@sun.com
 */
public class ChooseLocationDialog extends javax.swing.JDialog
{
    /**
     * Creates new form ChooseLocationDialog
     */
    public ChooseLocationDialog(java.awt.Frame parent, boolean modal)
    {
        this(parent, modal, null, null);
    }

    public ChooseLocationDialog(
        java.awt.Frame parent, boolean modal, File file, String title)
    {
        super(parent, modal);
        initComponents();
        
        setTitle(title);
        
        if (file != null && file.exists())
            locationChooser.setCurrentDirectory(file);
    }
    
    public File getFolderLocation()
    {
        return locationChooser.getSelectedFile();
    }

    public void setFolderLocation(String val)
    {
        setFolderLocation(new File(val));
    }

    public void setFolderLocation(File val)
    {
        locationChooser.setSelectedFile(val);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        locationChooser = new javax.swing.JFileChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Choose Folder Location to Export Code");

        locationChooser.setAcceptAllFileFilterUsed(false);
        locationChooser.setApproveButtonMnemonic(2);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/uml/codegen/action/ui/Bundle"); // NOI18N
        locationChooser.setApproveButtonText(bundle.getString("LBL_BrowseDialogApproveButton")); // NOI18N
        locationChooser.setApproveButtonToolTipText(bundle.getString("LBL_OpenButton_Tooltip")); // NOI18N
        locationChooser.setDialogTitle(bundle.getString("LBL_ChooseLocationDialog_Title")); // NOI18N
        locationChooser.setFileHidingEnabled(true);
        locationChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        locationChooser.setBorder(null);
        locationChooser.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                locationChooserActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(locationChooser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(locationChooser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void locationChooserActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_locationChooserActionPerformed
    {//GEN-HEADEREND:event_locationChooserActionPerformed
        setVisible(false);
    }//GEN-LAST:event_locationChooserActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new ChooseLocationDialog(
                    new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }

    public javax.swing.JFileChooser getLocationChooser()
    {
        return locationChooser;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser locationChooser;
    // End of variables declaration//GEN-END:variables
    
}
