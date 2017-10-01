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
package classycle.renderer;

import classycle.ClassAttributes;
import classycle.graph.AtomicVertex;
import classycle.graph.StrongComponent;

/**
 * Plain text renderer of an {@link AtomicVertex} with {@link ClassAttributes}.
 *
 * @author Franz-Josef Elmer
 */
public final class PlainClassRenderer implements AtomicVertexRenderer {

    /**
     * Renderes the specified vertex. It is assumed that the vertex attributes are of the type
     * {@link classycle.ClassAttributes}.
     *
     * @param vertex
     *            Vertex to be rendered.
     * @return the rendered vertex.
     */
    @Override
    public String render(AtomicVertex vertex, StrongComponent cycle, int layerIndex) {
        if (vertex.getAttributes() instanceof ClassAttributes) {
            int usesInternal = 0;
            int usesExternal = 0;
            for (int i = 0, n = vertex.getNumberOfOutgoingArcs(); i < n; i++) {
                if (((AtomicVertex) vertex.getHeadVertex(i)).isGraphVertex()) {
                    usesInternal++;
                } else {
                    usesExternal++;
                }
            }
            final StringBuilder result = new StringBuilder(vertex.getAttributes().toString());
            result.append(": Used by ").append(vertex.getNumberOfIncomingArcs()).append(" classes. Uses ")
                    .append(usesInternal).append('/').append(usesExternal).append(" internal/external classes");
            return result.toString();
        }
        throw new IllegalArgumentException("Missing class attributes in vertex " + vertex);
    }

}
