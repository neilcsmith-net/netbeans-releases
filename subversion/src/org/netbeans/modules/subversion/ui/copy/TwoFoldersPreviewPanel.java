/*
 * OneFolderPreviewPanel.java
 *
 * Created on April 26, 2006, 2:20 PM
 */

package org.netbeans.modules.subversion.ui.copy;

/**
 *
 * @author  tst
 */
public class TwoFoldersPreviewPanel extends javax.swing.JPanel {
    
    /** Creates new form OneFolderPreviewPanel */
    public TwoFoldersPreviewPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/subversion/resources/icons/twofolders.png")));

        repositoryFolderTextField1.setBackground(new java.awt.Color(238, 238, 238));
        repositoryFolderTextField1.setEditable(false);
        repositoryFolderTextField1.setText("jTextField1");
        repositoryFolderTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        repositoryFolderTextField2.setBackground(new java.awt.Color(238, 238, 238));
        repositoryFolderTextField2.setEditable(false);
        repositoryFolderTextField2.setText("jTextField1");
        repositoryFolderTextField2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        localFolderTextField.setBackground(new java.awt.Color(238, 238, 238));
        localFolderTextField.setEditable(false);
        localFolderTextField.setText("jTextField1");
        localFolderTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 391, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, localFolderTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, repositoryFolderTextField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                    .add(repositoryFolderTextField2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.BASELINE, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.BASELINE, repositoryFolderTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(repositoryFolderTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(107, 107, 107)
                .add(localFolderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {localFolderTextField, repositoryFolderTextField2}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    final javax.swing.JTextField localFolderTextField = new javax.swing.JTextField();
    final javax.swing.JTextField repositoryFolderTextField1 = new javax.swing.JTextField();
    final javax.swing.JTextField repositoryFolderTextField2 = new javax.swing.JTextField();
    // End of variables declaration//GEN-END:variables
    
}
