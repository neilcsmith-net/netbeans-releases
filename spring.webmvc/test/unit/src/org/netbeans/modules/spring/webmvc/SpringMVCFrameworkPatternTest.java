/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.spring.webmvc;

import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.web.api.webmodule.ExtenderController;

/**
 *
 * @author John Baker
 */
public class SpringMVCFrameworkPatternTest extends NbTestCase {

    private SpringWebModuleExtender extender;
    
    public SpringMVCFrameworkPatternTest(String testName) {
        super(testName);
    }

    public void testDispatcherNameEntry_NonWordCharacterPattern() throws Exception {
        extender = new SpringWebModuleExtender(null, ExtenderController.create(), false, "Dis-patcher", "*.htm");
        assertName(extender.getDispatcherName(), false);
    }
    
    public void testDispatcherNameEntry_EmptyWordCharacterPattern() throws Exception {
        extender = new SpringWebModuleExtender(null, ExtenderController.create(), false, "", "*.htm");
        assertName(extender.getDispatcherName(), false);
    }
            
    public void testDispatcherMappingEntry_ExtensionSpacePattern() throws Exception {
        extender = new SpringWebModuleExtender(null, ExtenderController.create(), false, "Dispatcher", "*.h tm");
        assertMapping(extender.getDispatcherMapping(), false);
    }
    
    public void testDispatcherMappingEntry_ExtensionNonWordPattern() throws Exception {
        extender = new SpringWebModuleExtender(null, ExtenderController.create(), false, "Dispatcher", "*.h&tm");
        assertMapping(extender.getDispatcherMapping(), false);
    }
    
    public void testDispatcherMappingEntry_ServletSpacePattern() throws Exception {
        extender = new SpringWebModuleExtender(null, ExtenderController.create(), false, "Dispatcher", "/a /*");
        assertMapping(extender.getDispatcherMapping(), false);
    }
    
    public void testDispatcherMappingEntry_PathSpacePattern() throws Exception {
        extender = new SpringWebModuleExtender(null, ExtenderController.create(), false, "Dispatcher", " /");
        assertMapping(extender.getDispatcherMapping(), false);
    }
    
    public void testDispatcherMappingEntry_InvalidExtensionPattern() throws Exception {
        extender = new SpringWebModuleExtender(null, ExtenderController.create(), false, "Dispatcher", "*.");
        assertMapping(extender.getDispatcherMapping(), false);
    }       
     
    public void testDispatcherMappingEntry_InvalidPathPattern() throws Exception {
        extender = new SpringWebModuleExtender(null, ExtenderController.create(), false, "Dispatcher", "/a");
        assertMapping(extender.getDispatcherMapping(), false);
    }
    
    public void testDispatcherMappingEntry_ValidPathPattern() throws Exception {
        extender = new SpringWebModuleExtender(null, ExtenderController.create(), false, "Dispatcher", "/");
        assertMapping(extender.getDispatcherMapping(), true);
    }
    
    public void testDispatcherMappingEntry_InvalidDefaultServletPattern() throws Exception {
        extender = new SpringWebModuleExtender(null, ExtenderController.create(), false, "Dispatcher", "/a*/");
        assertMapping(extender.getDispatcherMapping(), false);
    }
    
    public void testDispatcherMappingEntry_ValidDefaultServletPattern() throws Exception {
        extender = new SpringWebModuleExtender(null, ExtenderController.create(), false, "Dispatcher", "/app/*");
        assertMapping(extender.getDispatcherMapping(), true);
    }
    
    public void testDispatcherMappingAndNameEntry_InvalidPattern() throws Exception {
        extender = new SpringWebModuleExtender(null, ExtenderController.create(), false, "Dispa tcher", "*.h tm");
        assertMapping(extender.getDispatcherMapping(), false);
    }
    
    public void testDispatcherMappingAndNameEntry_ValidPattern() throws Exception {
        extender = new SpringWebModuleExtender(null, ExtenderController.create(), false, "Dispatcher", "*.htm");
        assertMapping(extender.getDispatcherMapping(), true);
    }
    
    private void assertMapping(String mapping, boolean valid) throws Exception {
        assertEquals(extender.isValid(), valid);
        
    }
    
    private void assertName(String name, boolean valid) throws Exception {
        assertEquals(extender.isValid(), valid);
    }


}
