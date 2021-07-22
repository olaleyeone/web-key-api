package com.github.olaleyeone.webkey.util;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

public class KeyParser {
    public RSAPublicKey getRsaPublicKey(byte[] data, String algorithm) {
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            X509EncodedKeySpec pubKs = new X509EncodedKeySpec(data);
            return (RSAPublicKey) kf.generatePublic(pubKs);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }
}
