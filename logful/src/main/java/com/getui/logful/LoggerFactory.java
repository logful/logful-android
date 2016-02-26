package com.getui.logful;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.getui.logful.annotation.LogProperties;
import com.getui.logful.appender.AsyncAppenderManager;
import com.getui.logful.config.LogfulConfigurer;
import com.getui.logful.crash.CrashReporter;
import com.getui.logful.db.DatabaseManager;
import com.getui.logful.net.ClientUserInitService;
import com.getui.logful.net.TransferManager;
import com.getui.logful.util.LogUtil;
import com.getui.logful.util.StringUtils;
import com.getui.logful.util.SystemConfig;

import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoggerFactory {

    private static final String TAG = "LoggerFactory";

    private static Application loggerApplication;

    private static Context loggerContext;

    private static LoggerConfigurator loggerConfiguration;

    private static final Lock lock = new ReentrantLock();

    private static String annotationLoggerName;

    private static String annotationMsgLayout;

    private static boolean initialized = false;

    private static boolean DEBUG = false;

    private static final ConcurrentHashMap<String, Logger> loggerCache = new ConcurrentHashMap<String, Logger>();

    /**
     * LoggerFactory init.
     *
     * @param activity Activity
     */
    public static void init(Activity activity) {
        LoggerFactory.init(activity.getApplication());
    }

    /**
     * LoggerFactory init.
     *
     * @param activity      Activity
     * @param configuration LoggerConfigurator
     */
    public static void init(Activity activity, LoggerConfigurator configuration) {
        LoggerFactory.init(activity.getApplication(), configuration);
    }

    /**
     * LoggerFactory init.
     *
     * @param application Application
     */
    public static void init(Application application) {
        LoggerConfigurator configuration = LoggerConfigurator.newBuilder().build();
        LoggerFactory.init(application, configuration);
    }

    /**
     * LoggerFactory init.
     *
     * @param application   Application
     * @param configuration LoggerConfigurator
     */
    public static void init(Application application, LoggerConfigurator configuration) {
        lock.lock();

        if (LoggerFactory.initialized) {
            lock.unlock();
            LogUtil.w(TAG, "LoggerFactory can only initialize once.");
            return;
        }

        Class<?> applicationClass = application.getClass();
        LogProperties logProperties = applicationClass.getAnnotation(LogProperties.class);
        if (logProperties != null) {
            annotationLoggerName = logProperties.defaultLogger();
            annotationMsgLayout = logProperties.defaultMsgLayout();
        }

        LoggerFactory.loggerApplication = application;
        LoggerFactory.loggerContext = application.getApplicationContext();
        LoggerFactory.loggerConfiguration = configuration;

        // 初始化数据库.
        DatabaseManager.manager();

        // 读取存储的配置信息.
        LogfulConfigurer.config().setFrequency(configuration.getUpdateSystemFrequency(), false, true);

        if (configuration.isCaughtException()) {
            // 捕捉未捕捉到的异常信息.
            CrashReporter.caught();
        }

        // 用户授权.
        ClientUserInitService.authenticate();

        LoggerFactory.initialized = true;

        // 读取缓存日志.
        if (LogfulConfigurer.config().on()) {
            AsyncAppenderManager.readCache();
        }

        lock.unlock();
    }

    private static String defaultLoggerName() {
        if (loggerConfiguration != null
                && !StringUtils.isEmpty(loggerConfiguration.getDefaultLoggerName())) {
            return loggerConfiguration.getDefaultLoggerName();
        } else {
            if (!StringUtils.isEmpty(annotationLoggerName)) {
                return annotationLoggerName;
            }
        }
        return LoggerConstants.DEFAULT_LOGGER_NAME;
    }

    private static String defaultMsgLayout() {
        if (loggerConfiguration != null
                && !StringUtils.isEmpty(loggerConfiguration.getDefaultMsgLayout())) {
            return loggerConfiguration.getDefaultMsgLayout();
        } else {
            if (!StringUtils.isEmpty(annotationMsgLayout)) {
                return annotationMsgLayout;
            }
        }
        return LoggerConstants.DEFAULT_MSG_LAYOUT;
    }

    public static Application application() {
        if (loggerApplication != null) {
            return loggerApplication;
        }
        return null;
    }

    public static Context context() {
        return loggerContext;
    }

    public static void setDebug(boolean debug) {
        LoggerFactory.DEBUG = debug;
    }

    public static boolean isDebug() {
        return LoggerFactory.DEBUG;
    }

    /**
     * Set api base url.
     *
     * @param apiUrl Api base url string
     */
    public static void setApiUrl(String apiUrl) {
        SystemConfig.saveBaseUrl(apiUrl);
    }

    /**
     * Set app key.
     *
     * @param appKey App key string
     */
    public static void setAppKey(String appKey) {
        SystemConfig.saveAppKey(appKey);
    }

    /**
     * Set app secret.
     *
     * @param appSecret App secret string
     */
    public static void setAppSecret(String appSecret) {
        SystemConfig.saveAppSecret(appSecret);
    }

    /**
     * 获取当前日志库版本.
     *
     * @return version string
     */
    public static String version() {
        return LoggerConstants.VERSION;
    }

    public static LoggerConfigurator config() {
        return loggerConfiguration;
    }

    /**
     * 根据名称获取 logger.
     *
     * @param loggerName Logger name
     * @return Logger instance
     */
    public static Logger logger(String loggerName) {
        Logger logger = loggerCache.get(loggerName);
        if (logger == null) {
            logger = new DefaultLogger(loggerName);
            // Set default msg layout
            logger.setMsgLayout(LoggerFactory.defaultMsgLayout());
            loggerCache.put(logger.getName(), logger);
        }
        return logger;
    }

    /**
     * 绑定用户别名.
     *
     * @param alias User alias
     */
    public static void bindAlias(String alias) {
        if (initialized) {
            SystemConfig.saveAlias(alias);
        }
    }

    /**
     * 打开日志记录.
     */
    public static void turnOnLog() {
        if (initialized) {
            LogfulConfigurer.config().setOn(true, true, true);
            // Read pre log event cache.
            AsyncAppenderManager.readCache();
        }
    }

    /**
     * 关闭日志记录.
     */
    public static void turnOffLog() {
        if (initialized) {
            LogfulConfigurer.config().setOn(false, true, true);
        }
    }

    /**
     * 获取当前日志状态.
     *
     * @return 是否打开
     */
    public static boolean isOn() {
        return initialized && LogfulConfigurer.config().on();
    }

    /**
     * 同步日志文件.
     */
    public static void syncLog() {
        if (initialized) {
            TransferManager.uploadLogFile();
            TransferManager.uploadAttachment();
        }
    }

    /**
     * 同步日志文件.
     *
     * @param startTime 需要同步的日志文件开始记录时间
     * @param endTime   需要同步的日志文件结束记录时间
     */
    public static void syncLog(long startTime, long endTime) {
        if (initialized) {
            TransferManager.uploadLogFile(startTime, endTime);
            TransferManager.uploadAttachment();
        }
    }

    /**
     * 中断当前所有正在写入的日志文件并立即上传.
     */
    public static void interruptThenSync() {
        if (initialized) {
            AsyncAppenderManager.interrupt();
            TransferManager.uploadLogFile();
            TransferManager.uploadAttachment();
            TransferManager.uploadCrashReport();
        }
    }

    /**
     * 解析推送控制日志动作链.
     *
     * @param data 动作链内容
     */
    public static void parseTransmission(String data) {
        LogUtil.i(TAG, "Receive payload: " + data);
        if (initialized) {
            try {
                JSONObject object = new JSONObject(data);
                if (object.has("logful")) {
                    LogfulConfigurer.config().parse(object.getJSONObject("logful"), true, true);
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "", e);
            }
        }
    }

    /**
     * 使用默认的 logger 打印 verbose 信息.
     *
     * @param tag Tag
     * @param msg Message
     */
    public static void verbose(String tag, String msg) {
        Logger defaultLogger = LoggerFactory.logger(LoggerFactory.defaultLoggerName());
        defaultLogger.verbose(tag, msg);
    }

    /**
     * 使用默认的 logger 打印 verbose 信息.
     *
     * @param tag     Tag
     * @param msg     Message
     * @param capture Capture screen
     */
    public static void verbose(String tag, String msg, boolean capture) {
        Logger defaultLogger = LoggerFactory.logger(LoggerFactory.defaultLoggerName());
        defaultLogger.verbose(tag, msg, capture);
    }

    /**
     * 使用默认的 logger 打印 debug 信息.
     *
     * @param tag Tag
     * @param msg Message
     */
    public static void debug(String tag, String msg) {
        Logger defaultLogger = LoggerFactory.logger(LoggerFactory.defaultLoggerName());
        defaultLogger.debug(tag, msg);
    }

    /**
     * 使用默认的 logger 打印 debug 信息.
     *
     * @param tag     Tag
     * @param msg     Message
     * @param capture Capture screen
     */
    public static void debug(String tag, String msg, boolean capture) {
        Logger defaultLogger = LoggerFactory.logger(LoggerFactory.defaultLoggerName());
        defaultLogger.debug(tag, msg, capture);
    }

    /**
     * 使用默认的 logger 打印 info 信息.
     *
     * @param tag Tag
     * @param msg Message
     */
    public static void info(String tag, String msg) {
        Logger defaultLogger = LoggerFactory.logger(LoggerFactory.defaultLoggerName());
        defaultLogger.info(tag, msg);
    }

    /**
     * 使用默认的 logger 打印 info 信息.
     *
     * @param tag     Tag
     * @param msg     Message
     * @param capture Capture screen
     */
    public static void info(String tag, String msg, boolean capture) {
        Logger defaultLogger = LoggerFactory.logger(LoggerFactory.defaultLoggerName());
        defaultLogger.info(tag, msg, capture);
    }

    /**
     * 使用默认的 logger 打印 warn 信息.
     *
     * @param tag Tag
     * @param msg Message
     */
    public static void warn(String tag, String msg) {
        Logger defaultLogger = LoggerFactory.logger(LoggerFactory.defaultLoggerName());
        defaultLogger.warn(tag, msg);
    }

    /**
     * 使用默认的 logger 打印 warn 信息.
     *
     * @param tag     Tag
     * @param msg     Message
     * @param capture Capture screen
     */
    public static void warn(String tag, String msg, boolean capture) {
        Logger defaultLogger = LoggerFactory.logger(LoggerFactory.defaultLoggerName());
        defaultLogger.warn(tag, msg, capture);
    }

    /**
     * 使用默认的 logger 打印 error 信息.
     *
     * @param tag Tag
     * @param msg Message
     */
    public static void error(String tag, String msg) {
        Logger defaultLogger = LoggerFactory.logger(LoggerFactory.defaultLoggerName());
        defaultLogger.error(tag, msg);
    }

    /**
     * 使用默认的 logger 打印 error 信息.
     *
     * @param tag     Tag
     * @param msg     Message
     * @param capture Capture screen
     */
    public static void error(String tag, String msg, boolean capture) {
        Logger defaultLogger = LoggerFactory.logger(LoggerFactory.defaultLoggerName());
        defaultLogger.error(tag, msg, capture);
    }

    /**
     * 使用默认的 logger 打印 exception 信息.
     *
     * @param tag       Tag
     * @param msg       Message
     * @param throwable Throwable
     */
    public static void exception(String tag, String msg, Throwable throwable) {
        Logger defaultLogger = LoggerFactory.logger(LoggerFactory.defaultLoggerName());
        defaultLogger.exception(tag, msg, throwable);
    }

    /**
     * 使用默认的 logger 打印 exception 信息.
     *
     * @param tag       Tag
     * @param msg       Message
     * @param throwable Throwable
     * @param capture   Capture screen
     */
    public static void exception(String tag, String msg, Throwable throwable, boolean capture) {
        Logger defaultLogger = LoggerFactory.logger(LoggerFactory.defaultLoggerName());
        defaultLogger.exception(tag, msg, throwable, capture);
    }

    /**
     * 使用默认的 logger 打印 fatal 信息.
     *
     * @param tag Tag
     * @param msg Message
     */
    public static void fatal(String tag, String msg) {
        Logger defaultLogger = LoggerFactory.logger(LoggerFactory.defaultLoggerName());
        defaultLogger.fatal(tag, msg);
    }

    /**
     * 使用默认的 logger 打印 fatal 信息.
     *
     * @param tag     Tag
     * @param msg     Message
     * @param capture Capture screen
     */
    public static void fatal(String tag, String msg, boolean capture) {
        Logger defaultLogger = LoggerFactory.logger(LoggerFactory.defaultLoggerName());
        defaultLogger.fatal(tag, msg, capture);
    }
}
