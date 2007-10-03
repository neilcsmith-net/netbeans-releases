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
package org.netbeans.api.java.source.gen;

import com.sun.source.tree.*;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import java.io.File;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TestUtilities;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.junit.NbTestSuite;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Pavel Flaska
 */
public class MethodBodyTest extends GeneratorTest {
    
    /** Creates a new instance of MethodBodyTest */
    public MethodBodyTest(String name) {
        super(name);
    }
    
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTestSuite(MethodBodyTest.class);
//        suite.addTest(new MethodBodyTest("testAddFirstStatement"));
//        suite.addTest(new MethodBodyTest("testAddBodyText"));
//        suite.addTest(new MethodBodyTest("testAddVarDecl"));
//        suite.addTest(new MethodBodyTest("testReplaceConstructorBody"));
//        suite.addTest(new MethodBodyTest("testSwitchStatement"));
        return suite;
    }

    /**
     * Add first method body statement
     */
    public void testAddFirstStatement() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile, 
            "package personal;\n" +
            "\n" +
            "public class Test {\n" +
            "    public Object method() {\n" +
            "    }\n" +
            "}\n");
        
         String golden = 
            "package personal;\n" +
            "\n" +
            "public class Test {\n" +
            "    public Object method() {\n" +
            "        System.out.println(\"test\");\n" +
            "    }\n" +
            "}\n";
                 
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();
                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                BlockTree block = method.getBody();
                ExpressionStatementTree est = make.ExpressionStatement(
                    make.MethodInvocation(
                        Collections.<ExpressionTree>emptyList(),
                        make.MemberSelect(
                            make.MemberSelect(
                                make.Identifier("System"),
                                "out"
                            ),
                            "println"
                        ),
                        Collections.<ExpressionTree>singletonList(
                            make.Literal("test")
                        )
                    )
                );
                workingCopy.rewrite(block, make.addBlockStatement(block, est));
            }
            
        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }
    
    /**
     * Add method body as a text
     */
    public void testAddBodyText() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile, 
            "package personal;\n" +
            "\n" +
            "public class Test {\n" +
            "    public Object method() {\n" +
            "    }\n" +
            "}\n");
        
         String golden = 
            "package personal;\n" +
            "\n" +
            "public class Test {\n" +
            "    public Object method() {\n" +
            "        System.out.println(\"test\");\n" +
            "    }\n" +
            "}\n";
                 
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();
                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                BlockTree newBody = make.createMethodBody(method, "{ System.out.println(\"test\"); }");
                workingCopy.rewrite(method.getBody(), newBody);
            }
            
        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }
    
    /**
     * "Map env = new HashMap();"
     */
    public void testAddVarDecl() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile, 
            "package personal;\n" +
            "\n" +
            "public class Test {\n" +
            "    public Object method() {\n" +
            "    }\n" +
            "}\n");
        
         String golden = 
            "package personal;\n" +
            "\n" +
            "import java.util.HashMap;\n" +
            "import java.util.Map;\n" +
            "\n" +
            "public class Test {\n" +
            "    public Object method() {\n" +
            "        Map env = new HashMap();\n" +
            "    }\n" +
            "}\n";
                 
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker treeMaker = workingCopy.getTreeMaker();
                TypeElement hashMapClass = workingCopy.getElements().getTypeElement("java.util.HashMap"); // NOI18N
                ExpressionTree hashMapEx = treeMaker.QualIdent(hashMapClass);
                TypeElement mapClass = workingCopy.getElements().getTypeElement("java.util.Map");// NOI18N
                ExpressionTree mapEx = treeMaker.QualIdent(mapClass);
                NewClassTree mapConstructor = treeMaker.NewClass(
                        null,
                        Collections.<ExpressionTree>emptyList(),
                        hashMapEx,
                        Collections.<ExpressionTree>emptyList(), null
                );
                VariableTree vt = treeMaker.Variable( treeMaker.Modifiers(
                        Collections.<Modifier>emptySet(),
                        Collections.<AnnotationTree>emptyList()
                        ), "env", mapEx, mapConstructor
                );
                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                workingCopy.rewrite(method.getBody(), treeMaker.addBlockStatement(method.getBody(), vt));
            }
            
        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }
    
    /**
     * diff switch statement
     */
    public void testSwitchStatement() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile, 
            "package personal;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void method() {\n" +
            "        int i = 3;\n" +
            "        switch (i) {\n" +
            "            case 1: System.err.println(); break;\n" +
            "            default: break;\n" +
            "        }\n" + 
            "    }\n" +
            "}\n");
        
         String golden = 
            "package personal;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void method() {\n" +
            "        int i = 3;\n" +
            "        switch (i) {\n" +
            "            case 1: System.err.println(); break;\n" +
            "            case 2: System.err.println(); break;\n" +
            "            default:  break;\n" +
            "        }\n" + 
            "    }\n" +
            "}\n";
                 
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker treeMaker = workingCopy.getTreeMaker();
                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                SwitchTree switchStatement = (SwitchTree) method.getBody().getStatements().get(1);
                
                List<CaseTree> cases = new LinkedList<CaseTree>();
                
                cases.add(treeMaker.Case(treeMaker.Literal(1), switchStatement.getCases().get(0).getStatements()));
                cases.add(treeMaker.Case(treeMaker.Literal(2), switchStatement.getCases().get(0).getStatements()));
                cases.add(treeMaker.Case(null, Collections.singletonList(treeMaker.Break(null))));
                
                workingCopy.rewrite(switchStatement, treeMaker.Switch(switchStatement.getExpression(), cases));
            }
            
        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }
    
    public void test117054a() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile, 
            "package personal;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void method() {\n" +
            "        new Runnable() {}.\n" + 
            "    }\n" +
            "}\n");
        
         String golden = 
            "package personal;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void method() {\n" +
            "        new Runnable() {\n" + 
            "\n" + 
            "            public void run() {\n" +
            "            }\n" +
            "        }.\n" +
            "    }\n" +
            "}\n";
                 
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker treeMaker = workingCopy.getTreeMaker();
                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                final NewClassTree[] nctFin = new NewClassTree[1];
                
                new TreeScanner() {
                    @Override
                    public Object visitNewClass(NewClassTree node, Object p) {
                        nctFin[0] = node;
                        return null;
                    }
                }.scan(method.getBody().getStatements().get(0), null);
                
                assertNotNull(nctFin[0]);
                
                NewClassTree nct = nctFin[0];
                ModifiersTree mods = treeMaker.Modifiers(EnumSet.of(Modifier.PUBLIC));
                Tree returnType = treeMaker.Type(workingCopy.getTypes().getNoType(TypeKind.VOID));
                MethodTree nueMethod = treeMaker.Method(mods, "run", returnType, Collections.<TypeParameterTree>emptyList(), Collections.<VariableTree>emptyList(), Collections.<ExpressionTree>emptyList(), "{}", null);
                
                workingCopy.rewrite(nct.getClassBody(), treeMaker.addClassMember(nct.getClassBody(), nueMethod));
            }
            
        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }
    
    public void test117054b() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile, 
            "package personal;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void method() {\n" +
            "        Runnable r = new Runnable() {}.\n" + 
            "    }\n" +
            "}\n");
        
         String golden = 
            "package personal;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void method() {\n" +
            "        Runnable r = new Runnable() {\n" + 
            "\n" + 
            "            public void run() {\n" +
            "            }\n" +
            "        }.\n" +
            "    }\n" +
            "}\n";
                 
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker treeMaker = workingCopy.getTreeMaker();
                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                final NewClassTree[] nctFin = new NewClassTree[1];
                
                new TreeScanner() {
                    @Override
                    public Object visitNewClass(NewClassTree node, Object p) {
                        nctFin[0] = node;
                        return null;
                    }
                }.scan(method.getBody().getStatements().get(0), null);
                
                assertNotNull(nctFin[0]);
                
                NewClassTree nct = nctFin[0];
                ModifiersTree mods = treeMaker.Modifiers(EnumSet.of(Modifier.PUBLIC));
                Tree returnType = treeMaker.Type(workingCopy.getTypes().getNoType(TypeKind.VOID));
                MethodTree nueMethod = treeMaker.Method(mods, "run", returnType, Collections.<TypeParameterTree>emptyList(), Collections.<VariableTree>emptyList(), Collections.<ExpressionTree>emptyList(), "{}", null);
                
                workingCopy.rewrite(nct.getClassBody(), treeMaker.addClassMember(nct.getClassBody(), nueMethod));
            }
            
        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }
    
    /**
     * Replace constructor body, lhasik's test-case #111769
     */
    public void XtestReplaceConstructorBody() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile, 
            "package personal;\n" +
            "\n" +
            "public class Test {\n" +
            "}\n");
        
         String golden = 
            "package personal;\n" +
            "\n" +
            "public class Test {\n" +
            "    public Test() {\n" +
            "        super(1, \"Tester\");\n" +
            "    }\n" +
            "}\n";
                 
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();
                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(0);
                TreeUtilities treeUtils = workingCopy.getTreeUtilities();
                Tree newBlock = treeUtils.parseStatement("{ super(1, \"Tester\"); }", new SourcePositions[1]);
                workingCopy.rewrite(method.getBody(), newBlock);
            }
        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }
    
    String getGoldenPckg() {
        return "";
    }

    String getSourcePckg() {
        return "";
    }

}
