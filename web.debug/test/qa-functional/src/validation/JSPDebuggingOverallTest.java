/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package validation;

import java.io.File;
import junit.textui.TestRunner;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jellytools.OutputTabOperator;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.QuestionDialogOperator;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jellytools.actions.ActionNoBlock;
import org.netbeans.jellytools.actions.DebugProjectAction;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.modules.debugger.AttachDialogOperator;
import org.netbeans.jellytools.modules.debugger.SessionsOperator;
import org.netbeans.jellytools.modules.debugger.actions.ContinueAction;
import org.netbeans.jellytools.modules.debugger.actions.DebugAction;
import org.netbeans.jellytools.modules.j2ee.actions.RefreshAction;
import org.netbeans.jellytools.modules.j2ee.actions.RestartAction;
import org.netbeans.jellytools.modules.j2ee.actions.StartAction;
import org.netbeans.jellytools.modules.j2ee.actions.StartDebugAction;
import org.netbeans.jellytools.modules.j2ee.actions.StopAction;
import org.netbeans.jellytools.modules.j2ee.nodes.J2eeServerNode;
import org.netbeans.jellytools.modules.web.nodes.WebPagesNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.nodes.SourcePackagesNode;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Waitable;
import org.netbeans.jemmy.Waiter;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.junit.ide.ProjectSupport;

/** Test of web application debugging. Manual test specification is here:
 * http://qa.netbeans.org/webapps/promo-f/jspdebug/jspdebug-testspec.html
 * <br>
 * !!! Be careful when using internal swing html browser. It posts http requests
 * three times. That's why is probably better to finish debugging each time you
 * went through page.
 *
 * @author Jiri.Skrivanek@sun.com
 */
public class JSPDebuggingOverallTest extends JellyTestCase {
    // status bar tracer used to wait for state
    private MainWindowOperator.StatusTextTracer stt;
    
    public JSPDebuggingOverallTest(String testName) {
        super(testName);
    }
    
    public static void main(String[] args) {
        TestRunner.run(suite());
    }
    
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTest(new JSPDebuggingOverallTest("testOpenProjects"));
        suite.addTest(new JSPDebuggingOverallTest("testSetSwingBrowser"));
        suite.addTest(new JSPDebuggingOverallTest("testSetTomcatPort"));
        suite.addTest(new JSPDebuggingOverallTest("testRunProject"));
        suite.addTest(new JSPDebuggingOverallTest("testDebugProject"));
        suite.addTest(new JSPDebuggingOverallTest("testSetBreakpoint"));
        suite.addTest(new JSPDebuggingOverallTest("testDebugReload"));
        suite.addTest(new JSPDebuggingOverallTest("testAttachDebugger"));
        suite.addTest(new JSPDebuggingOverallTest("testDebugAfterBreakpoint"));
        suite.addTest(new JSPDebuggingOverallTest("testDebugAndStopServer"));
        suite.addTest(new JSPDebuggingOverallTest("testStartAnotherSession"));
        suite.addTest(new JSPDebuggingOverallTest("testJavaSession"));
        return suite;
    }
    
    /** Print test name and initialize status bar tracer. */
    public void setUp() {
        System.out.println("########  "+getName()+"  #######");
        stt = MainWindowOperator.getDefault().getStatusTextTracer();
        // start to track Main Window status bar
        stt.start();
        // increase timeout to 60 seconds when waiting for status bar text
        MainWindowOperator.getDefault().getTimeouts().setTimeout("Waiter.WaitingTime", 60000);
    }
    
    /** Stops status bar tracer. */
    public void tearDown() {
        stt.stop();
    }
    
    // name of sample web application project
    private static final String SAMPLE_WEB_PROJECT_NAME = "MainTestApplication";  //NOI18N
    
    
    /** Opens test projects. */
    public void testOpenProjects() {
        String[] projects = {"MainTestApplication", "TestFreeformLibrary", "TestLibrary", "TestTagLibrary"}; //NOI18N
        for(int i=0;i<projects.length;i++) {
            ProjectSupport.openProject(new File(getDataDir(), projects[i]));
        }
        // Set Main Project
        String setMainItem = Bundle.getString("org.netbeans.modules.project.ui.actions.Bundle", "LBL_SetMainProjectAction_Name");
        new Action(null, setMainItem).perform(new ProjectsTabOperator().getProjectRootNode(SAMPLE_WEB_PROJECT_NAME));
    }
    
    /** Set Swing HTML Browser as default browser. */
    public void testSetSwingBrowser() {
        Utils.setSwingBrowser();
    }
    
    /** Set a random port for Tomcat server and socket debugger transport. */
    public void testSetTomcatPort() throws Exception {
        Utils.setTomcatProperties();
    }
    
    /** Run project. */
    public void testRunProject() {
        String runProjectItem = Bundle.getString("org.netbeans.modules.web.project.ui.Bundle", "LBL_RunAction_Name");
        new Action(null, runProjectItem).perform(new ProjectsTabOperator().getProjectRootNode(SAMPLE_WEB_PROJECT_NAME));
        // wait until page is displayed in internal browser
        long oldTimeout = JemmyProperties.getCurrentTimeout("ComponentOperator.WaitComponentTimeout");
        try {
            // increase time to wait to 120 second (it fails on Linux)
            JemmyProperties.setCurrentTimeout("ComponentOperator.WaitComponentTimeout", 120000);
            new TopComponentOperator("Test JSP Page").close(); // NOI18N
        } finally {
            // restore default timeout
            JemmyProperties.setCurrentTimeout("ComponentOperator.WaitComponentTimeout", oldTimeout);
            // log messages from output
            getLog("TomcatMessages0").print(new OutputTabOperator("Bundled Tomcat", 0).getText()); // NOI18N
            getLog("TomcatMessages1").print(new OutputTabOperator("Bundled Tomcat", 1).getText()); // NOI18N
            getLog("RunOutput").print(new OutputTabOperator(SAMPLE_WEB_PROJECT_NAME).getText()); // NOI18N
        }
    }

    /** Debug project.
     * - on project node call Debug Project popup
     * - wait until page appears in browser
     */
    public void testDebugProject() {
        Node rootNode = new ProjectsTabOperator().getProjectRootNode(SAMPLE_WEB_PROJECT_NAME);
        new DebugProjectAction().perform(rootNode);
        TopComponentOperator browserOper = new TopComponentOperator("Test JSP Page"); // NOI18N
    }

    /** Set breakpoint.
     * - open index.jsp
     * - select <h1> in editor
     * - toggle breakpoint at selected line
     */
    public void testSetBreakpoint() throws Exception {
        new OpenAction().performAPI(new Node(new WebPagesNode(SAMPLE_WEB_PROJECT_NAME), "index.jsp")); // NOI18N
        // find sample file in Editor
        EditorOperator eo = new EditorOperator("index.jsp"); // NOI18N
        Utils.setBreakpoint(eo, "<h1>"); // NOI18N
    }
    
    /** Reload browser while debugging.
     * - reload page in browser
     * - wait until debugger stops at previously set breakpoint
     * - continue debugging
     * - finish debugger 
     */
    public void testDebugReload() {
        TopComponentOperator browserOper = new TopComponentOperator("Test JSP Page"); //NOI18N
        // "Reload"
        String reloadTooltip = Bundle.getStringTrimmed("org.openide.awt.Bundle", "CTL_Reload");
        new JButtonOperator(browserOper, new Utils.ToolTipChooser(reloadTooltip)).push();
        // check breakpoint reached
        // wait status text "Thread main stopped at SampleClass1.java:##"
        EditorOperator eo = new EditorOperator("index.jsp"); // NOI18N
        int line = eo.getLineNumber();
        stt.waitText("index.jsp:"+line);
        new ContinueAction().perform();
        Utils.finishDebugger();
    }

    /** Attach debugger.
     * - call Run|Attach Debugger... main menu item
     * - in Attach dialog set socket attach, port and click OK
     * - wait User program running appears in status bar
     * - reload page in browser
     * - check breakpoint reached
     * - finish debugger
     */
    public void testAttachDebugger() {
        // assuming server is running in debug mode and page is opened in browser
        AttachDialogOperator ado = AttachDialogOperator.invoke();
        ado.selectConnector(ado.ITEM_SOCKET_ATTACH);
        ado.setPort(Utils.getSocketPort()); // NOI18N
        ado.ok();
        // "User program running"
        String runningLabel = Bundle.getString("org.netbeans.modules.debugger.jpda.ui.Bundle", "CTL_Debugger_running");
        stt.waitText(runningLabel);
        TopComponentOperator browserOper = new TopComponentOperator("Test JSP Page"); //NOI18N
        // "Reload"
        String reloadTooltip = Bundle.getStringTrimmed("org.openide.awt.Bundle", "CTL_Reload");
        new JButtonOperator(browserOper, new Utils.ToolTipChooser(reloadTooltip)).push();
        // check breakpoint reached
        // wait status text "Thread main stopped at SampleClass1.java:##"
        EditorOperator eo = new EditorOperator("index.jsp"); // NOI18N
        int line = eo.getLineNumber();
        stt.waitText("index.jsp:"+line);
        Utils.finishDebugger();
    }

    /** Restart debugger after breakpoint reached.
     * - start to debug main project from main menu
     * - wait until debugger stops at previously set breakpoint
     * - finish debugger
     * - start debugger again
     * - wait until debugger stops at breakpoint
     * - finish debugger
     */
    public void testDebugAfterBreakpoint() {
        // start debugging
        new DebugProjectAction().perform();
        // check the first breakpoint reached
        // wait status text "Thread main stopped at index.jsp:##"
        EditorOperator eo = new EditorOperator("index.jsp"); // NOI18N
        int line = eo.getLineNumber();
        stt.waitText("index.jsp:"+line);
        Utils.finishDebugger();
        // start debugger again
        new DebugProjectAction().perform();
        stt.waitText("index.jsp:"+line); // NOI18N
        Utils.finishDebugger();
    }

    /** Restart debugger after server stopped.
     * - start to debug main project from main menu
     * - wait until debugger stops at previously set breakpoint
     * - check it is not possible to stop server
     * - finish debugger
     * - stop server
     * - start debugger again
     * - wait until debugger stops at breakpoint
     * - finish debugger
     */
    public void testDebugAndStopServer() {
        // start debugging
        new DebugProjectAction().perform();
        // check the first breakpoint reached
        // wait status text "Thread main stopped at index.jsp:##"
        EditorOperator eo = new EditorOperator("index.jsp"); // NOI18N
        int line = eo.getLineNumber();
        stt.waitText("index.jsp:"+line);
        
        // check it is not possible to stop server
        // "Bundled Tomcat (x.y.z)"
        String tomcatLabel = Bundle.getStringTrimmed("org.netbeans.modules.tomcat5.Bundle", "LBL_BundledTomcat");
        J2eeServerNode serverNode = new J2eeServerNode(tomcatLabel);
        assertFalse("Start action on server node should be disabled when stopped at breakpoint.", new StartAction().isEnabled(serverNode));
        assertFalse("Stop action on server node should be disabled when stopped at breakpoint.", new StopAction().isEnabled(serverNode));
        assertFalse("Restart action on server node should be disabled when stopped at breakpoint.", new RestartAction().isEnabled(serverNode));
        assertFalse("Start in Debug Mode action on server node should be disabled when stopped at breakpoint.", new StartDebugAction().isEnabled(serverNode));
        assertTrue("Refresh action on server node should be enabled when stopped at breakpoint.", new RefreshAction().isEnabled(serverNode));

        Utils.finishDebugger();
        serverNode.stop();
        // start debugger again
        new DebugProjectAction().perform();
        stt.waitText("index.jsp:"+line);
        Utils.finishDebugger();
    }
    
    /** Start another session.
     * - start to debug main project from main menu
     * - wait until debugger stops at previously set breakpoint
     * - try to start debugger again
     * - cancel dialog with warning message
     * - try to start debugger again
     * - click OK in dialog with warning message
     * - wait until debugger stops at breakpoint
     * - try to run project
     * - cancel dialog with warning message
     * - try to run project again
     * - click OK in dialog with warning message
     * - wait until target run finishes
     * - wait for page in browser and close it
     */
    public void testStartAnotherSession() {
        // start debugging
        new DebugProjectAction().perform();
        // check the first breakpoint reached
        // wait status text "Thread main stopped at index.jsp:##"
        EditorOperator eo = new EditorOperator("index.jsp"); // NOI18N
        int line = eo.getLineNumber();
        stt.waitText("index.jsp:"+line);
        stt.clear();
        
        ActionNoBlock debugNoBlockAction = new ActionNoBlock(new DebugProjectAction().getMenuPath(), null);
        debugNoBlockAction.perform();
        new QuestionDialogOperator().cancel();
        debugNoBlockAction.perform();
        new QuestionDialogOperator().ok();
        
        stt.waitText("index.jsp:"+line); // NOI18N
        
        String runProjectItem = Bundle.getString("org.netbeans.modules.web.project.ui.Bundle", "LBL_RunAction_Name");
        ActionNoBlock runProjectNoBlockAction = new ActionNoBlock(null, runProjectItem);
        runProjectNoBlockAction.perform(new ProjectsTabOperator().getProjectRootNode(SAMPLE_WEB_PROJECT_NAME));
        new QuestionDialogOperator().cancel();
        runProjectNoBlockAction.perform(new ProjectsTabOperator().getProjectRootNode(SAMPLE_WEB_PROJECT_NAME));
        new QuestionDialogOperator().ok();
        
        // "SampleWebProject (run)"
        String outputTarget = Bundle.getString(
                "org.apache.tools.ant.module.run.Bundle", "TITLE_output_target", 
                new Object[] {SAMPLE_WEB_PROJECT_NAME, null, "run"});  // NOI18N
        // "Finished building SampleWebProject (run)"
        String finishedMessage = Bundle.getString(
                "org.apache.tools.ant.module.run.Bundle", "FMT_finished_target_status", 
                new Object[] {outputTarget});
        stt.waitText(finishedMessage);
        new TopComponentOperator("Test JSP Page").close(); // NOI18N
    }
    
    /** Test concurrent java and jsp debugging sessions. Also test debugging
     * of jsp in sub folder.
     * - open main class MyBean.java
     * - set breakpoint in it
     * - start debugger from popup on MyBean node
     * - wait until debugger stops at breakpoint
     * - open page simpleInclude.jsp in incl folder
     * - set breakpoint in it
     * - start debugger from popup on simpleInclude.jsp node
     * - wait until debugger stops at breakpoint
     * - open Window|Debugging|Session view
     * - call Make Current popup on MyBean debugging session
     * - wait until pointer is on breakpoint in MyBean.java
     * - call Make Current popup on simpleInclude.jsp debugging session
     * - wait until pointer is on breakpoint in simpleInclude.jsp
     * - call Finish All popup in Sessions view
     * - wait until debugger is finished
     * - close Sessions view
     */
    public void testJavaSession() throws Exception {
        Node beanNode = new Node(new SourcePackagesNode(SAMPLE_WEB_PROJECT_NAME), "org.netbeans.test|MyBean.java"); //NOI18N
        new OpenAction().performAPI(beanNode); // NOI18N
        EditorOperator eoBean = new EditorOperator("MyBean.java"); // NOI18N
        int line = Utils.setBreakpoint(eoBean, "System.out.println"); // NOI18N
        new DebugAction().perform(beanNode);
        stt.waitText("MyBean.java:"+line); //NOI18N

        Node pageNode = new Node(new WebPagesNode(SAMPLE_WEB_PROJECT_NAME), "incl|simpleInclude.jsp"); //NOI18N
        new OpenAction().performAPI(pageNode);
        EditorOperator eoPage = new EditorOperator("simpleInclude.jsp"); // NOI18N
        line = Utils.setBreakpoint(eoPage, "incl/simpleInclude.jsp"); // NOI18N
        new DebugAction().perform(pageNode);
        stt.waitText("simpleInclude.jsp:"+line); //NOI18N
        
        SessionsOperator so = SessionsOperator.invoke();
        so.makeCurrent("MyBean"); //NOI18N
        // wait pointer in editor (two annotations there)
        new Waiter(new Waitable() {
            public Object actionProduced(Object editorOper) {
                return ((EditorOperator)editorOper).getAnnotations().length == 2 ? Boolean.TRUE : null;
            }
            public String getDescription() {
                return("Wait 2 annotations in editor."); // NOI18N
            }
        }).waitAction(eoBean);
        // when issue 52506 fixed use proper name
        so.makeCurrent("name");
        // wait pointer in editor (two annotations there)
        new Waiter(new Waitable() {
            public Object actionProduced(Object editorOper) {
                return ((EditorOperator)editorOper).getAnnotations().length == 2 ? Boolean.TRUE : null;
            }
            public String getDescription() {
                return("Wait 2 annotations in editor."); // NOI18N
            }
        }).waitAction(eoPage);
        ContainerOperator debugToolbarOper = Utils.getDebugToolbar();
        so.finishAll();
        // wait until Debug toolbar dismiss
        debugToolbarOper.waitComponentVisible(false);
        so.close();
    }
}
