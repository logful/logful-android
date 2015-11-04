package com.getui.logful.net;

import java.io.File;

import android.content.Context;

import com.getui.logful.LoggerConfigurator;
import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;
import com.getui.logful.db.DatabaseManager;
import com.getui.logful.entity.LogFileMeta;
import com.getui.logful.util.Checksum;
import com.getui.logful.util.ClientAuthUtil;
import com.getui.logful.util.ConnectivityState;
import com.getui.logful.util.GzipTool;
import com.getui.logful.util.HttpRequest;
import com.getui.logful.util.LogStorage;
import com.getui.logful.util.LogUtil;
import com.getui.logful.util.StringUtils;
import com.getui.logful.util.SystemConfig;
import com.getui.logful.util.SystemInfo;
import com.getui.logful.util.UidTool;

public class UploadLogFileEvent extends UploadEvent {

    private static final String TAG = "UploadLogFileEvent";

    private LogFileMeta meta;

    private String layouts;

    private String filePath;

    private String cacheFilePath;

    public UploadLogFileEvent(LogFileMeta meta) {
        this.meta = meta;
    }

    public void setLayouts(String layouts) {
        this.layouts = layouts;
    }

    @Override
    public String identifier() {
        if (meta != null) {
            return String.format("%d-%d", meta.getId(), 1);
        }
        return "";
    }

    @Override
    public void authorized(String authorization) {
        super.authorized(authorization);

        if (meta == null) {
            return;
        }
        if (!ConnectivityState.shouldUpload()) {
            return;
        }

        String inFilePath = LogStorage.readableLogFilePath(meta);
        if (inFilePath == null) {
            LogUtil.w(TAG, "Can not read log file.");
            return;
        }

        Context context = LoggerFactory.context();
        if (context == null) {
            return;
        }

        File cacheDir;
        if (LogStorage.writable()) {
            cacheDir = context.getExternalCacheDir();
        } else {
            cacheDir = context.getCacheDir();
        }

        cacheFilePath = cacheDir + "/" + meta.getFilename();
        if (GzipTool.compress(inFilePath, cacheFilePath)) {
            String fileSumString = Checksum.fileMD5(cacheFilePath);
            String url = SystemConfig.baseUrl() + LoggerConstants.UPLOAD_LOG_FILE_URI;
            try {
                HttpRequest request = HttpRequest.post(url);
                request.header("Authorization", authorization);

                request.ignoreCloseExceptions();
                request.connectTimeout(LoggerConstants.DEFAULT_HTTP_REQUEST_TIMEOUT);
                request.readTimeout(LoggerConstants.DEFAULT_HTTP_REQUEST_TIMEOUT);

                request.part("platform", "android");
                request.part("sdkVersion", LoggerFactory.version());
                request.part("uid", UidTool.uid(context));
                request.part("appId", SystemInfo.appId());
                request.part("loggerName", meta.getLoggerName());
                request.part("layouts", layouts);
                request.part("level", String.valueOf(meta.getLevel()));
                request.part("alias", SystemConfig.alias());
                request.part("fileSum", fileSumString);
                request.part("logFile", meta.getFilename(), new File(cacheFilePath));
                if (request.ok()) {
                    filePath = inFilePath;
                    success();
                    deleteCacheFile();
                } else {
                    if (request.code() == 401) {
                        ClientAuthUtil.util().clearToken();
                    }
                    if (!StringUtils.isEmpty(request.body())) {
                        LogUtil.w(TAG, request.body());
                    }
                    deleteCacheFile();
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "", e);
            }
        }
    }

    private void success() {
        // Update log file meta
        LoggerConfigurator config = LoggerFactory.config();
        if (config != null && config.isDeleteUploadedLogFile()) {
            // 删除已上传的日志文件
            File file = new File(filePath);
            if (file.delete()) {
                meta.setStatus(LoggerConstants.STATE_DELETED);
                meta.setDeleteTime(System.currentTimeMillis());
                DatabaseManager.saveLogFileMeta(meta);
            } else {
                meta.setStatus(LoggerConstants.STATE_UPLOADED);
                DatabaseManager.saveLogFileMeta(meta);
            }
        } else {
            meta.setStatus(LoggerConstants.STATE_UPLOADED);
            DatabaseManager.saveLogFileMeta(meta);
        }
    }

    private void deleteCacheFile() {
        if (!StringUtils.isEmpty(cacheFilePath)) {
            File file = new File(cacheFilePath);
            if (!file.delete()) {
                LogUtil.w(TAG, "Delete cache file failed.");
            }
        }
    }
}
