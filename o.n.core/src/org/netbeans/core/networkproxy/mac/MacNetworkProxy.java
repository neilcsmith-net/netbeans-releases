/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.core.networkproxy.mac;

import com.sun.jna.Memory;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import org.netbeans.core.networkproxy.NetworkProxyResolver;
import org.netbeans.core.networkproxy.NetworkProxySettings;

/**
 *
 * @author lfischme
 */
public class MacNetworkProxy implements NetworkProxyResolver {
    
    private final static MacNetworkProxyLibrary cfNetworkLibrary = MacNetworkProxyLibrary.LIBRARY;
    private final static MacCoreFoundationLibrary cfLibrary = MacCoreFoundationLibrary.LIBRARY; 
    
    private final static NativeLibrary NETWORK_LIBRARY = NativeLibrary.getInstance("CFNetwork"); //NOI18N
    private final static String KEY_AUTO_DISCOVERY_ENABLE = "kCFNetworkProxiesProxyAutoDiscoveryEnable"; //NOI18N
    private final static String KEY_PAC_ENABLE = "kCFNetworkProxiesProxyAutoConfigEnable"; //NOI18N
    private final static String KEY_PAC_URL = "kCFNetworkProxiesProxyAutoConfigURLString"; //NOI18N
    private final static String KEY_HTTP_ENABLE = "kCFNetworkProxiesHTTPEnable"; //NOI18N
    private final static String KEY_HTTP_HOST = "kCFNetworkProxiesHTTPProxy"; //NOI18N
    private final static String KEY_HTTP_PORT = "kCFNetworkProxiesHTTPPort"; //NOI18N
    private final static String KEY_HTTPS_ENABLE = "kCFNetworkProxiesHTTPSEnable"; //NOI18N
    private final static String KEY_HTTPS_HOST = "kCFNetworkProxiesHTTPSProxy"; //NOI18N
    private final static String KEY_HTTPS_PORT = "kCFNetworkProxiesHTTPSPort"; //NOI18N
    private final static String KEY_SOCKS_ENABLE = "kCFNetworkProxiesSOCKSEnable"; //NOI18N
    private final static String KEY_SOCKS_HOST = "kCFNetworkProxiesSOCKSProxy"; //NOI18N
    private final static String KEY_SOCKS_PORT = "kCFNetworkProxiesSOCKSPort"; //NOI18N
    private final static String KEY_EXCEPTIONS_LIST = "kCFNetworkProxiesExceptionsList"; //NOI18N
    
    @Override
    public NetworkProxySettings getNetworkProxySettings() {        
        Pointer settingsDictionary = cfNetworkLibrary.CFNetworkCopySystemProxySettings(); 

        Pointer autoDiscoveryEnable = cfLibrary.CFDictionaryGetValue(settingsDictionary, getKeyCFStringRef(KEY_AUTO_DISCOVERY_ENABLE));
        if (getIntFromCFNumberRef(autoDiscoveryEnable) != 0) {
            return new NetworkProxySettings();
        }
        
        Pointer pacEnable = cfLibrary.CFDictionaryGetValue(settingsDictionary, getKeyCFStringRef(KEY_PAC_ENABLE));
        if (getIntFromCFNumberRef(pacEnable) != 0) {
            Pointer[] pacUrlPointer = new Pointer[1];
            if (cfLibrary.CFDictionaryGetValueIfPresent(settingsDictionary, getKeyCFStringRef(KEY_PAC_URL), pacUrlPointer)) {
                return new NetworkProxySettings(getStringFromCFStringRef(pacUrlPointer[0]));
            }
        }
        
        Pointer httpEnable = cfLibrary.CFDictionaryGetValue(settingsDictionary, getKeyCFStringRef(KEY_HTTP_ENABLE));
        Pointer httpsEnable = cfLibrary.CFDictionaryGetValue(settingsDictionary, getKeyCFStringRef(KEY_HTTPS_ENABLE));
        Pointer socksEnable = cfLibrary.CFDictionaryGetValue(settingsDictionary, getKeyCFStringRef(KEY_SOCKS_ENABLE));
        if (getIntFromCFNumberRef(httpEnable) != 0 || getIntFromCFNumberRef(httpsEnable) != 0 || getIntFromCFNumberRef(socksEnable) != 0) {
            String httpHost = getStringFromCFStringRef(getValueIfExists(settingsDictionary, KEY_HTTP_HOST));
            String httpPort = getStringFromCFNumberRef(getValueIfExists(settingsDictionary, KEY_HTTP_PORT));
            String httpsHost = getStringFromCFStringRef(getValueIfExists(settingsDictionary, KEY_HTTPS_HOST));
            String httpsPort = getStringFromCFNumberRef(getValueIfExists(settingsDictionary, KEY_HTTPS_PORT));
            String socksHost = getStringFromCFStringRef(getValueIfExists(settingsDictionary, KEY_SOCKS_HOST));
            String socksPort = getStringFromCFNumberRef(getValueIfExists(settingsDictionary, KEY_SOCKS_PORT));
            
            String[] noProxyHosts = getNoProxyHosts(getValueIfExists(settingsDictionary, KEY_EXCEPTIONS_LIST));
            
            return new NetworkProxySettings(httpHost, httpPort, httpsHost, httpsPort, socksHost, socksPort, noProxyHosts);
        }
        
        return new NetworkProxySettings(false);
    }
    
    private Pointer getKeyCFStringRef(String key) {
        return NETWORK_LIBRARY.getGlobalVariableAddress(key).getPointer(0L);
    }
    
    private String getStringFromCFStringRef(Pointer cfStringPointer) {
        if (cfStringPointer != null) {
            long lenght = cfLibrary.CFStringGetLength(cfStringPointer);
            long maxSize = cfLibrary.CFStringGetMaximumSizeForEncoding(lenght, 0x08000100); // 0x08000100 = UTF-8

            Pointer buffer = new Memory(maxSize);

            if (cfLibrary.CFStringGetCString(cfStringPointer, buffer, maxSize, 0x08000100)) { // 0x08000100 = UTF-8
                return buffer.getString(0L);
            }
        }
        
        return null;
    }
    
    private int getIntFromCFNumberRef(Pointer cfNumberPointer) {
        if (cfNumberPointer != null) {
            Pointer cfNumberType = cfLibrary.CFNumberGetType(cfNumberPointer);
            
            long numberSize = cfLibrary.CFNumberGetByteSize(cfNumberPointer);
            Pointer numberValue = new Memory(numberSize);
            if (cfLibrary.CFNumberGetValue(cfNumberPointer, cfNumberType, numberValue)) {
                return numberValue.getInt(0L);
            }
        }
        
        return 0;
    }
    
    private String getStringFromCFNumberRef(Pointer cfNumberPointer) {
        if (cfNumberPointer != null) {
            Pointer cfNumberType = cfLibrary.CFNumberGetType(cfNumberPointer);
            
            long numberSize = cfLibrary.CFNumberGetByteSize(cfNumberPointer);
            Pointer numberValue = new Memory(numberSize);
            if (cfLibrary.CFNumberGetValue(cfNumberPointer, cfNumberType, numberValue)) {
                return String.valueOf(numberValue.getInt(0L));
            }
        }
        
        return null;
    }
    
    private String[] getNoProxyHosts(Pointer noProxyHostsPointer) {
        if (noProxyHostsPointer != null) {
            long arrayLenght = cfLibrary.CFArrayGetCount(noProxyHostsPointer);
            String[] noProxyHosts = new String[(int) arrayLenght];
            for (long i = 0; i < arrayLenght; i++) {
                Pointer value = cfLibrary.CFArrayGetValueAtIndex(noProxyHostsPointer, new Pointer(i));
                String noProxyHost = getStringFromCFStringRef(value);
                noProxyHosts[(int) i] = noProxyHost;
            }
            
            return noProxyHosts;
        }
        
        return new String[0];
    }
    
    private Pointer getValueIfExists(Pointer settingsDictionary, String key) {
        Pointer[] returnValue = new Pointer[1];
        if (cfLibrary.CFDictionaryGetValueIfPresent(settingsDictionary, getKeyCFStringRef(key), returnValue)) {
            return returnValue[0];
        } else {
            return null;
        }
    }
}
