/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.uihandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;
import org.openide.awt.Mnemonics;

/**
 *
 * @author Martin Entlicher
 */
final class ButtonsHTMLParser {
    
    static final Logger logger = Logger.getLogger(ButtonsHTMLParser.class.getName());
    
    private final String definition;
    private FormHTMLParser formParser;
    private List<Object> options;
    private List<Object> additionalOptions;
    private boolean containsExitButton;
    
    public ButtonsHTMLParser(InputStream is) throws IOException {
        Reader r = new InputStreamReader(is, "utf-8");
        StringWriter sw = new StringWriter();
        char[] cbuf = new char[4096];
        int n;
        while ((n = r.read(cbuf)) > 0) {
            sw.write(cbuf, 0, n);
        }
        this.definition = sw.toString();
    }

    void parse() throws IOException {
        ParserDelegator pd = new ParserDelegator();
        formParser = new FormHTMLParser();
        Reader r = new StringReader(definition);
        pd.parse(r, formParser, true);
    }
    
    void createButtons() {
        if (formParser == null) {
            throw new IllegalStateException("parse() must be called before this.");
        }
        options = new ArrayList<>();
        additionalOptions = new ArrayList<>();
        List<MutableAttributeSet> inputs = formParser.getInputs();
        if (inputs.isEmpty()) {
            return ;
        }
        String action = attrValue(inputs.get(0), Attribute.ACTION);
        if (action == null || action.isEmpty()) {
            throw new IllegalStateException("Action should not be empty");
        }
        String url = action;
        for (int i = 1; i < inputs.size(); i++) {
            MutableAttributeSet node = inputs.get(i);
            String name = attrValue(node, Attribute.NAME);
            String value = attrValue(node, Attribute.VALUE);
            String align = attrValue(node, Attribute.ALIGN);
            String alt = attrValue(node, Attribute.ALT);
            //Incorrect value but we keep it here for backward compatibility
            //Correct value of atribute "disabled" is "disabled"
            boolean enabled = true;
            String disabledValue = attrValue(node, "disabled");                 // NOI18N
            if ("true".equals(disabledValue)) { // NOI18N
                enabled = false;
            } else if ("disabled".equals(disabledValue)) {
                enabled = false;
            }
            
            List<Object> addTo = "left".equals(align) ? additionalOptions : options;

            if (Installer.Button.isSubmitTrigger(name)) { // NOI18N
                String submitValue = value;
                JButton b = new JButton();
                Mnemonics.setLocalizedText(b, submitValue);
                b.setActionCommand(name);
                b.putClientProperty("url", url); // NOI18N
                b.setDefaultCapable(addTo.isEmpty() && addTo == options);
                b.putClientProperty("alt", alt); // NOI18N
                b.putClientProperty("now", submitValue); // NOI18N
                b.setEnabled(enabled);
                addTo.add(b);
            } else {
                JButton b = new JButton();
                Mnemonics.setLocalizedText(b, value);
                b.setActionCommand(name);
                b.setDefaultCapable(addTo.isEmpty() && addTo == options);
                b.putClientProperty("alt", alt); // NOI18N
                b.putClientProperty("now", value); // NOI18N
                b.setEnabled(enabled && Installer.Button.isKnown(name));
                addTo.add(b);
                if (Installer.Button.EXIT.isCommand(name)) {
                    containsExitButton = true;
                }
                if (Installer.Button.REDIRECT.isCommand(name)) {
                    b.putClientProperty("url", url); // NOI18N
                }
            }
        }
    }
    
    private static String attrValue(AttributeSet attr, Object attrName) {
        Object valueObj = attr.getAttribute(attrName);
        if (valueObj == null) {
            return null;
        }
        if (!(valueObj instanceof String)) {
            throw new IllegalStateException("Attribute "+attrName+" has a wrong value: "+valueObj);
        }
        return (String) valueObj;
    }
    
    private static String attrValue(AttributeSet attr, String attrName) {
        for (Enumeration en = attr.getAttributeNames(); en.hasMoreElements(); ) {
            Object name = en.nextElement();
            if (attrName.equalsIgnoreCase(name.toString())) {
                return attrValue(attr, name);
            }
        }
        return null;
    }
    
    List<Object> getOptions() {
        return options;
    }

    List<Object> getAdditionalOptions() {
        return additionalOptions;
    }

    String getTitle() {
        return formParser.getTitle();
    }

    boolean containsExitButton() {
        return containsExitButton;
    }
    
    private static final class FormHTMLParser extends ParserCallback {
        
        private static final String TAG_TITLE = "title";                        // NOI18N
        private static final String TAG_FORM = "form";                          // NOI18N
        private static final String TAG_INPUT = "input";                        // NOI18N
        
        private boolean readingTitle = false;
        private String title;
        private boolean readingForm = false;
        private final List<MutableAttributeSet> inputs = new ArrayList<>();
        
        public String getTitle() {
            return title;
        }
        
        public List<MutableAttributeSet> getInputs() {
            return inputs;
        }
        
        @Override
        public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            String tag = t.toString();
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "StartTag <{0}> with attributes: {1}",
                           new Object[]{ tag, Collections.list(a.getAttributeNames()).toString() });
            }
            if (TAG_TITLE.equalsIgnoreCase(tag)) {
                readingTitle = true;
                return ;
            } else {
                readingTitle = false;
            }
            if (TAG_FORM.equalsIgnoreCase(tag)) {
                readingForm = true;
                inputs.clear();
                inputs.add(new SimpleAttributeSet(a));
                return ;
            }
            if (readingForm) {
                if (TAG_INPUT.equalsIgnoreCase(tag)) {
                    inputs.add(new SimpleAttributeSet(a));
                }
            }
        }

        @Override
        public void handleEndTag(HTML.Tag t, int pos) {
            String tag = t.toString();
            logger.log(Level.FINE, "EndTag <{0}>", tag);
            if (TAG_TITLE.equalsIgnoreCase(tag)) {
                readingTitle = false;
            }
            if (TAG_FORM.equalsIgnoreCase(tag)) {
                readingForm = false;
            }
        }

        @Override
        public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            String tag = t.toString();
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "SimpleTag <{0}> with attributes: {1}",
                           new Object[]{tag, Collections.list(a.getAttributeNames()).toString()});
            }
            if (readingForm) {
                if (TAG_INPUT.equalsIgnoreCase(tag)) {
                    inputs.add(new SimpleAttributeSet(a));
                }
            }
        }

        @Override
        public void handleText(char[] data, int pos) {
            if (logger.isLoggable(Level.FINE)) {
                String text = new String(data);
                logger.log(Level.FINE, "Text: ''{0}''", new Object[]{ text });
            }
            if (readingTitle) {
                title = new String(data);
            }
        }

        @Override
        public void handleError(String errorMsg, int pos) {
            logger.log(Level.FINE, "Error: ''{0}'' at {1}", new Object[]{ errorMsg, pos });
        }
        
    }
    
}
