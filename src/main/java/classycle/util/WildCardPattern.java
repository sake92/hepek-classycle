/*******************************************************************************
 * Copyright (c) 2003-2008, Franz-Josef Elmer, All rights reserved.
 * Copyright (c) 2017, Sakib Hadžiavdić, All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED 
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package classycle.util;

import java.util.StringTokenizer;

/**
 * Wildcard string pattern matching class. Only '*' is interpreted as wild card meaning the occurance of any number of
 * arbitray characters.
 * <p>
 * This is a thread-safe immutable class.
 * <p>
 * Example: The code snippet
 * 
 * <pre>
 * <tt>
 *   StringPattern pattern = new WildCardPattern("Hello*");
 *   System.out.println(pattern.matches("Hello world!"));
 *   System.out.println(pattern.matches("Hi Jim!"));
 * </tt>
 * </pre>
 * 
 * will produce the output
 * 
 * <pre>
 * <tt>
 * true
 * false
 * </tt>
 * </pre>
 * 
 * @author Franz-Josef Elmer
 */
public class WildCardPattern implements StringPattern {

    private static final String WILD_CARD = "*";

    /**
     * Returns a {@link StringPattern} object based on a sequences of wild-card patterns separated by the specified
     * delimiter characters. The return object matches a string if at least one of the wild-card pattern matches.
     * 
     * @param patterns
     *            Wild-card patterns separated by delimiters defined in <tt>delimiters</tt>. The actual pattern will be
     *            trimed. That is, leading and trailing white-space characters are removed.
     * @param delimiters
     *            Recognized delimiters.
     * @return
     */
    public static StringPattern createFromsPatterns(String patterns, String delimiters) {
        if (delimiters.indexOf(WILD_CARD) >= 0) {
            throw new IllegalArgumentException(
                    "No wild card '" + WILD_CARD + "' are allowed as delimiters: " + delimiters);
        }
        OrStringPattern result = new OrStringPattern();
        StringTokenizer tokenizer = new StringTokenizer(patterns, delimiters);
        while (tokenizer.hasMoreTokens()) {
            result.appendPattern(new WildCardPattern(tokenizer.nextToken().trim()));
        }
        return result;
    }

    private final String _pattern;
    private final String[] _constantParts;
    private final boolean _startsWithAnything;
    private final boolean _endsWithAnything;

    /**
     * Creates an instance based on the specified pattern.
     * 
     * @param pattern
     *            Pattern which may contain '*' wildcard characters. Must be not <tt>null</tt>.
     */
    public WildCardPattern(String pattern) {
        _pattern = pattern;
        _startsWithAnything = pattern.startsWith(WILD_CARD);
        _endsWithAnything = pattern.endsWith(WILD_CARD);
        StringTokenizer tokenizer = new StringTokenizer(pattern, WILD_CARD);
        _constantParts = new String[tokenizer.countTokens()];
        for (int i = 0; i < _constantParts.length; i++) {
            _constantParts[i] = tokenizer.nextToken();
        }
    }

    /**
     * Returns the pattern as delivered to the constructor.
     */
    public String toString() {
        return _pattern;
    }

    /**
     * @return <tt>false</tt> if <tt>string == null</tt>.
     */
    public boolean matches(String string) {
        return string == null ? false : matches(string, 0, 0);
    }

    private boolean matches(String string, int indexInString, int indexInConstantParts) {
        boolean result = true;
        if (indexInConstantParts < _constantParts.length) {
            String constantPart = _constantParts[indexInConstantParts];
            do {
                int index = string.indexOf(constantPart, indexInString);
                if (index < 0 || (indexInString == 0 && !_startsWithAnything && index > 0)) {
                    result = false;
                    break;
                }
                indexInString = index + constantPart.length();
                result = matches(string, indexInString, indexInConstantParts + 1);
            } while (result == false);
        } else {
            result = result && (_endsWithAnything || indexInString == string.length());
        }
        return result;
    }
}
