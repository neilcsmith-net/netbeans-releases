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

package org.netbeans.modules.j2ee.dd.impl.common;

import org.netbeans.modules.schema2beans.BaseBean;
import org.netbeans.modules.schema2beans.BaseProperty;

/**
 * Customized comparator for web.xml
 *
 * @author  Milan Kuchtiak
 */
public class Comparator extends org.netbeans.modules.schema2beans.BeanComparator
{

    public BaseBean compareBean(String 		beanName,
				BaseBean 	curBean,
				BaseBean 	newBean) {
        if (curBean!=null && newBean!= null) {
            if (curBean instanceof KeyBean) {
                String prop = ((KeyBean)curBean).getKeyProperty();
                Object key1 = curBean.getValue(prop);
                Object key2 = newBean.getValue(prop);
                if (key1!=null) {
                    if (key1.equals(key2)) return curBean;
                    else {
                        BaseBean clonnedNewBean = (BaseBean)newBean.clone();
                        clonnedNewBean.setValue(prop,key1);
                        if (curBean.equals(super.compareBean(beanName, curBean, clonnedNewBean))) {
                            return curBean;
                        }
                    }
                }
            } 
        }
        return super.compareBean(beanName, curBean, newBean);
    }
    
    public Object compareProperty(String 	propertyName,
                                  BaseBean 	curBean,
                                  Object 	curValue,
                                  int		curIndex,
                                  BaseBean	newBean,
                                  Object 	newValue,
                                  int		newIndex) {
        return super.compareProperty(propertyName, curBean,curValue,curIndex,newBean,newValue, newIndex);
    }
}
