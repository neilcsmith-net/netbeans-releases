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


/*
 * SystemInfo.java
 *
 * Created on November 1, 2001, 6:33 PM
 */

package org.netbeans.xtest.pe.xmlbeans;

// for getting hostname 
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author  mb115822
 */
public class SystemInfo extends XMLBean {

    /** Creates new SystemInfo */
    public SystemInfo() {
        getSystemInfo();
    }
    
    // attributes
    public String   xmlat_host;
    //public String   xmlat_project;
    //public String   xmlat_build;
    public String   xmlat_osArch;
    public String   xmlat_osName;
    public String   xmlat_osVersion;
    public String   xmlat_javaVendor;
    public String   xmlat_javaVersion;
    public String   xmlat_userLanguage;
    public String   xmlat_userRegion;
    
    // child elements
    public SystemInfoExtra[] xmlel_SystemInfoExtra;
    
    // bussiness methods
    
    public static String getHost() {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException uhe) {
            // we cannot get the hostname
            host = "UnknownHost";
        }
        
        return host;
    }    
    
    public static String getOsArch() {
        return System.getProperty("os.arch");
    }
    
    public static String getOsName() {
        return System.getProperty("os.name");
    }
    
    public static String getOsVersion() {
        return System.getProperty("os.version");
    }
    
    public static String getJavaVendor() {
        return System.getProperty("java.vendor");
    }
    
    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }
    
    public static String getUserLanguage() {
        return System.getProperty("user.language");
    }
    
    public static String getUserRegion() {
        return System.getProperty("user.region");
    }
    
    public static String getPlatform() {
        return "OS: "+getOsName()+" "+getOsVersion()+" "+getOsArch()+", JDK: "+getJavaVersion()+" "+getJavaVendor();
    }        
    
    
    public void getSystemInfo() {      
        xmlat_host = getHost();
        xmlat_osArch = getOsArch();
        xmlat_osName = getOsName();
        xmlat_osVersion = getOsVersion();
        xmlat_javaVendor = getJavaVendor();
        xmlat_javaVersion = getJavaVersion();
        xmlat_userLanguage = getUserLanguage();
        xmlat_userRegion = getUserRegion();
    }

}
