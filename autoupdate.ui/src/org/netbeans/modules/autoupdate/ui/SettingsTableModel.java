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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.autoupdate.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import org.netbeans.api.autoupdate.UpdateUnitProvider;
import org.netbeans.api.autoupdate.UpdateUnitProvider;
import org.netbeans.api.autoupdate.UpdateUnitProviderFactory;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Radek Matous
 */
public class SettingsTableModel extends AbstractTableModel {
    private static final String[] COLUMN_NAME_KEYS = new String[] {
        "SettingsTable_ActiveColumn",
        "SettingsTable_NameColumn",
        /*"SettingsTable_URLColumn"*/
    };
    
    private static final Class[] COLUMN_TYPES = new Class[] {
        Boolean.class,
        String.class,
        /*String.class*/
    };
    private List<UpdateUnitProvider> updateProviders;
    private Set<String> originalProviders;
    private String filter;
    private PluginManagerUI pluginManager = null;
    
    private final Logger logger = Logger.getLogger ("org.netbeans.modules.autoupdate.ui.SettingsTableModel");
    /** Creates a new instance of SettingsTableModel */
    public SettingsTableModel () {
        refreshModel ();
    }
    
    void setPluginManager (PluginManagerUI manager) {
        this.pluginManager = manager;
    }
    
    PluginManagerUI getPluginManager () {
        return pluginManager;
    }
    
    void setFilter (final String filter)  {
        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                synchronized(SettingsTableModel.class) {
                    SettingsTableModel.this.filter = filter.toLowerCase ();
                }
                refreshModel ();
            }
        });
        
    }
    
    void refreshModel () {
        Set<String> oldValue = originalProviders;
        originalProviders = new HashSet<String> ();
        final List<UpdateUnitProvider> forRefresh = new ArrayList<UpdateUnitProvider> ();
        List<UpdateUnitProvider> providers = UpdateUnitProviderFactory.getDefault ().getUpdateUnitProviders (false);
        for (UpdateUnitProvider p : providers) {
            if (oldValue != null && !oldValue.contains (p.getName ())) {
                // new one provider
                if (p.isEnabled ()) {
                    forRefresh.add (p);
                }
            }
            originalProviders.add (p.getName ());
        }
        if (! forRefresh.isEmpty ()) {
            RequestProcessor.getDefault ().post (new Runnable () {
                public void run () {
                    Utilities.presentRefreshProviders (forRefresh, getPluginManager (), true);
                    getPluginManager ().updateUnitsChanged ();
                }
            });
        }
        updateProviders = new ArrayList<UpdateUnitProvider> (providers);
        if (filter != null && filter.length () > 0) {
            for (Iterator<UpdateUnitProvider> it = updateProviders.iterator (); it.hasNext ();) {
                UpdateUnitProvider updateUnitProvider = it.next ();
                if (updateUnitProvider.getDisplayName ().toLowerCase ().indexOf (filter) == -1 ) {
                    it.remove ();
                }
            }
        }
        sortAlphabetically (updateProviders);
        fireTableDataChanged ();
    }
    
    public void remove (int rowIndex) {
        UpdateUnitProvider unitProvider = getUpdateUnitProvider (rowIndex);
        if (unitProvider != null) {
            UpdateUnitProviderFactory.getDefault ().remove (unitProvider);
        }
        getPluginManager ().updateUnitsChanged ();
    }
    
    public void add (String name, String displayName, URL url, boolean state) {
        final UpdateUnitProvider uup = UpdateUnitProviderFactory.getDefault ().create (name, displayName, url);
        uup.setEnable (state);
        if (state) {
            RequestProcessor.getDefault ().post (new Runnable () {
                public void run () {
                    Utilities.presentRefreshProvider (uup, getPluginManager (), true);
                    getPluginManager ().updateUnitsChanged ();
                }
            });
        }
    }
    
    public UpdateUnitProvider getUpdateUnitProvider (int rowIndex) {
        return (rowIndex >= 0 && rowIndex <  updateProviders.size ()) ? updateProviders.get (rowIndex) : null;
    }
    
    @Override
    public boolean isCellEditable (int rowIndex, int columnIndex) {
        return true;
    }
    
    public int getRowCount () {
        return updateProviders.size ();
    }
    
    public int getColumnCount () {
        return COLUMN_NAME_KEYS.length;
    }
    
    @Override
    public void setValueAt (Object aValue, int rowIndex, int columnIndex) {
        final UpdateUnitProvider unitProvider = getUpdateUnitProvider (rowIndex);
        switch(columnIndex) {
        case 0:
            boolean oldValue = unitProvider.isEnabled ();
            boolean newValue = ((Boolean) aValue).booleanValue ();
            if (oldValue != newValue) {
                unitProvider.setEnable (newValue);
                if (oldValue) {
                    RequestProcessor.getDefault ().post (new Runnable () {
                        public void run () {
                            // was enabled and won't be more -> remove it from model
                            getPluginManager ().updateUnitsChanged ();
                        }
                    });
                } else {
                    // was enabled and won't be more -> add it from model and read its content
                    RequestProcessor.getDefault ().post (new Runnable () {
                        public void run () {
                            Utilities.presentRefreshProvider (unitProvider, getPluginManager (), false);
                            getPluginManager ().updateUnitsChanged ();
                        }
                    });
                }
            }
            break;
        case 1: unitProvider.setDisplayName ((String)aValue);break;
            /*case 2: URL u = null;
                try {
                    u = new URL((String)aValue);
                    unitProvider.setProviderURL(u);break;
                } catch(MalformedURLException mux) {
                    logger.warning(mux.getLocalizedMessage());
                }*/
        };
    }
    
    public Object getValueAt (int rowIndex, int columnIndex) {
        Object retval = null;
        UpdateUnitProvider unitProvider = updateProviders.get (rowIndex);
        switch(columnIndex) {
        case 0: retval = unitProvider.isEnabled ();break;
        case 1: retval = unitProvider.getDisplayName ();break;
            /*case 2: URL url = unitProvider.getProviderURL();
            retval = (url != null) ? url.toExternalForm() : "";//NOI18N
            break;*/
        };
        return retval;
    }
    
    @Override
    public Class<?> getColumnClass (int columnIndex) {
        return COLUMN_TYPES[columnIndex];
    }
    
    @Override
    public String getColumnName (int columnIndex) {
        return NbBundle.getMessage (SettingsTableModel.class, COLUMN_NAME_KEYS[columnIndex]);
    }
    private static void sortAlphabetically (List<UpdateUnitProvider> res) {
        Collections.sort (res, new Comparator<UpdateUnitProvider>(){
            public int compare (UpdateUnitProvider arg0, UpdateUnitProvider arg1) {
                return arg0.getDisplayName ().compareTo (arg1.getDisplayName ());
            }
        });
        
    }
    
}
