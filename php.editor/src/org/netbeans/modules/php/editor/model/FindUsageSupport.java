/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.editor.model;

import java.util.*;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.php.editor.api.ElementQuery;
import org.netbeans.modules.php.editor.api.NameKind;
import org.netbeans.modules.php.editor.api.PhpElementKind;
import org.netbeans.modules.php.editor.api.elements.ElementFilter;
import org.netbeans.modules.php.editor.api.elements.MethodElement;
import org.netbeans.modules.php.editor.api.elements.TypeElement;
import org.netbeans.modules.php.editor.model.impl.ModelVisitor;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.project.api.PhpSourcePath;
import org.netbeans.modules.php.project.api.PhpSourcePath.FileType;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 * @author Radek Matous
 */
public final class FindUsageSupport {

    private Set<FileObject> files;
    private ModelElement element;
    private ElementQuery.Index index;

    public static FindUsageSupport getInstance(ElementQuery.Index index, ModelElement element) {
        return new FindUsageSupport(index, element);
    }

    private FindUsageSupport(ElementQuery.Index index, ModelElement element) {
        this.element = element;
        this.files = new LinkedHashSet<FileObject>();
        this.index = index;
    }

    public Collection<MethodElement> overridingMethods() {
        if (element instanceof MethodElement) {
            MethodElement method = (MethodElement) element;
            TypeElement type = method.getType();
            HashSet inheritedByMethods = new HashSet<MethodElement>();
            for (TypeElement nextType : index.getInheritedByTypes(type)) {
                inheritedByMethods.addAll(index.getDeclaredMethods(nextType));
            }
            return ElementFilter.forName(NameKind.exact(method.getName())).filter(inheritedByMethods);
        } else if (element instanceof MethodScope) {
            MethodScope method = (MethodScope) element;
            TypeScope type = (TypeScope) method.getInScope();
            HashSet inheritedByMethods = new HashSet<MethodElement>();
            for (TypeElement nextType : index.getInheritedByTypes(type)) {
                inheritedByMethods.addAll(index.getDeclaredMethods(nextType));
            }
            return ElementFilter.forName(NameKind.exact(method.getName())).filter(inheritedByMethods);
        }

        return Collections.emptyList();
    }

    public Collection<TypeElement> subclasses() {
        if (element instanceof TypeElement) {
            return index.getInheritedByTypes((TypeElement) element);
        }
        return Collections.emptySet();
    }

    public Collection<TypeElement> directSubclasses() {
        if (element instanceof TypeElement) {
            return index.getDirectInheritedByTypes((TypeElement) element);
        }
        return Collections.emptySet();
    }

    @CheckForNull
    public Collection<Occurence> occurences(FileObject fileObject) {
        final Set<Occurence> retval = new TreeSet<Occurence>(new Comparator<Occurence>() {

            @Override
            public int compare(Occurence o1, Occurence o2) {
                return o1.getOccurenceRange().compareTo(o2.getOccurenceRange());
            }
        });
        if (fileObject != null && fileObject.isValid()) {
            try {
                ParserManager.parse(Collections.singleton(Source.create(fileObject)), new UserTask() {

                    @Override
                    public void run(ResultIterator resultIterator) throws Exception {
                        Result parameter = resultIterator.getParserResult();
                        if (parameter != null && parameter instanceof PHPParseResult) {
                            Model model = ModelFactory.getModel((PHPParseResult) parameter);
                            ModelVisitor modelVisitor = model.getModelVisitor();
                            retval.addAll(modelVisitor.getOccurence(element));
                        }
                    }
                });
            } catch (org.netbeans.modules.parsing.spi.ParseException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return retval;
    }

    /**
     * @return the files
     */
    public Set<FileObject> inFiles() {
        synchronized (this) {
            if (this.files.isEmpty()) {
                this.files.add(element.getFileObject());
                String name = element.getName();
                final PhpElementKind kind = element.getPhpElementKind();
                if (kind.equals(PhpElementKind.VARIABLE) || kind.equals(PhpElementKind.FIELD)) {
                    name = name.startsWith("$") ? name.substring(1) : name;
                } else if (kind.equals(PhpElementKind.METHOD) && MethodElement.CONSTRUCTOR_NAME.equalsIgnoreCase(name)) {
                    name = element.getInScope().getName();
                }
                for (FileObject fo : index.getLocationsForIdentifiers(name)) {
                    FileType fileType = PhpSourcePath.getFileType(fo);
                    if (fileType == PhpSourcePath.FileType.SOURCE
                            || fileType == PhpSourcePath.FileType.TEST) {
                        this.files.add(fo);
                    }
                }
            }
        }
        return files;
    }

    /**
     * @return the element
     */
    public ModelElement elementToFind() {
        return element;
    }
}
