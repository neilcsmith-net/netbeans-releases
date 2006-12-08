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

package org.netbeans.modules.web.project.ui.customizer;

import java.util.List;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

import org.netbeans.modules.websvc.api.webservices.WsCompileEditorSupport;


/** Host for WsCompile features editor for editing the features enabled for
 *  running WsCompile on a web service or a web service client.
 *
 *  property format: 'webservice.client.[servicename].features=xxx,yyy,zzz
 *
 * @author Peter Williams
 */
public class CustomizerWSServiceHost extends javax.swing.JPanel implements /*WebCustomizer.Panel, WebCustomizer.ValidatingPanel,*/ PropertyChangeListener, HelpCtx.Provider {
    
    private WebProjectProperties webProperties;
    private WsCompileEditorSupport.Panel wsCompileEditor;

    private List serviceSettings;
    
    public CustomizerWSServiceHost(WebProjectProperties webProperties, List serviceSettings) {
        assert serviceSettings != null;
        initComponents();

        this.webProperties = webProperties;
        this.wsCompileEditor = null;
        this.serviceSettings = serviceSettings;

        if (serviceSettings.size() > 0)
            initValues();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

        setLayout(new java.awt.BorderLayout());

    }//GEN-END:initComponents

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    public void addNotify() {
        super.addNotify();
        
//        System.out.println("WSClientCustomizer: addNotify (" + this.getComponentCount() + " subcomponents)");
        JPanel component = wsCompileEditor.getComponent();

        removeAll(); // !PW is this necessary?
        add(component);
        
        component.addPropertyChangeListener(WsCompileEditorSupport.PROP_FEATURES_CHANGED, this);
    }
    
    public void removeNotify() {
        super.removeNotify();
        
//        System.out.println("WSClientCustomizer: removeNotify");
        JPanel component = wsCompileEditor.getComponent();
        component.removePropertyChangeListener(WsCompileEditorSupport.PROP_FEATURES_CHANGED, this);
    }
   
    private void initValues() {
//        System.out.println("WSClientCustomizer: initValues");
        if(wsCompileEditor == null) {
			WsCompileEditorSupport editorSupport = (WsCompileEditorSupport) Lookup.getDefault().lookup(WsCompileEditorSupport.class);
            wsCompileEditor = editorSupport.getWsCompileSupport();
        }
        
        wsCompileEditor.initValues(serviceSettings);
    }   
    
//    public void validatePanel() throws WizardValidationException {
//        System.out.println("WSClientCustomizer: validatePanel ");
//        if(wsCompileEditor != null) {
//            wsCompileEditor.validatePanel();
//        }
//    }
    
    public void propertyChange(PropertyChangeEvent evt) {
//        System.out.println("WSClientCustomizer: propertyChange - " + evt.getPropertyName());
        
        WsCompileEditorSupport.FeatureDescriptor newFeatureDesc = (WsCompileEditorSupport.FeatureDescriptor) evt.getNewValue();
        String propertyName = "wscompile.service." + newFeatureDesc.getServiceName() + ".features";
        webProperties.putAdditionalProperty(propertyName, newFeatureDesc.getFeatures());
    }    
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(CustomizerWSServiceHost.class);
    }
}
