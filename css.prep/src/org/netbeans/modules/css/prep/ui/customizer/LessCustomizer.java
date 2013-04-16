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
package org.netbeans.modules.css.prep.ui.customizer;

import java.io.IOException;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.css.prep.less.LessCssPreprocessor;
import org.netbeans.modules.css.prep.preferences.LessPreferences;
import org.netbeans.modules.css.prep.preferences.LessPreferencesValidator;
import org.netbeans.modules.css.prep.util.ValidationResult;
import org.netbeans.modules.css.prep.util.Warnings;
import org.netbeans.modules.web.common.spi.CssPreprocessorImplementation;
import org.netbeans.modules.web.common.spi.ProjectWebRootProvider;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public final class LessCustomizer implements CssPreprocessorImplementation.Customizer {

    private final LessCssPreprocessor lessCssPreprocessor;
    private final Project project;

    private volatile LessCustomizerPanel customizerPanel = null;


    public LessCustomizer(LessCssPreprocessor lessCssPreprocessor, Project project) {
        assert lessCssPreprocessor != null;
        assert project != null;
        this.lessCssPreprocessor = lessCssPreprocessor;
        this.project = project;
    }

    @NbBundle.Messages("LessCustomizer.displayName=LESS")
    @Override
    public String getDisplayName() {
        return Bundle.LessCustomizer_displayName();
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        getComponent().addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        getComponent().removeChangeListener(listener);
    }

    @Override
    public synchronized LessCustomizerPanel getComponent() {
        if (customizerPanel == null) {
           customizerPanel = new LessCustomizerPanel(project.getLookup().lookup(ProjectWebRootProvider.class));
           customizerPanel.setLessEnabled(LessPreferences.isEnabled(project));
           customizerPanel.setMappings(LessPreferences.getMappings(project));
        }
        assert customizerPanel != null;
        return customizerPanel;
    }

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("org.netbeans.modules.css.prep.ui.customizer.LessCustomizer"); // NOI18N
    }

    @Override
    public boolean isValid() {
        return getValidationResult().isFaultless();
    }

    @Override
    public String getErrorMessage() {
        return getValidationResult().getFirstErrorMessage();
    }

    @Override
    public String getWarningMessage() {
        return getValidationResult().getFirstWarningMessage();
    }

    @Override
    public void save() throws IOException {
        Warnings.resetLessWarning();
        boolean fire = false;
        // enabled
        boolean originalEnabled = LessPreferences.isEnabled(project);
        boolean enabled = getComponent().isLessEnabled();
        LessPreferences.setEnabled(project, enabled);
        if (enabled != originalEnabled) {
            fire = true;
        }
        // mappings
        List<String> originalMappings = LessPreferences.getMappings(project);
        List<String> mappings = getComponent().getMappings();
        LessPreferences.setMappings(project, mappings);
        if (!mappings.equals(originalMappings)) {
            fire = true;
        }
        // change?
        if (fire) {
            lessCssPreprocessor.fireCustomizerChanged(project);
        }
    }

    private ValidationResult getValidationResult() {
        boolean enabled;
        List<String> mappings;
        if (customizerPanel == null) {
            // ui not shown (yet)
            enabled = LessPreferences.isEnabled(project);
            mappings = LessPreferences.getMappings(project);
        } else {
            enabled = getComponent().isLessEnabled();
            mappings = getComponent().getMappings();
        }
        return new LessPreferencesValidator()
                .validate(enabled, mappings)
                .getResult();
    }

}
