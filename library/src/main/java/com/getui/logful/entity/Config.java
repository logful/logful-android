package com.getui.logful.entity;

public class Config {

    private int level;

    private int targetLevel;

    private boolean shouldUpload;

    private int scheduleType;

    private long scheduleTime;

    private String[] scheduleArray;

    public static Config defaultConfig() {
        Config config = new Config();
        config.setShouldUpload(false);
        return config;
    }

    public boolean isShouldUpload() {
        return shouldUpload;
    }

    public void setShouldUpload(boolean shouldUpload) {
        this.shouldUpload = shouldUpload;
    }

    public int getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(int scheduleType) {
        this.scheduleType = scheduleType;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setTargetLevel(int targetLevel) {
        this.targetLevel = targetLevel;
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
