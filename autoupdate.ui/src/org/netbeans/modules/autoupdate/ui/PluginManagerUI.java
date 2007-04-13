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

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import org.netbeans.api.autoupdate.UpdateManager;
import org.netbeans.api.autoupdate.UpdateUnit;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  Jiri Rechtacek
 */
public class PluginManagerUI extends javax.swing.JPanel implements UpdateUnitListener {
    private List<UpdateUnit> units = Collections.emptyList ();
    private UnitTable installedTable;
    private UnitTable availableTable;
    private UnitTable updateTable;
    private UnitTable localTable;
    private JButton closeButton;
    
    /** Creates new form PluginManagerUI */
    public PluginManagerUI (JButton closeButton) {
        this.closeButton = closeButton;
        initComponents();
        postInitComponents();
        RequestProcessor.getDefault().post(new Runnable () {
            public void run() {
                units = UpdateManager.getDefault().getUpdateUnits();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        refreshUnits();
                    }
                });
                
            }
        });        
    }
    
    void setProgressComponent (final JLabel title, final JComponent progressComponent) {
        if (SwingUtilities.isEventDispatchThread ()) {
            setProgressComponentInAwt (title, progressComponent);
        } else {
            SwingUtilities.invokeLater (new Runnable () {
                public void run () {
                    setProgressComponentInAwt (title, progressComponent);
                }
            });
        }
    }
    
    private void setProgressComponentInAwt (JLabel title, JComponent progressComponent) {
        assert pProgress != null;
        assert SwingUtilities.isEventDispatchThread () : "Must be called in EQ.";
        pProgress.setVisible (true);
        pProgress.add (title, BorderLayout.WEST);
        pProgress.add (progressComponent, BorderLayout.CENTER);
        revalidate ();
    }
    
    void unsetProgressComponent (final JLabel title, final JComponent progressComponent) {
        if (SwingUtilities.isEventDispatchThread ()) {
            unsetProgressComponentInAwt (title, progressComponent);
        } else {
            SwingUtilities.invokeLater (new Runnable () {
                public void run () {
                    unsetProgressComponentInAwt (title, progressComponent);
                }
            });
        }
    }
    
    private void unsetProgressComponentInAwt (JLabel title, JComponent progressComponent) {
        assert pProgress != null;
        assert SwingUtilities.isEventDispatchThread () : "Must be called in EQ.";
        pProgress.remove (title);
        pProgress.remove (progressComponent);
        pProgress.setVisible (false);
        revalidate ();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        tpTabs = new javax.swing.JTabbedPane();
        pProgress = new javax.swing.JPanel();
        bClose = closeButton;

        pProgress.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(bClose, org.openide.util.NbBundle.getMessage(PluginManagerUI.class, "UnitTab_bClose_Text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(pProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 359, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 324, Short.MAX_VALUE)
                        .add(bClose))
                    .add(tpTabs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(tpTabs, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 471, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(bClose)
                    .add(pProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bClose;
    private javax.swing.JPanel pProgress;
    private javax.swing.JTabbedPane tpTabs;
    // End of variables declaration//GEN-END:variables
    
    private void postInitComponents () {
        Containers.initNotify();        
        installedTable = new UnitTable (new InstalledTableModel (Utilities.makeInstalledCategories (units)));
        updateTable = new UnitTable (new UpdateTableModel (Utilities.makeUpdateCategories (units, false)));
        availableTable = new UnitTable (new AvailableTableModel (Utilities.makeAvailableCategories (units, false)));
        localTable = new UnitTable (new LocallyDownloadedTableModel (new ArrayList<UnitCategory> ()));
        selectFirstRow(installedTable);
        selectFirstRow(updateTable);
        selectFirstRow(availableTable);

        SplittedUnitTab updateTab = new SplittedUnitTab(updateTable, new UnitDetails (), this);
        updateTab.addUpdateUnitListener (this);
        tpTabs.add (NbBundle.getMessage(PluginManagerUI.class, "PluginManagerUI_UnitTab_Update_Title"), updateTab);
        
        SplittedUnitTab availableTab = new SplittedUnitTab(availableTable, new UnitDetails (), this);
        availableTab.addUpdateUnitListener (this);
        tpTabs.add (NbBundle.getMessage(PluginManagerUI.class, "PluginManagerUI_UnitTab_Available_Title"), availableTab);
                
        SplittedUnitTab localTab = new SplittedUnitTab(localTable, new UnitDetails (), this);
        localTab.addUpdateUnitListener (this);
        tpTabs.add (NbBundle.getMessage(PluginManagerUI.class, "PluginManagerUI_UnitTab_Local_Title"), localTab);
        
        SplittedUnitTab installedTab = new SplittedUnitTab(installedTable, new UnitDetails (), this);
        installedTab.addUpdateUnitListener (this);
        tpTabs.add (NbBundle.getMessage(PluginManagerUI.class, "PluginManagerUI_UnitTab_Installed_Title"), installedTab);
        SettingsTab st = new SettingsTab();
        tpTabs.add (st.getDisplayName(), st);
        
        decorateTitle (0, updateTable, NbBundle.getMessage (PluginManagerUI.class, "PluginManagerUI_UnitTab_Update_Title"));
        decorateTitle (1, availableTable, NbBundle.getMessage (PluginManagerUI.class, "PluginManagerUI_UnitTab_Available_Title"));
        decorateTitle (2, localTable, NbBundle.getMessage (PluginManagerUI.class, "PluginManagerUI_UnitTab_Local_Title"));
        decorateTitle (3, installedTable, NbBundle.getMessage (PluginManagerUI.class, "PluginManagerUI_UnitTab_Installed_Title"));
    }
    
    private void decorateTitle (int index, JTable table, String originalName) {
        TableModel model = table.getModel ();
        assert model instanceof UnitCategoryTableModel : model + " is instanceof UnitCategoryTableModel.";
        UnitCategoryTableModel catModel = (UnitCategoryTableModel) model;
        int count = catModel.getItemCount ();
        int rawCount = catModel.getRawItemCount ();        
        String countInfo = (count == rawCount) ? String.valueOf(rawCount) : (count + "/"+ rawCount);
        String newName = originalName + " (" + countInfo + ")";
        tpTabs.setTitleAt (index, count == 0 ? originalName : newName);
    }

    private int findRowWithFirstUnit(UnitCategoryTableModel model) {
        for (int row = 1; row <= model.getRowCount(); row++) {
            if (model.getUnitAtRow(row) != null) {
                return row;
            }
        }
        return -1;
    }
    
    private void selectFirstRow(UnitTable table) {
        if (table.getSelectedRow() == -1) {
            UnitCategoryTableModel model = (UnitCategoryTableModel)table.getModel();
            int row = findRowWithFirstUnit(model);
            if (row != -1) {
                table.getSelectionModel().setSelectionInterval(row, row);
            }
        }        
    }
    
    private void refreshUnits () {
        units = UpdateManager.getDefault ().getUpdateUnits ();
        UnitCategoryTableModel installTableModel = ((UnitCategoryTableModel)installedTable.getModel ());        
        UnitCategoryTableModel updateTableModel = ((UnitCategoryTableModel)updateTable.getModel ());        
        UnitCategoryTableModel availableTableModel = ((UnitCategoryTableModel)availableTable.getModel ());
        UnitCategoryTableModel localTableModel = ((UnitCategoryTableModel)localTable.getModel ());
        
        updateTableModel.setData (Utilities.makeUpdateCategories (units, false));        
        installTableModel.setData (Utilities.makeInstalledCategories (units));
        availableTableModel.setData (Utilities.makeAvailableCategories (units, false));
        //localTableModel.setData (new ArrayList<UnitCategory> ());
        
        selectFirstRow(installedTable);
        selectFirstRow(updateTable);
        selectFirstRow(availableTable);
        decorateTitle (0, updateTable, NbBundle.getMessage (PluginManagerUI.class, "PluginManagerUI_UnitTab_Update_Title"));
        decorateTitle (1, availableTable, NbBundle.getMessage (PluginManagerUI.class, "PluginManagerUI_UnitTab_Available_Title"));
        decorateTitle (2, localTable, NbBundle.getMessage (PluginManagerUI.class, "PluginManagerUI_UnitTab_Local_Title"));
        decorateTitle (3, installedTable, NbBundle.getMessage (PluginManagerUI.class, "PluginManagerUI_UnitTab_Installed_Title"));
    }
        
    
    static boolean canContinue (String message) {
        return NotifyDescriptor.YES_OPTION.equals (DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Confirmation (message)));
    }

    public void updateUnitsChanged() {
        refreshUnits ();
    }
    
    public void buttonsChanged () {
        Component c = tpTabs.getSelectedComponent ();
        if (c instanceof UnitTab) {
            ((UnitTab) c).refreshState ();
        }
    }
}
