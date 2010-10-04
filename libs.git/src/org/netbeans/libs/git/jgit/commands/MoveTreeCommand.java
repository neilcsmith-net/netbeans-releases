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

package org.netbeans.libs.git.jgit.commands;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuildIterator;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.netbeans.libs.git.GitException;
import org.netbeans.libs.git.jgit.Utils;
import org.netbeans.libs.git.progress.FileProgressMonitor;

/**
 *
 * @author ondra
 */
abstract class MoveTreeCommand extends GitCommand {
    private final File source;
    private final File target;
    private final boolean after;
    private final FileProgressMonitor monitor;
    private final boolean keepSourceTree;

    protected MoveTreeCommand (Repository repository, File source, File target, boolean after, boolean keepSourceTree, FileProgressMonitor monitor) {
        super(repository, monitor);
        this.source = source;
        this.target = target;
        this.monitor = monitor;
        this.after = after;
        this.keepSourceTree = keepSourceTree;
    }

    @Override
    protected void run() throws GitException {
        if (!keepSourceTree && !after) {
            rename();
        }
        Repository repository = getRepository();
        try {
            DirCache cache = repository.lockDirCache();
            try {
                DirCacheBuilder builder = cache.builder();
                TreeWalk treeWalk = new TreeWalk(repository);
                PathFilter sourceFilter = PathFilter.create(Utils.getRelativePath(repository.getWorkTree(), source));
                PathFilter targetFilter = PathFilter.create(Utils.getRelativePath(repository.getWorkTree(), target));
                treeWalk.setFilter(PathFilterGroup.create(Arrays.asList(sourceFilter, targetFilter)));
                treeWalk.setRecursive(true);
                treeWalk.reset();
                treeWalk.addTree(new DirCacheBuildIterator(builder));
                while (treeWalk.next() && !monitor.isCanceled()) {
                    String path = treeWalk.getPathString();
                    File file = new File(repository.getWorkTree().getAbsolutePath() + File.separator + path);
                    DirCacheEntry e = treeWalk.getTree(0, DirCacheBuildIterator.class).getDirCacheEntry();
                    if (e != null) {
                        if (targetFilter.include(treeWalk)) {
                            monitor.notifyWarning(MessageFormat.format("Index already contains an entry for {0}", path));
                            continue;
                        }
                        String newPath = getRelativePath(file, source, target);
                        DirCacheEntry copied = new DirCacheEntry(newPath);
                        copied.copyMetaData(e);
                        File newFile = new File(repository.getWorkTree().getAbsolutePath() + File.separator + newPath);
                        monitor.notifyFile(newFile);
                        builder.add(copied);
                        if (keepSourceTree) {
                            builder.add(e);
                        }
                    }
                }
                if (!monitor.isCanceled()) {
                    builder.commit();
                }
            } finally {
                cache.unlock();
            }
        } catch (CorruptObjectException ex) {
            throw new GitException(ex);
        } catch (IOException ex) {
            throw new GitException(ex);
        }
    }

    @Override
    protected boolean prepareCommand() throws GitException {
        boolean retval = super.prepareCommand();
        if (retval) {
            File workTree = getRepository().getWorkTree();
            String relPathToSource = Utils.getRelativePath(workTree, source);
            String relPathToTarget = Utils.getRelativePath(workTree, target);
            if (relPathToSource.startsWith(relPathToTarget + "/")) { //NOI18N
                monitor.preparationsFailed(MessageFormat.format("Source folder [{0}] lies under the target [{1}]", new Object[] { relPathToSource, relPathToTarget } ));
                throw new GitException(MessageFormat.format("{0} lies under {1}", new Object[] { relPathToSource, relPathToTarget } ));
            } else if (relPathToTarget.startsWith(relPathToSource + "/")) { //NOI18N
                monitor.preparationsFailed(MessageFormat.format("Target folder [{0}] lies under the source [{1}]", new Object[] { relPathToTarget, relPathToSource } ));
                throw new GitException(MessageFormat.format("{0} lies under {1}", new Object[] { relPathToTarget, relPathToSource } ));
            }
        }
        return retval;
    }

    private void rename () throws GitException {
        File parentFile = target.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            throw new GitException("Cannot create target folder: " + parentFile.getAbsolutePath());
        }
        if (!source.renameTo(target)) {
            throw new GitException("Cannot rename " + source.getAbsolutePath() + " to " + target.getAbsolutePath());
        }
    }

    private String getRelativePath (File file, File ancestor, File target) {
        String relativePathToAncestor = Utils.getRelativePath(ancestor, file);
        StringBuilder relativePathToSource = new StringBuilder(Utils.getRelativePath(getRepository().getWorkTree(), target));
        if (!relativePathToAncestor.isEmpty()) {
            if (relativePathToSource.length() > 0) {
                relativePathToSource.append("/"); //NOI18N
            }
            relativePathToSource.append(relativePathToAncestor);
        }
        return relativePathToSource.toString();
    }
}
