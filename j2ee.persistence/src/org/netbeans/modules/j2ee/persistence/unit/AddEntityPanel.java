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

package org.netbeans.modules.j2ee.persistence.unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModelAction;
import org.netbeans.modules.j2ee.persistence.api.EntityClassScope;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.Entity;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.Entity;
import org.netbeans.modules.j2ee.persistence.api.metadata.orm.EntityMappingsMetadata;
import org.netbeans.modules.j2ee.persistence.util.MetadataModelReadHelper;
import org.netbeans.modules.j2ee.persistence.util.MetadataModelReadHelper.State;
import org.openide.util.Exceptions;

/**
 * Panel for adding entities to persistence unit.
 *
 * @author  Erno Mononen, Andrei Badea
 */
public class AddEntityPanel extends javax.swing.JPanel {
    
    private final MetadataModelReadHelper<EntityMappingsMetadata, List<String>> readHelper;
    
    public static AddEntityPanel create(EntityClassScope entityClassScope, Set<String> ignoreClassNames) {
        AddEntityPanel panel = new AddEntityPanel(entityClassScope, ignoreClassNames);
        panel.initialize();
        return panel;
    }
    
    private AddEntityPanel(EntityClassScope entityClassScope, final Set<String> ignoreClassNames) {
        initComponents();
        MetadataModel<EntityMappingsMetadata> model = entityClassScope.getEntityMappingsModel(true);
        readHelper = MetadataModelReadHelper.create(model, new MetadataModelAction<EntityMappingsMetadata, List<String>>() {
            public List<String> run(EntityMappingsMetadata metadata) {
                List<String> result = new ArrayList<String>();
                for (Entity entity : metadata.getRoot().getEntity()) {
                    String className = entity.getClass2();
                    if (!ignoreClassNames.contains(className)) {
                        result.add(className);
                    }
                }
                Collections.sort(result);
                return result;
            }
        });
    }
    
    private void initialize() {
        readHelper.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (readHelper.getState() == State.FINISHED) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                setEntityClassModel(readHelper.getResult());
                            } catch (ExecutionException e) {
                                Exceptions.printStackTrace(e);
                            }
                        }
                    });
                }
            }
        });
        readHelper.start();
    }
    
    private void setEntityClassModel(List<String> entityClassNames) {
        DefaultListModel model = new DefaultListModel();
        for (String each : entityClassNames) {
            model.addElement(each);
        }
        entityList.setModel(model);
    }
    
    /**
     * @return fully qualified names of the selected entities' classes.
     */
    public List<String> getSelectedEntityClasses(){
        List<String> result = new ArrayList<String>();
        for (Object elem : entityList.getSelectedValues()) {
            result.add((String)elem);
        }
        return result;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        entityList = new javax.swing.JList();

        jScrollPane1.setViewportView(entityList);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList entityList;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
}
