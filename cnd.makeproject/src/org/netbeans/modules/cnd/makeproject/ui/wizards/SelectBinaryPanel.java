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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.makeproject.ui.wizards;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexander Simon
 */
public class SelectBinaryPanel implements WizardDescriptor.FinishablePanel<WizardDescriptor>, NewMakeProjectWizardIterator.Name, ChangeListener {
    private WizardDescriptor wizardDescriptor;
    private SelectBinaryPanelVisual component;
    private String name;
    private boolean isValid = false;
    private final BinaryWizardStorage wizardStorage;
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);

    public SelectBinaryPanel(){
        name = NbBundle.getMessage(SelectBinaryPanel.class, "SelectBinaryPanelVisual.Title"); // NOI18N
        wizardStorage = new BinaryWizardStorage(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isFinishPanel() {
        return  Boolean.TRUE.equals(wizardDescriptor.getProperty(NewMakeProjectWizardIterator.PROPERTY_SIMPLE_MODE));
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        String[] res;
        Object o = component.getClientProperty(WizardDescriptor.PROP_CONTENT_DATA);
        String[] names = (String[]) o;
        if (Boolean.TRUE.equals(wizardDescriptor.getProperty(NewMakeProjectWizardIterator.PROPERTY_SIMPLE_MODE))){
            res = new String[]{names[0]};
        } else {
            res = new String[]{names[0], "..."}; // NOI18N
        }
        component.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, res);
      	fireChangeEvent();
    }

    @Override
    public SelectBinaryPanelVisual getComponent() {
        if (component == null) {
            component = new SelectBinaryPanelVisual(this);
      	    component.setName(name);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("NewBinaryWizard"); // NOI18N
    }

    @Override
    public void readSettings(WizardDescriptor settings) {
        wizardDescriptor = settings;
        getComponent().read(wizardDescriptor);
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        getComponent().store(settings);
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    @Override
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    private void validate(){
        isValid = component.valid();
        fireChangeEvent();
    }

    protected final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }

    WizardDescriptor getWizardDescriptor(){
        return wizardDescriptor;
    }

    public BinaryWizardStorage getWizardStorage(){
        return wizardStorage;
    }

    public static class BinaryWizardStorage {
        private String binaryPath = ""; // NOI18N
        private String sourceFolderPath = ""; // NOI18N
        private final SelectBinaryPanel controller;

        public BinaryWizardStorage(SelectBinaryPanel controller) {
            this.controller = controller;
        }

        public String getBinaryPath() {
            return binaryPath;
        }

        public void setBinaryPath(String path) {
            this.binaryPath = path.trim();
            controller.validate();
        }

        public String getSourceFolderPath() {
            return sourceFolderPath;
        }

        public void setSourceFolderPath(String path) {
            this.sourceFolderPath = path.trim();
            controller.validate();
        }

        public void validate() {
            controller.validate();
        }
    }
}
