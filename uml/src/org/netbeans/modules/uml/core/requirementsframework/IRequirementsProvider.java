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
 * IRequirementsProvider.java
 *
 * Created on June 24, 2004, 1:12 PM
 */

package org.netbeans.modules.uml.core.requirementsframework;

//import org.netbeans.modules.uml.core.addinframework.IAddInDescriptor;
import org.netbeans.modules.uml.core.metamodel.structure.IRequirementArtifact;
import org.netbeans.modules.uml.core.support.umlutils.ETList;

/**
 *
 * @author  Trey Spiva
 */
public interface IRequirementsProvider
{
   /**
    */
   //public IRequirementSource displaySources(IAddInDescriptor pAddInDescriptor)
   public IRequirementSource displaySources() throws RequirementsException;
   
   public String getProgID();
   
   public void setProgID(String newVal);
   
   public ETList < IRequirement > loadRequirements(IRequirementSource pReqSource )
      throws RequirementsException;
   
   public IRequirement getRequirement(IRequirementArtifact pRequirementArtifact,
                                      IRequirementSource pRequirementSource)
      throws RequirementsException;
   
   public String getDisplayName();
   
   public String getDescription();
}
