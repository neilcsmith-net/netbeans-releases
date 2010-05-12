/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nativeexecution.support;

import org.netbeans.modules.nativeexecution.api.util.MacroMap;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;
import org.netbeans.modules.nativeexecution.api.util.ProcessUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author ak119685
 */
public final class EnvWriter {

    public static final String[] wellKnownVars = new String[]{
        "LANG", "LC_COLLATE", "LC_CTYPE", "LC_MESSAGES", "LC_MONETARY", // NOI18N
        "LC_NUMERIC", "LC_TIME", "TMPDIR", "PATH", "LD_LIBRARY_PATH", // NOI18N
        "LD_PRELOAD" // NOI18N
    };
    private final OutputStream os;
    private final boolean remote;

    public EnvWriter(final OutputStream os, final boolean remote) {
        this.os = os;
        this.remote = remote;
    }

    public static byte[] getBytes(String str, boolean remote) {
        if (remote) {
            final String charSet = ProcessUtils.getRemoteCharSet();
            if (java.nio.charset.Charset.isSupported(charSet)) {
                try {
                    return str.getBytes(charSet);
                } catch (UnsupportedEncodingException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return str.getBytes();
    }

    public void write(final MacroMap env) throws IOException {
        if (!env.isEmpty()) {
            String value = null;
            // Very simple sanity check of vars...
            Pattern pattern = Pattern.compile("[A-Z0-9_]+"); // NOI18N

            for (String name : env.getExportVariablesSet()) {
                // check capitalized key by pattern
                if (!pattern.matcher(name.toUpperCase(java.util.Locale.ENGLISH)).matches()) {
                    continue;
                }

                value = env.get(name);

                if (value != null && value.indexOf('"') < 0) {
                    os.write(getBytes(name + "=\"" + value + "\" && export " + name + "\n", remote)); // NOI18N
                    os.flush();
                }
            }
        }
    }
}
