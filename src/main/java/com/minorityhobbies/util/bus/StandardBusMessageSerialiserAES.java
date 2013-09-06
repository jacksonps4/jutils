package com.minorityhobbies.util.bus;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

class StandardBusMessageSerialiserAES extends StandardBusMessageSerialiser {
	private static final String cipherSpec = "AES/ECB/PKCS5Padding";
	
	private final SecretKey key;
	
	public StandardBusMessageSerialiserAES()  {
		super();
		KeyGenerator keyGen;
		try {
			keyGen = KeyGenerator.getInstance("AES");
			key = keyGen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] toBytes(BusMessage msg) throws IOException {
		byte[] plaintext = super.toBytes(msg);
		return encrypt(plaintext);
	}

	byte[] encrypt(byte[] plaintext) throws IOException {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(cipherSpec);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(plaintext);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public BusMessage fromBytes(byte[] cipherText) throws IOException {
		byte[] plaintext = decrypt(cipherText);
		return super.fromBytes(plaintext);
	}

	byte[] decrypt(byte[] cipherText) throws IOException {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(cipherSpec);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(cipherText);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
