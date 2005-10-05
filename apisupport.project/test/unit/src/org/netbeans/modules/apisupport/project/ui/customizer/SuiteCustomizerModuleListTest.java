/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.project.ui.customizer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.apisupport.project.TestBase;
import org.netbeans.modules.apisupport.project.suite.SuiteProject;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.explorer.ExplorerManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/** Checks the behaviour of enabled module list.
 *
 * @author Jaroslav Tulach
 */
public class SuiteCustomizerModuleListTest extends TestBase {
    private FileObject suiteRepoFO;
    private SuiteProject suite1Prj;
    private SuiteProject suite2Prj;
    private SuiteProperties suite1Props;
    private FileObject suite1FO;
    private FileObject suite2FO;
    
    private SuiteCustomizerLibraries customizer;
    
    public SuiteCustomizerModuleListTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        suiteRepoFO = FileUtil.toFileObject(copyFolder(extexamplesF));
        suite1FO = suiteRepoFO.getFileObject("suite1");
        suite2FO = suiteRepoFO.getFileObject("suite2");
        suite1Prj = (SuiteProject) ProjectManager.getDefault().findProject(suite1FO);
        suite2Prj = (SuiteProject) ProjectManager.getDefault().findProject(suite1FO);
        SubprojectProvider suite1spp = (SubprojectProvider) suite1Prj.getLookup().lookup(SubprojectProvider.class);
        Set/*<Project>*/ suite1subModules = suite1spp.getSubprojects();
        this.suite1Props = new SuiteProperties(suite1Prj, suite1Prj.getHelper(),
                suite1Prj.getEvaluator(), suite1subModules);
        
        customizer = new SuiteCustomizerLibraries(this.suite1Props);
    }

    public void testDisableCluster() throws Exception {
        enableAllCusters(false);
        doDisableCluster(0, true);
    }
    
    public void testDisableCluster2() throws Exception {
        enableAllCusters(false);
        doDisableCluster(1, true);
    }
    
    public void testDisableTwoClusters() throws Exception {
        enableAllCusters(false);
        
        String c1 = doDisableCluster(1, true);
        String c2 = doDisableCluster(2, false);
        HashSet c = new HashSet();
        c.add(c1);
        c.add(c2);
        
        String[] xyz = suite1Props.getDisabledClusters();
        assertEquals("Two clusters disabled", 2, xyz.length);
        
        HashSet real = new HashSet(Arrays.asList(xyz));
        assertEquals("Same are disabled", c, real);
    }
    
    private String doDisableCluster(int index, boolean doCheck) throws Exception {
        Node n = customizer.getExplorerManager().getRootContext();
        Node[] clusters = n.getChildren().getNodes();
        if (clusters.length <= index) {
            fail ("Wrong, there should be some clusters. at least: " + index + " and was: " + clusters.length);
        }
        Node[] modules = clusters[index].getChildren().getNodes();
        if (modules.length == 0) {
            fail("Expected more modules for cluster: " + clusters[index]);
        }

        setNodeEnabled(clusters[index], false);
        assertEquals("No modules in disabled clusters", 
                clusters[index].getChildren().getNodes().length, modules.length);
        
        customizer.store();
        suite1Props.storeProperties();
        
        if (doCheck) {
            String[] xyz = suite1Props.getDisabledClusters();
            assertEquals("One cluster is disabled", 1, xyz.length);
            assertEquals("It's name is name of the node", clusters[index].getName(), xyz[0]);
        }
        
        return clusters[index].getName();
    }
    
    public void testDisableModule() throws Exception {
        enableAllCusters(true);
        
        Node n = customizer.getExplorerManager().getRootContext();
        Node[] clusters = n.getChildren().getNodes();
        if (clusters.length == 0) {
            fail("Should be at least one cluster");
        }
        Node[] modules = clusters[0].getChildren().getNodes();
        if (modules.length == 0) {
            fail("Expected at least one module in cluster: " + clusters[0]);
        }

        setNodeEnabled(modules[0], false);
        assertNodeEnabled(modules[0], Boolean.FALSE);
        
        customizer.store();
        suite1Props.storeProperties();
                
        String[] xyz = suite1Props.getDisabledModules();
        assertEquals("One module is disabled", 1, xyz.length);
        assertEquals("It's name is name of the node", modules[0].getName(), xyz[0]);
    }
    
    private static void assertNodeEnabled(Node n, Boolean value) throws Exception {
        org.openide.nodes.Node.PropertySet[] arr = n.getPropertySets();
        for (int i = 0; i < arr.length; i++) {
            org.openide.nodes.Node.Property[] x = arr[i].getProperties();
            for (int j = 0; j < x.length; j++) {
                if (x[j].getName().equals("enabled")) {
                    Object o = x[j].getValue();
                    assertEquals("Node is correctly enabled/disabled: " + n, value, o);
                    return;
                }
            }
        }
        fail("No enabled property found: " + n);
    }
    private static void setNodeEnabled(Node n, boolean value) throws Exception {
        org.openide.nodes.Node.PropertySet[] arr = n.getPropertySets();
        for (int i = 0; i < arr.length; i++) {
            org.openide.nodes.Node.Property[] x = arr[i].getProperties();
            for (int j = 0; j < x.length; j++) {
                if (x[j].getName().equals("enabled")) {
                    x[j].setValue(Boolean.valueOf(value));
                    return;
                }
            }
        }
        fail("No enabled property found: " + n);
    }

    private void enableAllCusters(boolean enableModulesAsWell) throws Exception {
        Node n = customizer.getExplorerManager().getRootContext();
        Node[] clusters = n.getChildren().getNodes();
        
        for (int i = 0; i < clusters.length; i++) {
            setNodeEnabled(clusters[i], true);
            if (enableModulesAsWell) {
                Node[] modules = clusters[i].getChildren().getNodes();
                for (int j = 0; j < modules.length; j++) {
                    setNodeEnabled(modules[j], true);
                }
            }
        }
    }
}
