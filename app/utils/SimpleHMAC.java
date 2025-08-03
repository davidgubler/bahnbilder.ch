package utils;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SimpleHMAC {
    private Mac mac;
    private SecretKeySpec signingKey;

    private void init(byte[] key, String algorithm) {
        try {
            mac = Mac.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        signingKey = new SecretKeySpec(key, algorithm);
    }

    public SimpleHMAC(byte[] key) {
        init(key, "HmacSHA512");
    }

    public byte[] sign(byte[] data) {
        try {
            mac.init(signingKey);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return mac.doFinal(data);
    }

    public String sign(String data) {
        return Hex.encodeHexString(sign(data.getBytes(Charset.forName("UTF-8"))));
    }

}
