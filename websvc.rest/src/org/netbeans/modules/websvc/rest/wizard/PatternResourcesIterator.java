/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.modules.websvc.rest.wizard;

import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.websvc.api.support.LogUtils;
import org.netbeans.modules.websvc.rest.RestUtils;
import org.netbeans.modules.websvc.rest.codegen.Constants.HttpMethodType;
import org.netbeans.modules.websvc.rest.codegen.Constants.MimeType;
import org.netbeans.modules.websvc.rest.codegen.GenericResourceGenerator;
import org.netbeans.modules.websvc.rest.codegen.model.GenericResourceBean;
import org.netbeans.modules.websvc.rest.spi.RestSupport;
import org.netbeans.modules.websvc.rest.support.SourceGroupSupport;
import org.netbeans.modules.websvc.rest.wizard.PatternResourcesSetupPanel.Pattern;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Generic (non-entities) REST Web Service wizard
 *
 * @author Nam Nguyen
 */
public class PatternResourcesIterator implements WizardDescriptor.ProgressInstantiatingIterator<WizardDescriptor> {
    private WizardDescriptor wizard;
    private int current;
    private transient AbstractPanel[] panels;
 
    @Override
    public Set instantiate() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set instantiate(ProgressHandle pHandle) throws IOException {
        final Set<FileObject> result = new HashSet<FileObject>();
        try {
            Project project = Templates.getProject(wizard);
            
            final RestSupport restSupport = project.getLookup().lookup(RestSupport.class);
            String restAppPackage = (String) wizard.getProperty(WizardProperties.APPLICATION_PACKAGE);
            String restAppClass = (String) wizard.getProperty(WizardProperties.APPLICATION_CLASS);
            
            pHandle.start();
            
            pHandle.progress(NbBundle.getMessage(PatternResourcesIterator.class,
                    "MSG_EnableRestSupport"));                  // NOI18N     
            
            boolean useJersey = Boolean.TRUE.equals(wizard.getProperty(WizardProperties.USE_JERSEY));
            if (!useJersey) {
                RestSupport.RestConfig.IDE.setAppClassName(restAppPackage+"."+restAppClass); //NOI18N
            }
            if ( restSupport!= null ){
                restSupport.ensureRestDevelopmentReady(useJersey ?
                        RestSupport.RestConfig.DD : RestSupport.RestConfig.IDE);
            }
            
            FileObject tmpTargetFolder = Templates.getTargetFolder(wizard);

            SourceGroup sourceGroup = (SourceGroup) wizard.getProperty(WizardProperties.SOURCE_GROUP);
            if (tmpTargetFolder == null) {
                String targetPackage = (String) wizard.getProperty(WizardProperties.TARGET_PACKAGE);
                tmpTargetFolder = SourceGroupSupport.getFolderForPackage(sourceGroup, targetPackage, true);
            }

            final FileObject targetFolder = tmpTargetFolder;
            final GenericResourceBean[] resourceBeans = getResourceBeans(wizard);
    
            // create application config class if required
            final FileObject restAppPack = restAppPackage == null ? null :  
                SourceGroupSupport.getFolderForPackage(sourceGroup, restAppPackage, true);
            final String appClassName = restAppClass;
            
            try {
                for (GenericResourceBean bean : resourceBeans) {
                    result.addAll(new GenericResourceGenerator(targetFolder, 
                            bean).generate(pHandle));
                }
                if (restSupport != null && restAppPack != null && appClassName != null && !useJersey) {
                    FileObject fo = RestUtils.createApplicationConfigClass(restSupport, restAppPack, appClassName);
                    if (fo != null) {
                        // open generated Application subclass too:
                        result.add(fo);
                    }
                }
                if (restSupport != null) {
                    restSupport.configure(
                            wizard.getProperty(
                                    WizardProperties.RESOURCE_PACKAGE).toString());
                }
                for (FileObject fobj : result) {
                    DataObject dobj = DataObject.find(fobj);
                    EditorCookie cookie = dobj.getCookie(EditorCookie.class);
                    cookie.open();
                }
            } catch(Exception iox) {
                Exceptions.printStackTrace(iox);
            } finally {
                pHandle.finish();
            }

            // logging usage of wizard
            Object[] params = new Object[5];
            params[0] = LogUtils.WS_STACK_JAXRS;
            params[1] = project.getClass().getName();
            J2eeModule j2eeModule = RestUtils.getJ2eeModule(project);
            params[2] = j2eeModule == null ? null : j2eeModule.getModuleVersion()+"(WAR)"; //NOI18N
            params[3] = "REST FROM PATTERNS"; //NOI18N
            params[4] = ((Pattern)wizard.getProperty(WizardProperties.PATTERN_SELECTION)).toString();
            LogUtils.logWsWizard(params);

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return result;
    }
    
    private GenericResourceBean[] getResourceBeans(WizardDescriptor wizard) {
        Pattern p = (Pattern) wizard.getProperty(WizardProperties.PATTERN_SELECTION);
        if (p == Pattern.CONTAINER) {
            return getContainerItemBeans(wizard, GenericResourceBean.CONTAINER_METHODS);
        } else if (p == Pattern.STANDALONE) {
            return getPlainResourceBeans(wizard);
        } else if (p == Pattern.CLIENTCONTROLLED) {
            return getContainerItemBeans(wizard, GenericResourceBean.CLIENT_CONTROL_CONTAINER_METHODS);
        } else {
            throw new IllegalArgumentException("Invalid pattern "+p);
        }
    }
    
    private GenericResourceBean[] getPlainResourceBeans(WizardDescriptor wizard) {
        String className = (String) wizard.getProperty(WizardProperties.RESOURCE_CLASS);
        String packageName = (String) wizard.getProperty(WizardProperties.RESOURCE_PACKAGE);
        String uriTemplate = (String) wizard.getProperty(WizardProperties.RESOURCE_URI);
        MimeType[] mimeTypes = (MimeType[]) wizard.getProperty(WizardProperties.MIME_TYPES);
        String[] types = Util.ensureTypes((String[]) wizard.getProperty(WizardProperties.REPRESENTATION_TYPES));
        
        HttpMethodType[] methods = GenericResourceBean.STAND_ALONE_METHODS;
        GenericResourceBean bean = new GenericResourceBean(className, packageName, uriTemplate, mimeTypes, types, methods);
        
        return new GenericResourceBean[] { bean };
    }
    
    private GenericResourceBean[] getContainerItemBeans(WizardDescriptor wizard, HttpMethodType[] containerMethods) {
        String className = (String) wizard.getProperty(WizardProperties.ITEM_RESOURCE_CLASS);
        String packageName = (String) wizard.getProperty(WizardProperties.RESOURCE_PACKAGE);
        String uriTemplate = (String) wizard.getProperty(WizardProperties.ITEM_RESOURCE_URI);
        MimeType[] mimeTypes = (MimeType[]) wizard.getProperty(WizardProperties.ITEM_MIME_TYPES);
        String[] types = Util.ensureTypes((String[]) wizard.getProperty(WizardProperties.ITEM_REPRESENTATION_TYPES));
        
        HttpMethodType[] methods = GenericResourceBean.ITEM_METHODS;
        GenericResourceBean bean = new GenericResourceBean(className, packageName, uriTemplate, mimeTypes, types, methods);
        bean.setGenerateUriTemplate(false);
        bean.setRootResource(false);
        
        String containerName = (String) wizard.getProperty(WizardProperties.CONTAINER_RESOURCE_CLASS);
        String containerUri = (String) wizard.getProperty(WizardProperties.CONTAINER_RESOURCE_URI);
        types = Util.ensureTypes((String[]) wizard.getProperty(WizardProperties.CONTAINER_REPRESENTATION_TYPES));
        GenericResourceBean containerBean = new GenericResourceBean(
                containerName, packageName, containerUri, mimeTypes, types, containerMethods);
        containerBean.addSubResource(bean);
        
        return new GenericResourceBean[] { bean, containerBean };
    }
    
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
        wizard.putProperty("NewFileWizard_Title",
                NbBundle.getMessage(PatternResourcesIterator.class, "Templates/WebServices/RestServicesFromPatterns"));
        String step1Name =
                NbBundle.getMessage(PatternResourcesIterator.class, "LBL_Select_Pattern");
        AbstractPanel patternPanel = new PatternSelectionPanel(step1Name, wizard); // NOI18N
        
        String step2Name =
                NbBundle.getMessage(PatternResourcesIterator.class, "LBL_Specify_Resource_Class");
        PatternResourcesSetupPanel containerPanel = new PatternResourcesSetupPanel(step2Name, wizard); // NOI18N
        
        panels = new AbstractPanel[] { patternPanel, containerPanel};
        current = 0;
        String names[] = new String[] { step1Name, step2Name };
        Util.mergeSteps(wizard, panels, names);
        containerPanel.saveStepsAndIndex();
    }

    public void uninitialize(WizardDescriptor wiz) {
        panels = null;
        current = 0;
    }
    
    public AbstractPanel current() {
        return panels[current];
    }
    
    public String name() {
        return NbBundle.getMessage(PatternResourcesIterator.class, "Templates/WebServices/RestServicesFromPatterns");
    }
    
    public boolean hasNext() {
        return current < panels.length - 1;
    }
    
    public boolean hasPrevious() {
        return current > 0;
    }
    
    public void nextPanel() {
        if (! hasNext()) throw new NoSuchElementException();
        if (current() instanceof PatternSelectionPanel) {
            Pattern p = ((PatternSelectionPanel)current()).getSelectedPattern();
            assert panels[current+1] instanceof PatternResourcesSetupPanel : "Expecting GenericRestServicePanel after Pattern panel";
            ((PatternResourcesSetupPanel)panels[current+1]).setCurrentPattern(p);
        }
        current++;
    }
    
    public void previousPanel() {
        if (! hasPrevious()) throw new NoSuchElementException();
        current--;
    }
    
    public void addChangeListener(ChangeListener l) {
    }
    
    public void removeChangeListener(ChangeListener l) {
    }
}
