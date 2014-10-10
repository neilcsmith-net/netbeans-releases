/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.makeproject.ui.wizards;

import javax.swing.filechooser.FileFilter;
import org.netbeans.modules.cnd.api.remote.ServerList;
import org.netbeans.modules.cnd.api.toolchain.CompilerFlavor;
import org.netbeans.modules.cnd.api.toolchain.CompilerSet;
import org.netbeans.modules.cnd.api.toolchain.CompilerSetManager;
import org.netbeans.modules.cnd.execution.ShellExecSupport;
import org.netbeans.modules.cnd.makeproject.api.wizards.PreBuildSupport;
import org.netbeans.modules.cnd.makeproject.api.wizards.PreBuildSupport.PreBuildArtifact;
import org.netbeans.modules.cnd.makeproject.api.wizards.PreBuildSupport.PreBuildArtifactProvider;
import org.netbeans.modules.cnd.utils.CndPathUtilities;
import org.netbeans.modules.cnd.utils.FileFilterFactory;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.remote.spi.FileSystemProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Alexander Simon
 */
@ServiceProvider(service = PreBuildArtifactProvider.class, position = 1000)
public final class ConfigureScriptSupport implements PreBuildArtifactProvider {
    private static final String PREDEFINED_FLAGS_GNU = "\"-g3 -gdwarf-2\""; // NOI18N
    private static final String PREDEFINED_FLAGS_SUN = "-g"; // NOI18N
    private static final String pattern[] = new String[]{"configure"}; // NOI18N

    @Override
    public PreBuildArtifact findScriptInFolder(FileObject folder, ExecutionEnvironment ee, CompilerSet compilerSet) {
        if (folder == null) {
            return null;
        }
        if (!(folder.isFolder() && (folder.canRead()))) {
            return null;
        }
        for (String name : pattern) {
            final FileObject child = folder.getFileObject(name); // NOI18N
            if (child == null || !child.isValid()) {
                continue;
            }
            ConfigureScriptArtifact res = new ConfigureScriptArtifact(child);
            if (res.validate(null, null) == null) {
                return res;
            }
        }
        return null;
    }

    @Override
    public PreBuildArtifact scriptToArtifact(FileObject script) {
        for (String name : pattern) {
            if (name.equals(script.getNameExt())) {
                return new ConfigureScriptArtifact(script);
            }
        }
        return null;
    }
    
    @Override
    public String getToolName() {
        return "configure"; //NOI18N
    }

    @Override
    public boolean isSupported(PreBuildArtifact script) {
        return script instanceof ConfigureScriptArtifact;
    }

    @Override
    public String getDisplayName() {
         return NbBundle.getMessage(ConfigureScriptSupport.class, "ScriptTypeDisplayName_configure"); //NOI18N
    }

    @Override
    public String getHint() {
        return NbBundle.getMessage(ConfigureScriptSupport.class, "SelectModeSimpleInstructionExtraText_Configure"); // NOI18N
    }

    @Override
    public String getFileChooserTitle() {
        return NbBundle.getMessage(ConfigureScriptSupport.class, "ScriptTypeFileChooser_configure"); //NOI18N
    }

    @Override
    public FileFilter[] getFileFilter() {
        return new FileFilter[]{FileFilterFactory.getConfigureFileFilter()};
    }

    static String getCompilerFlags(CompilerSet def){
        if (def != null) {
            CompilerFlavor flavor = def.getCompilerFlavor();
            if (flavor.isSunStudioCompiler()) {
                return PREDEFINED_FLAGS_SUN;
            }
        }
        return PREDEFINED_FLAGS_GNU;
    }
    
    static void appendIfNeed(String key, String flags, StringBuilder buf, String flag){
        if (!flags.contains(key) ){
            if (buf.length() > 0) {
                buf.append(' '); // NOI18N
            }
            buf.append(key).append(flag);
        }
    }

    private static final class ConfigureScriptArtifact implements PreBuildArtifact {
        private final FileObject script;
        
        ConfigureScriptArtifact(FileObject script) {
            this.script = script;
        }

        @Override
        public FileObject getScript() {
            return script;
        }

        @Override
        public String getArguments(ExecutionEnvironment ee, CompilerSet def, String flags) {
            ee = (ee != null) ? ee : ServerList.getDefaultRecord().getExecutionEnvironment();
            def = (def != null) ? def : CompilerSetManager.get(ee).getDefaultCompilerSet();
            StringBuilder buf = new StringBuilder(flags);
            String cCompilerFlags = ConfigureScriptSupport.getCompilerFlags(def);
            String cppCompilerFlags = ConfigureScriptSupport.getCompilerFlags(def);
            ConfigureScriptSupport.appendIfNeed("CC=", flags, buf, PreBuildSupport.C_COMPILER_MACRO); // NOI18N
            ConfigureScriptSupport.appendIfNeed("CXX=", flags, buf, PreBuildSupport.CPP_COMPILER_MACRO); // NOI18N
            ConfigureScriptSupport.appendIfNeed("CFLAGS=", flags, buf, cCompilerFlags); // NOI18N
            ConfigureScriptSupport.appendIfNeed("CXXFLAGS=", flags, buf, cppCompilerFlags); // NOI18N
            return buf.toString();
        }

        @Override
        public String getCommandLine(String arguments, String workingDir) {
            StringBuilder buf = new StringBuilder();
            String toRelativePath = CndPathUtilities.toRelativePath(workingDir, script.getPath());
            if (toRelativePath.equals(script.getNameExt())) {
                buf.append("./"); //NOI18N
            }
            buf.append(toRelativePath);
            buf.append(' ');
            buf.append(arguments);
            return buf.toString();
        }

        @Override
        public String validate(ExecutionEnvironment ee, CompilerSet compilerSet) {
            if (script.isValid() && script.isData() &&
               (script.canRead() || FileSystemProvider.canExecute(script))) {
                DataObject dObj;
                try {
                    dObj = DataObject.find(script);
                } catch (DataObjectNotFoundException ex) {
                    return NbBundle.getMessage(ConfigureScriptSupport.class, "CONFIGUREFILEISNOTEXECUTABLE"); //NOI18N
                }
                if (dObj == null) {
                    return NbBundle.getMessage(ConfigureScriptSupport.class, "CONFIGUREFILEISNOTEXECUTABLE"); //NOI18N
                }
                Node node = dObj.getNodeDelegate();
                if (node == null) {
                    return NbBundle.getMessage(ConfigureScriptSupport.class, "CONFIGUREFILEISNOTEXECUTABLE"); //NOI18N
                }
                ShellExecSupport ses = node.getLookup().lookup(ShellExecSupport.class);
                if (ses == null) {
                    return NbBundle.getMessage(ConfigureScriptSupport.class, "CONFIGUREFILEISNOTEXECUTABLE"); //NOI18N
                }
                return null;
            }
            return NbBundle.getMessage(ConfigureScriptSupport.class, "CONFIGUREFILEISNOTEXECUTABLE"); //NOI18N
        }
    }
}
