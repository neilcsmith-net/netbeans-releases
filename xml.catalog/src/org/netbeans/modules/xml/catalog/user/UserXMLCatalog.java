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

package org.netbeans.modules.xml.catalog.user;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import org.netbeans.modules.xml.catalog.spi.CatalogDescriptor;
import org.netbeans.modules.xml.catalog.spi.CatalogListener;
import org.netbeans.modules.xml.catalog.spi.CatalogReader;
import org.netbeans.modules.xml.catalog.spi.CatalogWriter;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.filesystems.*;
import org.xml.sax.*;
import java.util.*;
import java.io.*;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;
import org.xml.sax.ext.LexicalHandler;

/**
 * Supplies a catalog which lets user register DTD and XML schema in a very simple way.
 * 
 * Schema NS URIs should be represented as <code>&lt;uri></code> elements  in the catalog,
 * as suggested in <a href="http://xerces.apache.org/xerces2-j/faq-xcatalogs.html">Xerces FAQ</a>.
 * Therefore a SCHEMA: prefix (already used by NB catalogs in their PublicID maps), will be written
 * as <code>uri</code> element, with a special comment to distinguish it from custom URIs, which
 * use URI: prefix in their registration. 
 * 
 * <p/>
 * Not sure though whether URI: is used at all. The whole registration API is ugly as it interprets
 * contents of URI instead of using parameters...
 * 
 * @author Milan Kuchtiak
 */
public class UserXMLCatalog implements CatalogReader, CatalogWriter, CatalogDescriptor, EntityResolver {
    private static final String PROPERTY_LEX_HANDLER = "http://xml.org/sax/properties/lexical-handler";
    private Map publicIds;
    private List catalogListeners;
    private static final String catalogResource = "xml/catalogs/UserXMLCatalog.xml"; // NOI18N
    private static final String URI_PREFIX = "URI:"; // NOI18N
    private static final String PUBLIC_PREFIX = "PUBLIC:"; // NOI18N
    private static final String SYSTEM_PREFIX = "SYSTEM:"; // NOI18N
    private static final String SCHEMA_PREFIX = "SCHEMA:"; // NOI18N
    
    private static final int TYPE_PUBLIC=0;
    private static final int TYPE_SYSTEM=1;
    private static final int TYPE_URI=2;
    private static final int TYPE_SCHEMA = 3;
    
    private static final String NB_SCHEMA_MARKER = "NetBeans XML schema marker, do not remove"; // NOI18N
    private static final String NB_SCHEMA_MARKER_COMMENT = "<!-- " + NB_SCHEMA_MARKER + " -->"; // NOI18N
    
    /** Default constructor for use from layer. */
    public UserXMLCatalog() {
        catalogListeners=new ArrayList();
    }

    public String resolveURI(String name) {
        // first try to resolve the URI as a schema namespace:
        String res;
        res = (String)getPublicIdMap().get(SCHEMA_PREFIX + name);
        if (res != null) {
            return res;
        }
        res = (String)getPublicIdMap().get(URI_PREFIX+name);
        if (res != null) {
            return res;
        }
        return null;
    }

    public String resolvePublic(String publicId) {
        return (String)getPublicIdMap().get(PUBLIC_PREFIX+publicId);
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, java.io.IOException {
        getPublicIdMap();
        String url = null;
        if (publicId!=null) {
            url = (String)getPublicIdMap().get(PUBLIC_PREFIX+publicId);
            if (url == null) url = (String)getPublicIdMap().get(URI_PREFIX+publicId);
        } else if (systemId!=null) {
            // favor schemas a little:
            url = (String)getPublicIdMap().get(SCHEMA_PREFIX + systemId);
            if (url == null) {
                url = (String)getPublicIdMap().get(SYSTEM_PREFIX+systemId);
            }
        }
        if (url!=null) return new InputSource(url);
        else return null;
    }
    
    public String getSystemID(String publicId) {
        return (String)getPublicIdMap().get(publicId);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {}

    public void addPropertyChangeListener(PropertyChangeListener l) {}

    public void removeCatalogListener(CatalogListener l) {
        catalogListeners.remove(l);
    }

    public void addCatalogListener(CatalogListener l) {
        catalogListeners.add(l);
    }
    
    protected void fireEntryAdded(String publicId) {
        Iterator it = catalogListeners.iterator();
        while (it.hasNext()) {
            CatalogListener listener = (CatalogListener)it.next();
            listener.notifyNew(publicId);
        }
    }
    
    protected void fireEntryRemoved(String publicId) {
        Iterator it = catalogListeners.iterator();
        while (it.hasNext()) {
            CatalogListener listener = (CatalogListener)it.next();
            listener.notifyRemoved(publicId);
        }
    }
    
    protected void fireEntryUpdated(String publicId) {
        Iterator it = catalogListeners.iterator();
        while (it.hasNext()) {
            CatalogListener listener = (CatalogListener)it.next();
            listener.notifyUpdate(publicId);
        }
    }

    public Image getIcon(int type) {
        return ImageUtilities.loadImage("org/netbeans/modules/xml/catalog/impl/xmlCatalog.gif", true); //NOI18N
    }

    public void refresh() {
        Iterator it = catalogListeners.iterator();
        while (it.hasNext()) {
            CatalogListener listener = (CatalogListener)it.next();
            listener.notifyInvalidate();
        }
        FileObject userCatalog = FileUtil.getConfigFile(catalogResource);
        userCatalog.refresh();
        synchronized (this) {
            publicIds=null;
        }
    }

    public String getShortDescription() {
        return NbBundle.getMessage(UserXMLCatalog.class, "HINT_userCatalog");
    }

    public Iterator getPublicIDs() {
        return getPublicIdMap().keySet().iterator();
    }

    public String getDisplayName() {
        return NbBundle.getMessage(UserXMLCatalog.class, "LBL_userCatalog");
    }
    
    private synchronized Map getPublicIdMap() {
        if (publicIds==null) {
            try {
                FileObject userCatalog = FileUtil.getConfigFile(catalogResource);
                publicIds = parse(userCatalog);
            } catch (java.io.IOException ex) {
                publicIds = new HashMap();
                org.openide.ErrorManager.getDefault().notify(ex);
            } catch (SAXException ex) {
                publicIds = new HashMap();
                org.openide.ErrorManager.getDefault().notify(ex);
            }
        } 
        return publicIds;
    }
    
    private void addEntry (int entryType, String key, String value) throws IOException {
        FileObject userCatalog = FileUtil.getConfigFile(catalogResource);
        String tempBuffer = createCatalogBuffer(userCatalog);
        BufferedReader reader = new BufferedReader(new StringReader(tempBuffer));
        FileLock lock = userCatalog.lock();
        try {
            PrintWriter writer = new PrintWriter(userCatalog.getOutputStream(lock));
            try {
                String line;
                while ((line=reader.readLine())!=null) {
                    if (line.indexOf("</catalog>")>=0) { //NOI18N
                        switch (entryType) {
                            case TYPE_PUBLIC : {
                                writer.println("  <public publicId=\""+key+"\" uri=\""+value+"\"/>"); //NOI18N
                                getPublicIdMap().put(PUBLIC_PREFIX+key, value);
                                fireEntryAdded(PUBLIC_PREFIX+key);
                                break;
                            }
                            case TYPE_SYSTEM : {
                                writer.println("  <system systemId=\""+key+"\" uri=\""+value+"\"/>"); //NOI18N
                                getPublicIdMap().put(SYSTEM_PREFIX+key, value);
                                fireEntryAdded(SYSTEM_PREFIX+key);
                                break;
                            }
                            case TYPE_URI : {
                                writer.println("  <uri name=\""+key+"\" uri=\""+value+"\"/>"); //NOI18N
                                getPublicIdMap().put(URI_PREFIX+key, value);
                                fireEntryAdded(URI_PREFIX+key);
                                break;
                            }
                            case TYPE_SCHEMA: {
                                writer.println("   " + NB_SCHEMA_MARKER_COMMENT + " <uri name=\""+key+"\" uri=\""+value+"\"/> "); //NOI18N
                                getPublicIdMap().put(SCHEMA_PREFIX+key, value);
                                fireEntryAdded(SCHEMA_PREFIX+key);
                                break;
                            }
                        }
                    }
                    writer.println(line);
                }
            } finally {
                writer.close();
            }
        } finally {
            lock.releaseLock();
        }
    }
    
    private void removeEntry (int entryType, String key) throws IOException {
        FileObject userCatalog = FileUtil.getConfigFile(catalogResource);
        String tempBuffer = createCatalogBuffer(userCatalog);
        BufferedReader reader = new BufferedReader(new StringReader(tempBuffer));
        FileLock lock = userCatalog.lock();
        String prefix;
        String searchText;
        
        switch (entryType) {
            case TYPE_PUBLIC: 
                prefix = PUBLIC_PREFIX; 
                searchText = "<public publicId"; // NOI18N
                break;
            case TYPE_SYSTEM: 
                prefix = SYSTEM_PREFIX; 
                searchText = "<system systemId"; // NOI18N
                break;
            case TYPE_URI: 
                prefix = URI_PREFIX; 
                searchText = "<uri name"; // NOI18N
                break;
            case TYPE_SCHEMA: 
                prefix = SCHEMA_PREFIX; 
                searchText = "<uri name"; // NOI18N
                break;
            default:
                prefix = "";
                searchText = "<public publicId"; // NOI18N
        }
        searchText += "=\""+key+"\"";
        try {
            PrintWriter writer = new PrintWriter(userCatalog.getOutputStream(lock));
            try {
                String line;
                while ((line=reader.readLine())!=null) {
                    if (line.indexOf(searchText)>0) { //NOI18N
                        getPublicIdMap().remove(prefix+key);
                        fireEntryRemoved(prefix+key);
                    } else {
                        writer.println(line);
                    }
                }
            } finally {
                writer.close();
            }
        } finally {
            lock.releaseLock();
        }
    }
    
    private void updateEntry (int entryType, String key, String value) throws IOException {
        FileObject userCatalog = FileUtil.getConfigFile(catalogResource);
        String tempBuffer = createCatalogBuffer(userCatalog);
        BufferedReader reader = new BufferedReader(new StringReader(tempBuffer));
        FileLock lock = userCatalog.lock();
        try {
            PrintWriter writer = new PrintWriter(userCatalog.getOutputStream(lock));
            try {
                String line;
                while ((line=reader.readLine())!=null) {
                    switch (entryType) {
                        case TYPE_PUBLIC : {
                            if (line.indexOf("<public publicId=\""+key+"\"")>0) { //NOI18N
                                writer.println("  <public publicId=\""+key+"\" uri=\""+value+"\"/>"); //NOI18N
                                getPublicIdMap().put(PUBLIC_PREFIX+key, value);
                                fireEntryUpdated(PUBLIC_PREFIX+key);
                            } else {
                                writer.println(line);
                            }
                            break;
                        }
                        case TYPE_SYSTEM : {
                            if (line.indexOf("<system systemId=\""+key+"\"")>0) { //NOI18N
                                writer.println("  <system systemId=\""+key+"\" uri=\""+value+"\"/>"); //NOI18N
                                getPublicIdMap().put(SYSTEM_PREFIX+key,value);
                                fireEntryUpdated(SYSTEM_PREFIX+key);
                            } else {
                                writer.println(line);
                            }
                            break;
                        }
                        case TYPE_URI : {
                            if (line.indexOf("<uri name=\""+key+"\"")>0) { //NOI18N
                                writer.println("  <uri name=\""+key+"\" uri=\""+value+"\"/>"); //NOI18N
                                getPublicIdMap().put(URI_PREFIX+key, value);
                                fireEntryUpdated(URI_PREFIX+key);
                            } else {
                                writer.println(line);
                            }
                            break;
                        } 
                        case TYPE_SCHEMA: {
                            if (line.indexOf("<uri name=\""+key+"\"")>0) { //NOI18N
                                writer.println("   " + NB_SCHEMA_MARKER_COMMENT + " <uri name=\""+key+"\" uri=\""+value+"\"/>"); //NOI18N
                                getPublicIdMap().put(SCHEMA_PREFIX+key, value);
                                fireEntryUpdated(SCHEMA_PREFIX+key);
                            } else {
                                writer.println(line);
                            }
                            break;
                        }
                        default : writer.println(line);
                    }
                    
                }
            } finally {
                writer.close();
            }
        } finally {
            lock.releaseLock();
        }
    }
    
    private String createCatalogBuffer(FileObject fo) throws IOException {
        BufferedInputStream is = new BufferedInputStream(fo.getInputStream());
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        int b;
        byte[] buf = new byte[512];
        while ((b=is.read(buf, 0, 512)) !=-1) {
            temp.write(buf, 0, b);
        }
        is.close();
        temp.close();
        return temp.toString("UTF-8");//NOI18N
    }
    
    private Map parse(FileObject userCatalog) 
        throws SAXException, java.io.IOException {
        javax.xml.parsers.SAXParserFactory fact = javax.xml.parsers.SAXParserFactory.newInstance();
        fact.setValidating(false);
        try {
            javax.xml.parsers.SAXParser parser = fact.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setEntityResolver(new OasisCatalogResolver());
            CatalogHandler handler = new CatalogHandler();
            reader.setContentHandler(handler);
            reader.setProperty(PROPERTY_LEX_HANDLER, handler);
            reader.parse(new InputSource(userCatalog.getInputStream()));
            return handler.getValues();
        } catch(javax.xml.parsers.ParserConfigurationException ex) {
            org.openide.ErrorManager.getDefault().notify(ex);
            return new java.util.HashMap();
        }
    }
    /** Registers new entry (key:value) in catalog
     * if (value==null) removes the entry from catalog
     */
    public void registerCatalogEntry(String key, String value) {
        getPublicIdMap(); // to ensure that publicIds were created
        try {
            if (key.startsWith(PUBLIC_PREFIX)) {
                if (value!=null) {
                    if (getPublicIdMap().get(key)!=null) {
                        if (requestUpdate(key.substring(PUBLIC_PREFIX.length())))
                            updateEntry(TYPE_PUBLIC, key.substring(PUBLIC_PREFIX.length()), value);
                    } else
                        addEntry(TYPE_PUBLIC, key.substring(PUBLIC_PREFIX.length()), value);
                } else
                      removeEntry(TYPE_PUBLIC, key.substring(PUBLIC_PREFIX.length()));
            } else if (key.startsWith(SYSTEM_PREFIX)) {
                if (value!=null) {
                    if (getPublicIdMap().get(key)!=null) {
                        if (requestUpdate(key.substring(SYSTEM_PREFIX.length())))
                            updateEntry(TYPE_SYSTEM, key.substring(SYSTEM_PREFIX.length()), value);
                    } else
                        addEntry(TYPE_SYSTEM, key.substring(SYSTEM_PREFIX.length()), value);
                } else
                      removeEntry(TYPE_SYSTEM, key.substring(SYSTEM_PREFIX.length()));
            } else if (key.startsWith(URI_PREFIX)) {
                if (value!=null) {
                    if (getPublicIdMap().get(key)!=null) {
                        if (requestUpdate(key.substring(URI_PREFIX.length()))) updateEntry(TYPE_URI, key.substring(URI_PREFIX.length()), value);
                    } else
                        addEntry(TYPE_URI, key.substring(URI_PREFIX.length()), value);
                } else
                      removeEntry(TYPE_URI, key.substring(URI_PREFIX.length()));
            } else if (key.startsWith(SCHEMA_PREFIX)) {
                if (value!=null) {
                    if (getPublicIdMap().get(key)!=null) {
                        if (requestUpdate(key.substring(SCHEMA_PREFIX.length()))) updateEntry(TYPE_SCHEMA, key.substring(SCHEMA_PREFIX.length()), value);
                    } else
                        addEntry(TYPE_SCHEMA, key.substring(SCHEMA_PREFIX.length()), value);
                } else
                      removeEntry(TYPE_SCHEMA, key.substring(SCHEMA_PREFIX.length()));
            }
        } catch (IOException ex) {
            org.openide.ErrorManager.getDefault().notify(ex);
        }
    }
    
    private boolean requestUpdate(String id) {
        NotifyDescriptor desc = new NotifyDescriptor.Confirmation(
                NbBundle.getMessage(UserXMLCatalog.class,"TXT_updateEntry",id),NotifyDescriptor.YES_NO_OPTION);
        DialogDisplayer.getDefault().notify(desc);
        return (NotifyDescriptor.YES_OPTION==desc.getValue());
    }
    
    private static class CatalogHandler extends org.xml.sax.helpers.DefaultHandler implements LexicalHandler {
        private Map values;
        //private boolean insideEl, insideTag;
        private boolean schemaFound;

        CatalogHandler() {
            values = new HashMap();
        }
        public void startElement(String uri, String localName, String rawName, Attributes atts) throws SAXException {
            if ("public".equals(rawName)) { //NOI18N
                String val = atts.getValue("publicId"); //NOI18N
                if (val!=null) values.put(PUBLIC_PREFIX+val, atts.getValue("uri")); //NOI18N
                schemaFound = false;
            } else if ("system".equals(rawName)) { //NOI18N
                String val = atts.getValue("systemId"); //NOI18N
                if (val!=null) values.put(SYSTEM_PREFIX+val, atts.getValue("uri")); //NOI18N
                schemaFound = false;
            } else if ("uri".equals(rawName)) { //NOI18N
                String val = atts.getValue("name"); //NOI18N
                if (val!=null) {
                    addUri(val, atts.getValue("uri")); // NOI18N
                }
                schemaFound = false;
            }
        }
        
        private void addUri(String lastUri, String lastValue) {
            if (schemaFound) {
                values.put(SCHEMA_PREFIX + lastUri, lastValue);
            } else {
                values.put(URI_PREFIX + lastUri, lastValue);
            }
            lastUri = null;
        }

        @Override
        public void comment(char[] ch, int start, int length) throws SAXException {
            schemaFound = String.copyValueOf(ch, start, length).contains(NB_SCHEMA_MARKER);
        }

        @Override
        public void endCDATA() throws SAXException {}

        @Override
        public void endDTD() throws SAXException {}

        @Override
        public void endEntity(String name) throws SAXException {}

        @Override
        public void startCDATA() throws SAXException {}

        @Override
        public void startDTD(String name, String publicId, String systemId) throws SAXException {}

        @Override
        public void startEntity(String name) throws SAXException {}
        
        public Map getValues() {
            return values;
        }
    }
    
    private class OasisCatalogResolver implements EntityResolver {
        public InputSource resolveEntity (String publicId, String systemId) {
            if ("-//OASIS//DTD Entity Resolution XML Catalog V1.0//EN".equals(publicId)) { //NOI18N
                java.net.URL url = org.apache.xml.resolver.Catalog.class.getResource("etc/catalog.dtd"); //NOI18N
                return new InputSource(url.toExternalForm());
            }
            return null;
        }
    }
}
