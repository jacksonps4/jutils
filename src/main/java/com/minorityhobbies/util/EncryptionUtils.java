package com.minorityhobbies.util;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EncryptionUtils {
    private EncryptionUtils() {}

    /**
     * Encrypts the data read from <code>in</code> using the specified key and writes it to <code>out</code>.
     *
     * Note that the underlying stream <code>out</code> may be closed after this method returns.
     *
     * @param in    The stream from which to read plaintext
     * @param out   The stream to which to write ciphertext
     * @param key   The key for encryption.
     *
     * @throws IOException  If an error occurred during reading / writing.
     */
    public static void encrypt(InputStream in, OutputStream out, byte[] key) throws IOException {
        doCipher(in, out, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * Decrypts the data read from <code>in</code> using the specified key and writes the result to <code>out</code>.
     *
     * @param in    The stream from which to read ciphertext.
     * @param out   The stream to which to write plaintext.
     * @param key   The key for decryption.
     * @throws IOException  If an error occurred during reading / writing.
     */
    public static void decrypt(InputStream in, OutputStream out, byte[] key) throws IOException {
        doCipher(in, out, key, Cipher.DECRYPT_MODE);
    }

    static void doCipher(InputStream in, OutputStream out, byte[] key, int mode) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            SecretKey k = new SecretKeySpec(key, "AES");

            cipher.init(mode, k);
            try (CipherOutputStream cout = new CipherOutputStream(out, cipher)) {
                byte[] b = new byte[1024 * 64];
                for (int read = 0; (read = in.read(b)) > -1; ) {
                    cout.write(b, 0, read);
                }
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> arguments = getArguments(args);

        Console console = System.console();
        if (console == null) {
            throw new IllegalStateException("No console - cannot read key");
        }
        System.out.print("Enter key: ");
        char[] keyChars = console.readPassword();
        Charset utf8 = Charset.forName("UTF-8");
        byte[] key = utf8.encode(CharBuffer.wrap(keyChars)).array();

        InputStream in = System.in;
        OutputStream out = System.out;

        if (arguments.contains("-in")) {
            String filename = arguments.get(arguments.indexOf("-in") + 1);
            in = new FileInputStream(filename);
        }
        if (arguments.contains("-out")) {
            String filename = arguments.get(arguments.indexOf("-out") + 1);
            out = new FileOutputStream(filename);
        }

        if (arguments.contains("-encrypt")) {
            encrypt(in, out, key);
        } else if (arguments.contains("-decrypt")) {
            decrypt(in, out, key);
        } else {
            throw new IllegalArgumentException("Invalid arguments");
        }
        out.flush();
    }

    static List<String> getArguments(String[] args) {
        return Arrays.asList(args)
                .stream()
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
