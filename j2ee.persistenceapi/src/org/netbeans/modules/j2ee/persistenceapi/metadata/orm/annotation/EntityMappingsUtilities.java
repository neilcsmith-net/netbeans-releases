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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.persistenceapi.metadata.orm.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.netbeans.modules.j2ee.metadata.model.api.support.annotation.AnnotationModelHelper;
import org.netbeans.modules.j2ee.metadata.model.api.support.annotation.parser.AnnotationParser;
import org.netbeans.modules.j2ee.metadata.model.api.support.annotation.parser.ArrayValueHandler;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.JoinColumn;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.PrimaryKeyJoinColumn;

/**
 *
 * @author Andrei Badea
 */
public class EntityMappingsUtilities {

    public static boolean isTransient(Map<String, ? extends AnnotationMirror> annByType, Set<Modifier> modifiers) {
        return annByType.containsKey("javax.persistence.Transient") || modifiers.contains(Modifier.TRANSIENT); // NOI18N
    }

    public static boolean hasFieldAccess(AnnotationModelHelper helper, List<? extends Element> elements) {
        for (Element element : ElementFilter.methodsIn(elements)) {
            for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
                String annTypeName = helper.getAnnotationTypeName(annotation.getAnnotationType());
                if (annTypeName.startsWith("javax.persistence.")) { // NOI18N
                    return false;
                }
            }
        }
        // if we got here, no methods were annotated with a JPA annotations
        // then either fields are annotated, or there are no annotations in the class
        // (in which case the default -- field access -- applies)
        return true;
    }

    public static String getterNameToPropertyName(String getterName) {
        // a getter name starts with "get" or "is" and
        // is longer than 3 or 2 characters, respectively
        // i.e. "get()" and "is()" are not a property getters
        if (getterName.length() > 3 && getterName.startsWith("get")) { // NOI18N
            return toLowerCaseFirst(getterName.substring(3));
        }
        if (getterName.length() > 2 && getterName.startsWith("is")) { // NOI18N
            return toLowerCaseFirst(getterName.substring(2));
        }
        return null;
    }

    public static String toUpperCase(String value) {
        // XXX locale
        return value.toUpperCase();
    }

    public static String getElementTypeName(Element element) {
        TypeMirror elementType = element.asType();
        if (TypeKind.DECLARED.equals(elementType.getKind())) {
            return ((TypeElement)((DeclaredType)elementType).asElement()).getQualifiedName().toString(); // NOI18N
        }
        return void.class.getName();
    }

    public static String getCollectionArgumentTypeName(AnnotationModelHelper helper, Element element) {
        TypeMirror elementType = element.asType();
        if (EntityMappingsUtilities.isCollectionType(helper, elementType)) {
            TypeElement argTypeElement = EntityMappingsUtilities.getFirstTypeArgument(elementType);
            if (argTypeElement != null) {
                return argTypeElement.getQualifiedName().toString(); // NOI18N
            }
        }
        return void.class.getName();
    }

    public static List<JoinColumn> getJoinColumns(final AnnotationModelHelper helper, Map<String, ? extends AnnotationMirror> annByType) {
        final List<JoinColumn> result = new ArrayList<JoinColumn>();
        AnnotationMirror joinColumnAnn = annByType.get("javax.persistence.JoinColumn"); // NOI18N
        if (joinColumnAnn != null) {
            result.add(new JoinColumnImpl(helper, joinColumnAnn));
        } else {
            AnnotationMirror joinColumnsAnnotation = annByType.get("javax.persistence.JoinColumns"); // NOI18N
            if (joinColumnsAnnotation != null) {
                AnnotationParser jcParser = AnnotationParser.create(helper);
                jcParser.expectAnnotationArray("value", helper.resolveType("javax.persistence.JoinColumn"), new ArrayValueHandler() { // NOI18N
                    public Object handleArray(List<AnnotationValue> arrayMembers) {
                        for (AnnotationValue arrayMember : arrayMembers) {
                            AnnotationMirror joinColumnAnnotation = (AnnotationMirror)arrayMember.getValue();
                            result.add(new JoinColumnImpl(helper, joinColumnAnnotation));
                        }
                        return null;
                    }
                }, null);
                jcParser.parse(joinColumnsAnnotation);
            }
        }
        return result;
    }

    public static List<PrimaryKeyJoinColumn> getPrimaryKeyJoinColumns(final AnnotationModelHelper helper, Map<String, ? extends AnnotationMirror> annByType) {
        final List<PrimaryKeyJoinColumn> result = new ArrayList<PrimaryKeyJoinColumn>();
        AnnotationMirror pkJoinColumnAnn = annByType.get("javax.persistence.PrimaryKeyJoinColumn"); // NOI18N
        if (pkJoinColumnAnn != null) {
            result.add(new PrimaryKeyJoinColumnImpl(helper, pkJoinColumnAnn));
        } else {
            AnnotationMirror pkJoinColumnsAnnotation = annByType.get("javax.persistence.PrimaryKeyJoinColumns"); // NOI18N
            if (pkJoinColumnsAnnotation != null) {
                AnnotationParser pkjcParser = AnnotationParser.create(helper);
                pkjcParser.expectAnnotationArray("value", helper.resolveType("javax.persistence.PrimaryKeyJoinColumn"), new ArrayValueHandler() { // NOI18N
                    public Object handleArray(List<AnnotationValue> arrayMembers) {
                        for (AnnotationValue arrayMember : arrayMembers) {
                            AnnotationMirror joinColumnAnnotation = (AnnotationMirror)arrayMember.getValue();
                            result.add(new PrimaryKeyJoinColumnImpl(helper, joinColumnAnnotation));
                        }
                        return null;
                    }
                }, null);
                pkjcParser.parse(pkJoinColumnsAnnotation);
            }
        }
        return result;
    }
    
    public static String getTemporalType(AnnotationModelHelper helper, AnnotationMirror temporalAnnotation) {
        AnnotationParser parser = AnnotationParser.create(helper);
        parser.expectEnumConstant("value", helper.resolveType("javax.persistence.TemporalType"), null); // NOI18N
        return parser.parse(temporalAnnotation).get("value", String.class);
        
    }

    // not private because of unit tests
    static TypeElement getFirstTypeArgument(TypeMirror type) {
        if (TypeKind.DECLARED != type.getKind()) {
            return null;
        }
        List<? extends TypeMirror> typeArgs = ((DeclaredType)type).getTypeArguments();
        if (typeArgs.size() != 1) {
            return null;
        }
        TypeMirror typeArg = typeArgs.iterator().next();
        if (TypeKind.DECLARED != typeArg.getKind()) {
            return null;
        }
        Element typeArgElement = ((DeclaredType)typeArg).asElement();
        if (ElementKind.CLASS != typeArgElement.getKind()) {
            return null;
        }
        return (TypeElement)typeArgElement;
    }

    // not private because of unit tests
    static boolean isCollectionType(AnnotationModelHelper helper, TypeMirror type) {
        return helper.isSameRawType(type, "java.util.Collection") ||
               helper.isSameRawType(type, "java.util.Set") ||
               helper.isSameRawType(type, "java.util.List") ||
               helper.isSameRawType(type, "java.util.Map");
    }

    private static String toLowerCaseFirst(String value) {
        if (value.length() > 0) {
            // XXX incorrect wrt surrogate pairs
            char[] characters = value.toCharArray();
            // XXX locale
            characters[0] = Character.toLowerCase(characters[0]);
            return new String(characters);
        }
        return value;
    }
}
