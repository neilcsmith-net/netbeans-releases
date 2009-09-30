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
package org.netbeans.modules.profiler.attach.panels.components;

import java.awt.Color;
import java.awt.Container;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author  Jaroslav Bachorik
 */
public class DirectorySelector extends javax.swing.JPanel {

    public static final String LAYOUT_CHANGED_PROPERTY = DirectorySelector.class.getName() + "#LAYOUT_CHANGED";
    public static final String PATH_PROPERTY = "path"; // NOI18N
    private DocumentListener pathListener = new DocumentListener() {

        private String oldValue = ""; // NOI18N

        public void changedUpdate(DocumentEvent e) {
            firePathChange(textPath.getText());
        }

        public void insertUpdate(DocumentEvent e) {
            firePathChange(textPath.getText());
        }

        public void removeUpdate(DocumentEvent e) {
            firePathChange(textPath.getText());
        }

        private void firePathChange(String newValue) {
            firePropertyChange(PATH_PROPERTY, oldValue, newValue);
            oldValue = newValue;
        }
    };
    private String label = org.openide.util.NbBundle.getMessage(DirectorySelector.class, "DirectorySelector.caption.text"); // NOI18N
    private String initPath = ""; // NOI18N

    /** Creates new form DirectorySelector */
    public DirectorySelector() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        browsePanel = new javax.swing.JPanel();
        textPath = new javax.swing.JTextField();
        buttonBrowse = new javax.swing.JButton();
        hintPanel = new org.netbeans.modules.profiler.attach.panels.components.ResizableHintPanel();

        textPath.setText("null");
        textPath.getDocument().addDocumentListener(this.pathListener);

        org.openide.awt.Mnemonics.setLocalizedText(buttonBrowse, "null");
        buttonBrowse.setMaximumSize(new java.awt.Dimension(43, 23));
        buttonBrowse.setMinimumSize(new java.awt.Dimension(43, 23));
        buttonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout browsePanelLayout = new org.jdesktop.layout.GroupLayout(browsePanel);
        browsePanel.setLayout(browsePanelLayout);
        browsePanelLayout.setHorizontalGroup(
            browsePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, browsePanelLayout.createSequentialGroup()
                .add(textPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(buttonBrowse, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        browsePanelLayout.setVerticalGroup(
            browsePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(browsePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(buttonBrowse, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(textPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        textPath.getAccessibleContext().setAccessibleName("null");
        textPath.getAccessibleContext().setAccessibleDescription("null");
        buttonBrowse.getAccessibleContext().setAccessibleDescription("null");

        hintPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        hintPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        hintPanel.setPreferredSize(new java.awt.Dimension(0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(browsePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(hintPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(browsePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(hintPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBrowseActionPerformed
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        String dirPath = getPath();
        if (dirPath == null || dirPath.length() == 0) {
            dirPath = initPath;
        }
        final File cur = new File(dirPath);

        chooser.setCurrentDirectory(cur);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle(this.label);
        final int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File dir = chooser.getSelectedFile();
            setPath(dir.getAbsolutePath());
        }
    }//GEN-LAST:event_buttonBrowseActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel browsePanel;
    private javax.swing.JButton buttonBrowse;
    private org.netbeans.modules.profiler.attach.panels.components.ResizableHintPanel hintPanel;
    private javax.swing.JTextField textPath;
    // End of variables declaration//GEN-END:variables
    /**
     * Getter for property label.
     * @return Value of property label.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Setter for property label.
     * @param label New value of property label.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Getter for property path.
     * @return Value of property path.
     */
    public String getPath() {
        return this.textPath.getText();
    }

    /**
     * Setter for property path.
     * @param path New value of property path.
     */
    public void setPath(String path) {
        this.textPath.setText(path);
    }

    public void setInitialDir(String path) {
        this.initPath = path;
    }

    public String getInitialDir() {
        return this.initPath;
    }

    /**
     * Getter for property hint.
     * @return Value of property hint.
     */
    public String getHint() {
        return hintPanel.getHint();
    }

    /**
     * Setter for property hint.
     * @param hint New value of property hint.
     */
    public void setHint(String hint) {
        hintPanel.setHint(hint);
        if (hint == null || hint.length() == 0) {
            hintPanel.setVisible(false);
        } else {
            hintPanel.setVisible(true);
        }
        firePropertyChange(LAYOUT_CHANGED_PROPERTY, null, null);
    }

    public Color getHintForeground() {
        return hintPanel.getForeground();
//    return labelHint.getForeground();
    }

    public void setHintForeground(Color color) {
        hintPanel.setForeground(color);
//    labelHint.setForeground(color);
    }

    public Color getHintBackground() {
        return hintPanel.getBackground();
    }

    public void setHintBackground(Color bgcolor) {
        hintPanel.setBackground(bgcolor);
    }
}
