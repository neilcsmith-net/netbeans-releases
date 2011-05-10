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

package org.netbeans.modules.remote.impl.fs;

import java.io.IOException;
import java.util.HashSet;
import org.netbeans.modules.dlight.libs.common.PathUtilities;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Vladimir Kvashin
 */
public final class RemoteLink extends RemoteLinkBase {

    private String normalizedTargetPath;

    public static RemoteLink createNew(RemoteFileSystem fileSystem, ExecutionEnvironment execEnv, RemoteFileObjectBase parent, String remotePath, String link) {
        RemoteLink res = new RemoteLink(fileSystem, execEnv, parent, remotePath, link);
        return res;
    }

    private RemoteLink(RemoteFileSystem fileSystem, ExecutionEnvironment execEnv, RemoteFileObjectBase parent, String remotePath, String link) {
        super(fileSystem, execEnv, parent, remotePath);        
        setLink(link, parent);
    }

    private static String normalize(String link, FileObject parent) {
        if (link.startsWith("/")) { // NOI18N
            return link;
        }
        String parentPath = parent.getPath();
        if (!parentPath.startsWith("/")) { // NOI18N
            parentPath = "/" + parentPath; // NOI18N
        }
        return PathUtilities.normalizeUnixPath(parentPath + '/' + link);
    }

    @Override
    public FileType getType() {
        return FileType.Symlink;
    }

    @Override
    public RemoteFileObjectBase getDelegate() {
        HashSet<String> antiLoop = new HashSet<String>();
        antiLoop.add(getPath());
        RemoteFileObjectBase delegate = getFileSystem().findResource(normalizedTargetPath, antiLoop);
        return delegate;
    }

    @Override
    protected String getDelegateNormalizedPath() {
        return normalizedTargetPath;
    }

    /*package*/ final void setLink(String link, FileObject parent) {
        this.normalizedTargetPath = normalize(link, parent);
    }

    @Override
    protected void postDeleteChild(FileObject child) {
        getDelegate().postDeleteChild(child);
    }

    @Override
    protected void deleteImpl() throws IOException {
        RemoteFileSystemUtils.delete(getExecutionEnvironment(), getPath(), false);
    }
}
