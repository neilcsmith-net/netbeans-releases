/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.css.lib.api.properties.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import org.netbeans.modules.css.lib.api.properties.Node;


/**
 *
 * @author marekfukala
 */
public class NodeModel implements SemanticModel {

    private Node node;
    
    private Collection<NodeModel> submodels = new ArrayList<NodeModel>();

    public NodeModel(Node node) {
        this.node = node;
    }

    protected NodeModel() {
    }
    
    protected Node getNode() {
        return node;
    }
    
    protected Collection<NodeModel> getSubmodels() {
        return submodels;
    }

    protected void setSubmodel(String submodelClassName, NodeModel model) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        String fieldName = getSubmodelFieldName(submodelClassName);
        Class<? extends NodeModel> currentModelClass = getClass();
        Field field = currentModelClass.getField(fieldName);
        field.setAccessible(true);
        field.set(this, model);
        
        submodels.add(model);
    }
    
    static String getSubmodelFieldName(String submodelClassName) {
        StringBuilder sb = new StringBuilder(submodelClassName);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(getClass().getSimpleName())
                .append(":")
                .append(getNode() != null ? getNode().image() : super.toString())
                .toString();
    }

    @Override
    public String getName() {
        return new StringBuilder().append("NodeModel for: ").append(toString()).toString(); //NOI18N
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getCategoryName() {
        return null;
    }
    
    

}
