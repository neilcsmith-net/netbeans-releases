/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
/*
 * AppserverMgmtControllerTest.java
 * JUnit based test
 *
 * Created on January 28, 2005, 2:07 PM
 */

package org.netbeans.modules.j2ee.sun.util;

import com.sun.appserv.management.config.StandaloneServerConfig;
import junit.framework.*;
import java.io.IOException;
import com.sun.appserv.management.DomainRoot;

/**
 *
 * @author Rob
 */
public class AppserverMgmtControllerTest extends TestCase {
    
    private AppserverMgmtController controller;
    
    public AppserverMgmtControllerTest(String testName) {
        super(testName);
    }
    
    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = 
            new junit.framework.TestSuite(AppserverMgmtControllerTest.class);
        
        return suite;
    }

    /**
     * Test of getDomainRoot method, of class 
     * org.netbeans.modules.j2ee.sun.util.AppserverMgmtController.
     */
    public void testGetDomainRoot() {
        DomainRoot root = controller.getDomainRoot();
        assertNotNull(root);
        assertEquals(root.getAppserverDomainName(), "amx");
    }

    
    public void testGetStandaloneServerInstances() {
        String[] serverNames = controller.getStandaloneServerInstances();
        for(int i = 0; i < serverNames.length; i++) {
            System.out.println("Instance [" + i + "]: " + serverNames[i]);
        }
    }
    
    public void testGetServerMgmtController() {
        ServerMgmtController serverController = 
            controller.getServerMgmtController("server");
        assertNotNull(serverController);
    }
    
    public void testGetStandaloneServerConfigByName() {
        StandaloneServerConfig config = 
            controller.getStandaloneServerConfigByName("server");
        System.out.println(config.getFullType());
    }
    
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(suite());
    }
    

    protected void setUp() {
        try {
            controller = new AppserverMgmtController(
                AppserverConnectionFactory.getAppserverConnection("localhost",
                    4848, "admin", "adminadmin", null, false));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
