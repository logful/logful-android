package com.getui.logful.util;

import android.util.Base64;

public class CryptoTool {

    private static final String TAG = "CryptoTool";

    private static String pemPublicKeyString;

    private static String securityKeyString;

    private static byte[] errorBytes = new byte[]{0x00, 0x00};

    public static void addPublicKey(String keyString) {
        pemPublicKeyString = keyString;
    }

    public static String securityString() {
        if (StringUtils.isEmpty(pemPublicKeyString)) {
            return null;
        }
        if (StringUtils.isEmpty(SystemConfig.appKey())) {
            return null;
        }
        if (StringUtils.isEmpty(UidTool.uid())) {
            return null;
        }
        if (!StringUtils.isEmpty(securityKeyString)) {
            return securityKeyString;
        }
        try {
            byte[] keyBytes = pemPublicKeyString.getBytes();
            byte[] pwdBytes = SystemConfig.appKey().getBytes();
            byte[] saltBytes = UidTool.uid().getBytes();
            byte[] result = security(keyBytes, keyBytes.length, pwdBytes, pwdBytes.length, saltBytes, saltBytes.length);
            if (result != null) {
                securityKeyString = Base64.encodeToString(result, Base64.NO_WRAP);
                return securityKeyString;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "", e);
        }
        return null;
    }

    /**
     * Encrypt string with AES 256 ECB.
     *
     * @param string String to encrypt
     * @return Encrypted cipher bytes with PKCS#7 padding
     */
    public static synchronized byte[] AESEncrypt(String string) {
        if (StringUtils.isEmpty(SystemConfig.appKey())) {
            return errorBytes;
        }
        if (StringUtils.isEmpty(UidTool.uid())) {
            return errorBytes;
        }
        try {
            byte[] pwdBytes = SystemConfig.appKey().getBytes();
            byte[] saltBytes = UidTool.uid().getBytes();
            byte[] data = addPadding(string.getBytes());
            byte[] result = CryptoTool.encrypt(pwdBytes, pwdBytes.length, saltBytes, saltBytes.length, data, data.length);
            if (result != null) {
                return result;
            }
        } catch (Throwable throwable) {
            LogUtil.e(TAG, "", throwable);
        }
        return errorBytes;
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

    static {
        System.loadLibrary("logful");
    }

    public static native byte[] security(byte[] publicKey, int keyLen, byte[] pwd, int pwdLen, byte[] salt, int saltLen);

    private static native byte[] encrypt(byte[] pwd, int pwdLen, byte[] salt, int saltLen, byte[] data, int dataLen);
}