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

package org.netbeans.modules.uml.core.requirementsframework;

/**
 * @author brettb
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Satisfier implements ISatisfier
{
   private String m_strName;
   private String m_strType;
   private String m_strXMIID;
   private String m_strProjectName;
   private String m_strProjectID;

   public String getProjectID()
   {
      return m_strProjectID;
   }

   public void setProjectID(String newVal)
   {
      m_strProjectID = newVal;
   }

   public String getProjectName()
   {
      return m_strProjectName;
   }

   public void setProjectName(String newVal)
   {
      m_strProjectName = newVal;
   }

   public String getType()
   {
      return m_strType;
   }

   public void setType(String newVal)
   {
      m_strType = newVal;
   }

   public String getXMIID()
   {
      return m_strXMIID;
   }

   public void setXMIID(String newVal)
   {
      m_strXMIID = newVal;
   }

   public String getName()
   {
      return m_strName;
   }

   public void setName(String newVal)
   {
      m_strName = newVal;
   }
}
