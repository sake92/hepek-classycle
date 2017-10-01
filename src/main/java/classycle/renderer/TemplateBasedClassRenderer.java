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
import classycle.ClassAttributes;
import classycle.NameAndSourceAttributes;
import classycle.graph.AtomicVertex;
import classycle.graph.StrongComponent;

/**
 * Renderer of an {@link AtomicVertex} with {@link ClassAttributes}. The renderer is based on a
 * <tt>java.text.MessageFormat</tt> template. The variables in the template have the following meaning:
 * <table border=1 cellpadding=5>
 * <tr>
 * <th>Variable index</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>0</td>
 * <td>fully-qualified class name</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>class type</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>size of the class file in bytes</td>
 * </tr>
 * <tr>
 * <td>3</td>
 * <td><tt>true</tt> if inner class otherwise <tt>false</tt></td>
 * </tr>
 * <tr>
 * <td>4</td>
 * <td>Number of incoming arcs</td>
 * </tr>
 * <tr>
 * <td>5</td>
 * <td>Number of outgoing arcs to other vertices in the graph</td>
 * </tr>
 * <tr>
 * <td>6</td>
 * <td>Number of outgoing arcs to external vertices</td>
 * </tr>
 * <tr>
 * <td>7</td>
 * <td>Layer index</td>
 * </tr>
 * <tr>
 * <td>8</td>
 * <td>Name of the cycle or empty string</td>
 * </tr>
 * <tr>
 * <td>9</td>
 * <td>Source of class file if known</td>
 * </tr>
 * </table>
 *
 * @author Franz-Josef Elmer
 */
public class TemplateBasedClassRenderer implements AtomicVertexRenderer {

    private final MessageFormat format;

    /** Creates an instance for the specified template. */
    public TemplateBasedClassRenderer(String template) {
        format = new MessageFormat(template);
    }

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
        final String[] values = new String[10];
        final NameAndSourceAttributes attributes = (NameAndSourceAttributes) vertex.getAttributes();
        values[0] = attributes.getName();
        values[2] = Integer.toString(attributes.getSize());
        values[9] = attributes.getSources();
        if (attributes instanceof ClassAttributes) {
            final ClassAttributes ca = (ClassAttributes) attributes;
            values[1] = ca.getType();
            values[3] = ca.isInnerClass() ? "true" : "false";
        } else {
            values[1] = "";
            values[3] = "";
        }
        values[4] = Integer.toString(vertex.getNumberOfIncomingArcs());
        int usesInternal = 0;
        int usesExternal = 0;
        for (int i = 0, n = vertex.getNumberOfOutgoingArcs(); i < n; i++) {
            if (((AtomicVertex) vertex.getHeadVertex(i)).isGraphVertex()) {
                usesInternal++;
            } else {
                usesExternal++;
            }
        }
        values[5] = Integer.toString(usesInternal);
        values[6] = Integer.toString(usesExternal);
        values[7] = Integer.toString(layerIndex);
        values[8] = cycle == null ? "" : AbstractStrongComponentRenderer.createName(cycle);
        return format.format(values);
    }
}
