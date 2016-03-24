/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.jshell.launch;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.module.spi.AntEvent;
import org.apache.tools.ant.module.spi.AntLogger;
import org.apache.tools.ant.module.spi.AntSession;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author sdedic
 */
@ServiceProvider(service = AntLogger.class)
public class LaunchAntListener extends AntLogger {
    private String[] MONITOR_TARGETS = { 
        "do-debug-test-single",
        "debug-test-single-nb",
        "do-debug-test-main",
        "debug-test-main-nb",
        "debug-test-with-main",
        "debug-single",
        "debug"
    };
    private static final Logger LOG = Logger.getLogger(LaunchAntListener.class.getName());
    
    private ShellLaunchManager mgr;
    
    private ShellLaunchManager getManager() {
        if (mgr == null) {
            mgr = Lookup.getDefault().lookup(ShellLaunchManager.class);
            if (mgr == null) {
                throw new IllegalStateException("Could not find launch manager");
            }
        }
        return mgr;
    }

    @Override
    public boolean interestedInAllScripts(AntSession session) {
        return true;
    }

    @Override
    public String[] interestedInTargets(AntSession session) {
        return MONITOR_TARGETS;
    }

    @Override
    public void targetStarted(AntEvent event) {
        if (!Arrays.asList(MONITOR_TARGETS).contains(event.getTargetName())) {
            return;
        }
        super.targetStarted(event);
        InputOutput io = event.getSession().getIO();
        String authKey = getAuthKey(event.evaluate("${run.jvmargs.ide}"));
        String authKey2 = getAuthKey(event.evaluate("${run.jvmargs}"));
        if (authKey == null && authKey2 == null) {
            return;
        }
        getManager().attachInputOutput(authKey, io);
        getManager().attachInputOutput(authKey2, io);
    }
    
    @Override
    public boolean interestedInSession(AntSession session) {
        LOG.log(Level.FINE, "Checking interestInSession: " + session);
        String s = session.getProperties().get("run.jvmargs");
        return s != null && s.contains("netbeans-lib-jshell-agent.jar"); // NOI18N
        /*
        if (path == null) {
            return false;
        }
        FileObject f = FileUtil.toFileObject(new File(path));
        if (f == null) {
            return false;
        }
        Project p;
        try {
            p = ProjectManager.getDefault().findProject(f);
        } catch (IOException | IllegalArgumentException ex) {
            return false;
        }
        if (p == null) {
            return false;
        }
        boolean interested = ProjectUtils.isJShellRunEnabled(p);
        LOG.log(Level.FINE, "Session's project is: " + p + ", interested: " + interested);
        return interested;
                */
    }
    
    private static final Pattern REGEXP_KEY = Pattern.compile("-javaagent:[^ ]*key=([^,]+)");
    
    private String getAuthKey(String args) {
        if (args == null) {
            return null;
        }
        Matcher m = REGEXP_KEY.matcher(args);
        return m.find() ? m.group(1) : null;
    }

    @Override
    public void buildFinished(AntEvent event) {
        LOG.log(Level.FINE, "Got build finished: " + event);
        destroyAgent(event);
    }
    
    private void destroyAgent(AntEvent event) {
        String authKey = getAuthKey(event.evaluate("${run.jvmargs.ide}"));
        String authKey2 = getAuthKey(event.evaluate("${run.jvmargs}"));
        RequestProcessor.getDefault().post(() -> {
            getManager().destroyAgent(authKey);
            getManager().destroyAgent(authKey2);
        }, 5000);
    }

    @Override
    public void buildInitializationFailed(AntEvent event) {
        LOG.log(Level.FINE, "Got build init failed: " + event);
        destroyAgent(event);
    }
    
}
