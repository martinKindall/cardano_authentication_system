package com.walruscode.cardano.services;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

public class EncryptionService {
    private final SecureRandom secureRandom = new SecureRandom();
    private final static int GCM_IV_LENGTH = 12;
    private final SecretKey secretKey;
    private final byte[] associatedData;

    public EncryptionService(byte[] key) {
        secretKey = new SecretKeySpec(key, "AES");
        associatedData = "ProtocolVersion1".getBytes(StandardCharsets.UTF_8);
    }

    public byte[] encrypt(String plaintext) throws Exception {

        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        if (associatedData != null) {
            cipher.updateAAD(associatedData);
        }

        byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        return byteBuffer.array();
    }

    public String decrypt(byte[] cipherMessage) throws Exception {
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, cipherMessage, 0, GCM_IV_LENGTH);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmIv);

        if (associatedData != null) {
            cipher.updateAAD(associatedData);
        }
        byte[] plainText = cipher.doFinal(cipherMessage, GCM_IV_LENGTH, cipherMessage.length - GCM_IV_LENGTH);

        return new String(plainText, StandardCharsets.UTF_8);
    }
}
