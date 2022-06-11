import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sha256 {
    private static final int BLOCK_BITS = 512;
    private static final int BLOCK_BYTES = BLOCK_BITS / 8;
    BitField[] H = new BitField[8];
    BitField[] H0 = new BitField[8];
    BitField[] K = new BitField[64];

    private static final int[] K_ = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    private static final int[] H0_ = {
            0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
    };

    public Sha256() {
        for(int i=0; i<8; i++){
            H0[i] = new BitField(H0_[i], i*32, 32, "H0");
        }
        for(int i=0; i<64; i++){
            K[i] = new BitField(K_[i], i*32, 32, "K");
        }
    }

    public void initialize(){
        System.arraycopy(H0, 0, H, 0, H0.length);
    }

    public String toHex(BitField[] fields){
        StringBuilder hex = new StringBuilder();
        for (BitField bitField : fields) {
            hex.append("%08x".formatted(bitField.toInteger()));
        }
        return hex.toString();
    }

    public void update(BitField[] words) {
        BitField[] W = new BitField[words.length + 48];
        BitField[] T = new BitField[8];

        System.arraycopy(words, 0, W, 0, words.length);
        System.arraycopy(H, 0, T, 0, H.length);

        for (int t = 16; t < W.length; ++t) {
            W[t] = smallSig1(W[t - 2]).add(W[t - 7]).add(smallSig0(W[t - 15])).add(W[t - 16]);
        }

        for (int t = 0; t < W.length; ++t) {
            BitField t1 = T[7].add(bigSig1(T[4])).add(ch(T[4], T[5], T[6])).add(K[t]).add(W[t]);
            BitField t2 = bigSig0(T[0]).add(maj(T[0], T[1], T[2]));
            System.arraycopy(T, 0, T, 1, T.length - 1);
            T[4] = T[4].add(t1);
            T[0] = t1.add(t2);
        }

        for (int t = 0; t < H.length; ++t) {
            H[t] = H[t].add(T[t]);
        }
    }

    public BitField[] padd(byte[] message) {
        int finalBlockLength = message.length % BLOCK_BYTES;
        int blockCount = message.length / BLOCK_BYTES + (finalBlockLength + 1 + 8 > BLOCK_BYTES ? 2 : 1);
        final IntBuffer result = IntBuffer.allocate(blockCount * (BLOCK_BYTES / Integer.BYTES));
        ByteBuffer buf = ByteBuffer.wrap(message);
        for (int i = 0, n = message.length / Integer.BYTES; i < n; ++i) {
            result.put(buf.getInt());
        }
        ByteBuffer remainder = ByteBuffer.allocate(4);
        remainder.put(buf).put((byte) 0b10000000).rewind();
        result.put(remainder.getInt());
        result.position(result.capacity() - 2);
        long msgLength = message.length * 8L;
        result.put((int) (msgLength >>> 32));
        result.put((int) msgLength);
        int[] arr = result.array();
        BitField[] bitFields = new BitField[arr.length];
        for(int i=0; i<arr.length; i++)
            bitFields[i] = new BitField(arr[i], i*32, 32, "I");
        return bitFields;
    }

    public BitField[] getH() {
        return H;
    }

    private BitField ch(BitField x, BitField y, BitField z) {
        return (x.and(y)).or(x.not().and(z));
    }

    private BitField maj(BitField x, BitField y, BitField z) {
        return x.and(y).or(x.and(z)).or(y.and(z));
    }

    private BitField bigSig0(BitField x) {
        return x.ror(2).xor(x.ror(13)).xor(x.ror(22));
    }

    private BitField bigSig1(BitField x) {
        return x.ror(6).xor(x.ror(11)).xor(x.ror(25));
    }

    private BitField smallSig0(BitField x) {
        return x.ror(7).xor(x.ror(18)).xor(x.shr(3));
    }

    private BitField smallSig1(BitField x) {
        return x.ror(17).xor(x.ror(19)).xor(x.shr(10));
    }

    public static Set<String> dependencies(Map<String, Set<String>> data, String key, String expr){
        if(data.containsKey(key)) return data.get(key);
        Set<String> deps = new HashSet<>();
        Pattern p = Pattern.compile("\\$(.*?)!");
        Matcher m = p.matcher(expr);
        while(m.find()){
            String match = m.group(1);
            if(match.startsWith("T")){
                deps.addAll(dependencies(data, match, StandardBit.TEMPS.get(match)));
            } else {
                deps.add(match);
            }
        }
        data.put(key, deps);
        return deps;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Sha256 hash = new Sha256();
        String input = "Ahoj";
        BitField[] w = hash.padd(input.getBytes(StandardCharsets.UTF_8));
        hash.initialize();
        hash.update(w);

        BitField[] result = hash.getH();
        StringBuilder hex = new StringBuilder();

        hex.append("Input: ").append(input).append("\nHash: ");
        for (BitField bitField : result) {
            hex.append("%08x".formatted(bitField.toInteger()));
        }
        System.out.println(hex);

        try(PrintWriter writer = new PrintWriter(new FileOutputStream("expression.txt"))){
            Map<String, Set<String>> deps = new TreeMap<>();

            for(int i=0; i<result.length; i++){
                String expr = result[i].strEval("y", i * 32).toString();
                writer.append(expr).append('\n');
                /*String[] nl = expr.split("\n");
                for(String line : nl){
                    String[] parts = line.split("=");
                    dependencies(deps, parts[0].strip(), parts[1].strip());
                }*/
            }

            for(var kv : deps.entrySet()){
                if(!kv.getKey().startsWith("y")) continue;
                writer.append(kv.getKey()).append("_deps = ");
                for(var v : kv.getValue())
                    writer.append(v).append(", ");
                writer.append("\n");
            }

            for(Map.Entry<String, String> kv : StandardBit.TEMPS.entrySet()){
                writer.append(kv.getKey()).append(" = ").append(kv.getValue()).append('\n');
            }

        }
    }
}
