/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.developer.modules.loaders.properties;

import java.util.StringTokenizer;
import java.io.IOException;

import com.netbeans.ide.actions.*;
import com.netbeans.ide.filesystems.FileObject;
import com.netbeans.ide.loaders.MultiFileLoader;
import com.netbeans.ide.loaders.DataObject;
import com.netbeans.ide.loaders.MultiDataObject;
import com.netbeans.ide.loaders.FileEntry;
import com.netbeans.ide.util.actions.SystemAction;
import com.netbeans.ide.util.NbBundle;

/** Data loader which recognizes properties files.
* This class is final only for performance reasons,
* can be unfinaled if desired.
*
* @author Ian Formanek
*/
public final class PropertiesDataLoader extends MultiFileLoader {
                                        
  static final String PROPERTIES_EXTENSION = "properties";
                                        
  /** Character used to separate parts of bundle properties file name */                                                                                     
  public static final char PRB_SEPARATOR_CHAR = '_';

  /** Creates new PropertiesDataLoader */
  public PropertiesDataLoader() {
    super(PropertiesDataObject.class);
    initialize();
  }

  /** Does initialization. Initializes display name,
  * extension list and the actions. */
  private void initialize () {
    setDisplayName(NbBundle.getBundle(PropertiesDataLoader.class).
                   getString("PROP_PropertiesLoader_Name"));
//    ExtensionList ext = new ExtensionList();
//    ext.addExtension("properties");
//    setExtensions(ext);

    setActions(new SystemAction[] {
      SystemAction.get(OpenAction.class),
      SystemAction.get(ViewAction.class),
      null,
      SystemAction.get(CutAction.class),
      SystemAction.get(CopyAction.class),
      SystemAction.get(PasteAction.class),
      null,
      SystemAction.get(DeleteAction.class),
      SystemAction.get(RenameAction.class),
      null,
      SystemAction.get(NewAction.class),
      SystemAction.get(SaveAsTemplateAction.class),
      null,
      SystemAction.get(PropertiesAction.class)
    });
  }

  /** Creates new PropertiesDataObject for this FileObject.
  * @param fo FileObject
  * @return new PropertiesDataObject
  */
  protected MultiDataObject createMultiObject(final FileObject fo)
                            throws java.io.IOException {
    return new PropertiesDataObject(fo, this);
  }

  /** For a given file finds a primary file.
  * @param fo the file to find primary file for
  *
  * @return the primary file for the file or null if the file is not
  *   recognized by this loader
  */
  protected FileObject findPrimaryFile (FileObject fo) {
    if (fo.getExt().equalsIgnoreCase(PROPERTIES_EXTENSION)) {
      // returns a file whose name is the shortest valid prefix corresponding to an existing file
      String fName = fo.getName();
      int index = fName.indexOf(PRB_SEPARATOR_CHAR);
      while (index != -1) {
        FileObject candidate = fo.getParent().getFileObject(fName.substring(0, index), fo.getExt());
        if (candidate != null) {
          return candidate;     
        }  
        index = fName.indexOf(PRB_SEPARATOR_CHAR, index + 1);
      }
      return fo;                              
    }

    else 
      return null;
  }

  /** Creates the right primary entry for given primary file.
  *
  * @param primaryFile primary file recognized by this loader
  * @return primary entry for that file
  */
  protected MultiDataObject.Entry createPrimaryEntry (MultiDataObject obj, FileObject primaryFile) {
    return new PropertiesFileEntry(obj, primaryFile);
  }

  /** Creates right secondary entry for given file. The file is said to
  * belong to an object created by this loader.
  *
  * @param secondaryFile secondary file for which we want to create entry
  * @return the entry
  */
  protected MultiDataObject.Entry createSecondaryEntry (MultiDataObject obj, FileObject secondaryFile) {
    PropertiesFileEntry pfe = new PropertiesFileEntry(obj, secondaryFile);
    //((PropertiesDataObject)obj).registerEntryListener (pfe);
    return pfe;
  }

}

/*
* <<Log>>
*  6    Gandalf   1.5         5/12/99  Petr Jiricka    
*  5    Gandalf   1.4         5/11/99  Ian Formanek    Undone last change to 
*       compile
*  4    Gandalf   1.3         5/11/99  Petr Jiricka    
*  3    Gandalf   1.2         3/26/99  Ian Formanek    Fixed use of obsoleted 
*       NbBundle.getBundle (this)
*  2    Gandalf   1.1         3/9/99   Ian Formanek    Moved images to this 
*       package
*  1    Gandalf   1.0         1/22/99  Ian Formanek    
* $
*/
