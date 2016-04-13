/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.makeproject.launchers.actions.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cnd.makeproject.launchers.actions.ui.LaunchersConfig.LauncherConfig;
import org.netbeans.modules.cnd.makeproject.runprofiles.ui.ListTableModel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.ListView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Alexander Simon
 */
public class LaunchersPanel extends java.awt.Panel implements ExplorerManager.Provider {

    private final ExplorerManager manager = new ExplorerManager();
    private final SelectionChangeListener listener = new SelectionChangeListener();
    private final ArrayList<LauncherConfig> launchers = new ArrayList<>();
    private final LaunchersNodes nodes;
    private LauncherConfig selectedConfiguration;
    private final LaunchersConfig instance;
    private final ListTableModel envVarModel;
    private final JTable envVarTable;
    private boolean modified = false;

    /**
     * Creates new form LaunchersPanel
     */
    public LaunchersPanel(Project project) {
        setPreferredSize(new Dimension(600, 400));
        setMinimumSize(new Dimension(400, 200));
        initComponents();
        
        envVarModel = new ListTableModel(NbBundle.getMessage(LaunchersPanel.class, "EnvName"),
                                 NbBundle.getMessage(LaunchersPanel.class, "EnvValue"));
	envVarTable = new JTable(envVarModel);
	envVarModel.setTable(envVarTable);
	envVarScrollPane.setViewportView(envVarTable);
        envVarTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                validateEnvButtons(e);
            }
        });

        manager.addPropertyChangeListener(listener);
        instance = new LaunchersConfig(project);
        instance.load();
        for(Map.Entry<Integer, LauncherConfig> e : instance.getLoanchers().entrySet()) {
            launchers.add(e.getValue());
        }
        nodes = new LaunchersNodes(launchers);
        final ListView h_list = new ListView();
        h_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        LauncersListPanel.add(h_list, BorderLayout.CENTER);
        update();
    }

    public void saveConfigs() {
        updateSelectedConfiguration();
        if (modified) {
            instance.save(launchers);
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return manager;
    }

    final void update() {
        LauncherConfig sc = selectedConfiguration;
        manager.setRootContext(new AbstractNode(nodes));
        modified = false;

        if (sc == null) {
            if (nodes.getNodesCount() > 0) {
                try {
                    manager.setSelectedNodes(new Node[]{nodes.getNodeAt(0)});
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                // Send this event to activate/deactivate buttons...
                listener.propertyChange(new PropertyChangeEvent(this, ExplorerManager.PROP_SELECTED_NODES, null, null));
            }
        } else {
            selectNode(sc);
        }
    }

    private void selectNode(final LauncherConfig cfg) {
        Children children = manager.getRootContext().getChildren();
        for (Node node : children.getNodes()) {
            if (node instanceof LauncherNode) {
                if (((LauncherNode) node).getConfiguration() == cfg) {
                    try {
                        manager.setSelectedNodes(new Node[]{node});
                    } catch (PropertyVetoException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    break;
                }
            }
        }
    }

    private LauncherConfig getSelectedConfiguration() {
        Node[] selectedNodes = manager.getSelectedNodes();
        if (selectedNodes.length == 1 && selectedNodes[0] instanceof LauncherNode) {
            return ((LauncherNode) selectedNodes[0]).getConfiguration();
        } else {
            return null;
        }
    }

    private void updateSelectedConfiguration() {
        if (selectedConfiguration != null) {
            selectedConfiguration.setName(launcherNameTextField.getText().trim());
            selectedConfiguration.setCommand(runTextField.getText().trim());
            selectedConfiguration.setBuildCommand(buildTextField.getText().trim());
            selectedConfiguration.setRunDir(runDirTextField.getText().trim());
            selectedConfiguration.setSymbolFiles(symbolsTextField.getText().trim());
            selectedConfiguration.setPublic(publicCheckBox.isSelected());
            if (envVarTable.isEditing()) {
                TableCellEditor cellEditor = envVarTable.getCellEditor();
                if (cellEditor != null) {
                    cellEditor.stopCellEditing();
                }
            }

            HashMap<String, String> newContent = new HashMap<>();
            for(int i = 0; i < envVarModel.getRowCount(); i++) {
                String key = (String) envVarModel.getValueAt(i, 0);
                String value = (String) envVarModel.getValueAt(i, 1);
                if (key == null || value == null) {
                    continue;
                }
                key = key.trim();
                if (!key.isEmpty()) {
                    newContent.put(key, value.trim());
                    
                }
            }
            if ( selectedConfiguration.getEnv().size() != newContent.size()) {
               modified = true;
            } else {
                modified |= selectedConfiguration.getEnv().equals(newContent);
            }
            selectedConfiguration.getEnv().clear();
            selectedConfiguration.getEnv().putAll(newContent);
            modified |= selectedConfiguration.isModified();
            Node[] selectedNodes = manager.getSelectedNodes();
            if (selectedNodes.length == 1 && selectedNodes[0] instanceof LauncherNode) {
               LauncherNode node = ((LauncherNode) selectedNodes[0]);
               node.updateNode();
            }
        }
    }

    private void enableControls() {
        boolean b = selectedConfiguration != null;
        boolean c = true;
        boolean top = true;
        boolean bottom = true;
        if (b) {
            c = selectedConfiguration.getID() > 0;
            int index = launchers.indexOf(selectedConfiguration);
            if (index > 1) {
                bottom = index == launchers.size()-1;
                top = index == 2;
            }
        }
        upButton.setEnabled(b && !top);
        downButton.setEnabled(b && !bottom);
        removeButton.setEnabled(b && c);
        copyButton.setEnabled(b && c);
        launcherNameTextField.setEnabled(b && c);
        runTextField.setEnabled(b && c);
        buildTextField.setEnabled(b && c);
        publicCheckBox.setEnabled(b && c);
        runDirTextField.setEnabled(b);
        symbolsTextField.setEnabled(b);
        addEnvButton.setEnabled(b);
        removeEnvButton.setEnabled(b);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        leftPanel = new javax.swing.JPanel();
        launchersListLabel = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();
        LauncersListPanel = new javax.swing.JPanel();
        removeButton = new javax.swing.JButton();
        copyButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        rightPanel = new javax.swing.JPanel();
        launcherNameLabel = new javax.swing.JLabel();
        launcherNameTextField = new javax.swing.JTextField();
        runLabel = new javax.swing.JLabel();
        runTextField = new javax.swing.JTextField();
        buildLabel = new javax.swing.JLabel();
        buildTextField = new javax.swing.JTextField();
        runDirLabel = new javax.swing.JLabel();
        runDirTextField = new javax.swing.JTextField();
        symbolLabel = new javax.swing.JLabel();
        symbolsTextField = new javax.swing.JTextField();
        envLabel = new javax.swing.JLabel();
        envVarScrollPane = new javax.swing.JScrollPane();
        addEnvButton = new javax.swing.JButton();
        removeEnvButton = new javax.swing.JButton();
        publicCheckBox = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(launchersListLabel, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.launchersListLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        LauncersListPanel.setMaximumSize(new java.awt.Dimension(300, 2147483647));
        LauncersListPanel.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.removeButton.text")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(copyButton, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.copyButton.text")); // NOI18N
        copyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(upButton, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.upButton.text")); // NOI18N
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(downButton, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.downButton.text")); // NOI18N
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addComponent(launchersListLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(leftPanelLayout.createSequentialGroup()
                                .addComponent(upButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(downButton))
                            .addGroup(leftPanelLayout.createSequentialGroup()
                                .addComponent(addButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(copyButton)
                        .addGap(0, 39, Short.MAX_VALUE))
                    .addComponent(LauncersListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(launchersListLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LauncersListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(removeButton)
                    .addComponent(copyButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(upButton)
                    .addComponent(downButton)))
        );

        launcherNameLabel.setLabelFor(launcherNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(launcherNameLabel, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.launcherNameLabel.text")); // NOI18N

        launcherNameTextField.setMaximumSize(new java.awt.Dimension(300, 2147483647));

        org.openide.awt.Mnemonics.setLocalizedText(runLabel, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.runLabel.text")); // NOI18N

        runTextField.setMaximumSize(new java.awt.Dimension(300, 2147483647));

        org.openide.awt.Mnemonics.setLocalizedText(buildLabel, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.buildLabel.text")); // NOI18N

        buildTextField.setMaximumSize(new java.awt.Dimension(300, 2147483647));

        org.openide.awt.Mnemonics.setLocalizedText(runDirLabel, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.runDirLabel.text")); // NOI18N

        runDirTextField.setMaximumSize(new java.awt.Dimension(300, 2147483647));

        org.openide.awt.Mnemonics.setLocalizedText(symbolLabel, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.symbolLabel.text")); // NOI18N

        symbolsTextField.setMaximumSize(new java.awt.Dimension(300, 2147483647));

        org.openide.awt.Mnemonics.setLocalizedText(envLabel, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.envLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addEnvButton, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.addEnvButton.text")); // NOI18N
        addEnvButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEnvButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(removeEnvButton, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.removeEnvButton.text")); // NOI18N
        removeEnvButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeEnvButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(publicCheckBox, org.openide.util.NbBundle.getMessage(LaunchersPanel.class, "LaunchersPanel.publicCheckBox.text")); // NOI18N

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(envVarScrollPane, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(runLabel)
                            .addComponent(buildLabel)
                            .addComponent(runDirLabel)
                            .addComponent(symbolLabel)
                            .addComponent(launcherNameLabel))
                        .addGap(21, 21, 21)
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(runDirTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buildTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(runTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(symbolsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(launcherNameTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addComponent(envLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addComponent(publicCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 240, Short.MAX_VALUE)
                        .addComponent(addEnvButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeEnvButton)))
                .addContainerGap())
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(launcherNameLabel)
                    .addComponent(launcherNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(runTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(runLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buildLabel)
                    .addComponent(buildTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(runDirLabel)
                    .addComponent(runDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(symbolLabel)
                    .addComponent(symbolsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(envLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(envVarScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addEnvButton)
                    .addComponent(removeEnvButton)
                    .addComponent(publicCheckBox)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(leftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        updateSelectedConfiguration();
        LauncherConfig newConfiguration = new LauncherConfig(1000, true);
        launchers.add(newConfiguration);
        nodes.restKeys();
        selectNode(newConfiguration);
        //cbScriptConfigurator.setSelectedIndex(0);
        modified = true;
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // TODO: how to make it correctly??

        Node[] selectedNodes = manager.getSelectedNodes();
        Node nodeToSelect = null;
        int i = 0;

        if (selectedNodes.length > 0) {
            Node n = selectedNodes[0];
            Node[] nodes = manager.getRootContext().getChildren().getNodes();

            for (; i < nodes.length; i++) {
                if (nodes[i] == n) {
                    break;
                }
            }

            int idx = i + 1;

            if (idx >= nodes.length) {
                idx = i - 1;
            }

            nodeToSelect = idx < 0 ? null : nodes[idx];
        }

        launchers.remove(getSelectedConfiguration());
        nodes.restKeys();

        if (nodeToSelect != null) {
            try {
                manager.setSelectedNodes(new Node[]{nodeToSelect});
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        modified = true;
    }//GEN-LAST:event_removeButtonActionPerformed

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
        updateSelectedConfiguration();
        LauncherConfig newConfiguration = getSelectedConfiguration().copy();
        launchers.add(newConfiguration);
        nodes.restKeys();
        selectNode(newConfiguration);
        modified = true;
    }//GEN-LAST:event_copyButtonActionPerformed

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        Node[] selectedNodes = manager.getSelectedNodes();
        if (selectedNodes.length > 0) {
            updateSelectedConfiguration();
            final LauncherConfig current = getSelectedConfiguration();
            int curIndex = launchers.indexOf(current);
            LauncherConfig prev = launchers.get(curIndex-1);
            launchers.set(curIndex, prev);
            launchers.set(curIndex-1, current);
            nodes.restKeys();
            modified = true;
            try {
                manager.setSelectedNodes(new Node[0]);
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    Node[] nodes = manager.getRootContext().getChildren().getNodes();
                    int i = 0;
                    Node nodeToSelect = null;
                    for (; i < nodes.length; i++) {
                        if (nodes[i].getLookup().lookup(LauncherConfig.class) == current) {
                            nodeToSelect = nodes[i];
                            break;
                        }
                    }
                    if (nodeToSelect != null){
                        try {
                            manager.setSelectedNodes(new Node[]{nodeToSelect});
                        } catch (PropertyVetoException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            });
        }
    }//GEN-LAST:event_upButtonActionPerformed

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        Node[] selectedNodes = manager.getSelectedNodes();
        if (selectedNodes.length > 0) {
            updateSelectedConfiguration();
            final LauncherConfig current = getSelectedConfiguration();
            int curIndex = launchers.indexOf(current);
            LauncherConfig next = launchers.get(curIndex+1);
            launchers.set(curIndex, next);
            launchers.set(curIndex+1, current);
            nodes.restKeys();
            modified = true;
            try {
                manager.setSelectedNodes(new Node[0]);
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    Node[] nodes = manager.getRootContext().getChildren().getNodes();
                    int i = 0;
                    Node nodeToSelect = null;
                    for (; i < nodes.length; i++) {
                        if (nodes[i].getLookup().lookup(LauncherConfig.class) == current) {
                            nodeToSelect = nodes[i];
                            break;
                        }
                    }
                    if (nodeToSelect != null){
                        try {
                            manager.setSelectedNodes(new Node[]{nodeToSelect});
                        } catch (PropertyVetoException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            });
        }
    }//GEN-LAST:event_downButtonActionPerformed

    private void addEnvButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEnvButtonActionPerformed
        envVarModel.addRow();
    }//GEN-LAST:event_addEnvButtonActionPerformed

    private void removeEnvButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEnvButtonActionPerformed
        int selectedRow = envVarTable.getSelectedRow();
        if (selectedRow >= 0) {
            envVarModel.removeRows(new int[]{selectedRow});
        }
    }//GEN-LAST:event_removeEnvButtonActionPerformed

    private void validateEnvButtons(ListSelectionEvent e) {
	int[] selRows = envVarTable.getSelectedRows();
        removeButton.setEnabled(envVarModel.getRowCount() > 0 && selRows != null && selRows.length > 0);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel LauncersListPanel;
    private javax.swing.JButton addButton;
    private javax.swing.JButton addEnvButton;
    private javax.swing.JLabel buildLabel;
    private javax.swing.JTextField buildTextField;
    private javax.swing.JButton copyButton;
    private javax.swing.JButton downButton;
    private javax.swing.JLabel envLabel;
    private javax.swing.JScrollPane envVarScrollPane;
    private javax.swing.JLabel launcherNameLabel;
    private javax.swing.JTextField launcherNameTextField;
    private javax.swing.JLabel launchersListLabel;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JCheckBox publicCheckBox;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton removeEnvButton;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JLabel runDirLabel;
    private javax.swing.JTextField runDirTextField;
    private javax.swing.JLabel runLabel;
    private javax.swing.JTextField runTextField;
    private javax.swing.JLabel symbolLabel;
    private javax.swing.JTextField symbolsTextField;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables

    private final class SelectionChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                updateSelectedConfiguration();
                selectedConfiguration = getSelectedConfiguration();
                setContent(selectedConfiguration);
                enableControls();
            }
        }

        private void setContent(LauncherConfig cfg) {
            launcherNameTextField.setText(cfg == null ? null : cfg.getName());
            runTextField.setText(cfg == null ? null : cfg.getCommand());
            runDirTextField.setText(cfg == null ? null : cfg.getRunDir());
            buildTextField.setText(cfg == null ? null : cfg.getBuildCommand());
            symbolsTextField.setText(cfg == null ? null : cfg.getSymbolFiles());
            publicCheckBox.setSelected(cfg == null ? false : cfg.getPublic());
	    ArrayList<String> col0 = new ArrayList<>();
	    ArrayList<String> col1 = new ArrayList<>();
            int n;
            if (cfg != null) {
                for(Map.Entry<String,String> e : cfg.getEnv().entrySet()) {
                    col0.add(e.getKey());
                    col1.add(e.getValue());
                }
                n = cfg.getEnv().size();
            } else {
                n = 0;
            }
	    envVarModel.setData(n, col0, col1);
            envVarTable.tableChanged(null);
	}
    }

    private static final class LaunchersNodes extends Children.Keys<LauncherConfig> {
        private final ArrayList<LauncherConfig> launcers;
        public LaunchersNodes(final ArrayList<LauncherConfig> launcers) {
            this.launcers = launcers;
            setKeys(launcers);
        }

        private void restKeys() {
            setKeys(launcers);
        }

        @Override
        protected Node[] createNodes(LauncherConfig key) {
            return new LauncherNode[]{new LauncherNode(key)};
        }

    }

    public static class LauncherNode extends AbstractNode {

        private BufferedImage icon;

        public LauncherNode(LauncherConfig cfg) {
            super(Children.LEAF, Lookups.fixed(cfg));
            updateIcon(cfg);
        }

        private void updateIcon(LauncherConfig cfg) {
            icon = new BufferedImage(15, 15, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = (Graphics2D) icon.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color old = g.getColor();
            if (cfg.getPublic()) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.BLUE);
            }
            if (cfg.getID() > 0) {
                g.fillOval(7, 4, 5, 5);
            } else {
                g.fillOval(4, 1, 11, 11);
                g.setColor(old);
                g.fillOval(7, 4, 5, 5);
            }
        }

        public LauncherConfig getConfiguration() {
            return getLookup().lookup(LauncherConfig.class);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return icon;
        }

        // TODO: How to make this correctly?
        public void updateNode() {
            fireDisplayNameChange(null, getDisplayName());
            updateIcon(getConfiguration());
            fireIconChange();
        }

        @Override
        public Image getIcon(int type) {
            return icon;
        }

        @Override
        public String getDisplayName() {
            if (getConfiguration().getID() <= 0) {
                return NbBundle.getMessage(LaunchersPanel.class, "COMMON_PROPERTIES");
            } else {
                String name = getConfiguration().getName();
                if (name == null || name.isEmpty()) {
                    name = getConfiguration().getCommand();
                }
                return name;
            }
        }

        @Override
        public String toString() {
            return getDisplayName();
        }
    }
}
