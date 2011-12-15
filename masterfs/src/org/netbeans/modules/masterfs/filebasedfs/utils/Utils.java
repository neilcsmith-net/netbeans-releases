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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.masterfs.filebasedfs.utils;

import java.io.File;
import java.util.Locale;
import java.util.Stack;
import org.openide.util.Utilities;

/**
 *
 * @author rmatous
 */
public class Utils {
    public static boolean equals(File f1, File f2) {
        if (f1 == null) {
            return f2 == null;
        }
        if (f2 == null) {
            return f1 == null;
        }
        if (Utilities.isMac()) {
            return f1.getPath().compareToIgnoreCase(f2.getPath()) == 0;
        }
        return f1.equals(f2);
    }
    public static int hashCode(final File file) {
        if (Utilities.isMac()) {
            // same as in Win32FileSystem
            return file.getPath().toLowerCase(Locale.ENGLISH).hashCode() ^ 1234321;
        }
        return file.hashCode();
    }
    
    public static String getRelativePath(final File dir, final File file) {
        Stack<String> stack = new Stack<String>();
        File tempFile = file;
        while (tempFile != null && !equals(tempFile, dir)) {
            stack.push(tempFile.getName());
            tempFile = tempFile.getParentFile();
        }
        if (tempFile == null) {
            return null;
        }
        StringBuilder retval = new StringBuilder();
        while (!stack.isEmpty()) {
            retval.append(stack.pop());
            if (!stack.isEmpty()) {
                retval.append("/");//NOI18N
            }
        }
        return retval.toString();
    }
}
