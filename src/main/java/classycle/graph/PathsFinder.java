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
package classycle.graph;

import java.util.HashSet;

/**
 * Class searching for all (or only the shortest) paths between classes of a start set and classes of a final set.
 * 
 * @author Franz-Josef Elmer
 */
public class PathsFinder {

    private final VertexCondition _startSetCondition;
    private final VertexCondition _finalSetCondition;
    private final boolean _shortestPathsOnly;
    private final boolean _directPathsOnly;

    /**
     * Creates an instance for the specified vertex conditions.
     * 
     * @param startSetCondition
     *            Condition defining the start set.
     * @param finalSetCondition
     *            Condition defining the final set.
     * @param shortestPathsOnly
     *            if <code>true</code> only the shortest paths are returned.
     */
    public PathsFinder(VertexCondition startSetCondition, VertexCondition finalSetCondition,
            boolean shortestPathsOnly) {
        this(startSetCondition, finalSetCondition, shortestPathsOnly, false);
    }

    /**
     * Creates an instance for the specified vertex conditions.
     * 
     * @param startSetCondition
     *            Condition defining the start set.
     * @param finalSetCondition
     *            Condition defining the final set.
     * @param shortestPathsOnly
     *            if <code>true</code> only the shortest paths are returned.
     * @param directPathsOnly
     *            if <code>true</code> only paths of length 1 are returned.
     */
    public PathsFinder(VertexCondition startSetCondition, VertexCondition finalSetCondition, boolean shortestPathsOnly,
            boolean directPathsOnly) {
        _startSetCondition = startSetCondition;
        _finalSetCondition = finalSetCondition;
        _shortestPathsOnly = shortestPathsOnly;
        _directPathsOnly = directPathsOnly;
    }

    public VertexCondition getFinalSetCondition() {
        return _finalSetCondition;
    }

    public boolean isShortestPathsOnly() {
        return _shortestPathsOnly;
    }

    public VertexCondition getStartSetCondition() {
        return _startSetCondition;
    }

    /**
     * Finds all paths from the specified start vertices to the vertices fullfilling the specified condition.
     * 
     * @param graph
     *            Complete graph.
     * @return All vertices including start and end vertices defining the subgraph with all paths.
     */
    public AtomicVertex[] findPaths(AtomicVertex[] graph) {
        prepareGraph(graph);
        HashSet<Vertex> pathVertices = new HashSet<>();
        HashSet<AtomicVertex> currentPath = new HashSet<>();
        for (int i = 0; i < graph.length; i++) {
            AtomicVertex vertex = graph[i];
            if (_startSetCondition.isFulfilled(vertex)) {
                if (_directPathsOnly) {
                    findDirectPaths(vertex, pathVertices);
                } else {
                    prepareIfFinal(vertex);
                    int pathLength = calculateShortestPath(vertex, currentPath);
                    if (pathLength < Integer.MAX_VALUE) {
                        vertex.setOrder(pathLength);
                        followPaths(vertex, pathVertices);
                    }
                }
            }
        }
        return pathVertices.toArray(new AtomicVertex[pathVertices.size()]);
    }

    private void findDirectPaths(AtomicVertex vertex, HashSet<Vertex> pathVertices) {
        if (_finalSetCondition.isFulfilled(vertex)) {
            pathVertices.add(vertex);
        } else {
            for (int i = 0, n = vertex.getNumberOfOutgoingArcs(); i < n; i++) {
                Vertex headVertex = vertex.getHeadVertex(i);
                if (_finalSetCondition.isFulfilled(headVertex)) {
                    pathVertices.add(vertex);
                    pathVertices.add(headVertex);
                }
            }
        }
    }

    private void prepareGraph(AtomicVertex[] graph) {
        for (int i = 0; i < graph.length; i++) {
            AtomicVertex vertex = graph[i];
            prepareVertex(vertex);
            for (int j = 0, n = vertex.getNumberOfOutgoingArcs(); j < n; j++) {
                prepareVertex((AtomicVertex) vertex.getHeadVertex(j));
            }
        }
    }

    private void prepareVertex(AtomicVertex vertex) {
        vertex.reset();
        vertex.setOrder(Integer.MAX_VALUE);
        if (_startSetCondition.isFulfilled(vertex)) {
            vertex.visit();
        }
    }

    private int calculateShortestPath(AtomicVertex vertex, HashSet<AtomicVertex> currentPath) {
        currentPath.add(vertex);
        int shortestPath = Integer.MAX_VALUE;
        for (int i = 0, n = vertex.getNumberOfOutgoingArcs(); i < n; i++) {
            AtomicVertex nextVertex = (AtomicVertex) vertex.getHeadVertex(i);
            prepareIfFinal(nextVertex);
            int pathLength = _startSetCondition.isFulfilled(nextVertex) ? Integer.MAX_VALUE : nextVertex.getOrder();
            if (!currentPath.contains(nextVertex) && !nextVertex.isVisited()) {
                pathLength = calculateShortestPath(nextVertex, currentPath);
                nextVertex.setOrder(pathLength);
                nextVertex.visit();
            }
            shortestPath = Math.min(shortestPath, pathLength);
        }
        currentPath.remove(vertex);
        if (shortestPath < Integer.MAX_VALUE) {
            shortestPath++;
        }
        return shortestPath;
    }

    private void prepareIfFinal(AtomicVertex vertex) {
        if (_finalSetCondition.isFulfilled(vertex)) {
            vertex.visit();
            vertex.setOrder(0);
        }
    }

    private void followPaths(AtomicVertex vertex, HashSet<Vertex> pathVertices) {
        pathVertices.add(vertex);
        int shortestPathLength = vertex.getOrder() - 1;
        for (int i = 0, n = vertex.getNumberOfOutgoingArcs(); i < n; i++) {
            AtomicVertex nextVertex = (AtomicVertex) vertex.getHeadVertex(i);
            int pathLength = nextVertex.getOrder();
            if (pathLength < Integer.MAX_VALUE && !pathVertices.contains(nextVertex)) {
                if (!_shortestPathsOnly || pathLength == shortestPathLength) {
                    pathVertices.add(nextVertex);
                    if (pathLength > 0) {
                        followPaths(nextVertex, pathVertices);
                    }
                }
            }
        }
    }

    // private int search(AtomicVertex vertex, HashSet currentPath,
    // HashSet pathVertices)
    // {
    // currentPath.add(vertex);
    // boolean found = false;
    // int shortestPath = Integer.MAX_VALUE;
    // for (int i = 0, n = vertex.getNumberOfOutgoingArcs(); i < n; i++)
    // {
    // AtomicVertex nextVertex = (AtomicVertex) vertex.getHeadVertex(i);
    // int pathLength = nextVertex.getOrder();
    // if (!nextVertex.isVisited() && !currentPath.contains(nextVertex))
    // {
    // pathLength = search(nextVertex, currentPath, pathVertices);
    // nextVertex.setOrder(pathLength);
    // nextVertex.visit();
    // }
    // shortestPath = Math.min(shortestPath, pathLength);
    // }
    // for (int i = 0, n = vertex.getNumberOfOutgoingArcs(); i < n; i++)
    // {
    // AtomicVertex nextVertex = (AtomicVertex) vertex.getHeadVertex(i);
    // int pathLength = nextVertex.getOrder();
    // if (pathLength < Integer.MAX_VALUE)
    // {
    // if (!_shortestPathsOnly || pathLength == shortestPath)
    // {
    // pathVertices.add(nextVertex);
    // }
    // }
    // }
    // currentPath.remove(vertex);
    // if (shortestPath < Integer.MAX_VALUE)
    // {
    // shortestPath++;
    // }
    // return shortestPath;
    // }

}
