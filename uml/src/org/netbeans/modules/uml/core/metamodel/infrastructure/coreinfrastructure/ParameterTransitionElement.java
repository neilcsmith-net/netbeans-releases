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


package org.netbeans.modules.uml.core.metamodel.infrastructure.coreinfrastructure;

import org.netbeans.modules.uml.core.metamodel.core.foundation.IElement;
import org.netbeans.modules.uml.core.metamodel.core.foundation.ITransitionElement;
import org.netbeans.modules.uml.core.metamodel.core.foundation.TransitionElement;


public class ParameterTransitionElement extends Parameter implements
        IParameter, ITransitionElement
{
	private ITransitionElement m_TransitionElement = null;

	public ParameterTransitionElement()
	{
		m_TransitionElement = new TransitionElement();
	}

    /* (non-Javadoc)
     * @see org.netbeans.modules.uml.core.metamodel.core.foundation.ITransitionElement#getFutureOwner()
     */
    public IElement getFutureOwner()
    {
        return m_TransitionElement.getFutureOwner();
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.uml.core.metamodel.core.foundation.ITransitionElement#setFutureOwner(org.netbeans.modules.uml.core.metamodel.core.foundation.IElement)
     */
    public void setFutureOwner(IElement value)
    {
		m_TransitionElement.setFutureOwner(value);
    }
}
