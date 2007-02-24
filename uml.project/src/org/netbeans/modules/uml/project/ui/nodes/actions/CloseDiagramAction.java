/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

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

/*
 * CloseDiagramAction.java
 *
 * Created on January 25, 2005, 2:30 PM
 */

package org.netbeans.modules.uml.project.ui.nodes.actions;

import javax.swing.SwingUtilities;
import org.openide.cookies.CloseCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

/**
 *
 * @author  Craig Conover
 */
public class CloseDiagramAction extends CookieAction
{
	
	/**
	 * Creates a new instance of CloseDiagramAction
	 */
	public CloseDiagramAction()
	{
	}
	
	protected Class[] cookieClasses()
	{
		return new Class[] {CloseCookie.class};
	}
	
	public HelpCtx getHelpCtx()
	{
		return null;
	}
	
	public String getName()
	{
		return (String)NbBundle.getBundle(CloseDiagramAction.class)
		.getString("CloseDiagramAction_Name");
	}
	
	protected int mode()
	{
		return CookieAction.MODE_ANY;
	}
	
	protected void performAction(Node[] nodes)
	{
		for (Node curNode : nodes)
		{
			final CloseCookie cookie = 
					(CloseCookie)curNode.getCookie(CloseCookie.class);
			
			if (cookie != null)
			{
				Runnable runnable = new Runnable() 
				{
					public void run() 
					{
						cookie.close();
					}
				};

				SwingUtilities.invokeLater(runnable);
			}
		}
	}
	
}
