/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.developer.modules.loaders.form;

import java.beans.*;
import java.lang.reflect.Method;
import java.util.Vector;

import com.netbeans.ide.TopManager;
//import com.netbeans.developer.util.QuickSorter;
//import com.netbeans.developer.util.WindowToolkit;
import com.netbeans.ide.util.Utilities;

/** The MethodPicker is a form which allows user to pick one of methods 
* with specified required return type.
*
* @author  Ian Formanek
* @version 1.00, Aug 29, 1998
*/
public class MethodPicker extends javax.swing.JDialog {

  public static final int CANCEL = 0;
  public static final int OK = 1;

  /** Initializes the Form */
  public MethodPicker(java.awt.Frame parent, FormManager2 manager, RADComponent componentToSelect, Class requiredType) {
    super (parent != null ? parent : TopManager.getDefault ().getWindowManager ().getMainWindow (), true);

    this.manager = manager;
    this.requiredType = requiredType;
    initComponents ();

    setDefaultCloseOperation (javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
    addWindowListener (new java.awt.event.WindowAdapter () {
        public void windowClosing (java.awt.event.WindowEvent evt) {
          cancelDialog ();
        }
      }
    );
    
    // attach cancel also to Escape key
    getRootPane().registerKeyboardAction(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          cancelDialog ();
        }
      },
      javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, true),
      javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
    );
 
    Vector allComponents = new Vector ();
    allComponents.addElement (manager.getRADForm ().getTopLevelComponent ());
/*    RADComponent[] nodes = manager.getComponentsRoot ().getNonVisualsNode ().getSubComponents ();
    for (int i = 0; i < nodes.length; i++)
      allComponents.addElement (nodes[i]); */ 
    // [PENDING]
    
    addComponentsRecursively ((ComponentContainer)manager.getRADForm ().getTopLevelComponent (), allComponents); // [PENDING - incorrect cast]

    components = new RADComponent [allComponents.size ()];
    allComponents.copyInto (components);
    int selIndex = -1;
    for (int i = 0; i < components.length; i++) {
      componentsCombo.addItem (components[i].getName ());
      if ((componentToSelect != null) && (componentToSelect.equals (components[i])))
        selIndex = i;
    }

    if (selIndex != -1) {
      selectedComponent = components[selIndex];
      componentsCombo.setSelectedIndex (selIndex);
    }

    methodList.setSelectionMode (javax.swing.ListSelectionModel.SINGLE_SELECTION);
    updateMethodList ();
    
    // localize components
    setTitle ( java.text.MessageFormat.format (
        FormEditor.getFormBundle ().getString ("CTL_FMT_CW_SelectMethod"),
        new Object[] { Utilities.getShortClassName (requiredType) }
      )
    );
    componentLabel.setText (FormEditor.getFormBundle ().getString ("CTL_CW_Component")); // "Component:"
    okButton.setText ("OK"); // [PENDING]
        //com.netbeans.developer.util.NetbeansBundle.getBundle("com.netbeans.developer.locales.BaseBundle").getString ("CTL_OK"));   // "OK");
    cancelButton.setText ("Cancel"); // [PENDING]
        //com.netbeans.developer.util.NetbeansBundle.getBundle("com.netbeans.developer.locales.BaseBundle").getString ("CTL_CANCEL")); // "Cancel");
    parametersButton.setText (FormEditor.getFormBundle ().getString ("CTL_CW_Parameters")); // "Parameters"
    
    updateButtons ();

    pack ();
    FormUtils.centerWindow (this);
  }

  public java.awt.Dimension getPreferredSize () {
    java.awt.Dimension pref = super.getPreferredSize ();
    return new java.awt.Dimension (Math.max (pref.width, 250), Math.max (pref.height, 300));
  }
  
  int getReturnStatus () {
    return returnStatus;
  }

  RADComponent getSelectedComponent () {
    return selectedComponent;
  }

  MethodDescriptor getSelectedMethod () {
    if ((selectedComponent == null) || (methodList.getSelectedIndex () == -1))
      return null;
    return descriptors [methodList.getSelectedIndex ()];
  }

// ----------------------------------------------------------------------------
// private methods

  private void addComponentsRecursively (ComponentContainer cont, Vector vect) {
    RADComponent[] children = cont.getSubBeans ();
    for (int i = 0; i < children.length; i++) {
      vect.addElement (children[i]);
      if (children[i] instanceof ComponentContainer)
        addComponentsRecursively ((ComponentContainer)children[i], vect);
    }
  }

  private void updateMethodList () {
    RADComponent sel = getSelectedComponent ();
    if (sel == null) {
      methodList.setListData (new Object [0]);
      methodList.revalidate ();
      methodList.repaint ();
    } else {
      MethodDescriptor[] descs = sel.getBeanInfo ().getMethodDescriptors ();
      Vector filtered = new Vector ();
      for (int i = 0; i < descs.length; i ++) {
        if (requiredType.isAssignableFrom (descs[i].getMethod ().getReturnType ()) &&            
            (descs[i].getMethod ().getParameterTypes ().length == 0)) // [PENDING - currently we allow only methods without params]
        {
          filtered.addElement (descs[i]);
        }
      }
      descriptors = new MethodDescriptor[filtered.size ()];
      filtered.copyInto (descriptors);

      // sort the properties by name
/*      QuickSorter sorter = new QuickSorter () {
        public final int compare(Object o1, Object o2) {
          return (((MethodDescriptor)o1).getName ()).compareTo(((MethodDescriptor)o2).getName ());
        }
      };
      sorter.sort (descriptors); */ 
      // [PENDING]

      String[] items = new String [descriptors.length];
      for (int i = 0; i < descriptors.length; i++)
        items[i] = FormUtils.getMethodName (descriptors[i]);
      methodList.setListData (items);
      methodList.revalidate ();
      methodList.repaint ();
    }
  }

  private void updateButtons () {
    parametersButton.setEnabled (false); // [PENDING - temporarily disabled]
    if ((getSelectedComponent () == null) || (getSelectedMethod () == null)) {
      okButton.setEnabled (false);
//      parametersButton.setEnabled (false);
    } else {
      if (getSelectedMethod ().getMethod ().getParameterTypes ().length > 0) {
        okButton.setEnabled (false);
//        parametersButton.setEnabled (true);
      } else {
        okButton.setEnabled (true);
//        parametersButton.setEnabled (false);
      }
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the FormEditor.
   */
  private void initComponents () {//GEN-BEGIN:initComponents
    addWindowListener (new java.awt.event.WindowAdapter () {
        public void windowClosing (java.awt.event.WindowEvent evt) {
          closeDialog (evt);
        }
      }
    );
    getContentPane ().setLayout (new java.awt.BorderLayout ());

    insidePanel = new javax.swing.JPanel ();
    insidePanel.setBorder (new javax.swing.border.EmptyBorder (new java.awt.Insets(8, 8, 3, 8)));
    insidePanel.setLayout (new java.awt.BorderLayout (0, 5));

      propertiesScrollPane = new javax.swing.JScrollPane ();

        methodList = new javax.swing.JList ();
        methodList.addListSelectionListener (new javax.swing.event.ListSelectionListener () {
            public void valueChanged (javax.swing.event.ListSelectionEvent evt) {
              methodListValueChanged (evt);
            }
          }
        );

      propertiesScrollPane.setViewportView (methodList);
      insidePanel.add (propertiesScrollPane, "Center");

      jPanel1 = new javax.swing.JPanel ();
      jPanel1.setLayout (new java.awt.BorderLayout (8, 0));

        componentLabel = new javax.swing.JLabel ();
        componentLabel.setText ("Component:");
        jPanel1.add (componentLabel, "West");

        componentsCombo = new javax.swing.JComboBox ();
        componentsCombo.addItemListener (new java.awt.event.ItemListener () {
            public void itemStateChanged (java.awt.event.ItemEvent evt) {
              componentsComboItemStateChanged (evt);
            }
          }
        );
        jPanel1.add (componentsCombo, "Center");

      insidePanel.add (jPanel1, "North");

    getContentPane ().add (insidePanel, "Center");

    buttonsPanel = new javax.swing.JPanel ();
    buttonsPanel.setBorder (new javax.swing.border.EmptyBorder (new java.awt.Insets(0, 5, 5, 5)));
    buttonsPanel.setLayout (new java.awt.BorderLayout ());

      leftButtonsPanel = new javax.swing.JPanel ();
      leftButtonsPanel.setLayout (new java.awt.FlowLayout (0, 5, 5));

        parametersButton = new javax.swing.JButton ();
        parametersButton.setText ("Parameters");
        leftButtonsPanel.add (parametersButton);

      buttonsPanel.add (leftButtonsPanel, "West");

      rightButtonsPanel = new javax.swing.JPanel ();
      rightButtonsPanel.setLayout (new java.awt.FlowLayout (2, 5, 5));

        okButton = new javax.swing.JButton ();
        okButton.setText ("OK");
        okButton.addActionListener (new java.awt.event.ActionListener () {
            public void actionPerformed (java.awt.event.ActionEvent evt) {
              previousButtonActionPerformed (evt);
            }
          }
        );
        rightButtonsPanel.add (okButton);

        cancelButton = new javax.swing.JButton ();
        cancelButton.setText ("Cancel");
        cancelButton.addActionListener (new java.awt.event.ActionListener () {
            public void actionPerformed (java.awt.event.ActionEvent evt) {
              cancelButtonActionPerformed (evt);
            }
          }
        );
        rightButtonsPanel.add (cancelButton);

      buttonsPanel.add (rightButtonsPanel, "East");

    getContentPane ().add (buttonsPanel, "South");

  }//GEN-END:initComponents

  private void methodListValueChanged (javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_methodListValueChanged
    if (methodList.getSelectedIndex () == -1)
      selectedMethod = null;
    else
      selectedMethod = descriptors[methodList.getSelectedIndex ()];
    updateButtons ();
  }//GEN-LAST:event_methodListValueChanged

  private void componentsComboItemStateChanged (java.awt.event.ItemEvent evt) {//GEN-FIRST:event_componentsComboItemStateChanged
    if (componentsCombo.getSelectedIndex () == -1)
      selectedComponent = null;
    else
      selectedComponent = components[componentsCombo.getSelectedIndex ()];
    updateMethodList ();
  }//GEN-LAST:event_componentsComboItemStateChanged

  private void previousButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButtonActionPerformed
    returnStatus = OK;
    setVisible (false);
  }//GEN-LAST:event_previousButtonActionPerformed

  private void cancelButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    cancelDialog ();
  }//GEN-LAST:event_cancelButtonActionPerformed

  /** Closes the dialog */
  private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:closeDialog
    cancelDialog ();
  }//GEN-LAST:closeDialog

  private void cancelDialog () {
    returnStatus = CANCEL;
    setVisible (false);
  }
  
// Variables declaration - do not modify//GEN-BEGIN:variables
  javax.swing.JPanel insidePanel;
  javax.swing.JPanel buttonsPanel;
  javax.swing.JScrollPane propertiesScrollPane;
  javax.swing.JPanel jPanel1;
  javax.swing.JList methodList;
  javax.swing.JLabel componentLabel;
  javax.swing.JComboBox componentsCombo;
  javax.swing.JPanel leftButtonsPanel;
  javax.swing.JPanel rightButtonsPanel;
  javax.swing.JButton okButton;
  javax.swing.JButton cancelButton;
  javax.swing.JButton parametersButton;
// End of variables declaration//GEN-END:variables


  private FormManager2 manager;
  private int returnStatus = CANCEL;

  private RADComponent[] components;
  private Class requiredType;
  private MethodDescriptor[] descriptors;
  private RADComponent selectedComponent;
  private MethodDescriptor selectedMethod;

}

/*
 * Log
 *  2    Gandalf   1.1         5/15/99  Ian Formanek    
 *  1    Gandalf   1.0         5/13/99  Ian Formanek    
 * $
 */

