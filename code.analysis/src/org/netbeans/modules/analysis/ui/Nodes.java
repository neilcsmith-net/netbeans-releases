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

package org.netbeans.modules.analysis.ui;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.analysis.spi.Analyzer;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.actions.OpenAction;
import org.openide.cookies.LineCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOp;
import org.openide.text.Line;
import org.openide.text.Line.ShowOpenType;
import org.openide.text.Line.ShowVisibilityType;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author lahvac
 */
public class Nodes {
    
    public static Node constructSemiLogicalView(Map<Analyzer, List<ErrorDescription>> errors, boolean byCategory) {
        if (!byCategory) {
            return new AbstractNode(constructSemiLogicalViewChildren(sortErrors(errors, BY_FILE)));
        } else {
            Map<String, Map<Analyzer, List<ErrorDescription>>> byId = sortErrors(errors, BY_ID);
            List<Node> categoryNodes = new ArrayList<Node>(byId.size());

            for (Entry<String, Map<Analyzer, List<ErrorDescription>>> e : byId.entrySet()) {
                Analyzer analyzer = e.getValue().keySet().iterator().next();
                final Image icon = analyzer.getIcon();
                AbstractNode categoryNode = new AbstractNode(constructSemiLogicalViewChildren(sortErrors(e.getValue(), BY_FILE))) {
                    @Override public Image getIcon(int type) {
                        return icon;
                    }
                    @Override public Image getOpenedIcon(int type) {
                        return icon;
                    }
                };
                
                String categoryDisplayName = e.getKey() != null ? analyzer.getDisplayName4Id(e.getKey()) : null;

                categoryNode.setDisplayName(categoryDisplayName != null ? categoryDisplayName : "Unknown");

                categoryNodes.add(categoryNode);
            }

            Collections.sort(categoryNodes, new Comparator<Node>() {
                @Override public int compare(Node o1, Node o2) {
                    return o1.getDisplayName().compareTo(o2.getDisplayName());
                }
            });

            return new AbstractNode(new DirectChildren(categoryNodes));
        }
    }

    private static <A> Map<A, Map<Analyzer, List<ErrorDescription>>> sortErrors(Map<Analyzer, List<ErrorDescription>> errs, AttributeRetriever<A> attributeRetriever) {
        Map<A, Map<Analyzer, List<ErrorDescription>>> sorted = new HashMap<A, Map<Analyzer, List<ErrorDescription>>>();

        for (Entry<Analyzer, List<ErrorDescription>> e : errs.entrySet()) {
            for (ErrorDescription ed : e.getValue()) {
                A attribute = attributeRetriever.getAttribute(ed);
                Map<Analyzer, List<ErrorDescription>> errorsPerAttributeValue = sorted.get(attribute);

                if (errorsPerAttributeValue == null) {
                    sorted.put(attribute, errorsPerAttributeValue = new HashMap<Analyzer, List<ErrorDescription>>());
                }

                List<ErrorDescription> errors = errorsPerAttributeValue.get(e.getKey());

                if (errors == null) {
                    errorsPerAttributeValue.put(e.getKey(), errors = new ArrayList<ErrorDescription>());
                }

                errors.add(ed);
            }
        }

        return sorted;
    }

    private static interface AttributeRetriever<A> {
        public A getAttribute(ErrorDescription ed);
    }

    private static final AttributeRetriever<FileObject> BY_FILE = new AttributeRetriever<FileObject>() {
        @Override public FileObject getAttribute(ErrorDescription ed) {
            return ed.getFile();
        }
    };

    private static final AttributeRetriever<String> BY_ID = new AttributeRetriever<String>() {
        @Override public String getAttribute(ErrorDescription ed) {
            return ed.getId();
        }
    };

    private static Children constructSemiLogicalViewChildren(Map<FileObject, Map<Analyzer, List<ErrorDescription>>> errors) {
        Map<Project, Map<FileObject, Map<Analyzer, List<ErrorDescription>>>> projects = new HashMap<Project, Map<FileObject, Map<Analyzer, List<ErrorDescription>>>>();
        
        for (FileObject file : errors.keySet()) {
            Project project = FileOwnerQuery.getOwner(file);
            
            if (project == null) {
                Logger.getLogger(Nodes.class.getName()).log(Level.WARNING, "Cannot find project for: {0}", FileUtil.getFileDisplayName(file));
            }
            
            Map<FileObject, Map<Analyzer, List<ErrorDescription>>> projectErrors = projects.get(project);
            
            if (projectErrors == null) {
                projects.put(project, projectErrors = new HashMap<FileObject, Map<Analyzer, List<ErrorDescription>>>());
            }
            
            projectErrors.put(file, errors.get(file));
        }
        
        projects.remove(null);
        
        List<Node> nodes = new LinkedList<Node>();
        
        for (Project p : projects.keySet()) {
            nodes.add(constructSemiLogicalView(p, projects.get(p)));
        }
        
//        Children.Array subNodes = new Children.Array();
//        
//        subNodes.add(nodes.toArray(new Node[0]));
        
        return new DirectChildren(nodes);
    }
    
    private static Node constructSemiLogicalView(final Project p, Map<FileObject, Map<Analyzer, List<ErrorDescription>>> errors) {
        LogicalViewProvider lvp = p.getLookup().lookup(LogicalViewProvider.class);
        final Node view;
        
        if (lvp != null) {
            view = lvp.createLogicalView();
        } else {
            try {
                view = DataObject.find(p.getProjectDirectory()).getNodeDelegate();
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
                return new AbstractNode(Children.LEAF);
            }
        }
        
        Map<Node, Map<Analyzer, List<ErrorDescription>>> fileNodes = new HashMap<Node, Map<Analyzer, List<ErrorDescription>>>();
        
        for (FileObject file : errors.keySet()) {
            Map<Analyzer, List<ErrorDescription>> eds = errors.get(file);
            Node foundChild = locateChild(view, lvp, file);

            if (foundChild == null) {
                Node n = new AbstractNode(Children.LEAF) {
                    @Override
                    public Image getIcon(int type) {
                        return ImageUtilities.icon2Image(ProjectUtils.getInformation(p).getIcon());
                    }
                    @Override
                    public Image getOpenedIcon(int type) {
                        return getIcon(type);
                    }
                    @Override
                    public String getHtmlDisplayName() {
                        return view.getHtmlDisplayName() != null ? NbBundle.getMessage(Nodes.class, "ERR_ProjectNotSupported", view.getHtmlDisplayName()) : null;
                    }
                    @Override
                    public String getDisplayName() {
                        return NbBundle.getMessage(Nodes.class, "ERR_ProjectNotSupported", view.getDisplayName());
                    }
                };

                return n;
            }

            fileNodes.put(foundChild, eds);
            
        }
        
        return new Wrapper(view, fileNodes);
    }
    
    private static Node locateChild(Node parent, LogicalViewProvider lvp, FileObject file) {
        if (lvp != null) {
            return lvp.findPath(parent, file);
        }

        throw new UnsupportedOperationException("Not done yet");
    }

    private static class Wrapper extends FilterNode {

        public Wrapper(Node orig, Map<Node, Map<Analyzer, List<ErrorDescription>>> fileNodes) {
            super(orig, new WrapperChildren(orig, fileNodes), lookupForNode(orig, fileNodes));
        }
        
        public Wrapper(Node orig, Map<Analyzer, List<ErrorDescription>> errors, boolean file) {
            super(orig, new ErrorDescriptionChildren(errors), lookupForFileNode(orig, errors));
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[0];
        }

        }

    private static Lookup lookupForNode(Node n, Map<Node, Map<Analyzer, List<ErrorDescription>>> fileNodes) {
        return Lookups.fixed();
    }
    
    private static Lookup lookupForFileNode(Node n, Map<Analyzer, List<ErrorDescription>> errors) {
        return Lookups.fixed();
    }
    
    private static boolean isParent(Node parent, Node child) {
        if (NodeOp.isSon(parent, child)) {
            return true;
        }

        Node p = child.getParentNode();

        if (p == null) {
            return false;
        }

        return isParent(parent, p);
    }
        
    private static class WrapperChildren extends Children.Keys<Node> {

        private final Node orig;
        private final java.util.Map<Node, java.util.Map<Analyzer, List<ErrorDescription>>> fileNodes;

        public WrapperChildren(Node orig, java.util.Map<Node, java.util.Map<Analyzer, List<ErrorDescription>>> fileNodes) {
            this.orig = orig;
            this.fileNodes = fileNodes;
            
        }
        
        @Override
        protected void addNotify() {
            super.addNotify();
            doSetKeys();
        }

        private void doSetKeys() {
            Node[] nodes = orig.getChildren().getNodes(true);
            List<Node> toSet = new LinkedList<Node>();
            
            OUTER: for (Node n : nodes) {
                for (Node c : fileNodes.keySet()) {
                    if (n == c || isParent(n, c)) {
                        toSet.add(n);
                        continue OUTER;
                    }
                }
            }
            
            setKeys(toSet);
        }
        
        @Override
        protected Node[] createNodes(Node key) {
            if (fileNodes.containsKey(key)) {
                return new Node[] {new Wrapper(key, fileNodes.get(key), true)};
            }
            return new Node[] {new Wrapper(key, fileNodes)};
        }
        
    }
    
    private static final class DirectChildren extends Children.Keys<Node> {

        public DirectChildren(Collection<Node> nodes) {
            setKeys(nodes);
        }
        
        @Override
        protected Node[] createNodes(Node key) {
            return new Node[] {key};
        }
    }

    private static final class ErrorDescriptionChildren extends Children.Keys<ErrorDescription> {

        private final java.util.Map<ErrorDescription, Analyzer> error2Analyzer = new HashMap<ErrorDescription, Analyzer>();
        public ErrorDescriptionChildren(java.util.Map<Analyzer, List<ErrorDescription>> errors) {
            List<ErrorDescription> eds = new ArrayList<ErrorDescription>();

            for (Entry<Analyzer, List<ErrorDescription>> e : errors.entrySet()) {
                for (ErrorDescription ed : e.getValue()) {
                    error2Analyzer.put(ed, e.getKey());
                    eds.add(ed);
                }
            }

            Collections.sort(eds, new Comparator<ErrorDescription>() {
                @Override public int compare(ErrorDescription o1, ErrorDescription o2) {
                    try {
                        return o1.getRange().getBegin().getLine() - o2.getRange().getBegin().getLine();
                    } catch (IOException ex) {
                        throw new IllegalStateException(ex); //XXX
                    }
                }
            });

            setKeys(eds);
        }

        @Override
        protected Node[] createNodes(ErrorDescription key) {
            return new Node[] {new ErrorDescriptionNode(error2Analyzer.get(key), key)};
        }

    }

    private static final class ErrorDescriptionNode extends AbstractNode {

        private final Image icon;

        public ErrorDescriptionNode(Analyzer provider, ErrorDescription ed) {
            super(Children.LEAF, Lookups.fixed(ed, new OpenErrorDescription(ed)));
            int line = -1;
            try {
                line = ed.getRange().getBegin().getLine();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            setDisplayName((line != (-1) ? (line + ":") : "") + ed.getDescription());
            icon = provider.getIcon();
        }

        @Override
        public Action getPreferredAction() {
            return OpenAction.get(OpenAction.class);
        }

        @Override
        public Image getIcon(int type) {
            return icon;
        }

    }

    private static final class OpenErrorDescription implements OpenCookie {

        private final ErrorDescription ed;

        public OpenErrorDescription(ErrorDescription ed) {
            this.ed = ed;
        }

        @Override
        public void open() {
            openErrorDescription(ed);
        }
        
    }

    static void openErrorDescription(ErrorDescription ed) throws IndexOutOfBoundsException {
        try {
            DataObject od = DataObject.find(ed.getFile());
            LineCookie lc = od.getLookup().lookup(LineCookie.class);

            if (lc != null) {
                Line line = lc.getLineSet().getCurrent(ed.getRange().getBegin().getLine());

                line.show(ShowOpenType.OPEN, ShowVisibilityType.FOCUS);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
