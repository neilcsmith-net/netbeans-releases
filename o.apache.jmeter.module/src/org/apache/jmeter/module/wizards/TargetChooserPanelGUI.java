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

package org.apache.jmeter.module.wizards;

import java.awt.Color;
import java.awt.TextField;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author  Jaroslav Bachorik
 */
public class TargetChooserPanelGUI extends javax.swing.JPanel implements DocumentListener {

  public void changedUpdate(DocumentEvent e) {
    updateTargetFile();
  }

  public void insertUpdate(DocumentEvent e) {
    updateTargetFile();
  }

  public void removeUpdate(DocumentEvent e) {
    updateTargetFile();
  }

  private static final String JMETER_SCRIPTS_FOLDER = "jmeter";

  private Project project;
  private TargetChooserPanel wizardPanel;
  private FileObject scriptsDir = null;
  private String expectedExtension;

  /** Creates new form TargetChooserPanelGUI */
  public TargetChooserPanelGUI(final TargetChooserPanel wizardPanel, Project project) {
    try {
      this.wizardPanel = wizardPanel;
      this.project = project;

      initComponents();
      setName(NbBundle.getMessage(TargetChooserPanelGUI.class, "TITLE_name_location"));

      scriptsDir = FileUtil.createFolder(project.getProjectDirectory(), JMETER_SCRIPTS_FOLDER);
    } catch (IOException ex) {
      Exceptions.printStackTrace(ex);
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();
        documentNameTextField = new javax.swing.JTextField();
        projectLabel = new javax.swing.JLabel();
        projectTextField = new javax.swing.JTextField();
        folderLabel = new javax.swing.JLabel();
        folderTextField = new javax.swing.JTextField();
        folderTextField.getDocument().addDocumentListener(this);
        browseButton = new javax.swing.JButton();
        pathLabel = new javax.swing.JLabel();
        targetSeparator = new javax.swing.JSeparator();
        fileTextField = new javax.swing.JTextField();
        documentNameTextField.getDocument().addDocumentListener(this);
        fillerPanel = new javax.swing.JPanel();

        nameLabel.setText(org.openide.util.NbBundle.getMessage(TargetChooserPanelGUI.class, "TargetChooserPanelGUI.nameLabel.text")); // NOI18N

        projectLabel.setText(org.openide.util.NbBundle.getMessage(TargetChooserPanelGUI.class, "TargetChooserPanelGUI.projectLabel.text")); // NOI18N

        projectTextField.setEditable(false);

        folderLabel.setText(org.openide.util.NbBundle.getMessage(TargetChooserPanelGUI.class, "TargetChooserPanelGUI.folderLabel.text")); // NOI18N

        browseButton.setText(org.openide.util.NbBundle.getMessage(TargetChooserPanelGUI.class, "TargetChooserPanelGUI.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseForFolders(evt);
            }
        });

        pathLabel.setText(org.openide.util.NbBundle.getMessage(TargetChooserPanelGUI.class, "TargetChooserPanelGUI.pathLabel.text")); // NOI18N

        fileTextField.setEditable(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(targetSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
            .add(fillerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, projectLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pathLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, nameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, folderLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(fileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(folderTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(browseButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(projectTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                    .add(documentNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nameLabel)
                    .add(documentNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectLabel)
                    .add(projectTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(folderLabel)
                    .add(browseButton)
                    .add(folderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(pathLabel)
                    .add(fileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(targetSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(fillerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

  private void browseForFolders(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseForFolders
    browseFolders();
  }//GEN-LAST:event_browseForFolders

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JTextField documentNameTextField;
    private javax.swing.JTextField fileTextField;
    private javax.swing.JPanel fillerPanel;
    private javax.swing.JLabel folderLabel;
    private javax.swing.JTextField folderTextField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel pathLabel;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JTextField projectTextField;
    private javax.swing.JSeparator targetSeparator;
    // End of variables declaration//GEN-END:variables

  private String getRelativeSourcesFolder() {
    return JMETER_SCRIPTS_FOLDER;
  }

  public FileObject getLocationRoot() {
    return project.getProjectDirectory();
  }

  public String getTargetName() {

    String text = documentNameTextField.getText().trim();

    if (text.length() == 0) {
      return null;
    } else {
      return text;
    }
  }

  public File getTargetFile() {
    String text = getNormalizedFolder();

    if (text.length() == 0) {
      return FileUtil.toFile(project.getProjectDirectory());
    } else {
      return new File(FileUtil.toFile(project.getProjectDirectory()).getAbsolutePath(), text);
    }
  }

  public String getRelativeTargetFolder() {
    return getRelativeSourcesFolder() + getNormalizedFolder();
  }

  public String getNormalizedFolder() {
    String norm = folderTextField.getText().trim();
    if (norm.length() == 0) {
      return "";
    }
    norm = norm.replace('\\', '/');
    // removing leading slashes
    int i = 0;
    while (i < norm.length() && norm.charAt(i) == '/') {
      i++;
    }
    if (i == norm.length()) {
      return ""; //only slashes
    }
    norm = norm.substring(i);

    // removing multiple slashes
    java.util.StringTokenizer tokens = new java.util.StringTokenizer(norm, "/");
    java.util.List list = new java.util.ArrayList();
    StringBuffer buf = new StringBuffer(tokens.nextToken());
    while (tokens.hasMoreTokens()) {
      String token = tokens.nextToken();
      if (token.length() > 0) {
        buf.append("/" + token);
      }
    }
    return buf.toString();
  }

  public String getTargetFolder() {
    return getTargetFile().getPath();
  }

  public String getCreatedFilePath() {
    return fileTextField.getText();
  }

  public void initValues(Project p, FileObject template, FileObject preselectedFolder) {
    projectTextField.setText(ProjectUtils.getInformation(p).getDisplayName());

    // filling the folder field
    String target = null;
    FileObject docBase = getLocationRoot();
    if (preselectedFolder != null && FileUtil.isParentOf(docBase, preselectedFolder)) {
      target = FileUtil.getRelativePath(docBase, preselectedFolder);
    }

    if (target == null) {
      target = JMETER_SCRIPTS_FOLDER;
    }
    
    folderTextField.setText(target == null ? "" : target); // NOI18N
    String ext = template == null ? "" : template.getExt(); // NOI18N
    expectedExtension = ext.length() == 0 ? "" : "." + ext; // NOI18N
  }

  public boolean isPanelValid() {
    return true;
  }

  String getErrorMessage() {
    // check for errors and return appropriate message
    return null;
  }

  private void browseFolders() {
    org.openide.filesystems.FileObject fo = null;
    // Show the browse dialog
    Sources sources = ProjectUtils.getSources(project);
    fo = BrowseFolders.showDialog(sources.getSourceGroups(Sources.TYPE_GENERIC), org.openide.loaders.DataFolder.class, getTargetFolder().replace(File.separatorChar, '/'));

    if (fo != null) {
      folderTextField.setText(FileUtil.getRelativePath(project.getProjectDirectory(), fo));
    }
    wizardPanel.fireChange();
//    }
  }

  private void updateTargetFile() {
    if (documentNameTextField.getText().length() > 0) {
      fileTextField.setText(getTargetFolder() + File.separatorChar + documentNameTextField.getText() + expectedExtension);
    } else {
      fileTextField.setText("");
    }
    wizardPanel.fireChange();
  }
}
