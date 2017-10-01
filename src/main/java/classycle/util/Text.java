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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Collection of useful static method concerning string manipulation.
 *
 * @author Franz-Josef Elmer
 */
public final class Text {

    private static final String ESCAPE_CHARACTERS = "<>&\"'";
    private static final String[] ESCAPE_SEQUENCES = new String[] { "&lt;", "&gt;", "&amp;", "&quot;", "&apos;" };

    private Text() {
    }

    /**
     * Escapes special XML characters in the specified text.
     *
     * @param text
     *            Text to be escaped. Must be not <tt>null</tt>.
     * @return copy of the text where the special XML characters has been replaced by the escape sequences.
     */
    public static String excapeForXML(String text) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0, n = text.length(); i < n; i++) {
            final char c = text.charAt(i);
            final int index = ESCAPE_CHARACTERS.indexOf(c);
            if (index < 0) {
                builder.append(c);
            } else {
                builder.append(ESCAPE_SEQUENCES[index]);
            }
        }
        return builder.toString();
    }

    /**
     * Reads multi-line text from the specified file.
     *
     * @param file
     *            Text file.
     * @return read text file with standard Java newline characters.
     * @throws IOException
     *             if some reading error occurs.
     */
    public static String readTextFile(File file) throws IOException {
        final List<String> lines = Files.readAllLines(file.toPath());
        return String.join("\n", lines);
    }

}
