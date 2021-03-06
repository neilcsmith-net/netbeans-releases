/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.jumpto.type;

import java.util.Arrays;
import java.util.stream.Collectors;
import javax.swing.Icon;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.jumpto.settings.GoToSettings;
import org.netbeans.spi.jumpto.type.TypeDescriptor;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Tomas Zezula
 */
public class TypeComparatorTest extends NbTestCase {

    public TypeComparatorTest(final String name) {
        super(name);
    }

    public void testSorting() {

        TypeComparator tc = TypeComparator.create(GoToSettings.SortingType.LEVENSHTEIN, "JTC", false, false);   //NOI18N
        final TypeDescriptor[] tds = new TypeDescriptor[] {
            new MockTypeDescriptor("JavaTextComponent"),    //NOI18N
            new MockTypeDescriptor("JTextComponent"),       //NOI18N
        };
        Arrays.sort(tds, tc);
        assertEquals(
                Arrays.asList(
                    "JTextComponent",       //NOI18N
                    "JavaTextComponent"     //NOI18N
                ),
                Arrays.stream(tds)
                .map(TypeDescriptor::getSimpleName)
                .collect(Collectors.toList()));
    }

    private static class MockTypeDescriptor extends TypeDescriptor {
        private final String name;

        MockTypeDescriptor(String name) {
            this.name = name;
        }

        @Override
        public String getSimpleName() {
            return name;
        }

        @Override
        public String getOuterName() {
            return "";
        }

        @Override
        public String getTypeName() {
            return name;
        }

        @Override
        public String getContextName() {
            return "";
        }

        @Override
        public Icon getIcon() {
            return null;
        }

        @Override
        public String getProjectName() {
            return null;
        }

        @Override
        public Icon getProjectIcon() {
            return null;
        }

        @Override
        public FileObject getFileObject() {
            return null;
        }

        @Override
        public int getOffset() {
            return -1;
        }

        @Override
        public void open() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String toString() {
            return name;
        }

    }
}
