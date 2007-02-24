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


//	 Date:: Oct 23, 2003 1:51:44 PM

package org.netbeans.modules.uml.ui.support.messaging;

import org.netbeans.modules.uml.common.generics.ETPairT;

public interface IProgressDialog {
	
	public static final int CANCEL = 0;
	public static final int FINISH = 1;
	
	/**
	 * Sets the title of the dialog.
	*/
	public String getTitle();

	/**
	 * Sets the title of the dialog.
	*/
	public void setTitle( String value );

	/**
	 * Sets the lower and upper limits of the progress control.
	*/
	public void setLimits( ETPairT<Integer, Integer> pLimits);

	/**
	 * Retrieves the lower and upper settings of the progress control.
	*/
	public ETPairT<Integer, Integer> getLimits();

	/**
	 * The amount that the progress control will progress after each call to Increment.
	*/
	public int getIncrementAmount();

	/**
	 * The amount that the progress control will progress after each call to Increment.
	*/
	public void setIncrementAmount( int value );

	/**
	 * Set / Get the title of the grouping control surrounding the descriptive text boxes.
	*/
	public String getGroupingTitle();

	/**
	 * Set / Get the title of the grouping control surrounding the descriptive text boxes.
	*/
	public void setGroupingTitle( String value );

	/**
	 * Set / Get the contents of the top text field.
	*/
	public String getFieldOne();

	/**
	 * Set / Get the contents of the top text field.
	*/
	public void setFieldOne( String value );

	/**
	 * Set / Get the value of the middle text field.
	*/
	public String getFieldTwo();

	/**
	 * Set / Get the value of the middle text field.
	*/
	public void setFieldTwo( String value );

	/**
	 * Set / Get the contents of the bottom text field.
	*/
	public String getFieldThree();

	/**
	 * Set / Get the contents of the bottom text field.
	*/
	public void setFieldThree( String value );

	/**
	 * Increments the progress control of the control.
	*/
	public int increment(int value);

	/**
	 * Increments the progress control of the control.
	*/
	public int increment();

	/**
	 * Displays the progress dialog.
	*/
	public boolean display( /* ModalModeKind */ int mode );

	/**
	 * Takes the progress dialog down.
	*/
	public long close();

	/**
	 * Frees fields one, two, and three of their contents.
	*/
	public long clearFields();

	/**
	 * Gets / Sets the position of the progress control.
	*/
	public int getPosition();

	/**
	 * Gets / Sets the position of the progress control.
	*/
	public void setPosition( int value );

	/**
	 * Freezes the display of the Message Center pane.
	*/
	public void lockMessageCenterUpdate();

	/**
	 * Allows updating of message to the message center.
	*/
	public void unlockMessageCenterUpdate();

	/**
	 * Logs a message to the Message Center pane of the ProgressDialog
	*/
	public void log( /* ProgressDialogMessageKind */ int type, String group, String first, String second, String third );

	/**
	 * Set / Get the title of the grouping control surrounding the descriptive text boxes.
	*/
	public void setGroupingTitle( String newVal, /* ProgressDialogMessageKind */ int type );

	/**
	 * Set / Get the contents of the top text field.
	*/
	public void setFieldOne( String newVal, /* ProgressDialogMessageKind */ int type );

	/**
	 * Set / Get the value of the middle text field.
	*/
	public void setFieldTwo( String newVal, /* ProgressDialogMessageKind */ int type );

	/**
	 * Set / Get the contents of the bottom text field.
	*/
	public void setFieldThree( String newVal, /* ProgressDialogMessageKind */ int type );

	/**
	 * If the dialog has been set in modeless mode, this call will cause the dialog to capture input until the user has clicked the only button.
	*/
	public void promptForClosure( String buttonTitle, boolean beep );

	/**
	 * Sets / Gets the default name for the log file.
	*/
	public String getLogFileName();

	/**
	 * Sets / Gets the default name for the log file.
	*/
	public void setLogFileName( String value );

	/**
	 * The default extension to use when saving the log file.
	*/
	public String getDefaultExtension();

	/**
	 * The default extension to use when saving the log file.
	*/
	public void setDefaultExtension( String value );

	/**
	 * Displays the Progress Dialog in a smaller size, hiding the message center.
	*/
	public boolean getCollapse();

	/**
	 * Displays the Progress Dialog in a smaller size, hiding the message center.
	*/
	public void setCollapse( boolean value );

	/**
	 * True to automatically have the diagram dismiss itself upon completion.
	*/
	public boolean getCloseWhenDone();

	/**
	 * True to automatically have the diagram dismiss itself upon completion.
	*/
	public void setCloseWhenDone( boolean value );

	/**
	 * The interface called when the Progress dialog is placed in modal mode.
	*/
	public IProgressExecutor getProgressExecutor();

	/**
	 * The interface called when the Progress dialog is placed in modal mode.
	*/
	public void setProgressExecutor( IProgressExecutor value );

	/**
	 * Determines whether or not the user has cancelled the dialog.
	*/
	public boolean getIsCancelled();
	

	public void setIndeterminate(boolean newVal);
	
	
	public void setProgressController(IProgressController value);
	public IProgressController getProgressController();

	public void addListener(IProgressDialogListener listener);
	
	public void removeListener(IProgressDialogListener listener);

}
