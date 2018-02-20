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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.j2ee.sun.appsrvapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/* new algo to test of an app server(8.1 and 9.0) is secured or not
 * @author ludo champenois, and Jean Francois Arcand
 *
 **/

public class PortDetector {
    
    private static final int PORT_CHECK_TIMEOUT = 4000; // Port check timeout in ms

    /**
     *  This method accepts a hostname and port #.  It uses this information
     *  to attempt to connect to the port, send a test query, analyze the
     *  result to determine if the port is secure or unsecure (currently only
     *  http / https is supported).
     * it might emit a warning in the server log for GlassFish cases
     * No Harm, just an annoying warning, so we need to use this call only when really needed
     */
    public static boolean isSecurePort(String hostname, int port) 
            throws IOException, ConnectException, SocketTimeoutException {
        // Open the socket with a short timeout for connects and reads.
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(hostname, port), PORT_CHECK_TIMEOUT);
            socket.setSoTimeout(PORT_CHECK_TIMEOUT);
        } catch(SocketException ex) { // this could be bug 70020 due to SOCKs proxy not having localhost
            String socksNonProxyHosts = System.getProperty("socksNonProxyHosts");
            if(socksNonProxyHosts != null && socksNonProxyHosts.indexOf("localhost") < 0) {
                String localhost = socksNonProxyHosts.length() > 0 ? "|localhost" : "localhost";
                System.setProperty("socksNonProxyHosts",  socksNonProxyHosts + localhost);
                ConnectException ce = new ConnectException();
                ce.initCause(ex);
                throw ce; //status unknow at this point
                //next call, we'll be ok and it will really detect if we are secure or not
            }
        }
        
        //This is the test query used to ping the server in an attempt to
        //determine if it is secure or not.
        String testQuery = "GET / HTTP/1.0";
        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        pw.println(testQuery);
        pw.println();
        pw.flush();
        // Get the result
        InputStream is = socket.getInputStream();
        byte[] respArr = new byte[1024];
        boolean isSecure = true;
        while (is.read(respArr) != -1) {
            // Determine protocol from result
            // Can't read https response w/ OpenSSL (or equiv), so use as
            // default & try to detect an http response.
            String resp = new String(respArr);
            if (checkHelper(resp) == false) {
                isSecure = false;
                break;
            }
        }
        socket.close();
        return isSecure;
    }

    private static boolean checkHelper(String respText) {
        boolean isSecure = true;
        if (respText.startsWith("http/1.") || respText.startsWith("HTTP/1.")) {
            isSecure = false;
        } else if (respText.contains("<html")) {
            isSecure = false;
        } else if (respText.contains("</html")) {
            // New test added to resolve 106245
            // when the user has the IDE use a proxy (like webcache.foo.bar.com),
            // the response comes back as "d><title>....</html>".  It looks like
            // something eats the "<html><hea" off the front of the data that
            // gets returned.
            //
            // This test makes an allowance for that behavior. I figure testing
            // the likely "last bit" is better than testing a bit that is close
            // to the data that seems to get eaten.
            //
            isSecure = false;
        } else if (respText.contains("connection: ")) {
            isSecure = false;
        }
        return isSecure;
    }
    
    
    public static void main(String[] args) throws IOException{
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        System.out.println("host: " + " port: " + port);
        System.out.println("isSecure: " + isSecurePort(host,port));
    }
    
    
}

