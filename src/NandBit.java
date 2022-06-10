public class NandBit implements Bit {
    private enum Op {
        NAND, ZERO, ONE
    }

    private static int NAME_COUNTER = 0;
    Bit a;
    Bit b;
    String name;
    Op op;

    public NandBit() {
        this((byte)0);
    }

    public NandBit(byte value){
        this(value, "b" + (NAME_COUNTER++));
    }

    public NandBit(byte value, String name){
        this.name = name;
        op = value == 0 ? Op.ZERO : Op.ONE;
    }

    public NandBit(Bit a, Bit b, Op op){
        this.a = a;
        this.b = b;
        this.op = op;
    }

    /**
     * NAND
     * @param other operand
     * @return !(this & other)
     */
    public Bit nand(Bit other){
        return new NandBit(this, other, Op.NAND);
    }

    /**
     * NOT
     * @return !(this)
     */
    public Bit not() {
        return this.nand(this);
    }

    /**
     * NAND based AND
     * @param other operand
     * @return (this & other)
     */
    public Bit and(Bit other){
        Bit tmp = this.nand(other);
        return tmp.nand(tmp);
    }

    /**
     * NAND based OR
     * @param other operand
     * @return (this | other)
     */
    public Bit or(Bit other){
        Bit an = this.nand(this);
        Bit bn = other.nand(other);
        return an.nand(bn);
    }

    /**
     * NAND based XOR
     * @param other operand
     * @return (this ^ other)
     */
    public Bit xor(Bit other){
        Bit aNb = this.nand(other);
        Bit aNaNb = this.nand(aNb);
        Bit bNaNb = other.nand(aNb);
        return aNaNb.nand(bNaNb);
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
            case ZERO -> 0;
            case ONE -> 1;
            case NAND -> (byte)(1 - (a.eval() & b.eval()));
        };
    }

    /**
     * Evaluate to string form
     * @return string form
     */
    public String strEval() {
        return switch (op) {
            case ZERO, ONE -> name;
            case NAND -> "nand("+a.strEval()+", " + b.strEval()+")";
        };
    }
}