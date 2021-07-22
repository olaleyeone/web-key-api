package com.github.olaleyeone.webkey.model;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

public class WebKey {

    static Base64.Encoder encoder = Base64.getEncoder();

    private String modulus;
    private String exponent;

    public String getModulus() {
        return modulus;
    }

    public void setModulus(String modulus) {
        this.modulus = modulus;
    }

    public String getExponent() {
        return exponent;
    }

    public void setExponent(String exponent) {
        this.exponent = exponent;
    }

    public static WebKey fromRsaPublicKey(RSAPublicKey rsaPublicKey) {
        WebKey webKey = new WebKey();
        webKey.setExponent(encoder.encodeToString(rsaPublicKey.getPublicExponent().toByteArray()));
        webKey.setModulus(encoder.encodeToString(rsaPublicKey.getModulus().toByteArray()));
        return webKey;
    }
}
