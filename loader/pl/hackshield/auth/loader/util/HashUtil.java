/*
 * Decompiled with CFR 0.150.
 */
package pl.hackshield.auth.loader.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    public static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException var2) {
            throw new IllegalArgumentException(var2);
        }
    }

    public static MessageDigest getSha1Digest() {
        return HashUtil.getDigest("SHA-1");
    }

    public static byte[] sha1(byte[] var0) {
        return HashUtil.getSha1Digest().digest(var0);
    }

    public static String sha1Hex(byte[] var0) {
        return HashUtil.byteArrayToHexString(HashUtil.sha1(var0));
    }

    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; ++i) {
            result = result + Integer.toString((b[i] & 0xFF) + 256, 16).substring(1);
        }
        return result;
    }
}

