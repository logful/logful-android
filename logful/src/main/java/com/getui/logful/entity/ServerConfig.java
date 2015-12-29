package com.getui.logful.entity;

import org.json.JSONObject;

public class ServerConfig {

    private boolean granted;
    private int scheduleType;
    private long scheduleTime;
    private String[] scheduleArray;

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

    public int getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(int scheduleType) {
        this.scheduleType = scheduleType;
    }

    public long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String[] getScheduleArray() {
        return scheduleArray;
    }

    public void setScheduleArray(String[] scheduleArray) {
        this.scheduleArray = scheduleArray;
    }

}
