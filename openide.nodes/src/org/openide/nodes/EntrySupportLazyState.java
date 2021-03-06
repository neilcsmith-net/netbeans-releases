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
package org.openide.nodes;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.openide.nodes.Children.Entry;
import org.openide.util.Utilities;

/** This class should represent an immutable state of a EntrySupportLazy instance.
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
final class EntrySupportLazyState {
    static final EntrySupportLazyState UNINITIALIZED = new EntrySupportLazyState();
    
    private EntrySupportLazyState() {
        this(
            false, null, false, false, 
            Collections.<Entry>emptyList(),
            Collections.<Entry>emptyList(),
            Collections.<Entry,EntryInfo>emptyMap()
        );
    }

    private EntrySupportLazyState(
        boolean inited, 
        Thread initThread, 
        boolean initInProgress, 
        boolean mustNotifySetEntries, 
        List<Entry> entries, 
        List<Entry> visibleEntries, 
        Map<Entry, EntryInfo> entryToInfo
    ) {
        this.inited = inited;
        this.initThread = initThread;
        this.initInProgress = initInProgress;
        this.mustNotifySetEntries = mustNotifySetEntries;
        this.entries = entries;
        this.visibleEntries = visibleEntries;
        this.entryToInfo = entryToInfo;
    }
    
    
    private final boolean inited;
    private final Thread initThread;
    private final boolean initInProgress;
    private final boolean mustNotifySetEntries;
    
    private final List<Entry> entries;
    private final List<Entry> visibleEntries;
    private final Map<Entry, EntryInfo> entryToInfo;
    
    final boolean isInited() {
        return inited;
    }
    
    final boolean isInitInProgress() {
        return initInProgress;
    }
    final Thread initThread() {
        return initThread;
    }
    
    final boolean isMustNotify() {
        return mustNotifySetEntries;
    }
    final List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    final List<Entry> getVisibleEntries() {
        return Collections.unmodifiableList(visibleEntries);
    }
    final Map<Entry, EntryInfo> getEntryToInfo() {
        return Collections.unmodifiableMap(entryToInfo);
    }
    
    private EntrySupportLazyState cloneState() {
        try {
            return (EntrySupportLazyState)clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    final EntrySupportLazyState changeInited(boolean newInited) {
        return new EntrySupportLazyState(
            newInited, initThread, 
            initInProgress, mustNotifySetEntries, 
            entries, visibleEntries, entryToInfo
        );
    }

    final EntrySupportLazyState changeThread(Thread t) {
        return new EntrySupportLazyState(
            inited, t, 
            initInProgress, mustNotifySetEntries, 
            entries, visibleEntries, entryToInfo
        );
    }

    final EntrySupportLazyState changeProgress(boolean b) {
        return new EntrySupportLazyState(
            inited, initThread, 
            b, mustNotifySetEntries, 
            entries, visibleEntries, entryToInfo
        );
    }
    final EntrySupportLazyState changeMustNotify(boolean b) {
        return new EntrySupportLazyState(
            inited, initThread, 
            initInProgress, b,
            entries, visibleEntries, entryToInfo
        );
    }
    final EntrySupportLazyState changeEntries(
        List<Entry> entries, 
        List<Entry> visibleEntries,
        Map<Entry, EntryInfo> entryToInfo
    ) {
        if (entries == null) {
            entries = this.entries;
        }
        if (visibleEntries == null) {
            visibleEntries = this.visibleEntries;
        }
        if (entryToInfo == null) {
            entryToInfo = this.entryToInfo;
        }
        EntrySupportLazyState state = new EntrySupportLazyState(
            inited, initThread, 
            initInProgress, mustNotifySetEntries, 
            entries, visibleEntries, entryToInfo
        );
        int entriesSize = 0;
        int entryToInfoSize = 0;
        assert (entriesSize = state.getEntries().size()) >= 0;
        assert (entryToInfoSize = state.getEntryToInfo().size()) >= 0;
        assert state.getEntries().size() == state.getEntryToInfo().size() : "Entries: " + state.getEntries().size() + "; vis. entries: " + EntrySupportLazy.notNull(state.getVisibleEntries()).size() + "; Infos: " + state.getEntryToInfo().size() + "; entriesSize: " + entriesSize + "; entryToInfoSize: " + entryToInfoSize + EntrySupportLazy.dumpEntriesInfos(state.getEntries(), state.getEntryToInfo()); // NOI18N
        return state;
    }

    @Override
    public String toString() {
        int entriesSize = getEntries().size();
        int entryToInfoSize = getEntryToInfo().size();
        return 
    
            "Inited: " + inited +
            "\nThread: " + initThread +
            "\nInProgress: " + initInProgress +
            "\nMustNotify: " + mustNotifySetEntries +
            "\nEntries: " + getEntries().size() + "; vis. entries: " + 
            EntrySupportLazy.notNull(getVisibleEntries()).size() + "; Infos: " + 
            getEntryToInfo().size() + "; entriesSize: " + 
            entriesSize + "; entryToInfoSize: " + entryToInfoSize + 
            EntrySupportLazy.dumpEntriesInfos(getEntries(), getEntryToInfo());
    }
    
    static final class EntryInfo {
        private final EntrySupportLazy lazy;
        private final Entry entry;
        /**
         * my index in list of entries
         */
        private final int index;
        /**
         * cached node for this entry
         */
        private NodeRef refNode;
        /** reference to thread which is just creating the node for this info */
        Thread creatingNodeThread;

        public EntryInfo(EntrySupportLazy lazy, Entry entry) {
            this(lazy, entry, -1, (NodeRef)null);
        }
        
        private EntryInfo(EntrySupportLazy lazy, Entry entry, int index, NodeRef refNode) {
            this.lazy = lazy;
            this.entry = entry;
            this.index = index;
            this.refNode = refNode;
        }
        private EntryInfo(EntrySupportLazy lazy, Entry entry, int index, Node refNode) {
            this.lazy = lazy;
            this.entry = entry;
            this.index = index;
            this.refNode = new NodeRef(refNode, this);
        }

        final EntryInfo changeNode(Node node) {
            if (node != null) {
                return new EntryInfo(lazy, entry, index, node);
            } else {
                return new EntryInfo(lazy, entry, index, refNode);
            }
        }
        final EntryInfo changeIndex(int index) {
            return new EntryInfo(lazy, entry, index, refNode);
        }

        final EntrySupportLazy lazy() {
            return lazy;
        }
        
        final Entry entry() {
            return entry;
        }

        private Object lock() {
            return lazy.LOCK;
        }

        /**
         * Gets or computes the nodes. It holds them using weak reference so
         * they can get garbage collected.
         */
        public final Node getNode() {
            return getNode(false, null);
        }

        public final Node getNode(boolean refresh, Object source) {
            while (true) {
                Node node;
                boolean creating = false;
                synchronized (lock()) {
                    if (refresh) {
                        refNode = null;
                    }
                    if (refNode != null) {
                        node = refNode.get();
                        if (node != null) {
                            return node;
                        }
                    }
                    if (creatingNodeThread != null) {
                        if (creatingNodeThread == Thread.currentThread()) {
                            return new EntrySupportLazy.DummyNode();
                        }
                        try {
                            lock().wait();
                        } catch (InterruptedException ex) {
                        }
                    } else {
                        creatingNodeThread = Thread.currentThread();
                        creating = true;
                    }
                }
                Collection<Node> nodes = Collections.emptyList();
                try {
                    if (creating) {
                        try {
                            nodes = entry.nodes(source);
                        } catch (RuntimeException ex) {
                            NodeOp.warning(ex);
                        }
                    }
                } finally {
                    synchronized (lock()) {
                        if (!creating) {
                            if (refNode != null) {
                                node = refNode.get();
                                if (node != null) {
                                    return node;
                                }
                            }
                            // node created by other thread was GCed meanwhile, try once again
                            continue;
                        }
                        if (nodes.isEmpty()) {
                            node = new EntrySupportLazy.DummyNode();
                        } else {
                            if (nodes.size() > 1) {
                                EntrySupportLazy.LOGGER.log(Level.FINE, 
                                    "Number of nodes for Entry: {0} is {1} instead of 1", // NOI18N
                                    new Object[]{entry, nodes.size()}
                                ); 
                            }
                            node = nodes.iterator().next();
                        }
                        refNode = new NodeRef(node, this);
                        if (creating) {
                            creatingNodeThread = null;
                            lock().notifyAll();
                        }
                    }
                }
                final Children ch = lazy().children;
                // assign node to the new children
                node.assignTo(ch, -1);
                node.fireParentNodeChange(null, ch.parent);
                return node;
            }
        }

        /**
         * extract current node (if was already created)
         */
        Node currentNode() {
            synchronized (lock()) {
                return refNode == null ? null : refNode.get();
            }
        }

        final boolean isHidden() {
            return this.index == -2;
        }

        /**
         * Get index.
         */
        final int getIndex() {
            assert index >= 0 : "When first asked for it has to be set: " + index; // NOI18N
            return index;
        }

        @Override
        public String toString() {
            return "EntryInfo for entry: " + entry + ", node: " + (refNode == null ? null : refNode.get()); // NOI18N
        }
    }

    private static final class NodeRef extends WeakReference<Node> implements Runnable {

        private final EntryInfo info;

        public NodeRef(Node node, EntryInfo info) {
            super(node, Utilities.activeReferenceQueue());
            info.lazy().registerNode(1, info);
            this.info = info;
        }

        @Override
        public void run() {
            info.lazy().registerNode(-1, info);
        }
    }
    
}
