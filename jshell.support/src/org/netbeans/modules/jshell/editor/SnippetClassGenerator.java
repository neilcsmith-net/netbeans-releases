/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.jshell.editor;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ImportTree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import jdk.jshell.JShellAccessor;
import jdk.jshell.Snippet;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.project.Project;
import org.netbeans.api.templates.FileBuilder;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.netbeans.modules.java.hints.OrganizeImports;
import org.netbeans.modules.java.source.save.Reformatter;
import org.netbeans.modules.jshell.support.ShellSession;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author sdedic
 */
public class SnippetClassGenerator implements Runnable {
    private final Project project;
    private final ShellSession shellSession;
    private final FileObject targetFolder;
    private final String    className;
    private FileObject javaFile;
    private StringBuilder executableContent = new StringBuilder();
    private StringBuilder declarativeConent = new StringBuilder();
    private List<Snippet> liveSnippets;
    
    private Throwable error;

    public SnippetClassGenerator(Project project, ShellSession shellSession, FileObject targetFolder, String className) {
        this.project = project;
        this.shellSession = shellSession;
        this.targetFolder = targetFolder;
        this.className = className;
    }
    
    public Throwable getError() {
        return error;
    }
    
    @NbBundle.Messages({
        "EXC_UnexpectedTemplateContents=Unexpected plain class template contents",
        "EXC_ShellTemplateMissing=Unexpected plain class template contents"
    })
    private FileObject createJavaFile() throws IOException {
        FileObject template = FileUtil.getConfigFile("Templates/Classes/ShellClass.java"); // NOI18N
        if (template == null) {
            throw new IOException(Bundle.EXC_ShellTemplateMissing());
        }
        FileBuilder builder = new FileBuilder(template, targetFolder);
        builder.name(className);
        builder.param("executables", executableContent.toString());
        builder.param("declaratives", declarativeConent.toString());
        
        Collection<FileObject> l = builder.build();
        if (l.size() != 1) {
            throw new IOException(Bundle.EXC_UnexpectedTemplateContents());
        }
        return l.iterator().next();
    }

    /**
     * Copies all 'non-persistent' statements and expressions into
     * a method.
     */
    private void createStatementsText() {
        for (Snippet s : liveSnippets) {
            if (s.kind().isPersistent || s.kind() == Snippet.Kind.IMPORT) {
                continue;
            }
            String text = s.source();
            executableContent.append(text);
            if (!text.endsWith(";") && !text.endsWith("}")) {
                executableContent.append(";");
            }
            executableContent.append("\n"); // NOI18N
        }
    }
    
    private void prepareDeclarations() {
        for (Snippet s : liveSnippets) {
            if (!s.kind().isPersistent) {
                continue;
            }
            if (s.kind() == Snippet.Kind.IMPORT) {
                continue;
            }
            String text = s.source();
            if (declarativeConent.length() > 0) {
                // force some newline
                declarativeConent.append("\n"); // NOI18N
            }
            declarativeConent.append(text);
            if (!text.endsWith(";") && !text.endsWith("}")) {
                declarativeConent.append(";");
            }
            declarativeConent.append("\n"); // NOI18N
        }
    }
    
    private void prepareImports() {
    }
    
    private void copyImports() {
        List<ImportTree> imps = new ArrayList<>();
        for (Snippet s : shellSession.getSnippets(false, true)) {
            if (s.kind() != Snippet.Kind.IMPORT) {
                continue;
            }
            String importText = s.source();
            int ii = importText.indexOf("import");
            if (ii == -1) {
                continue;
            }
            String ident = importText.substring(ii + 6 /* length of import */).trim();
            if (ident.endsWith(";")) {
                ident = ident.substring(0, ident.length() - 1);
            }
            boolean stat = ident.startsWith("static");
            if (stat) {
                // do not import stuff from REPL classes:
                ident = ident.substring(6 /* length of static */).trim();

                if (ident.startsWith("REPL.$$")) {
                    continue;
                }
            }
            ExpressionTree qi = copy.getTreeMaker().QualIdent(ident);
            imps.add(copy.getTreeMaker().Import(qi, stat));
        }
        
        CompilationUnitTree t = copy.getCompilationUnit();
        for (ImportTree i : imps) {
            t = copy.getTreeMaker().addCompUnitImport(t, i);
        }
        copy.rewrite(copy.getCompilationUnit(), t);
    }
    
    private WorkingCopy copy;
    

    @Override
    public void run() {
        liveSnippets = shellSession.getSnippets(true, true);
        prepareDeclarations();
        createStatementsText();
        prepareImports();
        try {
            FileObject replaced = targetFolder.getFileObject(className, "java");
            if (replaced != null) {
                replaced.delete();
            }
            javaFile = createJavaFile();
            JavaSource src = JavaSource.forFileObject(javaFile);
            if (src == null) {
                return;
            }
            src.runModificationTask(wc ->  {
                wc.toPhase(JavaSource.Phase.RESOLVED);
               this.copy = wc;
               copyImports();
            }).commit();
            
            // reformat
            EditorCookie editor = javaFile.getLookup().lookup(EditorCookie.class);
            Document d = editor.openDocument();
            Reformat r = Reformat.get(d);
            r.lock();
            try {
                r.reformat(0, d.getLength());
            } finally {
                r.unlock();
            }
            // not organize those imports; must run a separate task, so the
            // analyzer sees the text:
            src.runModificationTask(wc ->  {
                wc.toPhase(JavaSource.Phase.RESOLVED);
               this.copy = wc;
               OrganizeImports.doOrganizeImports(copy, null, true);
            }).commit();
            editor.saveDocument();
        } catch (IOException ex) {
            error = ex;
        } catch (BadLocationException ex) {
            error = ex;
        }
    }
    
    public FileObject getJavaFile() {
        return javaFile;
    }
}
