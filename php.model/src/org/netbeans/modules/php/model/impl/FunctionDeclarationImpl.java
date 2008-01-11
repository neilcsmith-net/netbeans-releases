/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.php.model.impl;

import java.util.List;

import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTToken;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.model.FormalParameterList;
import org.netbeans.modules.php.model.FunctionDeclaration;
import org.netbeans.modules.php.model.PhpModelVisitor;
import org.netbeans.modules.php.model.SourceElement;
import org.netbeans.modules.php.model.impl.factory.FunctionDeclarationFactory;


/**
 * @author ads
 *
 */
public class FunctionDeclarationImpl extends SourceElementImpl 
    implements FunctionDeclaration 
{

    public FunctionDeclarationImpl( SourceElement parent, ASTNode node, 
            ASTNode realNode ,TokenSequence<?> sequence ) 
    {
        super(parent, node, realNode , sequence);
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.php.model.FunctionDeclaration#getName()
     */
    public String getName() {
        ASTNode node = getNarrowNode();
        node = node.getNode( FunctionDeclarationFactory.NAME );
        if ( node == null ) {
            return null;
        }
        return node.getAsText().trim();
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.php.model.FunctionDeclaration#getParamaterList()
     */
    public FormalParameterList getParamaterList() {
        return getChild( FormalParameterList.class );
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.php.model.FunctionDeclaration#hasReturnReference()
     */
    public boolean hasReturnReference() {
        ASTNode node = getNarrowNode();
        List<ASTItem> children = node.getChildren();
        for (ASTItem child : children) {
            if ( child instanceof ASTToken ) {
                ASTToken token = (ASTToken) child;
                if ( token.getIdentifier().equals( Utils.REFERENCE)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.netbeans.modules.php.model.Acceptor#accept(org.netbeans.modules.php.model.PhpModelVisitor)
     */
    public void accept( PhpModelVisitor visitor ) {
        visitor.visit( this );
    }


    /* (non-Javadoc)
     * @see org.netbeans.modules.php.model.SourceElement#getElementType()
     */
    public Class<? extends SourceElement> getElementType() {
        return FunctionDeclaration.class;
    }


}
