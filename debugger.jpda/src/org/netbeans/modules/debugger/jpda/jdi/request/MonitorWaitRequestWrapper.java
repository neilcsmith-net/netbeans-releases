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

package org.netbeans.modules.debugger.jpda.jdi.request;

// DO NOT MODIFY THIS CODE, GENERATED AUTOMATICALLY
// Generated by org.netbeans.modules.debugger.jpda.jdi.Generate class.

/**
 * Wrapper for MonitorWaitRequest JDI class from JDK 1.6.
 * Use methods of this class instead of direct calls on JDI objects.
 * These methods assure that exceptions thrown from JDI calls are handled appropriately.
 *
 * @author Martin Entlicher
 */
public final class MonitorWaitRequestWrapper {

    private MonitorWaitRequestWrapper() {}

    /** Wrapper for method addClassExclusionFilter from JDK 1.6. */
    public static void addClassExclusionFilter(Object/*com.sun.jdi.request.MonitorWaitRequest*/ a, java.lang.String b) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            a.getClass().getMethod("addClassExclusionFilter", java.lang.String.class).invoke(a, b);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        } catch (SecurityException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (t instanceof com.sun.jdi.VMDisconnectedException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper((com.sun.jdi.VMDisconnectedException) t);
            }
            if (t instanceof com.sun.jdi.InternalException) {
                org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report((com.sun.jdi.InternalException) t);
                throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper((com.sun.jdi.InternalException) t);
            }
            throw new IllegalStateException(t);
        }
    }

    /** Wrapper for method addClassFilter from JDK 1.6. */
    public static void addClassFilter(Object/*com.sun.jdi.request.MonitorWaitRequest*/ a, com.sun.jdi.ReferenceType b) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            a.getClass().getMethod("addClassFilter", com.sun.jdi.ReferenceType.class).invoke(a, b);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        } catch (SecurityException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (t instanceof com.sun.jdi.VMDisconnectedException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper((com.sun.jdi.VMDisconnectedException) t);
            }
            if (t instanceof com.sun.jdi.InternalException) {
                org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report((com.sun.jdi.InternalException) t);
                throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper((com.sun.jdi.InternalException) t);
            }
            throw new IllegalStateException(t);
        }
    }

    /** Wrapper for method addClassFilter from JDK 1.6. */
    public static void addClassFilter(Object/*com.sun.jdi.request.MonitorWaitRequest*/ a, java.lang.String b) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            a.getClass().getMethod("addClassFilter", java.lang.String.class).invoke(a, b);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        } catch (SecurityException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (t instanceof com.sun.jdi.VMDisconnectedException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper((com.sun.jdi.VMDisconnectedException) t);
            }
            if (t instanceof com.sun.jdi.InternalException) {
                org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report((com.sun.jdi.InternalException) t);
                throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper((com.sun.jdi.InternalException) t);
            }
            throw new IllegalStateException(t);
        }
    }

    /** Wrapper for method addInstanceFilter from JDK 1.6. */
    public static void addInstanceFilter(Object/*com.sun.jdi.request.MonitorWaitRequest*/ a, com.sun.jdi.ObjectReference b) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            a.getClass().getMethod("addInstanceFilter", com.sun.jdi.ObjectReference.class).invoke(a, b);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        } catch (SecurityException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (t instanceof com.sun.jdi.VMDisconnectedException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper((com.sun.jdi.VMDisconnectedException) t);
            }
            if (t instanceof com.sun.jdi.InternalException) {
                org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report((com.sun.jdi.InternalException) t);
                throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper((com.sun.jdi.InternalException) t);
            }
            throw new IllegalStateException(t);
        }
    }

    /** Wrapper for method addThreadFilter from JDK 1.6. */
    public static void addThreadFilter(Object/*com.sun.jdi.request.MonitorWaitRequest*/ a, com.sun.jdi.ThreadReference b) throws org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper, org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper {
        try {
            a.getClass().getMethod("addThreadFilter", com.sun.jdi.ThreadReference.class).invoke(a, b);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        } catch (SecurityException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (t instanceof com.sun.jdi.VMDisconnectedException) {
                throw new org.netbeans.modules.debugger.jpda.jdi.VMDisconnectedExceptionWrapper((com.sun.jdi.VMDisconnectedException) t);
            }
            if (t instanceof com.sun.jdi.InternalException) {
                org.netbeans.modules.debugger.jpda.JDIExceptionReporter.report((com.sun.jdi.InternalException) t);
                throw new org.netbeans.modules.debugger.jpda.jdi.InternalExceptionWrapper((com.sun.jdi.InternalException) t);
            }
            throw new IllegalStateException(t);
        }
    }

}
