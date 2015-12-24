package com.getui.logful.net;

import com.getui.logful.util.LogUtil;

public abstract class UploadEvent implements Runnable {

    private static final String TAG = "UploadEvent";

    @Override
    public void run() {
        if (ClientUserInitService.granted()) {
            startRequest(ClientUserInitService.authorization());
        } else {
            LogUtil.w(TAG, "Client user not allow to upload file!");
        }
    }

    public String identifier() {
        return "";
    }

    public void startRequest(String authorization) {
        // Rewrite
    }
}
