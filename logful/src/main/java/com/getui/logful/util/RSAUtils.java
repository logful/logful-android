package com.getui.logful.util;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtils {

    private static final String TAG = "RSAUtils";

    private static Cipher encryptCipher;

    public static PublicKey generatePublicKey(String base64String) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(base64String, Base64.DEFAULT)));
    }

    public static byte[] encrypt(byte[] data, PublicKey key) throws Exception {
        if (encryptCipher == null) {
            encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        }
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        return encryptCipher.doFinal(data);
    }
}
