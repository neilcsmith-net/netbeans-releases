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

package org.netbeans.modules.debugger.jpda.ui.models;

import java.util.WeakHashMap;
import javax.security.auth.Refreshable;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.InvalidExpressionException;
import org.netbeans.api.debugger.jpda.JPDAClassType;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.JPDAWatch;
import org.netbeans.api.debugger.jpda.LocalVariable;
import org.netbeans.api.debugger.jpda.ObjectVariable;
import org.netbeans.api.debugger.jpda.Super;
import org.netbeans.api.debugger.jpda.This;
import org.netbeans.api.debugger.jpda.Variable;
import org.netbeans.spi.debugger.ContextProvider;
import org.netbeans.spi.debugger.jpda.EditorContext.Operation;
import org.netbeans.spi.debugger.ui.Constants;
import org.netbeans.spi.viewmodel.TableModel;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;


/**
 *
 * @author   Jan Jancura
 */
public class VariablesTableModel implements TableModel, Constants {
    
    private JPDADebugger debugger;

    public VariablesTableModel(ContextProvider contextProvider) {
        debugger = contextProvider.lookupFirst(null, JPDADebugger.class);
    }
    
    public Object getValueAt (Object row, String columnID) throws 
    UnknownTypeException {
        
        if ( LOCALS_TO_STRING_COLUMN_ID.equals (columnID) ||
             WATCH_TO_STRING_COLUMN_ID.equals (columnID)
        ) {
            if (row instanceof Super)
                return "";
            else

            if (row instanceof ObjectVariable)
                try {
                    return ((ObjectVariable) row).getToStringValue ();
                } catch (InvalidExpressionException ex) {
                    return getMessage (ex);
                }
            else
            if (row instanceof Variable)
                return ((Variable) row).getValue ();
            if (row instanceof Operation ||
                row == "lastOperations" || // NOI18N
                row instanceof String && ((String) row).startsWith("operationArguments ")) { // NOI18N
                
                return ""; // NOI18N
            }
        } else
        if ( LOCALS_TYPE_COLUMN_ID.equals (columnID) ||
             WATCH_TYPE_COLUMN_ID.equals (columnID)
        ) {
            if (row instanceof Variable)
                return getShort (((Variable) row).getType ());
            if (row instanceof javax.swing.JToolTip) {
                row = ((javax.swing.JToolTip) row).getClientProperty("getShortDescription");
                if (row instanceof Variable) {
                    if (row instanceof Refreshable && !((Refreshable) row).isCurrent()) {
                        return "";
                    }
                    return ((Variable) row).getType();
                }
            }
        } else
        if ( LOCALS_VALUE_COLUMN_ID.equals (columnID) ||
             WATCH_VALUE_COLUMN_ID.equals (columnID)
        ) {
            if (row instanceof JPDAWatch) {
                JPDAWatch w = (JPDAWatch) row;
                String e = w.getExceptionDescription ();
                if (e != null)
                    return ">" + e + "<";
                return w.getValue ();
            } else 
            if (row instanceof Variable)
                return ((Variable) row).getValue ();
        }
        if (row instanceof JPDAClassType) {
            return ""; // NOI18N
        }
        if (row.toString().startsWith("SubArray")) { // NOI18N
            return ""; // NOI18N
        }
        if (row instanceof Operation ||
            row == "lastOperations" || // NOI18N
            row instanceof String && ((String) row).startsWith("operationArguments ")) { // NOI18N

            return ""; // NOI18N
        }
        throw new UnknownTypeException (row);
    }
    
    public boolean isReadOnly (Object row, String columnID) throws 
    UnknownTypeException {
        if (row instanceof Variable) {
            if ( LOCALS_TO_STRING_COLUMN_ID.equals (columnID) ||
                 WATCH_TO_STRING_COLUMN_ID.equals (columnID) ||
                 LOCALS_TYPE_COLUMN_ID.equals (columnID) ||
                 WATCH_TYPE_COLUMN_ID.equals (columnID)
            ) return true;
            if ( LOCALS_VALUE_COLUMN_ID.equals (columnID) ||
                 WATCH_VALUE_COLUMN_ID.equals (columnID) 
            ) {
                if (row instanceof This)
                    return true;
                else
                if ( row instanceof LocalVariable ||
                     row instanceof Field ||
                     row instanceof JPDAWatch
                ) {
                    if (WatchesNodeModel.isEmptyWatch(row)) {
                        return true;
                    } else {
                        return !debugger.canBeModified();
                    }
                } else {
                    return true;
                }
            }
        }
        if (row instanceof JPDAClassType) {
            return true;
        }
        if (row.toString().startsWith("SubArray")) {
            return true;
        }
        if (row instanceof Operation) {
            return true;
        }
        throw new UnknownTypeException (row);
    }
    
    public void setValueAt (Object row, String columnID, Object value) 
    throws UnknownTypeException {
        if (row instanceof LocalVariable) {
            if (LOCALS_VALUE_COLUMN_ID.equals (columnID)) {
                try {
                    ((LocalVariable) row).setValue ((String) value);
                } catch (InvalidExpressionException e) {
                    NotifyDescriptor.Message descriptor = 
                        new NotifyDescriptor.Message (
                            e.getLocalizedMessage (), 
                            NotifyDescriptor.WARNING_MESSAGE
                        );
                    DialogDisplayer.getDefault ().notify (descriptor);
                }
                return;
            }
        }
        if (row instanceof Field) {
            if ( LOCALS_VALUE_COLUMN_ID.equals (columnID) ||
                 WATCH_VALUE_COLUMN_ID.equals (columnID)
            ) {
                try {
                    ((Field) row).setValue ((String) value);
                } catch (InvalidExpressionException e) {
                    NotifyDescriptor.Message descriptor = 
                        new NotifyDescriptor.Message (
                            e.getLocalizedMessage (), 
                            NotifyDescriptor.WARNING_MESSAGE
                        );
                    DialogDisplayer.getDefault ().notify (descriptor);
                }
                return;
            }
        }
        if (row instanceof JPDAWatch) {
            if ( LOCALS_VALUE_COLUMN_ID.equals (columnID) ||
                 WATCH_VALUE_COLUMN_ID.equals (columnID)
            ) {
                try {
                    ((JPDAWatch) row).setValue ((String) value);
                } catch (InvalidExpressionException e) {
                    NotifyDescriptor.Message descriptor = 
                        new NotifyDescriptor.Message (
                            e.getLocalizedMessage (), 
                            NotifyDescriptor.WARNING_MESSAGE
                        );
                    DialogDisplayer.getDefault ().notify (descriptor);
                }
                return;
            }
        }
        throw new UnknownTypeException (row);
    }
    
    /** 
     * Registers given listener.
     * 
     * @param l the listener to add
     */
    public void addModelListener (ModelListener l) {
    }

    /** 
     * Unregisters given listener.
     *
     * @param l the listener to remove
     */
    public void removeModelListener (ModelListener l) {
    }
    
    static String getShort (String c) {
        int i = c.lastIndexOf ('.');
        if (i < 0) return c;
        return c.substring (i + 1);
    }
    
    private static String getMessage (InvalidExpressionException e) {
        String m = e.getLocalizedMessage ();
        if (m == null) {
            m = e.getMessage ();
        }
        if (m == null) {
            m = NbBundle.getMessage(VariablesTableModel.class, "MSG_NA");
        }
        return ">" + m + "<";
    }
}
