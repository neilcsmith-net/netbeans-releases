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
/**
 *        This generated bean class JoinUser
 *        matches the schema element '_join-user'.
 *
 *        Generated on Mon Sep 27 16:53:12 PDT 2004
 */
package org.netbeans.modules.collab.channel.filesharing.msgbean;

public class JoinUser {
    private User _User;

    public JoinUser() {
        _User = new User();
    }

    // Deep copy
    public JoinUser(org.netbeans.modules.collab.channel.filesharing.msgbean.JoinUser source) {
        _User = new org.netbeans.modules.collab.channel.filesharing.msgbean.User(source._User);
    }

    // This attribute is mandatory
    public void setUser(org.netbeans.modules.collab.channel.filesharing.msgbean.User value) {
        _User = value;
    }

    public org.netbeans.modules.collab.channel.filesharing.msgbean.User getUser() {
        return _User;
    }

    public void writeNode(java.io.Writer out, String nodeName, String indent)
    throws java.io.IOException {
        out.write(indent);
        out.write("<");
        out.write(nodeName);
        out.write(">\n");

        String nextIndent = indent + "	";

        if (_User != null) {
            _User.writeNode(out, "user", nextIndent);
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

            if (childNodeName == "user") {
                _User = new org.netbeans.modules.collab.channel.filesharing.msgbean.User();
                _User.readNode(childNode);
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

        if (name == "user") {
            setUser((User) value);
        } else {
            throw new IllegalArgumentException(name + " is not a valid property name for JoinUser");
        }
    }

    public Object fetchPropertyByName(String name) {
        if (name == "user") {
            return getUser();
        }

        throw new IllegalArgumentException(name + " is not a valid property name for JoinUser");
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
        if (_User != null) {
            if (recursive) {
                _User.childBeans(true, beans);
            }

            beans.add(_User);
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof org.netbeans.modules.collab.channel.filesharing.msgbean.JoinUser)) {
            return false;
        }

        org.netbeans.modules.collab.channel.filesharing.msgbean.JoinUser inst = (org.netbeans.modules.collab.channel.filesharing.msgbean.JoinUser) o;

        if (!((_User == null) ? (inst._User == null) : _User.equals(inst._User))) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result = 17;
        result = (37 * result) + ((_User == null) ? 0 : _User.hashCode());

        return result;
    }
}

/*
                The following schema file has been used for generation:

<?xml version="1.0" encoding="UTF-8"?>
<!--
    Document   : collab.xsd
    Created on : May 21, 2004, 7:45 PM
    Author     : Ayub Khan
    Description:
        Purpose of the document follows.
-->
<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"
            targetNamespace="http://sun.com/ns/collab/dev/1_0/filesharing"
            xmlns:c="http://sun.com/ns/collab/dev/1_0"
            xmlns:ch="http://sun.com/ns/collab/dev/1_0/filesharing"
            elementFormDefault="qaulified">

    <!-- collab element -->
    <xsd:element name="c:collab" type="_collab">
    </xsd:element>

    <xsd:complexType name="_collab">
        <xsd:sequence>
            <xsd:element name="version" type="xsd:string"
                        minOccurs="1" maxOccurs="1"/>
            <xsd:choice maxOccurs="1">
                <xsd:element name="ch:send-file" type="_send-file"
                        minOccurs="0" maxOccurs="1"/>
                <xsd:element name="ch:file-changed" type="_file-changed"
                        minOccurs="0" maxOccurs="1"/>
                <xsd:element name="ch:lock-region" type="_lock-region"
                        minOccurs="0" maxOccurs="1"/>
                <xsd:element name="ch:unlock-region" type="_unlock-region"
                        minOccurs="0" maxOccurs="1"/>
                <xsd:element name="ch:join-filesharing" type="_join-filesharing"
                        minOccurs="0" maxOccurs="1"/>
                <xsd:element name="ch:pause-filesharing" type="_pause-filesharing"
                        minOccurs="0" maxOccurs="1"/>
                <xsd:element name="ch:resume-filesharing" type="_resume-filesharing"
                        minOccurs="0" maxOccurs="1"/>
                <xsd:element name="ch:leave-filesharing" type="_leave-filesharing"
                        minOccurs="0" maxOccurs="1"/>
                <xsd:element name="ch:commands" type="_commands"
                        minOccurs="0" maxOccurs="1"/>
            </xsd:choice>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for send-file -->
    <xsd:complexType name="_send-file">
        <xsd:sequence>
            <xsd:element name="file-groups" type="_file-groups"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="send-file-data" type="_send-file-data"
                    minOccurs="1" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>

   <!-- Schema for file-changed -->
    <xsd:complexType name="_file-changed">
        <xsd:sequence>
            <xsd:element name="file-groups" type="_file-groups"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="file-changed-data" type="_file-changed-data"
                    minOccurs="1" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for lock-region -->
    <xsd:complexType name="_lock-region">
        <xsd:sequence>
            <xsd:element name="file-groups" type="_file-groups"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="lock-region-data" type="_lock-region-data"
                    minOccurs="1" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for unlock-region -->
    <xsd:complexType name="_unlock-region">
        <xsd:sequence>
            <xsd:element name="file-groups" type="_file-groups"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="unlock-region-data" type="_unlock-region-data"
                    minOccurs="1" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for join filesharing -->
    <xsd:complexType name="_join-filesharing">
        <xsd:sequence>
            <xsd:choice maxOccurs="1">
                <xsd:element name="begin-join"
                        minOccurs="0" maxOccurs="1"/>
                <xsd:element name="end-join"
                        minOccurs="0" maxOccurs="1"/>
            </xsd:choice>
            <xsd:element name="user" type="_user"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for pause filesharing -->
    <xsd:complexType name="_pause-filesharing">
        <xsd:sequence>
            <xsd:element name="join-user" type="_join-user"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="moderator" type="_moderator"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="file-owners" type="_file-owners"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="users" type="_users"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="file-groups" type="_file-groups"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for resume filesharing -->
    <xsd:complexType name="_resume-filesharing">
        <xsd:sequence>
            <xsd:element name="moderator" type="_moderator"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for pause filesharing -->
    <xsd:complexType name="_leave-filesharing">
        <xsd:sequence>
            <xsd:element name="user" type="_user"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="new-moderator" type="_moderator"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="new-file-owner" type="_new-file-owner"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="file-groups" type="_file-groups"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for commands -->
    <xsd:complexType name="_commands">
        <xsd:sequence>
            <xsd:choice maxOccurs="1">
                <xsd:element name="filesystem-command" type="_filesystem-command"
                        minOccurs="1" maxOccurs="1"/>
            </xsd:choice>
        </xsd:sequence>
    </xsd:complexType>

    <!-- ===================================================== -->

    <xsd:complexType name="_file-groups">
        <xsd:sequence>
            <xsd:element name="file-group" type="_file-group"
                    minOccurs="1" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_file-group">
        <xsd:sequence>
            <xsd:element name="file-group-name" type="xsd:string"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="user" type="_user"
                    minOccurs="1" maxOccurs="1"/>
            <!-- file-name contains full path -->
            <xsd:element name="file-name" type="xsd:string"
                    minOccurs="1" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_send-file-data">
        <xsd:sequence>
            <xsd:element name="file-data" type="_file-data"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="choose-line-region-function" type="xsd:boolean"
                        minOccurs="1" maxOccurs="1"/>
            <xsd:choice maxOccurs="1">
                <xsd:element name="line-region-function" type="_line-region-function"
                        minOccurs="1" maxOccurs="1"/>
                <xsd:element name="line-region" type="_line-region"
                        minOccurs="1" maxOccurs="unbounded"/>
            </xsd:choice>
            <xsd:element name="content" type="_content"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_line-region-function">
        <xsd:sequence>
            <xsd:element name="funtion-name" type="xsd:string"
                        minOccurs="1" maxOccurs="1"/>
            <xsd:element name="arguments"  type="xsd:string"
                    minOccurs="1" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_file-data">
        <xsd:sequence>
            <!-- file-name contains full path -->
            <xsd:element name="file-name" type="xsd:string"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="content-type" type="xsd:string"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="description" type="xsd:string"
                    minOccurs="0" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

   <!-- Schema for file-changed -->
    <xsd:complexType name="_file-changed-data">
        <xsd:sequence>
            <!-- file-name contains full path -->
            <xsd:element name="file-name" type="xsd:string"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="digest" type="xsd:string"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="region-changed" type="_region-changed"
                    minOccurs="1" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for lock-region -->
    <xsd:complexType name="_lock-region-data">
        <xsd:sequence>
            <!-- file-name contains full path -->
            <xsd:element name="file-name" type="xsd:string"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="line-region" type="_line-region"
                    minOccurs="1" maxOccurs="unbounded"/>
            <xsd:choice maxOccurs="1">
                <xsd:element name="text-region" type="_text-region"
                        minOccurs="1" maxOccurs="1"/>
                <xsd:element name="java-region" type="_java-region"
                        minOccurs="1" maxOccurs="1"/>
            </xsd:choice>
            <xsd:element name="content" type="_content"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- Schema for unlock-region -->
    <xsd:complexType name="_unlock-region-data">
        <xsd:sequence>
            <!-- file-name contains full path -->
            <xsd:element name="file-name" type="xsd:string"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="line-region" type="_line-region"
                    minOccurs="1" maxOccurs="unbounded"/>
            <xsd:choice maxOccurs="1">
                <xsd:element name="text-region" type="_text-region"
                        minOccurs="1" maxOccurs="1"/>
                <xsd:element name="java-region" type="_java-region"
                        minOccurs="1" maxOccurs="1"/>
            </xsd:choice>
            <xsd:element name="content" type="_content"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- user elements -->
    <xsd:complexType name="_moderator">
        <xsd:sequence>
            <xsd:element name="users" type="_users"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_join-user">
        <xsd:sequence>
            <xsd:element name="user" type="_user"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_file-owners">
        <xsd:sequence>
            <xsd:element name="users" type="_users"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_new-file-owner">
        <xsd:sequence>
            <xsd:element name="users" type="_users"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_users">
        <xsd:sequence>
            <xsd:element name="user" type="_user"
                    minOccurs="0" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_user">
        <xsd:sequence>
            <xsd:element name="id" type="xsd:string"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="name" type="xsd:string"
                    minOccurs="0" maxOccurs="1"/>
            <xsd:element name="description" type="xsd:string"
                    minOccurs="0" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- filesystem command schema -->
    <xsd:complexType name="_filesystem-command">
        <xsd:sequence>
            <xsd:choice maxOccurs="1">
            <!-- file commands -->
                <xsd:element name="delete-file" type="_delete-file"
                        minOccurs="1" maxOccurs="1"/>
            </xsd:choice>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_delete-file">
        <xsd:sequence>
            <!-- file-name contains full path -->
            <xsd:element name="file-name" type="xsd:string"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <!-- ===================================================== -->

    <xsd:complexType name="_region-changed">
        <xsd:sequence>
            <xsd:choice maxOccurs="1">
                <xsd:element name="text-region-changed" type="_text-region-changed"
                        minOccurs="1" maxOccurs="1"/>
                <xsd:element name="java-region-changed" type="_java-region-changed"
                        minOccurs="1" maxOccurs="1"/>
                <xsd:element name="line-region-changed" type="_line-region-changed"
                        minOccurs="1" maxOccurs="1"/>
            </xsd:choice>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_text-region-changed">
        <xsd:sequence>
            <xsd:element name="text-region" type="_text-region"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="text-change" type="_text-change"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_java-region-changed">
        <xsd:sequence>
            <xsd:element name="java-region" type="_java-region"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="java-change" type="_java-change"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_line-region-changed">
        <xsd:sequence>
            <xsd:element name="line-region" type="_line-region"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="line-change" type="_line-change"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_region" abstract="true">
        <xsd:sequence>
            <xsd:element name="region-name" type="xsd:string"
                        minOccurs="1" maxOccurs="1">
            </xsd:element>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_change" abstract="true"/>

    <xsd:complexType name="_content">
        <xsd:sequence>
            <xsd:element name="encoding" type="xsd:string"
                    minOccurs="1" maxOccurs="1">
            </xsd:element>
            <xsd:element name="digest" type="xsd:string"
                    minOccurs="1" maxOccurs="1">
            </xsd:element>
            <xsd:element name="data" type="xsd:string"
                    minOccurs="1" maxOccurs="1">
            </xsd:element>
        </xsd:sequence>
    </xsd:complexType>



    <!-- ===================================================== -->
    <!-- ================   Text Region    =================== -->
    <!-- ===================================================== -->

    <xsd:complexType name="_text-region">
        <xsd:complexContent>
            <xsd:extension base="_region">
                <xsd:sequence>
                    <xsd:element name="begin-offset" type="xsd:integer"
                            minOccurs="1" maxOccurs="1">
                    </xsd:element>
                    <xsd:element name="length" type="xsd:integer"
                            minOccurs="1" maxOccurs="1">
                    </xsd:element>
                </xsd:sequence>
            </xsd:extension>
        </xsd:complexContent>
    </xsd:complexType>

    <xsd:complexType name="_line-range">
        <xsd:sequence>
            <xsd:element name="from-line" type="xsd:integer"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="to-line" type="xsd:integer"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_offset-range">
        <xsd:sequence>
            <xsd:element name="begin-offset" type="xsd:integer"
                    minOccurs="1" maxOccurs="1">
            </xsd:element>
            <xsd:element name="length" type="xsd:integer"
                    minOccurs="1" maxOccurs="1">
            </xsd:element>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_text-change">
        <xsd:complexContent>
            <xsd:extension base="_change">
                    <xsd:choice maxOccurs="1">
                        <xsd:element name="change-texts" type="_change-texts"
                                minOccurs="1" maxOccurs="1"/>
                        <xsd:element name="content" type="_content"
                                minOccurs="1" maxOccurs="1"/>
                    </xsd:choice>
            </xsd:extension>
        </xsd:complexContent>
    </xsd:complexType>



    <!-- ===================================================== -->
    <!-- ================   Java Region    =================== -->
    <!-- ===================================================== -->
    <xsd:complexType name="_java-region">
        <xsd:complexContent>
            <xsd:extension base="_region">
                <xsd:sequence>
                    <xsd:element name="begin-offset" type="xsd:integer"
                            minOccurs="1" maxOccurs="1">
                    </xsd:element>
                    <xsd:element name="length" type="xsd:integer"
                            minOccurs="1" maxOccurs="1">
                    </xsd:element>
                </xsd:sequence>
            </xsd:extension>
        </xsd:complexContent>
    </xsd:complexType>

    <xsd:complexType name="_java-change">
        <xsd:complexContent>
            <xsd:extension base="_change">
                <xsd:sequence>
                    <xsd:choice maxOccurs="1">
                        <xsd:element name="change-texts" type="_change-texts"
                                minOccurs="1" maxOccurs="1"/>
                        <xsd:element name="content" type="_content"
                                minOccurs="1" maxOccurs="1"/>
                    </xsd:choice>
                </xsd:sequence>
            </xsd:extension>
        </xsd:complexContent>
    </xsd:complexType>



    <!-- ===================================================== -->
    <!-- ================   Line Region    =================== -->
    <!-- ===================================================== -->
    <xsd:complexType name="_line-region">
        <xsd:complexContent>
            <xsd:extension base="_region">
            </xsd:extension>
        </xsd:complexContent>
    </xsd:complexType>

    <xsd:complexType name="_line-change">
        <xsd:complexContent>
            <xsd:extension base="_change">
                <xsd:sequence>
                    <xsd:choice maxOccurs="1">
                        <xsd:element name="content" type="_content"
                                minOccurs="1" maxOccurs="1"/>
                    </xsd:choice>
                </xsd:sequence>
            </xsd:extension>
        </xsd:complexContent>
    </xsd:complexType>



    <!-- ===================================================== -->
    <!-- ================Common Region Type=================== -->
    <!-- ===================================================== -->

    <xsd:complexType name="_change-texts">
        <xsd:sequence>
            <xsd:element name="remove-texts" type="_remove-texts"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="insert-texts" type="_insert-texts"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_remove-texts">
        <xsd:sequence>
            <xsd:element name="remove-text" type="_remove-text"
                    minOccurs="1" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_insert-texts">
        <xsd:sequence>
            <xsd:element name="insert-text" type="_insert-text"
                    minOccurs="1" maxOccurs="unbounded"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_remove-text">
        <xsd:sequence>
            <xsd:element name="offset" type="xsd:integer"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

    <xsd:complexType name="_insert-text">
        <xsd:sequence>
            <xsd:element name="offset" type="xsd:integer"
                    minOccurs="1" maxOccurs="1"/>
            <xsd:element name="content" type="_content"
                    minOccurs="1" maxOccurs="1"/>
        </xsd:sequence>
    </xsd:complexType>

</xsd:schema>

*/
