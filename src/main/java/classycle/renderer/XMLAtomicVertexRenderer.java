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

import java.text.MessageFormat;
import classycle.graph.AtomicVertex;
import classycle.graph.NameAttributes;
import classycle.graph.StrongComponent;

/**
 * XML renderer of an {@link AtomicVertex} with {@link NameAttributes}.
 *
 * @author Franz-Josef Elmer
 */
public abstract class XMLAtomicVertexRenderer implements AtomicVertexRenderer {

    protected abstract String getElement();

    protected abstract String getRefElement();

    protected abstract AtomicVertexRenderer getVertexRenderer();

    /**
     * Renderes the specified vertex. It is assumed that the vertex attributes are of the type
     * {@link classycle.ClassAttributes}.
     *
     * @return the rendered vertex.
     */
    @Override
    public String render(AtomicVertex vertex, StrongComponent cycle, int layerIndex) {
        final StringBuilder result = new StringBuilder();
        result.append(getVertexRenderer().render(vertex, cycle, layerIndex));
        final MessageFormat format = new MessageFormat(
                "      <" + getRefElement() + " name=\"{0}\"" + " type=\"{1}\"/>\n");
        final String[] values = new String[2];
        for (int i = 0, n = vertex.getNumberOfIncomingArcs(); i < n; i++) {
            values[0] = ((NameAttributes) vertex.getTailVertex(i).getAttributes()).getName();
            values[1] = "usedBy";
            result.append(format.format(values));
        }
        for (int i = 0, n = vertex.getNumberOfOutgoingArcs(); i < n; i++) {
            values[0] = ((NameAttributes) vertex.getHeadVertex(i).getAttributes()).getName();
            values[1] = ((AtomicVertex) vertex.getHeadVertex(i)).isGraphVertex() ? "usesInternal" : "usesExternal";
            result.append(format.format(values));
        }
        result.append("    </").append(getElement()).append(">\n");
        return result.toString();
    }

}
