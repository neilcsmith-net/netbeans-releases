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

package org.netbeans.modules.project.uiapi;

import javax.swing.JFileChooser;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;

/**
 * Factory to be implemented bu the ui implementation
 * @author Petr Hrebejk
 */
public interface ProjectChooserFactory {
            
    public static final String WIZARD_KEY_PROJECT = "project"; // NOI18N

    public static final String WIZARD_KEY_TARGET_FOLDER = "targetFolder"; // NOI18N
    
    public static final String WIZARD_KEY_TARGET_NAME = "targetName"; // NOI18N
    
    public static final String WIZARD_KEY_TEMPLATE = "targetTemplate"; // NOI18N
    
    public JFileChooser createProjectChooser(); 
    
    public WizardDescriptor.Panel createSimpleTargetChooser( Project project, SourceGroup[] folders, WizardDescriptor.Panel bottomPanel );
            
}
