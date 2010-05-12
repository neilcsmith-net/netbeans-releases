/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s): theanuradha@netbeans.org
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.maven.actions;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.project.MavenProject;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * @deprecated Replaced by actions in Artifact viewer window, see #164992
 *
 * @author Anuradha
 */
public class ViewBugTrackerAction extends AbstractAction {

    private Artifact artifact;
    private List<ArtifactRepository> repos;

    public ViewBugTrackerAction(Artifact artifact, List<ArtifactRepository> repos) {
        this.artifact = artifact;
        this.repos = repos;
        putValue(Action.NAME, NbBundle.getMessage(ViewBugTrackerAction.class, "LBL_View_BugTracker"));
//TODO - this call is fairly expensive at times. comment out for now.
        //enable only if url persent
//        MavenProject mp = ActionsUtil.readMavenProject(artifact, repos);
//        setEnabled(mp != null && mp.getIssueManagement() != null && mp.getIssueManagement().getUrl() != null);
        setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        //TODO report progress in ui somehow, allow to cancel?
        RequestProcessor.getDefault().post(new Runnable() {
            @Override
            public void run() {
                IssueManagement im = ActionsUtil.readMavenProject(artifact, repos).getIssueManagement();
                if (im != null && im.getUrl() != null) {
                    try {
                        URLDisplayer.getDefault().showURL(new URL(im.getUrl()));
                    } catch (MalformedURLException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        });
    }
}
