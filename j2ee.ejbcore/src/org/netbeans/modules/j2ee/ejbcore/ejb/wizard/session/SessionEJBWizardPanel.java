/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.modules.j2ee.ejbcore.ejb.wizard.session;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.ant.AntArtifactQuery;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.j2ee.common.J2eeProjectCapabilities;
import org.netbeans.modules.j2ee.common.Util;
import org.netbeans.modules.j2ee.dd.api.ejb.Session;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.spi.project.ant.AntArtifactProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author  cwebster
 * @author Martin Adamek
 */
public class SessionEJBWizardPanel extends javax.swing.JPanel {

    private final ChangeListener listener;
    private final Project project;
    private ComboBoxModel projectsList;
    private final TimerOptions timerOptions;


    /** Creates new form SingleEJBWizardPanel */
    public SessionEJBWizardPanel(Project project, ChangeListener changeListener, TimerOptions timerOptions) {
        this.listener = changeListener;
        this.project = project;
        this.timerOptions = timerOptions;
        initComponents();

        J2eeProjectCapabilities projectCap = J2eeProjectCapabilities.forProject(project);
        if (projectCap.isEjb31LiteSupported()){
            boolean serverSupportsEJB31 = Util.getSupportedProfiles(project).contains(Profile.JAVA_EE_6_FULL);
            if (!projectCap.isEjb31Supported() && !serverSupportsEJB31){
                remoteCheckBox.setVisible(false);
                remoteCheckBox.setEnabled(false);
            }
            // enable Schedule section if Timer Session EJB, disable otherwise
            if (this.timerOptions == null) {
                schedulePanel.setVisible(false);
                schedulePanel.setEnabled(false);
            }  else {
                statefulButton.setEnabled(false);
                statefulButton.setVisible(false);
            }
        } else {
            // hide whole Schedule section
            schedulePanel.setVisible(false);
            schedulePanel.setEnabled(false);
            // hide singleton radio button
            singletonButton.setVisible(false);
            singletonButton.setEnabled(false);
            localCheckBox.setSelected(true);
        }

        localCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                listener.stateChanged(null);
            }
        });

        inProjectCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                listener.stateChanged(null);
            }
        });
        remoteCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                listener.stateChanged(null);
                updateInProjectCombo(remoteCheckBox.isSelected());
            }
        });
        scheduleTextArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                listener.stateChanged(null);
            }
            public void removeUpdate(DocumentEvent e) {
                listener.stateChanged(null);
            }
            public void changedUpdate(DocumentEvent e) {
                listener.stateChanged(null);
            }
        });
        updateInProjectCombo(false);
    }

    public static boolean isMaven(Project project) {
        return project.getLookup().lookup(AntArtifactProvider.class) == null;
    }

    private void updateInProjectCombo(boolean show) {
        if (show) {
            remoteCheckBox.setText(org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_In_Project")); // NOI18N
        } else {
            remoteCheckBox.setText(org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Remote")); // NOI18N
        }
        inProjectCombo.setVisible(show);
        if (show && projectsList == null) {
            List<Project> projects = SessionEJBWizardPanel.getProjectsList(project);
            projectsList = new DefaultComboBoxModel(projects.toArray(new Project[projects.size()]));
            final ListCellRenderer defaultRenderer = inProjectCombo.getRenderer();
            if (!projects.isEmpty()){
                inProjectCombo.setRenderer(new ListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        String name = ProjectUtils.getInformation((Project)value).getDisplayName();
                        return defaultRenderer.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
                    }
                });
                inProjectCombo.setModel(projectsList);
                inProjectCombo.setSelectedIndex(0);
            }
            listener.stateChanged(null);
        }
    }

    public static List<Project> getProjectsList(Project project) {
        List<Project> names = new ArrayList<Project>();
        boolean maven = isMaven(project);

        Set<Project> allProjects = new HashSet<Project>();
        // include all opened projects
        allProjects.addAll(Arrays.asList(OpenProjects.getDefault().getOpenProjects()));
        // include projects from given projects CP
        allProjects.addAll(getProjectsFromClasspath(project));

        for (Project p : allProjects) {
            if (p.equals(project)) {
                continue;
            }
            if (maven) {
                // if project is maven then only list maven projects which produce JARs
                if (p.getLookup().lookup(AntArtifactProvider.class) != null) {
                    continue;
                }
            } else {
                // if project is ant then only list ant projects which produce JARs
                if (p.getLookup().lookup(AntArtifactProvider.class) == null
                        || AntArtifactQuery.findArtifactsByType(p, JavaProjectConstants.ARTIFACT_TYPE_JAR).length == 0) {
                    continue;
                }
            }
            // list only projects which have java source root
            if (ProjectUtils.getSources(p).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA).length == 0) {
                continue;
            }
            // skip the j2ee projects
            if (p.getLookup().lookup(J2eeModuleProvider.class) != null) {
                continue;
            }
            names.add(p);
        }
        return names;
    }

    /**
     * Finds all projects on classpath of given source project.
     *
     * @param project which classpath will be scanned
     * @return {@code List} of all projects on its classpath
     */
    public static List<Project> getProjectsFromClasspath(Project project) {
        List<Project> projects = new ArrayList<Project>();
        SourceGroup[] groups = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (SourceGroup group : groups) {
            ClassPath cp = ClassPath.getClassPath(group.getRootFolder(), ClassPath.COMPILE);
            if (cp == null) {
                continue;
            }

            for (ClassPath.Entry entry : cp.entries()) {
                FileObject[] fos = SourceForBinaryQuery.findSourceRoots(entry.getURL()).getRoots();
                for (FileObject fo : fos) {
                    Project p = FileOwnerQuery.getOwner(fo);
                    if (p != null) {
                        projects.add(p);
                        break;
                    }
                }
            }
        }

        return projects;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sessionStateButtons = new javax.swing.ButtonGroup();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        sessionTypeLabel = new javax.swing.JLabel();
        statelessButton = new javax.swing.JRadioButton();
        statefulButton = new javax.swing.JRadioButton();
        interfaceLabel = new javax.swing.JLabel();
        remoteCheckBox = new javax.swing.JCheckBox();
        localCheckBox = new javax.swing.JCheckBox();
        singletonButton = new javax.swing.JRadioButton();
        inProjectCombo = new javax.swing.JComboBox();
        schedulePanel = new javax.swing.JPanel();
        scheduleLabel = new javax.swing.JLabel();
        scheduleScrollPane = new javax.swing.JScrollPane();
        scheduleTextArea = new javax.swing.JTextArea();
        exposeTimerMethod = new javax.swing.JCheckBox();

        jInternalFrame1.setVisible(true);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(sessionTypeLabel, org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_SessionType")); // NOI18N

        sessionStateButtons.add(statelessButton);
        statelessButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("MN_Stateless").charAt(0));
        statelessButton.setSelected(true);
        statelessButton.setText(org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Stateless")); // NOI18N

        sessionStateButtons.add(statefulButton);
        statefulButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("MN_Stateful").charAt(0));
        statefulButton.setText(org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Stateful")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(interfaceLabel, org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Interface")); // NOI18N

        remoteCheckBox.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("MN_Remote").charAt(0));
        remoteCheckBox.setText(org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Remote")); // NOI18N

        localCheckBox.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("MN_Local").charAt(0));
        localCheckBox.setText(org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Local")); // NOI18N

        sessionStateButtons.add(singletonButton);
        singletonButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("MN_Singleton").charAt(0));
        singletonButton.setText(org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Singleton")); // NOI18N

        scheduleLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle").getString("MN_Schedule").charAt(0));
        scheduleLabel.setLabelFor(scheduleTextArea);
        scheduleLabel.setText(org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Schedule")); // NOI18N
        scheduleLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        scheduleTextArea.setColumns(20);
        scheduleTextArea.setLineWrap(true);
        scheduleTextArea.setRows(4);
        scheduleTextArea.setText("minute=\"*\", second=\"0\", dayOfMonth=\"*\", month=\"*\", year=\"*\", hour=\"9-17\", dayOfWeek=\"Mon-Fri\""); // NOI18N
        scheduleTextArea.setWrapStyleWord(true);
        scheduleTextArea.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        scheduleScrollPane.setViewportView(scheduleTextArea);
        scheduleTextArea.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Schedule")); // NOI18N

        exposeTimerMethod.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        exposeTimerMethod.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(exposeTimerMethod, org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_ExposeTimerMethod")); // NOI18N

        javax.swing.GroupLayout schedulePanelLayout = new javax.swing.GroupLayout(schedulePanel);
        schedulePanel.setLayout(schedulePanelLayout);
        schedulePanelLayout.setHorizontalGroup(
            schedulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(schedulePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(schedulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(schedulePanelLayout.createSequentialGroup()
                        .addComponent(exposeTimerMethod)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(scheduleScrollPane)
                    .addGroup(schedulePanelLayout.createSequentialGroup()
                        .addComponent(scheduleLabel)
                        .addContainerGap())))
        );
        schedulePanelLayout.setVerticalGroup(
            schedulePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(schedulePanelLayout.createSequentialGroup()
                .addComponent(scheduleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scheduleScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exposeTimerMethod)
                .addContainerGap())
        );

        scheduleLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SessionEJBWizardPanel.class, "LBL_Schedule")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sessionTypeLabel)
            .addComponent(interfaceLabel)
            .addComponent(schedulePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statelessButton)
                    .addComponent(statefulButton)
                    .addComponent(singletonButton)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(remoteCheckBox)
                            .addComponent(localCheckBox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inProjectCombo, 0, 244, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sessionTypeLabel)
                .addGap(0, 0, 0)
                .addComponent(statelessButton)
                .addGap(0, 0, 0)
                .addComponent(statefulButton)
                .addGap(0, 0, 0)
                .addComponent(singletonButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(interfaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(localCheckBox)
                        .addGap(2, 2, 2)
                        .addComponent(remoteCheckBox))
                    .addComponent(inProjectCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(schedulePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/ejbcore/ejb/wizard/session/Bundle"); // NOI18N
        statelessButton.getAccessibleContext().setAccessibleName(bundle.getString("LBL_Stateless")); // NOI18N
        statelessButton.getAccessibleContext().setAccessibleDescription(bundle.getString("LBL_Stateless")); // NOI18N
        statefulButton.getAccessibleContext().setAccessibleName(bundle.getString("LBL_Stateful")); // NOI18N
        statefulButton.getAccessibleContext().setAccessibleDescription(bundle.getString("LBL_Stateful")); // NOI18N
        remoteCheckBox.getAccessibleContext().setAccessibleName(bundle.getString("LBL_Remote")); // NOI18N
        remoteCheckBox.getAccessibleContext().setAccessibleDescription(bundle.getString("LBL_Remote")); // NOI18N
        localCheckBox.getAccessibleContext().setAccessibleName(bundle.getString("LBL_Local")); // NOI18N
        localCheckBox.getAccessibleContext().setAccessibleDescription(bundle.getString("LBL_Local")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
                        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox exposeTimerMethod;
    private javax.swing.JComboBox inProjectCombo;
    private javax.swing.JLabel interfaceLabel;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JCheckBox localCheckBox;
    private javax.swing.JCheckBox remoteCheckBox;
    private javax.swing.JLabel scheduleLabel;
    private javax.swing.JPanel schedulePanel;
    private javax.swing.JScrollPane scheduleScrollPane;
    private javax.swing.JTextArea scheduleTextArea;
    private javax.swing.ButtonGroup sessionStateButtons;
    private javax.swing.JLabel sessionTypeLabel;
    private javax.swing.JRadioButton singletonButton;
    private javax.swing.JRadioButton statefulButton;
    private javax.swing.JRadioButton statelessButton;
    // End of variables declaration//GEN-END:variables

    public String getSessionType() {
        if (statelessButton.isSelected()){
            return Session.SESSION_TYPE_STATELESS;
        }else if (statefulButton.isSelected()){
            return Session.SESSION_TYPE_STATEFUL;
        }else if (singletonButton.isSelected()){
            return Session.SESSION_TYPE_SINGLETON;
        }

        return "";
    }
    
    public boolean isRemote() {
        return remoteCheckBox.isSelected();
    }
    
    public boolean isLocal() {
        return localCheckBox.isSelected();
    }

    public TimerOptions getTimerOptions() {
        if (timerOptions == null) {
            return null;
        } else {
            timerOptions.setTimerOptions(scheduleTextArea.getText());            
            return timerOptions;
        }
    }

    public boolean exposeTimerMethod() {
        return exposeTimerMethod.isSelected();
    }
    
    public String getTimerOptionsError() {
        return TimerOptions.validate(scheduleTextArea.getText());
    }
    
    public Project getRemoteInterfaceProject() {
        if (projectsList == null) {
            return null;
        }
        return (Project)projectsList.getSelectedItem();
    }
}
