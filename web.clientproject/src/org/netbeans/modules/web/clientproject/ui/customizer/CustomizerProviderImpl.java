/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.clientproject.ui.customizer;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.web.clientproject.ClientSideProject;
import org.netbeans.modules.web.clientproject.util.ClientSideProjectUtilities;
import org.netbeans.spi.project.ui.CustomizerProvider2;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.windows.WindowManager;


/**
 * @author Jan Becicka
 */
public class CustomizerProviderImpl implements CustomizerProvider2 {

    static final Logger LOGGER = Logger.getLogger(CustomizerProviderImpl.class.getName());

    static final String CUSTOMIZER_FOLDER_PATH = "Projects/org.netbeans.modules.web.clientproject/Customizer"; // NOI18N
    // @GuardedBy("EDT")
    static final Map<Project, Dialog> PROJECT_2_DIALOG = new HashMap<Project, Dialog>();

    final ClientSideProject project;


    public CustomizerProviderImpl(ClientSideProject project) {
        this.project = project;
    }

    @Override
    public void showCustomizer() {
        showCustomizer(null);
    }


    public void showCustomizer(String preselectedCategory) {
        showCustomizer(preselectedCategory, null);
    }

    @NbBundle.Messages({
        "# {0} - project name",
        "CustomizerProviderImpl.title=Project Properties - {0}"
    })
    @Override
    public void showCustomizer(final String preselectedCategory, final String preselectedSubCategory) {
        Mutex.EVENT.readAccess(new Runnable() {
            @Override
            public void run() {
                assert EventQueue.isDispatchThread();
                Dialog dialog = PROJECT_2_DIALOG.get(project);
                if (dialog != null) {
                    dialog.setVisible(true);
                    return;
                }
                try {
                    WaitCursor.show();
                    ClientSideProjectProperties uiProperties = new ClientSideProjectProperties(project);
                    Lookup context = Lookups.fixed(new Object[] {
                        project,
                        uiProperties,
                        new SubCategoryProvider(preselectedCategory, preselectedSubCategory)
                    });

                    OptionListener optionListener = new OptionListener(project);
                    StoreListener storeListener = new StoreListener(uiProperties);
                    dialog = ProjectCustomizer.createCustomizerDialog(CUSTOMIZER_FOLDER_PATH, context, preselectedCategory, optionListener, storeListener, null);
                    dialog.addWindowListener(optionListener);
                    dialog.setTitle(Bundle.CustomizerProviderImpl_title(ProjectUtils.getInformation(project).getDisplayName()));

                    PROJECT_2_DIALOG.put(project, dialog);
                } finally {
                    WaitCursor.hide();
                }
                dialog.setVisible(true);
            }
        });
    }

    //~ Inner classes

    private static final class StoreListener implements ActionListener {

        private final ClientSideProjectProperties uiProperties;


        StoreListener(ClientSideProjectProperties uiProperties) {
            this.uiProperties = uiProperties;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            assert !EventQueue.isDispatchThread();
            uiProperties.save();
            ClientSideProjectProperties.ProjectServer server = uiProperties.getProjectServer();
            ClientSideProjectUtilities.logUsage(CustomizerProviderImpl.class, "USG_PROJECT_HTML5_CONFIGURE", // NOI18N
                    new Object[] { "", // NOI18N // This used to be a list of newly added libraries
                        ClientSideProjectProperties.ProjectServer.INTERNAL.equals(server) ? "EMBEDDED" : "EXTERNAL", // NOI18N
                        ClientSideProjectProperties.ProjectServer.INTERNAL.equals(server) ? (uiProperties.getWebRoot().length() > 1 ? "YES" : "NO") : "" // NOI18N
                });
        }

    }

    private static class OptionListener extends WindowAdapter implements ActionListener {

        private final Project project;


        OptionListener(Project project) {
            this.project = project;
        }

        // listening to OK button
        @Override
        public void actionPerformed(ActionEvent e) {
            // close & dispose the the dialog
            Dialog dialog = PROJECT_2_DIALOG.get(project);
            if (dialog != null) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        }

        // listening to window events
        @Override
        public void windowClosed(WindowEvent e) {
            PROJECT_2_DIALOG.remove(project);
        }

        @Override
        public void windowClosing(WindowEvent e) {
            // dispose the dialog otherwise {@link WindowAdapter#windowClosed} may not be called
            Dialog dialog = PROJECT_2_DIALOG.get(project);
            if (dialog != null) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        }

    }

    static final class SubCategoryProvider {

        private final String subcategory;
        private final String category;


        SubCategoryProvider(String category, String subcategory) {
            this.category = category;
            this.subcategory = subcategory;
        }

        public String getCategory() {
            return category;
        }

        public String getSubcategory() {
            return subcategory;
        }

    }

    private static final class WaitCursor implements Runnable {

        // @GuardedBy("EDT")
        private boolean show;


        private WaitCursor(boolean show) {
            assert EventQueue.isDispatchThread();
            this.show = show;
        }

        public static void show() {
            invoke(new WaitCursor(true));
        }

        public static void hide() {
            invoke(new WaitCursor(false));
        }

        private static void invoke(WaitCursor wc) {
            Mutex.EVENT.readAccess(wc);
        }

        @Override
        public void run() {
            assert EventQueue.isDispatchThread();
            try {
                JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
                Component component = frame.getGlassPane();
                component.setVisible(show);
                component.setCursor(show ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null);
            } catch (NullPointerException npe) {
                LOGGER.log(Level.WARNING, null, npe);
            }
        }

    }

}
