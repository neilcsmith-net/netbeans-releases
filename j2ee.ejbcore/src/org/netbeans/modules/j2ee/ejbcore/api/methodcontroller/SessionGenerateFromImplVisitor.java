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
package org.netbeans.modules.j2ee.ejbcore.api.methodcontroller;

import java.util.Collections;
import javax.lang.model.element.Modifier;
import org.netbeans.modules.j2ee.common.method.MethodModel;
import org.netbeans.modules.j2ee.ejbcore.api.methodcontroller.MethodType.BusinessMethodType;
import org.netbeans.modules.j2ee.ejbcore.api.methodcontroller.MethodType.CreateMethodType;
import org.netbeans.modules.j2ee.ejbcore.api.methodcontroller.MethodType.FinderMethodType;
import org.netbeans.modules.j2ee.ejbcore.api.methodcontroller.MethodType.HomeMethodType;

/**
 *
 * @author Chris Webster
 * @author Martin Adamek
 */
final class SessionGenerateFromImplVisitor implements MethodType.MethodTypeVisitor, AbstractMethodController.GenerateFromImpl {

    private MethodModel intfMethod;
    private String destination;
    private String home;
    private String component;

    public void getInterfaceMethodFromImpl(MethodType methodType, String home, String component) {
        this.home = home;
        this.component = component;
        methodType.accept(this);
    }
    
    public MethodModel getInterfaceMethod() {
        return intfMethod;
    }
    
    public String getDestinationInterface() {
        return destination;
    }
    
    public void visit(BusinessMethodType bmt) {
        intfMethod = bmt.getMethodElement();
        destination = component;
    }
       
    public void visit(CreateMethodType cmt) {
        intfMethod = cmt.getMethodElement();
        String origName = intfMethod.getName();
        String newName = chopAndUpper(origName,"ejb"); //NOI18N
        intfMethod = MethodModel.create(
                newName, 
                home,
                intfMethod.getBody(),
                intfMethod.getParameters(),
                intfMethod.getExceptions(),
                Collections.singleton(Modifier.PUBLIC)
                );
        destination = home;
    }
    
    public void visit(HomeMethodType hmt) {
        assert false: "session beans do not have home methods";
    }
    
    public void visit(FinderMethodType fmt) {
        assert false: "session beans do not have finder methods";
    }
    
    private String chopAndUpper(String fullName, String chop) {
         StringBuffer buffer = new StringBuffer(fullName);
         buffer.delete(0, chop.length());
         buffer.setCharAt(0, Character.toLowerCase(buffer.charAt(0)));
         return buffer.toString();
    }
    
}
