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
 *
 * Created on Jul 1, 2003
 * @author Trey Spiva
 */
package org.netbeans.modules.uml.ui.support.commondialogs;

/**
 * Specifies the valid message dialog types.  These can be used when creating
 * any of the message dialogs.
 *
 * @see IQuestionDialog
 * @see IPickListDialog
 * @see IPreferenceQuestionDialog
 *
 * @author Trey Spiva
 */
public interface MessageDialogKindEnum
{
   public final static int SQDK_ABORTRETRYIGNORE = 0;
   public final static int SQDK_OK               = 1;
   public final static int SQDK_OKCANCEL         = 2;
   public final static int SQDK_RETRYCANCEL      = 3;
   public final static int SQDK_YESNO            = 4;
   public final static int SQDK_YESNOCANCEL      = 5;
   public final static int SQDK_YESNOALWAYS      = 6; 
   public final static int SQDK_YESNONEVER       = 7;
}
