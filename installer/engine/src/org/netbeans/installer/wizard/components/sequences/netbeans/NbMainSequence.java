/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2012 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Oracle
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 * 
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */
package org.netbeans.installer.wizard.components.sequences.netbeans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipOutputStream;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.utils.FileUtils;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.helper.ExecutionMode;
import org.netbeans.installer.utils.helper.ExecutionResults;
import org.netbeans.installer.utils.progress.CompositeProgress;
import org.netbeans.installer.utils.progress.Progress;
import org.netbeans.installer.utils.progress.ProgressListener;
import org.netbeans.installer.wizard.components.WizardAction;
import org.netbeans.installer.wizard.components.WizardComponent;
import org.netbeans.installer.wizard.components.WizardSequence;
import org.netbeans.installer.wizard.components.actions.DownloadConfigurationLogicAction;
import org.netbeans.installer.wizard.components.actions.DownloadInstallationDataAction;
import org.netbeans.installer.wizard.components.actions.InstallAction;
import org.netbeans.installer.wizard.components.actions.UninstallAction;
import org.netbeans.installer.wizard.components.actions.netbeans.NbMetricsAction;
import org.netbeans.installer.wizard.components.actions.netbeans.NbShowUninstallationSurveyAction;
import org.netbeans.installer.wizard.components.panels.LicensesPanel;
import org.netbeans.installer.wizard.components.panels.netbeans.NbJUnitLicensePanel;
import org.netbeans.installer.wizard.components.panels.netbeans.NbPostInstallSummaryPanel;
import org.netbeans.installer.wizard.components.panels.netbeans.NbPreInstallSummaryPanel;
import org.netbeans.installer.wizard.components.sequences.ProductWizardSequence;

/**
 *
 * @author Kirill Sorokin
 * @author Dmitry Lipin
 */
public class NbMainSequence extends WizardSequence {
    /////////////////////////////////////////////////////////////////////////////////
    // Instance

    private DownloadConfigurationLogicAction downloadConfigurationLogicAction;
    private LicensesPanel licensesPanel;
    private NbJUnitLicensePanel nbJUnitLicensePanel;
    private NbPreInstallSummaryPanel nbPreInstallSummaryPanel;
    private UninstallAction uninstallAction;
    private DownloadInstallationDataAction downloadInstallationDataAction;
    private InstallAction installAction;
    private NbPostInstallSummaryPanel nbPostInstallSummaryPanel;
    private NbMetricsAction metricsAction;
    private NbShowUninstallationSurveyAction showUninstallationSurveyAction;
    private Map<Product, ProductWizardSequence> productSequences;
    
    private static final String SIZE_UPDATES_PATTERN = "updates="; // NOI18N
    private static final String SIZE_MODULES_PATTERN = "modules="; // NOI18N
    private static final int INSTALL_STEP = 15;
    private static final int STARTUP_STEP = 20;

    public NbMainSequence() {
        downloadConfigurationLogicAction = new DownloadConfigurationLogicAction();
        licensesPanel = new LicensesPanel();
        nbJUnitLicensePanel = new NbJUnitLicensePanel();
        nbPreInstallSummaryPanel = new NbPreInstallSummaryPanel();
        uninstallAction = new UninstallAction();
        downloadInstallationDataAction = new DownloadInstallationDataAction();
        installAction = new InstallAction();
        nbPostInstallSummaryPanel = new NbPostInstallSummaryPanel();
        metricsAction = new NbMetricsAction();
        showUninstallationSurveyAction = new NbShowUninstallationSurveyAction();
        productSequences = new HashMap<Product, ProductWizardSequence>();

        installAction.setProperty(InstallAction.TITLE_PROPERTY,
                DEFAULT_IA_TITLE);
        installAction.setProperty(InstallAction.DESCRIPTION_PROPERTY,
                DEFAULT_IA_DESCRIPTION);
    }
    
    private static class PopulateCacheAction extends WizardAction {
        private final Product nbBase;
        private final Product nbJavaSE;
        CompositeProgress compositeProgress;
        private CountdownProgress countdownProgress;
        private int sizeOfModules = 0;
        private int sumOfModules = 0;
        private int sumOfUpdates = 0;
        private int loop = 0;
        private String oldDetail = null;
        private boolean downloadIsRunning = false;
        private int spendPercentage = 0;
        
        private static final String[] PREFIX_FOR_PROGRESS = new String[] {"FINE [org.netbeans.updater]: 780: ", "INFO: 780: "};
        
        public PopulateCacheAction(Product nbBase, Product nbJavaSE) {
            this.nbBase = nbBase;
            this.nbJavaSE = nbJavaSE;
            this.compositeProgress = new CompositeProgress();
        }

        @Override
        public void execute() {
            File tmpUserDir = new File(SystemUtils.getTempDirectory(), "tmpnb"); // NOI18N
            LogManager.log("try temporary directory for sure : ");
            try {                    
                if (tmpUserDir.exists()) {
                    FileUtils.deleteFile(tmpUserDir, true);
                }
            } catch (Exception ioe) {
                LogManager.log("    .... exception " + ioe.getMessage());
            }
            LogManager.log("running headless NetBeans IDE : ");
            getWizardUi().setProgress(compositeProgress);
            compositeProgress.setTitle(ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.title")); // NOI18N

            File nbInstallLocation = nbBase.getInstallationLocation();
            LogManager.log("    nbLocation = " + nbInstallLocation);
            
            String runIDE = new File(nbInstallLocation, "bin").getPath(); // NOI18N
            if (SystemUtils.isWindows()) {
                runIDE += File.separator + "netbeans.exe"; // NOI18N
            } else {
                runIDE += File.separator + "netbeans"; // NOI18N
            }
            
            File tmpCacheDir = new File(tmpUserDir, "var" + File.separator + "cache"); // NOI18N
            
            List<String> commandsBase = new ArrayList(Arrays.asList(runIDE,
                    "-J-Dnetbeans.close=true",
                    "--nosplash",
                    "-J-Dorg.netbeans.core.WindowSystem.show=false",
                    "--userdir",
                    tmpUserDir.getPath()));
            
            boolean installJUnit = nbJavaSE != null && Boolean.parseBoolean(nbJavaSE.getProperty(NbPreInstallSummaryPanel.JUNIT_ACCEPTED_PROPERTY));
            final boolean installJUnitOrig = installJUnit;
            boolean checkForUpdate = Boolean.getBoolean(NbPreInstallSummaryPanel.CHECK_FOR_UPDATES_CHECKBOX_PROPERTY);
            
            ExecutionResults executeResult;
            boolean start = true;
            System.out.println("###: spendPercentage BEFORE START " + spendPercentage);
            while (start) {
                System.out.println("==================================== START ======================");
                try {
                    compositeProgress.addProgressListener(new ProgressListener() {

                        @Override
                        public void progressUpdated(Progress progress) {
                            if (oldDetail != null && oldDetail.equals(progress.getDetail())) {
                                return ;
                            }
                            oldDetail = progress.getDetail();
                            if (progress.getDetail().startsWith(SIZE_UPDATES_PATTERN)
                                    || progress.getDetail().startsWith(SIZE_MODULES_PATTERN)) {
                                if (! downloadIsRunning) {
                                    downloadIsRunning = true;
                                    if (countdownProgress != null) {
                                        countdownProgress.detach();
                                    }
                                }
                                boolean modOrUpdate = progress.getDetail().startsWith(SIZE_MODULES_PATTERN);
                                String size = modOrUpdate ?
                                        progress.getDetail().substring(SIZE_MODULES_PATTERN.length()) :
                                        progress.getDetail().substring(SIZE_UPDATES_PATTERN.length());
                                try {
                                    sizeOfModules = Integer.valueOf(size);
                                    if (sizeOfModules > 0) {
                                        spendPercentage = spendPercentage + INSTALL_STEP;
                                        System.out.println("###(progressUpdated): spendPercentage INCREASED by " + INSTALL_STEP + " TO " + spendPercentage);
                                    }
                                    loop = 0;
                                    if (modOrUpdate) {
                                        sumOfModules = sumOfModules + sizeOfModules;
                                    } else {
                                        sumOfUpdates = sumOfUpdates + sizeOfModules;
                                    }
                                } catch (NumberFormatException nfe) {
                                    LogManager.log(nfe);
                                }
                                progress.setDetail("");
                            } else if (downloadIsRunning && sizeOfModules > 0) {
                                System.out.println("###: setPercentage(spendPercentage? " + spendPercentage + ", loop? + " + loop + ", sizeOfModules? " + sizeOfModules + "): " + (spendPercentage - INSTALL_STEP + Math.min(INSTALL_STEP, INSTALL_STEP * (loop+1) / (sizeOfModules * 2))));
                                compositeProgress.setPercentage(spendPercentage - INSTALL_STEP + Math.min(INSTALL_STEP, INSTALL_STEP * loop++ / (sizeOfModules * 2)));
                            }
                        }
                    });
                    executeResult = runIDE(commandsBase, nbInstallLocation, installJUnit, checkForUpdate, compositeProgress);
                    start = false;
                    if (executeResult.getErrorCode() > 0) {
                        LogManager.log("    .... exit code: " + executeResult.getErrorCode());
                        String msg = "";
                        switch (executeResult.getErrorCode()) {
                            case 31: // network problem
                                if (installJUnit && checkForUpdate) {
                                    msg = ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.NetworkProblemBoth"); // NOI18N
                                } else if (installJUnit) {
                                    msg = ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.NetworkProblemJunit"); // NOI18N
                                } else if (checkForUpdate) {
                                    msg = ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.NetworkProblemUpdates"); // NOI18N
                                }
                                break;
                            case 32: // install JUnit
                                msg = ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.ProblemInstallJUnit"); // NOI18N
                                break;
                            case 33: // update problem
                                msg = ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.ProblemInstallUpdates", sumOfUpdates); // NOI18N
                                break;
                            case 34: // timeout loading JUnit
                                LogManager.log("    ... timeout loading JUnit - run it again ");
                                start = true;
                                installJUnit = false;
                                downloadIsRunning = false;
                                break;
                        }
                        nbBase.setProperty(NbPostInstallSummaryPanel.NETBEANS_SUMMARY_MESSAGE_TEXT_PROPERTY, msg);
                    } else {
                        LogManager.log("    .... success ");
                        LogManager.log("    ...... installed " + sumOfModules + " new modules");
                        LogManager.log("    ...... installed " + sumOfUpdates + " updates");
                        String msg = "";
                        if (installJUnitOrig && checkForUpdate) {
                            if (sumOfUpdates > 0 && sumOfModules > 0) {
                                // installed JUnit and updates
                                msg = ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.SuccessIfBoth_JUnitInstalled_UpdatesInstalled", sumOfUpdates);
                            } else if (sumOfModules > 0) {
                                // installed JUnit and IDE is up-to-date (no updates)
                                msg = ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.SuccessIfBoth_JUnitInstalled_NoUpdates");
                            } else if (sumOfUpdates > 0) {
                                // JUnit not found and some updates was installed
                                msg = ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.SuccessIfBoth_JUnitNotFound_UpdatesInstalled", sumOfUpdates);
                            } else {
                                // JUnit not found and IDE is up-to-date (no updates)
                                msg = ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.SuccessIfBoth_JUnitNotFound_NoUpdates");
                            }
                        } else if (installJUnitOrig) {
                            msg = sumOfModules > 0 ? 
                                    // JUnit installed
                                    ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.SuccessIfJunit_JUnitInstalled") : // NOI18N
                                    // JUnit not found
                                    ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.SuccessIfJunit_JUnitNotFound"); // NOI18N
                        } else if (checkForUpdate) {
                            msg = sumOfUpdates > 0 ?
                                    // Updates installed
                                    ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.SuccessIfCheck_UpdatesInstalled", sumOfUpdates) : // NOI18N
                                    // No updates
                                    ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.SuccessIfCheck_NoUpdates"); // NOI18N
                        }
                        nbBase.setProperty(NbPostInstallSummaryPanel.NETBEANS_SUMMARY_MESSAGE_TEXT_PROPERTY, msg);
                    }
                } catch (Exception ioe) {
                    LogManager.log("    .... exception ", ioe);
                    return ;
                } finally {
                    LogManager.log("    .... done. ");
                }
            }
            
            LogManager.log("preparing caches : ");
            
            LogManager.log("    temporary cache location = " + tmpCacheDir);
            
            // zip OSGi cache
            try {
                LogManager.log("    zipping OSGi caches");
                File zipFile = new File(tmpCacheDir, "populate.zip");
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));            
                FileUtils.zip(new File (tmpCacheDir, "netigso"), zos, tmpCacheDir, new ArrayList <File> ());
                zos.close();
                LogManager.log("    .... success ");
            } catch (Exception ioe) {
                LogManager.log("    .... exception " + ioe.getMessage());
                return ;
            } finally {
                LogManager.log("    .... done. ");
            }
            
            
            // remove useless files
            LogManager.log("    remove useless files from cache");
            
            Progress removeUselessFileProgress = new Progress();
            compositeProgress.addChild(removeUselessFileProgress, 5);                       
            
            compositeProgress.setDetail((ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.cleaning"))); // NOI18N
            try {
                FileUtils.deleteFile(new File(tmpCacheDir, "netigso"), true, removeUselessFileProgress);
                FileUtils.deleteFile(new File(tmpCacheDir, "lastModified"), true, removeUselessFileProgress);
                FileUtils.deleteFile(new File(tmpCacheDir, "catalogcache"), true, removeUselessFileProgress);
                
                String[] splashFileNames = tmpCacheDir.list(new FilenameFilter() {

                                    @Override
                                    public boolean accept(File dir, String name) {
                                        return name.startsWith("splash"); // NOI18N
                                    }
                                });
                
                if (splashFileNames != null) {
                    for (String name : splashFileNames) {
                        FileUtils.deleteFile(new File(tmpCacheDir, name), removeUselessFileProgress);
                    }
                }
                LogManager.log("    .... success ");
            } catch (Exception ioe) {
                LogManager.log("    .... exception " + ioe.getMessage());
                return ;
            } finally {
                LogManager.log("    .... done. ");
            }
            
            
            // copy populate caches and delete temp files
            LogManager.log("copying NB log and populated caches : ");
            
            Progress populeteCacheDirProgress = new Progress();
            compositeProgress.addChild(populeteCacheDirProgress, 5);
            
            Progress deleteTempDirProgress = new Progress();
            compositeProgress.addChild(deleteTempDirProgress, 5);
                        
            File populateLogDir = new File(nbInstallLocation, "nb"/*nb cluster*/ + File.separator + "var" + File.separator + "log");
            File tmpMessagesLog = new File(tmpUserDir, "var" + File.separator + "log" + File.separator + "messages.log");
            LogManager.log("    NB log location = " + populateLogDir);
            try {                
                FileUtils.copyFile(tmpMessagesLog, new File(populateLogDir, "messages.log"), true, populeteCacheDirProgress);
                LogManager.log("    .... success ");
            } catch (Exception ioe) {
                LogManager.log("    .... exception " + ioe.getMessage());
                return ;
            } finally {
                LogManager.log("    .... done. ");
            }
            
            // adding log into list of installed files
            LogManager.log("add NB log in installed list: ");
            try {
                for (File f : populateLogDir.listFiles()) {
                    nbBase.getInstalledFiles().add(f);
                }
                nbBase.getInstalledFiles().add(populateLogDir);
                LogManager.log("    .... success ");
            } catch (Exception ioe) {
                LogManager.log("    .... exception " + ioe.getMessage());
            } finally {
                LogManager.log("    .... done. ");
            }
            
            File populateCacheDir = new File(nbInstallLocation, "nb"/*nb cluster*/ + File.separator + "var" + File.separator + "cache");
            LogManager.log("    populated cache location = " + populateCacheDir);
            try {                
                FileUtils.copyFile(tmpCacheDir, populateCacheDir, true, populeteCacheDirProgress);
                LogManager.log("    .... success ");
            } catch (Exception ioe) {
                LogManager.log("    .... exception " + ioe.getMessage());
                return ;
            } finally {
                LogManager.log("    .... done. ");
                try {                    
                    FileUtils.deleteFile(tmpUserDir, true, deleteTempDirProgress);
                    LogManager.log("    .... success ");
                } catch (Exception ioe) {
                    LogManager.log("    .... exception " + ioe.getMessage());
                }
            }
            
                        
            // adding files into list of installed files
            LogManager.log("add populated caches in installed list: ");
            try {
                for (File f : populateCacheDir.listFiles()) {
                    nbBase.getInstalledFiles().add(f);
                }
                nbBase.getInstalledFiles().add(populateCacheDir);
                LogManager.log("    .... success ");
            } catch (Exception ioe) {
                LogManager.log("    .... exception " + ioe.getMessage());
            } finally {
                LogManager.log("    .... done. ");
            }
            
            // save installed files list
            try {
                nbBase.getInstalledFiles().saveXmlGz(nbBase.getInstalledFilesList());
                LogManager.log("    .... success ");
            } catch (Exception xmle) {
                LogManager.log("    .... exception " + xmle.getMessage());
            }
        }        

        private ExecutionResults runIDE(List<String> commandsBase, File nbInstallLocation, boolean installJUnit, boolean checkForUpdate, CompositeProgress compositeProgress) throws IOException {
            List<String> commands = new ArrayList(commandsBase);

            String title = null;
            if (installJUnit) {
                // install JUnit
                if (! commands.contains("--modules")) {
                    commands.add("--modules");
                }
                commands.add("--install");
                commands.add("\".*junit.*\"");
                title = ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.InstallJUnit"); // NOI18N
                LogManager.log("    .... install JUnit");
            }
            if (checkForUpdate) {
                // check for updates
                if (! commands.contains("--modules")) {
                    commands.add("--modules");
                }
                commands.add("--update-all");
                title = title == null ?
                        ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.CheckForUpdate") : // NOI18N
                        ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.CheckForUpdateInstallJUnit"); // NOI18N
                LogManager.log("    .... check for updates");
            }
            if (title == null) {
                title = ResourceUtils.getString(NbMainSequence.class, "NBMS.CACHE.generate"); // NOI18N
            }
            LogManager.log("    Run " + commands);

            countdownProgress = new CountdownProgress(compositeProgress, 25*1000, STARTUP_STEP ,title);
            spendPercentage = spendPercentage + STARTUP_STEP;
            System.out.println("###(runIDE): spendPercentage INCREASED by " + STARTUP_STEP + " TO " + spendPercentage);
            loop = 0;
            sizeOfModules = 0;
            countdownProgress.countdown();
            try {
                return SystemUtils.executeCommand(compositeProgress, PREFIX_FOR_PROGRESS, nbInstallLocation, commands.toArray(new String[commands.size()]));
            } finally {
                countdownProgress.stop();
            }
        }
    }
    
    // a candidate to be placed somewhere in utils
    private static class CountdownProgress {

        private final CompositeProgress main;
        private final long time;
        private final int percentage;
        private final String title;
        private final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);
        private int current;
        private ScheduledFuture<?> ticTac;
        private Progress countdown;

        public CountdownProgress(CompositeProgress main, long time, int percentage, String title) {
            this.main = main;
            this.time = time;
            this.percentage = percentage;
            this.title = title;
            this.current = 0;
        }

        public void countdown() {
            countdown = new Progress();
            main.addChild(countdown, percentage);
            main.setDetail(title);
            current = 0;
            final Runnable tic = new Runnable() {

                @Override
                public void run() {
                    countdown.setPercentage(++current);
                }
            };
            ticTac = scheduler.scheduleAtFixedRate(tic, 0, time / 100, TimeUnit.MILLISECONDS);

            // schedule timout
            scheduler.schedule(new Runnable() {

                @Override
                public void run() {
                    countdown.setPercentage(Progress.COMPLETE);
                    ticTac.cancel(true);
                }
            }, time, TimeUnit.MILLISECONDS);

        }

        public void stop() {
            if (!ticTac.isDone() && !ticTac.isCancelled()) {
                countdown.setPercentage(Progress.COMPLETE);
                ticTac.cancel(true);
            }
        }

        private void detach() {
            if (countdown != null) {
                main.removeChild(countdown);
                ticTac.cancel(true);
                countdown = null;
            }
        }
    }

    @Override
    public void executeForward() {
        final Registry registry = Registry.getInstance();
        final List<Product> toInstall = registry.getProductsToInstall();
        final List<Product> toUninstall = registry.getProductsToUninstall();

        // remove all current children (if there are any), as the components
        // selection has probably changed and we need to rebuild from scratch
        getChildren().clear();

        // the set of wizard components differs greatly depending on the execution
        // mode - if we're installing, we ask for input, run a wizard sequence for
        // each selected component and then download and install; if we're creating
        // a bundle, we only need to download and package things

        if (toInstall.size() > 0) {
            addChild(downloadConfigurationLogicAction);
            addChild(licensesPanel);
            addChild(nbJUnitLicensePanel);

            for (Product product : toInstall) {
                if (!productSequences.containsKey(product)) {
                    productSequences.put(
                            product,
                            new ProductWizardSequence(product));
                }

                addChild(productSequences.get(product));
            }
        }

        addChild(nbPreInstallSummaryPanel);

        if (toUninstall.size() > 0) {
            addChild(uninstallAction);
        }

        if (toInstall.size() > 0) {
            addChild(downloadInstallationDataAction);
            addChild(installAction);
            Product nbBase = null;
            for (Product p : toInstall) {
                if ("nb-base".equals(p.getUid())) { // NOI18N
                    nbBase = p;
                    break;
                }
            }
            Product nbJavaSE = null;
            for (Product p : toInstall) {
                if ("nb-javase".equals(p.getUid())) {
                    nbJavaSE = p;
                    break;
                }
            }
            if (nbBase != null) {
                PopulateCacheAction pupolateCacheAction = new PopulateCacheAction(nbBase, nbJavaSE);
                addChild(pupolateCacheAction);
                pupolateCacheAction.setProperty(InstallAction.TITLE_PROPERTY, DEFAULT_IA_TITLE);
                pupolateCacheAction.setProperty(InstallAction.DESCRIPTION_PROPERTY, DEFAULT_IA_DESCRIPTION);
                
            }
        }

        addChild(nbPostInstallSummaryPanel);
        if (toInstall.size() > 0) {
            addChild(metricsAction);
        }
        if (toUninstall.size() > 0) {
            addChild(showUninstallationSurveyAction);
        }

        StringBuilder list = new StringBuilder();
        for (Product product : toInstall) {
            list.append(product.getUid() + "," + product.getVersion() + ";");
        }
        System.setProperty(
                LIST_OF_PRODUCTS_TO_INSTALL_PROPERTY,
                list.toString());

        list = new StringBuilder();
        for (Product product : toUninstall) {
            list.append(product.getUid() + "," + product.getVersion() + ";");
        }
        System.setProperty(
                LIST_OF_PRODUCTS_TO_UNINSTALL_PROPERTY,
                list.toString());

        list = new StringBuilder();
        for (Product product : toInstall) {
            for (WizardComponent component : productSequences.get(product).getChildren()) {
                list.append(component.getClass().getName() + ";");
            }
        }
        System.setProperty(
                PRODUCTS_PANEL_FLOW_PROPERTY,
                list.toString());

        super.executeForward();
    }

    @Override
    public boolean canExecuteForward() {
        return ExecutionMode.NORMAL == ExecutionMode.getCurrentExecutionMode();
    }
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    public static final String DEFAULT_IA_TITLE =
            ResourceUtils.getString(
            NbMainSequence.class,
            "NBMS.IA.title"); // NOI18N
    public static final String DEFAULT_IA_DESCRIPTION =
            ResourceUtils.getString(
            NbMainSequence.class,
            "NBMS.IA.description"); // NOI18N
    
    public static final String LIST_OF_PRODUCTS_TO_INSTALL_PROPERTY =
            "nbi.products.to.install"; // NOI18N
    public static final String LIST_OF_PRODUCTS_TO_UNINSTALL_PROPERTY =
            "nbi.products.to.uninstall"; // NOI18N
    public static final String PRODUCTS_PANEL_FLOW_PROPERTY =
            "nbi.products.panel.flow"; // NOI18N
}
