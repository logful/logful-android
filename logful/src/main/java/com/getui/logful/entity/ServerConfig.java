package com.getui.logful.entity;

import org.json.JSONObject;

public class ServerConfig {

    private boolean granted;

    public ServerConfig(JSONObject object) {
        // TODO
        if (object.has("granted")) {
            this.granted = object.optBoolean("granted");
        } else {
            throw new IllegalArgumentException("No granted field!");
        }
    }

    public boolean isGranted() {
        return granted;
    }

    public void setGranted(boolean granted) {
        this.granted = granted;
    }

}
