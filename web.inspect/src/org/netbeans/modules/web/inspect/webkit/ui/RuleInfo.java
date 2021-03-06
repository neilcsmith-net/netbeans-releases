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
package org.netbeans.modules.web.inspect.webkit.ui;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.inspect.CSSUtils;
import org.netbeans.modules.web.inspect.actions.Resource;
import org.netbeans.modules.web.common.sourcemap.Mapping;
import org.netbeans.modules.web.common.sourcemap.SourceMap;
import org.netbeans.modules.web.inspect.webkit.Utilities;
import org.netbeans.modules.web.webkit.debugging.api.css.Media;
import org.netbeans.modules.web.webkit.debugging.api.css.Rule;
import org.netbeans.modules.web.webkit.debugging.api.css.Style;
import org.netbeans.modules.web.webkit.debugging.api.css.StyleSheetBody;
import org.openide.filesystems.FileObject;

/**
 * Additional information about a rule.
 *
 * @author Jan Stola
 */
public class RuleInfo {
    /** Names of properties that are overridden by other rules. */
    private final Set<String> overridenProperties = new HashSet<String>();
    /**
     * Determines whether the rules matches the selected element or whether
     * it matches some parent of the selected element (i.e., is inherited).
     */
    private boolean inherited;
    /** Meta-source file of the rule. */
    private String metaSourceFile;
    /** Line number of the rule in the meta-source file. */
    private int metaSourceLine = -1;

    /**
     * Marks the specified property as overridden by other rules.
     *
     * @param propertyName name of the overridden property.
     */
    void markAsOverriden(String propertyName) {
        overridenProperties.add(propertyName);
    }

    /**
     * Determines whether the specified property is overridden by other rules.
     *
     * @param propertyName name of the property to check.
     * @return {@code true} when the property is overridden,
     * returns {@code false} otherwise.
     */
    public boolean isOverriden(String propertyName) {
        return overridenProperties.contains(propertyName);
    }

    /**
     * Sets whether the rule is inherited or not.
     * 
     * @param inherited determines whether the rule matches the selected
     * element or whether it matches some parent of the selected element
     * (i.e., is inherited).
     */
    void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    /**
     * Determines whether the rules matches the selected element or whether
     * it matches some parent of the selected element (i.e., is inherited).
     * 
     * @return {@code true} when the rule comes from some parent,
     * returns {@code false} otherwise.
     */
    public boolean isInherited() {
        return inherited;
    }

    /**
     * Sets the meta-source file of the rule.
     * 
     * @param metaSourceFile meta-source file of the rule.
     */
    private void setMetaSourceFile(String metaSourceFile) {
        this.metaSourceFile = metaSourceFile;
    }

    /**
     * Returns the meta-source file of the rule.
     * 
     * @return meta-source file of the rule.
     */
    public String getMetaSourceFile() {
        return metaSourceFile;
    }

    /**
     * Sets the line number of the rule in the meta-source file.
     * 
     * @param metaSourceLine line number of the rule.
     */
    private void setMetaSourceLine(int metaSourceLine) {
        this.metaSourceLine = metaSourceLine;
    }

    /**
     * Returns the line number of the rule in the meta-source file.
     * 
     * @return line number of the rule in the meta-source file.
     */
    public int getMetaSourceLine() {
        return metaSourceLine;
    }

    /**
     * Fills information about the meta-source of the specified rule
     * into this {@code RuleInfo}.
     * 
     * @param rule rule whose meta-source information should be filled.
     * @param project origin of the rule.
     */
    void fillMetaSourceInfo(Rule rule, Project project) {
        StyleSheetBody body = rule.getParentStyleSheet();
        if (body != null) {
            String styleSheetText = body.getText();
            if (styleSheetText != null) {
                String sourceMapPath = CSSUtils.sourceMapPath(styleSheetText);
                if (sourceMapPath != null) {
                    FileObject cssFile = new Resource(project, rule.getSourceURL()).toFileObject();
                    if (cssFile != null) {
                        FileObject folder = cssFile.getParent();
                        FileObject sourceMapFob = folder.getFileObject(sourceMapPath);
                        if (sourceMapFob != null) {
                            try {
                                String sourceMapText = sourceMapFob.asText();
                                SourceMap sourceMap = SourceMap.parse(sourceMapText);
                                final Mapping mapping = sourceMap.findMapping(rule.getSourceLine());
                                if (mapping != null) {
                                    int sourceIndex = mapping.getSourceIndex();
                                    String sourcePath = sourceMap.getSourcePath(sourceIndex);
                                    folder = sourceMapFob.getParent();
                                    FileObject source = folder.getFileObject(sourcePath);
                                    if (source == null) {
                                        // Invalid path in the source map.
                                        // Could be caused by incorrect options
                                        // of the compiler, see issue 238924.
                                        Logger.getLogger(RuleInfo.class.getName()).log(Level.INFO,
                                                "Unable to find the file {0} relative to the source map {1}!",
                                                new Object[] {sourcePath, sourceMapFob.getPath()});
                                    } else {
                                        String sourceURL = source.toURL().toExternalForm();
                                        String sourceFile = Utilities.relativeResourceName(sourceURL, project);
                                        setMetaSourceFile(sourceFile);
                                        setMetaSourceLine(mapping.getOriginalLine()+1);
                                        return;
                                    }
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(RuleInfo.class.getName()).log(Level.INFO, null, ex);
                            }
                        }
                    }
                }
            }
            List<Rule> rules = body.getRules();
            int index = rules.indexOf(rule);
            if (index != -1) {
                while (index > 0) {
                    index--;
                    Rule previousRule = rules.get(index);
                    List<Media> medias = previousRule.getMedia();
                    if (medias.isEmpty()) {
                        break;
                    } else {
                        Media media = medias.get(0);
                        String mediaText = media.getText();
                        if ("-sass-debug-info".equals(mediaText)) { // NOI18N
                             String selector = previousRule.getSelector();
                             if ("filename".equals(selector)) { // NOI18N
                                 org.netbeans.modules.web.webkit.debugging.api.css.Property property =
                                     property(previousRule, "font-family"); // NOI18N
                                 String file = (property == null) ? null : propertyValueHack(property);
                                 setMetaSourceFile(file);
                             } else if ("line".equals(selector)) { // NOI18N
                                 org.netbeans.modules.web.webkit.debugging.api.css.Property property =
                                     property(previousRule, "font-family"); // NOI18N
                                 String lineTxt = property.getValue();
                                 String prefix = "0003"; // NOI18N
                                 int prefixIndex = lineTxt.indexOf(prefix);
                                 if (prefixIndex != -1) {
                                     lineTxt = lineTxt.substring(prefixIndex + prefix.length());
                                     try {
                                        int lineNo = Integer.parseInt(lineTxt);
                                        setMetaSourceLine(lineNo);
                                     } catch (NumberFormatException nfex) {
                                         Logger.getLogger(MatchedRulesNode.class.getName()).log(Level.INFO, null, nfex);
                                     }
                                 }
                             }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the property with the specified name.
     * 
     * @param rule rule whose property should be returned.
     * @param propertyName name of the property that should be returned.
     * @return property with the specified name or {@code null} when
     * there is no property with such a name in the given rule.
     */
    private static org.netbeans.modules.web.webkit.debugging.api.css.Property property(Rule rule, String propertyName) {
        org.netbeans.modules.web.webkit.debugging.api.css.Property result = null;
        Style style = rule.getStyle();
        for (org.netbeans.modules.web.webkit.debugging.api.css.Property property : style.getProperties()) {
            String name = property.getName();
            if (propertyName.equals(name)) {
                 result = property;
                 break;
            }
        }
        return result;
    }

    /**
     * Method that attempts to get the value of the specified property
     * from the text of the property directly. This method is used to obtain
     * some debugging information for SASS/LESS that is incorrectly returned
     * by {@code Property.getValue()} because of some bug on Chrome side.
     * 
     * @param property property whose value should be returned.
     * @return value of the specified property.
     */
    private String propertyValueHack(org.netbeans.modules.web.webkit.debugging.api.css.Property property) {
        String text = property.getText();
        int index = text.indexOf(":"); // NOI18N
        text = text.substring(index+1).trim();
        StringBuilder sb = new StringBuilder();
        boolean slash = false;
        for (int i=0; i<text.length(); i++) {
            char c = text.charAt(i);
            if (slash && (c != ':' && c != '/' && c != '.')) {
                sb.append('\\');
            }
            slash = !slash && (c == '\\');
            if (!slash) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
