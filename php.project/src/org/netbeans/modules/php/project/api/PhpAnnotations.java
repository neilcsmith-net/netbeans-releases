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
package org.netbeans.modules.php.project.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.project.PhpProject;
import org.netbeans.modules.php.project.ProjectPropertiesSupport;
import org.netbeans.modules.php.spi.annotations.PhpAnnotationsProvider;
import org.netbeans.modules.php.spi.phpmodule.PhpFrameworkProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Parameters;

/**
 * Helper class to get PHP annotations.
 * @since 2.46
 */
public final class PhpAnnotations implements PropertyChangeListener {

    private static final PhpAnnotations INSTANCE = new PhpAnnotations();

    // @GuardedBy(this)
    private final Map<FileObject, List<PhpAnnotationsProvider>> cache = new WeakHashMap<FileObject, List<PhpAnnotationsProvider>>();


    private PhpAnnotations() {
    }

    /**
     * Get PHP annotations.
     * @return {@link PhpAnnotations} instance
     */
    public static PhpAnnotations getDefault() {
        return INSTANCE;
    }

    /**
     * Get PHP annotations providers for the given file.
     * @param fileObject file to get annotations for
     * @return PHP annotations providers
     */
    public synchronized List<PhpAnnotationsProvider> getProviders(FileObject fileObject) {
        Parameters.notNull("fileObject", fileObject);

        List<PhpAnnotationsProvider> providers = cache.get(fileObject);
        if (providers != null) {
            return providers;
        }
        cache.clear();
        providers = computeProviders(fileObject);
        cache.put(fileObject, providers);
        return providers;
    }

    private List<PhpAnnotationsProvider> computeProviders(FileObject fileObject) {
        assert Thread.holdsLock(this);

        List<PhpAnnotationsProvider> result = new ArrayList<PhpAnnotationsProvider>();
        // first, add global providers
        result.addAll(org.netbeans.modules.php.api.annotations.PhpAnnotations.getProviders());
        // next, add providers from php frameworks
        PhpProject phpProject = org.netbeans.modules.php.project.util.PhpProjectUtils.getPhpProject(fileObject);
        if (phpProject != null) {
            ProjectPropertiesSupport.addWeakProjectPropertyChangeListener(phpProject, this);
            final PhpModule phpModule = phpProject.getPhpModule();
            for (PhpFrameworkProvider provider : phpProject.getFrameworks()) {
                result.addAll(provider.getAnnotationsProviders(phpModule));
            }
        }
        return result;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PhpProject.PROP_FRAMEWORKS.equals(evt.getPropertyName())) {
            synchronized (this) {
                cache.clear();
            }
        }
    }

}
