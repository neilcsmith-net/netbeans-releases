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

package org.openide.explorer.propertysheet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import org.netbeans.junit.NbTestCase;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

// This test class tests the main functionality of the property sheet
public class ProxyNodeTest extends NbTestCase {
    public ProxyNodeTest(String name) {
        super(name);
    }
    
    protected boolean runInEQ() {
        return false;
    }
    
    
    ProxyNode pn = null;
    TNode a = null;
    TNode b = null;
    TProperty pa = null;
    TProperty pb = null;
    ProxyNodeListener nodeL = null;
    ProxyNodePCL pcl = null;
    protected void setUp() throws Exception {
        pa = new TProperty();
        pb = new TProperty();
        a = new TNode(pa);
        b = new TNode(pb);
        pn = new ProxyNode(new Node[] {a,b});
        nodeL = new ProxyNodeListener();
        pcl = new ProxyNodePCL();
        pn.addNodeListener(nodeL);
        pn.addPropertyChangeListener(pcl);
    }
    
    private Node.Property findProxyProperty() {
        return pn.getPropertySets()[0].getProperties()[0];
    }
    
    public void testProxyPropertyCreated() {
        assertNotNull("Proxied properties each have one property, but couldn't find matching ProxyProperty", findProxyProperty());
    }
    
    public void testProxyPropertyReflectsValueChanges() throws Exception {
        Object o = findProxyProperty().getValue();
        String name = findProxyProperty().getName();
        pa.setValue("Foo");
        nodeL.assertNoEvent("Setting a property value should not fire to NodeListeners");
        pcl.assertFired("Property change should have been fired on " + pa.getName(), pa.getName());
    }
    
    public void testProxyPropertyHandlesNodeDeletion() {
        a.destroyMe();
        nodeL.assertNotDestroyed();
        b.destroyMe();
        nodeL.assertNodeDestroyedFired("Destroying both nodes represented by a proxy node should make it fire NodeDestroyed");
    }
    
    public void testDisplayNameNotFiredToNodeListeners() {
        System.err.println("First display name " + pn.getDisplayName());
        a.setDisplayName("New name");
        System.err.println("Second display name " + pn.getDisplayName());
        nodeL.assertFired("Setting display name on a proxied node should trigger a property change event", Node.PROP_DISPLAY_NAME);
        pcl.assertNoEvent("Setting display name should not cause an event on NodeListeners");
    }
    
    public void testCookieChangeGoesToTheCorrectListenerOnly() {
        a.fireCookies();
        nodeL.assertFired("One node firing prop cookies should cause an event to node listeners", Node.PROP_COOKIE);
        pcl.assertNoEvent("No event should have been fired to a property change listener on a cookies change");
    }
    
    
    public class ProxyNodeListener implements NodeListener {
        public void childrenAdded(NodeMemberEvent ev) {
        }
        
        public void childrenRemoved(NodeMemberEvent ev) {
        }
        
        public void childrenReordered(NodeReorderEvent ev) {
        }
        
        NodeEvent ev = null;
        public void nodeDestroyed(NodeEvent ev) {
            this.ev = ev;
        }
        
        public void assertNotDestroyed() {
            assertNull(ev);
        }
        
        public void assertNodeDestroyedFired(String msg) {
            assertNotNull(msg + " - NodeDestroyed not fired", ev);
        }
        
        private PropertyChangeEvent evt = null;
        public void propertyChange(PropertyChangeEvent evt) {
            this.evt = evt;
        }
        
        public void assertNoEvent(String msg) {
            assertNull(msg + "- event was fired to NodeListener but should not have been - " + (evt != null ? evt.getPropertyName() : ""), evt);
            assertNull(msg + "- no node event should have been generated, but one was " + ev, ev);
        }
        
        public void assertFired(String msg, String propName) {
            assertTrue(msg + "- event was not fired to NodeListener but should have been", evt != null && propName.equals(evt.getPropertyName()));
        }
    }
    
    public class ProxyNodePCL implements PropertyChangeListener {
        private PropertyChangeEvent evt = null;
        public void propertyChange(PropertyChangeEvent evt) {
            this.evt = evt;
        }
        
        public void assertNoEvent(String msg) {
            assertNull(msg + "- event was fired to NodeListener but should not have been", evt);
        }
        
        public void assertFired(String msg, String propName) {
            assertTrue(msg + "- event was not fired to PropertyChangeListener but should have been", evt != null && propName.equals(evt.getPropertyName()));
        }
    }
    
    //Node definition
    public class TNode extends AbstractNode {
        private TProperty myprop;
        //create Node
        public TNode(TProperty myprop) {
            super(Children.LEAF);
            setName("TNode");
            setDisplayName("TNode");
            this.myprop = myprop;
        }
        //clone existing Node
        public Node cloneNode() {
            return new TNode(myprop);
        }
        
        // Create a property sheet:
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();
            // Make sure there is a "Properties" set:
            Sheet.Set props = sheet.get(Sheet.PROPERTIES);
            if (props == null) {
                props = Sheet.createPropertiesSet();
                sheet.put(props);
            }
            props.put(myprop);
            myprop.setFiringNode(this);
            return sheet;
        }
        // Method firing changes
        public void fireMethod(String s, Object o1, Object o2) {
            firePropertyChange(s,o1,o2);
        }
        
        public void setDisplayName(String s) {
            String old = super.getDisplayName();
            super.setDisplayName(s);
            fireDisplayNameChange(old, s);
        }
        
        public void destroyMe() {
            fireNodeDestroyed();
        }
        
        public void fireCookies() {
            fireCookieChange();
        }
    }
    
    // Property definition
    public class TProperty extends PropertySupport {
        private Object myValue = "Value";
        // Create new Property
        public TProperty() {
            super("TProperty", String.class, "TProperty", "", true, true);
        }
        // get property value
        public Object getValue() {
            return myValue;
        }
        private TNode node = null;
        void setFiringNode(TNode n) {
            this.node = n;
        }
        
        // set property value
        public void setValue(Object value) throws IllegalArgumentException,IllegalAccessException, InvocationTargetException {
            System.err.println("TProperty setValue: " + value);
            Object oldVal = myValue;
            myValue = value;
            System.err.println("TProperty triggering node property change");
            if (node != null) {
                node.fireMethod(getName(), oldVal, myValue);
            }
        }
    }
    
}
