/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.jellytools.modules.xsl.actions;

import java.awt.event.KeyEvent;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.actions.ActionNoBlock;

/** EditScenariosAction class
 * @author <a href="mailto:mschovanek@netbeans.org">Martin Schovanek</a> */
public class EditScenariosAction extends ActionNoBlock {
    
    private static final String popup =
    Bundle.getStringTrimmed("org.netbeans.modules.xsl.actions.Bundle", "LBL_Scenario_Action");
    
    private static final Shortcut shortcut =
    new Shortcut(KeyEvent.VK_S, KeyEvent.ALT_MASK);
    
    /** creates new EditScenariosAction instance */
    public EditScenariosAction() {
        super(null, popup, "org.netbeans.modules.xsl.actions.ScenarioAction", shortcut);
    }
}
