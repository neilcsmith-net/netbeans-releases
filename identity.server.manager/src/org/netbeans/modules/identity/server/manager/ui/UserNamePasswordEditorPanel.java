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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.identity.server.manager.ui;

import javax.swing.JComponent;
import org.openide.util.NbBundle;

/**
 * UI panel for editor username and password.
 *
 * Created on July 17, 2006, 12:05 AM
 *
 * @author  ptliu
 */
public class UserNamePasswordEditorPanel extends javax.swing.JPanel
        implements EditDialogDescriptor.Panel {
    
    private boolean add;
    private String[] userNames;
    private String originalUserName;
    
    /** Creates new form UserNamePasswordEditorPanel */
    public UserNamePasswordEditorPanel(boolean add, String[] userNames) {
        initComponents();
        
        this.add = add;
        this.userNames = new String[userNames.length];
        System.arraycopy(userNames, 0, this.userNames, 0, userNames.length);
        
        setUserName(null);
        setPassword(null);
    }
    
    public String getUserName() {
        return userNameTF.getText();
    }
    
    public String getPassword() {
        return new String(passwordTF.getPassword());
    }
    
    public void setUserName(String userName) {
        this.originalUserName = userName;
        userNameTF.setText(userName);
    }
    
    public void setPassword(String password) {
        passwordTF.setText(password);
    }
    
    public JComponent[] getEditableComponents() {
        return new JComponent[] {userNameTF, passwordTF};
    }
    
    public String checkValues() {
        String userName = userNameTF.getText();
        
        if (userName == null || userName.trim().length() == 0) {
            // return NbBundle.getMessage(UserNamePasswordEditorPanel.class,
            //          "MSG_EnterUsername");
            return "";      //NOI18N
        }
        
        for (int i = 0; i < userNames.length; i++) {
            if ((!add && !userName.equals(originalUserName)) || add) {
                if (userName.equals(userNames[i])) {
                    return NbBundle.getMessage(UserNamePasswordEditorPanel.class,
                            "MSG_UserNameExists");
                }
            }
        }
        
        String password = new String(passwordTF.getPassword());
        
        if (password == null || password.trim().length() == 0) {
            //return NbBundle.getMessage(UserNamePasswordEditorPanel.class,
            //        "MSG_EnterPassword");
            return "";      //NOI18N
        }
        
        
        return null;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        userNameLabel = new javax.swing.JLabel();
        userNameTF = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        passwordTF = new javax.swing.JPasswordField();

        userNameLabel.setLabelFor(userNameTF);
        org.openide.awt.Mnemonics.setLocalizedText(userNameLabel, java.util.ResourceBundle.getBundle("org/netbeans/modules/identity/server/manager/ui/Bundle").getString("LBL_UserName"));

        passwordLabel.setLabelFor(passwordTF);
        org.openide.awt.Mnemonics.setLocalizedText(passwordLabel, java.util.ResourceBundle.getBundle("org/netbeans/modules/identity/server/manager/ui/Bundle").getString("LBL_Password"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(userNameLabel)
                    .add(passwordLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(passwordTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                    .add(userNameTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userNameLabel)
                    .add(userNameTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(passwordLabel)
                    .add(passwordTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JPasswordField passwordTF;
    private javax.swing.JLabel userNameLabel;
    private javax.swing.JTextField userNameTF;
    // End of variables declaration//GEN-END:variables
    
}
