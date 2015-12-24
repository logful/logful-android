package com.getui.logful.net;

import com.getui.logful.LoggerConfigurator;
import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;
import com.getui.logful.db.DatabaseManager;
import com.getui.logful.entity.AttachmentFileMeta;
import com.getui.logful.entity.CrashReportFileMeta;
import com.getui.logful.entity.LogFileMeta;
import com.getui.logful.util.ConnectivityState;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TransferManager {

    private static ThreadPoolExecutor executor;
    private final ConcurrentLinkedQueue<UploadEvent> queue;

    private static class ClassHolder {
        static TransferManager manager = new TransferManager();
    }

    public static TransferManager manager() {
        return ClassHolder.manager;
    }

    public TransferManager() {
        this.queue = new ConcurrentLinkedQueue<UploadEvent>();
    }

    private ThreadPoolExecutor getExecutor() {
        if (executor == null || executor.isTerminated()) {
            LoggerConfigurator config = LoggerFactory.config();
            int threadSize;
            if (config != null) {
                threadSize = config.getActiveUploadTask();
            } else {
                threadSize = LoggerConstants.DEFAULT_ACTIVE_UPLOAD_TASK;
            }
            executor =
                    new ThreadPoolExecutor(threadSize, threadSize, 5, TimeUnit.SECONDS,
                            new ArrayBlockingQueue<Runnable>(1000));
        }
        return executor;
    }

    /**
     * 上传指定 level 的日志文件.
     */
    public static void uploadLogFile() {
        if (!ConnectivityState.shouldUpload()) {
            return;
        }

        TransferManager manager = manager();
        LoggerConfigurator config = LoggerFactory.config();
        if (config == null) {
            return;
        }
        int[] levels = config.getUploadLogLevel();
        List<LogFileMeta> metaList =
                DatabaseManager.findAllLogFileMetaByLevel(levels, LoggerConstants.STATE_WILL_UPLOAD);

        manager.uploadLogFileFromMetaList(metaList);
    }

    /**
     * 上传指定 level 和时间的日志文件.
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public static void uploadLogFile(long startTime, long endTime) {
        if (!ConnectivityState.shouldUpload()) {
            return;
        }

        TransferManager manager = manager();
        LoggerConfigurator config = LoggerFactory.config();
        if (config == null) {
            return;
        }
        int[] levels = config.getUploadLogLevel();
        List<LogFileMeta> metaList =
                DatabaseManager.findAllLogFileMetaByLevelAndTime(levels, startTime, endTime,
                        LoggerConstants.STATE_WILL_UPLOAD);

        manager.uploadLogFileFromMetaList(metaList);
    }

    /**
     * 上传崩溃日志文件.
     */
    public static void uploadCrashReport() {
        if (!ConnectivityState.shouldUpload()) {
            return;
        }

        TransferManager manager = manager();
        List<CrashReportFileMeta> metaList = DatabaseManager.findAllCrashFileMeta(LoggerConstants.STATE_NORMAL);
        for (CrashReportFileMeta meta : metaList) {
            UploadCrashReportFileEvent event = new UploadCrashReportFileEvent(meta);
            manager.addEvent(event);
        }
    }

    /**
     * 上传附件文件.
     */
    public static void uploadAttachment() {
        if (!ConnectivityState.shouldUpload()) {
            return;
        }

        TransferManager manager = manager();
        List<AttachmentFileMeta> metaList = DatabaseManager.findAllAttachmentFileMeta(LoggerConstants.STATE_NORMAL);
        for (AttachmentFileMeta meta : metaList) {
            UploadAttachmentFileEvent event = new UploadAttachmentFileEvent(meta);
            manager.addEvent(event);
        }
    }

    private void uploadLogFileFromMetaList(List<LogFileMeta> metaList) {
        String layoutJson = DatabaseManager.getLayoutJson();
        for (LogFileMeta meta : metaList) {
            if (meta.isEof()) {
                UploadLogFileEvent event = new UploadLogFileEvent(meta);
                event.setLayouts(layoutJson);
                addEvent(event);
            }
        }
    }

    private void addEvent(UploadEvent event) {
        boolean exist = false;
        for (UploadEvent item : queue) {
            if (item.identifier().equalsIgnoreCase(event.identifier())) {
                exist = true;
            }
        }

        if (!exist) {
            queue.add(event);
        }

        if (queue.size() > 0) {
            while (true) {
                UploadEvent task = queue.poll();
                if (task != null) {
                    getExecutor().submit(task);
                } else {
                    break;
                }
            }
        }
    }

}
