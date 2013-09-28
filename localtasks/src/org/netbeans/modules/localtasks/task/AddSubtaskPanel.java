/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008-2009 Sun Microsystems, Inc.
 */

/*
 * IzPanel.java
 *
 * Created on Nov 11, 2008, 3:32:39 PM
 */
package org.netbeans.modules.localtasks.task;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.bugtracking.api.Issue;
import org.netbeans.modules.bugtracking.api.IssueQuickSearch;
import org.netbeans.modules.bugtracking.api.Repository;
import org.netbeans.modules.bugtracking.util.RepositoryComboSupport;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Tomas Stupka
 * @author Marian Petras
 */
final class AddSubtaskPanel extends JPanel implements ItemListener, ChangeListener {

    private final IssueQuickSearch qs;
    private Repository selectedRepository;
    private final ChangeSupport support;

    AddSubtaskPanel () {
        initComponents();

        support = new ChangeSupport(this);
        qs = new IssueQuickSearch(this);
        GroupLayout layout = (GroupLayout) getLayout();
        layout.replace(issuePanel, qs.getComponent());
        issueLabel.setLabelFor(qs.getComponent());
        repositoryComboBox.addItemListener(this);
        enableFields();
        RepositoryComboSupport.setup(this, repositoryComboBox, true);
    }

    Issue getIssue () {
        return qs.getIssue();
    }

    Repository getSelectedRepository () {
        return selectedRepository;
    }

    private void enableFields () {
        boolean repoSelected = isRepositorySelected();
        issueLabel.setEnabled(repoSelected);
        qs.enableFields(repoSelected);
    }

    private boolean isRepositorySelected () {
        Object selectedItem = repositoryComboBox.getSelectedItem();
        return selectedItem instanceof Repository;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        issuePanel = new javax.swing.JPanel();
        repositoryLabel = new javax.swing.JLabel();
        issueLabel = new javax.swing.JLabel();

        setFocusable(false);

        issuePanel.setLayout(new java.awt.BorderLayout());

        repositoryLabel.setLabelFor(repositoryComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(repositoryLabel, org.openide.util.NbBundle.getMessage(AddSubtaskPanel.class, "AddSubtaskPanel.repositoryLabel.text")); // NOI18N
        repositoryLabel.setToolTipText(org.openide.util.NbBundle.getMessage(AddSubtaskPanel.class, "AddSubtaskPanel.repositoryLabel.TTtext")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(issueLabel, org.openide.util.NbBundle.getMessage(AddSubtaskPanel.class, "AddSubtaskPanel.issueLabel.text")); // NOI18N
        issueLabel.setToolTipText(org.openide.util.NbBundle.getMessage(AddSubtaskPanel.class, "AddSubtaskPanel.issueLabel.TTtext")); // NOI18N

        errorLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/bugtracking/local/resources/error.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(AddSubtaskPanel.class, "AddSubtaskPanel.errorLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(repositoryLabel)
                    .addComponent(issueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(repositoryComboBox, 0, 503, Short.MAX_VALUE)
                    .addComponent(issuePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(errorLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(repositoryLabel)
                    .addComponent(repositoryComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(issueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(issuePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(errorLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        repositoryComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddSubtaskPanel.class, "AddSubtaskPanel.repositoryComboBox.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    final javax.swing.JLabel errorLabel = new javax.swing.JLabel();
    private javax.swing.JLabel issueLabel;
    private javax.swing.JPanel issuePanel;
    final javax.swing.JComboBox repositoryComboBox = new javax.swing.JComboBox();
    private javax.swing.JLabel repositoryLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void itemStateChanged (ItemEvent e) {
        enableFields();
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Object item = e.getItem();
            Repository repo = (item instanceof Repository) ? (Repository) item : null;
            selectedRepository = repo;
            if (repo != null) {
                qs.setRepository(repo);
            }
        }
    }

    @Override
    public void addNotify () {
        super.addNotify();
        qs.addChangeListener(this);
    }

    @Override
    public void removeNotify () {
        qs.removeChangeListener(this);
        super.removeNotify();
    }

    @Override
    public void stateChanged (ChangeEvent e) {
        enableFields();
        support.fireChange();
    }
    
    void addChangeListener (ChangeListener listener) {
        support.addChangeListener(listener);
    }
    
    void removeChangeListener (ChangeListener listener) {
        support.removeChangeListener(listener);
    }

}
