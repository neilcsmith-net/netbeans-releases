/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.apigen.commands;

import java.io.File;
import java.net.MalformedURLException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpProgram;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.apigen.options.ApiGenOptions;
import org.netbeans.modules.php.apigen.ui.ApiGenPreferences;
import org.netbeans.modules.php.apigen.ui.options.ApiGenOptionsPanelController;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 * Represents <a href="http://apigen.org/">apigen</a> command line tool.
 */
public final class ApiGenScript extends PhpProgram {

    public static final String SCRIPT_NAME = "apigen"; // NOI18N
    public static final String SCRIPT_NAME_LONG = SCRIPT_NAME + FileUtils.getScriptExtension(true);


    private ApiGenScript(String command) {
        super(command);
    }

    /**
     * Get the default, <b>valid only</b> ApiGen script.
     * @return the default, <b>valid only</b> ApiGen script.
     * @throws InvalidPhpProgramException if ApiGen script is not valid.
     */
    public static ApiGenScript getDefault() throws InvalidPhpProgramException {
        String apiGen = ApiGenOptions.getInstance().getApiGen();
        String error = validate(apiGen);
        if (error != null) {
            throw new InvalidPhpProgramException(error);
        }
        return new ApiGenScript(apiGen);
    }

    public static String validate(String command) {
        return new ApiGenScript(command).validate();
    }

    @NbBundle.Messages("ApiGenScript.prefix=ApiGen script: {0}")
    @Override
    public String validate() {
        String error = FileUtils.validateFile(getProgram(), false);
        if (error == null) {
            return null;
        }
        return Bundle.ApiGenScript_prefix(error);
    }

    @NbBundle.Messages("ApiGenScript.api.generating=Generating API for {0}")
    public void generateDocumentation(final PhpModule phpModule) {
        String target = ApiGenPreferences.getTarget(phpModule, true);
        if (target == null) {
            // canceled
            return;
        }

        ExternalProcessBuilder processBuilder = getProcessBuilder()
                // from
                .addArgument("-s") // NOI18N
                .addArgument(FileUtil.toFile(phpModule.getSourceDirectory()).getAbsolutePath())
                // to
                .addArgument("-d") // NOI18N
                .addArgument(target)
                // title
                .addArgument("-title") // NOI18N
                .addArgument(ApiGenPreferences.getTitle(phpModule));
        ExecutionDescriptor executionDescriptor = getExecutionDescriptor()
                .frontWindow(false)
                .optionsPath(ApiGenOptionsPanelController.getOptionsPath());

        try {
            int status = executeAndWait(
                    processBuilder,
                    executionDescriptor,
                    Bundle.ApiGenScript_api_generating(phpModule.getDisplayName()));
            if (status == 0) {
                File index = new File(target, "index.html"); // NOI18N
                if (index.isFile()) {
                    // false for pdf e.g.
                    HtmlBrowser.URLDisplayer.getDefault().showURL(index.toURI().toURL());
                }
            }
        } catch (CancellationException ex) {
            // canceled
        } catch (ExecutionException ex) {
            UiUtils.processExecutionException(ex, ApiGenOptionsPanelController.OPTIONS_SUBPATH);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
        }
    }

}
