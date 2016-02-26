package com.getui.logful.net;

import android.content.Context;

import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;
import com.getui.logful.db.DatabaseManager;
import com.getui.logful.entity.CrashReportFileMeta;
import com.getui.logful.util.GzipUtils;
import com.getui.logful.util.HttpRequest;
import com.getui.logful.util.LogStorage;
import com.getui.logful.util.LogUtil;
import com.getui.logful.util.SystemConfig;
import com.getui.logful.util.UIDUtils;

public class UploadCrashReportFileEvent extends UploadEvent {

    private static final String TAG = "UploadCrashReport";

    private CrashReportFileMeta meta;

    public UploadCrashReportFileEvent(CrashReportFileMeta meta) {
        this.meta = meta;
    }

    @Override
    public String identifier() {
        if (meta != null) {
            return String.format("%d-%d", meta.getId(), 2);
        }
        return "";
    }

    @Override
    public void startRequest(String authorization) {
        if (meta == null) {
            return;
        }

        String filePath = LogStorage.readableCrashReportFilePath(meta);
        if (filePath == null) {
            return;
        }

        Context context = LoggerFactory.context();
        if (context == null) {
            return;
        }

        byte[] payload = GzipUtils.compress(filePath);
        if (payload == null || payload.length == 0) {
            return;
        }

        HttpRequest request = null;
        try {
            String baseUrl = SystemConfig.baseUrl() + LoggerConstants.UPLOAD_CRASH_REPORT_FILE_URI;
            String url = HttpRequest.append(baseUrl,
                    LoggerConstants.QUERY_PARAM_SDK_VERSION, LoggerFactory.version(),
                    LoggerConstants.QUERY_PARAM_PLATFORM, String.valueOf(LoggerConstants.PLATFORM_ANDROID),
                    LoggerConstants.QUERY_PARAM_UID, UIDUtils.uid());

            request = HttpRequest.post(url);
            request.header("Authorization", authorization);
            request.header("Content-Type", "application/octet-stream");
            request.ignoreCloseExceptions();
            request.connectTimeout(LoggerConstants.DEFAULT_HTTP_REQUEST_TIMEOUT);
            request.readTimeout(LoggerConstants.DEFAULT_HTTP_REQUEST_TIMEOUT);

            request.send(payload);

            if (request.ok()) {
                meta.setStatus(LoggerConstants.STATE_UPLOADED);
                DatabaseManager.saveCrashFileMeta(meta);
                LogUtil.d(TAG, "Send crash report " + meta.getFilename() + " failed.");
            } else {
                LogUtil.d(TAG, "Send crash report " + meta.getFilename() + " failed.");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "", e);
        } finally {
            if (request != null) {
                request.disconnect();
            }
        }
    }

}
