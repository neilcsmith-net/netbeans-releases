/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript2.editor.model.impl;


import com.oracle.nashorn.ir.*;
import com.oracle.nashorn.parser.TokenType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.javascript2.editor.model.Identifier;
import org.netbeans.modules.javascript2.editor.model.JsElement;
import org.netbeans.modules.javascript2.editor.model.JsObject;
import org.netbeans.modules.javascript2.editor.parser.JsParserResult;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Petr Pisl
 */
public class ModelVisitor extends PathNodeVisitor {

    private final ModelBuilder modelBuilder;
    /**
     * Keeps the name of the visited properties
     */
    private final List<List<FunctionNode>> functionStack;

    public ModelVisitor(JsParserResult parserResult) {
        FileObject fileObject = parserResult.getSnapshot().getSource().getFileObject();
        this.modelBuilder = new ModelBuilder(JsObjectImpl.createGlobal(fileObject));
        this.functionStack = new ArrayList<List<FunctionNode>>();
    }

    @Override
    public Node visit(BinaryNode binaryNode, boolean onset) {
        if (onset) {            
            if (binaryNode.tokenType() == TokenType.ASSIGN 
                    && !(binaryNode.rhs() instanceof ReferenceNode || binaryNode.rhs() instanceof ObjectNode)
                    && (binaryNode.lhs() instanceof AccessNode || binaryNode.lhs() instanceof IdentNode)) {
                // TODO probably not only assign                
                JsObjectImpl parent = modelBuilder.getCurrentObject();
                if (binaryNode.lhs() instanceof AccessNode) {
                    List<Identifier> name = getName(binaryNode);
                    AccessNode aNode = (AccessNode)binaryNode.lhs();
                    if (aNode.getBase() instanceof IdentNode && "this".equals(((IdentNode)aNode.getBase()).getName())) { //NOI18N
                        // a usage of field
                        String fieldName = aNode.getProperty().getName();
                        if(parent.getParent() instanceof JsFunctionImpl) {
                            parent = (JsObjectImpl)parent.getParent();
                        }
                        if(parent.getPropery(fieldName) == null) {
                            Identifier identifier = ModelElementFactory.create((IdentNode)aNode.getProperty());
                            parent.addProperty(fieldName, new JsObjectImpl(parent, identifier, identifier.getOffsetRange() ));
                        }
                    } else {
                        // probably a property of an object
                        List<Identifier> fqName = getName(aNode);
                        ModelUtils.getJsObject(modelBuilder.getGlobal(), fqName);
                    }
                    
                } else {
                    IdentNode ident = (IdentNode)binaryNode.lhs();
                    final Identifier name = new IdentifierImpl(ident.getName(), new OffsetRange(ident.getStart(), ident.getFinish()));
                    final String newVarName = name.getName();
                    boolean hasParent = parent.getPropery(newVarName) != null ;
                    boolean hasGrandParent = parent.getJSKind() == JsElement.Kind.METHOD && parent.getParent().getPropery(newVarName) != null;
                    if (!hasParent && !hasGrandParent && modelBuilder.getGlobal().getPropery(newVarName) == null) {
                        // variable was not found -> it's not declared and it has to be
                        // added to the global scope (filescope) as implicit variable
                        JsObjectImpl variable = new JsObjectImpl(modelBuilder.getGlobal(), name, name.getOffsetRange());
                        variable.setDeclared(false);
                        modelBuilder.getGlobal().addProperty(newVarName, variable);
                    }
                }
            }
        }
        return super.visit(binaryNode, onset);
    }

    @Override
    public Node visit(FunctionNode functionNode, boolean onset) {
        System.out.println("FunctionNode: " + functionNode.getName() + " , path: " + getPath().size() + " , onset: " + onset);

        if (onset) {
            JsObjectImpl inObject = modelBuilder.getGlobal();
            addToPath(functionNode);
            List<FunctionNode> functions = new ArrayList<FunctionNode>(functionNode.getFunctions().size());
            for (FunctionNode fn : functionNode.getFunctions()) {
                functions.add(fn);
            }

            for (FunctionNode fn : functions) {
                if (fn.getIdent().getStart() < fn.getIdent().getFinish()) {
                    fn.accept(this);
                }
            }

            List<Identifier> name = null;
            boolean isPrivate = false;
            int pathSize = getPath().size();
            if (pathSize > 1 && getPath().get(pathSize - 2) instanceof ReferenceNode) {
                List<FunctionNode> siblings = functionStack.get(functionStack.size() - 1);
                siblings.remove(functionNode);

                if (pathSize > 3) {
                    Node node = getPath().get(pathSize - 3);
                    if (node instanceof PropertyNode) {
                        name = getName((PropertyNode)node);
                    } else if (node instanceof BinaryNode) {
                        name = getName((BinaryNode)node);
                    } else if (node instanceof VarNode) {
                       name = getName((VarNode)node);
                        // private method
                        // It can be only if it's in a function
                        isPrivate = functionStack.size() > 1;
                    }
                }
            }

            if (name == null) {
                name = new ArrayList<Identifier>(1);
                name.add(new IdentifierImpl(functionNode.getIdent().getName(), 
                        new OffsetRange(functionNode.getIdent().getStart(), functionNode.getIdent().getFinish())));
            }
            functionStack.add(functions);

            // todo parameters;
            if (functionNode.getKind() != FunctionNode.Kind.SCRIPT) {
                JsObjectImpl scope = modelBuilder.getCurrentObject();
                JsFunctionImpl fncScope = ModelElementFactory.create(functionNode, name, modelBuilder);
                if (isPrivate) {
//                    Set<Modifier> modifier = fncScope.getModifiers();
//                    modifier.clear();
//                    modifier.add(Modifier.PRIVATE);
                }
                modelBuilder.setCurrentObject((JsObjectImpl)fncScope);
            }

            for (Node node : functionNode.getStatements()) {
                node.accept(this);
            }


            for (FunctionNode fn : functions) {
                if (fn.getIdent().getStart() >= fn.getIdent().getFinish()) {
                    System.out.println("   jeste nutno navstivit dalsi funkci:");
                    fn.accept(this);
                }
            }
            if (functionNode.getKind() != FunctionNode.Kind.SCRIPT) {
                modelBuilder.reset();
            }
            functionStack.remove(functionStack.size() - 1);
            removeFromPathTheLast();
            return null;

        }
        return super.visit(functionNode, onset);
    }

    public JsObject getGlobalObject() {
        return modelBuilder.getGlobal();
    }
    
    @Override
    public Node visit(ObjectNode objectNode, boolean onset) {
        if (onset) {
            List<Identifier> fqName = null;
            int pathSize = getPath().size();
            boolean isDeclaredInParent = false;
            Node lastVisited = getPath().get(pathSize - 1);
            if ( lastVisited instanceof VarNode) {
                fqName = getName((VarNode)lastVisited);
            } else if (lastVisited instanceof PropertyNode) {
                        fqName = getName((PropertyNode)lastVisited);
                    } else if (lastVisited instanceof BinaryNode) {
                        BinaryNode binNode = (BinaryNode)lastVisited;
                        fqName = getName(binNode);
                        if(binNode.lhs() instanceof AccessNode 
                                && ((AccessNode)binNode.lhs()).getBase() instanceof IdentNode
                                && ((IdentNode)((AccessNode)binNode.lhs()).getBase()).getName().equals("this")) {
                            isDeclaredInParent = true;
                        }
                    }
            if (fqName == null || fqName.size() == 0) {
                fqName = new ArrayList<Identifier>(1);
                fqName.add(new IdentifierImpl("UNKNOWN",   //NOI18N
                        new OffsetRange(objectNode.getStart(), objectNode.getFinish())));
            }
            JsObjectImpl scope = modelBuilder.getCurrentObject();
            
            JsObjectImpl objectScope = ModelElementFactory.create(objectNode, fqName, modelBuilder, isDeclaredInParent);

            modelBuilder.setCurrentObject(objectScope);
        } else {
            modelBuilder.reset();
        }

        return super.visit(functionNode, onset);
    }

    @Override
    public Node visit(PropertyNode propertyNode, boolean onset) {
        if (onset && propertyNode.getKey() instanceof IdentNode
                && !(propertyNode.getValue() instanceof ObjectNode)) {
            JsObjectImpl scope = modelBuilder.getCurrentObject();
            IdentNode key = (IdentNode)propertyNode.getKey();
            Identifier name = ModelElementFactory.create(key);
            scope.addProperty(name.getName(), new JsObjectImpl(scope, name, name.getOffsetRange()));
            if(propertyNode.getValue() instanceof CallNode) {
                // TODO for now, don't continue. There shoudl be handled cases liek
                // in the testFiles/model/property02.js file
                return null;
            }
        }
        return super.visit(propertyNode, onset);
    }

    @Override
    public Node visit(ReferenceNode referenceNode, boolean onset) {
        if (onset && referenceNode.getReference() instanceof FunctionNode) {
            addToPath(referenceNode);
            ((FunctionNode) referenceNode.getReference()).accept(this);
            removeFromPathTheLast();
            return null;
        }
        return super.visit(referenceNode, onset);
    }
    
    @Override
    public Node visit(VarNode varNode, boolean onset) {
        if (onset && !(varNode.getInit() instanceof ObjectNode || varNode.getInit() instanceof ReferenceNode)) {
            JsObject parent = modelBuilder.getCurrentObject();
            Identifier name = new IdentifierImpl(varNode.getName().getName(), 
                    new OffsetRange(varNode.getName().getStart(), varNode.getName().getFinish()));
            JsObjectImpl variable =  new JsObjectImpl(parent, name, name.getOffsetRange());
            variable.setDeclared(true);
            if(parent.getJSKind() != JsElement.Kind.FILE) {
                variable.getModifiers().remove(Modifier.PUBLIC);
                variable.getModifiers().add(Modifier.PRIVATE);
            }
            parent.addProperty(name.getName(), variable);
        }
        return super.visit(varNode, onset);
    }
    
//--------------------------------End of visit methods--------------------------------------
    
    private List<Identifier> getName(PropertyNode propertyNode) {
        List<Identifier> name = new ArrayList(1);
        if (propertyNode.getKey() instanceof IdentNode) {
            IdentNode ident = (IdentNode) propertyNode.getKey();
            name.add(new IdentifierImpl(ident.getName(),
                    new OffsetRange(ident.getStart(), ident.getFinish())));
        }
        return name;
    }
    
    private List<Identifier> getName(VarNode varNode) {
        List<Identifier> name = new ArrayList();
        name.add(new IdentifierImpl(varNode.getName().getName(), 
                new OffsetRange(varNode.getName().getStart(), varNode.getName().getFinish())));
        return name;
    }
    
    private List<Identifier> getName(BinaryNode binaryNode) {
        List<Identifier> name = new ArrayList();
        Node lhs = binaryNode.lhs();
        if (lhs instanceof AccessNode) {
            name = getName((AccessNode)lhs);
        } else if (lhs instanceof IdentNode) {
            IdentNode ident = (IdentNode) lhs;
            name.add(new IdentifierImpl(ident.getName(),
                        new OffsetRange(ident.getStart(), ident.getFinish())));
        }
        return name;
    }
    
    private List<Identifier> getName(AccessNode aNode) {
        List<Identifier> name = new ArrayList();
        name.add(new IdentifierImpl(aNode.getProperty().getName(),
                new OffsetRange(aNode.getProperty().getStart(), aNode.getProperty().getFinish())));
        while (aNode.getBase() instanceof AccessNode) {
            aNode = (AccessNode) aNode.getBase();
            name.add(new IdentifierImpl(aNode.getProperty().getName(),
                    new OffsetRange(aNode.getProperty().getStart(), aNode.getProperty().getFinish())));
        }
        if (name.size() > 0 && aNode.getBase() instanceof IdentNode) {
            IdentNode ident = (IdentNode) aNode.getBase();
            if (!"this".equals(ident.getName())) {
                name.add(new IdentifierImpl(ident.getName(),
                        new OffsetRange(ident.getStart(), ident.getFinish())));
            }
        }
        Collections.reverse(name);
        return name;
    }
    
//    private Variable findVarWithName(final Scope scope, final String name) {
//        Variable result = null;
//        Collection<Variable> variables = ScopeImpl.filter(scope.getElements(), new ScopeImpl.ElementFilter() {
//
//            @Override
//            public boolean isAccepted(ModelElement element) {
//                return element.getJSKind().equals(JsElement.Kind.VARIABLE)
//                        && element.getName().equals(name);
//            }
//        });
//        
//        if (!variables.isEmpty()) {
//            result = variables.iterator().next();
//        } else {
//            if (!(scope instanceof FileScope)) {
//                result = findVarWithName((Scope)scope.getInElement(), name);
//            }
//        }
//        
//        return result;
//    }
//    
//    private Field findFieldWithName(FunctionScope function, final String name) {
//        Field result = null;
//        Collection<? extends Field> fields = function.getFields();
//        result = ModelUtils.getFirst(ModelUtils.getFirst(fields, name));
//        if (result == null && function.getInElement() instanceof FunctionScope) {
//            FunctionScope parent = (FunctionScope)function.getInElement();
//            fields = parent.getFields();
//            result = ModelUtils.getFirst(ModelUtils.getFirst(fields, name));
//        }
//        return result;
//    }
    
}
