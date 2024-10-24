package com.amnex.agristack.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

@Service
public class PmKisanUtil {

	public static String encrypt(String text, String key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes("UTF-8");
		int len = b.length;
		if (len > keyBytes.length)
			len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
		String result = Base64.getEncoder().encodeToString(results);
		return result;
	}

	public static String decrypt(String text, String key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes("UTF-8");
		int len = b.length;
		if (len > keyBytes.length)
			len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		// BASE64Decoder decoder = new BASE64Decoder();
		byte[] results = cipher.doFinal(Base64.getDecoder().decode(text));
		return new String(results, "UTF-8");
	}


	public static String getUniqueKey(int maxSize) throws NoSuchAlgorithmException {
		String chrs  = "abcdefghijklmnopqrstuvwxyz";
		SecureRandom secureRandom = SecureRandom.getInstanceStrong();
		return secureRandom
				.ints(maxSize, 0, chrs.length()) // 9 is the length of the string you want
				.mapToObj(i -> chrs.charAt(i))
				.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
				.toString().toUpperCase();
	}

}
