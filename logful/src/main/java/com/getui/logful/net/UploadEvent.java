package com.getui.logful.net;

public abstract class UploadEvent implements Runnable {

    @Override
    public void run() {
        if (ClientUserInitService.authenticated()) {
            startRequest(ClientUserInitService.authorization());
        }
    }

    public String identifier() {
        return "";
    }

    public void startRequest(String authorization) {
        // Rewrite
    }
}
