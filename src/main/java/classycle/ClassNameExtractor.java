/*
 * Copyright (c) 2003-2008, Franz-Josef Elmer, All rights reserved.
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
 */
/*
 * Created on 24.09.2006
 */
package classycle;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import classycle.classfile.UTF8Constant;

class ClassNameExtractor {

    /** Returns <tt>true</tt> if <tt>className</tt> is a valid class name. */
    static boolean isValid(String className) {
        boolean valid = true;
        boolean firstCharacter = true;
        for (int i = 0, n = className.length(); valid && i < n; i++) {
            char c = className.charAt(i);
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

    private final String _constant;

    private int _index;
    private int _endIndex;
    private Set<String> _classNames = new LinkedHashSet<>();
    private boolean _valid = true;

    ClassNameExtractor(UTF8Constant constant) {
        _constant = constant.getString();
        _endIndex = _constant.length();
    }

    Set<String> extract() {
        if (getCurrentCharacter() == '<') {
            int ddIndex = _constant.indexOf("::", _index);
            if (ddIndex > 0) {
                _index = ddIndex + 2;
                parseTypes(false);
                if (getCurrentCharacter() == '>') {
                    _index++;
                } else {
                    setInvalid();
                }
            } else {
                setInvalid();
            }
        }
        if (getCurrentCharacter() == '(') {
            int endIndex = _constant.indexOf(')', _index);
            if (endIndex > 0) {
                _index++;
                _endIndex = endIndex;
                parseTypes(false);
                _index = endIndex + 1;
                _endIndex = _constant.length();
            } else {
                setInvalid();
            }
        }
        if (_valid) {
            int numberOfTypes = parseTypes(false);
            if (numberOfTypes == 0) {
                setInvalid();
            }
        }
        return _valid ? _classNames : Collections.emptySet();
    }

    private int parseTypes(boolean generics) {
        int numberOfTypes = 0;
        while (_valid && endOfTypes() == false) {
            parseType(generics);
            numberOfTypes++;
        }
        return numberOfTypes;
    }

    private char getCurrentCharacter() {
        return _index < _endIndex ? _constant.charAt(_index) : 0;
    }

    private void setInvalid() {
        _valid = false;
    }

    private boolean endOfTypes() {
        return _index >= _endIndex || _constant.charAt(_index) == '>';
    }

    private void parseType(boolean generics) {
        if (generics) {
            char currentCharacter = getCurrentCharacter();
            if (currentCharacter == '+') {
                _index++;
            } else if (currentCharacter == '*') {
                _index++;
                return;
            }
        }
        boolean arrayType = false;
        for (; getCurrentCharacter() == '['; _index++) {
            arrayType = true;
        }
        if (arrayType && endOfTypes()) {
            setInvalid();
        } else {
            char c = getCurrentCharacter();
            _index++;
            if (c == 'L') {
                parseComplexType();
            } else if (c == 'T') {
                int index = _constant.indexOf(';', _index);
                if (index < 0) {
                    setInvalid();
                } else {
                    _index = index + 1;
                }
            } else if ("BCDFIJSVZ".indexOf(c) < 0) {
                setInvalid();
            }
        }
    }

    private void parseComplexType() {
        int typeIndex = _constant.indexOf('<', _index);
        int endIndex = _constant.indexOf(';', _index);
        if (typeIndex >= 0 && typeIndex < endIndex) {
            extractClassName(typeIndex);
            parseTypes(true);
            _index += 2;
        } else if (endIndex > 0) {
            extractClassName(endIndex);
        } else {
            setInvalid();
        }
    }

    private void extractClassName(int endIndex) {
        String className = _constant.substring(_index, endIndex);
        className = className.replace('/', '.');
        _classNames.add(className);
        _index = endIndex + 1;
        if (isValid(className) == false) {
            setInvalid();
        }
    }

}
