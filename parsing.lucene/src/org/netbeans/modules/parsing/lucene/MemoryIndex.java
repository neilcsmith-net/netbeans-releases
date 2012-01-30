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
package org.netbeans.modules.parsing.lucene;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.modules.parsing.lucene.support.Convertor;
import org.netbeans.modules.parsing.lucene.support.Index;
import org.netbeans.modules.parsing.lucene.support.StoppableConvertor;
import org.openide.util.Parameters;

/**
 *
 * @author Tomas Zezula
 */
public class MemoryIndex implements Index {
    
    private final Analyzer analyzer;
    private final ReentrantReadWriteLock lock;
    //@GuardedBy("this")
    private RAMDirectory dir;
    //@GuardedBy("this")
    private IndexReader cachedReader;
    
    
    private MemoryIndex(@NonNull final Analyzer analyzer) {
        assert analyzer != null;
        this.analyzer = analyzer;
        this.lock = new ReentrantReadWriteLock();
    }
    
    @NonNull
    static MemoryIndex create(@NonNull Analyzer analyzer) {
        return new MemoryIndex(analyzer);
    }

    @NonNull
    @Override
    public Status getStatus(boolean tryOpen) throws IOException {
        return Status.VALID;
    }

    @Override
    public <T> void query(
            @NonNull Collection<? super T> result,
            @NonNull Convertor<? super Document, T> convertor,
            @NullAllowed FieldSelector selector,
            @NullAllowed AtomicBoolean cancel,
            @NonNull Query... queries) throws IOException, InterruptedException {
        Parameters.notNull("queries", queries);   //NOI18N
        Parameters.notNull("convertor", convertor); //NOI18N
        Parameters.notNull("result", result);       //NOI18N   
        
        if (selector == null) {
            selector = AllFieldsSelector.INSTANCE;
        }
        
        lock.readLock().lock();
        try {
            final IndexReader in = getReader();
            final BitSet bs = new BitSet(in.maxDoc());
            final Collector c = new BitSetCollector(bs);
            final Searcher searcher = new IndexSearcher(in);
            try {
                for (Query q : queries) {
                    if (cancel != null && cancel.get()) {
                        throw new InterruptedException ();
                    }
                    searcher.search(q, c);
                }
            } finally {
                searcher.close();
            }        
            for (int docNum = bs.nextSetBit(0); docNum >= 0; docNum = bs.nextSetBit(docNum+1)) {
                if (cancel != null && cancel.get()) {
                    throw new InterruptedException ();
                }
                final Document doc = in.document(docNum, selector);
                final T value = convertor.convert(doc);
                if (value != null) {
                    result.add (value);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <S, T> void queryDocTerms(
            @NonNull Map<? super T, Set<S>> result,
            @NonNull Convertor<? super Document, T> convertor,
            @NonNull Convertor<? super Term, S> termConvertor,
            @NullAllowed FieldSelector selector,
            @NullAllowed AtomicBoolean cancel,
            @NonNull Query... queries) throws IOException, InterruptedException {
        Parameters.notNull("result", result);   //NOI18N
        Parameters.notNull("convertor", convertor);   //NOI18N
        Parameters.notNull("termConvertor", termConvertor); //NOI18N
        Parameters.notNull("queries", queries);   //NOI18N
        
        
        if (selector == null) {
            selector = AllFieldsSelector.INSTANCE;
        }

        lock.readLock().lock();
        try {
            final IndexReader in = getReader();
            final BitSet bs = new BitSet(in.maxDoc());
            final Collector c = new BitSetCollector(bs);
            final Searcher searcher = new IndexSearcher(in);
            final TermCollector termCollector = new TermCollector(c);
            try {
                for (Query q : queries) {
                    if (cancel != null && cancel.get()) {
                        throw new InterruptedException ();
                    }
                    if (q instanceof TermCollector.TermCollecting) {
                        ((TermCollector.TermCollecting)q).attach(termCollector);
                    } else {
                        throw new IllegalArgumentException (
                                String.format("Query: %s does not implement TermCollecting",    //NOI18N
                                q.getClass().getName()));
                    }
                    searcher.search(q, termCollector);
                }
            } finally {
                searcher.close();
            }

            for (int docNum = bs.nextSetBit(0); docNum >= 0; docNum = bs.nextSetBit(docNum+1)) {
                if (cancel != null && cancel.get()) {
                    throw new InterruptedException ();
                }
                final Document doc = in.document(docNum, selector);
                final T value = convertor.convert(doc);
                if (value != null) {
                    final Set<Term> terms = termCollector.get(docNum);
                    if (terms != null) {
                        result.put (value, convertTerms(termConvertor, terms));
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <T> void queryTerms(
            @NonNull Collection<? super T> result,
            @NullAllowed Term start,
            @NonNull StoppableConvertor<Term, T> filter,
            @NullAllowed AtomicBoolean cancel) throws IOException, InterruptedException {
        Parameters.notNull("result", result);   //NOI18N
        Parameters.notNull("filter", filter); //NOI18N
        
        lock.readLock().lock();
        try {
            final IndexReader in = getReader();
            final TermEnum terms = start == null ? in.terms () : in.terms (start);
            try {
                do {
                    if (cancel != null && cancel.get()) {
                        throw new InterruptedException ();
                    }
                    final Term currentTerm = terms.term();
                    if (currentTerm != null) {                    
                        final T vote = filter.convert(currentTerm);
                        if (vote != null) {
                            result.add(vote);
                        }
                    }
                } while (terms.next());
            } catch (StoppableConvertor.Stop stop) {
                //Stop iteration of TermEnum
            } finally {
                terms.close();
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <S, T> void store(Collection<T> toAdd, Collection<S> toDelete, Convertor<? super T, ? extends Document> docConvertor, Convertor<? super S, ? extends Query> queryConvertor, boolean optimize) throws IOException {
        lock.writeLock().lock();
        try {
            final IndexWriter out = getWriter();
            try {
                for (S td : toDelete) {
                    out.deleteDocuments(queryConvertor.convert(td));
                }
                if (toAdd.isEmpty()) {
                    return;
                }
                for (Iterator<T> it = toAdd.iterator(); it.hasNext();) {
                    T entry = it.next();
                    it.remove();
                    final Document doc = docConvertor.convert(entry);
                    out.addDocument(doc);
                }
            } finally {

                try {
                    out.close();
                } finally {
                    refreshReader();
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void clear() throws IOException {
        lock.writeLock().lock();
        close();
        try {
            synchronized (MemoryIndex.this) {
                if (dir != null) {
                    dir.close();
                    dir = null;
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        lock.writeLock().lock();
        try {
            synchronized (MemoryIndex.this) {
                if (cachedReader != null) {
                    cachedReader.close();
                    cachedReader = null;
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private synchronized IndexReader getReader() throws IOException {
        if (cachedReader == null) {
            cachedReader = IndexReader.open(getDirectory(),true);
        }
        return cachedReader;
    }
    
    private synchronized void refreshReader() throws IOException {
        assert lock.isWriteLockedByCurrentThread();
        if (cachedReader != null) {
            final IndexReader newReader = cachedReader.reopen();
            if (newReader != cachedReader) {
                cachedReader.close();
                cachedReader = newReader;
            }
        }
    }
    
    private synchronized IndexWriter getWriter() throws IOException {
        return new IndexWriter (getDirectory(), analyzer, IndexWriter.MaxFieldLength.LIMITED);
    }
    
    private synchronized Directory getDirectory() {
        if (dir == null) {
            dir = new RAMDirectory();
        }
        return dir;
    }
    
    private static <T> Set<T> convertTerms(final Convertor<? super Term, T> convertor, final Set<? extends Term> terms) {
        final Set<T> result = new HashSet<T>(terms.size());
        for (Term term : terms) {
            result.add(convertor.convert(term));
        }
        return result;
    }
    
}
