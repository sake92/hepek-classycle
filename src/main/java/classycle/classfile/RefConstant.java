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
 * Abstract supper class of all reference constants.
 *
 * @author Franz-Josef Elmer
 */
public abstract class RefConstant extends Constant {

    private final int classIndex;
    private final int nameAndTypeIndex;

    /**
     * Creates an instance for the specified class, name, and type.
     *
     * @param pool
     *            Constant pool. Needed for resolving references.
     * @param classIndex
     *            Index of {@link ClassConstant}.
     * @param nameAndTypeIndex
     *            Index of {@link NameAndTypeConstant}.
     */
    public RefConstant(Constant[] pool, int classIndex, int nameAndTypeIndex) {
        super(pool);
        this.classIndex = classIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    /** Returns the class constant. */
    public ClassConstant getClassConstant() {
        return (ClassConstant) getConstant(classIndex);
    }

    /** Returns the name-and-type constant. */
    public NameAndTypeConstant getNameAndType() {
        return (NameAndTypeConstant) getConstant(nameAndTypeIndex);
    }

    /** Pretty printing. Will be used by <tt>toString</tt> of the subclasses. */
    protected String toString(String constantType) {
        return constantType + ": Class = " + getClassConstant().getName() + ", Name = " + getNameAndType().getName()
                + ", Descriptor = " + getNameAndType().getDescriptor();
    }

}
