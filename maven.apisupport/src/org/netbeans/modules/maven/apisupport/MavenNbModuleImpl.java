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

package org.netbeans.modules.maven.apisupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.netbeans.modules.maven.indexer.api.NBVersionInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryQueries;
import org.netbeans.modules.maven.api.PluginPropertyUtils;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import javax.xml.namespace.QName;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Resource;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.apisupport.project.api.LayerHandle;
import org.netbeans.modules.apisupport.project.spi.LayerUtil;
import org.netbeans.modules.apisupport.project.spi.NbModuleProvider;
import org.netbeans.modules.apisupport.project.spi.PlatformJarProvider;
import org.netbeans.modules.maven.api.FileUtilities;
import org.netbeans.modules.maven.api.ModelUtils;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.indexer.api.RepositoryInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryPreferences;
import org.netbeans.modules.maven.model.ModelOperation;
import org.netbeans.modules.maven.model.Utilities;
import org.netbeans.modules.maven.model.pom.Build;
import org.netbeans.modules.maven.model.pom.Configuration;
import org.netbeans.modules.maven.model.pom.POMExtensibilityElement;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.modules.maven.model.pom.Plugin;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.modules.SpecificationVersion;
import org.netbeans.spi.project.AuxiliaryProperties;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.filesystems.XMLFileSystem;
import org.openide.util.RequestProcessor;

/**
 *
 * @author mkleint
 */
@ProjectServiceProvider(service=NbModuleProvider.class, projectType="org-netbeans-modules-maven/" + NbMavenProject.TYPE_NBM)
public class MavenNbModuleImpl implements NbModuleProvider {
    private Project project;
    private DependencyAdder dependencyAdder = new DependencyAdder();
    private static final RequestProcessor RP = new RequestProcessor(MavenNbModuleImpl.class);
    
    private RequestProcessor.Task tsk = RP.create(dependencyAdder);
    
    public static final String NETBEANS_REPO_ID = "netbeans";
    /**
     * the property defined by nbm-maven-plugin's run-ide goal.
     * can help finding the defined netbeans platform.
     */ 
    private static final String PROP_NETBEANS_INSTALL = "netbeans.installation"; //NOI18N

    public static final String GROUPID_MOJO = "org.codehaus.mojo";
    public static final String NBM_PLUGIN = "nbm-maven-plugin";

    /** Creates a new instance of MavenNbModuleImpl 
     * @param project 
     */
    public MavenNbModuleImpl(Project project) {
        this.project = project;
    }

    static RepositoryInfo netbeansRepo() {
        return RepositoryPreferences.getInstance().getRepositoryInfoById(NETBEANS_REPO_ID);
    }
    
    private File getModuleXmlLocation() {
        String file = PluginPropertyUtils.getPluginProperty(project, 
                GROUPID_MOJO,
                NBM_PLUGIN, //NOI18N
                "descriptor", null, null); //NOI18N
        if (file == null) {
            file = "src/main/nbm/module.xml"; //NOI18N
        }
        File rel = new File(file);
        if (!rel.isAbsolute()) {
            rel = new File(FileUtil.toFile(project.getProjectDirectory()), file);
        }
        return FileUtil.normalizeFile(rel);
    }
    
    private Xpp3Dom getModuleDom() throws UnsupportedEncodingException, IOException, XmlPullParserException {
        //TODO convert to FileOBject and have the IO stream from there..
        File file = getModuleXmlLocation();
        if (!file.exists()) {
            return null;
        }
        FileInputStream is = new FileInputStream(file);
        Reader reader = new InputStreamReader(is, "UTF-8"); //NOI18N
        try {
            return Xpp3DomBuilder.build(reader);
        } finally {
            IOUtil.close(reader);
        }
    }
    
    @Override
    public String getSpecVersion() {
        NbMavenProject watch = project.getLookup().lookup(NbMavenProject.class);
        String specVersion = AdaptNbVersion.adaptVersion(watch.getMavenProject().getVersion(), AdaptNbVersion.TYPE_SPECIFICATION);
        return specVersion;
    }

    @Override
    public String getCodeNameBase() {
        String codename = PluginPropertyUtils.getPluginProperty(project, 
                GROUPID_MOJO,
                NBM_PLUGIN, //NOI18N
                "codeNameBase", "manifest", null);
        if (codename == null) {
            //this is deprecated in 3.8, but kept around for older versions
            try {
                Xpp3Dom dom = getModuleDom();
                if (dom != null) {
                    Xpp3Dom cnb = dom.getChild("codeNameBase"); //NOI18N
                    if (cnb != null) {
                        String val = cnb.getValue();
                        int slash = val.indexOf('/');
                        if (slash > -1) {
                            val = val.substring(0, slash);
                        }
                        return val;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            MavenProject prj = project.getLookup().lookup(NbMavenProject.class).getMavenProject();
            //same fallback is in nbm-maven-plugin, keep it synchronized with codeNameBase parameter
            codename = prj.getGroupId() + "." + prj.getArtifactId(); //NOI18N
            codename = codename.replaceAll( "-", "." ); //NOI18N
        }
        return codename;
    }

    @Override
    public String getSourceDirectoryPath() {
        //TODO
        return "src/main/java"; //NOI18N
    }
    
    @Override
    public String getTestSourceDirectoryPath() {
        //TODO
        return "src/test/java"; //NOI18N
    }

    @Override
    public FileObject getSourceDirectory() {
        FileObject fo = project.getProjectDirectory().getFileObject(getSourceDirectoryPath());
        if (fo == null) {
            try {
                fo = FileUtil.createFolder(project.getProjectDirectory(),
                                           getSourceDirectoryPath());
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return fo;
    }

    @Override
    public FileObject getManifestFile() {
        String manifest = PluginPropertyUtils.getPluginProperty(project, 
                GROUPID_MOJO,
                NBM_PLUGIN, //NOI18N
                "sourceManifestFile", "manifest", null);
        if (manifest != null) {
            return FileUtilities.convertStringToFileObject(manifest);
        }
        String path = "src/main/nbm/manifest.mf";  //NOI18N

        try {
            Xpp3Dom dom = getModuleDom();
            if (dom != null) {
                Xpp3Dom cnb = dom.getChild("manifest"); //NOI18N
                if (cnb != null) {
                    path = cnb.getValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return project.getProjectDirectory().getFileObject(path);
    }

    @Override
    public String getResourceDirectoryPath(boolean isTest) {
        NbMavenProject watch = project.getLookup().lookup(NbMavenProject.class);
        List<Resource> res;
        String defaultValue;
        
        if (isTest) {
            res = watch.getMavenProject().getTestResources();           
            defaultValue = "src/test/resources"; //NOI18N
        } else {
            res = watch.getMavenProject().getResources();
            defaultValue = "src/main/resources"; //NOI18N
        }
        for (Resource resource : res) {
            FileObject fo = FileUtilities.convertStringToFileObject(resource.getDirectory());
            if (fo != null && FileUtil.isParentOf(project.getProjectDirectory(), fo)) {
                return FileUtil.getRelativePath(project.getProjectDirectory(), fo);
            }
        }
        return defaultValue;
    }

    @Override
    public void addDependencies(NbModuleProvider.ModuleDependency[] dependencies) throws IOException {
        for (NbModuleProvider.ModuleDependency mdep : dependencies) {
        String codeNameBase = mdep.getCodeNameBase();
        SpecificationVersion version = mdep.getVersion();
        String artifactId = codeNameBase.replaceAll("\\.", "-"); //NOI18N
        NbMavenProject watch = project.getLookup().lookup(NbMavenProject.class);
        if (hasDependency(codeNameBase)) {
            //TODO
            //not sure we ought to check for spec or release version.
            // just ignore for now, not any easy way to upgrade anyway I guess.
            continue;
        }
        Dependency dep = null;
        RepositoryInfo nbrepo = netbeansRepo();
        if (nbrepo != null) {
            File platformFile = lookForModuleInPlatform(artifactId);
            if (platformFile != null) {
                List<NBVersionInfo> lst = RepositoryQueries.findBySHA1Result(platformFile, Collections.singletonList(nbrepo)).getResults();
                for (NBVersionInfo elem : lst) {
                    dep = new Dependency();
                    dep.setArtifactId(elem.getArtifactId());
                    dep.setGroupId(elem.getGroupId());
                    dep.setVersion(elem.getVersion());
                    break;
                }
            }
        }
        if (dep == null) {
            //TODO try to guess 
            dep = new Dependency();
            dep.setGroupId("org.netbeans.api"); //NOI18N
            dep.setArtifactId(artifactId);
            if (version != null) {
                dep.setVersion(version.toString());
            } else {
                //try guessing the version according to the rest of netbeans dependencies..
                for (Dependency d : watch.getMavenProject().getModel().getDependencies()) {
                    if ("org.netbeans.api".equals(d.getGroupId())) { // NOI18N
                        dep.setVersion(d.getVersion());
                    }
                }
            }
        }
        if (dep.getVersion() == null) {
            if (nbrepo != null) {
                List<NBVersionInfo> versions = RepositoryQueries.getVersionsResult("org.netbeans.cluster", "platform", Collections.singletonList(nbrepo)).getResults();
                if (!versions.isEmpty()) {
                    dep.setVersion(versions.get(0).getVersion());
                }
            }
        }
        if (dep.getVersion() == null) {
            dep.setVersion("99.99"); // NOI18N
        }
        if (mdep.isTestDependency()) {
            dep.setScope("test");
        }
        //#214674 heuristics to set the right expression if matching..
        MavenProject mp = watch.getMavenProject();
        String nbVersion = mp.getProperties() != null ? mp.getProperties().getProperty("netbeans.version") : null;
        if (nbVersion != null && nbVersion.equals(dep.getVersion())) {
            dep.setVersion("${netbeans.version}");
        }
        dependencyAdder.addDependency(dep);
        
        }
        dependencyAdder.run();
    }

    /**
     * 6.7 and higher apisupport uses this to add projects to Libraries for suite.
     *
     * Cannot use Maven-based apisupport projects this way as it doesn't build
     * modules into clusters. Workaround is to unpack resulting NBM somewhere
     * and add it as an external binary cluster.
     * @return null
     */
    @Override
    public File getModuleJarLocation() {
        return null;
    }

    public @Override boolean hasDependency(String codeNameBase) throws IOException {
        String artifactId = codeNameBase.replaceAll("\\.", "-"); //NOI18N
        NbMavenProject watch = project.getLookup().lookup(NbMavenProject.class);
        Set<Artifact> set = watch.getMavenProject().getDependencyArtifacts();
        if (set != null) {
            for (Artifact art : set) {
                if (art.getGroupId().startsWith("org.netbeans") && art.getArtifactId().equals(artifactId)) { // NOI18N
                    return true;
                }
            }
        }
        return false;
    }

    public @Override String getReleaseDirectoryPath() {
        return "src/main/release";
    }

    public @Override FileObject getReleaseDirectory() throws IOException {
        Utilities.performPOMModelOperations(project.getProjectDirectory().getFileObject("pom.xml"), Collections.<ModelOperation<POMModel>>singletonList(new ModelOperation<POMModel>() {
            public @Override void performOperation(POMModel model) {
                Build build = model.getProject().getBuild();
                if (build != null) {
                    Plugin nbmPlugin = build.findPluginById(GROUPID_MOJO, NBM_PLUGIN);
                    if (nbmPlugin != null) {
                        Configuration configuration = nbmPlugin.getConfiguration();
                        if (configuration == null) {
                            configuration = model.getFactory().createConfiguration();
                            nbmPlugin.setConfiguration(configuration);
                        }
                        POMExtensibilityElement resources = ModelUtils.getOrCreateChild(configuration, "nbmResources", model);
                        boolean needed = true;
                        NEEDED: for (POMExtensibilityElement configurationElement : resources.getExtensibilityElements()) {
                            if (configurationElement.getQName().getLocalPart().equals("nbmResource")) {
                                for (POMExtensibilityElement dir : configurationElement.getExtensibilityElements()) {
                                    if (dir.getQName().getLocalPart().equals("directory")) {
                                        if (dir.getElementText().equals(getReleaseDirectoryPath())) {
                                            needed = false;
                                            break NEEDED;
                                        }
                                    }
                                }
                            }
                        }
                        if (needed) {
                            POMExtensibilityElement dir = model.getFactory().createPOMExtensibilityElement(new QName("directory"));
                            dir.setElementText(getReleaseDirectoryPath());
                            POMExtensibilityElement res = model.getFactory().createPOMExtensibilityElement(new QName("nbmResource"));
                            res.addExtensibilityElement(dir);
                            resources.addExtensibilityElement(res);
                        }
                    }
                }
            }
        }));
        return FileUtil.createFolder(project.getProjectDirectory(), getReleaseDirectoryPath());
    }
    
    public @Override File getClassesDirectory() {
        return new File(project.getLookup().lookup(NbMavenProject.class).getMavenProject().getBuild().getOutputDirectory());
    }
    
    private class DependencyAdder implements Runnable {
        List<Dependency> toAdd = new ArrayList<Dependency>();
        
        private synchronized void addDependency(Dependency dep) {
            toAdd.add(dep);
        }
        
        @Override
        public void run() {
            FileObject fo = project.getProjectDirectory().getFileObject("pom.xml"); //NOI18N
            final DependencyAdder monitor = this;
            ModelOperation<POMModel> operation = new ModelOperation<POMModel>() {
                @Override
                public void performOperation(POMModel model) {
                    synchronized (monitor) {
                        for (Dependency dep : toAdd) {
                            org.netbeans.modules.maven.model.pom.Dependency mdlDep =
                                    ModelUtils.checkModelDependency(model, dep.getGroupId(), dep.getArtifactId(), true);
                            mdlDep.setVersion(dep.getVersion());
                            if (dep.getScope() != null) {
                                mdlDep.setScope(dep.getScope());
                            }
                        }
                        toAdd.clear();
                    }
                }
            };
            Utilities.performPOMModelOperations(fo, Collections.singletonList(operation));
            project.getLookup().lookup(NbMavenProject.class).synchronousDependencyDownload();
        }
    }
            
    @Override
    public String getProjectFilePath() {
        return "pom.xml"; //NOI18N
    }

    /**
     * get specification version for the given module.
     * The module isn't necessary a project dependency, more a property of the associated 
     * netbeans platform.
     */ 
    @Override
    public SpecificationVersion getDependencyVersion(String codenamebase) throws IOException {
        String artifactId = codenamebase.replaceAll("\\.", "-"); //NOI18N
        NbMavenProject watch = project.getLookup().lookup(NbMavenProject.class);
        for (Artifact art : watch.getMavenProject().getArtifacts()) {
            if (art.getGroupId().startsWith("org.netbeans") && art.getArtifactId().equals(artifactId)) { //NOI18N
                File jar = art.getFile();
                if (jar.isFile()) {
                ExamineManifest exa = new ExamineManifest();
                exa.setJarFile(jar);
                try {
                    exa.checkFile();
                } catch (MojoExecutionException x) {
                    throw new IOException(x);
                }
                if (exa.getSpecVersion() != null) {
                    return new SpecificationVersion(exa.getSpecVersion());
                }
                }
            }
        }
        // #190149: look up artifact in repo with same version as some existing org.netbeans.api:* dep
        for (Artifact art : watch.getMavenProject().getArtifacts()) {
            if (art.getGroupId().startsWith("org.netbeans")) { // NOI18N
                Artifact art2 = EmbedderFactory.getProjectEmbedder().getLocalRepository().find(
                        new DefaultArtifact("org.netbeans.api", artifactId, art.getVersion(), null, "jar", null, new DefaultArtifactHandler("jar"))); // NOI18N
                File jar = art2.getFile();
                if (jar != null && jar.isFile()) {
                    ExamineManifest exa = new ExamineManifest();
                    exa.setJarFile(jar);
                    try {
                        exa.checkFile();
                    } catch (MojoExecutionException x) {
                        throw new IOException(x);
                    }
                    if (exa.getSpecVersion() != null) {
                        return new SpecificationVersion(exa.getSpecVersion());
                    }
                }
            }
        }
        File fil = lookForModuleInPlatform(artifactId);
        if (fil != null) {
            ExamineManifest exa = new ExamineManifest();
            exa.setJarFile(fil);
            try {
                exa.checkFile();
            } catch (MojoExecutionException x) {
                throw new IOException(x);
            }
            if (exa.getSpecVersion() != null) {
                return new SpecificationVersion(exa.getSpecVersion());
            }
        }
        //TODO search local repository?? that's probably irrelevant here..
        
        //we're completely clueless.
        return null;
    }
    
    private File lookForModuleInPlatform(String artifactId) {
        File actPlatform = getActivePlatformLocation();
        if (actPlatform != null) {
            DirectoryScanner walk = new DirectoryScanner();
            walk.setBasedir(actPlatform);
            walk.setIncludes(new String[] {
                "**/" + artifactId + ".jar" //NOI18N
            });
            walk.scan();
            String[] candidates = walk.getIncludedFiles();
            assert candidates != null && candidates.length <= 1;
            if (candidates.length > 0) {
                return new File(actPlatform, candidates[0]);
            }
        }
        return null;
    }

    /**
     * get the NetBeans platform for the module
     * @return location of the root directory of NetBeans platform installation
     */
    private File getActivePlatformLocation() {
        File platformDir = findPlatformFolder();
        if (platformDir != null && platformDir.isDirectory()) {
            return platformDir;
        }
        platformDir = findIDEInstallation(project);
        if (platformDir != null && platformDir.isDirectory()) {
            return platformDir;
        }
        return null;
    }

    /**
     * Looks for the configured location of the IDE installation for a standalone or suite module.
     */
    static @CheckForNull File findIDEInstallation(Project project) {
        String installProp = project.getLookup().lookup(NbMavenProject.class).getMavenProject().getProperties().getProperty(PROP_NETBEANS_INSTALL);
        if (installProp == null) {
            installProp = PluginPropertyUtils.getPluginProperty(project, GROUPID_MOJO, NBM_PLUGIN, "netbeansInstallation", "run-ide", "netbeans.installation");
        }
        if (installProp != null) {
            return FileUtilities.convertStringToFile(installProp);
        } else {
            return null;
        }
    }

    static Project findAppProject(Project nbmProject) {
        NbMavenProject mp = nbmProject.getLookup().lookup(NbMavenProject.class);
        if (mp == null) {
            return null;
        }
        String groupId = mp.getMavenProject().getGroupId();
        String artifactId = mp.getMavenProject().getArtifactId();
        Project candidate = null;
        for (Project p : OpenProjects.getDefault().getOpenProjects()) {
            NbMavenProject mp2 = p.getLookup().lookup(NbMavenProject.class);
            if (mp2 != null && NbMavenProject.TYPE_NBM_APPLICATION.equals(mp2.getPackagingType())) {
                for (Artifact dep : mp2.getMavenProject().getArtifacts()) {
                    if (dep.getGroupId().equals(groupId) && dep.getArtifactId().equals(artifactId)) {
                        if (candidate != null) {
                            // multiple candidates
                            return null;
                        } else {
                            candidate = p;
                        }
                    }
                }
            }
        }
        return candidate;
    }
    @ProjectServiceProvider(service=ProjectOpenedHook.class, projectType="org-netbeans-modules-maven/" + NbMavenProject.TYPE_NBM)
    public static class RemoveOldPathToNbApplicationModule extends ProjectOpenedHook {
        private final Project p;
        public RemoveOldPathToNbApplicationModule(Project p) {
            this.p = p;
        }
        protected @Override void projectOpened() {
            AuxiliaryProperties aux = p.getLookup().lookup(AuxiliaryProperties.class);
            if (aux != null) {
                aux.put("pathToNbApplicationModule", null, true);
            }
        }
        protected @Override void projectClosed() {}
    }

    private File findPlatformFolder() {
            Project appProject = findAppProject(project);
            if (appProject == null) {
                //not a project directory.
                return null;
            }
            NbMavenProject watch = appProject.getLookup().lookup(NbMavenProject.class);
            if (watch == null) {
                return null; //not a maven project.
            }
            String outputDir = PluginPropertyUtils.getPluginProperty(appProject,
                    GROUPID_MOJO, NBM_PLUGIN, "outputDirectory", "cluster-app", null); //NOI18N
            if( null == outputDir ) {
                outputDir = "target"; //NOI18N
            }

            String brandingToken = PluginPropertyUtils.getPluginProperty(appProject,
                    GROUPID_MOJO, NBM_PLUGIN, "brandingToken", "cluster-app", "netbeans.branding.token"); //NOI18N
             return FileUtilities.resolveFilePath(FileUtil.toFile(appProject.getProjectDirectory()), outputDir + File.separator + brandingToken);
    }

    @Override public FileSystem getEffectiveSystemFilesystem() throws IOException {
        FileSystem projectLayer = LayerHandle.forProject(project).layer(false);
        Collection<FileSystem> platformLayers = new ArrayList<FileSystem>();
        PlatformJarProvider pjp = project.getLookup().lookup(PlatformJarProvider.class);
        if (pjp != null) {
            List<URL> urls = new ArrayList<URL>();
            for (File jar : pjp.getPlatformJars()) {
                // XXX use LayerHandle.forProject on this and sister modules instead
                urls.addAll(LayerUtil.layersOf(jar));
            }
            XMLFileSystem xmlfs = new XMLFileSystem();
            try {
                xmlfs.setXmlUrls(urls.toArray(new URL[urls.size()]));
            } catch (PropertyVetoException x) {
                throw new IOException(x);
            }
            platformLayers.add(xmlfs);
        }
        // XXX would using PlatformLayersCacheManager be beneficial? (would need to modify in several ways)
        return LayerUtil.mergeFilesystems(projectLayer, platformLayers);
    }

}
