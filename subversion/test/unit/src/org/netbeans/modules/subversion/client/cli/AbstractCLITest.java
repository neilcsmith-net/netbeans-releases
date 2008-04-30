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

package org.netbeans.modules.subversion.client.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.subversion.AbstractSvnTest;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.ISVNProperty;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.commandline.CmdLineClientAdapterFactory;

/**
 *
 * @author tomas
 */
public abstract class AbstractCLITest extends AbstractSvnTest {
    
    protected boolean importWC;
    protected String CI_FOLDER = "cifolder";    
    protected FileNotifyListener fileNotifyListener;
    
    public AbstractCLITest(String testName) throws Exception {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        importWC = true;
        if(getName().startsWith("testCheckout")) {
            importWC = false;            
        } 
        super.setUp();
        if(getName().startsWith("testCheckout") ) {
            cleanUpRepo(new String[] {CI_FOLDER});
        }        
        CmdLineClientAdapterFactory.setup13(null);      
    }

    protected boolean importOnSetup() {
        return importWC;
    }

    @Override
    protected void tearDown() throws Exception {
        if(getName().startsWith("testInfoLocked")) { 
            try {
                unlock(createFile("lockfile"), "unlock", true);
            } catch (Exception e) {
                // ignore
            }
        }
        super.tearDown();
    }    
        
    protected void assertInfos(ISVNInfo info, ISVNInfo refInfo) {
        assertNotNull(info);   
        assertNotNull(refInfo);   
        assertEquals(refInfo.getCopyRev(), info.getCopyRev());
        assertEquals(refInfo.getCopyUrl(), info.getCopyUrl());
        assertEquals(refInfo.getFile(), info.getFile());
        assertEquals(refInfo.getLastChangedDate(), info.getLastChangedDate());
        assertEquals(refInfo.getLastChangedRevision(), info.getLastChangedRevision());
        assertEquals(refInfo.getLastCommitAuthor(), info.getLastCommitAuthor());
        assertEquals(refInfo.getLastDatePropsUpdate(), info.getLastDatePropsUpdate());
        assertEquals(refInfo.getLastDateTextUpdate(), info.getLastDateTextUpdate());
        assertEquals(refInfo.getLockComment() != null ? refInfo.getLockComment().trim() : null, 
                     info.getLockComment() != null    ? info.getLockComment().trim()    : null);
        assertEquals(refInfo.getLockCreationDate(), info.getLockCreationDate());
        assertEquals(refInfo.getLockOwner(), info.getLockOwner());
        assertEquals(refInfo.getNodeKind(), info.getNodeKind());
        assertEquals(refInfo.getRepository(), info.getRepository());
        assertEquals(refInfo.getRevision(), info.getRevision());
        assertEquals(refInfo.getSchedule(), info.getSchedule());
        assertEquals(refInfo.getUrl(), info.getUrl());
        assertEquals(refInfo.getUrlString(), info.getUrlString());
        assertEquals(refInfo.getUuid(), info.getUuid());
    }
    
    protected void assertEntryArrays(ISVNDirEntry[] listedArray, ISVNDirEntry[] refArray) {
        assertEquals(listedArray.length, refArray.length);
        Map<String, ISVNDirEntry> entriesMap = new HashMap<String, ISVNDirEntry>();
        for (ISVNDirEntry e : listedArray) {
            entriesMap.put(e.getPath(), e);
        }
        ISVNDirEntry entry;
        for (int i = 0; i < refArray.length; i++) {
            entry = entriesMap.get(refArray[i].getPath());

            assertNotNull(entry);
            assertEquals(refArray[i].getPath(), entry.getPath());
            assertEquals(refArray[i].getHasProps(), entry.getHasProps());
            assertEquals(refArray[i].getLastChangedRevision(), entry.getLastChangedRevision());
            assertEquals(refArray[i].getLastCommitAuthor(), entry.getLastCommitAuthor());
            assertEquals(refArray[i].getNodeKind(), entry.getNodeKind());
            assertEquals(refArray[i].getSize(), entry.getSize());
            assertEquals(refArray[i].getLastChangedDate(), entry.getLastChangedDate());
        }
    }
        
    protected void assertNotifiedFiles(File... files) {
        Set<File> notifiedFiles = fileNotifyListener.getFiles();
        
        if(files.length != notifiedFiles.size()) {
            StringBuffer sb = new StringBuffer();
            sb.append("Expected files: \n");
            for (File file : files) {
                sb.append("\t");
                sb.append(file.getAbsolutePath());
                sb.append("\n");
            }
            sb.append("Notified files: \n");
            for (File file : notifiedFiles) {
                sb.append("\t");
                sb.append(file.getAbsolutePath());
                sb.append("\n");
            }    
            fail(sb.toString());
        }
        for (File f : files) {
            if(!notifiedFiles.contains(f)) fail("missing notification for file " + f);   
        }        
    }

    protected SVNUrl getFileUrl(File file) {
        return getTestUrl().appendPath(getWC().getName()).appendPath(file.getName());
    }

    protected class FileNotifyListener implements ISVNNotifyListener {
        private Set<File> files = new HashSet<File>();
        public void setCommand(int arg0) { }
        public void logCommandLine(String arg0) { }
        public void logMessage(String arg0) { }
        public void logError(String arg0) { }
        public void logRevision(long arg0, String arg1) { }
        public void logCompleted(String arg0) { }
        public void onNotify(File file, SVNNodeKind arg1) {
            files.add(file);
        }
        public Set<File> getFiles() {            
            return files;
        }        
    }
        
    protected File createFolder(String name) throws IOException {
        File file = new File(getWC(), name);
        file.mkdirs();
        return file;
    }
    
    protected File createFolder(File folder, String name) throws IOException {
        File file = new File(folder, name);
        file.mkdirs();
        return file;
    }
    
    protected File createFile(File folder, String name) throws IOException {
        File file = new File(folder, name);
        file.createNewFile();
        return file;
    }
    
    protected File createFile(String name) throws IOException {
        File file = new File(getWC(), name);
        file.createNewFile();
        return file;
    }

    protected ISVNClientAdapter getNbClient() {        
        ISVNClientAdapter c = new CommandlineClient();        
        fileNotifyListener = new FileNotifyListener();
        c.addNotifyListener(fileNotifyListener);
        return c;
    }
    
    protected ISVNClientAdapter getReferenceClient() {        
        ISVNClientAdapter c = SVNClientAdapterFactory.createSVNClient(CmdLineClientAdapterFactory.COMMANDLINE_CLIENT);        
        fileNotifyListener = new FileNotifyListener();
        c.addNotifyListener(fileNotifyListener);
        return c;
    }

    protected void clearNotifiedFiles() {
        fileNotifyListener.files.clear();
    }
                
    protected void write(File file, int data) throws IOException {
        OutputStream os = null;
        try {            
            os = new FileOutputStream(file);            
            os.write(data);
            os.flush();
        } finally {
            if (os != null) {
                os.close();
            }
        }        
    }
    
    protected void assertContents(File file, int contents) throws FileNotFoundException, IOException {        
        assertContents(new FileInputStream(file), contents);
    }
    
    protected void assertContents(InputStream is, int contents) throws FileNotFoundException, IOException {        
        try {
            int i = is.read();
            assertEquals(contents, i);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    
    protected void assertProperty(File file, String prop, String val) throws SVNClientException {
        ISVNClientAdapter c = getReferenceClient();
        ISVNProperty p = c.propertyGet(file, prop);
        assertEquals(val, new String(p.getData()));        
    }
    
    protected void assertProperty(File file, String prop, byte[] data) throws SVNClientException {
        ISVNClientAdapter c = getReferenceClient();
        ISVNProperty p = c.propertyGet(file, prop);
        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i], p.getData()[i]);                    
        }        
    }

    
}