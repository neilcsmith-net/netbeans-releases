package antlr;

/* ANTLR Translator Generator
 * Project led by Terence Parr at http://www.cs.usfca.edu
 * Software rights: http://www.antlr.org/license.html
 */

/**A Stream of characters fed to the lexer from a InputStream that can
 * be rewound via mark()/rewind() methods.
 * <p>
 * A dynamic array is used to buffer up all the input characters.  Normally,
 * "k" characters are stored in the buffer.  More characters may be stored during
 * guess mode (testing syntactic predicate), or when LT(i>k) is referenced.
 * Consumption of characters is deferred.  In other words, reading the next
 * character is not done by conume(), but deferred until needed by LA or LT.
 * <p>
 *
 * @see antlr.CharQueue
 */

import java.io.IOException;
import java.io.Reader;

// SAS: Move most functionality into InputBuffer -- just the file-specific
//      stuff is in here

public class CharBuffer extends InputBuffer {
    /** Create a character buffer */
    public CharBuffer(Reader input) { // SAS: for proper text i/o
        int readChunkSize = INITIAL_BUFFER_SIZE;
        try {
            int numRead=0;
            int pos = 0;
            do {
                    if ( pos+readChunkSize > data.length ) { // overflow?
                        resizeData(0);
                    }
                    numRead = input.read(data, pos, readChunkSize);
                    pos += numRead;
            } while (numRead==readChunkSize);

            if ( pos == data.length ) { //unable to append EOF
                resizeData(1);
            }
            data[pos] = CharScanner.EOF_CHAR; // Append EOF
	} catch (IOException io) {
            System.err.println("tmp error: can't load input: " + io);
        }
    }
}
