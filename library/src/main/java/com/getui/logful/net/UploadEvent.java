package com.getui.logful.net;

import com.getui.logful.util.ClientAuthUtil;
import com.getui.logful.util.ClientAuthUtil.AuthorizationListener;

public abstract class UploadEvent implements Runnable {

    @Override
    public void run() {
        ClientAuthUtil.util().addListener(listener).auth();
    }

    public String identifier() {
        return "";
    }

    public void authorized(String authorization) {
        ClientAuthUtil.util().removeListener(listener);
    }

    public void invalid() {
        ClientAuthUtil.util().removeListener(listener);
    }

    public void failure() {
        ClientAuthUtil.util().removeListener(listener);
    }

    private AuthorizationListener listener = new AuthorizationListener() {
        @Override
        public void onAuthorization(String token, String tokenType) {
            authorized(String.format("%s %s", tokenType, token));
        }

        @Override
        public void onInvalid() {
            invalid();
        }

        @Override
        public void onFailure() {
            failure();
        }
    };
}
