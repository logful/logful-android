package com.getui.logful.net;

import android.os.Build;
import android.util.Base64;

import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;
import com.getui.logful.entity.ServerConfig;
import com.getui.logful.util.CryptoTool;
import com.getui.logful.util.HttpRequest;
import com.getui.logful.util.LogUtil;
import com.getui.logful.util.StringUtils;
import com.getui.logful.util.SystemConfig;
import com.getui.logful.util.SystemInfo;
import com.getui.logful.util.UidTool;

import org.json.JSONObject;

public class ClientUserInitService {

    private static final String TAG = "ClientUserInitService";

    private String accessToken;

    private String tokenType;

    private long authorizationTime;

    private long expiresIn;

    private ServerConfig config;

    private static class ClassHolder {
        static ClientUserInitService service = new ClientUserInitService();
    }

    public static ClientUserInitService service() {
        return ClassHolder.service;
    }

    public static boolean granted() {
        ClientUserInitService service = service();
        // TODO
        return service._authenticated();
    }

    public static boolean authenticated() {
        ClientUserInitService service = service();
        return service._authenticated();
    }

    public static String authorization() {
        ClientUserInitService service = service();
        return service._authorization();
    }

    public static void authenticate() {
        ClientUserInitService service = service();
        service._authenticate();
    }

    public static void bind(final String deviceId) {
        ClientUserInitService service = service();
        service._bind(deviceId);
    }

    private boolean _authenticated() {
        return !StringUtils.isEmpty(tokenType) && !StringUtils.isEmpty(accessToken);
    }

    private String _authorization() {
        if (!StringUtils.isEmpty(tokenType) && !StringUtils.isEmpty(accessToken)) {
            return tokenType + " " + accessToken;
        }
        return "";
    }

    private void _authenticate() {
        final String appKey = SystemConfig.appKey();
        final String appSecret = SystemConfig.appSecret();
        if (StringUtils.isEmpty(appKey) || StringUtils.isEmpty(appSecret)) {
            LogUtil.w(TAG, "App key and secret not set!");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpRequest request = null;
                try {
                    String url = SystemConfig.apiUrl(LoggerConstants.CLIENT_AUTH_URI);
                    request = HttpRequest.post(url);
                    request.header("Accept", "application/json");
                    String temp = appKey + ":" + appSecret;
                    String authorization = "Basic " + Base64.encodeToString(temp.getBytes(), Base64.NO_WRAP);
                    request.header("Authorization", authorization);
                    request.part("grant_type", "client_credentials");
                    request.part("scope", "client");
                    if (request.ok()) {
                        String body = request.body();
                        LogUtil.i(TAG, body);

                        JSONObject object = new JSONObject(body);
                        accessToken = object.optString("access_token");
                        tokenType = object.optString("token_type");
                        expiresIn = object.optLong("expires_in");
                        authorizationTime = System.currentTimeMillis();
                        CryptoTool.addPublicKey(object.optString("public_key"));

                        LogUtil.i(TAG, "Client user authenticate successful!");
                        // Send client user report information.
                        sendUserReport();
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "", e);
                } finally {
                    if (request != null) {
                        request.disconnect();
                    }
                }
            }
        }).start();
    }

    private void _bind(final String deviceId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (_authenticated()) {
                    bindDeviceId(deviceId);
                } else {
                    try {
                        Thread.sleep(30 * 1000);
                        if (_authenticated()) {
                            bindDeviceId(deviceId);
                        } else {
                            LogUtil.w(TAG, "Bind push sdk cid failed!");
                        }
                    } catch (InterruptedException e) {
                        LogUtil.e(TAG, "", e);
                    }
                }
            }
        }).start();
    }

    private void bindDeviceId(final String deviceId) {
        String url = SystemConfig.apiUrl(LoggerConstants.BIND_DEVICE_ID_URI);
        HttpRequest request = null;
        try {
            JSONObject object = new JSONObject();
            object.put("sdkVersion", LoggerFactory.version());
            object.put("platform", LoggerConstants.PLATFORM_ANDROID);
            object.put("uid", UidTool.uid());
            object.put("appId", SystemInfo.appId());
            object.put("deviceId", deviceId);

            request = HttpRequest.post(url);
            request.header("Content-Type", "application/json");
            request.header("Accept", "application/json");
            request.header("Authorization", authorization());

            request.send(object.toString().getBytes());

            if (request.code() == 200) {
                LogUtil.i(TAG, "Bind push sdk cid successful!");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "", e);
        } finally {
            if (request != null) {
                request.disconnect();
            }
        }
    }

    private void sendUserReport() {
        String signature = CryptoTool.securityString();
        if (StringUtils.isEmpty(signature)) {
            LogUtil.e(TAG, "Encrypt AES key error!");
            return;
        }
        String info = userInformation();
        if (StringUtils.isEmpty(info)) {
            LogUtil.e(TAG, "Collect user report information error!");
            return;
        }
        byte[] data = CryptoTool.AESEncrypt(userInformation());
        if (data == null) {
            LogUtil.e(TAG, "Encrypt user report information failed!");
            return;
        }
        HttpRequest request = null;
        try {
            String url = SystemConfig.apiUrl(LoggerConstants.UPLOAD_USER_INFO_URI);
            request = HttpRequest.post(url);
            request.header("Content-Type", "application/json");
            request.header("Accept", "application/json");
            request.header("Authorization", authorization());

            JSONObject object = new JSONObject();
            object.put("sdkVersion", LoggerFactory.version());
            object.put("signature", signature);
            object.put("chunk", Base64.encodeToString(data, Base64.NO_WRAP));

            request.send(object.toString().getBytes());

            if (request.code() == 200) {
                LogUtil.i(TAG, "Send user report information successful!");
                String body = request.body();
                if (!StringUtils.isEmpty(body)) {
                    LogUtil.i(TAG, body);
                    ServerConfig config = new ServerConfig(new JSONObject(body));
                    LogUtil.i(TAG, "Read server config successful!");
                    impServerConfig(config);
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "", e);
        } finally {
            if (request != null) {
                request.disconnect();
            }
        }
    }

    private String userInformation() {
        try {
            JSONObject object = new JSONObject();
            object.put("platform", LoggerConstants.PLATFORM_ANDROID);
            object.put("uid", UidTool.uid());
            object.put("alias", SystemConfig.alias());
            object.put("model", Build.MODEL);
            object.put("imei", SystemInfo.imei());
            object.put("macAddress", SystemInfo.macAddress());
            object.put("osVersion", Build.VERSION.RELEASE);
            object.put("appId", SystemInfo.appId());
            object.put("version", SystemInfo.version());
            object.put("versionString", SystemInfo.versionString());
            object.put("recordOn", LoggerFactory.isOn());
            return object.toString();
        } catch (Exception e) {
            LogUtil.e(TAG, "", e);
        }
        return "";
    }

    private void impServerConfig(ServerConfig config) {
        this.config = config;
        if (config.isGranted()) {
            // TODO
        } else {
            LogUtil.d(TAG, "Client user not granted!");
        }
    }
}
