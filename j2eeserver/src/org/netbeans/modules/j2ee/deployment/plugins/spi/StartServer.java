/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.j2ee.deployment.plugins.spi;

import org.openide.debugger.DebuggerInfo;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.status.ProgressObject;

/**
 * Start Server functionality.  typically Start/Stop of Target server is through
 * jsr77 calls directly, as long as the j2eeserver correspondences of Target
 * are StateManageable - if not we have to disable this action anyway.
 *
 * @author George FinKlang
 * @author  nn136682
 * @version 0.1
 */

public interface StartServer extends DeploymentManagerWrapper {
    
    /**
     * Returns true if the admin server is also the given target server (share the same vm).
     * Start/stopping/debug apply to both servers.
     * @param the target server in question
     * @return true when admin is also target server
     */
    public boolean isAlsoTargetServer(Target target);
    
    /**
     * Returns true if the admin server is also the only target server (share the same vm).
     * Start/stopping/debug apply to both servers.
     * @return true when admin is also target server
     */
    //public boolean isAlsoSingleTargetServer();

    /**
     * Returns true if the admin server can be started through this spi.
     */
    public boolean supportsStartDeploymentManager();
    
    
    /**
     * Starts the admin server. Note that this means that the DeploymentManager
     * was originally created disconnected. After calling this, the DeploymentManager
     * will be created connected, so the old DeploymentManager will be discarded.
     * This has the result that any unsaved changes in edited server configurations
     * need to be saved or discarded, requiring user prompting.  All diagnostic
     * should be communicated through ServerProgres with no exceptions thrown.
     *
     * @return ProgressObject object used to monitor start server progress
     */
    public ProgressObject startDeploymentManager();
    
    /**
     * Stops the admin server. The DeploymentManager object will be disconnected.
     * All diagnostic should be communicated through ServerProgres with no
     * exceptions thrown.
     * @return ServerProgress object used to monitor start server progress
     */
    public ProgressObject stopDeploymentManager();
    
    
    /**
     * Returns true if the admin server should be started before server deployment configuration.
     */
    public boolean needsStartForConfigure();
    
    /**
     * Returns true if the admin server should be started before asking for
     * target list.
     */
    public boolean needsStartForTargetList();
    
    /**
     * Returns true if the admin server should be started before admininistrative configuration.
     */
    public boolean needsStartForAdminConfig();
    
    /**
     * Returns true if this admin server is running.
     */
    public boolean isRunning();
    
    /**
     * Returns true if this target is in debug mode.
     */
    public boolean isDebuggable(Target target);
    
    /**
     * Start or restart the target in debug mode.
     * If target is also domain admin, the amdin is restarted in debug mode.
     * All diagnostic should be communicated through ServerProgres with no exceptions thrown.
     * @param target the target server
     * @return ServerProgress object to monitor progress on start operation
     */
    public ProgressObject startDebugging(Target target);
    
    /**
     * Returns the host/port necessary for connecting to the server's debug information.
     */
    // PENDING use JpdaDebugInfo from debuggercore
    public DebuggerInfo getDebugInfo(Target target);
    
    /** 
     * Get the output from the server for display in the ide.
     * This output pane will be shown during start/stop server, deployment/execute
     * against the given target, and 'Show Event Window' action on the corresponding
     * instance or target node.
     *
     * Note: the implementation of this method is optional and plugin just return
     * null if not supported.  In this case if server platform support JSR-77 
     * events, the IDE JSR-77 framework will automatically show an output tab for 
     * event messages.
     *
     * @arg target the target server; null value imply deployment manager (admin server)
     * @returns  InputOutput tab showing output/error from server process
     * @throws IllegalStateException if server isn't running or log not available.
     */
    //public org.openide.windows.InputOutput[] getServerOutput(Target target);
}
