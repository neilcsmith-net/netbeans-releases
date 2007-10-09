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

package org.netbeans.modules.profiler.ui.wizards.panels;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.netbeans.modules.profiler.ui.wizards.providers.TargetPlatformEnum;
import org.netbeans.lib.profiler.common.integration.IntegrationProvider;
import org.openide.util.NbBundle;

/**
 *
 * @author  Jaroslav Bachorik
 */
public class ManualIntegrationPanelUI extends javax.swing.JPanel implements ListDataListener {
  private ManualIntegrationPanel.Model model;

  private DefaultListModel listModel = null;

  /**
   * Creates new form ManualIntegrationPanelUI
   */
  ManualIntegrationPanelUI(ManualIntegrationPanel.Model model) {
    this.model = model;
    initComponents();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        stepsPanel = new org.netbeans.modules.profiler.ui.wizards.panels.components.StepsPanelComponent();
        comboJvms = new javax.swing.JComboBox();
        label = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setMaximumSize(new java.awt.Dimension(800, 600));
        setMinimumSize(new java.awt.Dimension(470, 300));
        setPreferredSize(new java.awt.Dimension(500, 400));

        stepsPanel.setHintText(org.openide.util.NbBundle.getMessage(ManualIntegrationPanelUI.class, "ManualIntegrationPanelUI.stepsPanel.hintText")); // NOI18N
        stepsPanel.setMinimumSize(new java.awt.Dimension(400, 200));
        stepsPanel.setPreferredSize(new java.awt.Dimension(500, 200));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/profiler/ui/wizards/panels/Bundle"); // NOI18N
        stepsPanel.setTitle(bundle.getString("ManualIntegrationStepsWizardPanelUI_ManualIntegrationHintMsg")); // NOI18N

        comboJvms.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Java" }));
        comboJvms.setMaximumSize(new java.awt.Dimension(470, 24));
        comboJvms.setMinimumSize(new java.awt.Dimension(200, 24));
        comboJvms.setPreferredSize(new java.awt.Dimension(466, 24));
        comboJvms.getModel().addListDataListener(this);

        label.setLabelFor(comboJvms);
        org.openide.awt.Mnemonics.setLocalizedText(label, bundle.getString("TargetJVMWizardPanelUI_SelectJvmString")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, stepsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, label)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, comboJvms, 0, 476, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(comboJvms, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(14, 14, 14)
                .add(stepsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                .addContainerGap())
        );

        comboJvms.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ManualIntegrationPanelUI.class, "ManualIntegrationPanelUI.comboJvms.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comboJvms;
    private javax.swing.JLabel label;
    private org.netbeans.modules.profiler.ui.wizards.panels.components.StepsPanelComponent stepsPanel;
    // End of variables declaration//GEN-END:variables
  
  public ComboBoxModel getJvmListModel() {
    return new DefaultComboBoxModel() {
      public Object getElementAt(int index) {
        return model.getSupportedJvms() != null ? model.getSupportedJvms().get(index) : null;
      }
      public Object getSelectedItem() {
        return model.getJvm();
      }
      public int getSize() {
        return model.getSupportedJvms() != null ? model.getSupportedJvms().size() : 0;
      }
      public void setSelectedItem(Object anItem) {
        model.setJvm((TargetPlatformEnum)anItem);
        showIntegrationHints();
      }
    };
  }
  
  public void refresh() {
    final List supportedJvms = model.getSupportedJvms();
    
    TargetPlatformEnum defaultPlatform = TargetPlatformEnum.JDK5;
    if (model.getJvm() == null || !supportedJvms.contains(model.getJvm())) {
      if (!supportedJvms.contains(defaultPlatform))
        defaultPlatform = (TargetPlatformEnum)supportedJvms.get(0);
    } else {
      defaultPlatform = model.getJvm();
    }
    
    ((DefaultComboBoxModel)comboJvms.getModel()).removeAllElements();
    for (Iterator it = model.getSupportedJvms().iterator(); it.hasNext();) {
      ((DefaultComboBoxModel)comboJvms.getModel()).addElement(it.next());
    }
    
    comboJvms.setSelectedItem(defaultPlatform);
//    comboJvms.invalidate();
    
    showIntegrationHints();
    
    org.openide.awt.Mnemonics.setLocalizedText(label, MessageFormat.format(NbBundle.getMessage(ManualIntegrationPanel.class, "TargetJVMWizardPanelUI_SelectJvmString"), new Object[]{model.getApplication()})); // NOI18N
  }
  
  private void showIntegrationHints() {
    IntegrationProvider.IntegrationHints hints = this.model.getIntegrationHints();
    
    stepsPanel.setTitle(MessageFormat.format(NbBundle.getMessage(ManualIntegrationPanel.class, "ManualIntegrationStepsWizardPanelUI_ManualIntegrationHintMsg"), new Object[]{model.getApplication()})); // NOI18N
    stepsPanel.setSteps(this.model.getIntegrationHints());
  }
  
  public void contentsChanged(ListDataEvent e) {
    model.setJvm((TargetPlatformEnum)comboJvms.getSelectedItem());
    showIntegrationHints();
  }
  public void intervalAdded(ListDataEvent e) {}
  public void intervalRemoved(ListDataEvent e) {}
}
