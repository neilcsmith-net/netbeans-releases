/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.websvc.jaxrpc.project;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.PathResourceImplementation;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;

/**
 *
 * @author mkuchtiak
 */
@ProjectServiceProvider(service=ClassPathProvider.class, projectType={
    "org-netbeans-modules-java-j2seproject",
    "org-netbeans-modules-web-project",
    "org-netbeans-modules-j2ee-ejbjarproject",
    "org-netbeans-modules-j2ee-clientproject"
})
public class JaxRpcArtifactsClassPathProvider implements ClassPathProvider {
    private Project project;
    private ClassPath sourceCP, compileCP, bootCP, executeCP;
    
    private static final Logger LOG = Logger.getLogger(JaxRpcArtifactsClassPathProvider.class.getName());
    
    public JaxRpcArtifactsClassPathProvider(Project project) {
        this.project = project;
    }
    
    public ClassPath findClassPath(FileObject file, String type) {
        FileObject clientArtifactsFolder = 
                project.getProjectDirectory().getFileObject("build/generated/wsclient"); //NOI18N
        if (clientArtifactsFolder != null && 
                (file.equals(clientArtifactsFolder) || FileUtil.isParentOf(clientArtifactsFolder,file))) {
            if (ClassPath.SOURCE.equals(type)) {
                if (sourceCP == null) {
                    sourceCP = getClassPath(ClassPath.SOURCE);
                }
                return sourceCP;
            } else if (ClassPath.COMPILE.equals(type)) {
                if (compileCP == null) {
                    compileCP = getClassPath(ClassPath.COMPILE);
                }
                return compileCP;
            } else if (ClassPath.EXECUTE.equals(type)) {
                if (executeCP == null) {
                    executeCP = getClassPath(ClassPath.EXECUTE);
                }
                return executeCP;
            } else if (ClassPath.BOOT.equals(type)) {
                if (bootCP == null) {
                    bootCP = getBootClassPath();
                }
                return bootCP;
            }               
        }
           
        return null;
    }
    
    private ClassPath getClassPath(String classPathType) {
        SourceGroup[] groups = ProjectUtils.getSources(project).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        if (groups.length > 0) {
            return ClassPath.getClassPath(groups[0].getRootFolder(), classPathType);
        }
        return null;
    }
    
    private ClassPath getBootClassPath() {
        Set<URL> cp = new HashSet<URL>();
        // use JAX-RPC API jars supplied with the IDE
        File f = InstalledFileLocator.getDefault().locate("modules/ext/jaxrpc16", null, false); //NOI18N
        File[] files = f.listFiles();
        for (File file:files) {
            try {
                if (file.getName().contains("api") || file.getName().contains("spi")) { //NOI18N
                    cp.add(FileUtil.getArchiveRoot(file.toURI().toURL()));
                }
            } catch (MalformedURLException ex) {
                LOG.severe(ex.getMessage());
            }
        }
        List<PathResourceImplementation> path = new LinkedList<PathResourceImplementation>();
        for (URL url : cp) {
            path.add(ClassPathSupport.createResource(url));
        }
        return ClassPathSupport.createClassPath(path);
    }

}
