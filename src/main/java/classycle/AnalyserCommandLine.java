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

/**
 * Process command line arguments and options for the main application {@link Analyser}.
 *
 * @author Franz-Josef Elmer
 */
public class AnalyserCommandLine extends CommandLine {

    private static final String XML_FILE = "-xmlFile=";
    private static final String CSV_FILE = "-csvFile=";
    private static final String TITLE = "-title=";
    private boolean packagesOnly;
    private boolean raw;
    private boolean cycles;
    private boolean strong;
    private String title;
    private String xmlFile;
    private String csvFile;

    public AnalyserCommandLine(String[] args) {
        super(args);
        if (title == null && classFiles.length > 0) {
            title = classFiles[0];
        }
    }

    /**
     * Returns the name of the CSV file as defined by the option <tt>-csvFile</tt>.
     *
     * @return <tt>null</tt> if undefined.
     */
    public String getCsvFile() {
        return csvFile;
    }

    /**
     * Returns the title by the option <tt>-title</tt>. If undefined {@link #getClassFiles()}<tt>[0]</tt> will be used.
     *
     * @return String
     */
    public String getTitle() {
        return title;
    }

    /** Returns the usage of correct command line arguments and options. */
    @Override
    public String getUsage() {
        return "[-raw] [-packagesOnly] [-cycles|-strong] " + "[" + XML_FILE + "<file>] [" + CSV_FILE + "<file>] " + "["
                + TITLE + "<title>] " + super.getUsage();
    }

    /**
     * Returns the name of the XML file as defined by the option <tt>-xmlFile</tt>.
     *
     * @return <tt>null</tt> if undefined.
     */
    public String getXmlFile() {
        return xmlFile;
    }

    @Override
    protected void handleOption(String argument) {
        if (argument.equals("-raw")) {
            raw = true;
        } else if (argument.equals("-packagesOnly")) {
            packagesOnly = true;
        } else if (argument.equals("-cycles")) {
            cycles = true;
        } else if (argument.equals("-strong")) {
            strong = true;
        } else if (argument.startsWith(TITLE)) {
            title = argument.substring(TITLE.length());
            if (title.length() == 0) {
                valid = false;
            }
        } else if (argument.startsWith(XML_FILE)) {
            xmlFile = argument.substring(XML_FILE.length());
            if (xmlFile.length() == 0) {
                valid = false;
            }
        } else if (argument.startsWith(CSV_FILE)) {
            csvFile = argument.substring(CSV_FILE.length());
            if (csvFile.length() == 0) {
                valid = false;
            }
        } else {
            super.handleOption(argument);
        }
    }

    /** Returns <tt>true</tt> if the option <tt>-cycles</tt> has been set. */
    public boolean isCycles() {
        return cycles;
    }

    /** Returns <tt>true</tt> if the option <tt>-package</tt> has been set. */
    public boolean isPackagesOnly() {
        return packagesOnly;
    }

    /** Returns <tt>true</tt> if the option <tt>-raw</tt> has been set. */
    public boolean isRaw() {
        return raw;
    }

    /** Returns <tt>true</tt> if the option <tt>-strong</tt> has been set. */
    public boolean isStrong() {
        return strong;
    }
}
