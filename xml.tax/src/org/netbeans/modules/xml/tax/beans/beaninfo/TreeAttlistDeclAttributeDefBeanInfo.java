/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
package org.netbeans.modules.xml.tax.beans.beaninfo;

import java.beans.*;
import org.netbeans.tax.TreeAttlistDeclAttributeDef;
import org.netbeans.modules.xml.tax.beans.customizer.TreeAttlistDeclAttributeDefCustomizer;
import org.openide.util.Exceptions;

/**
 *
 * @author  Libor Kramolis
 * @version 0.1
 */
public class TreeAttlistDeclAttributeDefBeanInfo extends SimpleBeanInfo {

    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     * 
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
	return new BeanDescriptor  ( TreeAttlistDeclAttributeDef.class , TreeAttlistDeclAttributeDefCustomizer.class );
    }

    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     * 
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        int PROPERTY_name = 0;
        int PROPERTY_defaultValue = 1;
        PropertyDescriptor[] properties = new PropertyDescriptor[2];
        
        try {
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", TreeAttlistDeclAttributeDef.class, "getName", null ); // NOI18N
            properties[PROPERTY_name].setDisplayName ( Util.THIS.getString ( "PROP_TreeAttlistDeclAttributeDefBeanInfo_name" ) );
            properties[PROPERTY_name].setShortDescription ( Util.THIS.getString ( "HINT_TreeAttlistDeclAttributeDefBeanInfo_name" ) );
            properties[PROPERTY_defaultValue] = new PropertyDescriptor ( "defaultValue", TreeAttlistDeclAttributeDef.class, "getDefaultValue", null ); // NOI18N
            properties[PROPERTY_defaultValue].setDisplayName ( Util.THIS.getString ( "PROP_TreeAttlistDeclAttributeDefBeanInfo_defaultValue" ) );
            properties[PROPERTY_defaultValue].setShortDescription ( Util.THIS.getString ( "HINT_TreeAttlistDeclAttributeDefBeanInfo_defaultValue" ) );
        } catch( IntrospectionException e) {
            Exceptions.printStackTrace(e);
        }
            
        return properties;
    }

    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     * 
     * @return  An array of EventSetDescriptors describing the kinds of 
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        int EVENT_propertyChangeListener = 0;
        EventSetDescriptor[] eventSets = new EventSetDescriptor[1];

        try {
            eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( org.netbeans.tax.TreeAttlistDeclAttributeDef.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" ); // NOI18N
        }
        catch( IntrospectionException e) {
            Exceptions.printStackTrace(e);
        }

        return eventSets;
    }

    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     * 
     * @return  An array of MethodDescriptors describing the methods 
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        return new MethodDescriptor[0];
    }

}
