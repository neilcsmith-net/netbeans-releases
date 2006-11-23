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

package org.openide.windows;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.Keymap;
import org.openide.awt.Actions;
import org.openide.awt.UndoRedo;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAdapter;
import org.openide.nodes.NodeListener;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.WeakSet;
import org.openide.util.actions.SystemAction;

/**
 * Embeddable visual component to be displayed in NetBeans.
 * This is the basic unit of display--windows should not be
 * created directly, but rather use this class.
 * A top component may correspond to a single window, but may also
 * be a tab (e.g.) in a window. It may be docked or undocked,
 * have selected nodes, supply actions, etc.
 *
 * Important serialization note: Serialization of this TopComponent is designed
 * in a way that it's not desired to override writeReplace method. If you would
 * like to resolve to something, please implement readResolve() method directly
 * on your top component.
 *
 * @author Jaroslav Tulach, Petr Hamernik, Jan Jancura
 */
public class TopComponent extends JComponent implements Externalizable, Accessible, HelpCtx.Provider, Lookup.Provider {
    /** UI logger to notify about invocation of an action */
    private static Logger UILOG = Logger.getLogger("org.netbeans.ui.actions"); // NOI18N
    /** generated Serialized Version UID */
    static final long serialVersionUID = -3022538025284122942L;
    /** top component logger */
    static final Logger LOG = Logger.getLogger(TopComponent.class.getName());

    /** Behavior in which a top component closed (by the user) in one workspace
     * will be removed from <em>every</em> workspace.
     * Also, {@link #close} is called.
     * This is appropriate for top components such as Editor panes which
     * the user expects to really close (and prompt to save) when closed
     * in any
     * @deprecated Do not use. It is redundant since workspaces are not supported anymore. */
    public static final int CLOSE_EACH = 0;

    /** Behavior in which a top component closed (by the user) in one workspace
     * may be left in other workspaces.
     * Only when the last remaining manifestation in any workspace is closed
     * will the object be deleted using {@link #close}.
     * Appropriate for components containing no user data, for which closing
     * the component is only likely to result from the user's wanting to remove
     * it from active view (on the current workspace).
     * @deprecated Do not use. It is redundant since workspaces are not supported anymore. */
    public static final int CLOSE_LAST = 1;

    /** Persistence type of TopComponent instance. TopComponent is persistent. */
    public static final int PERSISTENCE_ALWAYS = 0;

    /** Persistence type of TopComponent instance. TopComponent is persistent only when
     * it is opened in Mode. */
    public static final int PERSISTENCE_ONLY_OPENED = 1;

    /** Persistence type of TopComponent instance. TopComponent is not persistent. */
    public static final int PERSISTENCE_NEVER = 2;

    /** a lock for operations in default impl of getLookup */
    private static Object defaultLookupLock = new Object();

    /** Classes that have been warned about overriding preferredID() */
    private static final Set<Class> warnedTCPIClasses = new WeakSet<Class>();

    /** Used to print warning about getPersistenceType */
    private static final Set<Class> warnedClasses = new WeakSet<Class>();

    /** reference to Lookup with default implementation for the
     * component or the lookup associated with the component itself
     */
    private Object defaultLookupRef;

    /** Holds support for sync with node display name or null */
    private NodeName nodeName;

    // Do not use, deprecated.

    /** constant for desired close operation */
    private int closeOperation = CLOSE_LAST;

    /** Icon of this <code>TopComponent</code> */
    private transient Image icon;

    /** Activated nodes of this <code>TopComponent</code>. */
    private transient Node[] activatedNodes;

    /** Localized display name of this <code>TopComponent</code>. */
    private transient String displayName;

    /** Holds localized display name of this <code>TopComponent</code> in html syntax,
     * or null if not needed */
    private String htmlDisplayName;
    
    /** identification of serialization version
    * Used in CloneableTopComponent readObject method.
    */
    short serialVersion = 1;
    private AttentionGetter attentionGetter = null;


    /** Create a top component.
    */
    public TopComponent() {
        this((Lookup) null);
    }

    /** Creates a top component for a provided lookup that will delegate
     * take and synchronize activated nodes and ActionMap from a provided
     * lookup. The lookup will also be returned from {@link #getLookup} method,
     * if not overriden.
     *
     * @param lookup the lookup to associate with
     * @since 4.19
     */
    public TopComponent(Lookup lookup) {
        if (lookup != null) {
            setLookup(lookup, true);
        }

        enableEvents(java.awt.AWTEvent.KEY_EVENT_MASK);

        // #27731 TopComponent itself shouldn't get the focus.
        // XXX What to do in case nothing in TopComponent is focusable?
        setFocusable(false);
        initActionMap(lookup);
    }

    // It is necessary so the old actions (clone and close from org.openide.actions package) remain working.

    /** Initialized <code>ActionMap</code> of this <code>TopComponent</code>.
     * @since 4.13 */
    private void initActionMap(Lookup lookup) {
        ActionMap inner = null;
        if (lookup != null) {
            inner = (ActionMap)lookup.lookup(ActionMap.class);
        }
        if (inner == null) {
            inner = new ActionMap();
        }
        
        javax.swing.ActionMap am = new DelegateActionMap(this, inner);

        if (this instanceof TopComponent.Cloneable) {
            am.put(
                "cloneWindow",
                new javax.swing.AbstractAction() { // NOI18N
                    public void actionPerformed(ActionEvent evt) {
                        TopComponent cloned = ((TopComponent.Cloneable) TopComponent.this).cloneComponent();
                        cloned.open();
                        cloned.requestActive();
                    }
                }
            );
        }

        am.put(
            "closeWindow",
            new javax.swing.AbstractAction() { // NOI18N
                public void actionPerformed(ActionEvent evt) {
                    TopComponent.this.close();
                }
            }
        );

        setActionMap(am);
    }

    /** Getter for class that allows obtaining of information about components.
    * It allows to find out which component is selected, which nodes are
    * currently or has been activated and list of all components.
    *
    * @return the registry of components
    */
    public static final Registry getRegistry() {
        return WindowManager.getDefault().getRegistry();
    }

    /** Get the set of activated nodes in this component.
     * @return the activated nodes for this component or <code>null</code>, <code>null</code>
     *         means such component does not change {@link Registry#getActivatedNodes()} just
     *         {@link Registry#getCurrentNodes()} when this component gets activated */
    public final Node[] getActivatedNodes() {
        return activatedNodes;
    }

    /** Set the set of activated nodes in this component.
    * @param activatedNodes activated nodes for this component
    */
    public final void setActivatedNodes(Node[] activatedNodes) {
        boolean l = LOG.isLoggable(Level.FINER);

        if (Arrays.equals(this.activatedNodes, activatedNodes)) {
            if (l) {
                LOG.finer("No change to activatedNodes for " + this); // NOI18N
            }
            return;
        }

        Lookup lookup = getLookup(false);

        if (lookup instanceof DefaultTopComponentLookup) {
            if (l) {
                LOG.finer("Updating lookup " + lookup + " for " + this); // NOI18N
            }
            ((DefaultTopComponentLookup) lookup).updateLookups(activatedNodes);
        }

        Node[] old = this.activatedNodes;
        this.activatedNodes = activatedNodes;

        if (l) {
            LOG.finer("activatedNodes changed: " + (activatedNodes == null ? "" : Arrays.asList(activatedNodes).toString())); // NOI18N
        }
        // notify all that are interested...
        WindowManager.getDefault().topComponentActivatedNodesChanged(this, this.activatedNodes);

        if (l) {
            LOG.finer("window manager notified: " + this); // NOI18N
        }


        firePropertyChange("activatedNodes", old, this.activatedNodes); // NOI18N

        if (l) {
            LOG.finer("listeners notified: " + this); // NOI18N
        }
    }

    /**
     * Overwrite when you want to change default persistence type. Default
     * persistence type is PERSISTENCE_ALWAYS.
     * Return value should be constant over a given TC's lifetime.
     * @return one of P_X constants
     * @since 4.20
     */
    public int getPersistenceType() {
        //First check for 'PersistenceType' client property for compatibility.
        if (warnedClasses.add(getClass()) && !TopComponent.class.equals(getClass())) {
            Logger.getAnonymousLogger().warning(
                "Note - " // NOI18N
                 +getClass().getName() + " ought to override getPersistenceType()" // NOI18N
                 +" rather than using the client property or accepting the default."
            ); // NOI18N
        }

        String propValue = (String) getClientProperty("PersistenceType"); // NOI18N

        if (propValue == null) {
            return PERSISTENCE_ALWAYS;
        } else if ("Never".equals(propValue)) { // NOI18N

            return PERSISTENCE_NEVER;
        } else if ("OnlyOpened".equals(propValue)) { // NOI18N

            return PERSISTENCE_ONLY_OPENED;
        } else {
            return PERSISTENCE_ALWAYS;
        }
    }

    /** Get the undo/redo support for this component.
    * The default implementation returns a dummy support that cannot
    * undo anything.
    *
    * @return undoable edit for this component
    */
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    /** Shows this <code>TopComponent</code>.
     * <em>Note:</em> This method only makes it visible, but does not
     * activates it.
     * @see #requestActive */
    public void open() {
        open(null);
    }

    /** Shows this <code>TopComponent</code> in current workspace.
     * <em>Node:</em> Currently workspaces are not supported. The method has the same effect
     * like {@link #open()}.
     * @deprecated Use {@link #open()} instead. */
    public void open(Workspace workspace) {
        WindowManager.getDefault().topComponentOpen(this);
    }

    /** Indicates whether this <code>TopComponent</code> is opened.
    * @return true if given top component is opened, false otherwise */
    public final boolean isOpened() {
        return isOpened(null);
    }

    /** Indicates whether this <code>TopComponent</code> is opened in current workspace.
     * <em>Node:</em> Currently workspaces are not supported. The method has the same effect
     * like {@link #isOpened()}.
     * @deprecated Use {@link #isOpened()} instead. */
    public final boolean isOpened(Workspace workspace) {
        return WindowManager.getDefault().topComponentIsOpened(this);
    }

    /** Closes this <code>TopComponent</code>.
     * @return true if top component was succesfully closed, false if
     * top component for some reason refused to close. */
    public final boolean close() {
        return close(null);
    }

    /** Closes this <code>TopComponent</code> in current workspace.
     * <em>Node:</em> Currently workspaces are not supported. The method has the same effect
     * like {@link #close()}.
     * @deprecated Use {@link #close()} instead. */
    public final boolean close(Workspace workspace) {
        if (!isOpened()) {
            return true;
        }

        if (canClose()) {
            WindowManager.getDefault().topComponentClose(this);

            return true;
        } else {
            return false;
        }
    }

    /** This method is called when this <code>TopComponent</code> is about to close.
     * Allows subclasses to decide if <code>TopComponent</code> is ready to close.
     * @since 4.13 */
    public boolean canClose() {
        if (!isOpened()) {
            return false;
        }

        return canClose(null, true);
    }

    /** This method is called when top component is about to close.
     * Allows subclasses to decide if top component is ready for closing
     * or not.<br>
     * Default implementation always return true.
     *
     * @param workspace the workspace on which we are about to close or
     *                  null which means that component will be closed
     *                  on all workspaces where it is opened (CLOSE_EACH mode)
     * @param last true if this is last workspace where top component is
     *             opened, false otherwise. If close operation is set to
     *             CLOSE_EACH, then this param is always true
     * @return true if top component is ready to close, false otherwise.
     * @deprecated Do not use anymore. Use {@link #canClose()} instead.
     * Both parameters are redundant since workspaces are not supported anymore. */
    public boolean canClose(Workspace workspace, boolean last) {
        return true;
    }

    /** Called only when top component was closed on all workspaces before and
     * now is opened for the first time on some workspace. The intent is to
     * provide subclasses information about TopComponent's life cycle across
     * all existing workspaces.
     * Subclasses will usually perform initializing tasks here.
     * @deprecated Use {@link #componentOpened} instead. */
    protected void openNotify() {
    }

    /** Called only when top component was closed so that now it is closed
     * on all workspaces in the system. The intent is to provide subclasses
     * information about TopComponent's life cycle across workspaces.
     * Subclasses will usually perform cleaning tasks here.
     * @deprecated Use {@link #componentClosed} instead.
     */
    protected void closeNotify() {
    }

    /** Gets the system actions which will appear in the popup menu of this component.
     * @return array of system actions for this component
     * @deprecated Use {@link #getActions()} instead.
     */
    public SystemAction[] getSystemActions() {
        return new SystemAction[0];
    }

    /** Gets the actions which will appear in the popup menu of this component.
     * <p>Subclasses are encouraged to override this method to specify
     * their own sets of actions.
     * <p>Remember to call the super method when overriding and add your actions
     * to the superclass' ones (in some order),
     * because the default implementation provides support for standard
     * component actions like save, close, and clone.
     * @return array of actions for this component
     * @since 3.32
     */
    public javax.swing.Action[] getActions() {
        Action[] actions = WindowManager.getDefault().topComponentDefaultActions(this);

        SystemAction[] sysActions = getSystemActions();

        // If there are some sys actions (i.e. the subclass overrided the defautl impl) add them.
        if (sysActions.length > 0) {
            List<Action> acs = new ArrayList<Action>(Arrays.asList(actions));
            acs.addAll(Arrays.asList(sysActions));

            return acs.toArray(new Action[0]);
        } else {
            return actions;
        }
    }

    /** Set the close mode for the component.
     * @param closeOperation one of {@link #CLOSE_EACH} or {@link #CLOSE_LAST}
     * @throws IllegalArgumentException if an unrecognized close mode was supplied
     * @see #close()
     * @deprecated Do not use. It is redundant since workspaces are not supported anymore. */
    public final void setCloseOperation(final int closeOperation) {
        if ((closeOperation != CLOSE_EACH) && (closeOperation != CLOSE_LAST)) {
            throw new IllegalArgumentException(
                NbBundle.getBundle(TopComponent.class).getString("EXC_UnknownOperation")
            );
        }

        if (this.closeOperation == closeOperation) {
            return;
        }

        this.closeOperation = closeOperation;
        firePropertyChange("closeOperation", null, null); // NOI18N
    }

    /** Get the current close mode for this component.
     * @return one of {@link #CLOSE_EACH} or {@link #CLOSE_LAST}
     * @deprecated Do not use. It is redundant since workspaces are not supported anymore. */
    public final int getCloseOperation() {
        return closeOperation;
    }

    /**
     * Subclasses are encouraged to override this method to provide preferred value
     * for unique TopComponent ID returned by {@link org.openide.windows.WindowManager#findTopComponentID}.
     *
     * Returned value should be a String, preferably describing semantics of
     * TopComponent subclass, such as "PieChartViewer" or "HtmlEditor" etc.
     * Value is then used by window system as prefix value for creating unique
     * TopComponent ID.
     *
     * Returned String value should be preferably unique, but need not be.
     * @since 4.13
     */
    protected String preferredID() {
        Class clazz = getClass();

        if (getPersistenceType() != PERSISTENCE_NEVER && warnedTCPIClasses.add(clazz)) {
            Logger.getAnonymousLogger().warning(
                clazz.getName() + " should override preferredID()"  //NOI18N
            ); 
        }

        String name = getName();

        // fix for #47021 and #47115
        if (name == null) {
            int ind = clazz.getName().lastIndexOf('.');
            name = (ind == -1) ? clazz.getName() : clazz.getName().substring(ind + 1);
        }

        return name;
    }

    /** Called only when top component was closed on all workspaces before and
     * now is opened for the first time on some workspace. The intent is to
     * provide subclasses information about TopComponent's life cycle across
     * all existing workspaces.
     * Subclasses will usually perform initializing tasks here.
     * @since 2.18 */
    protected void componentOpened() {
        openNotify();
    }

    /** Called only when top component was closed so that now it is closed
     * on all workspaces in the system. The intent is to provide subclasses
     * information about TopComponent's life cycle across workspaces.
     * Subclasses will usually perform cleaning tasks here.
     * @since 2.18 */
    protected void componentClosed() {
        closeNotify();
    }

    /** Called when <code>TopComponent</code> is about to be shown.
     * Shown here means the component is selected or resides in it own cell
     * in container in its <code>Mode</code>. The container is visible and not minimized.
     * <p><em>Note:</em> component
     * is considered to be shown, even its container window
     * is overlapped by another window.</p>
     * @since 2.18 */
    protected void componentShowing() {
    }

    /** Called when <code>TopComponent</code> was hidden. <em>Nore</em>:
     * <p><em>Note:</em> Beside typical situations when component is hidden,
     * it is considered to be hidden even in that case
     * the component is in <code>Mode</code> container hierarchy,
     * the cointainer is visible, not minimized,
     * but the component is neither selected nor in its own cell,
     * i.e. it has it's own tab, but is not the selected one.
     * @since 2.18 */
    protected void componentHidden() {
    }

    /** Called when this component is activated.
    * This happens when the parent window of this component gets focus
    * (and this component is the preferred one in it), <em>or</em> when
    * this component is selected in its window (and its window was already focussed).
    * Remember to call the super method.
    * The default implementation does nothing.
    */
    protected void componentActivated() {
    }

    /** Called when this component is deactivated.
    * This happens when the parent window of this component loses focus
    * (and this component is the preferred one in the parent),
    * <em>or</em> when this component loses preference in the parent window
    * (and the parent window is focussed).
    * Remember to call the super method.
    * The default implementation does nothing.
    */
    protected void componentDeactivated() {
    }

    /** Request focus for the window holding this top component.
     * Also makes the component preferred in that window.
     * The component will <em>not</em> be automatically {@link #open opened} first
     * if it is not already.
     * <p>Subclasses should override this method to transfer focus to desired
     * focusable component. <code>TopComponent</code> itself is not focusable.
     * See for example {@link org.openide.text.CloneableEditor#requestFocus}.
     * @deprecated Use {@link #requestActive} instead to make TopComponent active
     * in window system not only focused. This method should have been preserved
     * for focus management only but not activation of <code>TopComponent</code> inside
     * window system.  The default implementation does nothing, and does not call
     * super.requestFocus().
     */
    public void requestFocus() {
        if (isFocusable()) {
            //Issue 44304 - output window is focusable when empty, need some
            //way to give it focus
            super.requestFocus();
        }
    }

    /** Request focus for the top component inside focused window.
     * Also makes the component preferred in that window.
     * The component will <em>not</em> be automatically {@link #open opened} first
     * if it is not already.
     * <p>Subclasses should override this method to transfer focus to desired
     * focusable component. <code>TopComponent</code> itself is not focusable.
     * See for example {@link org.openide.text.CloneableEditor#requestFocusInWindow}.
     * @deprecated Use {@link #requestActive} instead to make TopComponent active
     * in window system not only focused. This method should have been preserved
     * for focus management only but not activation of <code>TopComponent</code> inside
     * window system. The default implementation does nothing, and does not call
     * super.requestFocusInWindow(). */
    public boolean requestFocusInWindow() {
        if (isFocusable()) {
            return super.requestFocusInWindow();
        } else {
            return false;
        }
    }

    /** Activates this <code>TopComponent<code> if it is opened.
     * @since 4.13 */
    public void requestActive() {
        WindowManager.getDefault().topComponentRequestActive(this);
    }

    /**
     * Attempts to bring the parent <code>Window</code> or <code>Frame</code>
     * of this <code>TopComponent<code> to front of other windows.
     * @since 5.8
     */
    public void toFront() {
        WindowManager.getDefault().topComponentToFront(this);
    }

    /** Selects this <code>TopComponent</code>, if it is opened, but does not activate it
     * unless it is in active mode already. */
    public void requestVisible() {
        WindowManager.getDefault().topComponentRequestVisible(this);
        org.netbeans.modules.openide.windows.GlobalActionContextImpl.blickActionMap(getActionMap());
    }

    /**
     * Cause this TopComponent's tab to flash or otherwise draw attention to
     * itself.  This method is thread-safe.
     * <p>
     * It will remain flashing until either <code>cancelRequestAttention</code>
     * is called, the component becomes selected or its activated state changes,
     * unless the <code>brief</code> argument is true, in which case it will stop
     * after a few second.
     * @param brief True if the tab should blink a few times and stop
     * @since 5.1
     */
    public final void requestAttention(final boolean brief) {
        //Reentrancy issues - always invoke later
        EventQueue.invokeLater(
            new Runnable() {
                public void run() {
                    if ((attentionGetter != null) && !brief) {
                        attentionGetter.kill();
                    } else if (!brief) {
                        WindowManager.getDefault().topComponentRequestAttention(TopComponent.this);
                    } else if (attentionGetter != null) {
                        attentionGetter.reset();
                    } else {
                        attentionGetter = new AttentionGetter();
                    }
                }
            }
        );
    }

    /**
     * Cause this TopComponent's tab to stop flashing if it was flashing.
     * @since 5.1
     */
    public final void cancelRequestAttention() {
        //Reentrancy issues - always invoke later
        EventQueue.invokeLater(
            new Runnable() {
                public void run() {
                    if (attentionGetter != null) {
                        attentionGetter.stop();
                    } else {
                        WindowManager.getDefault().topComponentCancelRequestAttention(TopComponent.this);
                    }
                }
            }
        );
    }

    /** Set the name of this top component.
    * The default implementation just notifies the window manager.
    * @param name the new display name
    */
    public void setName(final String name) {
        String old = getName();

        if ((name != null) && (name.equals(old))) {
            return;
        }

        super.setName(name);
        firePropertyChange("name", old, name); // NOI18N

        // XXX When displayName is null, it is used the name.
        WindowManager.getDefault().topComponentDisplayNameChanged(this, name);
    }

    /** Sets localized display name of this <code>TopComponent</code>.
     * @param displayName localized display name which is set
     * @since 4.13 */
    public void setDisplayName(String displayName) {
        String old = this.displayName;

        if ((displayName == old) || ((displayName != null) && displayName.equals(old))) {
            return;
        }

        // warning if display name contains html tags
        if (BasicHTML.isHTMLString(displayName)) {
            Logger.getAnonymousLogger().warning(
                "Call of " + getClass().getName() + ".setDisplayName(\"" + displayName + "\")" +
                " shouldn't contain any HTML tags. Please use " + getClass().getName() + ".setHtmlDisplayName(String)" +
                "for such purpose. For details please see http://www.netbeans.org/issues/show_bug.cgi?id=66777.");
        }
        
        this.displayName = displayName;
        firePropertyChange("displayName", old, displayName); // NOI18N

        WindowManager.getDefault().topComponentDisplayNameChanged(this, displayName);
    }

    /** Gets localized display name of this <code>TopComponent</code>.
     * @return localized display name or <code>null</code> if there is none
     * @since 4.13 */
    public String getDisplayName() {
        return displayName;
    }
    
    /** Sets localized html display name of this <code>TopComponent</code>.
     * Hmtl name usually contains basic html tags for text coloring and style.
     * Html name may be null if not needed.
     * Must apparently begin with <code>&lt;html&gt;</code>.
     *
     * @param htmlDisplayName localized html display name which is set
     *
     * @since 6.4
     */
    public void setHtmlDisplayName(String htmlDisplayName) {
        String old = this.htmlDisplayName;

        if ((htmlDisplayName == old) || ((htmlDisplayName != null) && htmlDisplayName.equals(old))) {
            return;
        }

        this.htmlDisplayName = htmlDisplayName;
        firePropertyChange("htmlDisplayName", old, htmlDisplayName); // NOI18N

        WindowManager.getDefault().topComponentHtmlDisplayNameChanged(this, htmlDisplayName);
    }

    /** Gets localized display name of this <code>TopComponent</code> with added
     * html tags for text coloring and/or font style. May return null.
     * Must apparently begin with <code>&lt;html&gt;</code>.
     *
     * @return localized html display name or <code>null</code> if there is none
     *
     * @since 6.4
     */
    public String getHtmlDisplayName() {
        return htmlDisplayName;
    }
    

    /** Sets toolTip for this <code>TopComponent</code>, adds notification
     * about the change to its <code>WindowManager.TopComponentManager</code>. */
    public void setToolTipText(String toolTip) {
        if ((toolTip != null) && toolTip.equals(getToolTipText())) {
            return;
        }

        super.setToolTipText(toolTip);

        // XXX #19428. Container updates name and tooltip in the same handler.
        WindowManager.getDefault().topComponentToolTipChanged(this, toolTip);
    }

    /** Set the icon of this top component.
    * The icon will be used for
    * the component's representation on the screen, e.g. in a multiwindow's tab.
    * The default implementation just notifies the window manager.
    * @param icon New components' icon.
    */
    public void setIcon(final Image icon) {
        if (icon == this.icon) {
            return;
        }

        Image old = this.icon;
        this.icon = icon;

        WindowManager.getDefault().topComponentIconChanged(this, this.icon); // TEMP

        firePropertyChange("icon", old, icon); // NOI18N
    }

    /** @return The icon of the top component */
    public Image getIcon() {
        return icon;
    }

    /** Get the help context for this component.
    * Subclasses should generally override this to return specific help.
    * @return the help context
    */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(TopComponent.class);   // XXX #63303
    }

    /** Allows top component to specify list of modes into which can be docked
     * by end user. Subclasses should override this method if they want to
     * alter docking policy of top component. <p>
     * So for example, by returning empty list, top component refuses
     * to be docked anywhere. <p>
     * Default implementation allows docking anywhere by returning
     * input list unchanged.
     *
     * @param modes list of {@link Mode} which represent all modes of current
     * workspace, can contain nulls. Items are structured in logical groups
     * separated by null entries. <p>
     * Input array also contains special constant modes for docking
     * into newly created frames. Their names are "SingleNewMode",
     * "MultiNewMode", "SplitNewMode", can be used for their
     * recognition. Please note that names and existence of special modes
     * can change in future releases.
     *
     * @return list of {@link Mode} which are available for dock, can contain nulls
     * @since 2.14
     */
    public List<Mode> availableModes(List<Mode> modes) {
        return modes;
    }

    /** Overrides superclass method, adds possible additional handling of global keystrokes
     * in case this <code>TopComoponent</code> is ancestor of focused component. */
    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
        boolean ret = super.processKeyBinding(ks, e, condition, pressed);

        // XXX #30189 Reason of overriding: to process global shortcut.
        if ((JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT == condition) && (ret == false) && !e.isConsumed()) { // NOI18N

            Keymap km = (Keymap) Lookup.getDefault().lookup(Keymap.class);
            Action action = (km != null) ? km.getAction(ks) : null;

            if (action == null) {
                return false;
            }

            // If necessary create context aware instance.
            if (action instanceof ContextAwareAction) {
                action = ((ContextAwareAction) action).createContextAwareInstance(getLookup());
            } else if (SwingUtilities.getWindowAncestor(e.getComponent()) instanceof java.awt.Dialog) {
                // #30303 For 'old type' actions check the transmodal flag,
                // if invoked in dialog. See ShorcutAndMenuKeyEventProcessor in core.
                Object value = action.getValue("OpenIDE-Transmodal-Action"); // NOI18N

                if (!Boolean.TRUE.equals(value)) {
                    return false;
                }
            }

            if (action.isEnabled()) {
                LogRecord rec = new LogRecord(Level.FINER, "UI_ACTION_KEY_PRESS"); // NOI18N
                rec.setParameters(new Object[] { ks, ks.toString(), action, action.getClass().getName(), action.getValue(Action.NAME) });
                rec.setResourceBundle(NbBundle.getBundle(TopComponent.class));
                rec.setLoggerName(UILOG.getName());
                UILOG.log(rec);

                ActionEvent ev = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, Utilities.keyToString(ks));
                action.actionPerformed(ev);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }

            return true;
        } else {
            return ret;
        }
    }

    /** Serialize this top component.
    * Subclasses wishing to store state must call the super method, then write to the stream.
    * @param out the stream to serialize to
    */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(new Short(serialVersion));
        out.writeInt(closeOperation);
        out.writeObject(getName());
        out.writeObject(getToolTipText());

        if (getDisplayName() != null) {
            out.writeObject(getDisplayName());
        }

        Node n = (nodeName == null) ? null : nodeName.getNode();
        Node.Handle h = (n == null) ? null : n.getHandle();
        out.writeObject(h);
    }

    /** Deserialize this top component.
    * Subclasses wishing to store state must call the super method, then read from the stream.
    * @param in the stream to deserialize from
    */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        Object firstObject = in.readObject();

        if (firstObject instanceof Integer) {
            // backward compatibility read
            serialVersion = 0;

            closeOperation = ((Integer) firstObject).intValue();

            // BCR: this is backward compatibility read and is likely not needed
            // BCR: anymore. So let's just ignore the read of the data object
            // BCR:     DataObject obj = (DataObject)in.readObject();
            in.readObject();

            super.setName((String) in.readObject());
            setToolTipText((String) in.readObject());

            // initialize the connection to a data object

            /* BCR: Remove this as we ignore the DataObject
                        if (obj != null) {
                            nodeName = new NodeName (this);
                            nodeName.attach (obj.getNodeDelegate ());
                        }
            */
        } else {
            // new serialization
            serialVersion = ((Short) firstObject).shortValue();

            closeOperation = in.readInt();
            super.setName((String) in.readObject());
            setToolTipText((String) in.readObject());

            Object obj = in.readObject();

            if (obj instanceof String) {
                setDisplayName((String) obj);
                obj = in.readObject();
            }

            Node.Handle h = (Node.Handle) obj;

            if (h != null) {
                Node n = h.getNode();
                NodeName.connect(this, n);
            }
        }

        if ((closeOperation != CLOSE_EACH) && (closeOperation != CLOSE_LAST)) {
            throw new IOException("invalid closeOperation: " + closeOperation); // NOI18N
        }
    }

    /** Delegates instance of replacer class to be serialized instead
    * of top component itself. Replacer class calls writeExternal and
    * constructor, readExternal and readResolve methods properly, so
    8 any top component can behave like any other externalizable object.
    * Subclasses can override this method to perform their
    * serialization differentrly */
    protected Object writeReplace() throws ObjectStreamException {
        return new Replacer(this);
    }

    /* Read accessible context
     * @return - accessible context
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new JComponent.AccessibleJComponent() {
                        public AccessibleRole getAccessibleRole() {
                            return AccessibleRole.PANEL;
                        }

                        public String getAccessibleName() {
                            if (accessibleName != null) {
                                return accessibleName;
                            }

                            return getName();
                        }

                        /* Fix for 19344: Null accessible decription of all TopComponents on JDK1.4 */
                        public String getToolTipText() {
                            return TopComponent.this.getToolTipText();
                        }
                    };
        }

        return accessibleContext;
    }

    /** Gets lookup which represents context of this component. By default
     * the lookup delegates to result of <code>getActivatedNodes</code>
     * method and result of this component <code>ActionMap</code> delegate.
     *
     * @return a lookup with designates context of this component
     * @see org.openide.util.ContextAwareAction
     * @see org.openide.util.Utilities#actionsToPopup(Action[], Lookup)
     * @since 3.29
     */
    public Lookup getLookup() {
        return getLookup(true);
    }

    /**
     * @param init should a lookup be initialized if it is not?
     * @return lookup or null
     */
    private Lookup getLookup(boolean init) {
        synchronized (defaultLookupLock) {
            if (defaultLookupRef instanceof Lookup) {
                return (Lookup) defaultLookupRef;
            }

            if (defaultLookupRef instanceof Object[]) {
                return (Lookup) ((Object[]) defaultLookupRef)[0];
            }

            if (defaultLookupRef instanceof java.lang.ref.Reference) {
                Object l = ((java.lang.ref.Reference) defaultLookupRef).get();

                if (l instanceof Lookup) {
                    return (Lookup) l;
                }
            }

            if (!init) {
                return null;
            }

            Lookup lookup = new DefaultTopComponentLookup(this); // Lookup of activated nodes and action map
            defaultLookupRef = new java.lang.ref.WeakReference<Lookup>(lookup);

            return lookup;
        }
    }

    /** Associates the provided lookup with the component. So it will
     * be returned from {@link #getLookup} method.
     *
     * @param lookup the lookup to associate
     * @exception IllegalStateException if there already is a lookup registered
     *   with this component
     * @since 4.23
     */
    protected final void associateLookup(Lookup lookup) {
        setLookup(lookup, true);
    }

    /** Associates the provided lookup with the component. So it will
     * be returned from {@link #getLookup} method.
     *
     * @param lookup the lookup to associate
     * @param sync synchronize return value of {@link #getActivatedNodes} with the
     *   content of lookup?
     * @exception IllegalStateException if there already is a lookup registered
     *   with this component
     */
    final void setLookup(Lookup lookup, boolean sync) {
        synchronized (defaultLookupLock) {
            if (defaultLookupRef != null) {
                throw new IllegalStateException(
                    "Trying to set lookup " + lookup + " but there already is " + defaultLookupRef +
                    " for component: " + this
                ); // NOI18N
            }

            defaultLookupRef = lookup;

            if (sync) {
                defaultLookupRef = new Object[] { defaultLookupRef, new SynchronizeNodes(lookup) };
            }
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("setLookup with " + lookup + " and sync: " + sync + " on " + this); // NOI18N
            }
        }
    }

    private void attachNodeName(NodeName nodeName) {
        this.nodeName = nodeName;
    }

    /** Each top component that wishes to be cloned should implement
    * this interface, so CloneAction can check it and call the cloneComponent
    * method.
    */
    public static interface Cloneable {
        /** Creates a clone of this component
        * @return cloned component.
        */
        public TopComponent cloneComponent();
    }

    /** Registry of all top components.
    * There is one instance that can be obtained via {@link TopComponent#getRegistry}
    * and it permits listening to the currently selected element, and to
    * the activated nodes assigned to it.
    */
    public static interface Registry {
        /** Name of property for the set of opened components. */
        public static final String PROP_OPENED = "opened"; // NOI18N

        /** Name of property for the selected top component. */
        public static final String PROP_ACTIVATED = "activated"; // NOI18N

        /** Name of property for currently selected nodes. */
        public static final String PROP_CURRENT_NODES = "currentNodes"; // NOI18N

        /** Name of property for lastly activated nodes. */
        public static final String PROP_ACTIVATED_NODES = "activatedNodes"; // NOI18N

        /** Name of property for listening to TopComponents opened through open() call,
         * either by user or programmatically.
         * Fired property change event returns opened TopComponent from its getNewValue()
         */
        public static final String PROP_TC_OPENED = "tcOpened"; // NOI18N
        
        /** Name of property for listening to TopComponents closed through close() call,
         * either by user or programmatically.
         * Fired property change event returns closed TopComponent from its getNewValue().
         */
        public static final String PROP_TC_CLOSED = "tcClosed"; // NOI18N
        
        /** Get reference to a set of all opened componets in the system.
        *
        * @return live read-only set of {@link TopComponent}s
        */
        public Set<TopComponent> getOpened();

        /** Get the currently selected element.
        * @return the selected top component, or <CODE>null</CODE> if there is none
        */
        public TopComponent getActivated();

        /** Getter for the currently selected nodes.
        * @return array of nodes or null if no component activated or it returns
        *   null from getActivatedNodes ().
        */
        public Node[] getCurrentNodes();

        /** Getter for the lastly activated nodes. Comparing
        * to previous method it always remembers the selected nodes
        * of the last component that had ones.
        *
        * @return array of nodes (not null)
        */
        public Node[] getActivatedNodes();

        /** Add a property change listener.
        * @param l the listener to add
        */
        public void addPropertyChangeListener(PropertyChangeListener l);

        /** Remove a property change listener.
        * @param l the listener to remove
        */
        public void removePropertyChangeListener(PropertyChangeListener l);
    }

    private class AttentionGetter implements ActionListener {
        Timer timer = null;

        public AttentionGetter() {
            reset();
        }

        public void reset() {
            assert EventQueue.isDispatchThread();

            if (timer != null) {
                timer.stop();
            }

            start();
            timer = new Timer(3500, this);
            timer.setRepeats(false);
            timer.start();
        }

        private void start() {
            WindowManager.getDefault().topComponentRequestAttention(TopComponent.this);
        }

        public void kill() {
            timer.stop();
            attentionGetter = null;
        }

        private void stop() {
            if (timer != null) {
                timer.stop();
            }

            attentionGetter = null;
            WindowManager.getDefault().topComponentCancelRequestAttention(TopComponent.this);
            attentionGetter = null;
        }

        public void actionPerformed(ActionEvent ae) {
            stop();
        }
    }

    /** This class provides the connection between the node name and
     * a name of the component.
     *
     * @deprecated Please do not use. This support class does nothing much
     * useful. If you need to synchronize display name of your TopComponent
     * with some Node's display name, we recommend you to do it manually in
     * your client's code.
     */
    public static class NodeName extends NodeAdapter {
        /** asociation with top component */
        private TopComponent top;

        /** weak ref to node we are attached to or null */
        private Reference node;

        /** Listener to node, used for weak listening */
        private NodeListener nodeL;

        /** Connects a top component and a node. The name of
        * component will be updated as the name of the node
        * changes.
        *
        * @param top top compoonent to modify its name
        * @param n node to take name from
         *
         * @since 4.3
        */
        public static void connect(TopComponent top, Node n) {
            new NodeName(top).attach(n);
        }

        /** Constructs new name adapter that
         * can be attached to any node and will listen on changes
         * of its display name and modify the name of the component.
         *
         * @param top top component to modify its name
         * 
         * @deprecated Please do not use, public just by an accident.
         */
        public NodeName(TopComponent top) {
            this.top = top;
        }

        /** Listens to Node.PROP_DISPLAY_NAME.
         *
         * @deprecated Please do not use, public just by an accident.
        */
        public void propertyChange(PropertyChangeEvent ev) {
            if (ev.getPropertyName().equals(Node.PROP_DISPLAY_NAME)) {
                Node n = (Node) node.get();
                if (n != null) {
                    top.setName(n.getDisplayName());
                }
            }
        }
        
        /** Attaches itself to a given node.
        */
        private void attach(Node n) {
            synchronized (top) {
                node = new WeakReference<Node>(n);
                nodeL = WeakListeners.create(NodeListener.class, this, n);
                n.addNodeListener(nodeL);
                top.attachNodeName(this);
                top.setActivatedNodes(new Node[] { n });
                top.setName(n.getDisplayName());
            }
        }

        private Node getNode() {
            return (Node) node.get();
        }
        
    } // end of NodeName

    /** Instance of this class is serialized instead of TopComponent itself.
    * Emulates behaviour of serialization of externalizable objects
    * to keep TopComponent serialization compatible with previous versions. */
    private static final class Replacer implements Serializable {
        /** SUID */
        static final long serialVersionUID = -8897067133215740572L;

        /** Asociation with top component which is to be serialized using
        * this replacer */
        transient TopComponent tc;

        public Replacer(TopComponent tc) {
            this.tc = tc;
        }

        private void writeObject(ObjectOutputStream oos)
        throws IOException, ClassNotFoundException {
            // write the name of the top component first
            oos.writeObject(tc.getClass().getName());

            // and now let top component to serialize itself
            tc.writeExternal(oos);
        }

        private void readObject(ObjectInputStream ois)
        throws IOException, ClassNotFoundException {
            // read the name of top component's class, instantiate it
            // and read its attributes from the stream
            String name = (String) ois.readObject();
            name = org.openide.util.Utilities.translate(name);

            try {
                ClassLoader loader = (ClassLoader) Lookup.getDefault().lookup(ClassLoader.class);

                if (loader == null) {
                    loader = getClass().getClassLoader();
                }

                Class tcClass = Class.forName(name, true, loader);

                // instantiate class event if it has protected or private
                // default constructor
                java.lang.reflect.Constructor con = tcClass.getDeclaredConstructor(new Class[0]);
                con.setAccessible(true);

                try {
                    tc = (TopComponent) con.newInstance(new Object[0]);
                } finally {
                    con.setAccessible(false);
                }

                tc.readExternal(ois);

                // call readResolve() if present and use resolved value
                Method resolveMethod = findReadResolveMethod(tcClass);

                if (resolveMethod != null) {
                    // check exceptions clause
                    Class[] result = resolveMethod.getExceptionTypes();

                    if ((result.length == 1) && ObjectStreamException.class.equals(result[0])) {
                        // returned value type
                        if (Object.class.equals(resolveMethod.getReturnType())) {
                            // make readResolve accessible (it can have any access modifier)
                            resolveMethod.setAccessible(true);

                            // invoke resolve method and accept its result
                            try {
                                TopComponent unresolvedTc = tc;
                                tc = (TopComponent) resolveMethod.invoke(tc);

                                if (tc == null) {
                                    throw new java.io.InvalidObjectException(
                                        "TopComponent.readResolve() cannot return null." // NOI18N
                                         +" See http://www.netbeans.org/issues/show_bug.cgi?id=27849 for more info." // NOI18N
                                         +" TopComponent:" + unresolvedTc
                                    ); // NOI18N
                                }
                            } finally {
                                resolveMethod.setAccessible(false);
                            }
                        }
                    }
                }
            } catch (Exception exc) {
                Throwable th = exc;

                // Extract target exception.
                if (th instanceof InvocationTargetException) {
                    th = ((InvocationTargetException) th).getTargetException();
                }

                // IOException throw directly.
                if (th instanceof IOException) {
                    throw (IOException) th;
                }

                // All others wrap into IOException.
                throw (IOException) new IOException(th.toString()).initCause(th);
            }
        }

        /** Resolve to original top component instance */
        private Object readResolve() throws ObjectStreamException {
            return tc;
        }

        /** Tries to find readResolve method in given class. Finds
        * both public and non-public occurences of the method and
        * searches also in superclasses */
        private static Method findReadResolveMethod(Class clazz) {
            Method result = null;
            Class[] params = new Class[0];

            // first try public occurences
            try {
                result = clazz.getMethod("readResolve", params); // NOI18N
            } catch (NoSuchMethodException exc) {
                // public readResolve does not exist
                // now try non-public occurences; search also in superclasses
                for (Class i = clazz; (i != null) && (i != TopComponent.class); i = i.getSuperclass()) { // perf: TC and its superclasses do not have readResolve method

                    try {
                        result = i.getDeclaredMethod("readResolve", params); // NOI18N

                        // get out of cycle if method found
                        break;
                    } catch (NoSuchMethodException exc2) {
                        // readResolve does not exist in current class
                    }
                }
            }

            return result;
        }
    }
     // end of Replacer inner class

    /** Synchronization between Lookup and getActivatedNodes
     */
    private class SynchronizeNodes implements org.openide.util.LookupListener, Runnable {
        private Lookup.Result<Node> res;

        public SynchronizeNodes(Lookup l) {
            res = l.lookup(new Lookup.Template<Node>(Node.class));
            res.addLookupListener(this);
            resultChanged(null);
        }

        public void resultChanged(org.openide.util.LookupEvent ev) {
            boolean l = LOG.isLoggable(Level.FINE);
            if (l) {
                LOG.fine("lookup changed for " + TopComponent.this + " is visible: " + TopComponent.this.isVisible()); // NOI18N
            }
            if (TopComponent.this.isVisible() && EventQueue.isDispatchThread()) {
                // run immediatelly
                run();
            } else {
                // replan
                EventQueue.invokeLater(this);
            }
            if (l) {
                LOG.fine("lookup changed exit " + TopComponent.this); // NOI18N
            }
        }

        public void run() {
            boolean l = LOG.isLoggable(Level.FINE);

            Collection<? extends Node> nodes = res.allInstances();

            if (l) {
                LOG.fine("setting nodes for " + TopComponent.this + " to " + nodes); // NOI18N
            }
            setActivatedNodes(nodes.toArray(new Node[0]));
            if (l) {
                LOG.fine("setting nodes done for " + TopComponent.this + " to " + nodes); // NOI18N
            }
        }
    }
     // end of SynchronizeNodes
}
