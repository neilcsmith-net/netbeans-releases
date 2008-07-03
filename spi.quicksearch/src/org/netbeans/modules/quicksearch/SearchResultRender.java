/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.quicksearch;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import org.netbeans.modules.quicksearch.ResultsModel.ItemResult;

/**
 * ListCellRenderer for SearchResults
 * @author Jan Becicka
 */
class SearchResultRender extends JLabel implements ListCellRenderer {
    
    private static final boolean IS_GTK = "GTK".equals(UIManager.getLookAndFeel().getID()); //NOI18N
    
    private JLabel fake = new JLabel("XXXXXXXXXXXXXXXX");
    static int shift;

    public SearchResultRender() {
        super();
        shift = fake.getPreferredSize().width;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (!(value instanceof ItemResult)) {
            return null;
        }
        
        JLabel categoryLabel = new JLabel();
        JPanel rendererComponent = new JPanel();
        categoryLabel.setFont(categoryLabel.getFont().deriveFont(Font.BOLD));
        categoryLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
        rendererComponent.setLayout(new BorderLayout());
        categoryLabel.setOpaque(true);
        rendererComponent.add(categoryLabel, BorderLayout.WEST);
        categoryLabel.setPreferredSize(fake.getPreferredSize());
        categoryLabel.setForeground(QuickSearchComboBar.getCategoryTextColor());
        shift = categoryLabel.getPreferredSize().width;
        ItemResult ir = (ItemResult) value;
        List<? extends KeyStroke> shortcut = ir.getShortcut();
        JMenuItem itemLabel = new JMenuItem(ir.getDisplayName());
        if (shortcut != null && shortcut.size() > 0) {
            // TBD - how to display multi shortcuts?
            itemLabel.setAccelerator(shortcut.get(0));
        }
        if (!IS_GTK) {
            itemLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 2));
        } else {
            itemLabel.setBorder(null);
        }

        CategoryResult cr = ir.getCategory();
        if (cr.isFirstItem(ir)) {
            categoryLabel.setText(cr.getCategory().getDisplayName());
            if (index > 0) {
                JPanel x = new JPanel();
                x.setBackground(QuickSearchComboBar.getShadowColor());
                x.setPreferredSize(new Dimension(x.getPreferredSize().width, 1));
                rendererComponent.add(x, BorderLayout.NORTH);
            }
        } else {
            categoryLabel.setText("");
        }

        JPanel itemPanel = new JPanel();
        itemPanel.setBackground(QuickSearchComboBar.getResultBackground());
        itemPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 1, 2));
        itemPanel.setLayout(new BorderLayout());
        itemPanel.add(itemLabel, BorderLayout.CENTER);
        rendererComponent.add(itemPanel, BorderLayout.CENTER);

        if (isSelected) {
            itemLabel.setBackground(list.getSelectionBackground());
            itemLabel.setForeground(list.getSelectionForeground());
        } else {
            itemLabel.setBackground(QuickSearchComboBar.getResultBackground());
            itemLabel.setForeground(list.getForeground());
        }
        // tell JMenuItem if it's selected or not
        itemLabel.setArmed(isSelected);
        itemLabel.setSelected(isSelected);
        ((JComponent) itemLabel).setOpaque(true);

        rendererComponent.setOpaque(true);
        return rendererComponent;
    }
}
