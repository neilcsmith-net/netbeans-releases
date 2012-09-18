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


package org.netbeans.modules.maven.queries;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.FileUtilities;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.spi.queries.JavaLikeRootProvider;
import org.netbeans.spi.java.queries.JavadocForBinaryQueryImplementation;
import org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation2;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;


/**
 * SourceForBinary and JavadocForBinary query impls.
 * @author  Milos Kleint 
 */
abstract class AbstractMavenForBinaryQueryImpl implements SourceForBinaryQueryImplementation2,
        JavadocForBinaryQueryImplementation {
    
   
    protected AbstractMavenForBinaryQueryImpl() {
    }

    public @Override SourceForBinaryQuery.Result findSourceRoots(URL url) {
        return findSourceRoots2(url);
    }


    static @CheckForNull String jarify(@NullAllowed String path) { // #200088
        return path != null ? path.replaceFirst("[.][^./]+$", ".jar") : null;
    }
    
    static FileObject[] getProjectSrcRoots(Project p) {
        NbMavenProjectImpl project = p.getLookup().lookup(NbMavenProjectImpl.class);
        Collection<FileObject> toReturn = new LinkedHashSet<FileObject>();
        for (String item : project.getOriginalMavenProject().getCompileSourceRoots()) {
            FileObject fo = FileUtilities.convertStringToFileObject(item);
            if (fo != null) {
                toReturn.add(fo);
            }
        }
        for (URI genRoot : project.getGeneratedSourceRoots(false)) {
            FileObject fo = FileUtilities.convertURItoFileObject(genRoot);
            if (fo != null) {
                toReturn.add(fo);
            }
        }
        for (JavaLikeRootProvider rp : project.getLookup().lookupAll(JavaLikeRootProvider.class)) {
            FileObject fo = project.getProjectDirectory().getFileObject("src/main/" + rp.kind());
            if (fo != null) {
                toReturn.add(fo);
            }
        }

        URI[] res = project.getResources(false);
        for (int i = 0; i < res.length; i++) {
            FileObject fo = FileUtilities.convertURItoFileObject(res[i]);
            if (fo != null) {
                boolean ok = true;
                //#166655 resource root cannot contain the real java/xxx roots
                for (FileObject form : toReturn) {
                    if (FileUtil.isParentOf(fo, form)) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    toReturn.add(fo);
                }
            }
        }
        return toReturn.toArray(new FileObject[toReturn.size()]);
    }
    
    static FileObject[] getProjectTestSrcRoots(Project p) {
        NbMavenProjectImpl project = p.getLookup().lookup(NbMavenProjectImpl.class);
        Collection<FileObject> toReturn = new LinkedHashSet<FileObject>();
        for (String item : project.getOriginalMavenProject().getTestCompileSourceRoots()) {
            FileObject fo = FileUtilities.convertStringToFileObject(item);
            if (fo != null) {
                toReturn.add(fo);
            }
        }
        for (URI genRoot : project.getGeneratedSourceRoots(true)) {
            FileObject fo = FileUtilities.convertURItoFileObject(genRoot);
            if (fo != null) {
                toReturn.add(fo);
            }
        }
        for (JavaLikeRootProvider rp : project.getLookup().lookupAll(JavaLikeRootProvider.class)) {
            FileObject fo = project.getProjectDirectory().getFileObject("src/test/" + rp.kind());
            if (fo != null) {
                toReturn.add(fo);
            }
        }

        URI[] res = project.getResources(true);
        for (int i = 0; i < res.length; i++) {
            FileObject fo = FileUtilities.convertURItoFileObject(res[i]);
            if (fo != null) {
                boolean ok = true;
                //#166655 resource root cannot contain the real java/xxx roots
                for (FileObject form : toReturn) {
                    if (FileUtil.isParentOf(fo, form)) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    toReturn.add(fo);
                }
            }
        }
        return toReturn.toArray(new FileObject[toReturn.size()]);
    }
    
}