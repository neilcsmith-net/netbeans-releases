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

package org.netbeans.installer;

import com.installshield.util.Log;
import com.installshield.wizard.RunnableWizardBeanState;
import com.installshield.wizard.WizardAction;
import com.installshield.wizard.WizardBeanEvent;
import com.installshield.wizard.WizardBuilderSupport;
import com.installshield.wizard.platform.win32.Win32RegistryService;
import com.installshield.wizard.service.ServiceException;
import com.installshield.wizard.service.security.SecurityService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/** This class is used to initialize some system properties at begining
 * of installation.
 */
public class SetSystemPropertiesAction extends WizardAction {
    String installedJdk = null;
    
    public void build(WizardBuilderSupport support) {
        try {
            support.putClass(Util.class.getName());
            support.putClass(RunCommand.class.getName());
            support.putRequiredService(SecurityService.NAME);
            support.putRequiredService(Win32RegistryService.NAME);
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }
    
    public void execute(WizardBeanEvent evt) {
        //RunnableWizardBeanState state = getState();
        //String msg = resolveString("$L(com.sun.installer.InstallerResources,INIT_PROPS_MSG)");
        //state.setStatusDescription(msg);
                              
        setAdminProperties();
        
	if (Util.isWindowsOS()) {
            installedJdk = findJdkHome();
            if (installedJdk != null)  {
                //If JDK is already installed then only installing NetBeans
                Util.setInstalledJdk(installedJdk);
                logEvent(this, Log.DBG,"installedJdk: " + installedJdk);
                Util.setJDKAlreadyInstalled(true);
            }
	}
    }

    private void setAdminProperties () {
        try {
            boolean isAdmin;

            SecurityService secService = (SecurityService)getServices().getService(SecurityService.NAME);
            isAdmin = secService.isCurrentUserAdmin();
	    Util.setBooleanPropertyValue("isAdmin",isAdmin);
            logEvent(this, Log.DBG,"isAdmin: " + isAdmin);
        }
        catch(ServiceException ex) {
            Util.logStackTrace(this, ex);                
        }
    }
    
    private String findJdkHome(){
        String jdkHome = null;
        try{
            logEvent(this, Log.DBG,"Checking Win32 Registry ... ");
            File regFile = File.createTempFile("forte",".reg");
            
            String command = "regedit -e " + regFile.getAbsolutePath() + " \"HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\"";
            RunCommand runCommand = new RunCommand();
            runCommand.execute(command);
            runCommand.getReturnStatus();
            BufferedReader reader;

            if (Util.isWindows98()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(regFile.getAbsolutePath())));
            } else if (Util.isWindowsNT()) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(regFile.getAbsolutePath())));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(regFile.getAbsolutePath()),"UTF-16"));
            }
            
            String line;
            while ((line = reader.readLine()) != null) {
                logEvent(this, Log.DBG,"findJdkHome line: " + line);
                if (line.startsWith("[HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit")){
                    String version = line.substring(line.lastIndexOf("\\") + 1);
                    if (version.startsWith("1.5.0")) {
                    
                        line = reader.readLine();
                        StringTokenizer st = new StringTokenizer(line,"\"");
                        String firstToken="",lastToken="";
                        if (st.hasMoreTokens())  firstToken = st.nextToken();
                        if (firstToken.compareTo("JavaHome") == 0){
                            while (st.hasMoreTokens())  lastToken = st.nextToken();
                            StringBuffer stringBuffer = new StringBuffer();
                            int index=0;
                            for (int i=0; i<lastToken.length(); i++) {
                                if ((lastToken.charAt(i) == '\\') && (index==0)) {
                                    index++;
                                }else {
                                    stringBuffer.append(lastToken.charAt(i));
                                    if(index > 0) index=0;
                                }
                            }
                            jdkHome=stringBuffer.toString();
                            if (!Util.checkJdkHome(jdkHome)) { 
				System.getProperties().put("corruptJdk",jdkHome);
				logEvent(this, Log.DBG, "corruptJdk: " + System.getProperties().get("corruptJdk"));
				jdkHome=null;
			    }
                        }
                    }
                }
            }
            reader.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return jdkHome;
    }
    
}
