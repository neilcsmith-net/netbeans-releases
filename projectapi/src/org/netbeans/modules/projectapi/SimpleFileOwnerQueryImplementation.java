/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.projectapi;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.FileOwnerQueryImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.URLMapper;
import org.openide.util.Utilities;
import org.openide.util.WeakSet;

/**
 * Finds a project by searching the directory tree.
 * @author Jesse Glick
 */
public class SimpleFileOwnerQueryImplementation implements FileOwnerQueryImplementation {
    
    /** Do nothing */
    public SimpleFileOwnerQueryImplementation() {}
    
    public Project getOwner(URI fileURI) {
        // Try to find a FileObject for it.
        URI test = fileURI;
        FileObject file;
        do {
            file = uri2FileObject(test);
            test = goUp(test);
        } while (file == null && test != null);
        if (file == null) {
            return null;
        }
        return getOwner(file);
    }
    
    private final Set<FileObject> warnedAboutBrokenProjects = new WeakSet<FileObject>();
        
    public Project getOwner(FileObject f) {
        while (f != null) {
            if (f.isFolder()) {
                Project p;
                try {
                    p = ProjectManager.getDefault().findProject(f);
                } catch (IOException e) {
                    // There is a project here, but we cannot load it...
                    if (warnedAboutBrokenProjects.add(f)) { // #60416
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                    }
                    return null;
                }
                if (p != null) {
                    return p;
                }
            }
            
            if (!externalOwners.isEmpty()) {
                Reference<FileObject> externalOwnersReference = externalOwners.get(fileObject2URI(f));

                if (externalOwnersReference != null) {
                    FileObject externalOwner = externalOwnersReference.get();

                    if (externalOwner != null) {
                        try {
                            // Note: will be null if there is no such project.
                            return ProjectManager.getDefault().findProject(externalOwner);
                        } catch (IOException e) {
                            // There is a project there, but we cannot load it...
                            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                            return null;
                        }
                    }
                }
            }
            f = f.getParent();
        }
        return null;
    }
    
    /**
     * Map from external source roots to the owning project directories.
     */
    private static final Map<URI,Reference<FileObject>> externalOwners =
        Collections.synchronizedMap(new WeakHashMap<URI,Reference<FileObject>>());
    private static final Map<FileObject,Collection<URI>> project2External =
        Collections.synchronizedMap(new WeakHashMap<FileObject,Collection<URI>>());
    
    /** @see FileOwnerQuery#reset */
    public static void reset() {
        externalOwners.clear();
    }
    
    private static URI fileObject2URI(FileObject f) {
        try {
            return URI.create(f.getURL().toString());
        } catch (FileStateInvalidException e) {
            throw (IllegalArgumentException) new IllegalArgumentException(e.toString()).initCause(e);
        }
    }
    
    /** @see FileOwnerQuery#markExternalOwner */
    public static void markExternalOwnerTransient(FileObject root, Project owner) {
        markExternalOwnerTransient(fileObject2URI(root), owner);
    }
    
    /** @see FileOwnerQuery#markExternalOwner */
    public static void markExternalOwnerTransient(URI root, Project owner) {
        if (owner != null) {
            externalOwners.put(root, new WeakReference<FileObject>(owner.getProjectDirectory()));            
            synchronized (project2External) {
                FileObject prjDir = owner.getProjectDirectory();
                Collection<URI> roots = project2External.get (prjDir);
                if (roots == null) {
                    roots = new LinkedList<URI>();
                    project2External.put(prjDir, roots);
                }
                roots.add (root);                
            }
        } else {
            Reference<FileObject> ownerReference = externalOwners.remove(root);
            
            if (ownerReference != null) {
                FileObject ownerFO = ownerReference.get();
                
                if (ownerFO != null) {
                    synchronized (project2External) {
                        Collection<URI> roots = project2External.get(ownerFO);
                        if (roots != null) {
                            roots.remove(root);
                            if (roots.size() == 0) {
                                project2External.remove(ownerFO);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static FileObject uri2FileObject(URI u) {
        URL url;
        try {
            url = u.toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            assert false : u;
            return null;
        }
        return URLMapper.findFileObject(url);
    }
    
    private static URI goUp(URI u) {
        assert u.isAbsolute() : u;
        assert u.getFragment() == null : u;
        assert u.getQuery() == null : u;
        // XXX isn't there any easier way to do this?
        // Using getPath in the new path does not work; nbfs: URLs break. (#39613)
        // On the other hand, nbfs: URLs are not really used any more, so do we care?
        String path = u.getPath();
        if (path == null || path.equals("/")) { // NOI18N
            return null;
        }
        String us = u.toString();
        if (us.endsWith("/")) { // NOI18N
            us = us.substring(0, us.length() - 1);
            assert path.endsWith("/"); // NOI18N
            path = path.substring(0, path.length() - 1);
        }
        int idx = us.lastIndexOf('/');
        assert idx != -1 : path;
        if (path.lastIndexOf('/') == 0) {
            us = us.substring(0, idx + 1);
        } else {
            us = us.substring(0, idx);
        }
        URI nue;
        try {
            nue = new URI(us);
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
        if (Utilities.isWindows()) {
            String pth = nue.getPath();
            // check that path is not "/C:" or "/"
            if ((pth.length() == 3 && pth.endsWith(":")) ||
                (pth.length() == 1 && pth.endsWith("/"))) {
                return null;
            }
        }
        assert nue.isAbsolute() : nue;
        assert u.toString().startsWith(nue.toString()) : "not a parent: " + nue + " of " + u;
        return nue;
    }
    
}
