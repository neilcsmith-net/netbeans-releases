/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */

package org.netbeans.modules.groovy.editor.api;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.DynamicVariable;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.groovy.editor.api.AstPath;

/**
 *
 * @author Martin Janicek
 */
public final class ElementUtils {

    private ElementUtils() {
    }

    public static ElementKind getKind(AstPath path, BaseDocument doc, int caret) {
        ASTNode node = path.leaf();
        ASTNode leafParent = path.leafParent();

        if ((node instanceof ClassNode) ||
            (node instanceof ClassExpression) ||
            FindTypeUtils.isCaretOnClassNode(path, doc, caret)) {
            return ElementKind.CLASS;
        } else if ((node instanceof MethodNode)) {
            if ("<init>".equals(((MethodNode) node).getName())) { // NOI18N
                return ElementKind.CONSTRUCTOR;
            }
            return ElementKind.METHOD;
        } else if ((node instanceof ConstantExpression) && (leafParent instanceof MethodCallExpression)) {
            return ElementKind.METHOD;
        } else if (node instanceof ConstructorCallExpression) {
            return ElementKind.CONSTRUCTOR;
        } else if (node instanceof FieldNode) {
            return ElementKind.FIELD;
        } else if (node instanceof PropertyNode) {
            return ElementKind.PROPERTY;
        } else if (node instanceof VariableExpression) {
            Variable variable = ((VariableExpression) node).getAccessedVariable();
            if (variable instanceof DynamicVariable) {
                // Not sure now if this is 100% correct, but if we have VariableExpression
                // like "Book^mark.get()" the accessed variable Bookmark (which is the type
                // name) is marked as DynamicVariable and in that case we want to return
                // different ElementKind in oposite to usage of 'normal' variables
                return ElementKind.CLASS;
            }
            return ElementKind.VARIABLE;
        } else if (node instanceof DeclarationExpression) {
            return ElementKind.VARIABLE;
        }
        return ElementKind.OTHER;
    }

    public static String getTypeName(ASTNode node) {
        ClassNode type = getType(node);
        return normalizeTypeName(type.getName(), type);
    }

    public static String getTypeNameWithoutPackage(ASTNode node) {
        ClassNode type = getType(node);
        return normalizeTypeName(type.getNameWithoutPackage(), type);
    }

    /**
     * Returns type for the given ASTNode. For example if FieldNode is passed
     * as a parameter, it returns type of the given field etc. If the Method call
     * is passed as a parameter, the method tried to interfere proper type and return it
     *
     * @param node where we want to know declared type
     * @return type of the given node
     * @throws IllegalStateException if an implementation is missing for the given ASTNode type
     */
    public static ClassNode getType(ASTNode node) {
        if (node instanceof ClassNode) {
            return ((ClassNode) node);
        } else if (node instanceof FieldNode) {
            return ((FieldNode) node).getType();
        } else if (node instanceof PropertyNode) {
            return ((PropertyNode) node).getType();
        } else if (node instanceof MethodNode) {
            return ((MethodNode) node).getReturnType();
        } else if (node instanceof Parameter) {
           return ((Parameter) node).getType();
        } else if (node instanceof ForStatement) {
            return ((ForStatement) node).getVariableType();
        } else if (node instanceof ImportNode) {
            return ((ImportNode) node).getType();
        } else if (node instanceof ClassExpression) {
            return ((ClassExpression) node).getType();
        } else if (node instanceof VariableExpression) {
            return ((VariableExpression) node).getType();
        } else if (node instanceof DeclarationExpression) {
            DeclarationExpression declaration = ((DeclarationExpression) node);
            if (declaration.isMultipleAssignmentDeclaration()) {
                return declaration.getTupleExpression().getType();
            } else {
                return declaration.getVariableExpression().getType();
            }
        } else if (node instanceof ConstructorCallExpression) {
            return ((ConstructorCallExpression) node).getType();
        } else if (node instanceof ArrayExpression) {
            return ((ArrayExpression) node).getElementType();
        }
        throw new IllegalStateException("Not implemented yet - GroovyRefactoringElement.getType() needs to be improve!"); // NOI18N
    }

    public static String getNameWithoutPackage(ASTNode node) {
        String name = null;
        if (node instanceof ClassNode) {
            name = ((ClassNode) node).getNameWithoutPackage();
        } else if (node instanceof MethodNode) {
            name = ((MethodNode) node).getName();
            if ("<init>".equals(name)) { // NOI18N
                name = getDeclaringClassNameWithoutPackage(node);
            }
        } else if (node instanceof FieldNode) {
            name = ((FieldNode) node).getName();
        } else if (node instanceof PropertyNode) {
            name = ((PropertyNode) node).getName();
        } else if (node instanceof Parameter) {
            name = ((Parameter) node).getName();
        } else if (node instanceof ForStatement) {
            name = ((ForStatement) node).getVariableType().getNameWithoutPackage();
        } else if (node instanceof ImportNode) {
            name = ((ImportNode) node).getClassName();
        } else if (node instanceof ClassExpression) {
            name = ((ClassExpression) node).getType().getNameWithoutPackage();
        } else if (node instanceof VariableExpression) {
            name = ((VariableExpression) node).getName();
        } else if (node instanceof DeclarationExpression) {
            DeclarationExpression declaration = ((DeclarationExpression) node);
            if (declaration.isMultipleAssignmentDeclaration()) {
                name = declaration.getTupleExpression().getType().getNameWithoutPackage();
            } else {
                name = declaration.getVariableExpression().getType().getNameWithoutPackage();
            }
        } else if (node instanceof ConstantExpression) {
            name = ((ConstantExpression) node).getText();
        } else if (node instanceof ConstructorCallExpression) {
            name = ((ConstructorCallExpression) node).getType().getNameWithoutPackage();
        } else if (node instanceof ArrayExpression) {
            name = ((ArrayExpression) node).getElementType().getNameWithoutPackage();
        }


        if (name != null) {
            return normalizeTypeName(name, null);
        }
        throw new IllegalStateException("Not implemented yet - GroovyRefactoringElement.getName() needs to be improve for type: " + node.getClass().getSimpleName()); // NOI18N
    }

    public static ClassNode getDeclaringClass(ASTNode node) {
        if (node instanceof ClassNode) {
            return (ClassNode) node;
        } else if (node instanceof MethodNode) {
            return ((MethodNode) node).getDeclaringClass();
        } else if (node instanceof FieldNode) {
            return ((FieldNode) node).getDeclaringClass();
        } else if (node instanceof PropertyNode) {
            return ((PropertyNode) node).getDeclaringClass();
        } else if (node instanceof Parameter) {
            return ((Parameter) node).getDeclaringClass();
        } else if (node instanceof ForStatement) {
            return ((ForStatement) node).getVariableType().getDeclaringClass();
        } else if (node instanceof ImportNode) {
            return ((ImportNode) node).getDeclaringClass();
        } else if (node instanceof ClassExpression) {
            return ((ClassExpression) node).getType().getDeclaringClass();
        } else if (node instanceof VariableExpression) {
            return ((VariableExpression) node).getDeclaringClass();
        } else if (node instanceof DeclarationExpression) {
            DeclarationExpression declaration = ((DeclarationExpression) node);
            if (declaration.isMultipleAssignmentDeclaration()) {
                return declaration.getTupleExpression().getDeclaringClass();
            } else {
                return declaration.getVariableExpression().getDeclaringClass();
            }
        } else if (node instanceof ConstantExpression) {
            return ((ConstantExpression) node).getDeclaringClass();
        } else if (node instanceof ConstructorCallExpression) {
            return ((ConstructorCallExpression) node).getType();
        } else if (node instanceof ArrayExpression) {
            return ((ArrayExpression) node).getDeclaringClass();
        }

        throw new IllegalStateException("Not implemented yet - GroovyRefactoringElement.getDeclaringClass() ..looks like the type: " + node.getClass().getName() + " isn't handled at the moment!"); // NOI18N
    }

    public static String getDeclaringClassName(ASTNode node) {
        ClassNode declaringClass = getDeclaringClass(node);
        if (declaringClass != null) {
            return declaringClass.getName();
        }
        return "Dynamic type!"; // NOI18N
    }

    public static String getDeclaringClassNameWithoutPackage(ASTNode node) {
        ClassNode declaringClass = getDeclaringClass(node);
        if (declaringClass != null) {
            return declaringClass.getNameWithoutPackage();
        }
        return "Dynamic type!"; // NOI18N
    }

    public static String normalizeTypeName(String typeName, ClassNode type) {
        // This will happened with all primitive type arrays, e.g. 'double [] x'
        if (typeName.startsWith("[") && type != null) { // NOI18N
            typeName = type.getComponentType().getNameWithoutPackage();
        }

        // This will happened with all arrays except primitive type arrays
        if (typeName.endsWith("[]")) { // NOI18N
            typeName = typeName.substring(0, typeName.length() - 2);
        }
        return typeName;
    }
}
