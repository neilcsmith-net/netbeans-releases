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
package org.netbeans.modules.search;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.spi.search.provider.SearchComposition;
import org.netbeans.spi.search.provider.SearchProvider;
import org.netbeans.spi.search.provider.SearchProvider.Presenter;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.Mnemonics;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;


/**
 *
 * @author jhavlin
 */
public class SearchPanel extends JPanel implements FocusListener,
        ActionListener {

    private static SearchPanel currentlyShown = null;
    private boolean replacing;
    private List<PresenterProxy> presenters;
    private DialogDescriptor dialogDescr;
    /**
     * OK button.
     */
    private JButton okButton;
    /**
     * Cancel button.
     */
    private JButton cancelButton;
    /**
     * Tabbed pane if there are extra providers.
     */
    JTabbedPane tabbedPane = null;
    /**
     * Dialog in which this search panel is displayed.
     */
    private Dialog dialog;
    /**
     * Selected Search presenter
     */
    private Presenter selectedPresenter = null;

    private boolean preferScopeSelection = false;

    /**
     * Panel that can show form with settings for several search providers.
     */
    public SearchPanel(boolean replacing) {
        this(replacing, null);
    }

    /**
     * Create search panel, using an explicit presenter for one of providers.
     */
    public SearchPanel(boolean replacing, Presenter presenter) {
        this.replacing = replacing;
        init(presenter);
    }

    private void init(Presenter explicitPresenter) {

        presenters = makePresenters(explicitPresenter);
        setLayout(new GridLayout(1, 1));

        if (presenters.isEmpty()) {
            throw new IllegalStateException("No presenter found");      //NOI18N
        } else if (presenters.size() == 1) {
            selectedPresenter = presenters.get(0).getPresenter();
            add(selectedPresenter.getForm());
        } else {
            tabbedPane = new JTabbedPane();
            for (PresenterProxy pp : presenters) {
                Component tab = tabbedPane.add(pp.getForm());
                if (pp.isInitialized()) {
                    tabbedPane.setSelectedComponent(tab);
                    selectedPresenter = pp.getPresenter();
                }
            }
            tabbedPane.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    tabChanged();
                }
            });
            add(tabbedPane);
        }
        if (selectedPresenter == null) {
            chooseLastUsedPresenter();
        }
        initLocalStrings();
        initAccessibility();
    }

    private void chooseLastUsedPresenter() {
        FindDialogMemory memory = FindDialogMemory.getDefault();
        String lastProv = memory.getProvider();
        if (lastProv != null) {
            for (PresenterProxy pp : presenters) {
                if (lastProv.equals(pp.getTitle())) {
                    selectedPresenter = pp.getPresenter();
                    tabbedPane.setSelectedComponent(pp.getForm());
                    return;
                }
            }
        }
        selectedPresenter = presenters.get(0).getPresenter();
    }

    private void initLocalStrings() throws MissingResourceException {
        setName(NbBundle.getMessage(SearchPanel.class,
                "TEXT_TITLE_CUSTOMIZE"));           //NOI18N

        Mnemonics.setLocalizedText(okButton = new JButton(),
                NbBundle.getMessage(
                org.netbeans.modules.search.SearchPanel.class,
                "TEXT_BUTTON_SEARCH"));     //NOI18N

        Mnemonics.setLocalizedText(cancelButton = new JButton(),
                NbBundle.getMessage(
                org.netbeans.modules.search.SearchPanel.class,
                "TEXT_BUTTON_CANCEL"));     //NOI18N
    }

    private void setDialogDescriptor(DialogDescriptor dialogDescriptor) {
        this.dialogDescr = dialogDescriptor;
    }

    private void initAccessibility() {
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SearchPanel.class, "ACS_SearchPanel")); // NOI18N
        if (tabbedPane != null) {
            tabbedPane.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SearchPanel.class, "ACSN_Tabs")); // NOI18N
            tabbedPane.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SearchPanel.class, "ACSD_Tabs")); // NOI18N
        }
        okButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SearchPanel.class, "ACS_TEXT_BUTTON_SEARCH")); // NOI18N
        cancelButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SearchPanel.class, "ACS_TEXT_BUTTON_CANCEL")); // NOI18N
    }

    /**
     * Make list of presenters created for all available search providers.
     *
     * @param explicitPresenter One of providers can be assigned an explicit
     * presenter, that will be used instead of creating a new one.
     *
     */
    private List<PresenterProxy> makePresenters(Presenter explicitPresenter) {

        List<PresenterProxy> presenterList = new LinkedList<PresenterProxy>();
        SearchProvider explicitProvider = explicitPresenter == null
                ? null
                : explicitPresenter.getSearchProvider();
        for (SearchProvider p :
                Lookup.getDefault().lookupAll(SearchProvider.class)) {
            if ((!replacing || p.isReplaceSupported()) && p.isEnabled()) {
                if (explicitProvider == p) {
                    presenterList.add(new PresenterProxy(explicitProvider,
                            explicitPresenter));
                } else {
                    presenterList.add(new PresenterProxy(p));
                }
            }
        }
        return presenterList;
    }

    public void showDialog() {

        String titleMsgKey = replacing
                ? "LBL_ReplaceInProjects" //NOI18N
                : "LBL_FindInProjects"; //NOI18N

        DialogDescriptor dialogDescriptor = new DialogDescriptor(
                this,
                NbBundle.getMessage(getClass(), titleMsgKey),
                false,
                new Object[]{okButton, cancelButton},
                okButton,
                DialogDescriptor.BOTTOM_ALIGN,
                new HelpCtx(getClass().getCanonicalName() + "." + replacing),
                this);

        dialogDescriptor.setTitle(NbBundle.getMessage(getClass(), titleMsgKey));
        dialogDescriptor.createNotificationLineSupport();

        dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        dialog.addWindowListener(new DialogCloseListener());
        this.setDialogDescriptor(dialogDescriptor);

        dialog.pack();
        setCurrentlyShown(this);
        dialog.setVisible(
                true);
        dialog.requestFocus();
        this.requestFocusInWindow();
        updateHelp();
        updateUsability();
        if (selectedPresenter == null) {
            chooseLastUsedPresenter();
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        // Tab changed
        tabChanged();
    }

    @Override
    public void focusLost(FocusEvent e) {
        // Tab changed
        tabChanged();
    }

    @Override
    public boolean requestFocusInWindow() {
        return selectedPresenter.getForm().requestFocusInWindow();
    }

    /**
     * Called when tab panel was changed.
     */
    private void tabChanged() {
        if (tabbedPane != null) {
            int i = tabbedPane.getSelectedIndex();
            PresenterProxy pp = presenters.get(i);
            selectedPresenter = pp.getPresenter();
            if (dialogDescr != null) {
                dialogDescr.getNotificationLineSupport().clearMessages();
                updateUsability();
                dialog.pack();
            }
            updateHelp();
            FindDialogMemory.getDefault().setProvider(
                    selectedPresenter.getSearchProvider().getTitle());
        }
    }

    private void updateHelp() {
        HelpCtx ctx = selectedPresenter.getHelpCtx();
        if (this.dialogDescr != null) {
            dialogDescr.setHelpCtx(ctx);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            search();
        } else if (e.getSource() == cancelButton) {
            cancel();
        }
    }

    private void search() {

        if (selectedPresenter != null) {
            SearchComposition<?> sc = selectedPresenter.composeSearch();
            if (sc != null) {
                SearchTask st = new SearchTask(sc, replacing);
                Manager.getInstance().scheduleSearchTask(st);
                close();
            }
        }
    }

    private void cancel() {
        close();
        ResultView.getInstance().clearReusableTab();
    }

    /**
     * Is this panel in search-and-replace mode?
     */
    boolean isSearchAndReplace() {
        return replacing;
    }

    /**
     * Close this search panel - dispose its containig dialog.
     *
     * {@link DialogCloseListener#windowClosed(java.awt.event.WindowEvent)} will
     * be called afterwards.
     */
    public void close() {
        if (dialog != null) {
            dialog.dispose();
            dialog = null;
        }
    }

    /**
     * Focus containig dialog.
     */
    void focusDialog() {
        if (dialog != null) {
            dialog.requestFocus();
        }
    }

    /**
     * Get currently displayed search panel, or null if no panel is shown.
     */
    public static SearchPanel getCurrentlyShown() {
        synchronized (SearchPanel.class) {
            return currentlyShown;
        }
    }

    /**
     * Set currently shoen panel, can be null (no panel shown currently.)
     */
    static void setCurrentlyShown(SearchPanel searchPanel) {
        synchronized (SearchPanel.class) {
            SearchPanel.currentlyShown = searchPanel;
        }
    }

    /**
     * Add change listener to a presenter.
     */
    private void initChangeListener(final Presenter p) {
        p.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                okButton.setEnabled(p.isUsable(
                        dialogDescr.getNotificationLineSupport()));
            }
        });
    }

    private void updateUsability() {
        okButton.setEnabled(selectedPresenter.isUsable(
                dialogDescr.getNotificationLineSupport()));
    }

    public boolean isPreferScopeSelection() {
        return preferScopeSelection;
    }

    public void setPreferScopeSelection(boolean preferScopeSelection) {
        this.preferScopeSelection = preferScopeSelection;
    }

    public static boolean isOpenedForSelection() {
        SearchPanel sp = getCurrentlyShown();
        if (sp == null) {
            return false;
        } else {
            return sp.isPreferScopeSelection();
        }
    }

    /**
     * Dialog-Close listener that clears reference to currently displayed panel
     * when its dialog is closed.
     */
    private class DialogCloseListener extends WindowAdapter {

        @Override
        public void windowClosed(WindowEvent e) {
            for (PresenterProxy presenter : presenters) {
                if (presenter.isInitialized()) {
                    presenter.getPresenter().clean();
                }
            }
            if (getCurrentlyShown() == SearchPanel.this) {
                setCurrentlyShown(null);
            }
        }
    }

    private class PresenterProxy {

        private SearchProvider searchProvider;
        private Presenter presenter;
        private JPanel panel;

        PresenterProxy(SearchProvider searchProvider) {
            this(searchProvider, null);
        }

        PresenterProxy(SearchProvider searchProvider,
                Presenter presenter) {
            this.searchProvider = searchProvider;
            this.presenter = presenter;
            this.panel = new JPanel();
            this.panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
            this.panel.setName(getTitle());
            if (presenter != null) {
                initUI();
            }
        }

        final String getTitle() {
            return searchProvider.getTitle();
        }

        synchronized Presenter getPresenter() {
            if (presenter == null) {
                presenter = searchProvider.createPresenter(replacing);
                initUI();
            }
            return presenter;
        }

        synchronized boolean isInitialized() {
            return presenter != null;
        }

        synchronized JComponent getForm() {
            return panel;
        }

        private void initUI() {
            panel.add(presenter.getForm());
            initChangeListener(presenter);
            panel.validate();
        }
    }
}
