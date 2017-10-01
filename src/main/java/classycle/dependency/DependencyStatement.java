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

import static classycle.dependency.DependencyDefinitionParser.DIRECTLY_INDEPENDENT_OF_KEY_WORD;
import java.util.HashSet;
import java.util.Set;
import classycle.graph.AtomicVertex;
import classycle.graph.PathsFinder;
import classycle.graph.Vertex;
import classycle.graph.VertexCondition;
import classycle.util.OrStringPattern;
import classycle.util.StringPattern;

/**
 * @author Franz-Josef Elmer
 */
public class DependencyStatement implements Statement {

    private static final class VertexUnionCondition implements VertexCondition {

        private final VertexCondition[] _conditions;

        VertexUnionCondition(VertexCondition[] conditions) {
            _conditions = conditions;
        }

        public boolean isFulfilled(Vertex vertex) {
            for (VertexCondition condition : _conditions) {
                if (condition.isFulfilled(vertex)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static final String CHECK = DependencyDefinitionParser.CHECK_KEY_WORD + ' ';
    private final StringPattern[] _startSets;
    private final StringPattern[] _finalSets;
    private final StringPattern _finalSet;
    private final String _dependencyType;
    private final VertexCondition[] _startConditions;
    private final VertexCondition[] _finalConditions;
    private final VertexCondition _finalCondition;
    private final SetDefinitionRepository _repository;
    private final ResultRenderer _renderer;

    public DependencyStatement(StringPattern[] startSets, StringPattern[] finalSets, String dependencyType,
            SetDefinitionRepository repository, ResultRenderer renderer) {
        _startSets = startSets;
        _finalSets = finalSets;
        _dependencyType = dependencyType;
        _repository = repository;
        _renderer = renderer;
        _startConditions = createVertexConditions(startSets);
        _finalConditions = createVertexConditions(finalSets);
        _finalSet = new OrStringPattern(_finalSets);
        _finalCondition = new VertexUnionCondition(_finalConditions);
    }

    private VertexCondition[] createVertexConditions(StringPattern[] patterns) {
        VertexCondition[] fromSets = new VertexCondition[patterns.length];
        for (int i = 0; i < fromSets.length; i++) {
            fromSets[i] = new PatternVertexCondition(patterns[i]);
        }
        return fromSets;
    }

    public Result execute(AtomicVertex[] graph) {
        ResultContainer result = new ResultContainer();
        boolean directPathsOnly = DIRECTLY_INDEPENDENT_OF_KEY_WORD.equals(_dependencyType);
        boolean dependsOnly = DependencyDefinitionParser.DEPENDENT_ONLY_ON_KEY_WORD.equals(_dependencyType);
        for (int i = 0; i < _startConditions.length; i++) {
            VertexCondition startCondition = _startConditions[i];
            StringPattern startSet = _startSets[i];
            if (dependsOnly) {
                Set<AtomicVertex> invalids = new HashSet<AtomicVertex>();
                for (AtomicVertex vertex : graph) {
                    if (startCondition.isFulfilled(vertex)) {
                        for (int j = 0, n = vertex.getNumberOfOutgoingArcs(); j < n; j++) {
                            Vertex headVertex = vertex.getHeadVertex(j);
                            if (_finalCondition.isFulfilled(headVertex) == false
                                    && startCondition.isFulfilled(headVertex) == false) {
                                invalids.add(vertex);
                                invalids.add((AtomicVertex) headVertex);
                            }
                        }
                    }
                }
                result.add(new DependencyResult(startSet, _finalSet, toString(startSet, _finalSet),
                        invalids.toArray(new AtomicVertex[0])));
            } else {
                for (int j = 0; j < _finalConditions.length; j++) {
                    PathsFinder finder = new PathsFinder(startCondition, _finalConditions[j],
                            _renderer.onlyShortestPaths(), directPathsOnly);
                    result.add(new DependencyResult(startSet, _finalSets[j], toString(i, j), finder.findPaths(graph)));
                }
            }
        }
        return result;
    }

    private String toString(int i, int j) {
        return toString(_startSets[i], _finalSets[j]);
    }

    private String toString(StringPattern startSet, StringPattern finalSet) {
        StringBuffer buffer = new StringBuffer(CHECK);
        buffer.append(_repository.toString(startSet)).append(' ').append(_dependencyType).append(' ')
                .append(_repository.toString(finalSet));
        return new String(buffer);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(CHECK);
        for (int i = 0; i < _startSets.length; i++) {
            buffer.append(_repository.toString(_startSets[i])).append(' ');
        }
        buffer.append(_dependencyType).append(' ');
        for (int i = 0; i < _finalSets.length; i++) {
            buffer.append(_repository.toString(_finalSets[i])).append(' ');
        }

        return new String(buffer.substring(0, buffer.length() - 1));
    }
}
