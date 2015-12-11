package com.getui.logful.util;

public class CryptoTool {

    private static final String TAG = "CryptoTool";

    private static final String CRYPTO_ERROR = "CRYPTO_ERROR";

    /**
     * AES 加密内容.
     *
     * @param string 需要加密的字符串
     * @return 加密过的字符串
     */
    public static synchronized byte[] aesEncrypt(String string) {
        String appId = SystemInfo.appId();

        if (appId != null) {
            try {
                byte[] result = CryptoTool.encrypt(appId, string, string.length());
                if (result != null) {
                    return result;
                }
            } catch (Throwable throwable) {
                LogUtil.e(TAG, "", throwable);
            }
        }

        return CRYPTO_ERROR.getBytes();
    }

    static {
        System.loadLibrary("logful");
    }

    private static native byte[] encrypt(String appId, String text, int textLen);
}