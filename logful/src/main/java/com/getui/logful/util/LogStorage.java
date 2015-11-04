package com.getui.logful.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;
import com.getui.logful.entity.AttachmentFileMeta;
import com.getui.logful.entity.CrashReportFileMeta;
import com.getui.logful.entity.LogFileMeta;

public class LogStorage {

    public static boolean readable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static boolean writable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static String externalLogDir() {
        return externalDir(LoggerConstants.LOG_DIR_NAME);
    }

    public static String internalLogDir() {
        return internalDir(LoggerConstants.LOG_DIR_NAME);
    }

    public static String externalCrashReportDir() {
        return externalDir(LoggerConstants.CRASH_REPORT_DIR_NAME);
    }

    public static String internalCrashReportDir() {
        return internalDir(LoggerConstants.CRASH_REPORT_DIR_NAME);
    }

    public static String externalAttachmentDir() {
        return externalDir(LoggerConstants.ATTACHMENT_DIR_NAME);
    }

    public static String internalAttachmentDir() {
        return internalDir(LoggerConstants.ATTACHMENT_DIR_NAME);
    }

    public static String writableLogFilePath(LogFileMeta meta) {
        return logFilePath(meta, true);
    }

    public static String readableLogFilePath(LogFileMeta meta) {
        return logFilePath(meta, false);
    }

    public static String writableCrashReportFilePath(CrashReportFileMeta meta) {
        return crashReportFilePath(meta, true);
    }

    public static String readableCrashReportFilePath(CrashReportFileMeta meta) {
        return crashReportFilePath(meta, false);
    }

    public static String writableAttachmentFilePath(AttachmentFileMeta meta) {
        return attachmentFilePath(meta, true);
    }

    public static String readableAttachmentFilePath(AttachmentFileMeta meta) {
        return attachmentFilePath(meta, false);
    }

    private static String logFilePath(LogFileMeta meta, boolean writable) {
        if (meta == null) {
            return null;
        }
        return fileFullPath(LoggerConstants.LOG_DIR_NAME, writable, meta.getLocation(), meta.getFilename());
    }

    public static String crashReportFilePath(CrashReportFileMeta meta, boolean writable) {
        if (meta == null) {
            return null;
        }
        return fileFullPath(LoggerConstants.CRASH_REPORT_DIR_NAME, writable, meta.getLocation(), meta.getFilename());
    }

    public static String attachmentFilePath(AttachmentFileMeta meta, boolean writable) {
        if (meta == null) {
            return null;
        }
        return fileFullPath(LoggerConstants.ATTACHMENT_DIR_NAME, writable, meta.getLocation(), meta.getFilename());
    }

    private static String fileFullPath(String dirName, boolean writable, int location, String filename) {
        String dirPath = null;
        switch (location) {
            case LoggerConstants.LOCATION_EXTERNAL:
                if (writable && writable()) {
                    dirPath = externalDir(dirName);
                } else if (!writable && readable()) {
                    dirPath = externalDir(dirName);
                }
                break;
            case LoggerConstants.LOCATION_INTERNAL:
                dirPath = internalDir(dirName);
                break;
        }
        if (dirPath != null) {
            return dirPath + "/" + filename;
        }
        return null;
    }

    public static String externalDir(String dirName) {
        Context context = LoggerFactory.context();
        if (context == null) {
            return null;
        }

        File dir = new File(context.getExternalFilesDir(null), dirName);
        if (dir.exists()) {
            if (dir.isDirectory()) {
                return dir.getAbsolutePath();
            } else {
                if (dir.delete()) {
                    return dir.mkdirs() ? dir.getAbsolutePath() : null;
                }
            }
        } else {
            return dir.mkdirs() ? dir.getAbsolutePath() : null;
        }

        return null;
    }

    public static String internalDir(String dirName) {
        Context context = LoggerFactory.context();
        if (context == null) {
            return null;
        }

        File dir = new File(context.getFilesDir(), dirName);
        if (dir.exists()) {
            if (dir.isDirectory()) {
                return dir.getAbsolutePath();
            } else {
                if (dir.delete()) {
                    return dir.mkdirs() ? dir.getAbsolutePath() : null;
                }
            }
        } else {
            return dir.mkdirs() ? dir.getAbsolutePath() : null;
        }
        return null;
    }

}
