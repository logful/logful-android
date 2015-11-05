package com.getui.logful.util;

import java.io.UnsupportedEncodingException;

import android.content.Context;

import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;

public class CryptoTool {

    private static final String TAG = "CryptoTool";

    private static class ClassHolder {
        static CryptoTool instance = new CryptoTool();
    }

    public static CryptoTool tool() {
        return ClassHolder.instance;
    }

    public static void loadLibrary() {
        CryptoTool tool = tool();
        tool.load();
    }

    private void load() {
        Context context = LoggerFactory.context();
        if (context == null) {
            return;
        }
        try {
            System.load(context.getFilesDir() + "/" + LoggerConstants.LIBRARY_FILE_NAME);
        } catch (Throwable throwable) {
            LogUtil.e(TAG, "", throwable);
        }
    }

    /**
     * AES 加密内容.
     *
     * @param string 需要加密的字符串
     * @return 加密过的字符串
     */
    public static synchronized byte[] aesEncrypt(String string) {
        CryptoTool tool = tool();
        String appId = SystemInfo.appId();
        if (appId == null) {
            return new byte[] {};
        }

        String result = null;
        try {
            result = tool.encrypt(appId, string);
        } catch (Throwable throwable) {
            LogUtil.e(TAG, "", throwable);
        }

        if (result != null) {
            try {
                return result.getBytes(LoggerConstants.CHARSET);
            } catch (UnsupportedEncodingException e) {
                LogUtil.e(TAG, "", e);
                return new byte[] {};
            }
        }
        return new byte[] {};
    }

    static {
        System.loadLibrary("logful");
    }

    private native String encrypt(String appId, String msg);
}
