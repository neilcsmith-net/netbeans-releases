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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.css.editor.csl;

import javax.swing.text.BadLocationException;
import org.netbeans.modules.css.editor.module.main.CssModuleTestBase;
import org.netbeans.modules.parsing.spi.ParseException;

/**
 *
 * @author mfukala@netbeans.org
 */
public class CssCompletionTest extends CssModuleTestBase {

    private static String[] AT_RULES = new String[]{"@charset", "@import", "@media", "@page", "@font-face"};

    public CssCompletionTest(String test) {
        super(test);
    }

    public void testAtRules() throws ParseException {
        checkCC("|", AT_RULES, Match.CONTAINS);
        checkCC("@|", AT_RULES, Match.CONTAINS);
        checkCC("@pa|", new String[]{"@page"}, Match.EXACT);

        checkCC("|  h1 { }", AT_RULES, Match.CONTAINS);
        checkCC("@| h1 { }", AT_RULES, Match.CONTAINS);
        checkCC("@pa| h1 { }", new String[]{"@page"}, Match.CONTAINS);
    }

    public void testAtRules2() throws ParseException {
        checkCC("@charset| ", new String[]{"@font-face"}, Match.DOES_NOT_CONTAIN);
        checkCC("@charset| div { }", new String[]{"@font-face"}, Match.DOES_NOT_CONTAIN);

        checkCC("@fon| div { }", new String[]{"@font-face"}, Match.EXACT);

        checkCC("@media| div { }", new String[]{"@media"}, Match.EXACT);

        checkCC("@page| div { }", new String[]{"@page"}, Match.EXACT);
    }

    public void testPropertyNames() throws ParseException {
        //empty rule
        checkCC("h1 { | }", arr("azimuth"), Match.CONTAINS);
        checkCC("h1 { az| }", arr("azimuth"), Match.CONTAINS);
        checkCC("h1 { azimuth| }", arr("azimuth"), Match.CONTAINS);

        //beginning of the rule
        checkCC("h1 { | \n color: red; }", arr("azimuth"), Match.CONTAINS);
        checkCC("h1 { az| \n color: red; }", arr("azimuth"), Match.CONTAINS);
        checkCC("h1 { azimuth| \n color: red; }", arr("azimuth"), Match.CONTAINS);

        //middle in the rule
        checkCC("h1 { color: red;\n | \n padding: 2px;}", arr("azimuth"), Match.CONTAINS);
        checkCC("h1 { color: red;\n az| \n padding: 2px;}", arr("azimuth"), Match.CONTAINS);
        checkCC("h1 { color: red;\n azimuth| \n padding: 2px;}", arr("azimuth"), Match.CONTAINS);

        //end of the rule
        checkCC("h1 { color: red;\n | }", arr("azimuth"), Match.CONTAINS);
        checkCC("h1 { color: red;\n az| }", arr("azimuth"), Match.CONTAINS);
        checkCC("h1 { color: red;\n azimuth| }", arr("azimuth"), Match.CONTAINS);
    }

    //there are only some basic checks since the values completion itself
    //is tested by org.netbeans.modules.css.editor.PropertyModelTest
    public void testPropertyValues() throws ParseException {
        checkCC("h1 { color: | }", arr("red"), Match.CONTAINS);
        checkCC("h1 { color: r| }", arr("red"), Match.CONTAINS);

//        checkCC("h1 { color: red| }", arr("red"), Match.CONTAINS);
        checkCC("h1 { color: r|ed }", arr("red"), Match.CONTAINS);

        checkCC("h1 { color: red | }", arr(), Match.EMPTY);
        checkCC("h1 { border: dotted | }", arr("blue"), Match.CONTAINS);
    }
    
    public void testHashColorCompletion() throws ParseException {
        String color = "#aabbcc";
        CssCompletion.TEST_USED_COLORS = new String[]{color};
        
        checkCC("h1 { color: | }", arr(color), Match.CONTAINS);
        checkCC("h1 { color: #| }", arr(color), Match.CONTAINS);
        checkCC("h1 { color: #| }", arr("red"), Match.DOES_NOT_CONTAIN);
        checkCC("h1 { color: #aabb| }", arr(color, "$color_chooser"), Match.EXACT);
        checkCC("h1 { color: #aa|bbcc }", arr(color, "$color_chooser"), Match.EXACT);
        checkCC("h1 { color: #aabbcc| }", arr(color, "$color_chooser"), Match.EXACT);
    }

    public void testCorners() throws ParseException {
        checkCC("h1 { bla| }", arr(), Match.EMPTY);
//        checkCC("h1 { color: ble| }", arr(), Match.EMPTY); //fails - issue #161129
    }

//    public void testIssue160870() throws ParseException {
//        checkCC("h1 { display : | }", arr("block"), Match.CONTAINS);
//    }
    public void testHtmlSelectorsCompletion() throws ParseException {
        checkCC("|", arr("html"), Match.CONTAINS);
        checkCC("ht| ", arr("html"), Match.EXACT);
        checkCC("html | ", arr("body"), Match.CONTAINS);
        checkCC("html bo| ", arr("body"), Match.EXACT);
        checkCC("html, bo| ", arr("body"), Match.EXACT);
        checkCC("html > bo| ", arr("body"), Match.EXACT);
        checkCC("html tit| { }", arr("title"), Match.CONTAINS);
    }
    
    public void testHtmlSelectorsAfterNamespacesSection() throws ParseException {
        checkCC("@namespace foo \"http://foo.org\";\n |", arr("html"), Match.CONTAINS);
        checkCC("@namespace foo \"http://foo.org\";\n ht| ", arr("html"), Match.EXACT);
    }

    public void testHtmlSelectorsCompletionAfterIdSelector() throws ParseException, BadLocationException {
        checkCC("#myid |", arr("html"), Match.CONTAINS);
        checkCC("#myid | { }", arr("html"), Match.CONTAINS);
        checkCC("#myid | body { }", arr("html"), Match.CONTAINS);

        checkCC("#myid h|", arr("html"), Match.CONTAINS);
        assertComplete("#myid b| { }", "#myid body| { }", "body");
        assertComplete("#myid | { }", "#myid body| { }", "body");
    }

    public void testHtmlSelectorsCompletionAfterClassSelector() throws ParseException, BadLocationException {
        checkCC(".aclass |", arr("html"), Match.CONTAINS);
        checkCC(".aclass h|", arr("html"), Match.CONTAINS);
        assertComplete(".aclass b| { }", ".aclass body| { }", "body");
        assertComplete(".aclass | { }", ".aclass body| { }", "body");
    }

    public void testCompleteSelectorAfterSelector() throws ParseException {
        checkCC("html | { }", arr("body"), Match.CONTAINS);
    }

    public void testCompleteSelectors() throws ParseException, BadLocationException {
        assertComplete("html b| { }", "html body| { }", "body");
        assertComplete("html bo| { }", "html body| { }", "body");
        assertComplete("html body| { }", "html body| { }", "body");
        assertComplete("html | { }", "html body| { }", "body");
        assertComplete("| { }", "body| { }", "body");
        assertComplete("b| { }", "body| { }", "body");
    }

    public void testHtmlSelectorsInContent() throws ParseException {
        checkCC("h1 { color:red; } | h2 { color:red; }", arr("html"), Match.CONTAINS);
    }

    public void testSystemColors() throws ParseException {
        checkCC("div { color: | }", arr("menu", "window"), Match.CONTAINS);
    }

    public void testHtmlSelectorsInMedia() throws ParseException {
//        checkCC("@media page {  |   } ", arr("html"), Match.CONTAINS);
//        checkCC("@media page {  |   } ", arr("@media"), Match.DOES_NOT_CONTAIN); //media not supported here
        checkCC("@media page {  h1 { } |   } ", arr("html"), Match.CONTAINS); //media not supported here

//        checkCC("@media page {  htm|   } ", arr("html"), Match.EXACT);
//        checkCC("@media page {  html, |   } ", arr("body"), Match.CONTAINS);
//        checkCC("@media page {  html, bo|   } ", arr("body"), Match.CONTAINS);
//        checkCC("@media page {  html > bo|   } ", arr("body"), Match.CONTAINS);
    }

    public void testVendorSpecificPropertyCompletion() throws ParseException {
        //just the completed line in ruleset
        checkCC("h1 { | }", arr("-moz-animation"), Match.CONTAINS);
        checkCC("h1 { -| }", arr("-moz-animation"), Match.CONTAINS);
        checkCC("h1 { -moz-an| }", arr("-moz-animation"), Match.CONTAINS);
        checkCC("h1 { -moz-an| }", arr("-moz-appearabce"), Match.DOES_NOT_CONTAIN);

        checkCC("h1 { %| }", arr("-moz-animation"), Match.EMPTY);
        checkCC("h1 { %moz| }", arr("-moz-animation"), Match.EMPTY);

        //after a declaration
        checkCC("h1 { color:red;  | }", arr("-moz-animation"), Match.CONTAINS);
        checkCC("h1 { color:red; -| }", arr("-moz-animation"), Match.CONTAINS);
        checkCC("h1 { color:red; -moz-an| }", arr("-moz-animation"), Match.CONTAINS);
        checkCC("h1 { color:red; -moz-an| }", arr("-moz-appearabce"), Match.DOES_NOT_CONTAIN);

        //do not offer after garbage
        checkCC("h1 { color:red; %| }", arr("-moz-animation"), Match.DOES_NOT_CONTAIN);
        checkCC("h1 { color:red; %moz| }", arr("-moz-animation"), Match.DOES_NOT_CONTAIN);

    }

    public void testCompletionInMozillaSpecificAtRule() throws ParseException {
        checkCC(" @-moz-document url(http://www.w3.org/) { | }", arr("div"), Match.CONTAINS);
        checkCC(" @-moz-document url(http://www.w3.org/) { p { } | }", arr("div"), Match.CONTAINS);
        checkCC(" @-moz-document url(http://www.w3.org/) { p { } | div { } }", arr("div"), Match.CONTAINS);
    }

    //Bug 204128 - CC stops work after # in a color attribute 
    public void testIssue204128() throws ParseException {
        CssCompletion.TEST_USED_COLORS = new String[]{"#aabbcc"};

        String code = "#test {\n"
                + "color: #|\n"
                + "\n"
                + "   }\n";

        checkCC(code, arr("#aabbcc"), Match.CONTAINS);
    }

    //Bug 204129 - CC doesn't work after *|
    public void testIssue204129() throws ParseException {
        //complete name selectors and universal selector in empty file        
        checkCC("|", arr("h1"), Match.CONTAINS);
        checkCC("|", arr("*"), Match.CONTAINS);

        //complete after named selector
        checkCC("a |", arr("h1"), Match.CONTAINS);
        checkCC("a |", arr("*"), Match.CONTAINS);

        //complete after universal selector
        checkCC("* |", arr("h1"), Match.CONTAINS);
        checkCC("* |", arr("*"), Match.CONTAINS);
        checkCC("*|", arr("*"), Match.CONTAINS);


    }

    public void testPropertyValueWithPrefix() throws ParseException {
        checkCC("div { font: italic la| }", arr("large"), Match.CONTAINS);
        checkCC("div { font: italic la|rge }", arr("large"), Match.CONTAINS);
    }

    //Bug 205893 - font-family completion issue
    public void testPropertyValueFontFamily() throws ParseException {
        checkCC("div { font-family: fa| }", arr("fantasy"), Match.EXACT);
        checkCC("div { font-family: fantasy,|}", arr("monospace"), Match.CONTAINS);
        checkCC("div { font-family: fantasy, |}", arr("monospace"), Match.CONTAINS);
        checkCC("div { font-family: fantasy, mo|}", arr("monospace"), Match.EXACT);

        checkCC("div { font-family: fa| \n}", arr("fantasy"), Match.EXACT);
        checkCC("div { font-family: fantasy,| \n}", arr("monospace"), Match.CONTAINS);
        checkCC("div { font-family: fantasy, | \n}", arr("monospace"), Match.CONTAINS);
        checkCC("div { font-family: fantasy, mo| \n}", arr("monospace"), Match.EXACT);
    }

    public void testPropertyValueFontFamilyProblem2() throws ParseException {
        //completion doesn't offer items that can immediatelly follow
        //a valid token
        checkCC("div { font-family: fantasy|}", arr(",", "!identifier"), Match.EXACT);
//        checkCC("div { font-family: fantasy |}", arr(",", "!identifier"), Match.EXACT);
    }

    public void testPropertyValueJustAfterRGB() throws ParseException {
        checkCC("div { color: rgb|}", arr("("), Match.EXACT);
    }

    public void testPropertyValueOfferItemsJustAfterUnit() throws ParseException {
        checkCC("div { animation: cubic-bezier(20| }", arr(","), Match.EXACT);
    }

    //Bug 204821 - Incorrect completion for vendor specific properties
    public void testVendorSpecificProperties() throws ParseException, BadLocationException {
        checkCC("div { -| }", arr("-moz-animation"), Match.CONTAINS);
        checkCC("div { -| }", arr("adding"), Match.DOES_NOT_CONTAIN);

        assertComplete("div { -| }", "div { -moz-animation: | }", "-moz-animation");


    }
    //Bug 212664 - No CC for inline CSS style (without prefix)

    public void testCompletionBeforeSemicolon() throws ParseException, BadLocationException {
        checkCC("div { background: | ; }", arr("red"), Match.CONTAINS);
        checkCC("div { background: |; }", arr("red"), Match.CONTAINS);
    }

    //Bug 217457 - Broken code completion inside identifier for values
    public void test217457() throws ParseException, BadLocationException {
        checkCC("div { transform: sca|leZ ; }", arr("scaleZ"), Match.CONTAINS);
        checkCC("div { transform: sca|leZ(0.3); }", arr("scaleZ"), Match.CONTAINS);
    }

    //http://netbeans.org/bugzilla/show_bug.cgi?id=221349
    public void testNoCompletionAfterImport() throws ParseException, BadLocationException {
        checkCC("@import \"s1.css\"; |  root { display: block;}", arr("body"), Match.CONTAINS_ONCE);

    }

    //http://netbeans.org/bugzilla/show_bug.cgi?id=221461
    //doubled items between rule w/o prefix
    public void testDoubledItemsBetweenRulesWithoutPrefix() throws ParseException, BadLocationException {
        //this works already (w/ prefix)
        checkCC("root { } bo| .x {  }", arr("body"), Match.CONTAINS_ONCE);
        //this doesn't
        checkCC("root { } | .x {  }", arr("body"), Match.CONTAINS_ONCE);

    }

    public void testInheritInColor() throws ParseException {
        checkCC("div { color:|  }", arr("inherit"), Match.CONTAINS_ONCE);
    }

    public void testURICompletion() throws ParseException {
        checkCC("div { background-image: | } ", arr("url"), Match.CONTAINS);
    }

    public void testClassCompletion() throws ParseException {
        CssCompletion.TEST_CLASSES = new String[]{"clz"};
        try {
            checkCC(".|", arr("clz"), Match.EXACT);
            checkCC(".c|", arr("clz"), Match.EXACT);

            checkCC(".c| ", arr("clz"), Match.EXACT);
            checkCC(".| ", arr("clz"), Match.EXACT);

            checkCC(".c| {}", arr("clz"), Match.EXACT);
            checkCC(".| {}", arr("clz"), Match.EXACT);

            checkCC(".my{} .c| ", arr("clz"), Match.EXACT);
            checkCC(".my{} .| ", arr("clz"), Match.EXACT);

            checkCC(".pre{} .c| .post{}", arr("clz"), Match.EXACT);
            checkCC(".pre{} .| .post{}", arr("clz"), Match.EXACT);

            checkCC(".c| .post{}", arr("clz"), Match.EXACT);
            checkCC(".| .post{}", arr("clz"), Match.EXACT);

            checkCC("a{} .c| .post{}", arr("clz"), Match.EXACT);
            checkCC("a{} .| .post{}", arr("clz"), Match.EXACT);

            checkCC("a{} .c| b{}", arr("clz"), Match.EXACT);
            checkCC("a{} .| b{}", arr("clz"), Match.EXACT);


        } finally {
            CssCompletion.TEST_CLASSES = null;
        }
    }

    public void testDoNotOfferSelectorsInRule() throws ParseException {
        checkCC("div { color: red; | }", arr("html"), Match.DOES_NOT_CONTAIN);
        checkCC("div { | color: red; }", arr("html"), Match.DOES_NOT_CONTAIN);
        checkCC("div { | }", arr("html"), Match.DOES_NOT_CONTAIN);
    }
    
    public void testDoNotOfferPropertyValuesAfterClosedPropertyDeclaration() throws ParseException {
        checkCC("div { color: red;| }", arr("blue"), Match.DOES_NOT_CONTAIN);
        checkCC("div { color: red; | }", arr("blue"), Match.DOES_NOT_CONTAIN);
    }
    
    public void testDoNotOfferPropertiesAfterUnclosedPropertyValue() throws ParseException {
        checkCC("div { font: bold | }", arr("azimuth"), Match.DOES_NOT_CONTAIN);
//        checkCC("div { font: bold | }", arr("100"), Match.CONTAINS);
    }
    
    public void testWrongInsertPositionInPropertyName() throws ParseException, BadLocationException {
        assertComplete("div { co| }",  "div { color: | }", "color");
    }
    
     public void testFontVariant() throws ParseException {
        checkCC("div { font-variant: | }", arr("normal"), Match.CONTAINS);
        checkCC("div { font-variant: | }", arr("small-caps"), Match.CONTAINS);
        checkCC("div { font-variant: sma| }", arr("small-caps"), Match.CONTAINS);
        
    }
    
}
