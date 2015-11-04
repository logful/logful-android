package com.getui.logful.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.getui.logful.Constants;
import com.getui.logful.LoggerConfigurator;
import com.getui.logful.LoggerFactory;

public class ConnectivityState {

    public static final int WIFI_CONNECTION = Constants.TYPE_WIFI;

    public static final int MOBILE_CONNECTION = Constants.TYPE_MOBILE;

    public static final int NO_CONNECTION = 0x03;

    public static final int UNKNOWN_CONNECTION = 0x04;

    public static int state(Context context) {
        if (context == null) {
            return UNKNOWN_CONNECTION;
        }
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.isConnected()) {
                // wifi 连接
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return WIFI_CONNECTION;
                }
                // 移动网络连接
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return MOBILE_CONNECTION;
                }
            }
        }
        return NO_CONNECTION;
    }

    public static boolean shouldUpload() {
        LoggerConfigurator config = LoggerFactory.config();
        Context context = LoggerFactory.context();
        if (config == null) {
            return false;
        }
        if (context == null) {
            return false;
        }
        boolean shouldUpload = false;
        int currentState = ConnectivityState.state(context);
        for (int state : config.getUploadNetworkType()) {
            if (state == currentState) {
                shouldUpload = true;
                break;
            }
        }
        return shouldUpload;
    }

}
