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
package org.netbeans.modules.visualweb.designer.cssengine;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.InheritValue;
import org.apache.batik.css.engine.value.LengthManager;
import org.apache.batik.css.engine.value.Value;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

/**
 * This class provides a manager for the "text-indent" CSS property,
 * as spec'ed in the CSS2.1 spec section 16.1.
 *
 * @author Tor Norbye
 */
public class TextIndentManager extends LengthManager {

    public boolean isInheritedProperty() {
	return true;
    }

    public String getPropertyName() {
        return CssConstants.CSS_TEXT_INDENT_PROPERTY;
    }

    public Value getDefaultValue() {
        return CssValueConstants.NUMBER_0;
    }

    protected int getOrientation() {
        return VERTICAL_ORIENTATION;
    }

    public Value createValue(LexicalUnit lu, CSSEngine engine)
        throws DOMException {
	if (lu.getLexicalUnitType() == LexicalUnit.SAC_INHERIT) {
	    return InheritValue.INSTANCE;
        }
        return super.createValue(lu, engine);
    }
}
