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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import classycle.util.AndStringPattern;
import classycle.util.NotStringPattern;
import classycle.util.OrStringPattern;
import classycle.util.StringPattern;
import classycle.util.WildCardPattern;

/**
 * Parser for a dependency definition file.
 * 
 * @author Franz-Josef Elmer
 */
public class DependencyDefinitionParser {

    public static final String INDEPENDENT_OF_KEY_WORD = "independentOf";
    public static final String EXCLUDING_KEY_WORD = "excluding";
    public static final String DIRECTLY_INDEPENDENT_OF_KEY_WORD = "directlyIndependentOf";
    public static final String DEPENDENT_ONLY_ON_KEY_WORD = "dependentOnlyOn";
    public static final String CHECK_KEY_WORD = "check";
    public static final String LAYER_KEY_WORD = "layer";
    public static final String SHOW_KEY_WORD = "show";
    public static final String SETS_KEY_WORD = "sets";
    public static final String CLASS_CYCLES_KEY_WORD = "absenceOfClassCycles";
    public static final String PACKAGE_CYCLES_KEY_WORD = "absenceOfPackageCycles";
    public static final String IN_KEY_WORD = "in";
    public static final String LAYERING_OF_KEY_WORD = "layeringOf";
    public static final String STRICT_LAYERING_OF_KEY_WORD = "strictLayeringOf";
    private static final String[] INDEPENDENT = new String[] { INDEPENDENT_OF_KEY_WORD,
            DIRECTLY_INDEPENDENT_OF_KEY_WORD, DEPENDENT_ONLY_ON_KEY_WORD };
    private static final String[] EXCLUDING = new String[] { EXCLUDING_KEY_WORD };
    private static final String PROP_DEF_BEGIN = "{";
    private static final String PROP_BEGIN = "${";
    private static final String PROP_END = "}";

    private final DependencyProperties _properties;
    private final ResultRenderer _renderer;
    final SetDefinitionRepository _setDefinitions = new SetDefinitionRepository();
    final LayerDefinitionRepository _layerDefinitions = new LayerDefinitionRepository();
    private final ArrayList<Statement> _statements = new ArrayList<>();

    public DependencyDefinitionParser(String dependencyDefinition, DependencyProperties properties,
            ResultRenderer renderer) {
        _properties = properties;
        _renderer = renderer;
        try {
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new StringReader(dependencyDefinition));
            String line;
            int lineNumber = 0;
            int lineNumberOfCurrentLogicalLine = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (!line.startsWith("#")) {
                    buffer.append(line);
                    if (line.endsWith("\\")) {
                        buffer.deleteCharAt(buffer.length() - 1).append(' ');
                    } else {
                        String logicalLine = replaceProperties(new String(buffer).trim(),
                                lineNumberOfCurrentLogicalLine);
                        if (logicalLine.length() > 0) {
                            parseLine(logicalLine, lineNumberOfCurrentLogicalLine);
                        }
                        buffer.setLength(0);
                        lineNumberOfCurrentLogicalLine = lineNumber + 1;
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    private String replaceProperties(String line, int lineNumber) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < line.length();) {
            int index = line.indexOf(PROP_BEGIN, i);
            if (index >= 0) {
                buffer.append(line.substring(i, index));
                i = line.indexOf(PROP_END, index);
                if (i < 0) {
                    throwException("Missing '" + PROP_END + "'.", lineNumber, -1);
                }
                String name = line.substring(index + PROP_BEGIN.length(), i);
                i += PROP_END.length();
                String property = _properties.getProperty(name);
                if (property == null) {
                    String message = "Undefines property " + line.substring(index, i);
                    throwException(message, lineNumber, -1);
                } else {
                    buffer.append(property);
                }
            } else {
                buffer.append(line.substring(i));
                i = line.length();
            }
        }
        return new String(buffer);
    }

    public Statement[] getStatements() {
        return (Statement[]) _statements.toArray(new Statement[0]);
    }

    private void parseLine(String line, int lineNumber) {
        if (line.startsWith(PROP_DEF_BEGIN)) {
            parsePropertyDefinition(line, lineNumber);
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(line);
        String[] tokens = new String[tokenizer.countTokens()];
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokenizer.nextToken();
        }
        String firstToken = tokens[0];
        if (firstToken.startsWith("[")) {
            parseSetDefinition(tokens, lineNumber);
        } else if (firstToken.equals(SHOW_KEY_WORD)) {
            parseShowStatement(tokens, lineNumber);
        } else if (firstToken.equals(LAYER_KEY_WORD)) {
            parseLayerDefinition(tokens, lineNumber);

        } else if (firstToken.equals(CHECK_KEY_WORD)) {
            parseCheckStatement(tokens, lineNumber);

        } else {
            throwException("Expecting either a property definition, a set name, '" + SHOW_KEY_WORD + "', '"
                    + LAYER_KEY_WORD + "', or '" + CHECK_KEY_WORD + "'.", lineNumber, 0);
        }
    }

    private void parsePropertyDefinition(String line, int lineNumber) {
        int index = line.indexOf(PROP_END);
        if (index < 0) {
            throwException("Missing '" + PROP_END + "' in property definition.", lineNumber, -1);
        }
        String name = line.substring(PROP_DEF_BEGIN.length(), index);
        String def = line.substring(index + PROP_END.length()).trim();
        if (def.startsWith("=") == false) {
            throwException("Missing '=' in propety definition.", lineNumber, -1);
        }
        _properties.setProperty(name, def.substring(1).trim());
    }

    private void parseSetDefinition(String[] tokens, int lineNumber) {
        String setName = tokens[0];
        if (setName.endsWith("]") == false) {
            throwException("Set name has to end with ']'.", lineNumber, 0);
        }
        if (_setDefinitions.contains(setName)) {
            throwException("Set " + setName + " already defined.", lineNumber, 0);
        }
        checkForEqualCharacter(tokens, lineNumber, 1);
        StringPattern[][] lists = getLists(tokens, lineNumber, EXCLUDING, 2);
        if (lists[0].length == 0 && lists[1].length == 0) {
            throwException("Missing terms in set definition.", lineNumber, 2);
        }
        AndStringPattern definition = new AndStringPattern();
        if (lists[0].length > 0) {
            definition.appendPattern(createOrSequence(lists[0]));
        }
        if (lists[1].length > 0) {
            definition.appendPattern(new NotStringPattern(createOrSequence(lists[1])));
        }
        _setDefinitions.put(setName, definition);
    }

    private void checkForEqualCharacter(String[] tokens, int lineNumber, int index) {
        if (tokens.length < index + 1 || !tokens[index].equals("=")) {
            throwException("'=' missing.", lineNumber, index);
        }
    }

    private StringPattern createOrSequence(StringPattern[] patterns) {
        OrStringPattern result = new OrStringPattern();
        for (int i = 0; i < patterns.length; i++) {
            result.appendPattern(patterns[i]);
        }
        return result;
    }

    private StringPattern createPattern(String term, int lineNumber, int tokenIndex) {
        StringPattern pattern = _setDefinitions.getPattern(term);
        if (pattern == null) {
            if (term.startsWith("[") && term.endsWith("]")) {
                throwException("Set " + term + " is undefined.", lineNumber, tokenIndex);
            }
            if (term.indexOf('.') < 0 && term.indexOf('*') < 0 && term.length() > 0
                    && Character.isLowerCase(term.charAt(0))) {
                throwException(
                        "Patterns without a '.' and a '*' should not start " + "with a lower-case letter: " + term,
                        lineNumber, tokenIndex);
            }
            pattern = new WildCardPattern(term);
        }
        return pattern;
    }

    private void parseLayerDefinition(String[] tokens, int lineNumber) {
        if (tokens.length < 2) {
            throwException("Missing layer name.", lineNumber, 1);
        }
        String layerName = tokens[1];
        if (_layerDefinitions.contains(layerName)) {
            throwException("Layer '" + layerName + "' already defined.", lineNumber, 1);
        }
        checkForEqualCharacter(tokens, lineNumber, 2);
        if (tokens.length < 4) {
            throwException("Missing terms in definition of layer '" + layerName + "'.", lineNumber, 3);
        }
        ArrayList<StringPattern> layer = new ArrayList<>();
        for (int i = 3; i < tokens.length; i++) {
            layer.add(createPattern(tokens[i], lineNumber, i));
        }
        StringPattern[] sets = new StringPattern[layer.size()];
        _layerDefinitions.put(layerName, (StringPattern[]) layer.toArray(sets));
    }

    private void parseShowStatement(String[] tokens, int lineNumber) {
        if (tokens.length < 2) {
            throwException("Missing display preference(s).", lineNumber, 1);
        }
        Preference[] preferences = new Preference[tokens.length - 1];
        for (int i = 0; i < preferences.length; i++) {
            preferences[i] = _renderer.getPreferenceFactory().get(tokens[i + 1]);
            if (preferences[i] == null) {
                throwException("Unknown display preference: " + tokens[i + 1], lineNumber, i + 1);
            }
        }
        _statements.add(new ShowStatement(_renderer, preferences));
    }

    private void parseCheckStatement(String[] tokens, int lineNumber) {
        if (tokens.length < 2) {
            throwException("Missing checking statement.", lineNumber, 1);
        }
        if (tokens[1].equals(STRICT_LAYERING_OF_KEY_WORD) || tokens[1].equals(LAYERING_OF_KEY_WORD)) {
            createLayeringStatement(tokens, lineNumber);
        } else if (tokens[1].equals(SETS_KEY_WORD)) {
            createCheckSetStatements(tokens, lineNumber);
        } else if (tokens[1].equals(CLASS_CYCLES_KEY_WORD) || tokens[1].equals(PACKAGE_CYCLES_KEY_WORD)) {
            createCyclesStatement(tokens, lineNumber);
        } else {
            createDependencyStatement(tokens, lineNumber);
        }
    }

    private void createCyclesStatement(String[] tokens, int lineNumber) {
        boolean packageCycles = tokens[1].equals(PACKAGE_CYCLES_KEY_WORD);
        if (tokens.length != 6) {
            throwException("Invalid statement.", lineNumber, tokens.length);
        }
        if (tokens[2].equals(">") == false) {
            throwException("'>' expected.", lineNumber, 2);
        }
        int size = 0;
        try {
            size = Integer.parseInt(tokens[3]);
        } catch (NumberFormatException e) {
            throwException("Number expected.", lineNumber, 3);
        }
        if (size < 1) {
            throwException("Size has to be >= 1", lineNumber, 3);
        }
        if (tokens[4].equals(IN_KEY_WORD) == false) {
            throwException("'in' expected.", lineNumber, 4);
        }
        StringPattern pattern = createPattern(tokens[5], lineNumber, 4);
        _statements.add(new CheckCyclesStatement(pattern, size, packageCycles, _setDefinitions));
    }

    private void createCheckSetStatements(String[] tokens, int lineNumber) {
        if (tokens.length < 3) {
            throwException("No sets to check.", lineNumber, 2);
        }
        for (int i = 2; i < tokens.length; i++) {
            StringPattern pattern = createPattern(tokens[i], lineNumber, i);
            _statements.add(new CheckSetStatement(pattern, _setDefinitions));
        }
    }

    private void createLayeringStatement(String[] tokens, int lineNumber) {
        StringPattern[][] layers = new StringPattern[tokens.length - 2][];
        for (int i = 0; i < layers.length; i++) {
            String name = tokens[i + 2];
            layers[i] = _layerDefinitions.getLayer(name);
            if (layers[i] == null) {
                throwException("Undefined layer '" + name + "'.", lineNumber, i + 2);
            }
        }
        boolean strict = tokens[1].equals(STRICT_LAYERING_OF_KEY_WORD);
        _statements.add(new LayeringStatement(layers, strict, _setDefinitions, _layerDefinitions, _renderer));
    }

    private void createDependencyStatement(String[] tokens, int lineNumber) {
        StringPattern[][] lists = getLists(tokens, lineNumber, INDEPENDENT, 1);
        if (lists[0].length == 0) {
            throwException("Missing start sets.", lineNumber, 1);
        }
        if (lists[1].length == 0) {
            throwException("Missing end sets. Probably one of the following " + "key words are missing: "
                    + Arrays.asList(INDEPENDENT), lineNumber, tokens.length);
        }
        _statements.add(
                new DependencyStatement(lists[0], lists[1], tokens[lists[0].length + 1], _setDefinitions, _renderer));
    }

    private StringPattern[][] getLists(String[] tokens, int lineNumber, String[] keyWords, int startIndex) {
        ArrayList<StringPattern> startSets = new ArrayList<>();
        ArrayList<StringPattern> endSets = new ArrayList<>();
        ArrayList<StringPattern> currentList = startSets;
        for (int i = startIndex; i < tokens.length; i++) {
            String token = tokens[i];
            if (isAKeyWord(token, keyWords)) {
                if (currentList == endSets) {
                    throwException("Invalid appearance of key word '" + token + "'.", lineNumber, i);
                }
                currentList = endSets;
            } else {
                currentList.add(createPattern(token, lineNumber, i));
            }
        }
        StringPattern[][] result = new StringPattern[2][];
        result[0] = (StringPattern[]) startSets.toArray(new StringPattern[0]);
        result[1] = (StringPattern[]) endSets.toArray(new StringPattern[0]);
        return result;
    }

    private boolean isAKeyWord(String token, String[] keyWords) {
        boolean result = false;
        for (int i = 0; i < keyWords.length; i++) {
            if (keyWords[i].equals(token)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void throwException(String message, int lineNumber, int tokenIndex) {
        StringBuffer buffer = new StringBuffer("Error in line ");
        buffer.append(lineNumber);
        if (tokenIndex >= 0) {
            buffer.append(" token ").append(tokenIndex + 1);
        }
        buffer.append(": ").append(message);
        throw new IllegalArgumentException(new String(buffer));
    }
}
