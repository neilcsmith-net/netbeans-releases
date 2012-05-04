/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2011 Oracle and/or its affiliates. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.glassfish.common;

import java.io.*;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import org.glassfish.tools.ide.data.GlassFishAdminInterface;
import org.glassfish.tools.ide.data.GlassFishServer;
import org.glassfish.tools.ide.data.GlassFishVersion;
import org.netbeans.api.server.ServerInstance;
import org.netbeans.modules.glassfish.common.nodes.Hk2InstanceNode;
import org.netbeans.modules.glassfish.common.ui.InstanceCustomizer;
import org.netbeans.modules.glassfish.common.ui.VmCustomizer;
import org.netbeans.modules.glassfish.spi.CustomizerCookie;
import org.netbeans.modules.glassfish.spi.GlassfishModule;
import org.netbeans.modules.glassfish.spi.GlassfishModule.OperationState;
import org.netbeans.modules.glassfish.spi.GlassfishModule.ServerState;
import org.netbeans.modules.glassfish.spi.GlassfishModuleFactory;
import org.netbeans.modules.glassfish.spi.RemoveCookie;
import org.netbeans.spi.server.ServerInstanceFactory;
import org.netbeans.spi.server.ServerInstanceImplementation;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.InputOutput;

/**
 *
 * @author Peter Williams
 */
public class GlassfishInstance implements ServerInstanceImplementation, Lookup.Provider, LookupListener, GlassFishServer {

    ////////////////////////////////////////////////////////////////////////////
    // Class attributes                                                       //
    ////////////////////////////////////////////////////////////////////////////

    // Reasonable default values for various server parameters.  Note, don't use
    // these unless the server's actual setting cannot be determined in any way.
    public static final String DEFAULT_HOST_NAME = "localhost"; // NOI18N
    public static final String DEFAULT_ADMIN_NAME = "admin"; // NOI18N
    public static final String DEFAULT_ADMIN_PASSWORD = "adminadmin"; // NOI18N
    public static final int DEFAULT_HTTP_PORT = 8080;
    public static final int DEFAULT_HTTPS_PORT = 8181;
    public static final int DEFAULT_ADMIN_PORT = 4848;
    public static final String DEFAULT_DOMAINS_FOLDER = "domains"; //NOI18N
    public static final String DEFAULT_DOMAIN_NAME = "domain1"; // NOI18N

    
    ////////////////////////////////////////////////////////////////////////////
    // Static methods                                                         //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Build and update copy of GlassFish properties to be stored in <code>this</code>
     * object.
     * <p/>
     * Constructor helper method.
     * <p/>
     * @param properties Source GlassFish properties.
     * @return Updated copy of GlassFish properties to be stored.
     */
    private static Map<String, String> prepareProperties(
            Map<String, String> properties) {
        boolean isRemote = properties.get(GlassfishModule.DOMAINS_FOLDER_ATTR) == null;
        String deployerUri = properties.get(GlassfishModule.URL_ATTR);
        updateString(properties, GlassfishModule.HOSTNAME_ATTR,
                DEFAULT_HOST_NAME);
        updateString(properties, GlassfishModule.GLASSFISH_FOLDER_ATTR, "");
        updateInt(properties, GlassfishModule.HTTPPORT_ATTR,
                DEFAULT_HTTP_PORT);
        updateString(properties, GlassfishModule.DISPLAY_NAME_ATTR,
                "Bogus display name");
        updateInt(properties, GlassfishModule.ADMINPORT_ATTR,
                DEFAULT_ADMIN_PORT);
        updateString(properties, GlassfishModule.SESSION_PRESERVATION_FLAG, "true");
        updateString(properties, GlassfishModule.START_DERBY_FLAG,
                isRemote ? "false" : "true");
        updateString(properties, GlassfishModule.USE_IDE_PROXY_FLAG, "true");
        updateString(properties, GlassfishModule.DRIVER_DEPLOY_FLAG, "true");
        updateString(properties, GlassfishModule.HTTPHOST_ATTR, "localhost");
        properties.put(GlassfishModule.JVM_MODE,
                isRemote && !deployerUri.contains("deployer:gfv3ee6wc")
                ? GlassfishModule.DEBUG_MODE : GlassfishModule.NORMAL_MODE);
        Map<String, String> newProperties =  Collections.synchronizedMap(
                new HashMap<String, String>(properties));
        // Asume a local instance is in NORMAL_MODE
        // Assume remote Prelude and 3.0 instances are in DEBUG (we cannot change them)
        // Assume a remote 3.1 instance is in NORMAL_MODE... we can restart it into debug mode
        // XXX username/password handling at some point.
        properties.put(GlassfishModule.USERNAME_ATTR, DEFAULT_ADMIN_NAME);
        properties.put(GlassfishModule.PASSWORD_ATTR, DEFAULT_ADMIN_PASSWORD);
        return newProperties;
    }

    /**
     * Add new <code>String</code> storedValue into <code>Map</code> when storedValue
     * with specified key does not exist.
     * <p/>
     * @param map   Map to be checked and updated.
     * @param key   Key used to search for already existing storedValue.
     * @param value Value to be added when nothing is found.
     * @return Value stored in <code>Map</code> or <code>value</code> argument
     *         when no value was stored int the <code>Map</code>
     */
    private static String updateString(Map<String, String> map, String key,
            String value) {
        String result = map.get(key);
        if(result == null) {
            map.put(key, value);
            result = value;
        }
        return result;
    }

    /**
     * Add new <code>Integer</code> storedValue into <code>Map</code> when storedValue
     * with specified key does not exist.
     * <p/>
     * @param map   Map to be checked and updated.
     * @param key   Key used to search for already existing storedValue.
     * @param value Value to be added when nothing is found.
     * @return Value stored in <code>Map</code> or <code>value</code> argument
     *         when no value was stored int the <code>Map</code>
     */
    private static int updateInt(Map<String, String> map, String key, int value) {
        int result;
        String storedValue = map.get(key);
        try {
            // Throws NumberFormatException also when storedValue is null.
            result = Integer.parseInt(storedValue);
        } catch(NumberFormatException ex) {
            map.put(key, Integer.toString(value));
            result = value;
        }
        return result;
    }
    ////////////////////////////////////////////////////////////////////////////
    // Instance attributes                                                    //
    ////////////////////////////////////////////////////////////////////////////

    // Server properties
    private boolean removable = true;
    
    /** GlassFish properties. */
    private transient Map<String, String> properties;

    /** GlassFish server version. Initial storedValue is <code>null</code>. Proper
     *  GlassFish server version is set after first <code>version</code>
     *  administration command response is received. */
    private transient GlassFishVersion version;

    private transient CommonServerSupport commonSupport;
    private transient InstanceContent ic;
    private transient Lookup lookup;
    private transient Lookup full;
    final private transient Lookup.Result<GlassfishModuleFactory> lookupResult = Lookups.forPath(Util.GF_LOOKUP_PATH).lookupResult(GlassfishModuleFactory.class);;
    private transient Collection<? extends GlassfishModuleFactory> currentFactories = Collections.emptyList();
    
    // API instance
    private ServerInstance commonInstance;
    private GlassfishInstanceProvider instanceProvider;
    
    ////////////////////////////////////////////////////////////////////////////
    // Constructors                                                           //
    ////////////////////////////////////////////////////////////////////////////

    private GlassfishInstance(Map<String, String> ip, GlassfishInstanceProvider instanceProvider, boolean updateNow) {
        String deployerUri = null;
        this.version = null;
        try {
            ic = new InstanceContent();
            lookup = new AbstractLookup(ic);
            full = lookup;
            this.instanceProvider = instanceProvider;
            String domainDirPath = ip.get(GlassfishModule.DOMAINS_FOLDER_ATTR);
            String domainName = ip.get(GlassfishModule.DOMAIN_NAME_ATTR);
            if (null != domainDirPath && null != domainName) {
                File domainDir = new File(domainDirPath,domainName);
                PortCollection pc = new PortCollection();
                if (Util.readServerConfiguration(domainDir, pc)) {
                    ip.put(GlassfishModule.ADMINPORT_ATTR, Integer.toString(pc.getAdminPort()));
                    ip.put(GlassfishModule.HTTPPORT_ATTR, Integer.toString(pc.getHttpPort()));
                }
            }
            this.properties = prepareProperties(ip);
            commonSupport = new CommonServerSupport(lookup, this, instanceProvider);

            // Flag this server URI as under construction
            deployerUri = getDeployerUri();
            GlassfishInstanceProvider.activeRegistrationSet.add(deployerUri);
            if (null == instanceProvider.getInstance(deployerUri)) {
                ic.add(this); // Server instance in lookup (to find instance from node lookup)

                ic.add(commonSupport); // Common action support, e.g start/stop, etc.
                commonInstance = ServerInstanceFactory.createServerInstance(this);

                // make this instance publicly accessible
                instanceProvider.addServerInstance(this);
            }
            if (updateNow) {
                updateModuleSupport();
            }
        } finally {
            if(deployerUri != null) {
                GlassfishInstanceProvider.activeRegistrationSet.remove(deployerUri);
            }
        }
    }



    ////////////////////////////////////////////////////////////////////////////
    // Getters and Setters                                                    //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Get GlassFish properties.
     * <p/>
     * @return GlassFish properties.
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Set GlassFish properties.
     * <p/>
     * @param properties GlassFish properties to set
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Fake Getters from GlassFishServer interface                            //
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get GlassFish server host from stored properties.
     * <p/>
     * @return lassFish server host.
     */
    @Override
    public String getHost() {
        return properties.get(GlassfishModule.HOSTNAME_ATTR);
    }

    /**
     * Get GlassFish server port from stored properties.
     * <p/>
     * @return GlassFish server port.
     */
    @Override
    public int getPort() {
        return intProperty(GlassfishModule.HTTPPORT_ATTR);
    }

    /**
     * Get GlassFish server administration port from stored properties.
     * <p/>
     * @return GlassFish server administration port.
     */
    @Override
    public int getAdminPort() {
        return intProperty(GlassfishModule.ADMINPORT_ATTR);
    }

    /**
     * Get GlassFish server administration user name from stored properties.
     * <p/>
     * @return GlassFish server administration user name.
     */
    @Override
    public String getAdminUser() {
        return properties.get(GlassfishModule.USERNAME_ATTR);
    }

    /**
     * Get GlassFish server administration user's password from
     * stored properties.
     * <p/>
     * @return GlassFish server administration user's password.
     */
    @Override
    public String getAdminPassword() {
        return properties.get(GlassfishModule.PASSWORD_ATTR);
    }

    /**
     * Get GlassFish server domains folder from stored properties.
     * <p/>
     * @return GlassFish server domains folder.
     */
    @Override
    public String getDomainsFolder() {
        return properties.get(GlassfishModule.DOMAINS_FOLDER_ATTR);
    }

    /**
     * Set GlassFish server domain name from stored properties.
     * <p/>
     * @param domainsFolder GlassFish server domain name.
     */
    @Override
    public String getDomainName() {
        return properties.get(GlassfishModule.DOMAIN_NAME_ATTR);
    }

    /**
     * Get GlassFish server URL from stored properties.
     * <p/>
     * @return GlassFish server URL.
     */
    @Override
    public String getUrl() {
        return properties.get(GlassfishModule.URL_ATTR);
    }

    /**
     * Get GlassFish server installation root.
     * <p/>
     * @return Server installation root.
     */
    @Override
    public String getServerHome() {
        return properties.get(GlassfishModule.GLASSFISH_FOLDER_ATTR);
    }

    /**
     * Get GlassFish server version.
     * <p/>
     * @return GlassFish server version or <code>null</code> when version is
     *         not known.
     */
    @Override
    public GlassFishVersion getVersion() {
        return version;
    }

    /**
     * Get GlassFish server administration interface type.
     * <p/>
     * @return GlassFish server administration interface type.
     */
    @Override
    public GlassFishAdminInterface getAdminInterface() {
        return GlassFishAdminInterface.HTTP;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Fake Getters                                                           //
    ////////////////////////////////////////////////////////////////////////////
    
    public String getInstallRoot() {
        return properties.get(GlassfishModule.INSTALL_FOLDER_ATTR);
    }

    public String getGlassfishRoot() {
        return properties.get(GlassfishModule.GLASSFISH_FOLDER_ATTR);
    }

    @Override
    public String getDisplayName() {
        return properties.get(GlassfishModule.DISPLAY_NAME_ATTR);
    }

    public String getDeployerUri() {
        return properties.get(GlassfishModule.URL_ATTR);
    }

    public String getUserName() {
        return properties.get(GlassfishModule.USERNAME_ATTR);
    }

    /**
     * Returns property value to which the specified <code>key</code> is mapped,
     * or <code>null</code> if this map contains no mapping for the
     * <code>key</code>.
     * <p/>
     * @param key Key whose associated value is to be returned.
     */
    public String getProperty(String key) {
        return properties.get(key);
    }

    /**
     * Associates the specified <code>value</code> with the specified
     * <code>key</code> in this map.
     * <p/>
     * If the map previously contained a mapping for the key, the old value
     * is replaced by the specified value.
     * @param key   Key with which the specified value is to be associated.
     * @param value Value to be associated with the specified key.
     */
    public void putProperty(String key, String value) {
        properties.put(key, value);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Methods                                                           //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Get property storedValue with given <code>name</code> as <code>int</code>
     * storedValue.
     * <p/>
     * Works for positive values only because <code>-1</code> storedValue is reserved
     * for error conditions.
     * <p/>
     * @param name Name of property to be retrieved.
     * @return Property storedValue as <code>int</code> or <code>-1</code>
     *         if property cannot be converted to integer storedValue.
     */
    private int intProperty(String name) {
        String property = properties.get(name);
        if (property == null) {
            Logger.getLogger("glassfish").log(Level.WARNING,
                    "Cannot convert null value to a number");
            return -1;
        }
        try {
            return Integer.parseInt(property);
        } catch (NumberFormatException nfe) {
            Logger.getLogger("glassfish").log(Level.WARNING, "Cannot convert "+
                    property +" to a number: ", nfe);
            return -1;
        }
    }

    private void updateFactories() {
        // !PW FIXME should read asenv.bat on windows.
        Properties asenvProps = new Properties();
        String homeFolder = getGlassfishRoot();
        File asenvConf = new File(homeFolder, "config/asenv.conf"); // NOI18N
        if(asenvConf.exists()) {
            InputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(asenvConf));
                asenvProps.load(is);
            } catch(FileNotFoundException ex) {
                Logger.getLogger("glassfish").log(Level.WARNING, null, ex); // NOI18N
            } catch(IOException ex) {
                Logger.getLogger("glassfish").log(Level.WARNING, null, ex); // NOI18N
                asenvProps.clear();
            } finally {
                if(is != null) {
                    try { is.close(); } catch (IOException ex) { }
                }
            }
        } else {
            Logger.getLogger("glassfish").log(Level.WARNING, "{0} does not exist", asenvConf.getAbsolutePath()); // NOI18N
        }
        Set<GlassfishModuleFactory> added = new HashSet<GlassfishModuleFactory>();
        //Set<GlassfishModuleFactory> removed = new HashSet<GlassfishModuleFactory>();
        synchronized (lookupResult) {
            Collection<? extends GlassfishModuleFactory> factories = lookupResult.allInstances();
            added.addAll(factories);
            added.removeAll(currentFactories);
            currentFactories = factories;

            List<Lookup> proxies = new ArrayList<Lookup>();
            proxies.add(lookup);
            for (GlassfishModuleFactory moduleFactory : added) {
                if(moduleFactory.isModuleSupported(homeFolder, asenvProps)) {
                    Object t = moduleFactory.createModule(lookup);
                    if (null == t) {
                        Logger.getLogger("glassfish").log(Level.WARNING, "{0} created a null module", moduleFactory); // NOI18N
                    } else {
                        ic.add(t);
                        if (t instanceof Lookup.Provider) {
                            proxies.add(Lookups.proxy((Lookup.Provider) t));
                        }
                    }
                }
            }

            if (!proxies.isEmpty()) {
                full = new ProxyLookup(proxies.toArray(new Lookup[proxies.size()]));
            }
        }

    }
    
    void updateModuleSupport() {
        // Find all modules that have NetBeans support, add them to lookup if server
        // supports them.
        updateFactories();
        lookupResult.addLookupListener(this);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        updateFactories();
    }

    /** 
     * Creates a GlassfishInstance object for a server installation.  This
     * instance should be added to the the provider registry if the caller wants
     * it to be persisted for future sessions or searchable.
     * 
     * @param displayName display name for this server instance.
     * @param homeFolder install folder where server code is located.
     * @param httpPort http port for this server instance.
     * @param adminPort admin port for this server instance.
     * @return GlassfishInstance object for this server instance.
     */
    public static GlassfishInstance create(String displayName, String installRoot, 
            String glassfishRoot, String domainsDir, String domainName, int httpPort, 
            int adminPort,String url, String uriFragment, GlassfishInstanceProvider gip) {
        Map<String, String> ip = new HashMap<String, String>();
        ip.put(GlassfishModule.DISPLAY_NAME_ATTR, displayName);
        ip.put(GlassfishModule.INSTALL_FOLDER_ATTR, installRoot);
        ip.put(GlassfishModule.GLASSFISH_FOLDER_ATTR, glassfishRoot);
        ip.put(GlassfishModule.DOMAINS_FOLDER_ATTR, domainsDir);
        ip.put(GlassfishModule.DOMAIN_NAME_ATTR, domainName);
        ip.put(GlassfishModule.HTTPPORT_ATTR, Integer.toString(httpPort));
        ip.put(GlassfishModule.ADMINPORT_ATTR, Integer.toString(adminPort));
        ip.put(GlassfishModule.URL_ATTR, url);
        // extract the host from the URL
        String[] bigUrlParts = url.split("]");
        if (null != bigUrlParts && bigUrlParts.length > 1) {
            String[] urlParts = bigUrlParts[1].split(":"); // NOI18N
            if (null != urlParts && urlParts.length > 2) {
                ip.put(GlassfishModule.HOSTNAME_ATTR, urlParts[2]);
            }
        }
        GlassfishInstance result = new GlassfishInstance(ip, gip, true);
        return result;
    }
    
    public static GlassfishInstance create(Map<String, String> ip,GlassfishInstanceProvider gip, boolean updateNow) {
        return new GlassfishInstance(ip, gip, updateNow);
    }

    public static GlassfishInstance create(Map<String, String> ip,GlassfishInstanceProvider gip) {
        GlassfishInstance result = new GlassfishInstance(ip, gip, true);
        return result;
    }
    
    public ServerInstance getCommonInstance() {
        return commonInstance;
    }
        
    public CommonServerSupport getCommonSupport() {
        return commonSupport;
    }
    
    @Override
    public Lookup getLookup() {
        synchronized (lookupResult) {
            return full;
        }
    }
    
    public void addChangeListener(final ChangeListener listener) {
        commonSupport.addChangeListener(listener);
    }

    public void removeChangeListener(final ChangeListener listener) {
        commonSupport.removeChangeListener(listener);
    }
    
    public ServerState getServerState() {
        return commonSupport.getServerState();
    }

    void stopIfStartedByIde(long timeout) {
        if(commonSupport.isStartedByIde()) {
            ServerState state = commonSupport.getServerState();
            if(state == ServerState.STARTING ||
                    (state == ServerState.RUNNING && commonSupport.isReallyRunning())) {
                try {
                    Future<OperationState> stopServerTask = commonSupport.stopServer(null);
                    if(timeout > 0) {
                        OperationState opState = stopServerTask.get(timeout, TimeUnit.MILLISECONDS);
                        if(opState != OperationState.COMPLETED) {
                            Logger.getLogger("glassfish").info("Stop server failed..."); // NOI18N
                        }
                    }
                } catch(TimeoutException ex) {
                    Logger.getLogger("glassfish").log(Level.FINE, "Server {0} timed out sending stop-domain command.", getDeployerUri()); // NOI18N
                } catch(Exception ex) {
                    Logger.getLogger("glassfish").log(Level.INFO, ex.getLocalizedMessage(), ex); // NOI18N
                }
            }
        } else {
            // prevent j2eeserver from stoping an authenticated server that
            // it did not start.
            commonSupport.disableStop();
        }
    }

    // ------------------------------------------------------------------------
    // ServerInstance interface implementation
    // ------------------------------------------------------------------------

    // TODO -- this should be done differently
    @Override
    public String getServerDisplayName() {
        return commonSupport.getInstanceProvider().getDisplayName(getDeployerUri());
    }

    @Override
    public Node getFullNode() {
        Logger.getLogger("glassfish").finer("Creating GF Instance node [FULL]"); // NOI18N
        return new Hk2InstanceNode(this, true);
    }

    @Override
    public Node getBasicNode() {
        Logger.getLogger("glassfish").finer("Creating GF Instance node [BASIC]"); // NOI18N
        return new Hk2InstanceNode(this, false);
    }
    
    @Override
    public JComponent getCustomizer() {
        JPanel commonCustomizer = new InstanceCustomizer(commonSupport);
        JPanel vmCustomizer = new VmCustomizer(commonSupport);

        Collection<JPanel> pages = new LinkedList<JPanel>();
        Collection<? extends CustomizerCookie> lookupAll = lookup.lookupAll(CustomizerCookie.class);
        for(CustomizerCookie cookie : lookupAll) {
            pages.addAll(cookie.getCustomizerPages());
        }
        pages.add(vmCustomizer);

        JTabbedPane tabbedPane = null;
        for(JPanel page : pages) {
            if(tabbedPane == null) {
                tabbedPane = new JTabbedPane();
                tabbedPane.add(commonCustomizer);
            }
            
            tabbedPane.add(page);
        }
        
        return tabbedPane != null ? tabbedPane : commonCustomizer;
    }

    @Override
    public boolean isRemovable() {
        return removable;
    }

    @Override
    public void remove() {
        // Just in case...
        if(!removable) {
            return;
        }
        
        // !PW FIXME Remove debugger hooks, if any
//        DebuggerManager.getDebuggerManager().removeDebuggerListener(debuggerStateListener);

        stopIfStartedByIde(3000L);
        
        // close the server io window
        String uri = getDeployerUri();
        InputOutput io = LogViewMgr.getServerIO(uri);
        if(io != null && !io.isClosed()) {
            io.closeInputOutput();
        }

        Collection<? extends RemoveCookie> lookupAll = lookup.lookupAll(RemoveCookie.class);
        for(RemoveCookie cookie: lookupAll) {
            cookie.removeInstance(getDeployerUri());
        }

        instanceProvider.removeServerInstance(this);
        ic.remove(this);
    }

    //
    // watch out for the localhost alias.
    //
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GlassfishInstance)) {
            return false;
        }
        GlassfishInstance other = (GlassfishInstance) obj;
        if (null == getDeployerUri()) {
            return false;
        }
        if (null == other.getDeployerUri()) {
            return false;
        }
        if (null == commonSupport) {
            return false;
        }
        if (null == commonSupport.getDomainName()) {
            return false;
        }
        if (null == other.getCommonSupport()) {
            return false;
        }
        if (null == other.getCommonSupport().getDomainName()) {
            return false;
        }
        return getDeployerUri().replace("127.0.0.1", "localhost").equals(other.getDeployerUri().replace("127.0.0.1", "localhost")) &&
                commonSupport.getDomainName().equals(other.getCommonSupport().getDomainName()) &&
                commonSupport.getDomainsRoot().equals(other.getCommonSupport().getDomainsRoot()) &&
                commonSupport.getHttpPort().equals(other.getCommonSupport().getHttpPort());
    }

    @Override
    public int hashCode() {
        String tmp = getDeployerUri().replace("127.0.0.1", "localhost")+commonSupport.getHttpPort()+
                commonSupport.getDomainsRoot()+commonSupport.getDomainName();
        return tmp.hashCode();
    }

}
