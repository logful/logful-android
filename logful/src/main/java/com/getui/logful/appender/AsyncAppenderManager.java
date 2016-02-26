package com.getui.logful.appender;

import com.getui.logful.LoggerConfigurator;
import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;
import com.getui.logful.db.DatabaseManager;
import com.getui.logful.entity.LogFileMeta;
import com.getui.logful.layout.BinaryLayout;
import com.getui.logful.util.BoundedPriorityBlockingQueue;
import com.getui.logful.util.DateTimeUtils;
import com.getui.logful.util.LogStorage;
import com.getui.logful.util.LogUtil;
import com.getui.logful.util.LruCache;

import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncAppenderManager {

    private static final String TAG = "AsyncAppenderManager";

    private static final int DEFAULT_QUEUE_CAPACITY = 5120;
    private static final int START_LOG_FILE_FRAGMENT = 1;
    private static final int LOG_FILE_FRAGMENT_STEP = 1;

    private final BoundedPriorityBlockingQueue<LogEvent> logEventQueue;
    private final ConcurrentHashMap<String, Appender> appenderMap = new ConcurrentHashMap<String, Appender>();
    private boolean interrupted;
    private ExecutorService releaseExecutor;
    private ThreadPoolExecutor appenderExecutor;
    private LruCache<String, LogEvent> logEventCache = new LruCache<String, LogEvent>(100);

    private static class ClassHolder {
        static AsyncAppenderManager manager = new AsyncAppenderManager();
    }

    public static AsyncAppenderManager manager() {
        return ClassHolder.manager;
    }

    public AsyncAppenderManager() {
        this.interrupted = false;
        this.logEventQueue = new BoundedPriorityBlockingQueue<LogEvent>(DEFAULT_QUEUE_CAPACITY, new QueuePriorityComparator());
    }

    /**
     * Append LogEvent wait if queue is full.
     *
     * @param logEvent LogEvent
     */
    public void append(LogEvent logEvent) {
        if (LoggerFactory.isOn()) {
            // LoggerFactory initialized.
            LoggerConfigurator config = LoggerFactory.config();
            if (config == null) {
                return;
            }

            submitWriteTask();

            try {
                logEventQueue.put(logEvent);
            } catch (InterruptedException e) {
                LogUtil.e(TAG, "", e);
            }
        } else {
            logEventCache.put(UUID.randomUUID().toString(), logEvent);
            LogUtil.w(TAG, "LoggerFactory is not on, pre log event will be cached.");
        }
    }

    public static void readCache() {
        AsyncAppenderManager manager = manager();
        manager.submitWriteTask();

        LogUtil.d(TAG, "Start read pre log event from cache.");
        Map<String, LogEvent> snapshot = manager.logEventCache.snapshot();
        LogUtil.d(TAG, snapshot.size() + " cached log event.");
        for (String key : snapshot.keySet()) {
            LogEvent logEvent = snapshot.get(key);
            if (logEvent != null) {
                try {
                    manager.logEventQueue.put(logEvent);
                } catch (InterruptedException e) {
                    LogUtil.e(TAG, "", e);
                }
            }
        }
        manager.logEventCache.evictAll();
    }

    public static void interrupt() {
        AsyncAppenderManager manager = manager();
        manager.closeAllAppender();
    }

    private synchronized void closeAllAppender() {
        interrupted = true;

        ConcurrentHashMap<String, Appender> temp = new ConcurrentHashMap<String, Appender>();
        temp.putAll(appenderMap);
        appenderMap.clear();

        for (Map.Entry<String, Appender> entry : temp.entrySet()) {
            Appender appender = entry.getValue();
            getReleaseExecutor().execute(new ReleaseAppender(appender));
        }

        DatabaseManager.closeAllFile();

        interrupted = false;
    }

    private ExecutorService getReleaseExecutor() {
        if (releaseExecutor == null || releaseExecutor.isTerminated()) {
            releaseExecutor = Executors.newSingleThreadExecutor();
        }
        return releaseExecutor;
    }

    private ThreadPoolExecutor getAppenderExecutor() {
        if (appenderExecutor == null || appenderExecutor.isTerminated()) {
            LoggerConfigurator config = LoggerFactory.config();
            int threadSize;
            if (config != null) {
                threadSize = config.getActiveLogWriter();
            } else {
                threadSize = LoggerConstants.DEFAULT_ACTIVE_LOG_WRITER;
            }
            appenderExecutor =
                    new ThreadPoolExecutor(threadSize, threadSize, 0L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<Runnable>());
        }
        return appenderExecutor;
    }

    private synchronized void submitWriteTask() {
        if (getAppenderExecutor().getActiveCount() == 0) {
            int writerCount;
            LoggerConfigurator config = LoggerFactory.config();
            if (config != null) {
                writerCount = config.getActiveLogWriter();
            } else {
                writerCount = LoggerConstants.DEFAULT_ACTIVE_LOG_WRITER;
            }

            for (int i = 0; i < writerCount; i++) {
                getAppenderExecutor().submit(new LogEventRunnable());
            }
        }
    }

    /**
     * 根据 Log event 获取当前的日志文件 appender.
     *
     * @param logEvent Log event
     * @return 日志文件 appender
     */
    private synchronized Appender appender(LogEvent logEvent) {
        String key = appenderKey(logEvent);
        Appender appender = appenderMap.get(key);
        if (appender == null) {
            // 创建新的日志文件 appender.
            appender = newAppender(logEvent);
            if (appender != null) {
                appenderMap.put(key, appender);
            }
        } else {
            // 判断日志文件是否已经达到最大容量.
            if (!appender.writeable()) {
                // 日志文件已达到最大容量.

                // 1. 停止写入并关闭日志文件.
                // Safe close.
                getReleaseExecutor().execute(new ReleaseAppender(appender));

                // 2. 更新日志文件 meta 信息到数据库.
                DatabaseManager.closeLogFile(logEvent.getLoggerName(), logEvent.getLevel(), appender.fragment());

                // 3. 从 map 中移除当前的 appender.
                appenderMap.remove(key);

                // 4. 创建新的日志文件 appender 并保存 meta 信息到数据库.
                int fragment = appender.fragment() + LOG_FILE_FRAGMENT_STEP;
                appender = createAppender(logEvent, null, fragment);

                // 5. 保存到 map 中.
                if (appender != null) {
                    appenderMap.put(key, appender);
                }
            }
        }
        return appender;
    }

    private synchronized Appender newAppender(LogEvent event) {
        Appender appender = null;
        int fragment;

        // 查找数据库中记录的最大序列数字.
        LogFileMeta meta = DatabaseManager.findMaxFragment(event);
        if (meta != null) {
            fragment = meta.getFragment() + LOG_FILE_FRAGMENT_STEP;

            // 日志文件已添加 eof 标记.
            if (meta.isEof()) {
                // 创建新的日志文件 appender.
                appender = createAppender(event, null, fragment);
            } else {
                // 判断日志文件是否已被删除.
                String filePath = LogStorage.readableLogFilePath(meta);
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    // 日志文件未被删除.
                    LoggerConfigurator config = LoggerFactory.config();
                    if (config != null) {
                        // 判断当前日志文件是否已经达到最大容量.
                        if (file.length() < config.getLogFileMaxSize()) {
                            // 日志文件未达到最大容量.
                            appender = createAppender(event, meta, meta.getFragment());
                        } else {
                            // 日志文件已经达到最大容量.

                            // 1. 更新日志文件 meta 信息到数据库.
                            meta.setStatus(LoggerConstants.STATE_WILL_UPLOAD);
                            meta.setEof(true);
                            DatabaseManager.saveLogFileMeta(meta);

                            // 2. 创建新的日志文件 appender.
                            appender = createAppender(event, null, fragment);
                        }
                    }
                } else {
                    // 日志文件已经被删除.

                    // 1. 更新数据库中的日志文件 meta 信息.
                    meta.setStatus(LoggerConstants.STATE_DELETED);
                    meta.setDeleteTime(System.currentTimeMillis());
                    meta.setEof(true);
                    DatabaseManager.saveLogFileMeta(meta);

                    // 2. 创建新的日志文件 appender.
                    appender = createAppender(event, null, fragment);
                }
            }
        } else {
            // 创建 fragment 为 START_LOG_FILE_FRAGMENT 的 日志文件 appender.
            fragment = START_LOG_FILE_FRAGMENT;
            appender = createAppender(event, null, fragment);
        }

        return appender;
    }

    private synchronized Appender createAppender(LogEvent event, LogFileMeta meta, int fragment) {
        String filename = generateFilename(event, fragment);

        String filePath;
        int location = 0;
        if (meta != null) {
            filePath = LogStorage.writableLogFilePath(meta);
        } else {
            String dirPath;
            if (LogStorage.writable()) {
                location = LoggerConstants.LOCATION_EXTERNAL;
                dirPath = LogStorage.externalLogDir();
            } else {
                location = LoggerConstants.LOCATION_INTERNAL;
                dirPath = LogStorage.internalLogDir();
            }
            if (dirPath == null) {
                LogUtil.e(TAG, "LogFileContext create file failed.");
            }

            filePath = String.format("%s/%s", dirPath, filename);
        }

        Appender appender = null;
        LoggerConfigurator config = LoggerFactory.config();
        if (config != null) {
            appender =
                    FileAppender.createAppender(event.getLoggerName(), filePath, new BinaryLayout(),
                            config.getLogFileMaxSize(), fragment);
            appender.setHandler(new AppendFailedHandler());

            if (meta == null) {
                // 保存新创建的日志文件 meta 信息到数据库.
                meta = LogFileMeta.createMeta(event, filename, fragment);
                meta.setLocation(location);
                DatabaseManager.saveLogFileMeta(meta);
            }
        }

        return appender;
    }

    private String appenderKey(LogEvent event) {
        return String.format("%s-%d", event.getLoggerName(), event.getLevel());
    }

    /**
     * 生成日志文件名称.
     *
     * @param event    LogEvent
     * @param fragment fragment
     * @return 日志文件名
     */
    private String generateFilename(LogEvent event, int fragment) {
        return String.format("%s-%s-%s-%d.bin", event.getLoggerName(), DateTimeUtils.dateString(),
                LoggerConstants.getLogLevelName(event.getLevel()), fragment);
    }

    /**
     * 队列排列顺序 优先级高的排前面 优先级相同的按照先入先出 FIFO 排序.
     */
    private class QueuePriorityComparator implements Comparator<LogEvent> {

        @Override
        public int compare(LogEvent lhs, LogEvent rhs) {
            if (lhs.getPriority() > rhs.getPriority()) {
                return -1;
            } else if (lhs.getPriority() < rhs.getPriority()) {
                return 1;
            } else if (lhs.getSequence() < rhs.getSequence()) {
                return -1;
            } else if (lhs.getSequence() > rhs.getSequence()) {
                return 1;
            }
            return 0;
        }
    }

    private class ReleaseAppender implements Runnable {

        private Appender appender;

        public ReleaseAppender(final Appender appender) {
            this.appender = appender;
        }

        @Override
        public void run() {
            if (appender != null) {
                while (true) {
                    if (!appender.writing()) {
                        appender.stop();
                        LogUtil.d(TAG, "Close appender with logger name: " + appender.getLoggerName() +
                                " fragment: " + appender.fragment());
                        break;
                    }
                }
            }
        }
    }

    private class LogEventRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                if (!interrupted) {
                    try {
                        LogEvent logEvent = logEventQueue.take();
                        Appender appender = AsyncAppenderManager.manager().appender(logEvent);
                        if (appender != null) {
                            appender.append(logEvent);
                        }
                    } catch (InterruptedException e) {
                        LogUtil.e("LogEventRunnable", "", e);
                    }
                } else {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        LogUtil.e("LogEventRunnable", "", e);
                    }
                }
            }
        }
    }

    /**
     * 日志写入错误 handler.
     */
    private class AppendFailedHandler implements ErrorHandler {

        @Override
        public void error(String msg) {
            LogUtil.e("AppendFailedHandler", msg);
        }

        @Override
        public void error(String msg, LogEvent event, Throwable throwable) {
            LogUtil.e("AppendFailedHandler", event.getLoggerName(), throwable);
        }

        @Override
        public void error(String msg, Throwable throwable) {
            LogUtil.e("AppendFailedHandler", "", throwable);
        }
    }
}
