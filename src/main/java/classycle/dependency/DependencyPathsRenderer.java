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

import java.util.HashSet;
import java.util.Set;
import classycle.graph.NameAttributes;
import classycle.graph.Vertex;
import classycle.graph.VertexCondition;
import classycle.util.StringPattern;

/**
 * @author Franz-Josef Elmer
 */
public class DependencyPathsRenderer {

    private static final String INDENT = "  ";
    private final Vertex[] graph;
    private final VertexCondition startSetCondition;
    private final VertexCondition finalSetCondition;
    private final Set<Vertex> vertices = new HashSet<>();

    public DependencyPathsRenderer(Vertex[] graph, StringPattern startSetPattern, StringPattern finalSetPattern) {
        this(graph, new PatternVertexCondition(startSetPattern), new PatternVertexCondition(finalSetPattern));
    }

    public DependencyPathsRenderer(Vertex[] graph, VertexCondition startSetCondition,
            VertexCondition finalSetCondition) {
        this.graph = graph;
        this.startSetCondition = startSetCondition;
        this.finalSetCondition = finalSetCondition;
        for (int i = 0; i < graph.length; i++) {
            vertices.add(graph[i]);
        }
    }

    private String getNameOf(Vertex vertex) {
        return ((NameAttributes) vertex.getAttributes()).getName();
    }

    public void renderGraph(DependencyPathRenderer renderer) {
        final Set<Vertex> visitedVertices = new HashSet<>();
        for (int i = 0; i < graph.length; i++) {
            final Vertex vertex = graph[i];
            if (startSetCondition.isFulfilled(vertex)) {
                renderer.add(getNameOf(vertex));
                renderPaths(renderer, vertex, visitedVertices);
            }
        }
    }

    public String renderGraph(final String lineStart) {
        final StringBuilder builder = new StringBuilder();
        final DependencyPathRenderer renderer = new DependencyPathRenderer() {

            String start = '\n' + lineStart;
            private int indentation;

            @Override
            public void add(String nodeName) {
                builder.append(start);
                for (int i = 0; i < indentation; i++) {
                    builder.append(INDENT);
                }
                if (indentation > 0) {
                    builder.append("-> ");
                }
                builder.append(nodeName);
            }

            @Override
            public void decreaseIndentation() {
                indentation--;
            }

            @Override
            public void increaseIndentation() {
                indentation++;
            }

        };
        renderGraph(renderer);

        return builder.toString();
    }

    private void renderPaths(DependencyPathRenderer renderer, Vertex vertex, Set<Vertex> visitedVertices) {
        visitedVertices.add(vertex);
        renderer.increaseIndentation();
        for (int i = 0, n = vertex.getNumberOfOutgoingArcs(); i < n; i++) {
            final Vertex headVertex = vertex.getHeadVertex(i);
            if (vertices.contains(headVertex) && !startSetCondition.isFulfilled(headVertex)) {
                renderer.add(getNameOf(headVertex));
                if (!finalSetCondition.isFulfilled(headVertex) && !visitedVertices.contains(headVertex)) {
                    renderPaths(renderer, headVertex, visitedVertices);
                }
            }
        }
        renderer.decreaseIndentation();
    }
}
