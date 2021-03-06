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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.j2ee.clientproject;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.Session;
import org.netbeans.api.debugger.jpda.AttachingDICookie;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.modules.j2ee.api.ejbjar.EjbProjectConstants;
import org.netbeans.modules.j2ee.clientproject.ui.customizer.AppClientProjectProperties;
import org.netbeans.modules.javaee.project.api.ant.ui.J2EEProjectProperties;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.netbeans.modules.java.api.common.ant.UpdateHelper;
import org.netbeans.modules.java.api.common.project.BaseActionProvider;
import org.netbeans.spi.project.SingleMethod;
import org.openide.util.Lookup;

/**
 * Action provider of the Application Client project.
 */
class AppClientActionProvider extends BaseActionProvider {
    
    private static final String COMMAND_VERIFY = "verify"; //NOI18N
    
    // Commands available from Application Client project
    private static final String[] supportedActions = {
        COMMAND_BUILD,
        COMMAND_CLEAN,
        COMMAND_REBUILD,
        COMMAND_COMPILE_SINGLE,
        COMMAND_RUN,
        COMMAND_RUN_SINGLE,
        COMMAND_DEBUG,
        COMMAND_DEBUG_SINGLE,
        EjbProjectConstants.COMMAND_REDEPLOY,
        JavaProjectConstants.COMMAND_JAVADOC,
        COMMAND_TEST,
        COMMAND_TEST_SINGLE,
        COMMAND_DEBUG_TEST_SINGLE,
        SingleMethod.COMMAND_RUN_SINGLE_METHOD,
        SingleMethod.COMMAND_DEBUG_SINGLE_METHOD,
        JavaProjectConstants.COMMAND_DEBUG_FIX,
        COMMAND_DEBUG_STEP_INTO,
        COMMAND_VERIFY,
        COMMAND_DELETE,
        COMMAND_COPY,
        COMMAND_MOVE,
        COMMAND_RENAME,
    };
    
    
    private static final String[] platformSensitiveActions = {
        COMMAND_BUILD,
        COMMAND_REBUILD,
        COMMAND_COMPILE_SINGLE,
        COMMAND_RUN,
        COMMAND_RUN_SINGLE,
        COMMAND_DEBUG,
        COMMAND_DEBUG_SINGLE,
        JavaProjectConstants.COMMAND_JAVADOC,
        COMMAND_TEST,
        COMMAND_TEST_SINGLE,
        COMMAND_DEBUG_TEST_SINGLE,
        JavaProjectConstants.COMMAND_DEBUG_FIX,
        COMMAND_DEBUG_STEP_INTO,
        SingleMethod.COMMAND_RUN_SINGLE_METHOD,
        SingleMethod.COMMAND_DEBUG_SINGLE_METHOD,
    };
    
    /**Set of commands which are affected by background scanning*/
    private Set<String> bkgScanSensitiveActions;

    /**Set of commands which need java model up to date*/
    private Set<String> needJavaModelActions;

    private static final String[] actionsDisabledForQuickRun = {
        COMMAND_COMPILE_SINGLE,
        JavaProjectConstants.COMMAND_DEBUG_FIX,
    };

    /** Map from commands to ant targets */
    Map<String,String[]> commands;
    
    public AppClientActionProvider( AppClientProject project, UpdateHelper updateHelper ) {
        super(project, updateHelper, project.evaluator(), project.getSourceRoots(),
                project.getTestSourceRoots(), project.getAntProjectHelper(), 
                new BaseActionProvider.CallbackImpl(project.getClassPathProvider()));
        commands = new HashMap<String, String[]>();
        commands.put(COMMAND_BUILD, new String[] {"dist"}); // NOI18N
        commands.put(COMMAND_CLEAN, new String[] {"clean"}); // NOI18N
        commands.put(COMMAND_REBUILD, new String[] {"clean", "dist"}); // NOI18N
        commands.put(COMMAND_COMPILE_SINGLE, new String[] {"compile-single"}); // NOI18N
        commands.put(COMMAND_RUN, new String[] {"run"}); // NOI18N
        commands.put(COMMAND_RUN_SINGLE, new String[] {"run-single"}); // NOI18N
        commands.put(EjbProjectConstants.COMMAND_REDEPLOY, new String[] {"run-deploy"}); // NOI18N
        commands.put(COMMAND_DEBUG, new String[] {"debug"}); // NOI18N
        commands.put(COMMAND_DEBUG_SINGLE, new String[] {"debug-single"}); // NOI18N
        commands.put(JavaProjectConstants.COMMAND_JAVADOC, new String[] {"javadoc"}); // NOI18N
        commands.put(COMMAND_TEST, new String[] {"test"}); // NOI18N
        commands.put(COMMAND_TEST_SINGLE, new String[] {"test-single"}); // NOI18N
        commands.put(COMMAND_DEBUG_TEST_SINGLE, new String[] {"debug-test"}); // NOI18N
        commands.put(JavaProjectConstants.COMMAND_DEBUG_FIX, new String[] {"debug-fix"}); // NOI18N
        commands.put(COMMAND_DEBUG_STEP_INTO, new String[] {"debug-stepinto"}); // NOI18N
        commands.put(COMMAND_VERIFY, new String[] {"verify"}); // NOI18N
        commands.put(COMMAND_DEBUG_SINGLE, new String[] {"debug-single"}); // NOI18N
        commands.put(SingleMethod.COMMAND_RUN_SINGLE_METHOD, new String[] {"test-single-method"}); // NOI18N
        commands.put(SingleMethod.COMMAND_DEBUG_SINGLE_METHOD, new String[] {"debug-single-method"}); // NOI18N

        this.needJavaModelActions = new HashSet<String>(Arrays.asList(
            JavaProjectConstants.COMMAND_DEBUG_FIX
        ));
        
        this.bkgScanSensitiveActions = new HashSet<String>(Arrays.asList(new String[] {
            COMMAND_RUN,
            COMMAND_RUN_SINGLE,
            COMMAND_DEBUG,
            COMMAND_DEBUG_SINGLE,
            COMMAND_DEBUG_STEP_INTO,
            SingleMethod.COMMAND_RUN_SINGLE_METHOD,
            SingleMethod.COMMAND_DEBUG_SINGLE_METHOD
        }));
    }

    @Override
    protected String[] getPlatformSensitiveActions() {
        return platformSensitiveActions;
    }

    @Override
    protected String[] getActionsDisabledForQuickRun() {
        return actionsDisabledForQuickRun;
    }

    @Override
    public Map<String, String[]> getCommands() {
        return commands;
    }

    @Override
    protected Set<String> getScanSensitiveActions() {
        return bkgScanSensitiveActions;
    }

    @Override
    protected Set<String> getJavaModelActions() {
        return needJavaModelActions;
    }

    @Override
    protected boolean isCompileOnSaveEnabled() {
        return false; // CoS not implemented for AppClient
    }

    @Override
    public String[] getSupportedActions() {
        return supportedActions;
    }

    @Override
    public String[] getTargetNames(String command, Lookup context, Properties p, boolean doJavaChecks) throws IllegalArgumentException {
        if (command.equals(COMMAND_RUN) || command.equals(EjbProjectConstants.COMMAND_REDEPLOY) || 
                command.equals(COMMAND_DEBUG) || command.equals(COMMAND_DEBUG_SINGLE) || command.equals(COMMAND_RUN_SINGLE)) {
            if (!checkSelectedServer(command.equals(COMMAND_DEBUG) || command.equals(COMMAND_DEBUG_SINGLE), false, false)) {
                return null;
            }
            if (isDebugged()) {
                p.setProperty("is.debugged", "true");
            }
            if (command.equals(EjbProjectConstants.COMMAND_REDEPLOY)) {
                p.setProperty("forceRedeploy", "true"); //NOI18N
            } else {
                p.setProperty("forceRedeploy", "false"); //NOI18N
            }
        }
        return super.getTargetNames(command, context, p, doJavaChecks);
    }
    
    @Override
    public boolean isActionEnabled( String command, Lookup context ) {
        boolean res = super.isActionEnabled(command, context);
        if (res && command.equals(COMMAND_VERIFY)) {
            return ((AppClientProject)getProject()).getCarModule().hasVerifierSupport();
        }
        if (command.equals(COMMAND_RUN) || command.equals(COMMAND_DEBUG)) {
            //see issue #92895
            //XXX - replace this method with a call to API as soon as issue 109895 will be fixed
            return res && checkSelectedServer(command.equals(COMMAND_DEBUG), false, true) && !isTargetServerRemote();
        }
        return res;
    }
    
    private boolean checkSelectedServer(boolean checkDebug, boolean checkProfile, boolean noMessages) {
        return J2EEProjectProperties.checkSelectedServer(getProject(), getAntProjectHelper(),
                ((AppClientProject) getProject()).getAPICar().getJ2eeProfile(), J2eeModule.Type.CAR, new J2EEProjectProperties.SetServerInstanceCallback() {

            @Override
            public void setServerInstance(String serverInstanceId) {
                AppClientActionProvider.this.setServerInstance(serverInstanceId);
            }
        }, checkDebug, checkProfile, false);
    }
    
    private void setServerInstance(final String serverInstanceId) {
        AppClientProjectProperties.setServerInstance((AppClientProject)getProject(), getAntProjectHelper(), serverInstanceId);
    }
    
   private boolean isDebugged() {
        J2eeModuleProvider jmp = getProject().getLookup().lookup(J2eeModuleProvider.class);
        ServerDebugInfo sdi = jmp.getServerDebugInfo();
        if (sdi == null) {
            return false;
        }
        Session[] sessions = DebuggerManager.getDebuggerManager().getSessions();
        
        for (int i=0; i < sessions.length; i++) {
            Session s = sessions[i];
            if (s != null) {
                Object o = s.lookupFirst(null, AttachingDICookie.class);
                if (o != null) {
                    AttachingDICookie attCookie = (AttachingDICookie)o;
                    if (sdi.getTransport().equals(ServerDebugInfo.TRANSPORT_SHMEM)) {
                        if (attCookie.getSharedMemoryName().equalsIgnoreCase(sdi.getShmemName())) {
                            return true;
                        }
                    } else {
                        if (attCookie.getHostName().equalsIgnoreCase(sdi.getHost()) &&
                                attCookie.getPortNumber() == sdi.getPort()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
   
    private boolean isTargetServerRemote() {
        J2eeModuleProvider module = getProject().getLookup().lookup(J2eeModuleProvider.class);
        InstanceProperties props = module.getInstanceProperties();
        String domain = props.getProperty("DOMAIN"); //NOI18N
        String location = props.getProperty("LOCATION"); //NOI18N
        return "".equals(domain) && "".equals(location); //NOI18N
    }
}
