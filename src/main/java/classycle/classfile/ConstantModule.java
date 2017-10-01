package classycle.classfile;

public class ConstantModule extends Constant {

    private final String _string;

    private final int _nameIndex;

    /**
     * Creates an instance for the specified string.
     * 
     * @param pool
     *            Constant pool.
     * @param string
     *            wrapped string.
     */
    public ConstantModule(Constant[] pool, int nameIndex) {
        super(pool);
        _string = "ConstantMethodType";
        _nameIndex = nameIndex;
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
