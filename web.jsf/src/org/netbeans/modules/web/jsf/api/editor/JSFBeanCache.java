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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.web.jsf.api.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModelAction;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModelException;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.jsf.api.metamodel.FacesManagedBean;
import org.netbeans.modules.web.jsf.api.metamodel.JsfModel;
import org.netbeans.modules.web.jsf.api.metamodel.JsfModelFactory;
import org.netbeans.modules.web.jsf.api.metamodel.ModelUnit;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Petr Pisl
 * @author ads
 */
public class JSFBeanCache {
    
    public static List<FacesManagedBean> getBeans(WebModule webModule) {
        final List<FacesManagedBean> beans = new ArrayList<FacesManagedBean>();
        /* Old implementation based on several models over faces-config.xml files.
         * 
         * FileObject[] files = null; 
        
        
        if (webModule != null) {
            files = ConfigurationUtils.getFacesConfigFiles(webModule);
        }
        
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                    JSFConfigModel model = ConfigurationUtils.getConfigModel(files[i], true);
                    if (model != null) {
                        FacesConfig facesConfig = model.getRootComponent();
                        if (facesConfig != null) {
                            Collection<ManagedBean> managedBeans = facesConfig.getManagedBeans();
                            for (Iterator<ManagedBean> it = managedBeans.iterator(); it.hasNext();) {
                                beans.add(it.next());   
                            }
                        }
                    }
            }
        }*/
        MetadataModel<JsfModel> model = getModel( webModule );
        if ( model == null){
            return beans;
        }
        try {
            model.runReadAction( new MetadataModelAction<JsfModel, Void>() {

                public Void run( JsfModel model ) throws Exception {
                    List<FacesManagedBean> managedBeans = model.getElements( 
                            FacesManagedBean.class);
                    beans.addAll( managedBeans );
                    return null;
                }
            });
        }
        catch (MetadataModelException e) {
            LOG.log( Level.WARNING , e.getMessage(), e );
        }
        catch (IOException e) {
            LOG.log( Level.WARNING , e.getMessage(), e );
        }
        return beans;
    }
    
    private static synchronized MetadataModel<JsfModel> getModel( WebModule module ){
        MetadataModel<JsfModel> model = MODELS.get( module );
        if ( model == null ){
            ModelUnit unit = getUnit( module );
            if ( unit == null ){
                return null;
            }
            model = JsfModelFactory.createMetaModel( unit );
            MODELS.put(module, model);
        }
        return model;
    }
    
    private static ModelUnit getUnit( WebModule module ) {
        if ( module == null ){
            return null;
        }
        FileObject fileObject = getFileObject( module );
        Project project = FileOwnerQuery.getOwner( fileObject );
        if ( project == null ){
            return null;
        }
        ClassPath boot = getClassPath( project , ClassPath.BOOT);
        ClassPath compile = getClassPath(project, ClassPath.COMPILE );
        ClassPath src = getClassPath(project , ClassPath.SOURCE);
        return ModelUnit.create(boot, compile, src, module);
    }
    
    private static FileObject getFileObject( WebModule module ) {
        FileObject fileObject = module.getDocumentBase();
        if ( fileObject != null ){
            return fileObject;
        }
        fileObject = module.getDeploymentDescriptor();
        if ( fileObject != null ){
            return fileObject;
        }
        fileObject = module.getWebInf();
        if ( fileObject != null ){
            return fileObject;
        }
        FileObject[] fileObjects = module.getJavaSources();
        if ( fileObjects!= null){
            for (FileObject source : fileObjects) {
                if ( source != null ){
                    return source;
                }
            }
        }
        return null;
    }

    private static ClassPath getClassPath( Project project, String type ) {
        ClassPathProvider provider = project.getLookup().lookup( 
                ClassPathProvider.class);
        if ( provider == null ){
            return null;
        }
        Sources sources = project.getLookup().lookup(Sources.class);
        if ( sources == null ){
            return null;
        }
        SourceGroup[] sourceGroups = sources.getSourceGroups( 
                JavaProjectConstants.SOURCES_TYPE_JAVA );
        ClassPath[] paths = new ClassPath[ sourceGroups.length];
        int i=0;
        for (SourceGroup sourceGroup : sourceGroups) {
            FileObject rootFolder = sourceGroup.getRootFolder();
            paths[ i ] = provider.findClassPath( rootFolder, type);
        }
        return ClassPathSupport.createProxyClassPath( paths );
    }

    private static final Map<WebModule, MetadataModel<JsfModel>> MODELS = 
        new WeakHashMap<WebModule, MetadataModel<JsfModel>>();
    
    private static final Logger LOG = Logger.getLogger( 
            JSFBeanCache.class.getCanonicalName() );
}
