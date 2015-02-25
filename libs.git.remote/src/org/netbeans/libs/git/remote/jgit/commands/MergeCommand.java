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

package org.netbeans.libs.git.remote.jgit.commands;

import org.netbeans.libs.git.remote.GitException;
import org.netbeans.libs.git.remote.GitMergeResult;
import org.netbeans.libs.git.remote.GitMergeResult.MergeResultContainer;
import org.netbeans.libs.git.remote.GitMergeResult.MergeStatus;
import org.netbeans.libs.git.remote.GitRepository.FastForwardOption;
import org.netbeans.libs.git.remote.GitRevisionInfo;
import org.netbeans.libs.git.remote.jgit.GitClassFactory;
import org.netbeans.libs.git.remote.jgit.JGitRepository;
import org.netbeans.libs.git.remote.progress.ProgressMonitor;
import org.netbeans.modules.remotefs.versioning.api.ProcessUtils;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;

/**
 *
 * @author ondra
 */
public class MergeCommand extends GitCommand {
    private final String revision;
    private GitMergeResult result;
    private String commitMessage;
    private final FastForwardOption ffOption;
    private final ProgressMonitor monitor;

    public MergeCommand (JGitRepository repository, GitClassFactory gitFactory, String revision,
            FastForwardOption ffOption, ProgressMonitor monitor) {
        super(repository, gitFactory, monitor);
        this.ffOption = ffOption;
        this.revision = revision;
        this.monitor = monitor;
    }

    @Override
    protected void prepare() throws GitException {
        setCommandsNumber(2);
        super.prepare();
        addArgument(0, "merge"); //NOI18N
        //addArgument(0, "--log"); //NOI18N
        //addArgument(0, "--stat"); //NOI18N
        if (ffOption != null) {
            addArgument(0, ffOption.toString());
        }
        addArgument(0, revision);

        addArgument(1, "log"); //NOI18N
        addArgument(1, "--raw"); //NOI18N
        addArgument(1, "--pretty=raw"); //NOI18N
        addArgument(1, "-1"); //NOI18N
    }
    
    public GitMergeResult getResult () {
        return result;
    }

    void setCommitMessage (String message) {
        if (message != null) {
            message = message.replace("\n", "").replace("\r", ""); //NOI18N
        }
        this.commitMessage = message;
    }

    @Override
    protected void run () throws GitException {
        ProcessUtils.Canceler canceled = new ProcessUtils.Canceler();
        if (monitor != null) {
            monitor.setCancelDelegate(canceled);
        }
        try {
            final MergeResultContainer merge = new GitMergeResult.MergeResultContainer();
            new Runner(canceled, 0){

                @Override
                public void outputParser(String output) throws GitException {
                    parseMergeOutput(output, merge);
                }

                @Override
                protected void errorParser(String error) throws GitException {
                    parseMergeError(error, merge);
                }
                
                
            }.runCLI();
            if (merge.mergeStatus == MergeStatus.MERGED) {
                final GitRevisionInfo.GitRevCommit status = new GitRevisionInfo.GitRevCommit();
                new Runner(canceled, 1){

                    @Override
                    public void outputParser(String output) throws GitException {
                        CommitCommand.parseLog(output, status);
                    }
                }.runCLI();
                merge.mergedCommits = status.parents.toArray(new String[status.parents.size()]);
            }
            if (canceled.canceled()) {
                return;
            }
            
            result = getClassFactory().createMergeResult(merge, this.getRepository().getLocation());
            
            //command.commandCompleted(exitStatus.exitCode);
        } catch (GitException t) {
            throw t;
        } catch (Throwable t) {
            if (canceled.canceled()) {
            } else {
                throw new GitException(t);
            }
        } finally {
            //command.commandFinished();
        }
//        Repository repository = getRepository().getRepository();
//        org.eclipse.jgit.api.MergeCommand command = new Git(repository).merge();
//        setFastForward(command);
//        Ref ref = null;
//        try {
//            ref = repository.getRef(revision);
//        } catch (IOException ex) {
//            throw new GitException(ex);
//        }
//
//        if (ref == null) {
//            command.include(Utils.findCommit(repository, revision));
//        } else {
//            String msg = commitMessage;
//            if (msg == null) {
//                msg = Utils.getRefName(ref);
//            }
//            command.include(msg, ref.getTarget().getObjectId());
//        }
//        try {
//            result = getClassFactory().createMergeResult(command.call(), getRepository().getLocation());
//        } catch (org.eclipse.jgit.api.errors.CheckoutConflictException ex) {
//            parseConflicts(ex);
//        } catch (JGitInternalException ex) {
//            if (ex.getCause() instanceof CheckoutConflictException) {
//                parseConflicts(ex.getCause());
//            }
//            throw new GitException(ex);
//        } catch (GitAPIException ex) {
//            throw new GitException(ex);
//        }
    }
    
    private void parseMergeOutput(String output, MergeResultContainer merge) {
        //git --no-pager merge e1085394d4624627297cc379e452d50b238fd95b
        //Merge made by the 'recursive' strategy.
        // f1 |    2 +-
        // 1 file changed, 1 insertion(+), 1 deletion(-)
        //
        //git --no-pager merge new_branch
        //Updating a0f0b66..420cc12
        //Fast-forward
        // file |    2 +-
        // 1 file changed, 1 insertion(+), 1 deletion(-)
        //
        //git --no-pager merge new_branch
        //Already up-to-date.
        //
        //git --no-pager merge new_branch
        //Auto-merging file
        //Merge made by the 'recursive' strategy.
        // file |    2 +-
        // 1 file changed, 1 insertion(+), 1 deletion(-)
        merge.mergeStatus = MergeStatus.NOT_SUPPORTED;
        System.err.println(output);
        for (String line : output.split("\n")) { //NOI18N
            if (line.startsWith("Updating ")) {
                String s = line.substring(9).trim();
                int i = s.indexOf("..");
                if (i > 0) {
                    merge.base = s.substring(0, i);
                    merge.newHead = s.substring(i+2);
                }
                continue;
            }
            if (line.startsWith(" ")) {
                int i = line.indexOf('|');
                if (i > 0) {
                    String file = line.substring(1,i).trim();
                }
                continue;
            }
            if (line.startsWith("Already up-to-date")) {
                merge.mergeStatus = MergeStatus.ALREADY_UP_TO_DATE;
                continue;
            }
            if (line.startsWith("Fast-forward")) {
                merge.mergeStatus = MergeStatus.FAST_FORWARD;
                continue;
            }
            if (line.startsWith("Auto-merging")) {
                merge.mergeStatus = MergeStatus.MERGED;
                continue;
            }
            if (line.startsWith("Merge")) {
                merge.mergeStatus = MergeStatus.MERGED;
                continue;
            }
            if (line.startsWith("Abort")) {
                merge.mergeStatus = MergeStatus.ABORTED;
                continue;
            }
            if (line.startsWith("Fail")) {
                merge.mergeStatus = MergeStatus.FAILED;
                continue;
            }
            if (line.startsWith("Conflict")) {
                merge.mergeStatus = MergeStatus.CONFLICTING;
                continue;
            }
        }
    }
    
    private void parseMergeError(String output, MergeResultContainer merge) {
        //error: Your local changes to the following files would be overwritten by merge:
        //	file
        //	file2
        //Please, commit your changes or stash them before you can merge.
        //Aborting        
        merge.mergeStatus = MergeStatus.NOT_SUPPORTED;
        System.err.println(output);
        for (String line : output.split("\n")) { //NOI18N
            if (line.startsWith("\t")) {
                String file = line.substring(1).trim();
                continue;
            }
            if (line.startsWith("Abort")) {
                merge.mergeStatus = MergeStatus.ABORTED;
                continue;
            }
        }
    }
}
