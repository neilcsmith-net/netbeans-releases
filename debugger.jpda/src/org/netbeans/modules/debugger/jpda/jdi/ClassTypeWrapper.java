/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.modules.debugger.jpda.jdi;

// DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
// Generated by org.netbeans.modules.debugger.jpda.jdi.Generate class.

/**
 * Wrapper for ClassType JDI class.
 * Use methods of this class instead of direct calls on JDI objects.
 * These methods assure that exceptions thrown from JDI calls are handled appropriately.
 *
 * @author Martin Entlicher
 */
public final class ClassTypeWrapper {

    private ClassTypeWrapper() {}

    public static java.util.List<com.sun.jdi.InterfaceType> allInterfaces0(com.sun.jdi.ClassType a) throws org.netbeans.modules.debugger.jpda.jdi.ClassNotPreparedExceptionWrapper {
        try {
            return a.allInterfaces();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.ClassNotPreparedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ClassNotPreparedExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.InterfaceType> allInterfaces(com.sun.jdi.ClassType a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ClassNotPreparedExceptionWrapper {
        try {
            return a.allInterfaces();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.ClassNotPreparedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ClassNotPreparedExceptionWrapper(ex);
        }
    }

    public static com.sun.jdi.Method concreteMethodByName(com.sun.jdi.ClassType a, java.lang.String b, java.lang.String c) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ClassNotPreparedExceptionWrapper {
        try {
            return a.concreteMethodByName(b, c);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.ClassNotPreparedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ClassNotPreparedExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.InterfaceType> interfaces0(com.sun.jdi.ClassType a) throws org.netbeans.modules.debugger.jpda.jdi.ClassNotPreparedExceptionWrapper {
        try {
            return a.interfaces();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.ClassNotPreparedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ClassNotPreparedExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.InterfaceType> interfaces(com.sun.jdi.ClassType a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ClassNotPreparedExceptionWrapper {
        try {
            return a.interfaces();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.ClassNotPreparedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ClassNotPreparedExceptionWrapper(ex);
        }
    }

    public static com.sun.jdi.Value invokeMethod(com.sun.jdi.ClassType a, com.sun.jdi.ThreadReference b, com.sun.jdi.Method c, java.util.List<? extends com.sun.jdi.Value> d, int e) throws com.sun.jdi.InvalidTypeException, com.sun.jdi.ClassNotLoadedException, com.sun.jdi.IncompatibleThreadStateException, com.sun.jdi.InvocationException, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.invokeMethod(b, c, d, e);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static boolean isEnum0(com.sun.jdi.ClassType a) {
        try {
            return a.isEnum();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return false;
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return false;
        }
    }

    public static boolean isEnum(com.sun.jdi.ClassType a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.isEnum();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static com.sun.jdi.ObjectReference newInstance(com.sun.jdi.ClassType a, com.sun.jdi.ThreadReference b, com.sun.jdi.Method c, java.util.List<? extends com.sun.jdi.Value> d, int e) throws com.sun.jdi.InvalidTypeException, com.sun.jdi.ClassNotLoadedException, com.sun.jdi.IncompatibleThreadStateException, com.sun.jdi.InvocationException, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.newInstance(b, c, d, e);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static void setValue(com.sun.jdi.ClassType a, com.sun.jdi.Field b, com.sun.jdi.Value c) throws com.sun.jdi.InvalidTypeException, com.sun.jdi.ClassNotLoadedException, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.ClassNotPreparedExceptionWrapper {
        try {
            a.setValue(b, c);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        } catch (com.sun.jdi.ClassNotPreparedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.ClassNotPreparedExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.ClassType> subclasses0(com.sun.jdi.ClassType a) {
        try {
            return a.subclasses();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        }
    }

    public static java.util.List<com.sun.jdi.ClassType> subclasses(com.sun.jdi.ClassType a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.subclasses();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static com.sun.jdi.ClassType superclass(com.sun.jdi.ClassType a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.superclass();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

}
