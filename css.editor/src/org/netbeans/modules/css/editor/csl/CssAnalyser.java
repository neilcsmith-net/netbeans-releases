/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
package org.netbeans.modules.css.editor.csl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.css.editor.Css3Utils;
import org.netbeans.modules.css.editor.CssDeclarationContext;
import org.netbeans.modules.css.editor.module.CssModuleSupport;
import org.netbeans.modules.css.editor.module.spi.SemanticAnalyzerResult;
import org.netbeans.modules.css.lib.api.CssParserResult;
import org.netbeans.modules.css.lib.api.ErrorsProvider;
import org.netbeans.modules.css.lib.api.Node;
import org.netbeans.modules.css.lib.api.NodeType;
import org.netbeans.modules.css.lib.api.NodeVisitor;
import org.netbeans.modules.css.lib.api.properties.Properties;
import org.netbeans.modules.css.lib.api.properties.PropertyDefinition;
import org.netbeans.modules.css.lib.api.properties.ResolvedProperty;
import org.netbeans.modules.css.lib.api.properties.Token;
import org.netbeans.modules.parsing.api.Snapshot;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.NotImplementedException;
import org.openide.util.lookup.ServiceProvider;

/**
 * Provides extended errors from semantic css analysis.
 * 
 * @author mfukala@netbeans.org
 */
@ServiceProvider(service=ErrorsProvider.class)
public class CssAnalyser implements ErrorsProvider {

    private static final String UNKNOWN_PROPERTY_BUNDLE_KEY = "unknown_property";//NOI18N
    private static final String UNKNOWN_PROPERTY_ERROR_KEY_DELIMITER = "/";//NOI18N
    private static final String UNKNOWN_PROPERTY_ERROR_KEY = "unknown_property" + UNKNOWN_PROPERTY_ERROR_KEY_DELIMITER;//NOI18N
    private static final String INVALID_PROPERTY_VALUE = "invalid_property_value";//NOI18N

    @Override
    public List<? extends Error> getExtendedDiagnostics(CssParserResult parserResult) {
        final Node node = parserResult.getParseTree();
        final Snapshot snapshot = parserResult.getSnapshot();
        final FileObject file = snapshot.getSource().getFileObject();
        
        List<Error> errors = new ArrayList<Error>();
        NodeVisitor<List<Error>> visitor = new NodeVisitor<List<Error>>(errors) {

            @Override
            public boolean visit(Node node) {
                if (node.type() == NodeType.declaration) {
                    
                    //do not check declarations in fontFace and counterStyle
                    Node parent = node.parent();
                    if(parent.type() == NodeType.declarations) {
                        switch(parent.parent().type()) {
                            case fontFace:
                            case counterStyle:
                                return false; 
                        }
                    }
                    SemanticAnalyzerResult analyzeDeclaration = CssModuleSupport.analyzeDeclaration(node);
                    switch(analyzeDeclaration.getType()) {
                        case ERRONEOUS:                            
                            throw new NotImplementedException(); //TODO
                        case VALID:
                            return false;
                        case UNKNOWN:
                            break;
                    }
                    
                    CssDeclarationContext ctx = new CssDeclarationContext(node);
                    
                    Node propertyNode = ctx.getProperty();
                    Node valueNode = ctx.getPropertyValue();
                    
                    if (propertyNode != null) {
                        String propertyName = ctx.getPropertyNameImage();

                        //check non css 2.1 compatible properties and ignore them
                        //values are not checked as well
                        if(isNonCss21CompatibleDeclarationPropertyName(propertyName)) {
                            return false;
                        }

                        //check for vendor specific properies - ignore them
                        PropertyDefinition property = Properties.getPropertyDefinition(propertyName);
                        if (!Css3Utils.containsGeneratedCode(propertyName) && !Css3Utils.isVendorSpecificProperty(propertyName) && property == null) {
                            //unknown property - report
                            String msg = NbBundle.getMessage(CssAnalyser.class, UNKNOWN_PROPERTY_BUNDLE_KEY, propertyName);
                            Error error = makeError(propertyNode.from(),
                                    propertyNode.to(),
                                    snapshot,
                                    UNKNOWN_PROPERTY_ERROR_KEY + propertyName,
                                    msg,
                                    msg,
                                    false /* not line error */,
                                    Severity.WARNING);
                            if(error != null) {
                                getResult().add(error);
                            }
                        }

                        //check value
                        if (valueNode != null && property != null) {
                            String valueImage = ctx.getPropertyValueImage();
                            
                            //do not check values which contains generated code
                            //we are no able to identify the templating semantic
                            if (!Css3Utils.containsGeneratedCode(valueImage) 
                                    //TODO add support for checking value of vendor specific properties, not it is disabled.
                                    && !Css3Utils.isVendorSpecificPropertyValue(file, valueImage)) {
                                ResolvedProperty pv = new ResolvedProperty(file, property, valueImage);
                                if (!pv.isResolved()) {
                                    if(!ctx.containsIEBS9Hack() && !ctx.containsIEStarHack()) {
                                        String errorMsg = null;

                                        //error in property 
                                        List<Token> unresolved = pv.getUnresolvedTokens();
                                        if(unresolved.isEmpty()) {
                                            return false;
                                        }
                                        Token unexpectedToken = unresolved.iterator().next();

                                        if(isNonCss21CompatiblePropertyValue(unexpectedToken.toString())) {
                                            return false;
                                        }
                                        
                                        CharSequence unexpectedTokenImg = unexpectedToken.image();
                                        if(Css3Utils.isVendorSpecificPropertyValue(file, unexpectedTokenImg)) {
                                            //the unexpected token is a vendor property value, ignore
                                            return false;
                                        }

                                        if (errorMsg == null) {
                                            errorMsg = NbBundle.getMessage(CssAnalyser.class, INVALID_PROPERTY_VALUE, unexpectedToken.image().toString());
                                        }

                                        Error error = makeError(valueNode.from(),
                                                valueNode.to(),
                                                snapshot,
                                                INVALID_PROPERTY_VALUE,
                                                errorMsg,
                                                errorMsg,
                                                false /* not line error */,
                                                Severity.WARNING);
                                        if(error != null) {
                                            getResult().add(error);
                                        }
                                    }
                                }
                            }
                        }

                    }

                } 
                
                return false;
            }
            
        };
        
        visitor.visitChildren(node);
        
        return errors;
    }

    private static Error makeError(int astFrom, int astTo, Snapshot snapshot, String key, String displayName, String description, boolean lineError, Severity severity) {
        assert astFrom <= astTo;

        return CssErrorFactory.createError(key,
                            displayName,
                            description,
                            snapshot.getSource().getFileObject(),
                            astFrom,
                            astTo,
                            lineError,
                            severity);

    }

    public static boolean isConfigurableError(String errorKey) {
        //unknown property errors can be suppressed, so far
        return isUnknownPropertyError(errorKey);
    }
    
    public static boolean isUnknownPropertyError(String errorKey) {
        return errorKey.startsWith(UNKNOWN_PROPERTY_ERROR_KEY);
    }

    public static String getUnknownPropertyName(String unknownPropertyErrorKey) {
        assert unknownPropertyErrorKey.startsWith(UNKNOWN_PROPERTY_ERROR_KEY);
        int index = unknownPropertyErrorKey.indexOf(UNKNOWN_PROPERTY_ERROR_KEY_DELIMITER);//NOI18N
        return unknownPropertyErrorKey.substring(index + 1);
    }

    

    //this is only a temporary hack for being able to filter out the css 2.1 errors for
    //commonly used properties not defined in the specification
    private static boolean isNonCss21CompatibleDeclarationPropertyName(String propertyName) {
        return NON_CSS21_DECLARATION_PROPERTY_NAMES.contains(propertyName);
    }

    private static boolean isNonCss21CompatiblePropertyValue(String propertyValue) {
        return NON_CSS21_DECLARATION_PROPERTY_VALUES.contains(propertyValue);
    }

    private static final Collection<String> NON_CSS21_DECLARATION_PROPERTY_NAMES = Arrays.asList(
            new String[]{"opacity", "resize", "text-overflow", "text-shadow", "filter"}); //NOI18N

    private static final Collection<String> NON_CSS21_DECLARATION_PROPERTY_VALUES = Arrays.asList(
            new String[]{"expression"}); //NOI18N

}
