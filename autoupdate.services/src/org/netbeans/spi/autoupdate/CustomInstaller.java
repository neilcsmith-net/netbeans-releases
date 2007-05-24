/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.spi.autoupdate;

import org.netbeans.api.autoupdate.OperationException;
import org.netbeans.api.progress.ProgressHandle;

/** The call-back interface which is called from Autoupdate infrastructure when
 * the native component is installed.
 *
 * @author Jiri Rechtacek
 */
public interface CustomInstaller {

    /**
     * 
     * @param codeName code name of the native component
     * @param specificationVersion specification version of component
     * @param handle ProgressHandle
     * @return true if the installation succeed
     * @throws org.netbeans.api.autoupdate.OperationException 
     */
    public boolean install (String codeName, String specificationVersion, ProgressHandle handle) throws OperationException;
    
}
