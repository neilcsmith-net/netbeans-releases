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
package org.netbeans.modules.javafx2.editor.parser;

import java.net.URL;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.xml.lexer.XMLTokenId;
import org.netbeans.modules.javafx2.editor.sax.ContentLocator;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import static org.netbeans.modules.javafx2.editor.completion.model.FxXmlSymbols.*;
import static org.netbeans.modules.javafx2.editor.JavaFXEditorUtils.FXML_FX_NAMESPACE;
import org.netbeans.modules.javafx2.editor.ErrorMark;
import org.netbeans.modules.javafx2.editor.sax.SequenceContentHandler;
import org.netbeans.modules.javafx2.editor.completion.model.EventHandler;
import org.netbeans.modules.javafx2.editor.completion.model.FxInclude;
import org.netbeans.modules.javafx2.editor.completion.model.FxInstance;
import org.netbeans.modules.javafx2.editor.completion.model.FxInstanceCopy;
import org.netbeans.modules.javafx2.editor.completion.model.FxModel;
import org.netbeans.modules.javafx2.editor.completion.model.FxNewInstance;
import org.netbeans.modules.javafx2.editor.completion.model.FxNode;
import org.openide.util.NbBundle;

import static org.netbeans.modules.javafx2.editor.parser.Bundle.*;
import org.netbeans.modules.javafx2.editor.completion.model.FxNode.Kind;
import org.netbeans.modules.javafx2.editor.completion.model.FxObjectBase;
import org.netbeans.modules.javafx2.editor.completion.model.FxXmlSymbols;
import org.netbeans.modules.javafx2.editor.completion.model.ImportDecl;
import org.netbeans.modules.javafx2.editor.completion.model.LanguageDecl;
import org.netbeans.modules.javafx2.editor.completion.model.MapProperty;
import org.netbeans.modules.javafx2.editor.completion.model.PropertySetter;
import org.netbeans.modules.javafx2.editor.completion.model.PropertyValue;
import org.netbeans.modules.javafx2.editor.completion.model.StaticProperty;
import org.openide.util.Utilities;

/**
 *
 * @author sdedic
 */
public class FxModelBuilder implements SequenceContentHandler, ContentLocator.Receiver {
    /**
     * URL of the current source.
     */
    private URL sourceURL;
    
    /**
     * Stack of Elements, as they are processed.
     */
    private Deque<FxNode> nodeStack = new LinkedList<FxNode>();
    
    /**
     * Supplemental interface, to get offsets & other info from the parser
     */
    private ContentLocator  contentLocator;
   
    /**
     * The created model
     */
    private FxModel     fxModel;
    
    /**
     * The current parsed bean instance.
     */
    private FxInstance  current;

    /**
     * Collected import declarations.
     */
    private List<ImportDecl>  imports = new ArrayList<ImportDecl>();
    
    /**
     * List of errors found during parsing
     */
    private List<ErrorMark> errors = new ArrayList<ErrorMark>();
    
    private ModelAccessor accessor = ModelAccessor.INSTANCE;
    
    private String controllerName;
    
    private FxInstance rootComponent;
    
    private LanguageDecl language;
    
    private String tagName;
    
    public void setBaseURL(URL sourceURL) {
        this.sourceURL = sourceURL;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        // no op, we don't need SAX locator.
    }
    
    NodeInfo i(FxNode n) {
        return accessor.i(n);
    }
    
    private void initElement(FxNode node) {
        NodeInfo ni = i(node);
        ni.startAt(contentLocator.getElementOffset()).startContent(contentLocator.getEndOffset());
        ni.setTagName(tagName);
    }
    
    private void initAttribute(FxNode node, String atQName) {
        NodeInfo ni = i(node);
        ni.makeAttribute();
        int[] offsets =  contentLocator.getAttributeOffsets(atQName);
        ni.startAt(offsets[ContentLocator.OFFSET_START]).endsAt(offsets[ContentLocator.OFFSET_END]).
                startContent(offsets[ContentLocator.OFFSET_VALUE_START]).endContent(offsets[ContentLocator.OFFSET_VALUE_END]);
    }

    @Override
    public void startDocument() throws SAXException {
        fxModel = accessor.newModel(sourceURL, imports, instanceDefinitions);
        initElement(fxModel);
        
        nodeStack.push(fxModel);
    }
    
    @Override
    public void endDocument() throws SAXException {
        accessor.initModel(fxModel, controllerName, rootComponent, language);
        int end = contentLocator.getElementOffset();
        i(fxModel).endContent(end).endsAt(end);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }
    
    private void addAttributeError(String qName, String code, String message, Object... params) {
        int[] offsets = contentLocator.getAttributeOffsets(qName);
        int s;
        
        if (offsets == null) {
            FxNode n = nodeStack.peek();
            if (n != null) {
                s = i(n).getStart();
            } else {
                s = -1;
            }
        } else {
            s = offsets[ContentLocator.OFFSET_START];
        }
        addError(new ErrorMark(
            s, qName.length(),
            code,
            message,
            params
        ));
    }
    
    @NbBundle.Messages({
        "# {0} - tag name",
        "ERR_tagNotJavaIdentifier=Invalid class name: {0}",
        "# {0} - tag name",
        "ERR_fxControllerPermittedOnRoot=fx:controller is not permitted on tag {0}. Can be only present on root element."
    })
    private FxNewInstance handleClassTag(String localName, Attributes atts) {
        String fxValueContent = null;
        String fxFactoryContent = null;
        String fxId = null;
        
        int off = contentLocator.getElementOffset() + 1; // the <
        
        for (int i = 0; i < atts.getLength(); i++) {
            String uri = atts.getURI(i);
            if (!FXML_FX_NAMESPACE.equals(uri)) {
                // no special attribute
                continue;
            }
            String name = atts.getLocalName(i);
            if (FX_VALUE.equals(name)) {
                fxValueContent = atts.getValue(i);
            } else if (FX_FACTORY.equals(name)) {
                fxFactoryContent = atts.getValue(i);
            } else if (FX_ID.equals(name)) {
                fxId = atts.getValue(i);
            } else if (FX_CONTROLLER.equals(name)) {
                if (nodeStack.peek().getKind() != Kind.Source) {
                    addAttributeError(atts.getQName(i),
                        "fx-controller-permitted-on-root",
                        ERR_fxControllerPermittedOnRoot(localName),
                        localName
                    );
                } else {
                    controllerName = atts.getValue(i);
                }
            } else {
                addAttributeError(
                    atts.getQName(i),
                    "invalid-property-reserved-name",
                    ERR_invalidReservedPropertyName(name),
                    name
                );
            }
        }
        
        // first we must check how this class tag is created. 
        FxNewInstance instance = accessor.createInstance(localName, fxValueContent, fxFactoryContent, fxId);
        
        if (!FxXmlSymbols.isQualifiedIdentifier(localName)) {
            // not a java identifier, error
            addError(
                new ErrorMark(
                    off, localName.length(), 
                    "invalid-class-name", 
                    ERR_tagNotJavaIdentifier(localName),
                    localName
            ));
            accessor.makeBroken(instance);
            return instance;
        }

        return instance;
    }
    
    private static final String EVENT_HANDLER_PREFIX = "on"; // NOI18N
    private static final int EVENT_HANDLER_PREFIX_LEN = 2;
    private static final String EVENT_HANDLER_METHOD_PREFIX = "#";
    
    private FxNode processEventHandlerAttribute(String event, String content) {
        EventHandler eh;

        if (content.startsWith(EVENT_HANDLER_METHOD_PREFIX)) {
            eh = accessor.asMethodRef(accessor.createEventHandler(event));
            accessor.addContent(eh, content.substring(1));
            
        } else {
            eh = accessor.createEventHandler(event);
            accessor.addContent(eh, content);
        }
        return eh;
    }
    
    @NbBundle.Messages({
        "# {0} - attribute name",
        "ERR_lowercasePropertyName=Invalid property name: {0}. Property name, or the last component of a static property name must start with lowercase.",
        "# {0} - attribute name",
        "ERR_invalidReservedPropertyName=Unknown name in FXML reserved namespace: {0}",
        "# {0} - attribute qname",
        "# {1} - tag name",
        "ERR_unsupportedAttribute=Unsupported attribute {0} on {1}"
    })
    private void processInstanceAttributes(Attributes atts) {
        for (int i = 0; i < atts.getLength(); i++) {
            String uri = atts.getURI(i);
            String name = atts.getLocalName(i);
            String qname = atts.getQName(i);
            
            PropertySetter ps = null;
            
            FxNode node;
            
            if (qname.startsWith("xmlns")) { // NOI18N
                // FIXME - xmlns attributes will be represented as FxNodes :-/
                continue;
            }
            
            if (FXML_FX_NAMESPACE.equals(uri)) {
                if (!(FX_ID.equals(name) || FX_CONTROLLER.equals(name) || FX_VALUE.equals(name) || FX_FACTORY.contains(name))) {
                    addAttributeError(qname, "error-unsupported-attribute", 
                            ERR_unsupportedAttribute(qname, tagName), 
                            qname, tagName);
                }
                continue;
            }
            
            if (current instanceof FxInstanceCopy) {
                if (FxXmlSymbols.FX_ATTR_REFERENCE_SOURCE.equals(name) && uri == null) {
                    // ignore source in fx:copy
                    continue;
                }
            }
            
            // if the name begins with "on", it's an event handler.
            if (name.startsWith(EVENT_HANDLER_PREFIX) && name.length() > EVENT_HANDLER_PREFIX_LEN) {
                String en = Character.toLowerCase(name.charAt(EVENT_HANDLER_PREFIX_LEN)) +
                        name.substring(EVENT_HANDLER_PREFIX_LEN + 1);
                node = processEventHandlerAttribute(en, atts.getValue(i));
                // special hack for fx:copy or fx:reference
            } else {
                // FIXME - error detection for static property
                int stProp = FxXmlSymbols.findStaticProperty(name);
                if (stProp == -2) {
                    // report error, not a well formed property name.
                    addAttributeError(
                            qname,
                            "invalid-property-name",
                            ERR_lowercasePropertyName(name),
                            name
                    );
                    node = accessor.makeBroken(accessor.createProperty(name, false));
                } else if (stProp == -1) {
                    // this is a normal property
                    node = ps = accessor.createProperty(name, false);
                } else {
                    // it is a static property
                    node = ps = accessor.createStaticProperty(
                            name.substring(stProp + 1),
                            name.substring(0, stProp)
                    );
                }
                if (ps != null) {
                    accessor.addContent(ps, atts.getValue(i));
                    node = ps;
                }
            }
            initAttribute(node, qname);
            attachProperty(ps);
            attachChildNode(node);
        }
    }
    
    private NodeInfo    definitionsNode;
    
    private List<FxNewInstance> instanceDefinitions = new ArrayList<FxNewInstance>();
    
    private int definitions;
    private boolean definitionsFound;
    
    @NbBundle.Messages({
        "# {0} - tag name",
        "ERR_invalidFxElement=Unknown element in fx: namespace: {0}",
        "ERR_duplicateDefinitions=Duplicate 'definitions' element"
    })
    private FxNode handleFxmlElement(String localName, Attributes atts) {
        if (FX_DEFINITIONS.equals(localName)) {
            definitions++;
            
            if (definitionsFound) {
                // error, defs cannot be nested or used more than once. Ignore.
                addError("duplicate-definitions", ERR_duplicateDefinitions());
            }
            FxNode n = accessor.createElement(localName);
            definitionsNode = accessor.i(n);
            return n;
        } else if (FX_COPY.equals(localName)) {
            return handleFxReference(atts, true);
        } else if (FX_REFERENCE.equals(localName)) {
            return handleFxReference(atts, false);
        } else if (FX_INCLUDE.equals(localName)) {
            return handleFxInclude(atts, localName);
        } else {
            // error, invalid fx: element
            FxNode n = accessor.createErrorElement(localName);
            initElement(n);
            addError("invalid-fx-element", ERR_invalidFxElement(localName), localName);
            return n;
        }
    }
    
    @NbBundle.Messages({
        "# {0} - attribute local name",
        "ERR_unexpectedReferenceAttribute=Unexpected attribute in fx:reference or fx:copy: {0}",
        "ERR_missingReferenceSource=Missing 'source' attribute in fx:reference or fx:copy"
    })
    private FxNode handleFxReference(Attributes atts, boolean copy) {
        String refId = null;
        String id = null;
        
        for (int i = 0; i < atts.getLength(); i++) {
            String ns = atts.getURI(i);
            String name = atts.getLocalName(i);
            if (!FXML_FX_NAMESPACE.equals(ns)) {
                if (FX_ATTR_REFERENCE_SOURCE.equals(name) && refId == null) {
                    refId = atts.getValue(i);
                } else if (!copy) {
                    // error, references do not support normal attributes
                    addAttributeError(atts.getQName(i),
                        "invalid-reference-attribute",
                        ERR_unexpectedReferenceAttribute(name),
                        name
                    );
                }
            } else {
                if (FX_ID.equals(name) && id == null) {
                    id = atts.getValue(i);
                } else {
                    // error, unexpected attribute
                    addAttributeError(atts.getQName(i),
                        "invalid-reference-attribute",
                        ERR_unexpectedReferenceAttribute(name),
                        name
                    );
                }
            }
        }
        
        FxObjectBase ref = accessor.createCopyReference(copy, refId);
        if (refId == null) {
            // error, no source attribute found
            addError(
                    "missing-reference-source",
                    ERR_missingReferenceSource()
            );
            accessor.makeBroken(ref);
        }
        return ref;
    }
    
    private void pushInstance(FxNode instance) {
        nodeStack.push(instance);
        if (instance.getKind() == Kind.Instance) {
            current = (FxInstance)instance;
        } else {
            current = null;
        }
    }
    
    /**
     * Checks that the instance is allowed in this context. May even create e.g.
     * default property setter etc. Will return true, if the instance can be attached to the parent.
     */
    @NbBundle.Messages({
        "# {0} - tag name",
        "ERR_moreRootElements=Duplicate root element: {0}",
        "ERR_instanceInMapProperty=Cannot add instances directly to readonly Map",
        "# {0} - parent tag name",
        "ERR_parentNotSupportInstance=Instances cannot be added to the parent {0}"
    })
    private FxNode attachInstance(FxObjectBase instance) {
        String localName = instance.getSourceName();
        int off = contentLocator.getElementOffset() + 1;
        
        // check the parent, whether it is appropriate to host such a node:
        FxNode parent = nodeStack.peek();
        
        if (parent.getKind() == Kind.Instance) {
            // pretend we have a default property
            PropertySetter s = accessor.createProperty(null, true);
            i(s).startAt(contentLocator.getElementOffset());
            attachChildNode(s);
            parent = s;
        }
        
        if (parent.getKind() == Kind.Source) {
            FxObjectBase old = rootComponent;
            if (old != null) {
                addError(new ErrorMark(
                    off, contentLocator.getEndOffset() - off,
                    "duplicate-root",
                    ERR_moreRootElements(localName),
                    localName
                ));
                accessor.makeBroken(instance);
            } else if (!(instance instanceof FxInstance)) {
                // FIXME - report error that fx:reference is not accepted on root element
                throw new UnsupportedOperationException();
            } else {
                rootComponent = (FxInstance)instance;
            }
        } else if (parent.getKind() == Kind.Property) {
            if (parent instanceof MapProperty) {
                addError(new ErrorMark(
                    off, contentLocator.getEndOffset() - off,
                    "instance-in-map-property",
                    ERR_instanceInMapProperty(),
                    localName
                ));
                accessor.makeBroken(instance);
            }
        } else if (parent.getKind() == Kind.Element &&
                parent.getSourceName().equals(FxXmlSymbols.FX_DEFINITIONS) && (instance instanceof FxNewInstance)) {
            instanceDefinitions.add((FxNewInstance)instance);
        } else {
            if (parent.getKind() != Kind.Error) {
                addError(new ErrorMark(
                    off, contentLocator.getEndOffset() - off,
                    "parent-not-support-instance",
                    ERR_parentNotSupportInstance(parent.getSourceName()))
                );
                accessor.makeBroken(instance);
            }
        }
        return instance;
    }
    
    private int start;
    private int end;
    
    private FxNode handleEventHandlerTag(String eventName) {
        return accessor.createEventHandler(eventName);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        this.tagName = localName;
        
        FxNode newElement;
        
        start = contentLocator.getElementOffset();
        end = contentLocator.getEndOffset();
        
        addElementErrors();
        
        if (FXML_FX_NAMESPACE.equals(uri)) {
            newElement = handleFxmlElement(localName, atts);
        } else {
            String eventName = FxXmlSymbols.getEventHandlerName(localName);
            // non-fx namespace, should be either an instance, or a property or an event
            if (eventName != null) {
                newElement = handleEventHandlerTag(eventName);
            } else if (FxXmlSymbols.isClassTagName(localName)) {
                newElement = handleClassTag(localName, atts);
            } else {
                newElement = handlePropertyTag(localName, atts);
            }
        }
        if (newElement == null) {
            throw new IllegalStateException();
        }
        initElement(newElement);
        
        FxNode newNode = newElement;
        
        // if not broken attempt to attach the Element to a parent
        if (!newElement.isBroken()) {
            if (newElement instanceof FxObjectBase) {
                newNode = attachInstance((FxObjectBase)newElement);
            } else if (newElement instanceof PropertyValue) {
                newNode = attachProperty((PropertyValue)newElement);
            }
        }
        attachChildNode(newNode);
        
        // process attributes, iff it is an instance. Attribute processing needs the node pushed
        // on the stack, so it is delayed after attachChildNode
        if (newNode.getKind() == Kind.Instance) {
            processInstanceAttributes(atts);
        }
    }
    
    private PropertyValue handleStaticProperty(String className,  String propName, Attributes atts) {
        // FIXME - check that attributes are empty
        StaticProperty s = accessor.createStaticProperty(propName, className);
        return s;
    }
    
    /**
     * Processes instance (non-static) property. As per examples in Guides, instance
     * property element must NOT have any attributes; otherwise it corresponds to
     * an readonly Map element, and the property must be of the Map type.
     * 
     * @param propName
     * @param atts As
     */
    @NbBundle.Messages({
        "# {0} - attribute name",
        "ERR_propertyElementNamespacedAttribute=Property elements may not contain attributes with namespace: {0}"
    })
    private PropertyValue handleSimpleProperty(String propName, Attributes atts) {
        PropertyValue p;
        
        // no relevant attributes to use, real simple property then
        p = accessor.createProperty(propName, false);
        
        return p;
    }
    
    @NbBundle.Messages({
        "# {0} - parent tag local name",
        "ERR_doesNotAcceptProperty=The parent element {0} does not accept properties"
    })
    private FxNode attachProperty(PropertyValue p) {
        // FIXME - if 'current' is null,
        if (current == null) {
            FxNode node = nodeStack.peek();
            addError(new ErrorMark(
                start, end - start, 
                "parent-not-accept-property",
                ERR_doesNotAcceptProperty(node.getSourceName()),
                node
            ));
            accessor.makeBroken(p);
        }
        return p;
    }
    
    private PropertyValue handleMapProperty(String propName, Attributes atts) {
        Map<String, CharSequence> contents = new HashMap<String, CharSequence>();
        
        for (int i = 0; i < atts.getLength(); i++) {
            String uri = atts.getURI(i);
            if (uri != null) {
                continue;
            }
            contents.put(atts.getLocalName(i), atts.getValue(i));
        }
        return accessor.createMapProperty(propName, contents);
    }
    
    @NbBundle.Messages({
        "# {0} - tag name",
        "ERR_invalidPropertyName=Invalid property name: {0}"
    })
    private FxNode handlePropertyTag(String propName, Attributes atts) {
        PropertyValue pv;
        
        int errorAttrs = 0;
        for (int i = 0; i < atts.getLength(); i++) {
            String uri = atts.getURI(i);
            if (uri != null) {
                String qn = atts.getQName(i);
                errorAttrs++;
                addAttributeError(qn, 
                    "property-namespaced-attribute",
                    ERR_propertyElementNamespacedAttribute(qn),
                    qn
                );
            }
        }
        
        int stProp = FxXmlSymbols.findStaticProperty(propName);
        switch (stProp) {
            case -1:
                // simple property
                if (!Utilities.isJavaIdentifier(propName)) {
                    addError(new ErrorMark(
                        start, end,
                        "invalid-property-name",
                        ERR_invalidPropertyName(propName),
                        propName
                    ));
                }
                if (errorAttrs == atts.getLength()) {
                    pv = handleSimpleProperty(propName, atts);
                } else {
                    pv = handleMapProperty(propName, atts);
                }
                break;
                
            case -2:
                // broken name, but must create a node
                pv = accessor.makeBroken(accessor.createProperty(propName, false));
                // do not add the property to the parent, it's broken beyond repair
                addError(new ErrorMark(
                    start, end,
                    "invalid-property-name",
                    ERR_invalidPropertyName(propName),
                    propName
                ));
                break;
                
            default:
                // static property, just ignore for now
                pv = handleStaticProperty(propName.substring(0, stProp), 
                        propName.substring(stProp + 1), atts);
                break;
        }
        return pv;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        addElementErrors();
        FxNode node = nodeStack.pop();
        i(node).endsAt(contentLocator.getEndOffset()).endContent(contentLocator.getElementOffset());
        if (node instanceof PropertySetter) {
            PropertySetter s = (PropertySetter)node;
            if (s.isImplicit()) {
                // actually the outer element ends
                node = nodeStack.pop();
                // copy the offset information
                i(node).endsAt(contentLocator.getEndOffset()).endContent(contentLocator.getElementOffset());
            }
        }
        String tn = node.getSourceName();
        if (!tn.equals(localName)) {
            throw new IllegalStateException();
        }
        // special hack for parent nodes, which are implicit property setters:
        FxNode parentNode = nodeStack.peek();
        if (parentNode instanceof PropertySetter) {
            PropertySetter ps = (PropertySetter)parentNode;
            if (ps.isImplicit() && ps.getContent() == null) {
                i(ps).endsAt(contentLocator.getEndOffset()).endContent(contentLocator.getEndOffset());
            }
        }
        if (!nodeStack.isEmpty() && nodeStack.peek().getKind() == Kind.Instance) {
            current = (FxInstance)nodeStack.peek();
        }
        
    }

    @Override
    public void characterSequence(CharSequence seq) {
        
        addElementErrors();
        
        int length = seq.length();
        FxNode node = nodeStack.peek();
        FxNode addedNode = null;
        
        switch (node.getKind()) {
            case Event:
                addedNode = handleEventContent(seq);
                break;
            case Instance:
                addedNode = handleInstanceContent(seq);
                break;
            case Property:
                addedNode = handlePropertyContent(seq);
                break;
                
            default:
                addError(new ErrorMark(
                    contentLocator.getElementOffset(),
                    length,
                    "unexpected-characters",
                    ERR_unexpectedCharacters()
                ));
        }
        if (addedNode != null) {
            i(addedNode).endsAt(contentLocator.getEndOffset());
        }

    }
    
    private FxNode handleEventContent(CharSequence content) {
        EventHandler eh = (EventHandler)nodeStack.peek();
        if (eh.isScript() && !eh.hasContent()) {
            if (content.length() == 0) {
                throw new UnsupportedOperationException();
            } else {
                if (content.charAt(0) == '#') {
                    content = content.subSequence(1, content.length());
                    eh = accessor.asMethodRef(eh);
                }
            }
        }
        accessor.addContent(eh, content);
        return eh;
    }

    @NbBundle.Messages({
        "ERR_unexpectedCharacters=Unexpected character content"
    })
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    }
    
    private FxNode handleInstanceContent(CharSequence seq) {
        // find among properties as setter, which is marked as implicit. If there's none, create one.
        PropertySetter defaultSetter = null;
        
        for (PropertyValue p : current.getProperties()) {
            if (p instanceof PropertySetter) {
                PropertySetter ps = (PropertySetter)p;
                if (ps.isImplicit()) {
                    defaultSetter = ps;
                }
            }
        }
        
        if (defaultSetter == null) {
            defaultSetter = accessor.createProperty(null, true);
            i(defaultSetter).startAt(contentLocator.getElementOffset());
            attachProperty(defaultSetter);
            attachChildNode(defaultSetter);
        }
        accessor.addContent(defaultSetter, seq);
        return defaultSetter;
    }
    
    private ErrorMark addError(String errCode, String message, Object... params) {
        int offs = contentLocator.getElementOffset();
        ErrorMark m = new ErrorMark(
            offs,
            contentLocator.getEndOffset() - offs,
            errCode,
            message, 
            params
        );
        addError(m);
        return m;
    }

    @NbBundle.Messages({
        "ERR_mixedContentNotAllowed=Mixed content is not allowed in property elements"
    })
    private FxNode handlePropertyContent(CharSequence seq) {
        FxNode node = nodeStack.peek();
        if (!(node instanceof PropertySetter)) {
            addError(
                "unexpected-characters", 
                ERR_unexpectedCharacters()
            );
            return null;
        }
        // if the property has already received some bean instances, report 
        // invalid content
        PropertySetter ps = (PropertySetter)node;
        if (ps.getValues() != null) {
            addError(
                "mixed-content-not-allowed", 
                ERR_mixedContentNotAllowed()
            );
        }
        accessor.addContent((PropertySetter)node, seq);
        return node;
    }

    @Override
    public void ignorableWhitespaceSequence(CharSequence seq) {
        addElementErrors();
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        // check whether the current node supports content
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        start = contentLocator.getElementOffset();
        end = contentLocator.getEndOffset();
        
        addElementErrors();
        
        FxNode node = null;
        
        if (FX_IMPORT.equals(target)) {
            node = handleFxImport(data);
        } else if (FX_LANGUAGE.equals(target)) {
            node = handleFxLanguage(data);
        } else if (!"xml".equals(target)) {
            handleErrorInstruction(target, data);
        }
        if (node == null) {
            return;
        }
        i(node).makePI().startAt(start).endsAt(end);
        attachChildNode(node);
    }
    
    /**
     * Processes "import" PI. Checks syntax of the identifier
     * @param data 
     */
    @NbBundle.Messages({
        "ERR_importNotJavaIdentifier=Imported symbol must be a class or package name.",
        "ERR_importInsideElement=Imports must be at top level, not nested in elements",
        "ERR_importFollowsRoot=Import must not follow the root element",
        "ERR_missingImportIdentifier=Identifier missing in ?import instruction"
    })
    private FxNode handleFxImport(String data) {
        if (data.endsWith("?")) {
            // recovery from unterminated ?> -- the lexer will report ? as part of PI data.
            data = data.substring(0, data.length() -1);
        }
        if ("".equals(data)) {
            addError("missing-import-identifier", ERR_missingImportIdentifier());
            return null;
        }
        int lastDot = data.lastIndexOf('.');
        boolean starImport = false;
        
        if (lastDot != -1 && lastDot < data.length() - 1) {
            if (FX_IMPORT_STAR.equals(data.substring(lastDot + 1))) {
                starImport = true;
                data = data.substring(0, lastDot);
            }
        }
        ImportDecl decl = accessor.createImport(data, starImport);
        if (!FxXmlSymbols.isQualifiedIdentifier(data)) {
            addAttributeError(ContentLocator.ATTRIBUTE_DATA,
                "import-not-java-identifier",
                ERR_importNotJavaIdentifier(), data
            );
            accessor.makeBroken(decl);
        }
        
        // check that ?import is at top level, and does not follow a root element:
        if (!isTopLevel()) {
            int o = contentLocator.getElementOffset();
            addError(
                new ErrorMark(o, contentLocator.getEndOffset() - o, 
                    "import-inside-element",
                    ERR_importInsideElement())
            );
        } 
        
        imports.add(decl);
        
        return decl;
    }
    
    /**
     * Processes ?include directive
     * 
     * @param include 
     */
    @NbBundle.Messages({
        "ERR_missingIncludeName=Missing include name",
        "# {0} - attribute name",
        "ERR_unexpectedIncludeAttribute=Unexpected attribute in fx:include: {0}"
    })
    private FxNode handleFxInclude(Attributes atts, String localName) {
        String include = null;
        
        for (int i = 0; i < atts.getLength(); i++) {
            String attName = atts.getLocalName(i);
            if (FX_ATTR_REFERENCE_SOURCE.equals(attName)) {
                include = atts.getValue(i);
            } else {
                String qName = atts.getQName(i);
                addAttributeError(
                    qName,
                    "unexpected-include-attribute",
                    ERR_unexpectedIncludeAttribute(qName),
                    qName
                );
            }
        }
        if (include == null) {
            // must be some text, otherwise = error
            addAttributeError(
                ContentLocator.ATTRIBUTE_TARGET, 
                "missing-included-name",
                ERR_missingIncludeName()
            );
            
            FxNode n = accessor.createErrorElement(localName);
            initElement(n);
            addError("invalid-fx-element", ERR_invalidFxElement(localName), localName);
            return n;
        }
        // guide: fnames starting with slash are treated relative to the classpath
        FxInclude fxInclude = accessor.createInclude(include);
        return fxInclude;
    }
    
    @NbBundle.Messages({
        "ERR_missingLanguageName=Language name is missing",
        "ERR_duplicateLanguageDeclaration=Language is already declared",
        "ERR_languageNotTopLevel=Language declaration must precede all elements"
    })
    private FxNode handleFxLanguage(String language) {
        LanguageDecl decl = accessor.createLanguage(language);
        if (language == null) {
            addAttributeError(
                ContentLocator.ATTRIBUTE_TARGET,
                "missing-language-name",
                ERR_missingLanguageName()
            );
            accessor.makeBroken(decl);
        } else {
            if (this.language != null) {
                // error, language can be specified only once:
                addError(new ErrorMark(
                    start, end - start,
                    "duplicate-language",
                    ERR_duplicateLanguageDeclaration(),
                    fxModel.getLanguage()
                ));
                accessor.makeBroken(decl);
            } else {
                if (!isTopLevel()) {
                    addError(
                        new ErrorMark(
                            start, end - start,
                            "language-not-toplevel",
                            ERR_languageNotTopLevel()
                        )
                    );
                    accessor.makeBroken(decl);
                }
                this.language = decl;
            }
        }
        return decl;
    }
    
    private boolean isTopLevel() {
        return nodeStack.peek() == fxModel;
    }
    
    void addElementErrors() {
        this.errors.addAll(contentLocator.getErrors());
    }
    
    void addError(ErrorMark mark) {
        this.errors.add(mark);
    }
    
    private void attachChildNode(FxNode node) {
        FxNode top = nodeStack.peek();
        i(top).addChild(node);
        if (!node.isBroken() && (node.getKind() != FxNode.Kind.Element)) {
            accessor.addChild(top, node);
        }
        if (i(node).isElement()) {
            pushInstance(node);
        }
    }
    
    @NbBundle.Messages({
        "# {0} - PI target",
        "ERR_invalidProcessingInstruction=Invalid processing instruction: {0}. Expected 'import', 'include' or 'language'",
        "ERR_missingProcessingInstruction=Missing processing intruction."
    })
    private void handleErrorInstruction(String target, String data) {
        int start = contentLocator.getElementOffset();
        int offset = -1;
        int piOffset = -1;
        
        TokenSequence<XMLTokenId> seq = contentLocator.getTokenSequence();
        
        // lex up to the invalid target:
        seq.move(start);
        boolean found = false;
        while (!found && seq.moveNext()) {
            Token<XMLTokenId> t = seq.token();
            switch (t.id()) {
                case PI_START:
                    piOffset = offset;
                    if (target == null) {
                        found = true;
                    }
                case WS:
                    break;
                    
                default:
                case PI_TARGET:
                    offset = seq.offset();
                    found = true;
                    break;
            }
        }
        ErrorMark mark;
        
        if (target != null) {
            mark = new ErrorMark(offset, seq.token().length(), 
                    "invalid-processing-instruction", 
                    ERR_invalidProcessingInstruction(target),
                    target
            );
        } else {
            mark = new ErrorMark(piOffset, seq.token().length(), 
                    "missing-processing-instruction",
                    ERR_missingProcessingInstruction()
            );
        }
        addError(mark);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }

    @Override
    public void setContentLocator(ContentLocator l) {
        this.contentLocator = l;
    }
    
    FxModel getModel() {
        return fxModel;
    }
    
    List<ErrorMark> getErrors() {
        return errors;
    }
}
