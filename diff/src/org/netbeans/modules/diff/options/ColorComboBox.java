/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.diff.options;

import org.openide.util.NbBundle;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 *
 * copied from editor/options.
 * @author Maros Sandor
 */
class ColorComboBox {
    
    public static final String PROP_COLOR = "color"; //NOI18N
    
    private static Object[] content = new Object[] {
	new ColorValue(Color.BLACK), 
	new ColorValue(Color.BLUE), 
	new ColorValue(Color.CYAN), 
	new ColorValue(Color.DARK_GRAY), 
	new ColorValue(Color.GRAY), 
	new ColorValue(Color.GREEN), 
	new ColorValue(Color.LIGHT_GRAY), 
	new ColorValue(Color.MAGENTA), 
	new ColorValue(Color.ORANGE), 
	new ColorValue(Color.PINK), 
	new ColorValue(Color.RED), 
	new ColorValue(Color.WHITE), 
	new ColorValue(Color.YELLOW), 
	ColorValue.CUSTOM_COLOR, 
    };
    
    
    /** Creates a new instance of ColorChooser */
    static void init (final JComboBox combo) {
        combo.setModel (new DefaultComboBoxModel (content));
        combo.setRenderer (new ColorComboBoxRenderer(combo));
        combo.setEditable (true);
        combo.setEditor (new ColorComboBoxRenderer(combo));
	combo.setSelectedItem (new ColorValue(null, null));
        combo.addActionListener (new ColorComboBox.ComboBoxListener(combo));
    }
    
    static void setColor (JComboBox combo, Color color) {
        if (color == null) {
            combo.setSelectedIndex (content.length - 1);
        } else {
            combo.setSelectedItem (new ColorValue(color));
        }
    }
    
    static Color getColor (JComboBox combo) {
        // The last item is Inherited Color or None
        if (combo.getSelectedIndex() < combo.getItemCount() - 1) {
            return ((ColorValue) combo.getSelectedItem()).color;
        } else {
            return null;
        }
    }
    
    private static String loc (String key) {
        return NbBundle.getMessage (ColorComboBox.class, key);
    }
    
    // ..........................................................................
    private static class ComboBoxListener implements ActionListener {
        
        private JComboBox combo;
        private Object lastSelection;
        
        ComboBoxListener(JComboBox combo) {
            this.combo = combo;
            lastSelection = combo.getSelectedItem();
        }
        
        public void actionPerformed(ActionEvent ev) {
            if (combo.getSelectedItem() == ColorValue.CUSTOM_COLOR) {
                Color c = JColorChooser.showDialog(
                    SwingUtilities.getAncestorOfClass(Dialog.class, combo),
                    loc("SelectColor"), //NOI18N
                    lastSelection != null ? ((ColorValue) lastSelection).color : null
                );
                if (c != null) {
                    setColor(combo, c);
                } else if (lastSelection != null) {
                    combo.setSelectedItem(lastSelection);
                }
            }
            lastSelection = combo.getSelectedItem();
        }
        
    } // ComboListener
    
}
