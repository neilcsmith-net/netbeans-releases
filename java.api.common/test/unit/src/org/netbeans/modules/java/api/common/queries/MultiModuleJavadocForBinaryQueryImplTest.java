/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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
 */
package org.netbeans.modules.java.api.common.queries;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import static junit.framework.TestCase.assertNotNull;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.queries.JavadocForBinaryQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.java.api.common.SourceRoots;
import org.netbeans.modules.java.api.common.TestProject;
import org.netbeans.modules.java.api.common.impl.ModuleTestUtilities;
import org.netbeans.modules.java.api.common.impl.MultiModule;
import org.netbeans.modules.java.api.common.project.ProjectProperties;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.test.MockChangeListener;
import org.openide.util.test.MockLookup;


/**
 *
 * @author Tomas Zezula
 */
public class MultiModuleJavadocForBinaryQueryImplTest extends NbTestCase {
    private FileObject src1;
    private FileObject src2;
    private FileObject mod1a;
    private FileObject mod1b;
    private FileObject mod2c;
    private FileObject mod1d;
    private FileObject mod2d;
    private TestProject tp;
    private ModuleTestUtilities mtu;

    public MultiModuleJavadocForBinaryQueryImplTest(@NonNull final String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        MockLookup.setInstances(TestProject.createProjectType());
        final FileObject wd = FileUtil.toFileObject(FileUtil.normalizeFile(getWorkDir()));
        src1 = wd.createFolder("src1"); //NOI18N
        assertNotNull(src1);
        src2 = wd.createFolder("src2"); //NOI18N
        assertNotNull(src2);
        mod1a = src1.createFolder("lib.common").createFolder("classes");        //NOI18N
        assertNotNull(mod1a);
        mod1b = src1.createFolder("lib.util").createFolder("classes");          //NOI18N
        assertNotNull(mod1b);
        mod2c = src2.createFolder("lib.discovery").createFolder("classes");     //NOI18N
        assertNotNull(mod2c);
        mod2d = src2.createFolder("lib.event").createFolder("classes");         //NOI18N
        assertNotNull(mod2d);
        mod1d = src1.createFolder("lib.event").createFolder("classes");         //NOI18N
        assertNotNull(mod1d);
        final Project prj = TestProject.createProject(wd, null, null);
        tp = prj.getLookup().lookup(TestProject.class);
        assertNotNull(tp);
        mtu = ModuleTestUtilities.newInstance(tp);
        assertNotNull(mtu);
    }

    public void testQueryForDistFolder() {
        assertTrue(mtu.updateModuleRoots(false, src1,src2));
        final SourceRoots modules = mtu.newModuleRoots(false);
        assertTrue(Arrays.equals(new FileObject[]{src1, src2}, modules.getRoots()));
        final SourceRoots sources = mtu.newSourceRoots(false);
        final MultiModule model = MultiModule.getOrCreate(modules, sources);

        final MultiModuleJavadocForBinaryQueryImpl q =
                new MultiModuleJavadocForBinaryQueryImpl(
                        tp.getUpdateHelper().getAntProjectHelper(),
                        tp.getEvaluator(),
                        model,
                        new String[]{ProjectProperties.DIST_DIR},
                        ProjectProperties.DIST_JAVADOC_DIR);
        final URL javadoc = getJavadocLocation();

        assertNull(q.findJavadoc(mtu.distFor("foo")));  //NOI18N

        JavadocForBinaryQuery.Result res = q.findJavadoc(mtu.distFor(mod1a.getParent().getNameExt()));
        assertNotNull(res);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));

        res = q.findJavadoc(mtu.distFor(mod1b.getParent().getNameExt()));
        assertNotNull(res);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));

        res = q.findJavadoc(mtu.distFor(mod2c.getParent().getNameExt()));
        assertNotNull(res);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));

        res = q.findJavadoc(mtu.distFor(mod1d.getParent().getNameExt()));
        assertNotNull(res);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));

        res = q.findJavadoc(mtu.distFor(mod2d.getParent().getNameExt()));
        assertNotNull(res);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));
    }

    public void testBuildFolder() {
        assertTrue(mtu.updateModuleRoots(false, src1,src2));
        final SourceRoots modules = mtu.newModuleRoots(false);
        assertTrue(Arrays.equals(new FileObject[]{src1, src2}, modules.getRoots()));
        final SourceRoots sources = mtu.newSourceRoots(false);
        final MultiModule model = MultiModule.getOrCreate(modules, sources);

        final MultiModuleJavadocForBinaryQueryImpl q =
                new MultiModuleJavadocForBinaryQueryImpl(
                        tp.getUpdateHelper().getAntProjectHelper(),
                        tp.getEvaluator(),
                        model,
                        new String[]{ProjectProperties.BUILD_CLASSES_DIR},
                        ProjectProperties.DIST_JAVADOC_DIR);
        final URL javadoc = getJavadocLocation();

        assertNull(q.findJavadoc(mtu.buildFor("foo")));  //NOI18N

        JavadocForBinaryQuery.Result res = q.findJavadoc(mtu.buildFor(mod1a.getParent().getNameExt()));
        assertNotNull(res);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));

        res = q.findJavadoc(mtu.buildFor(mod1b.getParent().getNameExt()));
        assertNotNull(res);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));

        res = q.findJavadoc(mtu.buildFor(mod2c.getParent().getNameExt()));
        assertNotNull(res);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));

        res = q.findJavadoc(mtu.buildFor(mod1d.getParent().getNameExt()));
        assertNotNull(res);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));

        res = q.findJavadoc(mtu.buildFor(mod2d.getParent().getNameExt()));
        assertNotNull(res);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));
    }

    public void testBuildFolderChanges() {
        assertTrue(mtu.updateModuleRoots(false, src1));
        final SourceRoots modules = mtu.newModuleRoots(false);
        assertTrue(Arrays.equals(new FileObject[]{src1}, modules.getRoots()));
        final SourceRoots sources = mtu.newSourceRoots(false);
        final MultiModule model = MultiModule.getOrCreate(modules, sources);
        final MultiModuleJavadocForBinaryQueryImpl q =
                new MultiModuleJavadocForBinaryQueryImpl(
                        tp.getUpdateHelper().getAntProjectHelper(),
                        tp.getEvaluator(),
                        model,
                        new String[]{ProjectProperties.BUILD_DIR},
                        ProjectProperties.DIST_JAVADOC_DIR);
        final URL javadoc = getJavadocLocation();

        final URL origBuildDir = mtu.buildFor(mod1a.getParent().getNameExt());
        JavadocForBinaryQuery.Result res = q.findJavadoc(origBuildDir);
        assertNotNull(res);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));

        ProjectManager.mutex().writeAccess(()->{
            try {
                final EditableProperties ep = tp.getUpdateHelper().getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                ep.setProperty(ProjectProperties.BUILD_DIR, "debug-build");  //NOI18N
                tp.getUpdateHelper().putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
                ProjectManager.getDefault().saveProject(tp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        //New build folder should return result
        JavadocForBinaryQuery.Result res2 = q.findJavadoc(mtu.buildFor(mod1a.getParent().getNameExt()));
        assertNotNull(res2);
        assertNotSame(res, res2);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res2.getRoots()));

        //Old result should have no javadoc
        assertEquals(Collections.emptyList(), Arrays.asList(res.getRoots()));

        //Old build folder should not return result
        JavadocForBinaryQuery.Result res3 = q.findJavadoc(origBuildDir);
        assertNull(res3);
    }

    public void testBuildFolderChangesFires() {
        assertTrue(mtu.updateModuleRoots(false, src1));
        final SourceRoots modules = mtu.newModuleRoots(false);
        assertTrue(Arrays.equals(new FileObject[]{src1}, modules.getRoots()));
        final SourceRoots sources = mtu.newSourceRoots(false);
        final MultiModule model = MultiModule.getOrCreate(modules, sources);
        final MultiModuleJavadocForBinaryQueryImpl q =
                new MultiModuleJavadocForBinaryQueryImpl(
                        tp.getUpdateHelper().getAntProjectHelper(),
                        tp.getEvaluator(),
                        model,
                        new String[]{ProjectProperties.BUILD_DIR},
                        ProjectProperties.DIST_JAVADOC_DIR);
        final URL javadoc = getJavadocLocation();

        JavadocForBinaryQuery.Result res = q.findJavadoc(mtu.buildFor(mod1a.getParent().getNameExt()));
        assertNotNull(res);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));

        final MockChangeListener l = new MockChangeListener();
        res.addChangeListener(l);
        final String[] origBuildDir = new String[1];
        ProjectManager.mutex().writeAccess(()->{
            try {
                final EditableProperties ep = tp.getUpdateHelper().getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                origBuildDir[0] = ep.getProperty(ProjectProperties.BUILD_DIR);
                ep.setProperty(ProjectProperties.BUILD_DIR, "debug-build");  //NOI18N
                tp.getUpdateHelper().putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
                ProjectManager.getDefault().saveProject(tp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        l.assertEventCount(1);
        assertEquals(Collections.emptyList(), Arrays.asList(res.getRoots()));

        ProjectManager.mutex().writeAccess(()->{
            try {
                final EditableProperties ep = tp.getUpdateHelper().getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                ep.setProperty(ProjectProperties.BUILD_DIR, origBuildDir[0]);
                tp.getUpdateHelper().putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
                ProjectManager.getDefault().saveProject(tp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        l.assertEventCount(1);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));
    }

    public void testJavadocDirChanges() {
        assertTrue(mtu.updateModuleRoots(false, src1));
        final SourceRoots modules = mtu.newModuleRoots(false);
        assertTrue(Arrays.equals(new FileObject[]{src1}, modules.getRoots()));
        final SourceRoots sources = mtu.newSourceRoots(false);
        final MultiModule model = MultiModule.getOrCreate(modules, sources);
        final MultiModuleJavadocForBinaryQueryImpl q =
                new MultiModuleJavadocForBinaryQueryImpl(
                        tp.getUpdateHelper().getAntProjectHelper(),
                        tp.getEvaluator(),
                        model,
                        new String[]{ProjectProperties.BUILD_DIR},
                        ProjectProperties.DIST_JAVADOC_DIR);
        final URL javadoc = getJavadocLocation();

        final URL origBuildDir = mtu.buildFor(mod1a.getParent().getNameExt());
        JavadocForBinaryQuery.Result res = q.findJavadoc(origBuildDir);
        assertNotNull(res);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));

        final String[] origJavadocDir = new String[1];
        ProjectManager.mutex().writeAccess(()->{
            try {
                final EditableProperties ep = tp.getUpdateHelper().getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                origJavadocDir[0] = ep.getProperty(ProjectProperties.DIST_JAVADOC_DIR);
                ep.setProperty(ProjectProperties.DIST_JAVADOC_DIR, "release-javadoc");  //NOI18N
                tp.getUpdateHelper().putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
                ProjectManager.getDefault().saveProject(tp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        final URL newJavadoc = getJavadocLocation();
        assertFalse(javadoc.equals(newJavadoc));
        //Result should new javadoc folder
        assertEquals(Arrays.asList(newJavadoc), Arrays.asList(res.getRoots()));

        ProjectManager.mutex().writeAccess(()->{
            try {
                final EditableProperties ep = tp.getUpdateHelper().getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                ep.setProperty(ProjectProperties.DIST_JAVADOC_DIR, origJavadocDir[0]);
                tp.getUpdateHelper().putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
                ProjectManager.getDefault().saveProject(tp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));
    }

    public void testJavadocDirChangesFires() {
        assertTrue(mtu.updateModuleRoots(false, src1));
        final SourceRoots modules = mtu.newModuleRoots(false);
        assertTrue(Arrays.equals(new FileObject[]{src1}, modules.getRoots()));
        final SourceRoots sources = mtu.newSourceRoots(false);
        final MultiModule model = MultiModule.getOrCreate(modules, sources);
        final MultiModuleJavadocForBinaryQueryImpl q =
                new MultiModuleJavadocForBinaryQueryImpl(
                        tp.getUpdateHelper().getAntProjectHelper(),
                        tp.getEvaluator(),
                        model,
                        new String[]{ProjectProperties.BUILD_DIR},
                        ProjectProperties.DIST_JAVADOC_DIR);
        final URL javadoc = getJavadocLocation();

        final URL origBuildDir = mtu.buildFor(mod1a.getParent().getNameExt());
        final JavadocForBinaryQuery.Result res = q.findJavadoc(origBuildDir);
        assertNotNull(res);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(res.getRoots()));

        final MockChangeListener l = new MockChangeListener();
        res.addChangeListener(l);
        final String[] origJavadocDir = new String[1];
        ProjectManager.mutex().writeAccess(()->{
            try {
                final EditableProperties ep = tp.getUpdateHelper().getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                origJavadocDir[0] = ep.getProperty(ProjectProperties.DIST_JAVADOC_DIR);
                ep.setProperty(ProjectProperties.DIST_JAVADOC_DIR, "release-javadoc");  //NOI18N
                tp.getUpdateHelper().putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
                ProjectManager.getDefault().saveProject(tp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        l.assertEventCount(1);

        ProjectManager.mutex().writeAccess(()->{
            try {
                final EditableProperties ep = tp.getUpdateHelper().getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                ep.setProperty(ProjectProperties.DIST_JAVADOC_DIR, origJavadocDir[0]);
                tp.getUpdateHelper().putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
                ProjectManager.getDefault().saveProject(tp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        l.assertEventCount(1);
    }

    public void testModulePathChanges() {
        assertTrue(mtu.updateModuleRoots(false, src1));
        final SourceRoots modules = mtu.newModuleRoots(false);
        assertTrue(Arrays.equals(new FileObject[]{src1}, modules.getRoots()));
        final SourceRoots sources = mtu.newSourceRoots(false);
        final MultiModule model = MultiModule.getOrCreate(modules, sources);
        final MultiModuleJavadocForBinaryQueryImpl q =
                new MultiModuleJavadocForBinaryQueryImpl(
                        tp.getUpdateHelper().getAntProjectHelper(),
                        tp.getEvaluator(),
                        model,
                        new String[]{ProjectProperties.BUILD_DIR},
                        ProjectProperties.DIST_JAVADOC_DIR);
        JavadocForBinaryQuery.Result r = q.findJavadoc(mtu.buildFor(mod1a.getParent().getNameExt()));
        assertNotNull(r);
        r = q.findJavadoc(mtu.buildFor(mod2c.getParent().getNameExt()));
        assertNull(r);

        assertTrue(mtu.updateModuleRoots(false, src1, src2));
        assertTrue(Arrays.equals(new FileObject[]{src1, src2}, modules.getRoots()));
        //Result for new module path entry should be returned
        r = q.findJavadoc(mtu.buildFor(mod1a.getParent().getNameExt()));
        assertNotNull(r);
        r = q.findJavadoc(mtu.buildFor(mod2c.getParent().getNameExt()));
        assertNotNull(r);

        assertTrue(mtu.updateModuleRoots(false, src2));
        assertTrue(Arrays.equals(new FileObject[]{src2}, modules.getRoots()));
        //Result for removed module path entry should not be returned
        r = q.findJavadoc(mtu.buildFor(mod1a.getParent().getNameExt()));
        assertNull(r);
        r = q.findJavadoc(mtu.buildFor(mod2c.getParent().getNameExt()));
        assertNotNull(r);
    }

    public void testModulePathChangesFires() {
        assertTrue(mtu.updateModuleRoots(false, src1, src2));
        final SourceRoots modules = mtu.newModuleRoots(false);
        assertTrue(Arrays.equals(new FileObject[]{src1, src2}, modules.getRoots()));
        final SourceRoots sources = mtu.newSourceRoots(false);
        final MultiModule model = MultiModule.getOrCreate(modules, sources);
        final MultiModuleJavadocForBinaryQueryImpl q =
                new MultiModuleJavadocForBinaryQueryImpl(
                        tp.getUpdateHelper().getAntProjectHelper(),
                        tp.getEvaluator(),
                        model,
                        new String[]{ProjectProperties.BUILD_DIR},
                        ProjectProperties.DIST_JAVADOC_DIR);
        final URL javadoc = getJavadocLocation();

        JavadocForBinaryQuery.Result r1 = q.findJavadoc(mtu.buildFor(mod1a.getParent().getNameExt()));
        assertNotNull(r1);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(r1.getRoots()));
        JavadocForBinaryQuery.Result r2 = q.findJavadoc(mtu.buildFor(mod2c.getParent().getNameExt()));
        assertNotNull(r2);
        assertEquals(Arrays.asList(javadoc), Arrays.asList(r2.getRoots()));

        final MockChangeListener l1 = new MockChangeListener();
        r1.addChangeListener(l1);
        final MockChangeListener l2 = new MockChangeListener();
        r2.addChangeListener(l2);
        assertTrue(mtu.updateModuleRoots(false, src1));
        assertTrue(Arrays.equals(new FileObject[]{src1}, modules.getRoots()));
        l1.assertNoEvents();
        l2.assertEventCount(1);

        assertTrue(mtu.updateModuleRoots(false, src1, src2));
        assertTrue(Arrays.equals(new FileObject[]{src1, src2}, modules.getRoots()));
        l1.assertNoEvents();
        l2.assertEventCount(1);
    }

    @CheckForNull
    private URL getJavadocLocation() {
        final PropertyEvaluator eval = tp.getEvaluator();
        final AntProjectHelper helper = tp.getUpdateHelper().getAntProjectHelper();
        return Optional.ofNullable(eval.getProperty(ProjectProperties.DIST_JAVADOC_DIR))
                .map((path) -> helper.resolveFile(path))
                .map((f) -> {
                    return FileUtil.urlForArchiveOrDir(f);
                })
                .orElse(null);
    }
}
