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


package org.netbeans.modules.uml.core.metamodel.core.foundation;
import org.netbeans.modules.uml.core.support.umlsupport.IResultCell;

public interface IExternalElementEventsSink
{
	/**
	 * Fired whenever an element is about to be loaded from an .etx file.
	*/
	public void onExternalElementPreLoaded( String uri, IResultCell cell );

	/**
	 * Fired whenever an element was loaded from an etx file.
	*/
	public void onExternalElementLoaded( IVersionableElement element, IResultCell cell );

	/**
	 * Fired whenever an element is about to be extracted from the current project and placed into an .etx file.
	*/
	public void onPreInitialExtraction( String fileName, IVersionableElement element, IResultCell cell );

	/**
	 * Fired whenever an element has been extracted to a .etx file.
	*/
	public void onInitialExtraction( IVersionableElement element, IResultCell cell );

}
