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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * A strong component is a subgraph of a directed graph where every two vertices are mutually reachable.
 *
 * @author Franz-Josef Elmer
 */
public class StrongComponent extends Vertex {

    private static class GeometryAttributes implements GraphAttributes {

        private int girth;
        private int radius;
        private int diameter;
        private final List<Vertex> centerVertices = new ArrayList<>();
        private int[] eccentricities;
        private int[] maximumFragmentSizes;
        private int bestFragmentSize;
        private final List<Vertex> bestFragmenters = new ArrayList<>();

        public GeometryAttributes() {
        }

        void addFragmenter(Vertex vertex) {
            bestFragmenters.add(vertex);
        }

        void addVertex(Vertex vertex) {
            centerVertices.add(vertex);
        }

        @Override
        public Vertex[] getBestFragmenters() {
            return bestFragmenters.toArray(new Vertex[bestFragmenters.size()]);
        }

        @Override
        public int getBestFragmentSize() {
            return bestFragmentSize;
        }

        @Override
        public Vertex[] getCenterVertices() {
            return centerVertices.toArray(new Vertex[centerVertices.size()]);
        }

        @Override
        public int getDiameter() {
            return diameter;
        }

        @Override
        public int[] getEccentricities() {
            return eccentricities;
        }

        @Override
        public int getGirth() {
            return girth;
        }

        @Override
        public int[] getMaximumFragmentSizes() {
            return maximumFragmentSizes;
        }

        @Override
        public int getRadius() {
            return radius;
        }

        void setEccentricities(int[] eccentricities) {
            this.eccentricities = eccentricities;

            // Calculate radius and diameter
            radius = Integer.MAX_VALUE;
            diameter = 0;
            for (int i = 0; i < eccentricities.length; i++) {
                radius = Math.min(radius, eccentricities[i]);
                diameter = Math.max(diameter, eccentricities[i]);
            }
        }

        void setGirth(int girth) {
            this.girth = girth;
        }

        void setMaximumFragmentSizes(int[] maximumFragmentSizes) {
            this.maximumFragmentSizes = maximumFragmentSizes;

            bestFragmentSize = Integer.MAX_VALUE;
            for (int i = 0; i < maximumFragmentSizes.length; i++) {
                bestFragmentSize = Math.min(bestFragmentSize, maximumFragmentSizes[i]);
            }
        }

    }

    private final Vector<AtomicVertex> vertices = new Vector<>();
    private boolean active;
    private int longestWalk;

    /**
     * Default constructor. The {@link Attributes} of a strong component will a <tt>null</tt> pointer.
     */
    public StrongComponent() {
        super(new GeometryAttributes());
    }

    /**
     * Adds the specified vertex to this strong component. Note, that added vertices are inserted at index 0 of the list
     * of vertices.
     */
    public void addVertex(AtomicVertex vertex) {
        vertices.insertElementAt(vertex, 0);
    }

    /**
     * Calculates all graph properties of this component. These properties can be obtained from <tt>getAttributes</tt>
     * casted as {@link GraphAttributes}.
     */
    public void calculateAttributes() {
        final HashMap<AtomicVertex, Integer> indexMap = calculateIndexMap();
        final int[][] distances = calculateDistances(indexMap);

        // Calculate girth and eccentricity
        final GeometryAttributes attributes = (GeometryAttributes) getAttributes();
        int girth = Integer.MAX_VALUE;
        final int[] eccentricities = new int[distances.length];
        for (int i = 0; i < distances.length; i++) {
            girth = Math.min(girth, distances[i][i]);
            eccentricities[i] = 0;
            for (int j = 0; j < distances.length; j++) {
                if (i != j) {
                    eccentricities[i] = Math.max(eccentricities[i], distances[i][j]);
                }
            }
        }
        attributes.setEccentricities(eccentricities);
        attributes.setGirth(girth);
        attributes.setMaximumFragmentSizes(calculateMaximumFragmentSizes(indexMap));

        // Obtain center vertices and best fragmenters
        for (int i = 0, r = attributes.getRadius(), s = attributes.getBestFragmentSize(); i < distances.length; i++) {
            if (eccentricities[i] == r) {
                attributes.addVertex(getVertex(i));
            }
            if (attributes.getMaximumFragmentSizes()[i] == s) {
                attributes.addFragmenter(getVertex(i));
            }
        }

    }

    private int[][] calculateDistances(HashMap<AtomicVertex, Integer> indexMap) {
        // Calculate the adjacency matrix
        final int n = getNumberOfVertices();
        final int[][] distances = new int[n][n];
        for (int i = 0; i < n; i++) {
            final int[] row = distances[i];
            final AtomicVertex vertex = getVertex(i);
            for (int j = 0; j < n; j++) {
                row[j] = Integer.MAX_VALUE / 2;
            }
            for (int j = 0, m = vertex.getNumberOfOutgoingArcs(); j < m; j++) {
                final Integer index = indexMap.get(vertex.getHeadVertex(j));
                if (index != null) {
                    row[index.intValue()] = 1;
                }
            }
        }

        // Floyd-Warshall algorithm for the distances
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (distances[i][k] + distances[k][j] < distances[i][j]) {
                        distances[i][j] = distances[i][k] + distances[k][j];
                    }
                }
            }
        }

        return distances;
    }

    private HashMap<AtomicVertex, Integer> calculateIndexMap() {
        final HashMap<AtomicVertex, Integer> result = new HashMap<>();
        for (int i = 0, n = getNumberOfVertices(); i < n; i++) {
            result.put(getVertex(i), new Integer(i));
        }
        return result;
    }

    private int[] calculateMaximumFragmentSizes(HashMap<AtomicVertex, Integer> indexMap) {
        // clone graph defining this strong component
        final AtomicVertex[] graph = new AtomicVertex[getNumberOfVertices()];
        for (int i = 0; i < graph.length; i++) {
            graph[i] = new AtomicVertex(null);
        }
        for (int i = 0; i < graph.length; i++) {
            final AtomicVertex vertex = getVertex(i);
            for (int j = 0, n = vertex.getNumberOfOutgoingArcs(); j < n; j++) {
                final Integer index = indexMap.get(vertex.getHeadVertex(j));
                if (index != null) {
                    graph[i].addOutgoingArcTo(graph[index.intValue()]);
                }
            }
        }

        final StrongComponentProcessor processor = new StrongComponentProcessor(false);
        final int[] maximumFragmentSizes = new int[getNumberOfVertices()];
        for (int i = 0; i < maximumFragmentSizes.length; i++) {
            graph[i].setDefaultValueOfGraphVertexFlag(false);
            processor.deepSearchFirst(graph);
            final StrongComponent[] fragments = processor.getStrongComponents();
            maximumFragmentSizes[i] = 0;
            for (int j = 0; j < fragments.length; j++) {
                maximumFragmentSizes[i] = Math.max(maximumFragmentSizes[i], fragments[j].getNumberOfVertices());
            }
            graph[i].setDefaultValueOfGraphVertexFlag(true);
        }
        return maximumFragmentSizes;
    }

    public int getLongestWalk() {
        return longestWalk;
    }

    /** Returns the number of vertices building this strong component. */
    public int getNumberOfVertices() {
        return vertices.size();
    }

    /** Returns the vertex of the specified index. */
    public AtomicVertex getVertex(int index) {
        return vertices.elementAt(index);
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Reset this component. Calls reset of the superclass. Sets the activity flag to false and the longest walk to -1.
     */
    @Override
    public void reset() {
        super.reset();
        active = false;
        longestWalk = -1;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setLongestWalk(int longestWalk) {
        this.longestWalk = longestWalk;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder("Strong component with ");
        final int n = getNumberOfVertices();
        result.append(n).append(n > 1 ? " vertices." : " vertex.");
        result.append(" Longest walk: ").append(getLongestWalk());
        for (int i = 0; i < n; i++) {
            result.append("\n    ").append(getVertex(i));
        }
        return result.toString();
    }

    public static Comparator<Vertex> comparatorByLongestWalk() {
        return (o1, o2) -> {
            if (o1 instanceof StrongComponent && o2 instanceof StrongComponent) {
                final StrongComponent o1Strong = (StrongComponent) o1;
                final StrongComponent o2Strong = (StrongComponent) o2;
                return o1Strong.getLongestWalk() - o2Strong.getLongestWalk();
            }
            return 0;
        };
    }

}
