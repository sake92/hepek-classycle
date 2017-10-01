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

/**
 * XML renderer of an {@link AtomicVertex} with {@link ClassAttributes}.
 *
 * @author Franz-Josef Elmer
 */
public class XMLClassRenderer extends XMLAtomicVertexRenderer {

    @Override
    protected String getElement() {
        return "class";
    }

    @Override
    protected String getRefElement() {
        return "classRef";
    }

    @Override
    protected AtomicVertexRenderer getVertexRenderer() {
        return new TemplateBasedClassRenderer(
                "    <" + getElement() + " name=\"{0}\" sources=\"{9}\" type=\"{1}\" innerClass=\"{3}\""
                        + " size=\"{2}\" usedBy=\"{4}\" usesInternal=\"{5}\""
                        + " usesExternal=\"{6}\" layer=\"{7}\" cycle=\"{8}\">\n");
    }
}