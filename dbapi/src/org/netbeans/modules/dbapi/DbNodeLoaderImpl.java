/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.dbapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.db.api.explorer.NodeProvider;
import org.netbeans.modules.db.explorer.DbNodeLoader;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 * Loads nodes from all registered node providers and delivers them to
 * the caller of getAllNodes().
 *  
 * @author David Van Couvering
 */
public class DbNodeLoaderImpl implements DbNodeLoader, ChangeListener {
    
            /** 
     * Not private because used in the tests.
     */
    static final String NODE_PROVIDER_PATH = "Databases/NodeProviders"; // NOI18N
    static Collection providers;
    
    final CopyOnWriteArrayList<ChangeListener> listeners = 
            new CopyOnWriteArrayList<ChangeListener>();
    
    public List<Node> getAllNodes() {
        List<Node> nodes = new ArrayList<Node>();
        
        if ( providers == null ) {
            providers = Lookups.forPath(NODE_PROVIDER_PATH).lookupAll(NodeProvider.class);    
        }
        
        for (Iterator i = providers.iterator(); i.hasNext();) {
            NodeProvider provider = (NodeProvider)i.next();
            List<Node> nodeList = provider.getNodes();
            if (nodeList != null) {
                nodes.addAll(provider.getNodes());
            }
            
            provider.addChangeListener(this);
        }
        
        return nodes;
    }

    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    public synchronized void stateChanged(ChangeEvent evt) {
        // At this point, any state change simply means that the consumer 
        // should re-call getAllNodes(), so delegate the state change up to 
        // the consumer.
        for ( ChangeListener listener : listeners ) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }
}
