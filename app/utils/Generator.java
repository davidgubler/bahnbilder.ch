package utils;

import org.apache.commons.codec.binary.Hex;

import java.security.SecureRandom;

public class Generator {
    public static byte[] generateRandomBytes(int n) {
        try {
            byte[] bytes = new byte[n];
            SecureRandom.getInstance("SHA1PRNG").nextBytes(bytes);
            return bytes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String generatePasswordSaltHex() {
        return Hex.encodeHexString(generateRandomBytes(8));
    }

    public static String generateSessionId() {
        return Hex.encodeHexString(generateRandomBytes(64));
    }

    private static final char[] passwordCharset;
    static {
        passwordCharset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    }

    public static String generatePassword() {
        char[] password = new char[16];
        SecureRandom s;
        try {
            s = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < password.length; i++) {
            password[i] = passwordCharset[s.nextInt(passwordCharset.length)];
        }
        return new String(password);
    }
}
