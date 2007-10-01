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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.hints.errors;

import com.sun.source.util.TreePath;
import java.util.List;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.java.hints.infrastructure.ErrorHintsTestBase;
import org.netbeans.spi.editor.hints.Fix;

/**
 *
 * @author Jan Lahoda
 */
public class MagicSurroundWithTryCatchFixTest extends ErrorHintsTestBase {
    
    public MagicSurroundWithTryCatchFixTest(String testName) {
        super(testName);
    }

    public void test104085() throws Exception {
        performFixTest("test/Test.java",
                       "package test; public class Test {public void test() {try {}finally{new java.io.FileInputStream(\"\");}}}",
                       127 - 43,
                       "FixImpl",
                       "package test; import java.io.FileNotFoundException; import java.util.logging.Level; import java.util.logging.Logger; public class Test {public void test() {try {}finally{try { new java.io.FileInputStream(\"\"); } catch (FileNotFoundException ex) { Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex); } }}}");
    }
    
    protected List<Fix> computeFixes(CompilationInfo info, int pos, TreePath path) throws Exception {
        return new UncaughtException().run(info, null, pos, path, null);
    }

    @Override
    protected String toDebugString(CompilationInfo info, Fix f) {
        if (f instanceof MagicSurroundWithTryCatchFix) {
            return "FixImpl";
        }
        
        return super.toDebugString(info, f);
    }
    
}
