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
 * Copyright (c) 2003-2008, Franz-Josef Elmer, All rights reserved.
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
import classycle.PackageProcessor;
import classycle.graph.AtomicVertex;
import classycle.graph.NameAttributes;
import classycle.graph.StrongComponent;
import classycle.graph.StrongComponentAnalyser;
import classycle.graph.Vertex;
import classycle.util.StringPattern;

public class CheckCyclesStatement implements Statement {

    private final StringPattern _set;
    private final int _maximumSize;
    private final boolean _packageCycles;
    private final SetDefinitionRepository _repository;

    public CheckCyclesStatement(StringPattern set, int size, boolean cycles, SetDefinitionRepository repository) {
        _set = set;
        _maximumSize = size;
        _packageCycles = cycles;
        _repository = repository;
    }

    public Result execute(AtomicVertex[] graph) {
        List<AtomicVertex> filteredGraph = new ArrayList<>();
        for (int i = 0; i < graph.length; i++) {
            if (_set.matches(((NameAttributes) graph[i].getAttributes()).getName())) {
                filteredGraph.add(graph[i]);
            }
        }
        graph = (AtomicVertex[]) filteredGraph.toArray(new AtomicVertex[0]);
        if (_packageCycles) {
            PackageProcessor processor = new PackageProcessor();
            processor.deepSearchFirst(graph);
            graph = processor.getGraph();
        }
        StrongComponentAnalyser analyser = new StrongComponentAnalyser(graph);
        Vertex[] condensedGraph = analyser.getCondensedGraph();
        CyclesResult result = new CyclesResult(createStatement(), _packageCycles);
        for (int i = 0; i < condensedGraph.length; i++) {
            StrongComponent strongComponent = (StrongComponent) condensedGraph[i];
            if (strongComponent.getNumberOfVertices() > _maximumSize) {
                result.addCycle(strongComponent);
            }
        }
        return result;
    }

    public String toString() {
        return createStatement();
    }

    private String createStatement() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(DependencyDefinitionParser.CHECK_KEY_WORD).append(' ');
        if (_packageCycles) {
            buffer.append(DependencyDefinitionParser.PACKAGE_CYCLES_KEY_WORD);
        } else {
            buffer.append(DependencyDefinitionParser.CLASS_CYCLES_KEY_WORD);
        }
        buffer.append(" > ").append(_maximumSize).append(' ');
        buffer.append(DependencyDefinitionParser.IN_KEY_WORD).append(' ');
        buffer.append(_repository.toString(_set));
        return new String(buffer);
    }

}
