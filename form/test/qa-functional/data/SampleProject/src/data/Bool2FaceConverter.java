/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package data;

/**
 * Simple binding convertor from boolean values to smilies and back
 * 
 * @author Jiri Vagner
 */
public class Bool2FaceConverter extends org.jdesktop.beansbinding.Converter {
    private static String TRUE_FACE = ":)";  // NOI18N
    private static String FALSE_FACE = ":(";  // NOI18N
    
    public Object convertForward(Object arg) {
        return ((Boolean) arg) ? TRUE_FACE : FALSE_FACE; 
    }

    public Object convertReverse(Object arg) {
        return ((String) arg).equals(TRUE_FACE);
    }
}