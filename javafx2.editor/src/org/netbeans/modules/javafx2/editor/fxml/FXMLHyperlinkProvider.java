/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javafx2.editor.fxml;

import java.awt.Toolkit;
import java.util.EnumSet;
import java.util.Set;
import javax.swing.text.Document;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.project.*;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProviderExt;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkType;
import org.netbeans.modules.javafx2.editor.JavaFXEditorUtils;
import org.openide.cookies.EditCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author Anton Chechel <anton.chechel@oracle.com>
 */
// TODO <ImageView image="@my_image.png"/> support
// TODO <Button text="Click Me!" onAction="java.lang.System.out.println('You clicked me!');"/> support
// TODO <Button text="Click Me!" onAction="#handleButtonAction"/> support
 
@MimeRegistration(mimeType=JavaFXEditorUtils.FXML_MIME_TYPE, service=HyperlinkProviderExt.class, position = 1000)
public class FXMLHyperlinkProvider implements HyperlinkProviderExt {

    @Override
    public Set<HyperlinkType> getSupportedHyperlinkTypes() {
        return EnumSet.of(HyperlinkType.GO_TO_DECLARATION);
    }

    @Override
    public boolean isHyperlinkPoint(Document doc, int offset, HyperlinkType type) {
        return getHyperlinkSpan(doc, offset, type) != null;
    }

    @Override
    public int[] getHyperlinkSpan(Document doc, int offset, HyperlinkType type) {
        return getIdentifierSpan(doc, offset);
    }

    @Override
    public void performClickAction(Document doc, int offset, HyperlinkType type) {
        EditCookie ec = getEditorCookie(doc, offset);
        if (ec != null) {
            ec.edit();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    // TODO
    @Override
    public String getTooltipText(Document doc, int offset, HyperlinkType type) {
        return null;
    }

    private EditCookie getEditorCookie(Document doc, int offset) {
        TokenHierarchy<?> th = TokenHierarchy.get(doc);

        TokenSequence ts = th.tokenSequence(Language.find(JavaFXEditorUtils.FXML_MIME_TYPE));
        if (ts == null) {
            return null;
        }

        ts.move(offset);
        if (!ts.moveNext()) {
            return null;
        }

        Token t = ts.token();
        FileObject fo = getFileObject(doc);
        String name = t.text().toString();

        FileObject props = findFile(fo, name);
        if (props != null) {
            try {
                DataObject dobj = DataObject.find(props);
                return dobj.getLookup().lookup(EditCookie.class);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }

    private static @CheckForNull
    FileObject findFile(FileObject docFO, String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        
        Project prj = FileOwnerQuery.getOwner(docFO);
        if (prj == null) {
            return null;
        }
        
        Sources srcs = ProjectUtils.getSources(prj);
        SourceGroup[] grps = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        if (grps.length == 0) {
            return null;
        }

        // XXX other source roots?
        final FileObject rootFolder = grps[0].getRootFolder();
        ClassPath cp = ClassPath.getClassPath(rootFolder, ClassPath.SOURCE);
        if (cp == null) {
            return null;
        }
        
        FileObject fo;
        String rootPath = FileUtil.normalizePath(rootFolder.getPath());
        String docPath = FileUtil.normalizePath(docFO.getParent().getPath());
        if (!docPath.startsWith(rootPath)) {
            // #228262 sanity check, for files which are outside of any source root
            return null;
        }

        // Java Controller
        String javaPath = path.trim().replace("\"", "").replace('.', '/') + ".java"; // NOI18N
        fo = cp.findResource(javaPath);
        if (fo == null) {
            javaPath = docPath.substring(rootPath.length()) + '/' + javaPath; // NOI18N
            fo = cp.findResource(javaPath);
        }
        
        // CSS file
        if (fo == null) {
            // try short path
            String cssPath = path.trim().replace("\"", "").replace("@", ""); // NOI18N
            fo = cp.findResource(cssPath);
            // try full path
            if (fo == null) {
                cssPath = docPath.substring(rootPath.length()) + '/' + cssPath; // NOI18N
                fo = cp.findResource(cssPath);
            }
        }
        return fo;
    }

    public static int[] getIdentifierSpan(Document doc, int offset) {
        FileObject fo = getFileObject(doc);
        if (fo == null) {
            //do nothing if FO is not attached to the document - the goto would not work anyway:
            return null;
        }
        Project prj = FileOwnerQuery.getOwner(fo);
        if (prj == null) {
            return null;
        }

//        NbModuleProvider module = prj.getLookup().lookup(NbModuleProvider.class);
//        if (module == null) {
//            return null;
//        }

        TokenHierarchy<?> th = TokenHierarchy.get(doc);
        TokenSequence ts = th.tokenSequence(Language.find(JavaFXEditorUtils.FXML_MIME_TYPE));
        if (ts == null) {
            return null;
        }

        ts.move(offset);
        if (!ts.moveNext()) {
            return null;
        }
        Token t = ts.token();
        if (findFile(fo, t.text().toString()) != null) {
            return new int[]{ts.offset() + 1, ts.offset() + t.length() - 1};
        }
        return null;

    }

    private static FileObject getFileObject(Document doc) {
        DataObject od = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);

        return od != null ? od.getPrimaryFile() : null;
    }
}
