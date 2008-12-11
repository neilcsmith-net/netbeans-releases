/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.db.explorer.action;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.lib.ddl.impl.DriverSpecification;
import org.netbeans.lib.ddl.impl.Specification;
import org.netbeans.modules.db.explorer.DatabaseConnection;
import org.netbeans.modules.db.explorer.DatabaseConnector;
import org.netbeans.modules.db.explorer.DbUtilities;
import org.netbeans.modules.db.explorer.dlg.AddIndexDialog;
import org.netbeans.modules.db.explorer.metadata.MetadataReader;
import org.netbeans.modules.db.explorer.node.IndexListNode;
import org.netbeans.modules.db.metadata.model.api.Catalog;
import org.netbeans.modules.db.metadata.model.api.Schema;
import org.netbeans.modules.db.metadata.model.api.Table;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author rob
 */
public class AddIndexAction extends BaseAction {
    private static final Logger LOGGER = Logger.getLogger(AddIndexAction.class .getName());

    public String getName() {
        return bundle().getString("AddIndex"); // NOI18N
    }


    @Override
    protected boolean enable(Node[] activatedNodes) {
        boolean enabled = false;

        if (activatedNodes.length == 1) {
            IndexListNode node = activatedNodes[0].getLookup().lookup(IndexListNode.class);
            enabled = node != null;
        }

        return enabled;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        final IndexListNode node = activatedNodes[0].getLookup().lookup(IndexListNode.class);
        RequestProcessor.getDefault().post(
            new Runnable() {
                public void run() {
                    perform(node);
                }
            }
        );
    }

    private void perform(final IndexListNode node) {
        try {
            DatabaseConnection dbConn = node.getLookup().lookup(DatabaseConnection.class);
            DatabaseConnector connector = dbConn.getConnector();

            Table table = node.getTable();
            final String tablename = table.getName();

            Schema schema = table.getParent();
            Catalog catalog = schema.getParent();

            String schemaName = MetadataReader.getSchemaWorkingName(schema);
            String catalogName = MetadataReader.getCatalogWorkingName(schema, catalog);

            Specification spec = connector.getDatabaseSpecification();

            final DriverSpecification drvSpec = connector.getDriverSpecification(catalogName);

            // List columns not present in current index
            Vector cols = new Vector(5);

            drvSpec.getColumns(tablename, "%");
            ResultSet rs = drvSpec.getResultSet();
            HashMap rset = new HashMap();
            while (rs.next()) {
                rset = drvSpec.getRow();
                cols.add((String) rset.get(new Integer(4)));
                rset.clear();
            }
            rs.close();

            if (cols.size() == 0)
                throw new Exception(bundle().getString("EXC_NoUsableColumnInPlace")); // NOI18N

            // Create and execute command
            final AddIndexDialog dlg = new AddIndexDialog(cols, spec, tablename, schemaName);
            dlg.setIndexName(tablename + "_idx"); // NOI18N
            if (dlg.run()) {
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        Node refreshNode = node.getParentNode();
                        if (refreshNode == null) {
                            refreshNode = node;
                        }

                        SystemAction.get(RefreshAction.class).performAction(new Node[] { refreshNode } );
                    }
                });
            }
        } catch(Exception exc) {
            LOGGER.log(Level.INFO, exc.getMessage(), exc);
            DbUtilities.reportError(bundle().getString("ERR_UnableToAddIndex"), exc.getMessage()); // NOI18N
        }
    }
}
