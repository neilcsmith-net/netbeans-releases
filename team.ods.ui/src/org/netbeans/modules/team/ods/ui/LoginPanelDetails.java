/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.team.ods.ui;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ToolTipManager;
import org.netbeans.modules.team.c2c.api.CloudServer;
import org.netbeans.modules.team.ui.common.LinkButton;
import org.netbeans.modules.team.ui.common.URLDisplayerAction;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Ondrej Vrabec
 */
class LoginPanelDetails extends javax.swing.JPanel {
    private final CloudServer server;
    private final Credentials credentials;

    /**
     * Creates new form LoginDetails
     */
    public LoginPanelDetails (CloudServer server, Credentials credentials) {
        this.server = server;
        this.credentials = credentials;
        initComponents();
    }

    void initialize () {
        for (ActionListener l : forgotPassword.getActionListeners()) {
            forgotPassword.removeActionListener(l);
        }
        forgotPassword.setAction(new URLDisplayerAction("", getForgetPasswordUrl()));
        for (ActionListener l:signUp.getActionListeners()) {
            signUp.removeActionListener(l);
        }
        signUp.setAction(new URLDisplayerAction("", getRegisterUrl()));

        forgotPassword.setText(NbBundle.getMessage(LoginPanelDetails.class, "LoginPanelDetails.forgotPassword.text"));
        signUp.setText(NbBundle.getMessage(LoginPanelDetails.class, "LoginPanelDetails.register.text"));

        setUsername(credentials.getUsername(server));
        setPassword(credentials.getPassword(server));
        setChildrenEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        signUp = new LinkButton(NbBundle.getMessage(LoginPanelDetails.class, "LoginPanelDetails.register.text"), new URLDisplayerAction("",getRegisterUrl()));
        lblPassword = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        lblUserName = new javax.swing.JLabel();
        lblNoAccount = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        forgotPassword = new LinkButton(NbBundle.getMessage(LoginPanelDetails.class, "LoginPanelDetails.forgotPassword.text"), new URLDisplayerAction("",getForgetPasswordUrl()));
        chkRememberMe = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(lblPassword, org.openide.util.NbBundle.getMessage(LoginPanelDetails.class, "LoginPanelDetails.lblPassword.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblUserName, org.openide.util.NbBundle.getMessage(LoginPanelDetails.class, "LoginPanelDetails.lblUserName.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblNoAccount, org.openide.util.NbBundle.getMessage(LoginPanelDetails.class, "LoginPanelDetails.lblNoAccount.text")); // NOI18N

        password.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passwordFocusGained(evt);
            }
        });

        chkRememberMe.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(chkRememberMe, org.openide.util.NbBundle.getMessage(LoginPanelDetails.class, "LoginPanelDetails.chkRememberMe.text")); // NOI18N
        chkRememberMe.setToolTipText(org.openide.util.NbBundle.getMessage(LoginPanelDetails.class, "LoginPanelDetails.chkRememberMe.toolTipText")); // NOI18N
        chkRememberMe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRememberMeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUserName)
                    .addComponent(lblPassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblNoAccount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(signUp))
                    .addComponent(chkRememberMe)
                    .addComponent(password, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                    .addComponent(username)
                    .addComponent(forgotPassword))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserName)
                    .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword)
                    .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkRememberMe)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(forgotPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNoAccount)
                    .addComponent(signUp))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void passwordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_passwordFocusGained
        password.setSelectionStart(0);
        password.setSelectionEnd(password.getPassword().length);
    }//GEN-LAST:event_passwordFocusGained

    private void chkRememberMeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRememberMeActionPerformed
        if (chkRememberMe.isSelected()) {
            ToolTipManager tooltipManager = ToolTipManager.sharedInstance();
            int initialDelay = tooltipManager.getInitialDelay();
            tooltipManager.setInitialDelay(0);
            tooltipManager.mouseMoved(new MouseEvent(chkRememberMe, 0, 0, 0, 0, 0, 0, false));
            tooltipManager.setInitialDelay(initialDelay);
        }
    }//GEN-LAST:event_chkRememberMeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkRememberMe;
    private javax.swing.JButton forgotPassword;
    private javax.swing.JLabel lblNoAccount;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JPasswordField password;
    private javax.swing.JButton signUp;
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables

    private URL getForgetPasswordUrl() {
        try {
            if (server != null) {
                return new URL(server.getUrl().toString() + "/people/forgot_password"); // NOI18N
            } else {
                return new URL("https://netbeans.org/people/forgot_password"); // NOI18N
            }
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    private URL getRegisterUrl() {
        try {
            if (server != null) {
                return new URL(server.getUrl().toString() + "/people/new"); // NOI18N
            } else {
                return new URL("https://netbeans.org/people/new"); // NOI18N
            }
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
    public char[] getPassword() {
        return password.getPassword();
    }

    public String getUsername() {
        return username.getText();
    }

    private void setUsername(String uname) {
        username.setText(uname);
        chkRememberMe.setSelected(true);
    }

    private void setPassword(char[] pwd) {
        password.setText(new String(pwd));
    }

    public boolean isStorePassword() {
        return chkRememberMe.isSelected();
    }
    
    public void focus () {
        password.requestFocus();
    }

    void setChildrenEnabled (boolean enabled) {
        for (Component c : getComponents()) {
            c.setEnabled(enabled);
        }
    }

    public static interface Credentials {

        public String getUsername (CloudServer kenai);
        
        public char[] getPassword(CloudServer kenai);
    }
}
