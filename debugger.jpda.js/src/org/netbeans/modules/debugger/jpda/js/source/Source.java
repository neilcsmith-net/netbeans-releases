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

package org.netbeans.modules.debugger.jpda.js.source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.debugger.jpda.CallStackFrame;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.JPDAClassType;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.ObjectVariable;
import org.netbeans.api.debugger.jpda.Variable;
import org.netbeans.modules.debugger.jpda.js.vars.DebuggerSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;

/**
 *
 * @author Martin
 */
public final class Source {
    
    private static final Logger LOG = Logger.getLogger(Source.class.getName());
    
    public static final String URL_PROTOCOL = "js-scripts"; // NOI18N
    
    private static final String SOURCE_CLASS = "jdk.nashorn.internal.runtime.Source";   // NOI18N
    private static final String SOURCE_FIELD = "source";    // NOI18N
    
    private static final String SOURCE_VAR_NAME = "name";   // NOI18N
    private static final String SOURCE_VAR_CONTENT = "content"; // NOI18N
    private static final String SOURCE_VAR_HASH = "hash";   // NOI18N
    private static final String SOURCE_VAR_URL = "url";     // NOI18N
    private static final String SOURCE_VAR_DATA = "data";   // NOI18N
    private static final String SOURCE_VAR_DATA_ARRAY = "array";    // NOI18N
    
    private static final Map<JPDADebugger, Map<Long, Source>> knownSources = new WeakHashMap<>();

    private final String name;
    private final JPDAClassType classType;
    private final URL url;          // The original file source
    private final URL runtimeURL;   // The current content in runtime, or null when equal to 'url'
    private final int contentLineShift; // Line shift of 'url' content in 'runtimeURL'. Can not be negative.
    private final int hash;
    private final String content;
    
    private Source(String name, JPDAClassType classType, URL url, boolean compareContent, int hash, String content) {
        this.name = name;
        this.classType = classType;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("new Source("+name+", "+classType.getName()+", "+url+")");
        }
        URL rURL = null;
        int lineShift = 0;
        if (url == null || !("file".equalsIgnoreCase(url.getProtocol()) ||
                             "jar".equalsIgnoreCase(url.getProtocol()))) {
            try {
                url = SourceFilesCache.getDefault().getSourceFile(name, hash, content);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else if (compareContent) {
            lineShift = getContentLineShift(url, content);
            if (lineShift > 0) {
                try {
                    rURL = SourceFilesCache.getDefault().getSourceFile(name, hash, content);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                lineShift = 0;
            }
        }
        this.url = url;
        this.runtimeURL = rURL;
        this.contentLineShift = lineShift;
        this.hash = hash;
        this.content = content;
    }
    
    public static Source getSource(CallStackFrame frame) {
        if (frame == null) {
            return null;
        }
        JPDAClassType classType;
        //classType = currentFrame.getClassType();
        try {
            classType = (JPDAClassType) frame.getClass().getMethod("getClassType").invoke(frame);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
        return getSource(classType);
    }
    
    public static Source getSource(JPDAClassType classType) {
        long uniqueClassID = classType.classObject().getUniqueID();
        //System.err.println("getSource("+classType+" = "+className+"): classType object's ID = "+uniqueClassID);
        JPDADebugger debugger;
        try {
            java.lang.reflect.Field debuggerField = classType.getClass().getDeclaredField("debugger");
            debuggerField.setAccessible(true);
            debugger = (JPDADebugger) debuggerField.get(classType);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
        synchronized (knownSources) {
            Map<Long, Source> dbgSources = knownSources.get(debugger);
            if (dbgSources != null) {
                Source src = dbgSources.get(uniqueClassID);
                if (src != null) {
                    return src;
                }
            }
        }
        
        ObjectVariable sourceVar = null;
        if (DebuggerSupport.hasSourceInfo(debugger)) {
            sourceVar = (ObjectVariable) DebuggerSupport.getSourceInfo(debugger, classType);
            LOG.log(Level.FINE, "Source info for class {0} is {1}", new Object[]{classType, sourceVar});
        }
        if (sourceVar == null) {
            sourceVar = getSourceVar(debugger, classType);
            LOG.log(Level.FINE, "Source var for class {0} is {1}", new Object[]{classType, sourceVar});
            if (sourceVar == null) {
                return null;
            }
        }
        return getSource(debugger, classType, sourceVar);
    }
    
    public static Source getSource(JPDADebugger debugger, JPDAClassType classType, ObjectVariable sourceVar) {
        long uniqueClassID = classType.classObject().getUniqueID();
        Field fieldName = sourceVar.getField(SOURCE_VAR_NAME);
        Field fieldContent = sourceVar.getField(SOURCE_VAR_CONTENT);
        Field fieldHash = sourceVar.getField(SOURCE_VAR_HASH);
        Field fieldURL = sourceVar.getField(SOURCE_VAR_URL);
        if (fieldContent == null && fieldURL == null) {
            // There is a Data inner class instead:
            Field fieldData = sourceVar.getField(SOURCE_VAR_DATA);
            if (fieldData != null && fieldData instanceof ObjectVariable) {
                fieldURL = ((ObjectVariable) fieldData).getField(SOURCE_VAR_URL);
                fieldContent = ((ObjectVariable) fieldData).getField(SOURCE_VAR_DATA_ARRAY);
            }
        }
        if (fieldName == null || fieldContent == null || fieldHash == null) {
            return null;
        }
        Object urlObj = fieldURL != null ? fieldURL.createMirrorObject() : null;
        URL url;
        boolean compareContent = false;
        if (urlObj == null && fieldURL != null) {
            // Check if there's a special URL handler. In that case we have to count with content shifting.
            url = readURLFromFields(fieldURL);
            compareContent = true;
        } else {
            url = (URL) urlObj;
        }
        Object hashMirror = fieldHash.createMirrorObject();
        if (!(hashMirror instanceof Integer)) {
            return null;
        }
        int hash = ((Integer) hashMirror).intValue();
        Object contentMirror = fieldContent.createMirrorObject();
        if (!(contentMirror instanceof char[])) {
            return null;
        }
        String content = new String((char[]) contentMirror);
        String name = fieldName.getValue();
        if (name.startsWith("\"") && name.endsWith("\"")) {
            name = name.substring(1, name.length() - 1);
        }
        if (!name.endsWith(".js") && !name.endsWith(".JS")) {
            name = name + ".js";
        }
        Source src = new Source(name, classType, url, compareContent, hash, content);
        synchronized (knownSources) {
            Map<Long, Source> dbgSources = knownSources.get(debugger);
            if (dbgSources == null) {
                dbgSources = new HashMap<>();
                knownSources.put(debugger, dbgSources);
            }
            dbgSources.put(uniqueClassID, src);
        }
        return src;
    }
    
    private static ObjectVariable getSourceVar(JPDADebugger debugger, JPDAClassType classType) {
        List<Field> staticFields = classType.staticFields();
        for (Field sf : staticFields) {
            if (sf instanceof ObjectVariable &&
                SOURCE_FIELD.equals(sf.getName()) &&
                SOURCE_CLASS.equals(sf.getType())) {
                
                return (ObjectVariable) sf;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
    
    public JPDAClassType getClassType() {
        return classType;
    }

    public URL getUrl() {
        return url;
    }
    
    public URL getRuntimeURL() {
        return runtimeURL;
    }
    
    public int getContentLineShift() {
        return contentLineShift;
    }

    public int getHash() {
        return hash;
    }

    public String getContent() {
        return content;
    }
    
    private static URL readURLFromFields(Field fieldURL) {
        if (!(fieldURL instanceof ObjectVariable)) {
            return null;
        }
        ObjectVariable urlObj = (ObjectVariable) fieldURL;
        Field protocolField = urlObj.getField("protocol");      // NOI18N
        Field authorityField = urlObj.getField("authority");    // NOI18N
        Field pathField = urlObj.getField("path");              // NOI18N
        if (protocolField == null || authorityField == null || pathField == null) {
            return null;
        }
        String protocol = stripQuotes(protocolField.getValue());
        String authority = stripQuotes(authorityField.getValue());
        String path = stripQuotes(pathField.getValue());
        StringBuilder result = new StringBuilder();
        result.append(protocol);
        result.append(":");
        if (authority != null && authority.length() > 0) {
            result.append("//");
            result.append(authority);
        }
        if (path != null) {
            result.append(path);
        }
        try {
            return new URL(result.toString());
        } catch (MalformedURLException ex) {
            return null;
        }
    }
    
    private static String stripQuotes(String str) {
        if ("null".equals(str)) {
            str = null;
        }
        if (str != null && str.startsWith("\"") && str.endsWith("\"")) {
            str = str.substring(1, str.length() - 1);
        }
        return str;
    }

    /**
     * 
     * @param url
     * @param content
     * @return a non-negative line shift of content of 'url' in 'content', or -1
     *         when content of 'url' is not a subset of 'content'.
     */
    static int getContentLineShift(URL url, String content) {
        String origContent;
        FileObject fo = URLMapper.findFileObject(url);
        if (fo != null) {
            try {
                origContent = fo.asText();
            } catch (IOException ex) {
                return 0;
            }
        } else {
            return 0;
        }
        int index = content.indexOf(origContent);
        if (index < 0) {
            return -1;
        }
        String prep = content.substring(0, index);
        return countNewLines(prep);
    }

    private static int countNewLines(String prep) {
        String nl = "\n";
        int c = 0;
        int index = 0;
        while ((index = prep.indexOf(nl, index)) >= 0) {
            c++;
            index++;
        }
        return c;
    }
    
}
