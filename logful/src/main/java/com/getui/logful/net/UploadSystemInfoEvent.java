package com.getui.logful.net;

import android.content.Context;
import android.os.Build;

import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;
import com.getui.logful.util.ClientAuthUtil;
import com.getui.logful.util.HttpRequest;
import com.getui.logful.util.LogUtil;
import com.getui.logful.util.StringUtils;
import com.getui.logful.util.SystemConfig;
import com.getui.logful.util.SystemInfo;
import com.getui.logful.util.UidTool;

public class UploadSystemInfoEvent extends UploadEvent {

    private static final String TAG = "UploadSystemInfoEvent";

    public interface UploadInfoListener {
        void onResponse(String response);

        void onFailure();
    }

    private UploadInfoListener listener;

    @Override
    public String identifier() {
        return String.format("%d-%d", 100, 3);
    }

    public void upload(UploadInfoListener listener) {
        this.listener = listener;
        run();
    }

    @Override
    public void authorized(String authorization) {
        super.authorized(authorization);
        Context context = LoggerFactory.context();
        if (context == null) {
            return;
        }

        try {
            String url = SystemConfig.baseUrl() + LoggerConstants.UPLOAD_SYSTEM_INFO_URI;
            HttpRequest request = HttpRequest.post(url);
            request.header("Authorization", authorization);

            request.ignoreCloseExceptions();
            request.connectTimeout(LoggerConstants.DEFAULT_HTTP_REQUEST_TIMEOUT);
            request.readTimeout(LoggerConstants.DEFAULT_HTTP_REQUEST_TIMEOUT);

            request.part("platform", "android");
            request.part("sdkVersion", LoggerFactory.version());
            request.part("uid", UidTool.uid(context));
            request.part("alias", SystemConfig.alias());
            request.part("model", Build.MODEL);
            request.part("imei", SystemInfo.imei(context));
            request.part("macAddress", SystemInfo.macAddress(context));
            request.part("osVersion", Build.VERSION.RELEASE);

            request.part("appId", SystemInfo.appId());
            request.part("version", SystemInfo.version());
            request.part("versionString", SystemInfo.versionString());

            if (request.ok()) {
                String response = request.body();
                if (listener != null) {
                    listener.onResponse(response);
                }
            } else {
                if (request.code() == 401) {
                    ClientAuthUtil.util().clearToken();
                }
                if (!StringUtils.isEmpty(request.body())) {
                    LogUtil.w(TAG, request.body());
                }
                if (listener != null) {
                    listener.onFailure();
                }
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onFailure();
            }
            LogUtil.e(TAG, "Upload user info failed.");
        }

    }

}
