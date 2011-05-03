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
package org.netbeans.modules.cnd.modelimpl.csm.deep;

import java.util.logging.Level;
import org.netbeans.modules.cnd.antlr.TokenStream;
import java.lang.ref.SoftReference;
import java.util.*;

import org.netbeans.modules.cnd.api.model.*;
import org.netbeans.modules.cnd.api.model.deep.*;
import org.netbeans.modules.cnd.modelimpl.csm.core.*;

import org.netbeans.modules.cnd.antlr.collections.AST;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javax.swing.SwingUtilities;
import org.netbeans.modules.cnd.modelimpl.parser.spi.CsmParserProvider;
import org.netbeans.modules.cnd.utils.CndUtils;

/**
 * Lazy statements
 *
 * @author Vladimir Kvashin, Nikolay Krasilnikov (nnnnnk@netbeans.org)
 */
abstract public class LazyStatementImpl extends StatementBase implements CsmScope {

    private volatile SoftReference<List<CsmStatement>> statements = null;

    protected LazyStatementImpl(CsmFile file, int start, int end, CsmFunction scope) {
        super(file, start, end, scope);
    }

    @Override
    public CsmStatement.Kind getKind() {
        return CsmStatement.Kind.COMPOUND;
    }

    public List<CsmStatement> getStatements() {
        if (statements == null) {
            return createStatements();
        } else {
            List<CsmStatement> list = statements.get();
            return (list == null) ? createStatements() : list;
        }
    }

    /**
     * 1) Creates a list of statements
     * 2) If it is created successfully, stores a soft reference to this list
     *	  and returns this list,
     *    otherwise just returns empty list
     */
    private List<CsmStatement> createStatements() {
        List<CsmStatement> list = statements == null ? null : statements.get();
        if (list == null) {
            list = new ArrayList<CsmStatement>();
            // code completion tests do work in EDT because otherwise EDT thread is not started by test harness
            CndUtils.assertTrueInConsole(!SwingUtilities.isEventDispatchThread() || CndUtils.isCodeCompletionUnitTestMode(), "Calling Parser in UI Thread");
            synchronized (this) {
                if (statements != null) {
                    List<CsmStatement> refList = statements.get();
                    if (refList != null) {
                        return refList;
                    }
                }
                statements = new SoftReference<List<CsmStatement>>(list);
                if (renderStatements(list)) {
                    return list;
                } else {
                    return Collections.emptyList();
                }
            }
        }
        return list;
    }

    private boolean renderStatements(List<CsmStatement> list) {
        FileImpl file = (FileImpl) getContainingFile();
        TokenStream stream = file.getTokenStream(getStartOffset(), getEndOffset(), getFirstTokenID(), true);
        if (stream == null) {
            Utils.LOG.log(Level.SEVERE, "Can\'t create compound statement: can\'t create token stream for file {0}", file.getAbsolutePath()); // NOI18N
            return false;
        } else {
            CsmParserProvider.CsmParserResult result = resolveLazyStatement(stream);
            if (result != null) {
                result.render(list);
            }
            return true;
        }
    }

    public void renderStatements(AST ast, List<CsmStatement> list) {
        for (ast = (ast == null ? null : ast.getFirstChild()); ast != null; ast = ast.getNextSibling()) {
            CsmStatement stmt = AstRenderer.renderStatement(ast, getContainingFile(), this);
            if (stmt != null) {
                list.add(stmt);
            }
        }
    }

    @Override
    public Collection<CsmScopeElement> getScopeElements() {
        // statements are scope elements
        @SuppressWarnings("unchecked")
        Collection<CsmScopeElement> out = (Collection<CsmScopeElement>) ((List<? extends CsmScopeElement>) getStatements());
        return out;
    }

    abstract protected CsmParserProvider.CsmParserResult resolveLazyStatement(TokenStream tokenStream);
    abstract protected int/*CPPTokenTypes*/ getFirstTokenID();    

    @Override
    public void write(DataOutput output) throws IOException {
        super.write(output);
    }

    public LazyStatementImpl(DataInput input) throws IOException {
        super(input);
        this.statements = null;
    }
}
