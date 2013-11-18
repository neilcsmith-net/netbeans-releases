/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008-2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.bugtracking.spi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.netbeans.modules.bugtracking.api.Repository;

/**
 * Represents a bugtracking connector.
 * <p>
 * Bugtracking system registration can be done via {@link Registration}. 
 * </p>
 *
 * <pre>
 *  {@literal @}BugtrackingConnector.Registration (
 *      id = "foo.bar.MyConnector",
 *      displayName ="My Connector",
 *      tooltip = "This is My Connector")    
 *   public class MyConnector extends BugtrackingConnector {
 *  
 *    ... 
 * 
 *   }
 * </pre>
 * 
 * @author Tomas Stupka
 * @since 1.85
 */
public interface BugtrackingConnector {

    /**
     * Creates a {@link Repository} instance.
     * 
     * @param info repository information based on which the repository should be created
     * 
     * @return a {@link Repository} instance.
     * @see BugtrackingSupport
     * @since 1.85
     */
    public Repository createRepository(RepositoryInfo info);  
    
    /**
     * Creates a new repository instance. 
     * 
     * @return the created repository
     * @see BugtrackingSupport
     * @since 1.85
     */
    public Repository createRepository();

    /**
     * Register a BugtrackingConnector in the IDE.
     * 
     * @author Tomas Stupka
     * @see org.openide.util.lookup.ServiceProvider
     * @since 1.85
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.TYPE, ElementType.METHOD})
    public @interface Registration {    
        
        /**
         * Returns a unique ID for this connector
         *
         * @return id
         * @since 1.85
         */
        public String id();

        /**
         * Returns the icon path for this connector
         *
         * @return the icon path
         * @since 1.85
         */
        public String iconPath() default "";

        /**
         * Returns the display name for this connector
         *
         * @return the display name for this connector
         * @since 1.85
         */
        public String displayName();

        /**
         * Returns tooltip for this connector
         *
         * @return tooltip for this connector
         * @since 1.85
         */
        public String tooltip();    
        
        /**
         * Determines if this connector provides the possibility for a user 
         * to create, edit or removal of repositories.
         * <p>
         * Typically the expected value for a connector is to return <code>true</code>. 
         * </p>
         * @return <code>true</code> if this connector provides the possibility 
         *         to create, edit or removal of repositories. Otherwise <code>false</code>.
         * @since 1.85
         */
        public boolean providesRepositoryManagement() default true;
        
    }    
}
