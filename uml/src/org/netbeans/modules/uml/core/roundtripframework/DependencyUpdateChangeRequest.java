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
 * File       : DependencyUpdateChangeRequest.java
 * Created on : Nov 20, 2003
 * Author     : Aztec
 */
package org.netbeans.modules.uml.core.roundtripframework;

/**
 * @author Aztec
 */
public class DependencyUpdateChangeRequest
    extends DependencyChangeRequest
    implements IDependencyUpdateChangeRequest
{
    private String m_OldIndependentElementName;

    /* (non-Javadoc)
     * @see org.netbeans.modules.uml.core.roundtripframework.IDependencyUpdateChangeRequest#getOldIndependentElementName()
     */
    public String getOldIndependentElementName()
    {
        return m_OldIndependentElementName;
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.uml.core.roundtripframework.IDependencyUpdateChangeRequest#setOldIndependentElementName(java.lang.String)
     */
    public void setOldIndependentElementName(String newVal)
    {
        m_OldIndependentElementName = newVal;
    }

}
