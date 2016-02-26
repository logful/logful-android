package com.getui.logful;

public class LoggerConstants {

    public static final String DATABASE_NAME = "logful.db";

    /**
     * 日志文件存储文件夹名称.
     */
    public static final String LOG_DIR_NAME = "log";

    /**
     * 崩溃日志文件存储文件夹.
     */
    public static final String CRASH_REPORT_DIR_NAME = "crash";

    /**
     * 附件截图文件夹名称.
     */
    public static final String ATTACHMENT_DIR_NAME = "attachment";

    /**
     * 本地配置文件名称.
     */
    public static final String CONFIG_FILE_NAME = "logful.config";

    public static final String VERBOSE_NAME = "verbose";

    public static final String DEBUG_NAME = "debug";

    public static final String INFO_NAME = "info";

    public static final String WARN_NAME = "warn";

    public static final String ERROR_NAME = "error";

    public static final String EXCEPTION_NAME = "exception";

    public static final String FATAL_NAME = "fatal";

    public static String getLogLevelName(int level) {
        switch (level) {
            case Constants.VERBOSE:
                return VERBOSE_NAME;
            case Constants.DEBUG:
                return DEBUG_NAME;
            case Constants.INFO:
                return INFO_NAME;
            case Constants.WARN:
                return WARN_NAME;
            case Constants.ERROR:
                return ERROR_NAME;
            case Constants.EXCEPTION:
                return EXCEPTION_NAME;
            case Constants.FATAL:
                return FATAL_NAME;
            default:
                return VERBOSE_NAME;
        }
    }

    public static final int DEFAULT_ACTIVE_UPLOAD_TASK = 2;

    public static final int DEFAULT_ACTIVE_LOG_WRITER = 2;

    public static final boolean DEFAULT_DELETE_UPLOADED_LOG_FILE = false;

    public static final boolean DEFAULT_CAUGHT_EXCEPTION = false;

    public static final int[] DEFAULT_UPLOAD_NETWORK_TYPE = {Constants.TYPE_WIFI, Constants.TYPE_MOBILE};

    public static final int[] DEFAULT_UPLOAD_LOG_LEVEL = {Constants.VERBOSE, Constants.DEBUG, Constants.INFO,
            Constants.WARN, Constants.ERROR, Constants.EXCEPTION, Constants.FATAL};

    public static final long DEFAULT_LOG_FILE_MAX_SIZE = 524288;

    public static final long DEFAULT_UPDATE_SYSTEM_FREQUENCY = 3600;

    public static final String DEFAULT_LOGGER_NAME = "app";

    public static final String DEFAULT_MSG_LAYOUT = "";

    public static final String API_BASE_URL = "http://demo.logful.aoapp.com:9600";

    public static final String CLIENT_AUTH_URI = "/oauth/token";

    public static final String UPLOAD_USER_INFO_URI = "/log/info/upload";

    public static final String BIND_DEVICE_ID_URI = "/log/bind";

    public static final String UPLOAD_LOG_FILE_URI = "/log/file/upload";

    public static final String UPLOAD_CRASH_REPORT_FILE_URI = "/log/crash/upload";

    public static final String UPLOAD_ATTACHMENT_FILE_URI = "/log/attachment/upload";

    public static final int DEFAULT_HTTP_REQUEST_TIMEOUT = 6000;

    public static final String CHARSET = "UTF-8";

    public static final String VERSION = "0.3.0";

    public static final int STATE_ALL = 0x00;

    public static final int STATE_NORMAL = 0x01;

    public static final int STATE_WILL_UPLOAD = 0x02;

    public static final int STATE_UPLOADED = 0x03;

    public static final int STATE_DELETED = 0x04;

    public static final int LOCATION_EXTERNAL = 0x01;

    public static final int LOCATION_INTERNAL = 0x02;

    public static final int DEFAULT_SCREENSHOT_QUALITY = 80;

    public static final float DEFAULT_SCREENSHOT_SCALE = 0.5f;

    public static final boolean DEFAULT_USE_NATIVE_CRYPTOR = true;

    public static final int PLATFORM_ANDROID = 1;

    public static final String QUERY_PARAM_SDK_VERSION = "sdk";

    public static final String QUERY_PARAM_PLATFORM = "platform";

    public static final String QUERY_PARAM_UID = "uid";
}
