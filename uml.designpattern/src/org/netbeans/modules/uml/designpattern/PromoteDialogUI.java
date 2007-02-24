/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

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


package org.netbeans.modules.uml.designpattern;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JComboBox;

import org.netbeans.modules.uml.core.metamodel.core.foundation.INamespace;
import org.netbeans.modules.uml.core.metamodel.structure.IProject;
import org.netbeans.modules.uml.core.support.umlutils.ETArrayList;
import org.netbeans.modules.uml.core.support.umlutils.ETList;
import org.netbeans.modules.uml.ui.swing.commondialogs.JCenterDialog;

public class PromoteDialogUI extends JCenterDialog
{
	/** Creates new form finddialog */
	public PromoteDialogUI(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		initDialog();
		center(parent);
	}

	/** This method is called from within the constructor to
		 * initialize the form.
		 * WARNING: Do NOT modify this code. The content of this method is
		 * always regenerated by the Form Editor.
		 */
		private void initComponents() {
			java.awt.GridBagConstraints gridBagConstraints;

			jPanel0 = new javax.swing.JPanel();
			jPanel1 = new javax.swing.JPanel();
			textLabel = new javax.swing.JLabel();
			m_ProjectCombo = new javax.swing.JComboBox();
			textLabel2 = new javax.swing.JLabel();
			m_NamespaceCombo = new javax.swing.JComboBox();
			jPanel3 = new javax.swing.JPanel();
			m_RemoveCheck = new javax.swing.JCheckBox();
			jPanel2 = new javax.swing.JPanel();
			m_OKButton = new javax.swing.JButton();
			m_CancelButton = new javax.swing.JButton();

			jPanel4 = new javax.swing.JPanel();
			jPanel4.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(35, 5, 5, 5)));

			setTitle(DefaultDesignPatternResource.getString("IDS_PROMOTETITLE"));
			addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent evt) {
					closeDialog(evt);
				}
			});
			jPanel0.setLayout(new javax.swing.BoxLayout(jPanel0, javax.swing.BoxLayout.Y_AXIS));
			jPanel0.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 5, 5, 5)));

			java.awt.Font theFont = new java.awt.Font("Dialog", 0, 11);

			jPanel1.setLayout(new GridBagLayout());
			// text label
			textLabel.setFont(theFont);
			textLabel.setText(DefaultDesignPatternResource.determineText(DefaultDesignPatternResource.getString("IDS_PROJECT")));
			DefaultDesignPatternResource.setMnemonic(textLabel, DefaultDesignPatternResource.getString("IDS_PROJECT"));
			DefaultDesignPatternResource.setFocusAccelerator(m_ProjectCombo, DefaultDesignPatternResource.getString("IDS_PROJECT"));
			jPanel1.add(textLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 0, 0, 0), 0, 0));

			// combo box
			jPanel1.add(m_ProjectCombo, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 10, 0));
			m_ProjectCombo.setEditable(false);
			m_ProjectCombo.setFont(theFont);
			m_ProjectCombo.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					//onSelChangeProjectList(evt);
				}
			});
			jPanel1.add(jPanel4);
			// text label
			textLabel2.setFont(theFont);
			textLabel2.setText(DefaultDesignPatternResource.determineText(DefaultDesignPatternResource.getString("IDS_NAMESPACE")));
			DefaultDesignPatternResource.setMnemonic(textLabel2, DefaultDesignPatternResource.getString("IDS_NAMESPACE"));
			DefaultDesignPatternResource.setFocusAccelerator(m_NamespaceCombo, DefaultDesignPatternResource.getString("IDS_NAMESPACE"));
			jPanel1.add(textLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 0, 0, 0), 0, 0));

			// combo box
			jPanel1.add(m_NamespaceCombo, new GridBagConstraints(1, 1, 1, 1, 1.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 10, 0));
			m_NamespaceCombo.setEditable(false);
			m_NamespaceCombo.setFont(theFont);
			jPanel0.add(jPanel1);

			// check box
			jPanel3.setLayout(new GridBagLayout());
			m_RemoveCheck.setFont(theFont);
			m_RemoveCheck.setText(DefaultDesignPatternResource.determineText(DefaultDesignPatternResource.getString("IDS_REMOVEFROMPROJECT")));
			DefaultDesignPatternResource.setMnemonic(m_RemoveCheck, DefaultDesignPatternResource.getString("IDS_REMOVEFROMPROJECT"));
			m_RemoveCheck.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					onRemoveCheck(evt);
				}
			});
			jPanel3.add(m_RemoveCheck, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			jPanel0.add(jPanel3);

			// buttons
			Dimension buttonSize = new Dimension(70, 25);
			jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.X_AXIS));

			m_OKButton.setFont(theFont);
			m_OKButton.setText(DefaultDesignPatternResource.getString("IDS_OK"));
			m_OKButton.setPreferredSize(buttonSize);
			//m_OKButton.setMaximumSize(buttonSize);
			m_OKButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					onOKButton(evt);
				}
			});
			getRootPane().setDefaultButton(m_OKButton);
			jPanel2.add(m_OKButton);

			m_CancelButton.setFont(theFont);
			m_CancelButton.setText(DefaultDesignPatternResource.getString("IDS_CANCEL"));
			m_CancelButton.setPreferredSize(buttonSize);
			//m_CancelButton.setMaximumSize(buttonSize);
			m_CancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					setVisible(false);
					dispose();
				}
			});
			m_CancelButton.addKeyListener(new KeyListener(){
				public void keyTyped(KeyEvent e)
				{
					e.consume();
				}
				public void keyPressed(KeyEvent e)
				{
					handleKeyDownCancelButton(e);
				}

				public void keyReleased(KeyEvent e)
				{
					e.consume();
				}
			});
			jPanel2.add(Box.createHorizontalStrut(5));
			jPanel2.add(m_CancelButton);

			jPanel0.add(jPanel2);

			getContentPane().add(jPanel0, java.awt.BorderLayout.CENTER);

			this.addActionListeners();


                        //CBeckham - changed to adjust panel for larger font sizes when used in
                        //different locales
                        int fontsize;
                        java.awt.Font f =
                            javax.swing.UIManager.getFont ("controlFont"); //NOI18N
                        if (f != null) {
                            fontsize = f.getSize();
                        } else {
                            fontsize = 12;
                        }
                        int width  = 300;
                        int height = 185;
                        int multiplyer = 2;

                        if (fontsize > 17 ) multiplyer =3;
                        width  = width  + Math.round(width*(multiplyer*fontsize/100f));
                        height = height + Math.round(height*(multiplyer*fontsize/100f));
                        setSize(width,height);
			//setSize(300, 185);
		}

		private void onRemoveCheck(java.awt.event.ActionEvent evt) {
			Object obj = evt.getSource();
			if (obj instanceof JCheckBox)
			{
				JCheckBox box = (JCheckBox)obj;
				boolean checkboxState = box.isSelected();
				if (checkboxState)
				{
				}
				else
				{
				}
			}
		}
		private void addActionListeners() {
			m_ProjectCombo.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
					m_ProjectCombo_actionPerformed(e);
				}
			});
		}

		private void m_ProjectCombo_actionPerformed(ActionEvent e) {
			onSelChangeProjectList();
		}

		/**
		 * User has hit the okay button
		 *
		 * @return
		 */
		private void onOKButton(java.awt.event.ActionEvent evt) {
			Object obj = evt.getSource();
			if (obj instanceof JButton)
			{
				onOKButton();
			}
		}
		private void onOKButton()
		{
			if (storeInformation())
			{
				ETList <String> errorList = validateInformation();
				if (errorList != null && errorList.size() == 0)
				{
					// user has hit okay on the gui and the gui information
					// has been validated, so begin the process of promoting it
					m_Manager.promotePattern(m_Details);
					setVisible(false);
					dispose();
				}
				else if (errorList != null && errorList.size() > 0)
				{
					// display the errors
					String msg = DesignPatternUtilities.formatErrorMessage(errorList);
					DesignPatternUtilities.displayErrorMessage(this, msg);
				}
			}
		}

		private void initDialog()
		{
			DesignPatternUtilities.populateProjectListWithDesignCenterProjects(m_ProjectCombo, true);
			populateProject();
		}
		/**
		 * Fills in the project list box default.  If there is a project in the pattern
		 * details, that means that the project is already known, so the user should not
		 * be allowed to change it.  Otherwise, default it to the first known open project
		 * in the workspace.
		 *
		 * @return HRESULT
		 */
		private void populateProject()
		{
			m_ProjectCombo.setEnabled(true);
			onSelChangeProjectList();
		}
		/**
		 * Store the information from the dialog on the pattern details
		 *
		 *
		 * @return bool
		 */
		private boolean storeInformation()
		{
			boolean retVal = true;
			if (m_Details != null)
			{
				// store project
				m_Details.setProject(null);
				if (m_Project != null)
				{
					m_Details.setProject(m_Project);
				}
				// store namespace
				m_Details.setNamespace(null);
				INamespace pNamespace = DesignPatternUtilities.getSelectedNamespace(m_NamespaceCombo, m_Project);
				if (pNamespace != null)
				{
					m_Details.setNamespace(pNamespace);
				}
				// store whether or not to move the pattern or just copy it
				if (m_RemoveCheck.isSelected())
				{
					m_Details.setRemoveOnPromote(true);
				}
				else
				{
					m_Details.setRemoveOnPromote(false);
				}
			}
			return retVal;
		}
	/**
	 * Validate the information on the dialog
	 *
	 *
	 * @return bool
	 */
	private ETList<String> validateInformation()
	{
		ETList<String> tempList = new ETArrayList<String>();
		if (m_ProjectCombo.getSelectedIndex() == -1)
		{
			String err = DesignPatternUtilities.translateString("IDS_SCOPE_NOSELECT");
			tempList.add(err);
		}
		return tempList;
	}
	/**
	 * Event called when an entry in the project list box changes
	 *
	 * @return HRESULT
	 */
	private void onSelChangeProjectList()
	{
		// get the new list entry
		m_Project = null;
		String selText = (String)m_ProjectCombo.getSelectedItem();
		if (selText != null && selText.length() > 0)
		{
			IProject pProject = DesignPatternUtilities.onSelChangeProjectList(selText, this);
			if (pProject != null)
			{
				m_Project = pProject;
				DesignPatternUtilities.populateNamespaceList(m_NamespaceCombo, pProject);
			}
		}
	}

	public void handleKeyDownCancelButton(KeyEvent e)
	{
		boolean consumeEvent = true;
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_ENTER)
		{
			consumeEvent = false;
			setVisible(false);
			dispose();
		}
		else if (keyCode == KeyEvent.VK_ESCAPE)
		{
			consumeEvent = false;
			setVisible(false);
			dispose();
		}

		if (consumeEvent && !e.isConsumed())
		{
			e.consume();
		}
	}

	/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt) {
		setVisible(false);
		dispose();
	}
	public PromoteDialogUI()
	{
		super();
		initComponents();
		initDialog();

	}
	public IDesignPatternDetails getDetails()
	{
		return m_Details;
	}
	public void setDetails(IDesignPatternDetails pDetails)
	{
		m_Details = pDetails;
	}
	public IDesignPatternManager getManager()
	{
		return m_Manager;
	}
	public void setManager(IDesignPatternManager pManager)
	{
		m_Manager = pManager;
	}
	// Variables declaration - do not modify
	private javax.swing.JButton m_OKButton;
	private javax.swing.JButton m_CancelButton;
	private javax.swing.JLabel textLabel;
	private javax.swing.JLabel textLabel2;
	private javax.swing.JComboBox m_ProjectCombo;
	private javax.swing.JComboBox m_NamespaceCombo;
	private javax.swing.JCheckBox m_RemoveCheck;
	private javax.swing.JPanel jPanel0;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	 // End of variables declaration

	private IProject m_Project = null;
	private IDesignPatternDetails m_Details = null;
	private IDesignPatternManager m_Manager = null;
}