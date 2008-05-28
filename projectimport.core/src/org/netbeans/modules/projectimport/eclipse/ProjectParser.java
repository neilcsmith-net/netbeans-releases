/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.projectimport.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parses given project's .project file and fills up the project with found
 * data.
 *
 * @author mkrauskopf
 */
final class ProjectParser {
    
    public static String parse(File dotProject, Set<String> natures, List<Link> links) throws IOException {
        Document dotProjectXml;
        try {
            dotProjectXml = XMLUtil.parse(new InputSource(dotProject.toURI().toString()), false, true, Util.defaultErrorHandler(), null);
        } catch (SAXException e) {
            IOException ioe = (IOException) new IOException(dotProject + ": " + e.toString()).initCause(e);
            throw ioe;
        }
        Element projectDescriptionEl = dotProjectXml.getDocumentElement();
        if (!"projectDescription".equals(projectDescriptionEl.getLocalName())) { // NOI18N
            throw new IllegalStateException("given file is not eclipse .project file");
        }
        
        Element naturesEl = Util.findElement(projectDescriptionEl, "natures", null);
        if (naturesEl != null) {
            List<Element> natureEls = Util.findSubElements(naturesEl);
            if (natureEls != null) {
                for (Element nature : natureEls) {
                    natures.add(nature.getTextContent());
                }
            }
        }
        
        Element linksEl = Util.findElement(projectDescriptionEl, "linkedResources", null);
        if (linksEl != null) {
            List<Element> linkEls = Util.findSubElements(linksEl);
            if (linkEls != null) {
                for (Element link : linkEls) {
                    // TODO: location can start with environment variable:
                    /*
                        <link>
                            <name>classes-webapp5</name>
                            <type>2</type>
                            <locationURI>SOME_ROOT/WebApplication5/build/web/WEB-INF/classes</locationURI>
                        </link>
                     */
                    //
                    // environment variable are stored in 
                    // .metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.core.resources.prefs
                    // which in this case would contain property:
                    // pathvariable.SOME_ROOT=/home/david/projs
                    links.add(new Link(Util.findElement(link, "name", null).getTextContent(), 
                            "1".equals(Util.findElement(link, "type", null).getTextContent()),
                            Util.findElement(link, "location", null).getTextContent()));
                }
            }
        }
        return Util.findElement(projectDescriptionEl, "name", null).getTextContent();
    }
    
    
}
