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


package org.netbeans.modules.uml.core.reverseengineering.parsingfacilities;

import org.netbeans.modules.uml.core.metamodel.core.foundation.IElement;
import org.netbeans.modules.uml.core.reverseengineering.reframework.IREOperation;
import org.netbeans.modules.uml.core.support.umlutils.ETList;

/**
 */
public interface IOpParserOptions
{
    public boolean initialize(ETList<IElement> pElements,
                           boolean bProcessTest,
                           boolean bProcessInit,
                           boolean bProcessPost);
    public void setProcessTest(boolean pVal);
    public boolean isProcessTest();
    public void setProcessInit(boolean pVal);
    public boolean isProcessInit();
    public void setProcessPost(boolean pVal);
    public boolean isProcessPost();
    public IREClassLoader getClassLoader();
    public void setClassLoader(IREClassLoader pVal);
    public IREOperation getOperation();
    public void setOperation(IREOperation pVal);
}
