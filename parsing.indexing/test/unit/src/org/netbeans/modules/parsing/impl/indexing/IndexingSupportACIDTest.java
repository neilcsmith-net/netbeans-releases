/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.parsing.impl.indexing;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.test.MockMimeLookup;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.junit.MockServices;
import org.netbeans.junit.NbTestCase;
import static org.netbeans.modules.parsing.impl.indexing.FooPathRecognizer.FOO_EXT;
import static org.netbeans.modules.parsing.impl.indexing.FooPathRecognizer.FOO_MIME;
import static org.netbeans.modules.parsing.impl.indexing.FooPathRecognizer.FOO_SOURCES;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.CustomIndexer;
import org.netbeans.modules.parsing.spi.indexing.CustomIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.PathRecognizer;
import org.netbeans.modules.parsing.spi.indexing.support.IndexDocument;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.netbeans.modules.parsing.spi.indexing.support.IndexingSupport;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Zezula
 */
public class IndexingSupportACIDTest extends IndexingTestBase {

    private final Map<String, Map<ClassPath,Void>> registeredClasspaths = new HashMap<String, Map<ClassPath,Void>>();

    private FileObject src1;
    private FileObject src2;
    private FileObject file1;
    private FileObject file2;
    private ClassPath cp1;
    private ClassPath cp2;

    public IndexingSupportACIDTest(@NonNull final String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        final File wd = getWorkDir();
        final FileObject wdo = FileUtil.toFileObject(wd);
        assertNotNull("No masterfs",wdo);   //NOI18N
        final FileObject cache = wdo.createFolder("cache"); //NOI18N
        CacheFolder.setCacheFolder(cache);
        src1 = wdo.createFolder("src1");        //NOI18N
        assertNotNull(src1);
        src2 = wdo.createFolder("src2");        //NOI18N
        assertNotNull(src2);
        file1 = src1.createData("test", FOO_EXT);   //NOI18N
        assertNotNull(file1);
        file2 = src2.createData("test2", FOO_EXT);  //NOI18N
        assertNotNull(file2);
        FileUtil.setMIMEType(FOO_EXT, FOO_MIME);
        cp1 = ClassPathSupport.createClassPath(src1);
        cp2 = ClassPathSupport.createClassPath(src2);
        MockMimeLookup.setInstances(MimePath.get(FOO_MIME), new FooIndexerFactory());
        RepositoryUpdaterTest.setMimeTypes(FOO_MIME);
        RepositoryUpdaterTest.waitForRepositoryUpdaterInit();
    }

    @Override
    protected void tearDown() throws Exception {
        final RepositoryUpdaterTest.TestHandler handler = new RepositoryUpdaterTest.TestHandler();
        final Logger logger = Logger.getLogger(RepositoryUpdater.class.getName()+".tests"); //NOI18N
        try {
            logger.setLevel (Level.FINEST);
            logger.addHandler(handler);
            for(String id : registeredClasspaths.keySet()) {
                final Map<ClassPath,Void> classpaths = registeredClasspaths.get(id);
                GlobalPathRegistry.getDefault().unregister(id, classpaths.keySet().toArray(new ClassPath[classpaths.size()]));
            }
            handler.await();
        } finally {
            logger.removeHandler(handler);
        }
        super.tearDown();
    }


    public void testChangesVisibleAfterCommit() throws InterruptedException, IOException {

        assertTrue(GlobalPathRegistry.getDefault().getPaths(FOO_SOURCES).isEmpty());
        final RepositoryUpdaterTest.TestHandler handler = new RepositoryUpdaterTest.TestHandler();
        final Logger logger = Logger.getLogger(RepositoryUpdater.class.getName()+".tests"); //NOI18N
        logger.setLevel (Level.FINEST);
        logger.addHandler(handler);

        globalPathRegistry_register(FOO_SOURCES,new ClassPath[]{cp1});
        assertTrue (handler.await());
        assertEquals(0, handler.getBinaries().size());
        assertEquals(1, handler.getSources().size());
        assertEquals(this.src1.toURL(), handler.getSources().get(0));
        assertTrue(MimeLookup.getLookup(MimePath.get(FOO_MIME)).lookup(FooIndexerFactory.class).isSuccess());
        QuerySupport qs = QuerySupport.forRoots(FooIndexerFactory.NAME, FooIndexerFactory.VERSION, src1);
        final Collection<? extends IndexResult> res = qs.query("_sn", "", QuerySupport.Kind.PREFIX, (String[]) null);   //NOI18N
        assertEquals(1, res.size());
        assertEquals(file1, res.iterator().next().getFile());
    }


    public void testChangesNotVisibleAfterRollBack() throws InterruptedException, IOException {

        assertTrue(GlobalPathRegistry.getDefault().getPaths(FOO_SOURCES).isEmpty());
        final RepositoryUpdaterTest.TestHandler handler = new RepositoryUpdaterTest.TestHandler();
        final Logger logger = Logger.getLogger(RepositoryUpdater.class.getName()+".tests"); //NOI18N
        logger.setLevel (Level.FINEST);
        logger.addHandler(handler);

        MimeLookup.getLookup(MimePath.get(FOO_MIME)).lookup(FooIndexerFactory.class).setCancelScanning(true);

        globalPathRegistry_register(FOO_SOURCES,new ClassPath[]{cp1});
        assertTrue (handler.await());
        assertEquals(0, handler.getBinaries().size());
        assertEquals(1, handler.getSources().size());
        assertEquals(this.src1.toURL(), handler.getSources().get(0));
        assertFalse(MimeLookup.getLookup(MimePath.get(FOO_MIME)).lookup(FooIndexerFactory.class).isSuccess());
        QuerySupport qs = QuerySupport.forRoots(FooIndexerFactory.NAME, FooIndexerFactory.VERSION, src1);
        final Collection<? extends IndexResult> res = qs.query("_sn", "", QuerySupport.Kind.PREFIX, (String[]) null);   //NOI18N
        assertEquals(0, res.size());
    }

    private void globalPathRegistry_register(String id, ClassPath [] classpaths) {
        Map<ClassPath,Void> map = registeredClasspaths.get(id);
        if (map == null) {
            map = new IdentityHashMap<ClassPath, Void>();
            registeredClasspaths.put(id, map);
        }
        for (ClassPath cp :  classpaths) {
            map.put(cp,null);
        }
        GlobalPathRegistry.getDefault().register(id, classpaths);
    }


    public static final class FooPathRecognizer extends PathRecognizer {

        @Override
        public Set<String> getSourcePathIds() {
            return Collections.<String>singleton(FOO_SOURCES);
        }

        @Override
        public Set<String> getLibraryPathIds() {
            return Collections.<String>emptySet();
        }

        @Override
        public Set<String> getBinaryLibraryPathIds() {
            return Collections.<String>emptySet();
        }

        @Override
        public Set<String> getMimeTypes() {
            return Collections.<String>singleton(FOO_MIME);
        }
    }

    private static class FooIndexerFactory extends CustomIndexerFactory {

        private static final String NAME = "FooIndexer";    //NOI18N
        private static final int VERSION = 1;

        private volatile boolean success;
        private volatile boolean cancelScanning;

        @Override
        public CustomIndexer createIndexer() {
            return new CustomIndexer() {
                @Override
                protected void index(Iterable<? extends Indexable> files, Context context) {
                    try {
                        final IndexingSupport is = IndexingSupport.getInstance(context);
                        for (Indexable i : files) {
                            final IndexDocument doc = is.createDocument(i);
                            is.addDocument(doc);
                        }
                    } catch (IOException ioe) {
                        Exceptions.printStackTrace(ioe);
                    }
                    if (cancelScanning) {
                        try {
                            final Field gwm = RepositoryUpdater.class.getDeclaredField("worker");  //NOI18N
                            gwm.setAccessible(true);
                            final Object task = gwm.get(RepositoryUpdater.getDefault());
                            final Field wipf = task.getClass().getDeclaredField("workInProgress");  //NOI18N
                            wipf.setAccessible(true);
                            RepositoryUpdater.Work work = (RepositoryUpdater.Work) wipf.get(task);
                            work.setCancelled(true);
                        } catch (ReflectiveOperationException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            };
        }

        @Override
        public boolean supportsEmbeddedIndexers() {
            return false;
        }

        @Override
        public void filesDeleted(Iterable<? extends Indexable> deleted, Context context) {
        }

        @Override
        public void filesDirty(Iterable<? extends Indexable> dirty, Context context) {
        }

        @Override
        public String getIndexerName() {
            return NAME;
        }

        @Override
        public int getIndexVersion() {
            return VERSION;
        }

        @Override
        public void scanFinished(Context context) {
            success = !context.isCancelled();
        }

        boolean isSuccess() {
            return success;
        }

        void setCancelScanning(final boolean cancelScanning) {
            this.cancelScanning = cancelScanning;
        }
    }
}
