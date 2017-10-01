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

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import classycle.graph.Attributes;
import classycle.graph.NameAttributes;

/**
 * Abstract super class of {@link Attributes} with a name and a set of sources.
 *
 * @author Franz-Josef Elmer
 */
public abstract class NameAndSourceAttributes extends NameAttributes {

    private final Set<String> _sources = new TreeSet<>();

    /**
     * Creates an instance for the specified name. Initially there are no sources.
     */
    public NameAndSourceAttributes(String name) {
        super(name);
    }

    /**
     * Adds the specified source.
     */
    protected void addSource(String source) {
        _sources.add(source);
    }

    /**
     * Adds the source of the specified attributes.
     */
    protected void addSourcesOf(NameAndSourceAttributes attributes) {
        _sources.addAll(attributes._sources);
    }

    /**
     * Returns a comma separated list of sources.
     */
    public String getSources() {
        StringBuffer buffer = new StringBuffer();
        for (Iterator<String> iterator = _sources.iterator(); iterator.hasNext();) {
            String source = (String) iterator.next();
            if (source.length() > 0) {
                if (buffer.length() > 0) {
                    buffer.append(", ");
                }
                buffer.append(source);
            }
        }
        return buffer.toString();
    }
}
