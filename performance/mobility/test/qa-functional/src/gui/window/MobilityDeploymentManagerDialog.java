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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package gui.window;

import org.netbeans.jellytools.actions.ActionNoBlock;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JDialogOperator;

/**
 *
 * @author mkhramov@netbeans.org
 */
public class MobilityDeploymentManagerDialog extends org.netbeans.performance.test.utilities.PerformanceTestCase {
    private JDialogOperator manager;
    private String cmdName;
    /**
     * 
     * @param testName 
     */
    public MobilityDeploymentManagerDialog(String testName) {
        super(testName);
        expectedTime = WINDOW_OPEN;        
    }
    /**
     * 
     * @param testName
     * @param performanceDataName 
     */
    public MobilityDeploymentManagerDialog(String testName, String performanceDataName) {
        super(testName,performanceDataName);
        expectedTime = WINDOW_OPEN;          
    }
    public void initialize() {
        log(":: initialize");
        cmdName = org.netbeans.jellytools.Bundle.getStringTrimmed("org.netbeans.modules.mobility.project.deployment.Bundle", "Title_DeploymentManager");
    }
    
    public void prepare() {
        log(":: prepare");
    }

    public ComponentOperator open() {
        log(":: open");
        new ActionNoBlock("Tools|"+cmdName,null).performMenu();
        manager = new JDialogOperator(cmdName);
        return null;
    }
    
    public void close() {
        log(":: close");
        manager.close();
        
    }    
    public void shutdown() {
        log(":: shutdown");
    }

}
