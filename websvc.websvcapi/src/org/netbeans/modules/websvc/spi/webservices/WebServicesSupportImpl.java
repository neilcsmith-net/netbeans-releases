/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.websvc.spi.webservices;

import org.openide.filesystems.FileObject;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.dd.api.webservices.ServiceImplBean;

/**
 *
 * @author Peter Williams
 */
public interface WebServicesSupportImpl {
	
    /*
     * Add web service related entries to the project file and the module's DD
     */
    public void addServiceImpl(String serviceName, String serviceEndpointInterface, String serviceEndpoint, FileObject configFile);
	
    /**
     * Get the FileObject of the webservices.xml file.
     */
    public FileObject getDD();

    /**
     *  Returns the directory that contains webservices.xml in the project
     */
    public FileObject getWsDDFolder();

    /**
     * Returns the name of the directory that contains the webservices.xml in
     * the archive
     */
    public String getArchiveDDFolderName();

    /**
     * Returns the name of the implementation bean class
     * given the servlet-link or ejb-link name
     */
    public String getImplementationBean(String linkName);

    /**
     *  Given the servlet-link or ejb-link, remove the servlet or
     *  ejb entry in the module's deployment descriptor.
     */
    public void removeServiceEntry(String serviceName, String linkName);
	
    /**
     * Get the AntProjectHelper from the project
     */
    public AntProjectHelper getAntProjectHelper();

    /**
     * Generate the implementation bean class and return the class name
     */
    public String generateImplementationBean(String wsName, FileObject pkg, Project project)throws java.io.IOException;

    /**
     *  Add the servlet link or ejb link in the webservices.xml entry
    */
    public void addServiceImplLinkEntry(ServiceImplBean serviceImplBean, String wsName);
	
}
