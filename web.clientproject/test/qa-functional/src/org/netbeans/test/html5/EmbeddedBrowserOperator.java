/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.test.html5;

import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.NameComponentChooser;

/**
 * Operator for Embedded Web browser used in HTML5 projects
 *
 * @author Vladimir Riha
 * @version 1.0
 */
public class EmbeddedBrowserOperator extends TopComponentOperator {

    public EmbeddedBrowserOperator(String pageTitle) {
        super(pageTitle);
    }
    private JButtonOperator _btBack;
    private JButtonOperator _btForward;
    private JButtonOperator _btReload;
    private JButtonOperator _btStop;
    private JToggleButtonOperator _tbDesktopResize;
    private JToggleButtonOperator _tbTabletLandscapeResize;
    private JToggleButtonOperator _tbTabletPortraitResize;
    private JToggleButtonOperator _tbSmartphoneLandscapeResize;
    private JToggleButtonOperator _tbSmartphonePortraitResize;
    private JToggleButtonOperator _tbFitToScreenResize;
    private JToggleButtonOperator _tbOption;
    private JComboBoxOperator _cboZoom;
    public static final String ITEM_200 = "200%";
    public static final String ITEM_150 = "150%";
    public static final String ITEM_100 = "100%";
    public static final String ITEM_75 = "75%";
    public static final String ITEM_50 = "50%";
    private JToggleButtonOperator _tbInspectMode;

    //******************************
    // Subcomponents definition part
    //******************************
    private JButtonOperator btBack() {
        if (_btBack == null) {
            _btBack = new JButtonOperator(this);
        }
        return _btBack;
    }

    private JButtonOperator btForward() {
        if (_btForward == null) {
            _btForward = new JButtonOperator(this, 1);
        }
        return _btForward;
    }

    private JButtonOperator btReload() {
        if (_btReload == null) {
            _btReload = new JButtonOperator(this, 2);
        }
        return _btReload;
    }

    private JButtonOperator btStop() {
        if (_btStop == null) {
            _btStop = new JButtonOperator(this, 3);
        }
        return _btStop;
    }

    private JToggleButtonOperator tbDesktopResize() {
        if (_tbDesktopResize == null) {
            _tbDesktopResize = new JToggleButtonOperator(this);
        }
        return _tbDesktopResize;
    }

    private JToggleButtonOperator tbTabletLandscapeResize() {
        if (_tbTabletLandscapeResize == null) {
            _tbTabletLandscapeResize = new JToggleButtonOperator(this, 1);
        }
        return _tbTabletLandscapeResize;
    }

    private JToggleButtonOperator tbTabletPortraitResize() {
        if (_tbTabletPortraitResize == null) {
            _tbTabletPortraitResize = new JToggleButtonOperator(this, 2);
        }
        return _tbTabletPortraitResize;
    }

    private JToggleButtonOperator tbSmartphoneLandscapeResize() {
        if (_tbSmartphoneLandscapeResize == null) {
            _tbSmartphoneLandscapeResize = new JToggleButtonOperator(this, 3);
        }
        return _tbSmartphoneLandscapeResize;
    }

    private JToggleButtonOperator tbSmartphonePortraitResize() {
        if (_tbSmartphonePortraitResize == null) {
            _tbSmartphonePortraitResize = new JToggleButtonOperator(this, 4);
        }
        return _tbSmartphonePortraitResize;
    }

    private JToggleButtonOperator tbFitToScreenResize() {
        if (_tbFitToScreenResize == null) {
            _tbFitToScreenResize = new JToggleButtonOperator(this, 5);
        }
        return _tbFitToScreenResize;
    }

    private JToggleButtonOperator tbOption() {
        if (_tbOption == null) {
            _tbOption = new JToggleButtonOperator(this, 6);
        }
        return _tbOption;
    }

    private JComboBoxOperator cboZoom() {
        if (_cboZoom == null) {
            _cboZoom = new JComboBoxOperator(this);
        }
        return _cboZoom;
    }

    private JToggleButtonOperator tbInspectMode() {
        if (_tbInspectMode == null) {
            _tbInspectMode = new JToggleButtonOperator(this, new NameComponentChooser("selectionModeSwitch"));
        }
        return _tbInspectMode;
    }

    //****************************************
    // Low-level functionality definition part
    //****************************************
    /**
     * clicks on Back button in browser
     */
    public void back() {
        btBack().push();
    }

    /**
     * clicks on Forward button in browser
     */
    public void forward() {
        btForward().push();
    }

    /**
     * clicks on Reload button in browser
     */
    public void reload() {
        btReload().push();
    }

    /**
     * clicks on Stop button in browser
     */
    public void stop() {
        btStop().push();
    }

    /**
     * checks or unchecks given desktop resize button
     *
     * @param state boolean requested state
     */
    public void checkDesktopResizeButton(boolean state) {
        if (tbDesktopResize().isSelected() != state) {
            tbDesktopResize().push();
        }
    }

    /**
     * checks or unchecks given tablet landscape resize button
     *
     * @param state boolean requested state
     */
    public void checkTabletLandscapeResizeButton(boolean state) {
        if (tbTabletLandscapeResize().isSelected() != state) {
            tbTabletLandscapeResize().push();
        }
    }

    /**
     * checks or unchecks given tablet portrait resize button
     *
     * @param state boolean requested state
     */
    public void checkTabletPortraitResizeButton(boolean state) {
        if (tbTabletPortraitResize().isSelected() != state) {
            tbTabletPortraitResize().push();
        }
    }

    /**
     * checks or unchecks given smartphone landscape resize button
     *
     * @param state boolean requested state
     */
    public void checkSmartphoneLandscapeResizeButton(boolean state) {
        if (tbSmartphoneLandscapeResize().isSelected() != state) {
            tbSmartphoneLandscapeResize().push();
        }
    }

    /**
     * checks or unchecks given smartphone portrait resize button
     *
     * @param state boolean requested state
     */
    public void checkSmartphonePortraitResizeButton(boolean state) {
        if (tbSmartphonePortraitResize().isSelected() != state) {
            tbSmartphonePortraitResize().push();
        }
    }

    /**
     * checks or unchecks given fit to screen resize button
     *
     * @param state boolean requested state
     */
    public void checkFitToScreenResizeButton(boolean state) {
        if (tbFitToScreenResize().isSelected() != state) {
            tbFitToScreenResize().push();
        }
    }

    /**
     * returns selected item for zoom combo box
     *
     * @return String item
     */
    public String getSelectedZoom() {
        return cboZoom().getSelectedItem().toString();
    }

    /**
     * selects zoom in zoom combo box
     *
     * @param item String item
     */
    public void selectZoom(String item) {
        cboZoom().selectItem(item);
    }

    /**
     * types zoom in zoom combo box
     *
     * @param text String text
     */
    public void typeZoom(String text) {
        cboZoom().typeText(text);
    }

    /**
     * checks or unchecks inspect mode button
     *
     * @param state boolean requested state
     */
    public void checkInspectModeButton(boolean state) {
        if (tbInspectMode().isSelected() != state) {
            tbInspectMode().push();
        }
    }
}
