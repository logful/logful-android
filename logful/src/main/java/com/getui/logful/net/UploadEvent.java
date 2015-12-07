package com.getui.logful.net;

import com.getui.logful.util.ClientAuthUtil;

public abstract class UploadEvent implements Runnable {

    @Override
    public void run() {
        if (ClientAuthUtil.authenticated()) {
            startRequest(ClientAuthUtil.authorization());
        }
    }

    public String identifier() {
        return "";
    }

    public void startRequest(String authorization) {
        // Rewrite
    }
}
