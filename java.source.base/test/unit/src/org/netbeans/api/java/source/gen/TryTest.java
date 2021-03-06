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
import com.sun.source.util.TreeScanner;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.Preferences;
import javax.lang.model.element.Modifier;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.CodeStyle.BracePlacement;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.*;
import org.netbeans.api.java.source.TestUtilities;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.java.ui.FmtOptions;
import org.openide.filesystems.FileUtil;

/**
 * Test different modifications in try/catch/finally section.
 * 
 * @author Pavel Flaska
 */
public class TryTest extends GeneratorTestMDRCompat {
    
    /** Creates a new instance of TryTest */
    public TryTest(String name) {
        super(name);
    }
    
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTestSuite(TryTest.class);
        return suite;
    }

    /**
     * Renames variable in try body.
     */
    public void testRenameInTryBody() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile,
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try {\n" +
            "            File f = new File(\"auto\");\n" +
            "            FileInputStream fis = new FileInputStream(f);\n" +
            "        } catch (FileNotFoundException ex) {\n" +
            "        } catch (NullPointerException ex) {\n" +
            "        }\n" +
            "    }\n" +
            "}\n"
            );
        String golden =
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try {\n" +
            "            File f = new File(\"auto\");\n" +
            "            FileInputStream input = new FileInputStream(f);\n" +
            "        } catch (FileNotFoundException ex) {\n" +
            "        } catch (NullPointerException ex) {\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();

                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                TryTree tt = (TryTree) method.getBody().getStatements().get(0);
                VariableTree var = (VariableTree) tt.getBlock().getStatements().get(1);
                workingCopy.rewrite(var, make.setLabel(var, "input"));
            }

        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }

    /**
     * #96551: Incorrectly formatted catch
     */
    public void testInsertCatchClause() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile,
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try {\n" +
            "            File f = new File(\"auto\");\n" +
            "            FileInputStream fis = new FileInputStream(f);\n" +
            "        } catch (FileNotFoundException ex) {\n" +
            "        }\n" +
            "    }\n" +
            "}\n"
            );
        String golden =
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try {\n" +
            "            File f = new File(\"auto\");\n" +
            "            FileInputStream fis = new FileInputStream(f);\n" +
            "        } catch (NullPointerException npe) {\n" +
            "        } catch (FileNotFoundException ex) {\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();

                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                TryTree tt = (TryTree) method.getBody().getStatements().get(0);
                CatchTree njuKec = make.Catch(make.Variable(
                        make.Modifiers(Collections.<Modifier>emptySet()),
                        "npe",
                        make.Identifier("NullPointerException"),
                        null),
                    make.Block(Collections.<StatementTree>emptyList(), false)
                );
                workingCopy.rewrite(tt, make.insertTryCatch(tt, 0, njuKec));
            }

        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }

    /**
     * #96551: Incorrectly formatted catch
     */
    public void testAddCatchClause() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile,
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try {\n" +
            "            File f = new File(\"auto\");\n" +
            "            FileInputStream fis = new FileInputStream(f);\n" +
            "        } catch (FileNotFoundException ex) {\n" +
            "        }\n" +
            "    }\n" +
            "}\n"
            );
        String golden =
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try {\n" +
            "            File f = new File(\"auto\");\n" +
            "            FileInputStream fis = new FileInputStream(f);\n" +
            "        } catch (FileNotFoundException ex) {\n" +
            "        } catch (NullPointerException npe) {\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();

                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                TryTree tt = (TryTree) method.getBody().getStatements().get(0);
                CatchTree njuKec = make.Catch(make.Variable(
                        make.Modifiers(Collections.<Modifier>emptySet()),
                        "npe",
                        make.Identifier("NullPointerException"),
                        null),
                    make.Block(Collections.<StatementTree>emptyList(), false)
                );
                workingCopy.rewrite(tt, make.addTryCatch(tt, njuKec));
            }

        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }
    
    public void testFF() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile,
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try {\n" +
            "            System.err.println(0);\n" +
            "        } catch (FileNotFoundException ex) {\n" +
            "        }\n" +
            "    }\n" +
            "}\n"
            );
        String golden =
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try {\n" +
            "            System.err.println(0);\n" +
            "        } catch (FileNotFoundException ex) {\n" +
            "        } finally {\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();

                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                TryTree tt = (TryTree) method.getBody().getStatements().get(0);
                TryTree nue = make.Try(tt.getBlock(), tt.getCatches(), make.Block(Collections.<StatementTree>emptyList(), false));
                workingCopy.rewrite(tt, nue);
            }

        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }

    public void testWithResource1() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile,
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        InputStream in = new FileInputStream(\"\");\n" +
            "    }\n" +
            "}\n"
            );
        String golden =
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try (InputStream in = new FileInputStream(\"\")) {\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();

                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                VariableTree vt = (VariableTree) method.getBody().getStatements().get(0);
                TryTree nue = make.Try(Collections.singletonList(vt), make.Block(Collections.<StatementTree>emptyList(), false), Collections.<CatchTree>emptyList(), null);
                workingCopy.rewrite(vt, nue);
            }

        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }

    public void testWithResource2() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile,
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "    }\n" +
            "}\n"
            );
        String golden =
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try (InputStream in = null) {\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();

                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                VariableTree vt = make.Variable(make.Modifiers(EnumSet.noneOf(Modifier.class)), "in", make.Identifier("InputStream"), make.Literal(null));
                TryTree nue = make.Try(Collections.singletonList(vt), make.Block(Collections.<StatementTree>emptyList(), false), Collections.<CatchTree>emptyList(), null);
                workingCopy.rewrite(method.getBody(), make.Block(Collections.singletonList(nue), false));
            }

        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }

    public void testWithResource3() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile,
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try {\n" +
            "        } catch (IOException e) {}\n" +
            "    }\n" +
            "}\n"
            );
        String golden =
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try (InputStream in = null) {\n" +
            "        } catch (IOException e) {}\n" +
            "    }\n" +
            "}\n";
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();

                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                VariableTree vt = make.Variable(make.Modifiers(EnumSet.noneOf(Modifier.class)), "in", make.Identifier("InputStream"), make.Literal(null));
                TryTree orig = (TryTree) method.getBody().getStatements().get(0);
                TryTree nue = make.Try(Collections.singletonList(vt), orig.getBlock(), orig.getCatches(), orig.getFinallyBlock());
                workingCopy.rewrite(orig, nue);
            }

        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }

    public void testWithResource4() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile,
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try (InputStream in = new FileInputStream(\"\")) {\n" +
            "        } catch (IOException e) {}\n" +
            "    }\n" +
            "}\n"
            );
        String golden =
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try {\n" +
            "        } catch (IOException e) {}\n" +
            "    }\n" +
            "}\n";
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {
            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.PARSED); //for RESOLVED, the 1.7 runtime (java.lang.AutoCloseable) would be needed
                TreeMaker make = workingCopy.getTreeMaker();

                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(0);
                TryTree orig = (TryTree) method.getBody().getStatements().get(0);
                TryTree nue = make.Try(Collections.<Tree>emptyList(), orig.getBlock(), orig.getCatches(), orig.getFinallyBlock());
                workingCopy.rewrite(orig, nue);
            }

        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }

    public void testWithResourceRename() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile,
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try (InputStream in = new FileInputStream(\"\")) {\n" +
            "        } catch (IOException e) {}\n" +
            "    }\n" +
            "}\n"
            );
        String golden =
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try (InputStream ni = new FileInputStream(\"\")) {\n" +
            "        } catch (IOException e) {}\n" +
            "    }\n" +
            "}\n";
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {
            public void run(final WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED); //for RESOLVED, the 1.7 runtime (java.lang.AutoCloseable) would be needed
                final TreeMaker make = workingCopy.getTreeMaker();

                new TreeScanner<Void, Void>() {
                    @Override
                    public Void visitVariable(VariableTree node, Void p) {
                        if (node.getName().contentEquals("in")) {
                            workingCopy.rewrite(node, make.setLabel(node, "ni"));
                        }
                        return super.visitVariable(node, p);
                    }
                }.scan(workingCopy.getCompilationUnit(), null);
            }

        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }

    public void test200708() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile,
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try\n" +
            "        {\n" +
            "            File f = new File(\"auto\");\n" +
            "            FileInputStream fis = new FileInputStream(f);\n" +
            "        }\n" +
            "        catch (FileNotFoundException ex)\n" +
            "        {\n" +
            "        }\n" +
            "        catch (NullPointerException ex)\n" +
            "        {\n" +
            "        }\n" +
            "    }\n" +
            "}\n"
            );
        String golden =
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try\n" +
            "        {\n" +
            "            File f = new File(\"auto\");\n" +
            "            FileInputStream fis = new FileInputStream(f);\n" +
            "        }\n" +
            "        catch (FileNotFoundException | NullPointerException ex)\n" +
            "        {\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();

                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                TryTree tt = (TryTree) method.getBody().getStatements().get(0);
                CatchTree ct = tt.getCatches().get(0);
                workingCopy.rewrite(ct.getParameter().getType(), make.UnionType(Arrays.asList(make.Identifier("FileNotFoundException"), make.Identifier("NullPointerException"))));
                workingCopy.rewrite(tt, make.removeTryCatch(tt, 1));
            }

        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }
    
    public void test211174() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile,
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.InputStream;\n" +
            "\n" +
            "public class ConnectionFilter\n" +
            "{\n" +
            "\n" +
            "    public void m()\n" +
            "    {\n" +
            "        InputStream in = null;\n" +
            "\n" +
            "        in.read();\n" +
            "\n" +
            "        try\n" +
            "        {\n" +
            "            System.err.println(\"x\");\n" +
            "        }\n" +
            "        finally\n" +
            "        {\n" +
            "            in.close();\n" +
            "        }\n" +
            "    }\n" +
            "}\n"
            );
        String golden =
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.InputStream;\n" +
            "\n" +
            "public class ConnectionFilter\n" +
            "{\n" +
            "\n" +
            "    public void m()\n" +
            "    {\n" +
            "        try\n" +
            "        {\n" +
            "            InputStream in = null;\n" +
//            "\n" + //TODO: should be here
            "            in.read();\n" +
//            "\n" + //TODO: should be here
            "            try\n" +
            "            {\n" +
            "                System.err.println(\"x\");\n" +
            "            }\n" +
            "            finally\n" +
            "            {\n" +
            "                in.close();\n" +
            "            }\n" +
            "        }\n" +
            "        catch (Exception ex)\n" +
            "        {\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        
        Map<String, String> adjustPreferences = new HashMap<String, String>();

        adjustPreferences.put(FmtOptions.placeFinallyOnNewLine, "true");
        adjustPreferences.put(FmtOptions.placeCatchOnNewLine, "true");
        adjustPreferences.put(FmtOptions.classDeclBracePlacement, BracePlacement.NEW_LINE.name());
        adjustPreferences.put(FmtOptions.methodDeclBracePlacement, BracePlacement.NEW_LINE.name());
        adjustPreferences.put(FmtOptions.otherBracePlacement, BracePlacement.NEW_LINE.name());
        Preferences preferences = MimeLookup.getLookup(JavaTokenId.language().mimeType()).lookup(Preferences.class);
        Map<String, String> origValues = new HashMap<String, String>();
        for (String key : adjustPreferences.keySet()) {
            origValues.put(key, preferences.get(key, null));
        }
        setValues(preferences, adjustPreferences);
        
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();

                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                TryTree nueTry = make.Try(make.Block(new ArrayList<StatementTree>(method.getBody().getStatements()), false),
                                 Collections.singletonList(make.Catch(make.Variable(make.Modifiers(EnumSet.noneOf(Modifier.class)),
                                                                                    "ex",
                                                                                    make.Type("java.lang.Exception"),
                                                                                    null),
                                                                      make.Block(Collections.<StatementTree>emptyList(), false))),
                                 null);
                workingCopy.rewrite(method.getBody(), make.Block(Collections.singletonList(nueTry), false));
            }

        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        // avoid affecting following test if assert check fails
        setValues(preferences, origValues);
        assertEquals(golden, res);
    }

    public void testWrapTryInTry() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile,
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try {\n" +
            "            File f = new File(\"auto\");\n" +
            "            FileInputStream fis = new FileInputStream(f);\n" +
            "        } catch (FileNotFoundException ex) {\n" +
            "        } finally {\n" +
            "        }\n" +
            "    }\n" +
            "}\n"
            );
        String golden =
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try {\n" +
            "            try {\n" +
            "                File f = new File(\"auto\");\n" +
            "                FileInputStream fis = new FileInputStream(f);\n" +
            "            } catch (FileNotFoundException ex) {\n" +
            "            } finally {\n" +
            "            }\n" +
            "        } finally {\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();

                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                TryTree tt = (TryTree) method.getBody().getStatements().get(0);
                workingCopy.rewrite(tt, make.Try(make.Block(Collections.singletonList(tt), false), Collections.<CatchTree>emptyList(), make.Block(Collections.<StatementTree>emptyList(), false)));
            }

        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }
    
    public void testAddFirstCatchToTWR() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile,
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try (InputStream in = new FileInputStream(\"\")) {\n" +
            "        }\n" +
            "    }\n" +
            "}\n"
            );
        String golden =
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try (InputStream in = new FileInputStream(\"\")) {\n" +
            "        } catch (Exception ex) {\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();

                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                TryTree tt = (TryTree) method.getBody().getStatements().get(0);
                TryTree nue = make.addTryCatch(tt, make.Catch(make.Variable(make.Modifiers(EnumSet.noneOf(Modifier.class)), "ex", make.Type("java.lang.Exception"), null), make.Block(Collections.<StatementTree>emptyList(), false)));
                workingCopy.rewrite(tt, nue);
            }

        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }
    
    public void testAddNewResourceToTWR() throws Exception {
        testFile = new File(getWorkDir(), "Test.java");
        TestUtilities.copyStringToFile(testFile,
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try (InputStream in = new FileInputStream(\"\"); OutputStream out = new FileOutputStream(\"\")) {\n" +
            "        }\n" +
            "    }\n" +
            "}\n"
            );
        String golden =
            "package hierbas.del.litoral;\n" +
            "\n" +
            "import java.io.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public void taragui() {\n" +
            "        try (InputStream in = new FileInputStream(\"\"); OutputStream out = new FileOutputStream(\"\"); InputStream other = new FileInputStream(\"\")) {\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        JavaSource testSource = JavaSource.forFileObject(FileUtil.toFileObject(testFile));
        Task<WorkingCopy> task = new Task<WorkingCopy>() {

            public void run(WorkingCopy workingCopy) throws java.io.IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();

                ClassTree clazz = (ClassTree) workingCopy.getCompilationUnit().getTypeDecls().get(0);
                MethodTree method = (MethodTree) clazz.getMembers().get(1);
                TryTree tt = (TryTree) method.getBody().getStatements().get(0);
                List<Tree> resources = new ArrayList<Tree>(tt.getResources());
                resources.add(make.Variable(make.Modifiers(EnumSet.noneOf(Modifier.class)), "other", make.Identifier("InputStream"), make.Identifier("new FileInputStream(\"\")")));
                workingCopy.rewrite(tt, make.Try(resources, tt.getBlock(), tt.getCatches(), tt.getFinallyBlock()));
            }

        };
        testSource.runModificationTask(task).commit();
        String res = TestUtilities.copyFileToString(testFile);
        System.err.println(res);
        assertEquals(golden, res);
    }
    
    private void setValues(Preferences p, Map<String, String> values) {
        for (Entry<String, String> e : values.entrySet()) {
            if (e.getValue() != null) {
                p.put(e.getKey(), e.getValue());
            } else {
                p.remove(e.getKey());
            }
        }
    }
    
    String getGoldenPckg() {
        return "";
    }

    String getSourcePckg() {
        return "";
    }

}
