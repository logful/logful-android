package com.getui.logful.exception;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;

import com.getui.logful.LoggerConstants;
import com.getui.logful.db.DatabaseManager;
import com.getui.logful.entity.CrashReportFileMeta;
import com.getui.logful.util.LogStorage;
import com.getui.logful.util.LogUtil;

public class FileReportSender implements ReportSender {

    @Override
    public void send(Context context, final CrashReportData crashData) {
        if (context == null) {
            return;
        }
        int location;
        String crashReportDir;
        if (LogStorage.writable()) {
            location = LoggerConstants.LOCATION_EXTERNAL;
            crashReportDir = LogStorage.externalCrashReportDir();
        } else {
            location = LoggerConstants.LOCATION_INTERNAL;
            crashReportDir = LogStorage.internalCrashReportDir();
        }

        if (crashReportDir == null) {
            return;
        }

        String filename = crashReportFileName();
        String filePath = crashReportDir + "/" + filename;
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(filePath, false));
            String properties = CrashReportDataFactory.properties(crashData);
            outputStream.write(properties.getBytes(LoggerConstants.CHARSET));
            outputStream.flush();
            outputStream.close();

            // 存储崩溃日志文件信息到数据库
            CrashReportFileMeta meta = new CrashReportFileMeta();
            meta.setLocation(location);
            meta.setFilename(filename);
            meta.setCause(crashData.getExceptionCause());
            DatabaseManager.saveCrashFileMeta(meta);

            // 上传崩溃日志文件
            upload(meta);
        } catch (IOException e) {
            LogUtil.e("FileReportSender", "IOException", e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                LogUtil.e("FileReportSender", "IOException", e);
            }
        }
    }

    private String crashReportFileName() {
        return "crash-report-" + System.currentTimeMillis() + ".bin";
    }

    private void upload(CrashReportFileMeta meta) {
        // if (!ConnectivityState.shouldUpload()) {
        // return;
        // }

        // TODO Upload crash report file.
    }
}
