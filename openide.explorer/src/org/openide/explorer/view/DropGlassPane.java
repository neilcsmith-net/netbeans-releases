/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
package org.openide.explorer.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import org.openide.util.Parameters;


/**
 * Glass pane which is used for paint of a drop line over <code>JComponent</code>.
 *
 * @author  Jiri Rechtacek
 *
 * @see java.awt.dnd.DropTarget
 * @see org.openide.explorer.view.TreeViewDropSupport
 */
final class DropGlassPane extends JPanel {
    private static HashMap<Integer, DropGlassPane> map = new HashMap<Integer, DropGlassPane>();
    final static private int MIN_X = 5;
    final static private int MIN_Y = 3;
    final static private int MIN_WIDTH = 10;
    final static private int MIN_HEIGTH = 3;
    transient static private Component oldPane;
    transient static private JComponent originalSource;
    transient static private boolean wasVisible;
    Line2D line = null;

    private DropGlassPane() {
    }

    /** Check the bounds of given line with the bounds of this pane. Optionally
     * calculate the new bounds in current pane's boundary.
     * @param comp
     * @return  */
    synchronized static public DropGlassPane getDefault(JComponent comp) {
        Integer id = new Integer(System.identityHashCode(comp));

        if ((map.get(id)) == null) {
            DropGlassPane dgp = new DropGlassPane();
            dgp.setOpaque(false);
            map.put(id, dgp);
        }

        return map.get(id);
    }

    /** Stores the original glass pane on given tree.
     * @param source the active container
     * @param pane the original glass
     * @param visible was glass pane visible
     */
    static void setOriginalPane(JComponent source, Component pane, boolean visible) {
        if (oldPane != null) {
            throw new IllegalStateException("Original pane already present");
        }
        Parameters.notNull("source", source);
        Parameters.notNull("pane", pane);
        oldPane = pane;
        originalSource = source;
        wasVisible = visible;
    }

    /** Is any original glass pane stored?
     * @return true if true; false otherwise
     */
    static boolean isOriginalPaneStored() {
        return oldPane != null;
    }

    /** Sets the original glass pane to the root pane of stored container.
     */
    static void putBackOriginal() {
        if (oldPane == null) {
            throw new IllegalStateException("No original pane present");
        }
        final JRootPane rp = originalSource.getRootPane();
        if (rp == null) {
            if( originalSource.isDisplayable() ) //#216921
                throw new IllegalStateException("originalSource " + originalSource + " has no root pane: " + rp); // NOI18N
        } else {
            rp.setGlassPane(oldPane);
            oldPane.setVisible(wasVisible);
        }
        oldPane = null;
    }

    /** Unset drop line if setVisible to false.
     * @param boolean aFlag new state */
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);

        if (!aFlag) {
            setDropLine(null);
        }
    }

    /** Set drop line. Given line is used by paint method.
     * @param line drop line */
    public void setDropLine(Line2D line) {
        Line2D oldLine = this.line;
        this.line = line;

        if( (null == oldLine && null != line)
                || (null != oldLine && null == line ) )
            repaint ();
    }

    /** Check the bounds of given line with the bounds of this pane. Optionally
     * calculate the new bounds in current pane's boundary.
     * @param line a line for check
     * @return  a line with bounds inside the pane's boundary */
    private Line2D checkLineBounds(Line2D line) {
        Rectangle bounds = getBounds();
        double startPointX;
        double startPointY;
        double endPointX;
        double endPointY;

        // check start point
        startPointX = Math.max(line.getX1(), bounds.x + MIN_X);
        startPointY = Math.max(line.getY1(), bounds.y + MIN_Y);

        // check end point
        endPointX = Math.min(line.getX2(), (bounds.x + bounds.width) - MIN_WIDTH);
        endPointY = Math.min(line.getY2(), (bounds.y + bounds.height) - MIN_HEIGTH);

        // set new bounds
        line.setLine(startPointX, startPointY, endPointX, endPointY);

        return line;
    }

    /** Paint drop line on glass pane.
     * @param Graphics g Obtained graphics */
    @Override
    public void paint(Graphics g) {
        if (line != null) {
            Color c = UIManager.getColor("Tree.dropLine");
            if (c != null) {
                g.setColor(c);
            }
            
            // check bounds
            line = checkLineBounds(line);

            int x1 = (int) line.getX1();
            int x2 = (int) line.getX2();
            int y1 = (int) line.getY1();

            g.setColor( UIManager.getColor( "Tree.selectionBackground" ) );

            // int y2 = (int)line.getY2 (); actually not used
            // LINE
            g.drawLine(x1 + 2, y1, x2 - 2, y1);
            g.drawLine(x1 + 2, y1 + 1, x2 - 2, y1 + 1);

            // RIGHT
            g.drawLine(x1, y1 - 2, x1, y1 + 3);
            g.drawLine(x1 + 1, y1 - 1, x1 + 1, y1 + 2);

            // LEFT
            g.drawLine(x2, y1 - 2, x2, y1 + 3);
            g.drawLine(x2 - 1, y1 - 1, x2 - 1, y1 + 2);
        }

        // help indication of glass pane for debugging

        /*g.drawLine (0, getBounds ().height / 2, getBounds ().width, getBounds ().height / 2);
        g.drawLine (0, getBounds ().height / 2+1, getBounds ().width, getBounds ().height / 2+1);
        g.drawLine (getBounds ().width / 2, 0, getBounds ().width / 2, getBounds ().height);
        g.drawLine (getBounds ().width / 2+1, 0, getBounds ().width / 2+1, getBounds ().height);
         */
    }
}
