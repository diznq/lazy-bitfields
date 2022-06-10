public class StandardBit implements Bit {
    private enum Op {
        NAND, AND, OR, XOR, NOT, ZERO, ONE
    }

    private static int NAME_COUNTER = 0;
    Bit a;
    Bit b;
    Op op;
    String name;

    public StandardBit() {
        this((byte)0);
    }

    public StandardBit(byte value){
        this(value, "b" + (NAME_COUNTER++));
    }

    public StandardBit(byte value, String name){
        this.name = name;
        op = value == 0 ? Op.ZERO : Op.ONE;
    }

    public StandardBit(Bit a, Bit b, Op op){
        this.a = a;
        this.b = b;
        this.op = op;
    }

    /**
     * NOT
     * @return !(this)
     */
    public Bit not() {
        return new StandardBit(this, null, Op.NOT);
    }

    /**
     * NAND
     * @param other operand
     * @return (this & other)
     */
    public Bit nand(Bit other){
        return new StandardBit(this, other, Op.NAND);
    }

    /**
     * AND
     * @param other operand
     * @return (this & other)
     */
    public Bit and(Bit other){
        return new StandardBit(this, other, Op.AND);
    }

    /**
     * OR
     * @param other operand
     * @return (this | other)
     */
    public Bit or(Bit other){
        return new StandardBit(this, other, Op.OR);
    }

    /**
     * XOR
     * @param other operand
     * @return (this ^ other)
     */
    public Bit xor(Bit other){
        return new StandardBit(this, other, Op.XOR);
    }

    /**
     * Add two numbers together
     * @param other operand
     * @param carry carry bit
     * @return [(this + operand + carry bit), new carry]
     */
    public Bit[] add(Bit other, Bit carry){
        return new Bit[]{
            xor(other).xor(carry),
            and(other).or(and(carry)).or(other.and(carry))
        };
    }

    /**
     * Perform evaluation
     * @return result, either 1 or 0
     */
    public byte eval() {
        return switch (op) {
            case ZERO -> (byte)0;
            case ONE -> (byte)1;
            case NOT -> (byte)(1 - a.eval());
            case NAND -> (byte)(1 - (a.eval() & b.eval()));
            case AND -> (byte) (a.eval() & b.eval());
            case OR -> (byte) (a.eval() | b.eval());
            case XOR -> (byte) (a.eval() ^ b.eval());
        };
    }

    /**
     * Evaluate to string form
     * @return string form
     */
    public String strEval() {
        return switch (op) {
            case ZERO, ONE -> name;
            case NOT -> "not(" + a.strEval() + ")";
            case NAND -> "nand("+a.strEval()+", " + b.strEval()+")";
            case AND -> "and("+a.strEval()+", " + b.strEval()+")";
            case OR -> "or("+a.strEval()+", " + b.strEval()+")";
            case XOR -> "xor("+a.strEval()+", " + b.strEval()+")";
        };
    }
}