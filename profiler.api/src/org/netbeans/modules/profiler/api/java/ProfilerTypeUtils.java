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
package org.netbeans.modules.profiler.api.java;

import java.util.Collection;
import org.netbeans.modules.profiler.spi.java.ProfilerTypeUtilsProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 * Java types related profiler utility methods
 * 
 * @author Jaroslav Bachorik
 */
final public class ProfilerTypeUtils {
    final private Lookup.Provider project;
    ProfilerTypeUtils(Lookup.Provider project) {
        this.project = project;
    }
    
    private ProfilerTypeUtilsProvider getProvider() {
        return Lookup.getDefault().lookup(ProfilerTypeUtilsProvider.class);
    }
    
    /**
     * Retrieves a list of subclasses for the given class name
     * @param className The class name to check for subclasses
     * @return Returns an array of subclasses for the class specified by the class name
     */
    public String[] getSubclasses(String className) {
        ProfilerTypeUtilsProvider typeUtils = getProvider();
        assert typeUtils != null;
        
        return typeUtils.getSubclasses(className, project);
    }
    
    /**
     * Finds the defining file for the given class name
     * @param className The class name to get the defining file for
     * @return Returns the defining file for the given class name or NULL
     */
    public FileObject findFile(String className) {
        ProfilerTypeUtilsProvider typeUtils = getProvider();
        assert typeUtils != null;
        
        return typeUtils.findFile(className, project);
    }
    
    /**
     * 
     * @return Returns a list of all main classes present in the project
     */
    public Collection<String> getMainClasses() {
        ProfilerTypeUtilsProvider typeUtils = getProvider();
        assert typeUtils  != null;
        
        return typeUtils.getMainClasses(project);
    }
}
