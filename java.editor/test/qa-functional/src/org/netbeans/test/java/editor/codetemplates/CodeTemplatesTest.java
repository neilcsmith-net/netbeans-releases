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
package org.netbeans.test.java.editor.codetemplates;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ListModel;
import junit.framework.Test;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JEditorPaneOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.lib.editor.codetemplates.SurroundWithFix;
import org.netbeans.test.java.editor.codetemplates.operators.CodeTemplatesOperator;
import org.netbeans.test.java.editor.lib.JavaEditorTestCase;

/**
 *
 * @author Jiri Prox Jiri.Prox@Sun.COM
 */
public class CodeTemplatesTest extends JavaEditorTestCase {

    public CodeTemplatesTest(String testMethodName) {
        super(testMethodName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        openProject("java_editor_test");
        //CustomizedLog.enableInstances(Logger.getLogger("TIMER"),"CompilationInfo", Level.FINEST);
    }

    @Override
    protected void tearDown() throws Exception {
        //CustomizedLog.assertInstances("All instances are not removed","CompilationInfo");
        super.tearDown();
        //throw new OutOfMemoryError("Intentional OOMF");
    }

    public void testFor() throws IOException {
        EditorOperator oper = null;
        try {
            openSourceFile("org.netbeans.test.java.editor.codetemplates", "Main");
            oper = new EditorOperator("Main");
            JEditorPaneOperator txtOper = oper.txtEditorPane();
            oper.setCaretPosition(6, 9);
            txtOper.typeText("fori");
            txtOper.pressKey(KeyEvent.VK_TAB);
            oper.setCaretPosition(9, 10);
            txtOper.typeText("whilen");
            txtOper.pressKey(KeyEvent.VK_TAB);
            compareGoldenFile(oper);
        } finally {
            if (oper != null) {
                oper.closeDiscardAll();
            }
        }
    }

    public void testDumpTemplates() {
        CodeTemplatesOperator oper = null;

        try {
            oper = CodeTemplatesOperator.invoke(true);
            List<String[]> dumpTemplatesTable = CodeTemplatesOperator.getDefaultTemplates();
            for (String[] strings : dumpTemplatesTable) {
                ref("-----------------------");
                for (int i = 0; i < 3; i++) {
                    ref(strings[i]);
                }
            }
            compareGoldenFile();
        } catch (IOException exception) {
            fail(exception);
        } finally {
            if (oper != null) {
                oper.ok();
            }
        }
    }

    public void testAddTemplate() {
        final String abbrev = "test";        
        final String expandedText = "System.out.println(\"test\");";
        final String expandedRegExpText = ".*System\\.out\\.println\\(\"test\"\\);.*";
        CodeTemplatesOperator oper = null;
        try {
            oper = CodeTemplatesOperator.invoke(true);
            boolean selectTemplate = oper.switchLanguage("Java").addNewTemplate(abbrev).selectTemplate(abbrev);
            assertTrue("Template with abbreviation " + abbrev + " not found", selectTemplate);
            oper.setExtendedText(expandedText);
        } finally {
            oper.ok();
        }
        prepareEditorAndUseTemplate(6, 9, null, -1, abbrev, false, expandedRegExpText);

    }

    public void testAddBlockTemplate() {
        final String abbrev = "testBlock";
        final String expandedText = "{ ${selection} }";
        CodeTemplatesOperator oper = null;
        try {
            oper = CodeTemplatesOperator.invoke(true);
            boolean selectTemplate = oper.switchLanguage("Java").addNewTemplate(abbrev).selectTemplate(abbrev);
            assertTrue("Template with abbreviation " + abbrev + " not found", selectTemplate);
            oper.setExtendedText(expandedText);
        } finally {
            oper.ok();
        }
        prepareEditorAndUseTemplate(6, 9, null, -1, abbrev, false, ".*( ){8}\\{\\s+( ){8}\\}.*");
    }

    public void testAddBlockTemplate2() {
        final String abbrev = "testBlock2";
        final String expandedText = "{ ${selection} }";
        final String description = "block";
        CodeTemplatesOperator oper = null;
        try {
            oper = CodeTemplatesOperator.invoke(true);
            boolean selectTemplate = oper.switchLanguage("Java").addNewTemplate(abbrev).selectTemplate(abbrev);
            assertTrue("Template with abbreviation " + abbrev + " not found", selectTemplate);
            oper.setExtendedText(expandedText).setDescription(description);
            new EventTool().waitNoEvent(250);
        } finally {
            oper.ok();
        }        
        prepareEditorAndUseTemplate(6, 9, "int i;", 14, description, true, ".*\\{\\s+int i;\\s+\\}.*");
    }

    public void testAddDuplicity() {
        final String abbrev = "2al";
        CodeTemplatesOperator oper = null;
        try {
            oper = CodeTemplatesOperator.invoke(true);
            oper.switchLanguage("Java").addNewTemplate(abbrev);
            JDialogOperator dialog = new JDialogOperator("Error");
            JButtonOperator button = new JButtonOperator(dialog, "OK");
            button.pushNoBlock();
        } finally {
            oper.ok();
        }
    }

    public void testAddEmpty() {
        final String abbrev = "";
        CodeTemplatesOperator oper = null;
        try {
            oper = CodeTemplatesOperator.invoke(true);
            oper.switchLanguage("Java").addNewTemplate(abbrev);
            JDialogOperator dialog = new JDialogOperator("Error");
            JButtonOperator button = new JButtonOperator(dialog, "OK");
            button.pushNoBlock();
        } finally {
            oper.ok();
        }
    }

    public void testRemoveTemplate() {
        final String abbrev = "vo";
        CodeTemplatesOperator oper = null;
        try {
            oper = CodeTemplatesOperator.invoke(true);
            boolean selectTemplate = oper.switchLanguage("Java").removeTemplate(abbrev);
            assertTrue("Template with abbreviation " + abbrev + " not found", selectTemplate);
            new EventTool().waitNoEvent(250);
            selectTemplate = oper.selectTemplate(abbrev);
            assertFalse("Template with abbreviation " + abbrev + " was found", selectTemplate);
        } finally {
            oper.ok();
        }
        prepareEditorAndUseTemplate(6, 9, null, -1, abbrev, false, ".*vo[^l].*");
    }

    public void testSorting() {
        CodeTemplatesOperator oper = null;
        String[] sortedByAbbrev = new String[]{"a", "b", "c"};
        String[] sortedByExpanded = new String[]{"c", "b", "a"};
        try {
            oper = CodeTemplatesOperator.invoke(true);
            oper.switchLanguage("Plain Text").addNewTemplate("a", "ccc").addNewTemplate("b", "bbb").addNewTemplate("c", "aaa");
            List<String[]> dump = oper.changeOrder(0).dumpTemplatesTable();
            for (int i = 0; i < 3; i++) {
                assertEquals("Incorrect order at index " + i, sortedByAbbrev[i], dump.get(i)[0]);
            }
            dump = oper.changeOrder(0).dumpTemplatesTable();
            for (int i = 0; i < 3; i++) {
                assertEquals("Incorrect order at index " + i, sortedByAbbrev[2 - i], dump.get(i)[0]);
            }
            dump = oper.changeOrder(1).dumpTemplatesTable();
            for (int i = 0; i < 3; i++) {
                assertEquals("Incorrect order at index " + i, sortedByExpanded[i], dump.get(i)[0]);
            }
            dump = oper.changeOrder(1).dumpTemplatesTable();
            for (int i = 0; i < 3; i++) {
                assertEquals("Incorrect order at index " + i, sortedByExpanded[2 - i], dump.get(i)[0]);
            }
        } finally {
            oper.ok();
        }
    }

    public void testEditCode() {
        final String abbrev = "St";
        CodeTemplatesOperator oper = null;
        EditorOperator editor = null;
        try {
            oper = CodeTemplatesOperator.invoke(true);
            boolean selectTemplate = oper.switchLanguage("Java").selectTemplate(abbrev);
            assertTrue("Template with abbreviation " + abbrev + " not found", selectTemplate);
            String text = oper.getExtendedText();
            oper.setExtendedText(text + " x;");
        } finally {
            oper.ok();
        }
        prepareEditorAndUseTemplate(6, 9, null, -1, abbrev, false, ".*String x;.*");
    }

    public void testEditDescription() {
        final String abbrev = "iff";
        final String description = "condition";
        CodeTemplatesOperator oper = null;
        try {
            oper = CodeTemplatesOperator.invoke(true);
            boolean selectTemplate = oper.switchLanguage("Java").selectTemplate(abbrev);
            assertTrue("Template with abbreviation " + abbrev + " not found", selectTemplate);
            oper.setDescription(description);        
        } finally {
            oper.ok();
        }        
        prepareEditorAndUseTemplate(6, 9, "int x;", 14, description, true, ".*if \\(true\\).*");
    }

    public void testRemoveAfterSorting() {
        CodeTemplatesOperator oper = null;
        try {
            oper = CodeTemplatesOperator.invoke(true);
            oper.switchLanguage("Plain Text").addNewTemplate("a", "ccc").addNewTemplate("b", "bbb").addNewTemplate("c", "aaa");            
            oper.changeOrder(1).selectLine(0).removeActualTemplate();
            oper.selectLine(0);                    
            new EventTool().waitNoEvent(250);
            List<String[]> dump = oper.dumpTemplatesTable();
            for (String[] strings : dump) {
                if (strings[0].equals("c")) {
                    fail("Template 'c' was not removed");
                }
            }
        } finally {
            oper.ok();
        }
    }

    public void testEditAfterSorting() {
        CodeTemplatesOperator oper = null;
        try {
            oper = CodeTemplatesOperator.invoke(true);
            oper.switchLanguage("Plain Text").addNewTemplate("a", "ccc").addNewTemplate("b", "bbb").addNewTemplate("c", "aaa");
            oper.changeOrder(1).selectLine(0).setExtendedText("ddd");
            new EventTool().waitNoEvent(250);
            oper.selectLine(1);
            new EventTool().waitNoEvent(250);
            List<String[]> dump = oper.dumpTemplatesTable();
            for (String[] strings : dump) {
                if (strings[0].equals("c")) {
                    assertEquals("Template is not updated", "ddd", strings[1]);
                }
            }
        } finally {
            oper.ok();
        }
    }

    public void testPersitenceOfSelectedTemplate() {
        CodeTemplatesOperator oper = null;
        final String abbrev = "Ob";
        try {
            oper = CodeTemplatesOperator.invoke(true);
            oper.switchLanguage("Java").selectTemplate(abbrev);
            oper.getOptionsOperator().selectGeneral();
            oper.getOptionsOperator().selectEditor();
            assertEquals("Wrong template selected", abbrev, oper.getSelectedTemplate());
        } finally {
            oper.ok();
        }
    }

    public void testScope() {
        CodeTemplatesOperator oper = null;
        String[] ctxs = {"FOR_LOOP", "DO_WHILE_LOOP", "LABELED_STATEMENT",
            "ENHANCED_FOR_LOOP", "WHILE_LOOP", "LAMBDA_EXPRESSION", "BLOCK",
            "IF", "CASE"};
        final String abbrev = "sout";
        try {
            oper = CodeTemplatesOperator.invoke(true);
            oper.switchLanguage("Java").selectTemplate(abbrev);
            Set<String> contexts = oper.getContexts();
            for (String string : ctxs) {
                assertTrue("Context " + string + " is not selected", contexts.contains(string));
            }
        } finally {
            oper.ok();
        }
        prepareEditorAndUseTemplate(4, 9, null, -1, abbrev, false, ".*sout.*");
        prepareEditorAndUseTemplate(6, 9, null, -1, abbrev, false, ".*System\\.out\\.println.*");
    }

    public void testChangeScope() {
        CodeTemplatesOperator oper = null;
        Set<String> contexts = new HashSet<>();
        contexts.add("ANNOTATION");
        contexts.add("BLOCK");
        final String abbrev = "test3";
        try {
            oper = CodeTemplatesOperator.invoke(true);
            oper.switchLanguage("Java").addNewTemplate(abbrev).selectTemplate(abbrev);
            oper.setExtendedText("System.out.println(\"ok\");").setContext(contexts);
        } finally {
            oper.ok();
        }
        prepareEditorAndUseTemplate(6, 9, null, -1, abbrev, false, ".*System\\.out\\.println\\(\"ok\"\\).*");
        try {
            oper = CodeTemplatesOperator.invoke(true);
            oper.switchLanguage("Java").selectTemplate(abbrev);
            contexts.remove("BLOCK");
            oper.setContext(contexts);
        } finally {
            oper.ok();
        }
        prepareEditorAndUseTemplate(6, 9, null, -1, abbrev, false, ".*test3.*");
    }

    public void testBlankScope() {
        CodeTemplatesOperator oper = null;
        final String abbrev = "sh";
        try {
            oper = CodeTemplatesOperator.invoke(true);
            oper.switchLanguage("Java").selectTemplate(abbrev);
            Set<String> contexts = oper.getContexts();
            assertEquals("Context is not empty", 0, contexts.size());
        } finally {
            oper.ok();
        }
        prepareEditorAndUseTemplate(2, 1, null, -1, abbrev, false, ".*short.*");
    }

    public void testSynchronizedBlock() {
        final String abbrev = "form";
        String regExp = ".*for \\(Map.Entry<Object, Object> en \\: m\\.entrySet\\(\\)\\) \\{.*";
        EditorOperator editor = null;
        try {
            openSourceFile("org.netbeans.test.java.editor.codetemplates", "Main");
            editor = new EditorOperator("Main");
            useTemplateAt(editor, 6, 9, abbrev);
            checkContentOfEditorRegexp(editor, regExp);
            JEditorPaneOperator jepo = editor.txtEditorPane();
            assertEquals("Text is not selected", "en", jepo.getSelectedText());
            assertEquals("Wrong start selection position ", 159, jepo.getSelectionStart());
            assertEquals("Wrong end selection position", 161, jepo.getSelectionEnd());

            jepo.typeText("var");
            checkContentOfEditorRegexp(editor, ".*var \\:.*var\\.getKey.*var\\.getValue.*");
            jepo.pressKey(KeyEvent.VK_TAB);
            assertEquals("Wrong start selection position ", 165, jepo.getSelectionStart());
            assertEquals("Wrong end selection position", 166, jepo.getSelectionEnd());
            jepo.pressKey(KeyEvent.VK_TAB);
            assertEquals("Wrong start selection position ", 193, jepo.getSelectionStart());
            assertEquals("Wrong end selection position", 199, jepo.getSelectionEnd());
            jepo.pressKey(KeyEvent.VK_TAB);
            assertEquals("Wrong start selection position ", 200, jepo.getSelectionStart());
            assertEquals("Wrong end selection position", 206, jepo.getSelectionEnd());
        } finally {
            if (editor != null) {
                editor.closeDiscard();
            }
        }
    }

    public void testNoIndentKeyword() {
        final String abbrev = "cn";
        prepareEditorAndUseTemplate(6, 2, null, -1, abbrev, false, ".* continue.*");
        if (true) {
        }
    }

    public void testNoFormatKeyword() {
        final String abbrev = "test4";
        final String expandedText = "${no-format}if(true) { }";
        CodeTemplatesOperator oper = null;
        try {
            oper = CodeTemplatesOperator.invoke(true);
            boolean selectTemplate = oper.switchLanguage("Java").addNewTemplate(abbrev).selectTemplate(abbrev);
            assertTrue("Template with abbreviation " + abbrev + " not found", selectTemplate);
            oper.setExtendedText(expandedText);
        } finally {
            oper.ok();
        }
        prepareEditorAndUseTemplate(6, 9, null, -1, abbrev, false, ".*if\\(true\\) \\{ \\}.*");
    }

    public void testChageExpansionKey() {
        final String abbrev = "Pf";
        CodeTemplatesOperator oper = null;
        CodeTemplatesOperator.ExpandTemplateOn expandOnDefault = CodeTemplatesOperator.ExpandTemplateOn.TAB;
        CodeTemplatesOperator.ExpandTemplateOn expandOn1 = CodeTemplatesOperator.ExpandTemplateOn.SHIFTSPACE;
        CodeTemplatesOperator.ExpandTemplateOn expandOn2 = CodeTemplatesOperator.ExpandTemplateOn.SPACE;
        CodeTemplatesOperator.ExpandTemplateOn expandOn3 = CodeTemplatesOperator.ExpandTemplateOn.ENTER;
        try {
            try {
                oper = CodeTemplatesOperator.invoke(true);
                oper.setExpandTemplateOn(expandOn1);
            } finally {
                oper.ok();
            }
            prepareEditorAndUseTemplate(6, 9, null, -1, abbrev, false, ".*Pf.*");
            expansionKey = expandOn1;
            prepareEditorAndUseTemplate(6, 9, null, -1, abbrev, false, ".*public final.*");
            try {
                oper = CodeTemplatesOperator.invoke(true);
                oper.setExpandTemplateOn(expandOn2);
            } finally {
                oper.ok();
            }
            expansionKey = expandOn2;
            prepareEditorAndUseTemplate(6, 9, null, -1, abbrev, false, ".*public final.*");
            try {
                oper = CodeTemplatesOperator.invoke(true);
                oper.setExpandTemplateOn(expandOn3);
            } finally {
                oper.ok();
            }
            expansionKey = expandOn3;
            prepareEditorAndUseTemplate(6, 9, null, -1, abbrev, false, ".*public final.*");
        } finally {
            try {
                expansionKey = expandOnDefault;
                oper = CodeTemplatesOperator.invoke(true);
                oper.setExpandTemplateOn(expandOnDefault);
            } finally {
                oper.ok();
            }
        }
    }

    public void testOnExpansionAction() {
        final String abbrev = "trycatch";
        CodeTemplatesOperator oper = null;
        try {
            try {
                oper = CodeTemplatesOperator.invoke(true);
                oper.setOnExpansion(CodeTemplatesOperator.OnExpansion.NOTHING);
            } finally {
                oper.ok();
            }
            prepareEditorAndUseTemplate(6, 1, null, -1, abbrev, false, ".*try \\{.*\\} catch \\(Exception e\\) \\{\\}.*");
            try {
                oper = CodeTemplatesOperator.invoke(true);
                oper.setOnExpansion(CodeTemplatesOperator.OnExpansion.REINDENT);
            } finally {
                oper.ok();
            }
            prepareEditorAndUseTemplate(6, 1, null, -1, abbrev, false, ".*        try \\{.*        \\} catch \\(Exception e\\) \\{\\}.*");
        } finally {
            try {
                oper = CodeTemplatesOperator.invoke(true);
                oper.setOnExpansion(CodeTemplatesOperator.OnExpansion.REFORMAT);
            } finally {
                oper.ok();
            }
        }
    }

    public void testExperessionOnLeftSide() {
        final String abbrev = "fore";
        EditorOperator editor = null;
        try {
            openSourceFile("org.netbeans.test.java.editor.codetemplates", "Main");
            editor = new EditorOperator("Main");
            JEditorPaneOperator jepo = editor.txtEditorPane();
            editor.setCaretPosition(4, 1);
            jepo.typeText("java.util.List<String> a;java.util.List<Integer> b;");
            useTemplateAt(editor, 6, 9, abbrev);
            jepo.pressKey(KeyEvent.VK_TAB);
            jepo.typeText("b");
            checkContentOfEditorRegexp(editor, ".*for \\(Integer integer \\: b\\) \\{.*");
        } finally {
            editor.closeDiscard();
        }
    }

    public void testTemplatesInCompletionByCode() {
        final String abbrev = "xxxyyy";
        final String expanded = "abcdef";
        final Set<String> context = new HashSet<>();
        context.add("BLOCK");
        CodeTemplatesOperator oper = null;
        EditorOperator editor = null;
        try {
            oper = CodeTemplatesOperator.invoke(true);
            oper.switchLanguage("Java").addNewTemplate(abbrev,expanded).selectTemplate(abbrev);
            oper.setContext(context);            
        } finally {
            oper.ok();
        }
        try {
            openSourceFile("org.netbeans.test.java.editor.codetemplates", "Main");
            editor = new EditorOperator("Main");
            JEditorPaneOperator jepo = editor.txtEditorPane();
            editor.setCaretPosition(6, 9);
            jepo.typeText("abcde");            
            jepo.pressKey(KeyEvent.VK_SPACE,KeyEvent.CTRL_DOWN_MASK);
            new EventTool().waitNoEvent(1000);
            jepo.pressKey(KeyEvent.VK_ENTER);
            checkContentOfEditorRegexp(editor, ".*abcdef.*");
        } finally {
            editor.closeDiscard();
        }
    }

    public void testTemplatesInCompletionByAbbrev() {
        final String abbrev = "xxxyyy";
        final String expanded = "abcdef";
        final Set<String> context = new HashSet<>();
        context.add("BLOCK");
        CodeTemplatesOperator oper = null;
        EditorOperator editor = null;
        try {
            oper = CodeTemplatesOperator.invoke(true);
            oper.switchLanguage("Java").addNewTemplate(abbrev,expanded).selectTemplate(abbrev);
            oper.setContext(context);            
        } finally {
            oper.ok();
        }
        try {
            openSourceFile("org.netbeans.test.java.editor.codetemplates", "Main");
            editor = new EditorOperator("Main");
            JEditorPaneOperator jepo = editor.txtEditorPane();
            editor.setCaretPosition(6, 9);
            jepo.typeText("xxxyy");            
            jepo.pressKey(KeyEvent.VK_SPACE,KeyEvent.CTRL_DOWN_MASK);
            new EventTool().waitNoEvent(1000);
            jepo.pressKey(KeyEvent.VK_ENTER);
            checkContentOfEditorRegexp(editor, ".*abcdef.*");
        } finally {
            editor.closeDiscard();
        }
    }

    public static Test suite() {
        return NbModuleSuite.create(
                NbModuleSuite.createConfiguration(CodeTemplatesTest.class)
                .addTest("testDumpTemplates")
                .addTest("testAddTemplate")
                .addTest("testAddBlockTemplate")
                .addTest("testAddBlockTemplate2")
                .addTest("testAddDuplicity")
                .addTest("testAddEmpty")
                .addTest("testRemoveTemplate")
                .addTest("testSorting")
                .addTest("testEditCode")
                .addTest("testEditDescription")
                .addTest("testRemoveAfterSorting")
                .addTest("testEditAfterSorting")
                .addTest("testPersitenceOfSelectedTemplate")
                .addTest("testScope")
                .addTest("testChangeScope")
                .addTest("testBlankScope")
                .addTest("testSynchronizedBlock")
                .addTest("testNoIndentKeyword")
                .addTest("testNoFormatKeyword")
                //                .addTest("testChageExpansionKey")
                .addTest("testOnExpansionAction")
                .addTest("testExperessionOnLeftSide")
                .addTest("testTemplatesInCompletionByCode")
                .addTest("testTemplatesInCompletionByAbbrev")
                .enableModules(".*")
                .clusters(".*"));
    }

    private CodeTemplatesOperator.ExpandTemplateOn expansionKey = CodeTemplatesOperator.ExpandTemplateOn.TAB;

    private void useTemplateAt(EditorOperator editor, int row, int col, String abbrev) {
        useTemplateAt(editor, row, col, abbrev, expansionKey.getKeyCode(), expansionKey.getKeyModifier());
    }

    private void useTemplateAt(EditorOperator editor, int row, int col, String abbrev, int key, int modifier) {
        editor.setCaretPosition(row, col);
        JEditorPaneOperator txtOper = editor.txtEditorPane();
        txtOper.typeText(abbrev);
        editor.pressKey(key, modifier);
        editor.releaseKey(key, modifier);        
    }

    private void invokeTemplateAsHint(EditorOperator editor, final String description) {
        final String blockTemplatePrefix = "<html>Surround with ";

        new EventTool().waitNoEvent(500);
        editor.pressKey(KeyEvent.VK_ENTER, KeyEvent.ALT_DOWN_MASK);
        JListOperator jlo = new JListOperator(MainWindowOperator.getDefault());
        ListModel model = jlo.getModel();
        int i;
        for (i = 0; i < model.getSize(); i++) {
            Object item = model.getElementAt(i);
            String hint = "n/a";
            if (item instanceof SurroundWithFix) {
                hint = ((SurroundWithFix) item).getText();
            }
            if (hint.startsWith(blockTemplatePrefix + description)) {
                break;
            }
        }
        if (i == model.getSize()) {
            fail("Template not found in the hint popup");
        }
        jlo.selectItem(i);
    }

    /**
     * Prepares content of editor and invokes hint.
     *
     * @param row Row where the template is applied
     * @param column Column where the template is applied
     * @param text Optional text which should by typed before template is
     * expanded
     * @param selectionEnd End of selection, if path of line should be selected,
     * -1 otherwise
     * @param abbrev Abbreviation or description of expanded template
     * @param asHint The template should be invoked from hint popup
     * @param expandedTextRegexp Regular expression checking the content of
     * editor after expansion
     */
    private void prepareEditorAndUseTemplate(final int row, final int column, String text, int selectionEnd, final String abbrev, final boolean asHint, final String expandedTextRegexp) {
        EditorOperator editor = null;
        try {
            openSourceFile("org.netbeans.test.java.editor.codetemplates", "Main");
            editor = new EditorOperator("Main");
            if (text != null) {
                editor.setCaretPosition(row, column);
                editor.insert(text);
            }
            if (selectionEnd > -1) {
                editor.select(row, column, selectionEnd);
            }
            if (asHint) {
                invokeTemplateAsHint(editor, abbrev);
            } else {
                useTemplateAt(editor, row, column, abbrev);
            }
            checkContentOfEditorRegexp(editor, expandedTextRegexp);
        } catch (Exception ex) {
            fail(ex);
        } finally {
            if (editor != null) {
                editor.closeDiscard();
            }
        }
    }
}
