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
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.netbeans.modules.websvc.wsitconf.design;

import java.util.Collection;
import org.netbeans.modules.websvc.design.javamodel.MethodModel;
import org.netbeans.modules.websvc.design.javamodel.ServiceChangeListener;
import org.netbeans.modules.websvc.wsitconf.ui.ComboConstants;
import org.netbeans.modules.websvc.wsitconf.util.Util;
import org.netbeans.modules.websvc.wsitconf.wsdlmodelext.ProfilesModelHelper;
import org.netbeans.modules.xml.wsdl.model.Binding;
import org.netbeans.modules.xml.wsdl.model.BindingOperation;
import org.netbeans.modules.xml.wsdl.model.Definitions;
import org.netbeans.modules.xml.wsdl.model.Message;
import org.netbeans.modules.xml.wsdl.model.Operation;
import org.netbeans.modules.xml.wsdl.model.PortType;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;

/**
 *
 * @author Martin Grebac
 */
public class WsitServiceChangeListener implements ServiceChangeListener {

    WSDLModel model;
    Binding b;
    
    public WsitServiceChangeListener(Binding b) {
        this.model = b.getModel();
        this.b = b;
    }

    public void propertyChanged(String propertyName, String oldValue, String newValue) {
        System.out.println("receieved propertychangeevent for propertyName="+propertyName);
    }

    public void operationAdded(MethodModel method) {
        BindingOperation bO = Util.generateOperation(b, Util.getPortType(b), method.getOperationName(), method.getImplementationClass());
        ProfilesModelHelper.setMessageLevelSecurityProfilePolicies(bO, ComboConstants.PROF_MUTUALCERT);
    }

    public void operationRemoved(MethodModel method) {
        String methodName = method.getOperationName();
        Definitions d = model.getDefinitions();
        Collection<Message> messages = d.getMessages();
        Collection<BindingOperation> bOperations = b.getBindingOperations();
        PortType portType = (PortType) d.getPortTypes().toArray()[0];
        Collection<Operation> operations = portType.getOperations();
        
        boolean isTransaction = model.isIntransaction();
        if (!isTransaction) {
            model.startTransaction();
        }
        
        try {
            for (BindingOperation bOperation : bOperations) {
                if (methodName.equals(bOperation.getName())) {
                    b.removeBindingOperation(bOperation);
                }
            }

            for (Operation o : operations) {
                if (methodName.equals(o.getName())) {
                    portType.removeOperation(o);
                }
            }

            for (Message m : messages) {
                if (methodName.equals(m.getName()) || (methodName + "Response").equals(m.getName())) {
                    d.removeMessage(m);
                }
            }

            Collection<BindingOperation> bOps = b.getBindingOperations();
            for (BindingOperation op : bOps) {
                if (op.getName().equals(method.getOperationName())) {
                    ProfilesModelHelper.setMessageLevelSecurityProfilePolicies(op, ComboConstants.PROF_MUTUALCERT);
                }
            }
        } finally {
            if (!isTransaction) {
                model.endTransaction();
            }
        }
    }

    public void operationChanged(MethodModel oldMethod,
                                 MethodModel newMethod) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
            
}
