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

package org.openide.nodes;

import java.util.Collections;
import org.netbeans.junit.*;

/** Tests whether notification to NodeListener is fired under Mutex.writeAccess
 *
 * @author Jaroslav Tulach
 */
public class NodeListenerTest extends NbTestCase {
    public NodeListenerTest(String name) {
        super(name);
    }

    /** Creates a node with children, attaches a listener and tests whether
     * notifications are delivered under correct lock.
     */
    public void testCorrectMutexUsage () throws Exception {
        Children.Array ch = new Children.Array ();
        AbstractNode n = new AbstractNode (ch);
        
        class L extends Object implements NodeListener, Runnable {
            private boolean run;
            
            public void childrenAdded (NodeMemberEvent ev) {
                ChildFactoryTest.assertNodeAndEvent(ev, ev.getSnapshot());
                runNows ();
            }
            public void childrenRemoved (NodeMemberEvent ev) {
                ChildFactoryTest.assertNodeAndEvent(ev, ev.getSnapshot());
                runNows ();
            }
            public void childrenReordered(NodeReorderEvent ev) {
                ChildFactoryTest.assertNodeAndEvent(ev, ev.getSnapshot());
            }
            public void nodeDestroyed (NodeEvent ev) {
                ChildFactoryTest.assertNodeAndEvent(ev, Collections.<Node>emptyList());
            }
            
            public void propertyChange (java.beans.PropertyChangeEvent ev) {
            }
            
            public void run () {
                run = true;
            }
            
            private void runNows () {
                L read = new L ();
                Children.MUTEX.postReadRequest (read);
                if (read.run) {
                    fail ("It is possible to run read access request");
                }
                
                L write = new L ();
                Children.MUTEX.postWriteRequest (write);
                if (!write.run) {
                    fail ("It is not possible to run write access request");
                }
            }
        }
        
        
        L l = new L ();
        
        n.addNodeListener (l);
        Node t = new AbstractNode (Children.LEAF);
        ch.add (new Node[] { t });
        
        ch.remove (new Node[] { t });
    }
}
