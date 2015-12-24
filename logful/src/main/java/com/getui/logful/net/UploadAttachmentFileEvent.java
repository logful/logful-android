package com.getui.logful.net;

import android.content.Context;

import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;
import com.getui.logful.db.DatabaseManager;
import com.getui.logful.entity.AttachmentFileMeta;
import com.getui.logful.util.Checksum;
import com.getui.logful.util.ConnectivityState;
import com.getui.logful.util.HttpRequest;
import com.getui.logful.util.LogStorage;
import com.getui.logful.util.LogUtil;
import com.getui.logful.util.StringUtils;
import com.getui.logful.util.SystemConfig;
import com.getui.logful.util.SystemInfo;
import com.getui.logful.util.UidTool;

import java.io.File;

public class UploadAttachmentFileEvent extends UploadEvent {

    private static final String TAG = "UploadAttachmentFile";

    private AttachmentFileMeta meta;

    public UploadAttachmentFileEvent(AttachmentFileMeta meta) {
        this.meta = meta;
    }

    @Override
    public String identifier() {
        if (meta != null) {
            return String.format("%d-%d", meta.getId(), 4);
        }
        return "";
    }

    @Override
    public void startRequest(String authorization) {
        if (meta == null) {
            return;
        }

        if (!ConnectivityState.shouldUpload()) {
            return;
        }

        String inFilePath = LogStorage.readableAttachmentFilePath(meta);
        if (inFilePath == null) {
            LogUtil.w(TAG, "Can not read attachment file.");
            return;
        }

        Context context = LoggerFactory.context();
        if (context == null) {
            return;
        }

        String sum = Checksum.fileMD5(inFilePath);
        if (sum == null || sum.length() == 0) {
            return;
        }

        String url = SystemConfig.baseUrl() + LoggerConstants.UPLOAD_ATTACHMENT_FILE_URI;
        HttpRequest request = null;
        try {
            request = HttpRequest.post(url);
            request.header("Authorization", authorization);

            request.ignoreCloseExceptions();
            request.connectTimeout(LoggerConstants.DEFAULT_HTTP_REQUEST_TIMEOUT);
            request.readTimeout(LoggerConstants.DEFAULT_HTTP_REQUEST_TIMEOUT);

            request.part("platform", "android");
            request.part("sdkVersion", LoggerFactory.version());
            request.part("uid", UidTool.uid());
            request.part("appId", SystemInfo.appId());
            request.part("fileSum", sum);
            request.part("attachmentId", meta.getSequence());
            request.part("attachmentFile", meta.getFilename(), new File(inFilePath));
            if (request.ok()) {
                success();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "", e);
        } finally {
            if (request != null) {
                request.disconnect();
            }
        }
    }

    private void success() {
        meta.setStatus(LoggerConstants.STATE_UPLOADED);
        DatabaseManager.saveAttachmentFileMeta(meta);
    }
}
