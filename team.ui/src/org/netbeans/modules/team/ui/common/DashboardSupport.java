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

package org.netbeans.modules.team.ui.common;

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.netbeans.modules.team.ui.Utilities;
import org.netbeans.modules.team.ui.common.DashboardSupport.DashboardImpl;
import org.netbeans.modules.team.ui.spi.DashboardProvider;
import org.netbeans.modules.team.ui.spi.ProjectHandle;
import org.netbeans.modules.team.ui.spi.TeamServer;
import org.netbeans.modules.team.ui.util.treelist.SelectionList;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Stupka
 */
@NbBundle.Messages("A11Y_TeamProjects=Team Projects")
public final class DashboardSupport<P> {
    
    /**
     * Name of the property that will be fired when some change in opened projects
     * in Dashboard occurs. Firing this property doesn't neccessary mean that number
     * of opened project has changed.
     */
    public static final String PROP_OPENED_PROJECTS = "openedProjects"; // NOI18N

    /**
     * fired when user clicks refresh
     */
    public static final String PROP_REFRESH_REQUEST = "refreshRequest";// NOI18N
    
    public static final String PREF_ALL_PROJECTS = "allProjects"; //NOI18N
    public static final String PREF_COUNT = "count"; //NOI18N
    public static final String PREF_ID = "id"; //NOI18N
    
    private final DashboardImpl<P> impl;
    
    public DashboardSupport(TeamServer server, DashboardProvider<P> dashboardProvider) {
         this.impl = Utilities.isMoreProjectsDashboard() ? 
                 new DefaultDashboard<P>(server, dashboardProvider) :
                 new OneProjectDashboard<P>(server, dashboardProvider);
    }

    public void addProject(ProjectHandle<P> pHandle, boolean b, boolean b0) {
        impl.addProject(pHandle, b, b0);
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChange) {
        impl.addPropertyChangeListener(propertyChange);
    }

    public void bookmarkingFinished(ProjectHandle<P> project) {
        impl.bookmarkingFinished(project);
    }

    public void bookmarkingStarted(ProjectHandle<P> project) {
        impl.bookmarkingStarted(project);
    }

    public void deletingFinished() {
        impl.deletingFinished();
    }

    public void deletingStarted() {
        impl.deletingStarted();
    }

    public JComponent getComponent() {
        return impl.getComponent();
    }

    public DashboardProvider<P> getDashboardProvider() {
        return impl.getDashboardProvider();
    }

    public ProjectHandle<P>[] getProjects(boolean b) {
        return impl.getProjects(b);
    }

    public TeamServer getServer() {
        return impl.getServer();
    }

    public void myProjectsProgressFinished() {
        impl.myProjectsProgressFinished();
    }

    public void myProjectsProgressStarted() {
        impl.myProjectsProgressStarted();
    }

    public void refreshMemberProjects(boolean b) {
        impl.refreshMemberProjects(b);
    }

    public void removeProject(ProjectHandle<P> project) {
        impl.removeProject(project);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        impl.removePropertyChangeListener(propertyChangeListener);
    }

    public void selectAndExpand(ProjectHandle<P> project) {
        impl.selectAndExpand(project);
    }

    public void xmppFinsihed() {
        impl.xmppFinsihed();
    }

    public void xmppStarted() {
        impl.xmppStarted();
    }
    
    public SelectionList getProjectsList( boolean forceRefresh ) {
        return impl.getProjectsList( forceRefresh );
    }
    
    interface DashboardImpl<P> {

        void addProject(ProjectHandle<P> pHandle, boolean b, boolean b0);

        void addPropertyChangeListener(PropertyChangeListener propertyChange);

        void bookmarkingFinished(ProjectHandle<P> project);

        void bookmarkingStarted(ProjectHandle<P> project);

        void deletingFinished();

        void deletingStarted();

        JComponent getComponent();

        DashboardProvider<P> getDashboardProvider();

        ProjectHandle<P>[] getProjects(boolean b);

        TeamServer getServer();

        void myProjectsProgressFinished();

        void myProjectsProgressStarted();

        void refreshMemberProjects(boolean b);

        void removeProject(ProjectHandle<P> project);

        void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);

        void selectAndExpand(ProjectHandle<P> project);

        void xmppFinsihed();

        void xmppStarted();

        SelectionList getProjectsList( boolean forceRefresh );
        
    }
}
