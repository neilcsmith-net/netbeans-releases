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
package org.netbeans.modules.maven.jaxws.wizards;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import org.apache.maven.model.Plugin;
import org.netbeans.modules.maven.api.PluginPropertyUtils;
import org.netbeans.modules.maven.api.customizer.ModelHandle;
import org.netbeans.modules.maven.jaxws.MavenModelUtils;
import org.netbeans.modules.maven.jaxws.WSUtils;
import org.netbeans.modules.maven.spi.customizer.ModelHandleUtils;
import org.netbeans.modules.websvc.api.support.ClientCreator;
import java.io.IOException;

import org.netbeans.api.project.Project;

import org.netbeans.modules.websvc.jaxws.light.api.JAXWSLightSupport;
import org.netbeans.modules.websvc.jaxws.light.api.JaxWsService;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author Milan Kuchtiak
 */
public class JaxWsClientCreator implements ClientCreator {

    private Project project;
    private WizardDescriptor wiz;

    /**
     * Creates a new instance of WebServiceClientCreator
     */
    public JaxWsClientCreator(Project project, WizardDescriptor wiz) {
        this.project = project;
        this.wiz = wiz;
    }

    public void createClient() throws IOException {
        JAXWSLightSupport jaxWsSupport = JAXWSLightSupport.getJAXWSLightSupport(project.getProjectDirectory());
        String wsdlUrl = (String)wiz.getProperty(WizardProperties.WSDL_DOWNLOAD_URL);
        String filePath = (String)wiz.getProperty(WizardProperties.WSDL_FILE_PATH);
        //Boolean useDispatch = (Boolean) wiz.getProperty(ClientWizardProperties.USEDISPATCH);
        //if (wsdlUrl==null) wsdlUrl = "file:"+(filePath.startsWith("/")?filePath:"/"+filePath); //NOI18N
        if(wsdlUrl == null) {
            wsdlUrl = FileUtil.toFileObject(FileUtil.normalizeFile(new File(filePath))).getURL().toExternalForm();
        }
        FileObject localWsdlFolder = jaxWsSupport.getLocalWsdlFolder(true);
        if (localWsdlFolder != null) {
            FileObject wsdlFo = null;
            try {
                wsdlFo = WSUtils.retrieveResource(
                        localWsdlFolder,
                        new URI(wsdlUrl));
            } catch (URISyntaxException ex) {
                //ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                String mes = NbBundle.getMessage(JaxWsClientCreator.class, "ERR_IncorrectURI", wsdlUrl); // NOI18N
                NotifyDescriptor desc = new NotifyDescriptor.Message(mes, NotifyDescriptor.Message.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(desc);
            } catch (UnknownHostException ex) {
                //ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                String mes = NbBundle.getMessage(JaxWsClientCreator.class, "ERR_UnknownHost", ex.getMessage()); // NOI18N
                NotifyDescriptor desc = new NotifyDescriptor.Message(mes, NotifyDescriptor.Message.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(desc);
            } catch (IOException ex) {
                //ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                String mes = NbBundle.getMessage(JaxWsClientCreator.class, "ERR_WsdlRetrieverFailure", wsdlUrl); // NOI18N
                NotifyDescriptor desc = new NotifyDescriptor.Message(mes, NotifyDescriptor.Message.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(desc);
            }
            if (wsdlFo != null) {
                MavenModelUtils.addJaxws21Library(project);
                String relativePath = FileUtil.getRelativePath(localWsdlFolder, wsdlFo);
                JaxWsService service = new JaxWsService(relativePath, false);
                
                String[] filepaths = PluginPropertyUtils.getPluginPropertyList(project, "org.codehaus.mojo", "jaxws-maven-plugin", "wsdlFiles", "wsdlFile",
                    "wsimport");
                              
                try {
                    ModelHandle mavenHandle = ModelHandleUtils.createModelHandle(project);
                    if (mavenHandle != null) {
                        Plugin jaxWsPlugin = MavenModelUtils.getJaxWSPlugin(mavenHandle);
                        if (jaxWsPlugin == null) {
                            // adding plugin
                            jaxWsPlugin = MavenModelUtils.addJaxWSPlugin(mavenHandle);
                        }
                        if (jaxWsPlugin != null) {
                            // writing wsdlFile to plugin
                            MavenModelUtils.addWsdlFile(mavenHandle, relativePath);
                        }
                        ModelHandleUtils.writeModelHandle(mavenHandle, project);
                        
                    }
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
