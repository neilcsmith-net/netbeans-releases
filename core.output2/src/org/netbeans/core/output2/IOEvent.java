/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.core.output2;

import java.awt.*;
import java.util.Arrays;

/**
 * An event type which carries data about an operation performed on an
 * instance of NbIO which an interested view must respond to on the
 * event queue.
 * <p>
 * While this is an unusual approach, it is also using the event queue in
 * exactly the way event queues are designed to be used.  It accomplishes
 * complete decoupling of the IO implementation from the view implementation -
 * the IO does not need to know anything about what component is rendering
 * it, it just posts relevant events onto the event queue.
 * <p>
 * Unfortunately, it is impossible to use an AWTEventListener to get notification
 * of custom event types (appears that it once was, and was optimized out in
 * the biggest if/then clause in history - see Toolkit).  So we have the
 * dispatch() method.
 * <p>
 * While all this could be done with SwingUtilities.invokeLater() and runnables,
 * this approach works well and is somewhat more lightweight, so I see no need
 * to change it.
 * <p>
 * Should someone want to make this package capable of supporting multiple
 * output windows, for some reason, the only thing necessary to do is to
 * add a registry of weakly referenced OutputContainers and iterate them all
 * in <code>dispatch()</code> - the only static tie to the rest of the
 * universe is the DEFAULT field of Controller.
 *
 * @author Tim Boudreau
 */
final class IOEvent extends AWTEvent implements ActiveEvent {
    static final int IO_EVENT_MASK = 0xF0000;
    /**
     * Command instructing the controller to create a new view for the IO.
     * If getValue() returns true, it will try to find an existing closed tab
     * with the same name and reuse it.
     */
    static final int CMD_CREATE = 0;
    /**
     * Command to set the output visible.  Output is always visible in the current
     * implementation, so this command is provided for completeness but will be ignored.
     */
    static final int CMD_OUTPUT_VISIBLE=1;
    /**
     * Set the input area visible.
     */
    static final int CMD_INPUT_VISIBLE=2;
    /**
     * Command to set the error output visible.  Error output is interleaved in the current
     * implementation, so this command is provided for completeness but will be ignored.
     */
    static final int CMD_ERR_VISIBLE=3;
    /**
     * Provided for completeness but will be ignored.
     */
    static final int CMD_ERR_SEPARATED=4;

    /**
     * Evil and unwise but supported.
     */
    static final int CMD_FOCUS_TAKEN=5;
    /**
     * Command indicating that the IO should become the selected tab.
     */
    static final int CMD_SELECT=6;
    /**
     * Command indicating that the IO's tab should be closed.
     */
    static final int CMD_CLOSE=7;
    /**
     * Command indicating that the IO has been closed for writes and the UI should performe
     * any needed state changes to reflect that.
     */
    static final int CMD_STREAM_CLOSED=8;
    /**
     * Command indicating that the output writer's reset() method has been called, and that any
     * exiting output in the IO's tab should be discarded, and the closed flag reset.
     */
    static final int CMD_RESET=9;
    /**
     * Set the toolbar actions that should be displayed.
     */
    static final int CMD_SET_TOOLBAR_ACTIONS = 10;
    /**
     * XXX may not be supported - dispose of the default output window instance
     */
    static final int CMD_DETACH = 11;
    
    /**
     * Array of IDs for checking legal values and generating a string representing the event.
     */
    private static final int[] IDS = new int[] {
        CMD_CREATE,
        CMD_OUTPUT_VISIBLE,
        CMD_INPUT_VISIBLE,
        CMD_ERR_VISIBLE,
        CMD_ERR_SEPARATED,
        CMD_FOCUS_TAKEN,
        CMD_SELECT,
        CMD_CLOSE,
        CMD_STREAM_CLOSED,
        CMD_RESET,
        CMD_SET_TOOLBAR_ACTIONS,
        CMD_DETACH,
    };

    /**
     * Strings matching the values in the IDS array for generating a string representing the event.
     */
    private static final String[] CMDS = new String[] {
        "CREATE", //NOI18N
        "OUTPUT_VISIBLE", //NOI18N
        "INPUT_VISIBLE", //NOI18N
        "ERR_VISIBLE", //NOI18N
        "ERR_SEPARATED", //NOI18N
        "FOCUS_TAKEN", //NOI18N
        "SELECT", //NOI18N
        "CLOSE", //NOI18N
        "STREAM_CLOSED", //NOI18N
        "RESET", //NOI18N
        "SET_TOOLBAR_ACTIONS", //NOI18N
        "DETACH"  //NOI18N
    };

    /**
     * Boolean value associated with this event.
     */
    private boolean value = false;
    /**
     * Data associated with this event (used by set toolbar actions)
     */
    private Object data = null;

    /**
     * Used by unit tests to ensure all pending events have been processed before
     * continuing.
     */
    static int pendingCount = 0;
    /**
     * Create an IOEvent with the specified source, command and boolean state for the command.
     *
     * @param source An instance of NbIO which something of interest has happened to; can be null
     *        in the case that this is CMD_DETACH, an instruction to the default instance to
     *        self-destruct (module uninstalled or winsys wants to install a new instance)
     * @param command The ID of what has happened
     * @param value The boolean state for the command to be performed
     */
    IOEvent(NbIO source, int command, boolean value) {
        //Null source only for destroying the default instance
        super(source == null ? new Object() : source, command + IO_EVENT_MASK);
        assert Arrays.binarySearch (IDS, command) >= 0 : "Unknown command: " + command; //NOI18N
        consumed = false;
        this.value = value;
        pendingCount++;
    }

    /**
     * Construct a data-bearing IOEvent with the specified source, commmand and data
     *
     * @param source The command source
     * @param command The ID of what has happened
     * @param data Data required to process this command (i.e. toolbar actions added)
     */
    IOEvent(NbIO source, int command, Object data) {
        this (source, command, false);
        this.data = data;
    }

    /**
     * Convenience getter for the command ID associated with this event.
     * Equivalent to <code>getID() - IO_EVENT_MASK</code>.
     *
     * @return The command
     */
    public int getCommand() {
        return getID() - IO_EVENT_MASK;
    }

    /**
     * Convenience getter for the NbIO associated with this
     * command.  Equivalent to <code>(NbIO) getSource()</code>
     * @return
     */
    public NbIO getIO() {
        return getSource() instanceof NbIO ? (NbIO) getSource() : null;
    }

    /**
     * Get a boolean value associated with the event - most use cases involve
     * some thread calling boolean setters/getters on an instance of NbIO.
     *
     * @return The boolean state associated with this command.
     */
    public boolean getValue() {
        return value;
    }

    /**
     * Get data associated with the event.  This is only used for supplying
     * toolbar actions.
     *
     * @return An object
     */
    public Object getData() {
        return data;
    }

    /**
     * Determine if the event is consumed.  Creation events will be.
     *
     * @return If the event is consumed
     */
    public boolean isConsumed() {
        return consumed;
    }

    /**
     * Overridden to avoid a bit of work AWTEvent does that's not
     * necessary for us.
     */
    public void consume() {
        consumed = true;
    }

    public String toString() {
        return "IOEvent@" + System.identityHashCode(this) + "-" + 
            cmdToString(getCommand()) + " on " + getIO() +
            " value= " + getValue() + " data=" + getData(); //NOI18N
    }

    public void dispatch() {
        //The only thing needed to make this package fully reentrant (capable of
        //supporting multiple output windows, FWIW) is to replace this line with
        //iterating a registry of OutputContainers, and calling eventDispatched on each.

        //Null check below so that if the module has been disabled (the only way
        //this can be null), the dead module doesn't reopen its output window
        if (OutputWindow.DEFAULT != null) {
            //Can be null after CMD_DETACH
            OutputWindow.DEFAULT.eventDispatched(this);
        }
        pendingCount--;
    }

    public static String cmdToString (int cmd) {
        return CMDS[Arrays.binarySearch(IDS, cmd)];
    }
}
