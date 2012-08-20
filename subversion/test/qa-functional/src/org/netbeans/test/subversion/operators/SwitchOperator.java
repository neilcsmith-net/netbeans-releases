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
package org.netbeans.test.subversion.operators;

import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.*;
import org.netbeans.test.subversion.operators.actions.SwitchAction;

/** Class implementing all necessary methods for handling "Switch AnagramGame to..." NbDialog.
 *
 * @author peter
 * @version 1.0
 */
public class SwitchOperator extends NbDialogOperator {

    /**
     * Creates new SwitchOperator that can handle it.
     */
    public SwitchOperator() {
        super("Switch");
    }
    
    /** Selects nodes and call switch action on them.
     * @param nodes an array of nodes
     * @return MergeOperator instance
     */
    public static SwitchOperator invoke(Node[] nodes) {
        new SwitchAction().perform(nodes);
        return new SwitchOperator();
    }
    
    /** Selects node and call switch action on it.
     * @param node node to be selected
     * @return SwitchOperator instance
     */
    public static SwitchOperator invoke(Node node) {
        return invoke(new Node[] {node});
    }

    private JLabelOperator _lblRepositoryFolder;
    private JComboBoxOperator _cboRepositoryFolder;
    private JButtonOperator _btBrowseRepositoryFolder;
    private JLabelOperator _lblRepositoryRevision;
    private JLabelOperator _lblEmptyMeansRepositoryHEAD;
    private JTextFieldOperator _txtRepositoryRevision;
    private JButtonOperator _btSearch;
    private JButtonOperator _btSwitch;
    private JButtonOperator _btCancel;
    private JButtonOperator _btHelp;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Repository Folder:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblRepositoryFolder() {
        if (_lblRepositoryFolder==null) {
            _lblRepositoryFolder = new JLabelOperator(this, "Repository Folder:");
        }
        return _lblRepositoryFolder;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboRepositoryFolder() {
        if (_cboRepositoryFolder==null) {
            _cboRepositoryFolder = new JComboBoxOperator(this);
        }
        return _cboRepositoryFolder;
    }

    /** Tries to find "Browse..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btBrowseRepositoryFolder() {
        if (_btBrowseRepositoryFolder==null) {
            _btBrowseRepositoryFolder = new JButtonOperator(this, "Browse");
        }
        return _btBrowseRepositoryFolder;
    }

    /** Tries to find "Repository Revision:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblRepositoryRevision() {
        if (_lblRepositoryRevision==null) {
            _lblRepositoryRevision = new JLabelOperator(this, "Repository Revision:");
        }
        return _lblRepositoryRevision;
    }

    /** Tries to find "(empty means repository HEAD)" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblEmptyMeansRepositoryHEAD() {
        if (_lblEmptyMeansRepositoryHEAD==null) {
            _lblEmptyMeansRepositoryHEAD = new JLabelOperator(this, "(empty means repository HEAD)");
        }
        return _lblEmptyMeansRepositoryHEAD;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtRepositoryRevision() {
        if (_txtRepositoryRevision==null) {
            _txtRepositoryRevision = new JTextFieldOperator(this, 1);
        }
        return _txtRepositoryRevision;
    }

    /** Tries to find "Search..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btSearch() {
        if (_btSearch==null) {
            _btSearch = new JButtonOperator(this, "Search...");
        }
        return _btSearch;
    }

    /** Tries to find "Switch" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btSwitch() {
        if (_btSwitch==null) {
            _btSwitch = new JButtonOperator(this, "Switch");
        }
        return _btSwitch;
    }

    /** Tries to find "Cancel" JButton in this dialog.
     * @return JButtonOperator
     */
    @Override
    public JButtonOperator btCancel() {
        if (_btCancel==null) {
            _btCancel = new JButtonOperator(this, "Cancel");
        }
        return _btCancel;
    }

    /** Tries to find "Help" JButton in this dialog.
     * @return JButtonOperator
     */
    @Override
    public JButtonOperator btHelp() {
        if (_btHelp==null) {
            _btHelp = new JButtonOperator(this, "Help");
        }
        return _btHelp;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /**
     * returns selected item for cboRepositoryFolder
     * 
     * @return String item
     */
    public String getSelectedRepositoryFolder() {
        return cboRepositoryFolder().getSelectedItem().toString();
    }

    public String getRepositoryFolder() {
        return cboRepositoryFolder().getEditor().getItem().toString();
    }
    
    /**
     * selects item for cboRepositoryFolder
     * 
     * @param item String item
     */
    public void selectJComboBox(String item) {
        cboRepositoryFolder().clearText();
        cboRepositoryFolder().selectItem(item);
    }

    /**
     * types text for cboRepositoryFolder
     * 
     * @param text String text
     */
    public void setRepositoryFolder(String text) {
        cboRepositoryFolder().clearText();
        cboRepositoryFolder().typeText(text);
    }

    /** clicks on "Browse..." JButton
     */
    public RepositoryBrowserOperator browseRepositoryFolder() {
        btBrowseRepositoryFolder().pushNoBlock();
        return new RepositoryBrowserOperator();
    }

    /**
     * gets text for txtRepositoryRevision
     * 
     * @return String text
     */
    public String getRepositoryRevision() {
        return txtRepositoryRevision().getText();
    }

    /**
     * types text for txtRepositoryRevision
     * 
     * @param text String text
     */
    public void setRepositoryRevision(String text) {
        txtRepositoryRevision().clearText();
        txtRepositoryRevision().typeText(text);
    }

    /** clicks on "Search..." JButton
     */
    public void search() {
        btSearch().push();
    }

    /** clicks on "Switch" JButton
     */
    public void switchBt() {
        btSwitch().push();
    }

    /** clicks on "Cancel" JButton
     */
    @Override
    public void cancel() {
        btCancel().push();
    }

    /** clicks on "Help" JButton
     */
    @Override
    public void help() {
        btHelp().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of SwitchAnagramGameTo by accessing all its components.
     */
    public void verify() {
        lblRepositoryFolder();
        cboRepositoryFolder();
        btBrowseRepositoryFolder();
        lblRepositoryRevision();
      // lblEmptyMeansRepositoryHEAD();
        txtRepositoryRevision();
        btSearch();
        btSwitch();
        btCancel();
        btHelp();
    }
}

