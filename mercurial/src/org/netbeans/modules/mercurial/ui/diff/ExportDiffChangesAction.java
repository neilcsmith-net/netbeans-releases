/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.mercurial.ui.diff;

import org.netbeans.modules.diff.builtin.visualizer.TextDiffVisualizer;
import org.netbeans.modules.mercurial.FileInformation;
import org.netbeans.modules.mercurial.Mercurial;
import org.netbeans.modules.mercurial.OutputLogger;
import org.netbeans.modules.mercurial.HgProgressSupport;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.mercurial.util.HgUtils;
import org.netbeans.modules.mercurial.ui.actions.ContextAction;
import org.netbeans.modules.versioning.util.Utils;
import org.netbeans.api.diff.Difference;
import org.netbeans.spi.diff.DiffProvider;
import org.openide.windows.TopComponent;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.NbBundle;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import org.netbeans.modules.mercurial.HgModuleConfig;
import org.netbeans.modules.versioning.util.ExportDiffSupport;

/**
 * Exports diff to file:
 *
 * <ul>
 * <li>for components that implements {@link DiffSetupSource} interface
 * exports actually displayed diff.
 *
 * <li>for DataNodes <b>local</b> differencies between the current
 * working copy and BASE repository version.
 * </ul>
 *  
 * @author Petr Kuzel
 */
public class ExportDiffChangesAction extends ContextAction {

    private final VCSContext context;

    public ExportDiffChangesAction(String name, VCSContext context) {
        this.context = context;
        putValue(Action.NAME, name);
    }

    public boolean isEnabled () {
        if(!HgUtils.isFromHgRepository(context)) {
            return false;
        }  
        TopComponent activated = TopComponent.getRegistry().getActivated();
        if (activated instanceof DiffSetupSource) {
            return true;
        }
        return super.isEnabled() && Lookup.getDefault().lookup(DiffProvider.class) != null;                
    }

    public void performAction(ActionEvent e) {
        boolean noop;
        TopComponent activated = TopComponent.getRegistry().getActivated();
        if (activated instanceof DiffSetupSource) {
            noop = ((DiffSetupSource) activated).getSetups().isEmpty();
        } else {
            // XXX containsFileOfStatus would be better (do not test exclusions from commit)
            File [] files = HgUtils.getModifiedFiles(context, FileInformation.STATUS_LOCAL_CHANGE, false);
            noop = files.length == 0;
        }
        if (noop) {
            NotifyDescriptor msg = new NotifyDescriptor.Message(NbBundle.getMessage(ExportDiffChangesAction.class, "BK3001"), NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(msg);
            return;
        }

        final File root = HgUtils.getRootFile(context);
        ExportDiffSupport exportDiffSupport = new ExportDiffSupport(new File[] {root}, HgModuleConfig.getDefault().getPreferences()) {
            @Override
            public void writeDiffFile(final File toFile) {
                ExportDiffAction.saveFolderToPrefs(toFile);
                RequestProcessor rp = Mercurial.getInstance().getRequestProcessor(root);
                HgProgressSupport ps = new HgProgressSupport() {
                    protected void perform() {
                        OutputLogger logger = getLogger();
                        async(this, context, toFile, logger);
                    }
                };
                ps.start(rp, root, org.openide.util.NbBundle.getMessage(ExportDiffChangesAction.class, "LBL_ExportChanges_Progress")).waitFinished();
            }
        };
        exportDiffSupport.export();

    }

    protected boolean asynchronous() {
        return false;
    }
    
    @SuppressWarnings("unchecked")
    private void async(HgProgressSupport progress, VCSContext context, File destination, OutputLogger logger) {
        boolean success = false;
        OutputStream out = null;
        int exportedFiles = 0;
        try {

            // prepare setups and common parent - root

            File root;
            List<Setup> setups;

            TopComponent activated = TopComponent.getRegistry().getActivated();
            if (activated instanceof DiffSetupSource) {
                setups = new ArrayList<Setup>(((DiffSetupSource) activated).getSetups());
                List<File> setupFiles = new ArrayList<File>(setups.size());
                for (Iterator i = setups.iterator(); i.hasNext();) {
                    Setup setup = (Setup) i.next();
                    setupFiles.add(setup.getBaseFile()); 
                }
                root = getCommonParent(setupFiles.toArray(new File[setupFiles.size()]));
            } else {
                // XXX containsFileOfStatus would be better (do not test exclusions from commit)
                File [] files = HgUtils.getModifiedFiles(context, FileInformation.STATUS_LOCAL_CHANGE, false);
                root = getCommonParent(context.getRootFiles().toArray(new File[context.getRootFiles().size()]));
                setups = new ArrayList<Setup>(files.length);
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    Mercurial.LOG.log(Level.INFO, "setups {0}", file);
                    Setup setup = new Setup(file, null, Setup.DIFFTYPE_LOCAL);
                    Mercurial.LOG.log(Level.INFO, "setup {0}", setup.getBaseFile());
                    setups.add(setup);
                }
            }
            if (root == null) {
                NotifyDescriptor nd = new NotifyDescriptor(
                        NbBundle.getMessage(ExportDiffChangesAction.class, "MSG_BadSelection_Prompt"), 
                        NbBundle.getMessage(ExportDiffChangesAction.class, "MSG_BadSelection_Title"), 
                        NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.ERROR_MESSAGE, null, null);
                DialogDisplayer.getDefault().notify(nd);
                return;
            }

            logger.outputInRed(
                    NbBundle.getMessage(ExportDiffChangesAction.class,
                    "MSG_EXPORT_CHANGES_TITLE")); // NOI18N
            logger.outputInRed(
                    NbBundle.getMessage(ExportDiffChangesAction.class,
                    "MSG_EXPORT_CHANGES_TITLE_SEP")); // NOI18N
            logger.outputInRed(NbBundle.getMessage(ExportDiffChangesAction.class, "MSG_EXPORT_CHANGES", destination)); // NOI18N

            String sep = System.getProperty("line.separator"); // NOI18N
            out = new BufferedOutputStream(new FileOutputStream(destination));
            // Used by PatchAction as MAGIC to detect right encoding
            out.write(("# This patch file was generated by NetBeans IDE" + sep).getBytes("utf8"));  // NOI18N
            out.write(("# Following Index: paths are relative to: " + root.getAbsolutePath() + sep).getBytes("utf8"));  // NOI18N
            out.write(("# This patch can be applied using context Tools: Patch action on respective folder." + sep).getBytes("utf8"));  // NOI18N
            out.write(("# It uses platform neutral UTF-8 encoding and \\n newlines." + sep).getBytes("utf8"));  // NOI18N
            out.write(("# Above lines and this line are ignored by the patching process." + sep).getBytes("utf8"));  // NOI18N


            Collections.sort(setups, new Comparator<Setup>() {
                public int compare(Setup o1, Setup o2) {
                    return o1.getBaseFile().compareTo(o2.getBaseFile());
                }
            });
            Iterator<Setup> it = setups.iterator();
            int i = 0;
            while (it.hasNext()) {
                Setup setup = it.next();
                File file = setup.getBaseFile();                
                Mercurial.LOG.log(Level.INFO, "setup {0}", file.getName());
                if (file.isDirectory()) continue;
                progress.setDisplayName(file.getName());

                String index = "Index: ";   // NOI18N
                String rootPath = root.getAbsolutePath();
                String filePath = file.getAbsolutePath();
                String relativePath = filePath;
                if (filePath.startsWith(rootPath)) {
                    relativePath = filePath.substring(rootPath.length() + 1).replace(File.separatorChar, '/');
                    index += relativePath + sep;
                    out.write(index.getBytes("utf8")); // NOI18N
                }
                exportDiff(setup, relativePath, out);
                i++;
            }

            exportedFiles = i;
            success = true;
        } catch (IOException ex) {
            Mercurial.LOG.log(Level.INFO, NbBundle.getMessage(ExportDiffChangesAction.class, "BK3003"), ex);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException alreadyClsoed) {
                }
            }
            if (success) {
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(ExportDiffChangesAction.class, "BK3004", new Integer(exportedFiles)));
                if (exportedFiles == 0) {
                    destination.delete();
                } else {
                    Utils.openFile(destination);
                }
            } else {
                destination.delete();
            }
            logger.outputInRed(NbBundle.getMessage(ExportDiffChangesAction.class, "MSG_EXPORT_CHANGES_DONE")); // NOI18N
            logger.output(""); // NOI18N


        }
    }

    private static File getCommonParent(File [] files) {
        File root = files[0];
        if (root.isFile()) root = root.getParentFile();
        for (int i = 1; i < files.length; i++) {
            root = Utils.getCommonParent(root, files[i]);
            if (root == null) return null;
        }
        return root;
    }

    /** Writes contextual diff into given stream.*/
    private void exportDiff(Setup setup, String relativePath, OutputStream out) throws IOException {
        setup.initSources();
        DiffProvider diff = (DiffProvider) Lookup.getDefault().lookup(DiffProvider.class);

        Reader r1 = null;
        Reader r2 = null;
        Difference[] differences;

        try {
            r1 = setup.getFirstSource().createReader();
            if (r1 == null) r1 = new StringReader("");  // NOI18N
            r2 = setup.getSecondSource().createReader();
            if (r2 == null) r2 = new StringReader("");  // NOI18N
            differences = diff.computeDiff(r1, r2);
        } finally {
            if (r1 != null) try { r1.close(); } catch (Exception e) {}
            if (r2 != null) try { r2.close(); } catch (Exception e) {}
        }

        try {
            InputStream is;
                r1 = setup.getFirstSource().createReader();
                if (r1 == null) r1 = new StringReader(""); // NOI18N
                r2 = setup.getSecondSource().createReader();
                if (r2 == null) r2 = new StringReader(""); // NOI18N
                TextDiffVisualizer.TextDiffInfo info = new TextDiffVisualizer.TextDiffInfo(
                    relativePath + " " + setup.getFirstSource().getTitle(), // NOI18N
                    relativePath + " " + setup.getSecondSource().getTitle(),  // NOI18N
                    null,
                    null,
                    r1,
                    r2,
                    differences
                );
                info.setContextMode(true, 3);
                String diffText = TextDiffVisualizer.differenceToUnifiedDiffText(info);
                is = new ByteArrayInputStream(diffText.getBytes("utf8"));  // NOI18N
            //}
            while(true) {
                int i = is.read();
                if (i == -1) break;
                out.write(i);
            }
        } finally {
            if (r1 != null) try { r1.close(); } catch (Exception e) {}
            if (r2 != null) try { r2.close(); } catch (Exception e) {}
        }
    }
}
