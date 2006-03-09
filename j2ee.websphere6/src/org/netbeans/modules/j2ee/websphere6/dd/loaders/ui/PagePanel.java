/*
 * PagePanel.java
 *
 */

package org.netbeans.modules.j2ee.websphere6.dd.loaders.ui;
import org.netbeans.modules.j2ee.websphere6.dd.beans.*;
import org.netbeans.modules.j2ee.websphere6.dd.loaders.webext.*;
import org.netbeans.modules.xml.multiview.*;
import org.netbeans.modules.xml.multiview.ui.*;

/**
 *
 * @author  dlm198383
 */
public class PagePanel extends javax.swing.JPanel {
    PageType page;
    
    
    /** Creates new form PagePanel */
    public PagePanel(PageType page) {
        this.page=page;
        initComponents();
        if(page!=null) {
            nameField.setText(page.getName());
            idField.setText(page.getXmiId());
            uriField.setText(page.getUri());
        }
    }
    
    public PagePanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        idLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        uriLabel = new javax.swing.JLabel();
        idField = new javax.swing.JTextField();
        nameField = new javax.swing.JTextField();
        uriField = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        idLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        idLabel.setText("ID:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 28;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 5);
        add(idLabel, gridBagConstraints);

        nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        nameLabel.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 17, 0, 5);
        add(nameLabel, gridBagConstraints);

        uriLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        uriLabel.setText("URI:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 17, 0, 5);
        add(uriLabel, gridBagConstraints);

        idField.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        add(idField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        add(nameField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        add(uriField, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField idField;
    private javax.swing.JLabel idLabel;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField uriField;
    private javax.swing.JLabel uriLabel;
    // End of variables declaration//GEN-END:variables
    public javax.swing.JTextField getIdField() {
        return idField;
    }
    public javax.swing.JTextField getNameField() {
        return nameField;
    }
    public javax.swing.JTextField getUriField() {
        return uriField;
    }
}
