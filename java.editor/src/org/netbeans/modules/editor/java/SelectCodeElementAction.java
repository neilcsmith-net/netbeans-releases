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

package org.netbeans.modules.editor.java;

import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Action;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.source.Task;
import javax.swing.text.StyledDocument;
import org.netbeans.api.java.source.Comment;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.progress.ProgressUtils;
import org.netbeans.editor.BaseAction;
import org.netbeans.editor.BaseDocument;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Code selection according to syntax tree. It also supports JavaDoc.
 *
 * @author Miloslav Metelka, Jan Pokorsky
 */
final class SelectCodeElementAction extends BaseAction {

    private boolean selectNext;

    /**
     * Construct new action that selects next/previous code elements
     * according to the language model.
     * <br>
     *
     * @param name name of the action (should be one of
     *  <br>
     *  <code>JavaKit.selectNextElementAction</code>
     *  <code>JavaKit.selectPreviousElementAction</code>
     * @param selectNext <code>true</code> if the next element should be selected.
     *  <code>False</code> if the previous element should be selected.
     */
    public SelectCodeElementAction(String name, boolean selectNext) {
        super(name);
        this.selectNext = selectNext;
        String desc = getShortDescription();
        if (desc != null) {
            putValue(SHORT_DESCRIPTION, desc);
        }
    }
        
    public String getShortDescription(){
        String name = (String)getValue(Action.NAME);
        if (name == null) return null;
        String shortDesc;
        try {
            shortDesc = NbBundle.getBundle(JavaKit.class).getString(name); // NOI18N
        }catch (MissingResourceException mre){
            shortDesc = name;
        }
        return shortDesc;
    }
    
    public void actionPerformed(ActionEvent evt, JTextComponent target) {
        if (target != null) {
            int selectionStartOffset = target.getSelectionStart();
            int selectionEndOffset = target.getSelectionEnd();
            if (selectionEndOffset > selectionStartOffset || selectNext) {
                SelectionHandler handler = (SelectionHandler)target.getClientProperty(SelectionHandler.class);
                if (handler == null) {
                    handler = new SelectionHandler(target, getShortDescription());
                    target.addCaretListener(handler);
                    // No need to remove the listener above as the handler
                    // is stored is the client-property of the component itself
                    target.putClientProperty(SelectionHandler.class, handler);
                }
                
                if (selectNext) { // select next element
                    handler.selectNext();
                } else { // select previous
                    handler.selectPrevious();
                }
            }
        }
    }

    private static final class SelectionHandler implements CaretListener, Task<CompilationController>, Runnable {
        
        private JTextComponent target;
        private String name;
        private SelectionInfo[] selectionInfos;
        private int selIndex = -1;
        private boolean ignoreNextCaretUpdate;
        private AtomicBoolean cancel;

        SelectionHandler(JTextComponent target, String name) {
            this.target = target;
        }

        public void selectNext() {
            if (selectionInfos == null) {
                final JavaSource js = JavaSource.forDocument(target.getDocument());
                if (js != null) {
                    cancel = new AtomicBoolean();
                    ProgressUtils.runOffEventDispatchThread(new Runnable() {
                        public void run() {
                            try {
                                js.runUserActionTask(SelectionHandler.this, true);
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    }, name, cancel, false);
                }
            }            
            run();
        }

        public synchronized void selectPrevious() {
            if (selIndex > 0) {
                select(selectionInfos[--selIndex]);
            }
        }

        private void select(SelectionInfo selectionInfo) {
            Caret caret = target.getCaret();
            ignoreNextCaretUpdate = true;
            try {
                caret.setDot(selectionInfo.getEndOffset());
                caret.moveDot(selectionInfo.getStartOffset());
            } finally {
                ignoreNextCaretUpdate = false;
            }
        }
        
        public void caretUpdate(CaretEvent e) {
            if (!ignoreNextCaretUpdate) {
                synchronized (this) {
                    selectionInfos = null;
                    selIndex = -1;
                }
            }
        }


        public void run(CompilationController cc) {
            try {
                if (cancel != null && cancel.get())
                    return;
                cc.toPhase(Phase.RESOLVED);
                if (cancel != null && cancel.get())
                    return;
                selectionInfos = initSelectionPath(target, cc);
                if (selectionInfos != null && selectionInfos.length > 0)
                    selIndex = 0;
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        private SelectionInfo[] initSelectionPath(JTextComponent target, CompilationController ci) {
            List<SelectionInfo> positions = new ArrayList<SelectionInfo>();
            int caretPos = target.getCaretPosition();
            positions.add(new SelectionInfo(caretPos, caretPos));
            if (target.getDocument() instanceof BaseDocument) {
                try {
                    int block[] = org.netbeans.editor.Utilities.getIdentifierBlock((BaseDocument) target.getDocument(), caretPos);
                    if (block != null) {
                        positions.add(new SelectionInfo(block[0], block[1]));
                    }
                } catch (BadLocationException ble) {}
            }
            SourcePositions sp = ci.getTrees().getSourcePositions();
	    final TreeUtilities treeUtilities = ci.getTreeUtilities();
            TreePath tp = treeUtilities.pathFor(caretPos);
            for (Tree tree: tp) {
                int startPos = (int)sp.getStartPosition(tp.getCompilationUnit(), tree);
                int endPos = (int)sp.getEndPosition(tp.getCompilationUnit(), tree);
                positions.add(new SelectionInfo(startPos, endPos));
		
		//Support selection of JavaDoc
		int docBegin = Integer.MAX_VALUE;
                for (Comment comment : treeUtilities.getComments(tree, true)) {
                    docBegin = Math.min(comment.pos(), docBegin);
                }
		int docEnd = Integer.MIN_VALUE;
                for (Comment comment : treeUtilities.getComments(tree, false)) {
                    docEnd = Math.max(comment.endPos(), docEnd);
                }
		if (docBegin != Integer.MAX_VALUE && docEnd != Integer.MIN_VALUE) {
		    positions.add(new SelectionInfo(docBegin, docEnd));
		} else if (docBegin == Integer.MAX_VALUE && docEnd != Integer.MIN_VALUE) {
		    positions.add(new SelectionInfo(startPos, docEnd));
		} else if (docBegin != Integer.MAX_VALUE && docEnd == Integer.MIN_VALUE) {
		    positions.add(new SelectionInfo(docBegin, endPos));
		}
            }
	    //sort selectioninfo by their start
	    SortedSet<SelectionInfo> orderedPositions = new TreeSet<SelectionInfo>(new Comparator<SelectionInfo>() {
		@Override
		public int compare(SelectionInfo o1, SelectionInfo o2) {
                    //to support selections, which start at the same offset also compare the end offsets
                    int offsetStartDiff = o2.getStartOffset() - o1.getStartOffset();
                    if (0 == offsetStartDiff) {
                        return (o1.getEndOffset() - o2.getEndOffset());
                    }
                    return offsetStartDiff;
		}
	    });
	    orderedPositions.addAll(positions);
	    //for each selectioninfo add its line selection
	    if (target.getDocument() instanceof StyledDocument) {
		StyledDocument doc = (StyledDocument) target.getDocument();
                
                Iterator<SelectionInfo> it = positions.iterator();
                SelectionInfo selectionInfo = it.hasNext() ? it.next() : null;
		while (selectionInfo != null) {
		    int startOffset = NbDocument.findLineOffset(doc, NbDocument.findLineNumber(doc, selectionInfo.getStartOffset()));
		    int endOffset = doc.getLength();
                    try {
                        endOffset = NbDocument.findLineOffset(doc, NbDocument.findLineNumber(doc, selectionInfo.getEndOffset()) + 1);
                    } catch (IndexOutOfBoundsException ioobe) {}
                    SelectionInfo next = it.hasNext() ? it.next() : null;
                    if (next == null || startOffset >= next.startOffset && endOffset <= next.endOffset) {
		        orderedPositions.add(new SelectionInfo(startOffset, endOffset));
                    }
                    selectionInfo = next;
		}
	    }
	    
	    return orderedPositions.toArray(new SelectionInfo[orderedPositions.size()]);
        }

        public void run() {
            if (selectionInfos != null && selIndex < selectionInfos.length - 1) {
                select(selectionInfos[++selIndex]);
            }
        }

    }
    
    private static final class SelectionInfo {
        
        private int startOffset;
        private int endOffset;
        
        SelectionInfo(int startOffset, int endOffset) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }
        
        public int getStartOffset() {
            return startOffset;
        }
        
        public int getEndOffset() {
            return endOffset;
        }

        @Override
        public String toString() {
            return "<" + startOffset + ":" + endOffset + ">"; //NOi18N
        }
    }
}
