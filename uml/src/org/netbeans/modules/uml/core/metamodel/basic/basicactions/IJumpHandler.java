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


package org.netbeans.modules.uml.core.metamodel.basic.basicactions;

import org.netbeans.modules.uml.core.metamodel.core.foundation.IElement;
import org.netbeans.modules.uml.core.metamodel.infrastructure.coreinfrastructure.ISignal;
import org.netbeans.modules.uml.core.support.umlutils.ETList;

public interface IJumpHandler extends IElement
{
	/**
	 * property JumpType
	*/
	public ISignal getJumpType();

	/**
	 * property JumpType
	*/
	public void setJumpType( ISignal value );

	/**
	 * method AddProtectedAction
	*/
	public void addProtectedAction( IAction pAction );

	/**
	 * method RemoveProtectedAction
	*/
	public void removeProtectedAction( IAction pAction );

	/**
	 * property ProtectedActions
	*/
	public ETList <IAction> getProtectedActions();

	/**
	 * property Body
	*/
	public IHandlerAction getBody();

	/**
	 * property Body
	*/
	public void setBody( IHandlerAction value );

	/**
	 * property IsDefault
	*/
	public boolean getIsDefault();

	/**
	 * property IsDefault
	*/
	public void setIsDefault( boolean value );

}
