import java.util.Map;
import java.util.TreeMap;

public class StandardBit implements Bit {
    public enum Op {
        ZERO, ONE, NOT,
        NAND, AND, OR, XOR,
        NAND3, AND3, OR3, XOR3,
    }

    private static int BIT_COUNTER = 0;
    private final static Map<String, Bit> BITS = new TreeMap<>();
    byte result = (byte)2;
    String expr;

    Bit a;
    Bit b;
    Bit c;
    Op op;
    String name;

    public StandardBit(byte value, String name){
        this.name = name;
        op = value == 0 ? Op.ZERO : Op.ONE;
        BITS.put(name, this);
    }

    public StandardBit(Bit a, Bit b, Op op, String name){
        this.a = a;
        this.b = b;
        this.op = op;
        this.name = name;
        BITS.put(name, this);
    }

    public StandardBit(Bit a, Bit b, Bit c, Op op, String name){
        this.a = a;
        this.b = b;
        this.c = c;
        this.op = op;
        this.name = name;
        BITS.put(name, this);
    }

    @Override
    public Bit not() {
        return new StandardBit(this, null, Op.NOT, "T%06d".formatted(BIT_COUNTER++));
    }

    @Override
    public Bit nand(Bit other){
        return new StandardBit(this, other, Op.NAND, "T%06d".formatted(BIT_COUNTER++));
    }

    @Override
    public Bit and(Bit other){
        return new StandardBit(this, other, Op.AND, "T%06d".formatted(BIT_COUNTER++));
    }

    @Override
    public Bit or(Bit other){
        return new StandardBit(this, other, Op.OR, "T%06d".formatted(BIT_COUNTER++));
    }

    @Override
    public Bit xor(Bit other){
        return new StandardBit(this, other, Op.XOR, "T%06d".formatted(BIT_COUNTER++));
    }

    /**
     * NAND3
     * @param op1 operand 1
     * @param op2 operand 1
     * @return !(this & op1 & op2)
     */
    public Bit nand3(Bit op1, Bit op2){
        return new StandardBit(this, op1, op2, Op.NAND3, "T%06d".formatted(BIT_COUNTER++));
    }

    /**
     * AND3
     * @param op1 operand 1
     * @param op2 operand 1
     * @return (this & op1 & op2)
     */
    public Bit and3(Bit op1, Bit op2){
        return new StandardBit(this, op1, op2, Op.AND3, "T%06d".formatted(BIT_COUNTER++));
    }

    /**
     * OR3
     * @param op1 operand 1
     * @param op2 operand 1
     * @return (this & op1 & op2)
     */
    public Bit or3(Bit op1, Bit op2){
        return new StandardBit(this, op1, op2, Op.OR3, "T%06d".formatted(BIT_COUNTER++));
    }

    /**
     * XOR3
     * @param op1 operand 1
     * @param op2 operand 1
     * @return (this ^ op1 ^ op2)
     */
    public Bit xor3(Bit op1, Bit op2){
        return new StandardBit(this, op1, op2, Op.XOR3, "T%06d".formatted(BIT_COUNTER++));
    }

    public Bit[] add(Bit other, Bit carry){
        return new Bit[]{
            xor3(other, carry),
            and(other).or(and(carry)).or(other.and(carry))
        };
    }

    @Override
    public byte eval() {
        if(result != 2) return result;
        return result = switch (op) {
            case ZERO -> (byte)0;
            case ONE -> (byte)1;
            case NOT -> (byte)(1 - a.eval());
            case NAND -> (byte)(1 - (a.eval() & b.eval()));
            case AND -> (byte) (a.eval() & b.eval());
            case OR -> (byte) (a.eval() | b.eval());
            case XOR -> (byte) (a.eval() ^ b.eval());
            case NAND3 -> (byte)(1 - (a.eval() & b.eval() & c.eval()));
            case AND3 -> (byte) (a.eval() & b.eval() & c.eval());
            case OR3 -> (byte) (a.eval() | b.eval() | c.eval());
            case XOR3 -> (byte) (a.eval() ^ b.eval() ^ c.eval());
        };
    }

    @Override
    public String strEval() {
        if(expr != null) return name;
        expr = switch (op) {
            case ZERO -> "0";
            case ONE -> "1";
            case NOT -> "not(" + a.strEval() + ")";
            case NAND -> "nand("+a.strEval()+", " + b.strEval()+")";
            case AND -> "and("+a.strEval()+", " + b.strEval()+")";
            case OR -> "or("+a.strEval()+", " + b.strEval()+")";
            case XOR -> "xor("+a.strEval()+", " + b.strEval()+")";
            case NAND3 -> "nand("+a.strEval()+", " + b.strEval()+", " + c.strEval()+ ")";
            case AND3 -> "and("+a.strEval()+", " + b.strEval()+", " + c.strEval()+ ")";
            case OR3 -> "or("+a.strEval()+", " + b.strEval()+", " + c.strEval()+ ")";
            case XOR3 -> "xor("+a.strEval()+", " + b.strEval()+", " + c.strEval()+ ")";
        };
        return name;
    }

    @Override
    public Map<String, Bit> getAll() {
        return BITS;
    }

    @Override
    public void reset() {
        result = 2;
        expr = null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte getComputedValue() {
        return result;
    }
}