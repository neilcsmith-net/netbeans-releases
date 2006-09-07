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

package org.netbeans.modules.apisupport.project.suite;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.apisupport.project.ProjectXMLManager;
import org.netbeans.modules.apisupport.project.universe.ModuleList;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Mutex;
import org.openide.util.MutexException;

/**
 * Servers for generating new NetBeans Modules templates.
 *
 * @author Martin Krauskopf
 */
public class SuiteProjectGenerator {
    
    private static final String PLATFORM_PROPERTIES_PATH =
            "nbproject/platform.properties"; // NOI18N
    public static final String PROJECT_PROPERTIES_PATH = "nbproject/project.properties"; // NOI18N
    public static final String PRIVATE_PROPERTIES_PATH = "nbproject/private/private.properties"; // NOI18N
    
    /** Use static factory methods instead. */
    private SuiteProjectGenerator() {/* empty constructor*/}
    
    /** Generates standalone NetBeans Module. */
    public static void createSuiteProject(final File projectDir, final String platformID) throws IOException {
        try {
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction() {
                public Object run() throws IOException {
                    final FileObject dirFO = FileUtil.createFolder(projectDir);
                    if (ProjectManager.getDefault().findProject(dirFO) != null) {
                        throw new IllegalArgumentException("Already a project in " + dirFO); // NOI18N
                    }
                    createSuiteProjectXML(dirFO);
                    createPlatformProperties(dirFO, platformID);
                    createProjectProperties(dirFO);
                    ModuleList.refresh();
                    ProjectManager.getDefault().clearNonProjectCache();
                    return null;
                }
            });
        } catch (MutexException e) {
            throw (IOException) e.getException();
        }
    }
    
    /**
     * Creates basic <em>nbbuild/project.xml</em> or whatever
     * <code>AntProjectHelper.PROJECT_XML_PATH</code> is pointing to for
     * <em>Suite</em>.
     */
    private static void createSuiteProjectXML(FileObject projectDir) throws IOException {
        ProjectXMLManager.generateEmptySuiteTemplate(
                createFileObject(projectDir, AntProjectHelper.PROJECT_XML_PATH),
                projectDir.getNameExt());
    }
    
    private static void createPlatformProperties(FileObject projectDir, String platformID) throws IOException {
        FileObject plafPropsFO = createFileObject(
                projectDir, PLATFORM_PROPERTIES_PATH);
        EditableProperties props = new EditableProperties(true);
        props.setProperty("nbplatform.active", platformID); // NOI18N
        storeProperties(plafPropsFO, props);
    }
    
    private static void createProjectProperties(FileObject projectDir) throws IOException {
        // #60026: ${modules} has to be defined right away.
        FileObject propsFO = createFileObject(projectDir, PROJECT_PROPERTIES_PATH);
        EditableProperties props = new EditableProperties(true);
        props.setProperty("modules", ""); // NOI18N
        storeProperties(propsFO, props);
    }
    
    /** Just utility method. */
    private static void storeProperties(FileObject bundleFO, EditableProperties props) throws IOException {
        FileLock lock = bundleFO.lock();
        try {
            OutputStream os = bundleFO.getOutputStream(lock);
            try {
                props.store(os);
            } finally {
                os.close();
            }
        } finally {
            lock.releaseLock();
        }
    }
    
    /**
     * Creates a new <code>FileObject</code>.
     * Throws <code>IllegalArgumentException</code> if such an object already
     * exists. Throws <code>IOException</code> if creation fails.
     */
    private static FileObject createFileObject(FileObject dir, String relToDir) throws IOException {
        FileObject createdFO = dir.getFileObject(relToDir);
        if (createdFO != null) {
            throw new IllegalArgumentException("File " + createdFO + " already exists."); // NOI18N
        }
        createdFO = FileUtil.createData(dir, relToDir);
        return createdFO;
    }
}


