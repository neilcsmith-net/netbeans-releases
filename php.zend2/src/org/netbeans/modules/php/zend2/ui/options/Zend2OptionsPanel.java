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
package org.netbeans.modules.php.zend2.ui.options;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

@NbBundle.Messages({"PhpOptions.zend2.keywords.TabTitle=Frameworks & Tools"})
@OptionsPanelController.Keywords(
        keywords={"php", "zend", "zend 2", "zend2", "framework", "zf", "zf2", "zf 2"},
        location=UiUtils.OPTIONS_PATH,
        tabTitle="#PhpOptions.zend2.keywords.TabTitle")
public class Zend2OptionsPanel extends JPanel {

    private static final long serialVersionUID = 1687321354656L;

    private static final String SKELETON_LAST_FOLDER_SUFFIX = ".skeleton"; // NOI18N
    @NbBundle.Messages("Zend2OptionsPanel.ziles.zip.filter=Zip File (*.zip)")
    private static final FileFilter ZIP_FILE_FILTER = new FileFilter() {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return f.isFile()
                    && f.getName().toLowerCase().endsWith(".zip"); // NOI18N
        }
        @Override
        public String getDescription() {
            return Bundle.Zend2OptionsPanel_ziles_zip_filter();
        }
    };

    private final ChangeSupport changeSupport = new ChangeSupport(this);


    public Zend2OptionsPanel() {
        initComponents();
        errorLabel.setText(" "); // NOI18N

        initListeners();
    }

    private void initListeners() {
        skeletonTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                processUpdate();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                processUpdate();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                processUpdate();
            }
            private void processUpdate() {
                fireChange();
            }
        });
    }

    public String getSkeleton() {
        return skeletonTextField.getText();
    }

    public void setSkeleton(String skeleton) {
        skeletonTextField.setText(skeleton);
    }

    public void setError(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        errorLabel.setText(message);
    }

    public void setWarning(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.warningForeground")); // NOI18N
        errorLabel.setText(message);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        skeletonLabel = new JLabel();
        skeletonTextField = new JTextField();
        browseSkeletonButton = new JButton();
        skeletonInfoLabel = new JLabel();
        noteLabel = new JLabel();
        downloadSkeletonLabel = new JLabel();
        errorLabel = new JLabel();

        Mnemonics.setLocalizedText(skeletonLabel, NbBundle.getMessage(Zend2OptionsPanel.class, "Zend2OptionsPanel.skeletonLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(browseSkeletonButton, NbBundle.getMessage(Zend2OptionsPanel.class, "Zend2OptionsPanel.browseSkeletonButton.text")); // NOI18N
        browseSkeletonButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                browseSkeletonButtonActionPerformed(evt);
            }
        });

        Mnemonics.setLocalizedText(skeletonInfoLabel, NbBundle.getMessage(Zend2OptionsPanel.class, "Zend2OptionsPanel.skeletonInfoLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(noteLabel, NbBundle.getMessage(Zend2OptionsPanel.class, "Zend2OptionsPanel.noteLabel.text")); // NOI18N

        Mnemonics.setLocalizedText(downloadSkeletonLabel, NbBundle.getMessage(Zend2OptionsPanel.class, "Zend2OptionsPanel.downloadSkeletonLabel.text")); // NOI18N
        downloadSkeletonLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                downloadSkeletonLabelMouseEntered(evt);
            }
            public void mousePressed(MouseEvent evt) {
                downloadSkeletonLabelMousePressed(evt);
            }
        });

        Mnemonics.setLocalizedText(errorLabel, "ERROR"); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(skeletonLabel)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(skeletonInfoLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(skeletonTextField)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(browseSkeletonButton))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(downloadSkeletonLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(noteLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(errorLabel))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(skeletonLabel)
                    .addComponent(skeletonTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseSkeletonButton))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(skeletonInfoLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(noteLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(downloadSkeletonLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(errorLabel))
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages("Zend2OptionsPanel.browse.skeleton=Select Zend Skeleton Application (.zip)")
    private void browseSkeletonButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_browseSkeletonButtonActionPerformed
        File skeleton = new FileChooserBuilder(Zend2OptionsPanel.class.getName() + SKELETON_LAST_FOLDER_SUFFIX)
                .setTitle(Bundle.Zend2OptionsPanel_browse_skeleton())
                .setFilesOnly(true)
                .setFileFilter(ZIP_FILE_FILTER)
                .showOpenDialog();
        if (skeleton != null) {
            skeleton = FileUtil.normalizeFile(skeleton);
            skeletonTextField.setText(skeleton.getAbsolutePath());
        }
    }//GEN-LAST:event_browseSkeletonButtonActionPerformed

    private void downloadSkeletonLabelMouseEntered(MouseEvent evt) {//GEN-FIRST:event_downloadSkeletonLabelMouseEntered
        evt.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_downloadSkeletonLabelMouseEntered

    private void downloadSkeletonLabelMousePressed(MouseEvent evt) {//GEN-FIRST:event_downloadSkeletonLabelMousePressed
        try {
            URL url = new URL("https://github.com/zendframework/ZendSkeletonApplication"); // NOI18N
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_downloadSkeletonLabelMousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton browseSkeletonButton;
    private JLabel downloadSkeletonLabel;
    private JLabel errorLabel;
    private JLabel noteLabel;
    private JLabel skeletonInfoLabel;
    private JLabel skeletonLabel;
    private JTextField skeletonTextField;
    // End of variables declaration//GEN-END:variables
}
