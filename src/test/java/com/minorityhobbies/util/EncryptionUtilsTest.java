package com.minorityhobbies.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;

public class EncryptionUtilsTest {
    private static final String CHARSET_NAME = "UTF-8";

    @Test
    public void roundTripTest() throws Exception {
        String data = "this is a test with some of the appropriate data in as test material";
        String key = "ALotofTestKeyMaterial";

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        EncryptionUtils.encrypt(new ByteArrayInputStream(data.getBytes(CHARSET_NAME)), out, key.getBytes());

        byte[] encryptedBytes = out.toByteArray();

        out = new ByteArrayOutputStream();
        EncryptionUtils.decrypt(new ByteArrayInputStream(encryptedBytes), out, key.getBytes());

        byte[] decodedBytes = out.toByteArray();
        String decryptedData = new String(decodedBytes, 0, decodedBytes.length, CHARSET_NAME);

        assertEquals(data, decryptedData);
    }
}
