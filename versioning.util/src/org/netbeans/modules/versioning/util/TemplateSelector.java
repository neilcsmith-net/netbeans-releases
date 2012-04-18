
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.versioning.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Stupka
 */
public class TemplateSelector implements ActionListener {

    private static final String KEY_AUTO_FILL     = "vcstemplate.autofill";     // NOI18N
    private static final String KEY_TEMPLATE      = "vcstemplate.value";        // NOI18N
    private static final String KEY_TEMPLATE_FILE = "vcstemplate.templatefile"; // NOI18N

    private TemplatesPanel panel;
    private final Preferences preferences;

    public TemplateSelector(Preferences preferences) {
        this.preferences = preferences;
    }

    /**
     * @deprecated use {@link #show(java.lang.String) } instead
     */
    @Deprecated
    public boolean show() {
        return show(TemplatesPanel.class.getName());
    }

    public boolean show (String helpCtxId) {
        getPanel().autoFillInCheckBox.setSelected(isAutofill());
        getPanel().templateTextArea.setText(getTemplate());
        if(showPanel(helpCtxId)) {
            setAutofill(getPanel().autoFillInCheckBox.isSelected());
            setTemplate(getPanel().templateTextArea.getText());
            return true;
        }
        return false;
    }

    private boolean showPanel (String helpCtxId) {
        DialogDescriptor descriptor = new DialogDescriptor (
                getPanel(),
                NbBundle.getMessage(TemplateSelector.class, "CTL_TemplateTitle"),   // NOI18N
                true,
                new Object[] {DialogDescriptor.OK_OPTION, DialogDescriptor.CANCEL_OPTION},
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.DEFAULT_ALIGN,
                new HelpCtx(helpCtxId),
                null);
        return DialogDisplayer.getDefault().notify(descriptor) == DialogDescriptor.OK_OPTION;
    }

    private TemplatesPanel getPanel() {
        if(panel == null) {
            panel = new TemplatesPanel();
            panel.openButton.addActionListener(this);
            panel.saveButton.addActionListener(this);
        }
        return panel;
    }

    public boolean isAutofill() {
        return preferences.getBoolean(KEY_AUTO_FILL, false);
    }

    public String getTemplate() {
        return preferences.get(KEY_TEMPLATE, ""); // NOI18N
    }

    private void setAutofill(boolean bl) {
        preferences.putBoolean(KEY_AUTO_FILL, bl);
    }

    private void setTemplate(String template) {
        preferences.put(KEY_TEMPLATE, template);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == getPanel().openButton) {
            onOpen();
        } else if(e.getSource() == getPanel().saveButton) {
            onSave();
        }
    }
    
    private void onOpen() {
        File file = selectFile(JFileChooser.OPEN_DIALOG, NbBundle.getMessage(TemplateSelector.class, "CTL_Load")); // NOI18N
        if (file == null) {
            return;
        }

        try {
            byte[] bytes = getFileContentsAsByteArray(file);
            if (bytes != null) {
                getPanel().templateTextArea.setText(new String(bytes));
            }
        } catch (IOException ex) {
            Utils.logError(TemplatesPanel.class, ex);
        }
        preferences.put(KEY_TEMPLATE_FILE, file.getAbsolutePath());
    }

    private void onSave() {
        File file = selectFile(JFileChooser.SAVE_DIALOG, NbBundle.getMessage(TemplateSelector.class, "CTL_Save")); // NOI18N
        if (file == null) {
            return;
        }

        String template = getPanel().templateTextArea.getText();
        try {
            FileUtils.copyStreamToFile(new ByteArrayInputStream(template.getBytes()), file);
        } catch (IOException ex) {
            Utils.logError(TemplatesPanel.class, ex);
        }
        preferences.put(KEY_TEMPLATE_FILE, file.getAbsolutePath());
    }

    private File selectFile(int dialogType, String approveButtonText) {
        JFileChooser fileChooser = new AccessibleJFileChooser(NbBundle.getMessage(TemplateSelector.class, "ACSD_SelectTemplate")/*, defaultDir*/);// NOI18N
        fileChooser.setDialogTitle(NbBundle.getMessage(TemplateSelector.class, "CTL_SelectTemplate"));// NOI18N
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogType(dialogType);

        File file = getTemplateFile();

//        if(file.isFile() && dialogType == JFileChooser.OPEN_DIALOG) {
//            fileChooser.setSelectedFile(file);
//        } else {
//            fileChooser.setCurrentDirectory(file.isFile() ? file.getParentFile() : file);
//        }
        if(file.isFile() ) {
            fileChooser.setSelectedFile(file);
        } else {
            fileChooser.setCurrentDirectory(file);
        }

        fileChooser.showDialog(getPanel(), approveButtonText);
        File f = fileChooser.getSelectedFile();
        return f;
    }

    private File getTemplateFile() {
        File file = null;

        String tmpFile = preferences.get(KEY_TEMPLATE_FILE, null);
        if(tmpFile != null) {
            file = new File(tmpFile);
        }

        if (file == null) {
            file = new File(System.getProperty("user.home"));  // NOI18N
        }
        return file;
    }

    private static byte[] getFileContentsAsByteArray (File file) throws IOException {
        long length = file.length();
        if(length > 1024 * 10) {
            NotifyDescriptor nd =
                new NotifyDescriptor(
                    NbBundle.getMessage(TemplateSelector.class, "MSG_FileTooBig"),
                    NbBundle.getMessage(TemplateSelector.class, "LBL_FileTooBig"),    // NOI18N
                    NotifyDescriptor.DEFAULT_OPTION,
                    NotifyDescriptor.WARNING_MESSAGE,
                    new Object[] {NotifyDescriptor.OK_OPTION, NotifyDescriptor.CANCEL_OPTION},
                    NotifyDescriptor.OK_OPTION);
            if(DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return null;
            }
        }

        return FileUtils.getFileContentsAsByteArray(file);
    }
}
