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
 * XMLBean.java
 *
 * Created on October 31, 2001, 2:47 PM
 */

package org.netbeans.xtest.pe.xmlbeans;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.util.*;

import java.lang.reflect.*;

/**
 * name of the class = name of the XML element
 * currently - all beans must exist in the same package as XMLBean :-(
 * attributes = public variables beginning with xml keyword
 *
 * @author  mb115822
 */
public abstract class XMLBean {

    
    public final static String XMLBEAN_PACKAGE = "org.netbeans.xtest.pe.xmlbeans";
    public final static String XMLBEAN_ATT_PREFIX = "xmlat_";
    public final static String XMLBEAN_ELEM_PREFIX = "xmlel_";
    
    // debugging flag - should be set to false :-)
    private static final boolean DEBUG = false;
    private static final void debugInfo(String message) {
        if (DEBUG) System.out.println("XMLBean."+message);
    }
    
    
    // pcdata of the XML element
    public String xml_pcdata = null;
    // cdata of the XML element
    public String xml_cdata = null;
    
    // utility methods
    public static Object[] addToArray(Object[] array, Object obj) throws IllegalArgumentException {
        debugInfo("addToArray(): array = "+array+" object to add = "+obj);
        if (array == null) {
            if (obj == null) {
                debugInfo("addToArray(): both array and obj are null - there is nothing to do - return null");
                return null;
            } else {            
                debugInfo("addToArray(): array is null, but we can construct a new array with just one object");                
                Object resultingArray = Array.newInstance(obj.getClass(),1);
                Array.set(resultingArray,0,obj);
                return (Object[])resultingArray;
            }      
        }
        debugInfo("addToArray(): adding to existing array");
        Class arrayType = array.getClass().getComponentType();
        int length = array.length;
        debugInfo("addToArray(): discovered array componennt type = "+arrayType+" and length = "+length);
        Object resultingArray = Array.newInstance(arrayType,length+1);
        debugInfo("addToArray(): copying old array to new one ");
        for (int i=0; i<length; i++) {
            Array.set(resultingArray,i,array[i]);
        }
        debugInfo("addToArray(): adding the new object at the end (position = "+length+")");
        Array.set(resultingArray,length,obj);
        return (Object[])resultingArray;
    }
    
    
    public boolean isObjectValid(Object obj) {
        if (obj != null) {
            if (obj.getClass().isInstance(this)) {
                return true;                
            }
        }
        return false;
    }
    
    
    
    public static boolean compareObjectsByFields(Object obj1, Object obj2, String[] fieldNames) 
                throws NoSuchFieldException {
        debugInfo("compareObjectsByFields(): start");
        if (fieldNames != null) {
            if (fieldNames.length>0) {
                // now compare objects                
                try {
                    if (obj1.getClass().isInstance(obj2)) {
                        debugInfo("compareObjectsByFields(): objects are of the same type");
                        for (int i=0;i<fieldNames.length;i++) {                            
                            Field field = obj1.getClass().getField(fieldNames[i]);
                            debugInfo("compareObjectsByFields(): comparing field"+field.getName());
                            Object value1 = field.get(obj1);
                            Object value2 = field.get(obj2);
                            if ((value1==null)|(value2==null)) {
                                debugInfo("compareObjectsByFields(): comparing for null");
                                if ((value1==null)&(value2==null)) {
                                    debugInfo("compareObjectsByFields(): both values are null");
                                } else {
                                     debugInfo("compareObjectsByFields(): comparing for null, but 2nd value is not null");
                                     return false;
                                } 
                            } else {
                                if (!value1.equals(value2)) {
                                    debugInfo("compareObjectsByFields(): values differ, fieldName = "+fieldNames[i]);
                                    debugInfo("compareObjectsByFields(): value1="+value1+"; value2="+value2);
                                    return false;
                                }
                            }
                        }
                        debugInfo("compareObjectsByFields(): all fields are equal - true");
                        return true;
                    } else {
                        debugInfo("compareObjectsByFields(): objects are not of the same type - false");
                        return false;
                    }
                } catch (NullPointerException npe) {                    
                    // there was some problem -> objects are not equal
                    debugInfo("compareObjectsByFields(): there was NPE, hence objects are not equal");
                    return false;
                } catch (IllegalAccessException iae) {
                    debugInfo("compareObjectsByFields(): there was IllegalAccessException, throwing NoSuchFieldException");
                    throw new NoSuchFieldException(" thrown IllegalAccessException: "+iae);
                }
            }
        }
        debugInfo("compareObjectsByFields(): throwing NoSuchAFieldException - fieldNames string is empty !!!");
        throw new NoSuchFieldException(" No fields were specified");        
    }
    
    
    public boolean equalByAttributes(Object obj) {
        debugInfo("equalByAttribues(): comparing XMLBean to another XMLBean by all its XML attributes");
        try {
            ArrayList attributeFieldList = new ArrayList();
            Field[] fields = this.getClass().getFields();
            for (int i = 0; i<fields.length; i++) {
                Field field = fields[i];
                if (field.getName().startsWith(XMLBean.XMLBEAN_ATT_PREFIX)) {
                    attributeFieldList.add(field.getName());
                }
            }
            String[] fieldNames = (String[])(attributeFieldList.toArray(new String[0]));
            debugInfo("equalByAttribues(): got field names -> comparing"); 
            return compareObjectsByFields(this,obj,fieldNames);
            // get all fields
        } catch (NoSuchFieldException nsfe) {
            // this should not happen            
            debugInfo("equalByAttribues(): NoSuchFieldException - weird: "+nsfe);
            return false;
        }
    }
    
    public static XMLBean findXMLBean(XMLBean[] existingBeans, XMLBean newBean) {
        debugInfo("findXMLBean(): existingBeans = "+existingBeans+", newBean = "+newBean);
        if (newBean == null) {
            debugInfo("findXMLBean(): newBean is null - cannot find null :-)");
            return null;
        }
        if (existingBeans == null) {
            debugInfo("findXMLBean(): existingBeans is null - what shoud I compare :-)");
            return null;
        }
        debugInfo("findXMLBean(): lets look for a bean ");
        for (int i=0; i<existingBeans.length ; i++) {
             if (newBean.equals(existingBeans[i])) {
                debugInfo("addOrFindEqualBean():, found equal XMLBean");
                return existingBeans[i];
            }
        }
        return null;
    }
       
    
    public static String cutPackage(String className) {
        if (DEBUG) System.out.println("XMLBean.cutPackage: className = "+className);
        int lastDot = className.lastIndexOf('.');
        if (lastDot != -1 ) {
            return className.substring(lastDot+1);
        } else {
            return className;
        }
    }
    
    public static String cutPrefix(String aString, String prefix) {
        if (prefix != null) {
            if (!prefix.equals("")) {                
                if (aString.startsWith(prefix)) {
                    return aString.substring(prefix.length());
                }
            }
        }
        return aString;
    }
    
     private static DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch(Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    
    
    // 
    
    public static void setField(Field field,Object obj, String value) 
                            throws IllegalArgumentException, IllegalAccessException {
        String fieldTypeName = field.getType().getName();
        if (fieldTypeName.equals("java.lang.String")) {
            field.set(obj,value);
            return;
        }
        if (fieldTypeName.equals("int")) {
            field.setInt(obj,Integer.parseInt(value));
            return;
        }
        
        if (fieldTypeName.equals("long")) {
            field.setLong(obj,Long.parseLong(value));
            return;
        }
        
        if (fieldTypeName.equals("float")) {
            field.setFloat(obj,Float.parseFloat(value));
            return;
        }
        
        if (fieldTypeName.equals("double")) {
            field.setDouble(obj,Double.parseDouble(value));
            return;
        }
        
        if (fieldTypeName.equals("boolean")) {
            field.setBoolean(obj,Boolean.getBoolean(value));
            return;
        }   
        
        if (fieldTypeName.equals("java.sql.Date")) {
            field.set(obj,java.sql.Date.valueOf(value));
            return;
        }
        
        if (fieldTypeName.equals("java.sql.Timestamp")) {
            field.set(obj,java.sql.Timestamp.valueOf(value));
            return;
        }
        
        throw new IllegalArgumentException("field type '"+fieldTypeName+"' not supported");
        
    }
    
    
    protected void fillAttributes(NamedNodeMap atts)
                      throws NoSuchFieldException {
        for (int i=0; i<atts.getLength(); i++) {
            Node attribute = atts.item(i);            
            String attributeName = attribute.getNodeName();
            // do we have such a attribute in XMLBean ?
            Field attField = this.getClass().getField(XMLBean.XMLBEAN_ATT_PREFIX+attributeName);
            if (attField != null) {
                String value = attribute.getNodeValue();
                if (DEBUG) System.out.println("XMLBean.fillAttributes(): setting field:"+attField.getName()+" with value = "+value);
                try {
                    XMLBean.setField(attField,this,value);
                } catch (Exception e) {
                    // will this work ???
                    NoSuchFieldException nsfe = new NoSuchFieldException("Cannot set field in XMLBean");
                    nsfe.fillInStackTrace();
                    throw nsfe;
                }
            }                        
        }        
    }
    
    
    protected void fillElements(NodeList elements, int depth) throws NoSuchFieldException, ClassNotFoundException {
        // if depth is zero, we don't want dig deeper
        if (depth==0) return;
        // otherwise continue with getting the bean   
        XMLBeanSet xmlBeans = new XMLBeanSet();
        for (int i=0; i<elements.getLength(); i++) {
            Node elementNode = elements.item(i);
            // is it really element ?
            switch (elementNode.getNodeType()) {
                
                case Node.ELEMENT_NODE :
                    Element element = (Element)elementNode;
                    String elementName = elementNode.getNodeName();
                    if (DEBUG) System.out.println("XMLBean.fillElements(): Got child element:"+elementName);
                    Field elemField = this.getClass().getField(XMLBean.XMLBEAN_ELEM_PREFIX+elementName);
                    if (DEBUG) System.out.println("XMLBean.fillElements(): Got field:"+elemField.getName());
                    if (elemField != null) {
                        // ok, lets get the XMLBean instance
                        XMLBean childBean = getXMLBean(element,depth-1);
                        if (DEBUG) System.out.println("XMLBean.fillElements(): got ChildBean!!!"+childBean);
                        // store it together with all other instances of this type
                        xmlBeans.addXMLBean(elemField,childBean);
                    }
                    break;
                case Node.TEXT_NODE :
                    if (DEBUG) System.out.println("XMLBean.fillElements(): Got TEXT_NODE:"+elementNode.getNodeValue());
                    this.xml_pcdata = elementNode.getNodeValue();
                    break;
                    
                case Node.CDATA_SECTION_NODE:
                    if (DEBUG) System.out.println("XMLBean.fillElements(): Got CDATA_NODE:"+elementNode.getNodeValue());
                    this.xml_cdata = elementNode.getNodeValue();
                    break;
                default :
                    if (DEBUG) System.out.println("XMLBean.fillElements(): Got unsupported node:"+elementNode.getNodeValue());
            }
        }
        
        if (DEBUG) System.out.println("XMLBean.fillElements(): all elements processed, now fill variables");
        
        // now we can fill the variables with beans ...
        Field[] fieldsToFill = xmlBeans.getFields();
        if (DEBUG) System.out.println("XMLBean.fillElements(): processing "+fieldsToFill+" fields, length = "+fieldsToFill.length);
        for (int i = 0 ; i < fieldsToFill.length; i++) {
            
            Field field = fieldsToFill[i];
            if (DEBUG) System.out.println("XMLBean.fillElements(): Field i"+i+" field="+field);
            if (DEBUG) System.out.println("XMLBean.fillElements(): filling this field:"+field.getName());
            // get all XML beans instances to be stored in this field
            XMLBean[] xmlBeanInstances = xmlBeans.getXMLBeans(field);
            // store all instances in the field variable
            Object xmlBeanArray = Array.newInstance(field.getType().getComponentType(),xmlBeanInstances.length);
            System.arraycopy(xmlBeanInstances,0,xmlBeanArray,0,xmlBeanInstances.length);
            try {
                field.set(this,xmlBeanArray);
            } catch (IllegalAccessException iae) {
                throw new NoSuchFieldException("Cannot access requested field");
            } 
        }        
    }
    
    
    public static XMLBean getXMLBean(Document doc) throws ClassNotFoundException {
        return getXMLBean(doc,-1);
    }
    
    public static XMLBean getXMLBean(Document doc,int depth) throws ClassNotFoundException {
        Element element = doc.getDocumentElement();
        return getXMLBean(element, depth);
    }
    
    public static XMLBean getXMLBean(Element element) throws ClassNotFoundException {
        return getXMLBean(element,-1);
    }
    
    public static XMLBean getXMLBean(Element element, int depth) throws ClassNotFoundException {
        // if depth is zero, we don't want dig deeper
        if (depth==0) return null;
        // otherwise continue with getting the bean   
        String elName = element.getTagName();
        Class xmlBeanClass = null;
        XMLBean xmlBean = null;
        // try to load class
        xmlBeanClass = Class.forName(XMLBEAN_PACKAGE+"."+elName);
        if (DEBUG) System.out.println("Trying to instintiate "+XMLBEAN_PACKAGE+"."+elName);
        Object aBean = null;
        try {
            aBean = xmlBeanClass.newInstance();
        } catch (IllegalAccessException iae) {
            throw new ClassNotFoundException("Cannot instintiate class - illegal access: "+xmlBeanClass);
        } catch (InstantiationException ie) {
            throw new ClassNotFoundException("Cannot instintiate class: "+xmlBeanClass);
        }
        // is the bean instance of xmlBean ?
        if (!XMLBean.class.isInstance(aBean)) {
            throw new ClassNotFoundException("class "+xmlBeanClass+" is not instance of XMLBean");
        }
        // try to instantiate the class - must have a constructor with no arguments
        xmlBean = (XMLBean)aBean;
        
        if (DEBUG) System.out.println("XMLBean.getXMLBean(): instintiated new XMLBean");
        // now we have the bean - so lets get attributes and fill them :-).
        NamedNodeMap atts = element.getAttributes();
        if (atts!=null) {
            if (atts.getLength() != 0) {
                try {
                    xmlBean.fillAttributes(atts);
                    if (DEBUG) System.out.println("XMLBean.getXMLBean(): got attributes");
                } catch (NoSuchFieldException nsfe) {
                    throw new ClassNotFoundException("Cannot fill defined attributes");
                }
            }
        }
        
        
        // do we have any children - lets get them as beans ... (or set pcdata if applicable)
        NodeList childElements = element.getChildNodes();
        if (childElements != null) {
            if (childElements.getLength()!=0) {
                if (DEBUG) System.out.println("XMLBean.getXMLBean(): have to process childElements, size="+childElements.getLength());
                try {
                    xmlBean.fillElements(childElements,depth);
                } catch (NoSuchFieldException nsfe) {
                    throw new ClassNotFoundException("Cannot fill children elements - no such a field exception");
                }
            }
        }
        return xmlBean;
    }
    
    public Document toDocument() throws DOMException{
        return toDocument(-1);
    }
    
    public Document toDocument(int depth) throws DOMException {
        if (DEBUG) System.out.println("XMLBean:toDocument() begin");
        Document doc =  getDocumentBuilder().newDocument();
        Element element = this.toElement(doc,depth);
        doc.appendChild(element);
        return doc;
    }
    
    public Element toElement(Document doc) throws DOMException {
        return toElement(doc,-1);
    }
    
    public Element toElement(Document doc, int depth) throws DOMException {
        // if depth is zero, we don't want serialize anymore, so return null,
        if (depth==0) return null;
        // otherwise continue with serialization
        if (DEBUG) System.out.println("XMLBean:toElement() begin, this="+this);
        // get the name of the class - it will be used as the name of the
        String fullClassName = this.getClass().getName();
        if (DEBUG) System.out.println("XMLBean:toElement() fullClassName="+fullClassName);
        Package p = this.getClass().getPackage();
        if (DEBUG) System.out.println("XMLBean:toElement() package="+p);
        //String packageName = p.getName();
        //if (DEBUG) System.out.println("XMLBean:toElement() packageName="+packageName);
        // element tag
        String className = cutPackage(fullClassName);
        if (DEBUG) System.out.println("XMLBean:toElement() - className="+className);
        Element element = doc.createElement(className);
        
        // do we have any pcdata ?
        if (this.xml_pcdata!=null) {
            if (DEBUG) System.out.println("XMLBean:toElement() adding PCDATA:"+this.xml_pcdata);
            Text textNode = doc.createTextNode(this.xml_pcdata);
            //textNode.setNodeValue();
            element.appendChild(textNode);
        }
        
        if (this.xml_cdata!=null) {
            if (DEBUG) System.out.println("XMLBean:toElement() adding CDATA:"+this.xml_cdata);
            
            CDATASection cdataNode = doc.createCDATASection(this.xml_cdata);
            element.appendChild(cdataNode);
        }
        
        // now search for variables and add attributes/elements
        Field[] fields = this.getClass().getFields();
        for (int i=0; i< fields.length; i++) {
            Field field = fields[i];
            if (DEBUG) System.out.println("XMLBean:toElement(): processing field="+field.getName());
            // search for attribute
            String fieldName = field.getName();
            if (fieldName.startsWith(XMLBEAN_ATT_PREFIX)) {
                String attributeName = cutPrefix(fieldName,XMLBEAN_ATT_PREFIX);
                Object fieldValue = null;
                try {
                    fieldValue = field.get(this);
                } catch (IllegalAccessException iae) {
                    throw new DOMException(Short.MIN_VALUE,"Cannot access XMLBean's field:"+field);
                }
                if (fieldValue != null) {
                    String value = fieldValue.toString();
                    if (DEBUG) System.out.println("XMLBean:toElement(): got attribute value = "+fieldValue);
                    element.setAttribute(attributeName,value);
                } else {
                    // nothing
                    if (DEBUG) System.out.println("XMLBean:toElement(): field value  = "+fieldValue);
                }
            }
            // search for element
            if (fieldName.startsWith(XMLBEAN_ELEM_PREFIX)) {
                Object value = null;
                try {
                    value = field.get(this);
                } catch (IllegalAccessException iae) {
                    throw new DOMException(Short.MIN_VALUE,"Cannot access XMLBean's field:"+field);
                }
                if (value != null) {
                    if (field.getType().isArray()) {
                        int length = Array.getLength(value);
                        for (int j = 0; j < length; j++) {
                            Object xmlBeanObject = Array.get(value,j);
                            if (xmlBeanObject!=null) {
                                if (xmlBeanObject instanceof XMLBean) {
                                    Element childElement = ((XMLBean)xmlBeanObject).toElement(doc, depth - 1);
                                    element.appendChild(childElement);
                                } else {
                                    if (DEBUG) System.out.println("XMLBean:toElement() object in the array is not instanceof XMLBean");
                                }
                            }
                        }
                    } else {
                        if (DEBUG) System.out.println("XMLBean:toElement() - cannot handle elements from non arrays");
                    }
                }
            }
        }
        // n
        
        return element;
    }
    
    
    
    // helper class for storing already instantiated XMLBeans grouped by
    // element types
    public static class XMLBeanSet {
        
        private HashMap fields;
        
        public XMLBeanSet() {
            fields = new HashMap();
        }
        
        public void addXMLBean(Field field, XMLBean xmlBean) {
            ArrayList xmlBeansInstances = (ArrayList)fields.get(field);
            if (xmlBeansInstances != null) {
                xmlBeansInstances.add(xmlBean);
            } else {
                xmlBeansInstances = new ArrayList();
                xmlBeansInstances.add(xmlBean);
                fields.put(field,xmlBeansInstances);
            }
        }
        
        public XMLBean[] getXMLBeans(Field field) {
            ArrayList xmlBeansInstances = (ArrayList)fields.get(field);
            if (xmlBeansInstances != null) {
                return (XMLBean[])(xmlBeansInstances.toArray(new XMLBean[0]));
            } else {
                return null;
            }
        }
        
        public Field[] getFields() {
            return (Field[])(fields.keySet().toArray(new Field[0]));
        }
        
    }
   

}
