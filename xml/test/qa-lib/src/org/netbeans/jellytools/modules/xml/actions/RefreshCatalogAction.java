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
package org.netbeans.jellytools.modules.xml.actions;

import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.actions.ActionNoBlock;

/** RefreshCatalogAction class 
 * @author <a href="mailto:mschovanek@netbeans.org">Martin Schovanek</a> */
public class RefreshCatalogAction extends ActionNoBlock {

    private static final String popup = 
    Bundle.getStringTrimmed("org.netbeans.modules.xml.catalog.Bundle", "LBL_Action");

    /** creates new RefreshCatalogAction instance */    
    public RefreshCatalogAction() {
        super(null, popup, "org.netbeans.modules.xml.catalog.RefreshAction");
    }
}