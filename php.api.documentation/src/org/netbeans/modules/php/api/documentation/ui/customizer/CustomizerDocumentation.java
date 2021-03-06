/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.api.documentation.ui.customizer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.LayoutStyle;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.documentation.PhpDocumentations;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.documentation.PhpDocumentationProvider;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

final class CustomizerDocumentation extends JPanel implements ChangeListener {

    private final ProjectCustomizer.Category category;
    private final PhpModule phpModule;
    private final Map<PhpDocumentationProvider, PhpModuleCustomizer> providerPanels;
    private final PhpDocumentationProvider originalProvider;

    private volatile PhpDocumentationProvider selectedProvider;


    CustomizerDocumentation(ProjectCustomizer.Category category, PhpModule phpModule) {
        assert EventQueue.isDispatchThread();
        assert category != null;
        assert phpModule != null;

        this.category = category;
        this.phpModule = phpModule;
        originalProvider = getOriginalProvider();

        providerPanels = createProviderPanels();

        initComponents();
        init();
    }

    private PhpDocumentationProvider getOriginalProvider() {
        for (PhpDocumentationProvider provider : PhpDocumentations.getDocumentations()) {
            if (provider.isInPhpModule(phpModule)) {
                return provider;
            }
        }
        return null;
    }

    private Map<PhpDocumentationProvider, PhpModuleCustomizer> createProviderPanels() {
        Map<PhpDocumentationProvider, PhpModuleCustomizer> panels = new ConcurrentHashMap<>();
        for (PhpDocumentationProvider provider : PhpDocumentations.getDocumentations()) {
            PhpModuleCustomizer customizer = provider.createPhpModuleCustomizer(phpModule);
            if (customizer != null) {
                panels.put(provider, customizer);
            }
        }
        return panels;
    }

    private void init() {
        for (PhpDocumentationProvider provider : getProviders()) {
            providerComboBox.addItem(provider);
        }
        if (originalProvider != null) {
            providerComboBox.setSelectedItem(originalProvider);
        }
        providerComboBox.setRenderer(new PhpDocumentationProviderRenderer());
        // listeners
        providerComboBox.addActionListener(new ProviderActionListener());
        category.setStoreListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                storeData();
            }
        });
        category.setCloseListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cleanup();
            }
        });
        // initial setup
        providerChanged();
    }

    void providerChanged() {
        assert EventQueue.isDispatchThread();
        removeListener();
        selectedProvider = getSelectedProvider();
        // switch panel
        providerPanel.removeAll();
        PhpModuleCustomizer selectedPanel = getSelectedPanel();
        if (selectedPanel != null) {
            providerPanel.add(selectedPanel.getComponent(), BorderLayout.CENTER);
            selectedPanel.addChangeListener(this);
        }
        providerPanel.revalidate();
        providerPanel.repaint();
        // validate
        validateData();
    }

    void validateData() {
        assert EventQueue.isDispatchThread();
        PhpModuleCustomizer customizer = getSelectedPanel();
        if (customizer != null) {
            String error = customizer.getErrorMessage();
            if (error != null) {
                category.setErrorMessage(error);
                category.setValid(false);
                return;
            }
            String warning = customizer.getWarningMessage();
            if (warning != null) {
                category.setErrorMessage(warning);
                category.setValid(true);
                return;
            }
        }
        // all ok
        category.setErrorMessage(null);
        category.setValid(true);
    }

    void storeData() {
        assert !EventQueue.isDispatchThread();
        if (originalProvider != selectedProvider) {
            if (originalProvider != null) {
                originalProvider.notifyEnabled(phpModule, false);
            }
            if (selectedProvider != null) {
                selectedProvider.notifyEnabled(phpModule, true);
            }
        }
        for (PhpModuleCustomizer customizer : getCustomizers()) {
            customizer.save();
        }
    }

    void cleanup() {
        removeListener();
        for (PhpModuleCustomizer customizer : getCustomizers()) {
            customizer.close();
        }
    }

    private void removeListener() {
        for (PhpModuleCustomizer customizer : getCustomizers()) {
            customizer.removeChangeListener(this);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // change in provider panel itself
        validateData();
    }

    @CheckForNull
    private PhpDocumentationProvider getSelectedProvider() {
        return (PhpDocumentationProvider) providerComboBox.getSelectedItem();
    }

    @CheckForNull
    private PhpModuleCustomizer getSelectedPanel() {
        assert EventQueue.isDispatchThread();
        assert providerPanels != null;
        PhpDocumentationProvider selecteProvider = getSelectedProvider();
        if (selecteProvider == null) {
            // #246324
            return null;
        }
        return providerPanels.get(selecteProvider);
    }

    private List<PhpDocumentationProvider> getProviders() {
        Set<PhpDocumentationProvider> providersWithPanels = providerPanels.keySet();
        List<PhpDocumentationProvider> providers = new ArrayList<>(providersWithPanels.size());
        for (PhpDocumentationProvider provider : PhpDocumentations.getDocumentations()) {
            if (providersWithPanels.contains(provider)) {
                providers.add(provider);
            }
        }
        return providers;
    }

    private List<PhpModuleCustomizer> getCustomizers() {
        List<PhpModuleCustomizer> customizers = new ArrayList<>(providerPanels.size());
        for (PhpDocumentationProvider provider : getProviders()) {
            PhpModuleCustomizer customizer = providerPanels.get(provider);
            assert customizer != null : "no customizer for " + provider.getName();
            customizers.add(providerPanels.get(provider));
        }
        return customizers;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        providerLabel = new JLabel();
        providerComboBox = new JComboBox<PhpDocumentationProvider>();
        separator = new JSeparator();
        providerPanel = new JPanel();

        Mnemonics.setLocalizedText(providerLabel, NbBundle.getMessage(CustomizerDocumentation.class, "CustomizerDocumentation.providerLabel.text")); // NOI18N

        providerPanel.setLayout(new BorderLayout());

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(separator)
            .addGroup(layout.createSequentialGroup()
                .addComponent(providerLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(providerComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addComponent(providerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(providerLabel)
                    .addComponent(providerComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(providerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox<PhpDocumentationProvider> providerComboBox;
    private JLabel providerLabel;
    private JPanel providerPanel;
    private JSeparator separator;
    // End of variables declaration//GEN-END:variables

    //~ Inner classes

    private static final class PhpDocumentationProviderRenderer implements ListCellRenderer<Object> {

        // @GuardedBy("EDT")
        private final ListCellRenderer<Object> defaultRenderer = new DefaultListCellRenderer();


        @Override
        public Component getListCellRendererComponent(JList<? extends Object> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            assert EventQueue.isDispatchThread();
            String label;
            if (value == null) {
                // #246324
                label = ""; // NOI18N
            } else {
                assert value instanceof PhpDocumentationProvider : value.getClass().getName();
                label = ((PhpDocumentationProvider) value).getDisplayName();
            }
            return defaultRenderer.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
        }

    }

    private final class ProviderActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            providerChanged();
        }

    }

}
