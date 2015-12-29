package com.getui.logful;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.getui.logful.annotation.LogProperties;
import com.getui.logful.appender.AsyncAppenderManager;
import com.getui.logful.db.DatabaseManager;
import com.getui.logful.exception.ExceptionReporter;
import com.getui.logful.net.ClientUserInitService;
import com.getui.logful.net.TransferManager;
import com.getui.logful.util.LogUtil;
import com.getui.logful.util.ParsePassThroughData;
import com.getui.logful.util.StringUtils;
import com.getui.logful.util.SystemConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoggerFactory {

    private static final String TAG = "LoggerFactory";

    private static Application loggerApplication;

    private static Context loggerContext;

    private static LoggerConfigurator loggerConfiguration;

    private static final Lock lock = new ReentrantLock();

    /**
     * 是否已初始化.
     */
    private static boolean initialized = false;

    private static boolean DEBUG = true;

    private static final ConcurrentHashMap<String, Logger> loggerCache = new ConcurrentHashMap<String, Logger>();

    /**
     * LoggerFactory init.
     *
     * @param application Application
     */
    public static void init(Application application) {
        LoggerFactory.loggerApplication = application;
        LoggerFactory.init(application.getApplicationContext());
    }

    /**
     * LoggerFactory init.
     *
     * @param application   Application
     * @param configuration LoggerConfigurator
     */
    public static void init(Application application, LoggerConfigurator configuration) {
        LoggerFactory.loggerApplication = application;

        String annotationLoggerName = null;
        String annotationMsgLayout = null;

        // 读取注解设置内容.
        Class<?> applicationClass = application.getClass();
        LogProperties logProperties = applicationClass.getAnnotation(LogProperties.class);
        if (logProperties != null) {
            annotationLoggerName = logProperties.defaultLogger();
            annotationMsgLayout = logProperties.defaultMsgLayout();
        }

        // 获取 LoggerConfigurator 设置内容.
        String loggerName = configuration.getDefaultLoggerName();
        String msgLayout = configuration.getDefaultMsgLayout();

        // 优先读取 config 设置的值.
        if (StringUtils.isEmpty(loggerName)) {
            if (!StringUtils.isEmpty(annotationLoggerName)) {
                configuration.setDefaultLoggerName(annotationLoggerName);
            } else {
                configuration.setDefaultLoggerName(LoggerConstants.DEFAULT_LOGGER_NAME);
            }
        }

        if (StringUtils.isEmpty(msgLayout)) {
            if (!StringUtils.isEmpty(annotationMsgLayout)) {
                configuration.setDefaultMsgLayout(annotationMsgLayout);
            } else {
                configuration.setDefaultMsgLayout(LoggerConstants.DEFAULT_MSG_LAYOUT);
            }
        }

        LoggerFactory.init(application.getApplicationContext(), configuration);
    }

    public static void init(Context context) {
        LoggerConfigurator configuration = LoggerConfigurator.build();
        LoggerFactory.init(context.getApplicationContext(), configuration);
    }

    public static void init(Context context, LoggerConfigurator configuration) {
        lock.lock();

        if (LoggerFactory.initialized) {
            lock.unlock();
            LogUtil.w(TAG, "LoggerFactory can only initialize once.");
            return;
        }

        LogUtil.DEBUG = LoggerFactory.DEBUG;

        LoggerFactory.loggerContext = context.getApplicationContext();
        LoggerFactory.loggerConfiguration = configuration;

        // 读取系统配置文件.
        SystemConfig.read();

        // 初始化数据库.
        DatabaseManager.manager();

        if (configuration.isCaughtException()) {
            // 捕捉未捕捉到的异常信息.
            ExceptionReporter.caught();
        }

        // 用户授权.
        // ClientAuthUtil.authenticate();
        ClientUserInitService.authenticate();

        LoggerFactory.initialized = true;

        lock.unlock();

        // Read pre log event cache.
        if (SystemConfig.isOn()) {
            AsyncAppenderManager.readCache();
        }
    }

    private static String defaultLoggerName() {
        if (loggerConfiguration == null) {
            return LoggerConstants.DEFAULT_LOGGER_NAME;
        }

        String loggerName = loggerConfiguration.getDefaultLoggerName();
        if (StringUtils.isEmpty(loggerName)) {
            return LoggerConstants.DEFAULT_LOGGER_NAME;
        }

        return loggerName;
    }

    private static String defaultMsgLayout() {
        if (loggerConfiguration == null) {
            return LoggerConstants.DEFAULT_MSG_LAYOUT;
        }

        String msgLayout = loggerConfiguration.getDefaultMsgLayout();
        if (StringUtils.isEmpty(msgLayout)) {
            return LoggerConstants.DEFAULT_MSG_LAYOUT;
        }

        return msgLayout;
    }

    public static Application application() {
        // TODO May be null
        if (loggerApplication != null) {
            return loggerApplication;
        }
        return null;
    }

    public static Context context() {
        return loggerContext;
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
            SystemConfig.saveStatus(true);

            // Read pre log event cache.
            AsyncAppenderManager.readCache();
        }
    }

    /**
     * 关闭日志记录.
     */
    public static void turnOffLog() {
        if (initialized) {
            SystemConfig.saveStatus(false);
        }
    }

    /**
     * 获取当前日志状态.
     *
     * @return 是否打开
     */
    public static boolean isOn() {
        return initialized && SystemConfig.isOn();
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
        }
    }

    /**
     * 解析推送控制日志动作链.
     *
     * @param transaction 动作链内容
     */
    public static void parseTransaction(String transaction) {
        if (initialized) {
            try {
                Log.d(TAG, "**************");
                ParsePassThroughData.parseData(transaction);
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
