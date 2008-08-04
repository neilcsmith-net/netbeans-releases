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

package org.netbeans.modules.javascript.libraries.ui.customizer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryChooser;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.modules.javascript.libraries.util.JSLibraryData;
import org.netbeans.modules.javascript.libraries.util.JSLibraryProjectUtils;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.NotifyDescriptor;
import org.openide.util.WeakListeners;

/**
 *
 * @author  Quy Nguyen <quynguyen@netbeans.org>
 */
public class CustomizerJSLibraries extends JPanel {
    private final ProjectCustomizer.Category category;
    private final Project project;
    private final JavaScriptLibraryListModel listModel;
    private final PropertyChangeListener libraryChangeListener;
    
    /** Creates new form JavaScriptLibrariesCustomizer */
    public CustomizerJSLibraries(ProjectCustomizer.Category category, Project project) {
        this.category = category;
        this.project = project;
        this.listModel = new JavaScriptLibraryListModel(project);

        initComponents();
        librariesList.setModel(listModel);
        librariesList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                updateRemoveButtonState();
                updateLocationDisplay();
            }

        });

        locationDisplay.setEditable(false);

        updateRemoveButtonState();

        libraryChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                // re-initialize all libraries
                for (int i = 0; i < listModel.getSize(); i++) {
                    String libName = listModel.getLibraryNameAt(i);
                    String libLocation = listModel.getLibraryLocationAt(i);
                    listModel.changeLibrary(i, libName, libLocation);
                }
            }

        };

        LibraryManager manager = JSLibraryProjectUtils.getLibraryManager(project);
        manager.addPropertyChangeListener(WeakListeners.propertyChange(libraryChangeListener, manager));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        locationLabel = new javax.swing.JLabel();
        locationDisplay = new javax.swing.JTextField();
        librariesListScrollPane = new javax.swing.JScrollPane();
        librariesList = new javax.swing.JList();
        libraryListLabel = new javax.swing.JLabel();
        addLibraryJButton = new javax.swing.JButton();
        removeLibraryJButton = new javax.swing.JButton();

        locationLabel.setLabelFor(locationDisplay);
        org.openide.awt.Mnemonics.setLocalizedText(locationLabel, org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.locationLabel.text")); // NOI18N

        librariesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        librariesListScrollPane.setViewportView(librariesList);
        librariesList.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.librariesList.AccessibleContext.accessibleName")); // NOI18N
        librariesList.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.librariesList.AccessibleContext.accessibleDescription")); // NOI18N

        libraryListLabel.setLabelFor(librariesList);
        org.openide.awt.Mnemonics.setLocalizedText(libraryListLabel, org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.libraryListLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addLibraryJButton, org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.addLibraryJButton.text")); // NOI18N
        addLibraryJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLibraryJButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(removeLibraryJButton, org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.removeLibraryJButton.text")); // NOI18N
        removeLibraryJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLibraryJButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(libraryListLabel)
                    .add(librariesListScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(removeLibraryJButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(addLibraryJButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)))
            .add(layout.createSequentialGroup()
                .add(locationLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(locationDisplay, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(locationLabel)
                    .add(locationDisplay, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(26, 26, 26)
                .add(libraryListLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(addLibraryJButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeLibraryJButton))
                    .add(librariesListScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE))
                .addContainerGap())
        );

        locationDisplay.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.locationDisplay.AccessibleContext.accessibleName")); // NOI18N
        locationDisplay.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.locationDisplay.AccessibleContext.accessibleDescription")); // NOI18N
        librariesListScrollPane.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.librariesListScrollPane.AccessibleContext.accessibleName")); // NOI18N
        addLibraryJButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.addLibraryJButton.AccessibleContext.accessibleDescription")); // NOI18N
        removeLibraryJButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(CustomizerJSLibraries.class, "CustomizerJSLibraries.removeLibraryJButton.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void addLibraryJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLibraryJButtonActionPerformed
   LibraryManager manager = JSLibraryProjectUtils.getLibraryManager(project);
    Set<Library> currentLibs = new LinkedHashSet<Library>();

    for (int i = 0; i < listModel.getSize(); i++) {
        String libName = listModel.getLibraryNameAt(i);
        Library library = manager.getLibrary(libName);
        if (library != null) {
            currentLibs.add(library);
        }
    }

    LibraryChooser.Filter filter = JSLibraryProjectUtils.createDefaultFilter(currentLibs);
    List<JSLibraryData> addedLibraries = JSLibraryProjectUtils.displayAddLibraryDialog(project, filter);

    if (addedLibraries != null) {
        List<JSLibraryData> confirmedLibrariesToCopy = new ArrayList<JSLibraryData>();
        List<JSLibraryData> confirmedLibrariesToWrite = new ArrayList<JSLibraryData>();
        List<ModelModification> listModifier = new ArrayList<ModelModification>();

        for (JSLibraryData libraryData : addedLibraries) {
            boolean addLibrary = true;
            Library library = manager.getLibrary(libraryData.getLibraryName());
            String location = libraryData.getLibraryLocation();

            if (!JSLibraryProjectUtils.isLibraryFolderEmpty(project, library, location)) {
                Object result = JSLibraryProjectUtils.displayLibraryOverwriteDialog(library);
                addLibrary = (result == NotifyDescriptor.YES_OPTION);
            }

            if (addLibrary) {
                String libraryName = library.getName();
                boolean foundMatch = false;
                for (int i = 0; i < listModel.getSize(); i++) {
                    String modelLibName = listModel.getLibraryNameAt(i);
                    if (modelLibName.equals(libraryName)) {
                        foundMatch = true;
                        listModifier.add(new ModelModification(i, modelLibName, location));
                        break;
                    }
                }

                JSLibraryData newData = new JSLibraryData(library.getName(), location);
                if (!foundMatch) {
                    listModifier.add(new ModelModification(libraryName, location));
                    confirmedLibrariesToWrite.add(newData);
                }

                confirmedLibrariesToCopy.add(newData);
            }
        }

        if (confirmedLibrariesToWrite.size() > 0) {
            JSLibraryProjectUtils.addJSLibraryMetadata(project, confirmedLibrariesToWrite);
        }

        if (confirmedLibrariesToCopy.size() > 0) {
            JSLibraryProjectUtils.extractLibrariesWithProgress(project, confirmedLibrariesToCopy);
        }

        // update UI last since display depends on file existence
        for (ModelModification mod : listModifier) {
            if (mod.getIndex() >= 0) {
                listModel.changeLibrary(mod.getIndex(), mod.getName(), mod.getLocation());
            } else {
                listModel.appendLibrary(mod.getName(), mod.getLocation());
            }
        }
    }
}//GEN-LAST:event_addLibraryJButtonActionPerformed

private void removeLibraryJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLibraryJButtonActionPerformed
    int[] removedLibIndices = librariesList.getSelectedIndices();
    assert removedLibIndices.length > 0;

    List<JSLibraryData> selectedLibraries = new ArrayList<JSLibraryData>();

    for (int i = removedLibIndices.length-1; i >= 0; i--) {
        int index = removedLibIndices[i];
        selectedLibraries.add(new JSLibraryData(
                listModel.getLibraryNameAt(index),
                listModel.getLibraryLocationAt(index)));
    }

    List<JSLibraryData> librariesToDelete = new ArrayList<JSLibraryData>();
    List<JSLibraryData> librariesToRemoveInfo = new ArrayList<JSLibraryData>();
    librariesToRemoveInfo.addAll(selectedLibraries);

    LibraryManager manager = JSLibraryProjectUtils.getLibraryManager(project);
    for (JSLibraryData libData : selectedLibraries) {
        String libName = libData.getLibraryName();
        String libLocation = libData.getLibraryLocation();

        Library library = manager.getLibrary(libName);
        if (library == null) {
            continue;
        }

        boolean removeLibrary;
        if (libLocation != null && !JSLibraryProjectUtils.isLibraryFolderEmpty(project, library, libLocation)) {
            Object result = JSLibraryProjectUtils.displayLibraryDeleteConfirm(library);
            removeLibrary = (result == NotifyDescriptor.YES_OPTION);

            if (result == NotifyDescriptor.CANCEL_OPTION) {
                librariesToRemoveInfo.remove(libData);
            }
        } else {
            removeLibrary = true;
        }

        if (removeLibrary) {
            librariesToDelete.add(libData);
        }
    }

    if (librariesToRemoveInfo.size() > 0) {
        for (int i = librariesToRemoveInfo.size()-1; i >= 0; i--) {
            listModel.removeLibrary(librariesToRemoveInfo.get(i).getLibraryName());
        }

        JSLibraryProjectUtils.removeJSLibraryMetadata(project, librariesToRemoveInfo);
    }

    if (librariesToDelete.size() > 0) {
        JSLibraryProjectUtils.deleteLibrariesWithProgress(project, librariesToDelete);
    }
}//GEN-LAST:event_removeLibraryJButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addLibraryJButton;
    private javax.swing.JList librariesList;
    private javax.swing.JScrollPane librariesListScrollPane;
    private javax.swing.JLabel libraryListLabel;
    private javax.swing.JTextField locationDisplay;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JButton removeLibraryJButton;
    // End of variables declaration//GEN-END:variables

    private void updateRemoveButtonState() {
        removeLibraryJButton.setEnabled(librariesList.getSelectedIndex() >= 0);
    }

    private void updateLocationDisplay() {
        int row = librariesList.getSelectedIndex();
        if (row >= 0 && row < listModel.getSize()) {
            String location = listModel.getLibraryLocationAt(row);
            location = (location == null) ? "" : location; // NOI18N

            locationDisplay.setText(location);
        } else {
            locationDisplay.setText("");
        }
    }

    private static final class ModelModification {
        private final String name;
        private final String location;
        private final int index;

        public ModelModification(int index, String name, String location) {
            this.index = index;
            this.name = name;
            this.location = location;
        }

        public ModelModification(String name, String location) {
            this(-1, name, location);
        }

        public int getIndex() {
            return index;
        }

        public String getLocation() {
            return location;
        }

        public String getName() {
            return name;
        }
    }
}
