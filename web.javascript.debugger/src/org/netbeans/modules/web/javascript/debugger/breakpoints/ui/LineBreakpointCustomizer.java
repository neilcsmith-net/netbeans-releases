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
package org.netbeans.modules.web.javascript.debugger.breakpoints.ui;

import java.beans.PropertyChangeListener;
import java.io.File;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.modules.web.javascript.debugger.MiscEditorUtil;
import org.netbeans.modules.web.javascript.debugger.breakpoints.LineBreakpoint;
import org.netbeans.spi.debugger.ui.Controller;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.Line;
import org.openide.util.HelpCtx;

/**
 *
 * @author david
 */
public class LineBreakpointCustomizer extends javax.swing.JPanel implements ControllerProvider, HelpCtx.Provider {

    private final CustomizerController controller;
    private final LineBreakpoint lb;
    private boolean createBreakpoint;
    
    private static LineBreakpoint createBreakpoint() {
        Line line = MiscEditorUtil.getCurrentLine();
        if (line == null) {
            return null;
        }
        return new LineBreakpoint(line);
    }
    
    /**
     * Creates new form LineBreakpointCustomizer
     */
    public LineBreakpointCustomizer() {
        this(createBreakpoint());
        createBreakpoint = true;
    }
    
    /**
     * Creates new form LineBreakpointCustomizer
     */
    public LineBreakpointCustomizer(LineBreakpoint lb) {
        this.lb = lb;
        initComponents();
        controller = new CustomizerController();
        if (lb != null) {
            Line line = lb.getLine();
            FileObject fo = line.getLookup().lookup(FileObject.class);
            if (fo != null) {
                File file = FileUtil.toFile(fo);
                if (file != null) {
                    fileTextField.setText(file.getAbsolutePath());
                } else {
                    fileTextField.setText(fo.toURL().toExternalForm());
                }
            }
            lineTextField.setText(Integer.toString(line.getLineNumber() + 1));
        }
    }
    
    @Override
    public Controller getController() {
        return controller;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileLabel = new javax.swing.JLabel();
        fileTextField = new javax.swing.JTextField();
        lineLabel = new javax.swing.JLabel();
        lineTextField = new javax.swing.JTextField();

        fileLabel.setText(org.openide.util.NbBundle.getMessage(LineBreakpointCustomizer.class, "LineBreakpointCustomizer.fileLabel.text")); // NOI18N

        fileTextField.setText(org.openide.util.NbBundle.getMessage(LineBreakpointCustomizer.class, "LineBreakpointCustomizer.fileTextField.text")); // NOI18N

        lineLabel.setText(org.openide.util.NbBundle.getMessage(LineBreakpointCustomizer.class, "LineBreakpointCustomizer.lineLabel.text")); // NOI18N

        lineTextField.setText(org.openide.util.NbBundle.getMessage(LineBreakpointCustomizer.class, "LineBreakpointCustomizer.lineTextField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lineLabel)
                    .addComponent(fileLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fileTextField)
                    .addComponent(lineTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileLabel)
                    .addComponent(fileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lineLabel)
                    .addComponent(lineTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fileLabel;
    private javax.swing.JTextField fileTextField;
    private javax.swing.JLabel lineLabel;
    private javax.swing.JTextField lineTextField;
    // End of variables declaration//GEN-END:variables

    @Override
    public HelpCtx getHelpCtx() {
        return new org.openide.util.HelpCtx("NetbeansDebuggerLineBreakpointJavaScript"); // NOI18N
    }
    
    private class CustomizerController implements Controller {

        @Override
        public boolean ok() {
            LineBreakpoint lb = LineBreakpointCustomizer.this.lb;
            String fileStr = fileTextField.getText();
            int lineNumber;
            try {
                lineNumber = Integer.parseInt(lineTextField.getText());
            } catch (NumberFormatException nfex) {
                return false;
            }
            lineNumber--;
            Line line = MiscEditorUtil.getLine(fileStr, lineNumber);
            if (line == null) {
                return false;
            }
            if (lb != null) {
                lb.setLine(line);
            } else {
                lb = new LineBreakpoint(line);
            }
            if (createBreakpoint) {
                DebuggerManager.getDebuggerManager().addBreakpoint(lb);
            }
            return true;
        }

        @Override
        public boolean cancel() {
            return true;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener l) {
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener l) {
        }
        
    }
}
