/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.j2ee.sun.share.configbean.customizers.webservice;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.netbeans.modules.j2ee.sun.dd.api.CommonDDBean;
import org.netbeans.modules.j2ee.sun.dd.api.common.WebserviceEndpoint;
import org.netbeans.modules.j2ee.sun.share.configbean.ServletRef;

import org.netbeans.modules.j2ee.sun.share.configbean.StorageBeanFactory;
import org.netbeans.modules.j2ee.sun.share.configbean.WebServiceDescriptor;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.LoginConfigEntry;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.WebServiceEndpointEntryPanel;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.BaseCustomizer;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.GenericTableModel;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.GenericTablePanel;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.HelpContext;


/**
 *
 * @author Peter Williams
 */
public class WebServiceDescriptorCustomizer extends BaseCustomizer implements 
    TableModelListener, PropertyChangeListener {

    private static final ResourceBundle bundle = ResourceBundle.getBundle(
       "org.netbeans.modules.j2ee.sun.share.configbean.customizers.webservice.Bundle"); // NOI18N

    private static final ResourceBundle customizerBundle = ResourceBundle.getBundle(
       "org.netbeans.modules.j2ee.sun.share.configbean.customizers.Bundle"); // NOI18N

	/** The bean currently being customized, or null if there isn't one
	 */
	private WebServiceDescriptor theBean;

	// Table for editing webservice endpoints
	private GenericTableModel webServiceEndpointModel;
	private GenericTablePanel webServiceEndpointPanel;
    private boolean updatingEndpointModel;
    
	/** Creates new form WebServiceDescriptorCustomizer */
	public WebServiceDescriptorCustomizer() {
		initComponents();
		initUserComponents();
	}

	public WebServiceDescriptor getBean() {
		return theBean;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLblName = new javax.swing.JLabel();
        jTxtName = new javax.swing.JTextField();
        jLblWsdlPublishLocation = new javax.swing.JLabel();
        jTxtWsdlPublishLocation = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLblName.setLabelFor(jTxtName);
        jLblName.setText(bundle.getString("LBL_WebServiceDescriptionName_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(jLblName, gridBagConstraints);
        jLblName.getAccessibleContext().setAccessibleName(bundle.getString("ACSN_WebServiceDescriptionName"));
        jLblName.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_WebServiceDescriptionName"));

        jTxtName.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(jTxtName, gridBagConstraints);

        jLblWsdlPublishLocation.setLabelFor(jTxtWsdlPublishLocation);
        jLblWsdlPublishLocation.setText(bundle.getString("LBL_WsdlPublishLocation_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(jLblWsdlPublishLocation, gridBagConstraints);
        jLblWsdlPublishLocation.getAccessibleContext().setAccessibleName(bundle.getString("ACSN_WsdlPublishLocation"));
        jLblWsdlPublishLocation.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_WsdlPublishLocation"));

        jTxtWsdlPublishLocation.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTxtWsdlPublishLocationKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel1.add(jTxtWsdlPublishLocation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        add(jPanel1, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void jTxtWsdlPublishLocationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtWsdlPublishLocationKeyReleased
		if(theBean != null) {
			try {
				theBean.setWsdlPublishLocation(jTxtWsdlPublishLocation.getText());
			} catch(java.beans.PropertyVetoException ex) {
				jTxtWsdlPublishLocation.setText(theBean.getWsdlPublishLocation());
			}
		}		
    }//GEN-LAST:event_jTxtWsdlPublishLocationKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLblName;
    private javax.swing.JLabel jLblWsdlPublishLocation;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTxtName;
    private javax.swing.JTextField jTxtWsdlPublishLocation;
    // End of variables declaration//GEN-END:variables

	private void initUserComponents() {
		// Add title panel
		addTitlePanel(bundle.getString("TITLE_WebServiceDescriptor"));
		getAccessibleContext().setAccessibleName(bundle.getString("ACSN_WebServiceDescriptor"));	// NOI18N
		getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_WebServiceDescriptor"));	// NOI18N

		/** Add webservice endpoints table panel :
		 *  TableEntry list has three properties: port component, endpoint address,
		 *  and transport which are all standard values.  Authentication method,
		 *  specified in the DTD, is not used for servlet based endpoints.
		 */
		ArrayList tableColumns = new ArrayList(4);
		tableColumns.add(new GenericTableModel.ValueEntry(WebserviceEndpoint.PORT_COMPONENT_NAME, customizerBundle.getString("LBL_PortComponentName"), true));	// NOI18N
		tableColumns.add(new LoginConfigEntry());
		tableColumns.add(new GenericTableModel.ValueEntry(WebserviceEndpoint.ENDPOINT_ADDRESS_URI, customizerBundle.getString("LBL_EndpointAddressURI")));	// NOI18N
		tableColumns.add(new GenericTableModel.ValueEntry(WebserviceEndpoint.TRANSPORT_GUARANTEE, customizerBundle.getString("LBL_TransportGuarantee")));	// NOI18N
		
		// add Properties table
        webServiceEndpointModel = new GenericTableModel(webserviceEndpointFactory, tableColumns);
        webServiceEndpointPanel = new GenericTablePanel(webServiceEndpointModel, 
			customizerBundle, "WebServiceEndpoint",	// NOI18N - property name
			WebServiceEndpointEntryPanel.class,
			HelpContext.HELP_SERVICE_ENDPOINT_POPUP);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		add(webServiceEndpointPanel, gridBagConstraints);
        
		// Add error panel
		addErrorPanel();
	}

	protected void initFields() {
		jTxtName.setText(theBean.getWebServiceDescriptionName());
        jTxtWsdlPublishLocation.setText(theBean.getWsdlPublishLocation());
        webServiceEndpointPanel.setModel(theBean.getWebServiceEndpoints());
        updatingEndpointModel = false;
	}

	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		String eventName = propertyChangeEvent.getPropertyName();
		
		if(WebServiceDescriptor.WEBSERVICE_DESCRIPTION_NAME.equals(eventName)) {
			jTxtName.setText(theBean.getWebServiceDescriptionName());
		} else if(WebServiceDescriptor.WEBSERVICE_ENDPOINT.equals(eventName)) {
            try {
                updatingEndpointModel = true;
                webServiceEndpointPanel.setModel(theBean.getWebServiceEndpoints());
            } finally {
                updatingEndpointModel = false;
            }
		}
	}
	
	public void tableChanged(TableModelEvent e) {
		if(theBean != null) {
			try {
                if(!updatingEndpointModel) {
                    theBean.setWebServiceEndpoints(webServiceEndpointModel.getData());
                    theBean.setDirty();
                }
			} catch(PropertyVetoException ex) {
				// FIXME undo whatever changed... how?
			}
		}		
	}
	
	protected void addListeners() {
		super.addListeners();
		theBean.addPropertyChangeListener(this);
		webServiceEndpointModel.addTableModelListener(this);
	}
	
	protected void removeListeners() {
		super.removeListeners();
		theBean.removePropertyChangeListener(this);
		webServiceEndpointModel.removeTableModelListener(this);
	}	
	
	protected boolean setBean(Object bean) {
		boolean result = super.setBean(bean);

		if(bean instanceof WebServiceDescriptor) {
			theBean = (WebServiceDescriptor) bean;
			result = true;
		} else {
			// if bean is not a WebServiceDescriptor, then it shouldn't have passed Base either.
			assert (result == false) :
				"WebServiceDescriptorCustomizer was passed wrong bean type in setBean(Object bean)";	// NOI18N

			theBean = null;
			result = false;
		}

		return result;
	}

	public String getHelpId() {
		return "AS_CFG_WebServiceDescriptor";    // NOI18N
	}
    
    // New for migration to sun DD API model.  Factory instance to pass to generic table model
    // to allow it to create webserviceEndpoint beans.
	private static GenericTableModel.ParentPropertyFactory webserviceEndpointFactory =
        new GenericTableModel.ParentPropertyFactory() {
            public CommonDDBean newParentProperty() {
                return StorageBeanFactory.getDefault().createWebserviceEndpoint();
            }
        };
}
