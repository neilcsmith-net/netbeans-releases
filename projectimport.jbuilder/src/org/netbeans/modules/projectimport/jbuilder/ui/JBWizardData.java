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

package org.netbeans.modules.projectimport.jbuilder.ui;

import java.io.File;
import java.util.Collection;
import org.netbeans.modules.projectimport.j2seimport.ui.BasicPanel;

/**
 *
 * @author Radek Matous
 */
public class JBWizardData extends BasicPanel.WizardData {
    private File projectFile = null;
    private File destinationDir = null;
    private Collection projectDefinition = null;
    private boolean includeDependencies;


    /** Creates a new instance of JBWizardData */
    public JBWizardData() {
    }


    public File getDestinationDir() {
        return destinationDir;
    }

    void setDestinationDir(File destination) {
        this.destinationDir = destination;
    }

    public File getProjectFile() {
        return projectFile;
    }

    void setProjectFile(File projectFile) {
        this.projectFile = projectFile;
    }

    public Collection getProjectDefinition() {
        return projectDefinition;
    }

    void setProjectDefinition(Collection projectDefinition) {
        this.projectDefinition = projectDefinition;
    }

    public boolean isIncludeDependencies() {
        return includeDependencies;
    }

    void setIncludeDependencies(boolean includeDependencies) {
        this.includeDependencies = includeDependencies;
    }    
}
