import java.io.IOException;

public class BitField {
    public static final int BIT_SIZE = 24;
    private static final Bit Z_CARRY = new StandardBit((byte)0, "ZC");

    Bit[] bits;

    /**
     * Initialize from bit array
     * @param bits bit array
     */
    public BitField(Bit[] bits){
        this.bits = bits;
    }

    /**
     * Initialize from Java integer
     * @param number number, i.e. 42
     * @param name basis name
     */
    public BitField(int number, String name){
        bits = new Bit[BIT_SIZE];
        for(int i=0; i<BIT_SIZE; i++){
            int b = (number >>> i) & 1;
            bits[BIT_SIZE - 1 - i] = new StandardBit((byte)b, name + i);
        }
    }

    /**
     * And with another bitfield
     * @param other operand
     * @return (this & other)
     */
    public BitField and(BitField other){
        Bit[] bits = new Bit[BIT_SIZE];
        for(int i=0; i<BIT_SIZE; i++){
            bits[i] = this.bits[i].and(other.bits[i]);
        }
        return new BitField(bits);
    }

    /**
     * Or with another bitfield
     * @param other operand
     * @return (this | other)
     */
    public BitField or(BitField other){
        Bit[] bits = new Bit[BIT_SIZE];
        for(int i=0; i<BIT_SIZE; i++){
            bits[i] = this.bits[i].or(other.bits[i]);
        }
        return new BitField(bits);
    }

    /**
     * Xor with another bitfield
     * @param other operand
     * @return (this ^ other)
     */
    public BitField xor(BitField other){
        Bit[] bits = new Bit[BIT_SIZE];
        for(int i=0; i<BIT_SIZE; i++){
            bits[i] = this.bits[i].xor(other.bits[i]);
        }
        return new BitField(bits);
    }

    /**
     * Negate bitfield
     * @return ~(this)
     */
    public BitField not(){
        Bit[] bits = new Bit[BIT_SIZE];
        for(int i=0; i<BIT_SIZE; i++){
            bits[i] = this.bits[i].not();
        }
        return new BitField(bits);
    }

    /**
     * Add another bitfield
     * @param other operand
     * @return (this + other)
     */
    public BitField add(BitField other){
        Bit[] bits = new Bit[BIT_SIZE];
        Bit prevCarry = Z_CARRY;
        for(int i=BIT_SIZE - 1; i >= 0; i--){
            Bit[] xy = this.bits[i].add(other.bits[i], prevCarry);
            prevCarry = xy[1];
            bits[i] = xy[0];
        }
        return new BitField(bits);
    }

    /**
     * Shift bit field left
     * @param amount amount to shift
     * @return (this << amount)
     */
    public BitField shl(int amount){
        Bit[] bits = new Bit[32];
        if (BIT_SIZE - amount >= 0) System.arraycopy(this.bits, amount, bits, 0, BIT_SIZE - amount);
        for(int i=BIT_SIZE - amount; i<BIT_SIZE; i++)
            bits[i] = Z_CARRY;
        return new BitField(bits);
    }

    /**
     * Shift right
     * @param amount amount to shift
     * @return (this >> amount)
     */
    public BitField shr(int amount){
        Bit[] bits = new Bit[32];
        if (BIT_SIZE - amount >= 0) System.arraycopy(this.bits, 0, bits, amount, BIT_SIZE - amount);
        for(int i=0; i<amount; i++)
            bits[i] = Z_CARRY;
        return new BitField(bits);
    }

    /**
     * Evaluate bitfield into string form
     * @return result
     */
    public String strEval(){
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<BIT_SIZE; i++){
            builder.append("y").append(i).append(" = ").append(bits[i].strEval()).append("\n");
        }
        return builder.toString();
    }

    /**
     * Evaluate bitfield into Java integer
     * @return result
     */
    public int toInteger() {
        int results = 0;
        for(int i=BIT_SIZE - 1; i>=0; i--){
            Bit bit = bits[i];
            results |= bit.eval() << (BIT_SIZE - 1 - i);
        }
        return results;
    }

    public static void main(String[] args) throws IOException {
        BitField a = new BitField(9, "a");
        BitField b = new BitField(7, "b");
        BitField result = a.add(b);
        System.out.println(result.toInteger());
        System.out.println(StandardBit.OPS);
    }
}
