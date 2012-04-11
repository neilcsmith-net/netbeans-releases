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
package org.netbeans.modules.cnd.remote.ui.networkneighbour;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;

/**
 *
 * @author akrasny
 */
public final class HostSelectionPanel extends JPanel {

    private final ChangeSupport cs = new ChangeSupport(this);
    private final MyChangeListener listener = new MyChangeListener();
    private PropertyChangeListener explorerListener = null;
    private Node selectedNode = null;

    public HostSelectionPanel() {
        initComponents();
        textPort.setText("22"); // NOI18N
        textHost.getDocument().addDocumentListener(listener);
        textPort.getDocument().addDocumentListener(listener);
    }

    public void addChangeListener(ChangeListener cl) {
        cs.addChangeListener(cl);
    }

    public void removeChangeListener(ChangeListener cl) {
        cs.removeChangeListener(cl);
    }

    public String getHostname() {
        return textHost.getText().trim();
    }

    public int getPort() {
        if ("".equals(textPort.getText())) { // NOI18N
            return 22;
        }

        try {
            return Integer.valueOf(Integer.parseInt(textPort.getText().trim()));
        } catch (NumberFormatException e) {
            //return Integer.valueOf(ExecutionEnvironmentFactory.DEFAULT_PORT);
            return 22;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblHostName = new javax.swing.JLabel();
        textHost = new javax.swing.JTextField();
        lblPort = new javax.swing.JLabel();
        textPort = new org.netbeans.modules.cnd.remote.ui.networkneighbour.PortTextField();

        lblHostName.setLabelFor(textHost);
        org.openide.awt.Mnemonics.setLocalizedText(lblHostName, org.openide.util.NbBundle.getMessage(HostSelectionPanel.class, "HostSelectionPanel.lblHostName.text")); // NOI18N

        textHost.setText(org.openide.util.NbBundle.getMessage(HostSelectionPanel.class, "HostSelectionPanel.textHost.text")); // NOI18N

        lblPort.setLabelFor(textPort);
        org.openide.awt.Mnemonics.setLocalizedText(lblPort, org.openide.util.NbBundle.getMessage(HostSelectionPanel.class, "HostSelectionPanel.lblPort.text")); // NOI18N

        textPort.setText(org.openide.util.NbBundle.getMessage(HostSelectionPanel.class, "HostSelectionPanel.textPort.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblHostName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textHost, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPort)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textPort, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(textHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblHostName)
                .addComponent(lblPort)
                .addComponent(textPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblHostName;
    private javax.swing.JLabel lblPort;
    private javax.swing.JTextField textHost;
    private org.netbeans.modules.cnd.remote.ui.networkneighbour.PortTextField textPort;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textHost.setEditable(enabled);
        textPort.setEditable(enabled);
    }

    public void set(String hostName, int port) {
        textHost.setText(hostName);
        if (port < 22) {
            port = 22;
        }
        textPort.setText(Integer.toString(port));
    }

    public void attach(ExplorerManager.Provider provider) {
        ExplorerManager mgr = provider.getExplorerManager();
        explorerListener = new MyExplorerListener(mgr);
        mgr.addPropertyChangeListener(explorerListener);
    }

    private final class MyChangeListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            cs.fireChange();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            cs.fireChange();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            cs.fireChange();
        }
    }

    private class MyExplorerListener implements PropertyChangeListener {

        private final ExplorerManager manager;

        private MyExplorerListener(ExplorerManager explorerManager) {
            this.manager = explorerManager;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Node[] nodes = manager.getSelectedNodes();
                if (nodes.length > 0) {
                    Node node = nodes[0];
                    if (selectedNode == null || !selectedNode.getDisplayName().equals(node.getDisplayName())) {
                        selectedNode = node;
                        NeighbourHost host = node.getLookup().lookup(NeighbourHost.class);
                        set(host.getName(), 22);
                    }
                }
            }
        }
    }
}
