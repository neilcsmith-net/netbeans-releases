//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.11.07 at 12:36:44 PM PST 
//


package org.netbeans.modules.websvc.rest.wadl.model.impl;

public enum HTTPMethodsImpl {

    GET,
    POST,
    PUT,
    HEAD,
    DELETE;

    public String value() {
        return name();
    }

    public static HTTPMethodsImpl fromValue(String v) {
        return valueOf(v);
    }

}
