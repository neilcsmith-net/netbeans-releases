/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.openide.loaders;


import java.beans.*;
import java.io.IOException;
import java.lang.ref.*;
import java.util.*;
import java.util.logging.*;
import org.openide.filesystems.*;
import org.openide.util.*;

/** Watches a folder and its children.
 *
 * <p>{@link java.beans.PropertyChangeListener}s
 * may be registered which will be informed about changes in the ordered
 * children list. The {@link java.beans.PropertyChangeEvent}s fired by instances
 * of this class do neither contain information about the old value nor about
 * the new value of the children list.</p>
 *
 * <p>The list of children can be retrieved by calls to
 * the methods {@link #getChildren()} resp. {@link #getChildrenList()}. If you
 * want to filter the children which shall be included into the folder list,
 * call {@link #computeChildrenList(FolderListListener)}. The same is true
 * if you want to trigger children computation asynchronously. In this case
 * the implementation of {@link FolderListListener#finished(List)} shall be 
 * used to get informed about the result of the computation.</p>
 *
 * <p>To retrieve the appropriate instance of this class for a given folder
 *   call {@link #find(FileObject, boolean)}.</p>
*
* @author Jaroslav Tulach
*/
final class FolderList extends Object 
implements FileChangeListener, DataObject.Container {
    
    /* -------------------------------------------------------------------- */
    /* -- Constants ------------------------------------------------------- */
    /* -------------------------------------------------------------------- */
    
    /** serial version UID */
    static final long serialVersionUID = -592616022226761148L;

    /** priority for tasks that can be run later */
    private static final int LATER_PRIORITY = Thread.NORM_PRIORITY;

    /** request processor for recognizing of folders */
    private static final RequestProcessor PROCESSOR = new RequestProcessor (
                "Folder recognizer" // NOI18N
            );
    
    /** map of (FileObject, Reference (FolderList)) */
    private static final Map map = new WeakHashMap (101);
    
    /** refresh time in milliseconds */
    private static int REFRESH_TIME = -1; // will be updated in getRefreshTime

    /* -------------------------------------------------------------------- */
    /* -- Instance attributes --------------------------------------------- */
    /* -------------------------------------------------------------------- */

    /** data folder to work with */
    private FileObject folder;

    /** The task that computes the content of FolderList. There is also
    * only one computation task in the PROCESSOR for each FolderList.
    * Whenever a new change notification arrives (thru file listener)
    * the previous task is canceled (if not running) and new is created.
    */
    transient private RequestProcessor.Task refreshTask;
    /** task that is non-null if a setOrder has been called
     */
    transient private volatile RequestProcessor.Task comparatorTask;

    /** Primary files in this folder. Maps (FileObject, Reference (DataObject))
    */
    transient private HashMap primaryFiles = null;

    /** order of primary files (FileObject) */
    transient private List order;

    private static final Logger err = Logger.getLogger("org.openide.loaders.FolderList"); // NOI18N
    
    /** property change support */
    transient private PropertyChangeSupport pcs;
    
    /**
     * If true, this folder has been fully created (though it might
     * still be refreshing etc.). Used to avoid e.g. MDO.PROP_FILES
     * firing before the folder is ready.
     */
    transient private boolean folderCreated = false;
    
    /* -------------------------------------------------------------------- */
    /* -- Constructor (private) ------------------------------------------- */
    /* -------------------------------------------------------------------- */

    /**
    * @param df data folder to show
    */
    private FolderList (FileObject folder, boolean attach) {
        this.folder = folder;
        if (attach) {
            // creates object that handles all elements in array and
            // assignes it to the
            folder.addFileChangeListener (org.openide.filesystems.FileUtil.weakFileChangeListener (this, folder));
        }
    }

/*    final void reassign(DataFolder df, FileObject fo) {
        folder = df;
        // reassign is called from DataFolder.handleMove()
        // in this time the folder - df - does not have
        // setup the right primary file
        // so the fo is the new primary file for df
        fo.addFileChangeListener (WeakListener.fileChange (this, fo));
    }
 */
    public String toString () {
        return "FolderList{" + folder + "}"; // NOI18N
    }
    
    /* -------------------------------------------------------------------- */
    /* -- Factory method (static) ----------------------------------------- */
    /* -------------------------------------------------------------------- */
    
    /** A public method to get the correct list for given file object.
     *
     * @param folder the folder to find FolderList for
     * @param create if true than new FolderList should be created if it does not exists
     * @return the FolderList or null if create was false
     */
    public static FolderList find (FileObject folder, boolean create) {
        FolderList list = null;
        synchronized (FolderList.class) {
            Reference ref = (Reference)map.get (folder);
            list = ref == null ? null : (FolderList)ref.get ();
            if (list == null && create) {
                list = new FolderList (folder, true);
                map.put (folder, new SoftReference (list));
            }
        }
        return list;
    }
    
    /**
     * Has this FolderList finished creation of this list (at least once)?
     * @return true if it has been created (may still be refreshing), false if still in progress
     */
    public boolean isCreated() {
        return folderCreated;
    }
    
    /* -------------------------------------------------------------------- */
    /* -- Static methods -------------------------------------------------- */
    /* -------------------------------------------------------------------- */
    
    /** Checks whether the calling thread is the FolderRecognizer.
     */
    public static boolean isFolderRecognizerThread () {
        return PROCESSOR.isRequestProcessorThread ();
    }
    
    /* -------------------------------------------------------------------- */
    /* -- Static methods to inform FolderList for a given folder ---------- */
    /* -------------------------------------------------------------------- */
    
    /** A method used to notify the FolderList system that order has changed
     * for a given file object. 
     * 
     * @param folder the affected file object
     */
    public static void changedFolderOrder (FileObject folder) {
        FolderList list = find (folder, false);
        if (list != null) {
            list.changeComparator ();
        }
    }
    
    /** Called when a data system changed so much that there is a need for refresh
     * of a content of a folder.
     *
     * @param folder file object that can be affected
     */
    public static void changedDataSystem (FileObject folder) {
        FolderList list = find (folder, false);
        if (err.isLoggable(Level.FINE)) {
            err.fine("changedDataSystem: " + folder + " on " + Thread.currentThread()); // NOI18N
        }
        if (list != null) {
            list.refresh ();
        }
    }
    
    /* -------------------------------------------------------------------- */
    /* -- Folder content and content processing --------------------------- */
    /* -------------------------------------------------------------------- */

    /** Computes array of children associated
    * with this folder.
    */
    public DataObject[] getChildren () {
        List res = getChildrenList ();
        DataObject[] arr = new DataObject[res.size ()];
        res.toArray (arr);
        return arr;
    }

    /** List all children.
    * @return array with children
    */
    public List<DataObject> getChildrenList () {
        ListTask lt;
        try {
            DataObjectPool.getPOOL().enterPrivilegedProcessor (PROCESSOR);
            lt = getChildrenList (null);
            lt.task.waitFinished();
        } finally {
            DataObjectPool.getPOOL().exitPrivilegedProcessor (PROCESSOR);
        }
        assert lt.result != null;
        return lt.result;
    }

    /** Blocks if the processing of content of folder is in progress.
    */
    public void waitProcessingFinished () {
        Task t = comparatorTask;
        if (t != null) {
            t.waitFinished ();
        }
        
        t = refreshTask;
        if (t != null) {
            t.waitFinished ();
        }
    }

    /** Starts computation of children list asynchronously.
    */
    public RequestProcessor.Task computeChildrenList (FolderListListener filter) {
        return getChildrenList (filter).task;
    }

    private ListTask getChildrenList (FolderListListener filter) {
        ListTask lt = new ListTask (filter);
        int priority = Thread.currentThread().getPriority();

        // and then post your read task and wait
        lt.task = PROCESSOR.post (lt, 0, priority);
        return lt;
    }

    /** Setter for sort mode.
    */
    private synchronized void changeComparator () {
        final boolean LOG = err.isLoggable(Level.FINE);
        if (LOG) err.fine("changeComparator on " + folder);
        final RequestProcessor.Task previous = comparatorTask;
        final RequestProcessor.Task[] COMP = new RequestProcessor.Task[1];
        synchronized (COMP) {
            comparatorTask = PROCESSOR.post (new Runnable () {
                public void run () {
                    synchronized (COMP) {
                        if (previous != null) {
                            previous.waitFinished ();
                        }
                        // if has been notified
                        // change mode and regenerated children
                        if (primaryFiles != null) {
                            // the old children
                            if (LOG) err.fine("changeComparator on " + folder + ": get old");
                            List v = getObjects (null);
                            if (v.size () != 0) {
                                // the new children - also are stored to be returned next time from getChildrenList ()
                                order = null;
                                if (LOG) err.fine("changeComparator: get new");
                                List r = getObjects (null);
                                if (LOG) err.fine("changeComparator: fire change");
                                fireChildrenChange (r, v);
                            }
                        }
                        synchronized (FolderList.this) {
                            // clean  the task if is my own not assigned by somebody else
                            if (comparatorTask == COMP[0]) {
                                comparatorTask = null;
                            }
                        }
                    }
                }
            }, 0, Thread.MIN_PRIORITY);
            COMP[0] = comparatorTask;
        }
    }
    
    /* -------------------------------------------------------------------- */
    /* -- Refresh --------------------------------------------------------- */
    /* -------------------------------------------------------------------- */

    /** Refreshes the list of children.
     */
    public void refresh () {
        final long now = System.currentTimeMillis();
        final boolean LOG = err.isLoggable(Level.FINE);
        if (LOG) err.fine("refresh on " + folder + " @" + now);
        synchronized (this) {
            if (refreshTask == null) {
                refreshTask = PROCESSOR.post (new Runnable () {
                    public void run () {
                        RequestProcessor.Task t = comparatorTask;
                        if (t != null) {
                            // first of all finish setting up comparator
                            t.waitFinished ();
                        }
                        
                        if (LOG) err.fine("-- refresh on " + folder + ": now=" + now);
                        if (primaryFiles != null) {
                            // list of children is created, recreate it for new files
                            createBoth (null, true);
                        }
                    }
                }, getRefreshTime(), LATER_PRIORITY);
            } else {
                refreshTask.schedule(getRefreshTime());
            }
        }
    }

    /** Tries to read the value of the refresh time from a system property.
     * If the system property is not present a default value (currently 10)
     * is used.
     */
    private static int getRefreshTime() {
        if (REFRESH_TIME >= 0) {
            return REFRESH_TIME;
        }
        
        String sysProp = System.getProperty("org.openide.loaders.FolderList.refresh.interval"); // NOI18N
        if (sysProp != null) {
            try {
                REFRESH_TIME = Integer.parseInt(sysProp);
            } catch (NumberFormatException nfe) {
                Logger.global.log(Level.WARNING, null, nfe);
            }
        }
        if (REFRESH_TIME < 0) {
            REFRESH_TIME = 10;
        }
        return REFRESH_TIME;
    }
    
    /* -------------------------------------------------------------------- */
    /* -- Implementation of FileChangeListener ---------------------------- */
    /* -------------------------------------------------------------------- */

    /** Fired when a file has been changed. Refreshes the list when a 
     *  has be changed which up to now was not a member of the list but
     *  becomes a member as a consequence of the change.
     *
     * @param fe the event describing context where action has taken place
     */
    public void fileChanged (FileEvent fe) {
        final boolean LOG = err.isLoggable(Level.FINE);
        if (LOG) err.fine("fileChanged: " + fe);
        
        FileObject fo = fe.getFile ();
        
        /** condition fo.isValid () is hot fix for solving problem (similar to #17328)
         * inside filesystems  and should be reviewed.
         */
        if (fo.isData () && fo.isValid ()) {
            // when a data on the disk has been changed, look whether we
            // should reparse children
            if (primaryFiles != null) {
                // a file has been changed and the list of files is created
                try {
                    DataObject obj = DataObject.find (fo);
                    if (!primaryFiles.containsKey (obj.getPrimaryFile ())) {
                        // BUGFIX: someone who recognized the file and who isn't registered
                        // yet =>
                        // may be still not O.K.

                        // this primary file is not registered yet
                        // so recreate list of children
                        refresh();
                    }
                } catch (DataObjectNotFoundException ex) {
                    Logger.global.log(Level.WARNING, null, ex);
                    // file without data object => no changes
                }
            }
            
            // Resort if sorting by last modification or size:
            DataFolder.SortMode sortMode = getComparator().getSortMode();
            if (sortMode == DataFolder.SortMode.LAST_MODIFIED || sortMode == DataFolder.SortMode.SIZE) {
                changeComparator();
            }
        }
    }

    /** Fired when a file has been deleted.
    * @param fe the event describing context where action has taken place
    */
    public void fileDeleted (FileEvent fe) {
        final boolean LOG = err.isLoggable(Level.FINE);
        if (LOG) err.fine("fileDeleted: " + fe);
        //    boolean debug = fe.getFile().toString().equals("P"); // NOI18N
        //if (debug) System.out.println ("fileDeleted: " + fe.getFile ()); // NOI18N
        //if (debug) System.out.println ("fileList: " + fileList + " file: " + fileList.get (fe.getFile ())); // NOI18N
        if (primaryFiles == null || primaryFiles.containsKey (fe.getFile ())) {
            // one of main files has been deleted => reparse
            //if (debug) System.out.println ("RecreateChildenList"); // NOI18N
            refresh();
            //if (debug) System.out.println ("Done"); // NOI18N
        }
    }

    /** Fired when a new file has been created. This action can only be
    * listened in folders containing the created file up to the root of
    * file system.
    *
    * @param fe the event describing context where action has taken place
    */
    public void fileDataCreated (FileEvent fe) {
        final boolean LOG = err.isLoggable(Level.FINE);
        if (LOG) err.fine("fileDataCreated: " + fe);
        refresh();
    }

    /** Fired when a new file has been created. This action can only be
    * listened in folders containing the created file up to the root of
    * file system.
    *
    * @param fe the event describing context where action has taken place
    */
    public void fileFolderCreated (FileEvent fe) {
        final boolean LOG = err.isLoggable(Level.FINE);
        if (LOG) err.fine("fileFolderCreated: " + fe);
        refresh();
    }

    /** Fired when a new file has been renamed.
    *
    * @param fe the event describing context where action has taken place
    */
    public void fileRenamed (FileRenameEvent fe) {
        final boolean LOG = err.isLoggable(Level.FINE);
        if (LOG) err.fine("fileRenamed: " + fe);
        refresh();
        // Typically order may change as a result (#13820):
        changeComparator();
    }

    /** Fired when a file attribute has been changed.
    *
    * @param fe the event describing context where action has taken place
    */
    public void fileAttributeChanged(FileAttributeEvent fe) {
        final boolean LOG = err.isLoggable(Level.FINE);
        if (LOG) err.fine("fileAttributeChanged: " + fe);
        // update list when attrs defining order were changed
        if (fe.getFile() == folder) {
            /** Means one of attributes were changed*/
            if (fe.getName() == null) {
                changeComparator();
                return;
            }
            
            if (DataFolder.EA_ORDER.equals(fe.getName()) ||
            DataFolder.EA_SORT_MODE.equals(fe.getName()) ||
            -1 != fe.getName().indexOf("/")) {
                changeComparator();
            }
        }
    }
        
    /* -------------------------------------------------------------------- */
    /* -- Processing methods (only called in PROCESSOR) ------------------- */
    /* -------------------------------------------------------------------- */
    
    /** The comparator for this file objects.
     * @return the comparator to use
     */
    private FolderOrder getComparator () {
        return FolderOrder.findFor (folder);
    }

    /** Getter for list of children.
    * @param f filter to be notified about additions
    * @return List with DataObject types
    */
    private List getObjects (FolderListListener f) {
        final boolean LOG = err.isLoggable(Level.FINE);
        if (LOG) err.fine("getObjects on " + folder);
        List res;
        if (primaryFiles == null) {
            res = createBoth (f, false);
        } else {
            if (order != null) {
                res = createObjects (order, primaryFiles, f);
            } else {
                res = createObjects (primaryFiles.keySet (), primaryFiles, f);
                res = carefullySort (res, getComparator ());
                order = createOrder (res);
            }
        }
        return res;
        /* createChildrenAndFiles ();/*
        ArrayList v = (Collection)childrenList.get ();
        //if (debug) System.out.println ("Children list xxxxxxxxxxxxxx");
        if (v == null) {
        //if (debug) System.out.println ("Create them xxxxxxxxxxxx");
          v = createChildrenList (f);
        //if (debug) System.out.println ("result: " + v);
    }
        return v;*/
    }

    /** Sort a list of DataObject's carefully.
     * The supplied comparator should supply a basic ordering,
     * and may also have an associated overriding partial ordering.
     * If the partial ordering is given and is self-contradictory,
     * it will be ignored and a warning issued.
     * @param l the list to sort
     * @param c a comparator and maybe partial comparator to use
     * @return the sorted list (may or may not be the same)
     */
    private /*static*/ List carefullySort (List l, FolderOrder c) {
        final boolean LOG = err.isLoggable(Level.FINE);
        if (LOG) err.fine("carefullySort on " + folder);
        // Not quite right: topologicalSort will not guarantee that these are left alone,
        // even if the constraints do not mention files in the existing folder order.
        // Adding constraints between adjacent pairs in the existing folder order is
        // not good either, since that could produce an inconsistency relative to
        // the explicitly specified constraints. E.g. you have:
        // {a, b, c, d, e, x, y} Folder-Order=[a, b, c, d, e] c/x c/y x/d y/d
        // This will currently produce the order: [a, b, c, e, x, y, d]
        // If you had the existing folder order [a, b, d, c, e], then trying to make
        // the sort stabler would just cause it to fail. XXX could try to add in the
        // stabilizing constraints first, and if that fails, try again without them...
        Collections.sort (l, c);
        Map constraints = c.getOrderingConstraints(l);
        if (constraints == null) {
            return l;
        } else {
            if (LOG) err.fine("carefullySort: partial orders");
            
            try {
                return Utilities.topologicalSort(l, constraints);
            } catch (TopologicalSortException ex) {
                List corrected = ex.partialSort();
                if (err.isLoggable(Level.WARNING)) {
                    err.warning("Note: folder " + folder + " cannot be consistently sorted due to ordering conflicts."); // NOI18N
                    err.log(Level.WARNING, null, ex);
                    err.warning("Using partial sort: " + corrected); // NOI18N
                }
                return corrected;
            }
        }
    }

    /** Creates list of primary files from the list of data objects.
    * @param list list of DataObject
    * @return list of FileObject
    */
    private static List createOrder (List list) {
        int size = list.size ();
        List res = new ArrayList (size);

        for (int i = 0; i < size; i++) {
            res.add (((DataObject)list.get (i)).getPrimaryFile ());
        }

        return res;
    }

    /** Creates array of data objects from given order
    * and mapping between files and data objects.
    *
    * @param order list of FileObjects that define the order to use
    * @param map mapping (FileObject, Reference (DataObject)) to create data objects from
    * @param f filter that is notified about additions - only items
    * which are accepted by the filter will be added. Null means no filtering.
    * @return array of data objects
    */
    private /*static*/ List createObjects (
        Collection order, Map map, FolderListListener f
    ) {
        final boolean LOG = err.isLoggable(Level.FINE);
        if (LOG) {
            err.fine("createObjects on " + folder);
        }
        int size = order.size ();

        Iterator it = order.iterator ();

        List res = new ArrayList (size);
        for (int i = 0; i < size; i++) {
            org.openide.filesystems.FileObject fo = (org.openide.filesystems.FileObject) it.next();

            if (LOG) {
                err.fine("  iterating" + fo);
            }
            if (!fo.isValid()) {
                if (LOG) {
                    err.fine("    not valid, continue");
                }
                continue;
            }
            java.lang.ref.Reference ref = (java.lang.ref.Reference) map.get(fo);
            org.openide.loaders.DataObject obj = ref != null ? (org.openide.loaders.DataObject) ref.get()
                                                             : null;

            if (obj == null) {
                // try to find new data object
                if (LOG) {
                    err.fine("    reference is " + ref + " obj is " + obj);
                }
                try {
                    obj = org.openide.loaders.DataObject.find(fo);
                    ref = new java.lang.ref.SoftReference(obj);
                    map.put(fo, ref);
                }
                catch (org.openide.loaders.DataObjectNotFoundException ex) {
                    Logger.global.log(Level.WARNING, null, ex);
                }
            }
            // add if accepted
            if (obj != null) {
                if (LOG) {
                    err.fine("    deliver: ref is " + ref + " obj is " + obj);
                }
                // JST: Cannot be avoided otherwise DataObject.files () can be unconsistent
                // avoid to checkFiles(this)
                // obj.recognizedByFolder();
                if (f == null) {
                    // accept all objects
                    res.add(obj);
                } else {
                    // allow the listener f to filter
                    // objects in the array res
                    f.process(obj, res);
                }
            }
        }

        if (f != null) {
            if (LOG) {
                err.fine("  finished: " + res); // NOI18N
            }
            f.finished (res);
        }
        
        if (LOG) {
            err.fine("createObjects ends on " + folder); // NOI18N
        }
        return res;
    }

    /** Scans for files in the folder and creates representation for
     * children. Fires info about changes in the nodes.
     *
     * @param filter listener to addition of nodes or null
     * @param notify true if changes in the children should be fired
     * @return vector of children
     */
    private List createBoth (FolderListListener filter, boolean notify) {
        final boolean LOG = err.isLoggable(Level.FINE);
        if (LOG) err.fine("createBoth on " + folder);
        // map for (FileObject, DataObject)
        final HashMap file = new HashMap ();

        // list of all processed objects
        List all = new ArrayList ();
        // result list to return from the method
        List res = new ArrayList ();

        // map of current objects (FileObject, DataObject)
        final HashMap remove = primaryFiles == null ?
                               new HashMap () : (HashMap)primaryFiles.clone ();

        // list of new objects to add
        final List add = new ArrayList ();

        DataLoaderPool pool = DataLoaderPool.getDefault();

        // hashtable with FileObjects that are marked to be recognized
        // and that is why being out of enumeration
        final HashSet marked = new HashSet ();
        DataLoader.RecognizedFiles recog = new DataLoader.RecognizedFiles () {
                                               /** Adds the file object to the marked hashtable.
                                               * @param fo file object (can be <CODE>null</CODE>)
                                               */
                                               public void markRecognized (FileObject fo) {
                                                   if (fo != null) {
                                                       marked.add (fo);
                                                   }
                                               }
                                           };
        // enumeration of all files in the folder
        Enumeration en = folder.getChildren (false);
        while (en.hasMoreElements ()) {
            FileObject fo = (FileObject)en.nextElement ();
            if (!marked.contains (fo)) {
                // the object fo has not been yet marked as recognized
                // => continue in computation
                DataObject obj;
                try {
                    obj = pool.findDataObject (fo, recog);
                } catch (DataObjectExistsException ex) {
                    // use existing data object
                    obj = ex.getDataObject ();
                } catch (IOException ex) {
                    // data object not recognized or not found
                    obj = null;
                    Exceptions.printStackTrace(ex);
                }

                if (obj != null) {
                    // adds object to data if it is not already there

                    // avoid to checkFiles(this)
                    obj.recognizedByFolder();

                    // primary file
                    FileObject primary = obj.getPrimaryFile ();

                    boolean doNotRemovePrimaryFile = false;
                    if (!file.containsKey (primary)) {
                        // realy added object, test if it is new

                        // if we have not created primaryFiles before, then it is new
                        boolean goIn = primaryFiles == null;
                        if (!goIn) {
                            Reference r = (Reference)primaryFiles.get (primary);
                            // if its primary file is not between original primary files
                            // then data object is new
                            goIn = r == null;
                            if (!goIn) {
                                // if the primary file is there, but the previous data object
                                // exists and is different, then this one is new
                                DataObject obj2 = (DataObject)r.get ();
                                goIn = obj2 == null || obj2 != obj;
                                if (goIn) {
                                    doNotRemovePrimaryFile = true;
                                }
                            }
                        }

                        if (goIn) {
                            // realy new
                            add.add (obj);
                            /* JST: In my opinion it should not be here
                            * so I moved this out of this if. Is it ok?

                            if (filter != null) {
                              // fire info about addition
                              filter.acceptDataObject (obj);
                        }
                            */
                        }
                        // adds the object
                        all.add (obj);
                        if (filter == null) {
                            res.add (obj);
                        } else {
                            filter.process (obj, res);
                        }
                    }

                    if (!doNotRemovePrimaryFile) {
                        // this object exists it should not be removed
                        remove.remove (primary);
                    }

                    // add it to the list of primary files
                    file.put (primary, new SoftReference (obj));
                } else {
                    // 1. nothing to add to data object list
                    // 2. remove this object if it was in list of previous ones
                    // 3. do not put the file into list of know primary files
                    // => do nothing at all
                }
            }
        }

        // !!! section that fires info about changes should be here !!!

        // now file contains newly inserted files
        // data contains data objects
        // remove contains data objects that should be removed
        // add contains data object that were added

        primaryFiles = file;

        all = carefullySort (all, getComparator ());
        order = createOrder (all);
        if (all.size () == res.size ()) {
            // assume no filtering has been done
            res = all;
        } else {
            // sort also content of res
            res = carefullySort (res, getComparator ());
        }
            

        ////if (debug) System.out.println ("Notified: " + notified + " added: " + add.size () + " removed: " + remove.size ()); // NOI18N
        if (notify) {
            fireChildrenChange (add, remove.keySet ());
        }

        // notify the filter
        if (LOG) {
            err.fine("Notifying filter: " + filter); // NOI18N
        }
        if (filter != null) {
            filter.finished (res);
        }

        return res;
    }
    
    /* -------------------------------------------------------------------- */
    /* -- PropertyChangeListener management ------------------------------- */
    /* -------------------------------------------------------------------- */

    /** Fires info about change of children to the folder.
    * @param add added data objects
    * @param removed removed data objects
    */
    private void fireChildrenChange (Collection add, Collection removed) {
        if (pcs != null) {
            if (!add.isEmpty() || !removed.isEmpty()) {
                pcs.firePropertyChange (PROP_CHILDREN, null, null);
            }
        }
    }

    /** Removes property change listener.
     * @param l the listener
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (pcs != null) {
            pcs.removePropertyChangeListener (l);
        }
    }
    
    /** Adds a listener.
     * @param l the listener
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
        if (pcs == null) {
            pcs = new PropertyChangeSupport(this);
        }
        pcs.addPropertyChangeListener(l);
    }
    
    /* -------------------------------------------------------------------- */
    /* -- Inner class ListTask -------------------------------------------- */
    /* -------------------------------------------------------------------- */
    
    /** Task that holds result and also task. Moreover
    * can do the computation.
    */
    private final class ListTask implements Runnable {
        private FolderListListener filter;

        public ListTask (FolderListListener filter) {
            this.filter = filter;
        }

        public List result;
        public RequestProcessor.Task task;

        public void run () {
            final boolean LOG = err.isLoggable(Level.FINE);
            if (LOG) err.fine("ListTask.run 1 on " + folder);
            // invokes the refresh task before we do anything else
            if (comparatorTask != null) {
                comparatorTask.waitFinished ();
            }
            if (refreshTask != null) {
                refreshTask.waitFinished ();
            }
            err.fine("ListTask.run 2");

            result = getObjects (filter);
            assert result != null;
            err.fine("ListTask.run 3");
            
            folderCreated = true;
        }
        
        public String toString() {
            return "ListTask@" + Integer.toHexString(System.identityHashCode(this)) + "[" + folder + "]"; // NOI18N
        }
    }
    

}
