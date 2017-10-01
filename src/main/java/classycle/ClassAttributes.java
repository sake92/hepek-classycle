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
 * Immutable class holding the attributes of a class vertex. They are
 * <ul>
 * <li>fully-qualified class name
 * <li>type (interface, abstract, concrete, unknown)
 * <li>flag <tt>innerClass</tt>
 * <li>size of the class file
 * </ul>
 *
 * @author Franz-Josef Elmer
 */
public class ClassAttributes extends NameAndSourceAttributes {

    /** Type constant. */
    public static final String INTERFACE = "interface", ABSTRACT_CLASS = "abstract class", CLASS = "class",
            UNKNOWN = "unknown external class";

    private final String type;
    private final boolean innerClass;
    private final int size;

    /**
     * Creates an instance based on the specified name, type, and size. The innerclass flag will be set if the name
     * contains a '$' character.
     *
     * @param name
     *            Fully-qualified class name.
     * @param source
     *            Optional source of the class file. Can be <code>null</code>.
     * @param type
     *            Type.
     * @param size
     *            Size.
     */
    public ClassAttributes(String name, String source, String type, int size) {
        super(name);
        if (source != null) {
            addSource(source);
        }
        this.type = type;
        innerClass = name != null && name.indexOf('$') > 0;
        this.size = size;
    }

    /** Returns the size of the class file in bytes. */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Returns the class type.
     *
     * @return either {@link #INTERFACE}, {@link #ABSTRACT_CLASS}, {@link #CLASS}, or {@link #UNKNOWN}.
     */
    public String getType() {
        return type;
    }

    /** Returns <tt>true</tt> in the case of an inner class. */
    public boolean isInnerClass() {
        return innerClass;
    }

    /** Returns the attributes as a string for pretty printing. */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(innerClass ? "inner " : "");
        builder.append(type).append(' ').append(getName());
        if (size > 0) {
            builder.append(" (").append(size).append(" bytes)");
        }
        final String sources = getSources();
        if (sources.length() > 0) {
            builder.append(" sources: ").append(sources);
        }
        return builder.toString();
    }

    /**
     * Creates an instance of the type {@link #ABSTRACT_CLASS}.
     *
     * @param name
     *            Fully-qualified class name.
     * @param source
     *            Optional source of the class file. Can be <code>null</code>.
     * @param size
     *            Size of the class file.
     * @return a new instance.
     */
    public static ClassAttributes createAbstractClass(String name, String source, int size) {
        return new ClassAttributes(name, source, ABSTRACT_CLASS, size);
    }

    /**
     * Creates an instance of the type {@link #CLASS}.
     *
     * @param name
     *            Fully-qualified class name.
     * @param source
     *            Optional source of the class file. Can be <code>null</code>.
     * @param size
     *            Size of the class file.
     * @return a new instance.
     */
    public static ClassAttributes createClass(String name, String source, int size) {
        return new ClassAttributes(name, source, CLASS, size);
    }

    /**
     * Creates an instance of the type {@link #INTERFACE}.
     *
     * @param name
     *            Fully-qualified class name.
     * @param source
     *            Optional source of the class file. Can be <code>null</code>.
     * @param size
     *            Size of the class file.
     * @return a new instance.
     */
    public static ClassAttributes createInterface(String name, String source, int size) {
        return new ClassAttributes(name, source, INTERFACE, size);
    }

    /**
     * Creates an instance of the type {@link #UNKNOWN}.
     *
     * @param name
     *            Fully-qualified class name.
     * @param size
     *            Size of the class file.
     * @return a new instance.
     */
    public static ClassAttributes createUnknownClass(String name, int size) {
        return new ClassAttributes(name, null, UNKNOWN, size);
    }

}
