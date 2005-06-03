/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.form.layoutdesign.support;

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.jdesktop.layout.*;

import org.netbeans.modules.form.layoutdesign.*;

/**
 * Generates Java layout code based on the passed layout model. 
 *
 * @author Jan Stola
 */
public class SwingLayoutCodeGenerator {
    private static final String LAYOUT_NAME = GroupLayout.class.getName();
    private static final String LAYOUT_VAR_NAME = "layout"; // NOI18N
    /** Layout model of the form. */
    private LayoutModel layoutModel;
    private String layoutVarName;

    /**
     * Maps from component ID to component's variable name.
     */
    private Map/*<String,String>*/ componentIDMap;

    /**
     * Creates new <code>SwingLayoutCodeGenerator</code>.
     *
     * @param layoutModel layout model of the form.
     */
    public SwingLayoutCodeGenerator(LayoutModel layoutModel) {
        componentIDMap = new HashMap/*<String,String>*/();
        this.layoutModel = layoutModel;
    }

    /**
     * Generates Java layout code for the specified container. The generated
     * code is written to the <code>writer</code>.
     *
     * @param writer the writer to generate the code into.
     * @param container container whose code should be generated.
     * @param contVarName code expression for the container.
     * @param compIds IDs of subcomponents.
     * @param compVarNames code expressions of subcomponents.
     */
    public void generateContainerLayout(Writer writer, LayoutComponent container,
        String contExprStr, String contVarName, String[] compIds, String[] compVarNames) throws IOException {
        if (contVarName == null) {
            layoutVarName = LAYOUT_VAR_NAME;
        } else {
            layoutVarName = contVarName + Character.toUpperCase(LAYOUT_VAR_NAME.charAt(0))
                + LAYOUT_VAR_NAME.substring(1);
        }
        fillMap(compIds, compVarNames);
        generateInstantiation(writer, contExprStr);
        StringBuffer sb = new StringBuffer();
        LayoutInterval horizontalInterval = container.getLayoutRoot(LayoutConstants.HORIZONTAL);
        composeGroup(sb, horizontalInterval, true, true);
        String horizontalGroup = sb.toString();
        writer.write(layoutVarName + ".setHorizontalGroup(\n" + horizontalGroup + "\n);\n"); // NOI18N
        sb = new StringBuffer();
        LayoutInterval verticalInterval = container.getLayoutRoot(LayoutConstants.VERTICAL);
        composeGroup(sb, verticalInterval, true, true);
        String verticalGroup = sb.toString();
        writer.write(layoutVarName + ".setVerticalGroup(\n" + verticalGroup + "\n);\n"); // NOI18N
    }

    /**
     * Fills the <code>componentIDMap</code>.
     *
     * @param compIds IDs of components.
     * @param compVarNames code expressions of components.
     */
    private void fillMap(String[] compIds, String[] compVarNames) {
        if (compIds.length != compVarNames.length) {
            throw new IllegalArgumentException("Sizes must match"); // NOI18N
        }
        for (int counter = 0; counter < compIds.length; counter++) {
            componentIDMap.put(compIds[counter], compVarNames[counter]);
        }
    }
    
    /**
     * Generates the "header" of the code e.g. instantiation of the layout
     * and call to the <code>setLayout</code> method.
     */
    private void generateInstantiation(Writer writer, String contExprStr) throws IOException {
        writer.write(LAYOUT_NAME + " " + layoutVarName + " "); // NOI18N
        writer.write("= new " + LAYOUT_NAME + "(" + contExprStr + ");\n"); // NOI18N
        writer.write(contExprStr + ".setLayout(" + layoutVarName + ");\n"); // NOI18N
    }
    
    /**
     * Generates layout code for a group that corresponds
     * to the <code>interval</code>.
     *
     * @param layout buffer to generate the code into.
     * @param interval layout model of the group.
     */
    private void composeGroup(StringBuffer layout, LayoutInterval interval,
        boolean first, boolean last) throws IOException {
        if (interval.isGroup()) {
            int groupAlignment = interval.getGroupAlignment();
            if (interval.isParallel()) {
                boolean notResizable = interval.getMaximumSize() == LayoutConstants.USE_PREFERRED_SIZE;
                if ((interval.getGroupAlignment() == LayoutConstants.DEFAULT) && !notResizable) {
                    layout.append(layoutVarName).append(".createParallelGroup("); // NOI18N
                } else {
                    String alignmentStr = convertAlignment(groupAlignment);
                    layout.append(layoutVarName).append(".createParallelGroup("); // NOI18N
                    layout.append(alignmentStr); // NOI18N
                    if (notResizable) {
                        layout.append(", false"); // NOI18N
                    }
                }
                layout.append(")"); // NOI18N
            } else if (interval.isSequential()) {
                layout.append(layoutVarName).append(".createSequentialGroup()"); // NOI18N
            } else {
                assert false;
            }
            if (interval.getSubIntervalCount() > 0) {
                layout.append("\n"); // NOI18N
            }
            Iterator subIntervals = interval.getSubIntervals();
            while (subIntervals.hasNext()) {
                LayoutInterval subInterval = (LayoutInterval)subIntervals.next();
                fillGroup(layout, subInterval, first,
                          last && (!interval.isSequential() || !subIntervals.hasNext()),
                          groupAlignment);
                if (first && interval.isSequential()) {
                    first = false;
                }
                if (subIntervals.hasNext()) {
                    layout.append("\n"); // NOI18N
                }
            }
        } else {
            layout.append(layoutVarName).append(".createSequentialGroup()\n"); // NOI18N
            fillGroup(layout, interval, true, true, LayoutConstants.DEFAULT);
        }
    }
    
    /**
     * Generate layout code for one element in the group.
     *
     * @param layout buffer to generate the code into.
     * @param interval layout model of the element.
     * @param groupAlignment alignment of the enclosing group.
     */
    private void fillGroup(StringBuffer layout, LayoutInterval interval,
        boolean first, boolean last, int groupAlignment) throws IOException {
        layout.append(".add("); // NOI18N
        if (interval.isGroup()) {
            int alignment = interval.getAlignment();
            if (alignment != LayoutConstants.DEFAULT) {
                String alignmentStr = convertAlignment(alignment);
                layout.append(alignmentStr).append(", "); // NOI18N
            }
            composeGroup(layout, interval, first, last);
        } else {
            int min = interval.getMinimumSize();
            int pref = interval.getPreferredSize();
            int max = interval.getMaximumSize();
            if (interval.isComponent()) {
                int alignment = interval.getAlignment();
                LayoutComponent layoutComp = interval.getComponent();
                String compVarName = (String)componentIDMap.get(layoutComp.getId());
                assert (compVarName != null);
                if ((alignment == LayoutConstants.DEFAULT) || (alignment == groupAlignment)) {
                    layout.append(compVarName);
                } else {
                    String alignmentStr = convertAlignment(alignment);
                    layout.append(alignmentStr).append(", ").append(compVarName); // NOI18N
                }
                if ((min != LayoutConstants.NOT_EXPLICITLY_DEFINED) ||
                    (pref != LayoutConstants.NOT_EXPLICITLY_DEFINED) ||
                    (max != LayoutConstants.NOT_EXPLICITLY_DEFINED)) {
                    layout.append(", "); // NOI18N
                    generateSizeParams(layout, min, pref, max);
                }
            } else if (interval.isEmptySpace()) {
                if (min == LayoutConstants.USE_PREFERRED_SIZE) {
                    min = pref;
                }
                if (max == LayoutConstants.USE_PREFERRED_SIZE) {
                    max = pref;
                }
                // PENDING
                if (interval.isDefaultPadding()) {
                    int padding = first || last ? 12 : 6;
                    min = (min == LayoutConstants.NOT_EXPLICITLY_DEFINED) ? padding : min;
                    pref = (pref == LayoutConstants.NOT_EXPLICITLY_DEFINED) ? padding : pref;
                    max = (max == LayoutConstants.NOT_EXPLICITLY_DEFINED) ? padding : max;
                    min = Math.min(pref, min);
                    max = Math.max(pref, max);
                }
                generateSizeParams(layout, min, pref, max);
            } else {
                assert false;
            }
        }
        layout.append(")"); // NOI18N
    }
    
    /**
     * Generates minimum/preferred/maximum size parameters..
     *
     * @param layout buffer to generate the code into.
     * @param min minimum size.
     * @param pref preffered size.
     * @param max maximum size.
     */
    private void generateSizeParams(StringBuffer layout, int min, int pref, int max) {
        layout.append(convertSize(min)).append(", "); // NOI18N
        layout.append(convertSize(pref)).append(", "); // NOI18N
        layout.append(convertSize(max));
    }

    /**
     * Converts alignment from the layout model constants
     * to <code>GroupLayout</code> constants.
     *
     * @param alignment layout model alignment constant.
     * @return <code>GroupLayout</code> alignment constant that corresponds
     * to the given layout model one.
     */
    private static String convertAlignment(int alignment) {
        String groupAlignment = null;
        switch (alignment) {
            case LayoutConstants.LEADING: groupAlignment = "LEADING"; break; // NOI18N
            case LayoutConstants.TRAILING: groupAlignment = "TRAILING"; break; // NOI18N
            case LayoutConstants.CENTER: groupAlignment = "CENTER"; break; // NOI18N
            case LayoutConstants.BASELINE: groupAlignment = "BASELINE"; break; // NOI18N
            default: assert false; break;
        }
        return LAYOUT_NAME + "." + groupAlignment; // NOI18N
    }
    
    /**
     * Converts minimum/preferred/maximums size from the layout model constants
     * to <code>GroupLayout</code> constants.
     *
     * @param size minimum/preferred/maximum size from layout model.
     * @return minimum/preferred/maximum size or <code>GroupLayout</code> constant
     * that corresponds to the given layout model one.
     */
    private static String convertSize(int size) {
        String convertedSize;
        switch (size) {
            case LayoutConstants.NOT_EXPLICITLY_DEFINED: convertedSize = LAYOUT_NAME + ".DEFAULT_SIZE"; break; // NOI18N
            case LayoutConstants.USE_PREFERRED_SIZE: convertedSize = LAYOUT_NAME + ".PREFERRED_SIZE"; break; // NOI18N
            case Short.MAX_VALUE: convertedSize = "Short.MAX_VALUE"; break; // NOI18N
            default: assert (size >= 0); convertedSize = new Integer(size).toString(); break;
        }
        return convertedSize;
    }

}
