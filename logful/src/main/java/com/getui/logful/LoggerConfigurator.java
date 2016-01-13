package com.getui.logful;


import com.getui.logful.security.DefaultSecurityProvider;
import com.getui.logful.security.SecurityProvider;
import com.getui.logful.util.LogUtil;

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

    private SecurityProvider securityProvider;

    public static Builder newBuilder() {
        return new Builder();
    }

    public SecurityProvider getSecurityProvider() {
        return securityProvider;
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
     * 获取当前设置的可以上传日志的网络类型.
     *
     * @return 网络类型
     */
    public synchronized int[] getUploadNetworkType() {
        return uploadNetworkType;
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
     * 获取当前设置是否上传已经上传的日志文件.
     *
     * @return 是否删除
     */
    public synchronized boolean isDeleteUploadedLogFile() {
        return deleteUploadedLogFile;
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
     * 获取设置同时写入的日志文件数量.
     *
     * @return 设置的值
     */
    public synchronized int getActiveLogWriter() {
        return activeLogWriter;
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
     * 获取当前是否自动捕捉异常.
     *
     * @return 是否自动捕捉异常
     */
    public synchronized boolean isCaughtException() {
        return caughtException;
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
     * 获取默认的消息模板.
     *
     * @return 消息模板内容
     */
    public String getDefaultMsgLayout() {
        return defaultMsgLayout;
    }

    /**
     * 获取设置的截图压缩质量数值.
     *
     * @return 压缩数值
     */
    public int getScreenshotQuality() {
        return screenshotQuality;
    }

    /**
     * 获取设置的截图缩放比例.
     *
     * @return 缩放比例
     */
    public float getScreenshotScale() {
        return screenshotScale;
    }

    public static class Builder {
        private static final String TAG = "Builder";

        private long logFileMaxSize = LoggerConstants.DEFAULT_LOG_FILE_MAX_SIZE;
        private int[] uploadNetworkType = LoggerConstants.DEFAULT_UPLOAD_NETWORK_TYPE;
        private int[] uploadLogLevel = LoggerConstants.DEFAULT_UPLOAD_LOG_LEVEL;
        private boolean deleteUploadedLogFile = LoggerConstants.DEFAULT_DELETE_UPLOADED_LOG_FILE;
        private long updateSystemFrequency = LoggerConstants.DEFAULT_UPDATE_SYSTEM_FREQUENCY;
        private int activeUploadTask = LoggerConstants.DEFAULT_ACTIVE_UPLOAD_TASK;
        private int activeLogWriter = LoggerConstants.DEFAULT_ACTIVE_LOG_WRITER;
        private boolean caughtException = LoggerConstants.DEFAULT_CAUGHT_EXCEPTION;
        private String defaultLoggerName = LoggerConstants.DEFAULT_LOGGER_NAME;
        private String defaultMsgLayout = LoggerConstants.DEFAULT_MSG_LAYOUT;
        private int screenshotQuality = LoggerConstants.DEFAULT_SCREENSHOT_QUALITY;
        private float screenshotScale = LoggerConstants.DEFAULT_SCREENSHOT_SCALE;
        private SecurityProvider securityProvider = new DefaultSecurityProvider();

        /**
         * 设置单个文件最大容量限制.
         *
         * @param logFileMaxSize 设置的最大值
         * @return LoggerConfigurator
         */
        public Builder setLogFileMaxSize(long logFileMaxSize) {
            this.logFileMaxSize = logFileMaxSize;
            return this;
        }

        /**
         * 设置可以上传日志的网络类型.
         *
         * @param uploadNetworkType 设置的网络类型
         * @return LoggerConfigurator
         */
        public Builder setUploadNetworkType(int... uploadNetworkType) {
            this.uploadNetworkType = uploadNetworkType;
            return this;
        }

        /**
         * 设置需要上传的日志级别.
         *
         * @param uploadLogLevel 日志级别
         * @return LoggerConfigurator
         */
        public Builder setUploadLogLevel(int... uploadLogLevel) {
            this.uploadLogLevel = uploadLogLevel;
            return this;
        }

        /**
         * 设置是否上传已经上传的日志文件.
         *
         * @param deleteUploadedLogFile 是否删除
         * @return LoggerConfigurator
         */
        public Builder setDeleteUploadedLogFile(boolean deleteUploadedLogFile) {
            this.deleteUploadedLogFile = deleteUploadedLogFile;
            return this;
        }

        /**
         * 设置定时任务周期.
         *
         * @param updateSystemFrequency 周期时间
         * @return LoggerConfigurator
         */
        public Builder setUpdateSystemFrequency(long updateSystemFrequency) {
            this.updateSystemFrequency = updateSystemFrequency;
            return this;
        }

        /**
         * 设置同时写入的文件数量.
         *
         * @param activeLogWriter 文件数量
         * @return LoggerConfigurator
         */
        public Builder setActiveLogWriter(int activeLogWriter) {
            this.activeLogWriter = activeLogWriter;
            return this;
        }

        /**
         * 设置同时上传的日志文件个数.
         *
         * @param activeUploadTask 文件数量
         * @return LoggerConfigurator
         */
        public Builder setActiveUploadTask(int activeUploadTask) {
            this.activeUploadTask = activeUploadTask;
            return this;
        }

        /**
         * 设置时候自动捕捉异常.
         *
         * @param caughtException 设置的值
         * @return LoggerConfigurator
         */
        public Builder setCaughtException(boolean caughtException) {
            this.caughtException = caughtException;
            return this;
        }

        /**
         * 设置默认的消息模板.
         *
         * @param defaultMsgLayout 消息模板内容
         */
        public Builder setDefaultMsgLayout(String defaultMsgLayout) {
            this.defaultMsgLayout = defaultMsgLayout;
            return this;
        }

        /**
         * 设置默认的 logger 名称.
         *
         * @param defaultLoggerName Logger 名称
         */
        public Builder setDefaultLoggerName(String defaultLoggerName) {
            this.defaultLoggerName = defaultLoggerName;
            return this;
        }

        /**
         * 设置截图压缩质量.
         *
         * @param screenshotQuality 压缩数值
         */
        public Builder setScreenshotQuality(int screenshotQuality) {
            if (screenshotQuality >= 1 && screenshotQuality <= 100) {
                this.screenshotQuality = screenshotQuality;
            } else {
                LogUtil.w(TAG, "Screenshot quality value must be set 1 ~ 100.");
            }
            return this;
        }

        /**
         * 设置截图缩放比例.
         *
         * @param screenshotScale 缩放比例
         */
        public Builder setScreenshotScale(float screenshotScale) {
            if (screenshotScale >= 0.1 && screenshotScale <= 1) {
                this.screenshotScale = screenshotScale;
            } else {
                LogUtil.w(TAG, "Screenshot scale value must be set 0.1 ~ 1.0.");
            }
            return this;
        }

        public Builder setSecurityProvider(SecurityProvider newProvider) {
            this.securityProvider = newProvider;
            return this;
        }

        public LoggerConfigurator build() {
            LoggerConfigurator configurator = new LoggerConfigurator();
            configurator.logFileMaxSize = this.logFileMaxSize;
            configurator.uploadNetworkType = this.uploadNetworkType;
            configurator.uploadLogLevel = this.uploadLogLevel;
            configurator.updateSystemFrequency = this.updateSystemFrequency;
            configurator.activeUploadTask = this.activeUploadTask;
            configurator.activeLogWriter = this.activeLogWriter;
            configurator.deleteUploadedLogFile = this.deleteUploadedLogFile;
            configurator.caughtException = this.caughtException;
            configurator.defaultLoggerName = this.defaultLoggerName;
            configurator.defaultMsgLayout = this.defaultMsgLayout;
            configurator.screenshotQuality = this.screenshotQuality;
            configurator.screenshotScale = this.screenshotScale;
            configurator.securityProvider = this.securityProvider;
            return configurator;
        }
    }
}
