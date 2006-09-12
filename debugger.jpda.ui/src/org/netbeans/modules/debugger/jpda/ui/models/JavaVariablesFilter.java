/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
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

package org.netbeans.modules.debugger.jpda.ui.models;

import java.util.HashSet;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.InvalidExpressionException;
import org.netbeans.api.debugger.jpda.ObjectVariable;
import org.netbeans.api.debugger.jpda.Variable;
import org.netbeans.spi.debugger.jpda.VariablesFilterAdapter;
import org.netbeans.spi.debugger.ui.Constants;
import org.netbeans.spi.viewmodel.TableModel;
import org.netbeans.spi.viewmodel.TreeModel;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.ErrorManager;


/**
 *
 * @author   Jan Jancura
 */
public class JavaVariablesFilter extends VariablesFilterAdapter {
    
    public String[] getSupportedTypes () {
        return new String[] {
            "java.lang.String",
            "java.lang.StringBuffer",
            
            "java.lang.Character",
            "java.lang.Integer",
            "java.lang.Float",
            "java.lang.Byte",
            "java.lang.Boolean",
            "java.lang.Double",
            "java.lang.Long",
            "java.lang.Short",
            
            "java.lang.ref.WeakReference",
            
            "java.util.ArrayList",
            "java.util.HashSet",
            "java.util.LinkedHashSet",
            "java.util.LinkedList",
            "java.util.Stack",
            "java.util.TreeSet",
            "java.util.Vector",
            "java.util.Hashtable",
            "java.util.Hashtable$Entry",
            "java.util.HashMap",
            "java.util.HashMap$Entry",
            "java.util.IdentityHashMap",
            "java.util.AbstractMap$SimpleEntry",
            "java.util.TreeMap",
            "java.util.TreeMap$Entry",
            "java.util.WeakHashMap",
            "java.util.LinkedHashMap",
            "java.util.LinkedHashMap$Entry",
            
            "java.beans.PropertyChangeSupport"
        };
    }
    
    public String[] getSupportedAncestors () {
        return new String[] {
        };
    }
    
    /** 
     * Returns filtered children for given parent on given indexes.
     *
     * @param   original the original tree model
     * @throws  NoInformationException if the set of children can not be
     *          resolved
     * @throws  ComputingException if the children resolving process 
     *          is time consuming, and will be performed off-line 
     * @throws  UnknownTypeException if this TreeModelFilter implementation is not
     *          able to resolve dchildren for given node type
     *
     * @return  children for given parent on given indexes
     */
    public Object[] getChildren (
        TreeModel original, 
        Variable variable, 
        int from, 
        int to
    ) throws UnknownTypeException {
        
        String type = variable.getType ();
        
        if (isToArrayType (type)) {
            ObjectVariable ov = (ObjectVariable) variable;
            try {
                ov = (ObjectVariable) ov.invokeMethod (
                    "toArray",
                    "()[Ljava/lang/Object;",
                    new Variable [0]
                );
                return original.getChildren(ov, from, to);
            } catch (NoSuchMethodException e) {
                Field elementData = ov.getField("elementData");
                if (elementData != null) {
                    return original.getChildren(elementData, from, to);
                } else {
                    ErrorManager.getDefault().notify(e);
                }
            } catch (InvalidExpressionException e) {
                if ( (e.getTargetException () != null) &&
                     (e.getTargetException () instanceof 
                       UnsupportedOperationException)
                ) {
                    // PATCH for J2ME. see 45543
                    return original.getChildren (variable, from, to);
                }
                ErrorManager.getDefault().notify(e);
            }
        }
        if (isMapMapType (type)) 
            try {
                ObjectVariable ov = (ObjectVariable) variable;
                ov = (ObjectVariable) ov.invokeMethod (
                    "entrySet",
                    "()Ljava/util/Set;",
                    new Variable [0]
                );
                ov = (ObjectVariable) ov.invokeMethod (
                    "toArray",
                    "()[Ljava/lang/Object;",
                    new Variable [0]
                );
                return original.getChildren(ov, from, to);
            } catch (InvalidExpressionException e) {
                if ( (e.getTargetException () != null) &&
                     (e.getTargetException () instanceof 
                       UnsupportedOperationException)
                ) {
                    // PATCH for J2ME. see 45543
                    return original.getChildren (variable, from, to);
                }
                ErrorManager.getDefault().notify(e);
            } catch (NoSuchMethodException e) {
                ErrorManager.getDefault().notify(e);
            }
        if ( isMapEntryType (type)
        ) {
            ObjectVariable ov = (ObjectVariable) variable;
            Field[] fs = new Field [2];
            fs [0] = ov.getField ("key");
            fs [1] = ov.getField ("value");
            return fs;
        }
        if ( "java.beans.PropertyChangeSupport".equals (type)
        ) 
            try {
                ObjectVariable ov = (ObjectVariable) variable;
                return ((ObjectVariable) ov.invokeMethod (
                    "getPropertyChangeListeners",
                    "()[Ljava/beans/PropertyChangeListener;",
                    new Variable [0]
                )).getFields (from, to);
            } catch (InvalidExpressionException e) {
                if ( (e.getTargetException () != null) &&
                     (e.getTargetException () instanceof 
                       UnsupportedOperationException)
                ) {
                    // PATCH for J2ME. see 45543
                    return original.getChildren (variable, from, to);
                }
                ErrorManager.getDefault().notify(e);
            } catch (NoSuchMethodException e) {
                ErrorManager.getDefault().notify(e);
            }
//        if ( type.equals ("java.lang.ref.WeakReference")
//        ) 
//            try {
//                ObjectVariable ov = (ObjectVariable) variable;
//                return new Object [] {ov.invokeMethod (
//                    "get",
//                    "()Ljava/lang/Object;",
//                    new Variable [0]
//                )};
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace ();
//            }
        if ( "java.lang.ref.WeakReference".equals (type)
        ) {
            ObjectVariable ov = (ObjectVariable) variable;
            return new Object [] {ov.getField ("referent")};
        }
        return original.getChildren (variable, from, to);
    }

    /**
     * Returns number of filtered children for given variable.
     *
     * @param   original the original tree model
     * @param   variable a variable of returned fields
     *
     * @throws  NoInformationException if the set of children can not be
     *          resolved
     * @throws  ComputingException if the children resolving process
     *          is time consuming, and will be performed off-line
     * @throws  UnknownTypeException if this TreeModelFilter implementation is not
     *          able to resolve dchildren for given node type
     *
     * @return  number of filtered children for given variable
     */
    public int getChildrenCount (TreeModel original, Variable variable) 
    throws UnknownTypeException {

        String type = variable.getType();

        if (isToArrayType (type)) {
            ObjectVariable ov = (ObjectVariable) variable;
            try {
                ov = (ObjectVariable) ov.invokeMethod (
                    "toArray",
                    "()[Ljava/lang/Object;",
                    new Variable [0]
                );
                return original.getChildrenCount(ov);
            } catch (NoSuchMethodException e) {
                Field elementData = ov.getField("elementData");
                if (elementData != null) {
                    return original.getChildrenCount(elementData);
                } else {
                    ErrorManager.getDefault().notify(e);
                }
            } catch (InvalidExpressionException e) {
                if ( (e.getTargetException () != null) &&
                     (e.getTargetException () instanceof 
                       UnsupportedOperationException)
                ) {
                    // PATCH for J2ME. see 45543
                    return original.getChildrenCount(variable);
                }
                ErrorManager.getDefault().notify(e);
            }
        } else if (isMapMapType (type)) {
            try {
                ObjectVariable ov = (ObjectVariable) variable;
                ov = (ObjectVariable) ov.invokeMethod (
                    "entrySet",
                    "()Ljava/util/Set;",
                    new Variable [0]
                );
                ov = (ObjectVariable) ov.invokeMethod (
                    "toArray",
                    "()[Ljava/lang/Object;",
                    new Variable [0]
                );
                return original.getChildrenCount(ov);
            } catch (InvalidExpressionException e) {
                if ( (e.getTargetException () != null) &&
                     (e.getTargetException () instanceof 
                       UnsupportedOperationException)
                ) {
                    // PATCH for J2ME. see 45543
                    return original.getChildrenCount(variable);
                }
                ErrorManager.getDefault().notify(e);
            } catch (NoSuchMethodException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        else if (isMapEntryType(type)) {
            return 2;
        }
        else if ("java.beans.PropertyChangeSupport".equals(type)) {
            return getChildren (original, variable, 0, 0).length;
        }
        else if ("java.lang.ref.WeakReference".equals(type)) {
            return 1;
        }
        return original.getChildrenCount(variable);
    }

    /**
     * Returns true if node is leaf.
     * 
     * @param   original the original tree model
     * @throws  UnknownTypeException if this TreeModel implementation is not
     *          able to resolve dchildren for given node type
     * @return  true if node is leaf
     */
    public boolean isLeaf (TreeModel original, Variable variable) 
    throws UnknownTypeException {
        String type = variable.getType ();

        // PATCH for J2ME
        if ( isLeafType (type) 
        ) return true;
        return original.isLeaf (variable);
    }
    
    public Object getValueAt (
        TableModel original, 
        Variable variable, 
        String columnID
    ) throws UnknownTypeException {

        String type = variable.getType ();
        ObjectVariable ov = (ObjectVariable) variable;
        if ( isMapEntryType (type) &&
             ( columnID == Constants.LOCALS_VALUE_COLUMN_ID ||
               columnID == Constants.WATCH_VALUE_COLUMN_ID)
        ) {
            return ov.getField ("key").getValue () + "=>" + 
                   ov.getField ("value").getValue ();
        }
        if ( isGetValueType (type) &&
             ( columnID == Constants.LOCALS_VALUE_COLUMN_ID ||
               columnID == Constants.WATCH_VALUE_COLUMN_ID)
        ) {
            return ov.getField ("value").getValue ();
        }
        if ( isToStringValueType (type) &&
             ( columnID == Constants.LOCALS_VALUE_COLUMN_ID ||
               columnID == Constants.WATCH_VALUE_COLUMN_ID)
        ) {
            try {
                return ov.getToStringValue ();
            } catch (InvalidExpressionException ex) {
                if ( (ex.getTargetException () != null) &&
                     (ex.getTargetException () instanceof 
                       UnsupportedOperationException)
                ) {
                    // PATCH for J2ME. see 45543
                    return original.getValueAt (variable, columnID);
                }
                return ex.getLocalizedMessage ();
            }
        }
        return original.getValueAt (variable, columnID);
    }

    
    // other methods ...........................................................
    
    private static HashSet getValueType;
    private static boolean isGetValueType (String type) {
        if (getValueType == null) {
            getValueType = new HashSet ();
            getValueType.add ("java.lang.Character");
            getValueType.add ("java.lang.Integer");
            getValueType.add ("java.lang.Float");
            getValueType.add ("java.lang.Byte");
            getValueType.add ("java.lang.Boolean");
            getValueType.add ("java.lang.Double");
            getValueType.add ("java.lang.Long");
            getValueType.add ("java.lang.Short");
        }
        return getValueType.contains (type);
    }
    
    private static HashSet leafType;
    private static boolean isLeafType (String type) {
        if (leafType == null) {
            leafType = new HashSet ();
            leafType.add ("java.lang.String");
            leafType.add ("java.lang.Character");
            leafType.add ("java.lang.Integer");
            leafType.add ("java.lang.Float");
            leafType.add ("java.lang.Byte");
            leafType.add ("java.lang.Boolean");
            leafType.add ("java.lang.Double");
            leafType.add ("java.lang.Long");
            leafType.add ("java.lang.Short");
        }
        return leafType.contains (type);
    }
    
    private static HashSet toStringValueType;
    private static boolean isToStringValueType (String type) {
        if (toStringValueType == null) {
            toStringValueType = new HashSet ();
            toStringValueType.add ("java.lang.StringBuffer");
        }
        return toStringValueType.contains (type);
    }
    
    private static HashSet mapEntryType;
    private static boolean isMapEntryType (String type) {
        if (mapEntryType == null) {
            mapEntryType = new HashSet ();
            mapEntryType.add ("java.util.HashMap$Entry");
            mapEntryType.add ("java.util.Hashtable$Entry");
            mapEntryType.add ("java.util.AbstractMap$SimpleEntry");
            mapEntryType.add ("java.util.LinkedHashMap$Entry");
            mapEntryType.add ("java.util.TreeMap$Entry");
        }
        return mapEntryType.contains (type);
    }
    
    private static HashSet mapMapType;
    private static boolean isMapMapType (String type) {
        if (mapMapType == null) {
            mapMapType = new HashSet ();
            mapMapType.add ("java.util.HashMap");
            mapMapType.add ("java.util.IdentityHashMap");
            mapMapType.add ("java.util.Hashtable");
            mapMapType.add ("java.util.TreeMap");
            mapMapType.add ("java.util.WeakHashMap");
            mapMapType.add ("java.util.LinkedHashMap");
            mapMapType.add ("java.util.concurrent.ConcurrentHashMap");
            mapMapType.add ("java.util.EnumMap");
        }
        return mapMapType.contains (type);
    }
    
    private static HashSet toArrayType;
    private static boolean isToArrayType (String type) {
        if (toArrayType == null) {
            toArrayType = new HashSet ();
            toArrayType.add ("java.util.ArrayList");
            toArrayType.add ("java.util.HashSet");
            toArrayType.add ("java.util.LinkedHashSet");
            toArrayType.add ("java.util.LinkedList");
            toArrayType.add ("java.util.Stack");
            toArrayType.add ("java.util.TreeSet");
            toArrayType.add ("java.util.Vector");
            toArrayType.add ("java.util.concurrent.CopyOnWriteArraySet");
            toArrayType.add ("java.util.EnumSet");
        }
        return toArrayType.contains (type);
    }
}
