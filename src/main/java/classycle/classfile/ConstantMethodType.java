package classycle.classfile;

public class ConstantMethodType extends Constant {

    private final String _string;

    private final int _descriptorIndex;

    /**
     * Creates an instance for the specified string.
     * 
     * @param pool
     *            Constant pool.
     * @param string
     *            wrapped string.
     */
    public ConstantMethodType(Constant[] pool, int descriptorIndex) {
        super(pool);
        _string = "ConstantMethodType";
        _descriptorIndex = descriptorIndex;
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
