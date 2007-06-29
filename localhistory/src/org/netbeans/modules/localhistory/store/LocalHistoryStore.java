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
package org.netbeans.modules.localhistory.store;

import java.beans.PropertyChangeListener;
import java.io.File;

/**
 *
 * @author Tomas Stupka
 */
// XXX what about multifile dataobjects ?
public interface LocalHistoryStore {
    
    
    /**
     * Indicates that the storage has changed.
     * First parameter: File which history has changed
     */
    public String PROPERTY_CHANGED = "LocalHistoryStore.changed";               // NOI18N  
        
    /**
     * Marks in the given files history that it was created with timestamp ts. The files content won't 
     * be copied into the storage until a change is notified via fileChange(). 
     * 
     * @param file 
     * @param ts 
     */
    public void fileCreate(File file, long ts);        
    
    /**
     * Stores the files actual state under the given timestamp and marks it as deleted
     * 
     * @param file the file which has to be stored
     * @param ts the timestamp under which the file has to be stored
     */
    public void fileDelete(File file, long ts);
    
    /**
     * Marks in toFile-s history that it was created with timestamp ts as a result from
     * being moved from fromFile. The toFile-s content won't 
     * be copied into the storage until a change is notified via fileChange(). 
     * 
     * @param fromFile
     * @param toFile 
     * @param ts 
     */
    public void fileCreateFromMove(File fromFile, File toFile, long ts);
    
    /**
     * Stores fromFile-s actual state under the given timestamp and 
     * marks that it has been moved to the toFile
     * 
     * @param from 
     * @param to 
     * @param ts 
     */
    public void fileDeleteFromMove(File fromFile, File toFile, long ts);
 
    /**
     * Stores the files actual state under the given timestamp
     * 
     * @param file the file which has to be stored
     * @param ts the timestamp under which the file has to be stored
     */
    public void fileChange(File file, long ts);                   
        
    /**
     * Sets a label for an entry represented by the given file and timestamp
     * 
     * @param file the file for which entry the label has to be set
     * @param ts timestamp
     * @param label the label to be set 
     */ 
    public void setLabel(File file, long ts, String label);    
    
    /**
     * Adds a property change listener
     * 
     * @param l the property change listener
     */
    public void addPropertyChangeListener(PropertyChangeListener l);
    
    /**
     * Removes a property change listener
     * 
     * @param l the property change listener
     */
    public void removePropertyChangeListener(PropertyChangeListener l);
    
    /**
     * Returns all entries for a file
     * 
     * @param file the file for which the entries are to be retrieved
     * @return StoreEntry[] all entries present in the storage
     */ 
    public StoreEntry[] getStoreEntries(File file);
    
    /**
     * Returns an entry representig the given files state in time ts
     * 
     * @param file the file for which the entries are to be retrieved
     * @param ts the time for which the StoreEntry has to retrieved
     * @return StoreEntry a StoreEntry representing the given file in time ts. 
     *         <tt>null</tt> if file is a directory or there is no entry with a timestamp &lt; <tt>ts</tt>
     */ 
    public StoreEntry getStoreEntry(File file, long ts);
    
    /**
     * Return an StoreEntry array representing the given root folders state 
     * in the history to the given timestamp ts. The files array contains the
     * actually existing files under root.
     * 
     * NOT REALY USED YET
     * 
     * @param root the folder for which the StoreEntry array has to be returned
     * @param files files which actually exist under root
     * @param ts timestamp to which teh history has to be retrieved
     * @return StoreEntry array representing the given root folders state 
     */ 
    public StoreEntry[] getFolderState(File root, File[] files, long ts);        
    
    /**
     * Returns StoreEntries for files which are directly 
     * under the given root folder and:
     * <ul>
     *  <li> their youngest entry is marked as deleted
     *  <li> or have an entry in the storage but don't exist under the given root anymore e.g. externaly deleted
     * </ul>
     * 
     * @param root 
     * @return an array of StoreEntries
     */ 
    public StoreEntry[] getDeletedFiles(File root);    
    
    /**
     * Deletes a StoreEntry from the storage represented by the given file and timestamp
     * 
     * @param file the file for which a StoreEntry has to be deleted
     * @param ts the timestamp for which a StoreEntry has to be deleted
     * 
     */ 
    public void deleteEntry(File file, long ts); 
        
    /**
     * Removes all history information from the storage which is older than now - ttl. 
     * 
     * @param ttl time to live
     */ 
    public void cleanUp(long ttl);    
}
