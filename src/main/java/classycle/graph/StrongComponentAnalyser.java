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

import java.util.HashMap;
import java.util.Map;

/**
 * Analyser of a directed graph for finding its strong components.
 *
 * @author Franz-Josef Elmer
 */
public class StrongComponentAnalyser {

    private final AtomicVertex[] graph;
    private StrongComponent[] components;
    private HashMap<AtomicVertex, Integer> layerMap;

    /** Creates an instance for the specified graph. */
    public StrongComponentAnalyser(AtomicVertex[] graph) {
        this.graph = graph;
    }

    /** Returns the graph of strong components. */
    public StrongComponent[] getCondensedGraph() {
        if (components == null) {
            final StrongComponentProcessor processor = new StrongComponentProcessor(true);
            processor.deepSearchFirst(graph);
            components = processor.getStrongComponents();
        }
        return components;
    }

    /** Returns the original graph. That is, the argument of the constructor. */
    public AtomicVertex[] getGraph() {
        return graph;
    }

    /**
     * @return Mapping of nodes of the original graph onto a layer index (i.e. length of the longest path of the
     *         condensed graph).
     */
    public Map<AtomicVertex, Integer> getLayerMap() {
        if (layerMap == null) {
            final StrongComponent[] components = getCondensedGraph();
            new LongestWalkProcessor().deepSearchFirst(components);
            layerMap = new HashMap<>();
            for (int i = 0; i < components.length; i++) {
                final StrongComponent component = components[i];
                final Integer layer = new Integer(component.getLongestWalk());
                for (int j = 0, n = component.getNumberOfVertices(); j < n; j++) {
                    layerMap.put(component.getVertex(j), layer);
                }
            }
        }
        return layerMap;
    }

}
