package com.getui.logful.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.getui.logful.LoggerConstants;

public class ClientAuthUtil {

    private static final String TAG = "ClientAuthUtil";

    public interface AuthorizationListener {
        void onAuthorization(String token, String tokenType);

        void onInvalid();

        void onFailure();
    }

    private String accessToken;

    private String tokenType;

    private long authorizationTime;

    private long expiresIn;

    private List<AuthorizationListener> listenerList = new ArrayList<AuthorizationListener>();

    private static class ClassHolder {
        static ClientAuthUtil util = new ClientAuthUtil();
    }

    public static ClientAuthUtil util() {
        return ClassHolder.util;
    }

    public ClientAuthUtil addListener(AuthorizationListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
        return this;
    }

    public ClientAuthUtil removeListener(AuthorizationListener listener) {
        listenerList.remove(listener);
        return this;
    }

    public ClientAuthUtil clearToken() {
        accessToken = null;
        tokenType = null;
        authorizationTime = 0;
        expiresIn = 0;
        return this;
    }

    public ClientAuthUtil auth() {
        if (!StringUtils.isEmpty(accessToken) && !StringUtils.isEmpty(tokenType)) {
            long diff = (System.currentTimeMillis() - authorizationTime) / 1000;
            if (diff <= expiresIn) {
                for (AuthorizationListener listener : listenerList) {
                    listener.onAuthorization(accessToken, tokenType);
                }
            } else {
                requestToken();
            }
        } else {
            requestToken();
        }
        return this;
    }

    private void requestToken() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = SystemConfig.baseUrl() + LoggerConstants.CLIENT_AUTH_URI;
                    HttpRequest request = HttpRequest.post(url);
                    request.header("Accept", "application/json");

                    String authorization =
                            String.format(
                                    "Basic %s",
                                    HttpRequest.Base64.encode(LoggerConstants.APP_KEY + ":"
                                            + LoggerConstants.APP_SECRET));
                    request.header("Authorization", authorization);
                    request.part("grant_type", "client_credentials");
                    request.part("scope", "client");

                    if (request.ok()) {
                        JSONObject object = new JSONObject(request.body());
                        accessToken = object.optString("access_token");
                        tokenType = object.optString("token_type");
                        expiresIn = object.optLong("expires_in");

                        authorizationTime = System.currentTimeMillis();

                        for (AuthorizationListener listener : listenerList) {
                            listener.onAuthorization(accessToken, tokenType);
                        }
                    } else {
                        for (AuthorizationListener listener : listenerList) {
                            listener.onInvalid();
                        }
                    }
                } catch (Exception e) {
                    for (AuthorizationListener listener : listenerList) {
                        listener.onFailure();
                    }
                    LogUtil.e(TAG, "", e);
                }
            }
        }).start();
    }
}
