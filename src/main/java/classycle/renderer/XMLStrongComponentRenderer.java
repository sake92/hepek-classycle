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
import classycle.graph.GraphAttributes;
import classycle.graph.NameAttributes;
import classycle.graph.StrongComponent;
import classycle.graph.Vertex;

/**
 * XML renderer of a {@link StrongComponent}.
 *
 * @author Franz-Josef Elmer
 */
public class XMLStrongComponentRenderer extends AbstractStrongComponentRenderer {

    private final int minimumSize;

    /**
     * Creates an instance for the specified minimum number vertices.
     *
     * @param minimumSize
     *            Minimum number of vertices the {@link StrongComponent} should have to be rendered.
     */
    public XMLStrongComponentRenderer(int minimumSize) {
        this.minimumSize = minimumSize;
    }

    protected String getBestFragmentersElementName() {
        return "bestFragmenters";
    }

    protected String getCenterNodesElementName() {
        return "centerClasses";
    }

    protected String getNodeElementName() {
        return "classRef";
    }

    private MessageFormat getNodeElementTemplate() {
        return new MessageFormat("        <" + getNodeElementName() + " name=\"{0}\"/>\n");
    }

    private MessageFormat getNodeElementTemplateWithEccentricity() {
        return new MessageFormat("        <" + getNodeElementName() + " name=\"{0}\" eccentricity=\"{1}\""
                + " maximumFragmentSize=\"{2}\"/>\n");
    }

    protected String getNodesElementName() {
        return "classes";
    }

    protected String getStrongComponentElementName() {
        return "cycle";
    }

    private MessageFormat getStrongComponentElementTemplate() {
        return new MessageFormat(
                "    <" + getStrongComponentElementName() + " name=\"{0}\" size=\"{1}\" longestWalk=\"{2}\""
                        + " girth=\"{3}\" radius=\"{4}\" diameter=\"{5}\"" + " bestFragmentSize=\"{6}\">\n");
    }

    @Override
    public String render(StrongComponent component) {
        final StringBuilder result = new StringBuilder();
        if (component.getNumberOfVertices() >= minimumSize) {
            final String[] values = new String[7];
            values[0] = createName(component);
            values[1] = Integer.toString(component.getNumberOfVertices());
            values[2] = Integer.toString(component.getLongestWalk());
            final GraphAttributes attributes = (GraphAttributes) component.getAttributes();
            values[3] = Integer.toString(attributes.getGirth());
            values[4] = Integer.toString(attributes.getRadius());
            values[5] = Integer.toString(attributes.getDiameter());
            values[6] = Integer.toString(attributes.getBestFragmentSize());
            result.append(getStrongComponentElementTemplate().format(values));

            renderClasses(component, result);
            renderVertices(attributes.getCenterVertices(), result, getCenterNodesElementName());
            renderVertices(attributes.getBestFragmenters(), result, getBestFragmentersElementName());
            result.append("    </").append(getStrongComponentElementName()).append(">\n");
        }
        return result.toString();
    }

    private void renderClasses(StrongComponent component, StringBuilder result) {
        result.append("      <").append(getNodesElementName()).append(">\n");
        final int[] eccentricities = ((GraphAttributes) component.getAttributes()).getEccentricities();
        final int[] maximumFragmentSizes = ((GraphAttributes) component.getAttributes()).getMaximumFragmentSizes();
        final String[] values = new String[3];
        final MessageFormat template = getNodeElementTemplateWithEccentricity();
        for (int i = 0, n = component.getNumberOfVertices(); i < n; i++) {
            values[0] = ((NameAttributes) component.getVertex(i).getAttributes()).getName();
            values[1] = Integer.toString(eccentricities[i]);
            values[2] = Integer.toString(maximumFragmentSizes[i]);
            result.append(template.format(values));
        }
        result.append("      </").append(getNodesElementName()).append(">\n");
    }

    private void renderVertices(Vertex[] vertices, StringBuilder result, String tagName) {
        result.append("      <").append(tagName).append(">\n");
        final String[] values = new String[1];
        final MessageFormat template = getNodeElementTemplate();
        for (int i = 0; i < vertices.length; i++) {
            values[0] = ((NameAttributes) vertices[i].getAttributes()).getName();
            result.append(template.format(values));
        }
        result.append("      </").append(tagName).append(">\n");
    }
}
