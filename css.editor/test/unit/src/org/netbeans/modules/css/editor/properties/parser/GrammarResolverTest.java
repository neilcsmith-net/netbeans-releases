/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.css.editor.properties.parser;

import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.css.editor.module.CssModuleSupport;
import org.netbeans.modules.css.editor.module.main.CssModuleTestBase;

/**
 *
 * @author marekfukala
 */
public class GrammarResolverTest extends CssModuleTestBase {

    public GrammarResolverTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
//        PRINT_INFO_IN_ASSERT_RESOLVE = true;
//        GrammarResolver.setLogging(GrammarResolver.Log.DEFAULT, true);
    }
    
    public void testParseSimpleGrammar() {
        String grammar = " url ( [ !string | !identifier ] )";

        assertResolve(grammar, "url(ahoj)");
        assertResolve(grammar, "url(\"ahoj\")");
    }

    public void testParseSimpleAmbiguousGrammar() {
        String grammar = " [ url ( !string ) ] | [ url ( !identifier ) ]";

        assertResolve(grammar, "url(ahoj)");
        assertResolve(grammar, "url(\"ahoj\")");
    }
    
    public void testParseSimpleAmbiguousGrammar2() {
        String grammar = "[ a ] | [ a b ]";
        assertResolve(grammar, "a b");
    }
    
    public void testParseSimpleAmbiguousGrammar3() {
        String grammar = "[ a b c ] | [ a b ] | [ a b d ]";
        assertResolve(grammar, "a b");
        assertResolve(grammar, "a b c");
        assertResolve(grammar, "a b d");
    }

    public void testOptinalMemberInSet() {
        String g = " a? | [ a b ]";
        
        assertResolve(g, "");
        assertResolve(g, "a");
        assertResolve(g, "a b");
    }
    
    public void testAmbiguousGrammarParsingPrecendence() {
        String g = " [ !identifier b ] | [ keyword b ]";
        
        
        GroupGrammarElement tree = GrammarParser.parse(g);
        
        PropertyValue v = new PropertyValue(tree, "keyword b");
        
        assertTrue(v.isResolved());
        
        List<ResolvedToken> resolved = v.getResolvedTokens();
        
        assertNotNull(resolved);
        assertEquals(2, resolved.size());
        
        ResolvedToken first = resolved.get(0); 
        
        //now check the group, matching keywords should have a precedence before
        //property acceptors
        
        GrammarElement ge1 = first.getGrammarElement();
        assertNotNull(ge1);
        
        assertEquals("[S0]/[L2]/keyword", ge1.path());

        ResolvedToken second = resolved.get(1); 
        
        GrammarElement ge2 = second.getGrammarElement();
        assertNotNull(ge2);
        
        assertEquals("[S0]/[L2]/b", ge2.path());

    }
    
    public void testAmbiguousGrammarParsingPrecendence2() {
        //just test if we can handle such situation - two same paths
        //in such case the first found path shoud be used
        
        String g = " [ !identifier b ] | [ !identifier b ]";
                
        GroupGrammarElement tree = GrammarParser.parse(g);
        
        PropertyValue v = new PropertyValue(tree, "keyword b");
        
        assertTrue(v.isResolved());
        
        List<ResolvedToken> resolved = v.getResolvedTokens();
        
        assertNotNull(resolved);
        assertEquals(2, resolved.size());
        
        ResolvedToken first = resolved.get(0); 
        
        GrammarElement ge1 = first.getGrammarElement();
        assertNotNull(ge1);
        
        assertEquals("[S0]/[L1]/!identifier", ge1.path());

        ResolvedToken second = resolved.get(1); 
        
        GrammarElement ge2 = second.getGrammarElement();
        assertNotNull(ge2);
        
        assertEquals("[S0]/[L1]/b", ge2.path());

    }
    
    public void testAmbiguousGrammarParsingPrecendence3() {
        //just test if we can handle such situation - two same paths
        //in such case the first found path shoud be used
        
        String g = " [ keyword b ] | [ keyword b ]";
        
        GroupGrammarElement tree = GrammarParser.parse(g);
        
        PropertyValue v = new PropertyValue(tree, "keyword b");
        
        assertTrue(v.isResolved());
        
        List<ResolvedToken> resolved = v.getResolvedTokens();
        
        assertNotNull(resolved);
        assertEquals(2, resolved.size());
        
        ResolvedToken first = resolved.get(0); 
        
        GrammarElement ge1 = first.getGrammarElement();
        assertNotNull(ge1);
        
        assertEquals("[S0]/[L1]/keyword", ge1.path());

        ResolvedToken second = resolved.get(1); 
        
        GrammarElement ge2 = second.getGrammarElement();
        assertNotNull(ge2);
        
        assertEquals("[S0]/[L1]/b", ge2.path());

    }
    
    public void testAmbiguousGrammarInList() {
        String g = "[ a ] || [ a c ] || [ a d ]";
        assertResolve(g, "a c");
    }
    
     public void testAmbiguousGrammarListParsingPrecendence() {
        String g = " [ !identifier b ] || [ keyword b ]";
        
        GroupGrammarElement tree = GrammarParser.parse(g);
        
        PropertyValue v = new PropertyValue(tree, "keyword b");
        
        assertTrue(v.isResolved());
        
        List<ResolvedToken> resolved = v.getResolvedTokens();
        
        assertNotNull(resolved);
        assertEquals(2, resolved.size());
        
        ResolvedToken first = resolved.get(0); 
        
        //now check the group, matching keywords should have a precedence before
        //property acceptors
        
        GrammarElement ge1 = first.getGrammarElement();
        assertNotNull(ge1);
        
        assertEquals("[C0]/[L2]/keyword", ge1.path());

        ResolvedToken second = resolved.get(1); 
        
        GrammarElement ge2 = second.getGrammarElement();
        assertNotNull(ge2);
        
        assertEquals("[C0]/[L2]/b", ge2.path());

    }
    
    public void testAmbiguousGrammarListParsingPrecendence2() {
        //just test if we can handle such situation - two same paths
        //in such case the first found path shoud be used

        String g = " [ !identifier b ] || [ !identifier b ]";
        
        GroupGrammarElement tree = GrammarParser.parse(g);
        
        PropertyValue v = new PropertyValue(tree, "keyword b");
        
        assertTrue(v.isResolved());
        
        List<ResolvedToken> resolved = v.getResolvedTokens();
        
        assertNotNull(resolved);
        assertEquals(2, resolved.size());
        
        ResolvedToken first = resolved.get(0); 
        
        GrammarElement ge1 = first.getGrammarElement();
        assertNotNull(ge1);
        
        assertEquals("[C0]/[L2]/!identifier", ge1.path());

        ResolvedToken second = resolved.get(1); 
        
        GrammarElement ge2 = second.getGrammarElement();
        assertNotNull(ge2);
        
        assertEquals("[C0]/[L2]/b", ge2.path());

    }
    
    public void testAmbiguousGrammarListParsingPrecendence3() {
        //just test if we can handle such situation - two same paths
        //in such case the first found path shoud be used
        
        String g = " [ keyword b ] || [ keyword b ]";
        
        GroupGrammarElement tree = GrammarParser.parse(g);
        
        PropertyValue v = new PropertyValue(tree, "keyword b");
        
        assertTrue(v.isResolved());
        
        List<ResolvedToken> resolved = v.getResolvedTokens();
        
        assertNotNull(resolved);
        assertEquals(2, resolved.size());
        
        ResolvedToken first = resolved.get(0); 
        
        GrammarElement ge1 = first.getGrammarElement();
        assertNotNull(ge1);
        
        assertEquals("[C0]/[L2]/keyword", ge1.path());

        ResolvedToken second = resolved.get(1); 
        
        GrammarElement ge2 = second.getGrammarElement();
        assertNotNull(ge2);
        
        assertEquals("[C0]/[L2]/b", ge2.path());

    }
    
    public void testAmbiguousGrammarAnimation() {
        PropertyModel p = CssModuleSupport.getPropertyModel("@animation-arg");
        assertResolve(p.getGrammar(), "cubic-bezier");
        
    }
    
    public void testParseMultiplicity() {
        String grammar = " [ x ]{1,2} ";

        assertResolve(grammar, "x");
        assertResolve(grammar, "x x");
    }

    public void testParseMultiplicity1() {
        String grammar = " [ x ]? ";

        assertResolve(grammar, "");
        assertResolve(grammar, "x");
    }

    public void testParseMultiplicity2() {
        String grammar = " [ x ]+ ";  // 1 - inf

        assertResolve(grammar, "x");
        assertResolve(grammar, "x x x");
    }

    public void testParseMultiplicity3() {
        String grammar = " [ x ]* ";  // 0 - inf

        assertResolve(grammar, "");
        assertResolve(grammar, "x");
        assertResolve(grammar, "x x x");
    }

    public void testParseMultiplicity4() {
        String grammar = " [ x ]* y ";

        assertResolve(grammar, "x y");
        assertResolve(grammar, "x x x x y");
        assertResolve(grammar, "y");
    }

    public void testParseMultiplicity5() {
        String grammar = " [ x ]*  [ y ]* ";

        assertResolve(grammar, "x y");
        assertResolve(grammar, "x x x x");
        assertResolve(grammar, "y y y y");
        assertResolve(grammar, "x x y y");
    }

    public void testSetWithArbitraryMember() {
        String grammar = " [ a | b? ] c?";
        assertResolve(grammar, "a");
        assertResolve(grammar, "b");
        assertResolve(grammar, "a c");
        assertResolve(grammar, "b c");
        
        //bit weird, but the [ a | b? ] is matched by empty input 
        //resolving the arbitrary b element
        assertResolve(grammar, "c"); 
    }

    public void testListWithArbitraryMember() {
        String grammar = " [ a  b?  c? d ]";
        assertResolve(grammar, "a b c d");
        assertResolve(grammar, "a c d");
        assertResolve(grammar, "a b d");
        assertResolve(grammar, "a d");
    }

    public void testParseMultiplicity_error1() {
        //causing OOM - some infinite loop
        String grammar = "[ [ x ]* ]+";
        assertResolve(grammar, "");
    }

    public void testCollection() {
        String grammar = " [ a || b || c ]";
        assertResolve(grammar, "a b c");
        assertResolve(grammar, "c b a");
        assertResolve(grammar, "b c a");
        assertResolve(grammar, "a c b");
        assertResolve(grammar, "c a");
        assertResolve(grammar, "a c");
        assertResolve(grammar, "b a");
        assertResolve(grammar, "a b");
        assertResolve(grammar, "b c");
        assertResolve(grammar, "c b");
        assertResolve(grammar, "a");
        assertResolve(grammar, "b");
        assertResolve(grammar, "c");
    }

    public void testCollection2() {
        String grammar = " [ a || b ] c";
        assertResolve(grammar, "a b c");
        assertResolve(grammar, "a c");
        assertResolve(grammar, "b c");
        assertResolve(grammar, "b a c");
    }

    public void testCollectionWithArbitraryMembers() {
        String grammar = " [ a || b? || c ]";
        assertResolve(grammar, "a b c");
        assertResolve(grammar, "c b a");
        assertResolve(grammar, "c a");
        assertResolve(grammar, "a");
        assertResolve(grammar, "b");
        assertResolve(grammar, "c");
        assertResolve(grammar, "b c");
    }

    public void testCase1() {
        String g = "[ a || [ b | c] ]";
        assertResolve(g, "a");
        assertResolve(g, "b");
        assertResolve(g, "c");
        assertResolve(g, "a b");
        assertResolve(g, "a c");
        assertResolve(g, "c a");
        assertResolve(g, "b a");
    }

    public void testCase2() {
        String g = "[ a | b ]{1,4}";

        assertResolve(g, "a");
        assertResolve(g, "a b");
        assertResolve(g, "a b a");
        assertResolve(g, "a b a a");

        assertNotResolve(g, "");
        assertNotResolve(g, "a a a b b");

    }

    public void testCase3() {
        String g = "[ [ a | b ] ]{1,4}";

        assertResolve(g, "a");
        assertResolve(g, "a b");
        assertResolve(g, "a b a");
        assertResolve(g, "a b a a");

        assertNotResolve(g, "");
        assertNotResolve(g, "a a a b b");

    }

    public void testSimpleSet() {
        GroupGrammarElement e = GrammarParser.parse("one | two | three");
        assertNotNull(e.elements());
        assertEquals(3, e.elements().size());
    }

    public void testSimpleList() {
        GroupGrammarElement e = GrammarParser.parse("one || two || three");
        assertNotNull(e.elements());
        assertEquals(3, e.elements().size());
    }

    public void testMultiplicity() {
        GroupGrammarElement e = GrammarParser.parse("one+ two? three{1,4}");
        assertNotNull(e.elements());
        assertEquals(3, e.elements().size());

        GrammarElement e1 = e.elements().get(0);
        assertEquals(1, e1.getMinimumOccurances());
        assertEquals(Integer.MAX_VALUE, e1.getMaximumOccurances());

        GrammarElement e2 = e.elements().get(1);
        assertEquals(0, e2.getMinimumOccurances());
        assertEquals(1, e2.getMaximumOccurances());

        GrammarElement e3 = e.elements().get(2);
        assertEquals(1, e3.getMinimumOccurances());
        assertEquals(4, e3.getMaximumOccurances());
    }

    public void testGroupsNesting() {
        GroupGrammarElement e = GrammarParser.parse("one [two] [[three] || [four]]");
        assertNotNull(e.elements());
        assertEquals(3, e.elements().size());
    }

    public void testConsume() {
        String rule = "[ [color]{1,4} | transparent ] | inherit ";

        assertTrue(new PropertyValue(rule, "color").isResolved());
        assertTrue(new PropertyValue(rule, "inherit").isResolved());
        assertTrue(new PropertyValue(rule, "color color").isResolved());
        assertTrue(new PropertyValue(rule, "color color color color").isResolved());
    }

    public void testConsumeFails() {
        String rule = "[ [color]{1,4} | transparent ] | inherit ";

        assertNotResolve(rule, "color inherit");
        assertNotResolve(rule, "color color color color color");
        assertNotResolve(rule, "transparent inherit");
    }

    public void testSequence() {
        String rule = "[marek]{1,2} jitka";
        String text = "marek marek jitka";

        PropertyValue csspv = new PropertyValue(rule, text);

        assertTrue(csspv.isResolved());
    }

    public void testSequenceFails() {
        assertResolve("marek jitka", "jitka", false);
    }

    public void testFont() {
        PropertyModel p = CssModuleSupport.getPropertyModel("font");
        PropertyValue pv = new PropertyValue(p, "20% serif");
        assertTrue(pv.isResolved());
    }
    
    public void testFont2() {
        PropertyModel p = CssModuleSupport.getPropertyModel("font");
        PropertyValue pv = assertResolve(p.getGrammarElement(), "20px / 20px fantasy");
        
        List<ResolvedToken> resolved = pv.getResolvedTokens();
        assertNotNull(resolved);
        assertEquals(4, resolved.size());
        
        Iterator<ResolvedToken> itr = resolved.iterator();
        assertEquals("[S0|font]/[S1]/[L2]/[S7|font-size]/!length (20px)", itr.next().toString());
        assertEquals("[S0|font]/[S1]/[L2]/[L10]// (/)", itr.next().toString());
        assertEquals("[S0|font]/[S1]/[L2]/[L10]/[S11|line-height]/!length (20px)", itr.next().toString());
        assertEquals("[S0|font]/[S1]/[L2]/[S12|font-family]/[L13]/[S14]/[S17|@generic-family]/fantasy (fantasy)", itr.next().toString());
        
    }

    public void testZeroMultiplicity() {
        String rule = "[marek]?  [jitka]?  [ovecka]";
        String text = "ovecka";
        PropertyValue csspv = new PropertyValue(rule, text);
        assertTrue(csspv.isResolved());
    }

    public void testFontFamily() {
        PropertyModel p = CssModuleSupport.getPropertyModel("font-family");

        assertTrue(new PropertyValue(p, "serif").isResolved());
        assertTrue(new PropertyValue(p, "cursive, serif").isResolved());
        
        //resolves since the "cursive serif" can be considered as unquoted custom family name
        assertTrue(new PropertyValue(p, "cursive serif").isResolved());

    }

    public void testFontFamilyWithQuotedValue() {
        PropertyModel p = CssModuleSupport.getPropertyModel("font-family");
        PropertyValue csspv = new PropertyValue(p, "'Times New Roman',serif");
//        dumpResult(csspv);
        assertTrue(csspv.isResolved());
    }

    public void testFontSize() {
        PropertyModel p = CssModuleSupport.getPropertyModel("font-size");
        String text = "xx-small";

        PropertyValue csspv = new PropertyValue(p, text);

        assertTrue(csspv.isResolved());
    }

    public void testBorder() {
        PropertyModel p = CssModuleSupport.getPropertyModel("border");
        String text = "20px double";
        PropertyValue csspv = new PropertyValue(p, text);
        assertTrue(csspv.isResolved());
    }

    public void testMarginWidth() {
        PropertyModel p = CssModuleSupport.getPropertyModel("margin");
        String text = "20px 10em 30px 30em";
        PropertyValue csspv = new PropertyValue(p, text);
        assertTrue(csspv.isResolved());
    }

    public void testPaddingWidth() {
        PropertyModel p = CssModuleSupport.getPropertyModel("padding");
        String text = "20px 10em 30px 30em";
        PropertyValue csspv = new PropertyValue(p, text);
        assertTrue(csspv.isResolved());
    }

    public void testTimeUnit() {
        PropertyModel p = CssModuleSupport.getPropertyModel("pause-after");
        String text = "200ms";
        PropertyValue csspv = new PropertyValue(p, text);
        assertTrue(csspv.isResolved());

        text = "200";
        csspv = new PropertyValue(p, text);
        assertFalse(csspv.isResolved());

        text = "AAms";
        csspv = new PropertyValue(p, text);
        assertFalse(csspv.isResolved());

    }

    public void testFrequencyUnit() {
        PropertyModel p = CssModuleSupport.getPropertyModel("pitch");
        String text = "200kHz";
        PropertyValue csspv = new PropertyValue(p, text);
        assertTrue(csspv.isResolved());

        text = "200";
        csspv = new PropertyValue(p, text);
        assertFalse(csspv.isResolved());

        text = "AAHz";
        csspv = new PropertyValue(p, text);
        assertFalse(csspv.isResolved());

    }

    public void testIdentifierUnit() {
        PropertyModel p = CssModuleSupport.getPropertyModel("counter-increment");
        String text = "ovecka";
        PropertyValue csspv = new PropertyValue(p, text);
        assertTrue(csspv.isResolved());

        text = "10ovecek";
        csspv = new PropertyValue(p, text);
        assertFalse(csspv.isResolved());

        text = "-beranek";
        csspv = new PropertyValue(p, text);
        assertTrue(csspv.isResolved());

    }

    public void testBackgroundImageURL() {
        PropertyModel p = CssModuleSupport.getPropertyModel("background-image");
        String text = "url('/images/v6/tabs-bg.png')";
        PropertyValue csspv = new PropertyValue(p, text);

//        dumpResult(csspv);

        assertTrue(csspv.isResolved());

        text = "url'/images/v6/tabs-bg.png')";
        csspv = new PropertyValue(p, text);
        assertFalse(csspv.isResolved());

        text = "ury('/images/v6/tabs-bg.png')";
        csspv = new PropertyValue(p, text);
        assertFalse(csspv.isResolved());

    }

    public void testAbsoluteLengthUnits() {
        PropertyModel p = CssModuleSupport.getPropertyModel("font");
        String text = "12px/14cm sans-serif";
        PropertyValue csspv = new PropertyValue(p, text);
        assertTrue(csspv.isResolved());
    }

    public void testUnquotedURL() {
        PropertyModel p = CssModuleSupport.getPropertyModel("@uri");
        String text = "url(http://www.redballs.com/redball.png)";
        PropertyValue csspv = new PropertyValue(p, text);
        assertTrue(csspv.isResolved());
    }

    public void testBackroundImage() {
        PropertyModel p = CssModuleSupport.getPropertyModel("background-image");
        assertNotResolve(p.getGrammar(), "");
    }

    public void testBackroundPositionOrder() {
        // TODO: fix #142254 and enable this test again
        PropertyModel p = CssModuleSupport.getPropertyModel("@bg-position");
        assertResolve(p.getGrammar(), "center top");
    }

    public void testBorderColor() {
        PropertyModel p = CssModuleSupport.getPropertyModel("border-color");
        assertResolve(p.getGrammar(), "red yellow black yellow");
        assertResolve(p.getGrammar(), "red yellow black");
        assertResolve(p.getGrammar(), "red yellow");
        assertResolve(p.getGrammar(), "red");

        assertNotResolve(p.getGrammar(), "xxx");
        assertNotResolve(p.getGrammar(), "");
    }

    public void testIssue185995() {
        PropertyModel p = CssModuleSupport.getPropertyModel("border-color");
        assertResolve(p.getGrammar(), "transparent transparent");
    }

    public void testBorder_Top_Style() {
        PropertyModel p = CssModuleSupport.getPropertyModel("border-top-style");

        PropertyValue csspv = new PropertyValue(p, "dotted dotted dashed dashed");
        assertFalse(csspv.isResolved());

        csspv = new PropertyValue(p, "dotted");
        assertTrue(csspv.isResolved());

    }

    public void testCaseSensitivity() {
        PropertyModel p = CssModuleSupport.getPropertyModel("azimuth");
        String text = "behind";
        PropertyValue csspv = new PropertyValue(p, text);
        assertTrue(csspv.isResolved());

        text = "BEHIND";
        csspv = new PropertyValue(p, text);
        assertTrue(csspv.isResolved());

    }

    public void testJindrasCase() {
        String g = "[ [ x || y ] || b";

        assertResolve(g, "x");
        assertResolve(g, "x y");
        assertResolve(g, "y");
        assertResolve(g, "y x");
        assertResolve(g, "y x b");
        assertResolve(g, "x b");
        assertResolve(g, "y b");
        assertResolve(g, "y x b");

        assertNotResolve(g, "x b y");
    }
    
    public void testContentFailure() {
        String g = "[ uri , ]* normal";
        
        assertResolve(g, "normal");
        assertResolve(g, "uri, normal");
        assertResolve(g, "uri, uri, normal");
    }
    
    public void testCase4() {
         String grammar2 = "[ a b ] | [ a c ]";
         assertNotResolve(grammar2, "a");
    }
    
    public void testMultiplicityOfList() {
        String g = " [ a || b ]*";
        
        assertResolve(g, "a");
        assertResolve(g, "a b");
        assertResolve(g, "a b a");
    }
    
    public void testAllGroup() {
        String g = "a && b";
        
        assertResolve(g, "a b");
        assertResolve(g, "b a");
        
        assertNotResolve(g, "");
        assertNotResolve(g, "a");
        assertNotResolve(g, "b");
        assertNotResolve(g, "b b");
        assertNotResolve(g, "a a");
        assertNotResolve(g, "x");
        assertNotResolve(g, "a x");
        assertNotResolve(g, "x x b");
    }
    
    public void testAllGroupComplex() {
        String g = "a [ a && b ] b";        
        assertResolve(g, "a a b b");
        assertResolve(g, "a b a b");        
    }

    public void testAllGroupComplex2() {
        String g = "a [ a && b ]? b";        
        assertResolve(g, "a a b b");
        assertResolve(g, "a b a b");        
        assertResolve(g, "a b");        
        
        assertNotResolve(g, "a a b");        
        assertNotResolve(g, "a b b");        
    }
    
    public void testAllGroupComplex3() {
        String g = "[ a && b ]*";
        assertResolve(g, "");
        assertResolve(g, "a b a b a b");
        assertResolve(g, "b a a b");        
        assertResolve(g, "b a");        
        
        assertNotResolve(g, "a b b");        
        assertNotResolve(g, "b a a b a");        
    }
 
    public void testAllGroupComplex4() {
        String g = "a && c?";
        assertResolve(g, "a");
        assertResolve(g, "a c");
        assertResolve(g, "c a");
    }
    
    public void testBackground() {
        PropertyModel pm = CssModuleSupport.getPropertyModel("background");
        assertResolve(pm.getGrammarElement(), "url(images/shadow.gif) no-repeat bottom right");
    }
    
    
    //Bug 206035 - Incorrect background property value validation/completion
    public void testBackground2() {
        PropertyModel pm = CssModuleSupport.getPropertyModel("background");
        assertResolve(pm.getGrammarElement(), "#fff url(\"../images/google\") no-repeat center left");
    }

    
//    //should be already fixed in easel (the grammar resolver uses antlr tokens)
//    public void testURI() {
//        PRINT_INFO_IN_ASSERT_RESOLVE = true;
//        GrammarResolver.setLogging(GrammarResolver.Log.DEFAULT, true);
//        
//        PropertyModel pm = CssModuleSupport.getPropertyModel("@uri");
//        assertResolve(pm.getGrammarElement(), "url(images/google)");
//        assertResolve(pm.getGrammarElement(), "url(../images/google)");        
//    }
//    
    
    public void testBgPosition() {
        PropertyModel pm = CssModuleSupport.getPropertyModel("@bg-position");
        assertResolve(pm.getGrammarElement(), "center left");
    }
    
    public void testBgPositionDetail_And() {
        //the minimized grammar to reproduce the bg-position resolving problem
        String grammar = "[ center | a ] && [ center | b ]";
        
        assertResolve(grammar, "center b");
        assertResolve(grammar, "b center");
        assertResolve(grammar, "a b");
        assertResolve(grammar, "b a");
        assertResolve(grammar, "center center");        
        assertResolve(grammar, "a center");
        
        assertResolve(grammar, "center a"); //this used to fail
        
    }
    
    public void testBgPositionDetail_Collection() {
        //the minimized grammar to reproduce the bg-position resolving problem
        String grammar = "[ center | a ] || [ center | b ]";
        
        assertResolve(grammar, "center");
        assertResolve(grammar, "b");
        assertResolve(grammar, "a");
        assertResolve(grammar, "b");
        
        assertResolve(grammar, "center b");
        assertResolve(grammar, "b center");
        assertResolve(grammar, "a b");
        assertResolve(grammar, "b a");
        assertResolve(grammar, "center center");        
        assertResolve(grammar, "a center");
        
        assertResolve(grammar, "center a"); //this used to fail
        
    }
    

}
