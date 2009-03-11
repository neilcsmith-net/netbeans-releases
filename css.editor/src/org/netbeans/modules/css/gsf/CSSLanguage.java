/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.css.gsf;

import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.api.CodeCompletionHandler;
import org.netbeans.modules.csl.api.Formatter;
import org.netbeans.modules.csl.api.KeystrokeHandler;
import org.netbeans.modules.csl.api.SemanticAnalyzer;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.CommentHandler;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.css.gsf.CSSSemanticAnalyzer;
import org.netbeans.modules.css.gsf.CSSStructureScanner;
import org.netbeans.modules.css.gsf.CssCommentHandler;
import org.netbeans.modules.css.lexer.api.CSSTokenId;
import org.netbeans.modules.parsing.spi.Parser;

/**
 * Configuration for CSS
 */
public class CSSLanguage extends DefaultLanguageConfig {
    
    public CSSLanguage() {
    }

    @Override
    public boolean isIdentifierChar(char c) {
         /** Includes things you'd want selected as a unit when double clicking in the editor */
        return Character.isJavaIdentifierPart(c) 
                || (c == '-') || (c == '@') 
                || (c == '&') || (c == '_')
                || (c == '#');
    }

    @Override
    public CommentHandler getCommentHandler() {
        return new CssCommentHandler();
    }

    @Override
    public Language getLexerLanguage() {
        return CSSTokenId.language();
    }

    @Override
    public String getDisplayName() {
        return "CSS"; //NOI18N ???
    }
    
    @Override
    public String getPreferredExtension() {
        return "css"; // NOI18N
    }

    // Service Registrations
    
    @Override
    public SemanticAnalyzer getSemanticAnalyzer() {
        return new CSSSemanticAnalyzer();
    }

    @Override
    public Parser getParser() {
        return new CSSGSFParser();
    }

    @Override
    public StructureScanner getStructureScanner() {
        return new CSSStructureScanner();
    }

    @Override
    public CodeCompletionHandler getCompletionHandler() {
        return new CSSCompletion();
    }

    @Override
    public KeystrokeHandler getKeystrokeHandler() {
        return new CssBracketCompleter();
    }
}
