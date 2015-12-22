package com.getui.logful.util;

import android.util.Log;

import com.getui.logful.LoggerConstants;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

public class ClientAuthUtil {

    private static final String TAG = "ClientAuthUtil";

    private AtomicBoolean authorizing = new AtomicBoolean(false);

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private String accessToken;

    private String tokenType;

    private long authorizationTime;

    private long expiresIn;

    private static class ClassHolder {
        static ClientAuthUtil util = new ClientAuthUtil();
    }

    public static ClientAuthUtil util() {
        return ClassHolder.util;
    }

    public static boolean authenticated() {
        ClientAuthUtil util = ClientAuthUtil.util();
        if (!StringUtils.isEmpty(util.accessToken) && !StringUtils.isEmpty(util.tokenType)) {
            long diff = (System.currentTimeMillis() - util.authorizationTime) / 1000;
            if (diff <= util.expiresIn) {
                return true;
            }
        }
        return false;
    }

    public static String authorization() {
        ClientAuthUtil util = ClientAuthUtil.util();
        if (!StringUtils.isEmpty(util.accessToken) && !StringUtils.isEmpty(util.tokenType)) {
            return util.tokenType + " " + util.accessToken;
        }
        return "";
    }

    public static void authenticate() {
        ClientAuthUtil util = ClientAuthUtil.util();
        if (!util.authorizing.get()) {
            util.accessToken = null;
            util.tokenType = null;
            util.expiresIn = 0;
            util.authorizing.set(true);
            util.requestToken();
        }
    }

    private void requestToken() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpRequest request = null;
                try {
                    String url = SystemConfig.baseUrl() + LoggerConstants.CLIENT_AUTH_URI;
                    request = HttpRequest.post(url);
                    request.header("Accept", "application/json");

                    String authorization = "Basic " +
                            HttpRequest.Base64.encode(LoggerConstants.APP_KEY + ":" + LoggerConstants.APP_SECRET);
                    request.header("Authorization", authorization);
                    request.part("grant_type", "client_credentials");
                    request.part("scope", "client");
                    if (request.ok()) {
                        JSONObject object = new JSONObject(request.body());
                        accessToken = object.optString("access_token");
                        tokenType = object.optString("token_type");
                        expiresIn = object.optLong("expires_in");
                        authorizationTime = System.currentTimeMillis();
                        if (!initialized.get()) {
                            RemoteConfig.read();
                            initialized.set(true);
                        }
                    } else {
                        LogUtil.v(TAG, request.body());
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "", e);
                } finally {
                    if (request != null) {
                        request.disconnect();
                    }
                }
                authorizing.set(false);
            }
        }).start();
    }
}
