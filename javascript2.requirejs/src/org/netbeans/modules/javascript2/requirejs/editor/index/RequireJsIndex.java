/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript2.requirejs.editor.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.javascript2.editor.model.TypeUsage;
import org.netbeans.modules.javascript2.editor.spi.model.ModelElementFactory;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;

/**
 *
 * @author Petr Pisl
 */
public class RequireJsIndex {

    private static final Logger LOGGER = Logger.getLogger(RequireJsIndex.class.getSimpleName());

    private final QuerySupport querySupport;
    private static final Map<Project, RequireJsIndex> INDEXES = new WeakHashMap();
    private static boolean areProjectsOpen = false;

    public static RequireJsIndex get(Project project) throws IOException {
        if (project == null) {
            return null;
        }
        synchronized (INDEXES) {
            RequireJsIndex index = INDEXES.get(project);
            if (index == null) {
                if (!areProjectsOpen) {
                    try {
                        // just be sure that the projects are open
                        OpenProjects.getDefault().openProjects().get();
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        areProjectsOpen = true;
                    }
                }
                Collection<FileObject> sourceRoots = QuerySupport.findRoots(project,
                        null /* all source roots */,
                        Collections.<String>emptyList(),
                        Collections.<String>emptyList());
                QuerySupport querySupport = QuerySupport.forRoots(RequireJsIndexer.Factory.NAME, RequireJsIndexer.Factory.VERSION, sourceRoots.toArray(new FileObject[]{}));
                index = new RequireJsIndex(querySupport);
                if (sourceRoots.size() > 0) {
                    INDEXES.put(project, index);
                }
            }
            return index;
        }
    }

    private RequireJsIndex(QuerySupport querySupport) throws IOException {
        this.querySupport = querySupport;
    }
    
    
    public Collection<? extends TypeUsage> getExposedTypes(String module, ModelElementFactory factory) {
        Collection<? extends IndexResult> result = null;
        String moduleName = module;
        String[] parts = moduleName.split("/");
        if (parts.length > 0) {
            moduleName = parts[parts.length - 1];
        }
        try {
            result = querySupport.query(RequireJsIndexer.FIELD_MODULE_NAME, moduleName, QuerySupport.Kind.PREFIX, RequireJsIndexer.FIELD_MODULE_NAME, RequireJsIndexer.FIELD_EXPOSED_TYPES);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (result != null && !result.isEmpty()) {
            System.out.println("Z indexu result: " + result.size());
            Collection<TypeUsage> types = new ArrayList<TypeUsage>();
            for (IndexResult indexResult : result) {
                String[] values = indexResult.getValues(RequireJsIndexer.FIELD_EXPOSED_TYPES);
                String mn = indexResult.getValue(RequireJsIndexer.FIELD_MODULE_NAME);
                if(mn.equals(moduleName)) {
                    for (String stype : values) {
                        String[] typeParts = stype.split(":");
                        String typeName = typeParts[0];
                        int offset = Integer.parseInt(typeParts[1]);
                        boolean resolved = "1".equals(typeParts[2]);
                        types.add(factory.newType(typeName, offset, resolved));
                    }
                }
            }
            return types;
        }
        return Collections.emptyList();
    }
}
