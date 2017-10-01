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

    private static final String OK = "\tOK";
    private static final String DEPENDENCIES_FOUND = "\n  Unexpected dependencies found:";
    private final StringPattern startSet;
    private final StringPattern finalSet;
    private final String statement;
    private final AtomicVertex[] paths;
    private final boolean ok;

    public DependencyResult(StringPattern startSet, StringPattern finalSet, String statement, AtomicVertex[] paths) {
        this.startSet = startSet;
        this.finalSet = finalSet;
        this.statement = statement;
        this.paths = paths;
        ok = paths.length == 0;
    }

    /**
     * Returns the pattern describing the final set.
     */
    public StringPattern getFinalSet() {
        return finalSet;
    }

    /**
     * Returns the vertices of the paths of unwanted dependencies.
     *
     * @return an empty array if no unwanted dependencies have been found.
     */
    public AtomicVertex[] getPaths() {
        return paths;
    }

    /**
     * Returns the pattern describing the start set.
     */
    public StringPattern getStartSet() {
        return startSet;
    }

    /**
     * Returns the statement causing this result.
     */
    public String getStatement() {
        return statement;
    }

    /**
     * Returns <code>true</code> if and only if {@link #getPaths()} is empty.
     */
    @Override
    public boolean isOk() {
        return ok;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(statement);
        if (ok) {
            builder.append(OK);
        } else {
            final DependencyPathsRenderer renderer = new DependencyPathsRenderer(paths, startSet, finalSet);
            builder.append(DEPENDENCIES_FOUND).append(renderer.renderGraph("  "));
        }
        return builder.append('\n').toString();
    }
}
