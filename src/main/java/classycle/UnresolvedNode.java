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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import classycle.util.StringPattern;

/**
 * Class representing a node without resolved links.
 *
 * @author Franz-Josef Elmer
 */
class UnresolvedNode {

    private ClassAttributes attributes;
    private final List<String> nodes = new ArrayList<>();

    public void addLinkTo(String node) {
        nodes.add(node);
    }

    public ClassAttributes getAttributes() {
        return attributes;
    }

    public boolean isMatchedBy(StringPattern pattern) {
        return pattern.matches(getAttributes().getName());
    }

    public Iterator<String> linkIterator() {
        return new Iterator<String>() {

            private int index;

            @Override
            public boolean hasNext() {
                return index < nodes.size();
            }

            @Override
            public String next() {
                return hasNext() ? nodes.get(index++) : null;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public void setAttributes(ClassAttributes attributes) {
        this.attributes = attributes;
    }

    public static Comparator<UnresolvedNode> comparatorByClassName() {
        return (o1, o2) -> o1.getAttributes().getName().compareTo(o2.getAttributes().getName());
    }

}
