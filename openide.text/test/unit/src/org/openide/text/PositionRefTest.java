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


package org.openide.text;

import java.util.Date;
import java.beans.*;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javax.swing.text.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;

import junit.textui.TestRunner;

import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;

import org.openide.text.CloneableEditorSupport;
import org.openide.text.FilterDocument;
import org.openide.text.NbDocument;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  Petr Nejedly
 */
public class PositionRefTest extends NbTestCase implements CloneableEditorSupport.Env {
   
    /** the support to work with */
    private CES support;

    // Env variables
    private String content = "Hello";
    private boolean valid = true;
    private boolean modified = false;
    private Date date = new Date ();
    private transient PropertyChangeSupport prop = new PropertyChangeSupport(this);
    private transient VetoableChangeSupport veto = new VetoableChangeSupport(this);
    private Exception exception;
        
    public PositionRefTest(String s) {
        super(s);
    }
    
    public static void main(String[] args) {
        TestRunner.run(new NbTestSuite(PositionRefTest.class));
    }

    protected void setUp () {
        support = new CES (this, org.openide.util.Lookup.EMPTY);
    }

    
    /**
     * Creates a PositionRef biased backwards and verifies it behaves correctly,
     * then closes and reopens the document and checks again
     */
    public void testBiasSurvivesStateChanges() throws Exception {
	// open the document
        Document doc = support.openDocument();
        
        PositionRef back = support.createPositionRef(3, Position.Bias.Backward);
        PositionRef forw = support.createPositionRef(3, Position.Bias.Forward);
        
        doc.insertString(3, "_", null);
        assertEquals("Backwards position should not move for insert at its position",
                3, back.getOffset());
        assertEquals("Forwards position should move for insert at its position",
                4, forw.getOffset());        

        // move positions at the same offset again
        doc.remove(3, 1);

        support.close();
        doc = support.openDocument();

        doc.insertString(3, "_", null);
        assertEquals("Backwards position should not move for insert at its position",
                3, back.getOffset());
        assertEquals("Forwards position should move for insert at its position",
                4, forw.getOffset());        
    }
        
    //
    // Implementation of the CloneableEditorSupport.Env
    //
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        prop.addPropertyChangeListener (l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        prop.removePropertyChangeListener (l);
    }

    public void addVetoableChangeListener(VetoableChangeListener l) {
        veto.addVetoableChangeListener (l);
    }

    public void removeVetoableChangeListener(VetoableChangeListener l) {
        veto.removeVetoableChangeListener (l);
    }
    
    
    public org.openide.windows.CloneableOpenSupport findCloneableOpenSupport() {
        return support;
    }
    
    public String getMimeType() {
        return "text/plain";
    }
    
    public java.util.Date getTime() {
        return date;
    }
    
    public java.io.InputStream inputStream() throws java.io.IOException {
	return new ByteArrayInputStream(content.getBytes());
    }
    
    public java.io.OutputStream outputStream() throws java.io.IOException {
        return new ByteArrayOutputStream();
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public boolean isModified() {
        return modified;
    }

    public void markModified() throws java.io.IOException {
        modified = true;
    }
    
    public void unmarkModified() {
        modified = false;
    }
    
    /** Implementation of the CES */
    private final class CES extends CloneableEditorSupport {
        
        public CES (Env env, org.openide.util.Lookup l) {
            super (env, l);
        }
        
        protected String messageName() {
            return "Name";
        }
        
        protected String messageOpened() {
            return "Opened";
        }
        
        protected String messageOpening() {
            return "Opening";
        }
        
        protected String messageSave() {
            return "Save";
        }
        
        protected String messageToolTip() {
            return "ToolTip";
        }        

        protected boolean canClose () {
            return true;
        }

    } // end of CES

}
