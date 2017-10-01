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
package classycle.graph;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Calculates for each vertex the longest walk. This processor assumes that the graph has no cycles.
 * 
 * @author Franz-Josef Elmer
 */
public class LongestWalkProcessor extends GraphProcessor {

    /** Does nothing. */
    protected void initializeProcessing(Vertex[] graph) {
    }

    /**
     * Resets the specified vertex.
     * 
     * @throws IllegalArgumentException
     *             if <tt>vertex</tt> is not an instance of {@link StrongComponent}.
     */
    protected void processBefore(Vertex vertex) {
        StrongComponent component = castAsStrongComponent(vertex);
        component.setActive(true);
        component.setLongestWalk(0);
    }

    /**
     * Processes arc from <tt>tail</tt> to <tt>head</tt>. Calculates the longest walk of <tt>tail</tt>.
     * 
     * @throws IllegalArgumentException
     *             if both vertices are not instances of {@link StrongComponent} or if <tt>head</tt> is visited and
     *             active which indicates a cycle in the graph.
     */
    protected void processArc(Vertex tail, Vertex head) {
        StrongComponent t = castAsStrongComponent(tail);
        StrongComponent h = castAsStrongComponent(head);
        if (!h.isVisited()) {
            process(h);
        } else if (h.isActive()) {
            // Oops! should never be happen if the graph has been created
            // with StrongComponentProcessor
            throw new IllegalArgumentException(h + " is not a strong component.");
        }
        t.setLongestWalk(Math.max(t.getLongestWalk(), 1 + h.getLongestWalk()));
    }

    /**
     * Deactivate the specified vertex.
     * 
     * @throws IllegalArgumentException
     *             if <tt>vertex</tt> is not an instance of {@link StrongComponent}.
     */
    protected void processAfter(Vertex vertex) {
        castAsStrongComponent(vertex).setActive(false);
    }

    /**
     * Finishes processing by sorting the result in accordance with the walk length.
     */
    protected void finishProcessing(Vertex[] graph) {
        Arrays.sort(graph, new Comparator() {
            public int compare(Object obj1, Object obj2) {
                return ((StrongComponent) obj1).getLongestWalk() - ((StrongComponent) obj2).getLongestWalk();
            }
        });
    }

    /**
     * Casts the specified vertex as a {@link StrongComponent}.
     * 
     * @throws IllegalArgumentException
     *             if <tt>vertex</tt> is not an instance of {@link StrongComponent}.
     */
    private StrongComponent castAsStrongComponent(Vertex vertex) {
        if (vertex instanceof StrongComponent) {
            return (StrongComponent) vertex;
        } else {
            throw new IllegalArgumentException(vertex + " is not an instance of StrongComponent");
        }
    }
} 
