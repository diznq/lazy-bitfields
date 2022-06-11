import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StandardBit implements Bit {
    private enum Op {
        ZERO, ONE, NOT,
        NAND, AND, OR, XOR,
        NAND3, AND3, OR3, XOR3,
    }

    public static int OPS = 0;
    private static int NAME_COUNTER = 0;
    private static int TEMP_COUNTER = 0;
    public static Map<String, String> TEMPS = new TreeMap<String, String>();
    Bit a;
    Bit b;
    Bit c;
    Op op;
    String name;
    byte result = (byte)2;

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

    public StandardBit(Bit a, Bit b, Bit c, Op op){
        this.a = a;
        this.b = b;
        this.c = c;
        this.op = op;
    }

    public Bit not() {
        return new StandardBit(this, null, Op.NOT);
    }

    public Bit nand(Bit other){
        return new StandardBit(this, other, Op.NAND);
    }

    public Bit and(Bit other){
        return new StandardBit(this, other, Op.AND);
    }

    public Bit or(Bit other){
        return new StandardBit(this, other, Op.OR);
    }

    public Bit xor(Bit other){
        return new StandardBit(this, other, Op.XOR);
    }

    /**
     * NAND3
     * @param op1 operand 1
     * @param op2 operand 1
     * @return !(this & op1 & op2)
     */
    public Bit nand3(Bit op1, Bit op2){
        return new StandardBit(this, op1, op2, Op.NAND3);
    }

    /**
     * AND3
     * @param op1 operand 1
     * @param op2 operand 1
     * @return (this & op1 & op2)
     */
    public Bit and3(Bit op1, Bit op2){
        return new StandardBit(this, op1, op2, Op.AND3);
    }

    /**
     * OR3
     * @param op1 operand 1
     * @param op2 operand 1
     * @return (this & op1 & op2)
     */
    public Bit or3(Bit op1, Bit op2){
        return new StandardBit(this, op1, op2, Op.OR3);
    }

    /**
     * XOR3
     * @param op1 operand 1
     * @param op2 operand 1
     * @return (this ^ op1 ^ op2)
     */
    public Bit xor3(Bit op1, Bit op2){
        return new StandardBit(this, op1, op2, Op.XOR3);
    }

    public Bit[] add(Bit other, Bit carry){
        return new Bit[]{
            xor3(other, carry),
            and(other).or(and(carry)).or(other.and(carry))
        };
    }

    public synchronized byte eval() {
        OPS++;
        if(result != 2) return result;
        result = switch (op) {
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
        return result;
    }

    public String strEval() {
        if(name != null) return ":" + name + "/";
        String result = switch (op) {
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
        name = "T" + TEMP_COUNTER++;
        TEMPS.put(name, result);
        return ":" + name + "/";
    }
}