/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.cnd.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

import org.netbeans.modules.cnd.MIMENames;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.ExtensionList;
import org.openide.util.SharedClassObject;

/**
 *  Recognizes .h header files and create .h data objects for them
 *
 *  This data loader recognizes .h header data files, creates a data object for
 *  each file, and sets up an appropriate action menus for .h file objects.
 */
public final class HDataLoader extends CndAbstractDataLoader {
    
    private static HDataLoader instance = null;

    /** Serial version number */
    static final long serialVersionUID = -2924582006340980748L;

    /** The suffix list for C/C++ header files */
    private static final String[] hdrExtensions =
				{ "h", "H", "hpp", "hxx", "SUNWCCh" }; // NOI18N

    public HDataLoader() {
        super(HDataObject.class.getName());
        instance = this;
        createExtentions(hdrExtensions);
    }

    public HDataLoader(String representationClassName) {
	super(representationClassName);
        instance = this;
	createExtentions(hdrExtensions);
    }

    public HDataLoader(Class representationClass) {
	super(representationClass);
        instance = this;
	createExtentions(hdrExtensions);
    }

    public static HDataLoader getInstance(){
        if (instance == null) {
            instance = (HDataLoader) SharedClassObject.findObject(HDataLoader.class, true);
        }
        return instance;
    }
    
    public boolean resolveMimeType(String ext){
        ExtensionList extensions = getExtensions();
        for (Enumeration e = extensions.extensions(); e != null &&  e.hasMoreElements();) {
            String ex = (String) e.nextElement();
            if (ex != null && ex.equals(ext))
                return true;
        }
        return false;
    }

    protected String getMimeType(){
        return MIMENames.CHEADER_MIME_TYPE;
    }

    /** set the default display name */
    protected String defaultDisplayName() {
	return NbBundle.getMessage(HDataLoader.class, "PROP_HDataLoader_Name"); // NOI18N
    }

    /** Override because we don't have secondary files */
    protected FileObject findSecondaryFile(FileObject fo){
	return null;
    }

    /**
     *  This is a special detector which samples suffix-less header files looking for the
     *  string "-*- C++ -*-".
     *
     *  Note: Not all Sun Studio headerless includes contain this comment.
     */
    public boolean detectCPPByComment(FileObject fo){
        boolean ret = false;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            if (fo.canRead() && fo.getExt().length() == 0) {
                isr = new InputStreamReader(fo.getInputStream());
                br = new BufferedReader(isr);
                String line = null;
                try {
                    line = br.readLine();
                } catch (IOException ex) {
                }
                if(line != null){
                    if (line.startsWith("//") && line.indexOf("-*- C++ -*-") > 0) { // NOI18N
                        ret = true;
                    }
                }
            }
        } catch (IOException ex) {
//            ex.printStackTrace();
        } finally {
            if (br != null){
                try {
                    br.close();
                } catch (IOException ex) {
//                    ex.printStackTrace();
                }
            }
            if (isr != null){
                try {
                    isr.close();
                } catch (IOException ex) {
//                    ex.printStackTrace();
                }
            }
        }
        return ret;
    }

    protected MultiDataObject createMultiObject(FileObject primaryFile)
	throws DataObjectExistsException, IOException {
	return new HDataObject(primaryFile, this);
    }
  
    protected MultiDataObject.Entry createPrimaryEntry(MultiDataObject obj,
			    FileObject primaryFile) {
	return new CndAbstractDataLoader.CndFormat(obj, primaryFile);
    }
}

