/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javafx2.platform;

import java.beans.Customizer;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.modules.javafx2.platform.api.JavaFXPlatformUtils;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Anton Chechel
 */
public class JavaFXPlatformCustomizer extends javax.swing.JPanel implements Customizer {
    private static final String DEFAULT_SDK_LOCATION = "C:\\Program Files\\Oracle\\"; // NOI18N
    
    private JavaPlatform platform;
    private File lastUsedFolder;

    public JavaFXPlatformCustomizer() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked") // NOI18N
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        enableCheckBox = new javax.swing.JCheckBox();
        sdkLabel = new javax.swing.JLabel();
        javadocLabel = new javax.swing.JLabel();
        srcLabel = new javax.swing.JLabel();
        sdkTextField = new javax.swing.JTextField();
        javadocTextField = new javax.swing.JTextField();
        srcTextField = new javax.swing.JTextField();
        browseSDKButton = new javax.swing.JButton();
        browseJavadocButton = new javax.swing.JButton();
        browseSourcesButton = new javax.swing.JButton();
        messageLabel = new javax.swing.JLabel();
        runtimeLabel = new javax.swing.JLabel();
        runtimeTextField = new javax.swing.JTextField();
        browseRuntimeButton = new javax.swing.JButton();

        enableCheckBox.setText(org.openide.util.NbBundle.getMessage(JavaFXPlatformCustomizer.class, "JavaFXPlatformCustomizer.enableCheckBox.text")); // NOI18N
        enableCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                enableCheckBoxItemStateChanged(evt);
            }
        });

        sdkLabel.setText(org.openide.util.NbBundle.getMessage(JavaFXPlatformCustomizer.class, "JavaFXPlatformCustomizer.sdkLabel.text")); // NOI18N

        javadocLabel.setText(org.openide.util.NbBundle.getMessage(JavaFXPlatformCustomizer.class, "JavaFXPlatformCustomizer.javadocLabel.text")); // NOI18N

        srcLabel.setText(org.openide.util.NbBundle.getMessage(JavaFXPlatformCustomizer.class, "JavaFXPlatformCustomizer.srcLabel.text")); // NOI18N

        sdkTextField.setEnabled(false);

        javadocTextField.setEnabled(false);

        srcTextField.setEnabled(false);

        browseSDKButton.setText(org.openide.util.NbBundle.getMessage(JavaFXPlatformCustomizer.class, "JavaFXPlatformCustomizer.browseSDKButton.text")); // NOI18N
        browseSDKButton.setEnabled(false);
        browseSDKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseSDKButtonActionPerformed(evt);
            }
        });

        browseJavadocButton.setText(org.openide.util.NbBundle.getMessage(JavaFXPlatformCustomizer.class, "JavaFXPlatformCustomizer.browseJavadocButton.text")); // NOI18N
        browseJavadocButton.setEnabled(false);
        browseJavadocButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseJavadocButtonActionPerformed(evt);
            }
        });

        browseSourcesButton.setText(org.openide.util.NbBundle.getMessage(JavaFXPlatformCustomizer.class, "JavaFXPlatformCustomizer.browseSourcesButton.text")); // NOI18N
        browseSourcesButton.setEnabled(false);
        browseSourcesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseSourcesButtonActionPerformed(evt);
            }
        });

        messageLabel.setForeground(java.awt.Color.red);
        messageLabel.setText(org.openide.util.NbBundle.getMessage(JavaFXPlatformCustomizer.class, "JavaFXPlatformCustomizer.messageLabel.text")); // NOI18N

        runtimeLabel.setText(org.openide.util.NbBundle.getMessage(JavaFXPlatformCustomizer.class, "JavaFXPlatformCustomizer.runtimeLabel.text")); // NOI18N

        runtimeTextField.setEnabled(false);

        browseRuntimeButton.setText(org.openide.util.NbBundle.getMessage(JavaFXPlatformCustomizer.class, "JavaFXPlatformCustomizer.browseRuntimeButton.text")); // NOI18N
        browseRuntimeButton.setEnabled(false);
        browseRuntimeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseRuntimeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(enableCheckBox)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(messageLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(srcLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(sdkLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(runtimeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(javadocLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(srcTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                                    .addComponent(javadocTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                                    .addComponent(runtimeTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                                    .addComponent(sdkTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(browseSourcesButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(browseJavadocButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(browseRuntimeButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(browseSDKButton, javax.swing.GroupLayout.Alignment.TRAILING))))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {browseJavadocButton, browseSDKButton, browseSourcesButton});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {javadocLabel, runtimeLabel, sdkLabel, srcLabel});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(enableCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sdkLabel)
                    .addComponent(browseSDKButton)
                    .addComponent(sdkTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(browseRuntimeButton)
                    .addComponent(runtimeLabel)
                    .addComponent(runtimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(browseJavadocButton)
                    .addComponent(javadocLabel)
                    .addComponent(javadocTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(srcLabel)
                    .addComponent(browseSourcesButton)
                    .addComponent(srcTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(messageLabel)
                .addContainerGap(25, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void enableCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_enableCheckBoxItemStateChanged
        sdkTextField.setEnabled(enableCheckBox.isSelected());
        runtimeTextField.setEnabled(enableCheckBox.isSelected());
        javadocTextField.setEnabled(enableCheckBox.isSelected());
        srcTextField.setEnabled(enableCheckBox.isSelected());
        browseSDKButton.setEnabled(enableCheckBox.isSelected());
        browseRuntimeButton.setEnabled(enableCheckBox.isSelected());
        browseJavadocButton.setEnabled(enableCheckBox.isSelected());
        browseSourcesButton.setEnabled(enableCheckBox.isSelected());
        
        if (enableCheckBox.isSelected()) {
            if (isPlatformValid()) {
                clearErrorMessage();
                saveProperties();
            } else {
                setErrorMessage();
            }
        } else {
            clearErrorMessage();
            clearProperties();
        }
    }//GEN-LAST:event_enableCheckBoxItemStateChanged

private void browseSDKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseSDKButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        
        if (lastUsedFolder != null) {
            chooser.setCurrentDirectory(lastUsedFolder);
        } else {
            String workDir = sdkTextField.getText();
            if (workDir.length() == 0) {
                File defaultFolder = new File(DEFAULT_SDK_LOCATION);
                if (defaultFolder.exists()) {
                    chooser.setCurrentDirectory(defaultFolder);
                } else {
                    FileObject platformFolder = platform.getInstallFolders().iterator().next();
                    chooser.setCurrentDirectory(FileUtil.toFile(platformFolder));
                }
            } else {
                chooser.setCurrentDirectory(new File(workDir));
            }
        }
        
        chooser.setDialogTitle(NbBundle.getMessage(JavaFXPlatformCustomizer.class, "Customizer_SDK_Folder_Browse_Title")); // NOI18N
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File file = FileUtil.normalizeFile(chooser.getSelectedFile());
            lastUsedFolder = file.getParentFile();
            sdkTextField.setText(file.getAbsolutePath());
            
            if (runtimeTextField.getText().length() == 0) {
                runtimeTextField.setText(JavaFXPlatformUtils.guessRuntimePath(file));
            }

            if (javadocTextField.getText().length() == 0) {
                javadocTextField.setText(JavaFXPlatformUtils.guessJavadocPath(file));
            }
        
            if (isPlatformValid()) {
                saveProperties();
                firePlatformChange();
                clearErrorMessage();
            } else {
                setErrorMessage();
            }
        }
}//GEN-LAST:event_browseSDKButtonActionPerformed

private void browseRuntimeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseRuntimeButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        
        if (lastUsedFolder != null) {
            chooser.setCurrentDirectory(lastUsedFolder);
        } else {
            String workDir = runtimeTextField.getText();
            if (workDir.length() == 0) {
                File defaultFolder = new File(DEFAULT_SDK_LOCATION);
                if (defaultFolder.exists()) {
                    chooser.setCurrentDirectory(defaultFolder);
                } else {
                    FileObject platformFolder = platform.getInstallFolders().iterator().next();
                    chooser.setCurrentDirectory(FileUtil.toFile(platformFolder));
                }
            } else {
                chooser.setCurrentDirectory(new File(workDir));
            }
        }
        
        chooser.setDialogTitle(NbBundle.getMessage(JavaFXPlatformCustomizer.class, "Customizer_Runtime_Folder_Browse_Title")); // NOI18N
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File file = FileUtil.normalizeFile(chooser.getSelectedFile());
            lastUsedFolder = file.getParentFile();
            runtimeTextField.setText(file.getAbsolutePath());

            if (isPlatformValid()) {
                saveProperties();
                firePlatformChange();
                clearErrorMessage();
            } else {
                setErrorMessage();
            }
        }
}//GEN-LAST:event_browseRuntimeButtonActionPerformed

private void browseJavadocButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseJavadocButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        
        if (lastUsedFolder != null) {
            chooser.setCurrentDirectory(lastUsedFolder);
        } else {
            String workDir = javadocTextField.getText();
            if (workDir.length() == 0) {
                File defaultFolder = new File(DEFAULT_SDK_LOCATION);
                if (defaultFolder.exists()) {
                    chooser.setCurrentDirectory(defaultFolder);
                } else {
                    List<URL> javadocFolders = platform.getJavadocFolders();
                    if (javadocFolders.size() > 0) {
                        FileObject javadocFolder = URLMapper.findFileObject(javadocFolders.get(0));
                        if (javadocFolder != null) {
                            workDir = FileUtil.toFile(javadocFolder).getAbsolutePath();
                            chooser.setCurrentDirectory(FileUtil.toFile(javadocFolder));
                        }
                    }
                }
            } else {
                chooser.setCurrentDirectory(new File(workDir));
            }
        }
        
        String workDir = javadocTextField.getText();
        if (workDir.length() == 0) {
        }
        chooser.setCurrentDirectory(new File(workDir));
        chooser.setDialogTitle(NbBundle.getMessage(JavaFXPlatformCustomizer.class, "Customizer_Javadoc_Folder_Browse_Title")); // NOI18N
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File file = FileUtil.normalizeFile(chooser.getSelectedFile());
            lastUsedFolder = file.getParentFile();
            javadocTextField.setText(file.getAbsolutePath());

            if (isPlatformValid()) {
                saveProperties();
                firePlatformChange();
                clearErrorMessage();
            } else {
                setErrorMessage();
            }
        }
}//GEN-LAST:event_browseJavadocButtonActionPerformed

private void browseSourcesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseSourcesButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        
        if (lastUsedFolder != null) {
            chooser.setCurrentDirectory(lastUsedFolder);
        } else {
            String workDir = srcTextField.getText();
            if (workDir.length() == 0) {
                File defaultFolder = new File(DEFAULT_SDK_LOCATION);
                if (defaultFolder.exists()) {
                    chooser.setCurrentDirectory(defaultFolder);
                } else {
                    ClassPath sourceFolders = platform.getSourceFolders();
                    FileObject[] roots = sourceFolders.getRoots();
                    if (roots != null && roots.length > 0) {
                        FileObject srcFolder = roots[0];
                        if (srcFolder != null) {
                            File srcFolderFile = FileUtil.toFile(srcFolder);
                            if (srcFolderFile != null) {
                                chooser.setCurrentDirectory(srcFolderFile);
                            }
                        }
                    }
                }
            } else {
                chooser.setCurrentDirectory(new File(workDir));
            }
        }

        chooser.setDialogTitle(NbBundle.getMessage(JavaFXPlatformCustomizer.class, "Customizer_Sources_Folder_Browse_Title")); // NOI18N
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File file = FileUtil.normalizeFile(chooser.getSelectedFile());
            lastUsedFolder = file.getParentFile();
            srcTextField.setText(file.getAbsolutePath());

            if (isPlatformValid()) {
                saveProperties();
                firePlatformChange();
                clearErrorMessage();
            } else {
                setErrorMessage();
            }
        }
}//GEN-LAST:event_browseSourcesButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseJavadocButton;
    private javax.swing.JButton browseRuntimeButton;
    private javax.swing.JButton browseSDKButton;
    private javax.swing.JButton browseSourcesButton;
    private javax.swing.JCheckBox enableCheckBox;
    private javax.swing.JLabel javadocLabel;
    private javax.swing.JTextField javadocTextField;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JLabel runtimeLabel;
    private javax.swing.JTextField runtimeTextField;
    private javax.swing.JLabel sdkLabel;
    private javax.swing.JTextField sdkTextField;
    private javax.swing.JLabel srcLabel;
    private javax.swing.JTextField srcTextField;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setObject(Object bean) {
        this.platform = (JavaPlatform) bean;
        if (JavaFXPlatformUtils.isJavaFXEnabled(platform)) {
            readProperties();
        } else {
            enableCheckBox.setSelected(false);
        }
    }

    private void saveProperties() {
        Map<String, String> map = new HashMap<String, String>(3);
        boolean propertiesChanged = false;

        String sdkPath = sdkTextField.getText();
        if (sdkPath.length() > 0) {
            map.put(Utils.getSDKPropertyKey(platform), sdkPath);
            propertiesChanged = true;
        }
        
        String runtimePath = runtimeTextField.getText();
        if (runtimePath.length() > 0) {
            map.put(Utils.getRuntimePropertyKey(platform), runtimePath);
            propertiesChanged = true;
        }
        
        String javadocPath = javadocTextField.getText();
        if (javadocPath.length() > 0) {
            map.put(Utils.getJavadocPropertyKey(platform), javadocPath);
            propertiesChanged = true;
        }
        
        String srcPath = srcTextField.getText();
        if (srcPath.length() > 0) {
            map.put(Utils.getSourcesPropertyKey(platform), srcPath);
            propertiesChanged = true;
        }
        
        if (propertiesChanged) {
            PlatformPropertiesHandler.saveGlobalProperties(map);
        }
    }
    
    private void clearProperties() {
        PlatformPropertiesHandler.clearGlobalPropertiesForPlatform(platform);
    }

    private void readProperties() {
        EditableProperties properties = PlatformPropertiesHandler.getGlobalProperties();

        String sdkPath = properties.get(Utils.getSDKPropertyKey(platform));
        String runtimePath = properties.get(Utils.getRuntimePropertyKey(platform));
        String javadocPath = properties.get(Utils.getJavadocPropertyKey(platform));
        String srcPath = properties.get(Utils.getSourcesPropertyKey(platform));

        sdkTextField.setText(sdkPath);
        runtimeTextField.setText(runtimePath);
        javadocTextField.setText(javadocPath);
        srcTextField.setText(srcPath);

        enableCheckBox.setSelected(true);
    }
    
    private boolean isPlatformValid() {
        return JavaFXPlatformUtils.areJFXLocationsCorrect(sdkTextField.getText(), runtimeTextField.getText());
    }
    
    // TODO use message label and icon from Categories ?
    private void setErrorMessage() {
        messageLabel.setText(NbBundle.getMessage(JavaFXPlatformCustomizer.class, "Customizer_Invalid_Platform_Msg")); // NOI18N
    }

    private void clearErrorMessage() {
        messageLabel.setText(null);
    }
    
    // XXX dirty hack, change it
    private void firePlatformChange() {
//        platform.firePropertyChange(Utils.PROP_JAVA_FX, null, null);
        try {
            Method method = JavaPlatform.class.getDeclaredMethod("firePropertyChange", String.class, Object.class, Object.class); // NOI18N
            method.setAccessible(true);
            method.invoke(platform, JavaFXPlatformUtils.PROPERTY_JAVA_FX, null, null);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
