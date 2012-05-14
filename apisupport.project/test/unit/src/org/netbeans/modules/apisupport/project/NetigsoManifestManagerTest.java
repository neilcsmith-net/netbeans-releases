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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
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

package org.netbeans.modules.apisupport.project;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.jar.Manifest;
import org.netbeans.modules.apisupport.project.api.ManifestManager;
import org.netbeans.junit.NbTestCase;
import org.openide.util.test.TestFileUtils;

/**
 * Test functionality of ManifestManager when it sees OSGi bundles.
 *
 * @author Jaroslav Tulach
 */
public class NetigsoManifestManagerTest extends NbTestCase {

    public NetigsoManifestManagerTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        clearWorkDir();
        super.setUp();
    }
    
    public void testBundle1() throws Exception {
        File manifest = new File(getWorkDir(), "testManifest.mf");
        String mfContent = "Manifest-Version: 1.0\n" +
                "Ant-Version: Apache Ant 1.6.5\n" +
                "Created-By: 1.4.2_10-b03 (Sun Microsystems Inc.)\n" +
                "Bundle-SymbolicName: org.netbeans.modules.sendopts\n" +
                "OpenIDE-Module-Localizing-Bundle: org/netbeans/modules/sendopts/Bundle.properties\n" +
                "Bundle-Version: 1.9\n" +
                "Export-Package: a, b, c\n" +
                "Bundle-RequireExecutionEnvironment: J2SE-1.3\n" +
                "OpenIDE-Module-Layer: org/netbeans/modules/sendopts/layer.xml\n";
        TestFileUtils.writeFile(manifest, mfContent);
        ManifestManager mm = ManifestManager.getInstance(manifest, true);
        assertEquals("cnb", "org.netbeans.modules.sendopts", mm.getCodeNameBase());
        assertEquals("version", "1.9", mm.getSpecificationVersion());
        assertEquals("layer", "org/netbeans/modules/sendopts/layer.xml", mm.getLayer());
        assertEquals("Three packages", 3, mm.getPublicPackages().length);
        assertEquals("a", mm.getPublicPackages()[0].getPackage());
        assertEquals("b", mm.getPublicPackages()[1].getPackage());
        assertEquals("c", mm.getPublicPackages()[2].getPackage());
        assertFalse("not recursivea", mm.getPublicPackages()[0].isRecursive());
        assertFalse("not recursiveb", mm.getPublicPackages()[1].isRecursive());
        assertFalse("not recursivec", mm.getPublicPackages()[2].isRecursive());
    }
    public void testDashesToUnderscore() throws Exception {
        File manifest = new File(getWorkDir(), "testManifest.mf");
        String mfContent = "Manifest-Version: 1.0\n" +
                "Ant-Version: Apache Ant 1.6.5\n" +
                "Created-By: 1.4.2_10-b03 (Sun Microsystems Inc.)\n" +
                "Bundle-SymbolicName: org.netbeans.send-opts; singleton:=true\n" +
                "Require-Bundle: org.netbeans.some-lib;version=\"[1.0,2)\"\n";
        TestFileUtils.writeFile(manifest, mfContent);
        ManifestManager mm = ManifestManager.getInstance(manifest, true);
        assertEquals("cnb", "org.netbeans.send_opts", mm.getCodeNameBase());
        assertEquals(Collections.singletonList("org.netbeans.some_lib"), Arrays.asList(mm.getRequiredTokens()));
    }

    public void testSingletonBundle() throws Exception {
        File manifest = new File(getWorkDir(), "testManifest.mf");
        String mfContent = "Manifest-Version: 1.0\n" +
                "Ant-Version: Apache Ant 1.6.5\n" +
                "Created-By: 1.4.2_10-b03 (Sun Microsystems Inc.)\n" +
                "Bundle-SymbolicName: org.netbeans.modules.sendopts; singleton:=true\n" +
                "OpenIDE-Module-Localizing-Bundle: org/netbeans/modules/sendopts/Bundle.properties\n" +
                "Bundle-Version: 1.9\n" +
                "Export-Package: a, b, c\n" +
                "Require-Bundle: test.core,test.tasks;bundle-version=\"[3.0.0,4.0.0)\"\n" +
                "Bundle-RequireExecutionEnvironment: J2SE-1.3\n" +
                "OpenIDE-Module-Layer: org/netbeans/modules/sendopts/layer.xml\n";
        TestFileUtils.writeFile(manifest, mfContent);
        ManifestManager mm = ManifestManager.getInstance(manifest, true);
        assertEquals("cnb", "org.netbeans.modules.sendopts", mm.getCodeNameBase());
        assertEquals("version", "1.9", mm.getSpecificationVersion());
        assertEquals("layer", "org/netbeans/modules/sendopts/layer.xml", mm.getLayer());
        assertEquals("Three packages", 3, mm.getPublicPackages().length);
        assertEquals("a", mm.getPublicPackages()[0].getPackage());
        assertEquals("b", mm.getPublicPackages()[1].getPackage());
        assertEquals("c", mm.getPublicPackages()[2].getPackage());
        assertFalse("not recursivea", mm.getPublicPackages()[0].isRecursive());
        assertFalse("not recursiveb", mm.getPublicPackages()[1].isRecursive());
        assertFalse("not recursivec", mm.getPublicPackages()[2].isRecursive());

        List<String> pt = assertProvidedTokens(mm);
        assertEquals("Provides four: " + pt, 4, pt.size());
        assertEquals("Bundle name first", "org.netbeans.modules.sendopts", pt.get(0));
        assertEquals("Packages then", "a", pt.get(1));
        assertEquals("Packages then", "b", pt.get(2));
        assertEquals("Packages then", "c", pt.get(3));
        assertEquals("Requires two: " + Arrays.toString(mm.getRequiredTokens()), 2, mm.getRequiredTokens().length);
        assertEquals("Needs core", "test.core", mm.getRequiredTokens()[0]);
        assertEquals("Needs tasks", "test.tasks", mm.getRequiredTokens()[1]);
    }


    public void testVersionIsConcatenatedAndPackagesExtracted() throws Exception {
        File manifest = new File(getWorkDir(), "testManifest.mf");
        String mfContent = "Manifest-Version: 1.0\n" +
                "Ant-Version: Apache Ant 1.6.5\n" +
                "Created-By: 1.4.2_10-b03 (Sun Microsystems Inc.)\n" +
                "Bundle-SymbolicName: org.netbeans.modules.sendopts\n" +
                "OpenIDE-Module-Localizing-Bundle: org/netbeans/modules/sendopts/Bundle.properties\n" +
                "Bundle-Version: 1.9.7.Prelude\n" +
                "Import-Package: client.prefs;" +
"version=\"3.0.0.Prelude\",admin.cli;version=\"3.5.0.Prelude\"\n" +
                "Export-Package: javax.mail.search;uses:=\"javax.mail.internet,javax.mai" +
 "l\";version=\"1.4\",javax.mail.event;uses:=\"javax.mail\";version=\"1.4\",ja" +
 "vax.mail.util;uses:=\"javax.activation,javax.mail.internet\";version=\"1" +
 ".4\",javax.mail.internet;uses:=\"javax.mail.util,javax.activation,javax" +
 ".mail\";version=\"1.4\",javax.mail;uses:=\"javax.mail.search,javax.mail.e" +
 "vent,javax.activation\";version=\"1.4\"\n";
        TestFileUtils.writeFile(manifest, mfContent);
        ManifestManager mm = ManifestManager.getInstance(manifest, true);
        assertEquals("cnb", "org.netbeans.modules.sendopts", mm.getCodeNameBase());
        assertEquals("version", "1.9.7", mm.getSpecificationVersion());
        assertEquals("version prelude taken as build version", "1.9.7.Prelude", mm.getImplementationVersion());
        assertEquals("Five packages: " + Arrays.asList(mm.getPublicPackages()), 5, mm.getPublicPackages().length);
        assertEquals("javax.mail.search", mm.getPublicPackages()[0].getPackage());
        assertEquals("javax.mail.event", mm.getPublicPackages()[1].getPackage());
        assertEquals("javax.mail.util", mm.getPublicPackages()[2].getPackage());
        assertEquals("javax.mail.internet", mm.getPublicPackages()[3].getPackage());
        assertEquals("javax.mail", mm.getPublicPackages()[4].getPackage());
        List<String> pt = assertProvidedTokens(mm);
        assertEquals("Six tokens: " + pt, 6, pt.size());
        assertEquals("org.netbeans.modules.sendopts", pt.get(0));
        assertEquals("javax.mail.search", pt.get(1));
        assertEquals("javax.mail.event", pt.get(2));
        assertEquals("javax.mail.util", pt.get(3));
        assertEquals("javax.mail.internet", pt.get(4));
        assertEquals("javax.mail", pt.get(5));
        assertEquals("Two required tokens: " + Arrays.asList(mm.getRequiredTokens()), 2, mm.getRequiredTokens().length);
        assertEquals("client.prefs", mm.getRequiredTokens()[0]);
        assertEquals("admin.cli", mm.getRequiredTokens()[1]);
    }
    
    public void testProvidedTokensOfOSGiContainer() throws Exception {
        File wrapperJar = new File(getWorkDir(), "wrapper.jar");
        TestFileUtils.writeZipFile(wrapperJar, "META-INF/MANIFEST.MF:" +
                "OpenIDE-Module: wrapper\n" +
                "OpenIDE-Module-Provides: org.osgi.framework.launch.FrameworkFactory\n" +
                "Class-Path: ext/container.jar\n");
        File ext = new File(getWorkDir(), "ext");
        ext.mkdir();
        TestFileUtils.writeZipFile(new File(ext, "container.jar"), "META-INF/MANIFEST.MF:" +
                "Bundle-SymbolicName: super.container; singleton:=true\n" +
                "Export-Package: super.container.features;version=\"1.0\"\n");
        assertEquals("[org.osgi.framework.launch.FrameworkFactory, super.container, super.container.features]",
                new TreeSet<String>(assertProvidedTokens(ManifestManager.getInstanceFromJAR(wrapperJar))).toString());
    }

    public void testImportJREPackage() throws Exception {
        Manifest m = new Manifest();
        m.getMainAttributes().putValue("Bundle-SymbolicName", "my.bundle");
        m.getMainAttributes().putValue("Require-Bundle", "whatever");
        m.getMainAttributes().putValue("Import-Package", "actual.api, javax.swing");
        assertEquals("[actual.api, whatever]",
                new TreeSet<String>(Arrays.asList(ManifestManager.getInstance(m, true).getRequiredTokens())).toString());
    }

    private static List<String> assertProvidedTokens(ManifestManager mm) {
        List<String> arr = new ArrayList<String>(Arrays.asList(mm.getProvidedTokens()));
        if (!arr.remove("cnb." + mm.getCodeNameBase())) {
            fail("There should be cnb." + mm.getCodeNameBase() + " in " + arr);
        }
        return arr;
    }
}
