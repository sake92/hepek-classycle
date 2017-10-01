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
package classycle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import classycle.util.StringPattern;

/**
 * Class representing a node without resolved links.
 * 
 * @author Franz-Josef Elmer
 */
class UnresolvedNode implements Comparable {

    private ClassAttributes _attributes;
    private List<String> _nodes = new ArrayList<>();

    void setAttributes(ClassAttributes attributes) {
        _attributes = attributes;
    }

    ClassAttributes getAttributes() {
        return _attributes;
    }

    void addLinkTo(String node) {
        _nodes.add(node);
    }

    Iterator<String> linkIterator() {
        return new Iterator<String>() {

            private int _index;

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public boolean hasNext() {
                return _index < _nodes.size();
            }

            public String next() {
                return hasNext() ? _nodes.get(_index++) : null;
            }
        };
    }

    public int compareTo(Object obj) {
        return getAttributes().getName().compareTo(((UnresolvedNode) obj).getAttributes().getName());
    }

    public boolean isMatchedBy(StringPattern pattern) {
        return pattern.matches(getAttributes().getName());
    }
}
