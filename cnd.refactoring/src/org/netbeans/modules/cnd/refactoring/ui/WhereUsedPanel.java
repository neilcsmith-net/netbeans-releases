/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.cnd.refactoring.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.refactoring.spi.ui.CustomRefactoringPanel;
import org.openide.util.NbBundle;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.cnd.api.model.CsmClass;
import org.netbeans.modules.cnd.api.model.CsmClassifier;
import org.netbeans.modules.cnd.api.model.CsmDeclaration;
import org.netbeans.modules.cnd.api.model.CsmEnum;
import org.netbeans.modules.cnd.api.model.CsmEnumerator;
import org.netbeans.modules.cnd.api.model.CsmField;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmFunction;
import org.netbeans.modules.cnd.api.model.CsmMacro;
import org.netbeans.modules.cnd.api.model.CsmMember;
import org.netbeans.modules.cnd.api.model.CsmMethod;
import org.netbeans.modules.cnd.api.model.CsmNamedElement;
import org.netbeans.modules.cnd.api.model.CsmNamespace;
import org.netbeans.modules.cnd.api.model.CsmObject;
import org.netbeans.modules.cnd.api.model.CsmQualifiedNamedElement;
import org.netbeans.modules.cnd.api.model.CsmTypedef;
import org.netbeans.modules.cnd.api.model.CsmVariable;
import org.netbeans.modules.cnd.api.model.CsmVisibility;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.api.model.xref.CsmReference;
import org.netbeans.modules.cnd.refactoring.support.CsmRefactoringUtils;
import org.netbeans.modules.cnd.refactoring.support.RefactoringModule;
import org.openide.awt.Mnemonics;


/**
 * Based on the WhereUsedPanel in Java refactoring by Jan Becicka.
 * @author Vladimir Voskresensky
 */
public class WhereUsedPanel extends JPanel implements CustomRefactoringPanel {

    private final transient CsmObject origObject;
    private transient CsmObject refObject;

    private final transient ChangeListener parent;
    private String name;
    private Scope defaultScope;
    /** Creates new form WhereUsedPanel */
    public WhereUsedPanel(String name, CsmObject csmObject,ChangeListener parent) {
        setName(NbBundle.getMessage(WhereUsedPanel.class, "LBL_WhereUsed")); // NOI18N
        this.origObject = csmObject;
        this.parent = parent;
        this.name = name;
        this.defaultScope = Scope.CURRENT;
        initComponents();
    }
    
    public enum Scope {
        ALL,
        CURRENT
    }
    
    public Scope getScope() {
        if (scope.getItemCount() == 0) {
            return defaultScope;
        }
        if (scope.getSelectedIndex() == 1) {
            return Scope.CURRENT;
        }
        return Scope.ALL;
    }
    
    private boolean initialized = false;
    private CsmClass methodDeclaringSuperClass = null;
    private CsmClass methodDeclaringClass = null;
    private CsmMethod baseVirtualMethod;

    /*package*/ String getBaseMethodDescription() {
        if (baseVirtualMethod != null) {
            CsmVisibility vis = baseVirtualMethod.getVisibility();
            String functionDisplayName = baseVirtualMethod.getSignature().toString();
            String displayClassName = methodDeclaringSuperClass.getName().toString();
            return getString("DSC_MethodUsages", functionDisplayName, displayClassName); // NOI18N
        } else {
            return name;
        }
    }

    /*package*/ CsmClass getMethodDeclaringClass() {
        return isMethodFromBaseClass() ? methodDeclaringSuperClass : methodDeclaringClass;
    }

    public void initialize() {
        // method is called to make initialization of components out of AWT
        if (initialized) {
            return;
        }
        initFields();

        final JLabel currentProject;
        final JLabel allProjects;
        if (!CsmKindUtilities.isLocalVariable(this.refObject)) {
            Project p = CsmRefactoringUtils.getContextProject(this.origObject);
            if (p!=null) {
                ProjectInformation pi = ProjectUtils.getInformation(p);
                currentProject = new JLabel(pi.getDisplayName(), pi.getIcon(), SwingConstants.LEFT);
                allProjects = new JLabel(NbBundle.getMessage(WhereUsedPanel.class, "LBL_AllProjects"), pi.getIcon(), SwingConstants.LEFT); // NOI18N
            } else {
                defaultScope = Scope.ALL;
                currentProject = null;
                allProjects = null;
            }
        } else {
            defaultScope = Scope.CURRENT;
            currentProject = null;
            allProjects = null;
        }
        
        final String labelText;
        String _isBaseClassText = null;
        boolean _needVirtualMethodPanel = false;
        boolean _needClassPanel = false;
        if (CsmKindUtilities.isMethod(refObject)) {
//            CsmVisibility vis = ((CsmMember)refObject).getVisibility();
            String functionDisplayName = ((CsmMethod)refObject).getSignature().toString();
            methodDeclaringClass = ((CsmMember)refObject).getContainingClass();
            String displayClassName = methodDeclaringClass.getName().toString();
            labelText = getString("DSC_MethodUsages", functionDisplayName, displayClassName); // NOI18N
            if (((CsmMethod)refObject).isVirtual()) {
                baseVirtualMethod = getOriginalVirtualMethod((CsmMethod)refObject);
                methodDeclaringSuperClass = baseVirtualMethod.getContainingClass();
                if (!refObject.equals(baseVirtualMethod)) {
                    _isBaseClassText = getString("LBL_UsagesOfBaseClass", methodDeclaringSuperClass.getName().toString()); // NOI18N
                }
                _needVirtualMethodPanel = true;
            }
        } else if (CsmKindUtilities.isFunction(refObject)) {
            String functionFQN = ((CsmFunction)refObject).getSignature().toString();
            functionFQN = CsmRefactoringUtils.htmlize(functionFQN);
            labelText = getString("DSC_FunctionUsages", functionFQN); // NOI18N
        } else if (CsmKindUtilities.isClass(refObject)) {
            CsmDeclaration.Kind classKind = ((CsmDeclaration)refObject).getKind();
            String key;
            if (classKind == CsmDeclaration.Kind.STRUCT) {
                key = "DSC_StructUsages"; // NOI18N
            } else if (classKind == CsmDeclaration.Kind.UNION) {
                key = "DSC_UnionUsages"; // NOI18N
            } else {
                key = "DSC_ClassUsages"; // NOI18N
            }
            labelText = getString(key, ((CsmClassifier)refObject).getQualifiedName().toString());
            _needClassPanel = true;
        } else if (CsmKindUtilities.isTypedef(refObject)) {
            String tdName = ((CsmTypedef)refObject).getQualifiedName().toString();
            labelText = getString("DSC_TypedefUsages", tdName); // NOI18N
        } else if (CsmKindUtilities.isEnum(refObject)) {
            labelText = getString("DSC_EnumUsages", ((CsmEnum)refObject).getQualifiedName().toString()); // NOI18N
        } else if (CsmKindUtilities.isEnumerator(refObject)) {
            CsmEnumerator enmtr = ((CsmEnumerator)refObject);
            labelText = getString("DSC_EnumeratorUsages", enmtr.getName().toString(), enmtr.getEnumeration().getName().toString()); // NOI18N
        } else if (CsmKindUtilities.isField(refObject)) {
            String fieldName = ((CsmField)refObject).getName().toString();
            String displayClassName = ((CsmField)refObject).getContainingClass().getName().toString();
            labelText = getString("DSC_FieldUsages", fieldName, displayClassName); // NOI18N
        } else if (CsmKindUtilities.isVariable(refObject)) {
            String varName = ((CsmVariable)refObject).getName().toString();
            labelText = getString("DSC_VariableUsages", varName); // NOI18N
        } else if (CsmKindUtilities.isFile(refObject)) {
            String fileName = ((CsmFile)refObject).getName().toString();
            labelText = getString("DSC_FileUsages", fileName); // NOI18N
        } else if (CsmKindUtilities.isNamespace(refObject)) {
            String nsName = ((CsmNamespace)refObject).getQualifiedName().toString();
            labelText = getString("DSC_NamespaceUsages", nsName); // NOI18N
//        } else if (element.getKind() == ElementKind.CONSTRUCTOR) {
//            String methodName = element.getName();
//            String className = getClassName(element);
//            labelText = getFormattedString("DSC_ConstructorUsages", methodName, className); // NOI18N
        } else if (CsmKindUtilities.isMacro(refObject)) {
            StringBuilder macroName = new StringBuilder(((CsmMacro)refObject).getName());
            if (((CsmMacro)refObject).getParameters() != null) {
                macroName.append("("); // NOI18N
                Iterator<? extends CharSequence> params = ((CsmMacro)refObject).getParameters().iterator();
                if (params.hasNext()) {
                    macroName.append(params.next());
                    while (params.hasNext()) {
                        macroName.append(", "); // NOI18N
                        macroName.append(params.next());
                    }
                }
                macroName.append(")"); // NOI18N
            }
            labelText = getString("DSC_MacroUsages", macroName.toString()); // NOI18N
        } else if (CsmKindUtilities.isQualified(refObject)) {
            labelText = ((CsmQualifiedNamedElement)refObject).getQualifiedName().toString();
        } else {
            labelText = this.name;
        }

        this.name = labelText;
        
//        final Set<Modifier> modifiers = modif;
        final String isBaseClassText = _isBaseClassText;
        final boolean showMethodPanel = _needVirtualMethodPanel;
        final boolean showClassPanel = _needClassPanel;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                remove(classesPanel);
                remove(methodsPanel);
                // WARNING for now since this feature is not ready yet
                //label.setText(labelText);
                String combinedLabelText = "<html><font style=\"color: red\">WARNING: This feature is in development and inaccurate!</font><br><br>" + labelText + "</html>"; // NOI18N
                label.setText(combinedLabelText);
                if (showMethodPanel) {
                    add(methodsPanel, BorderLayout.CENTER);
                    methodsPanel.setVisible(true);
                    if (isBaseClassText != null) {
                        Mnemonics.setLocalizedText(m_isBaseClass, isBaseClassText);
                        m_isBaseClass.setVisible(true);
                        m_isBaseClass.setSelected(true);
                    } else {
                        m_isBaseClass.setVisible(false);
                        m_isBaseClass.setSelected(false);
                    }
//                    if (methodDeclaringSuperClass != null) {
//                        m_overriders.setVisible(true);
//                        m_isBaseClass.setVisible(true);
//                        m_isBaseClass.setSelected(true);
//                        Mnemonics.setLocalizedText(m_isBaseClass, isBaseClassText);
//                    } else {
//                        m_overriders.setVisible(false);
//                        m_isBaseClass.setVisible(false);
//                        m_isBaseClass.setSelected(false);
//                    }                    
                } else if (showClassPanel) {
                    add(classesPanel, BorderLayout.CENTER);
                    classesPanel.setVisible(true);   
                } else {
//                if (element.getKind() == ElementKind.METHOD) {
//                    add(methodsPanel, BorderLayout.CENTER);
//                    methodsPanel.setVisible(true);
//                    m_usages.setVisible(!modifiers.contains(Modifier.STATIC));
//                    // TODO - worry about frozen?
//                    m_overriders.setVisible(modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.PRIVATE));
//                    if (methodDeclaringSuperClass != null) {
//                        m_isBaseClass.setVisible(true);
//                        m_isBaseClass.setSelected(true);
//                        Mnemonics.setLocalizedText(m_isBaseClass, isBaseClassText);
//                    } else {
//                        m_isBaseClass.setVisible(false);
//                        m_isBaseClass.setSelected(false);
//                    }
//                } else if ((element.getKind() == ElementKind.CLASS) || (element.getKind() == ElementKind.MODULE)) {
//                    add(classesPanel, BorderLayout.CENTER);
//                    classesPanel.setVisible(true);
//                } else {
//                    remove(classesPanel);
//                    remove(methodsPanel);
//                    c_subclasses.setVisible(false);
//                    m_usages.setVisible(false);
//                    c_usages.setVisible(false);
//                    c_directOnly.setVisible(false);
                }
                if (currentProject!=null) {
                    scope.setModel(new DefaultComboBoxModel(new Object[]{allProjects, currentProject }));
                    int defaultItem = (Integer) RefactoringModule.getOption("whereUsed.scope", 0); // NOI18N
                    scope.setSelectedIndex(defaultItem);
                    scope.setRenderer(new JLabelRenderer());
                } else {
                    scopePanel.setVisible(false);
                }                
                validate();
            }
        });

        initialized = true;
    }

    /*package*/ CsmMethod getBaseMethod() {
        return baseVirtualMethod;
    }

    /*package*/ CsmObject getReferencedObject() {
        return refObject;
    }

    /*package*/ String getDescription() {
        return name;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup = new javax.swing.ButtonGroup();
        methodsPanel = new javax.swing.JPanel();
        m_isBaseClass = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        m_overriders = new javax.swing.JCheckBox();
        m_usages = new javax.swing.JCheckBox();
        classesPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        c_subclasses = new javax.swing.JRadioButton();
        c_usages = new javax.swing.JRadioButton();
        c_directOnly = new javax.swing.JRadioButton();
        commentsPanel = new javax.swing.JPanel();
        label = new javax.swing.JLabel();
        searchInComments = new javax.swing.JCheckBox();
        scopePanel = new javax.swing.JPanel();
        scopeLabel = new javax.swing.JLabel();
        scope = new javax.swing.JComboBox();

        setLayout(new java.awt.BorderLayout());

        methodsPanel.setLayout(new java.awt.GridBagLayout());

        m_isBaseClass.setSelected(true);
        m_isBaseClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_isBaseClassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        methodsPanel.add(m_isBaseClass, gridBagConstraints);
        m_isBaseClass.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(WhereUsedPanel.class).getString("ACSD_isBaseClass")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        methodsPanel.add(jPanel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(m_overriders, org.openide.util.NbBundle.getMessage(WhereUsedPanel.class, "LBL_FindOverridingMethods")); // NOI18N
        m_overriders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_overridersActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        methodsPanel.add(m_overriders, gridBagConstraints);
        m_overriders.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(WhereUsedPanel.class, "LBL_FindOverridingMethods")); // NOI18N
        m_overriders.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(WhereUsedPanel.class).getString("ACSD_overriders")); // NOI18N

        m_usages.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(m_usages, org.openide.util.NbBundle.getMessage(WhereUsedPanel.class, "LBL_FindUsages")); // NOI18N
        m_usages.setMargin(new java.awt.Insets(10, 2, 2, 2));
        m_usages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_usagesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        methodsPanel.add(m_usages, gridBagConstraints);
        m_usages.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(WhereUsedPanel.class, "LBL_FindUsages")); // NOI18N
        m_usages.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(WhereUsedPanel.class).getString("ACSD_usages")); // NOI18N

        add(methodsPanel, java.awt.BorderLayout.CENTER);

        classesPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        classesPanel.add(jPanel2, gridBagConstraints);

        buttonGroup.add(c_subclasses);
        org.openide.awt.Mnemonics.setLocalizedText(c_subclasses, org.openide.util.NbBundle.getMessage(WhereUsedPanel.class, "LBL_FindAllSubtypes")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        classesPanel.add(c_subclasses, gridBagConstraints);
        c_subclasses.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(WhereUsedPanel.class).getString("ACSD_subclasses")); // NOI18N

        buttonGroup.add(c_usages);
        c_usages.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(c_usages, org.openide.util.NbBundle.getMessage(WhereUsedPanel.class, "LBL_FindUsages")); // NOI18N
        c_usages.setMargin(new java.awt.Insets(4, 2, 2, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        classesPanel.add(c_usages, gridBagConstraints);
        c_usages.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(WhereUsedPanel.class).getString("ACSD_usages")); // NOI18N

        buttonGroup.add(c_directOnly);
        org.openide.awt.Mnemonics.setLocalizedText(c_directOnly, org.openide.util.NbBundle.getMessage(WhereUsedPanel.class, "LBL_FindDirectSubtypesOnly")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        classesPanel.add(c_directOnly, gridBagConstraints);
        c_directOnly.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(WhereUsedPanel.class).getString("ACSD_directOnly")); // NOI18N

        add(classesPanel, java.awt.BorderLayout.CENTER);

        commentsPanel.setLayout(new java.awt.BorderLayout());
        commentsPanel.add(label, java.awt.BorderLayout.NORTH);

        searchInComments.setSelected(((Boolean) RefactoringModule.getOption("searchInComments.whereUsed", //NOI18N
            Boolean.FALSE)).booleanValue());
org.openide.awt.Mnemonics.setLocalizedText(searchInComments, org.openide.util.NbBundle.getBundle(WhereUsedPanel.class).getString("LBL_SearchInComents")); // NOI18N
searchInComments.setEnabled(false);
searchInComments.setMargin(new java.awt.Insets(10, 14, 2, 2));
searchInComments.addItemListener(new java.awt.event.ItemListener() {
    public void itemStateChanged(java.awt.event.ItemEvent evt) {
        searchInCommentsItemStateChanged(evt);
    }
    });
    commentsPanel.add(searchInComments, java.awt.BorderLayout.CENTER);
    searchInComments.getAccessibleContext().setAccessibleDescription(searchInComments.getText());

    add(commentsPanel, java.awt.BorderLayout.NORTH);

    scopeLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(WhereUsedPanel.class, "LBL_Scope_MNEM").charAt(0));
    scopeLabel.setLabelFor(scope);
    scopeLabel.setText(org.openide.util.NbBundle.getMessage(WhereUsedPanel.class, "LBL_Scope")); // NOI18N

    scope.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            scopeActionPerformed(evt);
        }
    });

    org.jdesktop.layout.GroupLayout scopePanelLayout = new org.jdesktop.layout.GroupLayout(scopePanel);
    scopePanel.setLayout(scopePanelLayout);
    scopePanelLayout.setHorizontalGroup(
        scopePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(scopePanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(scopeLabel)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(scope, 0, 283, Short.MAX_VALUE)
            .addContainerGap())
    );
    scopePanelLayout.setVerticalGroup(
        scopePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
        .add(scopeLabel)
        .add(scope, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE)
    );

    add(scopePanel, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void searchInCommentsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_searchInCommentsItemStateChanged
        // used for change default value for searchInComments check-box.
        // The value is persisted and then used as default in next IDE run.
        Boolean b = evt.getStateChange() == ItemEvent.SELECTED ? Boolean.TRUE : Boolean.FALSE;
        RefactoringModule.setOption("searchInComments.whereUsed", b); // NOI18N
    }//GEN-LAST:event_searchInCommentsItemStateChanged

    private void m_isBaseClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_isBaseClassActionPerformed
        parent.stateChanged(null);
    }//GEN-LAST:event_m_isBaseClassActionPerformed

    private void m_overridersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_overridersActionPerformed
        parent.stateChanged(null);
    }//GEN-LAST:event_m_overridersActionPerformed

    private void m_usagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_usagesActionPerformed
        parent.stateChanged(null);
    }//GEN-LAST:event_m_usagesActionPerformed

    private void scopeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scopeActionPerformed
        RefactoringModule.setOption("whereUsed.scope", scope.getSelectedIndex()); // NOI18N
    }//GEN-LAST:event_scopeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JRadioButton c_directOnly;
    private javax.swing.JRadioButton c_subclasses;
    private javax.swing.JRadioButton c_usages;
    private javax.swing.JPanel classesPanel;
    private javax.swing.JPanel commentsPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel label;
    private javax.swing.JCheckBox m_isBaseClass;
    private javax.swing.JCheckBox m_overriders;
    private javax.swing.JCheckBox m_usages;
    private javax.swing.JPanel methodsPanel;
    private javax.swing.JComboBox scope;
    private javax.swing.JLabel scopeLabel;
    private javax.swing.JPanel scopePanel;
    private javax.swing.JCheckBox searchInComments;
    // End of variables declaration//GEN-END:variables

    public boolean isMethodFromBaseClass() {
        return m_isBaseClass.isSelected();
    }

    public boolean isMethodOverriders() {
        return m_overriders.isSelected();
    }

    public boolean isClassSubTypes() {
        return c_subclasses.isSelected();
    }

    public boolean isClassSubTypesDirectOnly() {
        return c_directOnly.isSelected();
    }

    public boolean isMethodFindUsages() {
        return m_usages.isSelected();
    }

    public boolean isClassFindUsages() {
        return c_usages.isSelected();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension orig = super.getPreferredSize();
        return new Dimension(orig.width + 30, orig.height + 80);
    }

    public boolean isSearchInComments() {
        return searchInComments.isSelected();
    }

    public Component getComponent() {
        return this;
    }
    
    /*package*/ boolean isVirtualMethod() {
        return CsmKindUtilities.isMethod(refObject) && ((CsmMethod)refObject).isVirtual();
    }
    
    /*package*/ boolean isClass() {
        return CsmKindUtilities.isClass(refObject);
    }
    
    private void initFields() {
        this.refObject = getReferencedElement(origObject);
        this.name = getSearchElementName(refObject, this.name);
        System.err.println("initFields: refObject=" + refObject + "\n");
    }
    
    private CsmObject getReferencedElement(CsmObject csmObject) {
        if (csmObject instanceof CsmReference) {
            return getReferencedElement(((CsmReference)csmObject).getReferencedObject());
        } else {
            return csmObject;
        }
    }
    
    private String getSearchElementName(CsmObject csmObj, String defaultName) {
        String objName;
        if (CsmKindUtilities.isNamedElement(csmObj)) {
            objName = ((CsmNamedElement)csmObj).getName().toString();
        } else {
            System.err.println("Unhandled name for object " + csmObj);
            objName = defaultName;
        }
        return objName;
    }   

    private CsmMethod getOriginalVirtualMethod(CsmMethod csmMethod) {
        return csmMethod;
    }

    private String getString(String key) {
        return NbBundle.getBundle(WhereUsedPanel.class).getString(key);
    }
    
    private String getString(String key, String value) {
        return NbBundle.getMessage(WhereUsedPanel.class, key, value);
    }    
    
    private String getString(String key, String value1, String value2) {
        return NbBundle.getMessage(WhereUsedPanel.class, key, value1, value2);
    }    
    
    private static class JLabelRenderer extends JLabel implements ListCellRenderer {
        public JLabelRenderer () {
            setOpaque(true);
        }
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            
            // #89393: GTK needs name to render cell renderer "natively"
            setName("ComboBox.listRenderer"); // NOI18N
            
            if ( value != null ) {
                setText(((JLabel)value).getText());
                setIcon(((JLabel)value).getIcon());
            }
            
            if ( isSelected ) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            return this;
        }
    }    
}
