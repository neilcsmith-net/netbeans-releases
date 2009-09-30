/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 *
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

package org.netbeans.modules.settings;

import java.beans.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileObject;

import org.netbeans.spi.settings.Convertor;
import org.netbeans.spi.settings.Saver;
import org.openide.util.Exceptions;

/** Support handles automatic storing/upgrading; notifies about changes in file.
 *
 * @author  Jan Pokorsky
 */
final class SaveSupport {
    /** property means setting is changed and should be changed */
    public final static String PROP_SAVE = "savecookie"; //NOI18N
    /** property means setting file content is changed */
    public final static String PROP_FILE_CHANGED = "fileChanged"; //NOI18N
    /** data object name cached in the attribute to prevent instance creation when
     * its node is displayed.
     * @see org.openide.loaders.InstanceDataObject#EA_NAME
     */
    static final String EA_NAME = "name"; // NOI18N
    
    /** support for PropertyChangeListeners */
    private PropertyChangeSupport changeSupport;
    
    /** convertor for possible format upgrade */
    private Convertor convertor;
    /** SaveCookie implementation */
    private final SaveCookieImpl instToSave = new SaveCookieImpl();
    /** setting is already changed */
    private boolean isChanged = false;
    /** .settings file */
    private final FileObject file;
    /** reference to setting object */
    private final java.lang.ref.SoftReference<Object> instance;
    private final InstanceProvider ip;
    /** remember whether the DataObject is a template or not; calling isTemplate() is slow  */
    private Boolean knownToBeTemplate = null;
    
    /** Creates a new instance of SaveSupport
     * @param ip instance provider
     * @param inst setting object
     */
    public SaveSupport(InstanceProvider ip, Object inst) {
        this.ip = ip;
        this.instance = new java.lang.ref.SoftReference<Object>(inst);
        this.file = ip.getFile();
    }
    
    /** get convertor for possible upgrade; can be null */
    private Convertor getConvertor() {
        return convertor;
    }
    
    /** try to find out convertor for possible upgrade and cache it; can be null */
    private Convertor initConvertor() {
        Object inst = instance.get();
        if (inst == null) {
            throw new IllegalStateException("setting object cannot be null: " + ip);// NOI18N
        }
        
        try {
            FileObject newProviderFO = Env.findProvider(inst.getClass());
            if (newProviderFO != null) {
                if (getPublicID(newProviderFO).equals(getPublicID(ip.getProvider()))) {
                    // nothing to upgrade
                    convertor = ip.getConvertor();
                    return convertor;
                }
                Object attrb = newProviderFO.getAttribute(Env.EA_CONVERTOR);
                if (attrb == null || !(attrb instanceof Convertor)) {
                    throw new IOException("cannot create convertor: " + attrb + ", provider: " + newProviderFO); //NOI18N
                } else {
                    convertor = (Convertor) attrb;
                    return convertor;
                }
            }
            convertor = ip.getConvertor();
        } catch (IOException ex) {
            Logger.getLogger(SaveSupport.class.getName()).log(Level.WARNING, null, ex);
        }
        return convertor;
    }
    
    /** get publicid of the file fo */
    private String getPublicID(FileObject fo) throws IOException {
        FileObject foEntity = Env.findEntityRegistration(fo);
        if (foEntity == null) foEntity = fo;
        Object publicId = foEntity.getAttribute(Env.EA_PUBLICID);
        if (publicId == null || !(publicId instanceof String)) {
            throw new IOException("missing or invalid attribute: " + //NOI18N
                Env.EA_PUBLICID + ", provider: " + foEntity); //NOI18N
        }
        return (String) publicId;
    }
    
    /** return SaveCookie impl */
    public final SaveCookie getSaveCookie () {
            return instToSave;
    }
    
    /** is setting object changed? */
    public final boolean isChanged() {
        return isChanged;
    }
    
    /** Registers PropertyChangeListener to receive events; initialize
     * listening to events comming from the setting object and file object.
     * @param listener The listener to register.
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport == null) {
            changeSupport = new PropertyChangeSupport(this);
            Object inst = instance.get();
            if (inst == null) return;
            Convertor conv = initConvertor();
            if (conv != null) {
                conv.registerSaver(inst, instToSave);
            }
        }
        changeSupport.addPropertyChangeListener(listener);
    }
    
    /** Removes PropertyChangeListener from the list of listeners.
     * @param listener The listener to remove.
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport != null) {
            changeSupport.removePropertyChangeListener(listener);
            Object inst = instance.get();
            if (inst == null) return;
            Convertor conv = getConvertor();
            if (conv != null) {
                conv.unregisterSaver(inst, instToSave);
            }
        }
    }
    
    /** Notifies all registered listeners about the event.
     * @param event The event to be fired
     * @see #PROP_FILE_CHANGED
     * @see #PROP_SAVE
     */
    private void firePropertyChange(String name) {
        if (changeSupport != null)
            changeSupport.firePropertyChange(name, null, null);
    }
    
    /** called by ScheduledRequest in order to perform the request */
    public void writeDown() throws IOException {
        instToSave.writeDown();
    }
    
    /** Support for storing instances allowing identify the origin of file events 
     * fired as a consequence of this storing.
     */
    private class SaveCookieImpl implements FileSystem.AtomicAction, SaveCookie, Saver {
    
        private java.io.ByteArrayOutputStream buf;
        
        private SaveCookieImpl() {
        }
        
        /** store buffer to the file. */
        public void run () throws IOException {
            if (!ip.getDataObject().isValid()) {
                //invalid data object cannot be used for storing
                Logger.getAnonymousLogger().fine("invalid data object cannot be used for storing " + ip.getDataObject()); // NOI18N
                return;
            }
            org.openide.filesystems.FileLock lock = null;
            java.io.OutputStream los;
            synchronized (ip.READWRITE_LOCK) {
//                if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
//                    err.log("saving " + dobj); // NOI18N
//                }
                lock = ip.getScheduledRequest().getFileLock();
                if (lock == null) return;
                los = file.getOutputStream(lock);

                java.io.OutputStream os = new java.io.BufferedOutputStream(los, 1024);
                try {
                    buf.writeTo(os);
//                        if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
//                            dobj.err.log("saved " + dobj); // NOI18N
//                        }
                } finally {
                    os.close();
                }
            }
        } 
        
        /** Implementation of SaveCookie. */
        public void save() throws IOException {
            if (!isChanged) return;
            ip.getScheduledRequest().runAndWait();
        }
        
        private void writeDown() throws IOException {
            Object inst = instance.get();
            if (inst == null) return ;
            Convertor conv = getConvertor();
            if (conv == null) return ;
            java.io.ByteArrayOutputStream b = new java.io.ByteArrayOutputStream(1024);
            java.io.Writer w = ContextProvider.createWriterContextProvider(
                new java.io.OutputStreamWriter(b, "UTF-8"), // NOI18N
                SaveSupport.this.file
            );
            isChanged = false;
            try {
                conv.write(w, inst);
            } finally {
                w.close();
            }
            
            buf = b;
            file.getFileSystem().runAtomicAction(this);
            buf = null;
            synchronizeName(inst);
            if (!isChanged) firePropertyChange(PROP_SAVE);
        }
        
        public void markDirty() {
            if (isChanged || !ip.getDataObject().isValid()) return;
            if (knownToBeTemplate == null) knownToBeTemplate = ip.getDataObject().isTemplate() ? Boolean.TRUE : Boolean.FALSE;
            if (knownToBeTemplate.booleanValue()) return;
            isChanged = true;
            firePropertyChange(PROP_SAVE);
        }
        
        public void requestSave() throws java.io.IOException {
            if (isChanged || !ip.getDataObject().isValid()) return;
            if (knownToBeTemplate == null) knownToBeTemplate = ip.getDataObject().isTemplate() ? Boolean.TRUE : Boolean.FALSE;
            if (knownToBeTemplate.booleanValue()) return;
            isChanged = true;
            firePropertyChange(PROP_SAVE);
            ip.getScheduledRequest().schedule(instance.get());
        }
        
        /** try to synchronize file name with instance name */
        private void synchronizeName(Object inst) {
            java.lang.reflect.Method getter;
            try {
                try {
                    getter = inst.getClass().getMethod("getDisplayName"); // NOI18N
                } catch (NoSuchMethodException me) {
                    getter = inst.getClass().getMethod("getName"); // NOI18N
                }
            } catch (Exception ex) { // do nothing
                return;
            }
            if (!getter.isAccessible()) return;
            
            try {
                String name = (String) getter.invoke(inst);
                String oldName = ip.getDataObject().getName();
                if (!name.equals(oldName)) {
                    file.setAttribute(EA_NAME, name);
                } else if (file.getAttribute(EA_NAME) == null) {
                    file.setAttribute(EA_NAME, name);
                }
            } catch (Exception ex) {
                Exceptions.attachLocalizedMessage(ex, file.toString());
        	Logger.getLogger(SaveSupport.class.getName()).log(Level.WARNING, null, ex);
            }
        }
        
    }
    
}
