package com.getui.logful.util;

import android.util.Base64;

import java.security.PublicKey;

import javax.crypto.Cipher;

public class CryptoTool {

    private static final String TAG = "CryptoTool";

    private static final String CRYPTO_ERROR = "CRYPTO_ERROR";

    private static String base64Key;

    private static PublicKey publicKey;

    public static void setPublicKey(String keyString) {
        try {
            publicKey = RSAUtil.generatePublicKey(keyString);
        } catch (Exception e) {
            LogUtil.e(TAG, "", e);
        }
    }

    /**
     * Encrypt string with AES 256 ECB.
     *
     * @param string String to encrypt
     * @return Encrypted cipher bytes with PKCS#7 padding
     */
    public static synchronized byte[] encryptString(String string) {
        byte[] baseKey = "CzUTJo9ChrFJKvJnLBUyYtjOiPz72asS".getBytes();
        try {
            byte[] data = addPadding(string.getBytes());
            byte[] result = CryptoTool.encrypt(baseKey, data, data.length);
            if (result != null) {
                return result;
            }
        } catch (Throwable throwable) {
            LogUtil.e(TAG, "", throwable);
        }

        return CRYPTO_ERROR.getBytes();
    }

    /**
     * Add PKCS#7 padding.
     *
     * @param input Input cipher bytes
     * @return Padding block bytes
     */
    private static byte[] addPadding(byte[] input) {
        int length = input.length;

        int count = 16 - (length % 16);

        byte code = (byte) count;

        byte[] output = new byte[length + count];

        System.arraycopy(input, 0, output, 0, length);

        for (int i = 0; i < count; i++) {
            output[length + i] = code;
        }

        return output;
    }

    private static String signature(byte[] data) {
        if (publicKey == null) {
            return "";
        }
        if (!StringUtils.isEmpty(base64Key)) {
            return base64Key;
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] result = cipher.doFinal(data);
            base64Key = Base64.encodeToString(result, Base64.NO_WRAP);
            return base64Key;
        } catch (Exception e) {
            LogUtil.e(TAG, "", e);
        }
        return "";
    }

    static {
        System.loadLibrary("logful");
    }

    public static native String security(byte[] baseKey, int baseKeyLen);

    private static native byte[] encrypt(byte[] baseKey, byte[] data, int dataLen);
}