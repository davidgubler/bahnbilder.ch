package utils;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;

public class SimplePBKDF2 {
    private static final int ITERATIONS = 10000;

    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    private static final int ALGORITHM_OUPUT_SIZE = 512;

    public static String hash(String salt, String password) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), Hex.decodeHex(salt), ITERATIONS, ALGORITHM_OUPUT_SIZE);
            SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
            String hash = Hex.encodeHexString(f.generateSecret(spec).getEncoded());
            return hash;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
