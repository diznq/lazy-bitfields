import java.util.Map;
import java.util.TreeMap;

public class NandBit implements Bit {
    private enum Op {
        ZERO, ONE,
        NAND,
    }

    private static int TEMP_COUNTER = 0;
    public static Map<String, String> TEMPS = new TreeMap<String, String>();
    byte result = 2;

    Bit a;
    Bit b;
    Op op;
    String name;

    public NandBit(byte value, String name){
        this.name = name;
        op = value == 0 ? Op.ZERO : Op.ONE;
    }

    public NandBit(Bit a, Bit b, Op op){
        this.a = a;
        this.b = b;
        this.op = op;
    }

    public Bit nand(Bit other){
        return new NandBit(this, other, Op.NAND);
    }

    public Bit not() {
        return this.nand(this);
    }

    public Bit and(Bit other){
        Bit tmp = this.nand(other);
        return tmp.nand(tmp);
    }

    public Bit or(Bit other){
        Bit an = this.nand(this);
        Bit bn = other.nand(other);
        return an.nand(bn);
    }

    public Bit xor(Bit other){
        Bit aNb = this.nand(other);
        Bit aNaNb = this.nand(aNb);
        Bit bNaNb = other.nand(aNb);
        return aNaNb.nand(bNaNb);
    }

    public Bit[] add(Bit other, Bit carry){
        return new Bit[]{
            xor(other).xor(carry),
            and(other).or(and(carry)).or(other.and(carry))
        };
    }

    public byte eval() {
        if(result != 2) return result;
        return result = switch (op) {
            case ZERO -> 0;
            case ONE -> 1;
            case NAND -> (byte)(1 - (a.eval() & b.eval()));
        };
    }

    public String strEval() {
        if(name != null) return ":" + name + "/";
        String result = switch (op) {
            case ZERO -> "0";
            case ONE -> "1";
            case NAND -> "nand("+a.strEval()+", " + b.strEval()+")";
        };
        name = "T%06d".formatted(TEMP_COUNTER++);
        TEMPS.put(name, result);
        return ":" + name + "/";
    }
}