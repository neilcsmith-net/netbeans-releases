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

package org.netbeans.modules.php.editor.indent;

import java.io.IOException;
import org.netbeans.modules.options.editor.spi.PreferencesCustomizer;
import org.netbeans.modules.php.editor.indent.FmtOptions.CategorySupport;
import org.netbeans.modules.php.editor.indent.ui.Utils;


/**
 *
 * @author  phrebejk
 */
public class FmtBraces extends javax.swing.JPanel {
    
    /** Creates new form FmtAlignmentBraces */
    public FmtBraces() {
        initComponents();
        classDeclCombo.putClientProperty(CategorySupport.OPTION_ID, FmtOptions.openingBraceStyle);
    }
    
    public static PreferencesCustomizer.Factory getController() {
        String preview = "";
        try {
            preview = Utils.loadPreviewText(FmtTabsIndents.class.getClassLoader().getResourceAsStream("org/netbeans/modules/php/editor/indent/ui/Braces.php"));
        } catch (IOException ex) {
            // TODO log it
        }
        return new CategorySupport.Factory("braces", FmtBraces.class, //NOI18N
                preview,
                new String[] { FmtOptions.openingBraceStyle, FmtOptions.OBRACE_PRESERVE }
//                new String[] { FmtOptions.wrapArrayInit, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapAssert, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapAssignOps, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapBinaryOps, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapChainedMethodCalls, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapDoWhileStatement, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapEnumConstants, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapExtendsImplementsKeyword, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapExtendsImplementsList, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapFor, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapForStatement, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapIfStatement, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapMethodCallArgs, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapAnnotationArgs, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapMethodParams, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapTernaryOps, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapThrowsKeyword, WrapStyle.WRAP_ALWAYS.name() },
//                new String[] { FmtOptions.wrapThrowsList, WrapStyle.WRAP_ALWAYS.name() },
        );
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bracesPlacementLabel = new javax.swing.JLabel();
        classDeclCombo = new javax.swing.JComboBox();

        setName(org.openide.util.NbBundle.getMessage(FmtBraces.class, "LBL_Braces")); // NOI18N
        setOpaque(false);

        org.openide.awt.Mnemonics.setLocalizedText(bracesPlacementLabel, org.openide.util.NbBundle.getMessage(FmtBraces.class, "LBL_br_bracesPlacement")); // NOI18N

        classDeclCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(bracesPlacementLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(classDeclCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 149, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(bracesPlacementLabel)
                    .add(classDeclCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bracesPlacementLabel;
    private javax.swing.JComboBox classDeclCombo;
    // End of variables declaration//GEN-END:variables
    
}
