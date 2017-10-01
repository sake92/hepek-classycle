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
package classycle.dependency;

import classycle.graph.AtomicVertex;
import classycle.util.StringPattern;

/**
 * Result of a dependency check.
 * 
 * @author Franz-Josef Elmer
 */
public class DependencyResult implements Result {

    static final String OK = "\tOK";
    static final String DEPENDENCIES_FOUND = "\n  Unexpected dependencies found:";
    private final StringPattern _startSet;
    private final StringPattern _finalSet;
    private final String _statement;
    private final AtomicVertex[] _paths;
    private final boolean _ok;

    public DependencyResult(StringPattern startSet, StringPattern finalSet, String statement, AtomicVertex[] paths) {
        _startSet = startSet;
        _finalSet = finalSet;
        _statement = statement;
        _paths = paths;
        _ok = paths.length == 0;
    }

    /**
     * Returns <code>true</code> if and only if {@link #getPaths()} is empty.
     */
    public boolean isOk() {
        return _ok;
    }

    /**
     * Returns the statement causing this result.
     */
    public String getStatement() {
        return _statement;
    }

    /**
     * Returns the pattern describing the final set.
     */
    public StringPattern getFinalSet() {
        return _finalSet;
    }

    /**
     * Returns the vertices of the paths of unwanted dependencies.
     * 
     * @return an empty array if no unwanted dependencies have been found.
     */
    public AtomicVertex[] getPaths() {
        return _paths;
    }

    /**
     * Returns the pattern describing the start set.
     */
    public StringPattern getStartSet() {
        return _startSet;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(_statement);
        if (_ok) {
            buffer.append(OK);
        } else {
            DependencyPathsRenderer renderer = new DependencyPathsRenderer(_paths, _startSet, _finalSet);
            buffer.append(DEPENDENCIES_FOUND).append(renderer.renderGraph("  "));
        }
        return new String(buffer.append('\n'));
    }
}
