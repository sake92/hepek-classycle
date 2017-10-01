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
package classycle;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import classycle.graph.AtomicVertex;
import classycle.graph.StrongComponent;
import classycle.graph.StrongComponentAnalyser;
import classycle.renderer.AtomicVertexRenderer;
import classycle.renderer.PlainStrongComponentRenderer;
import classycle.renderer.StrongComponentRenderer;
import classycle.renderer.TemplateBasedClassRenderer;
import classycle.renderer.XMLClassRenderer;
import classycle.renderer.XMLStrongComponentRenderer;
import classycle.util.StringPattern;
import classycle.util.Text;
import classycle.util.TrueStringPattern;

/**
 * Main class of the Classycle tool. Runs on the command line and produces a report.
 *
 * @author Franz-Josef Elmer
 * @author Sakib Hadžiavdić
 */
public class Analyser {

    private static final String VERSION = "1.5";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String CSV_TEMPLATE = "{0},{1},{3},{2},{4},{5},{6},{7},{8},{9}\n";

    private final String[] classFiles;
    private final StringPattern pattern;
    private final StringPattern reflectionPattern;
    private final boolean mergeInnerClasses;
    private StrongComponentAnalyser classAnalyser;
    private StrongComponentAnalyser packageAnalyser;

    /**
     * Creates an instance for the specified files or folders.
     *
     * @param classFiles
     *            Absolute or relative file/folder names.
     */
    public Analyser(String[] classFiles) {
        this(classFiles, new TrueStringPattern(), null, false);
    }

    /**
     * Creates an instance for the specified files or folders which are filtered by the specified {@link StringPattern}
     * object.
     *
     * @param classFiles
     *            Absolute or relative file names.
     * @param pattern
     *            Pattern fully-qualified class name have to match in order to be a part of the class graph.
     * @param reflectionPattern
     *            Pattern ordinary string constants of a class file have to fullfill in order to be handled as a class
     *            references. In addition such strings have to be syntactically valid fully qualified class names. If
     *            <tt>null</tt> ordinary string constants will not be checked.
     * @param mergeInnerClasses
     *            If <code>true</code> merge inner classes with its outer class
     */
    public Analyser(String[] classFiles, StringPattern pattern, StringPattern reflectionPattern,
            boolean mergeInnerClasses) {
        this.classFiles = classFiles;
        this.pattern = pattern;
        this.reflectionPattern = reflectionPattern;
        this.mergeInnerClasses = mergeInnerClasses;
    }

    /**
     * Calculates the for each class its layer index. The layer index of a class is the length of the longest path in
     * the acyclic graph of strong components starting at the strong component to which the class belongs.
     *
     * @return the duration of this operation in milliseconds.
     * @throws IllegalStateException
     *             if this method is called before {@link #createClassGraph()}.
     */
    public long calculateClassLayerMap() {
        checkClassGraph("calculateClassLayerMap()");
        final long time = System.currentTimeMillis();
        classAnalyser.getLayerMap();
        return System.currentTimeMillis() - time;
    }

    /**
     * Calculates the for each package its layer index. The layer index of a package is the length of the longest path
     * in the acyclic graph of strong components starting at the strong component to which the package belongs.
     *
     * @return the duration of this operation in milliseconds.
     * @throws IllegalStateException
     *             if this method is called before {@link #createPackageGraph()}.
     */
    public long calculatePackageLayerMap() {
        checkPackageGraph("calculatePackageLayerMap()");
        final long time = System.currentTimeMillis();
        packageAnalyser.getLayerMap();
        return System.currentTimeMillis() - time;
    }

    private void checkClassGraph(String method) {
        if (classAnalyser == null) {
            throw new IllegalStateException(method + " should be invoked after createClassGraph().");
        }
    }

    private void checkPackageGraph(String method) {
        if (packageAnalyser == null) {
            throw new IllegalStateException(method + " should be invoked after createPackageGraph().");
        }
    }

    /**
     * Condenses the class graph to an acyclic graph of its strong components.
     *
     * @return the duration of this operation in milliseconds.
     * @throws IllegalStateException
     *             if this method is called before {@link #createClassGraph()}.
     */
    public long condenseClassGraph() {
        checkClassGraph("condenseClassGraph()");
        final long time = System.currentTimeMillis();
        classAnalyser.getCondensedGraph();
        return System.currentTimeMillis() - time;
    }

    /**
     * Condenses the package graph to an acyclic graph of its strong components.
     *
     * @return the duration of this operation in milliseconds.
     * @throws IllegalStateException
     *             if this method is called before {@link #createPackageGraph()}.
     */
    public long condensePackageGraph() {
        checkPackageGraph("condensePackageGraph()");
        final long time = System.currentTimeMillis();
        packageAnalyser.getCondensedGraph();
        return System.currentTimeMillis() - time;
    }

    /**
     * Parses the class files and creates the class graph.
     *
     * @return the duration of this operation in milliseconds.
     * @throws IOException
     *             if a problem occured during reading
     */
    public long createClassGraph() throws IOException {
        final long time = System.currentTimeMillis();
        final AtomicVertex[] classGraph = Parser.readClassFiles(classFiles, pattern, reflectionPattern,
                mergeInnerClasses);
        classAnalyser = new StrongComponentAnalyser(classGraph);
        return System.currentTimeMillis() - time;
    }

    /**
     * Creates package graph from the class graph.
     *
     * @return the duration of this operation in milliseconds.
     * @throws IllegalStateException
     *             if this method is called before {@link #createClassGraph()}.
     */
    public long createPackageGraph() {
        checkClassGraph("createPackageGraph()");
        final long time = System.currentTimeMillis();
        final PackageProcessor processor = new PackageProcessor();
        processor.deepSearchFirst(classAnalyser.getGraph());
        packageAnalyser = new StrongComponentAnalyser(processor.getGraph());
        return System.currentTimeMillis() - time;
    }

    /**
     * Returns the class graph. Invokes {@link #createClassGraph()} if not already invoked.
     */
    public AtomicVertex[] getClassGraph() {
        if (classAnalyser == null) {
            try {
                createClassGraph();
            } catch (final IOException e) {
                throw new RuntimeException(e.toString());
            }
        }
        return classAnalyser.getGraph();
    }

    /**
     * Calculates for each class its layer index and returns a <tt>Map</tt> where the classes are the keys (type
     * {@link AtomicVertex}) and the layer indices are the values (type <tt>Integer</tt>).
     *
     * @throws IllegalStateException
     *             if this method is called before {@link #createClassGraph()}.
     */
    public Map<AtomicVertex, Integer> getClassLayerMap() {
        checkClassGraph("getClassLayerMap()");
        return classAnalyser.getLayerMap();
    }

    /**
     * Returns the condensed the class graph, i.e.&nbsp;the acyclic graph of its strong components.
     *
     * @throws IllegalStateException
     *             if this method is called before {@link #createClassGraph()}.
     */
    public StrongComponent[] getCondensedClassGraph() {
        checkClassGraph("getCondenseClassGraph()");
        return classAnalyser.getCondensedGraph();
    }

    /**
     * Returns the condensed package graph, i.e.&nbsp;the acyclic graph of its strong components.
     *
     * @return the duration of this operation in milliseconds.
     * @throws IllegalStateException
     *             if this method is called before {@link #createPackageGraph()}.
     */
    public StrongComponent[] getCondensedPackageGraph() {
        checkPackageGraph("getCondensedPackageGraph()");
        return packageAnalyser.getCondensedGraph();
    }

    private StrongComponent getCycleFor(AtomicVertex vertex, List<StrongComponent> cycles) {
        for (int i = 0, n = cycles.size(); i < n; i++) {
            final StrongComponent cycle = cycles.get(i);
            for (int j = 0, m = cycle.getNumberOfVertices(); j < m; j++) {
                if (cycle.getVertex(j) == vertex) {
                    return cycle;
                }
            }
        }
        return null;
    }

    /**
     * Counts the number of external classes.
     */
    public int getNumberOfExternalClasses() {
        final AtomicVertex[] graph = getClassGraph();
        final HashSet<String> usedClasses = new HashSet<>();
        int result = 0;
        for (int i = 0; i < graph.length; i++) {
            final AtomicVertex vertex = graph[i];
            for (int j = 0, n = vertex.getNumberOfOutgoingArcs(); j < n; j++) {
                final ClassAttributes attributes = (ClassAttributes) vertex.getHeadVertex(j).getAttributes();
                if (ClassAttributes.UNKNOWN.equals(attributes.getType())) {
                    if (!usedClasses.contains(attributes.getName())) {
                        result++;
                        usedClasses.add(attributes.getName());
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns the package graph created the class graph.
     *
     * @throws IllegalStateException
     *             if this method is called before {@link #createClassGraph()}.
     */
    public AtomicVertex[] getPackageGraph() {
        if (packageAnalyser == null) {
            createPackageGraph();
        }
        return packageAnalyser.getGraph();
    }

    /**
     * Calculates the for each package its layer index and returns a <tt>Map</tt> where the packages are the keys (type
     * {@link AtomicVertex}) and the layer indices are the values (type <tt>Integer</tt>).
     *
     * @throws IllegalStateException
     *             if this method is called before {@link #createPackageGraph()}.
     */
    public Map<AtomicVertex, Integer> getPackageLayerMap() {
        checkPackageGraph("getPackageLayerMap()");
        return packageAnalyser.getLayerMap();
    }

    private List<StrongComponent> getTrueCycles(StrongComponent[] cycles) {
        final List<StrongComponent> list = new ArrayList<>();
        if (cycles != null) {
            for (int i = 0; i < cycles.length; i++) {
                if (cycles[i].getNumberOfVertices() > 1) {
                    list.add(cycles[i]);
                }
            }
        }
        return list;
    }

    /**
     * Prints for each strong component of the class graph a raw output into the specified writer. The strong component
     * must have at least <tt>minSize</tt> classes in order to be printed out. This output includes all classes of the
     * strong component.
     *
     * @param writer
     *            Output stream.
     * @param minSize
     *            Minimum size of the strong component.
     * @throws IllegalStateException
     *             if this method is called before {@link #createClassGraph()}.
     */
    public void printComponents(PrintWriter writer, int minSize) {
        checkClassGraph("printComponents()");
        final StrongComponent[] components = getCondensedClassGraph();
        final StrongComponentRenderer renderer = new PlainStrongComponentRenderer();
        for (int i = 0; i < components.length; i++) {
            final StrongComponent component = components[i];
            if (component.getNumberOfVertices() >= minSize) {
                writer.println(renderer.render(component));
            }
        }
        writer.close();
    }

    /**
     * Prints a CSV report into the specified writer. Delimiter is ','. First, a header with column titles is print. The
     * columns are
     * <ol>
     * <li>class name
     * <li>inner class (<tt>false</tt> or <tt>true</tt>)
     * <li>size (in bytes)
     * <li>used by (number of classes using this class)
     * <li>uses internal classes (number of classes of the graph used by this class)
     * <li>uses external classes (number of external classes used by this class)
     * <li>layer index
     * </ol>
     *
     * @param writer
     *            Output stream.
     */
    public void printCSV(PrintWriter writer) {
        final StrongComponent[] cycles = getCondensedClassGraph();
        final AtomicVertex[] graph = getClassGraph();
        final Map<AtomicVertex, Integer> map = getClassLayerMap();
        writer.println("class name,type,inner class,size,used by," + "uses internal classes,uses external classes,"
                + "layer index,cycle,source");
        render(graph, cycles, map, new TemplateBasedClassRenderer(CSV_TEMPLATE), writer);
        writer.close();
    }

    /**
     * Prints for each class a raw output into the specified writer. This output includes all classes used by the class.
     *
     * @param writer
     *            Output stream.
     */
    public void printRaw(PrintWriter writer) {
        final AtomicVertex[] graph = getClassGraph();
        for (int i = 0; i < graph.length; i++) {
            final AtomicVertex vertex = graph[i];
            writer.println(vertex.getAttributes());
            for (int j = 0, n = vertex.getNumberOfOutgoingArcs(); j < n; j++) {
                writer.println("    " + vertex.getHeadVertex(j).getAttributes());
            }
        }
        writer.close();
    }

    /**
     * Prints an XML report into the specified writer.
     *
     * @param title
     *            Title of the report.
     * @param packagesOnly
     *            if <tt>true</tt> classes are omitted.
     * @param writer
     *            Output stream.
     * @throws IllegalStateException
     *             if this method is called before {@link #createPackageGraph()}.
     */
    public void printXML(String title, boolean packagesOnly, PrintWriter writer) {
        checkPackageGraph("printXML()");
        writer.println("<?xml version='1.0' encoding='UTF-8'?>");
        writer.println("<?xml-stylesheet type='text/xsl' " + "href='reportXMLtoHTML.xsl'?>");
        writer.print("<classycle title='");
        writer.print(Text.excapeForXML(title));
        writer.print("' date='");
        writer.print(DATE_FORMAT.format(new Date()));
        writer.println("'>");
        if (!packagesOnly) {
            final StrongComponent[] components = getCondensedClassGraph();
            writer.println("  <cycles>");
            final StrongComponentRenderer sRenderer = new XMLStrongComponentRenderer(2);
            for (int i = 0; i < components.length; i++) {
                writer.print(sRenderer.render(components[i]));
            }
            writer.println("  </cycles>");
            writer.println("  <classes numberOfExternalClasses=\"" + getNumberOfExternalClasses() + "\">");
            final AtomicVertex[] graph = getClassGraph();
            final Map<AtomicVertex, Integer> layerMap = getClassLayerMap();
            render(graph, components, layerMap, new XMLClassRenderer(), writer);
            writer.println("  </classes>");
        }
        final StrongComponent[] components = getCondensedPackageGraph();
        writer.println("  <packageCycles>");
        final StrongComponentRenderer sRenderer = new XMLPackageStrongComponentRenderer(2);
        for (int i = 0; i < components.length; i++) {
            writer.print(sRenderer.render(components[i]));
        }
        writer.println("  </packageCycles>");
        writer.println("  <packages>");
        final AtomicVertex[] graph = getPackageGraph();
        final Map<AtomicVertex, Integer> layerMap = getPackageLayerMap();
        render(graph, components, layerMap, new XMLPackageRenderer(), writer);
        writer.println("  </packages>");

        writer.println("</classycle>");
        writer.close();
    }

    /**
     * Reads and analyses class files. Does only package analysis if <tt>packagesOnly == true</tt>. Reports progress of
     * analysis on <tt>System.out</tt>.
     *
     * @throws IOException
     *             in case of reading problems.
     */
    public void readAndAnalyse(boolean packagesOnly) throws IOException {
        System.out.println("============= Classycle V" + VERSION + " =============");
        System.out.println("========== by Franz-Josef Elmer ==========");
        System.out.print("read class files and create class graph ... ");
        long duration = createClassGraph();
        System.out.println("done after " + duration + " ms: " + getClassGraph().length + " classes analysed.");

        if (!packagesOnly) {
            // Condense class graph
            System.out.print("condense class graph ... ");
            duration = condenseClassGraph();
            System.out.println(
                    "done after " + duration + " ms: " + getCondensedClassGraph().length + " strong components found.");

            // Calculate class layer
            System.out.print("calculate class layer indices ... ");
            duration = calculateClassLayerMap();
            System.out.println("done after " + duration + " ms.");
        }
        System.out.print("create package graph ... ");
        duration = createPackageGraph();
        System.out.println("done after " + duration + " ms: " + getPackageGraph().length + " packages.");
        // Condense package graph
        System.out.print("condense package graph ... ");
        duration = condensePackageGraph();
        System.out.println(
                "done after " + duration + " ms: " + getCondensedPackageGraph().length + " strong components found.");
        // Calculate package layer
        System.out.print("calculate package layer indices ... ");
        duration = calculatePackageLayerMap();
        System.out.println("done after " + duration + " ms.");
    }

    private void render(AtomicVertex[] graph, StrongComponent[] cycles, Map<AtomicVertex, Integer> layerMap,
            AtomicVertexRenderer renderer, PrintWriter writer) {
        final List<StrongComponent> list = getTrueCycles(cycles);
        for (int i = 0; i < graph.length; i++) {
            final AtomicVertex vertex = graph[i];
            final Integer layerIndex = layerMap.get(vertex);
            writer.print(renderer.render(vertex, getCycleFor(vertex, list),
                    layerIndex == null ? -1 : layerIndex.intValue()));
        }
    }

    /**
     * Main method of the Analyser. Prints on the console its usage if some invalid command line argument occurs or is
     * missed.
     *
     * @param args
     *            command line arguments.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final AnalyserCommandLine commandLine = new AnalyserCommandLine(args);
        if (!commandLine.isValid()) {
            System.out.println("Usage: java -jar classycle.jar " + commandLine.getUsage());
            System.exit(0);
        }

        final Analyser analyser = new Analyser(commandLine.getClassFiles(), commandLine.getPattern(),
                commandLine.getReflectionPattern(), commandLine.isMergeInnerClasses());
        analyser.readAndAnalyse(commandLine.isPackagesOnly());

        // Create report(s)
        if (commandLine.getXmlFile() != null) {
            analyser.printXML(commandLine.getTitle(), commandLine.isPackagesOnly(),
                    new PrintWriter(new FileWriter(commandLine.getXmlFile())));
        }
        if (commandLine.getCsvFile() != null) {
            analyser.printCSV(new PrintWriter(new FileWriter(commandLine.getCsvFile())));
        }
        if (commandLine.isRaw()) {
            analyser.printRaw(new PrintWriter(System.out));
        }
        if (commandLine.isCycles() || commandLine.isStrong()) {
            analyser.printComponents(new PrintWriter(System.out), commandLine.isCycles() ? 2 : 1);
        }
    }
}
