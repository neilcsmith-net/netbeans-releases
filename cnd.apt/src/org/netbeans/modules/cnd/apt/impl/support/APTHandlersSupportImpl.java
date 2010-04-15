/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.apt.impl.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.cnd.apt.impl.support.APTBaseMacroMap.StateImpl;
import org.netbeans.modules.cnd.apt.impl.support.APTFileMacroMap.FileStateImpl;
import org.netbeans.modules.cnd.apt.support.APTHandlersSupport.StateKey;
import org.netbeans.modules.cnd.apt.support.APTIncludeHandler;
import org.netbeans.modules.cnd.apt.support.APTMacro;
import org.netbeans.modules.cnd.apt.support.APTMacroMap;
import org.netbeans.modules.cnd.apt.support.APTPreprocHandler;
import org.netbeans.modules.cnd.apt.support.IncludeDirEntry;
import org.netbeans.modules.cnd.apt.support.StartEntry;
import org.netbeans.modules.cnd.utils.cache.CndFileUtils;

/**
 * utilities for working with APT states (macro-state, include-state, preproc-state)
 * @author Vladimir Voskresensky
 */
public class APTHandlersSupportImpl {

    /** Prevents creation of an instance of APTHandlersSupportImpl */
    private APTHandlersSupportImpl() {
    }

    public static APTPreprocHandler createPreprocHandler(APTMacroMap macroMap, APTIncludeHandler inclHandler, boolean compileContext) {
        return new APTPreprocHandlerImpl(macroMap, inclHandler, compileContext);
    }

    public static APTPreprocHandler createEmptyPreprocHandler(StartEntry file) {
        return new APTPreprocHandlerImpl(new APTFileMacroMap(), new APTIncludeHandlerImpl(file), false);
    }

    public static void invalidatePreprocHandler(APTPreprocHandler preprocHandler) {
        ((APTPreprocHandlerImpl)preprocHandler).setValid(false);
    }
    
    public static APTIncludeHandler createIncludeHandler(StartEntry startFile, List<IncludeDirEntry> sysIncludePaths, List<IncludeDirEntry> userIncludePaths) {
        // user paths could contain "-include file" elements
        List<IncludeDirEntry> fileEntries = new ArrayList<IncludeDirEntry>(0);
        for (IncludeDirEntry includeDirEntry : userIncludePaths) {
            if (!includeDirEntry.isExistingDirectory()) {
                // check if this is file
                if (CndFileUtils.isExistingFile(includeDirEntry.getFile(), includeDirEntry.getAsSharedCharSequence().toString())) {
                    fileEntries.add(includeDirEntry);
                }
            }
        }
        return new APTIncludeHandlerImpl(startFile, sysIncludePaths, userIncludePaths, fileEntries);
    }

    public static APTMacroMap createMacroMap(APTMacroMap sysMap, List<String> userMacros) {
        APTMacroMap fileMap = new APTFileMacroMap(sysMap, userMacros);
        return fileMap;
    }

    public static APTPreprocHandler.State createCleanPreprocState(APTPreprocHandler.State orig) {
        return ((APTPreprocHandlerImpl.StateImpl)orig).copyCleaned();
    }
    
    public static APTPreprocHandler.State createInvalidPreprocState(APTPreprocHandler.State orig) {
        return ((APTPreprocHandlerImpl.StateImpl)orig).copyInvalid();
    }

    public static boolean isFirstLevel(APTIncludeHandler includeHandler) {
        if (includeHandler != null) {
            return ((APTIncludeHandlerImpl) includeHandler).isFirstLevel();
        } else {
            return false;
        }
    }

    public static Collection<IncludeDirEntry> extractIncludeFileEntries(APTIncludeHandler includeHandler) {
        Collection<IncludeDirEntry> out = new ArrayList<IncludeDirEntry>(0);
        if (includeHandler != null) {
            return ((APTIncludeHandlerImpl) includeHandler).getUserIncludeFilePaths();
        }
        return out;
    }

    public static Map<CharSequence, APTMacro> extractMacroMap(APTPreprocHandler.State state){
        assert state != null;
        APTBaseMacroMap.StateImpl macro = (StateImpl) ((APTPreprocHandlerImpl.StateImpl)state).macroState;
        return APTMacroMapSnapshot.addAllMacros(macro.snap, null);
    }

    public static APTBaseMacroMap.State extractMacroMapState(APTPreprocHandler.State state){
        assert state != null;
        return (StateImpl) ((APTPreprocHandlerImpl.StateImpl)state).macroState;
    }

    public static StateKey getMacroMapID(APTPreprocHandler.State state){
        assert state != null;
        APTFileMacroMap.FileStateImpl macro = (FileStateImpl) ((APTPreprocHandlerImpl.StateImpl)state).macroState;
        return macro.getStateKey();
    }

    public static boolean isEmptyActiveMacroMap(APTPreprocHandler.State state) {
        assert state != null;
        APTFileMacroMap.FileStateImpl macro = (FileStateImpl) ((APTPreprocHandlerImpl.StateImpl) state).macroState;
        return macro.isEmptyActiveMacroMap();
    }

    public static int getMacroSize(APTPreprocHandler.State state) {
        assert state != null;
        APTBaseMacroMap.StateImpl macro = (StateImpl) ((APTPreprocHandlerImpl.StateImpl)state).macroState;
        return APTMacroMapSnapshot.getMacroSize(macro.snap);
    }

    public static int getIncludeStackDepth(APTPreprocHandler.State state) {
        assert state != null;
        APTIncludeHandlerImpl.StateImpl incl = (APTIncludeHandlerImpl.StateImpl) ((APTPreprocHandlerImpl.StateImpl) state).inclState;
        return incl == null ? 0 : incl.getIncludeStackDepth();
    }

    private static final LinkedList<APTIncludeHandler.IncludeInfo> EMPLY_LINKED_LIST = new LinkedList<APTIncludeHandler.IncludeInfo>();

    public static LinkedList<APTIncludeHandler.IncludeInfo> extractIncludeStack(APTPreprocHandler.State state) {
        assert state != null;
        List<APTIncludeHandler.IncludeInfo> inclStack = getIncludeStack(((APTPreprocHandlerImpl.StateImpl)state).inclState);
        return inclStack == null ? EMPLY_LINKED_LIST : new LinkedList<APTIncludeHandler.IncludeInfo>(inclStack);
    }

    public static StartEntry extractStartEntry(APTPreprocHandler.State state) {
        return (state == null) ? null : extractStartEntry(((APTPreprocHandlerImpl.StateImpl)state).inclState);
    }
    
//    public static APTPreprocHandler.State copyPreprocState(APTPreprocHandler.State orig) {
//        return ((APTPreprocHandlerImpl.StateImpl)orig).copy();
//    }
    
    ////////////////////////////////////////////////////////////////////////////
    // impl details
    
    private static StartEntry extractStartEntry(APTIncludeHandler.State state) {
	return (state == null) ? null : ((APTIncludeHandlerImpl.StateImpl) state).getStartEntry();
    }
    
    private static LinkedList<APTIncludeHandler.IncludeInfo> getIncludeStack(APTIncludeHandler.State inclState) {
        return inclState == null ? null : ((APTIncludeHandlerImpl.StateImpl)inclState).getIncludeStack();
    }
    
    /*package*/ static APTIncludeHandler.State copyIncludeState(APTIncludeHandler.State inclState, boolean cleanState) {
        return inclState == null ? null : ((APTIncludeHandlerImpl.StateImpl)inclState).copy(cleanState);
    }

    /*package*/ static APTMacroMap.State createCleanMacroState(APTMacroMap.State macroState) {
        APTMacroMap.State out = null;
        if (macroState != null) {
            out = ((APTBaseMacroMap.StateImpl)macroState).copyCleaned();
        }
        return out;
    }       
}
