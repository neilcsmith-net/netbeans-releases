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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
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
package org.netbeans.modules.mercurial.remote.ui.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import org.netbeans.modules.mercurial.remote.HgException;
import org.netbeans.modules.mercurial.remote.HgModuleConfig;
import org.netbeans.modules.mercurial.remote.HgProgressSupport;
import org.netbeans.modules.mercurial.remote.Mercurial;
import org.netbeans.modules.mercurial.remote.OutputLogger;
import org.netbeans.modules.mercurial.remote.ui.branch.HgBranch;
import org.netbeans.modules.mercurial.remote.ui.log.RepositoryRevision.Kind;
import org.netbeans.modules.mercurial.remote.util.HgCommand;
import org.netbeans.modules.mercurial.remote.util.HgUtils;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Executes searches in Search History panel.
 * 
 * @author Maros Sandor
 */
class SearchExecutor extends HgProgressSupport {

    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");  // NOI18N
    
    static final SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");  // NOI18N
    static final DateFormat [] dateFormats = new DateFormat[] {
        fullDateFormat,
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),  // NOI18N
        simpleDateFormat,
        new SimpleDateFormat("yyyy-MM-dd"), // NOI18N
    };
    
    private final SearchHistoryPanel    master;
    private final VCSFileProxy                  root;
    private final Set<VCSFileProxy>             files;
    private final Map<String,VCSFileProxy>            pathToRoot;
    
    private final String fromRevision;
    private final String toRevision;
    private int limitRevisions;
    private final String branchName;
    static final int DEFAULT_LIMIT = 10;
    static final int UNLIMITTED = -1;
    private final boolean includeMerges;
    private HgBranch[] branches;

    public SearchExecutor(SearchHistoryPanel master) {
        this.master = master;
        SearchCriteriaPanel criteria = master.getCriteria();
        fromRevision = criteria.getFrom();
        toRevision = criteria.getTo();
        includeMerges = criteria.isIncludeMerges();
        limitRevisions = criteria.getLimit();
        if (limitRevisions <= 0) {
            limitRevisions = UNLIMITTED;
        }
        branchName = criteria.getBranch();
        
        pathToRoot = new HashMap<>(); 
        VCSFileProxy rootFile = Mercurial.getInstance().getRepositoryRoot(master.getRoots()[0]);
        if (rootFile == null) {
            rootFile = master.getRoots()[0];
        }
        root = rootFile;
        files = new HashSet<>(Arrays.asList(master.getRoots()));

    }    
        
    @Override
    public void perform() {
        OutputLogger logger = getLogger();
        try {
            this.branches = HgCommand.getBranches(root, OutputLogger.getLogger(null));
        } catch (HgException ex) {
            this.branches = new HgBranch[0];
            Mercurial.LOG.log(ex instanceof HgException.HgCommandCanceledException
                    ? Level.FINE
                    : Level.INFO, null, ex);
        }
        List<RepositoryRevision> results = search(fromRevision, toRevision, limitRevisions, branchName, this, logger);
        if (!isCanceled()) {
            checkFinished(results);
        }
    }

    public void start () {
        if (!HgBranch.DEFAULT_NAME.equals(branchName)) {
            // only for branches other than default
            HgModuleConfig.getDefault(root).setSearchOnBranchEnabled(master.getCurrentBranch(), !branchName.isEmpty());
        }

        RequestProcessor rp = Mercurial.getInstance().getRequestProcessor(root);
        start(rp, root, NbBundle.getMessage(SearchExecutor.class, "MSG_Search_Progress")); //NOI18N
    }

    private List<RepositoryRevision> search (String fromRevision, String toRevision, int limitRevisions, String branchName, HgProgressSupport progressSupport, OutputLogger logger) {
        if (progressSupport.isCanceled()) {
            return Collections.<RepositoryRevision>emptyList();
        }
        
        HgLogMessage[] messages = new HgLogMessage[0];
        try {
            if (master.isIncomingSearch()) {
                messages = HgCommand.getIncomingMessages(root, toRevision, branchName.isEmpty() ? null : branchName,
                        includeMerges, false, includeMerges, limitRevisions, logger);
            } else if (master.isOutSearch()) {
                messages = HgCommand.getOutMessages(root, toRevision, branchName.isEmpty() ? null : branchName,
                        includeMerges, includeMerges, limitRevisions, logger);
            } else {
                List<String> branchNames = branchName.isEmpty() ? Collections.<String>emptyList() : Collections.singletonList(branchName);
                messages = HgCommand.getLogMessages(root, files, fromRevision, toRevision, includeMerges, false, includeMerges, limitRevisions, branchNames, logger, true);
            }
        } catch (HgException.HgCommandCanceledException ex) {
            // do not take any action
        } catch (HgException ex) {
            HgUtils.notifyException(ex);
        }
        return appendResults(root, messages);
    }
  
    
    /**
     * Processes search results from a single repository. 
     * 
     * @param root repository root
     * @param logMessages events in chronological order
     */ 
    private List<RepositoryRevision> appendResults(VCSFileProxy root, HgLogMessage[] logMessages) {
        List<RepositoryRevision> results = new ArrayList<>();
        // traverse in reverse chronological order
        for (int i = logMessages.length - 1; i >= 0; i--) {
            HgLogMessage logMessage = logMessages[i];
            RepositoryRevision rev = new RepositoryRevision(logMessage, root, getCurrentRevisionKind(), master.getRoots(), getBranches(logMessage));
            results.add(rev);
        }
        return results;
    }
    
    private Kind getCurrentRevisionKind () {
        if (master.isIncomingSearch()) {
            return Kind.INCOMING;
        } else if (master.isOutSearch()) {
            return Kind.OUTGOING;
        } else {
            return Kind.LOCAL;
        }
    }

    private void checkFinished(final List<RepositoryRevision> results) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(results.isEmpty()) {
                    master.setResults(null, -1);
                } else {
                    master.setResults(results, limitRevisions);
                }

            }
        });
    }

    List<RepositoryRevision> search (int count, HgProgressSupport supp) {
        return search(fromRevision, toRevision, count, branchName, supp, supp.getLogger());
    }

    /**
     * Returns set of branches the given log message is head of
     * @param logMessage
     * @return 
     */
    private Set<String> getBranches (HgLogMessage logMessage) {
        Set<String> headOfBranches = new HashSet<>(2);
        for (HgBranch b : branches) {
            if (b.getRevisionInfo().getCSetShortID().equals(logMessage.getCSetShortID())) {
                headOfBranches.add(b.getName());
            }
        }
        return headOfBranches;
    }
  
}
