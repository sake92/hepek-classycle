/*
 * Copyright (c) 2003-2011, Franz-Josef Elmer, All rights reserved.
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
    private final Vertex[] _graph;
    private final VertexCondition _startSetCondition;
    private final VertexCondition _finalSetCondition;
    private final Set<Vertex> _vertices = new HashSet<Vertex>();

    public DependencyPathsRenderer(Vertex[] graph, StringPattern startSetPattern, StringPattern finalSetPattern) {
        this(graph, new PatternVertexCondition(startSetPattern), new PatternVertexCondition(finalSetPattern));
    }

    public DependencyPathsRenderer(Vertex[] graph, VertexCondition startSetCondition,
            VertexCondition finalSetCondition) {
        _graph = graph;
        _startSetCondition = startSetCondition;
        _finalSetCondition = finalSetCondition;
        for (int i = 0; i < graph.length; i++) {
            _vertices.add(graph[i]);
        }
    }

    public String renderGraph(final String lineStart) {
        final StringBuffer buffer = new StringBuffer();
        DependencyPathRenderer renderer = new DependencyPathRenderer() {

            String _start = '\n' + lineStart;
            private int _indentation;

            public void increaseIndentation() {
                _indentation++;
            }

            public void add(String nodeName) {
                buffer.append(_start);
                for (int i = 0; i < _indentation; i++) {
                    buffer.append(INDENT);
                }
                if (_indentation > 0) {
                    buffer.append("-> ");
                }
                buffer.append(nodeName);
            }

            public void decreaseIndentation() {
                _indentation--;
            }

        };
        renderGraph(renderer);

        return new String(buffer);
    }

    public void renderGraph(DependencyPathRenderer renderer) {
        Set<Vertex> visitedVertices = new HashSet<Vertex>();
        for (int i = 0; i < _graph.length; i++) {
            Vertex vertex = _graph[i];
            if (_startSetCondition.isFulfilled(vertex)) {
                renderer.add(getNameOf(vertex));
                renderPaths(renderer, vertex, visitedVertices);
            }
        }
    }

    private void renderPaths(DependencyPathRenderer renderer, Vertex vertex, Set<Vertex> visitedVertices) {
        visitedVertices.add(vertex);
        renderer.increaseIndentation();
        for (int i = 0, n = vertex.getNumberOfOutgoingArcs(); i < n; i++) {
            Vertex headVertex = vertex.getHeadVertex(i);
            if (_vertices.contains(headVertex) && !_startSetCondition.isFulfilled(headVertex)) {
                renderer.add(getNameOf(headVertex));
                if (!_finalSetCondition.isFulfilled(headVertex) && !visitedVertices.contains(headVertex)) {
                    renderPaths(renderer, headVertex, visitedVertices);
                }
            }
        }
        renderer.decreaseIndentation();
    }

    private String getNameOf(Vertex vertex) {
        return ((NameAttributes) vertex.getAttributes()).getName();
    }
}
