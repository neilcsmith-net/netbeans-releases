/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.remote.api.ui;

import java.io.File;
import javax.swing.JFileChooser;
import junit.framework.Test;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.util.ConnectionManager;
import org.netbeans.modules.nativeexecution.test.ForAllEnvironments;
import org.netbeans.modules.nativeexecution.test.NativeExecutionBaseTestCase;
import org.netbeans.modules.remote.test.RemoteApiBaseTestSuite;

/**
 *
 * @author Vladimir Kvashin
 */
public class RemoteFileChooserBuilderTestCase extends NativeExecutionBaseTestCase {

    public RemoteFileChooserBuilderTestCase(String name, ExecutionEnvironment env) {
        super(name, env);
    }

    public RemoteFileChooserBuilderTestCase(String name) {
        super(name);
    }

    @ForAllEnvironments(section="RemoteFileChooserBuilderTestCase")
    public void testRemoteFileChoser() throws Exception {
        ExecutionEnvironment env = getTestExecutionEnvironment();
        ConnectionManager.getInstance().connectTo(env);
        RemoteFileChooserBuilder fcb = new RemoteFileChooserBuilder(env);
        JFileChooser chooser = fcb.createFileChooser();
        int ret = chooser.showDialog(null, "Choose file at " + env.getDisplayName());
        switch (ret) {
            case JFileChooser.CANCEL_OPTION:
                System.err.printf("Canclled\n");
                break;
            case JFileChooser.APPROVE_OPTION:
                System.err.printf("Approved\n");
                File file = chooser.getSelectedFile();
                System.err.printf("Selected: %s\n", file);
                break;
            case JFileChooser.ERROR_OPTION:
                System.err.printf("Error\n");
                break;
        }

    }

    @SuppressWarnings("unchecked")
    public static Test suite() {
        return new RemoteApiBaseTestSuite(RemoteFileChooserBuilderTestCase.class);
    }
}
