/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.lib.editor.util;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

/**
 * Utility methods related to arrays.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public final class ArrayUtilities {

    private static boolean[] EMPTY_BOOLEAN_ARRAY;

    private static char[] EMPTY_CHAR_ARRAY;

    private static int[] EMPTY_INT_ARRAY;

    private ArrayUtilities() {
        // no instances
    }

    public static boolean[] booleanArray(boolean[] oldArray) {
        return booleanArray(oldArray, (oldArray != null) ? oldArray.length << 1 : 1);
    }

    public static boolean[] booleanArray(boolean[] oldArray, int newSize) {
        return booleanArray(oldArray, newSize,
                (oldArray != null) ? Math.min(newSize, oldArray.length) : 0, true);
    }
    
    public static boolean[] booleanArray(boolean[] oldArray, int newSize, int copyLen, boolean forwardFill) {
        boolean[] newArray = new boolean[newSize];
        if (copyLen > 0)
            if (forwardFill)
                System.arraycopy(oldArray, 0, newArray, 0, copyLen);
            else // backward fill
                System.arraycopy(oldArray, oldArray.length - copyLen,
                        newArray, newSize - copyLen, copyLen);
        return newArray;
    }

    public static char[] charArray(char[] oldArray) {
        return charArray(oldArray, (oldArray != null) ? oldArray.length << 1 : 1);
    }
    
    public static char[] charArray(char[] oldArray, int newSize) {
        return charArray(oldArray, newSize,
                (oldArray != null) ? Math.min(newSize, oldArray.length) : 0, true);
    }
    
    public static char[] charArray(char[] oldArray, int newSize, int copyLen, boolean forwardFill) {
        char[] newArray = new char[newSize];
        if (copyLen > 0)
            if (forwardFill)
                System.arraycopy(oldArray, 0, newArray, 0, copyLen);
            else // backward fill
                System.arraycopy(oldArray, oldArray.length - copyLen,
                        newArray, newSize - copyLen, copyLen);
        return newArray;
    }

    public static char[] charArray(char[] oldArray, int newSize, int gapStart, int gapLength) {
        char[] newArray = new char[newSize];
        if (gapStart > 0)
            System.arraycopy(oldArray, 0, newArray, 0, gapStart);
        gapStart += gapLength;
        gapLength = oldArray.length - gapStart;
        if (gapLength > 0)
            System.arraycopy(oldArray, gapStart,
                        newArray, newSize - gapLength, gapLength);
        return newArray;
    }

    public static int[] intArray(int[] oldArray) {
        return intArray(oldArray, (oldArray != null) ? oldArray.length << 1 : 1);
    }

    public static int[] intArray(int[] oldArray, int newSize) {
        return intArray(oldArray, newSize,
                (oldArray != null) ? Math.min(newSize, oldArray.length) : 0, true);
    }

    public static int[] intArray(int[] oldArray, int newSize, int copyLen, boolean forwardFill) {
        int[] newArray = new int[newSize];
        if (copyLen > 0)
            if (forwardFill)
                System.arraycopy(oldArray, 0, newArray, 0, copyLen);
            else // backward fill
                System.arraycopy(oldArray, oldArray.length - copyLen,
                        newArray, newSize - copyLen, copyLen);
        return newArray;
    }
    
    public static int[] intArray(int[] oldArray, int newSize, int gapStart, int gapLength) {
        int[] newArray = new int[newSize];
        if (gapStart > 0)
            System.arraycopy(oldArray, 0, newArray, 0, gapStart);
        gapStart += gapLength;
        gapLength = oldArray.length - gapStart;
        if (gapLength > 0)
            System.arraycopy(oldArray, gapStart,
                        newArray, newSize - gapLength, gapLength);
        return newArray;
    }
    
    public static boolean[] emptyBooleanArray() {
        if (EMPTY_BOOLEAN_ARRAY == null) { // unsynced intentionally
            EMPTY_BOOLEAN_ARRAY = new boolean[0];
        }
        return EMPTY_BOOLEAN_ARRAY;
    }

    public static char[] emptyCharArray() {
        if (EMPTY_CHAR_ARRAY == null) { // unsynced intentionally
            EMPTY_CHAR_ARRAY = new char[0];
        }
        return EMPTY_CHAR_ARRAY;
    }

    public static int[] emptyIntArray() {
        if (EMPTY_INT_ARRAY == null) { // unsynced intentionally
            EMPTY_INT_ARRAY = new int[0];
        }
        return EMPTY_INT_ARRAY;
    }

    public static int digitCount(int number) {
        return String.valueOf(Math.abs(number)).length();
    }

    public static void appendIndex(StringBuilder sb, int index, int maxDigitCount) {
        String indexStr = String.valueOf(index);
        appendSpaces(sb, maxDigitCount - indexStr.length());
        sb.append(indexStr);
    }

    public static void appendIndex(StringBuffer sb, int index, int maxDigitCount) {
        String indexStr = String.valueOf(index);
        appendSpaces(sb, maxDigitCount - indexStr.length());
        sb.append(indexStr);
    }

    public static void appendSpaces(StringBuilder sb, int spaceCount) {
        while (--spaceCount >= 0) {
            sb.append(' ');
        }
    }
    
    public static void appendSpaces(StringBuffer sb, int spaceCount) {
        while (--spaceCount >= 0) {
            sb.append(' ');
        }
    }
    
    public static void appendBracketedIndex(StringBuilder sb, int index, int maxDigitCount) {
        sb.append('[');
        appendIndex(sb, index, maxDigitCount);
        sb.append("]: ");
    }

    public static void appendBracketedIndex(StringBuffer sb, int index, int maxDigitCount) {
        sb.append('[');
        appendIndex(sb, index, maxDigitCount);
        sb.append("]: ");
    }
    
    /**
     * Return unmodifiable list for the given array.
     * <br/>
     * Unlike <code>Collections.unmodifiableList()</code> this method
     * does not use any extra wrappers etc.
     *
     * @since 1.14
     */
    public static <E> List<E> unmodifiableList(E[] array) {
        return new UnmodifiableList<E>(array);
    }
    
    public static String toString(Object[] array) {
        StringBuilder sb = new StringBuilder();
        int maxDigitCount = digitCount(array.length);
        for (int i = 0; i < array.length; i++) {
            appendBracketedIndex(sb, i, maxDigitCount);
            sb.append(array[i]);
            sb.append('\n');
        }
        return sb.toString();
    }

    public static String toString(int[] array) {
        StringBuilder sb = new StringBuilder();
        int maxDigitCount = digitCount(array.length);
        for (int i = 0; i < array.length; i++) {
            appendBracketedIndex(sb, i, maxDigitCount);
            sb.append(array[i]);
            sb.append('\n');
        }
        return sb.toString();
    }
    
    private static final class UnmodifiableList<E> extends AbstractList<E>
    implements RandomAccess {
        
        private E[] array;
        
        UnmodifiableList(E[] array) {
            this.array = array;
        }
        
        public E get(int index) {
            if (index >= 0 && index < array.length) {
                return array[index];
            } else {
                throw new IndexOutOfBoundsException("index = " + index + ", size = " + array.length); //NOI18N
            }
        }
        
        public int size() {
            return array.length;
        }
        

        public Object[] toArray() {
            return array.clone();
        }
        
        public <T> T[] toArray(T[] a) {
            if (a.length < array.length) {
                @SuppressWarnings("unchecked")
                T[] aa = (T[])java.lang.reflect.Array.
                        newInstance(a.getClass().getComponentType(), array.length);
                a = aa;
            }
            System.arraycopy(array, 0, a, 0, array.length);
            if (a.length > array.length)
                a[array.length] = null;
            return a;
        }

    }

    /**
     * Searches for the <code>key</code> in the <code>array</code>.
     * 
     * @param array the array to search in
     * @param key the number to search for
     * 
     * @return The index of the <code>key</code> in the <code>array</code>. For
     *   details see {@link #binarySearch(int[], int, int, int)}.
     * @since 1.18
     */
    public static int binarySearch(int[] array, int key) {
        return binarySearch(array, 0, array.length - 1, key);
    }
    
    /**
     * Searches for the <code>key</code> in the <code>array</code> in the area
     * between <code>start</code> and <code>end</code>.
     * 
     * @param array the array to search in
     * @param start the first index to look at
     * @param end the last index to look at
     * @param key the number to search for
     * 
     * @return The index of the search key, if it is contained in the array;
     *	       otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>.  The
     *	       <i>insertion point</i> is defined as the point at which the
     *	       key would be inserted into the array: the index of the first
     *	       element greater than the key, or <tt>array.length</tt>, if all
     *	       elements in the array are less than the specified key.  Note
     *	       that this guarantees that the return value will be &gt;= 0 if
     *	       and only if the key is found.
     * @since 1.18
     */
    public static int binarySearch(int[] array, int start, int end, int key) {
        assert start >= 0 && start < array.length : "Invalid start index " + start + " (length = " + array.length + ")"; //NOI18N
        assert end >= start && end < array.length : "Invalid end index " + end + " (start = " + start + ", length = " + array.length + ")"; //NOI18N
        
	int low = start;
	int high = end;

	while (low <= high) {
	    int mid = (low + high) >> 1;
	    int midVal = array[mid];
	    int cmp = midVal - key;

	    if (cmp < 0) {
		low = mid + 1;
            } else if (cmp > 0) {
		high = mid - 1;
            } else {
		return mid; // key found
            }
	}
        
	return -(low + 1);  // key not found
    }
}
