/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.apache.tools.ant.module.api;

import org.openide.nodes.Node;

/** Represents an object with an associated introspectable class.
 * Used for Ant elements which are matched to some Java class
 * (e.g. for a task or for a subelement).
 * Similar in concept to InstanceCookie; however InstanceCookie
 * requires the cookie to be able to load the actual class and
 * instantiate it (which is not always possible from these elements)
 * and also does not provide a way to get the class name <em>without</em>
 * loading the class (which is useful from these elements).
 * IntrospectedInfo can be used to look up introspection results from
 * the resulting class name.
 * @since 2.3
 * @see IntrospectedInfo
 * @deprecated No longer useful in new UI.
 */
public interface IntrospectionCookie extends Node.Cookie {
    
    /** Get the name of the class this object is associated with.
     * Objects <em>not</em> associated with a class, or not associated
     * with a known particular class, should not have this cookie.
     * @return the fully-qualified dot-separated class name
     */
    String getClassName ();
    
}
