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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import classycle.Analyser;
import classycle.graph.AtomicVertex;

/**
 * Checks a class graph for unwanted dependencies. The dependencies are described by a dependency definition file
 * (<tt>.ddf</tt>).
 *
 * @author Franz-Josef Elmer
 */
public class DependencyChecker {

    private final Analyser analyser;
    private final ResultRenderer renderer;
    private final DependencyProcessor processor;

    /**
     * Creates a new instance. Note, that the constructor does not create the graph. It only parses
     * <tt>dependencyDefinition</tt> as a preprocessing step. The calculation of the graph is done in
     * {@link #check(PrintWriter)}.
     *
     * @param analyser
     *            Analyzer instance.
     * @param dependencyDefinition
     *            Description (as read from a .ddf file) of the dependencies to be checked.
     * @param renderer
     *            Output renderer for unwanted dependencies found.
     */
    public DependencyChecker(Analyser analyser, String dependencyDefinition, Map<String, String> properties,
            ResultRenderer renderer) {
        this.analyser = analyser;
        this.renderer = renderer;
        final DependencyProperties dp = new DependencyProperties(properties);
        processor = new DependencyProcessor(dependencyDefinition, dp, renderer);
    }

    /**
     * Checks the graph.
     */
    public Result check() {
        final AtomicVertex[] graph = analyser.getClassGraph();
        final ResultContainer result = new ResultContainer();
        while (processor.hasMoreStatements()) {
            result.add(processor.executeNextStatement(graph));
        }
        return result;
    }

    /**
     * Checks the graph and write unwanted dependencies onto the specified writer.
     *
     * @return <tt>true</tt> if no unwanted dependency has been found.
     */
    public boolean check(PrintWriter writer) {
        final Result result = check();
        writer.print(renderer.render(result));
        return result.isOk();
    }

    /**
     * Runs the DependencyChecker application. Exit 0 if no unwanted dependency found otherwise 1 is returned.
     */
    public static void main(String[] args) {
        final DependencyCheckerCommandLine commandLine = new DependencyCheckerCommandLine(args);
        if (!commandLine.isValid()) {
            System.out.println(
                    "Usage: java -cp classycle.jar " + "classycle.DependencyChecker " + commandLine.getUsage());
            System.exit(1);
        }

        final Analyser analyser = new Analyser(commandLine.getClassFiles(), commandLine.getPattern(),
                commandLine.getReflectionPattern(), commandLine.isMergeInnerClasses());

        final HashMap<String, String> props = new HashMap<>();
        for (final String name : System.getProperties().stringPropertyNames()) {
            props.put(name, System.getProperties().getProperty(name));
        }
        final DependencyChecker dependencyChecker = new DependencyChecker(analyser,
                commandLine.getDependencyDefinition(), props, commandLine.getRenderer());
        final PrintWriter printWriter = new PrintWriter(System.out);
        final boolean ok = dependencyChecker.check(printWriter);
        printWriter.flush();
        System.exit(ok ? 0 : 1);
    }

}
