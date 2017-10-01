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
package classycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import classycle.graph.AtomicVertex;
import classycle.graph.GraphProcessor;
import classycle.graph.Vertex;

/**
 * Processor which extracts the package dependency graph from the class dependency graph.
 * 
 * @author Franz-Josef Elmer
 */
public class PackageProcessor extends GraphProcessor {

    private static final class Arc {

        final AtomicVertex tail;
        final AtomicVertex head;
        final boolean internalHeadClass;

        Arc(AtomicVertex tail, AtomicVertex head, boolean internalHeadClass) {
            this.tail = tail;
            this.head = head;
            this.internalHeadClass = internalHeadClass;
        }

        void create() {
            if (internalHeadClass || head.isGraphVertex() == false) {
                tail.addOutgoingArcTo(head);
            }
        }
    }

    private final HashMap<String, PackageVertex> _packageVertices = new HashMap<>();
    private final List<Arc> _arcs = new ArrayList<>();
    private AtomicVertex[] _packageGraph;

    /**
     * Returns the package graph after processing.
     * 
     * @return can be <tt>null</tt> before processing.
     */
    public AtomicVertex[] getGraph() {
        return _packageGraph;
    }

    protected void initializeProcessing(Vertex[] graph) {
        _packageVertices.clear();
    }

    protected void processBefore(Vertex vertex) {
    }

    protected void processArc(Vertex tail, Vertex head) {
        PackageVertex tailPackage = getPackageVertex(tail);
        PackageVertex headPackage = getPackageVertex(head);
        boolean internalHeadClass = ((AtomicVertex) head).isGraphVertex();
        _arcs.add(new Arc(tailPackage, headPackage, internalHeadClass));
    }

    private PackageVertex getPackageVertex(Vertex vertex) {
        ClassAttributes classAttributes = (ClassAttributes) vertex.getAttributes();
        String className = (classAttributes).getName();
        int index = className.lastIndexOf('.');
        String packageName = index < 0 ? "(default package)" : className.substring(0, index);
        PackageVertex result = (PackageVertex) _packageVertices.get(packageName);
        if (result == null) {
            result = new PackageVertex(packageName);
            _packageVertices.put(packageName, result);
        }
        if (isVertexFromGraph(vertex)) {
            // not an external package
            result.reset();
        }
        result.addClass(classAttributes);
        return result;
    }

    private boolean isVertexFromGraph(Vertex vertex) {
        return vertex instanceof AtomicVertex && ((AtomicVertex) vertex).isGraphVertex();
    }

    protected void processAfter(Vertex vertex) {
    }

    protected void finishProcessing(Vertex[] graph) {
        for (int i = 0; i < _arcs.size(); i++) {
            ((Arc) _arcs.get(i)).create();
        }
        Iterator<PackageVertex> vertices = _packageVertices.values().iterator();
        List<AtomicVertex> list = new ArrayList<>();

        while (vertices.hasNext()) {
            AtomicVertex vertex =  vertices.next();
            if (vertex.isGraphVertex()) {
                list.add(vertex);
            }
        }
        _packageGraph = (AtomicVertex[]) list.toArray(new AtomicVertex[list.size()]);
    }

}
