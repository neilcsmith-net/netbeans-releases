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
 * Wrapper for Method JDI class.
 * Use methods of this class instead of direct calls on JDI objects.
 * These methods assure that exceptions thrown from JDI calls are handled appropriately.
 *
 * @author Martin Entlicher
 */
public final class MethodWrapper {

    private MethodWrapper() {}

    public static java.util.List<com.sun.jdi.Location> allLineLocations0(com.sun.jdi.Method a) throws com.sun.jdi.AbsentInformationException {
        try {
            return a.allLineLocations();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        }
    }

    public static java.util.List<com.sun.jdi.Location> allLineLocations(com.sun.jdi.Method a) throws com.sun.jdi.AbsentInformationException, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.allLineLocations();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.Location> allLineLocations0(com.sun.jdi.Method a, java.lang.String b, java.lang.String c) throws com.sun.jdi.AbsentInformationException {
        try {
            return a.allLineLocations(b, c);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        }
    }

    public static java.util.List<com.sun.jdi.Location> allLineLocations(com.sun.jdi.Method a, java.lang.String b, java.lang.String c) throws com.sun.jdi.AbsentInformationException, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.allLineLocations(b, c);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static java.util.List<java.lang.String> argumentTypeNames0(com.sun.jdi.Method a) {
        try {
            return a.argumentTypeNames();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        }
    }

    public static java.util.List<java.lang.String> argumentTypeNames(com.sun.jdi.Method a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.argumentTypeNames();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.Type> argumentTypes0(com.sun.jdi.Method a) throws com.sun.jdi.ClassNotLoadedException {
        try {
            return a.argumentTypes();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        }
    }

    public static java.util.List<com.sun.jdi.Type> argumentTypes(com.sun.jdi.Method a) throws com.sun.jdi.ClassNotLoadedException, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.argumentTypes();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.LocalVariable> arguments0(com.sun.jdi.Method a) throws com.sun.jdi.AbsentInformationException {
        try {
            return a.arguments();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        }
    }

    public static java.util.List<com.sun.jdi.LocalVariable> arguments(com.sun.jdi.Method a) throws com.sun.jdi.AbsentInformationException, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.arguments();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static byte[] bytecodes(com.sun.jdi.Method a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.bytecodes();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static boolean equals0(com.sun.jdi.Method a, java.lang.Object b) {
        try {
            return a.equals(b);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return false;
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return false;
        }
    }

    public static boolean equals(com.sun.jdi.Method a, java.lang.Object b) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.equals(b);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static int hashCode0(com.sun.jdi.Method a) {
        try {
            return a.hashCode();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return 0;
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return 0;
        }
    }

    public static int hashCode(com.sun.jdi.Method a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.hashCode();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static boolean isAbstract0(com.sun.jdi.Method a) {
        try {
            return a.isAbstract();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return false;
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return false;
        }
    }

    public static boolean isAbstract(com.sun.jdi.Method a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.isAbstract();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static boolean isBridge0(com.sun.jdi.Method a) {
        try {
            return a.isBridge();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return false;
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return false;
        }
    }

    public static boolean isBridge(com.sun.jdi.Method a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.isBridge();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static boolean isConstructor0(com.sun.jdi.Method a) {
        try {
            return a.isConstructor();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return false;
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return false;
        }
    }

    public static boolean isConstructor(com.sun.jdi.Method a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.isConstructor();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static boolean isNative0(com.sun.jdi.Method a) {
        try {
            return a.isNative();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return false;
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return false;
        }
    }

    public static boolean isNative(com.sun.jdi.Method a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.isNative();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static boolean isObsolete0(com.sun.jdi.Method a) {
        try {
            return a.isObsolete();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return false;
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return false;
        }
    }

    public static boolean isObsolete(com.sun.jdi.Method a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.isObsolete();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static boolean isStaticInitializer0(com.sun.jdi.Method a) {
        try {
            return a.isStaticInitializer();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return false;
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return false;
        }
    }

    public static boolean isStaticInitializer(com.sun.jdi.Method a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.isStaticInitializer();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static boolean isSynchronized0(com.sun.jdi.Method a) {
        try {
            return a.isSynchronized();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return false;
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return false;
        }
    }

    public static boolean isSynchronized(com.sun.jdi.Method a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.isSynchronized();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static boolean isVarArgs0(com.sun.jdi.Method a) {
        try {
            return a.isVarArgs();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return false;
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return false;
        }
    }

    public static boolean isVarArgs(com.sun.jdi.Method a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.isVarArgs();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static com.sun.jdi.Location location(com.sun.jdi.Method a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.location();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static com.sun.jdi.Location locationOfCodeIndex(com.sun.jdi.Method a, long b) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.locationOfCodeIndex(b);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.Location> locationsOfLine0(com.sun.jdi.Method a, int b) throws com.sun.jdi.AbsentInformationException {
        try {
            return a.locationsOfLine(b);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        }
    }

    public static java.util.List<com.sun.jdi.Location> locationsOfLine(com.sun.jdi.Method a, int b) throws com.sun.jdi.AbsentInformationException, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.locationsOfLine(b);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.Location> locationsOfLine0(com.sun.jdi.Method a, java.lang.String b, java.lang.String c, int d) throws com.sun.jdi.AbsentInformationException {
        try {
            return a.locationsOfLine(b, c, d);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        }
    }

    public static java.util.List<com.sun.jdi.Location> locationsOfLine(com.sun.jdi.Method a, java.lang.String b, java.lang.String c, int d) throws com.sun.jdi.AbsentInformationException, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.locationsOfLine(b, c, d);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static com.sun.jdi.Type returnType(com.sun.jdi.Method a) throws com.sun.jdi.ClassNotLoadedException, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.returnType();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static java.lang.String returnTypeName(com.sun.jdi.Method a) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.returnTypeName();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.LocalVariable> variables0(com.sun.jdi.Method a) throws com.sun.jdi.AbsentInformationException {
        try {
            return a.variables();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        }
    }

    public static java.util.List<com.sun.jdi.LocalVariable> variables(com.sun.jdi.Method a) throws com.sun.jdi.AbsentInformationException, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.variables();
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

    public static java.util.List<com.sun.jdi.LocalVariable> variablesByName0(com.sun.jdi.Method a, java.lang.String b) throws com.sun.jdi.AbsentInformationException {
        try {
            return a.variablesByName(b);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            return java.util.Collections.emptyList();
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            return java.util.Collections.emptyList();
        }
    }

    public static java.util.List<com.sun.jdi.LocalVariable> variablesByName(com.sun.jdi.Method a, java.lang.String b) throws com.sun.jdi.AbsentInformationException, org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            return a.variablesByName(b);
        } catch (com.sun.jdi.VMDisconnectedException ex) {
            throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper(ex);
        } catch (com.sun.jdi.InternalException ex) {
            org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report(ex);
            throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper(ex);
        }
    }

}
