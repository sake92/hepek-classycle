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

import java.util.Vector;

/**
 * The base class for any type of vertex in a directed graph.
 * <p>
 * A <tt>Vertex</tt> holds an {@link Attributes} object which encapsulates all properties of the vertex which are not
 * necessary to know for parsing a graph in a {@link GraphProcessor}. <br>
 * Only the {@code visited} flag will be manipulated during parsing.
 * <p>
 * A <tt>Vertex</tt> knows the head and tail vertices of all its outgoing and incoming arcs. When a head vertex is added
 * by the method {@link #addOutgoingArcTo} also the corresponding incoming arc is built in the head vertex. The same is
 * true the other way around. Note, that multi-arcs are not possible. That is, adding an already added head/tail vertex
 * again as a head/tail vertex will be ignored.
 *
 * @author Franz-Josef Elmer
 */
public class Vertex {

    private final Vector<Vertex> heads = new Vector<>();
    private final Vector<Vertex> tails = new Vector<>();
    private final Attributes attributes;
    private boolean visited;

    /** Create a new instance for the specified attributes. */
    public Vertex(Attributes attributes) {
        this.attributes = attributes;
    }

    /**
     * Adds an incoming arc to the specified vertex. Also calls {@link #addOutgoingArcTo} for <tt>tailVertex</tt> with
     * <tt>this</tt> as the argument. Does nothing if <tt>tailVertex</tt> is the tail vertex of an already existing
     * incoming arc.
     *
     * @param tailVertex
     *            Tail vertex to be added to establish a new incoming arc. <tt>Null</tt> is not allowed.
     */
    public void addIncomingArcTo(Vertex tailVertex) {
        if (!tails.contains(tailVertex)) {
            tails.addElement(tailVertex);
            tailVertex.addOutgoingArcTo(this);
        }
    }

    /**
     * Adds an outgoing arc to the specified vertex. Also calls {@link #addIncomingArcTo} for <tt>headVertex</tt> with
     * <tt>this</tt> as the argument. Does nothing if <tt>headVertex</tt> is the head vertex of an already existing
     * outgoing arc.
     *
     * @param headVertex
     *            Head vertex to be added to establish a new outgoing arc. <tt>Null</tt> is not allowed.
     */
    public void addOutgoingArcTo(Vertex headVertex) {
        if (!heads.contains(headVertex)) {
            heads.addElement(headVertex);
            headVertex.addIncomingArcTo(this);
        }
    }

    /** Returns the attributes. */
    public Attributes getAttributes() {
        return attributes;
    }

    /** Returns the head vertex of the specified outgoing arc. */
    public Vertex getHeadVertex(int index) {
        return heads.elementAt(index);
    }

    /**
     * Returns the number of incoming arcs. This is equivalent to the number of tail vertices.
     */
    public int getNumberOfIncomingArcs() {
        return tails.size();
    }

    /**
     * Returns the number of outgoing arcs. This is equivalent to the number of head vertices.
     */
    public int getNumberOfOutgoingArcs() {
        return heads.size();
    }

    /** Returns the tail vertex of the specified outgoing arc. */
    public Vertex getTailVertex(int index) {
        return tails.elementAt(index);
    }

    /** Returns the visited flag. */
    public boolean isVisited() {
        return visited;
    }

    /** Reset this vertex. That is, the visited flag is set to <tt>false</tt>. */
    public void reset() {
        visited = false;
    }

    /**
     * Returns <tt>toString()</tt> of the attributes and the number of incoming and outgoing arcs.
     */
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(getAttributes() == null ? super.toString() : getAttributes().toString()).append(": ")
                .append(getNumberOfIncomingArcs()).append(" incoming arc(s), ").append(getNumberOfOutgoingArcs())
                .append(" outgoing arc(s).");
        return result.toString();
    }

    /**
     * Marks this instance as visited. That is, the visited flag becomes <tt>true</tt>.
     */
    public void visit() {
        visited = true;
    }

}
