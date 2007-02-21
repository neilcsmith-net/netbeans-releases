/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 
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

package org.netbeans.modules.bpel.debugger;

import org.netbeans.api.debugger.Session;
import org.netbeans.modules.bpel.debugger.api.AttachingCookie;
import org.netbeans.modules.bpel.debugger.api.SessionCookie;
import org.netbeans.spi.debugger.ContextProvider;

/**
 *
 * @author Alexander Zgursky
 */
public class AttachingSessionCookie implements SessionCookie {
    private ContextProvider myContextProvider;
    private String mySessionId;

    /** Creates a new instance of AttachingSessionCookie */
    public AttachingSessionCookie(ContextProvider contextProvider) {
        myContextProvider = contextProvider;
    }

    public String getSessionId() {
        if (mySessionId == null) {
            AttachingCookie attachingCookie = 
                (AttachingCookie) myContextProvider.lookupFirst (null, AttachingCookie.class);
            if (attachingCookie != null) {
                mySessionId = attachingCookie.getHost() + ":" + attachingCookie.getPort();
            } else {
                Session session = (Session)myContextProvider.lookupFirst(null, Session.class);
                mySessionId = session.getName();
            }
        }
        return mySessionId;
    }
}
