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
package classycle.classfile;

/**
 * Class constant. Refers to an instance of {@link UTF8Constant} which holds the fully qualifies class name.
 *
 * @author Franz-Josef Elmer
 */
public final class ClassConstant extends Constant {

    private final int nameIndex;

    /**
     * Creates an instance for the specified index refering an {@link UTF8Constant}.
     *
     * @param pool
     *            Pool of all {@link Constant Constants}.
     * @param nameIndex
     *            Index into <tt>pool</tt>.
     */
    public ClassConstant(Constant[] pool, int nameIndex) {
        super(pool);
        this.nameIndex = nameIndex;
    }

    /**
     * Returns the fully-qualified class name. In the case of an object array only the class name of the object is
     * returned.
     *
     * @return fully-qualified class name in standard notation with '.'.
     */
    public String getName() {
        String result = null;
        final Constant c = getConstant(nameIndex);
        if (c instanceof UTF8Constant) {
            result = ((UTF8Constant) c).getString().replace('/', '.');
            if (result.startsWith("[")) {
                // An array class: Extract class name
                final int index = result.indexOf('L');
                if (index > 0) {
                    result = result.substring(index + 1, result.length() - 1);
                }
            }
        }
        return result;
    }

    /** Returns the constant type and the class name. */
    @Override
    public String toString() {
        return "CONSTANT_Class: " + getName();
    }
}
