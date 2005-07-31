/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.collab.channel.filesharing.event;

import org.netbeans.modules.collab.channel.filesharing.mdc.EventContext;
import org.netbeans.modules.collab.channel.filesharing.mdc.event.ChannelEvent;


/**
 * ChannelCreated bean
 *
 * @author  Ayub Khan, ayub.khan@sun.com
 * @version 1.0
 */
public class FilesharingEvent extends ChannelEvent {
    ////////////////////////////////////////////////////////////////////////////
    // Class variables
    ////////////////////////////////////////////////////////////////////////////

    /* CHANNELCREATED ID */
    public static final String EVENT_ID = "Channel_created"; // NOI18N

    /**
     * constructor
     *
     */
    public FilesharingEvent(EventContext evContext) {
        super(evContext);
    }

    ////////////////////////////////////////////////////////////////////////////
    // methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * getter for ID
     *
     * @return        ID
     */
    public static String getEventID() {
        return EVENT_ID;
    }
}
