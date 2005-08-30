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

package org.netbeans.modules.versioning.system.cvss.ui.actions.update;

import org.netbeans.modules.versioning.system.cvss.ui.actions.AbstractSystemAction;
import org.netbeans.modules.versioning.system.cvss.*;
import org.netbeans.lib.cvsclient.file.FileUtils;
import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.admin.AdminHandler;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * Revert modifications action.
 * 
 * @author Maros Sandor
 */
public class GetCleanAction extends AbstractSystemAction {

    protected String getBaseName() {
        return "CTL_MenuItem_GetClean";
    }

    protected int getFileEnabledStatus() {
        return FileInformation.STATUS_IN_REPOSITORY & 
                ~FileInformation.STATUS_VERSIONED_UPTODATE;
    }
    
    public void performCvsAction(ActionEvent ev) {
        int res = JOptionPane.showConfirmDialog(
                null, 
                NbBundle.getMessage(GetCleanAction.class, "CTL_RevertModifications_Prompt"),
                NbBundle.getMessage(GetCleanAction.class, "CTL_RevertModifications_Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (res != JOptionPane.YES_OPTION) return;
        
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                revertModifications();
            }
        });
    }

    private void revertModifications() {
        FileStatusCache cache = CvsVersioningSystem.getInstance().getStatusCache();
        File [] files = cache.listFiles(getFilesToProcess(), FileInformation.STATUS_LOCAL_CHANGE & FileInformation.STATUS_IN_REPOSITORY);
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            rollback(file, VersionsCache.REVISION_BASE);
        }
    }
    
    /**
     * Overwrites given file with its specified revision. Revision number and sticky information in Entries is NOT modified, 
     * only the content is overwritten.
     * 
     * @param file the file to overwrite
     * @param revision revision to get
     */ 
    public static void rollback(File file, String revision) {
        FileStatusCache cache = CvsVersioningSystem.getInstance().getStatusCache();
        AdminHandler ah = CvsVersioningSystem.getInstance().getAdminHandler();
        Entry entry = null;
        try {
            entry = ah.getEntry(file);
        } catch (IOException e) {
            // non-fatal, we have no entry for this file
        }
        try {
            File cleanFile = VersionsCache.getInstance().getRemoteFile(file, revision);
            if (cleanFile != null) {
                FileUtils.copyFile(cleanFile, file);
                if (entry != null && entry.isUserFileToBeRemoved()) {
                    entry.setRevision(entry.getRevision().substring(1));
                    ah.setEntry(file, entry);
                }
                FileObject fo = FileUtil.toFileObject(file);
                if (fo != null) {
                    fo.refresh();
                }
                cache.refresh(file, revision == VersionsCache.REVISION_BASE ? FileStatusCache.REPOSITORY_STATUS_UPTODATE : FileStatusCache.REPOSITORY_STATUS_UNKNOWN);
            } else {
                // TODO can not find in repository
                // locally delete? NOt yet there seems to be bug in checkout -p
                System.err.println("CVSrepo: can not locate: " + file);
                cleanFile.getName(); // raise compatability NPE
            }
        } catch (Exception e) {
            ErrorManager.getDefault().notify(e);
        }
    }
}
