import java.util.Map;

public interface Bit {

    /**
     * NOT
     * @return !(this)
     */
    Bit not();

    /**
     * NAND
     * @param other operand
     * @return (this & other)
     */
    Bit nand(Bit other);

    /**
     * AND
     * @param other operand
     * @return (this & other)
     */
    Bit and(Bit other);

    /**
     * OR
     * @param other operand
     * @return (this | other)
     */
    Bit or(Bit other);

    /**
     * XOR
     * @param other operand
     * @return (this ^ other)
     */
    Bit xor(Bit other);

    /**
     * Add two numbers together
     * @param other operand
     * @param carry carry bit
     * @return [(this + operand + carry bit), new carry]
     */
    Bit[] add(Bit other, Bit carry);

    /**
     * Perform evaluation
     * @return result, either 1 or 0
     */
    byte eval();

    /**
     * Evaluate to string form
     * @return string form
     */
    String strEval();

    /**
     * Get all bits created
     * @return all instances
     */
    Map<String, Bit> getAll();

    /**
     * Reset cached state
     */
    void reset();

    /**
     * Get name
     */
    String getName();

    /**
     * Get computed value
     * @return computed value
     */
    byte getComputedValue();
}