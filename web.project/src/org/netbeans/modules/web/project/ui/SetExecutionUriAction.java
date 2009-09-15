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

package org.netbeans.modules.web.project.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.TypeElement;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.NodeAction;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.dd.api.web.WebAppMetadata;
import org.netbeans.modules.j2ee.dd.api.web.model.ServletInfo;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.web.project.ProjectWebModule;
import org.netbeans.modules.web.project.WebAppMetadataHelper;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
/** 
*
* @author Milan Kuchtiak
* @author ads
*/
public final class SetExecutionUriAction extends NodeAction {
    
    private static final String IS_SERVLET_FILE = 
                "org.netbeans.modules.web.IsServletFile";            // NOI18N
    public static final String ATTR_EXECUTION_URI = "execution.uri"; //NOI18N
    // Added as  fix for IZ#171708.
    private final MarkerClass myMarker = new MarkerClass();
    
    /**
     * Creates and starts a thread for generating documentation
     */
    protected void performAction(Node[] activatedNodes) {
        if ((activatedNodes != null) && (activatedNodes.length == 1)) {
            if (activatedNodes[0] != null) {
                DataObject data = (DataObject)activatedNodes[0].getCookie(DataObject.class);
                if (data != null) {
                    FileObject servletFo = data.getPrimaryFile();
                    WebModule webModule = WebModule.getWebModule(servletFo);
                    String[] urlPatterns = getServletMappings(webModule, servletFo);
                    if (urlPatterns!=null && urlPatterns.length>0) {
                        String oldUri = (String)servletFo.getAttribute(ATTR_EXECUTION_URI);
                        ServletUriPanel uriPanel = new ServletUriPanel(urlPatterns,oldUri,false);
                        DialogDescriptor desc = new DialogDescriptor(uriPanel,
                            NbBundle.getMessage (SetExecutionUriAction.class, "TTL_setServletExecutionUri"));
                        Object res = DialogDisplayer.getDefault().notify(desc);
                        if (res.equals(NotifyDescriptor.YES_OPTION)) {
                            try {
                                servletFo.setAttribute(ATTR_EXECUTION_URI,uriPanel.getServletUri());
                            } catch (java.io.IOException ex){}
                        } else return;
                    } else {
                        String mes = java.text.MessageFormat.format (
                                NbBundle.getMessage (SetExecutionUriAction.class, "TXT_missingServletMappings"),
                                new Object [] {servletFo.getName()}); //NOI18N
                        NotifyDescriptor desc = new NotifyDescriptor.Message(mes,NotifyDescriptor.Message.ERROR_MESSAGE);
                        DialogDisplayer.getDefault().notify(desc);
                    }
                }
            }
        }
    }
    
    protected boolean enable (Node[] activatedNodes) {
        if ((activatedNodes != null) && (activatedNodes.length == 1)) {
            if (activatedNodes[0] != null) {
                DataObject data = (DataObject)activatedNodes[0].getCookie(DataObject.class);
                if (data != null) {
                    FileObject javaClass = data.getPrimaryFile();
                    WebModule webModule = WebModule.getWebModule(javaClass);
                    if ( servletFilesScanning( webModule, javaClass ) ){
                        return false;
                    }
                    String mimetype = javaClass.getMIMEType();
                    if ( !"text/x-java".equals(mimetype) ){     // NOI18N
                        return false;
                    }
                    Boolean servletAttr = (Boolean)javaClass.getAttribute(IS_SERVLET_FILE);
                    if (!Boolean.TRUE.equals(servletAttr)) {
                        boolean isServletFile = isServletFile(webModule, 
                                javaClass, false );
                        if (isServletFile) {
                            try {
                                javaClass.setAttribute(IS_SERVLET_FILE, Boolean.TRUE); 
                            } catch (java.io.IOException ex) {
                                //we tried
                            }
                        }
                        servletAttr = Boolean.valueOf(isServletFile);
                    }
                    return Boolean.TRUE.equals(servletAttr);
                }
            }
        }
        return false;
    }

    /**
     * Help context where to find more about the action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx (SetExecutionUriAction.class);
    }

    /**
     * Human presentable name of the action. This should be presented as an item in a menu.
     * @return the name of the action
     */
    public String getName() {
        return NbBundle.getMessage(SetExecutionUriAction.class, "LBL_serveltExecutionUriAction");
    }
    
    /**
     * The action's icon location.
     * @return the action's icon location
     */
    @Override
    protected String iconResource () {
        return "org/netbeans/modules/web/project/ui/resources/servletUri.gif"; // NOI18N
    }
    
    @Override
    protected boolean asynchronous() {
        return false;
    }
    
    public static String[] getServletMappings(WebModule webModule, FileObject javaClass) {
        if (webModule == null)
            return null;
        
        ClassPath classPath = ClassPath.getClassPath (javaClass, ClassPath.SOURCE);
        String className = classPath.getResourceName(javaClass,'.',false);

        try {
            List<ServletInfo> servlets =
                    WebAppMetadataHelper.getServlets(webModule.getMetadataModel());
            List<String> mappingList = new ArrayList<String>();
            for (ServletInfo si : servlets) {
                if (className.equals(si.getServletClass())) {
                    mappingList.addAll(si.getUrlPatterns());
                }
            }
            String[] mappings = new String[mappingList.size()];
            mappingList.toArray(mappings);
            return mappings;
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Method check if initial servlet scanning has been started.
     * It's done via setting special mark for project ( actually 
     * for  ProjectWebModule ).
     * If this mark is present initial scanning is either in progress
     * or finished.
     * <code>myMarker</code> is set up if scanning was started.
     * Any other instance of MarkerClass signals that scanning 
     * is already finished.
     * 
     * Fix for IZ#171708 - AWT thread blocked for 15766 ms. (project not usable after opening - fresh userdir)
     */
    private boolean servletFilesScanning( final WebModule webModule , 
            final FileObject fileObject ) 
    {
        Project project = FileOwnerQuery.getOwner( fileObject );
        if ( project != null ){
            final ProjectWebModule prjWebModule = project.getLookup().lookup( 
                    ProjectWebModule.class);
            if (prjWebModule == null) {
                return false;
            }
            MarkerClass marker = prjWebModule.getLookup().lookup( 
                    MarkerClass.class );
            if ( marker == null ){
                Runnable runnable = new Runnable(){
                    public void run() {
                        isServletFile(webModule, fileObject , true );
                        prjWebModule.removeCookie( myMarker);
                        prjWebModule.addCookie( new MarkerClass() );
                    }
                };
                if ( prjWebModule.getLookup().lookup( MarkerClass.class ) == null ){
                    /* Double check . It's not good but not fatal. 
                     * In the worst case we will start several initial scanning.
                     */
                    RequestProcessor.getDefault().post(runnable);
                    prjWebModule.addCookie( myMarker );
                 }
                return true;
            }
            else if ( marker == myMarker ){
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }

    /*
     * Modified as fix for IZ#171708. 
     */
    private static boolean isServletFile(WebModule webModule, FileObject javaClass,
            boolean initialScan) 
    {
        if (webModule == null ) {
            return false;
        }
        
        ClassPath classPath = ClassPath.getClassPath (javaClass, ClassPath.SOURCE);
        if (classPath == null) {
            return false;
        }
        String className = classPath.getResourceName(javaClass,'.',false);
        if (className == null) {
            return false;
        }
        
        try {
            MetadataModel<WebAppMetadata> metadataModel = webModule
                    .getMetadataModel();
            boolean result = false;
            if ( initialScan || metadataModel.isReady()) {
                List<ServletInfo> servlets = WebAppMetadataHelper
                        .getServlets(metadataModel);
                List<String> servletClasses = new ArrayList<String>( servlets.size() );
                for (ServletInfo si : servlets) {
                    if (className.equals(si.getServletClass())) {
                        result =  true;
                    }
                    else {
                        servletClasses.add( si.getServletClass() );
                    }
                }
                setServletClasses( servletClasses,  javaClass , initialScan);
            }
            return result;
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    /*
     * Created as  fix for IZ#171708 - AWT thread blocked for 15766 ms. (project not usable after opening - fresh userdir)
     */
    private static void setServletClasses( final List<String> servletClasses, 
            final FileObject orig, boolean initial )
    {
        if ( initial ){
            JavaSource javaSource = JavaSource.forFileObject( orig );
            if ( javaSource == null) {
                return;
            }
            try {
            javaSource.runUserActionTask( new Task<CompilationController>(){
                public void run(CompilationController controller) throws Exception {
                    controller.toPhase( Phase.ELEMENTS_RESOLVED );
                    for( String servletClass : servletClasses){
                        TypeElement typeElem = controller.getElements().
                            getTypeElement( servletClass);
                        if ( typeElem == null ){
                            continue;
                        }
                        ElementHandle<TypeElement> handle = 
                            ElementHandle.create( typeElem );
                        FileObject fileObject = SourceUtils.getFile( handle, 
                                controller.getClasspathInfo());
                        if ( fileObject != null && !Boolean.TRUE.equals(
                                fileObject.getAttribute(IS_SERVLET_FILE)))
                        {
                            fileObject.setAttribute(IS_SERVLET_FILE, Boolean.TRUE); 
                        }
                    }
                }
            }, true);
            }
            catch(IOException e ){
                e.printStackTrace();
            }
        }
        else {
            Runnable runnable = new Runnable() {
                
                public void run() {
                    setServletClasses(servletClasses, orig, true);
                }
            };
            RequestProcessor.getDefault().post(runnable);
        }
    }

    /*
     * Created as  fix for IZ#171708 = AWT thread blocked for 15766 ms. (project not usable after opening - fresh userdir) 
     */
    private class MarkerClass {
    }
}
