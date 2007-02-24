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

import org.dom4j.Document;
import org.dom4j.Node;

/**
 * @author sumitabhk
 *
 */
public class LiteralString extends LiteralSpecification implements ILiteralString
{

	/**
	 *
	 */
	public LiteralString()
	{
		super();
	}

	/**
	 *
	 * Sets the value on this literal
	 *
	 * @param newVal[in] The new value
	 *
	 * @return HRESULT
	 *
	 */
	public void setValue(String value)
	{
		setAttributeValue("value", value);
	}

	/**
	 *
	 * Retrieves the current value on this literal
	 *
	 * @param pVal[in] The current value
	 *
	 * @return HRESULT
	 *
	 */
	public String getValue()
	{
		return getAttributeValue("value");
	}

	/**
	 * Establishes the appropriate XML elements for this UML type.
	 *
	 * [in] The document where this element will reside
	 * [in] The element's parent node.
	 *
	 * @return HRESULT
	 */
	public void establishNodePresence(Document doc, Node parent)
	{
		buildNodePresence( "UML:LiteralString", doc, parent );
	}

}



