/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.queries;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.AbstractArtifactResolutionException;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.embedder.MavenEmbedder;
import org.netbeans.modules.maven.embedder.exec.ProgressTransferListener;
import org.netbeans.modules.maven.indexer.api.NBVersionInfo;
import org.netbeans.modules.maven.indexer.api.RepositoryIndexer;
import org.netbeans.modules.maven.indexer.api.RepositoryPreferences;
import org.netbeans.modules.maven.indexer.api.RepositoryQueries;
import org.netbeans.spi.java.project.support.JavadocAndSourceRootDetection;
import org.netbeans.spi.java.queries.SourceJavadocAttacherImplementation;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service=SourceJavadocAttacherImplementation.Definer.class)
public class MavenSourceJavadocAttacher implements SourceJavadocAttacherImplementation.Definer {
    private static final Logger LOG = Logger.getLogger(MavenSourceJavadocAttacher.class.getName());

    @Messages({"# {0} - artifact ID", "attaching=Attaching {0}", 
        "LBL_DOWNLOAD_REPO=Downloading source jar from known Maven repositories for local repository file.",
        "LBL_DOWNLOAD_SHA1=Downloading source jar from known Maven repositories for jar with SHA1 match in Maven repository indexes."
    })
    private List<? extends URL> attach(@NonNull final URL root, @NonNull Callable<Boolean> cancel, final boolean javadoc) throws Exception {
        final File file = FileUtil.archiveOrDirForURL(root);
        if (file == null) {
            return Collections.emptyList();
        }
        String[] coordinates = MavenFileOwnerQueryImpl.findCoordinates(file);
        final boolean byHash = coordinates == null;
        //XXX: the big question here is accurate or fast?
        // without the indexes present locally, we return fast but nothing, only the next invokation after indexing finish is accurate..
        NBVersionInfo defined = null;
        StatusDisplayer.Message message = null;
        if (Boolean.TRUE.equals(cancel.call())) {
            return Collections.emptyList();
        }
        if (!byHash) { //from local repository, known coordinates and we always return a maven SFBQ.Result for it, no reason to let people choose a jar via the default SJAI
            //TODO classifier?
            defined = new NBVersionInfo(null, coordinates[0], coordinates[1], coordinates[2], null, null, null, null, null);
            message = StatusDisplayer.getDefault().setStatusText(Bundle.LBL_DOWNLOAD_REPO(), StatusDisplayer.IMPORTANCE_ERROR_HIGHLIGHT);
        } else if (file.isFile() && file.exists()) {
            List<RepositoryForBinaryQueryImpl.Coordinates> coordinates2 = RepositoryForBinaryQueryImpl.getJarMetadataCoordinates(file);
            if (coordinates != null && coordinates2.size() == 1) { //only when non-shaded?
                RepositoryForBinaryQueryImpl.Coordinates coord = coordinates2.get(0);
                defined = new NBVersionInfo(null, coord.groupId, coord.artifactId, coord.version, null, null, null, null, null);
            }
            if (defined == null) {
                RepositoryQueries.Result<NBVersionInfo> res = RepositoryQueries.findBySHA1Result(file, null);
                List<NBVersionInfo> candidates = res.getResults();
                for (NBVersionInfo nbvi : candidates) {
                    if (javadoc ? nbvi.isJavadocExists() : nbvi.isSourcesExists()) {
                        defined = nbvi;
                        message = StatusDisplayer.getDefault().setStatusText(Bundle.LBL_DOWNLOAD_SHA1(), StatusDisplayer.IMPORTANCE_ERROR_HIGHLIGHT);
                        break;
                    }
                }
                if (defined == null && res.isPartial()) {
                    //TODO should we wait?
                }
            }
        }

        if (defined == null) {
            return Collections.emptyList();
        }
        if (Boolean.TRUE.equals(cancel.call())) {
            return Collections.emptyList();
        }

        MavenEmbedder online = EmbedderFactory.getOnlineEmbedder();
        Artifact art = online.createArtifactWithClassifier(defined.getGroupId(), defined.getArtifactId(), defined.getVersion(), "jar", javadoc ? "javadoc" : "sources");
        if (Boolean.TRUE.equals(cancel.call())) {
            return Collections.emptyList();
        }

        AggregateProgressHandle hndl = AggregateProgressFactory.createHandle(Bundle.attaching(art.getId()),
                new ProgressContributor[]{AggregateProgressFactory.createProgressContributor("attach")},
                ProgressTransferListener.cancellable(), null);
        ProgressTransferListener.setAggregateHandle(hndl);
        try {
            hndl.start();
            // XXX should this be limited to _defined.getRepoId()?
            List<ArtifactRepository> repos = RepositoryPreferences.getInstance().remoteRepositories(online);
            online.resolve(art, repos, online.getLocalRepository());
            File result = art.getFile();
            if (result.isFile()) {
                URL rootUrl = findRoot(result, javadoc);
                if (rootUrl != null) {
                    return Collections.singletonList(rootUrl);
                }
            } else {
                if (Boolean.TRUE.equals(cancel.call())) {
                    return Collections.emptyList();
                }
                if (file.isFile()) {
                    List<RepositoryForBinaryQueryImpl.Coordinates> coordinates2 = RepositoryForBinaryQueryImpl.getJarMetadataCoordinates(result);
                    List<URL> res = new ArrayList<URL>();
                    if (coordinates2 != null) {
                        for (RepositoryForBinaryQueryImpl.Coordinates coordinate : coordinates2) {
                            if (Boolean.TRUE.equals(cancel.call())) {
                                return Collections.emptyList();
                            }
                            Artifact sources = EmbedderFactory.getOnlineEmbedder().createArtifactWithClassifier(
                                    coordinate.groupId,
                                    coordinate.artifactId,
                                    coordinate.version,
                                    "jar",
                                    javadoc ? "javadoc" : "sources"); //NOI18N
                            online.resolve(sources, repos, online.getLocalRepository());
                            URL rootUrl = findRoot(sources.getFile(), javadoc);
                            if (rootUrl != null) {
                                res.add(rootUrl);
                            }
                        }
                        if (!res.isEmpty()) {
                            return res;
                        }
                    }
                }
            }
        } catch (ThreadDeath d) {
        } catch (IllegalStateException ise) { //download interrupted in dependent thread. #213812
            if (!(ise.getCause() instanceof ThreadDeath)) {
                throw ise;
            }
        } catch (AbstractArtifactResolutionException x) {
            // XXX probably ought to display some sort of notification in status bar
        } finally {
            hndl.finish();
            ProgressTransferListener.clearAggregateHandle();
        }
        return Collections.emptyList();
    }
    
    private URL findRoot(File jarFile, boolean javadoc) {
        if (jarFile != null && jarFile.isFile()) {
            FileObject fo = FileUtil.toFileObject(jarFile);
            if (fo != null && FileUtil.isArchiveFile(fo)) {
                FileObject foRoot = FileUtil.getArchiveRoot(fo);
                foRoot = javadoc ? JavadocAndSourceRootDetection.findJavadocRoot(foRoot) : JavadocAndSourceRootDetection.findSourceRoot(foRoot);
                if (foRoot != null) {
                    return foRoot.toURL();
                }
            }
        }
        return null;

    }

    @Override
    @Messages("NAME_SourceJavadocAttacher=Maven")
    public String getDisplayName() {
        return Bundle.NAME_SourceJavadocAttacher();
    }

    @Override
    @Messages("DESC_SourceJavadocAttacher=Lookup javadoc/sources in known Maven repositories")
    public String getDescription() {
        return Bundle.DESC_SourceJavadocAttacher();
    }

    @Override
    public List<? extends URL> getSources(URL root, Callable<Boolean> cancel) {
        try {
            return attach(root, cancel, false);
        } catch (IOException io) {
            LOG.log(Level.INFO, "IO error while retrieving the source for " + root, io);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.emptyList();
    }

    @Override
    public List<? extends URL> getJavadoc(URL root, Callable<Boolean> cancel) {
        try {
            return attach(root, cancel, true);
        } catch (IOException io) {
            LOG.log(Level.INFO, "IO error while retrieving the javadoc for " + root, io);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.emptyList();
    }
}
