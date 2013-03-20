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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.discovery.wizard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cnd.api.model.CsmModel;
import org.netbeans.modules.cnd.api.model.CsmModelAccessor;
import org.netbeans.modules.cnd.api.project.NativeProject;
import org.netbeans.modules.cnd.discovery.api.ApplicableImpl;
import org.netbeans.modules.cnd.discovery.api.Configuration;
import org.netbeans.modules.cnd.discovery.api.DiscoveryExtensionInterface;
import org.netbeans.modules.cnd.discovery.api.DiscoveryProvider;
import org.netbeans.modules.cnd.discovery.api.DiscoveryProviderFactory;
import org.netbeans.modules.cnd.discovery.api.Progress;
import org.netbeans.modules.cnd.discovery.api.ProjectProperties;
import org.netbeans.modules.cnd.discovery.api.ProjectProxy;
import org.netbeans.modules.cnd.discovery.api.ProviderProperty;
import org.netbeans.modules.cnd.discovery.api.SourceFileProperties;
import org.netbeans.modules.cnd.discovery.projectimport.ImportExecutable;
import org.netbeans.modules.cnd.discovery.projectimport.ImportProject;
import org.netbeans.modules.cnd.discovery.services.DiscoveryManagerImpl;
import org.netbeans.modules.cnd.discovery.wizard.SelectConfigurationPanel.MyProgress;
import org.netbeans.modules.cnd.discovery.wizard.api.ConfigurationFactory;
import org.netbeans.modules.cnd.discovery.wizard.api.ConsolidationStrategy;
import org.netbeans.modules.cnd.discovery.wizard.api.DiscoveryDescriptor;
import org.netbeans.modules.cnd.discovery.wizard.api.ProjectConfiguration;
import org.netbeans.modules.cnd.discovery.wizard.support.impl.DiscoveryProjectGeneratorImpl;
import org.netbeans.modules.cnd.makeproject.api.wizards.IteratorExtension;
import org.netbeans.modules.cnd.modelimpl.csm.core.ModelImpl;
import org.netbeans.modules.cnd.support.Interrupter;
import org.netbeans.modules.cnd.utils.FSPath;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironmentFactory;
import org.netbeans.modules.remote.spi.FileSystemProvider;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexander Simon
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.cnd.makeproject.api.wizards.IteratorExtension.class)
public class DiscoveryExtension implements IteratorExtension, DiscoveryExtensionInterface {
    
    /** Creates a new instance of DiscoveryExtension */
    public DiscoveryExtension() {
    }

    @Override
    public void discoverArtifacts(Map<String, Object> map) {
        DiscoveryDescriptor descriptor = DiscoveryWizardDescriptor.adaptee(map);
        Applicable applicable = isApplicable(descriptor, null, false);
        if (applicable != null) {
            if (applicable.isApplicable()) {
                descriptor.setCompilerName(applicable.getCompilerName());
                descriptor.setDependencies(applicable.getDependencies());
                descriptor.setSearchPaths(applicable.getSearchPaths());
                descriptor.setRootFolder(applicable.getSourceRoot());
                descriptor.setErrors(applicable.getErrors());
            } else {
                descriptor.setErrors(applicable.getErrors());
            }
        }
    }
    
    @Override
    public Set<FileObject> createProject(WizardDescriptor wizard) throws IOException{
        return new ImportProject(wizard).create();
    }

    @Override
    public void apply(Map<String, Object> map, Project project) throws IOException {
        apply(map, project, null);
    }
    
    @Override
    public void apply(Map<String, Object> map, Project project, Interrupter interrupter) throws IOException {
        DiscoveryDescriptor descriptor = DiscoveryWizardDescriptor.adaptee(map);
        descriptor.setProject(project);
        DiscoveryProjectGeneratorImpl generator = new DiscoveryProjectGeneratorImpl(descriptor);
        generator.makeProject();
    }

    public DiscoveryExtensionInterface.Applicable isApplicable(DiscoveryDescriptor descriptor, Interrupter interrupter, boolean findMain) {
        Progress progress = new MyProgress(NbBundle.getMessage(DiscoveryExtension.class, "AnalyzingProjectProgress"));
        progress.start(0);
        try {
            List<String> errors = new  ArrayList<String>();
            DiscoveryExtensionInterface.Applicable applicable;
            applicable = isApplicableExecLog(descriptor);
            if (applicable.isApplicable()){
                return applicable;
            }
            applicable = isApplicableDwarfExecutable(descriptor, findMain);
            if (applicable.isApplicable()){
                return applicable;
            }
            if (applicable.getErrors() != null) {
                errors.addAll(applicable.getErrors());
            }
            applicable = isApplicableMakeLog(descriptor);
            if (applicable.isApplicable()){
                return applicable;
            }
            if (applicable.getErrors() != null) {
                errors.addAll(applicable.getErrors());
            }
            applicable = isApplicableDwarfFolder(descriptor, interrupter);
            if (applicable.isApplicable()){
                return applicable;
            }
            if (applicable.getErrors() != null) {
                errors.addAll(applicable.getErrors());
            }
            if (!errors.isEmpty()) {
                return ApplicableImpl.getNotApplicable(errors);
            } else {
                return ApplicableImpl.getNotApplicable(Collections.singletonList(NbBundle.getMessage(DiscoveryExtension.class, "NoExecutable_NoBaseFolder"))); // NOI18N
            }
        } finally {
            progress.done();
        }
    }
    
    private DiscoveryExtensionInterface.Applicable isApplicableDwarfExecutable(DiscoveryDescriptor descriptor, boolean findMain){
        String selectedExecutable = descriptor.getBuildResult();
        if (selectedExecutable == null) {
            return ApplicableImpl.getNotApplicable(null);
        }
        FileSystem fileSystem = descriptor.getFileSystem();
        if (fileSystem == null) {
            fileSystem = FileSystemProvider.getFileSystem(ExecutionEnvironmentFactory.getLocal());
        }
        FileObject file = new FSPath(fileSystem, selectedExecutable).getFileObject();
        if (file == null || !file.isValid()) {
            return ApplicableImpl.getNotApplicable(Collections.singletonList(NbBundle.getMessage(DiscoveryExtension.class, "NotFoundExecutable",selectedExecutable))); // NOI18N
        }
        ProjectProxy proxy = new ProjectProxyImpl(descriptor);
        DiscoveryProvider provider = DiscoveryProviderFactory.findProvider("dwarf-executable"); // NOI18N
        if (provider != null && provider.isApplicable(proxy)){
            provider.getProperty("executable").setValue(selectedExecutable); // NOI18N
            provider.getProperty("libraries").setValue(new String[0]); // NOI18N
            provider.getProperty("filesystem").setValue(descriptor.getFileSystem()); // NOI18N
            ProviderProperty property = provider.getProperty("find_main");
            if (property != null) {
                if (findMain) {
                    property.setValue(Boolean.TRUE);
                } else {
                    property.setValue(Boolean.FALSE);
                }
            }
            Applicable canAnalyze = provider.canAnalyze(proxy, null);
            if (canAnalyze.isApplicable()){
                descriptor.setProvider(provider);
                return canAnalyze;
            } else {
                if (canAnalyze.getErrors().size() > 0) {
                    return ApplicableImpl.getNotApplicable(canAnalyze.getErrors());
                } else {
                    return ApplicableImpl.getNotApplicable(Collections.singletonList(NbBundle.getMessage(DiscoveryExtension.class, "CannotAnalyzeExecutable",selectedExecutable))); // NOI18N
                }
            }
        }
        return ApplicableImpl.getNotApplicable(Collections.singletonList(NbBundle.getMessage(DiscoveryExtension.class, "NotFoundDiscoveryProvider"))); // NOI18N
    }
    
    private DiscoveryExtensionInterface.Applicable  isApplicableDwarfFolder(DiscoveryDescriptor descriptor, Interrupter interrupter){
        String rootFolder = descriptor.getRootFolder();
        if (rootFolder == null) {
            return ApplicableImpl.getNotApplicable(null);
        }
        ProjectProxy proxy = new ProjectProxyImpl(descriptor);
        DiscoveryProvider provider = DiscoveryProviderFactory.findProvider("dwarf-folder"); // NOI18N
        if (provider != null && provider.isApplicable(proxy)){
            provider.getProperty("folder").setValue(rootFolder); // NOI18N
            Applicable canAnalyze = provider.canAnalyze(proxy, interrupter);
            if (canAnalyze.isApplicable()){
                descriptor.setProvider(provider);
                return canAnalyze;
            } else {
                if (canAnalyze.getErrors().size() > 0) {
                    return ApplicableImpl.getNotApplicable(canAnalyze.getErrors());
                } else {
                    return ApplicableImpl.getNotApplicable(Collections.singletonList(NbBundle.getMessage(DiscoveryExtension.class, "CannotAnalyzeFolder",rootFolder))); // NOI18N
                }
            }
        }
        return ApplicableImpl.getNotApplicable(Collections.singletonList(NbBundle.getMessage(DiscoveryExtension.class, "NotFoundDiscoveryProvider"))); // NOI18N
    }

    private DiscoveryExtensionInterface.Applicable  isApplicableMakeLog(DiscoveryDescriptor descriptor){
        String rootFolder = descriptor.getRootFolder();
        if (rootFolder == null) {
            return ApplicableImpl.getNotApplicable(null);
        }
        String logFile = descriptor.getBuildLog();
        ProjectProxy proxy = new ProjectProxyImpl(descriptor);
        DiscoveryProvider provider = DiscoveryProviderFactory.findProvider("make-log"); // NOI18N
        if (provider != null && provider.isApplicable(proxy)){
            provider.getProperty("make-log-file").setValue(logFile); // NOI18N
            Applicable canAnalyze = provider.canAnalyze(proxy, null);
            if (canAnalyze.isApplicable()){
                descriptor.setProvider(provider);
                return canAnalyze;
            } else {
                if (canAnalyze.getErrors().size() > 0) {
                    return ApplicableImpl.getNotApplicable(canAnalyze.getErrors());
                } else {
                    return ApplicableImpl.getNotApplicable(Collections.singletonList(NbBundle.getMessage(DiscoveryExtension.class, "CannotAnalyzeBuildLog",logFile))); // NOI18N
                }
            }
        }
        return ApplicableImpl.getNotApplicable(Collections.singletonList(NbBundle.getMessage(DiscoveryExtension.class, "NotFoundDiscoveryProvider"))); // NOI18N
    }
    
    private DiscoveryExtensionInterface.Applicable  isApplicableExecLog(DiscoveryDescriptor descriptor){
        String rootFolder = descriptor.getRootFolder();
        if (rootFolder == null) {
            return ApplicableImpl.getNotApplicable(null);
        }
        String logFile = descriptor.getExecLog();
        ProjectProxy proxy = new ProjectProxyImpl(descriptor);
        DiscoveryProvider provider = DiscoveryProviderFactory.findProvider("exec-log"); // NOI18N
        if (provider != null) {
            provider.getProperty("exec-log-file").setValue(logFile); // NOI18N
            if (provider.isApplicable(proxy)){
                Applicable canAnalyze = provider.canAnalyze(proxy, null);
                if (canAnalyze.isApplicable()){
                    descriptor.setProvider(provider);
                    return canAnalyze;
                } else {
                    if (canAnalyze.getErrors().size() > 0) {
                        return ApplicableImpl.getNotApplicable(canAnalyze.getErrors());
                    } else {
                        return ApplicableImpl.getNotApplicable(Collections.singletonList(NbBundle.getMessage(DiscoveryExtension.class, "CannotAnalyzeBuildLog",logFile))); // NOI18N
                    }
                }
            }
        }
        return ApplicableImpl.getNotApplicable(Collections.singletonList(NbBundle.getMessage(DiscoveryExtension.class, "NotFoundDiscoveryProvider"))); // NOI18N
    }
    
    public DiscoveryExtensionInterface.Applicable isApplicable(Map<String,Object> map, Project project, boolean findMain) {
        DiscoveryDescriptor descriptor = DiscoveryWizardDescriptor.adaptee(map);
        return isApplicable(descriptor, null, findMain);
    }
    
    boolean canApply(DiscoveryDescriptor descriptor, Interrupter interrupter) {
        if (!isApplicable(descriptor, interrupter, false).isApplicable()){
            return false;
        }
        String level = descriptor.getLevel();
        if (level == null || level.length() == 0){
            return false;
        }
        DiscoveryProvider provider = descriptor.getProvider();
        if (provider == null){
            return false;
        }
        if ("dwarf-executable".equals(provider.getID())){ // NOI18N
            String selectedExecutable = descriptor.getBuildResult();
            String additional = descriptor.getAditionalLibraries();
            provider.getProperty("executable").setValue(selectedExecutable); // NOI18N
            ProviderProperty property = provider.getProperty("find_main");
            if (property != null) {
                property.setValue(Boolean.TRUE);
            }
            if (additional != null && additional.length()>0){
                List<String> list = new ArrayList<String>();
                StringTokenizer st = new StringTokenizer(additional,";");  // NOI18N
                while(st.hasMoreTokens()){
                    list.add(st.nextToken());
                }
                provider.getProperty("libraries").setValue(list.toArray(new String[list.size()])); // NOI18N
            } else {
                provider.getProperty("libraries").setValue(new String[0]); // NOI18N
            }
        } else if ("dwarf-folder".equals(provider.getID())){ // NOI18N
            String rootFolder = descriptor.getRootFolder();
            provider.getProperty("folder").setValue(rootFolder); // NOI18N
        } else if ("make-log".equals(provider.getID())){ // NOI18N
            //String rootFolder = descriptor.getRootFolder();
            //provider.getProperty("folder").setValue(rootFolder); // NOI18N
        } else if ("exec-log".equals(provider.getID())){ // NOI18N
            //String rootFolder = descriptor.getRootFolder();
            //provider.getProperty("folder").setValue(rootFolder); // NOI18N
        } else {
            return false;
        }
        buildModel(descriptor, interrupter);
        if (interrupter != null && interrupter.cancelled()) {
            return false;
        }
        return !descriptor.isInvokeProvider()
            && descriptor.getConfigurations() != null && descriptor.getConfigurations().size() > 0
            && descriptor.getIncludedFiles() != null;
    }
    
    public static void buildModel(final DiscoveryDescriptor wizardDescriptor, Interrupter interrupter){
        String rootFolder = wizardDescriptor.getRootFolder();
        DiscoveryProvider provider = wizardDescriptor.getProvider();
        String consolidation = wizardDescriptor.getLevel();
        assert consolidation != null;
        List<Configuration> configs = provider.analyze(new ProjectProxy() {
            @Override
            public boolean createSubProjects() {
                return false;
            }
            @Override
            public Project getProject() {
                return wizardDescriptor.getProject();
            }

            @Override
            public String getMakefile() {
                return null;
            }

            @Override
            public String getSourceRoot() {
                return wizardDescriptor.getRootFolder();
            }

            @Override
            public String getExecutable() {
                return wizardDescriptor.getBuildResult();
            }

            @Override
            public String getWorkingFolder() {
                return null;
            }

            @Override
            public boolean mergeProjectProperties() {
                return wizardDescriptor.isIncrementalMode();
            }
        }, new MyProgress(NbBundle.getMessage(DiscoveryExtension.class, "AnalyzingProjectProgress")), interrupter);
        if (interrupter != null && interrupter.cancelled()) {
            return;
        }
        MyProgress myProgress = new MyProgress(NbBundle.getMessage(DiscoveryExtension.class, "BuildCodeAssistanceProgress"));
        try {
            myProgress.start();
            List<ProjectConfiguration> projectConfigurations = new ArrayList<ProjectConfiguration>();
            List<String> includedFiles = new ArrayList<String>();
            wizardDescriptor.setIncludedFiles(includedFiles);
            Map<String, AtomicInteger> compilers = new HashMap<String, AtomicInteger>();
            Set<String> dep = new HashSet<String>();
            Set<String> buildArtifacts = new HashSet<String>();
            for (Iterator<Configuration> it = configs.iterator(); it.hasNext();) {
                Configuration conf = it.next();
                includedFiles.addAll(conf.getIncludedFiles());
                List<ProjectProperties> langList = conf.getProjectConfiguration();
                for (Iterator<ProjectProperties> it2 = langList.iterator(); it2.hasNext();) {
                    ProjectConfiguration project = ConfigurationFactory.makeRoot(it2.next(), rootFolder);
                    ConsolidationStrategy.consolidateModel(project, consolidation);
                    projectConfigurations.add(project);
                }
                for (SourceFileProperties source : conf.getSourcesConfiguration()) {
                    String compiler = source.getCompilerName();
                    if (compiler != null) {
                        AtomicInteger count = compilers.get(compiler);
                        if (count == null) {
                            count = new AtomicInteger();
                            compilers.put(compiler, count);
                        }
                        count.incrementAndGet();
                    }
                }
                if (conf.getDependencies() != null) {
                    dep.addAll(conf.getDependencies());
                }
                if (conf.getBuildArtifacts() != null) {
                    buildArtifacts.addAll(conf.getBuildArtifacts());
                }
            }
            wizardDescriptor.setInvokeProvider(false);
            wizardDescriptor.setDependencies(new ArrayList<String>(dep));
            wizardDescriptor.setBuildArtifacts(new ArrayList<String>(buildArtifacts));
            wizardDescriptor.setConfigurations(projectConfigurations);
            int max = 0;
            String top = "";
            for(Map.Entry<String, AtomicInteger> entry : compilers.entrySet()){
                if (entry.getValue().get() > max) {
                    max = entry.getValue().get();
                    top = entry.getKey();
                }
            }
            wizardDescriptor.setCompilerName(top);
        } finally {
            myProgress.done();
        }
    }

    @Override
    public boolean canApply(Map<String, Object> map, Project project) {
        return canApply(map, project, null);
    }
    
    @Override
    public boolean canApply(Map<String, Object> map, Project project, Interrupter interrupter) {
        DiscoveryDescriptor descriptor = DiscoveryWizardDescriptor.adaptee(map);
        descriptor.setProject(project);
        return canApply(descriptor, interrupter);
    }
    
    @Override
    public void discoverProject(final Map<String, Object> map, final Project lastSelectedProject, ProjectKind projectKind) {
        ImportExecutable importer = new ImportExecutable(map, lastSelectedProject, projectKind);
        if (lastSelectedProject != null) {
            importer.process(this);
        }
    }

    @Override
    public void discoverHeadersByModel(Project project) {
        DiscoveryManagerImpl.discoverHeadersByModel(project);
    }

    @Override
    public void disableModel(Project makeProject) {
        final CsmModel model = CsmModelAccessor.getModel();
        if (model instanceof ModelImpl && makeProject != null) {
            NativeProject np = makeProject.getLookup().lookup(NativeProject.class);
            ((ModelImpl) model).disableProject(np);
        }
    }

    private static class ProjectProxyImpl implements ProjectProxy {

        private DiscoveryDescriptor descriptor;

        private ProjectProxyImpl(DiscoveryDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        @Override
        public boolean createSubProjects() {
            return false;
        }

        @Override
        public Project getProject() {
            return null;
        }

        @Override
        public String getMakefile() {
            return null;
        }

        @Override
        public String getSourceRoot() {
            return descriptor.getRootFolder();
        }

        @Override
        public String getExecutable() {
            return descriptor.getBuildResult();
        }

        @Override
        public String getWorkingFolder() {
            return null;
        }

        @Override
        public boolean mergeProjectProperties() {
            return false;
        }
    };

}
