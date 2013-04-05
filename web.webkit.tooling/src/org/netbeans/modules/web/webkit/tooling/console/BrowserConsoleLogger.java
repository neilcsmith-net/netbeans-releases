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

package org.netbeans.modules.web.webkit.tooling.console;

import java.awt.Color;
import java.awt.SystemColor;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.common.api.RemoteFileCache;
import org.netbeans.modules.web.common.api.ServerURLMapping;
import org.netbeans.modules.web.webkit.debugging.api.console.Console;
import org.netbeans.modules.web.webkit.debugging.api.console.ConsoleMessage;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.IOColorPrint;
import org.openide.windows.IOColors;
import org.openide.windows.IOContainer;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;

/**
 *
 */
public class BrowserConsoleLogger implements Console.Listener {
    
    private static final String LEVEL_ERROR = "error";      // NOI18N
    private static final String LEVEL_DEBUG = "debug";      // NOI18N

    private Lookup projectContext;
    private InputOutput io;
    private Color colorStdBrighter;
    /** The last logged message. */
    private ConsoleMessage lastMessage;
    //private Color colorErrBrighter;
    private final AtomicBoolean shownOnError = new AtomicBoolean(false);

    private static final Logger LOG = Logger.getLogger(BrowserConsoleLogger.class.getName());

    public BrowserConsoleLogger(Lookup projectContext) {
        this.projectContext = projectContext;
        initIO();
    }
    
    @NbBundle.Messages({"BrowserConsoleLoggerTitle=Browser Log"})
    private void initIO() {
        io = IOProvider.getDefault().getIO(Bundle.BrowserConsoleLoggerTitle(), false);
        if (IOColors.isSupported(io) && IOColorPrint.isSupported(io)) {
            Color colorStd = IOColors.getColor(io, IOColors.OutputType.OUTPUT);
            //Color colorErr = IOColors.getColor(io, IOColors.OutputType.ERROR);
            Color background = UIManager.getDefaults().getColor("nb.output.background");    // NOI18N
            if (background == null) {
                background = SystemColor.window;
            }
            colorStdBrighter = shiftTowards(colorStd, background);
            //colorErrBrighter = shiftTowards(colorErr, background);
        }
    }

    public void close() {
        io.getErr().close();
        io.getOut().close();
    }

    private static Color shiftTowards(Color c, Color b) {
        return new Color((c.getRed() + b.getRed())/2, (c.getGreen() + b.getGreen())/2, (c.getBlue() + b.getBlue())/2);
    }
    
    @Override
    public void messageAdded(ConsoleMessage message) {
        try {
            lastMessage = message;
            logMessage(message);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void messagesCleared() {
        try {
            io.getOut().reset();
        } catch (IOException ex) {}
    }

    @Override
    public void messageRepeatCountUpdated(int count) {
        try {
            logMessage(lastMessage);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
    public static String getCurrentTime() {
        return formatter.format(new Date(System.currentTimeMillis()));
    }
    
    private void logMessage(ConsoleMessage msg) throws IOException {
        String level = msg.getLevel();
        boolean isErr = LEVEL_ERROR.equals(level);
        String time = getCurrentTime();

        Project project = projectContext.lookup(Project.class);
        
        String logInfo = createLogInfo(time, level, msg.getSource(), msg.getType());
        OutputWriter ow = isErr ? io.getErr() : io.getOut();
        String lines[] = msg.getText().replace("\r", "").split("\n");
        for (int i = 0; i < lines.length; i++) {
            String singleMessageLine = lines[i];
            if (colorStdBrighter == null && i == lines.length-1) {
                singleMessageLine += logInfo;
            }
            Object res[] = tryToConvertLineToHyperlink(project, singleMessageLine);
            MyListener l = null;
            String newMessage1 = null;
            String newMessage2 = null;
            if (res != null) {
                l = (MyListener)res[0];
                newMessage1 = (String)res[1];
                newMessage2 = (String)res[2];
            }
            if (l != null && l.isValidHyperlink()) {
                if (colorStdBrighter != null && i == lines.length-1) {
                    newMessage2 += logInfo;
                }
                ow.print(newMessage1);
                ow.println(newMessage2, l);
            } else {
                ow.print(singleMessageLine);
                if (colorStdBrighter != null && i == lines.length-1) {
                    //if (isErr) {
                    //    IOColorPrint.print(io, logInfo, colorErrBrighter);
                    //} else {
                        IOColorPrint.print(io, logInfo, colorStdBrighter);
                    //}
                } else {
                    ow.println("");
                }
            }
        }
        
        boolean doPrintStackTrace = LEVEL_ERROR.equals(level) ||
                                    LEVEL_DEBUG.equals(level);
        
        StringBuilder sb;
        boolean first = true;
        if (doPrintStackTrace && msg.getStackTrace() != null) {
            for (ConsoleMessage.StackFrame sf : msg.getStackTrace()) {
                String indent;
                if (first) {
                    indent = "    at ";
                    first = false;
                } else {
                    indent = "    at ";
                }
                ow.print(indent);
                ow.print(sf.getFunctionName());
                sb = new StringBuilder();
                
                String urlStr = sf.getURLString();
                urlStr = getProjectPath(project, urlStr);
                sb.append(" ("+urlStr+":"+sf.getLine()+":"+sf.getColumn()+")");
                MyListener l = new MyListener(project, sf.getURLString(), sf.getLine(), sf.getColumn());
                if (l.isValidHyperlink()) {
                    ow.println(sb.toString(), l);
                } else {
                    ow.println(sb.toString());
                }
            }
        }
        if (first && msg.getURLString() != null && msg.getURLString().length() > 0) {
            ow.print("  at ");
            String url = msg.getURLString();
            String file = getProjectPath(project, url);
            sb = new StringBuilder(file);
            int line = msg.getLine();
            if (line != -1 && line != 0) {
                sb.append(":");
                sb.append(line);
            }        
            MyListener l = new MyListener(project, url, line, -1);
            if (l.isValidHyperlink()) {
                ow.println(sb.toString(), l);
            } else {
                ow.println(sb.toString());
            }
        }
        if (io.isClosed() || (isErr && !shownOnError.getAndSet(true))) {
            io.select();
        }
    }
    
    // XXX: exact this algorithm is also in 
    // javascript.jstestdriver/src/org/netbeans/modules/javascript/jstestdriver/JSTestDriverSupport.java
    // keep them in sync
    private Object[] tryToConvertLineToHyperlink(Project project, String line) {
        // pattern is "at ...... (file:line:column)"
        // file can be also http:// url
        if (!line.endsWith(")")) {
            return tryToConvertLineURLToHyperlink(project, line);
        }
        int start = line.lastIndexOf('(');
        if (start == -1) {
            return null;
        }
        int lineNumberEnd = line.lastIndexOf(':');
        if (lineNumberEnd == -1) {
            return null;
        }
        int fileEnd = line.lastIndexOf(':', lineNumberEnd-1);
        if (fileEnd == -1) {
            return null;
        }
        if (start >= fileEnd) {
            return null;
        }
        int lineNumber = -1;
        int columnNumber = -1;
        try {
            lineNumber = Integer.parseInt(line.substring(fileEnd+1, lineNumberEnd));
            columnNumber = Integer.parseInt(line.substring(lineNumberEnd+1, line.length()-1));
        } catch (NumberFormatException e) {
            //ignore
        }
        if (columnNumber != -1 && lineNumber == -1) {
            // perhaps stack trace had only line number:
            lineNumber = columnNumber;
        }
        if (lineNumber == -1) {
            return null;
        }
        String file = line.substring(start+1, fileEnd);
        if (file.length() == 0) {
            return null;
        }
        String s1 = line.substring(0, start);
        String s2 = "(" +  // NOI18N
                getProjectPath(project, file) +
            line.substring(fileEnd, line.length());
        MyListener l = new MyListener(project, file, lineNumber, columnNumber);
        return new Object[]{l,s1,s2};
    }
    
    private Object[] tryToConvertLineURLToHyperlink(Project project, String line) {
        int u1 = line.indexOf("http://");   // NOI18N
        if (u1 < 0) {
            u1 = line.indexOf("https://");  // NOI18N
        }
        if (u1 < 0) {
            return null;
        }
        int ue = line.indexOf(' ', u1);
        if (ue < 0) {
            ue = line.length();
        }
        int col2 = line.lastIndexOf(':', ue);
        if (col2 < 0) {
            return null;
        }
        int col1 = line.lastIndexOf(':', col2 - 1);
        if (col1 < 0) {
            return null;
        }
        int lineNumber = -1;
        int columnNumber = -1;
        try {
            lineNumber = Integer.parseInt(line.substring(col1+1, col2));
            columnNumber = Integer.parseInt(line.substring(col2+1, ue));
        } catch (NumberFormatException e) {
            //ignore
        }
        if (columnNumber != -1 && lineNumber == -1) {
            // perhaps stack trace had only line number:
            lineNumber = columnNumber;
        }
        if (lineNumber == -1) {
            return null;
        }
        String file = line.substring(u1, col1);
        if (file.length() == 0) {
            return null;
        }
        String s1 = line.substring(0, u1);
        String s2 = line.substring(u1, line.length());
        MyListener l = new MyListener(project, file, lineNumber, columnNumber);
        return new Object[]{l,s1,s2};
    }
    
    
    private static final String LOG_IGNORED = "log";    // NOI18N
    private static final String CONSOLE_API = "console-api";    // NOI18N
    private static final String TIME_SEPARATOR = " | "; // NOI18N
    private static String createLogInfo(String time, String level, String source, String type) {
        //String logInfo = " ("+time+" | "+level+","+msg.getSource()+","+msg.getType()+")\n";
        StringBuilder logInfoBuilder = new StringBuilder(" (");
        logInfoBuilder.append(time);
        boolean separator = false;
        if (!LOG_IGNORED.equals(level)) {
            separator = true;
            logInfoBuilder.append(TIME_SEPARATOR);
            logInfoBuilder.append(level);
        }
        if (!CONSOLE_API.equals(source)) {
            if (separator) {
                logInfoBuilder.append(", ");
            } else {
                logInfoBuilder.append(TIME_SEPARATOR);
            }
            logInfoBuilder.append(source);
        }
        if (!LOG_IGNORED.equals(type)) {
            if (separator) {
                logInfoBuilder.append(", ");
            } else {
                logInfoBuilder.append(TIME_SEPARATOR);
            }
            logInfoBuilder.append(type);
        }
        logInfoBuilder.append(")\n");
        return logInfoBuilder.toString();
    }
    
    /**
     * Try to find a more readable project-relative path.<p>
     * E.g.: "http://localhost:89/SimpleLiveHTMLTest/js/app.js:8:9"
     * is turned into: "js/app.js:8:9"
     * @param urlStr The URL
     * @return a project-relative path, or the original URL.
     */
    public static String getProjectPath(Project project, String urlStr) {
        try {
            URL url = new URL(urlStr);
            if (project != null) {
                FileObject fo = ServerURLMapping.fromServer(project, url);
                if (fo != null) {
                    String relPath = FileUtil.getRelativePath(project.getProjectDirectory(), fo);
                    if (relPath != null) {
                        urlStr = relPath;
                    }
                }
            }
        } catch (MalformedURLException murl) {}
        return urlStr;
    }

    public static class MyListener implements OutputListener {

        private String url;
        private int line;
        private int column;
        private Project project;

        public MyListener(Project project, String url, int line, int column) {
            this.url = url;
            this.line = line;
            this.column = column;
            this.project = project;
        }
        
        @Override
        public void outputLineSelected(OutputEvent ev) {
        }

        @Override
        public void outputLineAction(OutputEvent ev) {
            Line l = getLine();
            if (l != null) {
                l.show(Line.ShowOpenType.OPEN, 
                    Line.ShowVisibilityType.FOCUS, column != -1 ? column -1 : -1);
            }
        }
        private Line getLine() {
            return BrowserConsoleLogger.getLine(project, url, line-1);
        }

        @Override
        public void outputLineCleared(OutputEvent ev) {
        }
        
        public boolean isValidHyperlink() {
            return getLine() != null;
        }
    
    }

    private static Line getLine(Project project, final String filePath, final int lineNumber) {
        if (filePath == null || lineNumber < 0) {
            return null;
        }

        FileObject fileObject = null;
        if (filePath.startsWith("http:") || filePath.startsWith("https:")) {    // NOI18N
            try {
                URL url = URI.create(filePath).toURL();
                if (project != null) {
                    fileObject = ServerURLMapping.fromServer(project, url);
                }
                if (fileObject == null) {
                    fileObject = RemoteFileCache.getRemoteFile(url);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            File file;
            if (filePath.startsWith("file:/")) {                                // NOI18N
                file = Utilities.toFile(URI.create(filePath));
            } else {
                file = new File(filePath);
            }
            fileObject = FileUtil.toFileObject(FileUtil.normalizeFile(file));
        }
        if (fileObject == null) {
            LOG.log(Level.FINE, "Cannot resolve \"{0}\"", filePath);
            return null;
        }

        LineCookie lineCookie = getLineCookie(fileObject);
        if (lineCookie == null) {
            LOG.log(Level.INFO, "No line cookie for \"{0}\"", fileObject);
            return null;
        }
        try {
            return lineCookie.getLineSet().getCurrent(lineNumber);
        } catch (IndexOutOfBoundsException ioob) {
            List<? extends Line> lines = lineCookie.getLineSet().getLines();
            if (lines.size() > 0) {
                return lines.get(lines.size() - 1);
            } else {
                return null;
            }
        }
    }

    public static LineCookie getLineCookie(final FileObject fo) {
        LineCookie result = null;
        try {
            DataObject dataObject = DataObject.find(fo);
            if (dataObject != null) {
                result = dataObject.getLookup().lookup(LineCookie.class);
            }
        } catch (DataObjectNotFoundException e) {
            Exceptions.printStackTrace(Exceptions.attachSeverity(e, Level.INFO));
        }
        return result;
    }
}
