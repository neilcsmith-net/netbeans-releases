/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.makeproject.api.wizards;

import java.util.List;
import org.netbeans.modules.cnd.api.toolchain.CompilerSet;
import org.netbeans.modules.cnd.makeproject.ui.wizards.MakeSampleProjectIterator;
import org.netbeans.modules.cnd.makeproject.ui.wizards.NewMakeProjectWizardIterator;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Alexander Simon
 */
public final class ProjectWizardPanels {

    private ProjectWizardPanels() {
    }
    
    public static List<WizardDescriptor.Panel<WizardDescriptor>> getNewProjectWizardPanels(int wizardtype, String name, String wizardTitle, String wizardACSD, boolean fullRemote) {
        return NewMakeProjectWizardIterator.getPanels(wizardtype, name, wizardTitle, wizardACSD, fullRemote);
    }

    public static MakeSamplePanel<WizardDescriptor> getMakeSampleProjectWizardPanel(int wizardtype, String name, String wizardTitle, String wizardACSD, boolean fullRemote) {
        return MakeSampleProjectIterator.getPanel(wizardtype, name, wizardTitle, wizardACSD, fullRemote);
    }
    
    public static MakeModePanel<WizardDescriptor> getSelectModePanel() {
        return  NewMakeProjectWizardIterator.createSelectModePanel();
    }

    public static WizardDescriptor.Panel<WizardDescriptor> getSelectBinaryPanel() {
        return  NewMakeProjectWizardIterator.getSelectBinaryPanel();
    }

    public interface MakeSamplePanel<T> extends WizardDescriptor.FinishablePanel<T> {
        void setFinishPanel(boolean isFinishPanel);
    }

    public interface MakeModePanel<T> extends MakeSamplePanel<T> {
        WizardStorage getWizardStorage();
    }

    public interface NamedPanel {
        String getName();
    }

    public interface WizardStorage {
        
        WizardDescriptor getAdapter();
        
        void setMode(boolean isSimple);

        String getProjectPath();

        FileObject getSourcesFileObject();

        void setProjectPath(String path);

        void setSourcesFileObject(FileObject fileObject);

        String getConfigure();

        String getMake();

        void setMake(FileObject makefileFO);

        String getFlags();

        String getRealFlags();

        void setFlags(String flags);

        boolean isSetMain();

        void setSetMain(boolean setMain);

        boolean isBuildProject();

        void setBuildProject(boolean buildProject);

        void setCompilerSet(CompilerSet cs);

        CompilerSet getCompilerSet();

        void setExecutionEnvironment(ExecutionEnvironment ee);

        ExecutionEnvironment getExecutionEnvironment();

        void setSourceExecutionEnvironment(ExecutionEnvironment sourceEnv);

        ExecutionEnvironment getSourceExecutionEnvironment();

        void setDefaultCompilerSet(boolean defaultCompilerSet);

        boolean isDefaultCompilerSet();
    }
}
