/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.cnd.debugger.dbx.breakpoints.types;

import org.netbeans.modules.cnd.debugger.common2.debugger.EditorBridge;
import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.NativeBreakpoint;
import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.BreakpointPanel;
import org.netbeans.modules.cnd.debugger.common2.utils.IpeUtils;

class ObjectBreakpointPanel extends BreakpointPanel {

    private ObjectBreakpoint fb;
    
    public void seed(NativeBreakpoint breakpoint) {
	seedCommonComponents(breakpoint);
	fb = (ObjectBreakpoint) breakpoint;

	objectText.setText(fb.getObject());
	baseToggle.setSelected(fb.isRecurse());
    }

    /*
     * Constructors
     */
    public ObjectBreakpointPanel() {
	this (new ObjectBreakpoint(NativeBreakpoint.TOPLEVEL), false);
    }

    public ObjectBreakpointPanel(NativeBreakpoint b) {
	this ((ObjectBreakpoint)b, true);
    }


    /** Creates new form ObjectBreakpointPanel */
    public ObjectBreakpointPanel(ObjectBreakpoint breakpoint,
				 boolean customizing ) {
	super(breakpoint, customizing);
	fb = breakpoint;

	initComponents();
	addCommonComponents(2);

	if (!customizing) {
	    String selection = EditorBridge.getCurrentSelection();
	    if (selection != null)
		breakpoint.setObject(selection);
	}

	seed(breakpoint);

	// Arrange to revalidate on changes
	objectText.getDocument().addDocumentListener(this);
    }
    
    public void setDescriptionEnabled(boolean enabled) {
	// objectLabel.setEnabled(false);
	objectText.setEnabled(false);
	baseToggle.setEnabled(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
	objectLabel = new javax.swing.JLabel();
	objectText = new javax.swing.JTextField();
	baseToggle = new javax.swing.JCheckBox();

	panel_settings.setLayout(new java.awt.GridBagLayout());
	java.awt.GridBagConstraints gridBagConstraints1;

	objectLabel.setText(Catalog.get("AllObjMethods"));	// NOI18N
	objectLabel.setDisplayedMnemonic(
	    Catalog.getMnemonic("MNEM_AllObjMethods"));		// NOI18N
	objectLabel.setLabelFor(objectText);
	gridBagConstraints1 = new java.awt.GridBagConstraints();
	gridBagConstraints1.ipadx = 5;
	gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
	panel_settings.add(objectLabel, gridBagConstraints1);

	gridBagConstraints1 = new java.awt.GridBagConstraints();
	gridBagConstraints1.gridwidth = 3;
	gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
	gridBagConstraints1.weightx = 1.0;
	panel_settings.add(objectText, gridBagConstraints1);

	baseToggle.setText(Catalog.get("IncludeParentClasses"));// NOI18N
	baseToggle.setMnemonic(
	    Catalog.getMnemonic("MNEM_IncludeParentClasses"));	// NOI18N
	gridBagConstraints1 = new java.awt.GridBagConstraints();
	gridBagConstraints1.gridwidth = 4;
	gridBagConstraints1.gridx = 0;
	gridBagConstraints1.gridy = 1;
	gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
	panel_settings.add(baseToggle, gridBagConstraints1);

	// a11y
	objectText.getAccessibleContext().setAccessibleDescription(
	    Catalog.get("ACSD_Object") // NOI18N
	);
	baseToggle.getAccessibleContext().setAccessibleDescription(
	    baseToggle.getText()
	);
    }

    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel objectLabel;
    private javax.swing.JTextField objectText;
    private javax.swing.JCheckBox baseToggle;

    protected void assignProperties() {
	fb.setObject(objectText.getText());
	fb.setRecurse(baseToggle.isSelected());
    }
    
    protected boolean propertiesAreValid() {
	if (IpeUtils.isEmpty(objectText.getText())) {
	    return false;
	}
	return true;
    }
}
