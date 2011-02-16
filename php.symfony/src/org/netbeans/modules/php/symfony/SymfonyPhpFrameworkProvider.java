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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.symfony;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.php.api.phpmodule.BadgeIcon;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleIgnoredFilesExtender;
import org.netbeans.modules.php.symfony.commands.SymfonyCommandSupport;
import org.netbeans.modules.php.symfony.editor.SymfonyEditorExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * @author Tomas Mysik
 */
public final class SymfonyPhpFrameworkProvider extends PhpFrameworkProvider {
    public static final String FILE_CONFIG = "config/ProjectConfiguration.class.php"; // NOI18N

    private static final String ICON_PATH = "org/netbeans/modules/php/symfony/ui/resources/symfony_badge_8.png"; // NOI18N
    private static final SymfonyPhpFrameworkProvider INSTANCE = new SymfonyPhpFrameworkProvider();
    private static final Set<String> CONFIG_FILE_EXTENSIONS = new HashSet<String>();

    private final BadgeIcon badgeIcon;

    static {
        CONFIG_FILE_EXTENSIONS.add("ini"); // NOI18N
        CONFIG_FILE_EXTENSIONS.add("yml"); // NOI18N
    }

    @PhpFrameworkProvider.Registration(position=100)
    public static SymfonyPhpFrameworkProvider getInstance() {
        return INSTANCE;
    }

    private SymfonyPhpFrameworkProvider() {
        super(NbBundle.getMessage(SymfonyPhpFrameworkProvider.class, "LBL_FrameworkName"), NbBundle.getMessage(SymfonyPhpFrameworkProvider.class, "LBL_FrameworkDescription"));
        badgeIcon = new BadgeIcon(
                ImageUtilities.loadImage(ICON_PATH),
                SymfonyPhpFrameworkProvider.class.getResource("/" + ICON_PATH)); // NOI18N
    }

    @Override
    public BadgeIcon getBadgeIcon() {
        return badgeIcon;
    }

    /**
     * Try to locate (find) a <code>relativePath</code> in source directory.
     * Currently, it searches source dir and its subdirs (if <code>subdirs</code> equals {@code true}).
     * @return {@link FileObject} or {@code null} if not found
     */
    public static FileObject locate(PhpModule phpModule, String relativePath, boolean subdirs) {
        FileObject sourceDirectory = phpModule.getSourceDirectory();

        FileObject fileObject = sourceDirectory.getFileObject(relativePath);
        if (fileObject != null || !subdirs) {
            return fileObject;
        }
        for (FileObject child : sourceDirectory.getChildren()) {
            fileObject = child.getFileObject(relativePath);
            if (fileObject != null) {
                return fileObject;
            }
        }
        return null;
    }

    @Override
    public boolean isInPhpModule(PhpModule phpModule) {
        // #170775
        // first search "symfony", then "config/ProjectConfiguration.class.php"
        FileObject symfony = locate(phpModule, SymfonyScript.SCRIPT_NAME, false);
        if (symfony != null && symfony.isData()) {
            return true;
        }
        FileObject config = locate(phpModule, FILE_CONFIG, true);
        return config != null && config.isData();
    }

    @Override
    public File[] getConfigurationFiles(PhpModule phpModule) {
        List<File> files = new LinkedList<File>();
        FileObject appConfig = phpModule.getSourceDirectory().getFileObject("config"); // NOI18N
        if (appConfig != null) {
            List<FileObject> fileObjects = new LinkedList<FileObject>();
            Enumeration<? extends FileObject> children = appConfig.getChildren(true);
            while (children.hasMoreElements()) {
                FileObject child = children.nextElement();
                if (child.isData()
                        && (CONFIG_FILE_EXTENSIONS.contains(child.getExt().toLowerCase()) || FileUtils.isPhpFile(child))
                        && !child.getExt().equals("svn-base")) { // NOI18N
                    fileObjects.add(child);
                }
            }
            Collections.sort(fileObjects, new Comparator<FileObject>() {
                @Override
                public int compare(FileObject o1, FileObject o2) {
                    // php files go last
                    boolean phpFile1 = FileUtils.isPhpFile(o1);
                    boolean phpFile2 = FileUtils.isPhpFile(o2);
                    if (phpFile1 && phpFile2) {
                        return o1.getNameExt().compareTo(o2.getNameExt());
                    } else if (phpFile1) {
                        return 1;
                    } else if (phpFile2) {
                        return -1;
                    }

                    // compare extensions, then full names
                    String ext1 = o1.getExt();
                    String ext2 = o2.getExt();
                    if (ext1.equals(ext2)) {
                        return o1.getNameExt().compareToIgnoreCase(o2.getNameExt());
                    }
                    return ext1.compareToIgnoreCase(ext2);
                }
            });

            for (FileObject fo : fileObjects) {
                files.add(FileUtil.toFile(fo));
            }
        }
        return files.toArray(new File[files.size()]);
    }

    @Override
    public PhpModuleExtender createPhpModuleExtender(PhpModule phpModule) {
        return new SymfonyPhpModuleExtender();
    }

    @Override
    public PhpModuleCustomizerExtender createPhpModuleCustomizerExtender(PhpModule phpModule) {
        return new SymfonyPhpModuleCustomizerExtender(phpModule);
    }

    @Override
    public PhpModuleProperties getPhpModuleProperties(PhpModule phpModule) {
        PhpModuleProperties properties = new PhpModuleProperties();
        FileObject web = locate(phpModule, "web", true); // NOI18N
        if (web != null) {
            properties = properties.setWebRoot(web);
        }
        FileObject testUnit = locate(phpModule, "test/unit", true); // NOI18N
        if (testUnit != null) {
            properties = properties.setTests(testUnit);
        }
        return properties;
    }

    @Override
    public PhpModuleActionsExtender getActionsExtender(PhpModule phpModule) {
        return new SymfonyPhpModuleActionsExtender();
    }

    @Override
    public PhpModuleIgnoredFilesExtender getIgnoredFilesExtender(PhpModule phpModule) {
        return new SymfonyPhpModuleIgnoredFilesExtender(phpModule);
    }

    @Override
    public SymfonyCommandSupport getFrameworkCommandSupport(PhpModule phpModule) {
        return new SymfonyCommandSupport(phpModule);
    }

    @Override
    public EditorExtender getEditorExtender(PhpModule phpModule) {
        return new SymfonyEditorExtender();
    }
}
