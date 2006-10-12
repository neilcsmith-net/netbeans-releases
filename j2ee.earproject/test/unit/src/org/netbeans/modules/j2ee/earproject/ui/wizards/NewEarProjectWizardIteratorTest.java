/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.earproject.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.j2ee.earproject.EarProject;
import org.netbeans.modules.j2ee.earproject.EarProjectTest;
import org.netbeans.modules.j2ee.earproject.TestPlatformProvider;
import org.netbeans.modules.j2ee.earproject.test.TestUtil;
import org.netbeans.modules.j2ee.earproject.ui.customizer.EarProjectProperties;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;

/**
 * @author Martin Krauskopf
 */
public class NewEarProjectWizardIteratorTest extends NbTestCase {
    
    private static final String DEFAULT_PLATFORM_ROOT = "1.5";
    
    private String serverInstanceID;
    
    /**
     * Generates a project in the same (or very similar) manner as would be
     * generated by the user using <em>New Enterprise Application</em> wizard.
     * May be used for generating project instances in tests.
     */
    public static void generateEARProject(
            File prjDirF, String name, String j2eeLevel,
            String serverInstanceID, String warName,
            String jarName, String carName, String mainClass,
            String platformName, String sourceLevel) throws IOException {
        NewEarProjectWizardIterator.testableInstantiate(prjDirF, name,
                j2eeLevel, serverInstanceID, warName, jarName,
                carName, mainClass, platformName, sourceLevel);
    }
    
    /**
     * Generates an empty Enterprise Application project, i.e. without
     * submodules.
     *
     * @see #generateEARProject(File, String, String, String, String, String, String, String, String, String)
     */
    public static void generateEARProject(File earDirF, String name,
            String j2eeLevel, String serverID) throws IOException {
        generateEARProject(earDirF, name, j2eeLevel, serverID, null, null, null,
                null, null, null);
    }
    
    public NewEarProjectWizardIteratorTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        clearWorkDir();
        FileObject scratch = TestUtil.makeScratchDir(this);
        FileObject defaultPlatformBootRoot = scratch.createFolder(DEFAULT_PLATFORM_ROOT);
        ClassPath defBCP = ClassPathSupport.createClassPath(new URL[] { defaultPlatformBootRoot.getURL() });
        
        serverInstanceID = TestUtil.registerSunAppServer(
                this, new Object[] { new TestPlatformProvider(defBCP, defBCP) });
        
        assertEquals("No Java platforms found.", 2, JavaPlatformManager.getDefault().getInstalledPlatforms().length);
    }
    
    public void testTestableInstantiate() throws Exception {
        File dirF = new File(getWorkDir(), "testEA");
        String name = "Test EnterpriseApplication";
        String j2eeLevel = "1.4";
        String warName = null;
        String jarName = null;
        String carName = null;
        String mainClass = null;
        String platformName = null;
        String sourceLevel = null;
        
        Set result = NewEarProjectWizardIterator.testableInstantiate(dirF, name,
                j2eeLevel, serverInstanceID, warName, jarName,
                carName, mainClass, platformName, sourceLevel);
        
        Set<FileObject> expResult = new HashSet<FileObject>();
        FileObject testEAFO = FileUtil.toFileObject(new File(getWorkDir(), "testEA"));
        assertNotNull("testEA directory", testEAFO);
        expResult = Collections.singleton(testEAFO);
        assertEquals(expResult, result);
        
        EditableProperties ep = TestUtil.loadProjectProperties(testEAFO);
        assertNull("app.client is not set", ep.getProperty(EarProjectProperties.APPLICATION_CLIENT));
        assertEquals("client.module.uri is empty", "", ep.getProperty(EarProjectProperties.CLIENT_MODULE_URI));
    }
    
    public void testTestableInstantiateWithAppClient() throws Exception {
        String resource = "org-netbeans-modules-j2ee-clientproject/application-client-5.xml";
        assertNotNull("application client registered", Repository.getDefault().getDefaultFileSystem().findResource(resource));
        File dirF = new File(getWorkDir(), "testEA");
        String name = "Test EnterpriseApplication";
        String j2eeLevel = "1.4";
        String warName = null;
        String jarName = null;
        String carName = "testEA-app-client";
        String mainClass = "testEA.app.client.Main";
        String platformName = null;
        String sourceLevel = null;
        
        Set result = NewEarProjectWizardIterator.testableInstantiate(dirF, name,
                j2eeLevel, serverInstanceID, warName, jarName,
                carName, mainClass, platformName, sourceLevel);
        
        Set<FileObject> expResult = new HashSet<FileObject>();
        File testEA = new File(getWorkDir(), "testEA");
        FileObject testEAFO = FileUtil.toFileObject(testEA);
        assertNotNull("testEA directory", testEAFO);
        FileObject testEAClientFO = FileUtil.toFileObject(new File(testEA, "testEA-app-client"));
        assertNotNull("testEA-app-client directory", testEAClientFO);
        expResult.add(testEAFO);
        expResult.add(testEAClientFO);
        assertEquals(expResult, result);
        
        EditableProperties ep = TestUtil.loadProjectProperties(testEAFO);
        assertEquals("app.client set", "testEA-app-client", ep.getProperty(EarProjectProperties.APPLICATION_CLIENT));
        assertEquals("client.module.uri is set to app. client", "Test EnterpriseApplication/${app.client}", ep.getProperty(EarProjectProperties.CLIENT_MODULE_URI));
    }
    
    public void testTestableInstantiateWithWebAndEJBAppClient() throws Exception {
        String resource = "org-netbeans-modules-j2ee-clientproject/application-client-5.xml";
        assertNotNull("application client registered", Repository.getDefault().getDefaultFileSystem().findResource(resource));
        File dirF = new File(getWorkDir(), "testEA");
        String name = "Test EnterpriseApplication";
        String j2eeLevel = "1.4";
        String warName = "testEA-war";
        String jarName = "testEA-ejb";
        String carName = "testEA-app-client";
        String mainClass = "testEA.app.client.Main";
        String platformName = null;
        String sourceLevel = null;
        
        Set result = NewEarProjectWizardIterator.testableInstantiate(dirF, name,
                j2eeLevel, serverInstanceID, warName, jarName,
                carName, mainClass, platformName, sourceLevel);
        
        Set<FileObject> expResult = new HashSet<FileObject>();
        File testEA = new File(getWorkDir(), "testEA");
        FileObject testEAFO = FileUtil.toFileObject(testEA);
        assertNotNull("testEA directory", testEAFO);
        FileObject testEAEjbFO = FileUtil.toFileObject(new File(testEA, "testEA-ejb"));
        assertNotNull("testEA-ejb directory", testEAEjbFO);
        FileObject testEAClientFO = FileUtil.toFileObject(new File(testEA, "testEA-app-client"));
        assertNotNull("testEA-app-client directory", testEAClientFO);
        FileObject testEAWebFO = FileUtil.toFileObject(new File(testEA, "testEA-war"));
        assertNotNull("testEA-war directory", testEAWebFO);
        
        expResult.add(testEAFO);
        expResult.add(testEAEjbFO);
        expResult.add(testEAClientFO);
        expResult.add(testEAWebFO);
        assertEquals(expResult, result);
        
        EditableProperties ep = TestUtil.loadProjectProperties(testEAFO);
        assertNull("app.client not set", ep.getProperty(EarProjectProperties.APPLICATION_CLIENT));
        assertEquals("client.module.uri is set to war", "testEA-war.war", ep.getProperty(EarProjectProperties.CLIENT_MODULE_URI));
        EarProjectTest.openProject((EarProject) ProjectManager.getDefault().findProject(testEAFO));
        
        doTestThatEJBWasAddedToWebAndAC(testEAWebFO, testEAClientFO); // #74123
    }
    
    static void doTestThatEJBWasAddedToWebAndAC( // #66546 and #74123
            final FileObject testEAWebFO, final FileObject testEAClientFO) throws IOException {
        
        Project testEAClientProject = ProjectManager.getDefault().findProject(testEAClientFO);
        SubprojectProvider acSubProjects = (SubprojectProvider)
                testEAClientProject.getLookup().lookup(SubprojectProvider.class);
        assertNotNull("application client has SubprojectProvider in its lookup", acSubProjects);
        assertEquals("ejb added to application client's", 1, acSubProjects.getSubprojects().size());
        
        Project testEAWebProject = ProjectManager.getDefault().findProject(testEAWebFO);
        SubprojectProvider webSubProjects = (SubprojectProvider)
                testEAWebProject.getLookup().lookup(SubprojectProvider.class);
        assertNotNull("web project has SubprojectProvider in its lookup", webSubProjects);
        assertEquals("ejb added to web project's", 1, webSubProjects.getSubprojects().size());
    }
    
}
