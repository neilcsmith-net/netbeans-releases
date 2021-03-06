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

package org.netbeans.tests.j2eeserver.plugin.jsr88;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.shared.DConfigBeanVersionType;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException;
import javax.enterprise.deploy.spi.exceptions.InvalidModuleException;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.j2ee.deployment.plugins.api.AppChangeDescriptor;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerProgress;


/**
 *
 * @author  gfink
 * @author nn136682
 */
public class TestDeploymentManager implements DeploymentManager {

    public static final String PLATFORM_ROOT_PROPERTY = "platform";

    public static final String MULTIPLE_TARGETS = "multitargets";

    public static final String WORK_DIR = "workdir";

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(TestDeploymentManager.class.getName());
    
    private final String url;

    private TestTarget[] targets;

    public TestDeploymentManager(String url, String user, String password) {
        this.url = url;
    }

    public String getName() {
        return url;
    }

    public InstanceProperties getInstanceProperties() {
        return InstanceProperties.getInstanceProperties(url);
    }

    public DeploymentConfiguration createConfiguration(DeployableObject deployableObject) throws InvalidModuleException {
        return null;
    }

    public ProgressObject distribute(Target[] targets, final File file, File file2) throws java.lang.IllegalStateException {
        LOGGER.log(java.util.logging.Level.FINEST,"Deploying " + file + " with " + file2);

        final TestProgressObject po = new TestProgressObject(this, targets, file, file2);
        Runnable r = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(200); //some latency
                } catch (Exception e) {
                    LOGGER.log(Level.INFO, null, e);
                }
                po.setStatusDistributeRunning("TestPluginDM: distributing "+ file);
                try {
                    Thread.sleep(500); //super server starting time
                } catch (Exception e) {
                    LOGGER.log(Level.INFO, null, e);
                }
                if (getTestBehavior() == DISTRIBUTE_FAILED) {
                    po.setStatusStartFailed("TestPluginDM distribute failed");
                } else {
                    po.setStatusStartCompleted("TestPluginDM distribute finish");
                }
            }
        };

        (new Thread(r)).start();
        return po;
    }

    public TestProgressObject incrementalDeploy(final TargetModuleID target, AppChangeDescriptor desc) throws java.lang.IllegalStateException {
        final TestProgressObject po = new TestProgressObject(this, new TargetModuleID[] { target });
        Runnable r = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(50); //some latency
                } catch (Exception e) {
                    LOGGER.log(Level.INFO, null, e);
                }
                po.setStatusDistributeRunning("TestPluginDM: incrementally deploying "+ target);
                try {
                    Thread.sleep(500); //super server starting time
                } catch (Exception e) {
                    LOGGER.log(Level.INFO, null, e);
                }
                if (getTestBehavior() == DISTRIBUTE_FAILED) {
                    po.setStatusStartFailed("TestPluginDM incremental deploy failed");
                } else {
                    po.setStatusStartCompleted("TestPluginDM incremental deploy finish");
                }
            }
        };

        (new Thread(r)).start();
        return po;
    }

    public boolean hasDistributed(String id) {
        for (int i = 0; i < getTargets().length; i++) {
            TestTarget t = (TestTarget) getTargets()[i];
            if (t.getTargetModuleID(id) != null) {
                return true;
            }
        }
        return false;
    }

    public ProgressObject distribute(Target[] target, InputStream inputStream, InputStream inputStream2) throws java.lang.IllegalStateException {
        return new TestProgressObject(this, target,inputStream,inputStream2);
    }

    public ProgressObject distribute(Target[] target, ModuleType moduleType, InputStream inputStream, InputStream inputStream0) throws IllegalStateException {
        return distribute(target, inputStream, inputStream0);
    }

    public TargetModuleID[] getAvailableModules(ModuleType moduleType, Target[] target) throws TargetException, java.lang.IllegalStateException {
        List l = new ArrayList();
        TestTarget[] mytargets = (TestTarget[]) getTargets();
        HashSet yours = new HashSet(Arrays.asList(target));
        for (int i = 0; i < mytargets.length; i++) {
            if (yours.contains(mytargets[i])) {
                l.addAll(Arrays.asList((mytargets[i]).getTargetModuleIDs()));
            }
        }
        return (TargetModuleID[]) l.toArray(new TargetModuleID[0]);
    }

    public Locale getCurrentLocale() {
        return Locale.getDefault();
    }

    public DConfigBeanVersionType getDConfigBeanVersion() {
        return DConfigBeanVersionType.V1_3;
    }

    public Locale getDefaultLocale() {
        return Locale.getDefault();
    }

    public TargetModuleID[] getNonRunningModules(ModuleType moduleType, Target[] target) throws TargetException, java.lang.IllegalStateException {
        return new TargetModuleID[0]; // PENDING see above.
    }

    public TargetModuleID[] getRunningModules(ModuleType moduleType, Target[] target) throws TargetException, java.lang.IllegalStateException {
        return new TargetModuleID[0]; // PENDING see above.
    }

    public Locale[] getSupportedLocales() {
        return new Locale[] { Locale.getDefault() };
    }

    public synchronized Target[] getTargets() throws IllegalStateException {
        if (targets == null) {
            if (getInstanceProperties().getProperty(MULTIPLE_TARGETS) == null
                    || Boolean.parseBoolean(getInstanceProperties().getProperty(MULTIPLE_TARGETS))) {
                targets = new TestTarget[] {new TestTarget("Target 1"), new TestTarget("Target 2")};
            } else {
                targets = new TestTarget[] {new TestTarget("Target")};
            }
        }
        return targets;
    }

    public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType dConfigBeanVersionType) {
        return true;
    }

    public boolean isLocaleSupported(Locale locale) {
        return Locale.getDefault().equals(locale);
    }

    public boolean isRedeploySupported() {
        return true; // PENDING use jsr88 redeploy?
    }

    public ProgressObject redeploy(TargetModuleID[] targetModuleID, InputStream inputStream, InputStream inputStream2) throws java.lang.UnsupportedOperationException, java.lang.IllegalStateException {
        throw new UnsupportedOperationException();
    }

    Set redeployed;
    public ProgressObject redeploy(TargetModuleID[] targetModuleID, File file, File file2) throws java.lang.UnsupportedOperationException, java.lang.IllegalStateException {
        final TestProgressObject po = new TestProgressObject(this, targetModuleID);
        final java.util.List targetModules = java.util.Arrays.asList(targetModuleID);
        Runnable r = new Runnable() {
            public void run() {
                try { Thread.sleep(200); //some latency
                } catch (Exception e) {}
                po.setStatusRedeployRunning("TestPluginDM: redeploy "+ targetModules);
                redeployed = new HashSet();
                try { Thread.sleep(500); //super server starting time
                } catch (Exception e) {}
                if (getTestBehavior() == REDEPLOY_FAILED) {
                    po.setStatusRedeployFailed("TestPluginDM: failed to redeploy "+targetModules);
                } else {
                    po.setStatusRedeployCompleted("TestPluginDM: done redeploy "+targetModules);
                    TargetModuleID[] result = po.getResultTargetModuleIDs();
                    for (int i=0; i<result.length; i++)
                        redeployed.add(result[i].toString());
                }
            }
        };

        (new Thread(r)).start();
        return po;
    }
    public boolean hasRedeployed(String id) {
        return redeployed != null && redeployed.contains(id);
    }

    public void release() {
    }

    public void setDConfigBeanVersion(DConfigBeanVersionType dConfigBeanVersionType) throws DConfigBeanVersionUnsupportedException {
    }

    public void setLocale(Locale locale) throws java.lang.UnsupportedOperationException {
    }

    public ProgressObject start(TargetModuleID[] targetModuleID) throws java.lang.IllegalStateException {
        final TestProgressObject po = new TestProgressObject(this, targetModuleID);
        final java.util.List targetModules = java.util.Arrays.asList(targetModuleID);
        Runnable r = new Runnable() {
            public void run() {
                try { Thread.sleep(200); //some latency
                } catch (Exception e) {}
                po.setStatusStartRunning("TestPluginDM: starting "+ targetModules);
                try { Thread.sleep(500); //super server starting time
                } catch (Exception e) {}
                if (getTestBehavior() == START_MODULES_FAILED) {
                    po.setStatusStartFailed("TestPluginDM: failed to start "+targetModules);
                } else {
                    po.setStatusStartCompleted("TestPluginDM: done starting "+targetModules);
                }
            }
        };

        (new Thread(r)).start();
        return po;
    }

    public ProgressObject stop(TargetModuleID[] targetModuleID) throws java.lang.IllegalStateException {
        final TestProgressObject po = new TestProgressObject(this, targetModuleID);
        final java.util.List targetModules = java.util.Arrays.asList(targetModuleID);
        Runnable r = new Runnable() {
            public void run() {
                try { Thread.sleep(200); //some latency
                } catch (Exception e) {}
                po.setStatusStopRunning("TestPluginDM: stopping "+ targetModules);
                try { Thread.sleep(500); //super server starting time
                } catch (Exception e) {}
                if (getTestBehavior() == STOP_MODULES_FAILED) {
                    po.setStatusStopFailed("TestPluginDM: failed to stop "+targetModules);
                } else {
                    po.setStatusStopCompleted("TestPluginDM: done stopping "+targetModules);
                }
            }
        };

        (new Thread(r)).start();
        return po;
    }

    public ProgressObject undeploy(TargetModuleID[] targetModuleID) throws java.lang.IllegalStateException {
        return new TestProgressObject(this, targetModuleID);
    }

    public static final int NORMAL = 0;
    public static final int START_FAILED = 1;
    public static final int STOP_FAILED = 2;
    public static final int START_MODULES_FAILED = 3;
    public static final int STOP_MODULES_FAILED = 4;
    public static final int DISTRIBUTE_FAILED = 5;
    public static final int REDEPLOY_FAILED = 6;

    private int testBehavior = NORMAL;
    public void setTestBehavior(int behavior) {
        testBehavior = behavior;
    }
    public int getTestBehavior() {
        return testBehavior;
    }
    public ServerProgress createServerProgress() {
        return new TestDeploymentManager.TestServerProgress();
    }
    public static final int STOPPED = 0;
    public static final int STARTING = 1;
    public static final int RUNNING = 2;
    public static final int STOPPING = 3;
    public static final int FAILED = 4;
    private int state = STOPPED;
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
    private class TestServerProgress extends ServerProgress {
        public TestServerProgress() {
            super(TestDeploymentManager.this);
        }
    }
}
