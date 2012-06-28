/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.web.jsf.editor.el;

import java.util.Map;
import org.netbeans.modules.html.editor.api.gsf.HtmlParserResult;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.web.jsf.editor.facelets.FaceletsLibraryDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.web.el.spi.ELPlugin;
import org.netbeans.modules.web.el.spi.Function;
import org.netbeans.modules.web.el.spi.ImplicitObject;
import org.netbeans.modules.web.el.spi.ImplicitObjectType;
import org.netbeans.modules.web.el.spi.ResourceBundle;
import org.netbeans.modules.web.jsf.api.editor.JSFResourceBundlesProvider;
import org.netbeans.modules.web.jsf.editor.JsfUtils;
import org.netbeans.modules.web.jsf.editor.facelets.DefaultFaceletLibraries;
import static org.netbeans.modules.web.el.spi.ImplicitObjectType.*;
import org.netbeans.modules.web.el.spi.ResolverContext;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author marekfukala
 */
@ServiceProvider(service=ELPlugin.class)
public class FaceletsELPlugin extends ELPlugin {

    private static final String PLUGIN_NAME = "JSF Facelets EL Plugin"; //NOI18N

    private Collection<ImplicitObject> IMPL_OBJECTS;

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public Collection<String> getMimeTypes() {
        return Collections.singletonList(JsfUtils.XHTML_MIMETYPE);
    }

    @Override
    public synchronized Collection<ImplicitObject> getImplicitObjects(FileObject file) {
        if(!getMimeTypes().contains(file.getMIMEType())) {
            return Collections.emptyList();
        }

        if(IMPL_OBJECTS == null) {
            IMPL_OBJECTS = new ArrayList<ImplicitObject>(9);

            IMPL_OBJECTS.addAll(getScopeObjects());

            IMPL_OBJECTS.add( new FacesContextObject());
            IMPL_OBJECTS.add( new ApplicationObject());
            IMPL_OBJECTS.add( new ComponentObject());
            IMPL_OBJECTS.add( new FlashObject());
            IMPL_OBJECTS.add( new ResourceObject());
            IMPL_OBJECTS.add( new SessionObject());
            IMPL_OBJECTS.add( new ViewObject() );
            IMPL_OBJECTS.add( new JsfImplicitObject("cookie", null, MAP_TYPE) ); //NOI18N
            IMPL_OBJECTS.add( new JsfImplicitObject("cc", null, RAW) ); //NOI18N
            IMPL_OBJECTS.add( new JsfImplicitObject("request", null, OBJECT_TYPE) ); //NOI18N
            IMPL_OBJECTS.add( new JsfImplicitObject("header", null, MAP_TYPE) ); //NOI18N
            IMPL_OBJECTS.add( new JsfImplicitObject("headerValues", null, MAP_TYPE) ); //NOI18N
            IMPL_OBJECTS.add( new JsfImplicitObject("initParam", null, MAP_TYPE) ); //NOI18N
            IMPL_OBJECTS.add( new JsfImplicitObject("param", null, MAP_TYPE) ); //NOI18N
            IMPL_OBJECTS.add( new JsfImplicitObject("paramValues", null, MAP_TYPE) ); //NOI18N
        }


        return IMPL_OBJECTS;
    }

    @Override
    public List<ResourceBundle> getResourceBundles(FileObject file, ResolverContext context) {
        Project project = FileOwnerQuery.getOwner(file);
        if (project == null) {
            return Collections.emptyList();
        }

        // caches bundles if not loaded yet
        if (context.getContent(FaceletsELPlugin.class.getName()) == null) {
            context.setContent(FaceletsELPlugin.class.getName(), JSFResourceBundlesProvider.getResourceBundles(project));
        }

        return (List<ResourceBundle>) context.getContent(FaceletsELPlugin.class.getName());
    }

    /**
     * @return the implicit scope objects, i.e. {@code requestScope, sessionScope} etc.
     */
    private static Collection<ImplicitObject> getScopeObjects() {
        Collection<ImplicitObject> result = new ArrayList<ImplicitObject>(4);
        result.add(new JsfImplicitObject("sessionScope", null, SCOPE_TYPE)); // NOI18N
        result.add(new JsfImplicitObject("applicationScope", null, SCOPE_TYPE)); // NOI18N
        result.add(new JsfImplicitObject("requestScope", null, SCOPE_TYPE));
        result.add(new JsfImplicitObject("viewScope", null, SCOPE_TYPE));
        return result;
    }

    @Override
    public List<Function> getFunctions(FileObject file) {
        List<Function> functions =  new ArrayList<Function>();
        final Map<String, String> namespaces = new HashMap<String, String>();

        try {
            Source source = Source.create(file);
            ParserManager.parse(Collections.singletonList(source), new UserTask() {

                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    Result parseResult = JsfUtils.getEmbeddedParserResult(resultIterator, "text/html"); //NOI18N
                    if (parseResult instanceof HtmlParserResult) {
                        namespaces.putAll(((HtmlParserResult) parseResult).getNamespaces());
                    }
                }
            });
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }

        Map<String, FaceletsLibraryDescriptor> librariesDescriptors = DefaultFaceletLibraries.getInstance().getLibrariesDescriptors();
        for (Map.Entry<String, FaceletsLibraryDescriptor> entry : librariesDescriptors.entrySet()) {
            String currentPrefix = namespaces.get(entry.getKey());
            if (currentPrefix != null) {
                functions.addAll(getFunctionsFromDescriptor(entry.getValue(), currentPrefix));
            }
        }

        return functions;
    }

     private static List<Function> getFunctionsFromDescriptor(FaceletsLibraryDescriptor descriptor, String prefix) {
        List<Function> functions = new ArrayList<Function>();
        for (Map.Entry<String, org.netbeans.modules.web.jsfapi.api.Function> entry : descriptor.getFunctions().entrySet()) {
            org.netbeans.modules.web.jsfapi.api.Function function = entry.getValue();
            functions.add(new Function(
                    prefix + ":" + function.getName(),
                    getReturnTypeForSignature(function.getSignature()),
                    getParametersForSignature(function.getSignature()),
                    function.getDescription()));
        }

        return functions;
    }

    private static String getReturnTypeForSignature(String signature) {
        String returnType = signature.substring(0, signature.indexOf(" ")); //NOI18N
        return getSimpleNameForType(returnType.trim());
    }

    private static List<String> getParametersForSignature(String signature) {
        List<String> params = new ArrayList<String>();
        String paramString = signature.substring(signature.indexOf("(") + 1, signature.indexOf(")")); //NOI18N
        for (String param : paramString.split(",")) { //NOI18N
            params.add(getSimpleNameForType(param.trim()));
        }
        return params;
    }

    private static String getSimpleNameForType(String fqn) {
        return fqn.substring(fqn.lastIndexOf(".") + 1); //NOI18N
    }

    static class FacesContextObject extends JsfImplicitObject {
        public FacesContextObject(){
            super("facesContext", "javax.faces.context.FacesContext", OBJECT_TYPE); //NOI18N
        }
    }

    static class ApplicationObject extends JsfImplicitObject {
        public ApplicationObject(){
            super("application",  null, OBJECT_TYPE); //NOI18N
        }
    }

    static class ComponentObject extends JsfImplicitObject {
        public ComponentObject(){
            super("component", "javax.faces.component.UIComponent", OBJECT_TYPE); //NOI18N
        }
    }

    static class FlashObject extends JsfImplicitObject {
        public FlashObject(){
            super("flash", "javax.faces.context.Flash", OBJECT_TYPE); //NOI18N
        }
    }

    static class ResourceObject extends JsfImplicitObject {
        public ResourceObject(){
            super("resource", "javax.faces.application.ResourceHandler", OBJECT_TYPE); //NOI18N
        }
    }

    static class SessionObject extends JsfImplicitObject {
        public SessionObject(){
            super("session", null, OBJECT_TYPE); //NOI18N
        }
    }

    static class ViewObject extends JsfImplicitObject {
        public ViewObject(){
            super("view", "javax.faces.component.UIViewRoot", OBJECT_TYPE); //NOI18N
        }
    }


    private static class JsfImplicitObject implements ImplicitObject {

        private String name, clazz;
        private ImplicitObjectType type;

        public JsfImplicitObject(String name, String clazz, ImplicitObjectType type) {
            this.name = name;
            this.clazz = clazz;
            this.type = type;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ImplicitObjectType getType() {
            return type;
        }

        @Override
        public String getClazz() {
            return clazz;
        }

    }


}
