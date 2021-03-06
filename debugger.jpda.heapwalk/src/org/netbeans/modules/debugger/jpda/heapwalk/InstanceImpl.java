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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.debugger.jpda.heapwalk;

import java.lang.reflect.InvocationTargetException;
import org.netbeans.lib.profiler.heap.FieldValue;
import org.netbeans.lib.profiler.heap.Instance;
import org.netbeans.lib.profiler.heap.JavaClass;
import org.netbeans.lib.profiler.heap.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.netbeans.api.debugger.jpda.ClassVariable;
import org.netbeans.api.debugger.jpda.JPDAArrayType;
import org.netbeans.api.debugger.jpda.JPDAClassType;
import org.netbeans.api.debugger.jpda.ObjectVariable;
import org.netbeans.api.debugger.jpda.Variable;
import org.netbeans.lib.profiler.heap.ObjectFieldValue;
import org.openide.util.Exceptions;

/**
 *
 * @author Martin Entlicher
 */
public class InstanceImpl implements Instance {
    
    private ObjectVariable var;
    private JavaClass varClass;
    protected HeapImpl heap;
    
    /** Creates a new instance of InstanceImpl */
    protected InstanceImpl(HeapImpl heap, ObjectVariable var) {
        this.var = var;
        this.heap = heap;
    }
    
    public static Instance createInstance(HeapImpl heap, ObjectVariable var) {
        InstanceImpl instance;
        JPDAClassType type = var.getClassType();
        if (type instanceof JPDAArrayType) {
            boolean isPrimitiveArray;
            isPrimitiveArray = !(((JPDAArrayType) type).getComponentType() instanceof JPDAClassType);
            if (isPrimitiveArray) {
                instance = new PrimitiveArrayInstanceImpl(heap, var);
            } else {
                instance = new ObjectArrayInstanceImpl(heap, var);
            }
        } else {
            instance = new InstanceImpl(heap, var);
        }
        instance.varClass = getJavaClass(heap, var);
        return instance;
    }

    private static JavaClass getJavaClass(HeapImpl heap, ObjectVariable var) {
        JPDAClassType type = var.getClassType();
        if (type != null) {
            return new JavaClassImpl(heap, type);
        } else {
            return new JavaClassImpl(var.getType());
        }
    }
    
    @Override
    public JavaClass getJavaClass() {
        return varClass;
    }

    @Override
    public long getInstanceId() {
        return var.getUniqueID();
    }

    @Override
    public int getInstanceNumber() {
        return (int) var.getUniqueID();
    }

    /*private int computeInstanceNumber() {
        JPDAClassType classType = var.getClassType();
        if (classType == null) {
            return 0;
        }
        List<ObjectVariable> vars = classType.getInstances(0);
        int i = 1;
        for (ObjectVariable obj: vars) {
            if (var.getUniqueID() == obj.getUniqueID()) {
                break;
            }
            i++;
        }
        return i;
    }*/
    
    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public List<FieldValue> getFieldValues() {
        int fieldsCount = var.getFieldsCount();
        org.netbeans.api.debugger.jpda.Field[] varFields = var.getFields(0, fieldsCount);
        List<FieldValue> fields = new ArrayList<FieldValue>(varFields.length);
        for (org.netbeans.api.debugger.jpda.Field field : varFields) {
            if (!field.isStatic()) {
                if (field instanceof ObjectVariable) {
                    Instance instance;
                    if (((ObjectVariable) field).getUniqueID() == 0L) {
                        instance = null;
                    } else {
                        instance = InstanceImpl.createInstance(heap, (ObjectVariable) field);
                    }
                    fields.add(new ObjectFieldValueImpl(heap, this, field, instance));
                } else {
                    fields.add(new FieldValueImpl(heap, this, field));
                }
            }
        }
        return fields;
    }

    @Override
    public Object getValueOfField(String name) {
        Iterator fIt = getFieldValues().iterator();
        FieldValue matchingFieldValue = null;

        while (fIt.hasNext()) {
            FieldValue fieldValue = (FieldValue) fIt.next();

            if (fieldValue.getField().getName().equals(name)) {
                matchingFieldValue = fieldValue;
            }
        }

        if (matchingFieldValue == null) {
            return null;
        }

        if (matchingFieldValue instanceof ObjectFieldValue) {
            return ((ObjectFieldValue) matchingFieldValue).getInstance();
        } else {
            return ((FieldValue) matchingFieldValue).getValue();
        }

   }

    @Override
    public List<FieldValue> getStaticFieldValues() {
        return getJavaClass().getStaticFieldValues();
    }

    @Override
    public List<Value> getReferences() {
        List<ObjectVariable> references = var.getReferringObjects(0);
        List<Value> values = new ArrayList<Value>(references.size());
        Set<org.netbeans.api.debugger.jpda.Field> referencedFields = new HashSet<org.netbeans.api.debugger.jpda.Field>();
        for (ObjectVariable obj : references) {
            JPDAClassType type = obj.getClassType();
            if (type instanceof JPDAArrayType) {
                int length = obj.getFieldsCount();
                int CHUNK = 1000;
                for (int i = 0; i < length; i += CHUNK) {
                    int to = Math.min(i + CHUNK, length);
                    Variable[] items = obj.getFields(i, to);
                    int j = i;
                    for (Variable item: items) {
                        if (var.equals(item)) {
                            Instance instance = createInstance(heap, obj);
                            values.add(new ArrayItemValueImpl(instance, this, j));
                            break;
                        }
                        j++;
                    }
                    if (j < to) {
                        break;
                    }
                }
            } else {
                org.netbeans.api.debugger.jpda.Field[] allFields;
                if (obj instanceof ClassVariable) {
                    try {
                        type = (JPDAClassType) obj.getClass().getMethod("getReflectedType").invoke(obj);
                    } catch (NoSuchMethodException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (SecurityException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IllegalAccessException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IllegalArgumentException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (InvocationTargetException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    allFields = type.staticFields().toArray(new org.netbeans.api.debugger.jpda.Field[0]);
                } else {
                    org.netbeans.api.debugger.jpda.Field[] instanceFields = obj.getFields(0, Integer.MAX_VALUE);
                    org.netbeans.api.debugger.jpda.Field[] inheritedFields = obj.getInheritedFields(0, Integer.MAX_VALUE);
                    org.netbeans.api.debugger.jpda.Field[] staticFields = obj.getAllStaticFields(0, Integer.MAX_VALUE);
                    allFields = new org.netbeans.api.debugger.jpda.Field
                            [instanceFields.length + inheritedFields.length + staticFields.length];
                    System.arraycopy(instanceFields, 0, allFields, 0, instanceFields.length);
                    System.arraycopy(inheritedFields, 0, allFields, instanceFields.length, inheritedFields.length);
                    System.arraycopy(staticFields, 0, allFields, instanceFields.length + inheritedFields.length, staticFields.length);
                }
                for (org.netbeans.api.debugger.jpda.Field field : allFields) {
                    if (field instanceof ObjectVariable &&
                        !referencedFields.contains(field) &&
                        var.getUniqueID() == ((ObjectVariable) field).getUniqueID()) {
                        
                        referencedFields.add(field);
                        Instance instance = createInstance(heap, obj);
                        values.add(new ObjectFieldValueImpl(heap, instance, field, this));
                        break;
                    }
                }
            }
        }
        return values;
    }

    @Override
    public boolean isGCRoot() {
        return false;
    }

    @Override
    public long getRetainedSize() {
        return 0;
    }

    @Override
    public long getReachableSize() {
        return 0;
    }
    
    @Override
    public Instance getNearestGCRootPointer() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof InstanceImpl)) {
            return false;
        }
        return var.getUniqueID() == ((InstanceImpl) obj).var.getUniqueID();
    }

    @Override
    public int hashCode() {
        return (int) var.getUniqueID();
    }
    
}
