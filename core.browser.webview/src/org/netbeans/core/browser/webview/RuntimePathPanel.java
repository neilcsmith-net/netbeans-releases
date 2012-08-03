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
package org.netbeans.core.browser.webview;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import org.netbeans.core.HtmlBrowserComponent;
import org.netbeans.core.browser.api.WebBrowser;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Panel with a Browse button where users can select the path to JavaFX runtime.
 * 
 * @author S. Aubrecht
 */
class RuntimePathPanel extends javax.swing.JPanel {

    /**
     * Creates new form RuntimePathPanel
     */
    public RuntimePathPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings( "unchecked" )
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblRuntime = new javax.swing.JLabel();
        btnBrowse = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        lblRuntime.setText(NbBundle.getMessage(RuntimePathPanel.class, "RuntimePathPanel.lblRuntime.text")); // NOI18N
        add(lblRuntime, new java.awt.GridBagConstraints());

        btnBrowse.setText(NbBundle.getMessage(RuntimePathPanel.class, "RuntimePathPanel.btnBrowse.text")); // NOI18N
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(btnBrowse, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBrowseActionPerformed( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_btnBrowseActionPerformed
        File runtimePath = browseRuntimeFolder();
        if( null != runtimePath ) {
            DefaultJFXRuntimeProvider.setJFXRuntimePath( runtimePath );
            TopComponent parent = ( TopComponent ) SwingUtilities.getAncestorOfClass( TopComponent.class, this );
            if( parent instanceof HtmlBrowserComponent ) {
                //there's no way to replace the component in an opened HTML browser window
                //so we have to reopen it
                reopenBrowser( ( HtmlBrowserComponent ) parent );
            }
        }

    }//GEN-LAST:event_btnBrowseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JLabel lblRuntime;
    // End of variables declaration//GEN-END:variables

    static File browseRuntimeFolder() {
        FileChooserBuilder fcb = new FileChooserBuilder( RuntimePathPanel.class );
        fcb.setDirectoriesOnly( true );
        fcb.setTitle( NbBundle.getMessage(RuntimePathPanel.class, "Title_BrowserRuntime") );
        final boolean[] appendRT = new boolean[1];
        fcb.setSelectionApprover( new FileChooserBuilder.SelectionApprover() {

            @Override
            public boolean approve( File[] selection ) {
                if( selection == null || selection.length != 1 ) {
                    DialogDisplayer.getDefault().notifyLater( new NotifyDescriptor.Message( NbBundle.getMessage(RuntimePathPanel.class, "Err_NoFolderSelected")) );
                    return false;
                }

                appendRT[0] = false;
                WebBrowser browser = WebBrowserImplProvider.createBrowser( selection[0] );
                if( null == browser || browser instanceof NoWebBrowserImpl ) {
                    selection[0] = new File( selection[0], "jre" ); //NOI18N
                    browser = WebBrowserImplProvider.createBrowser( selection[0] );
                    appendRT[0] = true;
                }
                if( null == browser || browser instanceof NoWebBrowserImpl ) {
                    DialogDisplayer.getDefault().notifyLater( new NotifyDescriptor.Message( NbBundle.getMessage(RuntimePathPanel.class, "Err_NoRuntimeFolder")) );
                    return false;
                }

                return true;
            }
        });
        fcb.setApproveText( NbBundle.getMessage(RuntimePathPanel.class, "Btn_SELECT") );
        JFileChooser chooser = fcb.createFileChooser();
        int res = chooser.showOpenDialog( WindowManager.getDefault().getMainWindow() );
        if( res != JFileChooser.APPROVE_OPTION )
            return null;
        File runtimePath = chooser.getSelectedFile();
        if( appendRT[0] )
            runtimePath = new File( runtimePath, "jre" ); //NOI18N
        return runtimePath;
    }

    private static void reopenBrowser( final HtmlBrowserComponent browser ) {
//        browser
        HtmlBrowser.Impl impl = browser.getBrowserImpl();
        final String url = impl.getLocation();
//        final boolean showToolbar = browser.isToolbarVisible();
//        final boolean showStatus = browser.isStatusLineVisible();
//        browser.close();
        SwingUtilities.invokeLater( new Runnable() {

            @Override
            public void run() {
//                HtmlBrowserComponent newBrowser = new HtmlBrowserComponent( new BrowserFactory(), showToolbar, showStatus );
//                newBrowser.setURL( url );
//                newBrowser.open();
//                newBrowser.requestActive();
                browser.recreateBrowser();
                browser.setURL( url );
                browser.requestActive();
            }
        } );
    }
}
