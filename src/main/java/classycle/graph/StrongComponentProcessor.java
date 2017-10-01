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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

/**
 * A processor which extracts the strong components of a directed graph. A strong component is a maximal strongly
 * connected subgraph of a directed graph. The implementation is based on Tarjan's algorithm.
 *
 * @author Franz-Josef Elmer
 */
public class StrongComponentProcessor extends GraphProcessor {

    private final boolean calculateAttributes;
    private int counter;
    private final Stack<AtomicVertex> vertexStack = new Stack<>();
    private final Vector<StrongComponent> strongComponents = new Vector<>();
    private final Hashtable<AtomicVertex, StrongComponent> vertexToComponents = new Hashtable<>();
    private StrongComponent[] graph;

    /**
     * Creates an instance.
     *
     * @param calculateAttributes
     *            If <tt>true</tt> the attributes of the strong components will be calculated. Otherwise not.
     */
    public StrongComponentProcessor(boolean calculateAttributes) {
        this.calculateAttributes = calculateAttributes;
    }

    /**
     * Casts the specified vertex as an {@link AtomicVertex}.
     *
     * @throws IllegalArgumentException
     *             if <tt>vertex</tt> is not an instance of {@link AtomicVertex}.
     */
    private AtomicVertex castAsAtomicVertex(Vertex vertex) {
        if (vertex instanceof AtomicVertex) {
            return (AtomicVertex) vertex;
        } else {
            throw new IllegalArgumentException(vertex + " is not an instance of AtomicVertex");
        }
    }

    /**
     * Adds all arcs to the strong components. There is an arc from a strong component to another one if there is at
     * least one arc from a vertex of one component to a vertex the other one.
     */
    @Override
    protected void finishProcessing(Vertex[] graph) {
        this.graph = new StrongComponent[strongComponents.size()];
        for (int i = 0; i < this.graph.length; i++) {
            this.graph[i] = strongComponents.elementAt(i);
            if (calculateAttributes) {
                this.graph[i].calculateAttributes();
            }
        }

        final Enumeration<AtomicVertex> keys = vertexToComponents.keys();
        while (keys.hasMoreElements()) {
            final AtomicVertex vertex = keys.nextElement();
            final StrongComponent tail = vertexToComponents.get(vertex);
            for (int i = 0, n = vertex.getNumberOfOutgoingArcs(); i < n; i++) {
                final AtomicVertex h = (AtomicVertex) vertex.getHeadVertex(i);
                if (h.isGraphVertex()) {
                    final StrongComponent head = vertexToComponents.get(h);
                    if (head != null && head != tail) {
                        tail.addOutgoingArcTo(head);
                    }
                }
            }
        }
    }

    /**
     * Returns the result of {@link #deepSearchFirst}.
     */
    public StrongComponent[] getStrongComponents() {
        return graph;
    }

    @Override
    protected void initializeProcessing(Vertex[] graph) {
        counter = 0;
        vertexStack.setSize(0);
        strongComponents.setSize(0);
        vertexToComponents.clear();
    }

    /**
     * Processes the specified vertex after all its outgoing arcs are processed.
     *
     * @throws IllegalArgumentException
     *             if <tt>vertex</tt> is not an instance of {@link AtomicVertex}.
     */
    @Override
    protected void processAfter(Vertex vertex) {
        final AtomicVertex atomicVertex = castAsAtomicVertex(vertex);
        if (atomicVertex.getLow() == atomicVertex.getOrder()) {
            final StrongComponent component = new StrongComponent();
            while (!vertexStack.isEmpty() && vertexStack.peek().getOrder() >= atomicVertex.getOrder()) {
                final AtomicVertex vertexOfComponent = vertexStack.pop();
                component.addVertex(vertexOfComponent);
                vertexToComponents.put(vertexOfComponent, component);
            }
            strongComponents.addElement(component);
        }
    }

    /**
     * @throws IllegalArgumentException
     *             if <tt>tail</tt> and <tt>head</tt> are not an instances of {@link AtomicVertex}.
     */
    @Override
    protected void processArc(Vertex tail, Vertex head) {
        final AtomicVertex t = castAsAtomicVertex(tail);
        final AtomicVertex h = castAsAtomicVertex(head);
        if (h.isGraphVertex()) {
            if (!h.isVisited()) {
                process(h);
                t.setLow(Math.min(t.getLow(), h.getLow()));
            } else if (h.getOrder() < t.getOrder() && vertexStack.contains(h)) {
                t.setLow(Math.min(t.getLow(), h.getOrder()));
            }
        }
    }

    /**
     * @throws IllegalArgumentException
     *             if <tt>vertex</tt> is not an instance of {@link AtomicVertex}.
     */
    @Override
    protected void processBefore(Vertex vertex) {
        final AtomicVertex atomicVertex = castAsAtomicVertex(vertex);
        atomicVertex.setOrder(counter);
        atomicVertex.setLow(counter++);
        vertexStack.push(atomicVertex);
    }

}
