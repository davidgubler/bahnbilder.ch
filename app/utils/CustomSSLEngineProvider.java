package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import javax.inject.Inject;
import javax.net.ssl.*;

import play.server.ApplicationProvider;
import play.server.SSLEngineProvider;

import java.io.ByteArrayInputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class CustomSSLEngineProvider implements SSLEngineProvider {
    private final ApplicationProvider applicationProvider;
    private final SecureRandom secureRandom = new SecureRandom();

    @Inject
    public CustomSSLEngineProvider(ApplicationProvider applicationProvider) {
        this.applicationProvider = applicationProvider;
    }

    @Override
    public SSLEngine createSSLEngine() {
        return sslContext().createSSLEngine();
    }

    @Override
    public SSLContext sslContext() {
        try {
            TrustManager[] trustManagers = null;
            KeyManager[] keyManagers = null;

            String chainStr = Config.Option.TLS_CERT_CHAIN.get() == null ? file(Config.Option.TLS_CERT_CHAIN_FILE.get()) : Config.Option.TLS_CERT_CHAIN.get();
            Certificate[] chain = getCertificates(chainStr);
            String privKeyStr = Config.Option.TLS_PRIVATE_KEY.get() == null ? file(Config.Option.TLS_PRIVATE_KEY_FILE.get()) : Config.Option.TLS_PRIVATE_KEY.get();
            PrivateKey privateKey = getPkcs8PrivateKey(privKeyStr);

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(null, null);
            TrustManagerFactory customCaTrustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            customCaTrustManager.init(trustStore);
            trustManagers = customCaTrustManager.getTrustManagers();

            KeyManagerFactory customCaKeyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, null);
            keyStore.setKeyEntry("private-key", privateKey, new char[0], chain);
            customCaKeyManager.init(keyStore, new char[0]);
            keyManagers = customCaKeyManager.getKeyManagers();

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, secureRandom);

            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String file(String file) {
        try {
            return new String(new FileInputStream(file).readAllBytes(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            return null;
        }
    }

    private Certificate[] getCertificates(String chain) {
        try {
            return CertificateFactory.getInstance("X.509").generateCertificates(new ByteArrayInputStream(chain.getBytes(Charset.forName("UTF-8")))).toArray(new Certificate[0]);
        } catch (CertificateException e) {
            throw new IllegalArgumentException("Certificate chain is not in valid X509 format, check " + Config.Option.TLS_CERT_CHAIN.name() + " or " + Config.Option.TLS_CERT_CHAIN_FILE.name() + ": " + e.getMessage());
        }
    }

    private PrivateKey getPkcs8PrivateKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        try {
            final String start = "-----BEGIN PRIVATE KEY-----";
            final String end = "-----END PRIVATE KEY-----";
            key = key.trim();
            if (!key.startsWith(start) || !key.endsWith(end)) {
                throw new IllegalArgumentException("Private key is not in PKCS8 format, must start with " + start + " and end with " + end + ", check " + Config.Option.TLS_PRIVATE_KEY.name() + " or " + Config.Option.TLS_PRIVATE_KEY_FILE.name());
            }
            key = key.substring(start.length(), key.length() - end.length());
            key = key.replace("\n", "");
            key = key.replace("\r", "");
            key = key.replace(" ", "");
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));
            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                return kf.generatePrivate(privateKeySpec);
            } catch (InvalidKeySpecException e) {
                // try DSA...
            }
            try {
                KeyFactory kf = KeyFactory.getInstance("DSA");
                return kf.generatePrivate(privateKeySpec);
            } catch (InvalidKeySpecException e) {
                // try EC...
            }
            KeyFactory kf = KeyFactory.getInstance("EC");
            return kf.generatePrivate(privateKeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Private key is not in PKCS8 format, check " + Config.Option.TLS_PRIVATE_KEY.name() + " or " + Config.Option.TLS_PRIVATE_KEY_FILE.name() + ": " + e.getMessage());
        }
    }
}
