package org.netbeans.modules.uml.ui.controls.newdialog;

import javax.swing.JPanel;
import org.netbeans.modules.uml.core.metamodel.core.foundation.INamespace;

public final class AddPackageVisualPanel1 extends JPanel {
    
    /** Creates new form AddPackageVisualPanel1 */
    public AddPackageVisualPanel1(INewDialogPackageDetails details) {
        m_Details = details;
        initComponents();
    }
    
    public String getName() {
        return org.openide.util.NbBundle.getBundle(AddPackageVisualPanel1.class).getString("IDS_CREATEPACKAGE");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getBundle(AddPackageVisualPanel1.class).getString("IDS_PACKAGE")));
        jLabel1.setLabelFor(jTextField1);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getBundle(AddPackageVisualPanel1.class).getString("IDS_NAME"));

        jTextField1.setText(NewDialogUtilities.getDefaultPackageName());
        jTextField1.selectAll();
        jTextField1.requestFocus();
        jTextField1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(AddPackageVisualPanel1.class).getString("ACSD_NEW_PACKAGE_WIZARD_PACKAGENAME_TEXTFIELD"));

        jLabel2.setLabelFor(jComboBox1);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getBundle(AddPackageVisualPanel1.class).getString("IDS_NAMESPACE"));

        populateNamespaceCombobox();
        jComboBox1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(AddPackageVisualPanel1.class).getString("ACSD_NEW_PACKAGE_WIZARD_PACKAGENAMESPACE_COMBOBOX"));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 309, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 281, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {jLabel1, jLabel2}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.linkSize(new java.awt.Component[] {jComboBox1, jTextField1}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(15, 15, 15)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getBundle(AddPackageVisualPanel1.class).getString("IDS_SCOPEDDIAGRAM")));
        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox1, org.openide.util.NbBundle.getBundle(AddPackageVisualPanel1.class).getString("IDS_CREATESCOPED"));
        jCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(AddPackageVisualPanel1.class).getString("ACSD_NEW_PACKAGE_WIZARD_CREATESCOPEDDIAGRAM_CHECKBOX"));

        jLabel3.setLabelFor(jTextField2);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getBundle(AddPackageVisualPanel1.class).getString("IDS_DIAGRAMNAME"));
        jLabel3.setEnabled(false);

        jTextField2.setText(NewDialogUtilities.getDefaultDiagramName());
        jTextField2.setEnabled(false);
        jTextField2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(AddPackageVisualPanel1.class).getString("ACSD_NEW_PACKAGE_WIZARD_SCOPEDDIAGRAM_NAME_TEXTFIELD"));

        jLabel4.setLabelFor(jComboBox2);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getBundle(AddPackageVisualPanel1.class).getString("IDS_DIAGRAMTYPE"));
        jLabel4.setEnabled(false);

        populateDiagramTypeCombobox();
        jComboBox2.setEnabled(false);
        jComboBox2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(AddPackageVisualPanel1.class).getString("ACSD_NEW_PACKAGE_WIZARD_SCOPEDDIAGRAM_NAMESPACE_COMBOBOX"));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel3)
                            .add(jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 295, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jComboBox2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 298, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jCheckBox1)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(new java.awt.Component[] {jComboBox2, jTextField2}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel2Layout.linkSize(new java.awt.Component[] {jLabel3, jLabel4}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jCheckBox1)
                .add(14, 14, 14)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(15, 15, 15)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(jComboBox2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        performCheckBoxToggleAction();
        
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    
    private void performCheckBoxToggleAction() {
        if (jCheckBox1.isSelected()) {
            jLabel3.setEnabled(true);
            jLabel4.setEnabled(true);
            jTextField2.setEditable(true);
            jTextField2.setEnabled(true);
            jTextField2.selectAll();
            jTextField2.requestFocus();
            jComboBox2.setEnabled(true);
            // now default the text in the text box to what the package name is
            String defaultName = jTextField1.getText().trim();
            if (defaultName != null && defaultName.length() > 0) {
                jTextField2.setText(defaultName);
                jTextField2.selectAll();
            }
        } else {
            jLabel3.setEnabled(false);
            jLabel4.setEnabled(false);
            jTextField2.setEditable(false);
            jTextField2.setEnabled(false);
            jComboBox2.setEnabled(false);
        }
    }


    private void populateNamespaceCombobox() {
        //load namespaces
        if (jComboBox1 != null) {
            
            INamespace space = null;
            if(m_Details != null) {
                space = m_Details.getNamespace();
            }
            NewDialogUtilities.loadNamespace(jComboBox1, space);
        }
    }

    private void populateDiagramTypeCombobox() {
        //load diagram types
        if (jComboBox2 != null) {
            NewDialogUtilities.loadDiagramTypes(jComboBox2);
            // default to "Class Diagram"
            jComboBox2.setSelectedIndex(1);
        }
        
    }
    
     protected String getPackageName() {
        return jTextField1.getText().trim();
    }
    
    protected Object getPackageNamespace() {
        return jComboBox1.getSelectedItem();
    }
    
    protected Object getScopedDiagramName() {
        return jTextField2.getText().trim();
    }
    
    protected Object getScopedDiagramKind() {
        return jComboBox2.getSelectedItem();        
    }
    
    protected int getScopedDiagramType() {
        return jComboBox2.getSelectedIndex();
    }
    
    protected boolean isCheckboxSelected() {
        return jCheckBox1.isSelected();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
    private INewDialogPackageDetails m_Details;
}

