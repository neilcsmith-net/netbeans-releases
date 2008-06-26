/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.remote.support;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import org.openide.util.Exceptions;

/**
 *
 * @author Sergey Grinev
 */
public class RemoteCopySupport extends RemoteConnectionSupport {
        
    public RemoteCopySupport(String host, String user) {
        super(host, user);
        
        // copy(remote, local);
    }
    
    
    @Override
    protected Channel createChannel() throws JSchException {
        return session.openChannel("exec");
    }

    private void setChannelCommand(String cmd) {
        ((ChannelExec) getChannel()).setCommand(cmd); //NOI18N
    }
    
    // TODO: not sure why we can't recreate channels through session?
    private void revitalize() {
        try {
            channel = this.createChannel();
        } catch (JSchException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public boolean copy(String remoteName, String localName) {
        FileOutputStream fos = null;
        try {
            String prefix = null;
            if (new File(localName).isDirectory()) {
                prefix = localName + File.separator;
            }

            // exec 'scp -f rfile' remotely
            String command = "scp -f " + remoteName; //NOI18N
            setChannelCommand(command); //TODO: absolutize

//            Channel channel = session.openChannel("exec");
//            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            byte[] buf = new byte[1024];

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            while (true) {
                long start = System.currentTimeMillis();
                
                int c = checkAck(in);
                if (c != 'C') {
                    break;
                }

                // read '0644 '
                in.read(buf, 0, 5);

                long filesize = 0L;
                while (true) {
                    if (in.read(buf, 0, 1) < 0) {
                        // error
                        break;
                    }
                    if (buf[0] == ' ') {
                        break;
                    }
                    filesize = filesize * 10L + (long) (buf[0] - '0');
                }

                String file = null;
                for (int i = 0;; i++) {
                    in.read(buf, i, 1);
                    if (buf[i] == (byte) 0x0a) {
                        file = new String(buf, 0, i);
                        break;
                    }
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();

                // read a content of lfile
                fos = new FileOutputStream(prefix == null ? localName : prefix + file);
                int foo;
                while (true) {
                    if (buf.length < filesize) {
                        foo = buf.length;
                    } else {
                        foo = (int) filesize;
                    }
                    foo = in.read(buf, 0, foo);
                    if (foo < 0) {
                        // error 
                        break;
                    }
                    fos.write(buf, 0, foo);
                    filesize -= foo;
                    if (filesize == 0L) {
                        break;
                    }
                }
                fos.close();
                fos = null;

                if (checkAck(in) != 0) {
                    System.exit(0);
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();

                System.err.println("Copying: filesize="+filesize+"b, file="+file + " took " + (System.currentTimeMillis() - start) + " ms");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (channel.isConnected()) {
                channel.disconnect();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception ee) {
            }
        }
        
        revitalize();
        return true;
    }

    private static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) {
            return b;
        }
        if (b == -1) {
            return b;
        }
        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) { // error

                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error

                System.out.print(sb.toString());
            }
        }
        return b;
    }
    
    
    // shouldn't be there but RemoteCommandSupport is not finished yet
    public boolean run(String command) {
        try {
            long startTime = System.currentTimeMillis();
            setChannelCommand(command); 
            InputStream is = channel.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringWriter out = new StringWriter();
            
            channel.connect();
            
            
            
            String line;
            while ((line = in.readLine()) != null || !channel.isClosed()) {
                if (line!=null) {
                    out.write(line);
                    out.flush();
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }
            in.close();
            is.close();
            
            System.err.println("run `" + command + "` took " + (System.currentTimeMillis() - startTime) + " ms.");
            String output = out.toString();
            if (output.length() > 0) System.err.println(output);
        } catch (JSchException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        } finally {
            if (channel.isConnected()) {
                channel.disconnect();
            }
        }
        revitalize();
        return true;
    }
}
