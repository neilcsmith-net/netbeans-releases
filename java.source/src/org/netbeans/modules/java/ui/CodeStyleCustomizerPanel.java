/*
 * CodeStyleCustomizerPanel.java
 *
 * Created on May 20, 2008, 4:53 PM
 */

package org.netbeans.modules.java.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.editor.indent.api.IndentUtils;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Dusan Balek
 */
public class CodeStyleCustomizerPanel extends javax.swing.JPanel implements ActionListener {
    
    private static final String GLOBAL_OPTIONS_CATEGORY = "JavaOptions/Formating"; //NOI18N

    public static class Factory implements ProjectCustomizer.CompositeCategoryProvider {
 
        private static final String CATEGORY_CODE_STYLE = "CodeStyle"; // NOI18N

        public ProjectCustomizer.Category createCategory(Lookup context) {
            return context.lookup(Project.class) == null ? null : ProjectCustomizer.Category.create(
                    CATEGORY_CODE_STYLE, 
                    NbBundle.getMessage(Factory.class, "LBL_CategoryCodeStyle"), //NOI18N
                    null);
        }

        public JComponent createComponent(ProjectCustomizer.Category category, Lookup context) {
            CodeStyleCustomizerPanel customizerPanel = new CodeStyleCustomizerPanel(context);
            category.setStoreListener(customizerPanel);
            return customizerPanel;
        }
    }
    
    private final FormatingOptionsPanelController controller;    
    private final JComponent customizerComponent;
    private final Preferences preferences;
    
    /** Creates new form CodeStyleCustomizerPanel */
    private CodeStyleCustomizerPanel(Lookup context) {
        controller = new FormatingOptionsPanelController();
        customizerComponent = controller.getComponent(context);
        initComponents();
        customizerPanel.add(customizerComponent, BorderLayout.CENTER);
        preferences = ProjectUtils.getPreferences(context.lookup(Project.class), IndentUtils.class, true).node(FmtOptions.CODE_STYLE_PROFILE);
        String profile = preferences.get(FmtOptions.usedProfile, null);
        if (profile == null) {
            controller.loadFrom(FmtOptions.getGlobalPreferences());
            profile = FmtOptions.DEFAULT_PROFILE;
        } else {
            controller.update();
        }
        if (FmtOptions.DEFAULT_PROFILE.equals(profile))
            globalButton.doClick();
        else
            projectButton.doClick();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        group = new javax.swing.ButtonGroup();
        globalButton = new javax.swing.JRadioButton();
        editGlobalButton = new javax.swing.JButton();
        projectButton = new javax.swing.JRadioButton();
        loadButton = new javax.swing.JButton();
        customizerPanel = new javax.swing.JPanel();

        group.add(globalButton);
        globalButton.setText(org.openide.util.NbBundle.getMessage(CodeStyleCustomizerPanel.class, "LBL_CodeStyleCustomizer_Global")); // NOI18N
        globalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                globalButtonActionPerformed(evt);
            }
        });

        editGlobalButton.setText(org.openide.util.NbBundle.getMessage(CodeStyleCustomizerPanel.class, "LBL_CodeStyleCustomizer_EditGlobal")); // NOI18N
        editGlobalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editGlobalButtonActionPerformed(evt);
            }
        });

        group.add(projectButton);
        projectButton.setText(org.openide.util.NbBundle.getMessage(CodeStyleCustomizerPanel.class, "LBL_CodeStyleCustomizer_Project")); // NOI18N
        projectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectButtonActionPerformed(evt);
            }
        });

        loadButton.setText(org.openide.util.NbBundle.getMessage(CodeStyleCustomizerPanel.class, "LBL_CodeStyleCustomizer_Load")); // NOI18N
        loadButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });

        customizerPanel.setLayout(new java.awt.BorderLayout());

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(projectButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                    .add(globalButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
                .add(14, 14, 14)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(editGlobalButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(loadButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .add(layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(customizerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(globalButton)
                    .add(editGlobalButton))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectButton)
                    .add(loadButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(customizerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void globalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_globalButtonActionPerformed
    loadButton.setEnabled(false);
    setEnabled(customizerComponent, false);
}//GEN-LAST:event_globalButtonActionPerformed

private void projectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectButtonActionPerformed
    loadButton.setEnabled(true);
    setEnabled(customizerComponent, true);
}//GEN-LAST:event_projectButtonActionPerformed

private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
    JFileChooser chooser = ProjectChooser.projectChooser();
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        File f = chooser.getSelectedFile();
        FileObject fo = FileUtil.toFileObject(f);
        if (fo != null) {
            try {
                Project p = ProjectManager.getDefault().findProject(fo);
                controller.loadFrom(FmtOptions.getProjectPreferences(p));
            } catch (Exception e) {}
        }
    }
}//GEN-LAST:event_loadButtonActionPerformed

private void editGlobalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editGlobalButtonActionPerformed
    OptionsDisplayer.getDefault().open(GLOBAL_OPTIONS_CATEGORY);
}//GEN-LAST:event_editGlobalButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel customizerPanel;
    private javax.swing.JButton editGlobalButton;
    private javax.swing.JRadioButton globalButton;
    private javax.swing.ButtonGroup group;
    private javax.swing.JButton loadButton;
    private javax.swing.JRadioButton projectButton;
    // End of variables declaration//GEN-END:variables

    public void actionPerformed(ActionEvent e) {
        String profile = globalButton.isSelected() ? FmtOptions.DEFAULT_PROFILE
                : FmtOptions.PROJECT_PROFILE;
        preferences.put(FmtOptions.usedProfile, profile);
        controller.applyChanges();
    }
    
    private void setEnabled(Component component, boolean enabled) {
        component.setEnabled(enabled);
        if (component instanceof Container) {
            for (Component c : ((Container)component).getComponents())
                setEnabled(c, enabled);
        }
    }
}
