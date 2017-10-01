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

import java.util.List;
import java.util.Stack;
import classycle.graph.AtomicVertex;
import classycle.graph.Attributes;
import classycle.graph.NameAttributes;
import classycle.graph.StrongComponent;
import classycle.renderer.AbstractStrongComponentRenderer;
import classycle.util.StringPattern;
import classycle.util.Text;

/**
 * Renderer which renders dependency checking results as XML. It ignores preferences.
 *
 * @author Franz-Josef Elmer
 */
public class XMLResultRenderer extends ResultRenderer {

    private static final class XMLBuilder {

        private static final int INDENTATION_INCREMENT = 2;
        private final StringBuilder builder = new StringBuilder("<?xml version='1.0' encoding='UTF-8'?>\n");

        private final Stack<String> stack = new Stack<>();
        private boolean unfinishedStartTag;
        private boolean textAdded;

        void attribute(String name, String value) {
            builder.append(' ').append(name).append("=\'").append(Text.excapeForXML(value)).append("\'");
        }

        void begin(String element) {
            if (unfinishedStartTag) {
                builder.append(">\n");
            }
            indent();
            builder.append("<").append(element);
            stack.push(element);
            unfinishedStartTag = true;
        }

        void end() {
            final String element = stack.pop();
            if (unfinishedStartTag) {
                builder.append("/>\n");
                unfinishedStartTag = false;
            } else {
                if (textAdded == false) {
                    indent();
                }
                textAdded = false;
                builder.append("</").append(element).append(">\n");
            }
        }

        private void indent() {
            for (int i = 0; i < stack.size() * INDENTATION_INCREMENT; i++) {
                builder.append(' ');
            }
        }

        void text(String text) {
            builder.append(">").append(Text.excapeForXML(text));
            unfinishedStartTag = false;
            textAdded = true;
        }

        @Override
        public String toString() {
            return builder.toString();
        }
    }

    private static final String ELEMENT_DEPENDENCY_RESULT = "dependency-checking-results";

    private static final String ATTRIBUTE_STATEMENT = "statement";

    private void addTo(XMLBuilder builder, CyclesResult result) {
        builder.begin("cycles");
        builder.attribute(ATTRIBUTE_STATEMENT, result.getStatement());
        builder.attribute("vertex-type", result.isPackageCycle() ? "package" : "class");
        final List<StrongComponent> cycles = result.getCycles();
        for (final StrongComponent component : cycles) {
            builder.begin("cycle");
            builder.attribute("name", AbstractStrongComponentRenderer.createName(component));
            for (int i = 0, n = component.getNumberOfVertices(); i < n; i++) {
                builder.begin("class");
                final Attributes attributes = component.getVertex(i).getAttributes();
                if (attributes instanceof NameAttributes) {
                    builder.text(((NameAttributes) attributes).getName());
                }
                builder.end();
            }
            builder.end();
        }
        builder.end();
    }

    private void addTo(XMLBuilder builder, DependencyResult result) {
        builder.begin("unexpected-dependencies");
        builder.attribute(ATTRIBUTE_STATEMENT, result.getStatement());
        final AtomicVertex[] paths = result.getPaths();
        final StringPattern startSet = result.getStartSet();
        final StringPattern finalSet = result.getFinalSet();
        final DependencyPathsRenderer renderer = new DependencyPathsRenderer(paths, startSet, finalSet);
        renderer.renderGraph(createPathRenderer(builder));
        builder.end();
    }

    private void addTo(XMLBuilder builder, Result result) {
        if (result instanceof CyclesResult) {
            addTo(builder, (CyclesResult) result);
        } else if (result instanceof DependencyResult) {
            addTo(builder, (DependencyResult) result);
        } else if (result instanceof ResultContainer) {
            addTo(builder, (ResultContainer) result);
        } else if (result instanceof TextResult) {
            addTo(builder, (TextResult) result);
        }
    }

    private void addTo(XMLBuilder builder, ResultContainer result) {
        final int numberOfResults = result.getNumberOfResults();
        for (int i = 0; i < numberOfResults; i++) {
            addTo(builder, result.getResult(i));
        }
    }

    private void addTo(XMLBuilder builder, TextResult result) {
        if (result.isOk() == false || result.toString().trim().length() > 0) {
            builder.begin(result.isOk() ? "info" : "checking-error");
            builder.text(result.toString());
            builder.end();
        }
    }

    @Override
    public void considerPreference(Preference preference) {
    }

    private DependencyPathRenderer createPathRenderer(final XMLBuilder builder) {
        return new DependencyPathRenderer() {

            private boolean openTag;

            @Override
            public void add(String nodeName) {
                if (openTag) {
                    builder.end();
                }
                builder.begin("node");
                builder.attribute("name", nodeName);
                openTag = true;
            }

            @Override
            public void decreaseIndentation() {
                if (openTag) {
                    builder.end();
                }
                openTag = false;
                builder.end();
            }

            @Override
            public void increaseIndentation() {
                openTag = false;
            }

        };
    }

    @Override
    public Result getDescriptionOfCurrentPreferences() {
        return new TextResult("");
    }

    @Override
    public PreferenceFactory getPreferenceFactory() {
        return key -> () -> key;
    }

    @Override
    public String render(Result result) {
        final XMLBuilder builder = new XMLBuilder();
        builder.begin(ELEMENT_DEPENDENCY_RESULT);
        addTo(builder, result);
        builder.end();
        return builder.toString();
    }

}
