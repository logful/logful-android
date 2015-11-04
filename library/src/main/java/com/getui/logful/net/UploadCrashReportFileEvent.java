package com.getui.logful.net;

import java.io.File;

import android.content.Context;

import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;
import com.getui.logful.db.DatabaseManager;
import com.getui.logful.entity.CrashReportFileMeta;
import com.getui.logful.util.Checksum;
import com.getui.logful.util.ClientAuthUtil;
import com.getui.logful.util.HttpRequest;
import com.getui.logful.util.LogStorage;
import com.getui.logful.util.LogUtil;
import com.getui.logful.util.StringUtils;
import com.getui.logful.util.SystemConfig;
import com.getui.logful.util.SystemInfo;
import com.getui.logful.util.UidTool;

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
    public void authorized(String authorization) {
        super.authorized(authorization);

        if (meta == null) {
            return;
        }

        String fullPath = LogStorage.readableCrashReportFilePath(meta);
        if (fullPath == null) {
            return;
        }

        Context context = LoggerFactory.context();
        if (context == null) {
            return;
        }

        String fileSumString = Checksum.fileMD5(fullPath);
        if (fileSumString == null) {
            LogUtil.v("UploadCrashReportFileEvent", "Check MD5 " + fullPath + " failed");
            return;
        }

        try {
            String url = SystemConfig.baseUrl() + LoggerConstants.UPLOAD_CRASH_REPORT_FILE_URI;

            HttpRequest request = HttpRequest.post(url);
            request.header("Authorization", authorization);

            request.ignoreCloseExceptions();
            request.connectTimeout(LoggerConstants.DEFAULT_HTTP_REQUEST_TIMEOUT);
            request.readTimeout(LoggerConstants.DEFAULT_HTTP_REQUEST_TIMEOUT);

            request.part("platform", "android");
            request.part("sdkVersion", LoggerFactory.version());
            request.part("uid", UidTool.uid(context));
            request.part("appId", SystemInfo.appId());
            request.part("fileSum", fileSumString);
            request.part("reportFile", meta.getFilename(), new File(fullPath));

            if (request.ok()) {
                meta.setFileMD5(fileSumString);
                meta.setStatus(LoggerConstants.STATE_UPLOADED);
                DatabaseManager.saveCrashFileMeta(meta);
            } else {
                if (request.code() == 401) {
                    ClientAuthUtil.util().clearToken();
                }
                if (!StringUtils.isEmpty(request.body())) {
                    LogUtil.w(TAG, request.body());
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "", e);
        }
    }

}
