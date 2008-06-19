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
package org.netbeans.modules.websvc.saas.codegen.java;

import java.io.IOException;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.websvc.saas.codegen.Constants;
import org.netbeans.modules.websvc.saas.codegen.Constants.SaasAuthenticationType;
import org.netbeans.modules.websvc.saas.codegen.model.CustomClientSaasBean;
import org.netbeans.modules.websvc.saas.codegen.model.ParameterInfo;
import org.netbeans.modules.websvc.saas.codegen.util.Util;
import org.netbeans.modules.websvc.saas.model.CustomSaasMethod;
import org.netbeans.modules.websvc.saas.model.SaasMethod;

/**
 * Code generator for Accessing Saas services.
 *
 * @author nam
 */
public class CustomClientServletCodeGenerator extends CustomClientPojoCodeGenerator {

    public CustomClientServletCodeGenerator() {
        setDropFileType(Constants.DropFileType.SERVLET);
    }
    
    @Override
    public void init(SaasMethod m, Document doc) throws IOException {
        super.init(m, new CustomClientSaasBean((CustomSaasMethod) m, true), doc);
    }
    
    @Override
    public boolean canAccept(SaasMethod method, Document doc) {
        if (method instanceof CustomSaasMethod && 
                Util.isServlet(NbEditorUtilities.getDataObject(doc))) {
            return true;
        }
        return false;
    }
    
    @Override
    protected List<ParameterInfo> getServiceMethodParameters() {
        if(getBean().getAuthenticationType() == SaasAuthenticationType.SESSION_KEY ||
                getBean().getAuthenticationType() == SaasAuthenticationType.HTTP_BASIC)
            return getServiceMethodParametersForWeb(getBean());
        else
            return super.getServiceMethodParameters();
    }
    
    
    protected String getCustomMethodBody(String paramDecl, String paramUse, String indent2) {
        String indent = "             ";
        String methodBody = "";
        methodBody += indent+"try {\n";
        methodBody += paramDecl + "\n";
        methodBody +=indent2+REST_RESPONSE+" result = " + getBean().getSaasServiceName() + 
                "." + getBean().getSaasServiceMethodName() + "(" + paramUse + ");\n";
        methodBody += Util.createPrintStatement(
                getBean().getOutputWrapperPackageName(), 
                getBean().getOutputWrapperName(),
                getDropFileType(), 
                getBean().getHttpMethod(), 
                getBean().canGenerateJAXBUnmarshaller(), indent2);
        methodBody += indent+"} catch (Exception ex) {\n";
        methodBody += indent2+"ex.printStackTrace();\n";
        methodBody += indent+"}\n";
        return methodBody;
    }
    
    @Override
    protected List<ParameterInfo> getAuthenticatorMethodParameters() {
        if(getBean().getAuthenticationType() == SaasAuthenticationType.SESSION_KEY ||
                getBean().getAuthenticationType() == SaasAuthenticationType.HTTP_BASIC)
            return Util.getAuthenticatorMethodParametersForWeb();
        else
            return super.getAuthenticatorMethodParameters();
    }
    
    @Override
    protected String getLoginArguments() {
        return Util.getLoginArgumentsForWeb();
    }
}
