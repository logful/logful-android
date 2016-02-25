package com.getui.logful.util;

import android.util.Base64;

import com.getui.logful.LoggerConfigurator;
import com.getui.logful.LoggerFactory;
import com.getui.logful.security.SecurityProvider;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoTool {

    private static final String TAG = "CryptoTool";

    private static String pemPublicKeyString;

    private static String securityKeyString;

    private static byte[] errorBytes = new byte[]{0x00, 0x00};

    private static AtomicBoolean loaded = new AtomicBoolean(false);

    private static final int AES_KEY_SIZE = 256;

    private static Cipher cipher;

    private static byte[] AESKeyBytes;

    public static synchronized void addPublicKey(String keyString) {
        pemPublicKeyString = keyString;
    }

    public static String securityString() {
        LoggerConfigurator configurator = LoggerFactory.config();
        if (configurator == null) {
            return null;
        }

        SecurityProvider provider = configurator.getSecurityProvider();
        if (provider == null) {
            return null;
        }

        if (StringUtils.isEmpty(pemPublicKeyString)) {
            return null;
        }

        if (!StringUtils.isEmpty(securityKeyString)) {
            return securityKeyString;
        }

        byte[] keyBytes = pemPublicKeyString.getBytes();
        byte[] pwdBytes = provider.password();
        byte[] saltBytes = provider.salt();

        try {
            byte[] result = null;
            // Check if use native cryptor.
            if (configurator.isUseNativeCryptor()) {
                if (CryptoTool.JNI_load()) {
                    result = CryptoTool.JNI_security(keyBytes, pwdBytes, saltBytes);
                }
            } else {
                result = CryptoTool.JAVA_security(keyBytes, pwdBytes, saltBytes);
            }
            if (result != null) {
                securityKeyString = HttpRequest.Base64.encodeBytes(result);
                return securityKeyString;
            }
        } catch (Exception e) {
            LogUtil.wtf(TAG, "", e);
        }

        return null;
    }

    /**
     * Encrypt string with AES 256 ECB.
     *
     * @param string String to encrypt
     * @return Encrypted cipher bytes with PKCS#7 padding
     */
    public static byte[] AESEncrypt(String string) {
        LoggerConfigurator configurator = LoggerFactory.config();
        if (configurator == null) {
            return errorBytes;
        }

        SecurityProvider provider = configurator.getSecurityProvider();
        if (provider == null) {
            return errorBytes;
        }

        byte[] pwdBytes = provider.password();
        byte[] saltBytes = provider.salt();
        byte[] data = string.getBytes();

        try {
            byte[] result = null;
            // Check if use native cryptor.
            if (configurator.isUseNativeCryptor()) {
                if (CryptoTool.JNI_load()) {
                    result = CryptoTool.JNI_encrypt(pwdBytes, saltBytes, data);
                }
            } else {
                result = CryptoTool.JAVA_encrypt(pwdBytes, saltBytes, data);
            }
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
            LogUtil.wtf(TAG, "", e);
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

    private static synchronized byte[] JAVA_security(byte[] pemKey, byte[] pwd, byte[] salt) throws Exception {
        if (pemKey == null || pwd == null || salt == null) {
            return null;
        }

        AESKeyBytes = CryptoTool.JAVA_calculateAESKey(pwd, salt);
        if (AESKeyBytes == null || AESKeyBytes.length != 32) {
            return null;
        }

        String pemKeyString = new String(pemKey);
        String pubKeyPEM = pemKeyString.replace("-----BEGIN PUBLIC KEY-----", "");
        pubKeyPEM = pubKeyPEM.replace("-----END PUBLIC KEY-----", "");
        pubKeyPEM = pubKeyPEM.replace("\r", "");
        pubKeyPEM = pubKeyPEM.replace("\n", "");
        pubKeyPEM = pubKeyPEM.replace("\t", "");
        pubKeyPEM = pubKeyPEM.replace(" ", "");

        byte[] encoded = Base64.decode(pubKeyPEM, Base64.NO_WRAP);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = factory.generatePublic(new X509EncodedKeySpec(encoded));

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(AESKeyBytes);
    }

    private static synchronized byte[] JAVA_encrypt(byte[] pwd, byte[] salt, byte[] data) throws Exception {
        if (pwd == null || salt == null || data == null) {
            return null;
        }

        if (AESKeyBytes == null || AESKeyBytes.length != 32) {
            AESKeyBytes = CryptoTool.JAVA_calculateAESKey(pwd, salt);
        }

        if (AESKeyBytes == null || AESKeyBytes.length != 32) {
            return null;
        }

        SecretKeySpec secretKeySpec = new SecretKeySpec(AESKeyBytes, "AES");
        if (cipher == null) {
            cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        }
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    private static synchronized byte[] JNI_security(byte[] publicKey, byte[] pwd, byte[] salt) {
        if (publicKey == null || pwd == null || salt == null) {
            return null;
        }
        return CryptoTool.security(publicKey, publicKey.length, pwd, pwd.length, salt, salt.length);
    }

    private static synchronized byte[] JNI_encrypt(byte[] pwd, byte[] salt, byte[] data) {
        if (pwd == null || salt == null || data == null) {
            return null;
        }
        byte[] input = CryptoTool.addPadding(data);
        return CryptoTool.encrypt(pwd, pwd.length, salt, salt.length, input, input.length);
    }

    private static synchronized byte[] JAVA_calculateAESKey(byte[] pwd, byte[] salt) throws Exception {
        if (pwd == null || salt == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (byte b : pwd) {
            builder.append(String.format("%02x", b));
        }

        char[] pwdChar = builder.toString().toCharArray();
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(pwdChar, salt, 50, AES_KEY_SIZE);
        SecretKey secretKey = factory.generateSecret(spec);

        return secretKey.getEncoded();
    }

    private static synchronized boolean JNI_load() {
        if (loaded.get()) {
            return true;
        }

        try {
            System.loadLibrary("logful");
            loaded.set(true);
            return true;
        } catch (Exception e) {
            LogUtil.wtf(TAG, "", e);
        }

        return false;
    }

    public static native byte[] security(byte[] publicKey, int keyLen, byte[] pwd, int pwdLen, byte[] salt, int saltLen);

    private static native byte[] encrypt(byte[] pwd, int pwdLen, byte[] salt, int saltLen, byte[] data, int dataLen);
}