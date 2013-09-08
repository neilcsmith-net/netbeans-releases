/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
package org.netbeans.performance.j2ee.actions;

import junit.framework.Test;
import org.netbeans.modules.performance.utilities.PerformanceTestCase;
import org.netbeans.performance.j2ee.setup.J2EESetup;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.actions.SaveAllAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.nodes.ProjectRootNode;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.operators.Operator;

/**
 * Test of opening files.
 *
 * @author lmartinek@netbeans.org
 */
public class OpenJ2EEFilesTest extends PerformanceTestCase {

    /**
     * Node to be opened/edited
     */
    public static Node openNode;

    /**
     * Folder with data
     */
    public static String fileProject;

    /**
     * Folder with data
     */
    public static String filePath;

    /**
     * Name of file to open
     */
    public static String editorTitle;

    /**
     * Menu item name that opens the editor
     */
    public static String menuItem;

    protected static String OPEN = "Open";

    /**
     * Creates a new instance of OpenFiles
     *
     * @param testName the name of the test
     */
    public OpenJ2EEFilesTest(String testName) {
        super(testName);
        expectedTime = WINDOW_OPEN;
    }

    /**
     * Creates a new instance of OpenFiles
     *
     * @param testName the name of the test
     * @param performanceDataName measured values will be saved under this name
     */
    public OpenJ2EEFilesTest(String testName, String performanceDataName) {
        super(testName, performanceDataName);
        expectedTime = WINDOW_OPEN;
    }

    public static Test suite() {
        return emptyConfiguration().addTest(J2EESetup.class).addTest(OpenJ2EEFilesTest.class).suite();
    }

    public void testOpeningJava() {
        WAIT_AFTER_OPEN = 1000;
        fileProject = "TestApplication-ejb";
        filePath = "Source Packages|test|TestSessionRemote.java";
        editorTitle = "TestSessionRemote.java";
        menuItem = OPEN;
        repaintManager().addRegionFilter(repaintManager().EDITOR_FILTER);
        doMeasurement();
    }

    public void testOpeningSessionBean() {
        WAIT_AFTER_OPEN = 1000;
        fileProject = "TestApplication-ejb";
        filePath = "Enterprise Beans|TestSessionSB";
        editorTitle = "TestSessionBean.java";
        menuItem = OPEN;
        repaintManager().addRegionFilter(repaintManager().EDITOR_FILTER);
        doMeasurement();
    }

    public void testOpeningEntityBean() {
        WAIT_AFTER_OPEN = 1000;
        fileProject = "TestApplication-ejb";
        filePath = "Enterprise Beans|TestEntityEB";
        editorTitle = "TestEntityBean.java";
        menuItem = OPEN;
        repaintManager().addRegionFilter(repaintManager().EDITOR_FILTER);
        doMeasurement();
    }

    public void testOpeningEjbJarXml() {
        WAIT_AFTER_OPEN = 1000;
        fileProject = "TestApplication-ejb";
        filePath = "Configuration Files|ejb-jar.xml";
        editorTitle = "ejb-jar.xml";
        menuItem = OPEN;
        repaintManager().resetRegionFilters();
        doMeasurement();
    }

    public void testOpeningSunEjbJarXml() {
        WAIT_AFTER_OPEN = 1000;
        fileProject = "TestApplication-ejb";
        filePath = "Configuration Files|sun-ejb-jar.xml";
        editorTitle = "sun-ejb-jar.xml";
        menuItem = OPEN;
        repaintManager().resetRegionFilters();
        doMeasurement();
    }

    public void testOpeningApplicationXml() {
        WAIT_AFTER_OPEN = 1000;
        fileProject = "TestApplication";
        filePath = "Configuration Files|application.xml";
        editorTitle = "application.xml";
        menuItem = "Edit";
        repaintManager().addRegionFilter(repaintManager().EDITOR_FILTER);
        doMeasurement();
    }

    public void testOpeningSunApplicationXml() {
        WAIT_AFTER_OPEN = 1000;
        fileProject = "TestApplication";
        filePath = "Configuration Files|sun-application.xml";
        editorTitle = "sun-application.xml";
        menuItem = OPEN;
        repaintManager().resetRegionFilters();
        doMeasurement();
    }

    @Override
    public void initialize() {
        EditorOperator.closeDiscardAll();
        addEditorPhaseHandler();
        disableEditorCaretBlinking();
    }

    @Override
    public void shutdown() {
        repaintManager().resetRegionFilters();
        EditorOperator.closeDiscardAll();
        removeEditorPhaseHandler();
    }

    public void prepare() {
        JTreeOperator tree = new ProjectsTabOperator().tree();
        tree.setComparator(new Operator.DefaultStringComparator(true, true));
        openNode = new Node(new ProjectRootNode(tree, fileProject), filePath);

        if (openNode == null) {
            throw new Error("Cannot find node [" + filePath + "] in project [" + fileProject + "]");
        }
        log("========== Open file path =" + openNode.getPath());
    }

    public ComponentOperator open() {
        JPopupMenuOperator popup = openNode.callPopup();
        if (popup == null) {
            throw new Error("Cannot get context menu for node [" + filePath + "] in project [" + fileProject + "]");
        }
        log("------------------------- after popup invocation ------------");
        try {
            popup.pushMenu(menuItem);
        } catch (org.netbeans.jemmy.TimeoutExpiredException tee) {
            throw new Error("Cannot push menu item " + menuItem + " of node [" + filePath + "] in project [" + fileProject + "]");
        }
        log("------------------------- after open ------------");
        return new TopComponentOperator(editorTitle);
    }

    @Override
    public void close() {
        if (testedComponentOperator != null) {
            // HACK
            new SaveAllAction().performAPI();
            ((TopComponentOperator) testedComponentOperator).close();
        } else {
            throw new Error("no component to close");
        }
    }
}
