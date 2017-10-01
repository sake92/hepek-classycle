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

import classycle.util.AndStringPattern;
import classycle.util.NotStringPattern;
import classycle.util.StringPattern;
import classycle.util.StringPatternSequence;
import classycle.util.TrueStringPattern;
import classycle.util.WildCardPattern;

/**
 * @author Franz-Josef Elmer
 */
public abstract class CommandLine {

    private static final String MERGE_INNER_CLASSES = "-mergeInnerClasses";
    private static final String INCLUDING_CLASSES = "-includingClasses=";
    private static final String EXCLUDING_CLASSES = "-excludingClasses=";
    private static final String REFLECTION_PATTERN = "-reflectionPattern=";

    private boolean _mergeInnerClasses;
    protected boolean _valid = true;
    protected StringPatternSequence _pattern = new AndStringPattern();
    protected StringPattern _reflectionPattern;
    protected String[] _classFiles;

    public CommandLine(String[] args) {
        int index = 0;
        for (; index < args.length && args[index].charAt(0) == '-'; index++) {
            handleOption(args[index]);
        }
        _classFiles = new String[args.length - index];
        System.arraycopy(args, index, _classFiles, 0, _classFiles.length);
        if (_classFiles.length == 0) {
            _valid = false;
        }
    }

    protected void handleOption(String argument) {
        if (argument.startsWith(MERGE_INNER_CLASSES)) {
            _mergeInnerClasses = true;
        } else if (argument.startsWith(INCLUDING_CLASSES)) {
            String patterns = argument.substring(INCLUDING_CLASSES.length());
            _pattern.appendPattern(WildCardPattern.createFromsPatterns(patterns, ","));
        } else if (argument.startsWith(EXCLUDING_CLASSES)) {
            String patterns = argument.substring(EXCLUDING_CLASSES.length());
            StringPattern p = WildCardPattern.createFromsPatterns(patterns, ",");
            _pattern.appendPattern(new NotStringPattern(p));
        } else if (argument.startsWith(REFLECTION_PATTERN)) {
            String patterns = argument.substring(REFLECTION_PATTERN.length());
            if (patterns.length() == 0) {
                _reflectionPattern = new TrueStringPattern();
            } else {
                _reflectionPattern = WildCardPattern.createFromsPatterns(patterns, ",");
            }
        } else {
            _valid = false;
        }
    }

    /**
     * Returns all class file descriptors (i.e., class files, directorys, jar files, or zip files).
     */
    public String[] getClassFiles() {
        return _classFiles;
    }

    /**
     * Returns the pattern fully qualified class names have to match. The pattern is based on the options
     * <tt>-includingClasses</tt> and <tt>-excludingClasses</tt>. If <tt>-includingClasses</tt> is missing every classes
     * is included which is not excluded. If <tt>-excludingClasses</tt> is missing no class is excluded.
     */
    public StringPattern getPattern() {
        return _pattern;
    }

    /**
     * Returns the reflection pattern as extracted from the option <tt>-reflectionPattern</tt>.
     */
    public StringPattern getReflectionPattern() {
        return _reflectionPattern;
    }

    /**
     * Returns <tt>true</tt> if the command line arguments and options are valid.
     */
    public boolean isValid() {
        return _valid;
    }

    /**
     * Returns <code>true</code> if the command line option <code>-mergeInnerClasses</code> occured.
     */
    public boolean isMergeInnerClasses() {
        return _mergeInnerClasses;
    }

    /** Returns the usage of correct command line arguments and options. */
    public String getUsage() {
        return "[" + MERGE_INNER_CLASSES + "] " + "[" + INCLUDING_CLASSES + "<pattern1>,<pattern2>,...] " + "["
                + EXCLUDING_CLASSES + "<pattern1>,<pattern2>,...] " + "[" + REFLECTION_PATTERN
                + "<pattern1>,<pattern2>,...] " + "<class files, zip/jar/war/ear files, or folders>";
    }

}
