package com.getui.logful;

public class LoggerConstants {

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

    /**
     * 日志文件存储文件夹名称.
     */
    public static final String LOG_DIR_NAME = "log";

    public static final String LIBRARY_FILE_NAME = "liblogful.so";

    /**
     * 崩溃日志记录文件名称.
     */
    public static final String CRASH_REPORT_FILE_PREFIX = "crash-report";

    /**
     * 崩溃日志文件存储文件夹.
     */
    public static final String CRASH_REPORT_DIR_NAME = "crash";

    public static final String ATTACHMENT_DIR_NAME = "attachment";

    public static final int DEFAULT_ACTIVE_UPLOAD_TASK = 2;

    public static final int DEFAULT_ACTIVE_LOG_WRITER = 2;

    public static final boolean DEFAULT_DELETE_UPLOADED_LOG_FILE = false;

    public static final boolean DEFAULT_CAUGHT_EXCEPTION = false;

    public static final int[] DEFAULT_UPLOAD_NETWORK_TYPE = new int[] {Constants.TYPE_WIFI, Constants.TYPE_MOBILE};

    public static final int[] DEFAULT_UPLOAD_LOG_LEVEL = new int[] {Constants.VERBOSE, Constants.DEBUG, Constants.INFO,
            Constants.WARN, Constants.ERROR, Constants.EXCEPTION, Constants.FATAL};

    public static final long DEFAULT_LOG_FILE_MAX_SIZE = 524288;

    public static final long DEFAULT_UPDATE_SYSTEM_FREQUENCY = 3600;

    public static final String DEFAULT_LOGGER_NAME = "app";

    public static final String DEFAULT_MSG_LAYOUT = "";

    public static final String API_BASE_URL = "http://192.168.14.198:9600";

    public static final String CLIENT_AUTH_URI = "/oauth/token";

    public static final String UPLOAD_SYSTEM_INFO_URI = "/log/info/upload";

    public static final String UPLOAD_LOG_FILE_URI = "/log/file/upload";

    public static final String UPLOAD_CRASH_REPORT_FILE_URI = "/log/crash/upload";

    public static final String UPLOAD_ATTACHMENT_FILE_URI = "/log/attachment/upload";

    public static final int DEFAULT_HTTP_REQUEST_TIMEOUT = 5000;

    public static final String CHARSET = "UTF-8";

    /**
     * 系统配置文件名称.
     */
    public static final String SYSTEM_CONFIG_FILE_NAME = "logful_system_config.bin";

    /**
     * 用户自定义配置.
     */
    public static final String USER_CONFIG_ASSET_FILE_NAME = "logful.properties";

    /**
     * 日志库当前版本.
     */
    public static final String VERSION = "1.0.0";

    public static final int STATE_ALL = 0x00;

    public static final int STATE_NORMAL = 0x01;

    public static final int STATE_WILL_UPLOAD = 0x02;

    public static final int STATE_UPLOADED = 0x03;

    public static final int STATE_DELETED = 0x04;

    public static final int LOCATION_EXTERNAL = 0x01;

    public static final int LOCATION_INTERNAL = 0x02;

    public static final int DEFAULT_SCREENSHOT_QUALITY = 80;

    public static final float DEFAULT_SCREENSHOT_SCALE = 0.5f;

    public static final String APP_KEY = "525b8747323d49078a96e49f0189de98";

    public static final String APP_SECRET = "02ce8e2adba94ae5a4807e3f12ea34f3";
}
