/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.websvc.axis2.services.model.impl;

import org.netbeans.modules.websvc.axis2.services.model.Service;
import org.netbeans.modules.websvc.axis2.services.model.ServiceGroup;
import org.netbeans.modules.websvc.axis2.services.model.Services;
import org.netbeans.modules.websvc.axis2.services.model.ServicesVisitor;
import org.netbeans.modules.websvc.axis2.services.model.ServicesComponent;
import org.netbeans.modules.xml.xam.ComponentUpdater;
import org.netbeans.modules.xml.xam.ComponentUpdater.Operation;


public class SyncUpdateVisitor extends ServicesVisitor.Default implements ComponentUpdater<ServicesComponent> {
    private ServicesComponent target;
    private Operation operation;
    private int index;
    
    public SyncUpdateVisitor() {
    }
    
    public void update(ServicesComponent target, ServicesComponent child, Operation operation) {
        update(target, child, -1 , operation);
    }
    
    public void update(ServicesComponent target, ServicesComponent child, int index, Operation operation) {
        assert target != null;
        assert child != null;
        this.target = target;
        this.index = index;
        this.operation = operation;
        child.accept(this);
    }
    
    private void insert(String propertyName, ServicesComponent component) {
        ((ServicesComponentImpl)target).insertAtIndex(propertyName, component, index);
    }
    
    private void remove(String propertyName, ServicesComponent component) {
        ((ServicesComponentImpl)target).removeChild(propertyName, component);
    }
    
    public void visit(ServiceGroup serviceGroup) {
        if (target instanceof Services) {
            if (operation == Operation.ADD) {
                insert(ServiceGroup.SERVICE_GROUP_PROP, serviceGroup);
            } else {
                remove(ServiceGroup.SERVICE_GROUP_PROP, serviceGroup);
            }
        }
    }
    
    public void visit(Service service) {
        if (target instanceof Services) {
            if (operation == Operation.ADD) {
                insert(Service.SERVICE_PROP, service);
            } else {
                remove(Service.SERVICE_PROP, service);
            }
        }
    }
    
}
