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
package org.netbeans.modules.maven.j2ee;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.api.project.Project;
import org.netbeans.modules.javaee.project.api.JavaEEProjectSettings;
import org.netbeans.modules.javaee.project.spi.JavaEEProjectSettingsImplementation;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.j2ee.utils.MavenProjectSupport;
import org.netbeans.modules.web.browser.api.BrowserUISupport;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.util.Exceptions;

/**
 * Implementation of {@link JavaEEProjectSettingsImplementation}.
 *
 * Client shouldn't use this class directly, but access it via {@link JavaEEProjectSettings}.
 *
 * @author Martin Fousek <marfous@netbeans.org>
 */
@ProjectServiceProvider(
    service = {
        JavaEEProjectSettingsImplementation.class
    }, 
    projectType = {
        "org-netbeans-modules-maven"
    }
)
public class JavaEEProjectSettingsImpl implements JavaEEProjectSettingsImplementation {

    private final Project project;

    public JavaEEProjectSettingsImpl(Project project) {
        this.project = project;
    }

    @Override
    public void setProfile(Profile profile) {
        MavenProjectSupport.setSettings(project, MavenJavaEEConstants.HINT_J2EE_VERSION, profile.toPropertiesString(), true);
    }

    @Override
    public void setBrowserID(String browserID) {
        Preferences preferences = MavenProjectSupport.getPreferences(project, false);

        if (browserID == null || "".equals(browserID)) {
            preferences.remove(MavenJavaEEConstants.SELECTED_BROWSER);
        } else {
            preferences.put(MavenJavaEEConstants.SELECTED_BROWSER, browserID);
        }
        try {
            preferences.flush();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void setServerInstanceID(String serverInstanceID) {
        MavenProjectSupport.setSettings(project, MavenJavaEEConstants.HINT_DEPLOY_J2EE_SERVER_ID, serverInstanceID, false);
    }

    @Override
    public Profile getProfile() {
        return Profile.fromPropertiesString(MavenProjectSupport.getSettings(project, MavenJavaEEConstants.HINT_J2EE_VERSION, true));
    }

    @Override
    public String getBrowserID() {
        String selectedBrowser = MavenProjectSupport.getSettings(project, MavenJavaEEConstants.SELECTED_BROWSER, false);
        if (selectedBrowser != null) {
            return selectedBrowser;
        } else {
            return BrowserUISupport.getDefaultBrowserChoice(true).getId();
        }
    }

    @Override
    public String getServerInstanceID() {
        return MavenProjectSupport.getSettings(project, MavenJavaEEConstants.HINT_DEPLOY_J2EE_SERVER_ID, false);
    }
}
