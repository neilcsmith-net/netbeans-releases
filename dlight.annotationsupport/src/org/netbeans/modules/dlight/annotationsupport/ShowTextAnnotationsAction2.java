/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.dlight.annotationsupport;

import java.awt.event.ActionEvent;
import javax.swing.JEditorPane;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.openide.windows.WindowManager;

/**
 * Enables or disables breakpoints.
 *
 * @author Martin Entlicher
 */
public class ShowTextAnnotationsAction2 extends BooleanStateAction {

    @Override
    public boolean isEnabled() {
        return hasAnnotations();
    }

    @Override
    public boolean getBooleanState() {
        return AnnotationSupport.getInstance().getTextAnnotationVisible();
    }

    public String getName() {
        return NbBundle.getMessage(ShowTextAnnotationsAction.class, "CTL_MenuItem_ShowAnnotations2");
    }

    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        boolean tav = AnnotationSupport.getInstance().getTextAnnotationVisible();
        AnnotationSupport.getInstance().setTextAnnotationVisible(!tav);
    }

    private boolean hasAnnotations() {
//        if (AnnotatedSourceSupportImpl.getInstance() == null) {
//            return false;
//        }
        Node[] nodes = WindowManager.getDefault().getRegistry().getCurrentNodes();

        if (nodes == null || nodes.length != 1) {
            return true; // Maybe
        }

        Node node = nodes[0];
        if (node == null) {
            return true; // Maybe
        }

        DataObject dataObject = node.getLookup().lookup(DataObject.class);
        if (dataObject == null) {
            return true; // Maybe
        }
//
//        File file = FileUtil.toFile(dataObject.getPrimaryFile());
//        String filePath = FileUtil.normalizeFile(file).getAbsolutePath();
//        FileAnnotationInfo fileAnnotationInfo = AnnotatedSourceSupportImpl.getInstance().getFileAnnotationInfo(filePath);
//        return fileAnnotationInfo != null;

        EditorCookie editorCookie = dataObject.getCookie(EditorCookie.class);
        if (editorCookie == null) {
            return true; // Maybe
        }

        JEditorPane panes[] = editorCookie.getOpenedPanes();
        boolean annotated = false;
        for (JEditorPane pane : panes) {
            AnnotationBar ab = (AnnotationBar) pane.getClientProperty(AnnotationBarManager.BAR_KEY);
            if (ab.hasAnnotations()) {
                annotated = true;
                break;
            }
        }

        return annotated; // Probably
    }
}
