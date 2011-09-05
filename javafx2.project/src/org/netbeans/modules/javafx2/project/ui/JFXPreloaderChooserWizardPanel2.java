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
package org.netbeans.modules.javafx2.project.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileView;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.javafx2.project.JFXProjectProperties;
import org.netbeans.modules.javafx2.project.JFXProjectProperties.PreloaderSourceType;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

public class JFXPreloaderChooserWizardPanel2 implements WizardDescriptor.Panel<JFXPreloaderChooserWizard> {

    private static final Icon JAR_ICON = ImageUtilities.loadImageIcon(
            "org/netbeans/modules/javawebstart/resources/jar.gif",   // NOI18N
            false);

    private JFileChooser projectChooser;
    private static String lastProjectDirectoryUsed;
    private JFileChooser jarFileChooser;
    private static String lastJARDirectoryUsed;

    //private JFXPreloaderChooserWizard wizard;
    
    private JFXProjectProperties.PreloaderSourceType sourceType = JFXProjectProperties.PreloaderSourceType.NONE;
            
    private final ChangeSupport cs = new ChangeSupport(this);
    
    JFXPreloaderChooserWizardPanel2(JFXProjectProperties.PreloaderSourceType sourceType) {
        this.sourceType = sourceType;
    }
            
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    //private Component component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public Component getComponent() {
//        if (component == null) {
//            component = new JFXPreloaderChooserVisualPanel2Project();
//        }
//        return component;
        if(sourceType == JFXProjectProperties.PreloaderSourceType.PROJECT) {
            //jarFileChooser = null;
            if (projectChooser == null) { // create the UI component for the wizard step
                projectChooser = ProjectChooser.projectChooser();
                projectChooser.setPreferredSize(new Dimension(400, 300));
                projectChooser.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

                // wizard API: set the caption and index of this panel
                projectChooser.setName(NbBundle.getMessage (JFXPreloaderChooserWizardPanel2.class, "CTL_SelectProject_Caption")); // NOI18N
                projectChooser.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, // NOI18N
                                                 new Integer(1));

                if (lastProjectDirectoryUsed != null)
                    projectChooser.setCurrentDirectory(new File(lastProjectDirectoryUsed));
                projectChooser.setControlButtonsAreShown(false);

                projectChooser.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent ev) {
                        String propName = ev.getPropertyName();
                        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(propName)
                             || JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(propName))
                            cs.fireChange();
                    }
                });
            }
            return projectChooser;
        }
        if(sourceType == JFXProjectProperties.PreloaderSourceType.JAR) {
            //projectChooser = null;
            if (jarFileChooser == null) { // create the UI component for the wizard step
                jarFileChooser = new JFileChooser(lastJARDirectoryUsed);
                jarFileChooser.setFileView(new JARFileView());
                jarFileChooser.setPreferredSize(new Dimension(400, 300));
                jarFileChooser.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

                // wizard API: set the caption and index of this panel
                jarFileChooser.setName(NbBundle.getMessage (JFXPreloaderChooserWizardPanel2.class, "CTL_SelectJAR_Caption")); // NOI18N
                jarFileChooser.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, // NOI18N
                                              new Integer(1));

                jarFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jarFileChooser.setAcceptAllFileFilterUsed(true);
                jarFileChooser.setControlButtonsAreShown(false);
                jarFileChooser.setMultiSelectionEnabled(false);

                jarFileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory()
                               || f.getName().toLowerCase().endsWith(".jar"); // NOI18N
                    }
                    @Override
                    public String getDescription() {
                        return NbBundle.getMessage (JFXPreloaderChooserWizardPanel2.class, "CTL_JarArchivesMask"); // NOI18N
                    }
                });

//                jarFileChooser.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent ev) {
//                        wizard.update();
////                        if (JFileChooser.APPROVE_SELECTION.equals(ev.getActionCommand()))
////                            wizard.update(); //.stepToNext();
////                        else if (JFileChooser.CANCEL_SELECTION.equals(ev.getActionCommand()))
////                            jarFileChooser.getTopLevelAncestor().setVisible(false);
//                    };
//                });

                jarFileChooser.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent ev) {
//                        if (JFileChooser.SELECTED_FILES_CHANGED_PROPERTY
//                                            .equals(ev.getPropertyName()))
//                            cs.fireChange();
                        String propName = ev.getPropertyName();
                        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(propName)
                             || JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(propName))
                            cs.fireChange();
                    }
                });

                jarFileChooser.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage (JFXPreloaderChooserWizardPanel2.class, "CTL_SelectJAR_Step")); // NOI18N
            }
            return jarFileChooser;
        }
        return null;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }

    @Override
    public boolean isValid() {
        if(sourceType == JFXProjectProperties.PreloaderSourceType.PROJECT && projectChooser != null) {
            File file = projectChooser.getSelectedFile();
            if (file != null) {
                FileObject projectDir = FileUtil.toFileObject(FileUtil.normalizeFile(file));
                if (projectDir != null) {
                    try {
                        Project project = ProjectManager.getDefault()
                                                   .findProject(projectDir);
                        if (project != null) { // it is a project directory
                            lastProjectDirectoryUsed = projectChooser.getCurrentDirectory()
                                                               .getAbsolutePath();
                            return true;
                        }
                    }
                    catch (IOException ex) {} // ignore
                }
            }
        }
        if(sourceType == JFXProjectProperties.PreloaderSourceType.JAR && jarFileChooser != null) {
            if(jarFileChooser.getSelectedFile() != null) {
                lastJARDirectoryUsed = jarFileChooser.getCurrentDirectory().getAbsolutePath();
                return true;
            }
        }
        return false;
    }

    @Override
    public final void addChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }
    /*
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
    public final void addChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.add(l);
    }
    }
    public final void removeChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.remove(l);
    }
    }
    protected final void fireChangeEvent() {
    Iterator<ChangeListener> it;
    synchronized (listeners) {
    it = new HashSet<ChangeListener>(listeners).iterator();
    }
    ChangeEvent ev = new ChangeEvent(this);
    while (it.hasNext()) {
    it.next().stateChanged(ev);
    }
    }
     */

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    @Override
    public void readSettings(JFXPreloaderChooserWizard settings) {
        //wizard = settings;
        //setSourceType(wizard.getSourceType());
    }

    @Override
    public void storeSettings(JFXPreloaderChooserWizard settings) {
        if (projectChooser != null) {
            File file = projectChooser.getSelectedFile();
            if (file == null) {
                return;
            }
            FileObject projectDir = FileUtil.toFileObject(FileUtil.normalizeFile(file));
            if (projectDir == null) {
                return;
            }

            Project project = null;
            try {
                project = ProjectManager.getDefault().findProject(projectDir);
            }
            catch (IOException ex) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            }
            if (project == null)
                return;

            settings.setSourceType(PreloaderSourceType.PROJECT);
            settings.setSelectedSource(file);
        }
        if (jarFileChooser != null) {
            File file = jarFileChooser.getSelectedFile();
            if (file == null) {
                return;
            }
            settings.setSourceType(PreloaderSourceType.JAR);
            settings.setSelectedSource(file);
        }
        return;
    }

    public void setSourceType(JFXProjectProperties.PreloaderSourceType sourceType) {
        this.sourceType = sourceType;
    }
    
    public JFXProjectProperties.PreloaderSourceType getSourceType() {
        return sourceType;
    }

    private class JARFileView extends FileView {
        
        @Override
        public Icon getIcon(File f) {
            if( f.getName().toLowerCase().endsWith(".jar") ) {
                return JAR_ICON;
            }
            return super.getIcon(f);
        }
        
    }
}
