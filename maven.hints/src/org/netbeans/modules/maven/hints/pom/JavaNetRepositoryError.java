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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.hints.pom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.NbEditorUtilities;
import static org.netbeans.modules.maven.hints.pom.Bundle.*;
import org.netbeans.modules.maven.hints.pom.spi.Configuration;
import org.netbeans.modules.maven.hints.pom.spi.POMErrorFixProvider;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.modules.maven.model.pom.Profile;
import org.netbeans.modules.maven.model.pom.Repository;
import org.netbeans.modules.maven.model.pom.RepositoryContainer;
import org.netbeans.modules.xml.xam.Model;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.text.Line;
import org.openide.util.NbBundle;

/**
 *
 * @author mkleint
 */
public class JavaNetRepositoryError implements POMErrorFixProvider {
    static final String PROP_SELECTED = "selectedOnly";
    static final String PROP_URLS = "urls";
    
    static final String DEFAULT_URLS = "http://download.java.net/maven/2/ http://download.java.net/maven/1/ ";
    
    private static final Logger LOG = Logger.getLogger(JavaNetRepositoryError.class.getName());
    
    private final Configuration configuration;
    private JComponent component;

    @NbBundle.Messages({
        "TIT_JavaNetRepositoryError=Uses blacklisted repository",
        "DESC_JavaNetRepositoryError=Use of blacklisted repositories, repositories merged into central like java.net repositories or repositories that should not be used because they contain wrong artifacts etc."
    })
    public JavaNetRepositoryError() {
        configuration = new Configuration("JavaNetRepositoryError", //NOI18N
                TIT_JavaNetRepositoryError(),
                DESC_JavaNetRepositoryError(),
                true, Configuration.HintSeverity.WARNING);
    }


    @Override
    public List<ErrorDescription> getErrorsForDocument(POMModel model, Project prj) {
        assert model != null;
        List<ErrorDescription> toRet = new ArrayList<ErrorDescription>();
        if (prj == null) {
            return toRet;
        }
        
        checkRepositoryList(model.getProject().getRepositories(), model, model.getProject(), false, toRet);
        checkRepositoryList(model.getProject().getPluginRepositories(), model, model.getProject(), true, toRet);
        List<Profile> profiles = model.getProject().getProfiles();
        if (profiles != null) {
            for (Profile prof : profiles) {
                checkRepositoryList(prof.getRepositories(), model, prof, false, toRet);
                checkRepositoryList(prof.getPluginRepositories(), model, prof, true, toRet);
            }
        }
        return toRet;

    }
    @NbBundle.Messages("TXT_UsesJavanetRepository=References a blacklisted repository")
    private void checkRepositoryList( List<Repository> repositories, POMModel model, RepositoryContainer container, boolean isPlugin, List<ErrorDescription> toRet) {
        if (repositories != null) {
            boolean justSelected = configuration.getPreferences().getBoolean(PROP_SELECTED, true);
            Set<String> forbidden = getForbidden();
            for (Repository rep : repositories) {
                String url = rep.getUrl();
                if (url != null) {
                    if (!url.endsWith("/")) {
                        url = url + "/"; //just to make queries consistent
                    }
                    if (!justSelected || forbidden.contains(url)) {
                        int position = rep.findChildElementPosition(model.getPOMQNames().URL.getQName());
                        try {
                            Line line = NbEditorUtilities.getLine(model.getBaseDocument(), position, false);
                            OverrideFix basefix = new OverrideFix(rep, container, isPlugin);
                            toRet.add(ErrorDescriptionFactory.createErrorDescription(
                                                            configuration.getSeverity(configuration.getPreferences()).toEditorSeverity(),
                                                     TXT_UsesJavanetRepository(),
                                                     Collections.<Fix>singletonList(ErrorDescriptionFactory.attachSubfixes(basefix, Collections.singletonList(new Configure(configuration)))),
                                                     model.getBaseDocument(), line.getLineNumber() + 1));
                        } catch (IndexOutOfBoundsException e) {
                            LOG.log(Level.INFO, "wrong repository pos in model for : " + url, e);
                        }
                    }
                }
            }
        }
    }


    @Override
    public JComponent getCustomizer(Preferences preferences) {
        if (component == null) {
            component = new JavaNetRepositoryErrorCustomizer(preferences);
        }
        return component;
    }
    
    @Override
    public String getSavedValue(JComponent customCustomizer, String key) {
        return ((JavaNetRepositoryErrorCustomizer) customCustomizer).getSavedValue(key);
    }

    @Override
    public void cancel() {
        component = null;
    }
    
    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    private Set<String> getForbidden() {
        String urls = configuration.getPreferences().get(PROP_URLS, DEFAULT_URLS);
        HashSet<String> toRet = new HashSet<String>();
        for (String s : urls.split("([\\s])+")) {
            s = s.trim();
            if (!s.endsWith("/")) {
                s = s + "/";
            }
            toRet.add(s);
        }
        return toRet;
    }
    
    private static class OverrideFix implements Fix, Runnable {
        private final Repository repository;
        private final RepositoryContainer container;
        private final boolean pluginRepo;

        OverrideFix(Repository plg, RepositoryContainer container, boolean isPlugin) {
            repository = plg;
            this.container = container;
            this.pluginRepo = isPlugin;
        }

        @Override
        @NbBundle.Messages("TEXT_UseJavanetRepository=Remove blacklisted repository declaration")
        public String getText() {
            return TEXT_UseJavanetRepository();
        }

        @Override
        public void run() {
            if (pluginRepo) {
                container.removePluginRepository(repository);
            } else {
                container.removeRepository(repository);
            }
        }

        @Override
        public ChangeInfo implement() throws Exception {
            ChangeInfo info = new ChangeInfo();
            POMModel mdl = repository.getModel();
            if (!mdl.getState().equals(Model.State.VALID)) {
                return info;
            }
            PomModelUtils.implementInTransaction(mdl, this);
            return info;
        }
    }
    
    @NbBundle.Messages(
            {"# {0} - hint name", 
             "TXT_Configure=Configure \"{0}\" hint"})
    private static class Configure implements Fix {
        private final Configuration config;

        Configure(Configuration configuration) {
            this.config = configuration;
        }
        
        @Override
        public String getText() {
            return TXT_Configure(config.getDisplayName());
        }

        @Override
        public ChangeInfo implement() throws Exception {
            OptionsDisplayer.getDefault().open("Editor/Hints/text/x-maven-pom+xml/" + config.getId());
            return new ChangeInfo();
        }
        
    }

}
