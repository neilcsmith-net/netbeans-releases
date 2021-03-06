/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript.grunt;

import java.awt.EventQueue;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.project.Project;
import org.netbeans.modules.javascript.grunt.exec.GruntExecutable;
import org.netbeans.modules.javascript.grunt.file.GruntTasks;
import org.netbeans.modules.javascript.grunt.util.GruntUtils;
import org.netbeans.modules.web.clientproject.api.build.BuildTools;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

public class GruntBuildToolSupport implements BuildTools.BuildToolSupport {

    protected final Project project;
    @NullAllowed
    protected final FileObject gruntfile;
    protected final GruntTasks gruntTasks;


    public GruntBuildToolSupport(Project project, @NullAllowed FileObject gruntfile) {
        assert project != null;
        this.project = project;
        this.gruntfile = gruntfile;
        gruntTasks = GruntBuildTool.forProject(project).getGruntTasks(gruntfile);
    }

    @Override
    public String getIdentifier() {
        return GruntBuildTool.IDENTIFIER;
    }

    @Override
    public String getBuildToolExecName() {
        return GruntExecutable.GRUNT_NAME;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public FileObject getWorkDir() {
        if (gruntfile == null) {
            return project.getProjectDirectory();
        }
        return gruntfile.getParent();
    }

    @Override
    public Future<List<String>> getTasks() {
        return new TasksFuture(gruntTasks);
    }

    @Override
    public void runTask(String... args) {
        assert !EventQueue.isDispatchThread();
        GruntExecutable grunt = getGruntExecutable();
        if (grunt != null) {
            GruntUtils.logUsageGruntBuild();
            grunt.run(args);
        }
    }

    public void addChangeListener(ChangeListener listener) {
        gruntTasks.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        gruntTasks.removeChangeListener(listener);
    }

    @CheckForNull
    private GruntExecutable getGruntExecutable() {
        if (gruntfile == null) {
            return GruntExecutable.getDefault(project, true);
        }
        return GruntExecutable.getDefault(project, FileUtil.toFile(gruntfile).getParentFile(), true);
    }

    //~ Inner classes

    private static final class TasksFuture implements Future<List<String>> {

        private final GruntTasks gruntTasks;


        public TasksFuture(GruntTasks gruntTasks) {
            assert gruntTasks != null;
            this.gruntTasks = gruntTasks;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return gruntTasks.getTasks() != null;
        }

        @Override
        public List<String> get() throws InterruptedException, ExecutionException {
            try {
                return gruntTasks.loadTasks(null, null);
            } catch (TimeoutException ex) {
                assert false;
            }
            return null;
        }

        @Override
        public List<String> get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return gruntTasks.loadTasks(timeout, unit);
        }

    }

}
