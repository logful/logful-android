package com.getui.logful.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Checksum {

    private static final String TAG = "Checksum";

    public static String fileMD5(String filePath) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            return String.format("%32s", output).replace(' ', '0');
        } catch (IOException e) {
            LogUtil.v(TAG, "Unable to process file for MD5.");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LogUtil.e(TAG, "", e);
            }
        }

        return null;
    }

    public static String md5(String string) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(TAG, "Get digest failed.");
            return "";
        }

        digest.update(string.getBytes());
        byte[] bytes = digest.digest();
        StringBuilder stringBuffer = new StringBuilder();
        for (byte b : bytes) {
            stringBuffer.append(String.format("%02x", b & 0xff));
        }

        return stringBuffer.toString();
    }

}
