/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package threaddemo.views;

import java.io.IOException;
import java.util.*;
import javax.swing.Action;
import org.netbeans.api.looks.*;
import org.netbeans.spi.looks.*;
import org.openide.actions.*;
import org.openide.cookies.*;
import org.openide.util.WeakListener;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import threaddemo.model.*;

/**
 * A look which wraps phadhails.
 * @author Jesse Glick
 */
final class PhadhailLook extends DefaultLook implements PhadhailListener/*, PhadhailEditorSupport.Saver*/ {
    
    PhadhailLook() {
        super("PhadhailLook");
    }
    
    public void attachTo(Object o) {
        Phadhail ph = (Phadhail)o;
        ph.addPhadhailListener((PhadhailListener)WeakListener.create(PhadhailListener.class, this, ph));
    }
    
    /* XXX phrebejk: Uncomment if present in Look; then also remove WeakListener usage from attachTo:
    public void unregister(Object o) {
        Phadhail ph = (Phadhail)o;
        ph.removePhadhailListener(this);
    }
     */
    
    public boolean isLeaf(Object o) {
        Phadhail ph = (Phadhail)o;
        return !ph.hasChildren();
    }
    
    public List getChildObjects(Object o) {
        Phadhail ph = (Phadhail)o;
        return ph.getChildren();
    }
    
    public String getName(Object o) {
        Phadhail ph = (Phadhail)o;
        return ph.getName();
    }

    public String getDisplayName(Object o) {
        Phadhail ph = (Phadhail)o;
        return ph.getPath();
    }
    
    public boolean canRename(Object o) {
        return true;
    }
    
    public void setName(Object o, String newName) {
        Phadhail ph = (Phadhail)o;
        try {
            ph.rename(newName);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }
    
    public boolean canDestroy(Object o) {
        return true;
    }
    
    public void destroy(Object o) throws IOException {
        Phadhail ph = (Phadhail)o;
        ph.delete();
    }
    
    public Action[] getActions(Object o) {
        return new Action[] {
            SystemAction.get(OpenAction.class),
            SystemAction.get(SaveAction.class),
            null,
            SystemAction.get(NewAction.class),
            null,
            SystemAction.get(DeleteAction.class),
            SystemAction.get(RenameAction.class),
            SystemAction.get(ToolsAction.class),
        };
    }
    
    public NewType[] getNewTypes(Object o) {
        Phadhail ph = (Phadhail)o;
        if (ph.hasChildren()) {
            return new NewType[] {
                new PhadhailNewType(ph, false),
                new PhadhailNewType(ph, true),
            };
        } else {
            return new NewType[0];
        }
    }
    
    /* XXX phrebejk: uncomment if Look gets support for "poor man's Lookup":
    // cache of save cookies for unsaved phadhails
    private final Map saveCookies = new WeakHashMap(); // Map<Phadhail,SaveCookie>
    
    public void addSaveCookie(Phadhail ph, SaveCookie s) {
        saveCookies.put(ph, s);
        fireLookupItemsChanged(ph);
    }
    
    public void removeSaveCookie(Phadhail ph) {
        saveCookies.remove(ph);
        fireLookupItemsChanged(ph);
    }
    
    // cache of editor supports; need to retain identity since they have state
    private final Map editorCookies = new WeakHashMap(); // Map<Phadhail,EditorCookie>
    
    public Collection getLookupItems(Object o) {
        Phadhail ph = (Phadhail)o;
        EditorCookie ec = (EditorCookie)editorCookies.get(ph);
        if (ec == null) {
            ec = new PhadhailEditorSupport(ph, this);
            editorCookies.put(ph, ec);
        }
        SaveCookie sc = (SaveCookie)saveCookies.get(ph);
        if (sc != null) {
            Collection c = new ArrayList(2);
            c.add(ec);
            c.add(sc);
            return c;
        } else {
            return Collections.singleton(ec);
        }
    }
     */
    
    public void childrenChanged(PhadhailEvent ev) {
        if (!java.awt.EventQueue.isDispatchThread()) Thread.dumpStack();//XXX
        refreshChildren(ev.getPhadhail());
    }
    
    public void nameChanged(PhadhailNameEvent ev) {
        if (!java.awt.EventQueue.isDispatchThread()) Thread.dumpStack();//XXX
        fireNameChange(ev.getPhadhail(), ev.getOldName(), ev.getNewName());
        fireDisplayNameChange(ev.getPhadhail(), ev.getOldName(), ev.getNewName());
    }
    
}
