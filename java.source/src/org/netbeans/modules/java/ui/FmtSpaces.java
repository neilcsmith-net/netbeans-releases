/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import org.openide.util.NbBundle;
import static org.netbeans.modules.java.ui.FmtOptions.*;

/**
 *
 * @author  phrebejk
 */
public class FmtSpaces extends JPanel implements TreeCellRenderer, MouseListener, KeyListener {
    
    private static Controller controller;
        
    private DefaultTreeModel model;
  
    private DefaultTreeCellRenderer dr = new DefaultTreeCellRenderer();    
    private JCheckBox renderer = new JCheckBox();
    
    /** Creates new form FmtSpaces */
    public FmtSpaces() {
        initComponents();
        model = createModel();
        cfgTree.setModel(model);
        cfgTree.setRootVisible(false);
        cfgTree.setShowsRootHandles(true);
        cfgTree.setCellRenderer(this);
        cfgTree.setEditable(false);
        cfgTree.addMouseListener(this);
        cfgTree.addKeyListener(this);
        
        dr.setIcon(null);
        dr.setOpenIcon(null);
        dr.setClosedIcon(null);
        
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        for( int i = root.getChildCount(); i >= 0; i-- ) {
            cfgTree.expandRow(i);
        }
    }
    
    public static FormatingOptionsPanel.Category getController() {
        if (controller == null ) {
            controller =  new Controller();
        }
        return controller;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        cfgTree = new javax.swing.JTree();

        setLayout(new java.awt.GridBagLayout());

        cfgTree.setRootVisible(false);
        jScrollPane1.setViewportView(cfgTree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree cfgTree;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
    // TreeCellRenderer implemetation ------------------------------------------
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        renderer.setBackground( selected ? dr.getBackgroundSelectionColor() : dr.getBackgroundNonSelectionColor() );
        renderer.setForeground( selected ? dr.getTextSelectionColor() : dr.getTextNonSelectionColor() );
        renderer.setEnabled( true );

        Object data = ((DefaultMutableTreeNode)value).getUserObject();
        
        if ( data instanceof Item ) {
            Item item = ((Item)data);
            
            if ( ((DefaultMutableTreeNode)value).getAllowsChildren() ) {
                Component c = dr.getTreeCellRendererComponent(tree, value, leaf, expanded, leaf, row, hasFocus);
                return c;
            }
            else {
                renderer.setText( item.displayName );
                renderer.setSelected( item.value );
            }
        }        
        else {
            Component c = dr.getTreeCellRendererComponent(tree, value, leaf, expanded, leaf, row, hasFocus);             
            return c;
        }

        return renderer;
    }
    
    
    // MouseListener implementation --------------------------------------------
    
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        TreePath path = cfgTree.getPathForLocation(e.getPoint().x, e.getPoint().y);
        if ( path != null ) {
            Rectangle r = cfgTree.getPathBounds(path);
            if (r != null) {
                r.width = r.height;
                if ( r.contains(p)) {
                    toggle( path );
                }
            }
        }
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}
    
    // KeyListener implementation ----------------------------------------------

    public void keyTyped(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER ) {

            if ( e.getSource() instanceof JTree ) {
                JTree tree = (JTree) e.getSource();
                TreePath path = tree.getSelectionPath();

                if ( toggle( path )) {
                    e.consume();
                }
            }
        }
    }
    
    // Private methods ---------------------------------------------------------
    
    private DefaultTreeModel createModel() {
        
        Item[] categories = new Item[] {
            new Item("BeforeKeywords",                          // NOI18N
                new Item(spaceBeforeWhile),
                new Item(spaceBeforeElse),
                new Item(spaceBeforeCatch),
                new Item(spaceBeforeFinally) ),
    
            new Item("BeforeParentheses",                       // NOI18N
                new Item(spaceBeforeMethodDeclParen),
                new Item(spaceBeforeMethodCallParen),
                new Item(spaceBeforeIfParen),
                new Item(spaceBeforeForParen),
                new Item(spaceBeforeWhileParen),
                new Item(spaceBeforeCatchParen),
                new Item(spaceBeforeSwitchParen),
                new Item(spaceBeforeSynchronizedParen),
                new Item(spaceBeforeAnnotationParen) ),
    
            new Item("AroundOperators",                         // NOI18N
                new Item(spaceAroundUnaryOps),
                new Item(spaceAroundBinaryOps),
                new Item(spaceAroundTernaryOps),
                new Item(spaceAroundAssignOps) ),
            
            new Item("BeforeLeftBraces",                        // NOI18N
                new Item(spaceBeforeClassDeclLeftBrace),
                new Item(spaceBeforeMethodDeclLeftBrace),
                new Item(spaceBeforeIfLeftBrace),
                new Item(spaceBeforeElseLeftBrace),
                new Item(spaceBeforeWhileLeftBrace),
                new Item(spaceBeforeForLeftBrace),
                new Item(spaceBeforeDoLeftBrace),
                new Item(spaceBeforeSwitchLeftBrace),
                new Item(spaceBeforeTryLeftBrace),
                new Item(spaceBeforeCatchLeftBrace),
                new Item(spaceBeforeFinallyLeftBrace),
                new Item(spaceBeforeSynchronizedLeftBrace),
                new Item(spaceBeforeStaticInitLeftBrace),
                new Item(spaceBeforeArrayInitLeftBrace) ),

            new Item("WithinParentheses",                       // NOI18N
                new Item(spaceWithinParens),
                new Item(spaceWithinMethodDeclParens),
                new Item(spaceWithinMethodCallParens),
                new Item(spaceWithinIfParens),
                new Item(spaceWithinForParens),
                new Item(spaceWithinWhileParens),
                new Item(spaceWithinSwitchParens),
                new Item(spaceWithinCatchParens),
                new Item(spaceWithinSynchronizedParens),
                new Item(spaceWithinTypeCastParens),
                new Item(spaceWithinAnnotationParens),
                new Item(spaceWithinBraces),
                new Item(spaceWithinArrayInitBrackets) ),
                    
                
             new Item("Other",                                  // NOI18N
                new Item(spaceBeforeComma),
                new Item(spaceAfterComma),
                new Item(spaceBeforeSemi),
                new Item(spaceAfterSemi),
                new Item(spaceBeforeColon),
                new Item(spaceAfterColon),
                new Item(spaceAfterTypeCast) )
                
        };
         
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root", true); // NOI18N
        DefaultTreeModel model = new DefaultTreeModel( root );
        
        
        for( Item item : categories ) {
            DefaultMutableTreeNode cn = new DefaultMutableTreeNode( item, true );
            root.add(cn);
            for ( Item si : item.items ) {
                DefaultMutableTreeNode in = new DefaultMutableTreeNode( si, false );
                cn.add(in);
            }
        }
        
        return model;
    }
    
    private boolean toggle(TreePath treePath) {
        
        if( treePath == null ) {
            return false;
        }

        Object o = ((DefaultMutableTreeNode)treePath.getLastPathComponent()).getUserObject();

        DefaultTreeModel model = (DefaultTreeModel)cfgTree.getModel();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();

        if ( o instanceof Item ) {
            Item item = (Item)o;
            
            if ( node.getAllowsChildren() ) {
                return false;
            }
            
            item.value = !item.value;            
            model.nodeChanged(node); 
            model.nodeChanged(node.getParent());
            controller.changed();
        }
        
        return false;
    }
    
    // Innerclasses ------------------------------------------------------------
    
    private static class Item {
        
        String id;        
        String displayName;        
        boolean value;        
        Item[] items;

        
        public Item(String id, Item... items) {
            this.id = id;
            this.items = items;
            this.displayName = NbBundle.getMessage(FmtSpaces.class, "LBL_" + id ); // NOI18N            
        }

        @Override
        public String toString() {
            return displayName;
        }
        
    }
    
    private static class Controller extends FmtOptions.CategorySupport {

        FmtSpaces panel;
        
        public Controller() {
            super("LBL_Spaces", // NOI18N
                  new FmtSpaces(), 
                  NbBundle.getMessage( FmtSpaces.class ,"SAMPLE_Spaces"), // NOI18N
                  new String[] {FmtOptions.placeCatchOnNewLine, Boolean.FALSE.toString()},
                  new String[] {FmtOptions.placeElseOnNewLine, Boolean.FALSE.toString()},
                  new String[] {FmtOptions.placeWhileOnNewLine, Boolean.FALSE.toString()},
                  new String[] {FmtOptions.placeFinallyOnNewLine, Boolean.FALSE.toString()} );
            this.panel = (FmtSpaces) getComponent(null); 
            update();
        }

        @Override
        protected void addListeners() {
            // Should not do anything
        }

        
        @Override
        public void update() {
            
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) panel.model.getRoot();
            List<Item> items = getAllItems();
            
            Preferences node = getPreferences(getCurrentProfileId());
            
            for (Item item : items) {
                boolean df = FmtOptions.getDefaultAsBoolean(item.id);
                item.value = node.getBoolean(item.id, df);
            }

        }

        @Override
        public void applyChanges() {
            storeTo(getPreferences(getCurrentProfileId()));            
        }

        @Override
        public void storeTo(Preferences preferences) {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) panel.model.getRoot();
            List<Item> items = getAllItems();
            
            for (Item item : items) {
                preferences.putBoolean(item.id, item.value);
            }
        }
        
        private List<Item> getAllItems() {
            List<Item> result = new LinkedList<FmtSpaces.Item>();
            
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) panel.model.getRoot();
            
            Enumeration children = root.depthFirstEnumeration();
            
            while( children.hasMoreElements() ) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
                Object o = node.getUserObject();
                if (o instanceof Item) {
                    Item item = (Item) o;
                    if ( item.items == null || item.items.length == 0 ) {
                        result.add( item );
                    }
                }
            }
            
            return result;
        }
    }
    

}
