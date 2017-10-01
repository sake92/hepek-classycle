package classycle.classfile;

public class ConstantMethodHandle extends Constant {

    private final String _string;

    private final int _referenceKind;// u1
    private final int _referenceIndex;// u2

    /**
     * Creates an instance for the specified string.
     * 
     * @param pool
     *            Constant pool.
     * @param string
     *            wrapped string.
     */
    public ConstantMethodHandle(Constant[] pool, int referenceKind, int referenceIndex) {
        super(pool);
        _string = "ConstantMethodHandle";
        _referenceKind = referenceKind;
        _referenceIndex = referenceIndex;
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
