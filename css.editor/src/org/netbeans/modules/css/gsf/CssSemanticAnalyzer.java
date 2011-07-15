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
package org.netbeans.modules.css.gsf;

import java.util.Set;
import java.util.Map;
import org.netbeans.modules.csl.api.ColoringAttributes;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.SemanticAnalyzer;
import org.netbeans.modules.css.editor.module.CssModuleSupport;
import org.netbeans.modules.css.editor.module.spi.FeatureCancel;
import org.netbeans.modules.css.editor.module.spi.FeatureContext;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;

/**
 *
 * @author mfukala@netbeans.org
 */
public class CssSemanticAnalyzer extends SemanticAnalyzer {

    private FeatureCancel featureCancel = new FeatureCancel();
    private Map<OffsetRange, Set<ColoringAttributes>> semanticHighlights;

    @Override
    public Map<OffsetRange, Set<ColoringAttributes>> getHighlights() {
        return semanticHighlights;
    }

    @Override
    public void cancel() {
        featureCancel.cancel();
    }

    @Override
    public void run(Result result, SchedulerEvent event) {
        resume();
        
        CssParserResultCslWrapper wrappedResult = (CssParserResultCslWrapper) result;
        FeatureContext featureContext = new FeatureContext(wrappedResult.getWrappedCssParserResult());
        semanticHighlights = CssModuleSupport.getSemanticHighlights(featureContext, featureCancel);
        
//        NodeVisitor visitor = new NodeVisitor() {
//
//            //XXX using the ColoringAttributes.YYY java specific codes should
//            //be changed to something more meaningful
//            
//            public boolean visit(Node node) {
//                if (node.type() == NodeType.elementName || node.type() == NodeType.elementSubsequent) {
//                    //selector name
//                    int dso = snapshot.getOriginalOffset(node.from());
//                    if(dso == -1) {
//                        //try next offset - for virtually created class and id
//                        //selectors the . an # prefix are virtual code and is not
//                        //a part of the source document, try to highlight just
//                        //the class or id name
//                        dso = snapshot.getOriginalOffset(node.to() + 1);
//                    }
//
//                    int deo =snapshot.getOriginalOffset(node.to());
//                    //filter out generated and inlined style definitions - they have just virtual selector which
//                    //is mapped to empty string
//                    if(dso >= 0 && deo >= 0) {
//                        OffsetRange range = new OffsetRange(dso, deo);
//                        highlights.put(range, ColoringAttributes.METHOD_SET);
//                    }
//                } else if (node.type() == NodeType.property) {
//                    int dso = snapshot.getOriginalOffset(node.from());
//                    int deo =snapshot.getOriginalOffset(node.to());
//
//                    if (dso >= 0 && deo >= 0) { //filter virtual nodes
//                        //check vendor speficic property
//                        OffsetRange range = new OffsetRange(dso, deo);
//
//                        String propertyName = node.name().trim();
//                        if(CssGSFParser.containsGeneratedCode(propertyName)) {
//                            return false;
//                        }
//                        
//                        if (CssAnalyser.isVendorSpecificProperty(propertyName)) {
//                            //special highlight for vend. spec. properties
//                            highlights.put(range, ColoringAttributes.CUSTOM2_SET);
//                        } else {
//                            highlights.put(range, ColoringAttributes.CUSTOM1_SET);
//                        }
//                    }
//                }
//                return false;
//            }
//            
//        };
//
//        NodeVisitor.visitChildren(root, visitor);


    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE; //todo find out some reasonable value
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return null; //todo what class to return here?
    }

    private void resume() {
        featureCancel = new FeatureCancel();
    }
}
