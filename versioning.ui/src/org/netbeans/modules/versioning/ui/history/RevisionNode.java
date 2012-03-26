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
package org.netbeans.modules.versioning.ui.history;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.*;
import java.util.logging.Level;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;
import org.netbeans.swing.etable.QuickFilter;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Stupka
 *
 */
public class RevisionNode extends AbstractNode implements Comparable {
    
    static final String PROPERTY_NAME_LABEL = "label";                          // NOI18N        
    static final String PROPERTY_NAME_USER = "user";                            // NOI18N        
    static final String PROPERTY_NAME_VERSION = "version";                      // NOI18N                       

    private HistoryEntry entry; 
    private static DateFormat dateFormat = DateFormat.getDateTimeInstance();                      
    private static DateFormat timeFormat = DateFormat.getTimeInstance();

    private RevisionNode(HistoryEntry entry, Lookup l) {                
        super(createChildren(entry), l);                        
        this.entry = entry;
        initProperties();
    }        
         
    static RevisionNode create(HistoryEntry entry) {
        return new RevisionNode(entry, Lookups.fixed(new Object [] { entry }));
    }
    
    private static Children createChildren(HistoryEntry entry) {
        if(entry.getFiles().length == 1) {
            return Children.LEAF;
        } else {
            FileNode[] nodes = new FileNode[entry.getFiles().length];
            int i = 0;
            for (VCSFileProxy file : entry.getFiles()) {
                nodes[i++] = new FileNode(entry, file);            
            }
            Children.SortedArray children = new Children.SortedArray();            
            children.add(nodes);
            return children;        
        }
    }
        
    private void initProperties() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set ps = Sheet.createPropertiesSet();
                
        ps.put(new RevisionProperty()); // XXX show only if VCS available
        ps.put(new UserProperty()); 
        ps.put(entry.canEdit() ? new EditableMessageProperty() : new MessageProperty());
        
        sheet.put(ps);
        setSheet(sheet);        
    }   
    
    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getName() {                
        return getFormatedDate(entry);
    }    
       
    static String getFormatedDate(HistoryEntry se)  {
        int day = getDay(se.getDateTime().getTime());
        switch(day) {
            case 0:  return NbBundle.getMessage(RevisionNode.class, "LBL_Today", new Object[] {timeFormat.format(se.getDateTime())});   
            case 1:  return NbBundle.getMessage(RevisionNode.class, "LBL_Yesterday", new Object[] {timeFormat.format(se.getDateTime())});
            default: return dateFormat.format(se.getDateTime());
        }
    }
    
    private static int getDay(long ts) {
        Date date = new Date(ts);
                
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());        
        
        // set the cal at today midnight
        int todayMillis = c.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000 +
                          c.get(Calendar.MINUTE)      * 60 * 1000 + 
                          c.get(Calendar.SECOND)      * 1000 + 
                          c.get(Calendar.MILLISECOND);                
        c.add(Calendar.MILLISECOND, -1 * todayMillis);                        
        
        if(c.getTime().compareTo(date) < 0) {
            return 0;
        }
        
        return (int) ( (c.getTimeInMillis() - ts) / (24 * 60 * 60 * 1000) ) + 1;
                
    }    
    @Override
    public Action[] getActions(boolean context) {
        return entry.getActions();          
    }

    @Override
    public int compareTo(Object obj) {
        if( !(obj instanceof RevisionNode) || obj == null) {
            return -1;
        }
        RevisionNode node = (RevisionNode) obj;
        return node.entry.getDateTime().compareTo(entry.getDateTime());
    }
    
    class MessageProperty extends PropertySupport.ReadOnly<TableEntry> {
        private TableEntry te;
        public MessageProperty() {
            super(PROPERTY_NAME_LABEL, TableEntry.class, NbBundle.getMessage(RevisionNode.class, "LBL_LabelProperty_Name"), NbBundle.getMessage(RevisionNode.class, "LBL_LabelProperty_Desc"));
            te = new TableEntry() {
                @Override
                public String getDisplayValue() {
                    return entry.getMessage();
                }
                @Override
                public String getTooltip() {
                    return entry.getMessage();
                }
            };   
        }
        @Override
        public TableEntry getValue() throws IllegalAccessException, InvocationTargetException {
            return te;
        }

        @Override
        public String toString() {
            return entry.getMessage();
        }
    }
    
    class EditableMessageProperty extends PropertySupport.ReadWrite<TableEntry> {
        private TableEntry te;
        public EditableMessageProperty() {
            super(PROPERTY_NAME_LABEL, TableEntry.class, NbBundle.getMessage(RevisionNode.class, "LBL_LabelProperty_Name"), NbBundle.getMessage(RevisionNode.class, "LBL_LabelProperty_Desc"));
            te = new TableEntry() {
                @Override
                public String getDisplayValue() {
                    return entry.getMessage();
                }
                @Override
                public String getTooltip() {
                    return entry.getMessage();
                }
            };   
        }
        @Override
        public TableEntry getValue() throws IllegalAccessException, InvocationTargetException {
            return te;
        }    
        @Override        
        public void setValue(TableEntry te) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
        {        
            this.te = te;
        }        
        @Override
        public PropertyEditor getPropertyEditor() {
            return new PropertyEditorSupport() {
                @Override
                public void setAsText(String text) throws IllegalArgumentException {
                    if (text instanceof String) {
                        try {
                            entry.setMessage(!text.equals("") ? text : null);
                        } catch (IOException ex) {
                            History.LOG.log(Level.WARNING, null, ex);
                        }
                        return;
                    }
                    throw new java.lang.IllegalArgumentException(text);
                }
                @Override
                public String getAsText() {
                    return te.getDisplayValue();
                }
            };
        }           
        
        @Override
        public String toString() {
            return entry.getMessage();
        }
    }                      
    
    class UserProperty extends PropertySupport.ReadOnly<TableEntry> {
        private TableEntry te;
        public UserProperty() {
            super(PROPERTY_NAME_USER, TableEntry.class, NbBundle.getMessage(RevisionNode.class, "LBL_UserProperty_Name"), NbBundle.getMessage(RevisionNode.class, "LBL_UserProperty_Desc"));
            te = new TableEntry() {
                @Override
                public String getDisplayValue() {
                    return entry.getUsernameShort();
                }
                @Override
                public String getTooltip() {
                    return entry.getUsername();
                }
            };            
        }
        @Override
        public TableEntry getValue() throws IllegalAccessException, InvocationTargetException {
            return te;
        }

        @Override
        public String toString() {
            return entry.getUsername();
        }
    }        
    
    class RevisionProperty extends PropertySupport.ReadOnly<TableEntry> {
        private TableEntry te;
        public RevisionProperty() {
            super(PROPERTY_NAME_VERSION, TableEntry.class, NbBundle.getMessage(RevisionNode.class, "LBL_VersionProperty_Name"), NbBundle.getMessage(RevisionNode.class, "LBL_VersionProperty_Desc"));
            te = new TableEntry() {
                @Override
                public String getDisplayValue() {
                    return entry.getRevisionShort();
                }
                @Override
                public String getTooltip() {
                    return entry.getRevision();
                }
                @Override
                public int compareTo(TableEntry e) {
                    if(e == null) return 1;
                    Integer i1;
                    Integer i2;
                    try {
                        i1 = Integer.parseInt(getDisplayValue());
                        i2 = Integer.parseInt(e.getDisplayValue());
                        return i1.compareTo(i2);
                    } catch (NumberFormatException ex) {}
                    return super.compareTo(e);
                }
            };
        }
        @Override
        public TableEntry getValue() throws IllegalAccessException, InvocationTargetException {
            return te;
        }

        @Override
        public String toString() {
            return entry.getRevision();
        }
    } 

    private static class FileNode extends AbstractNode implements Comparable {        

        private final HistoryEntry entry;
        private final VCSFileProxy file;
        
        FileNode(HistoryEntry entry, VCSFileProxy file) {
            super(Children.LEAF, Lookups.fixed(new Object [] { file, entry }));                        
            this.entry = entry;
            this.file = file;
        }
    
        @Override
        public Action[] getActions(boolean context) {
            return entry.getActions();       
        }

        @Override
        public String getName() {
            return file.getName(); 
        }  
        
        @Override
        public int compareTo(Object obj) {
            if( !(obj instanceof FileNode) || obj == null) {
                return -1;
            }
            FileNode node = (FileNode) obj;        
            return getName().compareTo(node.getName());            
        }        
    }    
    
    public static abstract class Filter implements QuickFilter {
        public boolean filtersProperty(Property property) {
            return false;
        }
        public abstract String getDisplayName();
        protected HistoryEntry getEntry(Object value) {
            if(value instanceof Node) {
                return getHistoryEntry((Node)value);
        }
            return null;
        }
 
        private HistoryEntry getHistoryEntry(Node node) {
            if(node instanceof RevisionNode) {
                return ((RevisionNode)node).entry;
            } else if (node instanceof FileNode) {
                return ((FileNode)node).entry;
            } else {
                Node[] nodes = node.getChildren().getNodes();
                return nodes != null && nodes.length > 0 ? getHistoryEntry(nodes[0]) : null;
            }
        }
        
        public String getRendererValue(String value) {
            return HistoryUtils.escapeForHTMLLabel(value);
        }

        @Override
        public String toString() {
            return getDisplayName();
        }
    }

}

