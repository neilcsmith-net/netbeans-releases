/*
 * Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is the Jemmy library.
 * The Initial Developer of the Original Code is Alexandre Iline.
 * All Rights Reserved.
 * 
 * Contributor(s): Alexandre Iline.
 * 
 * $Id$ $Revision$ $Date$
 * 
 */

package org.netbeans.jemmy.util;

import java.lang.reflect.InvocationTargetException;

import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.JemmyException;

import org.netbeans.jemmy.operators.Operator.StringComparator;

/**
 * Be executed under 1.4 uses <code>java.util.regex.Pattern</code> functionality.
 * Otherwise understands only "." and "*" simbols, i.e. regexprs like ".*Ques.ion.*".
 */
public class RegExComparator implements StringComparator {
    private static final int ANY_SIMBOL = -1;
    private static final int IGNORE_SIMBOL = -999;
    public boolean equals(String caption, String match) {
        if(System.getProperty("java.version").startsWith("1.4")) {
            try {
                Object result = new ClassReference("java.util.regex.Pattern").
                    invokeMethod("matches", 
                                 new Object[] {match, caption}, 
                                 new Class[]  {String.class, Class.forName("java.lang.CharSequence")});
                return(((Boolean)result).booleanValue());
            } catch(InvocationTargetException e) {
                throw(new JemmyException("Exception during regexpr using",
                                         e));
            } catch(ClassNotFoundException e) {
                throw(new JemmyException("Exception during regexpr using",
                                         e));
            } catch(NoSuchMethodException e) {
                throw(new JemmyException("Exception during regexpr using",
                                         e));
            } catch(IllegalAccessException e) {
                throw(new JemmyException("Exception during regexpr using",
                                         e));
            }
        } else {
            return(parse(new String(caption), new String(match)));
        }
    }
    public boolean parse(String caption, String match) {
        if(match.length() == 0 &&
           caption.length() == 0) {
            return(true);
        } else if(match.length() == 0) {
            return(false);
        }
        int c0 = match.charAt(0);
        int c1 = IGNORE_SIMBOL;
        if(match.length() > 1) {
            c1 = match.charAt(1);
        }
        int shift = 1;
        switch(c0) {
        case '\\':
            if(match.length() == 1) {
                throw(new RegExParsingException("\\ is not appropriate"));
            }
            c0 = match.charAt(1);
            if(match.length() > 2) {
                c1 = match.charAt(2);
            } else {
                c1 = IGNORE_SIMBOL;
            }
            shift = 2;
            break;
        case '.':
            c0 = ANY_SIMBOL;
            break;
        case '*':
            throw(new RegExParsingException("* is not appropriate"));
        }
        if(c1 == '*') {
            shift = shift + 1;
            int i = 0;
            while(i <= caption.length()) {
                if(i == 0 ||
                   checkOne(caption.substring(i-1), c0)) {
                    if(parse(caption.substring(i), match.substring(shift))) {
                        return(true);
                    }
                } else {
                    return(false);
                }
                i++;
            }
            return(false);
        } else {
            if(caption.length() == 0) {
                return(false);
            }
            if(checkOne(caption, c0)) {
                return(parse(caption.substring(1), match.substring(shift)));
            } else {
                return(false);
            }
        }
    }
    private boolean checkOne(String caption, int simbol) {
        return(simbol == ANY_SIMBOL ||
               simbol == caption.charAt(0));
    }
    public static class RegExParsingException extends JemmyException {
        public RegExParsingException(String message) {
            super(message);
        }
        public RegExParsingException(String message, Exception innerException) {
            super(message, innerException);
        }
    }
}
