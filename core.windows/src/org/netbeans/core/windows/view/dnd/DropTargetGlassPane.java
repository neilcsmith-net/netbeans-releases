/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.core.windows.view.dnd;


import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import org.netbeans.core.windows.Constants;
import org.netbeans.core.windows.Debug;
import org.netbeans.core.windows.view.Controller;
import org.netbeans.core.windows.view.ui.tabcontrol.EqualPolygon;


/**
 * Glass pane which is used for <code>DefaultContainerImpl</code>
 * as a component associated with <code>DropTarget</code> to be able
 * to paint 'drag under' indications for that container. 
 *
 *
 * @author  Peter Zavadsky
 *
 * @see java.awt.dnd.DropTarget
 * @see org.netbeans.core.windows.DefaultContainerImpl
 */
public final class DropTargetGlassPane extends JPanel implements DropTargetListener {

    // XXX PENDING
    private final Observer observer;
    // XXX PENDING
    private final Informer informer;
    
    private static boolean isHardwareDoubleBuffer = false;
    
    /** Current location of cursor in over the glass pane,
     * or <code>null</code> in the case there it is not above
     * this component currently. */
    private Point location;
    
    /** <code>TopComponentDroppable</code> used in paint to get indication
     * rectangle. */
    private TopComponentDroppable droppable;
    
    /** Debugging flag. */
    private static final boolean DEBUG = Debug.isLoggable(DropTargetGlassPane.class);

    

    /** Creates non initialized <code>DropTargetGlassPane</code>. */
    public DropTargetGlassPane(Observer observer, Informer informer) {
        this.observer = observer;
        this.informer = informer;
        isHardwareDoubleBuffer = !RepaintManager.currentManager(this).isDoubleBufferingEnabled();
        
        setOpaque(false);
    }

    
    /** Called when started drag operation, to save the old visibility state. */
    public void initialize() {
        if(isVisible()) {
            // For unselected internal frame the visibility could
            // be already set, but due to a bug is needed to revalidate it.
            revalidate();
        } else {
            setVisible(true);
        }
    }

    /** Called when finished drag operation, to reset the old visibility state. */
    public void uninitialize() {
        if(location != null) {
            // #22123. Not removed drop inidication.
            dragFinished();
        }

        setVisible(false);
    }
    
    /** Called when the drag operation performed over this drop target. */
    void dragOver(Point location, TopComponentDroppable droppable) {
        this.droppable = droppable;
        if (dragRepaintManager == null) {
            setDragRepaintManager (new DragRepaintManager(this));
        }
        setDragLocation (location);
    }
    
    
    private Point dragLocation = null;
    private void setDragLocation (Point p) {
        Point old = dragLocation;
        dragLocation = p;
        if (p != null && p.equals(old)) {
            return;
        } else if (p == null) {
            //XXX clear?
            return;
        }
        
        if (droppable != null) {
            Component c = droppable.getDropComponent();
            
            Shape s = droppable.getIndicationForLocation (
                SwingUtilities.convertPoint(this, p, c));
            
            dragRepaintManager.setShapeAndTarget(s, c);
        } else {
            dragRepaintManager.eraseLastIndication(null);
        }
        
    }
    

    
    /** Called when the drag operation exited from this drop target. */
    private void dragExited() {
        clear();
    }
    
    private boolean guidedPaint = false;
    
    /** DragRepaintManager will call paint directly to quickly produce
     * visual feedback.  It will call this method when it is doing this,
     * so the component knows it will not need to paint the drop indication;
     * but it should paint it from a normal RepaintManager induced paint. */
    private void setGuidedPaint (boolean val) {
        guidedPaint = val;
    }
    
    /** Determine if the current paint cycle is caused by standard repainting
     * or the custom instant repaint logic in DragRepaintManager */
    private boolean isGuidedPaint () {
        return guidedPaint;
    }
    
    /** Hacks the problem when exiting of drop target, sometimes the framework
     * "forgets" to send drag exit event (when moved from the drop target too
     * quickly??) thus the indication rectangle remains visible. Used to fix
     * this problem. */
    public void clearIndications() {
        clear();
    }

    /** Called when changed drag action. */
    private void dragActionChanged(Point location) {
        setDragLocation(location);
    }

    /** Called when drag operation finished. */
    private void dragFinished() {
        clear();
        setDragRepaintManager(null);
    }
    
    private void setDragRepaintManager (DragRepaintManager drm) {
        this.dragRepaintManager = drm;
    }

    private DragRepaintManager dragRepaintManager = null;

    /** Clears glass pane. */
    private void clear() {
        this.droppable = null;
        
        if (dragRepaintManager != null) {
            dragRepaintManager.setShapeAndTarget(null, null);
        }
        setDragLocation(null);
    }

    /** Overrides superclass method, to indicate the 'drag under' gesture
     * in the case there is cursor drag operation in progress and cursor
     * is above this drop target. */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!isGuidedPaint() && dragRepaintManager != null) {
            dragRepaintManager.paintCurrentIndication ((Graphics2D) g);
        }
    }
    
    // PENDING Take the color from UI Defaults
    private static final Color FILL_COLOR = new Color( 200, 200, 200, 120 );
    
    
    // >> DropTargetListener implementation >>
    /** Implements <code>DropTargetListener</code> method.
     * accepts/rejects the drag operation if move or copy operation
     * is specified. */
    public void dragEnter(DropTargetDragEvent evt) {
        if(DEBUG) {
            debugLog(""); // NOI18N
            debugLog("dragEnter"); // NO18N
        }
        
        int dropAction = evt.getDropAction();
        // Mask action NONE to MOVE one.
        if(dropAction == DnDConstants.ACTION_NONE) {
            dropAction = DnDConstants.ACTION_MOVE;
        }
        
        if((dropAction & DnDConstants.ACTION_COPY_OR_MOVE) > 0) {
            evt.acceptDrag(dropAction);
        } else {
            evt.rejectDrag();
        }
    }

    /** Implements <code>DropTargetListener</code> method.
     * Unsets the glass pane to show 'drag under' gestures. */
    public void dragExit(DropTargetEvent evt) {
        if(DEBUG) {
            debugLog(""); // NOI18N
            debugLog("dragExit"); // NO18N
        }
        
        Component c = evt.getDropTargetContext().getComponent();
        if(c == this) {
            this.dragExited();
        }
    }
    
    /** Implements <code>DropTargetListener</code> method.
     * Informs the glass pane about the location of dragged cursor above
     * the component. */
    public void dragOver(DropTargetDragEvent evt) {
        if(DEBUG) {
            debugLog(""); // NOI18N
            debugLog("dragOver"); // NOI18N
        }
        
        if (dragRepaintManager == null) {
            setDragRepaintManager (new DragRepaintManager(this));
        }
        
        // XXX Eliminate bug, see dragExitedHack.
        observer.setLastDropTarget(this);
    }

    /** Implements <code>DropTargetListener</code> method.
     * When changed the drag action accepts/rejects the drag operation
     * appropriatelly */
    public void dropActionChanged(DropTargetDragEvent evt) {
        if(DEBUG) {
            debugLog(""); // NOI18N
            debugLog("dropActionChanged"); // NOI18N
        }
        
        int dropAction = evt.getDropAction();
        boolean acceptDrag;
        
        if((dropAction == DnDConstants.ACTION_MOVE)
        || (dropAction == DnDConstants.ACTION_COPY
            && informer.isCopyOperationPossible())) {
                
            acceptDrag = true;
        } else {
            acceptDrag = false;
        }

        if(acceptDrag) {
            evt.acceptDrag(dropAction);
        } else {
            evt.rejectDrag();
        }
        
        Component c = evt.getDropTargetContext().getComponent();
        if(c == this) {
            this.dragActionChanged(acceptDrag ? evt.getLocation() : null);
        }
    }

    /** Implements <code>DropTargetListener</code> method. 
     * Perfoms the actual drop operation. */
    public void drop(DropTargetDropEvent evt) {
        if(DEBUG) {
            debugLog(""); // NOI18N
            debugLog("drop"); // NOI18N
        }
        
        // Inform glass pane about finished drag operation.
        Component c = evt.getDropTargetContext().getComponent();
        if(c == this) {
            this.dragFinished();
        }

        int dropAction = evt.getDropAction();
        if(dropAction != DnDConstants.ACTION_MOVE
        && dropAction != DnDConstants.ACTION_COPY) {
            // Not supported dnd operation.
            evt.rejectDrop();
            return;
        }
        
        // Accepts drop operation.
        evt.acceptDrop(dropAction);
        
        boolean success = false;

        try {
            Point location = evt.getLocation();

            // Checks whetger it is in around center panel area.
            // In that case the drop will be tried later.
            // PENDING unify it.
            SwingUtilities.convertPointToScreen(location, c);
            if(WindowDnDManager.isAroundCenterPanel(location)) {
                return;
            }

            success = WindowDnDManager.tryPerformDrop(
                    informer.getController(), informer.getFloatingFrames(),
                    location, dropAction, evt.getTransferable());
        } finally {
            // Complete the drop operation.
            // XXX #21917.
            observer.setDropSuccess(success);
            evt.dropComplete(false);
            //evt.dropComplete(success);
        }
    }
    // >> DropTargetListener implementation >>



    private static void debugLog(String message) {
        Debug.log(DropTargetGlassPane.class, message);
    }
    
    
    // XXX
    /** Glass pane uses this interface to inform about changes. */
    interface Observer {
        public void setDropSuccess(boolean success);
        public void setLastDropTarget(DropTargetGlassPane glassPane);
    } // End of Observer.

    // XXX
    interface Informer {
        public boolean isCopyOperationPossible();
        public Controller getController();
        public Set getFloatingFrames();
    }
    
    /** A scratch rectangle to save allocating one for every pixel the mouse
     * is dragged */
    private static final Rectangle scratch = new Rectangle();
    private static int ct = 0;
    /** Encapsulates the painting logic associated with dragging, and provides
     * optimized repainting services.  Will paint the tab indication onto the
     * glass pane directly in real time.  The trigger method is setShapeAndTarget(),
     * which will trigger painting the indication on the current target, and
     * erasing the previous one if necessary.  Note it is important that the
     * Shape objects passed honor the equals() method properly. 
     */
    private static class DragRepaintManager {
        private Shape shape = null;
        private Component lastDropComponent;
        DropTargetGlassPane pane;
        private Graphics2D g = null;
        
        public DragRepaintManager (DropTargetGlassPane pane) {
            this.pane = pane;
            if (isHardwareDoubleBuffer) {
               //Only way to avoid screen corruption on OS-X
               RepaintManager.currentManager(pane).setDoubleBufferingEnabled(true);
            }
        }
        
        public void finalize() {
            if (g != null) g.dispose();
            if (isHardwareDoubleBuffer) {
               RepaintManager.currentManager(pane).setDoubleBufferingEnabled(false);
            }
        }
        
        private Graphics2D getGraphics() {
            if (g == null) {
                g = (Graphics2D) pane.getGraphics();
            }
            return g;
        }
        
        public void clear() {
            lastDropComponent = null;
        }
        
        public void setShapeAndTarget (Shape s, Component c) {
            Shape old = shape;
            if (old != null && s != null) {
                if (!shape.equals(s)) {
                    shape = s;
                    shapeChange (old, shape, c);
                }
            } else if ((old == null) != (s == null)) {
                shape = s;
                shapeChange (old, s, c);
            } 
        }
        
        public void paintCurrentIndication (Graphics2D g) {
            if (g == null) {
                g = getGraphics();
            }
            if (shape != null) {
                paintShapeOnGlassPane (shape, g, false);
            }
        }
        
        public void eraseLastIndication (Graphics2D g) {
            if (g == null) {
                g = getGraphics();
            }
            if (shape == null) {
                return;
            }
            paintShapeOnGlassPane (shape, g, true);
        }
        
        private void shapeChange (Shape old, Shape nue, Component c) {
            if (old != null) {
                paintShapeOnGlassPane (old, g, true);
            }
            lastDropComponent = c;
            if (nue != null) {
                paintShapeOnGlassPane (nue, g, false);
            }
        }
        
        private void paintShapeOnGlassPane (Shape s, Graphics2D g, boolean erasing) {
            if (g == null) {
                g = getGraphics();
            }
            if (g == null) {
                return;
            }
            pane.setGuidedPaint(true);
            try {
                JComponent toPaint;
                //If we are erasing, we want to paint the root pane so any 
                //pixels outside the bounds of the component are repainted
                if (erasing) {
                    toPaint = (JComponent) ((JComponent)lastDropComponent).getRootPane();
                } else {
                    toPaint = (JComponent) lastDropComponent;
                }
                if (erasing) {
                    toPaint.paint (g);
                } else {
                    // only set the clip when really want to paint it, not when erasing..
                    Shape clip = getClipForIndication (s, true, lastDropComponent);
                    g.setClip (clip);
                    paintShape (s, g);
                }

                //Clear the clip - at least on OS-X not doing so can interfere
                //with pending paint events, even though it shouldn't
                g.setClip (null);
            } finally {
                pane.setGuidedPaint(false);
            }
            if (isHardwareDoubleBuffer) {
                Toolkit.getDefaultToolkit().sync();
            }
        }
        
        private void paintShape (Shape s, Graphics2D g) {
            if (g == null) {
                g = getGraphics();
            }
            Color oldColor = g.getColor();
            Stroke oldStroke = g.getStroke();
            
            g.setColor(Color.red);        	
	
            g.setStroke(createIndicationStroke());
            Color fillColor = Constants.SWITCH_DROP_INDICATION_FADE ? FILL_COLOR : null; 
            if(s instanceof Rectangle) {
                drawIndicationRectangle(g, (Rectangle)s, lastDropComponent, fillColor);
            } else if(s instanceof GeneralPath) {
                drawIndicationGeneralPath(g, (GeneralPath)s, lastDropComponent, fillColor);	    
            } else if (s instanceof Polygon) {
                drawIndicationPolygon (g, (Polygon) s, lastDropComponent, fillColor);
            }
            g.setColor(oldColor);
            g.setStroke(oldStroke);
        }
        
        /** Creates indication pen stroke. Utility method. */
        private Stroke createIndicationStroke() {
            return new BasicStroke(2); // width of stroke is bigger to default one
    //        float[] dashPattern = { 1, 1 };
    //        return new BasicStroke(
    //            3.0F, // width
    //            BasicStroke.CAP_BUTT, // decoration of the ends
    //            BasicStroke.JOIN_MITER, // decoration applied when segments meet
    //            1.0F, // mitter limit where to trim the join
    //            dashPattern, // dashing pattern
    //            0.0F // offset to start the dashing pattern
    //        ); // PENDING
        }        
        
        
        private Shape getClipForIndication (Shape indication, boolean translate, Component target) {
            Shape clip;
            if (indication instanceof Rectangle) {
                scratch.setBounds((Rectangle) indication);
                scratch.x -= 3;
                scratch.y -= 3;
                scratch.width +=6;
                scratch.height +=6;
                Area a = new Area(scratch);
                scratch.setBounds ((Rectangle) indication);
                scratch.x += 4;
                scratch.y += 4;
                scratch.width -=8;
                scratch.height -= 8;
                a.subtract(new Area(scratch));
                if (translate) {
                    Point p = new Point (0,0);
                    
                    p = SwingUtilities.convertPoint(lastDropComponent, p, 
                        pane);
                    AffineTransform at = AffineTransform.getTranslateInstance(p.x, p.y);
                    a.transform(at);
                }
                clip = a;
            } else {
                if (indication instanceof Polygon) {
                    if (translate) {
                        indication = getTransformedPath ((Polygon) indication, target);
                    }
                } else if (indication instanceof GeneralPath) {
                    if (translate) {
                        indication = getTransformedPath ((GeneralPath)indication, target);
                    }
                } else {
                    //who knows what it is...
                    return null;
                }
                clip = new BasicStroke (5).createStrokedShape(indication);
            }
            return clip;
        }
        
        private Polygon getTransformedPath (Polygon path, Component source) {
            Polygon result = new EqualPolygon (path.xpoints, path.ypoints, path.npoints);
            //XXX shrink the polgon here
            Point point = new Point(0, 0);
            point =  SwingUtilities.convertPoint(source, point, pane);
            result.translate (point.x, point.y);
            return result;
        }

        /** We do some munging of GeneralPaths before painting them.  That logic
         * is encapsulated here so it can also be used by the code that generates
         * a clip shape */
        private GeneralPath getTransformedPath (GeneralPath path, Component source) {
            Point point = new Point(0, 0);
            point =  SwingUtilities.convertPoint(source, point, pane);
            path = (GeneralPath) ((GeneralPath) path).clone();
            path.transform(new AffineTransform(1D, 0D, 0D, 1D, point.x, point.y));
            return path;
        }

        private void drawIndicationPolygon(Graphics2D g, Polygon p, Component source, Color fillColor ) {
            if (g == null) {
                g = getGraphics();
            }
            Point point = new Point (0,0);
            point = SwingUtilities.convertPoint(source, point, pane);
            p = new EqualPolygon (p.xpoints, p.ypoints, p.npoints);
            p.translate (point.x, point.y);
            g.drawPolygon (p);
            if ( fillColor != null ) {
                g.setColor( fillColor );
                g.fillPolygon (p);
            }
        }   
        
        private void drawIndicationRectangle(Graphics2D g, Rectangle r, Component source, Color fillColor ) {
            if (g == null) {
                g = getGraphics();
            }
            r = SwingUtilities.convertRectangle(source, r, pane);
            // XXX Shrinks the rectangle to take into account the width of pen stroke.	
            g.drawRect(r.x+1, r.y+1, r.width-2, r.height-2);
            if ( fillColor != null ) {
                g.setColor(fillColor);
                g.fillRect(r.x+1, r.y+1, r.width-2, r.height-2);
            }
        }

        private void drawIndicationGeneralPath(Graphics2D g, GeneralPath path, Component source, Color fillColor ) {
            if (g == null) {
                g = getGraphics();
            }
            // Convert path to this coordinates.
            path = getTransformedPath (path, source);

            g.draw(path);
            if ( fillColor != null ) {
                g.setColor( fillColor );
                g.fill(path);
            }
        }        
    }

}
