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

import org.netbeans.modules.uml.core.metamodel.core.foundation.IVersionableElement;
import org.netbeans.modules.uml.core.support.umlsupport.IResultCell;
import org.netbeans.modules.uml.core.support.umlutils.ETList;

/**
 *
 * @author Trey Spiva
 */
public class AffectedElementEventsAdapter implements IAffectedElementEventsSink
{

   /* (non-Javadoc)
    * @see com.embarcadero.describe.coreinfrastructure.IAffectedElementEventsSink#onPreImpacted(com.embarcadero.describe.coreinfrastructure.IClassifier, com.embarcadero.describe.foundationcollections.IVersionableElements, com.embarcadero.describe.umlsupport.IResultCell)
    */
   public void onPreImpacted(
      IClassifier classifier,
      ETList<IVersionableElement> impacted,
      IResultCell cell)
   {
      // TODO Auto-generated method stub
   }

   /* (non-Javadoc)
    * @see com.embarcadero.describe.coreinfrastructure.IAffectedElementEventsSink#onImpacted(com.embarcadero.describe.coreinfrastructure.IClassifier, com.embarcadero.describe.foundationcollections.IVersionableElements, com.embarcadero.describe.umlsupport.IResultCell)
    */
   public void onImpacted(
      IClassifier classifier,
      ETList<IVersionableElement> impacted,
      IResultCell cell)
   {
      // TODO Auto-generated method stub
   }

}
