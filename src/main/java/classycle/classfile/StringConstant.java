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
 * Constant of a <tt>String</tt> value.
 *
 * @author Franz-Josef Elmer
 */
public final class StringConstant extends Constant {

    private final int stringIndex;

    /**
     * Creates an instance for the specfied index.
     *
     * @param pool
     *            Constant pool. Needed for resolving the reference.
     * @param stringIndex
     *            Index of an {@link UTF8Constant}.
     */
    public StringConstant(Constant[] pool, int stringIndex) {
        super(pool);
        this.stringIndex = stringIndex;
    }

    /** Returns the string value. */
    public String getString() {
        String result = null;
        final Constant c = getConstant(stringIndex);
        if (c instanceof UTF8Constant) {
            result = ((UTF8Constant) c).getString();
        }
        return result;
    }

    /** Returns the constant type and the string value. */
    @Override
    public String toString() {
        return "CONSTANT_String: " + getString();
    }

}
