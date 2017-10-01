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
package classycle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import classycle.graph.AtomicVertex;

class GraphBuilder {

    /**
     * Creates a graph from the bunch of unresolved nodes.
     * 
     * @param unresolvedNodes
     *            All nodes with unresolved references.
     * @param mergeInnerClasses
     *            Merge inner class nodes with their outer class if <code>true</code>.
     * @return an array of length <tt>unresolvedNodes.size()</tt> with all unresolved nodes transformed into
     *         <tt>Node</tt> objects with appropriated links. External nodes are created and linked but not added to the
     *         result array.
     */
    static AtomicVertex[] createGraph(UnresolvedNode[] unresolvedNodes, boolean mergeInnerClasses) {
        Arrays.sort(unresolvedNodes);
        Map<String, AtomicVertex> vertices = createVertices(unresolvedNodes, mergeInnerClasses);
        AtomicVertex[] result = (AtomicVertex[]) vertices.values().toArray(new AtomicVertex[0]);

        // Add arces to vertices
        for (int i = 0; i < unresolvedNodes.length; i++) {
            UnresolvedNode node = unresolvedNodes[i];
            String name = normalize(node.getAttributes().getName(), mergeInnerClasses);
            AtomicVertex vertex = (AtomicVertex) vertices.get(name);
            for (Iterator<String> iterator = node.linkIterator(); iterator.hasNext();) {
                name = normalize(iterator.next(), mergeInnerClasses);
                AtomicVertex head = vertices.get(name);
                if (head == null) {
                    head = new AtomicVertex(ClassAttributes.createUnknownClass(name, 0));
                    vertices.put(name, head);
                }
                if (vertex != head) {
                    vertex.addOutgoingArcTo(head);
                }
            }
        }

        return result;
    }

    private static Map<String, AtomicVertex> createVertices(UnresolvedNode[] unresolvedNodes,
            boolean mergeInnerClasses) {
        Map<String, AtomicVertex> vertices = new HashMap<>();
        for (int i = 0; i < unresolvedNodes.length; i++) {
            ClassAttributes attributes = unresolvedNodes[i].getAttributes();
            String type = attributes.getType();
            String originalName = attributes.getName();
            int size = attributes.getSize();
            String name = normalize(originalName, mergeInnerClasses);
            AtomicVertex vertex = (AtomicVertex) vertices.get(name);
            if (vertex != null) {
                ClassAttributes vertexAttributes = (ClassAttributes) vertex.getAttributes();
                size += vertexAttributes.getSize();
                if (name.equals(originalName) == false) {
                    type = vertexAttributes.getType();
                }
                attributes.addSourcesOf(vertexAttributes);
            }
            ClassAttributes newAttributes = new ClassAttributes(name, null, type, size);
            newAttributes.addSourcesOf(attributes);
            vertex = new AtomicVertex(newAttributes);
            vertices.put(name, vertex);
        }
        return vertices;
    }

    private static String normalize(String name, boolean mergeInnerClasses) {
        if (mergeInnerClasses) {
            int index = name.indexOf('$');
            if (index >= 0) {
                name = name.substring(0, index);
            }
        }
        return name;
    }

}
