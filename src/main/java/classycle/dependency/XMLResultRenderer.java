/*
 * Copyright (c) 2011, Franz-Josef Elmer, All rights reserved.
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

    private static final String ELEMENT_DEPENDENCY_RESULT = "dependency-checking-results";
    private static final String ATTRIBUTE_STATEMENT = "statement";

    private static final class XMLBuilder {

        private static final int INDENTATION_INCREMENT = 2;
        private final StringBuilder _builder = new StringBuilder("<?xml version='1.0' encoding='UTF-8'?>\n");

        private Stack<String> _stack = new Stack<String>();
        private boolean _unfinishedStartTag;
        private boolean _textAdded;

        void begin(String element) {
            if (_unfinishedStartTag) {
                _builder.append(">\n");
            }
            indent();
            _builder.append("<").append(element);
            _stack.push(element);
            _unfinishedStartTag = true;
        }

        void attribute(String name, String value) {
            _builder.append(' ').append(name).append("=\'").append(Text.excapeForXML(value)).append("\'");
        }

        void text(String text) {
            _builder.append(">").append(Text.excapeForXML(text));
            _unfinishedStartTag = false;
            _textAdded = true;
        }

        void end() {
            String element = _stack.pop();
            if (_unfinishedStartTag) {
                _builder.append("/>\n");
                _unfinishedStartTag = false;
            } else {
                if (_textAdded == false) {
                    indent();
                }
                _textAdded = false;
                _builder.append("</").append(element).append(">\n");
            }
        }

        private void indent() {
            for (int i = 0; i < _stack.size() * INDENTATION_INCREMENT; i++) {
                _builder.append(' ');
            }
        }

        @Override
        public String toString() {
            return _builder.toString();
        }
    }

    @Override
    public PreferenceFactory getPreferenceFactory() {
        return new PreferenceFactory() {

            public Preference get(final String key) {
                return new Preference() {

                    public String getKey() {
                        return key;
                    }
                };
            }
        };
    }

    @Override
    public void considerPreference(Preference preference) {
    }

    @Override
    public Result getDescriptionOfCurrentPreferences() {
        return new TextResult("");
    }

    @Override
    public String render(Result result) {
        XMLBuilder builder = new XMLBuilder();
        builder.begin(ELEMENT_DEPENDENCY_RESULT);
        addTo(builder, result);
        builder.end();
        return builder.toString();
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

    private void addTo(XMLBuilder builder, CyclesResult result) {
        builder.begin("cycles");
        builder.attribute(ATTRIBUTE_STATEMENT, result.getStatement());
        builder.attribute("vertex-type", result.isPackageCycle() ? "package" : "class");
        List<StrongComponent> cycles = result.getCycles();
        for (StrongComponent component : cycles) {
            builder.begin("cycle");
            builder.attribute("name", AbstractStrongComponentRenderer.createName(component));
            for (int i = 0, n = component.getNumberOfVertices(); i < n; i++) {
                builder.begin("class");
                Attributes attributes = component.getVertex(i).getAttributes();
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
        AtomicVertex[] paths = result.getPaths();
        StringPattern startSet = result.getStartSet();
        StringPattern finalSet = result.getFinalSet();
        DependencyPathsRenderer renderer = new DependencyPathsRenderer(paths, startSet, finalSet);
        renderer.renderGraph(createPathRenderer(builder));
        builder.end();
    }

    private DependencyPathRenderer createPathRenderer(final XMLBuilder builder) {
        return new DependencyPathRenderer() {

            private boolean _openTag;

            public void increaseIndentation() {
                _openTag = false;
            }

            public void decreaseIndentation() {
                if (_openTag) {
                    builder.end();
                }
                _openTag = false;
                builder.end();
            }

            public void add(String nodeName) {
                if (_openTag) {
                    builder.end();
                }
                builder.begin("node");
                builder.attribute("name", nodeName);
                _openTag = true;
            }

        };
    }

    private void addTo(XMLBuilder builder, ResultContainer result) {
        int numberOfResults = result.getNumberOfResults();
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

}
