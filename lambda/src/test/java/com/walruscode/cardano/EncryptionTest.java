package com.walruscode.cardano;

import com.walruscode.cardano.services.EncryptionService;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static com.walruscode.cardano.Utils.Utils.generateRandomString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncryptionTest {

    @Test
    public void testEncryption() throws Exception {
        SecureRandom secureRandom = new SecureRandom();

        byte[] key = new byte[16];
        secureRandom.nextBytes(key);

        EncryptionService encryptionService = new EncryptionService(EncryptionService.encodeUsingHexFormat(key));

        String message = "the secret message: " + generateRandomString();
        String cipherText = encryptionService.encrypt(message);

        String decrypted = encryptionService.decrypt(cipherText);

        assertEquals(message, decrypted);
    }
}
