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
package classycle;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import classycle.classfile.UTF8Constant;

class ClassNameExtractor {

    private final String constant;

    private int index;
    private int endIndex;
    private final Set<String> classNames = new LinkedHashSet<>();
    private boolean valid = true;

    ClassNameExtractor(UTF8Constant utf8Constant) {
        constant = utf8Constant.getString();
        endIndex = constant.length();
    }

    private boolean endOfTypes() {
        return index >= endIndex || constant.charAt(index) == '>';
    }

    Set<String> extract() {
        if (getCurrentCharacter() == '<') {
            final int ddIndex = constant.indexOf("::", index);
            if (ddIndex > 0) {
                index = ddIndex + 2;
                parseTypes(false);
                if (getCurrentCharacter() == '>') {
                    index++;
                } else {
                    setInvalid();
                }
            } else {
                setInvalid();
            }
        }
        if (getCurrentCharacter() == '(') {
            final int endBraceIndex = constant.indexOf(')', index);
            if (endBraceIndex > 0) {
                index++;
                endIndex = endBraceIndex;
                parseTypes(false);
                index = endBraceIndex + 1;
                endIndex = constant.length();
            } else {
                setInvalid();
            }
        }
        if (valid) {
            final int numberOfTypes = parseTypes(false);
            if (numberOfTypes == 0) {
                setInvalid();
            }
        }
        return valid ? classNames : Collections.emptySet();
    }

    private void extractClassName(int endIndex) {
        String className = constant.substring(index, endIndex);
        className = className.replace('/', '.');
        classNames.add(className);
        index = endIndex + 1;
        if (isValid(className) == false) {
            setInvalid();
        }
    }

    private char getCurrentCharacter() {
        return index < endIndex ? constant.charAt(index) : 0;
    }

    private void parseComplexType() {
        final int typeIndex = constant.indexOf('<', index);
        final int endIndex = constant.indexOf(';', index);
        if (typeIndex >= 0 && typeIndex < endIndex) {
            extractClassName(typeIndex);
            parseTypes(true);
            index += 2;
        } else if (endIndex > 0) {
            extractClassName(endIndex);
        } else {
            setInvalid();
        }
    }

    private void parseType(boolean generics) {
        if (generics) {
            final char currentCharacter = getCurrentCharacter();
            if (currentCharacter == '+') {
                index++;
            } else if (currentCharacter == '*') {
                index++;
                return;
            }
        }
        boolean arrayType = false;
        for (; getCurrentCharacter() == '['; index++) {
            arrayType = true;
        }
        if (arrayType && endOfTypes()) {
            setInvalid();
        } else {
            final char c = getCurrentCharacter();
            index++;
            if (c == 'L') {
                parseComplexType();
            } else if (c == 'T') {
                final int scIndex = constant.indexOf(';', index);
                if (scIndex < 0) {
                    setInvalid();
                } else {
                    index = scIndex + 1;
                }
            } else if ("BCDFIJSVZ".indexOf(c) < 0) {
                setInvalid();
            }
        }
    }

    private int parseTypes(boolean generics) {
        int numberOfTypes = 0;
        while (valid && endOfTypes() == false) {
            parseType(generics);
            numberOfTypes++;
        }
        return numberOfTypes;
    }

    private void setInvalid() {
        valid = false;
    }

    /** Returns <tt>true</tt> if <tt>className</tt> is a valid class name. */
    static boolean isValid(String className) {
        boolean valid = true;
        boolean firstCharacter = true;
        for (int i = 0, n = className.length(); valid && i < n; i++) {
            final char c = className.charAt(i);
            if (firstCharacter) {
                firstCharacter = false;
                valid = Character.isJavaIdentifierStart(c);
            } else {
                if (c == '.') {
                    firstCharacter = true;
                } else {
                    valid = Character.isJavaIdentifierPart(c);
                }
            }
        }
        return valid && firstCharacter == false;
    }

}
