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

        private final VertexCondition[] conditions;

        VertexUnionCondition(VertexCondition[] conditions) {
            this.conditions = conditions;
        }

        @Override
        public boolean isFulfilled(Vertex vertex) {
            for (final VertexCondition condition : conditions) {
                if (condition.isFulfilled(vertex)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static final String CHECK = DependencyDefinitionParser.CHECK_KEY_WORD + ' ';
    private final StringPattern[] startSets;
    private final StringPattern[] finalSets;
    private final StringPattern finalSet;
    private final String dependencyType;
    private final VertexCondition[] startConditions;
    private final VertexCondition[] finalConditions;
    private final VertexCondition finalCondition;
    private final SetDefinitionRepository repository;
    private final ResultRenderer renderer;

    public DependencyStatement(StringPattern[] startSets, StringPattern[] finalSets, String dependencyType,
            SetDefinitionRepository repository, ResultRenderer renderer) {
        this.startSets = startSets;
        this.finalSets = finalSets;
        this.dependencyType = dependencyType;
        this.repository = repository;
        this.renderer = renderer;
        startConditions = createVertexConditions(startSets);
        finalConditions = createVertexConditions(finalSets);
        finalSet = new OrStringPattern(finalSets);
        finalCondition = new VertexUnionCondition(finalConditions);
    }

    private VertexCondition[] createVertexConditions(StringPattern[] patterns) {
        final VertexCondition[] fromSets = new VertexCondition[patterns.length];
        for (int i = 0; i < fromSets.length; i++) {
            fromSets[i] = new PatternVertexCondition(patterns[i]);
        }
        return fromSets;
    }

    @Override
    public Result execute(AtomicVertex[] graph) {
        final ResultContainer result = new ResultContainer();
        final boolean directPathsOnly = DIRECTLY_INDEPENDENT_OF_KEY_WORD.equals(dependencyType);
        final boolean dependsOnly = DependencyDefinitionParser.DEPENDENT_ONLY_ON_KEY_WORD.equals(dependencyType);
        for (int i = 0; i < startConditions.length; i++) {
            final VertexCondition startCondition = startConditions[i];
            final StringPattern startSet = startSets[i];
            if (dependsOnly) {
                final Set<AtomicVertex> invalids = new HashSet<>();
                for (final AtomicVertex vertex : graph) {
                    if (startCondition.isFulfilled(vertex)) {
                        for (int j = 0, n = vertex.getNumberOfOutgoingArcs(); j < n; j++) {
                            final Vertex headVertex = vertex.getHeadVertex(j);
                            if (finalCondition.isFulfilled(headVertex) == false
                                    && startCondition.isFulfilled(headVertex) == false) {
                                invalids.add(vertex);
                                invalids.add((AtomicVertex) headVertex);
                            }
                        }
                    }
                }
                result.add(new DependencyResult(startSet, finalSet, toString(startSet, finalSet),
                        invalids.toArray(new AtomicVertex[0])));
            } else {
                for (int j = 0; j < finalConditions.length; j++) {
                    final PathsFinder finder = new PathsFinder(startCondition, finalConditions[j],
                            renderer.onlyShortestPaths(), directPathsOnly);
                    result.add(new DependencyResult(startSet, finalSets[j], toString(i, j), finder.findPaths(graph)));
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(CHECK);
        for (int i = 0; i < startSets.length; i++) {
            builder.append(repository.toString(startSets[i])).append(' ');
        }
        builder.append(dependencyType).append(' ');
        for (int i = 0; i < finalSets.length; i++) {
            builder.append(repository.toString(finalSets[i])).append(' ');
        }

        return builder.substring(0, builder.length() - 1).toString();
    }

    private String toString(int i, int j) {
        return toString(startSets[i], finalSets[j]);
    }

    private String toString(StringPattern startSet, StringPattern finalSet) {
        final StringBuilder builder = new StringBuilder(CHECK);
        builder.append(repository.toString(startSet)).append(' ').append(dependencyType).append(' ')
                .append(repository.toString(finalSet));
        return builder.toString();
    }
}
