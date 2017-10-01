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

import classycle.graph.AtomicVertex;
import classycle.graph.GraphAttributes;
import classycle.graph.NameAttributes;
import classycle.graph.StrongComponent;
import classycle.graph.Vertex;

/**
 * Abstract superclass of all {@link StrongComponentRenderer}.
 *
 * @author Franz-Josef Elmer
 */
public abstract class AbstractStrongComponentRenderer implements StrongComponentRenderer {

    /**
     * Creates an appropriated name for the specified {@link StrongComponent}. Usually, the name is the fully-qualified
     * class name of the first vertex in <tt>component</tt> extended by "et al." if <tt>component</tt> contains more
     * than one vertex. If <tt>component</tt> contains only a class and its inner classes the name is the
     * fully-qualified class name of the outer class extended by "and inner classes".
     */
    public static String createName(StrongComponent component) {
        final GraphAttributes ga = (GraphAttributes) component.getAttributes();
        final Vertex fragmenter = ga.getBestFragmenters()[0];
        String result = ((NameAttributes) fragmenter.getAttributes()).getName();
        // String result = component.getVertex(0).getAttributes().toString();
        if (component.getNumberOfVertices() > 1) {
            AtomicVertex vertex = component.getVertex(0);
            NameAttributes attributes = (NameAttributes) vertex.getAttributes();
            String outerClass = attributes.getName();
            final int index = outerClass.indexOf('$');
            if (index > 0) {
                outerClass = outerClass.substring(0, index);
            }
            boolean isInnerClass = true;
            for (int i = 0, n = component.getNumberOfVertices(); i < n; i++) {
                attributes = (NameAttributes) component.getVertex(i).getAttributes();
                if (attributes.getName().equals(outerClass)) {
                    vertex = component.getVertex(i);
                } else if (!attributes.getName().startsWith(outerClass)
                        || attributes.getName().charAt(outerClass.length()) != '$') {
                    isInnerClass = false;
                    break;
                }
            }
            attributes = (NameAttributes) vertex.getAttributes();
            if (isInnerClass) {
                result = attributes.getName() + " and inner classes";
            } else {
                result += " et al.";
            }
        }
        return result;
    }

}
