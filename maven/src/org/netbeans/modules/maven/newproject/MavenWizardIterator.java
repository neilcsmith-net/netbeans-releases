/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.newproject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.validation.adapters.WizardDescriptorAdapter;
import org.netbeans.modules.maven.api.archetype.Archetype;
import org.netbeans.modules.maven.api.archetype.ArchetypeWizards;
import static org.netbeans.modules.maven.newproject.Bundle.*;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle.Messages;

/**
 *
 *@author mkleint
 */
@TemplateRegistration(folder=ArchetypeWizards.TEMPLATE_FOLDER, position=990, displayName="#template.pickArchetype", iconBase="org/netbeans/modules/maven/resources/Maven2Icon.gif", description="MavenDescription.html")
@Messages("template.pickArchetype=Project from Archetype")
public class MavenWizardIterator implements WizardDescriptor.BackgroundInstantiatingIterator<WizardDescriptor> {
    
    private static final long serialVersionUID = 1L;
    static final String PROPERTY_CUSTOM_CREATOR = "customCreator"; //NOI18N
    static final String PROP_ARCHETYPE = "archetype";
    static final String JAVAFX_SAMPLES_TEMPLATE_FOLDER = "Project/Samples/JavaFX";
    private transient int index;
    private transient List<WizardDescriptor.Panel<WizardDescriptor>> panels;
    private transient WizardDescriptor wiz;
    private final Archetype archetype;
    private final AtomicBoolean hasNextCalled = new AtomicBoolean(); //#216236
    private final String titlename;

    public MavenWizardIterator() {
        this(null, null);
    }
    
    public MavenWizardIterator(Archetype archetype, String titleName) {
        this.archetype = archetype;
        this.titlename = titleName;
    }

//    @TemplateRegistration(folder=ArchetypeWizards.TEMPLATE_FOLDER, position=100, displayName="#LBL_Maven_Quickstart_Archetype", iconBase="org/netbeans/modules/maven/resources/jaricon.png", description="quickstart.html")
//    @Messages("LBL_Maven_Quickstart_Archetype=Java Application")
//    public static WizardDescriptor.InstantiatingIterator<?> quickstart() {
//        return ArchetypeWizards.definedArchetype("org.apache.maven.archetypes", "maven-archetype-quickstart", "1.1", null, LBL_Maven_Quickstart_Archetype());
//    }
    
    @TemplateRegistration(folder=ArchetypeWizards.TEMPLATE_FOLDER, position=120, displayName="#LBL_Maven_JavaFx_Archetype", iconBase="org/netbeans/modules/maven/resources/jaricon.png", description="javafx.html")
    @Messages("LBL_Maven_JavaFx_Archetype=JavaFX Application")
    public static WizardDescriptor.InstantiatingIterator<?> javafx() {
        return ArchetypeWizards.definedArchetype("org.codehaus.mojo.archetypes", "javafx", "0.6", null, LBL_Maven_JavaFx_Archetype());
    }

    @TemplateRegistration(folder=JAVAFX_SAMPLES_TEMPLATE_FOLDER, position=2450, displayName="#LBL_Maven_JavaFx_Sample_Archetype", iconBase="org/netbeans/modules/maven/resources/jaricon.png", description="javafx.html")
    @Messages("LBL_Maven_JavaFx_Sample_Archetype=Maven FXML MigPane Sample")
    public static WizardDescriptor.InstantiatingIterator<?> javafxSample() {
        return ArchetypeWizards.definedArchetype("org.codehaus.mojo.archetypes", "sample-javafx", "0.5", null, LBL_Maven_JavaFx_Sample_Archetype());
    }

//    @TemplateRegistration(folder=ArchetypeWizards.TEMPLATE_FOLDER, position=980, displayName="#LBL_Maven_POM_Archetype", iconBase="org/netbeans/modules/maven/resources/Maven2Icon.gif", description="pom-root.html")
//    @Messages("LBL_Maven_POM_Archetype=POM Project")
//    public static WizardDescriptor.InstantiatingIterator<?> pomRoot() {
//        return ArchetypeWizards.definedArchetype("org.codehaus.mojo.archetypes", "pom-root", "1.1", null, LBL_Maven_POM_Archetype());
//    }

    public @Override Set<FileObject> instantiate() throws IOException {
        return ArchetypeWizardUtils.instantiate(wiz);
    }
    
    @Override
    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        if (titlename != null) {
            wiz.putProperty ("NewProjectWizard_Title", titlename); // NOI18N        
        }
        index = 0;
        ValidationGroup vg = ValidationGroup.create(new WizardDescriptorAdapter(wiz));
        panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        List<String> steps = new ArrayList<String>();
        if (archetype == null) {
            panels.add(new ChooseWizardPanel());
            steps.add(LBL_CreateProjectStep());
        }
        panels.add(new BasicWizardPanel(vg, null, true, true)); //only download archetype (for additional props) when unknown archetype is used.
        steps.add(LBL_CreateProjectStep2());
        for (int i = 0; i < panels.size(); i++) {
            JComponent c = (JComponent) panels.get(i).getComponent();
            c.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
            c.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps.toArray(new String[0]));
      }
        if (archetype != null) {
            wiz.putProperty(PROP_ARCHETYPE, archetype);
        }
    }
    
    @Override
    public void uninitialize(WizardDescriptor wiz) {
//        wiz.putProperty(CommonProjectActions.PROJECT_PARENT_FOLDER, null); //NOI18N
        wiz.putProperty("name",null); //NOI18N
        this.wiz = null;
        panels = null;
    }
    
    @Messages({"# {0} - index", "# {1} - length", "NameFormat={0} of {1}"})
    public @Override String name() {
        return NameFormat(index + 1, panels.size());
    }
    
    @Override
    public boolean hasNext() {
        hasNextCalled.set(true);
        return hasNextImpl();        
    }
    
    private boolean hasNextImpl() {
        return index < panels.size() - 1;
    }
    
    @Override
    public boolean hasPrevious() {
        return index > 0;
    }
    
    @Override
    public void nextPanel() {
        final boolean hnc = hasNextCalled.getAndSet(false);
        if (!hasNextImpl()) {
            throw new NoSuchElementException( //#216236
                    MessageFormat.format(
                    "index: {0}, panels: {1}, called has next: {2}",
                    index,
                    panels.size(),
                    hnc));
        }
        index++;
    }
    
    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }
    
    @Override
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        return panels.get(index);
    }
    
    public @Override void addChangeListener(ChangeListener l) {}
    
    public @Override void removeChangeListener(ChangeListener l) {}

}
