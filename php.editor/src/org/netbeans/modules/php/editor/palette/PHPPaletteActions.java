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
package org.netbeans.modules.php.editor.palette;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.Utilities;
import org.netbeans.spi.palette.PaletteActions;
import org.netbeans.spi.palette.PaletteController;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.text.ActiveEditorDrop;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Petr Pisl
 */
public class PHPPaletteActions extends PaletteActions {

    /**
     * Creates a new instance of jbossddPaletteActions
     */
    public PHPPaletteActions() {
    }

    @Override
    public Action[] getImportActions() {
        return new Action[0];
    }

    @Override
    public Action[] getCustomCategoryActions(Lookup category) {
        return new Action[0];
    }

    @Override
    public Action[] getCustomItemActions(Lookup item) {
        return new Action[0];
    }

    @Override
    public Action[] getCustomPaletteActions() {
        return new Action[0];
    }

    @Override
    public Action getPreferredAction(Lookup item) {
        return new MFPaletteInsertAction(item);
    }

    private static class MFPaletteInsertAction extends AbstractAction {

        private final Lookup item;

        MFPaletteInsertAction(Lookup item) {
            this.item = item;
        }

        @Override
        public void actionPerformed(ActionEvent event) {

            ActiveEditorDrop drop = (ActiveEditorDrop) item.lookup(ActiveEditorDrop.class);

            JTextComponent target = Utilities.getFocusedComponent();
            if (target == null) {
                String msg = NbBundle.getMessage(PHPPaletteActions.class, "MSG_ErrorNoFocusedDocument");
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
                return;
            }
            if (drop == null) {
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "{0} doesn''t provide {1}", new Object[]{item.getClass(), ActiveEditorDrop.class});//NOI18N
                return;
            }
            try {
                drop.handleTransfer(target);
            } finally {
                Utilities.requestFocus(target);
            }

            try {
                PaletteController paletteController = PHPPaletteFactory.getPalette();
                paletteController.clearSelection();
            } catch (IOException ioe) {
                Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, null, ioe);
            }

        }
    }
}
