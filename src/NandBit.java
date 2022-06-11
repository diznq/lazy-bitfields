import java.util.Map;
import java.util.TreeMap;

public class NandBit implements Bit {
    public enum Op {
        ZERO, ONE,
        NAND,
    }

    private static int BIT_COUNTER = 0;
    private static final Map<String, Bit> BITS = new TreeMap<>();
    byte result = 2;
    String expr;

    Bit a;
    Bit b;
    Op op;
    String name;

    public NandBit(byte value, String name){
        this.name = name;
        this.op = value == 0 ? Op.ZERO : Op.ONE;
        BITS.put(name, this);
    }

    public NandBit(Bit a, Bit b, Op op, String name){
        this.a = a;
        this.b = b;
        this.op = op;
        this.name = name;
        BITS.put(name, this);
    }

    @Override
    public Bit nand(Bit other){
        return new NandBit(this, other, Op.NAND, "T%06d".formatted(BIT_COUNTER++));
    }

    @Override
    public Bit not() {
        return this.nand(this);
    }

    @Override
    public Bit and(Bit other){
        Bit tmp = this.nand(other);
        return tmp.nand(tmp);
    }

    @Override
    public Bit or(Bit other){
        Bit an = this.nand(this);
        Bit bn = other.nand(other);
        return an.nand(bn);
    }

    @Override
    public Bit xor(Bit other){
        Bit aNb = this.nand(other);
        Bit aNaNb = this.nand(aNb);
        Bit bNaNb = other.nand(aNb);
        return aNaNb.nand(bNaNb);
    }

    @Override
    public Bit[] add(Bit other, Bit carry){
        return new Bit[]{
            xor(other).xor(carry),
            and(other).or(and(carry)).or(other.and(carry))
        };
    }

    @Override
    public byte eval() {
        if(result != 2) return result;
        return result = switch (op) {
            case ZERO -> 0;
            case ONE -> 1;
            case NAND -> (byte)(1 - (a.eval() & b.eval()));
        };
    }

    @Override
    public String strEval() {
        if(expr != null) return name;
        expr = switch (op) {
            case ZERO -> "0";
            case ONE -> "1";
            case NAND -> "nand("+a.strEval()+", " + b.strEval()+")";
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