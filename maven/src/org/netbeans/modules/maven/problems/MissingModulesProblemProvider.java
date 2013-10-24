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
package org.netbeans.modules.maven.problems;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.project.Project;
import org.netbeans.modules.autoupdate.ui.api.PluginManager;
import org.netbeans.modules.maven.api.NbMavenProject;
import static org.netbeans.modules.maven.problems.Bundle.*;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.netbeans.spi.project.ui.ProjectProblemResolver;
import org.netbeans.spi.project.ui.ProjectProblemsProvider;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author mkleint
 */
public class MissingModulesProblemProvider implements ProjectProblemsProvider {
    private static final RequestProcessor RP  = new RequestProcessor(MissingModulesProblemProvider.class);
    
    @NbBundle.Messages({
        "ERR_MissingJ2eeModule=Maven Java EE support missing",
        "MSG_MissingJ2eeModule=You are missing the Maven Java EE support module in your installation. "
        + "This means that all EE-related functionality (for example, Deployment, File templates) is missing. "
        + "The most probable cause is that part of the general Java EE support is missing as well. "
        + "Please go to Tools/Plugins and install the plugins related to Java EE."
    })
    @ProjectServiceProvider(service = ProjectProblemsProvider.class, projectType = "org-netbeans-modules-maven")
    public static ProjectProblemsProvider j2ee(Project project) {
        Set<String> packs = new HashSet<String>();
        packs.add(NbMavenProject.TYPE_WAR);
        packs.add(NbMavenProject.TYPE_EAR);
        packs.add(NbMavenProject.TYPE_EJB);
        return new MissingModulesProblemProvider(project, packs, "org.netbeans.modules.maven.j2ee", "org.netbeans.modules.j2ee.kit", ERR_MissingJ2eeModule(), MSG_MissingJ2eeModule());
    }
    @NbBundle.Messages({
        "ERR_MissingApisupportModule=Maven NetBeans Module Projects support missing",
        "MSG_MissingApisupportModule=You are missing the Maven NetBeans Module Projects module in your installation. "
        + "This means that all NetBeans Platform functionality (for example, API wizards, running Platform applications) is missing. "
        + "The most probable cause is that part of the general Platform development support is missing as well. "
        + "Please go to Tools/Plugins and install the plugins related to NetBeans development."
    })
    @ProjectServiceProvider(service = ProjectProblemsProvider.class, projectType = "org-netbeans-modules-maven")
    public static ProjectProblemsProvider apisupport(Project project) {
        Set<String> packs = new HashSet<String>();
        packs.add(NbMavenProject.TYPE_NBM);
        packs.add(NbMavenProject.TYPE_NBM_APPLICATION);
        return new MissingModulesProblemProvider(project, packs, "org.netbeans.modules.maven.apisupport", "org.netbeans.modules.apisupport.kit", ERR_MissingApisupportModule(), MSG_MissingApisupportModule());
    }
    
    
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private final Project project;
    private EnablementListener listener;
    private String lastPackaging;
    private final AtomicBoolean projectListenerSet = new AtomicBoolean(false);
    private final PropertyChangeListener projectListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                if (lastPackaging != null) {
                    if (!lastPackaging.equals(project.getLookup().lookup(NbMavenProject.class).getPackagingType())) {
                        //reset only if packaging changed, that's when maybe the missing modules do or don't matter anymore
                        firePropertyChange();
                    }
                }
            }
        }
    };
    private final Set<String> packagings;
    private final String moduleCodenameBase;
    private final String kitCodeNameBase;
    private final String problemDescription;
    private final String problemName;

    private MissingModulesProblemProvider(Project project, Set<String> packagings, String moduleCodenameBase, String kitCodeNameBase, String errorMessage, String errorDescription) {
        this.project = project;
        this.packagings = packagings;
        this.moduleCodenameBase = moduleCodenameBase;
        this.kitCodeNameBase = kitCodeNameBase;
        this.problemName = errorMessage;
        this.problemDescription = errorDescription;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    @Override
    public Collection<? extends ProjectProblem> getProblems() {
        //lazy adding listener only when someone asks for the problems the first time
        if (projectListenerSet.compareAndSet(false, true)) {
            //TODO do we check only when the project is opened?
            project.getLookup().lookup(NbMavenProject.class).addPropertyChangeListener(projectListener);
        }
        return doIDEConfigChecks();
    }

    public synchronized Collection<ProjectProblem> doIDEConfigChecks() {
        Collection<ProjectProblem> toRet = new ArrayList<ProjectProblem>();
        NbMavenProject nbproject = project.getLookup().lookup(NbMavenProject.class);
        String packaging = nbproject.getPackagingType();
        
        if (packagings.contains(packaging)) {
            //TODO check on lastpackaging to prevent re-calculation
            ModuleInfo moduleInfo = listener != null ? listener.info : findModule(moduleCodenameBase);
            boolean foundModule = moduleInfo != null && moduleInfo.isEnabled();
            if (!foundModule) {
                if (listener == null) {
                    ProjectProblem problem = ProjectProblem.createWarning(problemName, problemDescription, new InstallModulesResolver(kitCodeNameBase));
                    listener = new EnablementListener(moduleInfo, problem);
                    listener.startListening();
                }
                toRet.add(listener.problem);
            } else {
                if (listener != null) {
                    listener.stopListening();
                    listener = null;
                }
            }
        }
        lastPackaging = packaging;
        
        return toRet;
    }

    private void firePropertyChange() {
        support.firePropertyChange(ProjectProblemsProvider.PROP_PROBLEMS, null, null);
    }

    private ModuleInfo findModule(@NonNull String codenamebase) {
        Collection<? extends ModuleInfo> infos = Lookup.getDefault().lookupAll(ModuleInfo.class);
        for (ModuleInfo info : infos) {
            if (codenamebase.equals(info.getCodeNameBase())) {
                return info;
            }
        }
        return null;
    }

    private class EnablementListener implements PropertyChangeListener {

        private final @NullAllowed ModuleInfo info;
        private final @NonNull ProjectProblem problem;

        public EnablementListener(@NullAllowed ModuleInfo info, @NonNull ProjectProblem problem) {
            this.info = info;
            this.problem = problem;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (ModuleInfo.PROP_ENABLED.equals(evt.getPropertyName())) {
                if (info != null && info.isEnabled()) {
                    info.removePropertyChangeListener(this);
                    firePropertyChange();
                }
            }
        }
        
        public void stopListening() {
            if (info != null) {
                info.removePropertyChangeListener(this);
            }
        }

        private void startListening() {
            if (info != null) {
                info.addPropertyChangeListener(this);
            }
        }
    }

    private static class InstallModulesResolver implements ProjectProblemResolver {
        private final String codenamebase;

        public InstallModulesResolver(String codenamebase) {
            this.codenamebase = codenamebase;
        }

        @Override
        public Future<Result> resolve() {
            FutureTask<Result> task = new FutureTask<Result>(new Callable<Result>() {
                @Override
                public Result call() throws Exception {
                    final Result[] res = new Result[1];
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                Object retval = PluginManager.install(Collections.singleton(codenamebase));
                                res[0] = retval == null ? Result.create(Status.RESOLVED) : Result.create(Status.UNRESOLVED);
                            }
                        });
                    } catch (InterruptedException ex) {
                        res[0] = Result.create(Status.UNRESOLVED);
                    } catch (InvocationTargetException ex) {
                        res[0] = Result.create(Status.UNRESOLVED);
                    }
                    return res[0];
                }
            });
            RP.execute(task);
            return task;
        }
    }

}
