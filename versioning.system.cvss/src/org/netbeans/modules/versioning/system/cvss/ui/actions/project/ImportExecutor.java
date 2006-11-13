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

package org.netbeans.modules.versioning.system.cvss.ui.actions.project;

import org.netbeans.modules.versioning.system.cvss.*;
import org.netbeans.modules.versioning.system.cvss.executor.CheckoutExecutor;
import org.netbeans.modules.versioning.system.cvss.ui.actions.checkout.CheckoutAction;
import org.netbeans.modules.versioning.system.cvss.ui.selectors.Kit;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.importcmd.ImportCommand;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.admin.Entry;
import org.openide.util.NbBundle;
import org.openide.util.TaskListener;
import org.openide.util.Task;
import org.openide.util.actions.SystemAction;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;

import java.io.*;
import java.util.Iterator;
import java.util.Date;

/**
 * Simple import command ExecutorSupport subclass.
 *
 * @author Petr Kuzel
 */
final class ImportExecutor extends ExecutorSupport implements Runnable {

    private final String module;
    private final String cvsRoot;
    private final boolean checkout;
    private final String folder;
    private CheckoutExecutor checkoutExecutor;
    private File checkoutDir;
    private final ExecutorGroup group;

    /**
     * Creates new executor that on succesfull import
     * launches post checkout.
     *
     * @param cmd
     * @param options
     * @param checkout perform initial checkout
     * @param folder structure that is imported
     * @param group that this executor joins
     */
    public ImportExecutor(ImportCommand cmd, GlobalOptions options, boolean checkout, String folder, ExecutorGroup group) {
        super(CvsVersioningSystem.getInstance(), cmd, options);
        module = cmd.getModule();
        cvsRoot = options.getCVSRoot();
        this.checkout = checkout;
        this.folder = folder;
        this.group = group;

        group.addExecutor(this);
        if (checkout) {
            checkoutDir = Kit.createTmpFolder();
            CheckoutAction checkoutAction = (CheckoutAction) SystemAction.get(CheckoutAction.class);
            checkoutExecutor = checkoutAction.checkout(cvsRoot, module, null, checkoutDir.getAbsolutePath(), false, group);
            group.addBarrier(this);
        }
    }

    protected void commandFinished(ClientRuntime.Result result) {
    }

    // TODO detect conflics
    // test for "No conflicts created by this import"

    /**
     * afrer checkout barrier implementation
     * @thread called asynchrously from random thread (ClientRuntime)
     */
    public void run() {
        CvsVersioningSystem.getInstance().versionedFilesChanged();
        if (checkoutExecutor.isSuccessful()) {
            copyMetadata();
        }
        Kit.deleteRecursively(checkoutDir);
    }

    private void copyMetadata() {
        File dest = new File(folder);
        File src = new File(checkoutDir, module);  // checkout creates new subdir

        assert src.isDirectory() : src.getAbsolutePath();

        copyFolderMeta(src, dest);

        FileStatusCache cache = CvsVersioningSystem.getInstance().getStatusCache();
        cache.refresh(dest, FileStatusCache.REPOSITORY_STATUS_UNKNOWN);
    }

    private void copyFolderMeta(File src, File dest) {
        File[] files = src.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                if ("CVS".equals(file.getName())) { // NOI18N
                    copyCvsMeta(file, dest);
                } else {
                    File destDir = new File(dest, file.getName());
                    if (destDir.isDirectory()) {
                        copyFolderMeta(file, destDir);  // RESURSION
                    }
                }
            }
        }
    }

    private void copyCvsMeta(File src, File dest) {
        File destCvsDir = new File(dest, "CVS"); // NOI18N
        if (destCvsDir.exists() == false || (destCvsDir.isDirectory() && destCvsDir.listFiles().length == 0) ) {
            destCvsDir.mkdirs();
            if (destCvsDir.isDirectory()) {
                // be on safe side copy only Root, Entries, Repository
                try {
                    File root = new File(src, "Root"); // NOI18N
                    copyFile(root, new File(destCvsDir, "Root")); // NOI18N
                    File repository = new File(src, "Repository"); // NOI18N
                    copyFile(repository, new File(destCvsDir, "Repository")); // NOI18N
                    File entries = new File(src, "Entries"); // NOI18N
                    copyFile(entries, new File(destCvsDir, "Entries")); // NOI18N

                    // set file timestamps according to entries
                    StandardAdminHandler parser = new StandardAdminHandler();
                    Iterator it = parser.getEntries(dest);
                    while (it.hasNext()) {
                        Entry entry = (Entry) it.next();
                        String name = entry.getName();
                        // TODO GMT conversions to local
                        Date date = entry.getLastModified();

                        File sourceFile = new File(dest, name);
                        if (sourceFile.isFile()) {
                            sourceFile.setLastModified(date.getTime());
                        }
                    }
                } catch (IOException e) {
                    ErrorManager err = ErrorManager.getDefault();
                    err.annotate(e, NbBundle.getMessage(ImportExecutor.class, "BK3001"));
                    err.notify(e);
                }
            }
        }
    }

    private static void copyFile(File src, File dst) throws IOException {
        FileOutputStream fos = new FileOutputStream(dst);
        FileInputStream fis = new FileInputStream(src);
        long len = src.length();
        assert ((int) len) == len : "Unsupported file size:" + len; // NOI18N
        copyStream(fos, fis, (int) len);
    }

    private static void copyStream(OutputStream out, InputStream in, int len) throws IOException {
        byte [] buffer = new byte[4096];
        for (;;) {
            int n = (len <= 4096) ? len : 4096;
            n = in.read(buffer, 0, n);
            if (n < 0) throw new EOFException();
            out.write(buffer, 0, n);
            if ((len -= n) == 0) break;
        }
    }

}
