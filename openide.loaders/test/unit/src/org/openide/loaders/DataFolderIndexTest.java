/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.openide.loaders;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.util.Mutex;

/**
 * Tests Index cookio of DataFolder (when uses DataFilter).
 *
 * @author Jiri Rechtacek
 */
public class DataFolderIndexTest extends LoggingTestCaseHid {
    DataFolder df;
    FileObject fo;
    
    /** Constructor required by JUnit.
     * @param testName method name to be used as testcase
     */
    public DataFolderIndexTest(String s) {
        super(s);
    }
    
    protected void setUp () throws Exception {
        registerIntoLookup(new ErrManager());
        registerIntoLookup(new Pool ());    

		FileObject old = Repository.getDefault ().getDefaultFileSystem ().findResource ("TestTemplates");
		if (old != null) {
			old.delete();
		}
        
		fo = Repository.getDefault ().getDefaultFileSystem ().getRoot ().createFolder ("TestTemplates");
		df = DataFolder.findFolder (fo);
		assertNotNull ("DataFolder found for AA", df);

		df.getPrimaryFile ().createData ("marie");
		df.getPrimaryFile ().createData ("jakub");
		df.getPrimaryFile ().createData ("eva");
		df.getPrimaryFile ().createData ("adam");

		assertNotNull ("Folder " + df + " has a children.", df.getChildren ());
		assertEquals ("Folder " + df + " has 4 childs.", 4, df.getChildren ().length);

        ErrManager.resetMessages();
    }
    
    public void testIndexWithoutInitialization() throws Exception {
        Node n = df.getNodeDelegate();
        
        Index fromNode = (Index) n.getLookup ().lookup (Index.class);
        assertNotNull ("DataFolderNode has Index.", fromNode);

        int x = fromNode.getNodesCount();
        assertEquals("The same number of nodes like folder children", df.getChildren().length, x);
    }

    public void testIndexWithoutInitializationInReadAccess() throws Exception {
        org.openide.nodes.Children.MUTEX.readAccess(new Mutex.ExceptionAction () {
            public Object run () throws Exception {
                Node n = df.getNodeDelegate();

                Index fromNode = (Index) n.getLookup ().lookup (Index.class);
                assertNotNull ("DataFolderNode has Index.", fromNode);

                int x = fromNode.getNodesCount();
                assertEquals("Folder has few children", 4, df.getChildren().length);
                assertEquals("Cannot initialize the count in nodes as we are in read access", 0, x);
                return null;
            }
        });
        
        if (ErrManager.messages.length() > 0) {
            fail("No messages shall be reported: " + ErrManager.messages);
        }
    }
    
    public void testIndexNodesWithoutInitialization() throws Exception {
        Node n = df.getNodeDelegate();
        
        Index fromNode = (Index) n.getLookup ().lookup (Index.class);
        assertNotNull ("DataFolderNode has Index.", fromNode);

        int x = fromNode.getNodes().length;
        assertEquals("The same number of nodes like folder children", df.getChildren().length, x);
    }
    
    public void testWithoutFilter () throws Exception {
        testMatchingIndexes (df, df.getNodeDelegate ());
    }
    
    public void testIndexCookieOfferedOnlyWhenAppropriate() throws Exception {
        Node n = df.getNodeDelegate();
        assertNotNull("have an index cookie on SFS", n.getLookup().lookup(Index.class));
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(getWorkDir());
        n = DataFolder.findFolder(lfs.getRoot()).getNodeDelegate();
        assertNull("have no index cookie on a local folder", n.getLookup().lookup(Index.class));
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject x = fs.getRoot().createFolder("x");
        FileObject y = fs.getRoot().createFolder("y");
        y.setAttribute("DataFolder.Index.reorderable", Boolean.TRUE);
        n = DataFolder.findFolder(x).getNodeDelegate();
        assertNull("have no index cookie on a random folder in a random FS", n.getLookup().lookup(Index.class));
        n = DataFolder.findFolder(y).getNodeDelegate();
        assertNotNull("do have index cookie if magic attr is set", n.getLookup().lookup(Index.class));
    }
    
    private void testMatchingIndexes (DataFolder f, Node n) {
        Node [] arr = n.getChildren ().getNodes (true);
        
        Index fromNode = (Index) n.getLookup ().lookup (Index.class);
        assertNotNull ("DataFolderNode has Index.", fromNode);
        
        Index fromFolder = new DataFolder.Index (f, n);
        assertNotNull ("DataFolderNode has Index.", fromFolder);
        
        assertTrue ("Index contains some items", fromNode.getNodesCount () > 0);
        assertTrue ("Index contains some items", fromFolder.getNodesCount () > 0);
        
        log ("Node's index: " + Arrays.asList (fromNode.getNodes ()));
        log ("Folder's index: " + Arrays.asList (fromFolder.getNodes ()));
        
        for (int i = 0; i < arr.length; i++) {
            log("Computing index for [" + i + "] which is node " + arr[i]);
            int index = fromNode.indexOf (arr [i]);
            log("Index computed to be " + index);
            
            log("Computing index from the folder [" + i + "]: " + arr[i]);
            int folderIndex = fromFolder.indexOf (arr [i]);
            log("Folder index is to be " + folderIndex);
            
            if (folderIndex != index) {
                fail(
                    i + "th iteration - Node " + arr [i] + 
                    " has as same position in Node's Index [" + 
                    Arrays.asList (fromNode.getNodes ()) + "]" +
                    "as in folder's Index [" + 
                    Arrays.asList (fromFolder.getNodes ()) + "]. folder: " + 
                    folderIndex + " node: " + index
                );
            }
        }
    }
    
    private static final class Pool extends DataLoaderPool {
        public static DataLoader extra;
        
        
        protected java.util.Enumeration loaders () {
            if (extra == null) {
                return org.openide.util.Enumerations.empty ();
            } else {
                return org.openide.util.Enumerations.singleton (extra);
            }
        }
    }

    static final class ErrManager extends org.openide.ErrorManager {
        static final StringBuffer messages = new StringBuffer();
        static int nOfMessages;
        static final String DELIMITER = ": ";
        static final String WARNING_MESSAGE_START = WARNING + DELIMITER;
        
        static void resetMessages() {
            messages.delete(0, ErrManager.messages.length());
            nOfMessages = 0;
        }
        
        public void log(int severity, String s) {
            /*
            nOfMessages++;
            messages.append(severity + DELIMITER + s);
            messages.append('\n');
             */
        }
        
        public Throwable annotate(Throwable t, int severity,
                String message, String localizedMessage,
                Throwable stackTrace, java.util.Date date) {
            return t;
        }
        
        public Throwable attachAnnotations(Throwable t, Annotation[] arr) {
            return t;
        }
        
        public org.openide.ErrorManager.Annotation[] findAnnotations(Throwable t) {
            return null;
        }
        
        public org.openide.ErrorManager getInstance(String name) {
            return this;
        }
        
        public void notify(int severity, Throwable t) {
            StringWriter w = new StringWriter();
            t.printStackTrace(new PrintWriter(w));
            messages.append(w.toString());
        }
    }
    
}
