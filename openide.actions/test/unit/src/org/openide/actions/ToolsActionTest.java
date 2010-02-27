/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.openide.actions;

import javax.swing.Action;
import java.util.List;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileObject;
import org.netbeans.junit.NbTestCase;
import static org.junit.Assert.*;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
public class ToolsActionTest extends NbTestCase {

    public ToolsActionTest(String n) {
        super(n);
    }

    public void testActionReadFromLayer () throws Exception {
        CopyAction copy = CopyAction.get(CopyAction.class);
        CutAction cut = CutAction.get(CutAction.class);
        PasteAction paste = PasteAction.get(PasteAction.class);

        FileObject fo = FileUtil.createFolder(FileUtil.getConfigRoot(), "UI/ToolActions");
        assertNotNull("ToolActions folder found", fo);

        fo.createFolder("Cat1").createData("org-openide-actions-CutAction.instance").setAttribute("position", 100);
        fo.getFileObject("Cat1").createData("org-openide-actions-PasteAction.instance").setAttribute("position", 200);
        fo.createFolder("Cat2").createData("org-openide-actions-CopyAction.instance");

        List<Action> ToolActions = ToolsAction.getToolActions();
        assertEquals("Four actions: " + ToolActions, 4, ToolActions.size());
        assertEquals("Cut first", cut, ToolActions.get(0));
        assertEquals("Paste snd", paste, ToolActions.get(1));
        assertEquals("Separator in middle", null, ToolActions.get(2));
        assertEquals("Copy last", copy, ToolActions.get(3));

    }

}