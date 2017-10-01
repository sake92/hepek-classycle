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
/*
 * Copyright (c) 2003-2011, Franz-Josef Elmer, All rights reserved.
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
package classycle.dependency;

import java.util.ArrayList;
import java.util.List;
import classycle.graph.AtomicVertex;
import classycle.graph.NameAttributes;
import classycle.graph.StrongComponent;
import classycle.renderer.AbstractStrongComponentRenderer;

/**
 * Result of a cyclic check.
 * 
 * @author Franz-Josef Elmer
 */
public class CyclesResult implements Result {

    private final List<StrongComponent> _cycles = new ArrayList<StrongComponent>();
    private final String _statement;
    private final boolean _packageCycle;

    /**
     * Creates an instance for the specified statement.
     * 
     * @param packageCycle
     *            If <code>true/false</code> the check is on package/class cycles
     */
    public CyclesResult(String statement, boolean packageCycle) {
        _statement = statement;
        _packageCycle = packageCycle;
    }

    /**
     * Adds the specified cycle.
     */
    public void addCycle(StrongComponent cycle) {
        _cycles.add(cycle);
    }

    /**
     * Returns all added cycles.
     */
    public List<StrongComponent> getCycles() {
        return _cycles;
    }

    /**
     * Returns the statement.
     */
    public String getStatement() {
        return _statement;
    }

    /**
     * Returns <code>true/false</code> if this result is on package/class cycles.
     */
    public boolean isPackageCycle() {
        return _packageCycle;
    }

    /**
     * Returns <code>true</code> if no cycles have been added.
     */
    public boolean isOk() {
        return _cycles.size() == 0;
    }

    /**
     * Returns the result in a human-readable form which is used by the {@link DefaultResultRenderer}.
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(_statement);
        if (isOk()) {
            buffer.append("\tOK");
        } else {
            for (StrongComponent component : _cycles) {
                int numberOfVertices = component.getNumberOfVertices();
                buffer.append("\n  ");
                buffer.append(AbstractStrongComponentRenderer.createName(component));
                buffer.append(" contains ").append(numberOfVertices);
                buffer.append(' ').append(_packageCycle ? "packages" : "classes").append(':');
                for (int i = 0; i < numberOfVertices; i++) {
                    buffer.append("\n    ");
                    AtomicVertex vertex = component.getVertex(i);
                    buffer.append(((NameAttributes) vertex.getAttributes()).getName());
                }
            }
        }
        return new String(buffer.append('\n'));
    }

}
