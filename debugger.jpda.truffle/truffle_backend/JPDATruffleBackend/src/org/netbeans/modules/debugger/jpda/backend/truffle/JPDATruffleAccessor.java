/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */

package org.netbeans.modules.debugger.jpda.backend.truffle;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleRuntime;
import com.oracle.truffle.api.debug.Breakpoint;
import com.oracle.truffle.api.debug.DebugSupportException;
import com.oracle.truffle.api.debug.DebugSupportProvider;
import com.oracle.truffle.api.debug.Debugger;
import com.oracle.truffle.api.debug.ExecutionEvent;
import com.oracle.truffle.api.debug.SuspendedEvent;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameInstance;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.impl.Accessor;
import com.oracle.truffle.api.instrument.Visualizer;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.LineLocation;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.vm.TruffleVM;
import com.oracle.truffle.js.runtime.JSFrameUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptEngine;

/**
 * Truffle accessor for JPDA debugger.
 * 
 * This class serves as a intermediary between the {@link JPDATruffleDebugManager}
 * and JPDA Java debugger (<code>org.netbeans.modules.debugger.jpda.truffle.access.TruffleAccessor</code>),
 * which submits breakpoints and calls methods of this class.
 * To be able to invoke methods via debugger at any time, use {@link AccessLoop}.
 * 
 * Creation of a TruffleVM instance is out of control for the debugger.
 * Thus to intercept execution and suspension, we add Java method breakpoints into
 * <code>com.oracle.truffle.api.vm.TruffleVM.dispatchExecutionEvent()</code> and
 * <code>com.oracle.truffle.api.vm.TruffleVM.dispatchSuspendedEvent()</code> methods.
 * 
 * @author Martin
 */
public class JPDATruffleAccessor extends Object {
    
    private static final String ACCESS_THREAD_NAME = "JPDA Truffle Access Loop";   // NOI18N
    private static volatile boolean accessLoopRunning = false;
    //private static volatile AccessLoop accessLoopRunnable;
    private static volatile Thread accessLoopThread;
    private static JPDATruffleDebugManager debugManager;
    /** Explicitly set this field to true to step into script calls. */
    static boolean isSteppingInto = false; // Step into was issued in JPDA debugger
    static int steppingIntoTruffle = 0; // = 0 no stepping change, > 0 set step into, < 0 unset stepping into
    /** A field to test for whether the access loop is sleeping and can be interrupted. */
    static boolean accessLoopSleeping = false;
    private static boolean stepIntoPrepared;
    /** A step command:
     * 0 no step (continue)
     * 1 step into
     * 2 step over
     * 3 step out
     */
    //private static int stepCmd = 0;

    public JPDATruffleAccessor() {}
    
    static Thread startAccessLoop() {
        if (!accessLoopRunning) {
            Thread loop;
            AccessLoop accessLoop;
            try {
                accessLoop = new AccessLoop();
                loop = new Thread(accessLoop, ACCESS_THREAD_NAME);
                loop.setDaemon(true);
                loop.setPriority(Thread.MIN_PRIORITY);
            } catch (SecurityException se) {
                return null;
            }
            accessLoopThread = loop;
            //accessLoopRunnable = accessLoop;
            accessLoopRunning = true;
            loop.start();
        }
        return accessLoopThread;
    }
    
    static void stopAccessLoop() {
        if (debugManager != null) {
            debugManager.dispose();
        }
        accessLoopRunning = false;
        //accessLoopRunnable = null;
        if (accessLoopThread != null) {
            accessLoopThread.interrupt();
            accessLoopThread = null;
        }
    }
    
    /*
    static JPDATruffleDebugManager setUpDebugManager() {
        if (debugManager == null) {
            debugManager = JPDATruffleDebugManager.setUp();
        }
        return debugManager;
    }*/
    
    //static JPDATruffleDebugManager setUpDebugManagerFor(ScriptEngine engine) {
    static JPDATruffleDebugManager setUpDebugManagerFor(/*ExecutionEvent*/Object event) {
        Debugger debugger = ((ExecutionEvent) event).getDebugger();
        TruffleVM tvm;
        try {
            Field vmField = debugger.getClass().getDeclaredField("vm");
            vmField.setAccessible(true);
            tvm = (TruffleVM) vmField.get(debugger);
        } catch (IllegalAccessException | IllegalArgumentException |
                 NoSuchFieldException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
        debugManager = JPDATruffleDebugManager.setUp(debugger, tvm);
        return debugManager;
    }
    
    /*
    static void executionHalted(Node astNode, MaterializedFrame frame,
                                long srcId, String srcName, String srcPath, int line, String code,
                                FrameSlot[] frameSlots, String[] slotNames, String[] slotTypes,
                                FrameInstance[] stackTrace, String topFrame, Object thisObject) {
        // Called when the execution is halted.
        setCommand();
    }
    
    static void executionStepInto(Node astNode, MaterializedFrame frame, String name,
                                  long srcId, String srcName, String srcPath, int line, String code,
                                  FrameSlot[] frameSlots, String[] slotNames, String[] slotTypes,
                                  FrameInstance[] stackTrace, String topFrame, Object thisObject) {
        // Called when the execution steps into a call.
        setCommand();
    }
    */
    
    static /*SourcePosition*/Object getSourcePosition(/*SuspendedEvent*/Object suspendedEvent) {
        SuspendedEvent evt = (SuspendedEvent) suspendedEvent;
        return JPDATruffleDebugManager.getPosition(evt.getNode());
    }
    
    static /*FrameInfo*/Object getFrameInfo(/*SuspendedEvent*/Object suspendedEvent) {
        SuspendedEvent evt = (SuspendedEvent) suspendedEvent;
        Node node = evt.getNode();
        Visualizer visualizer = getDebugSupport(node).getVisualizer();
        return new FrameInfo(evt.getFrame(), visualizer, node);
    }
    
    static void setStep(/*SuspendedEvent*/Object suspendedEvent, int stepCmd) {
        SuspendedEvent evt = (SuspendedEvent) suspendedEvent;
        switch (stepCmd) {
            case 0: evt.prepareContinue();
                    break;
            case 1: evt.prepareStepInto(1);
                    break;
            case 2: evt.prepareStepOver(1);
                    break;
            case 3: evt.prepareStepOut();
                    //System.err.println("Successful step out = "+success);
                    break;
            default:
                    throw new IllegalStateException("Unknown step command: "+stepCmd);
        }
    }
    
    private static TruffleLanguage getLanguage(Node node) {
        try {
            Field apiField = Accessor.class.getDeclaredField("API");
            apiField.setAccessible(true);
            Object api = apiField.get(null);
            Field nodesField = Accessor.class.getDeclaredField("NODES");
            nodesField.setAccessible(true);
            Object nodes = nodesField.get(null);
            Method findLanguageMethod = Accessor.class.getDeclaredMethod("findLanguage", RootNode.class);
            findLanguageMethod.setAccessible(true);
            Class languageClass = (Class) findLanguageMethod.invoke(nodes, node.getRootNode());
            
            findLanguageMethod = Accessor.class.getDeclaredMethod("findLanguage", TruffleVM.class, Class.class);
            findLanguageMethod.setAccessible(true);
            Object tl = findLanguageMethod.invoke(api, debugManager.getTruffleVM(), languageClass);
            return (TruffleLanguage) tl;
        } catch (IllegalAccessException | IllegalArgumentException |
                 NoSuchFieldException | NoSuchMethodException |
                 SecurityException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        } catch (StackOverflowError soe) {
            throw new IllegalStateException(node.toString(), soe);
        }
    }
    
    private static DebugSupportProvider getDebugSupport(Node node) {
        TruffleLanguage tl = getLanguage(node);
        try {
            Method getDebugSupportMethod = TruffleLanguage.class.getDeclaredMethod("getDebugSupport");
            getDebugSupportMethod.setAccessible(true);
            return (DebugSupportProvider) getDebugSupportMethod.invoke(tl);
        } catch (IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException | NoSuchMethodException |
                 SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /*
    private static void setCommand() {
        stepIntoPrepared = false;
        switch (stepCmd) {
            case 0: debugManager.getDebugger().prepareContinue();
                    break;
            case 1: debugManager.getDebugger().prepareStepInto(1);
                    break;
            case 2: debugManager.getDebugger().prepareStepOver(1);
                    break;
            case 3: debugManager.getDebugger().prepareStepOut();
                    //System.err.println("Successful step out = "+success);
                    break;
        }
        stepCmd = 0;
    }*/
    
    static Object getSlotValue(Object frameObj, Object slotObj) {
        Frame frame = (Frame) frameObj;
        FrameSlot slot = (FrameSlot) slotObj;
        switch(slot.getKind()) {
            case Boolean:   return FrameUtil.getBooleanSafe(frame, slot);
            case Byte:      return FrameUtil.getByteSafe(frame, slot);
            case Double:    return FrameUtil.getDoubleSafe(frame, slot);
            case Float:     return FrameUtil.getFloatSafe(frame, slot);
            case Int:       return FrameUtil.getIntSafe(frame, slot);
            case Long:      return FrameUtil.getLongSafe(frame, slot);
            case Object:    Object obj = FrameUtil.getObjectSafe(frame, slot);
                            //Node node = frame.materialize().getFrameDescriptor().
                            Node node = Truffle.getRuntime().getCurrentFrame().getCallNode();   // TODO find frame's node
                            Visualizer visualizer = getDebugSupport(node).getVisualizer();
                            //return context.getVisualizer().displayValue(context, obj);
                            String name = visualizer.displayIdentifier(slot);
                            TruffleObject to = new TruffleObject(visualizer, name, obj);
                            //System.err.println("TruffleObject: "+to);
                            //System.err.println("  children Generic = "+Arrays.toString(to.getChildrenGeneric()));
                            //System.err.println("  children JS = "+Arrays.toString(to.getChildrenJS()));
                            return to;
            case Illegal:   
            default:        return null;
        }
    }
    
    /*
    static TruffleFrame[] getFramesInfo(FrameInstance[] frames) {
        Visualizer visualizer = debugManager.getContext().getVisualizer();
        int n = frames.length;
        TruffleFrame[] frameInfos = new TruffleFrame[n];
        for (int i = 0; i < n; i++) {
            FrameInstance fi = frames[i];
            TruffleFrame tf = new TruffleFrame();
            tf.callTargetName = visualizer.displayCallTargetName(fi.getCallTarget());
            tf.methodName = visualizer.displayMethodName(fi.getCallNode());
            tf.sourceLocation = visualizer.displaySourceLocation(fi.getCallNode());
            frameInfos[i] = tf;
        }
        return frameInfos;
    }
    */
    /**
     * @param frames The array of stack frame infos
     * @return An array of two elements: a String of frame information and
     * an array of code contents.
     */
    static Object[] getFramesInfo(FrameInstance[] frames) {
        //Visualizer visualizer = debugManager.getVisualizer();
        int n = frames.length;
        //TruffleFrame[] frameInfos = new TruffleFrame[n];
        StringBuilder frameInfos = new StringBuilder();
        String[] codes = new String[n];
        Object[] thiss = new Object[n];
        for (int i = 0; i < n; i++) {
            FrameInstance fi = frames[i];
            Visualizer visualizer = getDebugSupport(fi.getCallNode()).getVisualizer();
            //TruffleFrame tf = new TruffleFrame();
            frameInfos.append(visualizer.displayCallTargetName(fi.getCallTarget()));
            frameInfos.append('\n');
            frameInfos.append(visualizer.displayMethodName(fi.getCallNode()));
            frameInfos.append('\n');
            frameInfos.append(visualizer.displaySourceLocation(fi.getCallNode()));
            frameInfos.append('\n');
            if (fi.getCallNode() == null) {
                /* frames with null call nodes are filtered out by JPDATruffleDebugManager.FrameInfo
                System.err.println("Frame with null call node: "+fi);
                System.err.println("  is virtual frame = "+fi.isVirtualFrame());
                System.err.println("  call target = "+fi.getCallTarget());
                System.err.println("frameInfos = "+frameInfos);
                */
            }
            SourcePosition position = JPDATruffleDebugManager.getPosition(fi.getCallNode());
            frameInfos.append(position.id);
            frameInfos.append('\n');
            frameInfos.append(position.name);
            frameInfos.append('\n');
            frameInfos.append(position.path);
            frameInfos.append('\n');
            frameInfos.append(position.line);
            
            frameInfos.append("\n\n");
            
            codes[i] = position.code;
            
            Frame f = fi.getFrame(FrameInstance.FrameAccess.READ_ONLY, false);
            if (f instanceof VirtualFrame) {
                thiss[i] = JSFrameUtil.getThisObj((VirtualFrame) f);
            }
        }
        return new Object[] { frameInfos.toString(), codes, thiss };
    }
    
    static void debuggerAccess() {
        // A breakpoint is submitted on this method.
        // When accessLoopThread is interrupted, this breakpoint is hit
        // and methods can be executed via JPDA debugger.
    }
    
    /*
    static boolean inStepInto(Node astNode, String name) {
        if (isSteppingInto) {
            executionStepInto(astNode, name);
            return true;
        } else {
            return false;
        }
    }
    */
    
    static Breakpoint setLineBreakpoint(String path, int line,
                                            int ignoreCount, String condition) {
        return doSetLineBreakpoint(path, line, ignoreCount, condition, false);
    }
    
    static Breakpoint setLineBreakpoint(URL url, int line,
                                            int ignoreCount, String condition) {
        return doSetLineBreakpoint(url, line, ignoreCount, condition, false);
    }
    
    static Breakpoint setOneShotLineBreakpoint(String path, int line) {
        return doSetLineBreakpoint(path, line, 0, null, true);
    }
    
    static Breakpoint setOneShotLineBreakpoint(URL url, int line) {
        return doSetLineBreakpoint(url, line, 0, null, true);
    }
    
    private static Breakpoint doSetLineBreakpoint(String path, int line,
                                                  int ignoreCount, String condition,
                                                  boolean oneShot) {
        try {
            return doSetLineBreakpoint(new File(path).toURI().toURL(), line,
                                       ignoreCount, condition, oneShot);
        } catch (MalformedURLException muex) {
            System.err.println(muex.getLocalizedMessage());
            muex.printStackTrace();
        }
        Source source;
        try {
            source = Source.fromFileName(path);
        } catch (IOException ioex) {
            //System.err.println("setLineBreakpoint("+path+", "+line+"): "+ioex.getLocalizedMessage());
            return null;
        }
        return doSetLineBreakpoint(source, line, ignoreCount, condition, oneShot);
    }
    
    private static Breakpoint doSetLineBreakpoint(URL url, int line,
                                                  int ignoreCount, String condition,
                                                  boolean oneShot) {
        Source source;
        try {
            source = Source.fromURL(url, url.getPath());
        } catch (IOException ioex) {
            return null;
        }
        return doSetLineBreakpoint(source, line, ignoreCount, condition, oneShot);
    }
    
    private static Breakpoint doSetLineBreakpoint(Source source, int line,
                                                  int ignoreCount, String condition,
                                                  boolean oneShot) {
        LineLocation bpLineLocation = source.createLineLocation(line);
        Breakpoint lb;
        try {
            lb = debugManager.getDebugger().setLineBreakpoint(0, bpLineLocation, oneShot);
        } catch (IOException dex) {
            System.err.println("setLineBreakpoint("+source+", "+line+"): "+dex);
            return null;
        }
        System.err.println("setLineBreakpoint("+source+", "+line+"): source = "+source+", line location = "+bpLineLocation+", lb = "+lb);
        if (ignoreCount != 0) {
            lb.setIgnoreCount(ignoreCount);
        }
        if (condition != null) {
            try {
                lb.setCondition(condition);
            } catch (IOException dex) {
                System.err.println("Wrong condition "+condition+" : "+dex);
            }
        }
        return lb;
    }
    
    static void removeBreakpoint(Object br) {
        ((Breakpoint) br).dispose();
    }
    
    /*
    static String evaluateToStr(String expression) {
        //System.err.println("evaluate("+expression+")");
        final Source source = Source.fromText(expression, "EVAL");
        Object value;
        try {
            value = debugManager.eval(source);
        } catch (DebugException ex) {
            return "> "+ex.getLocalizedMessage()+" <";
        }
        //System.err.println("  value = "+value);
        if (value == null) {
            return null;
        }
        Visualizer visualizer = debugManager.getVisualizer();
        String strValue = visualizer.displayValue(value, TruffleObject.DISPLAY_TRIM);
        //System.err.println("evaluate("+expression+") = "+strValue);
        return strValue;
    }
    */
    
    static Object evaluate(/*SuspendedEvent*/Object suspendedEvent, String expression) {
        //System.err.println("evaluate("+expression+")");
        SuspendedEvent evt = (SuspendedEvent) suspendedEvent;
        Node node = evt.getNode();
        DebugSupportProvider dsp = getDebugSupport(node);
        Visualizer visualizer = dsp.getVisualizer();
        final Source source = Source.fromText(expression, "EVAL");
        Object value;
        try {
            value = dsp.evalInContext(source, node, evt.getFrame());
        } catch (DebugSupportException ex) {
            return new TruffleObject(visualizer, ex.getLocalizedMessage(), ex);
        }
        //System.err.println("  value = "+value);
        if (value == null) {
            return null;
        }
        String strValue = visualizer.displayValue(value, TruffleObject.DISPLAY_TRIM);
        TruffleObject to = new TruffleObject(visualizer, strValue, value);
        return to;
    }
    
    static Object evaluate(String expression, Object frameInstance) {
        FrameInstance fi = (FrameInstance) frameInstance;
        MaterializedFrame frame = fi.getFrame(FrameInstance.FrameAccess.MATERIALIZE, true).materialize();
        final Source source = Source.fromText(expression, "EVAL");
        Node node = fi.getCallNode();
        DebugSupportProvider dsp = getDebugSupport(node);
        Visualizer visualizer = dsp.getVisualizer();
        Object value;
        try {
            value = dsp.evalInContext(source, node, frame);
            //value = debugManager.getDebugger().eval(source, fi.getCallNode(), frame);
        } catch (DebugSupportException ex) {
            return new TruffleObject(visualizer, ex.getLocalizedMessage(), ex);
        }
        if (value == null) {
            return null;
        }
        String strValue = visualizer.displayValue(value, TruffleObject.DISPLAY_TRIM);
        TruffleObject to = new TruffleObject(visualizer, strValue, value);
        return to;
    }
    
    static Object[] getFrameSlots(FrameInstance frameInstance) {
        FrameInstance fi = (FrameInstance) frameInstance;
        // returns { Frame frame, FrameSlot[] frameSlots, String[] slotNames, String[] slotTypes }
        Object[] slots = new Object[4];
        MaterializedFrame frame = fi.getFrame(FrameInstance.FrameAccess.MATERIALIZE, true).materialize();
        FrameDescriptor frameDescriptor = frame.getFrameDescriptor();
        List<? extends FrameSlot> slotsList = frameDescriptor.getSlots();
        ArrayList<FrameSlot> slotsArr = new ArrayList<>();
        for (FrameSlot fs : slotsList) {
            FrameSlotKind kind = fs.getKind();
            if (FrameSlotKind.Illegal.equals(kind)) {
                continue;
            }
            slotsArr.add(fs);
        }
        FrameSlot[] frameSlots = slotsArr.toArray(new FrameSlot[]{});
        String[] slotNames = new String[slots.length];
        String[] slotTypes = new String[slots.length];
        Visualizer visualizer = getDebugSupport(fi.getCallNode()).getVisualizer();//debugManager.getVisualizer();
        for (int i = 0; i < frameSlots.length; i++) {
            slotNames[i] = visualizer.displayIdentifier(frameSlots[i]);// slots[i].getIdentifier().toString();
            slotTypes[i] = frameSlots[i].getKind().toString();
        }
        slots[0] = frame;
        slots[1] = frameSlots;
        slots[2] = slotNames;
        slots[3] = slotTypes;
        return slots;
    }
    
    private static class AccessLoop implements Runnable {
        
        @Override
        public void run() {
            while (accessLoopRunning) {
                accessLoopSleeping = true;
                // Wait until we're interrupted
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException iex) {}
                accessLoopSleeping = false;
                //System.err.println("AccessLoop: steppingIntoTruffle = "+steppingIntoTruffle+", isSteppingInto = "+isSteppingInto+", stepIntoPrepared = "+stepIntoPrepared);
                /*
                if (steppingIntoTruffle != 0) {
                    if (steppingIntoTruffle > 0) {
                        if (!stepIntoPrepared) {
                            try {
                                debugManager.getDebugger().prepareStepInto(1);
                            } catch (IllegalStateException isex) {
                                forceStepInto();
                            }
                            stepIntoPrepared = true;
                            //System.err.println("Prepared step into and continue.");
                        }
                        isSteppingInto = true;
                    } else {
                        // un-prepare step into, if possible.
                        isSteppingInto = false;
                    }
                    steppingIntoTruffle = 0;
                    continue;
                }*/
                //System.err.println("accessLoopRunning = "+accessLoopRunning+", possible debugger access...");
                if (accessLoopRunning) {
                    debuggerAccess();
                }
            }
        }
        
        /** Workaround for inability to prepare step into when continue is prepared. */
        /*
        private void forceStepInto() {
            try {
                Field debugContextField = DebugEngine.class.getDeclaredField("debugContext");
                debugContextField.setAccessible(true);
                Object debugContext = debugContextField.get(debugManager.getDebugger());
                Class stepStrategyClass = Class.forName(DebugEngine.class.getName()+"$StepStrategy");
                Method replaceStrategyMethod = debugContext.getClass().getDeclaredMethod("replaceStrategy", stepStrategyClass);
                replaceStrategyMethod.setAccessible(true);
                Class stepIntoClass = Class.forName(DebugEngine.class.getName()+"$StepInto");
                Constructor[] declaredConstructors = stepIntoClass.getDeclaredConstructors();
                //System.err.println("  declaredConstructors = "+Arrays.toString(declaredConstructors));
                Constructor stepIntoConstructor = declaredConstructors[0];//stepIntoClass.getDeclaredConstructor(Integer.TYPE);
                stepIntoConstructor.setAccessible(true);
                Object stepInto = stepIntoConstructor.newInstance(debugManager.getDebugger(), 1);
                replaceStrategyMethod.invoke(debugContext, stepInto);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/

    }
    
}
