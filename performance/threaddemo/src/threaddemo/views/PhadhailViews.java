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

package threaddemo.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyVetoException;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreeModel;
import threaddemo.views.looktree.LookTreeView;
import org.openide.explorer.ExplorerPanel;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import threaddemo.model.Phadhail;
import org.netbeans.api.nodes2looks.Nodes;
import org.netbeans.spi.looks.Selectors;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Mutex;
import threaddemo.locking.LockAction;
import threaddemo.locking.Locks;

/**
 * Factory for views over Phadhail.
 * All views are automatically scrollable; you do not need to wrap them in JScrollPane.
 * @author Jesse Glick
 */
public class PhadhailViews {
    
    private PhadhailViews() {}
    
    private static Component nodeBasedView(Node root) {
        Node root2;
        if (Children.MUTEX == Mutex.EVENT) {
            // #35833 branch.
            root2 = root;
        } else {
            root2 = new EQReplannedNode(root);
        }
        ExplorerPanel p = new ExplorerPanel();
        p.setLayout(new BorderLayout());
        JComponent tree = new BeanTreeView();
        p.add(tree, BorderLayout.CENTER);
        p.getExplorerManager().setRootContext(root2);
        try {
            p.getExplorerManager().setSelectedNodes(new Node[] {root2});
        } catch (PropertyVetoException pve) {
            pve.printStackTrace();
        }
        Object key = "org.openide.actions.PopupAction";
        KeyStroke ks = KeyStroke.getKeyStroke("shift F10");
        tree.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ks, key);
        return p;
    }
    
    /** use Nodes API with an Explorer view */
    public static Component nodeView(Phadhail root) {
        return nodeBasedView(new PhadhailNode(root));
    }
    
    /** use Looks and Nodes API with an Explorer view */
    public static Component lookNodeView(Phadhail root) {
        return nodeBasedView(Nodes.node(root, null, Selectors.selector( new PhadhailLookProvider())));
    }
    
    /** use raw Looks API with a JTree */
    public static Component lookView(Phadhail root) {
        // XXX pending a stable API...
        return new JScrollPane(new LookTreeView(root, Selectors.selector( new PhadhailLookProvider() )));
    }
    
    /** use Phadhail directly in a JTree */
    public static Component rawView(Phadhail root) {
        TreeModel model = new PhadhailTreeModel(root);
        JTree tree = new JTree(model) {
            // Could also use a custom TreeCellRenderer, but this is a bit simpler for now.
            public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Phadhail ph = (Phadhail)value;
                return ph.getPath();
            }
        };
        tree.setLargeModel(true);
        return new JScrollPane(tree);
    }
    
    /**
     * Workaround for the fact that Node/Look currently do not run only in AWT.
     */
    private static final class EQReplannedNode extends FilterNode {
        public EQReplannedNode(Node n) {
            super(n, n.isLeaf() ? Children.LEAF : new EQReplannedChildren(n));
        }
        public String getName() {
            return (String)Locks.event().read(new LockAction() {
                public Object run() {
                    return EQReplannedNode.super.getName();
                }
            });
        }
        public String getDisplayName() {
            return (String)Locks.event().read(new LockAction() {
                public Object run() {
                    return EQReplannedNode.super.getDisplayName();
                }
            });
        }
        // XXX any other methods could also be replanned as needed
    }
    private static final class EQReplannedChildren extends FilterNode.Children {
        public EQReplannedChildren(Node n) {
            super(n);
        }
        protected Node copyNode(Node n) {
            return new EQReplannedNode(n);
        }
        public Node findChild(final String name) {
            return (Node)Locks.event().read(new LockAction() {
                public Object run() {
                    return EQReplannedChildren.super.findChild(name);
                }
            });
        }
        public Node[] getNodes(final boolean optimalResult) {
            return (Node[])Locks.event().read(new LockAction() {
                public Object run() {
                    return EQReplannedChildren.super.getNodes(optimalResult);
                }
            });
        }
        protected void filterChildrenAdded(final NodeMemberEvent ev) {
            Locks.event().readLater(new Runnable() {
                public void run() {
                    EQReplannedChildren.super.filterChildrenAdded(ev);
                }
            });
        }
        protected void filterChildrenRemoved(final NodeMemberEvent ev) {
            Locks.event().readLater(new Runnable() {
                public void run() {
                    EQReplannedChildren.super.filterChildrenRemoved(ev);
                }
            });
        }
        protected void filterChildrenReordered(final NodeReorderEvent ev) {
            Locks.event().readLater(new Runnable() {
                public void run() {
                    EQReplannedChildren.super.filterChildrenReordered(ev);
                }
            });
        }
    }
    
}
