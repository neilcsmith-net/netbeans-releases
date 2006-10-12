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

package org.netbeans.modules.j2ee.ddloaders.web.multiview;

import org.netbeans.modules.j2ee.dd.api.common.ResourceEnvRef;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.ddloaders.web.DDDataObject;
import org.netbeans.modules.xml.multiview.ui.DefaultTablePanel;
import org.netbeans.modules.xml.multiview.ui.EditDialog;
import org.netbeans.modules.xml.multiview.ui.SimpleDialogPanel;
import org.openide.util.NbBundle;

/** ResEnvRefsTablePanel - panel containing table for resource-env-references
 *
 * @author  mk115033
 * Created on April 11, 2005
 */
public class ResEnvRefsTablePanel extends DefaultTablePanel {
    private ResEnvRefTableModel model;
    private WebApp webApp;
    private DDDataObject dObj;
    
    /** Creates new form ContextParamPanel */
    public ResEnvRefsTablePanel(final DDDataObject dObj, final ResEnvRefTableModel model) {
    	super(model);
    	this.model=model;
        this.dObj=dObj;
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dObj.modelUpdatedFromUI();
                dObj.setChangedFromUI(true);
                int row = getTable().getSelectedRow();
                model.removeRow(row);
                dObj.setChangedFromUI(false);
            }
        });
        editButton.addActionListener(new TableActionListener(false));
        addButton.addActionListener(new TableActionListener(true));
    }

    void setModel(WebApp webApp, ResourceEnvRef[] params) {
        model.setData(webApp,params);
        this.webApp=webApp;
    }
    
    private class TableActionListener implements java.awt.event.ActionListener {
        private boolean add;
        TableActionListener(boolean add) {
            this.add=add;
        }
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            
            final int row = (add?-1:getTable().getSelectedRow());
            final ResEnvRefPanel dialogPanel = new ResEnvRefPanel();
            if (!add) {
                ResourceEnvRef resRef = model.getResourceEnvRef(row);
                dialogPanel.setResEnvRefName(resRef.getResourceEnvRefName());
                dialogPanel.setResEnvRefType(resRef.getResourceEnvRefType());
                dialogPanel.setDescription(resRef.getDefaultDescription());
            }
            EditDialog dialog = new EditDialog(dialogPanel,NbBundle.getMessage(ResEnvRefsTablePanel.class,"TTL_ResourceEnvRef"),add) {
                protected String validate() {
                    String name = dialogPanel.getResEnvRefName().trim();
                    String type = dialogPanel.getResEnvRefType().trim();
                    if (name.length()==0) {
                        return NbBundle.getMessage(ResEnvRefsTablePanel.class,"TXT_EmptyResRefName");
                    } else {
                        ResourceEnvRef[] params = webApp.getResourceEnvRef();
                        boolean exists=false;
                        for (int i=0;i<params.length;i++) {
                            if (row!=i && name.equals(params[i].getResourceEnvRefName())) {
                                exists=true;
                                break;
                            }
                        }
                        if (exists) {
                            return NbBundle.getMessage(ResEnvRefsTablePanel.class,"TXT_ResRefNameExists",name);
                        }
                    }
                    if (type.length()==0) {
                        return NbBundle.getMessage(ResEnvRefsTablePanel.class,"TXT_EmptyResTypeValue");
                    }
                    return null;
                }
            };
            
            if (add) dialog.setValid(false); // disable OK button
            javax.swing.event.DocumentListener docListener = new EditDialog.DocListener(dialog);
            dialogPanel.getNameTF().getDocument().addDocumentListener(docListener);
            dialogPanel.getTypeTF().getDocument().addDocumentListener(docListener);
            
            java.awt.Dialog d = org.openide.DialogDisplayer.getDefault().createDialog(dialog);
            d.setVisible(true);
            dialogPanel.getNameTF().getDocument().removeDocumentListener(docListener);
            dialogPanel.getTypeTF().getDocument().removeDocumentListener(docListener);
            
            if (dialog.getValue().equals(EditDialog.OK_OPTION)) {
                dObj.modelUpdatedFromUI();
                dObj.setChangedFromUI(true);
                String name = dialogPanel.getResEnvRefName().trim();
                String type = dialogPanel.getResEnvRefType().trim();
                String description = dialogPanel.getDescription();
                if (add) model.addRow(new String[]{name,type,description});
                else model.editRow(row,new String[]{name,type,description});
                dObj.setChangedFromUI(false);
            }
        }
    }
}
