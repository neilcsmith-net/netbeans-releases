/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.git.ui.status;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.JTable;
import org.netbeans.modules.git.AbstractGitTestCase;
import org.netbeans.modules.git.Git;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.versioning.util.SystemActionBridge;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author ondra
 */
public class StatusTest extends AbstractGitTestCase {

    public StatusTest (String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Git.STATUS_LOG.setLevel(Level.ALL);
    }

    public void testVersioningPanel () throws Exception {
        final JTable tables[] = new JTable[1];
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                GitVersioningTopComponent tc = GitVersioningTopComponent.findInstance();
                VCSContext ctx = VCSContext.forNodes(new Node[] {
                    new AbstractNode(Children.LEAF, Lookups.singleton(repositoryLocation))
                });
                SystemActionBridge.createAction(SystemAction.get(StatusAction.class), "sss", ctx.getElements()).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
                Field f;
                try {
                    f = GitVersioningTopComponent.class.getDeclaredField("controller");
                    f.setAccessible(true);
                    VersioningPanelController controller = (VersioningPanelController) f.get(tc);
                    f = VersioningPanelController.class.getDeclaredField("syncTable");
                    f.setAccessible(true);
                    SyncTable table = (SyncTable) f.get(controller);
                    f = SyncTable.class.getDeclaredField("table");
                    f.setAccessible(true);
                    tables[0] = (JTable) f.get(table);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
        JTable table = tables[0];
        assertNotNull(table);
        assertTable(table, Collections.<File>emptySet());
        File file = new File(repositoryLocation, "file");

        file.createNewFile();
        File[] files = new File[] { repositoryLocation };
        getCache().refreshAllRoots(files);
        assertTable(table, Collections.singleton(file));

        add();
        commit();
        getCache().refreshAllRoots(files);
        assertTable(table, Collections.<File>emptySet());

        write(file, "blabla");
        add(file);
        getCache().refreshAllRoots(files);
        assertTable(table, Collections.singleton(file));

        commit();
        getCache().refreshAllRoots(files);
        assertTable(table, Collections.<File>emptySet());

        delete(false, file);
        getCache().refreshAllRoots(files);
        assertTable(table, Collections.singleton(file));
    }

    private void assertTable (final JTable table, Set<File> files) throws Exception {
        Thread.sleep(5000);
        final Set<File> displayedFiles = new HashSet<File>();
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < table.getRowCount(); ++i) {
                    String path = table.getValueAt(i, 2).toString();
                    displayedFiles.add(new File(repositoryLocation, path));
                }
            }
        });
        assertEquals(files, displayedFiles);
    }
}
