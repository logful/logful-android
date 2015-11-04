package com.getui.logful;


public class LoggerConfigurator {

    /**
     * 单个日志文件最大字节数（单位：字节）.
     */
    private long logFileMaxSize;

    /**
     * 允许上传日志文件的网络环境.
     */
    private int[] uploadNetworkType;

    /**
     * 需要上传的日志级别.
     */
    private int[] uploadLogLevel;

    /**
     * 是否删除已经上传的日志文件.
     */
    private boolean deleteUploadedLogFile;

    /**
     * 定时刷新日志系统（单位：秒）.
     */
    private long updateSystemFrequency;

    /**
     * 同时上传文件数量.
     */
    private int activeUploadTask;

    /**
     * 同时写入日志文件数量.
     */
    private int activeLogWriter;

    /**
     * 是否捕捉未捕捉的异常信息.
     */
    private boolean caughtException;

    /**
     * 默认的 logger 名称.
     */
    private String defaultLoggerName;

    /**
     * 默认的消息模板.
     */
    private String defaultMsgLayout;

    /**
     * 截图文件压缩质量（1~100）.
     */
    private int screenshotQuality;

    /**
     * 截图文件缩放比例（0.1~1）.
     */
    private float screenshotScale;

    public LoggerConfigurator() {
        this.logFileMaxSize = LoggerConstants.DEFAULT_LOG_FILE_MAX_SIZE;
        this.uploadNetworkType = LoggerConstants.DEFAULT_UPLOAD_NETWORK_TYPE;
        this.uploadLogLevel = LoggerConstants.DEFAULT_UPLOAD_LOG_LEVEL;
        this.updateSystemFrequency = LoggerConstants.DEFAULT_UPDATE_SYSTEM_FREQUENCY;
        this.activeUploadTask = LoggerConstants.DEFAULT_ACTIVE_UPLOAD_TASK;
        this.activeLogWriter = LoggerConstants.DEFAULT_ACTIVE_LOG_WRITER;
        this.deleteUploadedLogFile = LoggerConstants.DEFAULT_DELETE_UPLOADED_LOG_FILE;
        this.caughtException = LoggerConstants.DEFAULT_CAUGHT_EXCEPTION;
        this.defaultLoggerName = LoggerConstants.DEFAULT_LOGGER_NAME;
        this.defaultMsgLayout = LoggerConstants.DEFAULT_MSG_LAYOUT;
        this.screenshotQuality = LoggerConstants.DEFAULT_SCREENSHOT_QUALITY;
        this.screenshotScale = LoggerConstants.DEFAULT_SCREENSHOT_SCALE;
    }

    /**
     * 获取默认的 config 配置
     *
     * @return LoggerConfigurator 配置
     */
    public static LoggerConfigurator build() {
        return new LoggerConfigurator();
    }

    /**
     * 获取当前设置的单个文件最大容量限制.
     *
     * @return 设置的大小
     */
    public synchronized long getLogFileMaxSize() {
        return logFileMaxSize;
    }

    /**
     * 设置单个文件最大容量限制.
     *
     * @param logFileMaxSize 设置的最大值
     * @return LoggerConfigurator
     */
    public synchronized LoggerConfigurator setLogFileMaxSize(long logFileMaxSize) {
        this.logFileMaxSize = logFileMaxSize;
        return this;
    }

    /**
     * 获取当前设置的可以上传日志的网络类型.
     * 
     * @return 网络类型
     */
    public synchronized int[] getUploadNetworkType() {
        return uploadNetworkType;
    }

    /**
     * 设置可以上传日志的网络类型.
     *
     * @param uploadNetworkType 设置的网络类型
     * @return LoggerConfigurator
     */
    public synchronized LoggerConfigurator setUploadNetworkType(int... uploadNetworkType) {
        this.uploadNetworkType = uploadNetworkType;
        return this;
    }

    /**
     * 获取当前设置的需要上传的日志级别.
     *
     * @return 设置的级别
     */
    public synchronized int[] getUploadLogLevel() {
        return uploadLogLevel;
    }

    /**
     * 设置需要上传的日志级别.
     *
     * @param uploadLogLevel 日志级别
     * @return LoggerConfigurator
     */
    public synchronized LoggerConfigurator setUploadLogLevel(int... uploadLogLevel) {
        this.uploadLogLevel = uploadLogLevel;
        return this;
    }

    /**
     * 获取当前设置是否上传已经上传的日志文件.
     *
     * @return 是否删除
     */
    public synchronized boolean isDeleteUploadedLogFile() {
        return deleteUploadedLogFile;
    }

    /**
     * 设置是否上传已经上传的日志文件.
     *
     * @param deleteUploadedLogFile 是否删除
     * @return LoggerConfigurator
     */
    public synchronized LoggerConfigurator setDeleteUploadedLogFile(boolean deleteUploadedLogFile) {
        this.deleteUploadedLogFile = deleteUploadedLogFile;
        return this;
    }

    /**
     * 获取当前定时任务周期.
     *
     * @return 设置的周期（单位：秒）
     */
    public synchronized long getUpdateSystemFrequency() {
        return updateSystemFrequency;
    }

    /**
     * 设置定时任务周期.
     *
     * @param updateSystemFrequency 周期时间
     * @return LoggerConfigurator
     */
    public synchronized LoggerConfigurator setUpdateSystemFrequency(long updateSystemFrequency) {
        this.updateSystemFrequency = updateSystemFrequency;
        return this;
    }

    /**
     * 获取设置同时写入的日志文件数量.
     *
     * @return 设置的值
     */
    public synchronized int getActiveLogWriter() {
        return activeLogWriter;
    }

    /**
     * 设置同时写入的文件数量.
     *
     * @param activeLogWriter 文件数量
     * @return LoggerConfigurator
     */
    public synchronized LoggerConfigurator setActiveLogWriter(int activeLogWriter) {
        this.activeLogWriter = activeLogWriter;
        return this;
    }

    /**
     * 获取设置的同时上传的日志文件个数.
     *
     * @return 设置的值
     */
    public synchronized int getActiveUploadTask() {
        return activeUploadTask;
    }

    /**
     * 设置同时上传的日志文件个数.
     *
     * @param activeUploadTask 文件数量
     * @return LoggerConfigurator
     */
    public synchronized LoggerConfigurator setActiveUploadTask(int activeUploadTask) {
        this.activeUploadTask = activeUploadTask;
        return this;
    }

    /**
     * 获取当前是否自动捕捉异常.
     *
     * @return 是否自动捕捉异常
     */
    public synchronized boolean isCaughtException() {
        return caughtException;
    }

    /**
     * 设置时候自动捕捉异常.
     *
     * @param caughtException 设置的值
     * @return LoggerConfigurator
     */
    public synchronized LoggerConfigurator setCaughtException(boolean caughtException) {
        this.caughtException = caughtException;
        return this;
    }

    /**
     * 获取设置的默认 logger 名称.
     *
     * @return Logger 名称
     */
    public String getDefaultLoggerName() {
        return defaultLoggerName;
    }

    /**
     * 设置默认的 logger 名称.
     *
     * @param defaultLoggerName Logger 名称
     */
    public void setDefaultLoggerName(String defaultLoggerName) {
        this.defaultLoggerName = defaultLoggerName;
    }

    /**
     * 获取默认的消息模板.
     *
     * @return 消息模板内容
     */
    public String getDefaultMsgLayout() {
        return defaultMsgLayout;
    }

    /**
     * 设置默认的消息模板.
     *
     * @param defaultMsgLayout 消息模板内容
     */
    public void setDefaultMsgLayout(String defaultMsgLayout) {
        this.defaultMsgLayout = defaultMsgLayout;
    }

    public int getScreenshotQuality() {
        if (screenshotQuality >= 1 && screenshotQuality <= 100) {
            return screenshotQuality;
        }
        return LoggerConstants.DEFAULT_SCREENSHOT_QUALITY;
    }

    public void setScreenshotQuality(int screenshotQuality) {
        this.screenshotQuality = screenshotQuality;
    }

    public float getScreenshotScale() {
        if (screenshotScale >= 0.1 && screenshotScale <= 1) {
            return screenshotScale;
        }
        return LoggerConstants.DEFAULT_SCREENSHOT_SCALE;
    }

    public void setScreenshotScale(float screenshotScale) {
        this.screenshotScale = screenshotScale;
    }
}
