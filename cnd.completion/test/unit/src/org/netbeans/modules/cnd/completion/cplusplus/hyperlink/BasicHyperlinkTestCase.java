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

package org.netbeans.modules.cnd.completion.cplusplus.hyperlink;

/**
 *
 * @author Vladimir Voskresensky
 */
public class BasicHyperlinkTestCase extends HyperlinkBaseTestCase {

    public BasicHyperlinkTestCase(String testName) {
        super(testName);
    }

    public void testVarInFunWithInitalization() throws Exception {
        performTest("main.c", 19, 10, "main.c", 19, 5); // iiii in int iiii = fun(null, null);
    }
    
    public void testParamWithoutSpace() throws Exception {
        performTest("main.c", 18, 17, "main.c", 18, 10); // aaa in void foo(char* aaa, char**bbb)
        performTest("main.c", 18, 28, "main.c", 18, 21); // bbb in void foo(char* aaa, char**bbb)
    }
    
    public void testFileLocalVariable() throws Exception {
        performTest("main.c", 15, 12, "main.c", 15, 1); // VALUE in const int VALUE = 10;
        performTest("main.c", 16, 30, "main.c", 15, 1); // VALUE in const int VALUE_2 = 10 + VALUE;
        performTest("main.c", 16, 12, "main.c", 16, 1); // VALUE_2 in const int VALUE_2 = 10 + VALUE;
    }
    
    public void testFuncParamUsage() throws Exception {
        performTest("main.c", 3, 15, "main.c", 2, 9); // aa in 'int kk = aa + bb;'
        performTest("main.c", 3, 20, "main.c", 2, 17); // bb in 'int kk = aa + bb;'
    }

    public void testFuncUsage() throws Exception {
        performTest("kr.c", 6, 13, "kr.c", 9, 1); // foo in "return foo(kk) + boo(kk);"
        performTest("kr.c", 6, 23, "kr.c", 17, 1); // boo in "return foo(kk) + boo(kk);"
    }

    public void testFuncLocalVarsUsage() throws Exception {
        performTest("main.c", 5, 20, "main.c", 3, 5); // kk in "for (int ii = kk; ii > 0; ii--) {"
        performTest("main.c", 6, 10, "main.c", 4, 5); // res in "res *= ii;"
        performTest("main.c", 8, 13, "main.c", 4, 5); // res in "return res;"
        performTest("kr.c", 6, 17, "kr.c", 5, 5); // first kk in "return foo(kk) + boo(kk);"
        performTest("kr.c", 6, 27, "kr.c", 5, 5); // second kk in "return foo(kk) + boo(kk);"
    }

    public void testForLoopLocalVarsUsage() throws Exception {
        performTest("main.c", 5, 24, "main.c", 5, 10); // second ii in "for (int ii = kk; ii > 0; ii--) {"
        performTest("main.c", 5, 32, "main.c", 5, 10); // third ii in "for (int ii = kk; ii > 0; ii--) {"
        performTest("main.c", 6, 17, "main.c", 5, 10); // ii in "res *= ii;"
    }

    public void testNameWithUnderscore() throws Exception {
        performTest("main.c", 12, 6, "main.c", 11, 1); // method_name_with_underscore();
    }
    
    public void testSameNameDiffScope() throws Exception {
        // IZ#131560: Hyperlink does not distinguish variables with the same names within function body
        // function parameter
        performTest("main.c", 22, 30, "main.c", 22, 24); // name in void sameNameDiffScope(int name) {
        performTest("main.c", 23, 10, "main.c", 22, 24); // name in if (name++) {
        performTest("main.c", 26, 17, "main.c", 22, 24); // name in } else if (name++) {
        performTest("main.c", 26, 17, "main.c", 22, 24); // name in name--;
        
        // local variable
        performTest("main.c", 24, 17, "main.c", 24, 9); // name in name--;
        performTest("main.c", 25, 10, "main.c", 24, 9); // name in name--;        
        
        // second local variable
        performTest("main.c", 27, 17, "main.c", 27, 9); // name in name--;
        performTest("main.c", 28, 17, "main.c", 27, 9); // name in name--;        
    }
    
    public void testGlobalVar() throws Exception {
        // IZ#132295: Hyperlink does not  distinguish local variable and global
        // variable if they has same name
        
        // local variable
        performTest("main.c", 33, 24, "main.c", 32, 5);
        performTest("main.c", 34, 36, "main.c", 32, 5);
        
        // global variable
        performTest("main.c", 33, 14, "main.c", 37, 1);
        performTest("main.c", 34, 12, "main.c", 37, 1);
        performTest("main.c", 34, 28, "main.c", 37, 1);
    }
    
    public void testConstParameter() throws Exception {
        // IZ#76032: ClassView component doubles function in some cases
        // (partial fix: made const parameters resolve correctly)
        performTest("const.cc", 5, 44, "const.cc", 1, 1);
        performTest("const.cc", 5, 50, "const.cc", 2, 5);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // K&R style

    public void testKRFuncParamUsage() throws Exception {
        performTest("kr.c", 12, 15, "kr.c", 10, 1); // index in 'return index;'
    }

    public void testKRFooDeclDefUsageH() throws Exception {
        // See IZ116715
        performTest("kr.h", 2, 6, "kr.h", 9, 1); // int foo(); -> int foo(index)
        performTest("kr.h", 9, 6, "kr.h", 2, 1); // int foo(index) -> int foo();
        performTest("kr.h", 15, 6, "kr.h", 17, 1); // int boo(); -> int boo(int i)
        performTest("kr.h", 17, 6, "kr.h", 15, 1); // int boo(int i) -> int boo();
    }

    public void testKRFooDeclDefUsageC() throws Exception {
        // See IZ116715
        performTest("kr.c", 2, 6, "kr.c", 9, 1); // int foo(); -> int foo(index)
        performTest("kr.c", 9, 6, "kr.c", 2, 1); // int foo(index) -> int foo();
        performTest("kr.c", 15, 6, "kr.c", 17, 1); // int boo(); -> int boo(int i)
        performTest("kr.c", 17, 6, "kr.c", 15, 1); // int boo(int i) -> int boo();
    }

    public void testStaticVariable() throws Exception {
        // See IZ136481
        performTest("static_variable.c", 4, 16, "static_variable.h", 2, 1);
        performTest("static_variable.c", 5, 15, "static_variable.h", 1, 1);
    }

    public void testIZ131555() throws Exception {
        for (int i = 5; i <=13; i++ ) {
            performTest("IZ131555.c", i, 16, "IZ131555.c", 2, 5);
        }
    }

    public void testIZ136730() throws Exception {
        performTest("IZ136730.c", 2, 11, "IZ136730.c", 3, 1);
    }
    
    public void testTemplateParameter() throws Exception {
        performTest("template_parameter.cc", 2, 13, "template_parameter.cc", 1, 17);
        performTest("template_parameter.cc", 3, 13, "template_parameter.cc", 1, 17);
        performTest("template_parameter.cc", 6, 19, "template_parameter.cc", 1, 17);
        performTest("template_parameter.cc", 7, 14, "template_parameter.cc", 1, 17);
        performTest("template_parameter.cc", 7, 12, "template_parameter.cc", 1, 29);
        performTest("template_parameter.cc", 7, 26, "template_parameter.cc", 1, 10);
        performTest("template_parameter.cc", 8, 11, "template_parameter.cc", 1, 10);
    }

    public void testTemplateParameterBeforeFunction() throws Exception {
        // IZ#138099 : unresolved identifier for functions' template parameter
        performTest("template_parameter2.cc", 1, 18, "template_parameter2.cc", 1, 11);
        performTest("template_parameter2.cc", 4, 22, "template_parameter2.cc", 4, 15);
        performTest("template_parameter2.cc", 4, 66, "template_parameter2.cc", 4, 15);
        performTest("template_parameter2.cc", 5, 15, "template_parameter2.cc", 5, 14);
        performTest("template_parameter2.cc", 5, 41, "template_parameter2.cc", 5, 14);
        performTest("template_parameter2.cc", 8, 20, "template_parameter2.cc", 8, 10);
        performTest("template_parameter2.cc", 8, 46, "template_parameter2.cc", 8, 10);
        performTest("template_parameter2.cc", 9, 20, "template_parameter2.cc", 9, 10);
        performTest("template_parameter2.cc", 9, 46, "template_parameter2.cc", 9, 10);
        performTest("template_parameter2.cc", 11, 11, "template_parameter2.cc", 11, 10);
        performTest("template_parameter2.cc", 11, 55, "template_parameter2.cc", 11, 10);
        performTest("template_parameter2.cc", 13, 17, "template_parameter2.cc", 13, 10);
        performTest("template_parameter2.cc", 13, 29, "template_parameter2.cc", 13, 22);
        performTest("template_parameter2.cc", 13, 33, "template_parameter2.cc", 13, 22);
    }

    public void testIZ131625() throws Exception {
        performTest("IZ131625.cc",  4, 11, "IZ131625.cc", 10, 1);
        performTest("IZ131625.cc",  7, 23, "IZ131625.cc", 10, 1);
        performTest("IZ131625.cc",  7, 23, "IZ131625.cc", 10, 1);
        performTest("IZ131625.cc", 14, 35, "IZ131625.cc", 12, 3);
        performTest("IZ131625.cc", 18, 24, "IZ131625.cc", 10, 1);
        performTest("IZ131625.cc", 20,  3, "IZ131625.cc", 10, 1);
        performTest("IZ131625.cc", 21, 12, "IZ131625.cc", 13, 3);
        performTest("IZ131625.cc", 22, 11, "IZ131625.cc", 13, 3);
        performTest("IZ131625.cc", 10, 20, "IZ131625.cc",   4, 3);
    }

    public void testIZ136146() throws Exception {
        performTest("IZ136146.cc", 20, 10, "IZ136146.cc", 15, 5);
        performTest("IZ136146.cc", 21, 12, "IZ136146.cc", 15, 5);
    }

    public void testIZ132903() throws Exception {
        performTest("IZ132903.cc", 16, 10, "IZ132903.cc",  9, 5);
    }

    public void testIZ136167() throws Exception {
        performTest("IZ136167.cc", 21, 13, "IZ136167.cc",  3, 5);
    }

    public void testIZ138833() throws Exception {
        performTest("IZ138833.cc", 4, 17, "IZ138833.cc",  3, 5);
    }

    public void testIZ138905() throws Exception {
        // IZ#138905 : IDE highlights 'a1' as invalid identifier (struct {...} a1;)
        performTest("IZ138905.cc", 4, 4, "IZ138905.cc", 4, 3);
        performTest("IZ138905.cc", 9, 4, "IZ138905.cc", 9, 3);
        performTest("IZ138905.cc", 12, 18, "IZ138905.cc", 12, 17);
    }

    public void testIZ139056() throws Exception {
        // IZ#139056 : using directive affects only single namespace definition
        performTest("IZ139056.cc", 10, 8, "IZ139056.cc", 2, 5);
        performTest("IZ139056.cc", 10, 24, "IZ139056.cc", 2, 5);
        performTest("IZ139056.cc", 15, 8, "IZ139056.cc", 2, 5);
        performTest("IZ139056.cc", 15, 24, "IZ139056.cc", 2, 5);
    }
    
    public void testIZ139141() throws Exception {
        // IZ#139141 : unable to resolve constructor of nested structure
        performTest("IZ139141.cc", 7, 6, "IZ139141.cc", 7, 5);
        performTest("IZ139141.cc", 8, 6, "IZ139141.cc", 8, 5);
    }

    public void testIZ139618() throws Exception {
        // IZ#139618 : Syntax hightlighting failure for unnamed union
        performTest("IZ139618.cc", 2, 11, "IZ139618.cc", 2, 9);
        performTest("IZ139618.cc", 2, 15, "IZ139618.cc", 2, 14);
        performTest("IZ139618.cc", 3, 13, "IZ139618.cc", 3, 5);
        performTest("IZ139618.cc", 8, 16, "IZ139618.cc", 8, 9);
        performTest("IZ139618.cc", 9, 15, "IZ139618.cc", 9, 9);
        performTest("IZ139618.cc", 11, 7, "IZ139618.cc", 8, 9);
        performTest("IZ139618.cc", 12, 6, "IZ139618.cc", 9, 9);
        performTest("IZ139618.cc", 12, 19, "IZ139618.cc", 10, 7);
        performTest("IZ139618.cc", 12, 22, "IZ139618.cc", 9, 9);
    }

    public void testIZ139693() throws Exception {
        // IZ#139693 : function-local typedefs are not resolved
        performTest("IZ139693.cc", 2, 21, "IZ139693.cc", 2, 5);
        performTest("IZ139693.cc", 3, 9, "IZ139693.cc", 2, 5);
    }

    public static class Failed extends HyperlinkBaseTestCase {

        @Override
        protected Class getTestCaseDataClass() {
            return BasicHyperlinkTestCase.class;
        }

        public Failed(String testName) {
            super(testName, true);
        }

        public void testKRFuncParamDecl() throws Exception {
            performTest("kr.c", 9, 10, "kr.c", 10, 1); // index in 'int foo(index)'
        }

        public void testKRFooDeclDefUsage() throws Exception {
            // See IZ116715
            performTest("kr.c", 2, 6, "kr.c", 9, 1); // int foo(); -> int foo(index)
            performTest("kr.c", 9, 6, "kr.c", 2, 1); // int foo(index) -> int foo();
            performTest("kr.c", 15, 6, "kr.c", 17, 1); // int boo(); -> int boo(int i)
            performTest("kr.c", 17, 6, "kr.c", 15, 1); // int boo(int i) -> int boo();
        }
    }
}
