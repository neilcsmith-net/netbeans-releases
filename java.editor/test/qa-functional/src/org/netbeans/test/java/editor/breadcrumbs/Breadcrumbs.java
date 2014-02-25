/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.test.java.editor.breadcrumbs;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import javax.swing.JPanel;
import junit.framework.Test;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.operators.JComponentOperator;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.test.java.editor.lib.JavaEditorTestCase;
import org.openide.util.Exceptions;

/**
 *
 * @author jprox
 */
public class Breadcrumbs extends JavaEditorTestCase {

    public Breadcrumbs(String testMethodName) {
        super(testMethodName);
    }

    private EditorOperator oper = null;

    private static final String TEST_FILE = "Breadcrumbs";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        openProject("java_editor_test");
        openSourceFile("org.netbeans.test.java.editor.breadcrumbs", TEST_FILE);
        oper = new EditorOperator(TEST_FILE);
    }

    @Override
    protected void tearDown() throws Exception {
        if (oper != null) {
            oper.closeDiscard();
        }
        super.tearDown();
    }

    public static Test suite() {
        return NbModuleSuite.create(
                NbModuleSuite.createConfiguration(Breadcrumbs.class)                
                .enableModules(".*")
                .clusters(".*"));
    }

    public void testCompilationUnit() {
        assertEquals("[]", getBreadcrumbsAt(10, 1));
    }
    public void testClass() {
        assertEquals("[Breadcrumbs]", getBreadcrumbsAt(16, 28));
    }
    public void testField() {
        assertEquals("[Breadcrumbs, x]", getBreadcrumbsAt(19, 10));
    }
    public void testMethod() {
        assertEquals("[Breadcrumbs, method]", getBreadcrumbsAt(22, 9));
    }
    public void testFor() {
        assertEquals("[Breadcrumbs, method, for <font color=#707070>(int i = 0; i &lt; 10; i++)</font>]", getBreadcrumbsAt(23, 39));
    }
    public void testResource() {
        assertEquals("[Breadcrumbs, method, try, rd]", getBreadcrumbsAt(26, 48));
    }
    public void testTry() {
        assertEquals("[Breadcrumbs, method, try]", getBreadcrumbsAt(27, 13));
    }
    public void testCatch() {
        assertEquals("[Breadcrumbs, method, try, catch <font color=#707070>Exception ex</font>]", getBreadcrumbsAt(29, 13));
    }
    public void testFinally() {
        assertEquals("[Breadcrumbs, method, try, finally]", getBreadcrumbsAt(32, 13));
    }
    public void testWhile() {
        assertEquals("[Breadcrumbs, method, while <font color=#707070>(x > 0)</font>]", getBreadcrumbsAt(36, 13));
    }
    public void testDoWhile() {
        assertEquals("[Breadcrumbs, method, do ... while <font color=#707070>(x &lt; 10)</font>]", getBreadcrumbsAt(41, 9));
    }
    public void testIf() {
        assertEquals("[Breadcrumbs, method, if <font color=#707070>(x == 10)</font>]", getBreadcrumbsAt(45, 1));
    }
    public void testIfElse() {
        assertEquals("[Breadcrumbs, method, if <font color=#707070>(x == 10)</font> else]", getBreadcrumbsAt(47, 1));
    }
    public void testIfElseIf() {
        assertEquals("[Breadcrumbs, method, if <font color=#707070>(x == 10)</font> else, if <font color=#707070>(x == 2)</font>]", getBreadcrumbsAt(49, 1));
    }
    public void testFor15() {
        assertEquals("[Breadcrumbs, method, for <font color=#707070>(Object object : new String[]{&quot;&quot;})</font>]", getBreadcrumbsAt(53, 1));
    }
    public void testSynchronized() {
        assertEquals("[Breadcrumbs, method, synchronized <font color=#707070>(this)</font>]", getBreadcrumbsAt(56, 1));
    }
    public void testAnonymousClass() {
        assertEquals("[Breadcrumbs, method, Runnable]", getBreadcrumbsAt(60, 1));
    }
    public void testAnonymousClassMethod() {
        assertEquals("[Breadcrumbs, method, Runnable, run]", getBreadcrumbsAt(63, 1));
    }
    public void testSwitch() {
        assertEquals("[Breadcrumbs, method, switch <font color=#707070>(x)</font>]", getBreadcrumbsAt(67, 1));
    }
    public void testCase() {
        assertEquals("[Breadcrumbs, method, switch <font color=#707070>(x)</font>, case <font color=#707070>1:</font>]", getBreadcrumbsAt(70, 1));
    }
    public void testDefaultCase() {
        assertEquals("[Breadcrumbs, method, switch <font color=#707070>(x)</font>, default:]", getBreadcrumbsAt(73, 1));
    }
    public void testInner() {
        assertEquals("[Breadcrumbs, Inner]", getBreadcrumbsAt(79, 1));
    }
    public void testInnerMethod() {
        assertEquals("[Breadcrumbs, Inner, innerMethod]", getBreadcrumbsAt(82, 1));
    }
    public void testEnum() {
        assertEquals("[Breadcrumbs, E]", getBreadcrumbsAt(88, 1));
    }
    public void testEnumConstantBody() {
        assertEquals("[Breadcrumbs, E, A, E]", getBreadcrumbsAt(90, 1));
    }
    public void testEnumConstantMethod() {
        assertEquals("[Breadcrumbs, E, A, E, m]", getBreadcrumbsAt(92, 1));
    }
    public void testEnumConstant() {
        assertEquals("[Breadcrumbs, E, B]", getBreadcrumbsAt(95, 10));
    }
    
    

    private String getBreadcrumbsAt(int row, int col) {
        oper.setCaretPosition(row, col);
        new EventTool().waitNoEvent(1000);
        Container container = getBreadcrumbsContainer();
        if (container != null) {

            String[] nodesAsArray = getNodesAsArray(container.getComponents()[0]);
            return Arrays.toString(nodesAsArray);
        } else {
            fail("Breadcrumbs sidebar not found");
        }
        return null;
    }

    private Container getBreadcrumbsContainer() {
        Container container = JComponentOperator.findContainer(oper.getWindow(), new ComponentChooser() {

            @Override
            public boolean checkComponent(Component comp) {                
                return comp.getClass().getName().contains("SideBarFactoryImpl$SideBar");
            }

            @Override
            public String getDescription() {
                return "Breadcrumbs Sidebar";
            }
        });
        return container;
    }

    private String[] getNodesAsArray(Object component) {
        try {
            Class<?> breadcrumbComponent = Class.forName("org.netbeans.modules.editor.breadcrumbs.BreadCrumbComponent");
            Field nodeFiled = breadcrumbComponent.getDeclaredField("nodes");
            nodeFiled.setAccessible(true);
            Object[] nodes = (Object[]) nodeFiled.get(component);
            String[] res = new String[nodes.length];
            Class<?> breadcrumbNode = Class.forName("org.netbeans.modules.editor.breadcrumbs.BreadCrumbsNodeImpl");
            for (int i = 0; i < nodes.length; i++) {
                Object node = nodes[i];
                Method declaredMethod = breadcrumbNode.getDeclaredMethod("getHtmlDisplayName");
                declaredMethod.setAccessible(true);
                String invoke = (String) declaredMethod.invoke(node);
                res[i] = invoke;
            }
            return res;
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            fail(ex);
        }
        return null;
    }

}
