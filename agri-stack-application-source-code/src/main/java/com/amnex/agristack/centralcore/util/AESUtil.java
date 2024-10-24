package com.amnex.agristack.centralcore.util;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    private static final String ALGO = "AES"; // Default uses ECB PKCS5Padding
    static final String secretKey = "amnex@123#%00014";

    public static String encrypt(String Data) throws Exception {
        Key key = generateKey(encodeKey(secretKey));
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = Base64.getEncoder().encodeToString(encVal);
        return encryptedValue;
    }

    public static String decrypt(String strToDecrypt) {
        try {
            Key key = generateKey(encodeKey(secretKey));
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    private static Key generateKey(String secret) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(secret.getBytes());
        Key key = new SecretKeySpec(decoded, ALGO);
        return key;
    }

    public static String decodeKey(String str) {
        byte[] decoded = Base64.getDecoder().decode(str.getBytes());
        return new String(decoded);
    }

    public static String encodeKey(String str) {
        byte[] encoded = Base64.getEncoder().encode(str.getBytes());
        System.out.println(new String(encoded));
        return new String(encoded);
    }

    // public static void main(String a[]) throws Exception {
    //     String toEncrypt = "Please encrypt this message!";
    //     System.out.println("Plain text = " + toEncrypt);
    //     // AES Encryption based on above secretKey
    //     String encrStr = encrypt(toEncrypt);
    //     System.out.println("Cipher Text: Encryption of str = " + encrStr);
    //     // AES Decryption based on above secretKey
    //     String decrStr = decrypt(encrStr);
    //     System.out.println("Decryption of str = " + decrStr);
    // }

    public static String passwordDecrypt(String encryptedText,String key) throws Exception {
    	String newKey=secretKey+key;
        byte[] keyBytes = newKey.getBytes();
        byte[] ivBytes = "8745178413689054".getBytes();

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivBytes));

        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decrypted);
    }
}
