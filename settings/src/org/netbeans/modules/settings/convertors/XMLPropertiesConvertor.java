/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.settings.convertors;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Properties;

import org.xml.sax.SAXException;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;

import org.netbeans.spi.settings.Convertor;
import org.netbeans.spi.settings.Saver;

import org.netbeans.modules.settings.Env;

/** Implementation of xml properties format described by
 * /org/netbeans/modules/settings/resources/properties.dtd
 *
 * @author  Jan Pokorsky
 */
public final class XMLPropertiesConvertor extends Convertor implements PropertyChangeListener {
    /** file attribute containnig value whether the setting object will be
     * stored automaticaly (preventStoring==false) or SaveCookie will be provided.
     * Default value is <code>preventStoring==false</code>. Usage
     * <code>&lt;attr name="xmlproperties.preventStoring" boolvalue="[true|false]"/>
     * </code>
     */
    public final static String EA_PREVENT_STORING = "xmlproperties.preventStoring"; //NOI18N
    /** file attribute containnig list of property names their changes will be ignored. Usage
     * <code>&lt;attr name="xmlproperties.ignoreChanges" stringvalue="name[, ...]"/>
     * </code>
     */
    public final static String EA_IGNORE_CHANGES = "xmlproperties.ignoreChanges"; //NOI18N
    private FileObject providerFO;
    /** cached property names to be filtered */
    private java.util.Set ignoreProperites;
    
    /** create convertor instance; should be used in module layers
     * @param providerFO provider file object
     */
    public static Convertor create(org.openide.filesystems.FileObject providerFO) {
        return new XMLPropertiesConvertor(providerFO);
    }
    
    public XMLPropertiesConvertor(org.openide.filesystems.FileObject fo) {
        this.providerFO = fo;
    }
    
    public Object read(java.io.Reader r) throws IOException, ClassNotFoundException {
        Object def = defaultInstanceCreate();
        readSetting(r, def);
        return def;
    }
    
    public void write(java.io.Writer w, Object inst) throws IOException {
        w.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"); // NOI18N
        w.write("<!DOCTYPE properties PUBLIC \""); // NOI18N
        
        FileObject foEntity = Env.findEntityRegistration(providerFO);
        if (foEntity == null) foEntity = providerFO;
        Object publicId = foEntity.getAttribute(Env.EA_PUBLICID);
        if (publicId == null || !(publicId instanceof String)) {
            throw new IOException("missing or invalid attribute: " + //NOI18N
                Env.EA_PUBLICID + ", provider: " + foEntity); //NOI18N
        }
        
        w.write((String) publicId);
        w.write("\" \"http://www.netbeans.org/dtds/properties-1_0.dtd\">\n"); // NOI18N
        w.write("<properties>\n"); // NOI18N
        Properties p = getProperties(inst);
        if (p != null && !p.isEmpty()) writeProperties(w, p);
        w.write("</properties>\n"); // NOI18N
    }
    
    /** an object listening on the setting changes */
    private Saver saver;
    public void registerSaver(Object inst, Saver s) {
        if (saver != null) {
            ErrorManager.getDefault().log(ErrorManager.EXCEPTION, "[Warning] Saver already registered");
            return;
        }
        
        // add propertyChangeListener
        try {
            java.lang.reflect.Method method = inst.getClass().getMethod(
                "addPropertyChangeListener", // NOI18N
                new Class[] {PropertyChangeListener.class});
            method.invoke(inst, new Object[] {this});
            this.saver = s;
//System.out.println("XMLPropertiesConvertor.registerPropertyListener...ok " + inst);
        } catch (NoSuchMethodException ex) {
            ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL,
            "ObjectChangesNotifier: NoSuchMethodException: " + // NOI18N
            inst.getClass().getName() + ".addPropertyChangeListener"); // NOI18N
        } catch (IllegalAccessException ex) {
            ErrorManager.getDefault().notify(ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    public void unregisterSaver(Object inst, Saver s) {
        if (saver == null) return;
        if (saver != s) {
            ErrorManager.getDefault().log(ErrorManager.EXCEPTION, "[Warning] trying unregistered unknown Saver");
            return;
        }
        try {
            java.lang.reflect.Method method = inst.getClass().getMethod(
                "removePropertyChangeListener", // NOI18N
                new Class[] {PropertyChangeListener.class});
            method.invoke(inst, new Object[] {this});
            this.saver = null;
//System.out.println("XMLPropertiesConvertor.unregisterPropertyListener...ok " + inst);
        } catch (NoSuchMethodException ex) {
            ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL,
            "ObjectChangesNotifier: NoSuchMethodException: " + // NOI18N
            inst.getClass().getName() + ".removePropertyChangeListener"); // NOI18N
            // just changes done through gui will be saved
        } catch (IllegalAccessException ex) {
            ErrorManager.getDefault().notify(ex);
            // just changes done through gui will be saved
        } catch (java.lang.reflect.InvocationTargetException ex) {
            ErrorManager.getDefault().notify(ex);
            // just changes done through gui will be saved
        }
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if (saver == null || ignoreChange(evt)) return;
        if (acceptSave()) {
            try {
                saver.requestSave();
            } catch (IOException ex) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
            }
        } else {
            saver.markDirty();
        }
    }
    
    
    ////////////////////////////////////////////////////////////
    // Private implementation
    ////////////////////////////////////////////////////////////
    
    /** filtering of Property Change Events */
    private boolean ignoreChange(java.beans.PropertyChangeEvent pce) {
        if (pce == null || pce.getPropertyName() == null) return true;
        
        if (ignoreProperites == null) {
            ignoreProperites = Env.parseAttribute(
                providerFO.getAttribute(EA_IGNORE_CHANGES));
        }
        if (ignoreProperites.contains(pce.getPropertyName())) return true;
        
        return ignoreProperites.contains("all"); // NOI18N
    }
    
    private boolean acceptSave() {
        Object storing = providerFO.getAttribute(EA_PREVENT_STORING);
        if (storing == null) return true;
        if (storing instanceof Boolean)
            return !((Boolean) storing).booleanValue();
        if (storing instanceof String)
            return !Boolean.valueOf((String) storing).booleanValue();
        return true;
    }
    
    private final static String INDENT = "    "; // NOI18N
    private String instanceClass = null;
    

    private Object defaultInstanceCreate() throws IOException, ClassNotFoundException {
        Object instanceCreate = providerFO.getAttribute(Env.EA_INSTANCE_CREATE);
        if (instanceCreate != null) return instanceCreate;
        
        Class c = getInstanceClass();
        try {
            return c.newInstance();
        } catch (Exception ex) { // IllegalAccessException, InstantiationException
            throw (IOException) ErrorManager.getDefault().annotate(
                new IOException("Cannot create instance of " + c.getName()), //NOI18N
                ex);
        }
    }

    private Class getInstanceClass() throws IOException, ClassNotFoundException {
        if (instanceClass == null) {
            Object name = providerFO.getAttribute(Env.EA_INSTANCE_CLASS_NAME);
            if (name == null || !(name instanceof String)) {
                throw new IllegalStateException(
                    "missing or invalid ea attribute: " +
                    Env.EA_INSTANCE_CLASS_NAME); //NOI18N
            }
            instanceClass = (String) name;
        }
        return org.openide.TopManager.getDefault().systemClassLoader().loadClass(instanceClass);
    }
    
    private void readSetting(java.io.Reader input, Object inst) throws IOException {
        try {
            java.lang.reflect.Method m = inst.getClass().getDeclaredMethod(
                "readProperties", new Class[] {Properties.class}); // NOI18N
            m.setAccessible(true);
            XMLPropertiesConvertor.Reader r = new XMLPropertiesConvertor.Reader();
            r.parse(input);
            m.setAccessible(true);
            m.invoke(inst, new Object[] {r.getProperties()});
        } catch (NoSuchMethodException ex) {
            throw (IOException) ErrorManager.getDefault().annotate(
                new IOException(ex.getLocalizedMessage()), ex);
        } catch (IllegalAccessException ex) {
            throw (IOException) ErrorManager.getDefault().annotate(
                new IOException(ex.getLocalizedMessage()), ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            throw (IOException) ErrorManager.getDefault().annotate(
                new IOException(t.getLocalizedMessage()), t);
        }
    }
    
    private static void writeProperties(java.io.Writer w, Properties p) throws IOException {
        java.util.Iterator it = p.keySet().iterator();
        String key;
        while (it.hasNext()) {
            key = (String) it.next();
            w.write(INDENT);
            w.write("<property name=\""); // NOI18N
            w.write(key);
            w.write("\" value=\""); // NOI18N
            w.write(p.getProperty(key));
            w.write("\"/>\n"); // NOI18N
        }
    }

    private static Properties getProperties (Object inst) throws IOException {
        try {
            java.lang.reflect.Method m = inst.getClass().getDeclaredMethod(
                "writeProperties", new Class[] {Properties.class}); // NOI18N
            m.setAccessible(true);
            Properties prop = new Properties();
            m.invoke(inst, new Object[] {prop});
            return prop;
        } catch (NoSuchMethodException ex) {
            throw (IOException) ErrorManager.getDefault().annotate(
                new IOException(ex.getLocalizedMessage()), ex);
        } catch (IllegalAccessException ex) {
            throw (IOException) ErrorManager.getDefault().annotate(
                new IOException(ex.getLocalizedMessage()), ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            throw (IOException) ErrorManager.getDefault().annotate(
                new IOException(t.getLocalizedMessage()), t);
        }
    }
    
    /** support for reading xml/properties format */
    private static class Reader extends org.xml.sax.helpers.DefaultHandler implements org.xml.sax.ext.LexicalHandler {

        private static final String ELM_PROPERTY = "property"; // NOI18N
        private static final String ATR_PROPERTY_NAME = "name"; // NOI18N
        private static final String ATR_PROPERTY_VALUE = "value"; // NOI18N

        private Properties props = new Properties();
        private String publicId;

        public org.xml.sax.InputSource resolveEntity(String publicId, String systemId)
        throws SAXException {
            if (this.publicId != null && this.publicId.equals (publicId)) {
                return new org.xml.sax.InputSource (new java.io.ByteArrayInputStream (new byte[0]));
            } else {
                return null; // i.e. follow advice of systemID
            }
        }

        public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attribs) throws SAXException {
            if (ELM_PROPERTY.equals(qName)) {
                String propertyName = attribs.getValue(ATR_PROPERTY_NAME);
                String propertyValue = attribs.getValue(ATR_PROPERTY_VALUE);
                props.setProperty(propertyName, propertyValue);
            }
        }

        public void parse(java.io.Reader src) throws IOException {
            try {
                org.xml.sax.XMLReader reader = org.openide.xml.XMLUtil.createXMLReader(false, false);
                reader.setContentHandler(this);
                reader.setEntityResolver(this);
                org.xml.sax.InputSource is =
                    new org.xml.sax.InputSource(src);
                try {
                    reader.setProperty("http://xml.org/sax/properties/lexical-handler", this);  //NOI18N
                } catch (SAXException sex) {
                    ErrorManager.getDefault().log(ErrorManager.EXCEPTION,
                    "Warning: XML parser does not support lexical-handler feature.");  //NOI18N
                }
                reader.parse(is);
            } catch (SAXException ex) {
                IOException ioe = new IOException();
                ErrorManager emgr = ErrorManager.getDefault();
                emgr.annotate(ioe, ex);
                if (ex.getException () != null) {
                    emgr.annotate (ioe, ex.getException());
                }
                throw ioe;
            }
        }
        
        public Properties getProperties() {
            return props;
        }
        
        public String getPublicID() {
            return publicId;
        }

        // LexicalHandler implementation
        public void startDTD(String name, String publicId, String systemId) throws SAXException {
            this.publicId = publicId;
        }
        
        public void endDTD() throws SAXException {}
        public void startEntity(String str) throws SAXException {}
        public void endEntity(String str) throws SAXException {}
        public void comment(char[] values, int param, int param2) throws SAXException {}
        public void startCDATA() throws SAXException {}
        public void endCDATA() throws SAXException {}
    }
}
