package com.salat.acloud.services;

public class EncryptionService {

//    public String publicKey;
//    public String privateKey;

    private static final int shift = 42;

    public static byte[] encode(byte[] src) {
        // simple Caesar cipher
        byte[] encoded = new byte[src.length];
        for (int i = 0; i < src.length; i++) {
            encoded[i] = (byte) (src[i] + shift);
        }
        return encoded;
    }

    public static byte[] decode(byte[] src) {
        byte[] encoded = new byte[src.length];
        for (int i = 0; i < src.length; i++) {
            encoded[i] = (byte) (src[i] - shift);
        }
        return encoded;
    }
}
