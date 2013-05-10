/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.browser.ui.picker;

import java.util.Collection;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.modules.web.browser.api.BrowserUISupport;
import org.netbeans.modules.web.browser.api.WebBrowser;
import org.netbeans.modules.web.browser.api.WebBrowsers;

/**
 * Browser combo box that uses BrowserMenu as its popup list.
 * 
 * @author S. Aubrecht
 */
public final class BrowserCombo extends JComboBox<WebBrowser> {

    private final BrowserMenu popup;

    public BrowserCombo( @NullAllowed String selectedBrowserId, boolean showIDEGlobalBrowserOption, boolean includePhoneGap ) {

        Collection<WebBrowser> browsers = WebBrowsers.getInstance().getAll(false, showIDEGlobalBrowserOption, includePhoneGap, true);
        WebBrowser selectedBrowser = null;
        if( null != selectedBrowserId ) {
            selectedBrowser = BrowserUISupport.getBrowser( selectedBrowserId);
        }
        popup = new BrowserMenu( browsers, selectedBrowser );
        setMaximumRowCount( 1 );
        setRenderer( BrowserUISupport.createBrowserRenderer() );

        setModel( BrowserUISupport.createBrowserModel( selectedBrowserId, showIDEGlobalBrowserOption, includePhoneGap ) );

        popup.addChangeListener( new ChangeListener() {

            @Override
            public void stateChanged( ChangeEvent e ) {
                WebBrowser selBrowser = popup.getSelectedBrowser();
                if( null == selBrowser )
                    return;
                for( int i=0; i<getItemCount(); i++ ) {
                    WebBrowser wb = getItemAt( i );
                    if( wb.getId().equals( selBrowser.getId() ) ) {
                        setSelectedItem( wb );
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void firePopupMenuWillBecomeVisible() {
        super.firePopupMenuWillBecomeVisible();
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                popup.show( BrowserCombo.this, 0, getHeight() );
            }
        });
    }
}
