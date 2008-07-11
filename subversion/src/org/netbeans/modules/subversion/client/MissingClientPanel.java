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

/*
 * MissingClientPanel.java
 *
 * Created on Jul 9, 2008, 4:55:42 PM
 */

package org.netbeans.modules.subversion.client;

import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.openide.util.Utilities;

/**
 *
 * @author Tomas Stupka
 */
public class MissingClientPanel extends javax.swing.JPanel {

    /** Creates new form MissingClientPanel */
    public MissingClientPanel() {
        initComponents();
        if(Utilities.isWindows()) {
            tipLabel.setText(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingSvnClientPanel.jLabel1.windows.text"));
        } else {
            tipLabel.setText(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingSvnClientPanel.jLabel1.unix.text"));
        }
        String text = org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingClientPanel.textPane.text");
        textPane.setText(text);
        HTMLEditorKit kit = (HTMLEditorKit) textPane.getEditorKit();
        StyleSheet css = kit.getStyleSheet();
        if (css.getStyleSheets() == null) {
            StyleSheet css2 = new StyleSheet();
            Font f = new JLabel().getFont();
            int size = f.getSize();
            css2.addRule(new StringBuffer("body { font-size: ").append(size) // NOI18N
                    .append("; font-family: ").append(f.getName()).append("; }").toString()); // NOI18N
            css2.addStyleSheet(css);
            kit.setStyleSheet(css2);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        tipLabel = new javax.swing.JLabel();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingSvnClientPanel.jLabel2.text_1")); // NOI18N

        buttonGroup1.add(downloadRadioButton);
        downloadRadioButton.setSelected(true);
        downloadRadioButton.setText(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingSvnClientPanel.bundledRadioButton.text")); // NOI18N

        buttonGroup1.add(cliRadioButton);
        cliRadioButton.setText(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingSvnClientPanel.cliRadioButton.text")); // NOI18N

        textPane.setBackground(jLabel1.getBackground());
        textPane.setBorder(null);
        textPane.setContentType(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingClientPanel.textPane.contentType")); // NOI18N
        textPane.setEditable(false);
        textPane.setText(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingClientPanel.textPane.text")); // NOI18N

        browseButton.setText(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingClientPanel.browseButton.text")); // NOI18N

        tipLabel.setText(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingSvnClientPanel.jLabel1.unix.text")); // NOI18N

        org.jdesktop.layout.GroupLayout cliPanelLayout = new org.jdesktop.layout.GroupLayout(cliPanel);
        cliPanel.setLayout(cliPanelLayout);
        cliPanelLayout.setHorizontalGroup(
            cliPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tipLabel)
            .add(cliPanelLayout.createSequentialGroup()
                .add(executablePathTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 562, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(browseButton))
            .add(textPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
        );
        cliPanelLayout.setVerticalGroup(
            cliPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cliPanelLayout.createSequentialGroup()
                .add(textPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(27, 27, 27)
                .add(cliPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(executablePathTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(browseButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tipLabel))
        );

        forceSharedCheckBox.setText(org.openide.util.NbBundle.getMessage(MissingClientPanel.class, "MissingClientPanel.forceSharedCheckBox.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(23, 23, 23)
                                .add(forceSharedCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(420, 420, 420))
                            .add(layout.createSequentialGroup()
                                .add(downloadRadioButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
                                .add(98, 98, 98))
                            .add(cliRadioButton))))
                .add(0, 0, 0))
            .add(layout.createSequentialGroup()
                .add(44, 44, 44)
                .add(cliPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(downloadRadioButton)
                .add(4, 4, 4)
                .add(forceSharedCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cliRadioButton)
                .add(11, 11, 11)
                .add(cliPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JButton browseButton = new javax.swing.JButton();
    private javax.swing.ButtonGroup buttonGroup1;
    final javax.swing.JPanel cliPanel = new javax.swing.JPanel();
    final javax.swing.JRadioButton cliRadioButton = new javax.swing.JRadioButton();
    final javax.swing.JRadioButton downloadRadioButton = new javax.swing.JRadioButton();
    final javax.swing.JTextField executablePathTextField = new javax.swing.JTextField();
    final javax.swing.JCheckBox forceSharedCheckBox = new javax.swing.JCheckBox();
    private javax.swing.JLabel jLabel1;
    final javax.swing.JTextPane textPane = new javax.swing.JTextPane();
    private javax.swing.JLabel tipLabel;
    // End of variables declaration//GEN-END:variables

}
