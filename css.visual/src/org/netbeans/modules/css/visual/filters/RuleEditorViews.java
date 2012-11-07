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
package org.netbeans.modules.css.visual.filters;

import javax.swing.JComponent;
import javax.swing.JToggleButton;
import org.netbeans.modules.css.visual.RuleEditorPanel;
import org.netbeans.modules.css.visual.api.ViewMode;

/**
 *
 * @author mfukala@netbeans.org
 */
public final class RuleEditorViews {
    
    private JToggleButton updatedOnlyToggleButton;
    private JToggleButton categorizedToggleButton;
    private JToggleButton allToggleButton;
    
    private RuleEditorPanel ruleEditorPanel;
    private FiltersManager filters;

    public RuleEditorViews(RuleEditorPanel ruleEditorPanel) {
        this.ruleEditorPanel = ruleEditorPanel;
    }

    public FiltersManager getInstance() {
        if (filters == null) {
            filters = FiltersManager.create(new FiltersDescription()); //empty
        }
        return filters;
    }

    public JComponent getComponent() {
        FiltersManager f = getInstance();
        return f.getComponent(createSortButtons());
    }

    void setViewMode(ViewMode mode) {
        ruleEditorPanel.setViewMode(mode);

        //update the toggle bottons (they are switching)
        updatedOnlyToggleButton.setSelected(mode == ViewMode.UPDATED_ONLY);
        categorizedToggleButton.setSelected(mode == ViewMode.CATEGORIZED);
        allToggleButton.setSelected(mode == ViewMode.ALL);
    }

    ViewMode getViewMode() {
        return ruleEditorPanel.getViewMode();
    }

    private JToggleButton[] createSortButtons() {
            JToggleButton[] res = new JToggleButton[3];
            if (null == updatedOnlyToggleButton) {
                updatedOnlyToggleButton = new JToggleButton(new ViewActionSupport.UpdatedOnlyViewAction(this));
                updatedOnlyToggleButton.setToolTipText(updatedOnlyToggleButton.getText());
                updatedOnlyToggleButton.setText(null);
                updatedOnlyToggleButton.setSelected(getViewMode() == ViewMode.UPDATED_ONLY);
                updatedOnlyToggleButton.setFocusable(false);
            }
            res[0] = updatedOnlyToggleButton;

            if (null == categorizedToggleButton) {
                categorizedToggleButton = new JToggleButton(new ViewActionSupport.CategorizedViewAction(this));
                categorizedToggleButton.setToolTipText(categorizedToggleButton.getText());
                categorizedToggleButton.setText(null);
                categorizedToggleButton.setSelected(getViewMode() == ViewMode.CATEGORIZED);
                categorizedToggleButton.setFocusable(false);
            }
            res[1] = categorizedToggleButton;
            
            if (null == allToggleButton) {
                allToggleButton = new JToggleButton(new ViewActionSupport.AllViewAction(this));
                allToggleButton.setToolTipText(allToggleButton.getText());
                allToggleButton.setText(null);
                allToggleButton.setSelected(getViewMode() == ViewMode.ALL);
                allToggleButton.setFocusable(false);
            }
            res[2] = allToggleButton;
            return res;
    }
}
