/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.debugger.gdb2.mi;

/**
 * A command to be sent to the engine and to handle results.
 */

public abstract class MICommand {
    private final int routingToken;
    private final String command;	// including args

    private MICommandManager manager;	// ... managing us
    private int token;			// ... generated by manager

    private java.util.List<String> logStream;
					// stores log stream data assoc. with
					// this command.

    private java.util.List<String> consoleStream;
					// stores console stream data assoc.
					// with this command.

    private final boolean consoleCommand;

    /**
     * Constructor for commands.
     */

    public MICommand(int routingToken, String command) {
	this.routingToken = routingToken;
	this.command = command;
        this.consoleCommand = !command.startsWith("-"); // NOI18N
    } 


    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        if (routingToken != 0) {
            res.append('(');
            res.append(routingToken);
            res.append(')');
        }
        res.append(token);
        res.append(command);
	return res.toString();
    }


    /**
     * Retrieve the routingToken.
     */

    public int routingToken() {
	return routingToken;
    } 

    /**
     * Retrieve full value of command (with possible arguments).
     */

    public String command() {
	return command;
    } 


    /**
     * Called on receipt of "^done".
     */

    protected abstract void onDone(MIRecord record);


    /**
     * Called on receipt of "^running".
     */

    protected abstract void onRunning(MIRecord record);


    /**
     * Called on receipt of "^error".
     */

    protected abstract void onError(MIRecord record);


    /**
     * Called on receipt of "^exit".
     */

    protected abstract void onExit(MIRecord record);


    /**
     * Called on receipt of "*stopped".
     */

    protected abstract void onStopped(MIRecord record);


    /**
     * Called on receipt of "^connected" and "*&lt;others&gt;".
     */

    protected abstract void onOther(MIRecord record);

    /**
     * Called when we see something like this 
     * > 
     * in the log.
     */
    protected abstract void onUserInteraction(MIUserInteraction ui);


    /**
     * Specialization should call this method to take command out of 
     * managers pending list.
     */
    protected void finish() {
	if (manager == null)
	    return;
	else
	    manager.finish(this);
    }


    /**
     * Used by MICommandManager.
     */

    void setManagerData(MICommandManager manager, int token) {
	assert manager != null;
	assert this.manager == null : "MICommand can only be sent once";
	this.manager = manager;
	this.token = token;
    } 


    /**
     * Used by MICommandManager.
     */

    public int getToken() {
	assert token != 0 :
	       "MICommand.getToken(): " + // NOI18N
	       "cannot access before command has been sent"; // NOI18N
	return token;
    } 

    /**
     * Used by MICommandManager.
     */
    void recordLogStream(String data) {
	if (logStream == null)
	    logStream = new java.util.LinkedList<String>();
	logStream.add(data);
    }

    /**
     * Used by MICommandManager.
     */
    void recordConsoleStream(String data) {
	if (consoleStream == null)
	    consoleStream = new java.util.LinkedList<String>();
	if (data.startsWith("> ")) {		// NOI18N
	    onUserInteraction(new MIUserInteraction(getConsoleStream()));
	} else {
	    consoleStream.add(data);
	}
    }

    /**
     * Return accumulated log stream data for this command. May return null.
     */
    public String getLogStream() {
	if (logStream == null)
	    return null;
	StringBuilder sb = new StringBuilder();
	for (String s : logStream)
	    sb.append(String.format(s));
	return sb.toString();
    }

    /**
     * Return accumulated console stream data for this command. May return null.
     */
    public String getConsoleStream() {
	if (consoleStream == null)
	    return null;
	StringBuilder sb = new StringBuilder();
	for (String s : consoleStream)
	    sb.append(String.format(s));
	return sb.toString();
    }
   
    public boolean isConsoleCommand() {
        return consoleCommand;
    }
}

