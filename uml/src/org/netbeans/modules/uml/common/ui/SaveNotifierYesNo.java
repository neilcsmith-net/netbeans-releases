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

package org.netbeans.modules.uml.common.ui;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

/**
 *
 * @author Craig Conover, craig.conover@sun.com
 */
public class SaveNotifierYesNo 
{
    private static  SaveNotifierYesNo instance = null;
    public final static Object SAVE_ALWAYS_OPTION = new Integer(9999);
    
    public static SaveNotifierYesNo getDefault()
    {
        if (instance == null)
            instance = new SaveNotifierYesNo();
        
        return instance;
    }
    
    /**
     * Creates a new instance of SaveNotifierYesNo
     */
    private SaveNotifierYesNo() 
    {
    }
    
    public Object displayNotifier(
            String dialogTitle, String saveType, String saveName) 
    {
        DialogManager dmgr = new DialogManager(dialogTitle, saveType, saveName);
        dmgr.prompt();
        
        return dmgr.getResult();
    }
    
    
    private static class DialogManager implements ActionListener 
    {
        private DialogDescriptor dialogDesc = null;
        private Dialog dialog = null;
        private Object result = DialogDescriptor.CANCEL_OPTION;

        private final Object[] closeOptions =
        {
            DialogDescriptor.DEFAULT_OPTION,
            DialogDescriptor.NO_OPTION,
            DialogDescriptor.DEFAULT_OPTION,
            DialogDescriptor.YES_OPTION
        };
        
        public DialogManager(
            String dialogTitle, String saveType, String saveName) 
        {
            
            JButton noButton = new JButton(NbBundle.getMessage(
                SaveNotifierYesNo.class, "LBL_NoButton")); // NOI18N
            
            noButton.setActionCommand(NbBundle.getMessage(
                    SaveNotifierYesNo.class, "LBL_NoButton")); // NOI18N
            
            Mnemonics.setLocalizedText(
                    noButton, NbBundle.getMessage(
                        SaveNotifierYesNo.class, "LBL_NoButton")); // NOI18N

            JButton saveAlwaysButton = new JButton(NbBundle.getMessage(
                SaveNotifierYesNo.class, "LBL_SaveAlwaysButton")); // NOI18N
            
            saveAlwaysButton.setActionCommand(NbBundle.getMessage(
                    SaveNotifierYesNo.class, "LBL_SaveAlwaysButton")); // NOI18N
            
            saveAlwaysButton.getAccessibleContext().setAccessibleDescription(
                    NbBundle.getMessage(
                        SaveNotifierYesNo.class, "ACSD_SaveAlwaysButton")); // NOI18N
            
            Mnemonics.setLocalizedText(
                    saveAlwaysButton, NbBundle.getMessage(
                        SaveNotifierYesNo.class, "LBL_SaveAlwaysButton")); // NOI18N

            Object[] buttonOptions =
            {
                DialogDescriptor.YES_OPTION,
                noButton,
                DialogDescriptor.CANCEL_OPTION,
                saveAlwaysButton
            };
            
            dialogDesc = new DialogDescriptor(
                NbBundle.getMessage(SaveNotifierYesNo.class, 
                    "LBL_SaveNotifierYesNoDialog_Question", saveType, saveName), // NOI18N
                dialogTitle, // title
                true, // modal?
                buttonOptions,
                DialogDescriptor.YES_OPTION, // default option
                DialogDescriptor.DEFAULT_ALIGN,
                null, // help context
                this, // button action listener
                false); // leaf?
            
            dialogDesc.setClosingOptions(closeOptions);
            dialog = DialogDisplayer.getDefault().createDialog(dialogDesc);
        }
        
        private void prompt() 
        {
            dialog.setVisible(true);
        }
        
        public void actionPerformed(ActionEvent actionEvent) 
        {
            if (actionEvent.getActionCommand().equalsIgnoreCase(
                NbBundle.getMessage(SaveNotifierYesNo.class, "LBL_YesButton"))) // NOI18N
            {
                result = DialogDescriptor.YES_OPTION;
            }
            
            else if (actionEvent.getActionCommand().equalsIgnoreCase(
                NbBundle.getMessage(SaveNotifierYesNo.class, "LBL_NoButton"))) // NOI18N))
            {
                result = DialogDescriptor.NO_OPTION;
            }
            
            else if (actionEvent.getActionCommand().equalsIgnoreCase(
                NbBundle.getMessage(SaveNotifierYesNo.class, "LBL_SaveAlwaysButton"))) // NOI18N))
            {
                result = SAVE_ALWAYS_OPTION;
            }
            
            else // Cancel or 'x' box close
                result = DialogDescriptor.CANCEL_OPTION;
            
            dialog.setVisible(false);
            dialog.dispose();
        }
        
        public Object getResult() 
        {
            return result;
        }
    }
    
}
