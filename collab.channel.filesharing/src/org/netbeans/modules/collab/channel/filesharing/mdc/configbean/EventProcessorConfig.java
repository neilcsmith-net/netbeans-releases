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
/**
 *        This generated bean class EventProcessorConfig
 *        matches the schema element '_event-processor-config'.
 *
 *        Generated on Thu Aug 19 15:45:47 PDT 2004
 */
package org.netbeans.modules.collab.channel.filesharing.mdc.configbean;

public class EventProcessorConfig {
    private java.util.List _RegisterEventHandler = new java.util.ArrayList(); // List<RegisterEventHandler>

    public EventProcessorConfig() {
    }

    // Deep copy
    public EventProcessorConfig(
        org.netbeans.modules.collab.channel.filesharing.mdc.configbean.EventProcessorConfig source
    ) {
        for (java.util.Iterator it = source._RegisterEventHandler.iterator(); it.hasNext();) {
            _RegisterEventHandler.add(
                new org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler(
                    (org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler) it.next()
                )
            );
        }
    }

    // This attribute is an array containing at least one element
    public void setRegisterEventHandler(
        org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler[] value
    ) {
        if (value == null) {
            value = new RegisterEventHandler[0];
        }

        _RegisterEventHandler.clear();

        for (int i = 0; i < value.length; ++i) {
            _RegisterEventHandler.add(value[i]);
        }
    }

    public void setRegisterEventHandler(
        int index, org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler value
    ) {
        _RegisterEventHandler.set(index, value);
    }

    public org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler[] getRegisterEventHandler() {
        RegisterEventHandler[] arr = new RegisterEventHandler[_RegisterEventHandler.size()];

        return (RegisterEventHandler[]) _RegisterEventHandler.toArray(arr);
    }

    public java.util.List fetchRegisterEventHandlerList() {
        return _RegisterEventHandler;
    }

    public org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler getRegisterEventHandler(
        int index
    ) {
        return (RegisterEventHandler) _RegisterEventHandler.get(index);
    }

    // Return the number of registerEventHandler
    public int sizeRegisterEventHandler() {
        return _RegisterEventHandler.size();
    }

    public int addRegisterEventHandler(
        org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler value
    ) {
        _RegisterEventHandler.add(value);

        return _RegisterEventHandler.size() - 1;
    }

    // Search from the end looking for @param value, and then remove it.
    public int removeRegisterEventHandler(
        org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler value
    ) {
        int pos = _RegisterEventHandler.indexOf(value);

        if (pos >= 0) {
            _RegisterEventHandler.remove(pos);
        }

        return pos;
    }

    public void writeNode(java.io.Writer out, String nodeName, String indent)
    throws java.io.IOException {
        out.write(indent);
        out.write("<");
        out.write(nodeName);
        out.write(">\n");

        String nextIndent = indent + "	";

        for (java.util.Iterator it = _RegisterEventHandler.iterator(); it.hasNext();) {
            org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler element = (org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler) it.next();

            if (element != null) {
                element.writeNode(out, "register-event-handler", nextIndent);
            }
        }

        out.write(indent);
        out.write("</" + nodeName + ">\n");
    }

    public void readNode(org.w3c.dom.Node node) {
        org.w3c.dom.NodeList children = node.getChildNodes();

        for (int i = 0, size = children.getLength(); i < size; ++i) {
            org.w3c.dom.Node childNode = children.item(i);
            String childNodeName = ((childNode.getLocalName() == null) ? childNode.getNodeName().intern()
                                                                       : childNode.getLocalName().intern());
            String childNodeValue = "";

            if (childNode.getFirstChild() != null) {
                childNodeValue = childNode.getFirstChild().getNodeValue();
            }

            if (childNodeName == "register-event-handler") {
                RegisterEventHandler aRegisterEventHandler = new org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler();
                aRegisterEventHandler.readNode(childNode);
                _RegisterEventHandler.add(aRegisterEventHandler);
            } else {
                // Found extra unrecognized childNode
            }
        }
    }

    public void changePropertyByName(String name, Object value) {
        if (name == null) {
            return;
        }

        name = name.intern();

        if (name == "registerEventHandler") {
            addRegisterEventHandler((RegisterEventHandler) value);
        } else if (name == "registerEventHandler[]") {
            setRegisterEventHandler((RegisterEventHandler[]) value);
        } else {
            throw new IllegalArgumentException(name + " is not a valid property name for EventProcessorConfig");
        }
    }

    public Object fetchPropertyByName(String name) {
        if (name == "registerEventHandler[]") {
            return getRegisterEventHandler();
        }

        throw new IllegalArgumentException(name + " is not a valid property name for EventProcessorConfig");
    }

    // Return an array of all of the properties that are beans and are set.
    public java.lang.Object[] childBeans(boolean recursive) {
        java.util.List children = new java.util.LinkedList();
        childBeans(recursive, children);

        java.lang.Object[] result = new java.lang.Object[children.size()];

        return (java.lang.Object[]) children.toArray(result);
    }

    // Put all child beans into the beans list.
    public void childBeans(boolean recursive, java.util.List beans) {
        for (java.util.Iterator it = _RegisterEventHandler.iterator(); it.hasNext();) {
            org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler element = (org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler) it.next();

            if (element != null) {
                if (recursive) {
                    element.childBeans(true, beans);
                }

                beans.add(element);
            }
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof org.netbeans.modules.collab.channel.filesharing.mdc.configbean.EventProcessorConfig)) {
            return false;
        }

        org.netbeans.modules.collab.channel.filesharing.mdc.configbean.EventProcessorConfig inst = (org.netbeans.modules.collab.channel.filesharing.mdc.configbean.EventProcessorConfig) o;

        if (sizeRegisterEventHandler() != inst.sizeRegisterEventHandler()) {
            return false;
        }

        // Compare every element.
        for (
            java.util.Iterator it = _RegisterEventHandler.iterator(), it2 = inst._RegisterEventHandler.iterator();
                it.hasNext() && it2.hasNext();
        ) {
            org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler element = (org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler) it.next();
            org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler element2 = (org.netbeans.modules.collab.channel.filesharing.mdc.configbean.RegisterEventHandler) it2.next();

            if (!((element == null) ? (element2 == null) : element.equals(element2))) {
                return false;
            }
        }

        return true;
    }

    public int hashCode() {
        int result = 17;
        result = (37 * result) + ((_RegisterEventHandler == null) ? 0 : _RegisterEventHandler.hashCode());

        return result;
    }
}

/*
                The following schema file has been used for generation:

<?xml version="1.0" encoding="UTF-8"?>
<!--
    Document   : collab_config.xsd
    Created on : Aug 19, 2004, 7:45 AM
    Author     : Ayub Khan
    Description:
        Purpose of the document follows.
-->
<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"
            targetNamespace="http://sun.com/ns/collab/dev/1_0/mdc"
            xmlns:c="http://sun.com/ns/collab/dev/1_0"
            xmlns:mdc="http://sun.com/ns/collab/dev/1_0/mdc"
            elementFormDefault="qaulified">

    <!-- collab element -->
    <xsd:element name="c:collab" type="_collab"/>

    <xsd:complexType name="_collab">
        <xsd:sequence>
            <xsd:choice maxOccurs="1">
                <xsd:element name="mdc:config" type="_config"
                        minOccurs="1" maxOccurs="unbounded"/>
            </xsd:choice>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for config -->
    <xsd:complexType name="_config">
        <xsd:sequence>
            <xsd:choice maxOccurs="1">
                <xsd:element name="mdc:event-notifier-config" type="_event-notifier-config"
                        minOccurs="1" maxOccurs="1"/>
                <xsd:element name="mdc:event-processor-config" type="_event-processor-config"
                        minOccurs="1" maxOccurs="1"/>
            </xsd:choice>
        </xsd:sequence>
        <xsd:attribute name="version" type="xsd:string"/>
    </xsd:complexType>

   <!-- Schema for event-notifier-config -->
    <xsd:complexType name="_event-notifier-config">
        <xsd:sequence>
            <xsd:element name="register-event" type="_register-event"
                    minOccurs="1" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>

   <!-- Schema for event-processor-config -->
    <xsd:complexType name="_event-processor-config">
        <xsd:sequence>
            <xsd:element name="register-event-handler" type="_register-event-handler"
                    minOccurs="1" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for register-event -->
    <xsd:complexType name="_register-event">
        <xsd:sequence>
            <xsd:element name="event-class" type="_class-name"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="event-name" type="_event-name"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for register-event-handler -->
    <xsd:complexType name="_register-event-handler">
        <xsd:sequence>
            <xsd:element name="event-name" type="_event-name"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="event-handler-info" type="_event-handler-info"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for _event-handler -->
    <xsd:complexType name="_event-handler-info">
        <xsd:sequence>
            <xsd:element name="handler-class" type="_class-name"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="stateful" type="xsd:boolean"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for _event-name -->
    <xsd:complexType name="_event-name">
        <xsd:simpleContent>
            <xsd:extension base="xsd:string"/>
        </xsd:simpleContent>
    </xsd:complexType>

    <!-- Schema for _class -->
    <xsd:complexType name="_class-name">
        <xsd:simpleContent>
            <xsd:extension base="xsd:string"/>
        </xsd:simpleContent>
    </xsd:complexType>

</xsd:schema>

*/
