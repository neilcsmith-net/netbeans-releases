/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.ant.freeform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.Sources;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.w3c.dom.Element;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.modules.project.ant.AntBasedProjectFactorySingleton;
import org.openide.filesystems.FileLock;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

// XXX: part of the testSourceFoldersAndSourceViews test is commented out
// becasue implementation of Source interface does not refresh automatically.
/**
 * Tests for FreeformProjectGenerator.
 *
 * @author David Konecny
 */
public class FreeformProjectGeneratorTest extends NbTestCase {

    private File lib1;
    private File lib2;
    private File src;
    private File test;
    
    public FreeformProjectGeneratorTest(java.lang.String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        clearWorkDir();
    }
    
    protected void tearDown() throws Exception {
    }
    
    private AntProjectHelper createEmptyProject(String projectFolder, String projectName, boolean notSoEmpty) throws Exception {
        File base = new File(getWorkDir(), projectFolder);
        base.mkdir();
        File antScript = new File(base, "build.xml");
        antScript.createNewFile();
        src = new File(base, "src");
        src.mkdir();
        test = new File(base, "test");
        test.mkdir();
        File libs = new File(base, "libs");
        libs.mkdir();
        lib1 = new File(libs, "some.jar");
        createRealJarFile(lib1);
        lib2 = new File(libs, "some2.jar");
        createRealJarFile(lib2);
        
// XXX: might need to call refresh here??
//        FileObject fo = FileUtil.toFileObject(getWorkDir());
//        fo.refresh();
        
        ArrayList sources = new ArrayList();
        ArrayList compUnits = new ArrayList();
        if (notSoEmpty) {
            FreeformProjectGenerator.SourceFolder sf = new FreeformProjectGenerator.SourceFolder();
            sf.label = "src";
            sf.type = "java";
            sf.style = "packages";
            sf.location = src.getAbsolutePath();
            sources.add(sf);
            FreeformProjectGenerator.JavaCompilationUnit cu = new FreeformProjectGenerator.JavaCompilationUnit();
            FreeformProjectGenerator.JavaCompilationUnit.CP cp = new FreeformProjectGenerator.JavaCompilationUnit.CP();
            cp.classpath = lib1.getAbsolutePath();
            cp.mode = "compile";
            cu.classpath = Collections.singletonList(cp);
            cu.sourceLevel = "1.4";
            cu.packageRoots = Collections.singletonList(src.getAbsolutePath());
            compUnits.add(cu);
        }
        AntProjectHelper helper = FreeformProjectGenerator.createJavaProject(base, base, projectName, null, new ArrayList(), sources, compUnits);
        return helper;
    }
    
    public void testCreateProject() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj1", "proj-1", false);
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
        assertNotNull("Project was not created", p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        
        ProjectInformation pi = (ProjectInformation)p.getLookup().lookup(ProjectInformation.class);
        assertEquals("Project name was not set", "proj-1", pi.getName());
    }
    
    public void testRawCreateProject() throws Exception {
        File base = new File(getWorkDir(), "proj");
        base.mkdir();
        File diffFolder = new File(getWorkDir(), "separate");
        diffFolder.mkdir();
        File antScript = new File(diffFolder, "build.xml");
        antScript.createNewFile();
        
// XXX: might need to call refresh here??
//        FileObject fo = FileUtil.toFileObject(getWorkDir());
//        fo.refresh();

        AntProjectHelper helper = FreeformProjectGenerator.createJavaProject(diffFolder, base, "p-r-o-j", antScript, new ArrayList(), new ArrayList(), new ArrayList());
        Project p = ProjectManager.getDefault().findProject(helper.getProjectDirectory());
        assertNotNull("Project was not created", p);
        List mappings = new ArrayList();
        FreeformProjectGenerator.TargetMapping tm = new FreeformProjectGenerator.TargetMapping();
        tm.name = "foo";
        tm.script = "antScript";
        mappings.add(tm);
        List customActions = new ArrayList();
        FreeformProjectGenerator.CustomTarget ct = new FreeformProjectGenerator.CustomTarget();
        ct.label = "customAction1";
        customActions.add(ct);
        List folders = new ArrayList();
        FreeformProjectGenerator.SourceFolder sf = new FreeformProjectGenerator.SourceFolder();
        sf.label = "folder3";
        sf.location = "location3";
        sf.style = "style";
        folders.add(sf);
        FreeformProjectGenerator.putTargetMappings(helper, mappings);
        FreeformProjectGenerator.putContextMenuAction(helper, mappings);
        FreeformProjectGenerator.putCustomContextMenuActions(helper, customActions);
        FreeformProjectGenerator.putSourceFolders(helper, folders, null);
        FreeformProjectGenerator.putSourceViews(helper, folders, null);
//        ProjectManager.getDefault().saveAllProjects();
        
        // check that all elements are written in expected order
        
        Element el = helper.getPrimaryConfigurationData(true);
        List subElements = Util.findSubElements(el);
        assertEquals(5, subElements.size());
        assertElementArray(subElements, 
            new String[]{"name", "properties", "folders", "ide-actions", "view"}, 
            new String[]{null, null, null, null, null});
        Element el2 = (Element)subElements.get(4);
        subElements = Util.findSubElements(el2);
        assertEquals(2, subElements.size());
        assertElementArray(subElements, 
            new String[]{"items", "context-menu"}, 
            new String[]{null, null});
        Element el3 = (Element)subElements.get(0);
        List subEls = Util.findSubElements(el3);
        assertEquals(2, subEls.size());
        assertElementArray(subEls, 
            new String[]{"source-folder", "source-file"}, 
            new String[]{null, null});
        el3 = (Element)subElements.get(1);
        subEls = Util.findSubElements(el3);
        assertEquals(2, subEls.size());
        assertElementArray(subEls, 
            new String[]{"ide-action", "action"}, 
            new String[]{null, null});
            
        // calling getters and setters in random order cannot change order of elements

        mappings = FreeformProjectGenerator.getTargetMappings(helper);
        customActions = FreeformProjectGenerator.getCustomContextMenuActions(helper);
        folders = FreeformProjectGenerator.getSourceFolders(helper, null);
        // style is not read by getSourceFolders and needs to be fixed here:
        ((FreeformProjectGenerator.SourceFolder)folders.get(0)).style = "style";
        FreeformProjectGenerator.putTargetMappings(helper, mappings);
        FreeformProjectGenerator.putContextMenuAction(helper, mappings);
        FreeformProjectGenerator.putCustomContextMenuActions(helper, customActions);
        FreeformProjectGenerator.putSourceFolders(helper, folders, null);
        FreeformProjectGenerator.putSourceViews(helper, folders, null);
        FreeformProjectGenerator.putSourceViews(helper, folders, null);
        FreeformProjectGenerator.putSourceFolders(helper, folders, null);
        FreeformProjectGenerator.putCustomContextMenuActions(helper, customActions);
        FreeformProjectGenerator.putContextMenuAction(helper, mappings);
        FreeformProjectGenerator.putTargetMappings(helper, mappings);
//        ProjectManager.getDefault().saveAllProjects();
        el = helper.getPrimaryConfigurationData(true);
        subElements = Util.findSubElements(el);
        assertEquals(5, subElements.size());
        assertElementArray(subElements, 
            new String[]{"name", "properties", "folders", "ide-actions", "view"}, 
            new String[]{null, null, null, null, null});
        el2 = (Element)subElements.get(4);
        subElements = Util.findSubElements(el2);
        assertEquals(2, subElements.size());
        assertElementArray(subElements, 
            new String[]{"items", "context-menu"}, 
            new String[]{null, null});
        el3 = (Element)subElements.get(0);
        subEls = Util.findSubElements(el3);
        assertEquals(2, subEls.size());
        assertElementArray(subEls, 
            new String[]{"source-folder", "source-file"}, 
            new String[]{null, null});
        el3 = (Element)subElements.get(1);
        subEls = Util.findSubElements(el3);
        assertEquals(2, subEls.size());
        assertElementArray(subEls, 
            new String[]{"ide-action", "action"}, 
            new String[]{null, null});

        // validate against schema:
        ProjectManager.getDefault().saveAllProjects();
        validate(p);
    }
    
    public void testTargetMappings() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj2", "proj-2", false);
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
        assertNotNull("Project was not created", p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        
        ActionProvider ap = (ActionProvider)p.getLookup().lookup(ActionProvider.class);
        assertNotNull("Project does not have ActionProvider", ap);
        assertEquals("Project cannot have any action", 0, ap.getSupportedActions().length);
        
        List list = FreeformProjectGenerator.getTargetMappings(helper);
        assertNotNull("getTargetMappings() cannot return null", list);
        assertEquals("Project cannot have any action", 0, list.size());
        
        list = new ArrayList();
        FreeformProjectGenerator.TargetMapping tm = new FreeformProjectGenerator.TargetMapping();
        tm.name = "clean";
        tm.targets = Collections.singletonList("clean-target");
        list.add(tm);
        tm = new FreeformProjectGenerator.TargetMapping();
        tm.name = "build";
        tm.targets = Collections.singletonList("build-target");
        tm.script = "${ant.script.two}";
        list.add(tm);
        tm = new FreeformProjectGenerator.TargetMapping();
        tm.name = "rebuild";
        tm.targets = Arrays.asList(new String[]{"clean-target", "build-target"});
        tm.script = "${ant.script.three}";
        list.add(tm);
        tm = new FreeformProjectGenerator.TargetMapping();
        FreeformProjectGenerator.putTargetMappings(helper, list);
        List list2 = FreeformProjectGenerator.getTargetMappings(helper);
        // once again: put and get
        FreeformProjectGenerator.putTargetMappings(helper, list2);
        list2 = FreeformProjectGenerator.getTargetMappings(helper);
        assertNotNull("getTargetMappings() cannot return null", list2);
        assertEquals("Project must have 3 actions", 3, list2.size());
        assertEquals("Script was not correctly saved", null, ((FreeformProjectGenerator.TargetMapping)list2.get(0)).script);
        assertEquals("Script was not correctly saved", "${ant.script.two}", ((FreeformProjectGenerator.TargetMapping)list2.get(1)).script);
        assertEquals("Script was not correctly saved", "${ant.script.three}", ((FreeformProjectGenerator.TargetMapping)list2.get(2)).script);
        assertEquals("Project must have 3 actions", 3, ap.getSupportedActions().length);
        assertTrue("Action clean must be enabled", ap.isActionEnabled("clean", Lookup.EMPTY));
        assertTrue("Action build must be enabled", ap.isActionEnabled("build", Lookup.EMPTY));
        assertTrue("Action rebuild must be enabled", ap.isActionEnabled("rebuild", Lookup.EMPTY));
        boolean ok = false;
        try {
            assertFalse("Action javadoc must be disabled", ap.isActionEnabled("javadoc", Lookup.EMPTY));
        } catch (IllegalArgumentException ex) {
            ok = true;
        }
        assertTrue("Exception must be thrown for non-existing actions", ok);
        ProjectManager.getDefault().saveAllProjects();
    }

    public void testRawTargetMappings() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj", "proj", false);
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
        assertNotNull("Project was not created", p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        
        // check that all data are correctly persisted
        
        List mappings = new ArrayList();
        FreeformProjectGenerator.TargetMapping tm = new FreeformProjectGenerator.TargetMapping();
        tm.name = "first-targetName";
        tm.script = "antScript";
        tm.targets = new ArrayList();
        tm.targets.add("target-1");
        tm.targets.add("target-2");
        tm.targets.add("target-3");
        tm.contexts = new ArrayList();
        FreeformProjectGenerator.TargetMapping.Context ctx = new FreeformProjectGenerator.TargetMapping.Context();
        ctx.folder = "someFolder1";
        ctx.format = "someFormat1";
        ctx.property = "someProperty1";
        tm.contexts.add(ctx);
        ctx = new FreeformProjectGenerator.TargetMapping.Context();
        ctx.folder = "someFolder2";
        ctx.format = "someFormat2";
        ctx.property = "someProperty2";
        tm.contexts.add(ctx);
        mappings.add(tm);
        tm = new FreeformProjectGenerator.TargetMapping();
        tm.name = "second-targetName";
        tm.script = "second-antScript";
        tm.targets = new ArrayList();
        tm.targets.add("second-target-1");
        tm.targets.add("second-target-2");
        tm.targets.add("second-target-3");
        tm.contexts = new ArrayList();
        ctx = new FreeformProjectGenerator.TargetMapping.Context();
        ctx.folder = "second-someFolder1";
        ctx.format = "second-someFormat1";
        ctx.property = "second-someProperty1";
        tm.contexts.add(ctx);
        ctx = new FreeformProjectGenerator.TargetMapping.Context();
        ctx.folder = "second-someFolder2";
        ctx.format = "second-someFormat2";
        ctx.property = "second-someProperty2";
        tm.contexts.add(ctx);
        mappings.add(tm);
        FreeformProjectGenerator.putTargetMappings(helper, mappings);
        // test getter and setter here:
        mappings = FreeformProjectGenerator.getTargetMappings(helper);
        FreeformProjectGenerator.putTargetMappings(helper, mappings);
//        ProjectManager.getDefault().saveAllProjects();
        Element el = helper.getPrimaryConfigurationData(true);
        el = Util.findElement(el, "ide-actions", FreeformProjectType.NS_GENERAL);
        assertNotNull("Target mapping were not saved correctly",  el);
        List subElements = Util.findSubElements(el);
        assertEquals(2, subElements.size());
        // compare first target mapping
        Element el2 = (Element)subElements.get(0);
        assertElement(el2, "action", null, "name", "first-targetName");
        List l1 = Util.findSubElements(el2);
        assertEquals(6, l1.size());
        assertElementArray(l1, 
            new String[]{"script", "target", "target", "target", "context", "context"}, 
            new String[]{"antScript", "target-1", "target-2", "target-3", null, null});
        el2 = (Element)l1.get(4);
        List l2 = Util.findSubElements(el2);
        assertEquals(3, l2.size());
        assertElementArray(l2, 
            new String[]{"property", "format", "folder"}, 
            new String[]{"someProperty1", "someFormat1", "someFolder1"});
        el2 = (Element)l1.get(5);
        l2 = Util.findSubElements(el2);
        assertEquals(3, l2.size());
        assertElementArray(l2, 
            new String[]{"property", "format", "folder"}, 
            new String[]{"someProperty2", "someFormat2", "someFolder2"});
        // compare second target mapping
        el2 = (Element)subElements.get(1);
        assertElement(el2, "action", null, "name", "second-targetName");
        l1 = Util.findSubElements(el2);
        assertEquals(6, l1.size());
        assertElementArray(l1, 
            new String[]{"script", "target", "target", "target", "context", "context"}, 
            new String[]{"second-antScript", "second-target-1", "second-target-2", "second-target-3", null, null});
        el2 = (Element)l1.get(4);
        l2 = Util.findSubElements(el2);
        assertEquals(3, l2.size());
        assertElementArray(l2, 
            new String[]{"property", "format", "folder"}, 
            new String[]{"second-someProperty1", "second-someFormat1", "second-someFolder1"});
        el2 = (Element)l1.get(5);
        l2 = Util.findSubElements(el2);
        assertEquals(3, l2.size());
        assertElementArray(l2, 
            new String[]{"property", "format", "folder"}, 
            new String[]{"second-someProperty2", "second-someFormat2", "second-someFolder2"});
        // validate against schema:
        ProjectManager.getDefault().saveAllProjects();
        validate(p);
            
        // test updating
            
        mappings = new ArrayList();
        tm = new FreeformProjectGenerator.TargetMapping();
        tm.name = "foo";
        tm.script = "antScript";
        tm.targets = new ArrayList();
        tm.targets.add("target-1");
        tm.targets.add("target-2");
        mappings.add(tm);
        FreeformProjectGenerator.putTargetMappings(helper, mappings);
//        ProjectManager.getDefault().saveAllProjects();
        el = helper.getPrimaryConfigurationData(true);
        el = Util.findElement(el, "ide-actions", FreeformProjectType.NS_GENERAL);
        assertNotNull("Target mapping were not saved correctly",  el);
        subElements = Util.findSubElements(el);
        assertEquals(1, subElements.size());
        // compare first target mapping
        el2 = (Element)subElements.get(0);
        assertElement(el2, "action", null, "name", "foo");
        l1 = Util.findSubElements(el2);
        assertEquals(3, l1.size());
        assertElementArray(l1, 
            new String[]{"script", "target", "target"}, 
            new String[]{"antScript", "target-1", "target-2"});
        mappings = new ArrayList();
        tm = new FreeformProjectGenerator.TargetMapping();
        tm.name = "foo";
        tm.script = "diff-script";
        tm.targets = new ArrayList();
        tm.targets.add("target-1");
        tm.targets.add("target-B");
        mappings.add(tm);
        FreeformProjectGenerator.putTargetMappings(helper, mappings);
//        ProjectManager.getDefault().saveAllProjects();
        el = helper.getPrimaryConfigurationData(true);
        el = Util.findElement(el, "ide-actions", FreeformProjectType.NS_GENERAL);
        assertNotNull("Target mapping were not saved correctly",  el);
        subElements = Util.findSubElements(el);
        assertEquals(1, subElements.size());
        // compare first target mapping
        el2 = (Element)subElements.get(0);
        assertElement(el2, "action", null, "name", "foo");
        l1 = Util.findSubElements(el2);
        assertEquals(3, l1.size());
        assertElementArray(l1, 
            new String[]{"script", "target", "target"}, 
            new String[]{"diff-script", "target-1", "target-B"});
        // validate against schema:
        ProjectManager.getDefault().saveAllProjects();
        validate(p);
    }

    public void testRawContextMenuActions() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj", "proj", false);
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
        assertNotNull("Project was not created", p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        
        // check that all data are correctly persisted
        
        List mappings = new ArrayList();
        FreeformProjectGenerator.TargetMapping tm = new FreeformProjectGenerator.TargetMapping();
        tm.name = "first-targetName";
        mappings.add(tm);
        tm = new FreeformProjectGenerator.TargetMapping();
        tm.name = "second-targetName";
        mappings.add(tm);
        FreeformProjectGenerator.putContextMenuAction(helper, mappings);
//        ProjectManager.getDefault().saveAllProjects();
        Element el = helper.getPrimaryConfigurationData(true);
        el = Util.findElement(el, "view", FreeformProjectType.NS_GENERAL);
        assertNotNull("Target mapping were not saved correctly",  el);
        el = Util.findElement(el, "context-menu", FreeformProjectType.NS_GENERAL);
        assertNotNull("Target mapping were not saved correctly",  el);
        List subElements = Util.findSubElements(el);
        assertEquals(2, subElements.size());
        assertElementArray(subElements, 
            new String[]{"ide-action", "ide-action"}, 
            new String[]{null, null},
            new String[]{"name", "name"}, 
            new String[]{"first-targetName", "second-targetName"}
            );
        // validate against schema:
        ProjectManager.getDefault().saveAllProjects();
        validate(p);
            
        // test updating
            
        tm = new FreeformProjectGenerator.TargetMapping();
        tm.name = "foo";
        mappings.add(tm);
        tm = new FreeformProjectGenerator.TargetMapping();
        tm.name = "bar";
        mappings.add(tm);
        FreeformProjectGenerator.putTargetMappings(helper, mappings);
//        ProjectManager.getDefault().saveAllProjects();
        el = helper.getPrimaryConfigurationData(true);
        el = Util.findElement(el, "view", FreeformProjectType.NS_GENERAL);
        assertNotNull("Target mapping were not saved correctly",  el);
        el = Util.findElement(el, "context-menu", FreeformProjectType.NS_GENERAL);
        assertNotNull("Target mapping were not saved correctly",  el);
        subElements = Util.findSubElements(el);
        assertEquals(2, subElements.size());
        assertElementArray(subElements, 
            new String[]{"ide-action", "ide-action", "ide-action", "ide-action"},
            new String[]{null, null, null, null},
            new String[]{"name", "name", "name", "name"}, 
            new String[]{"first-targetName", "second-targetName", "foo", "bar"}
            );
        // validate against schema:
        ProjectManager.getDefault().saveAllProjects();
        validate(p);
    }

    public void testRawCustomContextMenuActions() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj", "proj", false);
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
        assertNotNull("Project was not created", p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        
        // check that all data are correctly persisted
        
        List customActions = new ArrayList();
        FreeformProjectGenerator.CustomTarget ct = new FreeformProjectGenerator.CustomTarget();
        ct.label = "customAction1";
        ct.script = "customScript1";
        ct.targets = new ArrayList();
        ct.targets.add("customTarget1");
        ct.targets.add("customTarget2");
        customActions.add(ct);
        ct = new FreeformProjectGenerator.CustomTarget();
        ct.label = "customAction2";
        ct.script = "customScript2";
        ct.targets = new ArrayList();
        ct.targets.add("second-customTarget1");
        ct.targets.add("second-customTarget2");
        customActions.add(ct);
        FreeformProjectGenerator.putCustomContextMenuActions(helper, customActions);
        // test getter and setter here:
        customActions = FreeformProjectGenerator.getCustomContextMenuActions(helper);
        FreeformProjectGenerator.putCustomContextMenuActions(helper, customActions);
//        ProjectManager.getDefault().saveAllProjects();
        Element el = helper.getPrimaryConfigurationData(true);
        el = Util.findElement(el, "view", FreeformProjectType.NS_GENERAL);
        assertNotNull("Target mapping were not saved correctly",  el);
        el = Util.findElement(el, "context-menu", FreeformProjectType.NS_GENERAL);
        assertNotNull("Target mapping were not saved correctly",  el);
        List subElements = Util.findSubElements(el);
        assertEquals(2, subElements.size());
        assertElementArray(subElements, 
            new String[]{"action", "action"}, 
            new String[]{null, null});
        // compare first custom action
        Element el2 = (Element)subElements.get(0);
        List l1 = Util.findSubElements(el2);
        assertEquals(4, l1.size());
        assertElementArray(l1, 
            new String[]{"script", "label", "target", "target"}, 
            new String[]{"customScript1", "customAction1", "customTarget1", "customTarget2"});
        // compare second custom action
        el2 = (Element)subElements.get(1);
        l1 = Util.findSubElements(el2);
        assertEquals(4, l1.size());
        assertElementArray(l1, 
            new String[]{"script", "label", "target", "target"}, 
            new String[]{"customScript2", "customAction2", "second-customTarget1", "second-customTarget2"});
        // validate against schema:
        ProjectManager.getDefault().saveAllProjects();
        validate(p);
            
        // test updating
            
        customActions = new ArrayList();
        ct = new FreeformProjectGenerator.CustomTarget();
        ct.label = "fooLabel";
        customActions.add(ct);
        ct = new FreeformProjectGenerator.CustomTarget();
        ct.label = "barLabel";
        customActions.add(ct);
        FreeformProjectGenerator.putCustomContextMenuActions(helper, customActions);
//        ProjectManager.getDefault().saveAllProjects();
        el = helper.getPrimaryConfigurationData(true);
        el = Util.findElement(el, "view", FreeformProjectType.NS_GENERAL);
        assertNotNull("Target mapping were not saved correctly",  el);
        el = Util.findElement(el, "context-menu", FreeformProjectType.NS_GENERAL);
        assertNotNull("Target mapping were not saved correctly",  el);
        subElements = Util.findSubElements(el);
        assertEquals(2, subElements.size());
        assertElementArray(subElements, 
            new String[]{"action", "action"}, 
            new String[]{null, null});
        // compare first custom action
        el2 = (Element)subElements.get(0);
        l1 = Util.findSubElements(el2);
        assertEquals(1, l1.size());
        assertElementArray(l1, 
            new String[]{"label"}, 
            new String[]{"fooLabel"});
        // compare second custom action
        el2 = (Element)subElements.get(1);
        l1 = Util.findSubElements(el2);
        assertEquals(1, l1.size());
        assertElementArray(l1, 
            new String[]{"label"}, 
            new String[]{"barLabel"});
        // validate against schema:
        ProjectManager.getDefault().saveAllProjects();
        validate(p);
    }

    /**
     * Asserts that given Element has expected name and its text match expected value.
     * @param element element to test
     * @param expectedName expected name of element; cannot be null
     * @param expectedValue can be null in which case value is not tested
     */
    public static void assertElement(Element element, String expectedName, String expectedValue) {
        String message = "Element "+element+" does not match [name="+expectedName+",value="+expectedValue+"]"; // NOI18N
        assertEquals(message, expectedName, element.getLocalName());
        if (expectedValue != null) {
            assertEquals(message, expectedValue, Util.findText(element));
        }
    }

    /**
     * See {@link #assertElement(Element, String, String)} for more details. This 
     * method does exactly the same just on the list of elements and expected names. 
     */
    public static void assertElementArray(List/*<Element>*/ elements, String[] expectedNames, String[] expectedValues) {
        for (int i=0; i<elements.size(); i++) {
            assertElement((Element)elements.get(i), expectedNames[i], expectedValues[i]);
        }
    }
    
    /**
     * Asserts that given Element has expected name and its text match expected value and
     * it also has expect attribute with expected value.
     * @param element element to test
     * @param expectedName expected name of element; cannot be null
     * @param expectedValue can be null in which case value is not tested
     * @param expectedAttrName expected name of attribute; cannot be null
     * @param expectedAttrValue expected value of attribute; cannot be null
     */
    public static void assertElement(Element element, String expectedName, String expectedValue, String expectedAttrName, String expectedAttrValue) {
        String message = "Element "+element+" does not match [name="+expectedName+",value="+
            expectedValue+", attr="+expectedAttrName+", attrvalue="+expectedAttrValue+"]"; // NOI18N
        assertEquals(message, expectedName, element.getLocalName());
        if (expectedValue != null) {
            assertEquals(message, expectedValue, Util.findText(element));
        }
        String val = element.getAttribute(expectedAttrName);
        assertEquals(expectedAttrValue, val);
    }
    
    /**
     * See {@link #assertElement(Element, String, String)} for more details. This 
     * method does exactly the same just on the list of elements and expected names
     * and expected attributes.
     */
    public static void assertElementArray(List/*<Element>*/ elements, String[] expectedNames, String[] expectedValues, String[] expectedAttrName, String[] expectedAttrValue) {
        for (int i=0; i<elements.size(); i++) {
            assertElement((Element)elements.get(i), expectedNames[i], expectedValues[i], expectedAttrName[i], expectedAttrValue[i]);
        }
    }
    
    public void testSourceFolders() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj3", "proj-3", true);
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
        assertNotNull("Project was not created", p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        
        Sources ss = ProjectUtils.getSources(p);
        assertEquals("Project must have one java source group", 1, ss.getSourceGroups("java").length);
        assertEquals("Project cannot have csharp source group", 0, ss.getSourceGroups("csharp").length);

        Listener l = new Listener();
        ss.addChangeListener(l);
        
        List sfs = FreeformProjectGenerator.getSourceFolders(helper, null);
        assertEquals("There must be one source folder", 1, sfs.size());
        FreeformProjectGenerator.SourceFolder sf = new FreeformProjectGenerator.SourceFolder();
        sf.label = "test";
        sf.type = "java";
        sf.location = test.getAbsolutePath();
        sfs.add(sf);
        FreeformProjectGenerator.putSourceFolders(helper, sfs, null);
        assertEquals("Project must have two java source groups", 2, ss.getSourceGroups("java").length);
        assertEquals("Project cannot have csharp source group", 0, ss.getSourceGroups("csharp").length);
        // XXX still crude impl that does not try to fire a minimal number of changes:
        /*
        assertEquals("Number of fired events does not match", 1, l.count);
         */
        l.reset();
        
        sfs = new ArrayList();
        sf = new FreeformProjectGenerator.SourceFolder();
        sf.label = "xdoc";
        sf.type = "x-doc";
        // just some path
        sf.location = test.getAbsolutePath();
        sfs.add(sf);
        FreeformProjectGenerator.putSourceFolders(helper, sfs, "x-doc");
        assertEquals("Project must have two java source groups", 2, ss.getSourceGroups("java").length);
        assertEquals("Project must have two java source groups", 2, FreeformProjectGenerator.getSourceFolders(helper, "java").size());
        assertEquals("Project cannot have csharp source group", 0, ss.getSourceGroups("csharp").length);
        assertEquals("Project must have one x-doc source group", 1, ss.getSourceGroups("x-doc").length);
        sf = new FreeformProjectGenerator.SourceFolder();
        sf.label = "xdoc2";
        sf.type = "x-doc";
        // just some path
        sf.location = src.getAbsolutePath();
        sfs.add(sf);
        FreeformProjectGenerator.putSourceFolders(helper, sfs, "x-doc");
        assertEquals("Project must have two java source groups", 2, ss.getSourceGroups("java").length);
        assertEquals("Project must have two java source groups", 2, FreeformProjectGenerator.getSourceFolders(helper, "java").size());
        assertEquals("Project cannot have csharp source group", 0, ss.getSourceGroups("csharp").length);
        assertEquals("Project must have two x-doc source groups", 2, ss.getSourceGroups("x-doc").length);
        assertEquals("Project must have two x-doc source groups", 2, FreeformProjectGenerator.getSourceFolders(helper, "x-doc").size());
        assertEquals("Project must have four source groups", 4, FreeformProjectGenerator.getSourceFolders(helper, null).size());

        sfs = FreeformProjectGenerator.getSourceFolders(helper, null);
        FreeformProjectGenerator.putSourceFolders(helper, sfs, null);
        assertEquals("Project must have two java source groups", 2, ss.getSourceGroups("java").length);
        assertEquals("Project must have two java source groups", 2, FreeformProjectGenerator.getSourceFolders(helper, "java").size());
        assertEquals("Project cannot have csharp source group", 0, ss.getSourceGroups("csharp").length);
        assertEquals("Project must have two x-doc source groups", 2, ss.getSourceGroups("x-doc").length);
        assertEquals("Project must have two x-doc source groups", 2, FreeformProjectGenerator.getSourceFolders(helper, "x-doc").size());
        assertEquals("Project must have four source groups", 4, FreeformProjectGenerator.getSourceFolders(helper, null).size());

        ProjectManager.getDefault().saveAllProjects();
    }
    
    public void testRawSourceFolders() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj", "proj", false);
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
        assertNotNull("Project was not created", p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        
        // check that all data are correctly persisted
        
        List folders = new ArrayList();
        FreeformProjectGenerator.SourceFolder sf = new FreeformProjectGenerator.SourceFolder();
        sf.label = "folder1";
        sf.type = "type1";
        sf.location = "location1";
        folders.add(sf);
        sf = new FreeformProjectGenerator.SourceFolder();
        sf.label = "folder2";
        sf.type = "type2";
        sf.location = "location2";
        folders.add(sf);
        FreeformProjectGenerator.putSourceFolders(helper, folders, null);
        // test getter and setter here:
        folders = FreeformProjectGenerator.getSourceFolders(helper, null);
        FreeformProjectGenerator.putSourceFolders(helper, folders, null);
//        ProjectManager.getDefault().saveAllProjects();
        Element el = helper.getPrimaryConfigurationData(true);
        el = Util.findElement(el, "folders", FreeformProjectType.NS_GENERAL);
        assertNotNull("Source folders were not saved correctly",  el);
        List subElements = Util.findSubElements(el);
        assertEquals(2, subElements.size());
        // compare first source folder
        Element el2 = (Element)subElements.get(0);
        assertElement(el2, "source-folder", null);
        List l1 = Util.findSubElements(el2);
        assertEquals(3, l1.size());
        assertElementArray(l1, 
            new String[]{"label", "type", "location"}, 
            new String[]{"folder1", "type1", "location1"});
        // compare second source folder
        el2 = (Element)subElements.get(1);
        assertElement(el2, "source-folder", null);
        l1 = Util.findSubElements(el2);
        assertEquals(3, l1.size());
        assertElementArray(l1, 
            new String[]{"label", "type", "location"}, 
            new String[]{"folder2", "type2", "location2"});
        // validate against schema:
        ProjectManager.getDefault().saveAllProjects();
        validate(p);
            
        // test rewriting of source folder of some type
        
        folders = new ArrayList();
        sf = new FreeformProjectGenerator.SourceFolder();
        sf.label = "folder3";
        sf.type = "type2";
        sf.location = "location3";
        folders.add(sf);
        FreeformProjectGenerator.putSourceFolders(helper, folders, "type2");
        ProjectManager.getDefault().saveAllProjects();
        el = helper.getPrimaryConfigurationData(true);
        el = Util.findElement(el, "folders", FreeformProjectType.NS_GENERAL);
        assertNotNull("Source folders were not saved correctly",  el);
        subElements = Util.findSubElements(el);
        assertEquals(2, subElements.size());
        // compare first source folder
        el2 = (Element)subElements.get(0);
        assertElement(el2, "source-folder", null);
        l1 = Util.findSubElements(el2);
        assertEquals(3, l1.size());
        assertElementArray(l1, 
            new String[]{"label", "type", "location"}, 
            new String[]{"folder1", "type1", "location1"});
        // compare second source folder
        el2 = (Element)subElements.get(1);
        assertElement(el2, "source-folder", null);
        l1 = Util.findSubElements(el2);
        assertEquals(3, l1.size());
        assertElementArray(l1, 
            new String[]{"label", "type", "location"}, 
            new String[]{"folder3", "type2", "location3"});
        // validate against schema:
        ProjectManager.getDefault().saveAllProjects();
        validate(p);
    }

    public void testSourceViews() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj6", "proj-6", true);
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
        assertNotNull("Project was not created", p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        
        Sources ss = ProjectUtils.getSources(p);
        assertEquals("Project must have one java source group", 1, ss.getSourceGroups("java").length);

        LogicalViewProvider lvp = (LogicalViewProvider)p.getLookup().lookup(LogicalViewProvider.class);
        assertNotNull("Project does not have LogicalViewProvider", lvp);
        Node n = lvp.createLogicalView();
        // expected subnodes: #1) src folder and #2) build.xml
        assertEquals("There must be two subnodes in logical view", 2, n.getChildren().getNodesCount());
        
        List sfs = FreeformProjectGenerator.getSourceViews(helper, null);
        assertEquals("There must be one source view", 1, sfs.size());
        FreeformProjectGenerator.SourceFolder sf = new FreeformProjectGenerator.SourceFolder();
        sf.label = "test";
        sf.style = "packages";
        sf.location = test.getAbsolutePath();
        sfs.add(sf);
        FreeformProjectGenerator.putSourceViews(helper, sfs, null);
        assertEquals("Project must have two packages source views", 2, FreeformProjectGenerator.getSourceViews(helper, "packages").size());
        assertEquals("Project cannot have any flat source view", 0, FreeformProjectGenerator.getSourceViews(helper, "flat").size());
        
        n = lvp.createLogicalView();
        // expected subnodes: #1) src folder and #2) build.xml and #3) tests
//        assertEquals("There must be three subnodes in logical view", 3, n.getChildren().getNodesCount());

        sfs = new ArrayList();
        sf = new FreeformProjectGenerator.SourceFolder();
        sf.label = "xdoc";
        sf.style = "tree";
        // just some path
        sf.location = test.getAbsolutePath();
        sfs.add(sf);
        FreeformProjectGenerator.putSourceViews(helper, sfs, "tree");
        assertEquals("Project must have two packages source views", 2, FreeformProjectGenerator.getSourceViews(helper, "packages").size());
        assertEquals("Project cannot have any flat source view", 0, FreeformProjectGenerator.getSourceViews(helper, "flat").size());
        assertEquals("Project must have one tree source view", 1, FreeformProjectGenerator.getSourceViews(helper, "tree").size());
        assertEquals("Project must have three source views", 3, FreeformProjectGenerator.getSourceViews(helper, null).size());
        sf = new FreeformProjectGenerator.SourceFolder();
        sf.label = "xdoc2";
        sf.style = "tree";
        // just some path
        sf.location = src.getAbsolutePath();
        sfs.add(sf);
        FreeformProjectGenerator.putSourceViews(helper, sfs, "tree");
        assertEquals("Project must have two packages source views", 2, FreeformProjectGenerator.getSourceViews(helper, "packages").size());
        assertEquals("Project cannot have any flat source view", 0, FreeformProjectGenerator.getSourceViews(helper, "flat").size());
        assertEquals("Project must have two tree source views", 2, FreeformProjectGenerator.getSourceViews(helper, "tree").size());
        assertEquals("Project must have four source views", 4, FreeformProjectGenerator.getSourceViews(helper, null).size());

        sfs = FreeformProjectGenerator.getSourceViews(helper, null);
        FreeformProjectGenerator.putSourceViews(helper, sfs, null);
        assertEquals("Project must have two packages source views", 2, FreeformProjectGenerator.getSourceViews(helper, "packages").size());
        assertEquals("Project cannot have any flat source view", 0, FreeformProjectGenerator.getSourceViews(helper, "flat").size());
        assertEquals("Project must have two tree source views", 2, FreeformProjectGenerator.getSourceViews(helper, "tree").size());
        assertEquals("Project must have four source views", 4, FreeformProjectGenerator.getSourceViews(helper, null).size());

        ProjectManager.getDefault().saveAllProjects();
    }
    
    public void testRawSourceViews() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj", "proj", false);
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
        assertNotNull("Project was not created", p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        
        // check that all data are correctly persisted
        
        List folders = new ArrayList();
        FreeformProjectGenerator.SourceFolder sf = new FreeformProjectGenerator.SourceFolder();
        sf.label = "folder1";
        sf.style = "style1";
        sf.location = "location1";
        folders.add(sf);
        sf = new FreeformProjectGenerator.SourceFolder();
        sf.label = "folder2";
        sf.style = "style2";
        sf.location = "location2";
        folders.add(sf);
        FreeformProjectGenerator.putSourceViews(helper, folders, null);
        // test getter and setter here:
        folders = FreeformProjectGenerator.getSourceViews(helper, null);
        FreeformProjectGenerator.putSourceViews(helper, folders, null);
        ProjectManager.getDefault().saveAllProjects();
        Element el = helper.getPrimaryConfigurationData(true);
        el = Util.findElement(el, "view", FreeformProjectType.NS_GENERAL);
        assertNotNull("View folders were not saved correctly",  el);
        el = Util.findElement(el, "items", FreeformProjectType.NS_GENERAL);
        assertNotNull("View folders were not saved correctly",  el);
        List subElements = Util.findSubElements(el);
        // there will be three sublements: <source-file> is added for build.xml during project.creation
        assertEquals(3, subElements.size());
        // compare first source view
        Element el2 = (Element)subElements.get(0);
        assertElement(el2, "source-folder", null, "style", "style1");
        List l1 = Util.findSubElements(el2);
        assertEquals(2, l1.size());
        assertElementArray(l1, 
            new String[]{"label", "location"}, 
            new String[]{"folder1", "location1"});
        // compare second source view
        el2 = (Element)subElements.get(1);
        assertElement(el2, "source-folder", null, "style", "style2");
        l1 = Util.findSubElements(el2);
        assertEquals(2, l1.size());
        assertElementArray(l1, 
            new String[]{"label", "location"}, 
            new String[]{"folder2", "location2"});
        // validate against schema:
        ProjectManager.getDefault().saveAllProjects();
        validate(p);
            
        // test rewriting of source view of some style
        
        folders = new ArrayList();
        sf = new FreeformProjectGenerator.SourceFolder();
        sf.label = "folder3";
        sf.style = "style2";
        sf.location = "location3";
        folders.add(sf);
        FreeformProjectGenerator.putSourceViews(helper, folders, "style2");
        ProjectManager.getDefault().saveAllProjects();
        el = helper.getPrimaryConfigurationData(true);
        el = Util.findElement(el, "view", FreeformProjectType.NS_GENERAL);
        assertNotNull("Source views were not saved correctly",  el);
        el = Util.findElement(el, "items", FreeformProjectType.NS_GENERAL);
        assertNotNull("View folders were not saved correctly",  el);
        subElements = Util.findSubElements(el);
        // there will be three sublements: <source-file> is added for build.xml during project.creation
        assertEquals(3, subElements.size());
        // compare first source view
        el2 = (Element)subElements.get(0);
        assertElement(el2, "source-folder", null, "style", "style1");
        l1 = Util.findSubElements(el2);
        assertEquals(2, l1.size());
        assertElementArray(l1, 
            new String[]{"label", "location"}, 
            new String[]{"folder1", "location1"});
        // compare second source view
        el2 = (Element)subElements.get(1);
        assertElement(el2, "source-folder", null, "style", "style2");
        l1 = Util.findSubElements(el2);
        assertEquals(2, l1.size());
        assertElementArray(l1, 
            new String[]{"label", "location"}, 
            new String[]{"folder3", "location3"});
        // validate against schema:
        ProjectManager.getDefault().saveAllProjects();
        validate(p);
    }

    public void testAuxiliaryConfiguration() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj4", "proj-4", true);
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
        assertNotNull("Project was not created", p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        
        AuxiliaryConfiguration au = FreeformProjectGenerator.getAuxiliaryConfiguration(helper);
        assertNotNull("Project does not have AuxiliaryConfiguration", au);
        Element el = au.getConfigurationFragment("java-data", FreeformProjectType.NS_JAVA, true);
        assertNotNull("Project does not have correct aux data", el);
    }

    public void testJavaCompilationUnits() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj5", "proj-5", true);
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
        assertNotNull("Project was not created", p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        
        ClassPathProvider cpp = (ClassPathProvider)p.getLookup().lookup(ClassPathProvider.class);
        assertNotNull("Project does not have ClassPathProvider", cpp);
        ClassPath cp = cpp.findClassPath(FileUtil.toFileObject(src), ClassPath.COMPILE);
        assertEquals("Project must have one classpath root", 1, cp.getRoots().length);
        assertEquals("Classpath root does not match", "jar:"+lib1.toURI().toURL()+"!/", (cp.getRoots()[0]).getURL().toExternalForm());
        cp = cpp.findClassPath(FileUtil.toFileObject(src).getParent(), ClassPath.COMPILE);
        assertEquals("There is no classpath for this file", null, cp);
        
        AuxiliaryConfiguration aux = FreeformProjectGenerator.getAuxiliaryConfiguration(helper);
        List cus = FreeformProjectGenerator.getJavaCompilationUnits(helper, aux);
        assertEquals("There must be one compilation unit", 1, cus.size());
        FreeformProjectGenerator.JavaCompilationUnit cu = (FreeformProjectGenerator.JavaCompilationUnit)cus.get(0);
        assertEquals("The compilation unit must have one classpath", 1, cu.classpath.size());
        
        FreeformProjectGenerator.JavaCompilationUnit.CP cucp = new FreeformProjectGenerator.JavaCompilationUnit.CP();
        cucp.classpath = lib2.getAbsolutePath();
        cucp.mode = "execute";
        cu.classpath.add(cucp);
        ArrayList outputs = new ArrayList();
        outputs.add("output1.jar");
        outputs.add("output2.jar");
        outputs.add("output3.jar");
        cu.output = outputs;
        FreeformProjectGenerator.putJavaCompilationUnits(helper, aux, cus);
        cus = FreeformProjectGenerator.getJavaCompilationUnits(helper, aux);
        assertEquals("There must be one compilation unit", 1, cus.size());
        cu = (FreeformProjectGenerator.JavaCompilationUnit)cus.get(0);
        assertEquals("The compilation unit must have one classpath", 2, cu.classpath.size());
        assertEquals("The compilation unit must have one classpath", 3, cu.output.size());
        
        cu = new FreeformProjectGenerator.JavaCompilationUnit();
        cu.sourceLevel = "1.4";
        cucp = new FreeformProjectGenerator.JavaCompilationUnit.CP();
        cucp.classpath = lib2.getAbsolutePath();
        cucp.mode = "compile";
        cu.classpath = Collections.singletonList(cucp);
        cu.packageRoots = Collections.singletonList(test.getAbsolutePath());
        cus.add(cu);
        FreeformProjectGenerator.putJavaCompilationUnits(helper, aux, cus);
        cus = FreeformProjectGenerator.getJavaCompilationUnits(helper, aux);
        assertEquals("There must be two compilation units", 2, cus.size());
        cp = cpp.findClassPath(FileUtil.toFileObject(src), ClassPath.COMPILE);
        assertEquals("Project must have one classpath root", 1, cp.getRoots().length);
        assertEquals("Classpath root does not match", "jar:"+lib1.toURI().toURL()+"!/", (cp.getRoots()[0]).getURL().toExternalForm());
        cp = cpp.findClassPath(FileUtil.toFileObject(src).getParent(), ClassPath.COMPILE);
        assertEquals("There is no classpath for this file", null, cp);
        cp = cpp.findClassPath(FileUtil.toFileObject(test), ClassPath.COMPILE);
        assertEquals("Project must have one classpath root", 1, cp.getRoots().length);
        assertEquals("Classpath root does not match", "jar:"+lib2.toURI().toURL()+"!/", (cp.getRoots()[0]).getURL().toExternalForm());
        
        ProjectManager.getDefault().saveAllProjects();
    }
    
    public void testRawJavaCompilationUnits() throws Exception {
        AntProjectHelper helper = createEmptyProject("proj", "proj", false);
        FileObject base = helper.getProjectDirectory();
        Project p = ProjectManager.getDefault().findProject(base);
        assertNotNull("Project was not created", p);
        assertEquals("Project folder is incorrect", base, p.getProjectDirectory());
        
        // check that all data are correctly persisted
        
        List units = new ArrayList();
        FreeformProjectGenerator.JavaCompilationUnit cu = new FreeformProjectGenerator.JavaCompilationUnit();
        cu.packageRoots = new ArrayList();
        cu.packageRoots.add("pkgroot1");
        cu.packageRoots.add("pkgroot2");
        cu.output = new ArrayList();
        cu.output.add("output1");
        cu.output.add("output2");
        cu.classpath = new ArrayList();
        FreeformProjectGenerator.JavaCompilationUnit.CP cp = new FreeformProjectGenerator.JavaCompilationUnit.CP();
        cp.mode = "compile";
        cp.classpath = "classpath1";
        cu.classpath.add(cp);
        cp = new FreeformProjectGenerator.JavaCompilationUnit.CP();
        cp.mode = "boot";
        cp.classpath = "classpath2";
        cu.classpath.add(cp);
        cu.sourceLevel = "1.3";
        units.add(cu);
        cu = new FreeformProjectGenerator.JavaCompilationUnit();
        cu.packageRoots = new ArrayList();
        cu.packageRoots.add("sec-pkgroot1");
        cu.packageRoots.add("sec-pkgroot2");
        cu.output = new ArrayList();
        cu.output.add("sec-output1");
        cu.output.add("sec-output2");
        cu.classpath = new ArrayList();
        cp = new FreeformProjectGenerator.JavaCompilationUnit.CP();
        cp.mode = "compile";
        cp.classpath = "sec-classpath1";
        cu.classpath.add(cp);
        cp = new FreeformProjectGenerator.JavaCompilationUnit.CP();
        cp.mode = "boot";
        cp.classpath = "sec-classpath2";
        cu.classpath.add(cp);
        cu.sourceLevel = "1.4";
        units.add(cu);
        AuxiliaryConfiguration aux = FreeformProjectGenerator.getAuxiliaryConfiguration(helper);
        FreeformProjectGenerator.putJavaCompilationUnits(helper, aux, units);
        // test getter and setter here:
        units = FreeformProjectGenerator.getJavaCompilationUnits(helper, aux);
        FreeformProjectGenerator.putJavaCompilationUnits(helper, aux, units);
//        ProjectManager.getDefault().saveAllProjects();
        Element el = aux.getConfigurationFragment("java-data", FreeformProjectType.NS_JAVA, true);
        assertNotNull("Java compilation units were not saved correctly",  el);
        List subElements = Util.findSubElements(el);
        assertEquals(2, subElements.size());
        // compare first compilation unit
        Element el2 = (Element)subElements.get(0);
        assertElement(el2, "compilation-unit", null);
        List l1 = Util.findSubElements(el2);
        assertEquals(7, l1.size());
        assertElementArray(l1, 
            new String[]{"package-root", "package-root", "classpath", "classpath", "built-to", "built-to", "source-level"}, 
            new String[]{"pkgroot1", "pkgroot2", "classpath1", "classpath2", "output1", "output2", "1.3"});
        el2 = (Element)l1.get(2);
        assertElement(el2, "classpath", "classpath1", "mode", "compile");
        el2 = (Element)l1.get(3);
        assertElement(el2, "classpath", "classpath2", "mode", "boot");
        // compare second compilation unit
        el2 = (Element)subElements.get(1);
        assertElement(el2, "compilation-unit", null);
        l1 = Util.findSubElements(el2);
        assertEquals(7, l1.size());
        assertElementArray(l1, 
            new String[]{"package-root", "package-root", "classpath", "classpath", "built-to", "built-to", "source-level"}, 
            new String[]{"sec-pkgroot1", "sec-pkgroot2", "sec-classpath1", "sec-classpath2", "sec-output1", "sec-output2", "1.4"});
        el2 = (Element)l1.get(2);
        assertElement(el2, "classpath", "sec-classpath1", "mode", "compile");
        el2 = (Element)l1.get(3);
        assertElement(el2, "classpath", "sec-classpath2", "mode", "boot");
        // validate against schema:
        ProjectManager.getDefault().saveAllProjects();
        validate(p);
            
        // test updating
            
        units = new ArrayList();
        cu = new FreeformProjectGenerator.JavaCompilationUnit();
        cu.packageRoots = new ArrayList();
        cu.packageRoots.add("foo-package-root");
        units.add(cu);
        FreeformProjectGenerator.putJavaCompilationUnits(helper, aux, units);
//        ProjectManager.getDefault().saveAllProjects();
        el = aux.getConfigurationFragment("java-data", FreeformProjectType.NS_JAVA, true);
        assertNotNull("Java compilation units were not saved correctly",  el);
        subElements = Util.findSubElements(el);
        assertEquals(1, subElements.size());
        // compare first compilation unit
        el2 = (Element)subElements.get(0);
        assertElement(el2, "compilation-unit", null);
        l1 = Util.findSubElements(el2);
        assertEquals(1, l1.size());
        assertElementArray(l1, 
            new String[]{"package-root"}, 
            new String[]{"foo-package-root"});
        // validate against schema:
        ProjectManager.getDefault().saveAllProjects();
        validate(p);
    }

    
    private static class Listener implements ChangeListener {
        int count = 0;
        public void stateChanged(ChangeEvent ev) {
            count++;
        }
        public void reset() {
            count = 0;
        }
    }

    // create real Jar otherwise FileUtil.isArchiveFile returns false for it
    public void createRealJarFile(File f) throws Exception {
        OutputStream os = new FileOutputStream(f);
        try {
            JarOutputStream jos = new JarOutputStream(os);
//            jos.setMethod(ZipEntry.STORED);
            JarEntry entry = new JarEntry("foo.txt");
//            entry.setSize(0L);
//            entry.setTime(System.currentTimeMillis());
//            entry.setCrc(new CRC32().getValue());
            jos.putNextEntry(entry);
            jos.flush();
            jos.close();
        } finally {
            os.close();
        }
    }

    private static String[] getSchemas() throws Exception {
        String[] URIs = new String[4];
        URIs[0] = FreeformProjectGenerator.class.getResource("resources/freeform-project-general.xsd").toExternalForm();
        URIs[1] = FreeformProjectGenerator.class.getResource("resources/freeform-project-java.xsd").toExternalForm();
        URIs[2] = FreeformProjectGenerator.class.getResource("resources/freeform-project-web.xsd").toExternalForm();
        URIs[3] = AntBasedProjectFactorySingleton.class.getResource("project.xsd").toExternalForm();
        return URIs;
    }
    
    public static void validate(Project proj) throws Exception {
        File projF = FileUtil.toFile(proj.getProjectDirectory());
        File xml = new File(new File(projF, "nbproject"), "project.xml");
        SAXParserFactory f = (SAXParserFactory)Class.forName("org.apache.xerces.jaxp.SAXParserFactoryImpl").newInstance();
        if (f == null) {
            System.err.println("Validation skipped because org.apache.xerces.jaxp.SAXParserFactoryImpl was not found on classpath");
            return;
        }
        f.setNamespaceAware(true);
        f.setValidating(true);
        SAXParser p = f.newSAXParser();
        p.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
            "http://www.w3.org/2001/XMLSchema");
        p.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", getSchemas());
        try {
            p.parse(xml.toURI().toString(), new Handler());
        } catch (SAXParseException e) {
            assertTrue("Validation of XML document "+xml+" against schema failed. Details: "+
            e.getSystemId() + ":" + e.getLineNumber() + ": " + e.getLocalizedMessage(), false);
        }
    }
    
    private static final class Handler extends DefaultHandler {
        public void warning(SAXParseException e) throws SAXException {
            throw e;
        }
        public void error(SAXParseException e) throws SAXException {
            throw e;
        }
        public void fatalError(SAXParseException e) throws SAXException {
            throw e;
        }
    }

}
