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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.editor.elements;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport.Kind;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpClass.Field;
import org.netbeans.modules.php.api.editor.PhpVariable;
import org.netbeans.modules.php.editor.api.AbstractElementQuery;
import org.netbeans.modules.php.editor.api.AliasedName;
import org.netbeans.modules.php.editor.api.NameKind;
import org.netbeans.modules.php.editor.api.NameKind.Exact;
import org.netbeans.modules.php.editor.api.elements.AliasedElement.Trait;
import org.netbeans.modules.php.editor.api.elements.PhpElement;
import org.netbeans.modules.php.editor.api.elements.ClassElement;
import org.netbeans.modules.php.editor.api.elements.ConstantElement;
import org.netbeans.modules.php.editor.api.elements.ElementFilter;
import org.netbeans.modules.php.editor.api.ElementQuery;
import org.netbeans.modules.php.editor.api.PhpElementKind;
import org.netbeans.modules.php.editor.api.elements.FieldElement;
import org.netbeans.modules.php.editor.api.elements.FunctionElement;
import org.netbeans.modules.php.editor.api.elements.InterfaceElement;
import org.netbeans.modules.php.editor.api.elements.MethodElement;
import org.netbeans.modules.php.editor.api.elements.NamespaceElement;
import org.netbeans.modules.php.editor.api.elements.TypeConstantElement;
import org.netbeans.modules.php.editor.api.elements.TypeElement;
import org.netbeans.modules.php.editor.api.elements.TreeElement;
import org.netbeans.modules.php.editor.api.elements.VariableElement;
import org.netbeans.modules.php.editor.index.PHPIndexer;
import org.netbeans.modules.php.editor.api.QualifiedName;
import org.netbeans.modules.php.editor.api.elements.AliasedClass;
import org.netbeans.modules.php.editor.api.elements.AliasedConstant;
import org.netbeans.modules.php.editor.api.elements.AliasedElement;
import org.netbeans.modules.php.editor.api.elements.AliasedFunction;
import org.netbeans.modules.php.editor.api.elements.AliasedInterface;
import org.netbeans.modules.php.editor.api.elements.AliasedNamespace;
import org.netbeans.modules.php.editor.api.elements.AliasedType;
import org.netbeans.modules.php.editor.api.elements.TraitElement;
import org.netbeans.modules.php.editor.api.elements.TraitedElement;
import org.netbeans.modules.php.editor.api.elements.TypeMemberElement;
import org.netbeans.modules.php.editor.index.Signature;
import org.netbeans.modules.php.editor.model.Model;
import org.netbeans.modules.php.editor.model.NamespaceScope;
import org.netbeans.modules.php.editor.model.UseElement;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;

/**
 * @author Radek Matous
 */
public final class IndexQueryImpl implements ElementQuery.Index {

    public static final String FIELD_TOP_LEVEL = PHPIndexer.FIELD_TOP_LEVEL;
    private static final Logger LOG = Logger.getLogger(IndexQueryImpl.class.getName());
    private static Collection<NamespaceElement> namespacesCache = null;
    private final QuerySupport index;
    private final Set<AliasedName> aliases = new HashSet<AliasedName>();
    private AbstractElementQuery extendedQuery = new AbstractElementQuery(QueryScope.VIRTUAL_SCOPE);

    /** Creates a new instance of JsIndex */
    private IndexQueryImpl(QuerySupport index, final Model model) {
        this.index = index;
        if (model != null) {
            init(model);
        }
    }

    private void init(final Model model) {
        initExtendedQuery(model);
        initAliases(model);
    }

    /**
     * Intended to be used for fake additions of aliases that are returned
     * as if they were in index
     */
    private void initAliases(final Model model) {
        for (final NamespaceScope namespaceScope : model.getFileScope().getDeclaredNamespaces()) {
            if (namespaceScope != null) {
                Collection<? extends UseElement> declaredUses = namespaceScope.getDeclaredUses();
                for (UseElement useElement : declaredUses) {
                    AliasedName aliasedName = useElement.getAliasedName();
                    if (aliasedName != null) {
                        aliases.add(aliasedName);
                    }
                }
            }
        }
    }

    /**
     * Intended to be used for fake additions of frameworks that are returned
     * as if they were in index
     */
    private void initExtendedQuery(final Model model) {
        //TODO: the best approach would be the frameworks returned ElementQuery instance
        //and get rid of PhpBaseElement and its subclasses
        List<PhpBaseElement> extendedElements = model.getExtendedElements();
        for (PhpBaseElement phpBaseElement : extendedElements) {
            if (phpBaseElement instanceof PhpVariable) {
                PhpVariable variable = (PhpVariable) phpBaseElement;
                extendedQuery.addElement(VariableElementImpl.fromFrameworks(variable, extendedQuery));
                final PhpClass type = variable.getType();
                if (type != null) {
                    final ClassElement classElement = ClassElementImpl.fromFrameworks(type, extendedQuery);
                    extendedQuery.addElement(classElement);
                    for (final Field field : type.getFields()) {
                        final FieldElement fieldElement = FieldElementImpl.fromFrameworks(classElement, field, extendedQuery);
                        extendedQuery.addElement(fieldElement);
                    }
                }
            }
        }
    }
    private IndexQueryImpl(QuerySupport index) {
        this(index, null);
    }

    public static Index create(final QuerySupport querySupport) {
        return new IndexQueryImpl(querySupport);
    }
    public static Index create(final QuerySupport querySupport, final Model model) {
        return new IndexQueryImpl(querySupport, model);
    }
    public static Index getModelInstance(final PHPParseResult parseResult) {
        return parseResult.getModel().getIndexScope().getIndex();
    }
    public static void clearNamespaceCache() {
        synchronized(IndexQueryImpl.class) {
            namespacesCache = null;
        }
    }

    private Set<ClassElement> getClassesImpl(final NameKind query) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<ClassElement> classes = new HashSet<ClassElement>();
        final Collection<? extends IndexResult> result = results(ClassElementImpl.IDX_FIELD, query);
        for (final IndexResult indexResult : result) {
            Set<ClassElement> allClasses = ClassElementImpl.fromSignature(query, this, indexResult);
            if (query.isPrefix()) {
                classes.addAll(allClasses);
            } else {
                for (ClassElement classElement : allClasses) {
                    if (query.getQuery().getNamespaceName().equals(classElement.getNamespaceName().toString())) {
                        classes.add(classElement);
                    }
                }
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<ClassElement> getClasses", query, start);//NOI18N
        }
        return Collections.unmodifiableSet(classes);
    }



    private Set<InterfaceElement> getInterfacesImpl(final NameKind query) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<InterfaceElement> ifaces = new HashSet<InterfaceElement>();
        final Collection<? extends IndexResult> result = results(InterfaceElementImpl.IDX_FIELD, query);
        for (final IndexResult indexResult : result) {
            ifaces.addAll(InterfaceElementImpl.fromSignature(query, this, indexResult));
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<InterfaceElement> getInterfaces", query, start);//NOI18N
        }
        return Collections.unmodifiableSet(ifaces);
    }

    private Set<TraitElement> getTraitsImpl(final NameKind query) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<TraitElement> ifaces = new HashSet<TraitElement>();
        final Collection<? extends IndexResult> result = results(TraitElementImpl.IDX_FIELD, query);
        for (final IndexResult indexResult : result) {
            ifaces.addAll(TraitElementImpl.fromSignature(query, this, indexResult));
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<TraitElement> getTraits", query, start);//NOI18N
        }
        return Collections.unmodifiableSet(ifaces);
    }

    private static Set<NameKind> queriesForAlias(final NameKind query, final AliasedName aliasedName, final PhpElementKind elementKind) {
        final Set<NameKind> aliasQueries = new HashSet<NameKind>();
        final boolean fullyQualified = query.getQuery().getKind().isFullyQualified();
        final Kind queryKind = query.getQueryKind();
        final LinkedList<String> segments = query.getQuery().getSegments();
        for (int i = 0; i < segments.size(); i++) {
            final String nextSegment = segments.get(i);
            if (!nextSegment.isEmpty() && (i == 0 || (i == segments.size() - 1)) && (!nextSegment.isEmpty() || segments.size() == 1)
                    && NameKind.create(nextSegment, queryKind).matchesName(elementKind, aliasedName.getAliasName())) {
                final LinkedList<String> nSegments = new LinkedList<String>();
                nSegments.addAll(segments.subList(0, i));
                nSegments.addAll(aliasedName.getRealName().getSegments());
                if (i + 1 < segments.size()) {
                    nSegments.addAll(segments.subList(i + 1, segments.size()));
                }
                aliasQueries.add(NameKind.create(QualifiedName.create(fullyQualified, nSegments), queryKind));
            }
        }
        return aliasQueries;
    }

    private Set<TypeElement> getTypesImpl(NameKind query) {
        final Set<TypeElement> types = new HashSet<TypeElement>();
        types.addAll(getClassesImpl(query));
        types.addAll(getInterfacesImpl(query));
        types.addAll(getTraitsImpl(query));
        return types;
    }


    @Override
    public final Set<MethodElement> getAccessibleMagicMethods(final TypeElement type) {
        return MethodElementImpl.getMagicMethods(type);
    }

    private final Set<FunctionElement> getFunctionsImpl(final NameKind query) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<FunctionElement> functions = new HashSet<FunctionElement>();
        final Collection<? extends IndexResult> result = results(FunctionElementImpl.IDX_FIELD, query);
        for (final IndexResult indexResult : result) {
            functions.addAll(FunctionElementImpl.fromSignature(query, this, indexResult));
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<FunctionElement> getFunctions", query, start);//NOI18N
        }
        return Collections.unmodifiableSet(functions);
    }

    private Set<NamespaceElement> getNamespacesImpl(final NameKind query) {
        final Set<NamespaceElement> retval = new HashSet<NamespaceElement>();
        synchronized (IndexQueryImpl.class) {
            if (namespacesCache == null) {
                final Map<String, NamespaceElement> namespacesMap = new LinkedHashMap<String, NamespaceElement>();
                for (final NamespaceElement namespace : namespacesImpl(NameKind.empty())) {
                    NamespaceElement original = null;
                    QualifiedName qn = namespace.getFullyQualifiedName();
                    while (original == null && !qn.isDefaultNamespace()) {
                        original = namespacesMap.put(qn.toFullyQualified().toString().toLowerCase(),
                                new NamespaceElementImpl(qn, namespace.getOffset(), namespace.getFilenameUrl(),namespace.getElementQuery()));
                        qn = qn.toNamespaceName();
                    }
                }
                namespacesCache = namespacesMap.values();
            }
        }
        for (final NamespaceElement indexedNamespace : namespacesCache) {
            if (query.matchesName(indexedNamespace)) {
                retval.add(indexedNamespace);
            }
        }
        return retval;
    }

    private Set<NamespaceElement> namespacesImpl(NameKind query) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        //TODO: not cached yet
        final Set<NamespaceElement> namespaces = new HashSet<NamespaceElement>();
        final Collection<? extends IndexResult> result = results(NamespaceElementImpl.IDX_FIELD, query);
        for (final IndexResult indexResult : result) {
            namespaces.addAll(NamespaceElementImpl.fromSignature(query, this, indexResult));
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<NamespaceElement> getNamespaces", query, start);//NOI18N
        }
        return Collections.unmodifiableSet(namespaces);
    }


    private Set<ConstantElement> getConstantsImpl(final NameKind query) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<ConstantElement> constants = new HashSet<ConstantElement>();
        final Collection<? extends IndexResult> result = results(ConstantElementImpl.IDX_FIELD, query);
        for (final IndexResult indexResult : result) {
            constants.addAll(ConstantElementImpl.fromSignature(query, this, indexResult));
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<ConstantElement> getConstants", query, start);//NOI18N
        }
        return Collections.unmodifiableSet(constants);
    }

    private Set<PhpElement> getTopLevelElementsImpl(NameKind query) {
        final boolean isVariable = query.getQueryName().startsWith(VariableElementImpl.DOLLAR_PREFIX);
        final String[] fieldsToLoad = isVariable ?
            new String[] {
            PHPIndexer.FIELD_VAR
        } : new String[] {
            PHPIndexer.FIELD_BASE,
            PHPIndexer.FIELD_CONST,
            PHPIndexer.FIELD_CLASS,
            PHPIndexer.FIELD_IFACE,
            PHPIndexer.FIELD_NAMESPACE,
            PHPIndexer.FIELD_TRAIT
        };
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<PhpElement> elements = new HashSet<PhpElement>();
        final Collection<? extends IndexResult> result = results(FIELD_TOP_LEVEL, query, fieldsToLoad);
        for (final IndexResult indexResult : result) {
            if (isVariable) {
                elements.addAll(VariableElementImpl.fromSignature(query, this, indexResult));
            } else {
                elements.addAll(ClassElementImpl.fromSignature(query, this, indexResult));
                elements.addAll(InterfaceElementImpl.fromSignature(query, this, indexResult));
                elements.addAll(FunctionElementImpl.fromSignature(query, this, indexResult));
                elements.addAll(ConstantElementImpl.fromSignature(query, this, indexResult));
                elements.addAll(TraitElementImpl.fromSignature(query, this, indexResult));
            }
        }
        if (isVariable) {
            elements.addAll(extendedQuery.getTopLevelVariables(query));
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<PhpElement> getTopLevelElements", query, start);//NOI18N
        }
        return Collections.unmodifiableSet(elements);
    }

    @Override
    public final Set<VariableElement> getTopLevelVariables(final NameKind query) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<VariableElement> vars = new HashSet<VariableElement>();
        final Collection<? extends IndexResult> result = results(VariableElementImpl.IDX_FIELD, query);
        for (final IndexResult indexResult : result) {
            vars.addAll(VariableElementImpl.fromSignature(query, this, indexResult));
        }
        vars.addAll(extendedQuery.getTopLevelVariables(query));
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<VariableElement> getTopLevelVariables", query, start);//NOI18N
        }
        return Collections.unmodifiableSet(vars);
    }


    @Override
    public Set<MethodElement> getConstructors(final ClassElement classElement) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<MethodElement> retval = getConstructorsImpl( classElement, classElement, new LinkedHashSet<ClassElement>());
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<MethodElement> getConstructors", NameKind.exact(classElement.getFullyQualifiedName()), start);//NOI18N
        }
        return retval.isEmpty() ? getDefaultConstructors(classElement) : retval;
    }

    private Set<MethodElement> getConstructorsImpl(NameKind typeQuery) {
        final Set<MethodElement> retval = new HashSet<MethodElement>();
        final Set<ClassElement> classes = getClassesImpl(typeQuery);
        for (ClassElement classElement : classes) {
            retval.addAll(getConstructors(classElement));
        }
        return retval;
    }

    private final Set<MethodElement> getConstructorsImpl(final ClassElement originalClass, final ClassElement inheritedClass, final LinkedHashSet<ClassElement> check) {
        final Set<MethodElement> methods = new HashSet<MethodElement>();
        if (!check.contains(inheritedClass)) {
            check.add(inheritedClass);
            final Exact typeQuery = NameKind.exact(inheritedClass.getFullyQualifiedName());
            final Collection<? extends IndexResult> constructorResults = results(ClassElementImpl.IDX_FIELD, typeQuery,
                    new String[]{ClassElementImpl.IDX_FIELD, MethodElementImpl.IDX_CONSTRUCTOR_FIELD, MethodElementImpl.IDX_FIELD});
            final Set<MethodElement> methodsForResult = new HashSet<MethodElement>();
            final ElementFilter forEqualTypes = ElementFilter.forEqualTypes(inheritedClass);
            for (final IndexResult indexResult : constructorResults) {
                Set<ClassElement> classes = ClassElementImpl.fromSignature(this, indexResult);
                for (ClassElement classElement : classes) {
                    if (forEqualTypes.isAccepted(classElement)) {
                        methodsForResult.addAll(MethodElementImpl.fromSignature(originalClass, this, indexResult));
                    }
                }
            }
            methods.addAll(ElementFilter.forName(NameKind.exact(MethodElementImpl.CONSTRUCTOR_NAME)).filter(methodsForResult));
            if (methods.isEmpty()) {
                for (TypeElement typeElement : getDirectInheritedTypes(inheritedClass, true, false)) {
                    if (typeElement instanceof ClassElement) {
                        methods.addAll(getConstructorsImpl(originalClass, (ClassElement) typeElement, check));
                        if (!methods.isEmpty()) {
                            break;
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableSet(methods);
    }

    private Set<MethodElement> getDefaultConstructors(final ClassElement classElement) {
        Set<MethodElement> magicMethods = getAccessibleMagicMethods(classElement);
        for (MethodElement methodElement : magicMethods) {
            if (methodElement.isConstructor()) {
                return Collections.singleton(methodElement);
            }
        }
        throw new IllegalStateException();
    }


    @Override
    public final Set<MethodElement> getMethods(final NameKind methodQuery) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<MethodElement> methods = new HashSet<MethodElement>();
        final Collection<? extends IndexResult> methResults = results(MethodElementImpl.IDX_FIELD, methodQuery,
                new String[]{ClassElementImpl.IDX_FIELD, InterfaceElementImpl.IDX_FIELD, TraitElementImpl.IDX_FIELD, MethodElementImpl.IDX_FIELD});
        for (final IndexResult indexResult : methResults) {
            final Set<TypeElement> types = new HashSet<TypeElement>();
            types.addAll(ClassElementImpl.fromSignature(this, indexResult));
            types.addAll(InterfaceElementImpl.fromSignature(this, indexResult));
            types.addAll(TraitElementImpl.fromSignature(this, indexResult));
            for (final TypeElement typeElement : types) {
                methods.addAll(MethodElementImpl.fromSignature(typeElement, methodQuery, this, indexResult));
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<MethodElement> getMethods", methodQuery, start);//NOI18N
        }
        return Collections.unmodifiableSet(methods);
    }

    @Override
    public Set<TypeMemberElement> getDeclaredTypeMembers(TypeElement typeElement) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final QualifiedName fullyQualifiedName = typeElement.getFullyQualifiedName();
        final Set<TypeMemberElement> members = new HashSet<TypeMemberElement>();
        final NameKind.Exact typeQuery = NameKind.exact(fullyQualifiedName);
        final NameKind memberQuery = NameKind.empty();
        final FileObject typeFo = typeElement.getFileObject();

        switch (typeElement.getPhpElementKind()) {
            case CLASS:
                final Collection<? extends IndexResult> clzResults = results(ClassElementImpl.IDX_FIELD, typeQuery,
                        new String[]{
                            ClassElementImpl.IDX_FIELD,
                            FieldElementImpl.IDX_FIELD,
                            TypeConstantElementImpl.IDX_FIELD,
                            MethodElementImpl.IDX_FIELD
                        });
                for (final IndexResult indexResult : clzResults) {
                    for (final TypeElement clzElement : ClassElementImpl.fromSignature(typeQuery, this, indexResult)) {
                        if (fullyQualifiedName.getNamespaceName().equals(clzElement.getNamespaceName().toString())) {
                            members.addAll(MethodElementImpl.fromSignature(clzElement, memberQuery, this, indexResult));
                            members.addAll(FieldElementImpl.fromSignature(clzElement, memberQuery, this, indexResult));
                            members.addAll(TypeConstantElementImpl.fromSignature(clzElement, memberQuery, this, indexResult));
                        }
                    }
                }
                break;
            case IFACE:
                final Collection<? extends IndexResult> ifaceResults = results(InterfaceElementImpl.IDX_FIELD, typeQuery,
                        new String[]{
                            InterfaceElementImpl.IDX_FIELD,
                            TypeConstantElementImpl.IDX_FIELD,
                            MethodElementImpl.IDX_FIELD
                        });
                for (final IndexResult indexResult : ifaceResults) {
                    for (final TypeElement ifaceElement : InterfaceElementImpl.fromSignature(typeQuery, this, indexResult)) {
                        members.addAll(MethodElementImpl.fromSignature(ifaceElement, memberQuery, this, indexResult));
                        members.addAll(TypeConstantElementImpl.fromSignature(ifaceElement, memberQuery, this, indexResult));
                    }
                }
                break;
            case TRAIT:
                final Collection<? extends IndexResult> traitResults = results(TraitElementImpl.IDX_FIELD, typeQuery,
                        new String[]{
                            TraitElementImpl.IDX_FIELD,
                            FieldElementImpl.IDX_FIELD,
                            MethodElementImpl.IDX_FIELD
                        });
                for (final IndexResult indexResult : traitResults) {
                    for (final TypeElement traitElement : TraitElementImpl.fromSignature(typeQuery, this, indexResult)) {
                        members.addAll(MethodElementImpl.fromSignature(traitElement, memberQuery, this, indexResult));
                        members.addAll(FieldElementImpl.fromSignature(traitElement, memberQuery, this, indexResult));
                    }
                }
                break;
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<PhpElement> getTypeMembers", typeQuery, memberQuery, start);//NOI18N
        }
        final Set<TypeMemberElement> retval = new HashSet<TypeMemberElement>();
        final Exact exactTypeName = NameKind.exact(typeElement.getFullyQualifiedName());
        retval.addAll(extendedQuery.getFields(exactTypeName,NameKind.empty()));
        retval.addAll(extendedQuery.getMethods(exactTypeName, NameKind.empty()));
        retval.addAll(extendedQuery.getTypeConstants(exactTypeName, NameKind.empty()));
        retval.addAll(ElementFilter.forFiles(typeFo).filter(members));
        return retval;
    }

    @Override
    public Set<MethodElement> getDeclaredConstructors(ClassElement typeElement) {
        return ElementFilter.forName(NameKind.exact(MethodElementImpl.CONSTRUCTOR_NAME)).
                filter(getDeclaredMethods(typeElement));
    }


    @Override
    public final Set<MethodElement> getDeclaredMethods(final TypeElement typeElement) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final QualifiedName fullyQualifiedName = typeElement.getFullyQualifiedName();
        final Set<MethodElement> methods = new HashSet<MethodElement>();
        final NameKind.Exact typeQuery = NameKind.exact(fullyQualifiedName);
        final NameKind methodQuery = NameKind.empty();
        final FileObject typeFo = typeElement.getFileObject();

        switch (typeElement.getPhpElementKind()) {
            case CLASS:
                final Collection<? extends IndexResult> clzResults = results(ClassElementImpl.IDX_FIELD, typeQuery,
                        new String[]{ClassElementImpl.IDX_FIELD, MethodElementImpl.IDX_FIELD});
                for (final IndexResult indexResult : clzResults) {
                    for (final TypeElement clzElement : ClassElementImpl.fromSignature(typeQuery, this, indexResult)) {
                        methods.addAll(MethodElementImpl.fromSignature(clzElement, methodQuery, this, indexResult));
                    }
                }
                break;
            case IFACE:
                final Collection<? extends IndexResult> ifaceResults = results(InterfaceElementImpl.IDX_FIELD, typeQuery,
                        new String[]{InterfaceElementImpl.IDX_FIELD, MethodElementImpl.IDX_FIELD});
                for (final IndexResult indexResult : ifaceResults) {
                    for (final TypeElement ifaceElement : InterfaceElementImpl.fromSignature(typeQuery, this, indexResult)) {
                        methods.addAll(MethodElementImpl.fromSignature(ifaceElement, methodQuery, this, indexResult));
                    }
                }
                break;
            case TRAIT:
                final Collection<? extends IndexResult> traitResults = results(TraitElementImpl.IDX_FIELD, typeQuery,
                        new String[]{TraitElementImpl.IDX_FIELD, MethodElementImpl.IDX_FIELD});
                for (final IndexResult indexResult : traitResults) {
                    for (final TypeElement traitElement : TraitElementImpl.fromSignature(typeQuery, this, indexResult)) {
                        methods.addAll(MethodElementImpl.fromSignature(traitElement, methodQuery, this, indexResult));
                    }
                }
                break;
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<MethodElement> getMethods", typeQuery, methodQuery, start);//NOI18N
        }
        return ElementFilter.forFiles(typeFo).filter(methods);
    }

    @Override
    public final Set<TypeMemberElement> getTypeMembers(final NameKind.Exact typeQuery, final NameKind memberQuery) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<TypeMemberElement> members = new HashSet<TypeMemberElement>();
        //two queries: once for classes, second for ifaces
        final Collection<? extends IndexResult> clzResults = results(ClassElementImpl.IDX_FIELD, typeQuery,
                new String[]{
                    ClassElementImpl.IDX_FIELD,
                    MethodElementImpl.IDX_FIELD,
                    FieldElementImpl.IDX_FIELD,
                    TypeConstantElementImpl.IDX_FIELD
                });
        for (final IndexResult indexResult : clzResults) {
            for (final TypeElement typeElement : ClassElementImpl.fromSignature(typeQuery, this, indexResult)) {
                members.addAll(MethodElementImpl.fromSignature(typeElement, memberQuery, this, indexResult));
                members.addAll(FieldElementImpl.fromSignature(typeElement, memberQuery, this, indexResult));
                members.addAll(TypeConstantElementImpl.fromSignature(typeElement, memberQuery, this, indexResult));
            }
        }
        final Collection<? extends IndexResult> ifaceResults = results(InterfaceElementImpl.IDX_FIELD, typeQuery,
                new String[]{
                    InterfaceElementImpl.IDX_FIELD,
                    MethodElementImpl.IDX_FIELD,
                    TypeConstantElementImpl.IDX_FIELD
                });

        for (final IndexResult indexResult : ifaceResults) {
            for (final TypeElement typeElement : InterfaceElementImpl.fromSignature(typeQuery, this, indexResult)) {
                members.addAll(MethodElementImpl.fromSignature(typeElement, memberQuery, this, indexResult));
                members.addAll(TypeConstantElementImpl.fromSignature(typeElement, memberQuery, this, indexResult));
            }
        }
        final Collection<? extends IndexResult> traitResults = results(TraitElementImpl.IDX_FIELD, typeQuery,
                new String[] {
                    TraitElementImpl.IDX_FIELD,
                    MethodElementImpl.IDX_FIELD,
                    FieldElementImpl.IDX_FIELD
                });
        for (IndexResult indexResult : traitResults) {
            for (final TypeElement typeElement : TraitElementImpl.fromSignature(typeQuery, this, indexResult)) {
                members.addAll(MethodElementImpl.fromSignature(typeElement, memberQuery, this, indexResult));
                members.addAll(FieldElementImpl.fromSignature(typeElement, memberQuery, this, indexResult));
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<PhpElement> getTypeMembers", typeQuery, memberQuery, start);//NOI18N
        }
        return Collections.unmodifiableSet(members);
    }

    @Override
    public final Set<MethodElement> getMethods(final NameKind.Exact typeQuery, final NameKind methodQuery) {
        return getMethodsImpl(typeQuery, methodQuery, EnumSet.of(PhpElementKind.CLASS,PhpElementKind.IFACE));
    }

    private final Set<MethodElement> getMethodsImpl(final NameKind.Exact typeQuery, final NameKind methodQuery, EnumSet<PhpElementKind> typeKinds) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<MethodElement> methods = new HashSet<MethodElement>();
        //two queries: once for classes, second for ifaces
        if (typeKinds.contains(PhpElementKind.CLASS)) {
            final Collection<? extends IndexResult> clzResults = results(ClassElementImpl.IDX_FIELD, typeQuery,
                    new String[]{ClassElementImpl.IDX_FIELD, MethodElementImpl.IDX_FIELD});
            for (final IndexResult indexResult : clzResults) {
                for (final TypeElement typeElement : ClassElementImpl.fromSignature(typeQuery, this, indexResult)) {
                    methods.addAll(MethodElementImpl.fromSignature(typeElement, methodQuery, this, indexResult));
                }
            }
        }
        if (typeKinds.contains(PhpElementKind.IFACE)) {
            final Collection<? extends IndexResult> ifaceResults = results(InterfaceElementImpl.IDX_FIELD, typeQuery,
                    new String[]{InterfaceElementImpl.IDX_FIELD, MethodElementImpl.IDX_FIELD});
            for (final IndexResult indexResult : ifaceResults) {
                for (final TypeElement typeElement : InterfaceElementImpl.fromSignature(typeQuery, this, indexResult)) {
                    methods.addAll(MethodElementImpl.fromSignature(typeElement, methodQuery, this, indexResult));
                }
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<MethodElement> getMethods", typeQuery, methodQuery, start);//NOI18N
        }
        return Collections.unmodifiableSet(methods);
    }

    @Override
    public Set<FileObject> getLocationsForIdentifiers(String identifierName) {
        final Set<FileObject> result = new HashSet<FileObject>();

        Collection<? extends IndexResult> idIndexResult =search(PHPIndexer.FIELD_IDENTIFIER, identifierName.toLowerCase(), QuerySupport.Kind.PREFIX, PHPIndexer.FIELD_BASE, PHPIndexer.FIELD_IDENTIFIER);
        for (IndexResult indexResult : idIndexResult) {
            String[] values = indexResult.getValues(PHPIndexer.FIELD_IDENTIFIER);
            for (String val : values) {
                Signature sig = Signature.get(val);

                if (identifierName.equalsIgnoreCase(sig.string(0))) {
                    URL url = indexResult.getUrl();
                    FileObject fo = null;
                    try {
                        fo = "file".equals(url.getProtocol()) ? //NOI18N
                                FileUtil.toFileObject(new java.io.File(url.toURI())) : URLMapper.findFileObject(url);
                    } catch (URISyntaxException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    if (fo != null) {
                        result.add(fo);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Set<FieldElement> getDeclaredFields(TypeElement typeElement) {
        final Set<FieldElement> retval = new HashSet<FieldElement>();
        final Exact typeNameQuery = NameKind.exact(typeElement.getFullyQualifiedName());
        retval.addAll(ElementFilter.forFiles(typeElement.getFileObject())
                .filter(getFields(typeNameQuery,NameKind.empty())));
        retval.addAll(extendedQuery.getFields(typeNameQuery, NameKind.empty()));
        return retval;
    }

    @Override
    public final Set<FieldElement> getFields(final NameKind fieldQuery) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<FieldElement> fields = new HashSet<FieldElement>();
        final Collection<? extends IndexResult> classFieldResults = results(FieldElementImpl.IDX_FIELD, fieldQuery,
                new String[]{ClassElementImpl.IDX_FIELD, FieldElementImpl.IDX_FIELD});
        for (final IndexResult indexResult : classFieldResults) {
            for (final TypeElement typeElement : ClassElementImpl.fromSignature(this, indexResult)) {
                fields.addAll(FieldElementImpl.fromSignature(typeElement, fieldQuery, this, indexResult));
            }
        }
        final Collection<? extends IndexResult> traitFieldResults = results(FieldElementImpl.IDX_FIELD, fieldQuery,
                new String[]{TraitElementImpl.IDX_FIELD, FieldElementImpl.IDX_FIELD});
        for (final IndexResult indexResult : traitFieldResults) {
            for (final TypeElement typeElement : TraitElementImpl.fromSignature(this, indexResult)) {
                fields.addAll(FieldElementImpl.fromSignature(typeElement, fieldQuery, this, indexResult));
            }
        }
        fields.addAll(extendedQuery.getFields(fieldQuery));
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<FieldElement> getFields", fieldQuery, start);//NOI18N
        }
        return Collections.unmodifiableSet(fields);
    }

    @Override
    public final Set<FieldElement> getFields(final NameKind.Exact classQuery, final NameKind fieldQuery) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<FieldElement> fields = new HashSet<FieldElement>();
        final Collection<? extends IndexResult> clzResults = results(ClassElementImpl.IDX_FIELD, classQuery,
                new String[]{ClassElementImpl.IDX_FIELD, FieldElementImpl.IDX_FIELD});
        for (final IndexResult indexResult : clzResults) {
            for (final TypeElement typeElement : ClassElementImpl.fromSignature(classQuery, this, indexResult)) {
                fields.addAll(FieldElementImpl.fromSignature(typeElement, fieldQuery, this, indexResult));
            }
        }
        final Collection<? extends IndexResult> traitResults = results(TraitElementImpl.IDX_FIELD, classQuery,
                new String[]{TraitElementImpl.IDX_FIELD, FieldElementImpl.IDX_FIELD});
        for (final IndexResult indexResult : traitResults) {
            for (final TypeElement typeElement : TraitElementImpl.fromSignature(classQuery, this, indexResult)) {
                fields.addAll(FieldElementImpl.fromSignature(typeElement, fieldQuery, this, indexResult));
            }
        }
        fields.addAll(extendedQuery.getFields(classQuery, fieldQuery));
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<FieldElement> getFields", classQuery, fieldQuery, start);//NOI18N
        }
        return Collections.unmodifiableSet(fields);
    }

    @Override
    public Set<TypeConstantElement> getDeclaredTypeConstants(TypeElement typeElement) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final QualifiedName fullyQualifiedName = typeElement.getFullyQualifiedName();
        final FileObject typeFo = typeElement.getFileObject();
        final Set<TypeConstantElement> constants = new HashSet<TypeConstantElement>();
        final NameKind.Exact typeQuery = NameKind.exact(fullyQualifiedName);
        final NameKind constantQuery = NameKind.empty();

        switch (typeElement.getPhpElementKind()) {
            case CLASS:
                final Collection<? extends IndexResult> clzResults = results(ClassElementImpl.IDX_FIELD, typeQuery,
                        new String[]{ClassElementImpl.IDX_FIELD, TypeConstantElementImpl.IDX_FIELD});
                for (final IndexResult indexResult : clzResults) {
                    for (final TypeElement classElement : ClassElementImpl.fromSignature(typeQuery, this, indexResult)) {
                        constants.addAll(TypeConstantElementImpl.fromSignature(classElement, constantQuery, this, indexResult));
                    }
                }
                break;
            case IFACE:
                final Collection<? extends IndexResult> ifaceResults = results(InterfaceElementImpl.IDX_FIELD, typeQuery,
                        new String[]{InterfaceElementImpl.IDX_FIELD, TypeConstantElementImpl.IDX_FIELD});
                for (final IndexResult indexResult : ifaceResults) {
                    for (final TypeElement ifaceElement : InterfaceElementImpl.fromSignature(typeQuery, this, indexResult)) {
                        constants.addAll(TypeConstantElementImpl.fromSignature(ifaceElement, constantQuery, this, indexResult));
                    }
                }
                break;
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<TypeConstantElement> getTypeConstants", typeQuery, constantQuery, start);//NOI18N
        }
        return Collections.unmodifiableSet(ElementFilter.forFiles(typeFo).filter(constants));
    }

    @Override
    public Set<TypeConstantElement> getTypeConstants(NameKind constantQuery) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<TypeConstantElement> constants = new HashSet<TypeConstantElement>();
        final Collection<? extends IndexResult> constantResults = results(TypeConstantElementImpl.IDX_FIELD, constantQuery,
                new String[]{ClassElementImpl.IDX_FIELD, InterfaceElementImpl.IDX_FIELD, TypeConstantElementImpl.IDX_FIELD});
        for (final IndexResult indexResult : constantResults) {
            final Set<TypeElement> types = new HashSet<TypeElement>();
            types.addAll(ClassElementImpl.fromSignature(this, indexResult));
            types.addAll(InterfaceElementImpl.fromSignature(this, indexResult));
            for (final TypeElement typeElement : types) {
                constants.addAll(TypeConstantElementImpl.fromSignature(typeElement, constantQuery, this, indexResult));
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<TypeConstantElement> getTypeConstants", constantQuery, start);//NOI18N
        }
        return Collections.unmodifiableSet(constants);
    }

    @Override
    public Set<TypeConstantElement> getTypeConstants(NameKind.Exact typeQuery, NameKind constantQuery) {
        return getTypeConstantsImpl(typeQuery, constantQuery, EnumSet.of(PhpElementKind.CLASS, PhpElementKind.IFACE));
    }

    private Set<TypeConstantElement> getTypeConstantsImpl(NameKind.Exact typeQuery, NameKind constantQuery, EnumSet<PhpElementKind> typeKinds) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<TypeConstantElement> constants = new HashSet<TypeConstantElement>();
        //two queries: once for classes, second for ifaces
        if (typeKinds.contains(PhpElementKind.CLASS)) {
            final Collection<? extends IndexResult> clzResults = results(ClassElementImpl.IDX_FIELD, typeQuery,
                    new String[]{ClassElementImpl.IDX_FIELD, TypeConstantElementImpl.IDX_FIELD});
            for (final IndexResult indexResult : clzResults) {
                for (final TypeElement typeElement : ClassElementImpl.fromSignature(typeQuery, this, indexResult)) {
                    constants.addAll(TypeConstantElementImpl.fromSignature(typeElement, constantQuery, this, indexResult));
                }
            }
        }
        if (typeKinds.contains(PhpElementKind.IFACE)) {
            final Collection<? extends IndexResult> ifaceResults = results(InterfaceElementImpl.IDX_FIELD, typeQuery,
                    new String[]{InterfaceElementImpl.IDX_FIELD, TypeConstantElementImpl.IDX_FIELD});
            for (final IndexResult indexResult : ifaceResults) {
                for (final TypeElement typeElement : InterfaceElementImpl.fromSignature(typeQuery, this, indexResult)) {
                    constants.addAll(TypeConstantElementImpl.fromSignature(typeElement, constantQuery, this, indexResult));
                }
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<TypeConstantElement> getTypeConstants", typeQuery, constantQuery, start);//NOI18N
        }
        return Collections.unmodifiableSet(constants);
    }

    @Override
    public QueryScope getQueryScope() {
        return QueryScope.INDEX_SCOPE;
    }

    @Override
    public Set<MethodElement> getStaticInheritedMethods(TypeElement typeElement) {
        return ElementFilter.forStaticModifiers(true).filter(getInheritedMethods(typeElement));
    }


    /**
     * @param enclosingType null if not enclosed at all
     */
    private static ElementFilter forAccessibleTypeMembers(final TypeElement enclosingType, final Collection<TypeElement> inheritedTypes) {
        final ElementFilter publicOnly = ElementFilter.forPublicModifiers(true);
        final ElementFilter publicAndProtectedOnly = ElementFilter.forPrivateModifiers(false);
        final ElementFilter fromEnclosingType = ElementFilter.forMembersOfType(enclosingType);
        return new ElementFilter() {
            private ElementFilter[] subtypesFilters = null;
            @Override
            public boolean isAccepted(final PhpElement element) {
                if (element instanceof TypeMemberElement &&
                        !element.getPhpElementKind().equals(PhpElementKind.TYPE_CONSTANT)) {
                    if (enclosingType != null) {
                        return isFromEnclosingType(element) ? true :
                            (isFromSubclassOfEnclosingType(element) ?
                                publicAndProtectedOnly.isAccepted(element) :
                                publicOnly.isAccepted(element));
                    }
                    return publicOnly.isAccepted(element);
                }
                return true;
            }

            private boolean isFromEnclosingType(final PhpElement element) {
                return fromEnclosingType.isAccepted(element);
            }
            private boolean isFromSubclassOfEnclosingType(final PhpElement element) {
                for (TypeElement nextType : inheritedTypes) {
                    if (ElementFilter.forMembersOfType(nextType).isAccepted(element)) {
                        return true;
                    }
                }
                if (subtypesFilters == null) {
                    subtypesFilters = createSubtypeFilters();
                }
                return subtypesFilters.length == 0 ? false :
                    ElementFilter.anyOf(subtypesFilters).isAccepted(element);
            }

            private ElementFilter[] createSubtypeFilters() {
                final List<ElementFilter> filters = new ArrayList<ElementFilter>();
                final ElementQuery elementQuery = enclosingType.getElementQuery();
                if (elementQuery.getQueryScope().isIndexScope()) {
                    final ElementQuery.Index index = (ElementQuery.Index) elementQuery;
                    final LinkedHashSet<TypeElement> inheritedTypes = index.getInheritedTypes(enclosingType);
                    for (final TypeElement nextType : inheritedTypes) {
                        filters.add(ElementFilter.forMembersOfType(nextType));
                    }
                }
                return filters.toArray(new ElementFilter[filters.size()]);
            }
        };
    }

    @Override
    public Set<MethodElement> getAccessibleMethods(final TypeElement typeElement, final TypeElement calledFromEnclosingType) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<MethodElement> allMethods = getAllMethods(typeElement);
        Collection<TypeElement> subTypes = Collections.emptySet();
        if (calledFromEnclosingType != null && ElementFilter.forEqualTypes(typeElement).isAccepted(calledFromEnclosingType)) {
            subTypes = toTypes(allMethods);
        }
        final ElementFilter filterForAccessible = forAccessibleTypeMembers(calledFromEnclosingType, subTypes);
        Set<MethodElement> retval  = filterForAccessible.filter(allMethods);
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<MethodElement> getAccessibleMethods", NameKind.exact(typeElement.getFullyQualifiedName()), start);//NOI18N
        }
        return Collections.unmodifiableSet(retval);
    }

    @Override
    public Set<FieldElement> getAccessibleFields(final TypeElement typeElement, final TypeElement calledFromEnclosingType) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<FieldElement> allFields = getAlllFields(typeElement);
        Collection<TypeElement> subTypes = Collections.emptySet();
        if (calledFromEnclosingType != null && ElementFilter.forEqualTypes(typeElement).isAccepted(calledFromEnclosingType)) {
            subTypes = toTypes(allFields);
        }
        final ElementFilter filterForAccessible = forAccessibleTypeMembers(calledFromEnclosingType, subTypes);
        Set<FieldElement> retval  = filterForAccessible.filter(allFields);
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<FieldElement> getAccessibleFields", NameKind.exact(typeElement.getFullyQualifiedName()), start);//NOI18N
        }
        return Collections.unmodifiableSet(retval);
    }

    private LinkedHashSet<TypeMemberElement> getDirectInheritedTypeMembers(final TypeElement typeElement,
            EnumSet<PhpElementKind> typeKinds, EnumSet<PhpElementKind> memberKinds) {
        final LinkedHashSet<TypeMemberElement> directTypes = new LinkedHashSet<TypeMemberElement>();
        if (typeKinds.contains(PhpElementKind.CLASS) && (typeElement instanceof ClassElement)) {
            QualifiedName superClassName = null;
            Collection<QualifiedName> possibleFQSuperClassNames = ((ClassElement) typeElement).getPossibleFQSuperClassNames();
            if (possibleFQSuperClassNames.size() == 1) {
                superClassName = possibleFQSuperClassNames.iterator().next();
            } else {
                superClassName = ((ClassElement) typeElement).getSuperClassName();
            }
            if (superClassName != null) {
                directTypes.addAll(extendedQuery.getFields(NameKind.exact(superClassName), NameKind.empty()));
                directTypes.addAll(extendedQuery.getMethods(NameKind.exact(superClassName), NameKind.empty()));
                directTypes.addAll(extendedQuery.getTypeConstants(NameKind.exact(superClassName), NameKind.empty()));
                if (memberKinds.size() != 1) {
                    directTypes.addAll(ElementFilter.forFiles(typeElement.getFileObject()).prefer(getTypeMembers(NameKind.exact(superClassName), NameKind.empty())));
                } else {
                    switch(memberKinds.iterator().next()) {
                        case METHOD:
                            directTypes.addAll(ElementFilter.forFiles(typeElement.getFileObject()).prefer(
                                    getMethodsImpl(NameKind.exact(superClassName), NameKind.empty(), EnumSet.of(PhpElementKind.CLASS))));
                            break;
                        case FIELD:
                            directTypes.addAll(ElementFilter.forFiles(typeElement.getFileObject()).prefer(getFields(NameKind.exact(superClassName), NameKind.empty())));
                            break;
                        case TYPE_CONSTANT:
                            directTypes.addAll(ElementFilter.forFiles(typeElement.getFileObject()).prefer(
                                    getTypeConstantsImpl(NameKind.exact(superClassName), NameKind.empty(), EnumSet.of(PhpElementKind.CLASS))));
                            break;
                    }
                }
            }
        }
        if (typeKinds.contains(PhpElementKind.IFACE)) {
            Collection<QualifiedName> interfaceNames = null;
            Collection<QualifiedName> fQSuperInterfaceNames = typeElement.getFQSuperInterfaceNames();
            if (!fQSuperInterfaceNames.isEmpty()) {
                interfaceNames = fQSuperInterfaceNames;
            } else {
                interfaceNames = typeElement.getSuperInterfaces();
            }
            for (QualifiedName iface : interfaceNames) {
                directTypes.addAll(extendedQuery.getFields(NameKind.exact(iface), NameKind.empty()));
                directTypes.addAll(extendedQuery.getMethods(NameKind.exact(iface), NameKind.empty()));
                directTypes.addAll(extendedQuery.getTypeConstants(NameKind.exact(iface), NameKind.empty()));
                if (memberKinds.size() != 1) {
                    directTypes.addAll(ElementFilter.forFiles(typeElement.getFileObject()).prefer(getTypeMembers(NameKind.exact(iface), NameKind.empty())));
                } else {
                    switch(memberKinds.iterator().next()) {
                        case METHOD:
                            directTypes.addAll(ElementFilter.forFiles(typeElement.getFileObject()).prefer(getMethodsImpl(NameKind.exact(iface), NameKind.empty(),
                                    EnumSet.of(PhpElementKind.IFACE))));
                            break;
                        case TYPE_CONSTANT:
                            directTypes.addAll(ElementFilter.forFiles(typeElement.getFileObject()).prefer(getTypeConstantsImpl(NameKind.exact(iface), NameKind.empty(),
                                    EnumSet.of(PhpElementKind.IFACE))));
                            break;
                    }
                }
            }
        }
        if (typeKinds.contains(PhpElementKind.TRAIT) && (typeElement instanceof TraitedElement)) {
            TraitedElement traitedElement = (TraitedElement) typeElement;
            Collection<QualifiedName> usedTraits = traitedElement.getUsedTraits();
            for (QualifiedName trait : usedTraits) {
                if (memberKinds.size() != 1) {
                    directTypes.addAll(ElementFilter.forFiles(typeElement.getFileObject()).prefer(getTypeMembers(NameKind.exact(trait), NameKind.empty())));
                } else {
                    switch(memberKinds.iterator().next()) {
                        case METHOD:
                            directTypes.addAll(ElementFilter.forFiles(typeElement.getFileObject()).prefer(getMethodsImpl(NameKind.exact(trait), NameKind.empty(),
                                    EnumSet.of(PhpElementKind.TRAIT))));
                            break;
                        case FIELD:
                            directTypes.addAll(ElementFilter.forFiles(typeElement.getFileObject()).prefer(getFields(NameKind.exact(trait), NameKind.empty())));
                            break;
                    }
                }
            }
        }
        return directTypes;
    }

    @Override
    public Set<MethodElement> getInheritedMethods(final TypeElement typeElement) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final LinkedHashSet<TypeMemberElement> typeMembers =
                getInheritedTypeMembers(typeElement, new LinkedHashSet<TypeElement>(),
                new LinkedHashSet<TypeMemberElement>(),
                EnumSet.of(PhpElementKind.CLASS,PhpElementKind.IFACE),
                EnumSet.of(PhpElementKind.METHOD));
        final Set<MethodElement> retval = new HashSet<MethodElement>();
        for (TypeMemberElement member : typeMembers) {
            if (member instanceof MethodElement) {
                retval.add((MethodElement)member);
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<MethodElement> getInheritedMethods", NameKind.exact(typeElement.getFullyQualifiedName()), start);//NOI18N
        }
        return Collections.unmodifiableSet(retval);
    }

    @Override
    public Set<MethodElement> getAllMethods(TypeElement typeElement) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final LinkedHashSet<TypeMemberElement> typeMembers =
                getInheritedTypeMembers(typeElement, new LinkedHashSet<TypeElement>(),
                new LinkedHashSet<TypeMemberElement>(getDeclaredMethods(typeElement)),
                EnumSet.of(PhpElementKind.CLASS,PhpElementKind.IFACE),
                EnumSet.of(PhpElementKind.METHOD));
        final Set<MethodElement> retval = new HashSet<MethodElement>();
        for (TypeMemberElement member : typeMembers) {
            if (member instanceof MethodElement) {
                retval.add((MethodElement)member);
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<MethodElement> getAllMethods", NameKind.exact(typeElement.getFullyQualifiedName()), start);//NOI18N
        }
        return Collections.unmodifiableSet(retval);
    }

    @Override
    public Set<FieldElement> getAlllFields(TypeElement typeElement) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final LinkedHashSet<TypeMemberElement> typeMembers =
                getInheritedTypeMembers(typeElement, new LinkedHashSet<TypeElement>(),
                new LinkedHashSet<TypeMemberElement>(getDeclaredFields(typeElement)),
                EnumSet.of(PhpElementKind.CLASS),
                EnumSet.of(PhpElementKind.FIELD));
        final Set<FieldElement> retval = new HashSet<FieldElement>();
        for (TypeMemberElement member : typeMembers) {
            if (member instanceof FieldElement) {
                retval.add((FieldElement)member);
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<FieldElement> getAlllFields", NameKind.exact(typeElement.getFullyQualifiedName()), start);//NOI18N
        }
        return Collections.unmodifiableSet(retval);
    }

    @Override
    public Set<TypeConstantElement> getAllTypeConstants(TypeElement typeElement) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final LinkedHashSet<TypeMemberElement> typeMembers =
                getInheritedTypeMembers(typeElement, new LinkedHashSet<TypeElement>(),
                new LinkedHashSet<TypeMemberElement>(getDeclaredTypeConstants(typeElement)),
                EnumSet.of(PhpElementKind.CLASS,PhpElementKind.IFACE),
                EnumSet.of(PhpElementKind.TYPE_CONSTANT));
        final Set<TypeConstantElement> retval = new HashSet<TypeConstantElement>();
        for (TypeMemberElement member : typeMembers) {
            if (member instanceof TypeConstantElement) {
                retval.add((TypeConstantElement)member);
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<TypeConstantElement> getAllTypeConstants", NameKind.exact(typeElement.getFullyQualifiedName()), start);//NOI18N
        }
        return Collections.unmodifiableSet(retval);
    }

    @Override
    public Set<TypeMemberElement> getAllTypeMembers(TypeElement typeElement) {
        final EnumSet<PhpElementKind> typeKinds = EnumSet.of(
                PhpElementKind.CLASS,
                PhpElementKind.IFACE,
                PhpElementKind.TRAIT
                );
        final EnumSet<PhpElementKind> memberKinds = EnumSet.of(
                PhpElementKind.METHOD,
                PhpElementKind.FIELD,
                PhpElementKind.TYPE_CONSTANT
                );
        return getInheritedTypeMembers(typeElement, new LinkedHashSet<TypeElement>(),
                new LinkedHashSet<TypeMemberElement>(getDeclaredTypeMembers(typeElement)), typeKinds, memberKinds);
    }

    @Override
    public Set<TypeMemberElement> getInheritedTypeMembers(final TypeElement typeElement) {
        final EnumSet<PhpElementKind> typeKinds = EnumSet.of(
                PhpElementKind.CLASS,
                PhpElementKind.IFACE
                );
        final EnumSet<PhpElementKind> memberKinds = EnumSet.of(
                PhpElementKind.METHOD,
                PhpElementKind.FIELD,
                PhpElementKind.TYPE_CONSTANT
                );
        return getInheritedTypeMembers(typeElement, new LinkedHashSet<TypeElement>(),
                new LinkedHashSet<TypeMemberElement>(), typeKinds, memberKinds);
    }

    @Override
    public Set<TypeMemberElement> getAccessibleTypeMembers(TypeElement typeElement, TypeElement calledFromEnclosingType) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<TypeMemberElement> allTypeMembers = getAllTypeMembers(typeElement);
        Collection<TypeElement> subTypes = Collections.emptySet();
        if (calledFromEnclosingType != null && ElementFilter.forEqualTypes(typeElement).isAccepted(calledFromEnclosingType)) {
            subTypes = toTypes(allTypeMembers);
        }
        final ElementFilter filterForAccessible = forAccessibleTypeMembers(calledFromEnclosingType, subTypes);
        Set<TypeMemberElement> retval  = new HashSet<TypeMemberElement>();
        retval.addAll(filterForAccessible.filter(allTypeMembers));
        ElementFilter allOf = ElementFilter.allOf(ElementFilter.forVirtualExtensions(), ElementFilter.forMembersOfTypeName(typeElement));
        retval.addAll(allOf.filter(allTypeMembers));
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<PhpElement> getAccessibleTypeMembers", NameKind.exact(typeElement.getFullyQualifiedName()), start);//NOI18N
        }
        return Collections.unmodifiableSet(retval);
    }

    private LinkedHashSet<TypeMemberElement> getInheritedTypeMembers(final TypeElement typeElement, final LinkedHashSet<TypeElement> recursionPrevention,
            LinkedHashSet<TypeMemberElement> retval, EnumSet<PhpElementKind> typeKinds, EnumSet<PhpElementKind> memberKinds) {
        if (recursionPrevention.add(typeElement)) {
            final LinkedHashSet<TypeMemberElement> typeMembers =
                    getDirectInheritedTypeMembers(typeElement, typeKinds, memberKinds);
            retval.addAll(forComparingNameKinds(retval).reverseFilter(typeMembers));
            for (final TypeElement tp : typeMembers.isEmpty() ? getDirectInheritedTypes(typeElement) : toTypes(typeMembers)) {
                retval.addAll(getInheritedTypeMembers(tp, recursionPrevention, retval, typeKinds, memberKinds));
            }
        }
        return retval;
    }

    @Override
    public Set<MethodElement> getAllMethods(final Exact typeQuery, final NameKind methodQuery) {
        Set<MethodElement> retval = new HashSet<MethodElement>();
        Set<TypeElement> types = new HashSet<TypeElement>();
        types.addAll(getClassesImpl(typeQuery));
        types.addAll(getInterfacesImpl(typeQuery));
        types.addAll(getTraitsImpl(typeQuery));
        for (TypeElement typeElement : types) {
            retval.addAll(ElementFilter.forName(methodQuery).filter(getAllMethods(typeElement)));
        }
        return retval;
    }

    @Override
    public Set<FieldElement> getAlllFields(final Exact typeQuery, final NameKind fieldQuery) {
        Set<FieldElement> retval = new HashSet<FieldElement>();
        Set<ClassElement> types = getClassesImpl(typeQuery);
        for (TypeElement typeElement : types) {
            retval.addAll(ElementFilter.forName(fieldQuery).filter(getAlllFields(typeElement)));
        }
        return retval;
    }

    @Override
    public Set<TypeConstantElement> getAllTypeConstants(final Exact typeQuery, final NameKind constantQuery) {
        Set<TypeConstantElement> retval = new HashSet<TypeConstantElement>();
        Set<TypeElement> types = new HashSet<TypeElement>();
        types.addAll(getClassesImpl(typeQuery));
        types.addAll(getInterfacesImpl(typeQuery));
        for (TypeElement typeElement : types) {
            retval.addAll(ElementFilter.forName(constantQuery).filter(getAllTypeConstants(typeElement)));
        }
        return retval;
    }

    @Override
    public Set<FieldElement> getAccessibleStaticFields(final TypeElement classElement, final TypeElement calledFromEnclosingType) {
        return ElementFilter.forStaticModifiers(true).filter(getAccessibleFields(classElement, calledFromEnclosingType));
    }

    @Override
    public Set<MethodElement> getAccessibleStaticMethods(final TypeElement typeElement, final TypeElement calledFromEnclosingType) {
        return ElementFilter.forStaticModifiers(true).filter(getAccessibleMethods(typeElement, calledFromEnclosingType));
    }

    private Set<TypeConstantElement> getNotPrivateTypeConstants(TypeElement oneType) {
        return ElementFilter.forPublicModifiers(true).filter(getDeclaredTypeConstants(oneType));
    }

    private Set<FieldElement> getNotPrivateFields(ClassElement oneClass) {
        return ElementFilter.forPrivateModifiers(false).filter(getDeclaredFields(oneClass));
    }

    @Override
    public Set<FieldElement> getStaticInheritedFields(final TypeElement classElement) {
        return ElementFilter.forStaticModifiers(true).filter(getInheritedFields(classElement));
    }

    @Override
    public Set<FieldElement> getInheritedFields(final TypeElement classElement) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<FieldElement> retval = new HashSet<FieldElement>();
        final LinkedHashSet<ClassElement> inheritedClasses = getInheritedClasses(classElement);
        final Set<String> declaredFieldNames = toNames(getDeclaredFields(classElement));
        for (ClassElement oneClass : inheritedClasses) {
            final Set<FieldElement> fields = getNotPrivateFields(oneClass);
            for (final FieldElement fieldElement : fields) {
                final String fieldName = fieldElement.getName();
                if (!declaredFieldNames.contains(fieldName)) {
                    retval.add(fieldElement);
                    declaredFieldNames.add(fieldName);
                }
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<FieldElement> getInheritedFields", NameKind.exact(classElement.getFullyQualifiedName()), start);//NOI18N
        }
        return Collections.unmodifiableSet(retval);
    }


    @Override
    public Set<TypeConstantElement> getInheritedTypeConstants(TypeElement typeElement) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final Set<TypeConstantElement> retval = new HashSet<TypeConstantElement>();
        final LinkedHashSet<? extends TypeElement> inheritedTypes = getInheritedTypes(typeElement);
        final Set<String> declaredConstantNames = toNames(getDeclaredTypeConstants(typeElement));
        for (TypeElement oneType : inheritedTypes) {
            final Set<TypeConstantElement> constants = getNotPrivateTypeConstants(oneType);
            for (final TypeConstantElement constantElement : constants) {
                final String constantName = constantElement.getName();
                if (!declaredConstantNames.contains(constantName)) {
                    retval.add(constantElement);
                    declaredConstantNames.add(constantName);
                }
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("Set<TypeConstantElement> getInheritedTypeConstants", NameKind.exact(typeElement.getFullyQualifiedName()), start);//NOI18N
        }
        return Collections.unmodifiableSet(retval);
    }

    @Override
    public LinkedHashSet<TypeElement> getInheritedByTypes(final TypeElement typeElement) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final LinkedHashSet<TypeElement> retval = new LinkedHashSet<TypeElement>();
        getInheritedByTypes(typeElement, retval);
        retval.remove(typeElement);
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("LinkedHashSet<TypeElement> getInheritedByTypes", NameKind.exact(typeElement.getFullyQualifiedName()), start);//NOI18N
        }
        return retval;
    }

    @Override
    public LinkedHashSet<TypeElement> getInheritedTypes(final TypeElement typeElement) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final LinkedHashSet<TypeElement> retval = new LinkedHashSet<TypeElement>();
        getInheritedTypes(typeElement, retval, true, true);
        retval.remove(typeElement);
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("LinkedHashSet<TypeElement> getInheritedTypes", NameKind.exact(typeElement.getFullyQualifiedName()), start);//NOI18N
        }
        return retval;
    }

    @Override
    public LinkedHashSet<ClassElement> getInheritedClasses(final TypeElement classElement) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final LinkedHashSet<ClassElement> retvalClasses = new LinkedHashSet<ClassElement>();
        final LinkedHashSet<TypeElement> retvalTypes = new LinkedHashSet<TypeElement>();
        getInheritedTypes(classElement, retvalTypes, true, false);
        retvalTypes.remove(classElement);
        for (TypeElement te : retvalTypes) {
            if (te instanceof ClassElement) {
                retvalClasses.add((ClassElement) te);
            } else {
                assert false : te.toString();
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("LinkedHashSet<ClassElement> getInheritedClasses", NameKind.exact(classElement.getFullyQualifiedName()), start);//NOI18N
        }
        return retvalClasses;
    }

    @Override
    public LinkedHashSet<InterfaceElement> getInheritedInterfaces(TypeElement ifaceElement) {
        final long start = (LOG.isLoggable(Level.FINE)) ? System.currentTimeMillis() : 0;
        final LinkedHashSet<InterfaceElement> retvalIfaces = new LinkedHashSet<InterfaceElement>();
        final LinkedHashSet<TypeElement> retvalTypes = new LinkedHashSet<TypeElement>();
        getInheritedTypes(ifaceElement, retvalTypes, false, true);
        retvalTypes.remove(ifaceElement);
        for (TypeElement te : retvalTypes) {
            if (te instanceof InterfaceElement) {
                retvalIfaces.add((InterfaceElement) te);
            } else {
                assert false : te.toString();
            }
        }
        if (LOG.isLoggable(Level.FINE)) {
            logQueryTime("LinkedHashSet<InterfaceElement> getInheritedInterfaces", NameKind.exact(ifaceElement.getFullyQualifiedName()), start);//NOI18N
        }
        return retvalIfaces;
    }

    private static ElementFilter forComparingNameKinds(final Collection<? extends PhpElement> elements) {
        return new ElementFilter() {

            @Override
            public boolean isAccepted(PhpElement element) {
                final ElementFilter forKind = ElementFilter.forKind(element.getPhpElementKind());
                final ElementFilter forName = ElementFilter.forName(NameKind.exact(element.getName()));
                for (PhpElement nextElement : elements) {
                    if (forKind.isAccepted(nextElement) && forName.isAccepted(nextElement)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private static LinkedHashSet<TypeElement> toTypes(final Collection<? extends TypeMemberElement> typeMembers) {
        final LinkedHashSet<TypeElement> retval = new LinkedHashSet<TypeElement>();
        for (final TypeMemberElement typeMemberElement : typeMembers) {
            retval.add(typeMemberElement.getType());
        }
        return retval;
    }

    private void getInheritedTypes(final TypeElement typeElement, final LinkedHashSet<TypeElement> retval,
            final boolean includeClasses, final boolean includeIfaces) {
        if (retval.add(typeElement)) {
            LinkedHashSet<TypeElement> directTypes = getDirectInheritedTypes(typeElement, includeClasses, includeIfaces);
            for (TypeElement tp : directTypes) {
                getInheritedTypes(tp, retval, includeClasses, includeIfaces);
            }
        }
    }

    @Override
    public LinkedHashSet<ClassElement> getDirectInheritedClasses(final TypeElement typeElement) {
        final LinkedHashSet<ClassElement> retval = new LinkedHashSet<ClassElement>();
        final LinkedHashSet<TypeElement> types = getDirectInheritedTypes(typeElement, true, false);
        for (final TypeElement nextType : types) {
            if (nextType instanceof ClassElement) {
                retval.add((ClassElement)nextType);
            }
        }
        return retval;
    }

    @Override
    public LinkedHashSet<InterfaceElement> getDirectInheritedInterfaces(final TypeElement typeElement) {
        final LinkedHashSet<InterfaceElement> retval = new LinkedHashSet<InterfaceElement>();
        final LinkedHashSet<TypeElement> types = getDirectInheritedTypes(typeElement, false, true);
        for (final TypeElement nextType : types) {
            if (nextType instanceof InterfaceElement) {
                retval.add((InterfaceElement)nextType);
            }
        }
        return retval;
    }

    @Override
    public final LinkedHashSet<TypeElement> getDirectInheritedTypes(final TypeElement typeElement) {
        return getDirectInheritedTypes(typeElement, true, true);
    }

    private LinkedHashSet<TypeElement> getDirectInheritedTypes(final TypeElement typeElement, final boolean includeClasses, final boolean includeIfaces) {
        final LinkedHashSet<TypeElement> directTypes = new LinkedHashSet<TypeElement>();
        if (includeClasses && (typeElement instanceof ClassElement)) {
            QualifiedName superClassName = ((ClassElement) typeElement).getSuperClassName();
            if (superClassName != null) {
                directTypes.addAll(ElementFilter.forFiles(typeElement.getFileObject()).prefer(getClassesImpl(NameKind.exact(superClassName))));
            }
        }
        if (includeIfaces) {
            for (QualifiedName iface : typeElement.getSuperInterfaces()) {
                directTypes.addAll(ElementFilter.forFiles(typeElement.getFileObject()).prefer(getInterfacesImpl(NameKind.exact(iface))));
            }
        }
        return directTypes;
    }

    private void getInheritedByTypes(final TypeElement typeElement, final LinkedHashSet<TypeElement> retval) {
        if (retval.add(typeElement)) {
            LinkedHashSet<TypeElement> directTypes = getDirectInheritedByTypes(typeElement);
            for (TypeElement tp : directTypes) {
                getInheritedByTypes(tp, retval);
            }
        }
    }

    @Override
    public LinkedHashSet<TypeElement> getDirectInheritedByTypes(final TypeElement typeElement) {
        final LinkedHashSet<TypeElement> directTypes = new LinkedHashSet<TypeElement>();
        final Exact query = NameKind.exact(typeElement.getFullyQualifiedName());
        if (typeElement.isClass()) {
            final Collection<? extends IndexResult> result = results(PHPIndexer.FIELD_SUPER_CLASS, query,
                    new String[] {PHPIndexer.FIELD_SUPER_CLASS, ClassElementImpl.IDX_FIELD});
            for (final IndexResult indexResult : result) {
                String[] values = indexResult.getValues(PHPIndexer.FIELD_SUPER_CLASS);
                for (String value : values) {
                    Signature signature = Signature.get(value);
                    final QualifiedName fqnForValue = QualifiedName.createFullyQualified(signature.string(1), signature.string(2));
                    if (query.matchesName(PhpElementKind.CLASS, fqnForValue)) {
                        directTypes.addAll(ClassElementImpl.fromSignature(NameKind.empty(), this, indexResult));
                    }
                }
            }
        } else if (typeElement.isInterface()) {
            final Collection<? extends IndexResult> result = results(PHPIndexer.FIELD_SUPER_IFACE, query,
                    new String[] {PHPIndexer.FIELD_SUPER_IFACE, InterfaceElementImpl.IDX_FIELD, ClassElementImpl.IDX_FIELD});
            for (final IndexResult indexResult : result) {
                String[] values = indexResult.getValues(PHPIndexer.FIELD_SUPER_IFACE);
                for (String value : values) {
                    Signature signature = Signature.get(value);
                    final QualifiedName fqnForValue = QualifiedName.createFullyQualified(signature.string(1), signature.string(2));
                    if (query.matchesName(PhpElementKind.IFACE, fqnForValue)) {
                        directTypes.addAll(InterfaceElementImpl.fromSignature(NameKind.empty(), this, indexResult));
                        directTypes.addAll(ClassElementImpl.fromSignature(NameKind.empty(), this, indexResult));
                    }
                }
            }
        }
        return directTypes;
    }

    private static Set<String> toNames(Set<? extends PhpElement> elements) {
        Set<String> names = new HashSet<String>();
        for (PhpElement elem : elements) {
            names.add(elem.getName());
        }
        return names;
    }

    private Collection<? extends IndexResult> search(String key, String name, QuerySupport.Kind kind, String... terms) {
        try {
            long start = (LOG.isLoggable(Level.FINER)) ? System.currentTimeMillis() : 0;
            Collection<? extends IndexResult> results = index.query(key, name, kind, terms);

            if (LOG.isLoggable(Level.FINER)) {
                String msg = "IndexQuery.search(" + key + ", " + name + ", " + kind + ", " //NOI18N
                        + (terms == null || terms.length == 0 ? "no terms" : Arrays.asList(terms)) + ")"; //NOI18N
                LOG.finer(msg);

                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.log(Level.FINEST, null, new Throwable(msg));
                }

                for (IndexResult r : results) {
                    LOG.finer("Fields in " + r + " (" + r.getFile().getPath() + "):"); //NOI18N
                    for (String field : PHPIndexer.ALL_FIELDS) {
                        String value = r.getValue(field);
                        if (value != null) {
                            LOG.finest(" <" + field + "> = <" + value + ">"); //NOI18N
                        }
                    }
                    LOG.finer("----"); //NOI18N
                }
                LOG.finer(String.format("took: %d [ms]", System.currentTimeMillis() - start)); //NOI18N
                LOG.finer("===="); //NOI18N
            }

            return results;
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
            return Collections.<IndexResult>emptySet();
        }
    }

    private Collection<? extends IndexResult> results(final String indexField, final NameKind query) {
        return results(indexField, query, new String[]{indexField});
    }

    private Collection<? extends IndexResult> results(final String indexField,
            final NameKind query, final String[] fieldsToLoad) {
        return search(indexField, prepareIdxQuery(query.getQueryName(),query.getQueryKind()), Kind.CASE_INSENSITIVE_PREFIX, fieldsToLoad);
    }

    private void logQueryTime(final String queryDescription, final NameKind typeQuery,
            final NameKind memberQuery, final long start) {
        LOG.fine(String.format("%s for type query: [%s:%s] and took: member query: [%s:%s] %d [ms]", queryDescription,//NOI18N
                typeQuery.getQueryKind().toString(), typeQuery.getQuery().toString(),
                memberQuery.getQueryKind().toString(), memberQuery.getQuery().toString(),
                System.currentTimeMillis() - start)); //NOI18N

    }

    private void logQueryTime(final String queryDescription, final NameKind query, final long start) {
        LOG.fine(String.format("%s for query: [%s:%s] took: %d [ms]", queryDescription,//NOI18N
                query.getQueryKind().toString(), query.getQuery().toString(),
                System.currentTimeMillis() - start)); //NOI18N
    }

    private static String prepareIdxQuery(String textForQuery, Kind kind) {
        String query = textForQuery.toLowerCase();
        if (kind.equals(QuerySupport.Kind.CAMEL_CASE)) {
            final int length = textForQuery.length();
            if (length > 0 && Character.isLetter(textForQuery.charAt(0))) {
                query = query.substring(0, 1);//NOI18N
            } else if (length > 1 && textForQuery.charAt(0) == '$') {//NOI18N
                query = query.substring(0, 1);//NOI18N
            } else {
                query = "";//NOI18N
            }
        }
        return query;
    }

    @Override
    public TreeElement<TypeElement> getInheritedTypesAsTree(TypeElement typeElement) {
        return new TypeTreeElementImpl(typeElement, true);
    }

    @Override
    public TreeElement<TypeElement> getInheritedTypesAsTree(TypeElement typeElement, Set<TypeElement> preferredTypes) {
        return new TypeTreeElementImpl(typeElement, preferredTypes, true);
    }

    @Override
    public TreeElement<TypeElement> getInheritedByTypesAsTree(TypeElement typeElement) {
                return new TypeTreeElementImpl(typeElement, false);
    }

    @Override
    public TreeElement<TypeElement> getInheritedByTypesAsTree(TypeElement typeElement, Set<TypeElement> preferredTypes) {
                return new TypeTreeElementImpl(typeElement, preferredTypes, false);
    }

    @Override
    public Set<PhpElement> getTopLevelElements(NameKind query) {
        return getTopLevelElements(query, aliases, null);
    }

    @Override
    public Set<ClassElement> getClasses() {
        return getClasses(NameKind.empty(), aliases, null);
    }

    @Override
    public Set<ClassElement> getClasses(NameKind query) {
        return getClasses(query, aliases, null);
    }

    @Override
    public Set<InterfaceElement> getInterfaces() {
        return getInterfaces(NameKind.empty(), aliases, null);
    }

    @Override
    public Set<InterfaceElement> getInterfaces(NameKind query) {
        return getInterfaces(query, aliases, null);
    }

    @Override
    public Set<TypeElement> getTypes(NameKind query) {
        return getTypes(query, aliases, null);
    }

    @Override
    public Set<FunctionElement> getFunctions() {
        return getFunctions(NameKind.empty(), aliases, null);
    }

    @Override
    public Set<FunctionElement> getFunctions(NameKind query) {
        return getFunctions(query, aliases, null);
    }

    @Override
    public Set<ConstantElement> getConstants() {
        return getConstants(NameKind.empty(), aliases, null);
    }

    @Override
    public Set<ConstantElement> getConstants(NameKind query) {
        return getConstants(query, aliases, null);
    }

    @Override
    public Set<MethodElement> getConstructors(NameKind typeQuery) {
        return getConstructors(typeQuery, aliases, null);
    }

    @Override
    public Set<NamespaceElement> getNamespaces(NameKind query) {
        return getNamespaces(query, aliases, null);
    }

    @Override
    public Set<ClassElement> getClasses(final NameKind query, final Set<AliasedName> aliasedNames, final Trait trait) {
        final Set<ClassElement> retval = new HashSet<ClassElement>();
        for (final AliasedName aliasedName : aliasedNames) {
            for (final NameKind nextQuery : queriesForAlias(query, aliasedName, PhpElementKind.CLASS)) {
                for (final ClassElement nextClass : getClassesImpl(nextQuery)) {
                    final AliasedClass aliasedClass = new AliasedClass(aliasedName, nextClass);
                    if (trait != null) {
                        aliasedClass.setTrait(trait);
                    }
                    retval.add(aliasedClass);
                }
            }
        }
        retval.addAll(getClassesImpl(query));
        return retval;
    }

    @Override
    public Set<NamespaceElement> getNamespaces(final NameKind query, final Set<AliasedName> aliasedNames, final Trait trait) {
        final Set<NamespaceElement> retval = new HashSet<NamespaceElement>();
        for (final AliasedName aliasedName : aliasedNames) {
            final Set<NameKind> queriesForAlias = queriesForAlias(query, aliasedName, PhpElementKind.NAMESPACE_DECLARATION);
            for (final NameKind nextQuery : queriesForAlias) {
                for (final NamespaceElement nextNamespace : getNamespacesImpl(nextQuery)) {
                    final AliasedNamespace aliasedNamespace = new AliasedNamespace(aliasedName, nextNamespace);
                    if (trait != null) {
                        aliasedNamespace.setTrait(trait);
                    }
                    retval.add(aliasedNamespace);
                }
            }
        }
        retval.addAll(getNamespacesImpl(query));
        return retval;
    }

    @Override
    public Set<FunctionElement> getFunctions(final NameKind query, final Set<AliasedName> aliasedNames, final Trait trait) {
        final Set<FunctionElement> retval = new HashSet<FunctionElement>();
        for (final AliasedName aliasedName : aliasedNames) {
            for (final NameKind nextQuery : queriesForAlias(query, aliasedName, PhpElementKind.FUNCTION)) {
                for (final FunctionElement nextFunction : getFunctionsImpl(nextQuery)) {
                    final AliasedFunction aliasedFunction = new AliasedFunction(aliasedName, nextFunction);
                    if (trait != null) {
                        aliasedFunction.setTrait(trait);
                    }
                    retval.add(aliasedFunction);
                }
            }
        }
        retval.addAll(getFunctionsImpl(query));
        return retval;
    }

    @Override
    public Set<ConstantElement> getConstants(final NameKind query, final Set<AliasedName> aliasedNames, final Trait trait) {
        final Set<ConstantElement> retval = new HashSet<ConstantElement>();
        for (final AliasedName aliasedName : aliasedNames) {
            for (final NameKind nextQuery : queriesForAlias(query, aliasedName, PhpElementKind.CONSTANT)) {
                for (final ConstantElement nextConstant : getConstantsImpl(nextQuery)) {
                    final AliasedConstant aliasedConstant = new AliasedConstant(aliasedName, nextConstant);
                    if (trait != null) {
                        aliasedConstant.setTrait(trait);
                    }
                    retval.add(aliasedConstant);
                }
            }
        }
        retval.addAll(getConstantsImpl(query));
        return retval;
    }

    @Override
    public Set<MethodElement> getConstructors(final NameKind typeQuery, final Set<AliasedName> aliasedNames, final Trait trait) {
        final Set<MethodElement> retval = new HashSet<MethodElement>();
        for (final AliasedName aliasedName : aliasedNames) {
            for (final NameKind nextQuery : queriesForAlias(typeQuery, aliasedName, PhpElementKind.CLASS)) {
                for (ClassElement classElement : getClassesImpl(nextQuery)) {
                    final AliasedClass aliasedClass = new AliasedClass(aliasedName, classElement);
                    if (trait != null) {
                        aliasedClass.setTrait(trait);
                    }
                    final Set<MethodElement> constructorsImpl = getConstructorsImpl(aliasedClass, classElement, new LinkedHashSet<ClassElement>());
                    retval.addAll(constructorsImpl.isEmpty() ? getDefaultConstructors(aliasedClass) : constructorsImpl);
                }
            }
        }
        retval.addAll(getConstructorsImpl(typeQuery));
        return retval;
    }


    @Override
    public Set<PhpElement> getTopLevelElements(final NameKind query, final Set<AliasedName> aliasedNames, final Trait trait) {
        final Set<PhpElement> retval = new HashSet<PhpElement>();
        for (final AliasedName aliasedName : aliasedNames) {
            for (final NameKind nextQuery : queriesForAlias(query, aliasedName, PhpElementKind.CLASS)) {
                for (final PhpElement nextElement : getTopLevelElementsImpl(nextQuery)) {
                    AliasedElement aliasedElement = null;
                    if (nextElement instanceof ConstantElement) {
                        aliasedElement = new AliasedConstant(aliasedName, (ConstantElement) nextElement);
                    } else if (nextElement instanceof FunctionElement) {
                        aliasedElement = new AliasedFunction(aliasedName, (FunctionElement) nextElement);
                    } else if (nextElement instanceof ClassElement) {
                        aliasedElement = new AliasedClass(aliasedName, (ClassElement) nextElement);
                    } else if (nextElement instanceof InterfaceElement) {
                        aliasedElement = new AliasedInterface(aliasedName, (InterfaceElement) nextElement);
                    }
                    if (aliasedElement != null) {
                        if (trait != null) {
                            aliasedElement.setTrait(trait);
                        }
                        retval.add(aliasedElement);
                    }
                }
            }
        }
        retval.addAll(getTopLevelElementsImpl(query));
        return retval;
    }

    @Override
    public Set<InterfaceElement> getInterfaces(final NameKind query, final Set<AliasedName> aliasedNames, final Trait trait) {
        final Set<InterfaceElement> retval = new HashSet<InterfaceElement>();
        for (final AliasedName aliasedName : aliasedNames) {
            for (final NameKind nextQuery : queriesForAlias(query, aliasedName, PhpElementKind.IFACE)) {
                for (final InterfaceElement nextIface : getInterfacesImpl(nextQuery)) {
                    final AliasedInterface aliasedInterface = new AliasedInterface(aliasedName, nextIface);
                    if (trait != null) {
                        aliasedInterface.setTrait(trait);
                    }
                    retval.add(aliasedInterface);
                }
            }
        }
        retval.addAll(getInterfacesImpl(query));
        return retval;
    }

    public Set<TraitElement> getTraits() {
        return getTraits(NameKind.empty());
    }

    @Override
    public Set<TraitElement> getTraits(final NameKind query) {
        final Set<TraitElement> retval = new HashSet<TraitElement>();
        retval.addAll(getTraitsImpl(query));
        return retval;
    }

    @Override
    public Set<TypeElement> getTypes(final NameKind query, final Set<AliasedName> aliasedNames, final Trait trait) {
        final Set<TypeElement> retval = new HashSet<TypeElement>();
        for (final AliasedName aliasedName : aliasedNames) {
            for (final NameKind nextQuery : queriesForAlias(query, aliasedName, PhpElementKind.CLASS)) {
                for (final TypeElement nextType : getTypesImpl(nextQuery)) {
                    AliasedType aliasedType = null;
                    if (nextType instanceof ClassElement) {
                        aliasedType = new AliasedClass(aliasedName, (ClassElement) nextType);
                    } else if (nextType instanceof InterfaceElement) {
                        aliasedType = new AliasedInterface(aliasedName, (InterfaceElement) nextType);
                    } else {
                        assert false : nextType.getClass();
                    }
                    if (aliasedType != null) {
                        if (trait != null) {
                            aliasedType.setTrait(trait);
                        }
                        retval.add(aliasedType);
                    }
                }
            }
        }
        retval.addAll(getTypesImpl(query));
        return retval;
    }


}
