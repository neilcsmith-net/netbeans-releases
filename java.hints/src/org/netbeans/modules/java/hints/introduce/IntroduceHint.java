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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2010 Sun
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
package org.netbeans.modules.java.hints.introduce;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.swing.JButton;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.GeneratorUtilities;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.TypeMirrorHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.java.source.support.CaretAwareJavaSourceTaskFactory;
import org.netbeans.api.java.source.support.SelectionAwareJavaSourceTaskFactory;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.java.editor.codegen.GeneratorUtils;
import org.netbeans.modules.java.hints.errors.Utilities;
import org.netbeans.modules.java.hints.introduce.CopyFinder.MethodDuplicateDescription;
import org.netbeans.spi.editor.highlighting.HighlightsLayer;
import org.netbeans.spi.editor.highlighting.HighlightsLayerFactory;
import org.netbeans.spi.editor.highlighting.ZOrder;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.Union2;

/**
 *
 * @author Jan Lahoda
 */
public class IntroduceHint implements CancellableTask<CompilationInfo> {

    private AtomicBoolean cancel = new AtomicBoolean();

    public IntroduceHint() {
    }

    private static final Set<TypeKind> NOT_ACCEPTED_TYPES = EnumSet.of(TypeKind.ERROR, TypeKind.NONE, TypeKind.OTHER, TypeKind.VOID, TypeKind.EXECUTABLE);
    private static final Set<JavaTokenId> WHITESPACES = EnumSet.of(JavaTokenId.WHITESPACE, JavaTokenId.BLOCK_COMMENT, JavaTokenId.LINE_COMMENT, JavaTokenId.JAVADOC_COMMENT);

    static int[] ignoreWhitespaces(CompilationInfo ci, int start, int end) {
        TokenSequence<JavaTokenId> ts = ci.getTokenHierarchy().tokenSequence(JavaTokenId.language());

        if (ts == null) {
            return new int[] {start, end};
        }

        ts.move(start);

        if (ts.moveNext()) {
            boolean wasMoveNext = true;

            while (WHITESPACES.contains(ts.token().id()) && (wasMoveNext = ts.moveNext()))
                ;

            if (wasMoveNext && ts.offset() > start)
                start = ts.offset();
        }

        ts.move(end);

        while (ts.movePrevious() && WHITESPACES.contains(ts.token().id()) && ts.offset() < end)
            end = ts.offset();

        return new int[] {start, end};
    }

    static TreePath validateSelection(CompilationInfo ci, int start, int end) {
        return validateSelection(ci, start, end, NOT_ACCEPTED_TYPES);
    }

    public static TreePath validateSelection(CompilationInfo ci, int start, int end, Set<TypeKind> ignoredTypes) {
        TreePath tp = ci.getTreeUtilities().pathFor((start + end) / 2 + 1);

        for ( ; tp != null; tp = tp.getParentPath()) {
            Tree leaf = tp.getLeaf();

            if (   !ExpressionTree.class.isAssignableFrom(leaf.getKind().asInterface())
                && (leaf.getKind() != Kind.VARIABLE || ((VariableTree) leaf).getInitializer() == null))
               continue;

            long treeStart = ci.getTrees().getSourcePositions().getStartPosition(ci.getCompilationUnit(), leaf);
            long treeEnd   = ci.getTrees().getSourcePositions().getEndPosition(ci.getCompilationUnit(), leaf);

            if (treeStart != start || treeEnd != end) {
                continue;
            }

            TypeMirror type = ci.getTrees().getTypeMirror(tp);

            if (type != null && type.getKind() == TypeKind.ERROR) {
                type = ci.getTrees().getOriginalType((ErrorType) type);
            }

            if (type == null || ignoredTypes.contains(type.getKind()))
                continue;

            if(tp.getLeaf().getKind() == Kind.ASSIGNMENT)
                continue;

            if (tp.getLeaf().getKind() == Kind.ANNOTATION)
                continue;

            if (!isInsideClass(tp))
                return null;

            TreePath candidate = tp;

            tp = tp.getParentPath();

            while (tp != null) {
                switch (tp.getLeaf().getKind()) {
                    case VARIABLE:
                        VariableTree vt = (VariableTree) tp.getLeaf();
                        if (vt.getInitializer() == leaf) {
                            return candidate;
                        } else {
                            return null;
                        }
                    case NEW_CLASS:
                        NewClassTree nct = (NewClassTree) tp.getLeaf();
                        
                        if (nct.getIdentifier().equals(candidate.getLeaf())) { //avoid disabling hint ie inside of anonymous class higher in treepath
                            for (Tree p : nct.getArguments()) {
                                if (p == leaf) {
                                    return candidate;
                                }
                            }

                            return null;
                        }
                }

                leaf = tp.getLeaf();
                tp = tp.getParentPath();
            }

            return candidate;
        }

        return null;
    }

    public static TreePathHandle validateSelectionForIntroduceMethod(CompilationInfo ci, int start, int end, int[] statementsSpan) {
        int[] span = ignoreWhitespaces(ci, Math.min(start, end), Math.max(start, end));

        start = span[0];
        end   = span[1];

        if (start >= end)
            return null;

        TreePath tp = ci.getTreeUtilities().pathFor((start + end) / 2 + 1);

        for ( ; tp != null; tp = tp.getParentPath()) {
            Tree leaf = tp.getLeaf();

            if (!StatementTree.class.isAssignableFrom(leaf.getKind().asInterface()))
               continue;

            long treeStart = ci.getTrees().getSourcePositions().getStartPosition(ci.getCompilationUnit(), leaf);
            long treeEnd   = ci.getTrees().getSourcePositions().getEndPosition(ci.getCompilationUnit(), leaf);

            if (treeStart != start || treeEnd != end) {
                continue;
            }

            List<? extends StatementTree> statements = CopyFinder.getStatements(tp);
            statementsSpan[0] = statements.indexOf(tp.getLeaf());
            statementsSpan[1] = statementsSpan[0];

            return TreePathHandle.create(tp, ci);
        }

        TreePath tpStart = ci.getTreeUtilities().pathFor(start);
        TreePath tpEnd = ci.getTreeUtilities().pathFor(end);

        if (tpStart.getLeaf() != tpEnd.getLeaf() || (tpStart.getLeaf().getKind() != Kind.BLOCK && tpStart.getLeaf().getKind() != Kind.CASE)) {
                    //??? not in the same block:
            return null;
        }

        int from = -1;
        int to   = -1;

        List<? extends StatementTree> statements =   tpStart.getLeaf().getKind() == Kind.BLOCK
                                                   ? ((BlockTree) tpStart.getLeaf()).getStatements()
                                                   : ((CaseTree) tpStart.getLeaf()).getStatements();

        int index = 0;

        for (StatementTree s : statements) {
            long sStart = ci.getTrees().getSourcePositions().getStartPosition(ci.getCompilationUnit(), s);

            if (sStart == start) {
                from = index;
            }

            if (end < sStart && to == (-1)) {
                to = index - 1;
            }

            index++;
        }

        if (from == (-1)) {
            return null;
        }

        if (to == (-1))
            to = statements.size() - 1;

        if (to < from) {
            return null;
        }

        statementsSpan[0] = from;
        statementsSpan[1] = to;

        return TreePathHandle.create(new TreePath(tpStart, statements.get(from)), ci);
    }

    public void run(CompilationInfo info) {
        cancel.set(false);

        FileObject file = info.getFileObject();
        int[] selection = SelectionAwareJavaSourceTaskFactory.getLastSelection(file);

        if (selection == null) {
            //nothing to do....
            HintsController.setErrors(info.getFileObject(), IntroduceHint.class.getName(), Collections.<ErrorDescription>emptyList());
        } else {
            HintsController.setErrors(info.getFileObject(), IntroduceHint.class.getName(), computeError(info, selection[0], selection[1], null, new EnumMap<IntroduceKind, String>(IntroduceKind.class), cancel));
        }
    }

    public void cancel() {
        cancel.set(true);
    }

    private static boolean isConstructor(CompilationInfo info, TreePath path) {
        Element e = info.getTrees().getElement(path);

        return e != null && e.getKind() == ElementKind.CONSTRUCTOR;
    }

    private static boolean isInAnnotationType(CompilationInfo info, TreePath path) {
        Element e = info.getTrees().getElement(path);
        if (e != null) {
            e = e.getEnclosingElement();
            return e != null && e.getKind() == ElementKind.ANNOTATION_TYPE;
        }
        return false;
    }

    private static List<TreePath> findConstructors(CompilationInfo info, TreePath method) {
        List<TreePath> result = new LinkedList<TreePath>();
        TreePath parent = method.getParentPath();

        if (TreeUtilities.CLASS_TREE_KINDS.contains(parent.getLeaf().getKind())) {
            for (Tree t : ((ClassTree) parent.getLeaf()).getMembers()) {
                TreePath tp = new TreePath(parent, t);

                if (isConstructor(info, tp)) {
                    result.add(tp);
                }
            }
        }

        return result;
    }

    private static boolean isInsideClass(TreePath tp) {
        while (tp != null) {
            if (TreeUtilities.CLASS_TREE_KINDS.contains(tp.getLeaf().getKind()))
                return true;

            tp = tp.getParentPath();
        }

        return false;
    }

    static List<ErrorDescription> computeError(CompilationInfo info, int start, int end, Map<IntroduceKind, Fix> fixesMap, Map<IntroduceKind, String> errorMessage, AtomicBoolean cancel) {
        List<ErrorDescription> hints = new LinkedList<ErrorDescription>();
        List<Fix> fixes = new LinkedList<Fix>();
        TreePath resolved = validateSelection(info, start, end);

        if (resolved != null) {
            TreePathHandle h = TreePathHandle.create(resolved, info);
            TreePath method   = findMethod(resolved);
            boolean expressionStatement = resolved.getParentPath().getLeaf().getKind() == Kind.EXPRESSION_STATEMENT;
            TreePath value = resolved.getLeaf().getKind() != Kind.VARIABLE ? resolved : new TreePath(resolved, ((VariableTree) resolved.getLeaf()).getInitializer());
            boolean isConstant = checkConstantExpression(info, value) && !expressionStatement;
            boolean isVariable = findStatement(resolved) != null && method != null && resolved.getLeaf().getKind() != Kind.VARIABLE;
            Set<TreePath> duplicatesForVariable = isVariable ? CopyFinder.computeDuplicates(info, resolved, method, cancel, null).keySet() : null;
            Set<TreePath> duplicatesForConstant = /*isConstant ? */CopyFinder.computeDuplicates(info, resolved, new TreePath(info.getCompilationUnit()), cancel, null).keySet();// : null;
            Scope scope = info.getTrees().getScope(resolved);
            boolean statik = scope != null ? info.getTreeUtilities().isStaticContext(scope) : false;
            String guessedName = Utilities.guessName(info, resolved);
            Fix variable = isVariable ? new IntroduceFix(h, info.getJavaSource(), guessedName, duplicatesForVariable.size() + 1, IntroduceKind.CREATE_VARIABLE) : null;
            Fix constant = isConstant ? new IntroduceFix(h, info.getJavaSource(), guessedName, duplicatesForConstant.size() + 1, IntroduceKind.CREATE_CONSTANT) : null;
            Fix field = null;
            Fix methodFix = null;

            if (method != null && !isInAnnotationType(info, method) && !expressionStatement) {
                int[] initilizeIn = computeInitializeIn(info, resolved, duplicatesForConstant);

                if (statik) {
                    initilizeIn[0] &= ~IntroduceFieldPanel.INIT_CONSTRUCTORS;
                    initilizeIn[1] &= ~IntroduceFieldPanel.INIT_CONSTRUCTORS;
                }

                boolean allowFinalInCurrentMethod = false;

                if (isConstructor(info, method)) {
                    //how many constructors do we have in the target class?:
                    allowFinalInCurrentMethod = findConstructors(info, method).size() == 1;
                }

                field = new IntroduceFieldFix(h, info.getJavaSource(), guessedName, duplicatesForConstant.size() + 1, initilizeIn, statik, allowFinalInCurrentMethod);

                if (resolved.getLeaf().getKind() != Kind.VARIABLE) {
                    //introduce method based on expression:
                    Element methodEl = info.getTrees().getElement(method);
                    Map<TypeMirror, TreePathHandle> typeVar2Def = new HashMap<TypeMirror, TreePathHandle>();
                    List<TreePathHandle> typeVars = new LinkedList<TreePathHandle>();

                    prepareTypeVars(method, info, typeVar2Def, typeVars);

                    ScanStatement scanner = new ScanStatement(info, resolved.getLeaf(), resolved.getLeaf(), typeVar2Def, cancel);

                    if (methodEl != null && (methodEl.getKind() == ElementKind.METHOD || methodEl.getKind() == ElementKind.CONSTRUCTOR)) {
                        ExecutableElement ee = (ExecutableElement) methodEl;

                        scanner.localVariables.addAll(ee.getParameters());
                    }

                    scanner.scan(method, null);

                    List<TreePathHandle> params = new LinkedList<TreePathHandle>();

                    boolean error186980 = false;
                    for (VariableElement ve : scanner.usedLocalVariables) {
                        TreePath path = info.getTrees().getPath(ve);
                        if (path == null) {
                            error186980 = true;
                            Logger.getLogger(IntroduceHint.class.getName()).warning("Cannot get TreePath for local variable " + ve + "\nfile=" + info.getFileObject().getPath());
                        } else {
                            params.add(TreePathHandle.create(path, info));
                        }
                    }

                    if (!error186980) {
                        Set<TypeMirror> exceptions = new HashSet<TypeMirror>(info.getTreeUtilities().getUncaughtExceptions(resolved));

                        Set<TypeMirrorHandle> exceptionHandles = new HashSet<TypeMirrorHandle>();

                        for (TypeMirror tm : exceptions) {
                            exceptionHandles.add(TypeMirrorHandle.create(tm));
                        }

                        int duplicatesCount = CopyFinder.computeDuplicatesAndRemap(info, Collections.singletonList(resolved), new TreePath(info.getCompilationUnit()), scanner.usedLocalVariables, cancel).size();

                        typeVars.retainAll(scanner.usedTypeVariables);

                        methodFix = new IntroduceExpressionBasedMethodFix(info.getJavaSource(), h, params, exceptionHandles, duplicatesCount, typeVars);
                    }
                }
            }

            if (fixesMap != null) {
                fixesMap.put(IntroduceKind.CREATE_VARIABLE, variable);
                fixesMap.put(IntroduceKind.CREATE_CONSTANT, constant);
                fixesMap.put(IntroduceKind.CREATE_FIELD, field);
                fixesMap.put(IntroduceKind.CREATE_METHOD, methodFix);
            }


            if (variable != null) {
                fixes.add(variable);
            }

            if (constant != null) {
                fixes.add(constant);
            }

            if (field != null) {
                fixes.add(field);
            }

            if (methodFix != null) {
                fixes.add(methodFix);
            }
        }

        Fix introduceMethod = computeIntroduceMethod(info, start, end, fixesMap, errorMessage, cancel);

        if (introduceMethod != null) {
            fixes.add(introduceMethod);
            if (fixesMap != null) {
                fixesMap.put(IntroduceKind.CREATE_METHOD, introduceMethod);
            }
        }

        if (!fixes.isEmpty()) {
            int pos = CaretAwareJavaSourceTaskFactory.getLastPosition(info.getFileObject());
            String displayName = NbBundle.getMessage(IntroduceHint.class, "HINT_Introduce");

            hints.add(ErrorDescriptionFactory.createErrorDescription(Severity.HINT, displayName, fixes, info.getFileObject(), pos, pos));
        }

        return hints;
    }

    static Fix computeIntroduceMethod(CompilationInfo info, int start, int end, Map<IntroduceKind, Fix> fixesMap, Map<IntroduceKind, String> errorMessage, AtomicBoolean cancel) {
        int[] statements = new int[2];

        TreePathHandle h = validateSelectionForIntroduceMethod(info, start, end, statements);

        if (h == null) {
            errorMessage.put(IntroduceKind.CREATE_METHOD, "ERR_Invalid_Selection"); // NOI18N
            return null;
        }

        TreePath block = h.resolve(info);
        TreePath method = findMethod(block);

        if (method == null) {
            errorMessage.put(IntroduceKind.CREATE_METHOD, "ERR_Invalid_Selection"); // NOI18N
            return null;
        }

        if (method.getLeaf().getKind() == Kind.METHOD && ((MethodTree) method.getLeaf()).getParameters().contains(block.getLeaf())) {
            errorMessage.put(IntroduceKind.CREATE_METHOD, "ERR_Invalid_Selection"); // NOI18N
            return null;
        }

        Map<TypeMirror, TreePathHandle> typeVar2Def = new HashMap<TypeMirror, TreePathHandle>();
        List<TreePathHandle> typeVars = new LinkedList<TreePathHandle>();

        prepareTypeVars(method, info, typeVar2Def, typeVars);
        
        Element methodEl = info.getTrees().getElement(method);
        List<? extends StatementTree> parentStatements = CopyFinder.getStatements(block);
        List<? extends StatementTree> statementsToWrap = parentStatements.subList(statements[0], statements[1] + 1);
        ScanStatement scanner = new ScanStatement(info, statementsToWrap.get(0), statementsToWrap.get(statementsToWrap.size() - 1), typeVar2Def, cancel);
        Set<TypeMirror> exceptions = new HashSet<TypeMirror>();
        int index = 0;
        TypeMirror methodReturnType = info.getTypes().getNoType(TypeKind.VOID);

        if (methodEl != null && (methodEl.getKind() == ElementKind.METHOD || methodEl.getKind() == ElementKind.CONSTRUCTOR)) {
            ExecutableElement ee = (ExecutableElement) methodEl;

            scanner.localVariables.addAll(ee.getParameters());
            methodReturnType = ee.getReturnType();
        }

        scanner.scan(method, null);

        List<TreePath> pathsOfStatementsToWrap = new LinkedList<TreePath>();

        for (StatementTree s : parentStatements) {
            TreePath path = new TreePath(block, s);

            if (index >= statements[0] && index <= statements[1]) {
                exceptions.addAll(info.getTreeUtilities().getUncaughtExceptions(path));
                pathsOfStatementsToWrap.add(path);
            }

            index++;
        }

        boolean exitsFromAllBranches = Utilities.exitsFromAllBranchers(info, new TreePath(block, statementsToWrap.get(statementsToWrap.size() - 1)));

        String exitsError = scanner.verifyExits(exitsFromAllBranches);

        if (exitsError != null) {
            errorMessage.put(IntroduceKind.CREATE_METHOD, exitsError);
            return null;
        }

        List<TreePathHandle> params = new LinkedList<TreePathHandle>();

        for (VariableElement ve : scanner.usedLocalVariables) {
            params.add(TreePathHandle.create(info.getTrees().getPath(ve), info));
        }

        List<VariableElement> additionalLocalVariables = new LinkedList<VariableElement>(scanner.selectionWrittenLocalVariables);

        additionalLocalVariables.removeAll(scanner.usedLocalVariables);
        additionalLocalVariables.removeAll(scanner.selectionLocalVariables);

        List<TypeMirrorHandle> additionaLocalTypes = new LinkedList<TypeMirrorHandle>();
        List<String> additionaLocalNames = new LinkedList<String>();

        for (VariableElement ve : additionalLocalVariables) {
            additionaLocalTypes.add(TypeMirrorHandle.create(ve.asType()));
            additionaLocalNames.add(ve.getSimpleName().toString());
        }

        List<TreePathHandle> exits = new LinkedList<TreePathHandle>();

        for (TreePath tp : scanner.selectionExits) {
            if(isInsideSameClass(tp, method))
                exits.add(TreePathHandle.create(tp, info));
        }

        TypeMirror returnType;
        TreePathHandle returnAssignTo;
        boolean declareVariableForReturnValue;

        int duplicatesCount = CopyFinder.computeDuplicatesAndRemap(info, pathsOfStatementsToWrap, new TreePath(info.getCompilationUnit()), scanner.usedLocalVariables, cancel).size();

        if (!scanner.usedSelectionLocalVariables.isEmpty()) {
            VariableElement result = scanner.usedSelectionLocalVariables.iterator().next();

            returnType = result.asType();
            returnAssignTo = TreePathHandle.create(info.getTrees().getPath(result), info);
            declareVariableForReturnValue = scanner.selectionLocalVariables.contains(result);
        } else {
            if (!exits.isEmpty() && !exitsFromAllBranches) {
                returnType = info.getTypes().getPrimitiveType(TypeKind.BOOLEAN);
                returnAssignTo = null;
                declareVariableForReturnValue = false;
            } else {
                if (exitsFromAllBranches && scanner.hasReturns) {
                    returnType = methodReturnType;
                    returnAssignTo = null;
                    declareVariableForReturnValue = false;
                } else {
                    returnType = info.getTypes().getNoType(TypeKind.VOID);
                    returnAssignTo = null;
                    declareVariableForReturnValue = false;
                }
            }
        }

        Set<TypeMirrorHandle> exceptionHandles = new HashSet<TypeMirrorHandle>();

        for (TypeMirror tm : exceptions) {
            exceptionHandles.add(TypeMirrorHandle.create(tm));
        }

        typeVars.retainAll(scanner.usedTypeVariables);

        return new IntroduceMethodFix(info.getJavaSource(), h, params, additionaLocalTypes, additionaLocalNames, TypeMirrorHandle.create(returnType), returnAssignTo, declareVariableForReturnValue, exceptionHandles, exits, exitsFromAllBranches, statements[0], statements[1], duplicatesCount, typeVars);
    }

    private static boolean isInsideSameClass(TreePath one, TreePath two) {
        ClassTree oneClass = null;
        ClassTree twoClass = null;

        while (one.getLeaf().getKind() != Kind.COMPILATION_UNIT && one.getLeaf().getKind() != null) {
            Tree t = one.getLeaf();
            if (TreeUtilities.CLASS_TREE_KINDS.contains(t.getKind())) {
                oneClass = (ClassTree) t;
                break;
            }
            one = one.getParentPath();
        }

        while (two.getLeaf().getKind() != Kind.COMPILATION_UNIT && two.getLeaf().getKind() != null) {
            Tree t = two.getLeaf();
            if (TreeUtilities.CLASS_TREE_KINDS.contains(t.getKind())) {
                twoClass = (ClassTree) t;
                break;
            }
            two = two.getParentPath();
        }

        if (oneClass != null && oneClass.equals(twoClass))
            return true;
        
        return false;
    }


    static boolean checkConstantExpression(CompilationInfo info, TreePath path) {
        Tree expr = path.getLeaf();

        if (expr.getKind().asInterface() == BinaryTree.class) {
            BinaryTree bt = (BinaryTree) expr;

            return    checkConstantExpression(info, new TreePath(path, bt.getLeftOperand()))
                   && checkConstantExpression(info, new TreePath(path, bt.getRightOperand()));
        }

        if (UNARY_OPERATORS_FOR_CONSTANTS.contains(expr.getKind())) {
            return checkConstantExpression(info, new TreePath(path, ((UnaryTree) expr).getExpression()));
        }

        if (expr.getKind() == Kind.PARENTHESIZED) {
            return checkConstantExpression(info, new TreePath(path, ((ParenthesizedTree) expr).getExpression()));
        }

        if (expr.getKind() == Kind.IDENTIFIER || expr.getKind() == Kind.MEMBER_SELECT || expr.getKind() == Kind.METHOD_INVOCATION) {
            Element e = info.getTrees().getElement(path);

            if (e == null)
                return false;

            if (e.getKind() == ElementKind.METHOD && expr.getKind() == Kind.METHOD_INVOCATION) {
                List<? extends ExpressionTree> arguments = ((MethodInvocationTree) expr).getArguments();
                for (ExpressionTree et : arguments) {
                    Element element = info.getTrees().getElement(new TreePath(path, et));
                    if (element != null && element.getKind() == ElementKind.FIELD && !info.getTrees().getElement(new TreePath(path, et)).getModifiers().contains(Modifier.STATIC)) {
                        return false;
                    }
                }

                if (e.getModifiers().contains(Modifier.STATIC)) {
                    return true;
                } else {
                    return false;
                }
            }

            if (e.getKind() != ElementKind.FIELD)
                return false;

            if (!e.getModifiers().contains(Modifier.STATIC))
                return false;

            if (!e.getModifiers().contains(Modifier.FINAL))
                return false;

            TypeMirror type = e.asType();

            if (type.getKind().isPrimitive())
                return true;

            if (type.getKind() == TypeKind.DECLARED) {
                TypeElement te = (TypeElement) ((DeclaredType) type).asElement();

                return "java.lang.String".equals(te.getQualifiedName().toString()); // NOI18N
            }

            return false;
        }

        return LITERALS.contains(expr.getKind());
    }

    private static final Set<Kind> UNARY_OPERATORS_FOR_CONSTANTS = EnumSet.of(Kind.UNARY_MINUS, Kind.UNARY_PLUS, Kind.BITWISE_COMPLEMENT, Kind.LOGICAL_COMPLEMENT);
    private static final Set<Kind> LITERALS = EnumSet.of(Kind.STRING_LITERAL, Kind.CHAR_LITERAL, Kind.INT_LITERAL, Kind.LONG_LITERAL, Kind.FLOAT_LITERAL, Kind.DOUBLE_LITERAL);
    private static final Set<ElementKind> LOCAL_VARIABLES = EnumSet.of(ElementKind.EXCEPTION_PARAMETER, ElementKind.LOCAL_VARIABLE, ElementKind.PARAMETER);

    private static TreePath findStatement(TreePath statementPath) {
        while (    statementPath != null
                && (   !StatementTree.class.isAssignableFrom(statementPath.getLeaf().getKind().asInterface())
                || (   statementPath.getParentPath() != null
                && statementPath.getParentPath().getLeaf().getKind() != Kind.BLOCK))) {
            if (TreeUtilities.CLASS_TREE_KINDS.contains(statementPath.getLeaf().getKind()))
                return null;

            statementPath = statementPath.getParentPath();
        }

        return statementPath;
    }

    private static TreePath findMethod(TreePath path) {
        while (path != null) {
            if (path.getLeaf().getKind() == Kind.METHOD) {
                return path;
            }

            if (   path.getLeaf().getKind() == Kind.BLOCK
                && path.getParentPath() != null
                && TreeUtilities.CLASS_TREE_KINDS.contains(path.getParentPath().getLeaf().getKind())) {
                //initializer:
                return path;
            }

            path = path.getParentPath();
        }

        return null;
    }

    private static TreePath findClass(TreePath path) {
        while (path != null) {
            if (TreeUtilities.CLASS_TREE_KINDS.contains(path.getLeaf().getKind())) {
                return path;
            }

            path = path.getParentPath();
        }

        return null;
    }

    private static boolean isParentOf(TreePath parent, TreePath path) {
        Tree parentLeaf = parent.getLeaf();

        while (path != null && path.getLeaf() != parentLeaf) {
            path = path.getParentPath();
        }

        return path != null;
    }

    private static boolean isParentOf(TreePath parent, List<? extends TreePath> candidates) {
        for (TreePath tp : candidates) {
            if (!isParentOf(parent, tp))
                return false;
        }

        return true;
    }

    private static BlockTree findAddPosition(CompilationInfo info, TreePath original, Set<? extends TreePath> candidates, int[] outPosition) {
        //find least common block holding all the candidates:
        TreePath statement = original;

        for (TreePath p : candidates) {
            Tree leaf = p.getLeaf();
            int  leafStart = (int) info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), leaf);
            int  stPathStart = (int) info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), statement.getLeaf());

            if (leafStart < stPathStart) {
                statement = p;
            }
        }

        List<TreePath> allCandidates = new LinkedList<TreePath>();

        allCandidates.add(original);
        allCandidates.addAll(candidates);

        statement = findStatement(statement);

        if (statement == null) {
            //XXX: well....
            return null;
        }

        while (statement.getParentPath() != null && !isParentOf(statement.getParentPath(), allCandidates)) {
            statement = statement.getParentPath();
        }

        //#126269: the common parent may not be block:
        while (statement.getParentPath() != null && statement.getParentPath().getLeaf().getKind() != Kind.BLOCK) {
            statement = statement.getParentPath();
        }

        if (statement.getParentPath() == null)
            return null;//XXX: log

        BlockTree statements = (BlockTree) statement.getParentPath().getLeaf();
        StatementTree statementTree = (StatementTree) statement.getLeaf();

        int index = statements.getStatements().indexOf(statementTree);

        if (index == (-1)) {
            //really strange...
            return null;
        }

        outPosition[0] = index;

        return statements;
    }

    private static int[] computeInitializeIn(final CompilationInfo info, TreePath firstOccurrence, Set<TreePath> occurrences) {
        int[] result = new int[] {7, 7};
        boolean inOneMethod = true;
        Tree currentMethod = findMethod(firstOccurrence).getLeaf();

        for (TreePath occurrence : occurrences) {
            TreePath method = findMethod(occurrence);

            if (method == null || currentMethod != method.getLeaf()) {
                inOneMethod = false;
                break;
            }
        }

        class Result extends RuntimeException {
            @Override
            public synchronized Throwable fillInStackTrace() {
                return null;
            }

        }
        class ReferencesLocalVariable extends TreePathScanner<Void, Void> {
            @Override
            public Void visitIdentifier(IdentifierTree node, Void p) {
                Element e = info.getTrees().getElement(getCurrentPath());

                if (e != null && LOCAL_VARIABLES.contains(e.getKind())) {
                    throw new Result();
                }

                return null;
            }
        }

        boolean referencesLocalvariables = false;

        try {
            new ReferencesLocalVariable().scan(firstOccurrence, null);
        } catch (Result r) {
            referencesLocalvariables = true;
        }

        if (!inOneMethod) {
            result[1] = IntroduceFieldPanel.INIT_FIELD | IntroduceFieldPanel.INIT_CONSTRUCTORS;
        }

        if (referencesLocalvariables) {
            result[0] = IntroduceFieldPanel.INIT_METHOD;
            result[1] = IntroduceFieldPanel.INIT_METHOD;
        }

        return result;
    }

    private static List<ExpressionTree> realArguments(final TreeMaker make, List<VariableElement> parameters) {
        List<ExpressionTree> realArguments = new LinkedList<ExpressionTree>();

        for (VariableElement p : parameters) {
            realArguments.add(make.Identifier(p.getSimpleName()));
        }

        return realArguments;
    }

    private static List<ExpressionTree> realArgumentsForTrees(final TreeMaker make, List<Union2<VariableElement, TreePath>> parameters) {
        List<ExpressionTree> realArguments = new LinkedList<ExpressionTree>();

        for (Union2<VariableElement, TreePath> p : parameters) {
            if (p.hasFirst()) {
                realArguments.add(make.Identifier(p.first().getSimpleName()));
            } else {
                realArguments.add((ExpressionTree) p.second().getLeaf());
            }
        }

        return realArguments;
    }

    private static List<VariableTree> createVariables(WorkingCopy copy, List<VariableElement> parameters) {
        final TreeMaker make = copy.getTreeMaker();
        List<VariableTree> formalArguments = new LinkedList<VariableTree>();

        for (VariableElement p : parameters) {
            TypeMirror tm = p.asType();
            Tree type = make.Type(tm);
            Name formalArgName = p.getSimpleName();
            Set<Modifier> formalArgMods = EnumSet.noneOf(Modifier.class);

            if (p.getModifiers().contains(Modifier.FINAL)) {
                formalArgMods.add(Modifier.FINAL);
            }

            formalArguments.add(make.Variable(make.Modifiers(formalArgMods), formalArgName, type, null));
        }

        return formalArguments;
    }

    private static List<ExpressionTree> typeHandleToTree(WorkingCopy copy, Set<TypeMirrorHandle> thrownTypes) {
        final TreeMaker make = copy.getTreeMaker();
        List<ExpressionTree> thrown = new LinkedList<ExpressionTree>();

        for (TypeMirrorHandle h : thrownTypes) {
            TypeMirror t = h.resolve(copy);

            if (t == null) {
                return null;
            }

            thrown.add((ExpressionTree) make.Type(t));
        }

        return thrown;
    }

    private static final OffsetsBag introduceBag(Document doc) {
        OffsetsBag bag = (OffsetsBag) doc.getProperty(IntroduceHint.class);

        if (bag == null) {
            doc.putProperty(IntroduceHint.class, bag = new OffsetsBag(doc));
        }

        return bag;
    }

    private static List<VariableElement> resolveVariables(CompilationInfo info, Collection<? extends TreePathHandle> handles) {
        List<VariableElement> vars = new LinkedList<VariableElement>();

        for (TreePathHandle tph : handles) {
            vars.add((VariableElement) tph.resolveElement(info));
        }

        return vars;
    }

    private static void prepareTypeVars(TreePath method, CompilationInfo info, Map<TypeMirror, TreePathHandle> typeVar2Def, List<TreePathHandle> typeVars) throws IllegalArgumentException {
        if (method.getLeaf().getKind() == Kind.METHOD) {
            MethodTree mt = (MethodTree) method.getLeaf();

            for (TypeParameterTree tv : mt.getTypeParameters()) {
                TreePath def = new TreePath(method, tv);
                TypeMirror type = info.getTrees().getTypeMirror(def);

                if (type != null && type.getKind() == TypeKind.TYPEVAR) {
                    TreePathHandle tph = TreePathHandle.create(def, info);

                    typeVar2Def.put(type, tph);
                    typeVars.add(tph);
                }
            }
        }
    }

    private static final class ScanStatement extends TreePathScanner<Void, Void> {
        private static final int PHASE_BEFORE_SELECTION = 1;
        private static final int PHASE_INSIDE_SELECTION = 2;
        private static final int PHASE_AFTER_SELECTION = 3;

        private CompilationInfo info;
        private int phase = PHASE_BEFORE_SELECTION;
        private Tree firstInSelection;
        private Tree lastInSelection;
        private Set<VariableElement> localVariables = new HashSet<VariableElement>();
        private Set<VariableElement> usedLocalVariables = new LinkedHashSet<VariableElement>();
        private Set<VariableElement> selectionLocalVariables = new HashSet<VariableElement>();
        private Set<VariableElement> selectionWrittenLocalVariables = new HashSet<VariableElement>();
        private Set<VariableElement> usedSelectionLocalVariables = new HashSet<VariableElement>();
        private Set<TreePath> selectionExits = new HashSet<TreePath>();
        private Set<Tree> treesSeensInSelection = new HashSet<Tree>();
        private final Map<TypeMirror, TreePathHandle> typeVar2Def;
        private Set<TreePathHandle> usedTypeVariables = new HashSet<TreePathHandle>();
        private boolean hasReturns = false;
        private boolean hasBreaks = false;
        private boolean hasContinues = false;
        private boolean secondPass = false;
        private boolean stopSecondPass = false;
        private final AtomicBoolean cancel;

        public ScanStatement(CompilationInfo info, Tree firstInSelection, Tree lastInSelection, Map<TypeMirror, TreePathHandle> typeVar2Def, AtomicBoolean cancel) {
            this.info = info;
            this.firstInSelection = firstInSelection;
            this.lastInSelection = lastInSelection;
            this.typeVar2Def = typeVar2Def;
            this.cancel = cancel;
        }

        @Override
        public Void scan(Tree tree, Void p) {
            if (stopSecondPass)
                return null;

            if (phase != PHASE_AFTER_SELECTION) {
                if (tree == firstInSelection) {
                    phase = PHASE_INSIDE_SELECTION;
                }

                if (phase == PHASE_INSIDE_SELECTION) {
                    treesSeensInSelection.add(tree);
                }
            }

            if (secondPass && tree == firstInSelection) {
                stopSecondPass = true;
                return null;
            }

            super.scan(tree, p);

            if (tree == lastInSelection) {
                phase = PHASE_AFTER_SELECTION;
            }

            return null;
        }

        @Override
        public Void visitVariable(VariableTree node, Void p) {
            Element e = info.getTrees().getElement(getCurrentPath());

            if (e != null && LOCAL_VARIABLES.contains(e.getKind())) {
                switch (phase) {
                    case PHASE_BEFORE_SELECTION:
                        localVariables.add((VariableElement) e);
                        break;
                    case PHASE_INSIDE_SELECTION:
                        selectionLocalVariables.add((VariableElement) e);
                        break;
                }
            }

            return super.visitVariable(node, p);
        }

        @Override
        public Void visitAssignment(AssignmentTree node, Void p) {
            if (phase == PHASE_INSIDE_SELECTION) {
                Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getVariable()));

                if (e != null && LOCAL_VARIABLES.contains(e.getKind()) && localVariables.contains(e)) {
                    selectionWrittenLocalVariables.add((VariableElement) e);
                }
            }

            //make sure the variable on the left side is not considered to be read
            //#162163: but dereferencing array is a read
            if (node.getVariable() != null && node.getVariable().getKind() != Kind.IDENTIFIER) {
                scan(node.getVariable(), p);
            }
            
            return scan(node.getExpression(), p);
        }

        @Override
        public Void visitCompoundAssignment(CompoundAssignmentTree node, Void p) {
            if (phase == PHASE_INSIDE_SELECTION) {
                Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getVariable()));

                if (e != null && LOCAL_VARIABLES.contains(e.getKind())) {
                    selectionWrittenLocalVariables.add((VariableElement) e);
                }
            }

            return super.visitCompoundAssignment(node, p);
        }

        @Override
        public Void visitUnary(UnaryTree node, Void p) {
            Kind k = node.getKind();

            if (k == Kind.POSTFIX_DECREMENT || k == Kind.POSTFIX_INCREMENT || k == Kind.PREFIX_DECREMENT || k == Kind.PREFIX_INCREMENT) {
                //#109663:
                if (phase == PHASE_INSIDE_SELECTION) {
                    Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getExpression()));

                    if (e != null && LOCAL_VARIABLES.contains(e.getKind())) {
                        selectionWrittenLocalVariables.add((VariableElement) e);
                    }
                }
            }
            return super.visitUnary(node, p);
        }

        @Override
        public Void visitIdentifier(IdentifierTree node, Void p) {
            Element e = info.getTrees().getElement(getCurrentPath());

            if (e != null) {
                if (LOCAL_VARIABLES.contains(e.getKind())) {
                    switch (phase) {
                        case PHASE_INSIDE_SELECTION:
                            if (localVariables.contains(e)) {
                                usedLocalVariables.add((VariableElement) e);
                            }
                            break;
                        case PHASE_AFTER_SELECTION:
                            if (selectionLocalVariables.contains(e) || selectionWrittenLocalVariables.contains(e)) {
                                usedSelectionLocalVariables.add((VariableElement) e);
                            }
                            break;
                    }
                }
            }

            if (phase == PHASE_INSIDE_SELECTION) {
                TypeMirror type = info.getTrees().getTypeMirror(getCurrentPath());

                if (type != null) {
                    TreePathHandle def = typeVar2Def.get(type);

                    usedTypeVariables.add(def);
                }
            }

            return super.visitIdentifier(node, p);
        }

        @Override
        public Void visitReturn(ReturnTree node, Void p) {
            if (phase == PHASE_INSIDE_SELECTION) {
                selectionExits.add(getCurrentPath());
                hasReturns = true;
            }
            return super.visitReturn(node, p);
        }

        @Override
        public Void visitBreak(BreakTree node, Void p) {
            if (phase == PHASE_INSIDE_SELECTION && !treesSeensInSelection.contains(info.getTreeUtilities().getBreakContinueTarget(getCurrentPath()))) {
                selectionExits.add(getCurrentPath());
                hasBreaks = true;
            }
            return super.visitBreak(node, p);
        }

        @Override
        public Void visitContinue(ContinueTree node, Void p) {
            if (phase == PHASE_INSIDE_SELECTION && !treesSeensInSelection.contains(info.getTreeUtilities().getBreakContinueTarget(getCurrentPath()))) {
                selectionExits.add(getCurrentPath());
                hasContinues = true;
            }
            return super.visitContinue(node, p);
        }

        @Override
        public Void visitWhileLoop(WhileLoopTree node, Void p) {
            super.visitWhileLoop(node, p);

            if (phase == PHASE_AFTER_SELECTION) {
                //#109663&#112552:
                //the selection was inside the while-loop, the variables inside the
                //condition&statement of the while loop need to be considered to be used again after the loop:
                if (!secondPass) {
                    secondPass = true;
                    scan(node.getCondition(), p);
                    scan(node.getStatement(), p);
                    secondPass = false;
                    stopSecondPass = false;
                }
            }

            return null;
        }

        @Override
        public Void visitForLoop(ForLoopTree node, Void p) {
            super.visitForLoop(node, p);

            if (phase == PHASE_AFTER_SELECTION) {
                //#109663&#112552:
                //the selection was inside the for-loop, the variables inside the
                //condition, update and statement parts of the for loop need to be considered to be used again after the loop:
                if (!secondPass) {
                    secondPass = true;
                    scan(node.getCondition(), p);
                    scan(node.getUpdate(), p);
                    scan(node.getStatement(), p);
                    secondPass = false;
                    stopSecondPass = false;
                }
            }

            return null;
        }

        @Override
        public Void visitDoWhileLoop(DoWhileLoopTree node, Void p) {
            super.visitDoWhileLoop(node, p);

            if (phase == PHASE_AFTER_SELECTION) {
                //#109663&#112552:
                //the selection was inside the do-while, the variables inside the
                //statement part of the do-while loop need to be considered to be used again after the loop:
                if (!secondPass) {
                    secondPass = true;
                    scan(node.getStatement(), p);
                    secondPass = false;
                    stopSecondPass = false;
                }
            }

            return null;
        }

        private String verifyExits(boolean exitsFromAllBranches) {
            int i = 0;

            i += hasReturns ? 1 : 0;
            i += hasBreaks ? 1 : 0;
            i += hasContinues ? 1 : 0;

            if (i > 1) {
                return "ERR_Too_Many_Different_Exits"; // NOI18N
            }

            if ((exitsFromAllBranches ? 0 : i) + usedSelectionLocalVariables.size() > 1) {
                return "ERR_Too_Many_Return_Values"; // NOI18N
            }

            StatementTree breakOrContinueTarget = null;
            boolean returnValueComputed = false;
            TreePath returnValue = null;

            for (TreePath tp : selectionExits) {
                if (tp.getLeaf().getKind() == Kind.RETURN) {
                    if (!exitsFromAllBranches) {
                        ReturnTree rt = (ReturnTree) tp.getLeaf();
                        TreePath currentReturnValue = rt.getExpression() != null ? new TreePath(tp, rt.getExpression()) : null;

                        if (!returnValueComputed) {
                            returnValue = currentReturnValue;
                            returnValueComputed = true;
                        } else {
                            if (returnValue != null && currentReturnValue != null) {
                                Set<TreePath> candidates = CopyFinder.computeDuplicates(info, returnValue, currentReturnValue, cancel, null).keySet();

                                if (candidates.size() != 1 || candidates.iterator().next().getLeaf() != rt.getExpression()) {
                                    return "ERR_Different_Return_Values"; // NOI18N
                                }
                            } else {
                                if (returnValue != currentReturnValue) {
                                    return "ERR_Different_Return_Values"; // NOI18N
                                }
                            }
                        }
                    }
                } else {
                    StatementTree target = info.getTreeUtilities().getBreakContinueTarget(tp);

                    if (breakOrContinueTarget == null) {
                        breakOrContinueTarget = target;
                    }

                    if (breakOrContinueTarget != target)
                        return "ERR_Break_Mismatch"; // NOI18N
                }
            }

            return null;
        }
    }


    private static void removeFromParent(WorkingCopy parameter, TreePath what) throws IllegalAccessException {
        final TreeMaker make = parameter.getTreeMaker();
        Tree parentTree = what.getParentPath().getLeaf();
        Tree original = what.getLeaf();
        Tree newParent;

        switch (parentTree.getKind()) {
            case BLOCK:
                newParent = make.removeBlockStatement((BlockTree) parentTree, (StatementTree) original);
                break;
            case CASE:
                newParent = make.removeCaseStatement((CaseTree) parentTree, (StatementTree) original);
                break;
            default:
                throw new IllegalAccessException(parentTree.getKind().toString());
        }

        parameter.rewrite(parentTree, newParent);
    }

    private static final class IntroduceFix implements Fix {

        private String guessedName;
        private TreePathHandle handle;
        private JavaSource js;
        private int numDuplicates;
        private IntroduceKind kind;

        public IntroduceFix(TreePathHandle handle, JavaSource js, String guessedName, int numDuplicates, IntroduceKind kind) {
            this.handle = handle;
            this.js = js;
            this.guessedName = guessedName;
            this.numDuplicates = numDuplicates;
            this.kind = kind;
        }

        @Override
        public String toString() {
            return "[IntroduceFix:" + guessedName + ":" + numDuplicates + ":" + kind + "]"; // NOI18N
        }

        public String getKeyExt() {
            switch (kind) {
                case CREATE_CONSTANT:
                    return "IntroduceConstant"; //NOI18N
                case CREATE_VARIABLE:
                    return "IntroduceVariable"; //NOI18N
                default:
                    throw new IllegalStateException();
            }
        }

        public String getText() {
            return NbBundle.getMessage(IntroduceHint.class, "FIX_" + getKeyExt()); //NOI18N
        }

        public ChangeInfo implement() throws IOException, BadLocationException {
            JButton btnOk = new JButton( NbBundle.getMessage( IntroduceHint.class, "LBL_Ok" ) );
            JButton btnCancel = new JButton( NbBundle.getMessage( IntroduceHint.class, "LBL_Cancel" ) );
            IntroduceVariablePanel panel = new IntroduceVariablePanel(numDuplicates, guessedName, kind == IntroduceKind.CREATE_CONSTANT, handle.getKind() == Kind.VARIABLE, btnOk);
            String caption = NbBundle.getMessage(IntroduceHint.class, "CAP_" + getKeyExt()); //NOI18N
            DialogDescriptor dd = new DialogDescriptor(panel, caption, true, new Object[] {btnOk, btnCancel}, btnOk, DialogDescriptor.DEFAULT_ALIGN, null, null);
            if (DialogDisplayer.getDefault().notify(dd) != btnOk) {
                return null;//cancel
            }
            final String name = panel.getVariableName();
            final boolean replaceAll = panel.isReplaceAll();
            final boolean declareFinal = panel.isDeclareFinal();
            final Set<Modifier> access = kind == IntroduceKind.CREATE_CONSTANT ? panel.getAccess() : null;
            js.runModificationTask(new Task<WorkingCopy>() {
                public void run(WorkingCopy parameter) throws Exception {
                    parameter.toPhase(Phase.RESOLVED);

                    TreePath resolved = handle.resolve(parameter);
                    
                    if (resolved == null) {
                        return ; //TODO...
                    }

                    TypeMirror tm = parameter.getTrees().getTypeMirror(resolved);

                    if (tm == null) {
                        return ; //TODO...
                    }

                    tm = Utilities.convertIfAnonymous(Utilities.resolveCapturedType(parameter, tm));

                    Tree original = resolved.getLeaf();
                    boolean variableRewrite = original.getKind() == Kind.VARIABLE;
                    ExpressionTree expression = !variableRewrite ? (ExpressionTree) resolved.getLeaf() : ((VariableTree) original).getInitializer();
                    ModifiersTree mods;
                    final TreeMaker make = parameter.getTreeMaker();
                    
                    boolean expressionStatement = resolved.getParentPath().getLeaf().getKind() == Tree.Kind.EXPRESSION_STATEMENT;

                    switch (kind) {
                        case CREATE_CONSTANT:
                            //find first class:
                            TreePath pathToClass = resolved;

                            while (pathToClass != null && !TreeUtilities.CLASS_TREE_KINDS.contains(pathToClass.getLeaf().getKind())) {
                                pathToClass = pathToClass.getParentPath();
                            }

                            if (pathToClass == null) {
                                return ; //TODO...
                            }

                            Set<Modifier> localAccess = EnumSet.of(Modifier.FINAL, Modifier.STATIC);

                            localAccess.addAll(access);

                            mods = make.Modifiers(localAccess);

                            VariableTree constant;

                            if (!variableRewrite) {
                                constant = make.Variable(mods, name, make.Type(tm), expression);
                            } else {
                                VariableTree originalVar = (VariableTree) original;
                                constant = make.Variable(mods, originalVar.getName(), originalVar.getType(), originalVar.getInitializer());
                                removeFromParent(parameter, resolved);
                                expressionStatement = true;
                            }
                            
                            ClassTree nueClass = GeneratorUtils.insertClassMember(parameter, pathToClass, constant);

                            parameter.rewrite(pathToClass.getLeaf(), nueClass);

                            if (replaceAll) {
                                for (TreePath p : CopyFinder.computeDuplicates(parameter, resolved, new TreePath(parameter.getCompilationUnit()), new AtomicBoolean(), null).keySet()) {
                                    parameter.rewrite(p.getLeaf(), make.Identifier(name));
                                }
                            }
                            break;
                        case CREATE_VARIABLE:
                            TreePath method        = findMethod(resolved);

                            if (method == null) {
                                return ; //TODO...
                            }

                            BlockTree statements;
                            int       index;

                            if (replaceAll) {
                                Set<TreePath> candidates = CopyFinder.computeDuplicates(parameter, resolved, method, new AtomicBoolean(), null).keySet();
                                for (TreePath p : candidates) {
                                    Tree leaf = p.getLeaf();

                                    parameter.rewrite(leaf, make.Identifier(name));
                                }

                                int[] out = new int[1];
                                statements = findAddPosition(parameter, resolved, candidates, out);

                                if (statements == null) {
                                    return;
                                }

                                index = out[0];
                            } else {
                                int[] out = new int[1];
                                statements = findAddPosition(parameter, resolved, Collections.<TreePath>emptySet(), out);

                                if (statements == null) {
                                    return;
                                }

                                index = out[0];
                            }

                            List<StatementTree> nueStatements = new LinkedList<StatementTree>(statements.getStatements());
                            mods = make.Modifiers(declareFinal ? EnumSet.of(Modifier.FINAL) : EnumSet.noneOf(Modifier.class));

                            nueStatements.add(index, make.Variable(mods, name, make.Type(tm), expression));

                            if (expressionStatement)
                                nueStatements.remove(resolved.getParentPath().getLeaf());

                            BlockTree nueBlock = make.Block(nueStatements, false);

                            parameter.rewrite(statements, nueBlock);
                            break;
                    }

                    if (!expressionStatement) {
                        Tree origParent = resolved.getParentPath().getLeaf();
                        Tree newParent = parameter.getTreeUtilities().translate(origParent, Collections.singletonMap(resolved.getLeaf(), make.Identifier(name)));
                        parameter.rewrite(origParent, newParent);

                    }
                }
            }).commit();
            return null;
        }
    }

    private static final class IntroduceFieldFix implements Fix {

        private String guessedName;
        private TreePathHandle handle;
        private JavaSource js;
        private int numDuplicates;
        private int[] initilizeIn;
        private boolean statik;
        private boolean allowFinalInCurrentMethod;

        public IntroduceFieldFix(TreePathHandle handle, JavaSource js, String guessedName, int numDuplicates, int[] initilizeIn, boolean statik, boolean allowFinalInCurrentMethod) {
            this.handle = handle;
            this.js = js;
            this.guessedName = guessedName;
            this.numDuplicates = numDuplicates;
            this.initilizeIn = initilizeIn;
            this.statik = statik;
            this.allowFinalInCurrentMethod = allowFinalInCurrentMethod;
        }

        public String getText() {
            return NbBundle.getMessage(IntroduceHint.class, "FIX_IntroduceField");
        }

        @Override
        public String toString() {
            return "[IntroduceField:" + guessedName + ":" + numDuplicates + ":" + statik + ":" + allowFinalInCurrentMethod + ":" + Arrays.toString(initilizeIn) + "]"; // NOI18N
        }

        public ChangeInfo implement() throws IOException, BadLocationException {
            JButton btnOk = new JButton( NbBundle.getMessage( IntroduceHint.class, "LBL_Ok" ) );
            btnOk.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(IntroduceHint.class, "AD_IntrHint_OK"));
            JButton btnCancel = new JButton( NbBundle.getMessage( IntroduceHint.class, "LBL_Cancel" ) );
            btnCancel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(IntroduceHint.class, "AD_IntrHint_Cancel"));
            IntroduceFieldPanel panel = new IntroduceFieldPanel(guessedName, initilizeIn, numDuplicates, allowFinalInCurrentMethod, handle.getKind() == Kind.VARIABLE, btnOk);
            String caption = NbBundle.getMessage(IntroduceHint.class, "CAP_IntroduceField");
            DialogDescriptor dd = new DialogDescriptor(panel, caption, true, new Object[] {btnOk, btnCancel}, btnOk, DialogDescriptor.DEFAULT_ALIGN, null, null);
            if (DialogDisplayer.getDefault().notify(dd) != btnOk) {
                return null;//cancel
            }
            final String name = panel.getFieldName();
            final boolean replaceAll = panel.isReplaceAll();
            final boolean declareFinal = panel.isDeclareFinal();
            final Set<Modifier> access = panel.getAccess();
            final int initializeIn = panel.getInitializeIn();
            js.runModificationTask(new Task<WorkingCopy>() {
                public void run(WorkingCopy parameter) throws Exception {
                    parameter.toPhase(Phase.RESOLVED);

                    TreePath resolved = handle.resolve(parameter);
                    TypeMirror tm = parameter.getTrees().getTypeMirror(resolved);

                    if (resolved == null || tm == null) {
                        return ; //TODO...
                    }

                    tm = Utilities.convertIfAnonymous(Utilities.resolveCapturedType(parameter, tm));

                    TreePath pathToClass = resolved;

                    while (pathToClass != null && !TreeUtilities.CLASS_TREE_KINDS.contains(pathToClass.getLeaf().getKind())) {
                        pathToClass = pathToClass.getParentPath();
                    }

                    if (pathToClass == null) {
                        return ; //TODO...
                    }

                    Tree original = resolved.getLeaf();
                    boolean variableRewrite = original.getKind() == Kind.VARIABLE;
                    ExpressionTree expression = !variableRewrite ? (ExpressionTree) resolved.getLeaf() : ((VariableTree) original).getInitializer();

                    Set<Modifier> mods = declareFinal ? EnumSet.of(Modifier.FINAL) : EnumSet.noneOf(Modifier.class);

                    if (statik) {
                        mods.add(Modifier.STATIC);
                    }

                    mods.addAll(access);
                    final TreeMaker make = parameter.getTreeMaker();

                    boolean isAnyOccurenceStatic = false;

                    if (replaceAll) {
                        for (TreePath p : CopyFinder.computeDuplicates(parameter, resolved, new TreePath(parameter.getCompilationUnit()), new AtomicBoolean(), null).keySet()) {
                            parameter.rewrite(p.getLeaf(), make.Identifier(name));
                            Scope occurenceScope = parameter.getTrees().getScope(p);
                            if(parameter.getTreeUtilities().isStaticContext(occurenceScope))
                                isAnyOccurenceStatic = true;

                        }
                    }

                    if(!statik && isAnyOccurenceStatic) {
                        mods.add(Modifier.STATIC);
                    }

                    ModifiersTree modsTree = make.Modifiers(mods);
                    Tree parentTree = resolved.getParentPath().getLeaf();
                    VariableTree field;

                    if (!variableRewrite) {
                        field = make.Variable(modsTree, name, make.Type(tm), initializeIn == IntroduceFieldPanel.INIT_FIELD ? expression : null);

                        Tree nueParent = parameter.getTreeUtilities().translate(parentTree, Collections.singletonMap(resolved.getLeaf(), make.Identifier(name)));
                        parameter.rewrite(parentTree, nueParent);
                    } else {
                        VariableTree originalVar = (VariableTree) original;

                        field = make.Variable(modsTree, originalVar.getName(), originalVar.getType(), initializeIn == IntroduceFieldPanel.INIT_FIELD ? expression : null);

                        removeFromParent(parameter, resolved);
                    }
                    
                    ClassTree nueClass = GeneratorUtils.insertClassMember(parameter, pathToClass, field);

                    TreePath method        = findMethod(resolved);

                    if (method == null) {
                        return ; //TODO...
                    }

                    if (initializeIn == IntroduceFieldPanel.INIT_METHOD) {
                        TreePath statementPath = resolved;

                        statementPath = findStatement(statementPath);

                        if (statementPath == null) {
                            //XXX: well....
                            return ;
                        }

                        BlockTree statements = (BlockTree) statementPath.getParentPath().getLeaf();
                        StatementTree statement = (StatementTree) statementPath.getLeaf();

                        int index = statements.getStatements().indexOf(statement);

                        if (index == (-1)) {
                            //really strange...
                            return ;
                        }

                        List<StatementTree> nueStatements = new LinkedList<StatementTree>(statements.getStatements());

                        if (expression.getKind() == Kind.NEW_ARRAY) {
                            List<? extends ExpressionTree> initializers = ((NewArrayTree) expression).getInitializers();
                            expression = make.NewArray(make.Type(((ArrayType)tm).getComponentType()), Collections.<ExpressionTree>emptyList(), initializers);
                        }

                        nueStatements.add(index, make.ExpressionStatement(make.Assignment(make.Identifier(name), expression)));

                        BlockTree nueBlock = make.Block(nueStatements, false);

                        parameter.rewrite(statements, nueBlock);
                    }

                    if (initializeIn == IntroduceFieldPanel.INIT_CONSTRUCTORS) {
                        for (TreePath constructor : findConstructors(parameter, method)) {
                            //check for syntetic constructor:
                            if (parameter.getTreeUtilities().isSynthetic(constructor)) {
                                List<StatementTree> nueStatements = new LinkedList<StatementTree>();
                                ExpressionTree reference = make.Identifier(name);
                                Element clazz = parameter.getTrees().getElement(pathToClass);
                                ModifiersTree constrMods = clazz.getKind() != ElementKind.ENUM?make.Modifiers(EnumSet.of(Modifier.PUBLIC)):make.Modifiers(Collections.EMPTY_SET);

                                nueStatements.add(make.ExpressionStatement(make.Assignment(reference, expression)));

                                BlockTree nueBlock = make.Block(nueStatements, false);
                                MethodTree nueConstr = make.Method(constrMods, "<init>", null, Collections.<TypeParameterTree>emptyList(), Collections.<VariableTree>emptyList(), Collections.<ExpressionTree>emptyList(), nueBlock, null); //NOI18N

                                nueClass = GeneratorUtils.insertClassMember(parameter, new TreePath(new TreePath(parameter.getCompilationUnit()), nueClass), nueConstr);

                                nueClass = make.removeClassMember(nueClass, constructor.getLeaf());
                                break;
                            }

                            boolean hasParameterOfTheSameName = false;
                            MethodTree constr = ((MethodTree) constructor.getLeaf());

                            for (VariableTree p : constr.getParameters()) {
                                if (name.equals(p.getName().toString())) {
                                    hasParameterOfTheSameName = true;
                                    break;
                                }
                            }

                            BlockTree origBody = constr.getBody();
                            List<StatementTree> nueStatements = new LinkedList<StatementTree>();
                            ExpressionTree reference = hasParameterOfTheSameName ? make.MemberSelect(make.Identifier("this"), name) : make.Identifier(name); // NOI18N

                            List<? extends StatementTree> origStatements = origBody.getStatements();
                            StatementTree canBeSuper = origStatements.get(0);
                            if (!parameter.getTreeUtilities().isSynthetic(TreePath.getPath(constructor, canBeSuper))) {
                                nueStatements.add(canBeSuper);
                            }
                            nueStatements.add(make.ExpressionStatement(make.Assignment(reference, expression)));
                            nueStatements.addAll(origStatements.subList(1, origStatements.size()));

                            BlockTree nueBlock = make.Block(nueStatements, false);

                            parameter.rewrite(origBody, nueBlock);
                        }
                    }

                    parameter.rewrite(pathToClass.getLeaf(), nueClass);
                }
            }).commit();
            return null;
        }
    }

    private static final AttributeSet DUPE = AttributesUtilities.createImmutable(StyleConstants.Background, Color.GRAY);
    
    private static final class IntroduceMethodFix implements Fix {

        private JavaSource js;

        private TreePathHandle parentBlock;
        private List<TreePathHandle> parameters;
        private List<TypeMirrorHandle> additionalLocalTypes;
        private List<String> additionalLocalNames;
        private TypeMirrorHandle returnType;
        private TreePathHandle returnAssignTo;
        private boolean declareVariableForReturnValue;
        private Set<TypeMirrorHandle> thrownTypes;
        private List<TreePathHandle> exists;
        private boolean exitsFromAllBranches;
        private int from;
        private int to;
        private final int duplicatesCount;
        private final List<TreePathHandle> typeVars;

        public IntroduceMethodFix(JavaSource js, TreePathHandle parentBlock, List<TreePathHandle> parameters, List<TypeMirrorHandle> additionalLocalTypes, List<String> additionalLocalNames, TypeMirrorHandle returnType, TreePathHandle returnAssignTo, boolean declareVariableForReturnValue, Set<TypeMirrorHandle> thrownTypes, List<TreePathHandle> exists, boolean exitsFromAllBranches, int from, int to, int duplicatesCount, List<TreePathHandle> typeVars) {
            this.js = js;
            this.parentBlock = parentBlock;
            this.parameters = parameters;
            this.additionalLocalTypes = additionalLocalTypes;
            this.additionalLocalNames = additionalLocalNames;
            this.returnType = returnType;
            this.returnAssignTo = returnAssignTo;
            this.declareVariableForReturnValue = declareVariableForReturnValue;
            this.thrownTypes = thrownTypes;
            this.exists = exists;
            this.exitsFromAllBranches = exitsFromAllBranches;
            this.from = from;
            this.to = to;
            this.duplicatesCount = duplicatesCount;
            this.typeVars = typeVars;
        }

        public String getText() {
            return NbBundle.getMessage(IntroduceHint.class, "FIX_IntroduceMethod");
        }

        public String toDebugString(CompilationInfo info) {
            return "[IntroduceMethod:" + from + ":" + to + "]"; // NOI18N
        }

        public ChangeInfo implement() throws Exception {
            JButton btnOk = new JButton( NbBundle.getMessage( IntroduceHint.class, "LBL_Ok" ) );
            JButton btnCancel = new JButton( NbBundle.getMessage( IntroduceHint.class, "LBL_Cancel" ) );
            IntroduceMethodPanel panel = new IntroduceMethodPanel("", duplicatesCount); //NOI18N
            panel.setOkButton( btnOk );
            String caption = NbBundle.getMessage(IntroduceHint.class, "CAP_IntroduceMethod");
            DialogDescriptor dd = new DialogDescriptor(panel, caption, true, new Object[] {btnOk, btnCancel}, btnOk, DialogDescriptor.DEFAULT_ALIGN, null, null);
            if (DialogDisplayer.getDefault().notify(dd) != btnOk) {
                return null;//cancel
            }
            final String name = panel.getMethodName();
            final Set<Modifier> access = panel.getAccess();
            final boolean replaceOther = panel.getReplaceOther();

            js.runModificationTask(new Task<WorkingCopy>() {
                public void run(WorkingCopy copy) throws Exception {
                    copy.toPhase(Phase.RESOLVED);

                    TreePath firstStatement = parentBlock.resolve(copy);
                    TypeMirror returnType = IntroduceMethodFix.this.returnType.resolve(copy);

                    if (firstStatement == null || returnType == null) {
                        return ; //TODO...
                    }

                    GeneratorUtilities.get(copy).importComments(firstStatement.getParentPath().getLeaf(), copy.getCompilationUnit());
                    
                    Scope s = copy.getTrees().getScope(firstStatement);
                    boolean isStatic = copy.getTreeUtilities().isStaticContext(s);
                    List<? extends StatementTree> statements = CopyFinder.getStatements(firstStatement);
                    List<StatementTree> nueStatements = new LinkedList<StatementTree>();

                    nueStatements.addAll(statements.subList(0, from));

                    final TreeMaker make = copy.getTreeMaker();
                    List<VariableElement> parameters = resolveVariables(copy, IntroduceMethodFix.this.parameters);
                    List<ExpressionTree> realArguments = realArguments(make, parameters);

                    List<StatementTree> methodStatements = new LinkedList<StatementTree>();

                    Iterator<TypeMirrorHandle> additionalType = additionalLocalTypes.iterator();
                    Iterator<String> additionalName = additionalLocalNames.iterator();

                    while (additionalType.hasNext() && additionalName.hasNext()) {
                        TypeMirror tm = additionalType.next().resolve(copy);

                        if (tm == null) {
                            //XXX:
                            return ;
                        }

                        Tree type = make.Type(tm);

                        methodStatements.add(make.Variable(make.Modifiers(EnumSet.noneOf(Modifier.class)), additionalName.next(), type, null));
                    }

                    methodStatements.addAll(statements.subList(from, to + 1));

                    Tree returnTypeTree = make.Type(returnType);
                    ExpressionTree invocation = make.MethodInvocation(Collections.<ExpressionTree>emptyList(), make.Identifier(name), realArguments);

                    ReturnTree ret = null;
                    VariableElement returnAssignTo = null;

                    if (IntroduceMethodFix.this.returnAssignTo != null) {
                        returnAssignTo = (VariableElement) IntroduceMethodFix.this.returnAssignTo.resolveElement(copy);

                        if (returnAssignTo == null) {
                            return; //TODO...
                        }
                    }

                    if (returnAssignTo != null) {
                        ret = make.Return(make.Identifier(returnAssignTo.getSimpleName()));
                        if (declareVariableForReturnValue) {
                            nueStatements.add(make.Variable(make.Modifiers(EnumSet.noneOf(Modifier.class)), returnAssignTo.getSimpleName(), returnTypeTree, invocation));
                            invocation = null;
                        } else {
                            invocation = make.Assignment(make.Identifier(returnAssignTo.getSimpleName()), invocation);
                        }
                    }

                    if (!exists.isEmpty()) {
                        TreePath handle = null;

                        handle = exists.iterator().next().resolve(copy);

                        if (handle == null) {
                            return ; //TODO...
                        }

                        assert handle != null;

                        if (exitsFromAllBranches && handle.getLeaf().getKind() == Kind.RETURN) {
                            nueStatements.add(make.Return(invocation));
                        } else {
                            if (ret == null) {
                                if (exitsFromAllBranches) {
                                    ret = make.Return(null);
                                } else {
                                    ret = make.Return(make.Literal(true));
                                }
                            }

                            for (TreePathHandle h : exists) {
                                TreePath resolved = h.resolve(copy);

                                if (resolved == null) {
                                    return ; //TODO...
                                }

                                copy.rewrite(resolved.getLeaf(), ret);
                            }

                            StatementTree branch = null;

                            switch (handle.getLeaf().getKind()) {
                                case BREAK:
                                    branch = make.Break(((BreakTree) handle.getLeaf()).getLabel());
                                    break;
                                case CONTINUE:
                                    branch = make.Continue(((ContinueTree) handle.getLeaf()).getLabel());
                                    break;
                                case RETURN:
                                    branch = make.Return(((ReturnTree) handle.getLeaf()).getExpression());
                                    break;
                            }

                            if (returnAssignTo != null || exitsFromAllBranches) {
                                nueStatements.add(make.ExpressionStatement(invocation));
                                nueStatements.add(branch);
                            } else {
                                nueStatements.add(make.If(make.Parenthesized(invocation), branch, null));
                                methodStatements.add(make.Return(make.Literal(false)));
                            }
                        }

                        invocation = null;
                    } else {
                        if (ret != null) {
                            methodStatements.add(ret);
                        }
                    }

                    if (invocation != null)
                        nueStatements.add(make.ExpressionStatement(invocation));

                    nueStatements.addAll(statements.subList(to + 1, statements.size()));

                    if (replaceOther) {
                        //handle duplicates
                        Document doc = copy.getDocument();
                        List<TreePath> statementsPaths = new LinkedList<TreePath>();

                        for (StatementTree t : statements.subList(from, to + 1)) {
                            statementsPaths.add(new TreePath(firstStatement.getParentPath(), t));
                        }

                        for (MethodDuplicateDescription mdd : CopyFinder.computeDuplicatesAndRemap(copy, statementsPaths, new TreePath(copy.getCompilationUnit()), parameters, new AtomicBoolean())) {
                            List<? extends StatementTree> parentStatements = CopyFinder.getStatements(mdd.firstLeaf);
                            int startOff = (int) copy.getTrees().getSourcePositions().getStartPosition(copy.getCompilationUnit(), parentStatements.get(mdd.dupeStart));
                            int endOff = (int) copy.getTrees().getSourcePositions().getEndPosition(copy.getCompilationUnit(), parentStatements.get(mdd.dupeEnd));

                            introduceBag(doc).clear();
                            introduceBag(doc).addHighlight(startOff, endOff, DUPE);

                            String title = NbBundle.getMessage(IntroduceHint.class, "TTL_DuplicateMethodPiece");
                            String message = NbBundle.getMessage(IntroduceHint.class, "MSG_DuplicateMethodPiece");

                            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(message, title, NotifyDescriptor.YES_NO_OPTION);

                            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.YES_OPTION) {
                                continue;
                            }

                            List<StatementTree> newStatements = new LinkedList<StatementTree>();

                            newStatements.addAll(parentStatements.subList(0, mdd.dupeStart));

                            //XXX:
                            List<Union2<VariableElement, TreePath>> dupeParameters = new LinkedList<Union2<VariableElement, TreePath>>();

                            for (VariableElement ve : parameters) {
                                if (mdd.variablesRemapToTrees.containsKey(ve)) {
                                    dupeParameters.add(Union2.<VariableElement, TreePath>createSecond(mdd.variablesRemapToTrees.get(ve)));
                                } else {
                                    dupeParameters.add(Union2.<VariableElement, TreePath>createFirst(ve));
                                }
                            }

                            List<ExpressionTree> dupeRealArguments = realArgumentsForTrees(make, dupeParameters);
                            ExpressionTree dupeInvocation = make.MethodInvocation(Collections.<ExpressionTree>emptyList(), make.Identifier(name), dupeRealArguments);

                            if (returnAssignTo != null) {
                                TreePath remappedTree = mdd.variablesRemapToTrees.containsKey(returnAssignTo) ? mdd.variablesRemapToTrees.get(returnAssignTo) : null;
                                VariableElement remappedElement = mdd.variablesRemapToElement.containsKey(returnAssignTo) ? (VariableElement) mdd.variablesRemapToElement.get(returnAssignTo) : null;
//                                VariableElement dupeReturnAssignTo = mdd.variablesRemapToTrees.containsKey(returnAssignTo) ? (VariableElement) mdd.variablesRemapToTrees.get(returnAssignTo) : returnAssignTo;
                                if (declareVariableForReturnValue) {
                                    assert remappedElement != null || remappedTree == null;
                                    Name name = remappedElement != null ? remappedElement.getSimpleName() : returnAssignTo.getSimpleName();
                                    newStatements.add(make.Variable(make.Modifiers(EnumSet.noneOf(Modifier.class)), name, returnTypeTree/*???: more specific type?*/, invocation));
                                    dupeInvocation = null;
                                } else {
                                    ExpressionTree sel = remappedTree != null ? (ExpressionTree) remappedTree.getLeaf()
                                                                              : remappedElement != null ? make.Identifier(remappedElement.getSimpleName())
                                                                                                        : make.Identifier(returnAssignTo.getSimpleName());
                                    dupeInvocation = make.Assignment(sel, dupeInvocation);
                                }
                            }

                            if (dupeInvocation != null)
                                newStatements.add(make.ExpressionStatement(dupeInvocation));

                            newStatements.addAll(parentStatements.subList(mdd.dupeEnd + 1, parentStatements.size()));

                            doReplaceInBlockCatchSingleStatement(copy, mdd.firstLeaf, newStatements);
                        }

                        introduceBag(doc).clear();
                        //handle duplicates end
                    }

                    Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);

                    if (isStatic) {
                        modifiers.add(Modifier.STATIC);
                    }

                    modifiers.addAll(access);

                    ModifiersTree mods = make.Modifiers(modifiers);
                    List<VariableTree> formalArguments = createVariables(copy, parameters);

                    if (formalArguments == null) {
                        return ; //XXX
                    }

                    List<ExpressionTree> thrown = typeHandleToTree(copy, thrownTypes);

                    if (thrownTypes == null) {
                        return; //XXX
                    }

                    List<TypeParameterTree> typeVars = new LinkedList<TypeParameterTree>();

                    for (TreePathHandle tph : IntroduceMethodFix.this.typeVars) {
                        typeVars.add((TypeParameterTree) tph.resolve(copy).getLeaf());
                    }

                    MethodTree method = make.Method(mods, name, returnTypeTree, typeVars, formalArguments, thrown, make.Block(methodStatements, false), null);

                    TreePath pathToClass = findClass(firstStatement);

                    assert pathToClass != null;
                    
                    Tree parent = findMethod(firstStatement).getLeaf();
                    ClassTree nueClass = null;
                    if (parent.getKind() == Kind.METHOD) {
                        nueClass = GeneratorUtils.insertMethodAfter(copy, pathToClass, method, (MethodTree) parent);
                    } else {
                        nueClass = GeneratorUtilities.get(copy).insertClassMember((ClassTree)pathToClass.getLeaf(), method);
                    }

                    copy.rewrite(pathToClass.getLeaf(), nueClass);
                    doReplaceInBlockCatchSingleStatement(copy, firstStatement, nueStatements);
                }
            }).commit();

            return null;
        }

    }

    private static void doReplaceInBlockCatchSingleStatement(WorkingCopy copy, TreePath firstLeaf, List<? extends StatementTree> newStatements) {
        TreeMaker make = copy.getTreeMaker();
        Tree toReplace = firstLeaf.getParentPath().getLeaf();
        Tree nueTree;

        switch (toReplace.getKind()) {
            case BLOCK:
                nueTree = make.Block(newStatements, ((BlockTree) toReplace).isStatic());
                break;
            case CASE:
                nueTree = make.Case(((CaseTree) toReplace).getExpression(), newStatements);
                break;
            default:
                assert CopyFinder.getStatements(firstLeaf).size() == 1 : CopyFinder.getStatements(firstLeaf).toString();
                assert newStatements.size() == 1 : newStatements.toString();
                toReplace = firstLeaf.getLeaf();
                nueTree = newStatements.get(0);
                break;
        }

        copy.rewrite(toReplace, nueTree);
    }

    private static final class IntroduceExpressionBasedMethodFix implements Fix {

        private final JavaSource js;

        private final TreePathHandle expression;
        private final List<TreePathHandle> parameters;
        private final Set<TypeMirrorHandle> thrownTypes;
        private final int duplicatesCount;
        private final List<TreePathHandle> typeVars;

        public IntroduceExpressionBasedMethodFix(JavaSource js, TreePathHandle expression, List<TreePathHandle> parameters, Set<TypeMirrorHandle> thrownTypes, int duplicatesCount, List<TreePathHandle> typeVars) {
            this.js = js;
            this.expression = expression;
            this.parameters = parameters;
            this.thrownTypes = thrownTypes;
            this.duplicatesCount = duplicatesCount;
            this.typeVars = typeVars;
        }

        public String getText() {
            return NbBundle.getMessage(IntroduceHint.class, "FIX_IntroduceMethod");
        }

        public String toString() {
            return "[IntroduceExpressionBasedMethodFix]"; // NOI18N
        }

        public ChangeInfo implement() throws Exception {
            JButton btnOk = new JButton( NbBundle.getMessage( IntroduceHint.class, "LBL_Ok" ) );
            JButton btnCancel = new JButton( NbBundle.getMessage( IntroduceHint.class, "LBL_Cancel" ) );
            IntroduceMethodPanel panel = new IntroduceMethodPanel("", duplicatesCount); //NOI18N
            panel.setOkButton( btnOk );
            String caption = NbBundle.getMessage(IntroduceHint.class, "CAP_IntroduceMethod");
            DialogDescriptor dd = new DialogDescriptor(panel, caption, true, new Object[] {btnOk, btnCancel}, btnOk, DialogDescriptor.DEFAULT_ALIGN, null, null);
            if (DialogDisplayer.getDefault().notify(dd) != btnOk) {
                return null;//cancel
            }
            final String name = panel.getMethodName();
            final Set<Modifier> access = panel.getAccess();
            final boolean replaceOther = panel.getReplaceOther();

            js.runModificationTask(new Task<WorkingCopy>() {
                public void run(WorkingCopy copy) throws Exception {
                    copy.toPhase(Phase.RESOLVED);

                    TreePath expression = IntroduceExpressionBasedMethodFix.this.expression.resolve(copy);
                    TypeMirror returnType = expression != null ? copy.getTrees().getTypeMirror(expression) : null;

                    if (expression == null || returnType == null) {
                        return ; //TODO...
                    }

                    returnType = Utilities.convertIfAnonymous(Utilities.resolveCapturedType(copy, returnType));

                    final TreeMaker make = copy.getTreeMaker();
                    Tree returnTypeTree = make.Type(returnType);
                    List<VariableElement> parameters = resolveVariables(copy, IntroduceExpressionBasedMethodFix.this.parameters);
                    List<ExpressionTree> realArguments = realArguments(make, parameters);

                    ExpressionTree invocation = make.MethodInvocation(Collections.<ExpressionTree>emptyList(), make.Identifier(name), realArguments);

                    Scope s = copy.getTrees().getScope(expression);
                    boolean isStatic = copy.getTreeUtilities().isStaticContext(s);

                    Set<Modifier> modifiers = EnumSet.noneOf(Modifier.class);

                    if (isStatic) {
                        modifiers.add(Modifier.STATIC);
                    }

                    modifiers.addAll(access);

                    ModifiersTree mods = make.Modifiers(modifiers);
                    List<VariableTree> formalArguments = createVariables(copy, parameters);

                    if (formalArguments == null) {
                        return ; //XXX
                    }

                    List<ExpressionTree> thrown = typeHandleToTree(copy, thrownTypes);

                    if (thrownTypes == null) {
                        return ; //XXX
                    }

                    List<StatementTree> methodStatements = new LinkedList<StatementTree>();

                    methodStatements.add(make.Return((ExpressionTree) expression.getLeaf()));

                    List<TypeParameterTree> typeVars = new LinkedList<TypeParameterTree>();

                    for (TreePathHandle tph : IntroduceExpressionBasedMethodFix.this.typeVars) {
                        typeVars.add((TypeParameterTree) tph.resolve(copy).getLeaf());
                    }

                    MethodTree method = make.Method(mods, name, returnTypeTree, typeVars, formalArguments, thrown, make.Block(methodStatements, false), null);
                    TreePath pathToClass = findClass(expression);

                    assert pathToClass != null;

                    Tree parent = findMethod(expression).getLeaf();
                    ClassTree nueClass = null;
                    if (parent.getKind() == Kind.METHOD) {
                        nueClass = GeneratorUtils.insertMethodAfter(copy, pathToClass, method, (MethodTree) parent);
                    } else {
                        nueClass = GeneratorUtilities.get(copy).insertClassMember((ClassTree)pathToClass.getLeaf(), method);
                    }
                    
                    copy.rewrite(pathToClass.getLeaf(), nueClass);

                    Tree parentTree = expression.getParentPath().getLeaf();
                    Tree nueParent = copy.getTreeUtilities().translate(parentTree, Collections.singletonMap(expression.getLeaf(), invocation));
                    copy.rewrite(parentTree, nueParent);

                    if (replaceOther) {
                        //handle duplicates
                        Document doc = copy.getDocument();

                        for (MethodDuplicateDescription mdd : CopyFinder.computeDuplicatesAndRemap(copy, Collections.singletonList(expression), new TreePath(copy.getCompilationUnit()), parameters, new AtomicBoolean())) {
                            int startOff = (int) copy.getTrees().getSourcePositions().getStartPosition(copy.getCompilationUnit(), mdd.firstLeaf.getLeaf());
                            int endOff = (int) copy.getTrees().getSourcePositions().getEndPosition(copy.getCompilationUnit(), mdd.firstLeaf.getLeaf());

                            introduceBag(doc).clear();
                            introduceBag(doc).addHighlight(startOff, endOff, DUPE);

                            String title = NbBundle.getMessage(IntroduceHint.class, "TTL_DuplicateMethodPiece");
                            String message = NbBundle.getMessage(IntroduceHint.class, "MSG_DuplicateMethodPiece");

                            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(message, title, NotifyDescriptor.YES_NO_OPTION);

                            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.YES_OPTION) {
                                continue;
                            }

                            //XXX:
                            List<Union2<VariableElement, TreePath>> dupeParameters = new LinkedList<Union2<VariableElement, TreePath>>();

                            for (VariableElement ve : parameters) {
                                if (mdd.variablesRemapToTrees.containsKey(ve)) {
                                    dupeParameters.add(Union2.<VariableElement, TreePath>createSecond(mdd.variablesRemapToTrees.get(ve)));
                                } else {
                                    dupeParameters.add(Union2.<VariableElement, TreePath>createFirst(ve));
                                }
                            }

                            List<ExpressionTree> dupeRealArguments = realArgumentsForTrees(make, dupeParameters);
                            ExpressionTree dupeInvocation = make.MethodInvocation(Collections.<ExpressionTree>emptyList(), make.Identifier(name), dupeRealArguments);

                            copy.rewrite(mdd.firstLeaf.getLeaf(), dupeInvocation);
                        }

                        introduceBag(doc).clear();
                        //handle duplicates end
                    }
                }
            }).commit();

            return null;
        }

    }

    public static final class HLFImpl implements HighlightsLayerFactory {

        public HighlightsLayer[] createLayers(Context context) {
            return new HighlightsLayer[] {
                HighlightsLayer.create(IntroduceHint.class.getName(), ZOrder.CARET_RACK, true, introduceBag(context.getDocument())),
            };
        }

    }
}
