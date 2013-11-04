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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.bugtracking.vcs;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.bugtracking.APIAccessor;
import org.netbeans.modules.bugtracking.BugtrackingOwnerSupport;
import org.netbeans.modules.bugtracking.api.Repository;
import org.netbeans.modules.bugtracking.commons.FileToRepoMappingStorage;
import org.netbeans.modules.bugtracking.commons.Util;
import org.netbeans.modules.bugtracking.team.spi.TeamUtil;
import static org.netbeans.modules.bugtracking.team.spi.TeamUtil.getRepository;
import org.netbeans.modules.team.spi.TeamAccessorUtils;
import org.netbeans.modules.team.spi.TeamProject;
import org.netbeans.modules.versioning.util.VCSBugtrackingAccessor;

/**
 * Only for team needs
 * 
 * XXX lets do that directly from kenai (after a clone) via the bugtracking bridge
 * 
 * @author Tomas Stupka
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.versioning.util.VCSBugtrackingAccessor.class)
public class VCSBugtrackingSupportImpl extends VCSBugtrackingAccessor {

    private static final Logger LOG = Logger.getLogger("org.netbeans.modules.bugtracking.bridge"); // NOI18N

    @Override
    public void setFirmAssociations(File[] files, String url) {
        if (files == null) {
            throw new IllegalArgumentException("files is null");        //NOI18N
        }
        if (files.length == 0) {
            return;
        }
        
        Repository repo;
        try {
            repo = getRepository(url);
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "No issue tracker available for the given vcs url " + url, ex);         // NOI18N
            return;
        }
        if(repo == null) {
            LOG.log(Level.WARNING, "No issue tracker available for the given vcs url {0}", url);         // NOI18N
            return;
        }


        FileToRepoMappingStorage.getInstance().setFirmAssociation(
                Util.getLargerContext(files[0]),
                repo.getUrl());
    }
    
    public static void setFirmAssociations(File[] files, Repository repository) {
        BugtrackingOwnerSupport.getInstance().setFirmAssociations(files, APIAccessor.IMPL.getImpl(repository));
    }
    
    /**
     * Returns a Repository corresponding to the given team url and a name. The url
     * might be either a team vcs repository, an issue or the team server url.
     * @param repositoryUrl
     * @return
     * @throws IOException
     */
    private static Repository getRepository(String repositoryUrl) throws IOException {
        TeamProject project = TeamAccessorUtils.getTeamProjectForRepository(repositoryUrl);
        return (project != null)
               ? TeamUtil.getRepository(project)
               : null;        //not a team project repository
    }    

}
