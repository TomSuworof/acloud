package com.salat.acloud.services;

import java.io.*;
import java.nio.file.Files;

public class EncryptionService {

//    public String publicKey;
//    public String privateKey;

    private static final int shift = 42;

//    public EncryptionService(String publicKey) {
//        this.publicKey = publicKey;
//        shift = Integer.parseInt(this.publicKey);
//    }

    public static void main(String[] args) {
        try {
            File file = new File("resume.pdf");
            byte[] content = Files.readAllBytes(file.toPath());

            content = encode(content);
            content = decode(content);

            File newFile = new File("resumeNew.pdf");

            OutputStream writer = new FileOutputStream(newFile);
            writer.write(content);

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
