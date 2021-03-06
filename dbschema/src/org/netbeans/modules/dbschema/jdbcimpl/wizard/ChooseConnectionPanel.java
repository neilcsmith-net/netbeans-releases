/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009-2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.dbschema.jdbcimpl.wizard;

import java.awt.Dialog;
import javax.swing.SwingUtilities;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.support.DatabaseExplorerUIs;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author  David
 */
public class ChooseConnectionPanel extends javax.swing.JPanel {
    DatabaseConnection conn = null;
    DialogDescriptor desc = null;
    final String url;

    /** Creates new form ChooseConnectionPanel */
    public ChooseConnectionPanel(String url) {
        this.url = url;
        initComponents();
        DatabaseExplorerUIs.connect(connCombo, ConnectionManager.getDefault());
    }
    
    private void setDialogDescriptor(DialogDescriptor desc) {
        this.desc = desc;
    }
    
    private DatabaseConnection getConnection() {
        Object item = connCombo.getSelectedItem();
        if ( item == null ) {
            return null;
        } else {
            return (DatabaseConnection)item;
        }
    }

    public static DatabaseConnection showChooseConnectionDialog(String url) {
        assert SwingUtilities.isEventDispatchThread();

        ChooseConnectionPanel panel = new ChooseConnectionPanel(url);
        String title = NbBundle.getMessage(ChooseConnectionPanel.class, 
                "ChooseConnectionPanel.LBL_ChooseConnectionTitle");

        DialogDescriptor desc = new DialogDescriptor(panel, title);
        panel.setDialogDescriptor(desc);

        Dialog dialog = DialogDisplayer.getDefault().createDialog(desc);
        String acsd = NbBundle.getMessage(ChooseConnectionPanel.class, 
                "ChooseConnectionPanel.ACSD_ChooseConnectionPanel");
        dialog.getAccessibleContext().setAccessibleDescription(acsd);
        dialog.setVisible(true);
        dialog.dispose();

        // The user cancelled
        if (!DialogDescriptor.OK_OPTION.equals(desc.getValue())) {
            return null;
        }
        
        return panel.getConnection();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        connCombo = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        connCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        connCombo.setToolTipText(org.openide.util.NbBundle.getMessage(ChooseConnectionPanel.class, "ChooseConnectionPanel.connCombo.toolTipText")); // NOI18N

        jTextArea1.setBackground(getBackground());
        jTextArea1.setEditable(false);
        jTextArea1.setFont(javax.swing.UIManager.getFont("Label.font"));
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(3);
        jTextArea1.setText(org.openide.util.NbBundle.getMessage(ChooseConnectionPanel.class, "ChooseConnectionPanel.jTextArea1.text", new Object[] {url})); // NOI18N
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setBorder(null);
        jTextArea1.setDisabledTextColor(getForeground());
        jTextArea1.setEnabled(false);
        jTextArea1.setOpaque(false);
        jTextArea1.setRequestFocusEnabled(false);
        jScrollPane1.setViewportView(jTextArea1);
        jTextArea1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ChooseConnectionPanel.class, "ChooseConnectionPanel.jTextArea1.AccessibleContext.accessibleName")); // NOI18N
        jTextArea1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ChooseConnectionPanel.class, "ChooseConnectionPanel.jTextArea1.AccessibleContext.accessibleDescription", new Object[] {url})); // NOI18N
        jTextArea1.getAccessibleContext().setAccessibleParent(this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(connCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, 485, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(connCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        connCombo.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ChooseConnectionPanel.class, "ChooseConnectionPanel.connCombo.AccessibleContext.accessibleName")); // NOI18N
        connCombo.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ChooseConnectionPanel.class, "ChooseConnectionPanel.connCombo.AccessibleContext.accessibleDescription", new Object[] {url})); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ChooseConnectionPanel.class, "ChooseConnectionPanel.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ChooseConnectionPanel.class, "ChooseConnectionPanel.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox connCombo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

}
