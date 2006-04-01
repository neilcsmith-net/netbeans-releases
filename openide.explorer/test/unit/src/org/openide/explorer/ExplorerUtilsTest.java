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

package org.openide.explorer;

import javax.swing.KeyStroke;
import org.netbeans.junit.NbTestCase;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;

/**
 * Check the behaviour of the ExplorerUtils
 *
 * @author Petr Nejedly
 */
public class ExplorerUtilsTest extends NbTestCase {
    public ExplorerUtilsTest(String testName) {
        super(testName);
    }
    
    public void testGetHelpCtx() throws Exception {
        HelpCtx DEF = new HelpCtx("default");
        
        assertEquals("Use default help for no nodes",
                DEF,
                ExplorerUtils.getHelpCtx(new Node[0], DEF));
        
        assertEquals("Use default help for single node w/o help",
                DEF,
                ExplorerUtils.getHelpCtx(new Node[] {new NoHelpNode()}, DEF));
        
        assertEquals("Use provided help for single node with help",
                new HelpCtx("foo"),
                ExplorerUtils.getHelpCtx(new Node[] {new WithHelpNode("foo")}, DEF));
        
        assertEquals("Use default help for more nodes w/o help",
                DEF,
                ExplorerUtils.getHelpCtx(new Node[] {new NoHelpNode(), new NoHelpNode()}, DEF));
        
        assertEquals("Use provided help if only one node has help",
                new HelpCtx("foo"),
                ExplorerUtils.getHelpCtx(new Node[] {new NoHelpNode(), new WithHelpNode("foo")}, DEF));
        
        assertEquals("Use provided help if more nodes have the same help",
                new HelpCtx("foo"),
                ExplorerUtils.getHelpCtx(new Node[] {new WithHelpNode("foo"), new WithHelpNode("foo")}, DEF));
        
        assertEquals("Use default help if nodes have different helps",
                DEF,
                ExplorerUtils.getHelpCtx(new Node[] {new WithHelpNode("foo"), new WithHelpNode("bar")}, DEF));
    }
    
    public void testUseBigLettersInJavaDocIssue46615() throws Exception {
        assertNotNull(KeyStroke.getKeyStroke("control C"));
        assertNotNull(KeyStroke.getKeyStroke("control X"));
        assertNotNull(KeyStroke.getKeyStroke("control V"));
    }
    
    private static final class NoHelpNode extends AbstractNode {
        public NoHelpNode() {
            super(Children.LEAF);
        }
    }
    
    private static final class WithHelpNode extends AbstractNode {
        private final String id;
        public WithHelpNode(String id) {
            super(Children.LEAF);
            this.id = id;
        }
        public HelpCtx getHelpCtx() {
            return new HelpCtx(id);
        }
    }
    
}
