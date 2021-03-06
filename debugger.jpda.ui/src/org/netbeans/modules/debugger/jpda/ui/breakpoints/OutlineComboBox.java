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
package org.netbeans.modules.debugger.jpda.ui.breakpoints;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Combo box with a tree-like outline in the popup menu.
 * Only one expansion level is supported, for simplicity.
 * 
 * @author martin
 */
public class OutlineComboBox extends JComboBox {
    
    private JList popupList;
    private boolean keepPopupVisible = false;
    private Set<Object> expandedItems = new HashSet<Object>();
    private boolean areExpandables;
    
    public OutlineComboBox() {
        setRenderer(new OutlineComboBoxRenderer());
    }
    
    public void setItems(Object[] items) {
        expandedItems.clear();
        areExpandables = false;
        for (Object o : items) {
            if (o instanceof Expandable) {
                areExpandables = true;
                break;
            }
        }
        setModel(new OutlineComboBoxModel(items));
    }
        
    private void setKeepPopupVisible(boolean keepPopupVisible) {
        this.keepPopupVisible = keepPopupVisible;
    }

    @Override
    public void setPopupVisible(boolean v) {
        if (v || !keepPopupVisible) {
            super.setPopupVisible(v);
        }
    }
    
    public static interface Expandable {
        
        Object[] getItems();
        
        boolean isExpanded();
        
        void setExpanded(boolean expanded);
        
    }
    
    public static interface PopupMenuItem {
        
        String toPopupMenuString();
    }
    
    private class OutlineComboBoxModel extends DefaultComboBoxModel {
        
        public OutlineComboBoxModel(Object[] items) {
            super(items);
        }

        @Override
        public void setSelectedItem(Object item) {
            if (item instanceof Expandable) {
                Expandable exp = (Expandable) item;
                exp.setExpanded(!exp.isExpanded());
                if (popupList != null) {
                    OutlineComboBox.this.setKeepPopupVisible(true);
                    popupList.repaint();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            OutlineComboBox.this.setKeepPopupVisible(false);
                        }
                    });
                }
                addRemoveSubItems(exp);
            } else {
                super.setSelectedItem(item);
            }
        }
        
        private void addRemoveSubItems(Expandable exp) {
            int index = getIndexOf(exp);
            if (exp.isExpanded()) {
                for (Object item : exp.getItems()) {
                    insertElementAt(item, ++index);
                    expandedItems.add(item);
                }
            } else {
                index++;
                for (Object item : exp.getItems()) {
                    removeElementAt(index);
                    expandedItems.remove(item);
                }
            }
        }
    }
    
    private class OutlineComboBoxRenderer extends JLabel
                                          implements ListCellRenderer {
        
        private ShiftBorder sborder;
        
        public OutlineComboBoxRenderer() {
            setOpaque(true);
            sborder = new ShiftBorder(getBorder());
            setBorder(sborder);
        }
        
        @Override
        public Component getListCellRendererComponent(
                                           JList list,
                                           Object value,
                                           int index,
                                           boolean isSelected,
                                           boolean cellHasFocus) {
            popupList = list;
            if (value instanceof Expandable) {
                Expandable exp = (Expandable) value;
                if (exp.isExpanded()) {
                    setIcon(getExpandedIcon());
                } else {
                    setIcon(getCollapsedIcon());
                }
            } else {
                setIcon(null);
            }
            String text;
            if (value instanceof PopupMenuItem) {
                text = ((PopupMenuItem) value).toPopupMenuString();
            } else {
                text = value.toString();
            }
            setText(text);
            int itgap = getIconTextGap();
            int shift = areExpandables ? 1 : 0;
            if (expandedItems.contains(value)) {
                shift++;
            }
            if (value instanceof Expandable) {
                shift--;
            }
            sborder.setShift(shift * (getExpansionHandleWidth() + itgap));
            
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }
        
        private class ShiftBorder implements Border {
            
            private Border b;
            private int shift;
            private Insets NO_INSETS = new Insets(0, 0, 0, 0);
            
            public ShiftBorder(Border b) {
                this.b = b;
            }
            
            public void setShift(int shift) {
                this.shift = shift;
            }

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                if (b != null) {
                    b.paintBorder(c, g, x, y, width, height);
                }
            }

            @Override
            public Insets getBorderInsets(Component c) {
                Insets in;
                if (b == null) {
                    in = NO_INSETS;
                } else {
                    in = b.getBorderInsets(c);
                }
                if (shift != 0) {
                    in = new Insets(in.top, shift + in.left, in.bottom, in.right);
                }
                return in;
            }

            @Override
            public boolean isBorderOpaque() {
                if (b == null) return false;
                return b.isBorderOpaque();
            }
            
        } 
    }
    
    private static Icon getExpandedIcon() {
        return UIManager.getIcon ("Tree.expandedIcon"); //NOI18N
    }
    
    private static Icon getCollapsedIcon() {
        return UIManager.getIcon ("Tree.collapsedIcon"); //NOI18N
    }
    
    private static int expansionHandleWidth;
    private static int getExpansionHandleWidth() {
        if (expansionHandleWidth == 0) {
            expansionHandleWidth = getExpandedIcon ().getIconWidth ();
        }
        return expansionHandleWidth;
    }

}
