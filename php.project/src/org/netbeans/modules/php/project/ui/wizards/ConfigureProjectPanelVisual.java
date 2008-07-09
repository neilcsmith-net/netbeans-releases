/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.project.ui.wizards;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.netbeans.modules.php.project.ui.LocalServer;
import org.netbeans.modules.php.project.ui.LocalServerController;
import org.netbeans.modules.php.project.ui.ProjectNameProvider;
import org.netbeans.modules.php.project.ui.Utils.EncodingModel;
import org.netbeans.modules.php.project.ui.Utils.EncodingRenderer;
import org.openide.WizardDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

class ConfigureProjectPanelVisual extends JPanel implements ProjectNameProvider, DocumentListener, ChangeListener, ActionListener {

    private static final long serialVersionUID = 5104232239516379L;

    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final LocalServerController localServerComponent;
    private final ProjectFolder projectFolderComponent;

    ConfigureProjectPanelVisual(ConfigureProjectPanel wizardPanel) {
        // Provide a name in the title bar.
        setName(NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_ProjectNameLocation"));
        putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, 0); // NOI18N
        // Step name (actually the whole list for reference).
        putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, wizardPanel.getSteps()); // NOI18N

        initComponents();
        localServerComponent = LocalServerController.create(localServerComboBox, localServerButton,
                NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_SelectSourceFolderTitle"));
        projectFolderComponent = new ProjectFolder(this);
        projectFolderPanel.add(BorderLayout.NORTH, projectFolderComponent);
        init();
    }

    private void init() {
        projectNameTextField.getDocument().addDocumentListener(this);
        localServerComponent.addChangeListener(this);

        encodingComboBox.setModel(new EncodingModel());
        encodingComboBox.setRenderer(new EncodingRenderer());

        projectFolderComponent.addProjectFolderListener(this);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // same problem as in 31086, initial focus on Cancel button
        projectNameTextField.requestFocus();
    }

    void addConfigureProjectListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    void removeConfigureProjectListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectNameLabel = new JLabel();
        projectNameTextField = new JTextField();
        sourcesLabel = new JLabel();
        localServerComboBox = new JComboBox();
        localServerButton = new JButton();
        localServerInfoLabel = new JLabel();
        encodingLabel = new JLabel();
        encodingComboBox = new JComboBox();
        separator = new JSeparator();
        projectFolderPanel = new JPanel();

        projectNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        projectNameLabel.setLabelFor(projectNameTextField);
        Mnemonics.setLocalizedText(projectNameLabel, NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_ProjectName"));
        projectNameLabel.setVerticalAlignment(SwingConstants.TOP);

        sourcesLabel.setLabelFor(localServerComboBox);
        Mnemonics.setLocalizedText(sourcesLabel, NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_Sources"));
        sourcesLabel.setVerticalAlignment(SwingConstants.TOP);

        localServerComboBox.setEditable(true);

        Mnemonics.setLocalizedText(localServerButton,NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_LocalServerBrowse")); // NOI18N
        Mnemonics.setLocalizedText(localServerInfoLabel, "dummy");
        localServerInfoLabel.setEnabled(false);

        encodingLabel.setLabelFor(encodingComboBox);

        Mnemonics.setLocalizedText(encodingLabel, NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_Encoding"));
        projectFolderPanel.setLayout(new BorderLayout());

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(projectNameLabel)
                    .add(sourcesLabel)
                    .add(encodingLabel))
                .add(24, 24, 24)
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(projectNameTextField, GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(localServerComboBox, 0, 202, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(localServerButton))
                    .add(layout.createSequentialGroup()
                        .add(localServerInfoLabel)
                        .addContainerGap())
                    .add(encodingComboBox, 0, 297, Short.MAX_VALUE)))
            .add(separator, GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
            .add(projectFolderPanel, GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
        
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(projectNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(projectNameLabel))
                .add(7, 7, 7)
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(localServerComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(localServerButton)
                    .add(sourcesLabel, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(localServerInfoLabel)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(encodingComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(encodingLabel))
                .addPreferredGap(LayoutStyle.UNRELATED)
                .add(separator, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.UNRELATED)
                .add(projectFolderPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox encodingComboBox;
    private JLabel encodingLabel;
    private JButton localServerButton;
    private JComboBox localServerComboBox;
    private JLabel localServerInfoLabel;
    private JPanel projectFolderPanel;
    private JLabel projectNameLabel;
    protected JTextField projectNameTextField;
    private JSeparator separator;
    private JLabel sourcesLabel;
    // End of variables declaration//GEN-END:variables

    public String getProjectName() {
        return projectNameTextField.getText().trim();
    }

    public void setProjectName(String projectName) {
        projectNameTextField.setText(projectName);
        projectNameTextField.selectAll();
    }

    public String getProjectFolder() {
        return projectFolderComponent.getProjectFolder();
    }

    public void setProjectFolder(String projectFolder) {
        projectFolderComponent.setProjectFolder(projectFolder);
    }

    public LocalServer getSourcesLocation() {
        return localServerComponent.getLocalServer();
    }

    public MutableComboBoxModel getLocalServerModel() {
        return localServerComponent.getLocalServerModel();
    }

    public void setLocalServerModel(MutableComboBoxModel localServers) {
        localServerComponent.setLocalServerModel(localServers);
    }

    public void selectSourcesLocation(LocalServer localServer) {
        localServerComponent.selectLocalServer(localServer);
    }

    Charset getEncoding() {
        return (Charset) encodingComboBox.getSelectedItem();
    }

    void setEncoding(Charset encoding) {
        encodingComboBox.setSelectedItem(encoding);
    }

    // listeners
    public void insertUpdate(DocumentEvent e) {
        processUpdate();
    }

    public void removeUpdate(DocumentEvent e) {
        processUpdate();
    }

    public void changedUpdate(DocumentEvent e) {
        processUpdate();
    }

    private void processUpdate() {
        changeSupport.fireChange();
    }

    public void stateChanged(ChangeEvent e) {
        changeSupport.fireChange();
    }

    public void actionPerformed(ActionEvent e) {
        changeSupport.fireChange();
    }
}
