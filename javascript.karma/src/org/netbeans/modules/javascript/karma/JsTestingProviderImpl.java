/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javascript.karma;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.javascript.karma.exec.KarmaServers;
import org.netbeans.modules.javascript.karma.preferences.KarmaPreferences;
import org.netbeans.modules.javascript.karma.ui.customizer.KarmaCustomizer;
import org.netbeans.modules.javascript.karma.ui.logicalview.KarmaChildrenList;
import org.netbeans.modules.web.clientproject.api.jstesting.JsTestingProviders;
import org.netbeans.modules.web.clientproject.api.jstesting.TestRunInfo;
import org.netbeans.modules.web.clientproject.spi.jstesting.JsTestingProviderImplementation;
import org.netbeans.modules.web.common.api.WebUtils;
import org.netbeans.spi.project.ui.support.NodeList;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = JsTestingProviderImplementation.class, path = JsTestingProviders.JS_TESTING_PATH, position = 100)
public class JsTestingProviderImpl implements JsTestingProviderImplementation {

    private static final Logger LOGGER = Logger.getLogger(JsTestingProviderImpl.class.getName());


    @Override
    public String getIdentifier() {
        return "Karma"; // NOI18N
    }

    @NbBundle.Messages("JsTestingProviderImpl.displayName=Karma")
    @Override
    public String getDisplayName() {
        return Bundle.JsTestingProviderImpl_displayName();
    }

    @Override
    public boolean isEnabled(Project project) {
        return KarmaPreferences.isEnabled(project);
    }

    @Override
    public void runTests(Project project, TestRunInfo runInfo) {
        KarmaServers.getInstance().runTests(project);
    }

    @Override
    public FileObject fromServer(Project project, URL serverUrl) {
        String serverUrlString = WebUtils.urlToString(serverUrl);
        String prefix = KarmaServers.getInstance().getServerUrl(project, "base/"); // NOI18N
        if (prefix == null) {
            return null;
        }
        assert prefix.endsWith("/") : prefix;
        if (!serverUrlString.startsWith(prefix)) {
            return null;
        }
        String projectRelativePath = serverUrlString.substring(prefix.length());
        try {
            projectRelativePath = URLDecoder.decode(projectRelativePath, "UTF-8"); // NOI18N
        } catch (UnsupportedEncodingException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
        if (!projectRelativePath.isEmpty()) {
            return project.getProjectDirectory().getFileObject(projectRelativePath);
        }
        return null;
    }

    @Override
    public URL toServer(Project project, FileObject projectFile) {
        String prefix = KarmaServers.getInstance().getServerUrl(project, "base/"); // NOI18N
        if (prefix == null) {
            return null;
        }
        assert prefix.endsWith("/") : prefix;
        String relativePath = FileUtil.getRelativePath(project.getProjectDirectory(), projectFile);
        if (relativePath != null) {
            try {
                return new URL(prefix + relativePath);
            } catch (MalformedURLException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
        return null;
    }

    @Override
    public ProjectCustomizer.CompositeCategoryProvider createCustomizer(Project project) {
        return new KarmaCustomizer();
    }

    @Override
    public void notifyEnabled(Project project, boolean enabled) {
        KarmaPreferences.setEnabled(project, enabled);
        if (!enabled) {
            cleanup(project);
        }
    }

    @Override
    public void projectOpened(Project project) {
        // noop
    }

    @Override
    public void projectClosed(Project project) {
        cleanup(project);
    }

    @Override
    public NodeList<Node> createNodeList(Project project) {
        return new KarmaChildrenList(project);
    }

    private void cleanup(Project project) {
        KarmaPreferences.removeFromCache(project);
        KarmaServers.getInstance().stopServer(project, true);
    }

}
