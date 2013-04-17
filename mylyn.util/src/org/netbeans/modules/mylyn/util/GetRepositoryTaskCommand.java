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
package org.netbeans.modules.mylyn.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 *
 * @author Ondrej Vrabec
 */
public class GetRepositoryTaskCommand extends BugtrackingCommand {

    private String stringValue;
    private final TaskRepository taskRepository;
    private final String taskId;
    private final CancelableProgressMonitor monitor;
    private final TaskDataManager taskDataManager;
    private final AbstractRepositoryConnector connector;
    private ITask task;
    private boolean taskAdded;

    GetRepositoryTaskCommand (AbstractRepositoryConnector connector,
            TaskRepository taskRepository, String taskId,
            TaskDataManager taskDataManager) {
        this.connector = connector;
        this.taskRepository = taskRepository;
        this.taskId = taskId;
        this.taskDataManager = taskDataManager;
        this.monitor = new CancelableProgressMonitor();
    }

    @Override
    public void execute () throws CoreException, IOException, MalformedURLException {
        Logger log = Logger.getLogger(this.getClass().getName());
        if (log.isLoggable(Level.FINE)) {
            log.log(
                    Level.FINE,
                    "executing GetRepositoryTaskCommand for task id {0}:{1}", //NOI18N
                    new Object[] { taskRepository.getUrl(), taskId });
        }
        task = MylynSupport.getInstance().getOrCreateTask(taskRepository, taskId, true);
        TaskData taskData = connector.getTaskData(taskRepository, taskId, monitor);
        if (monitor.isCanceled()) {
            return;
        }
        if (taskData != null) {
            taskDataManager.putUpdatedTaskData(task, taskData, true);
            taskAdded = true;
        }
    }

    @Override
    public void cancel () {
        monitor.setCanceled(true);
    }

    @Override
    public String toString () {
        if (stringValue == null) {
            StringBuilder sb = new StringBuilder()
                    .append("Getting task ") //NOI18N
                    .append(taskId)
                    .append(",repository=") //NOI18N
                    .append(taskRepository.getUrl())
                    .append("]"); //NOI18N
            stringValue = sb.toString();
        }
        return stringValue;
    }

    public ITask getTask () {
        return taskAdded ? task : null;
    }
}
