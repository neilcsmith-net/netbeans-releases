/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.openide.explorer.view;

import javax.swing.JPanel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;
import org.netbeans.junit.NbTestCase;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 * Tests for class ContextTreeViewTest
 */
public class ContextTreeViewModelTest extends NbTestCase {
    
    private static final int NO_OF_NODES = 3;
    
    public ContextTreeViewModelTest(String name) {
        super(name);
    }
    
    protected boolean runInEQ() {
        return true;
    }
    
    public void testCheckThatTheModelFiresChangesAboutOnlyVisibleNodes() throws Throwable {
        final AbstractNode root = new AbstractNode(new Children.Array());
        root.setName("test root");
        
        root.getChildren().add(new Node[] {
            createNode("kuk", true),
            createNode("huk", true),
        });
        
        
        NodeTreeModel m = new ContextTreeView.NodeContextModel();
        m.setNode(root);
        TreeNode visual = (TreeNode)m.getRoot();
        waitEQ();
        
        
        assertEquals("Leaf nodes are not counted", 0, m.getChildCount(visual));
        
        Listener listener = new Listener();
        m.addTreeModelListener(listener);
        
        Node n = createNode("blik", true);
        
        root.getChildren().add(new Node[] { n });
        assertEquals("Leaf nodes are not counted even when added", 0, m.getChildCount(visual));
        assertEquals("Really added", n.getParentNode(), root);
        listener.assertEvents("No events", 0);
        
        root.getChildren().remove(new Node[] { n });
        listener.assertEvents("Still no events", 0);
        assertNull("Removed", n.getParentNode());
        
        Node nonLeaf = createNode("nonleaf", false);
        root.getChildren().add(new Node[] { nonLeaf });
        assertEquals("One child is there", 1, m.getChildCount(visual));
        listener.assertEvents("This node is visible there", 1);
        listener.assertIndexes("Added at position zero", new int[] { 0 });
        assertEquals("Really added", nonLeaf.getParentNode(), root);
        
        root.getChildren().remove(new Node[] { nonLeaf });
        assertEquals("One child is away", 0, m.getChildCount(visual));
        assertNull("Removed", nonLeaf.getParentNode());
        listener.assertEvents("And it has been removed", 1);
        listener.assertIndexes("Removed from position zero", new int[] { 0 });
        
    }
    
    public void testABitMoreComplexAddAndRemoveEventCheck() throws Throwable {
        final AbstractNode root = new AbstractNode(new Children.Array());
        root.setName("test root");
        
        root.getChildren().add(new Node[] {
            createNode("kuk", false),
            createNode("huk", false),
        });
        
        
        NodeTreeModel m = new ContextTreeView.NodeContextModel();
        m.setNode(root);
        TreeNode visual = (TreeNode)m.getRoot();
        waitEQ();
        
        
        assertEquals("Initial size is two", 2, m.getChildCount(visual));
        
        Listener listener = new Listener();
        m.addTreeModelListener(listener);
        
        Node[] arr = {
            createNode("add1", false), createNode("add2", false)
        };
        
        root.getChildren().add(arr);
        listener.assertEvents("One addition", 1);
        listener.assertIndexes("after the two first", new int[] { 2, 3 });
        
        root.getChildren().remove(arr);
        listener.assertEvents("One removal", 1);
        listener.assertIndexes("from end", new int[] { 2, 3 });
        
    }
    
    public void testRemoveInMiddle() throws Throwable {
        final AbstractNode root = new AbstractNode(new Children.Array());
        root.setName("test root");
        root.getChildren().add(new Node[] { createNode("Ahoj", false) });
        
        Node[] first = {
            createNode("kuk", false),
            createNode("huk", false),
        };
        
        root.getChildren().add(first);
        
        
        NodeTreeModel m = new ContextTreeView.NodeContextModel();
        m.setNode(root);
        TreeNode visual = (TreeNode)m.getRoot();
        waitEQ();
        
        
        assertEquals("Initial size is two", 3, m.getChildCount(visual));
        
        Listener listener = new Listener();
        m.addTreeModelListener(listener);
        
        Node[] arr = {
            createNode("add1", false), createNode("add2", false)
        };
        
        root.getChildren().add(arr);
        listener.assertEvents("One addition", 1);
        listener.assertIndexes("after the three first", new int[] { 3, 4 });
        
        root.getChildren().remove(first);
        listener.assertEvents("One removal", 1);
        listener.assertIndexes("from end", new int[] { 1, 2 });
        
    }
    
    private static Node createNode(String name, boolean leaf) {
        AbstractNode n = new AbstractNode(leaf ? Children.LEAF : new Children.Array());
        n.setName(name);
        return n;
    }
    
    private void waitEQ() throws Throwable {
        /*
        try {
            javax.swing.SwingUtilities.invokeAndWait (new Runnable () { public void run () { } } );
        } catch (java.lang.reflect.InvocationTargetException ex) {
            throw ex.getTargetException ();
        }
         */
    }
    
    private static class Panel extends JPanel
            implements ExplorerManager.Provider {
        private ExplorerManager em = new ExplorerManager();
        
        public ExplorerManager getExplorerManager() {
            return em;
        }
    }
    
    private class Listener implements TreeModelListener {
        private int cnt;
        private int[] indexes;
        
        public void assertEvents(String msg, int cnt) throws Throwable {
            waitEQ();
            assertEquals(msg, cnt, this.cnt);
            this.cnt = 0;
        }
        public void assertIndexes(String msg, int[] arr) throws Throwable {
            waitEQ();
            assertNotNull(msg + " there has to be some", indexes);
            boolean bad = false;
            if (arr.length != indexes.length) {
                bad = true;
            } else {
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i] != indexes[i]) {
                        bad = true;
                    }
                }
            }
            if (bad) {
                fail(msg + " expected: " + toStr(arr) + " was: " + toStr(indexes));
            }
            
            this.indexes = null;
        }
        
        private String toStr(int[] arr) {
            StringBuffer sb = new StringBuffer();
            String sep = "[";
            for (int i = 0; i < arr.length; i++) {
                sb.append(sep);
                sb.append(arr[i]);
                sep = ", ";
            }
            sb.append(']');
            return sb.toString();
        }
        
        public void treeNodesChanged(TreeModelEvent treeModelEvent) {
            cnt++;
        }
        
        public void treeNodesInserted(TreeModelEvent treeModelEvent) {
            cnt++;
            indexes = treeModelEvent.getChildIndices();
        }
        
        public void treeNodesRemoved(TreeModelEvent treeModelEvent) {
            cnt++;
            indexes = treeModelEvent.getChildIndices();
        }
        
        public void treeStructureChanged(TreeModelEvent treeModelEvent) {
            cnt++;
        }
    }
}
