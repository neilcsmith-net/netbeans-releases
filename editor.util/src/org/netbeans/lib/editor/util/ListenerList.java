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

package org.netbeans.lib.editor.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

/**
 * Listener list storing listeners of a single type.
 *
 * @author Miloslav Metelka
 * @since 1.11
 */

public final class ListenerList<T extends EventListener> implements Serializable {

    static final long serialVersionUID = 0L;
    
    /** A null array to be shared by all empty listener lists */
    private static final EventListener[] EMPTY_LISTENER_ARRAY = new EventListener[0];
    
    /* The array of listeners. */
    private transient ImmutableList<T> listenersList;
    
    public ListenerList() {
        listenersList = new ImmutableList<T>(EMPTY_LISTENER_ARRAY);
    }
    
    /**
     * Returns a list of listeners.
     * 
     * <p>The listeners are returned in exactly the same order
     * as they were added to this list. Use the following code
     * for firing events to all the listeners you get from this
     * method.
     * 
     * <pre>
     *      List<MyListener> listeners = listenerList.getListeners();
     *      for (MyListener l : listeners) {
     *          l.notify(evt);
     *      }
     * </pre>
     * 
     * @return An immutable list of listeners contained in this listener list.
     */
    public synchronized List<T> getListeners() {
        return listenersList;
    }
    
    /**
     * Returns the total number of listeners for this listener list.
     */
    public synchronized int getListenerCount() {
        return listenersList.size();
    }
    
    /**
     * Adds the given listener to this listener list.
     * 
     * @param listener the listener to be added. If null is passed it is ignored (nothing gets added).
     */
    public synchronized void add(T listener) {
        if (listener == null)
            return;

        EventListener [] arr = new EventListener[listenersList.getArray().length + 1];
        if (arr.length > 1) {
            System.arraycopy(listenersList.getArray(), 0, arr, 0, arr.length - 1);
        }
        arr[arr.length - 1] = listener;
        
        listenersList = new ImmutableList<T>(arr);
    }
    
    /**
     * Removes the given listener from this listener list.
     * 
     * @param listener the listener to be removed. If null is passed it is ignored (nothing gets removed).
     */
    public synchronized void remove(T listener) {
        if (listener == null)
            return;

        int idx = listenersList.indexOf(listener);
        if (idx == -1) {
            return;
        }
        
        EventListener [] arr = new EventListener[listenersList.getArray().length - 1];
        if (arr.length > 0) {
            System.arraycopy(listenersList.getArray(), 0, arr, 0, idx);
        }
        if (arr.length > idx) {
            System.arraycopy(listenersList.getArray(), idx + 1, arr, idx, listenersList.getArray().length - idx - 1);
        }
        
        listenersList = new ImmutableList<T>(arr);
    }
    
    // Serialization support.
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        
        // Write in opposite order of adding 
        for (Iterator<T> i = listenersList.iterator(); i.hasNext(); ) {
            T l = i.next();
            // Save only the serializable listeners
            if (l instanceof Serializable) {
                s.writeObject(l);
            }
        }
        
        s.writeObject(null);
    }
    
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        List<T> lList = new ArrayList<T>();
        Object listenerOrNull;
        while (null != (listenerOrNull = s.readObject())) {
            @SuppressWarnings("unchecked")
            T l = (T)listenerOrNull;
            lList.add(l);
        }
        this.listenersList = new ImmutableList<T>((EventListener [])lList.toArray(new EventListener[lList.size()]));
    }
    
    public String toString() {
        return listenersList.toString();
    }

    private static final class ImmutableList<E extends EventListener> extends AbstractList<E> {

        private EventListener[] array;
        
        public ImmutableList(EventListener[] array) {
            super();
            
            assert array != null : "The array can't be null"; //NOI18N
            this.array = array;
        }
        
        public E get(int index) {
            if (index >= 0 && index < array.length) {
                @SuppressWarnings("unchecked") 
                E element = (E) array[index];
                return element;
            } else {
                throw new IndexOutOfBoundsException("index = " + index + ", size = " + array.length); //NOI18N
            }
        }
        
        public int size() {
            return array.length;
        }
        
        public EventListener[] getArray() {
            return array;
        }
    } // End of ImmutableList class
}
