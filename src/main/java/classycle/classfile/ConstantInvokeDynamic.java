package classycle.classfile;

class ConstantInvokeDynamic extends Constant {

    private final String _string;

    private final int _bootstrapMethodAttrIndex;
    private final int _nameAndTypeIndex;

    /**
     * Creates an instance for the specified string.
     * 
     * @param pool
     *            Constant pool.
     * @param string
     *            wrapped string.
     */
    public ConstantInvokeDynamic(Constant[] pool, int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
        super(pool);
        _string = "ConstantInvokeDynamic";
        _bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
        _nameAndTypeIndex = nameAndTypeIndex;
    }

    /** Returns the wrapped string. */
    public String getString() {
        return _string;
    }

    /** Returns the constant type and the wrapped string. */
    public String toString() {
        return "ConstantMethodHandle: " + _string;
    }

}
