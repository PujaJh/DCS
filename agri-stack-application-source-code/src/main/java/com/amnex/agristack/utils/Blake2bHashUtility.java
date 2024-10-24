package com.amnex.agristack.utils;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.io.DigestOutputStream;
//import org.bouncycastle.crypto.io.OutputStreamHashCalculator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Blake2bHashUtility {

    public static byte[] calculateBlake2bHash(byte[] data) {
        Blake2bDigest blake2bDigest = new Blake2bDigest();
        blake2bDigest.update(data, 0, data.length);

        byte[] hash = new byte[blake2bDigest.getDigestSize()];
        blake2bDigest.doFinal(hash, 0);

        return hash;
    }

    public static String calculateBlake2bHexHash(String data) {
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        byte[] hashBytes = calculateBlake2bHash(dataBytes);

        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : hashBytes) {
            hexStringBuilder.append(String.format("%02x", b));
        }

        return hexStringBuilder.toString();
    }
}
