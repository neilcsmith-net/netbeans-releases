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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.j2ee.dd.impl.common.annotation;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.netbeans.modules.j2ee.dd.api.common.EjbLocalRef;
import org.netbeans.modules.j2ee.dd.api.common.EjbRef;
import org.netbeans.modules.j2ee.metadata.model.api.support.annotation.AnnotationHandler;
import org.netbeans.modules.j2ee.metadata.model.api.support.annotation.AnnotationModelHelper;
import org.netbeans.modules.j2ee.metadata.model.api.support.annotation.parser.AnnotationParser;
import org.netbeans.modules.j2ee.metadata.model.api.support.annotation.parser.ArrayValueHandler;
import org.netbeans.modules.j2ee.metadata.model.api.support.annotation.parser.ParseResult;

/**
 *
 * @author Martin Adamek
 */
public class EjbRefHelper {
    
    private static final String EJB_ANN = "javax.ejb.EJB"; // NOI18N
    private static final String EJBS_ANN = "javax.ejb.EJBs"; // NOI18N
    
    private EjbRefHelper() {
    }
    
    /**
     * Searches for EJB references and sets them into provided lists.
     * If one of the lists is null, search for particular reference type 
     * won't be performed.
     */
    public static void setEjbRefs(final AnnotationModelHelper helper, final List<EjbRef> resultEjbRefs, final List<EjbLocalRef> resultEjbLocalRefs) {
        try {
            helper.getAnnotationScanner().findAnnotations(
                EJBS_ANN, 
                EnumSet.of(ElementKind.CLASS), 
                new AnnotationHandler() {
                    public void handleAnnotation(TypeElement typeElement, Element element, AnnotationMirror annotation) {
                        parseEJBsAnnotation(helper, typeElement, resultEjbRefs, resultEjbLocalRefs);
                    }
                });
            helper.getAnnotationScanner().findAnnotations(
                EJB_ANN, 
                EnumSet.of(ElementKind.CLASS, ElementKind.METHOD, ElementKind.FIELD), 
                new AnnotationHandler() {
                    public void handleAnnotation(TypeElement typeElement, Element element, AnnotationMirror annotation) {
                        parseElement(helper, typeElement, element, resultEjbRefs, resultEjbLocalRefs);
                    }
                });
        } catch (InterruptedException ie) {
            // do nothing
        }
    }
    
    public static void setEjbRefsForClass(final AnnotationModelHelper helper, TypeElement typeElement, 
            final List<EjbRef> resultEjbRefs, final List<EjbLocalRef> resultEjbLocalRefs) {
        assert helper != null;
        assert resultEjbRefs != null;
        assert resultEjbLocalRefs != null;

        // javax.ejb.EJBs is array of javax.ejb.EJB and is applicable to class
        parseEJBsAnnotation(helper, typeElement, resultEjbRefs, resultEjbLocalRefs);
        
        // @EJB at class
        if (helper.hasAnnotation(typeElement.getAnnotationMirrors(), EJB_ANN)) {
            parseElement(helper, typeElement, typeElement, resultEjbRefs, resultEjbLocalRefs);
        }
        // @EJB at field
        for (VariableElement variableElement : ElementFilter.fieldsIn(typeElement.getEnclosedElements())) {
            if (helper.hasAnnotation(variableElement.getAnnotationMirrors(), EJB_ANN)) {
                parseElement(helper, typeElement, variableElement, resultEjbRefs, resultEjbLocalRefs);
            }
        }
        // @EJB at method
        for (ExecutableElement executableElement : ElementFilter.methodsIn(typeElement.getEnclosedElements())) {
            if (helper.hasAnnotation(executableElement.getAnnotationMirrors(), EJB_ANN)) {
                parseElement(helper, typeElement, executableElement, resultEjbRefs, resultEjbLocalRefs);
            }
        }
        
    }
    
    private static void parseEJBsAnnotation(final AnnotationModelHelper helper, TypeElement typeElement,
            final List<EjbRef> resultEjbRefs, final List<EjbLocalRef> resultEjbLocalRefs) {
        Map<String, ? extends AnnotationMirror> annByType = helper.getAnnotationsByType(typeElement.getAnnotationMirrors());
        AnnotationMirror ejbsAnnotation = annByType.get(EJBS_ANN); // NOI18N
        AnnotationParser parser = AnnotationParser.create(helper);
        parser.expectAnnotationArray("value", helper.resolveType(EJB_ANN), new ArrayValueHandler() { // NOI18N
            public Object handleArray(List<AnnotationValue> arrayMembers) {
                for (AnnotationValue arrayMember : arrayMembers) {
                    Object arrayMemberValue = arrayMember.getValue();
                    if (arrayMemberValue instanceof AnnotationMirror) {
                        parseAnnotation(helper, (AnnotationMirror) arrayMemberValue, resultEjbRefs, resultEjbLocalRefs);
                    }
                }
                return null;
            }
        }, null);
        parser.parse(ejbsAnnotation);
    }
    
    /**
     * Parses element
     */
    private static void parseElement(AnnotationModelHelper helper, TypeElement ownerClass, Element element, List<EjbRef> resultEjbRefs, List<EjbLocalRef> resultEjbLocalRefs) {
        
        String name = null;
        String beanInterface = null;
        String beanName = null;
        String mappedName = null;
        String description = null;
        
        TypeElement interfaceTypeElement = null;
        
        Map<String, ? extends AnnotationMirror> annByType = helper.getAnnotationsByType(element.getAnnotationMirrors());
        
        if (ElementKind.CLASS == element.getKind()) {
            
            AnnotationParser parser = AnnotationParser.create(helper);
            parser.expectString("name", null); // NOI18N
            parser.expectClass("beanInterface", null); // NOI18N
            parser.expectString("beanName", null); // NOI18N
            parser.expectString("mappedName", null); // NOI18N
            parser.expectString("description", null); // NOI18N
            ParseResult parseResult = parser.parse(annByType.get(EJB_ANN));
            
            name = parseResult.get("name", String.class); // NOI18N
            beanInterface = parseResult.get("beanInterface", String.class); // NOI18N
            beanName = parseResult.get("beanName", String.class); // NOI18N
            mappedName = parseResult.get("mappedName", String.class); // NOI18N
            description = parseResult.get("description", String.class); // NOI18N
            
            if (beanInterface != null) {
                interfaceTypeElement = helper.getCompilationController().getElements().getTypeElement(beanInterface);
            }
        } else if (ElementKind.FIELD == element.getKind() || ElementKind.METHOD == element.getKind()) {
            
            TypeMirror fieldTypeMirror = element.asType();
            AnnotationParser parser = AnnotationParser.create(helper);
            
            if (ElementKind.METHOD == element.getKind()) {
                String fieldName = element.getSimpleName().toString();
                fieldName = Character.toLowerCase(fieldName.charAt(3)) + fieldName.substring(4);
                parser.expectString("name", parser.defaultValue("java:comp/env/" + ownerClass.getQualifiedName() + '/' + fieldName)); // NOI18N
                if (!element.getSimpleName().toString().startsWith("set")) { // NOI18N
                    return;
                }
                ExecutableElement method = (ExecutableElement) element;
                List<? extends VariableElement> parameters = method.getParameters();
                if (parameters.size() != 1) {
                    return;
                }
                fieldTypeMirror = parameters.get(0).asType();
            } else {
                parser.expectString("name", parser.defaultValue("java:comp/env/" + ownerClass.getQualifiedName() + '/' + element.getSimpleName())); // NOI18N
            }
            
            if (TypeKind.DECLARED == fieldTypeMirror.getKind()) {
                DeclaredType fieldDeclaredType = (DeclaredType) fieldTypeMirror;
                Element fieldTypeElement = fieldDeclaredType.asElement();
                if (ElementKind.INTERFACE == fieldTypeElement.getKind()) {
                    interfaceTypeElement = (TypeElement) fieldTypeElement;
                    beanInterface = interfaceTypeElement.getQualifiedName().toString();
                }
            }
            
            ParseResult parseResult = parser.parse(annByType.get(EJB_ANN));
            name = parseResult.get("name", String.class); // NOI18N
        } else {
            return;
        }
        
        if (interfaceTypeElement != null) {
            createReference(helper, interfaceTypeElement, resultEjbRefs, resultEjbLocalRefs, name, beanInterface, beanName, mappedName, description);
        }
    }
    
    /**
     * Creates local or remote reference from annotation
     */
    private static void parseAnnotation(AnnotationModelHelper helper, AnnotationMirror annotationMirror, List<EjbRef> resultEjbRefs, List<EjbLocalRef> resultEjbLocalRefs) {
        
        String name;
        String beanInterface;
        String beanName;
        String mappedName;
        String description;
        
        TypeElement interfaceTypeElement;
        
        AnnotationParser parser = AnnotationParser.create(helper);
        parser.expectString("name", null); // NOI18N
        parser.expectClass("beanInterface", null); // NOI18N
        parser.expectString("beanName", null); // NOI18N
        parser.expectString("mappedName", null); // NOI18N
        parser.expectString("description", null); // NOI18N
        ParseResult parseResult = parser.parse(annotationMirror);
        
        name = parseResult.get("name", String.class); // NOI18N
        beanInterface = parseResult.get("beanInterface", String.class); // NOI18N
        beanName = parseResult.get("beanName", String.class); // NOI18N
        mappedName = parseResult.get("mappedName", String.class); // NOI18N
        description = parseResult.get("description", String.class); // NOI18N
        
        interfaceTypeElement = helper.getCompilationController().getElements().getTypeElement(beanInterface);
        
        createReference(helper, interfaceTypeElement, resultEjbRefs, resultEjbLocalRefs, name, beanInterface, beanName, mappedName, description);
    }
    
    private static void createReference(AnnotationModelHelper helper, TypeElement interfaceTypeElement, List<EjbRef> resultEjbRefs, List<EjbLocalRef> resultEjbLocalRefs, 
            String name, String beanInterface, String beanName, String mappedName, String description) {
        
        // TODO: implement good-enough algorithm to recognize if referenced interface is local or remote
        // this one just checks if there is @Remote annotation; if not, it is local
        boolean isLocal = true;
        Map<String, ? extends AnnotationMirror> memberAnnByType = helper.getAnnotationsByType(interfaceTypeElement.getAnnotationMirrors());
        if (memberAnnByType.get("javax.ejb.Remote") != null) { // NOI18N
            isLocal = false;
        }
        
        if (resultEjbLocalRefs != null && isLocal) {
            resultEjbLocalRefs.add(new EjbLocalRefImpl(name, beanInterface, beanName, mappedName, description));
        } else if (resultEjbRefs != null && !isLocal) {
            resultEjbRefs.add(new EjbRefImpl(name, beanInterface, beanName, mappedName, description));
        }
    }
}
