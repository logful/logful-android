package com.getui.logful.net;

import android.util.Base64;

import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;
import com.getui.logful.util.CryptoTool;
import com.getui.logful.util.HttpRequest;
import com.getui.logful.util.LogUtil;
import com.getui.logful.util.StringUtils;
import com.getui.logful.util.SystemConfig;

import org.json.JSONObject;

public class ClientUserInitService {

    private static final String TAG = "ClientUserInitService";

    private String accessToken;

    private String tokenType;

    private long authorizationTime;

    private long expiresIn;

    private static class ClassHolder {
        static ClientUserInitService service = new ClientUserInitService();
    }

    public static ClientUserInitService service() {
        return ClassHolder.service;
    }

    public static boolean authenticated() {
        return true;
    }

    public static String authorization() {
        ClientUserInitService service = service();
        return service._authorization();
    }

    public static void authenticate() {
        ClientUserInitService service = service();
        service._authenticate();
    }

    public String _authorization() {
        if (!StringUtils.isEmpty(tokenType) && !StringUtils.isEmpty(accessToken)) {
            return tokenType + " " + accessToken;
        }
        return "";
    }

    private void _authenticate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpRequest request = null;
                try {
                    String url = SystemConfig.baseUrl() + LoggerConstants.CLIENT_AUTH_URI;
                    request = HttpRequest.post(url);
                    request.header("Accept", "application/json");
                    String temp = LoggerConstants.APP_KEY + ":" + LoggerConstants.APP_SECRET;
                    String authorization = "Basic " + Base64.encodeToString(temp.getBytes(), Base64.NO_WRAP);
                    request.header("Authorization", authorization);
                    request.part("grant_type", "client_credentials");
                    request.part("scope", "client");
                    if (request.ok()) {
                        JSONObject object = new JSONObject(request.body());
                        accessToken = object.optString("access_token");
                        tokenType = object.optString("token_type");
                        expiresIn = object.optLong("expires_in");
                        authorizationTime = System.currentTimeMillis();
                        CryptoTool.addPublicKey(object.optString("public_key"));

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

    private void sendUserReport() {
        String signature = CryptoTool.securityString();
        if (StringUtils.isEmpty(signature)) {
            LogUtil.e(TAG, "Encrypt AES key error!");
            return;
        }
        HttpRequest request = null;
        try {
            String url = SystemConfig.baseUrl() + LoggerConstants.UPLOAD_USER_INFO_URI;
            request = HttpRequest.post(url);
            request.header("Content-Type", "application/json");
            request.header("Accept", "application/json");
            request.header("Authorization", authorization());

            JSONObject object = new JSONObject();
            object.put("sdkVersion", LoggerFactory.version());
            object.put("signature", signature);
            object.put("chunk", "++++++++");

            request.send(object.toString().getBytes());

            if (request.code() == 200) {
                // TODO
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
        return "";
    }
}
