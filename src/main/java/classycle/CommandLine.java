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

    private boolean mergeInnerClasses;
    protected boolean valid = true;
    protected StringPatternSequence pattern = new AndStringPattern();
    protected StringPattern reflectionPattern;
    protected String[] classFiles;

    public CommandLine(String[] args) {
        int index = 0;
        for (; index < args.length && args[index].charAt(0) == '-'; index++) {
            handleOption(args[index]);
        }
        classFiles = new String[args.length - index];
        System.arraycopy(args, index, classFiles, 0, classFiles.length);
        if (classFiles.length == 0) {
            valid = false;
        }
    }

    /**
     * Returns all class file descriptors (i.e., class files, directorys, jar files, or zip files).
     */
    public String[] getClassFiles() {
        return classFiles;
    }

    /**
     * Returns the pattern fully qualified class names have to match. The pattern is based on the options
     * <tt>-includingClasses</tt> and <tt>-excludingClasses</tt>. If <tt>-includingClasses</tt> is missing every classes
     * is included which is not excluded. If <tt>-excludingClasses</tt> is missing no class is excluded.
     */
    public StringPattern getPattern() {
        return pattern;
    }

    /**
     * Returns the reflection pattern as extracted from the option <tt>-reflectionPattern</tt>.
     */
    public StringPattern getReflectionPattern() {
        return reflectionPattern;
    }

    /** Returns the usage of correct command line arguments and options. */
    public String getUsage() {
        return "[" + MERGE_INNER_CLASSES + "] " + "[" + INCLUDING_CLASSES + "<pattern1>,<pattern2>,...] " + "["
                + EXCLUDING_CLASSES + "<pattern1>,<pattern2>,...] " + "[" + REFLECTION_PATTERN
                + "<pattern1>,<pattern2>,...] " + "<class files, zip/jar/war/ear files, or folders>";
    }

    protected void handleOption(String argument) {
        if (argument.startsWith(MERGE_INNER_CLASSES)) {
            mergeInnerClasses = true;
        } else if (argument.startsWith(INCLUDING_CLASSES)) {
            final String patterns = argument.substring(INCLUDING_CLASSES.length());
            pattern.appendPattern(WildCardPattern.createFromsPatterns(patterns, ","));
        } else if (argument.startsWith(EXCLUDING_CLASSES)) {
            final String patterns = argument.substring(EXCLUDING_CLASSES.length());
            final StringPattern p = WildCardPattern.createFromsPatterns(patterns, ",");
            pattern.appendPattern(new NotStringPattern(p));
        } else if (argument.startsWith(REFLECTION_PATTERN)) {
            final String patterns = argument.substring(REFLECTION_PATTERN.length());
            if (patterns.length() == 0) {
                reflectionPattern = new TrueStringPattern();
            } else {
                reflectionPattern = WildCardPattern.createFromsPatterns(patterns, ",");
            }
        } else {
            valid = false;
        }
    }

    /**
     * Returns <code>true</code> if the command line option <code>-mergeInnerClasses</code> occured.
     */
    public boolean isMergeInnerClasses() {
        return mergeInnerClasses;
    }

    /**
     * Returns <tt>true</tt> if the command line arguments and options are valid.
     */
    public boolean isValid() {
        return valid;
    }

}
