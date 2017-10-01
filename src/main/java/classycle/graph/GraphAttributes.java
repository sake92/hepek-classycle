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

/**
 * Attributes of a graph. The following properties can be accessed with this interface:
 * <dl>
 * <dt>Girth:</dt>
 * <dd>The length of the shortest cycle.</dd>
 * <dt>Eccentricities:</dt>
 * <dd>The eccentricity for each vertex of the graph. The eccentricity of a vertex is the largest <em>distance</em> to
 * other vertices of the graph. The distance between vertex A and B is defined as the shortest path from A to B. The
 * distance is infinite if there is no path from A to B.
 * <dt>Diameter:</dt>
 * <dd>The largest eccentricity.</dd>
 * <dt>Radius:</dt>
 * <dd>The smallest eccentricity.</dd>
 * <dt>Center:</dt>
 * <dd>The set of vertices of the graph with the smallest eccentricities.</dd>
 * <dt>Maximum fragment sizes:</dt>
 * <dd>The maximum fragment sizes for each vertex of the graph. The maximum fragment size of a vertex is defined as the
 * size of the largest strong component of the graph after the vertex has been removed.</dd>
 * <dt>Best fragment size:</dt>
 * <dd>The smallest maximum fragment size.</dd>
 * <dt>Best fragmenters:</dt>
 * <dd>The set of vertices of the graph with smallest maximum fragment size.</dd>
 * </dl>
 *
 * @author Franz-Josef Elmer
 */
public interface GraphAttributes extends Attributes {

    /**
     * Returns those vertices of a {@link StrongComponent} where the maximum fragment size is equal to the best fragment
     * size.
     */
    Vertex[] getBestFragmenters();

    /** Returns the best fragment size. */
    int getBestFragmentSize();

    /** Returns the vertices of the center. */
    Vertex[] getCenterVertices();

    /** Returns the diameter. */
    int getDiameter();

    /**
     * Returns the eccentricies of all vertices of a {@link StrongComponent}.
     */
    int[] getEccentricities();

    /** Returns the girth. */
    int getGirth();

    /**
     * Returns the maximum fragment sizes of all vertices of a {@link StrongComponent}.
     */
    int[] getMaximumFragmentSizes();

    /** Returns the radius. */
    int getRadius();

}
