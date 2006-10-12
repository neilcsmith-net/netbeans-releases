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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.clientproject.wsclient;

import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.clientproject.AppClientProject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.netbeans.modules.websvc.api.client.WebServicesClientSupport;
import org.netbeans.modules.websvc.api.jaxws.client.JAXWSClientSupport;
import org.netbeans.modules.websvc.spi.client.WebServicesClientSupportProvider;


/** Provider object to locate web service client support for j2se project.
 *
 * @author Martin Grebac
 */
public class AppClientProjectWebServicesSupportProvider implements WebServicesClientSupportProvider {

    public AppClientProjectWebServicesSupportProvider () {
    }

    public WebServicesClientSupport findWebServicesClientSupport (FileObject file) {
        Project project = FileOwnerQuery.getOwner (file);
        if (project != null && project instanceof AppClientProject) {
            return ((AppClientProject) project).getAPIWebServicesClientSupport();
        }
        return null;
    }
        
    public JAXWSClientSupport findJAXWSClientSupport(FileObject file) {
        Project project = FileOwnerQuery.getOwner(file);
        if (project != null && project instanceof AppClientProject) {
            return ((AppClientProject) project).getAPIJAXWSClientSupport();
        }
        return null;
    }
}
