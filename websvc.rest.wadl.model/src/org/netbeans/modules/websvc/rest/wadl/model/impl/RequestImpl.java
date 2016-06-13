/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008, 2016 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2008.11.07 at 12:36:44 PM PST
//


package org.netbeans.modules.websvc.rest.wadl.model.impl;

import java.util.Collection;
import java.util.Vector;
import org.netbeans.modules.websvc.rest.wadl.model.*;
import org.netbeans.modules.websvc.rest.wadl.model.visitor.WadlVisitor;
import org.w3c.dom.Element;

public class RequestImpl extends NamedImpl implements Request {

    /** Creates a new instance of RequestImpl */
    public RequestImpl(WadlModel model, Element e) {
        super(model, e);
    }

    public RequestImpl(WadlModel model){
        this(model, createNewElement(WadlQNames.REQUEST.getQName(), model));
    }

    public Collection<Param> getParam() {
        return getChildren(Param.class);
    }
    public void addParam(Param param) {
        addAfter(PARAM_PROPERTY, param, TypeCollection.FOR_PARAM.types());
    }

    public void removeParam(Param param) {
        removeChild(PARAM_PROPERTY, param);
    }

    public Collection<Representation> getRepresentation() {
        return getChildren(Representation.class);
    }

    public void addRepresentation(Representation rep) {
        addAfter(REPRESENTATION_PROPERTY, rep, TypeCollection.FOR_REPRESENTATION.types());
    }

    public void removeRepresentation(Representation rep) {
        removeChild(REPRESENTATION_PROPERTY, rep);
    }

    public void accept(WadlVisitor visitor) {
        visitor.visit(this);
    }
    
    public ParamStyle[] getValidParamStyles() {
        Vector<ParamStyle> v = new Vector<ParamStyle>();
        for(ParamStyle s:VALID_PARAM_STYLES) {
            v.add(s);
        }
        return (ParamStyle[]) v.toArray(new ParamStyle[0]);
    }

    public String[] getValidParamStyles(boolean toUpper) {
        Vector<String> v = new Vector<String>();
        for(ParamStyle s:VALID_PARAM_STYLES) {
            v.add(s.value());
        }
        return (String[]) v.toArray(new String[0]);
    }
}
