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

import com.netbeans.ide.TopManager;
import com.netbeans.ide.filesystems.FileObject;
import com.netbeans.ide.loaders.DataObject;
import com.netbeans.ide.loaders.DataFolder;
import com.netbeans.developer.impl.IDESettings;
import com.netbeans.ide.modules.ModuleInstall;

/**
* Module installation class for Form Editor
*
* @author Ian Formanek
*/
public class FormEditorModule implements ModuleInstall {

  /** Module installed for the first time. */
  public void installed () {
//    System.out.println("FormEditorModule: installed");

  // -----------------------------------------------------------------------------
  // 1. add bean infos
    addBeanInfos ();
    
  // -----------------------------------------------------------------------------
  // 2. create Component Palette under system
    createComponentPalette ();
  }

  /** Module installed again. */
  public void restored () {
//    System.out.println("FormEditorModule: restored");
    addBeanInfos ();
  }

  /** Module was uninstalled. */
  public void uninstalled () {
    // [PENDING - ask and delete ComponentPalette]
  }

  /** Module is being closed. */
  public boolean closing () {
    return true; // agree to close
  }
  
// -----------------------------------------------------------------------------
// Private methods
  
  private void addBeanInfos () {
    // [PENDING] IAN - Highly temporary solution
    IDESettings is = new IDESettings ();
    String[] bisp = is.getBeanInfoSearchPath ();
    String[] bisp2 = new String[bisp.length+1];
    System.arraycopy (bisp2, 0, bisp, 0, bisp.length);
    bisp2 [bisp2.length-1] = "com.netbeans.developer.modules.beaninfo.awt";
    is.setBeanInfoSearchPath (bisp2);
  }

  private void createComponentPalette () {
    FileObject root = TopManager.getDefault ().getRepository ().getDefaultFileSystem ().getRoot ();
    FileObject paletteFolder;
    if ((paletteFolder = root.getFileObject ("Palette")) == null) {
      try {
        paletteFolder = root.createFolder ("Palette");
      } catch (java.io.IOException e) {
        e.printStackTrace ();
        return;
      }
    }
    DataFolder paletteDataFolder = DataFolder.findFolder (paletteFolder);

    FileObject awtCategory = null; DataFolder awtFolder;
    FileObject swingCategory = null; DataFolder swingFolder;
    FileObject swing2Category = null; DataFolder swing2Folder;
    FileObject beansCategory = null; DataFolder beansFolder;
    FileObject layoutsCategory = null; DataFolder layoutsFolder;
    FileObject bordersCategory = null; DataFolder bordersFolder;

    // -----------------------------------------------------------------------------
    // Create AWT Category and components
    try {
      if ((awtCategory = paletteFolder.getFileObject ("AWT")) == null) awtCategory = paletteFolder.createFolder ("AWT");
      createInstances (awtCategory, defaultAWTComponents);
    } catch (java.io.IOException e) {
      e.printStackTrace ();
    }
    awtFolder = DataFolder.findFolder (awtCategory);
    
    // -----------------------------------------------------------------------------
    // Create Swing Category and components
    try {
      if ((swingCategory = paletteFolder.getFileObject ("Swing")) == null) swingCategory = paletteFolder.createFolder ("Swing");
      createInstances (swingCategory, defaultSwingComponents);
    } catch (java.io.IOException e) {
      e.printStackTrace ();
    }
    swingFolder = DataFolder.findFolder (swingCategory);

    // -----------------------------------------------------------------------------
    // Create Swing2 Category and components
    try {
      if ((swing2Category = paletteFolder.getFileObject ("Swing2")) == null) swing2Category = paletteFolder.createFolder ("Swing2");
      createInstances (swing2Category, defaultSwing2Components);
    } catch (java.io.IOException e) {
      e.printStackTrace ();
    }
    swing2Folder = DataFolder.findFolder (swing2Category);

    // -----------------------------------------------------------------------------
    // Create Beans Category and components
    try {
      if ((beansCategory = paletteFolder.getFileObject ("Beans")) == null) beansCategory = paletteFolder.createFolder ("Beans");
      createInstances (beansCategory, defaultBeansComponents);
    } catch (java.io.IOException e) {
      e.printStackTrace ();
    }
    beansFolder = DataFolder.findFolder (beansCategory);

    // -----------------------------------------------------------------------------
    // Create Layouts Category and components
    try {
      if ((layoutsCategory = paletteFolder.getFileObject ("Layouts")) == null) layoutsCategory = paletteFolder.createFolder ("Layouts");
      createInstances (layoutsCategory, defaultLayoutsComponents);
    } catch (java.io.IOException e) {
      e.printStackTrace ();
    }
    layoutsFolder = DataFolder.findFolder (layoutsCategory);

    // -----------------------------------------------------------------------------
    // Create Borders Category and components
    try {
      if ((bordersCategory = paletteFolder.getFileObject ("Borders")) == null) bordersCategory = paletteFolder.createFolder ("Borders");
      createInstances (bordersCategory, defaultBorders);
    } catch (java.io.IOException e) {
      e.printStackTrace ();
    }
    bordersFolder = DataFolder.findFolder (bordersCategory);

    try {
      paletteDataFolder.setOrder (new DataObject[] { awtFolder, swingFolder, swing2Folder, beansFolder, layoutsFolder, bordersFolder } );
    } catch (java.io.IOException e) {
      e.printStackTrace ();
    }
  }

  private void createInstances (FileObject folder, String[] classNames) {
    for (int i = 0; i < classNames.length; i++) {
      String fileName = formatName (classNames[i]);
      try {
        folder.createData (fileName, "instance");
      } catch (java.io.IOException e) {
        e.printStackTrace ();
      }
    }
  }

  private String formatName (String className) {
    return className.substring (className.lastIndexOf (".") + 1) + "[" + className.replace ('.', '-') + "]";
  }
  
// -----------------------------------------------------------------------------
// Default Palette contents
  
  /** The default AWT Components */
  private final static String[] defaultAWTComponents = new String[] {
    "java.awt.Label",
    "java.awt.Button",
    "java.awt.TextField",
    "java.awt.TextArea",
    "java.awt.Checkbox",
    "java.awt.Choice",
    "java.awt.List",
    "java.awt.Scrollbar",
    "java.awt.ScrollPane",
    "java.awt.Panel",
    "java.awt.MenuBar",
    "java.awt.PopupMenu",
  };

  /** The default Swing Components */
  private final static String[] defaultSwingComponents = new String[] {
    "javax.swing.JLabel",
    "javax.swing.JButton",
    "javax.swing.JCheckBox",
    "javax.swing.JRadioButton",
    "javax.swing.JComboBox",
    "javax.swing.JList",
    "javax.swing.JTextField",
    "javax.swing.JTextArea",
    "javax.swing.JToggleButton",
    "javax.swing.JPanel",
    "javax.swing.JTabbedPane",
    "javax.swing.JScrollBar",
    "javax.swing.JScrollPane",
    "javax.swing.JMenuBar",
    "javax.swing.JPopupMenu",
  };

  /** The default Swing Components - Swing2 category */
  private final static String[] defaultSwing2Components = new String[] {
    "javax.swing.JSlider",
    "javax.swing.JProgressBar",
    "javax.swing.JSplitPane",
    "javax.swing.JPasswordField",
    "javax.swing.JSeparator",
    "javax.swing.JTextPane",
    "javax.swing.JEditorPane",
    "javax.swing.JTree",
    "javax.swing.JTable",
    "javax.swing.JToolBar",
    "javax.swing.JInternalFrame",
    "javax.swing.JLayeredPane",
    "javax.swing.JDesktopPane",
    "javax.swing.JOptionPane",
  };

  /*
  private final static String[] defaultDBComponents = new String[] {
    "com.netbeans.sql.JDBCRowSet",
    "com.netbeans.sql.components.DataNavigator",
  };*/

  /** The default Swing Components - beans category */
  private final static String[] defaultBeansComponents = new String[] {
    // for future use.
  };

  /** The default Layout Components */
  private final static String[] defaultLayoutsComponents = new String[] {
    "com.netbeans.developerx.loaders.form.formeditor.layouts.DesignFlowLayout",
    "com.netbeans.developerx.loaders.form.formeditor.layouts.DesignBorderLayout",
    "com.netbeans.developerx.loaders.form.formeditor.layouts.DesignGridLayout",
    "com.netbeans.developerx.loaders.form.formeditor.layouts.DesignCardLayout",
    "com.netbeans.developerx.loaders.form.formeditor.layouts.DesignAbsoluteLayout",
    "com.netbeans.developerx.loaders.form.formeditor.layouts.DesignGridBagLayout",
    "com.netbeans.developerx.loaders.form.formeditor.layouts.DesignBoxLayout",
  };

  /** The default Swing Borders */
  private final static String[] defaultBorders = new String[] {
    "com.netbeans.developerx.loaders.form.formeditor.border.EmptyBorderInfo",
    "com.netbeans.developerx.loaders.form.formeditor.border.LineBorderInfo",
    "com.netbeans.developerx.loaders.form.formeditor.border.MatteIconBorderInfo",
    "com.netbeans.developerx.loaders.form.formeditor.border.MatteColorBorderInfo",
    "com.netbeans.developerx.loaders.form.formeditor.border.TitledBorderInfo",
    "com.netbeans.developerx.loaders.form.formeditor.border.EtchedBorderInfo",
    "com.netbeans.developerx.loaders.form.formeditor.border.BevelBorderInfo",
    "com.netbeans.developerx.loaders.form.formeditor.border.SoftBevelBorderInfo",
    "com.netbeans.developerx.loaders.form.formeditor.border.CompoundBorderInfo",
  };
  
}

/*
 * Log
 *  6    Gandalf   1.5         3/30/99  Ian Formanek    Creates default palette 
 *       on first installation
 *  5    Gandalf   1.4         3/30/99  Ian Formanek    
 *  4    Gandalf   1.3         3/27/99  Ian Formanek    
 *  3    Gandalf   1.2         3/26/99  Ian Formanek    
 *  2    Gandalf   1.1         3/22/99  Ian Formanek    
 *  1    Gandalf   1.0         3/22/99  Ian Formanek    
 * $
 */
