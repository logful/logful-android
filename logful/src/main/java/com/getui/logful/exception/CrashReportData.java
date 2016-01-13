package com.getui.logful.exception;

public class CrashReportData {

    /**
     * 唯一 id.
     */
    private String uid;
    /**
     * Exception cause type.
     */
    private String exceptionCause;
    /**
     * Stack Trace.
     */
    private String stackTrace;
    /**
     * Stack Trace hash.
     */
    private String stackTraceHash;
    /**
     * app 启动时间.
     */
    private String appStartDate;
    /**
     * app 崩溃时间.
     */
    private String crashDate;
    /**
     * app 启动时间戳.
     */
    private String appStartTimeMillis;
    /**
     * app 崩溃时间戳.
     */
    private String crashTimeMillis;
    /**
     * 总内存.
     */
    private String totalMemorySize;
    /**
     * 可用内存.
     */
    private String availableMemorySize;
    /**
     * Logcat default extract. Requires READ_LOGS permission.
     */
    private String logcat;
    /**
     * Logcat eventsLog extract. Requires READ_LOGS permission.
     */
    private String eventsLog;
    /**
     * Logcat radio extract. Requires READ_LOGS permission.
     */
    private String radioLog;
    /**
     * Retrieves details of the failing thread (id, name, group name).
     */
    private String threadDetail;

    public String getExceptionCause() {
        return exceptionCause;
    }

    public void setExceptionCause(String exceptionCause) {
        this.exceptionCause = exceptionCause;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getStackTraceHash() {
        return stackTraceHash;
    }

    public void setStackTraceHash(String stackTraceHash) {
        this.stackTraceHash = stackTraceHash;
    }

    public String getAppStartDate() {
        return appStartDate;
    }

    public void setAppStartDate(String appStartDate) {
        this.appStartDate = appStartDate;
    }

    public String getCrashDate() {
        return crashDate;
    }

    public void setCrashDate(String crashDate) {
        this.crashDate = crashDate;
    }

    public String getAppStartTimeMillis() {
        return appStartTimeMillis;
    }

    public void setAppStartTimeMillis(String appStartTimeMillis) {
        this.appStartTimeMillis = appStartTimeMillis;
    }

    public String getCrashTimeMillis() {
        return crashTimeMillis;
    }

    public void setCrashTimeMillis(String crashTimeMillis) {
        this.crashTimeMillis = crashTimeMillis;
    }

    public String getTotalMemorySize() {
        return totalMemorySize;
    }

    public void setTotalMemorySize(String totalMemorySize) {
        this.totalMemorySize = totalMemorySize;
    }

    public String getAvailableMemorySize() {
        return availableMemorySize;
    }

    public void setAvailableMemorySize(String availableMemorySize) {
        this.availableMemorySize = availableMemorySize;
    }

    public String getLogcat() {
        return logcat;
    }

    public void setLogcat(String logcat) {
        this.logcat = logcat;
    }

    public String getEventsLog() {
        return eventsLog;
    }

    public void setEventsLog(String eventsLog) {
        this.eventsLog = eventsLog;
    }

    public String getRadioLog() {
        return radioLog;
    }

    public void setRadioLog(String radioLog) {
        this.radioLog = radioLog;
    }

    public String getThreadDetail() {
        return threadDetail;
    }

    public void setThreadDetail(String threadDetail) {
        this.threadDetail = threadDetail;
    }

}
