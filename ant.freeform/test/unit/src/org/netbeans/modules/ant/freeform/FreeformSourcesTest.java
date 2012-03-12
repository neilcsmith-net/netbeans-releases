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

package org.netbeans.modules.ant.freeform;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.junit.RandomlyFails;
import org.netbeans.modules.ant.freeform.spi.support.Util;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// XXX testExternalSourceRootChanges
// - should check that FOQ changes as well

/**
 * Test {@link FreeformSources}.
 * @author Jesse Glick
 */
public class FreeformSourcesTest extends TestBase {
    
    public FreeformSourcesTest(String name) {
        super(name);
    }
    
    public void testSources() throws Exception {
        Sources s = ProjectUtils.getSources(simple);
        SourceGroup[] groups = s.getSourceGroups(Sources.TYPE_GENERIC);
        assertEquals("one generic group", 1, groups.length);
        assertEquals("right root folder", simple.getProjectDirectory(), groups[0].getRootFolder());
        assertEquals("right display name", "Simple Freeform Project", groups[0].getDisplayName());
        groups = s.getSourceGroups("java");
        assertEquals("two Java groups", 2, groups.length);
        assertEquals("right root folder #1", simple.getProjectDirectory().getFileObject("src"), groups[0].getRootFolder());
        assertEquals("right display name #1", "Main Sources", groups[0].getDisplayName());
        assertEquals("right root folder #2", simple.getProjectDirectory().getFileObject("antsrc"), groups[1].getRootFolder());
        assertEquals("right display name #2", "Ant Task Sources", groups[1].getDisplayName());
    }
    
    public void testExternalSourceRoot() throws Exception {
        Sources s = ProjectUtils.getSources(extsrcroot);
        SourceGroup[] groups = s.getSourceGroups(Sources.TYPE_GENERIC);
        assertEquals("one generic group", 1, groups.length);
        assertEquals("right root folder", egdirFO.getFileObject("extsrcroot"), groups[0].getRootFolder());
        assertEquals("right display name", "Top-Level Dir", groups[0].getDisplayName());
        groups = s.getSourceGroups("java");
        assertEquals("one Java group", 1, groups.length);
        assertEquals("right root folder", egdirFO.getFileObject("extsrcroot/src"), groups[0].getRootFolder());
        assertEquals("right display name", "External Sources", groups[0].getDisplayName());
        assertEquals("correct file owner", extsrcroot, FileOwnerQuery.getOwner(egdirFO.getFileObject("extsrcroot/src/org/foo/Foo.java")));
    }
    
    public void testSourceRootChanges() throws Exception {
        FileObject top = FileUtil.toFileObject(copyFolder(FileUtil.toFile(egdirFO.getFileObject("extsrcroot"))));
        FreeformProject extsrcroot_ = (FreeformProject) ProjectManager.getDefault().findProject(top.getFileObject("proj"));
        Sources s = ProjectUtils.getSources(extsrcroot_);
        SourceGroup[] groups = s.getSourceGroups("java");
        assertEquals("one Java group", 1, groups.length);
        assertEquals("right root folder", top.getFileObject("src"), groups[0].getRootFolder());
        TestCL l = new TestCL();
        s.addChangeListener(l);
        Element data = extsrcroot_.getPrimaryConfigurationData();
        Element folders = XMLUtil.findElement(data, "folders", FreeformProjectType.NS_GENERAL);
        assertNotNull("have <folders>", folders);
        List<Element> sourceFolders = XMLUtil.findSubElements(folders);
        assertEquals("have 2 <source-folder>s", 2, sourceFolders.size());
        Element sourceFolder = sourceFolders.get(1);
        Element location = XMLUtil.findElement(sourceFolder, "location", FreeformProjectType.NS_GENERAL);
        assertNotNull("have <location>", location);
        NodeList nl = location.getChildNodes();
        assertEquals("one child (text)", 1, nl.getLength());
        location.removeChild(nl.item(0));
        location.appendChild(location.getOwnerDocument().createTextNode("../src2"));
        extsrcroot_.putPrimaryConfigurationData(data);
        assertEquals("got a change in Sources", 1, l.changeCount());
        groups = s.getSourceGroups("java");
        assertEquals("one Java group", 1, groups.length);
        assertEquals("right root folder", top.getFileObject("src2"), groups[0].getRootFolder());
    }
    
    public void testExternalBuildRoot() throws Exception {
        // Check that <build-folder> works.
        FileObject builtFile = egdirFO.getFileObject("extbuildroot/build/built.file");
        assertNotNull("have built.file", builtFile);
        assertEquals("owned by extbuildroot project", extbuildroot, FileOwnerQuery.getOwner(builtFile));
    }

    @RandomlyFails
    public void testIncludesExcludes() throws Exception {
        clearWorkDir();
        File d = getWorkDir();
        AntProjectHelper helper = FreeformProjectGenerator.createProject(d, d, "prj", null);
        Project p = ProjectManager.getDefault().findProject(helper.getProjectDirectory());
        FileUtil.createData(new File(d, "s/relevant/included/file"));
        FileUtil.createData(new File(d, "s/relevant/excluded/file"));
        FileUtil.createData(new File(d, "s/ignored/file"));
        Element data = Util.getPrimaryConfigurationData(helper);
        Document doc = data.getOwnerDocument();
        Element sf = (Element) data.insertBefore(doc.createElementNS(Util.NAMESPACE, "folders"), XMLUtil.findElement(data, "view", Util.NAMESPACE)).
                appendChild(doc.createElementNS(Util.NAMESPACE, "source-folder"));
        sf.appendChild(doc.createElementNS(Util.NAMESPACE, "label")).appendChild(doc.createTextNode("Sources"));
        sf.appendChild(doc.createElementNS(Util.NAMESPACE, "type")).appendChild(doc.createTextNode("stuff"));
        sf.appendChild(doc.createElementNS(Util.NAMESPACE, "location")).appendChild(doc.createTextNode("s"));
        Util.putPrimaryConfigurationData(helper, data);
        ProjectManager.getDefault().saveProject(p);
        Sources s = ProjectUtils.getSources(p);
        SourceGroup[] gs = s.getSourceGroups("stuff");
        assertEquals(1, gs.length);
        assertEquals(FileUtil.toFileObject(new File(d, "s")), gs[0].getRootFolder());
        assertEquals("Sources", gs[0].getDisplayName());
        assertEquals("ignored{file} relevant{excluded{file} included{file}}", expand(gs[0]));
        // Now configure includes and excludes.
        EditableProperties ep = new EditableProperties();
        ep.put("includes", "relevant/");
        ep.put("excludes", "**/excluded/");
        helper.putProperties("config.properties", ep);
        data = Util.getPrimaryConfigurationData(helper);
        doc = data.getOwnerDocument();
        data.getElementsByTagName("properties").item(0).
                appendChild(doc.createElementNS(Util.NAMESPACE, "property-file")).
                appendChild(doc.createTextNode("config.properties"));
        Util.putPrimaryConfigurationData(helper, data);
        ProjectManager.getDefault().saveProject(p);
        data = Util.getPrimaryConfigurationData(helper);
        doc = data.getOwnerDocument();
        sf = (Element) data.getElementsByTagName("source-folder").item(0);
        sf.appendChild(doc.createElementNS(Util.NAMESPACE, "includes")).
                appendChild(doc.createTextNode("${includes}"));
        sf.appendChild(doc.createElementNS(Util.NAMESPACE, "excludes")).
                appendChild(doc.createTextNode("${excludes}"));
        Util.putPrimaryConfigurationData(helper, data);
        ProjectManager.getDefault().saveProject(p);
        gs = s.getSourceGroups("stuff");
        assertEquals("relevant{included{file}}", expand(gs[0]));
        // Now change them.
        TestPCL l = new TestPCL();
        gs[0].addPropertyChangeListener(l);
        ep = helper.getProperties("config.properties");
        ep.remove("includes");
        helper.putProperties("config.properties", ep);
        ProjectManager.getDefault().saveProject(p);
        assertEquals("ignored{file} relevant{included{file}}", expand(gs[0]));
        assertEquals(Collections.singleton(SourceGroup.PROP_CONTAINERSHIP), l.changed);
    }

    public void testNonExistentRoot() throws Exception {
        clearWorkDir();
        final File d = getWorkDir();
        final File proj = new File (d,"proj");
        final File extSrcDir = new File (d,"ext");
        proj.mkdir();
        AntProjectHelper helper = FreeformProjectGenerator.createProject(proj, proj, "prj", null);
        Project p = ProjectManager.getDefault().findProject(helper.getProjectDirectory());

        Element data = Util.getPrimaryConfigurationData(helper);
        Document doc = data.getOwnerDocument();
        Element sf = (Element) data.insertBefore(doc.createElementNS(Util.NAMESPACE, "folders"), XMLUtil.findElement(data, "view", Util.NAMESPACE)).
                appendChild(doc.createElementNS(Util.NAMESPACE, "source-folder"));
        sf.appendChild(doc.createElementNS(Util.NAMESPACE, "label")).appendChild(doc.createTextNode("Sources"));
        sf.appendChild(doc.createElementNS(Util.NAMESPACE, "location")).appendChild(doc.createTextNode("../ext"));
        Util.putPrimaryConfigurationData(helper, data);
        ProjectManager.getDefault().saveProject(p);

        final Sources src = ProjectUtils.getSources(p);
        SourceGroup[] sgs = src.getSourceGroups(Sources.TYPE_GENERIC);
        assertEquals(1,sgs.length);
        assertEquals(p.getProjectDirectory(), sgs[0].getRootFolder());
        final FileObject extFo = FileUtil.createFolder(extSrcDir);
        sgs = src.getSourceGroups(Sources.TYPE_GENERIC);
        assertSourceGroupsEquals(new FileObject[] {p.getProjectDirectory(), extFo}, sgs);
        assertEquals(p, FileOwnerQuery.getOwner(extFo));
    }

    private void assertSourceGroupsEquals(final FileObject[] expected, final SourceGroup[] sgs) {
        assertEquals(
            "Roots: " + Arrays.toString(expected) + " SourceGroups: " + Arrays.toString(sgs),
            expected.length,
            sgs.length);
        final Set<FileObject> fos = new HashSet<FileObject>(Arrays.asList(expected));
        for (SourceGroup sg : sgs) {
            assertTrue(
                "Roots: " + Arrays.toString(expected) + " SourceGroups: " + Arrays.toString(sgs),
                fos.remove(sg.getRootFolder()));
        }
    }

    private static String expand(SourceGroup g) {
        return expand(g, g.getRootFolder());
    }
    private static String expand(SourceGroup g, FileObject d) {
        SortedSet<String> subs = new TreeSet<String>();
        for (FileObject kid : d.getChildren()) {
            if (!g.contains(kid)) {
                continue;
            }
            String sub = kid.getNameExt();
            if (kid.isFolder()) {
                sub += '{' + expand(g, kid) + '}';
            }
            subs.add(sub);
        }
        StringBuilder b = new StringBuilder();
        for (String sub : subs) {
            if (b.length() > 0) {
                b.append(' ');
            }
            b.append(sub);
        }
        return b.toString();
    }
    
}
