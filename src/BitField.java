import java.util.Map;

public class BitField {
    private static final Bit Z_CARRY = new NandBit((byte)0, "ZC");
    private final int BIT_SIZE;

    Bit[] bits;

    /**
     * Initialize from bit array
     * @param bits bit array
     */
    public BitField(Bit[] bits){
        this.bits = bits;
        this.BIT_SIZE = bits.length;
    }

    /**
     * Initialize from Java integer
     * @param number number, i.e. 42
     * @param name basis name
     */
    public BitField(int number, int offset, int bitSize, String name){
        BIT_SIZE = bitSize;
        bits = new Bit[BIT_SIZE];
        for(int i=0; i<BIT_SIZE; i++){
            int b = (number >>> i) & 1;
            bits[BIT_SIZE - 1 - i] = new NandBit((byte)b, "%s_%04d".formatted(name, i + offset));
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
        Bit[] bits = new Bit[BIT_SIZE];
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
        Bit[] bits = new Bit[BIT_SIZE];
        if (BIT_SIZE - amount >= 0) System.arraycopy(this.bits, 0, bits, amount, BIT_SIZE - amount);
        for(int i=0; i<amount; i++)
            bits[i] = Z_CARRY;
        return new BitField(bits);
    }

    /**
     * Rotate right
     * @param amount amount to rotate
     * @return (this >> amount) | (this << (BIT_SIZE - amount))
     */
    public BitField ror(int amount){
        Bit[] bits = new Bit[BIT_SIZE];
        System.arraycopy(this.bits, 0, bits, amount, BIT_SIZE - amount);
        System.arraycopy(this.bits, BIT_SIZE - amount, bits, 0, amount);
        return new BitField((bits));
    }

    /**
     * Create bitfield copy
     * @return copy of this
     */
    public BitField copy(){
        Bit[] bits = new Bit[BIT_SIZE];
        System.arraycopy(this.bits, 0, bits, 0, BIT_SIZE);
        return new BitField(bits);
    }

    /**
     * Evaluate bitfield into string form
     * @return result
     */
    public StringBuilder strEval(String prefix, int offset){
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<BIT_SIZE; i++){
            builder.append(prefix).append(offset + i).append(" = ").append(bits[i].strEval()).append("\n");
        }
        return builder;
    }

    /**
     * Evaluate bitfield into Java integer
     * @return result
     */
    public int toInteger() {
        int results = 0;
        for(int i=BIT_SIZE - 1; i>=0; i--){
            results |= (bits[i].eval()) << (BIT_SIZE - 1 - i);
        }
        return results;
    }

    /**
     * Get all bit instances
     * @return
     */
    Map<String, Bit> getAllBits() {
        return bits[0].getAll();
    }
}
