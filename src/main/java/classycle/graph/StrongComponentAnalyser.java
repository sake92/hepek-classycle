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

import java.util.HashMap;
import java.util.Map;

/**
 * Analyser of a directed graph for finding its strong components.
 * 
 * @author Franz-Josef Elmer
 */
public class StrongComponentAnalyser {

	private final AtomicVertex[] _graph;
	private StrongComponent[] _components;
	private HashMap<AtomicVertex, Integer> _layerMap;

	/** Creates an instance for the specified graph. */
	public StrongComponentAnalyser(AtomicVertex[] graph) {
		_graph = graph;
		// Arrays.sort(_graph, null);
	}

	/** Returns the original graph. That is, the argument of the constructor. */
	public AtomicVertex[] getGraph() {
		return _graph;
	}

	/** Returns the graph of strong components. */
	public StrongComponent[] getCondensedGraph() {
		if (_components == null) {
			StrongComponentProcessor processor = new StrongComponentProcessor(true);
			processor.deepSearchFirst(_graph);
			_components = processor.getStrongComponents();
		}
		return _components;
	}

	/**
	 * @return Mapping of nodes of the original graph onto a layer index (i.e.
	 *         length of the longest path of the condensed graph).
	 */
	public Map<AtomicVertex, Integer> getLayerMap() {
		if (_layerMap == null) {
			StrongComponent[] components = getCondensedGraph();
			new LongestWalkProcessor().deepSearchFirst(components);
			_layerMap = new HashMap<>();
			for (int i = 0; i < components.length; i++) {
				StrongComponent component = components[i];
				Integer layer = new Integer(component.getLongestWalk());
				for (int j = 0, n = component.getNumberOfVertices(); j < n; j++) {
					_layerMap.put(component.getVertex(j), layer);
				}
			}
		}
		return _layerMap;
	}

}
