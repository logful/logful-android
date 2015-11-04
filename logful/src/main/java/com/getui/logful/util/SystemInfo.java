package com.getui.logful.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.getui.logful.LoggerFactory;

public class SystemInfo {

    private static final String TAG = "SystemInfo";

    private static String packageName;
    private static int versionCode = -1;
    private static String versionName;

    public static String appId() {
        if (!StringUtils.isEmpty(packageName)) {
            return packageName;
        }

        readPackageInfo();

        if (StringUtils.isEmpty(packageName)) {
            return "";
        } else {
            return packageName;
        }
    }

    public static int version() {
        if (versionCode != -1) {
            return versionCode;
        }

        readPackageInfo();

        return versionCode;
    }

    public static String versionString() {
        if (!StringUtils.isEmpty(versionName)) {
            return versionName;
        }

        readPackageInfo();

        if (StringUtils.isEmpty(versionName)) {
            return "";
        } else {
            return versionName;
        }
    }

    public static String androidId(Context context) {
        if (context == null) {
            return "";
        }
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId != null && !androidId.equals("9774d56d682e549c")) {
            return androidId;
        }
        return "";
    }

    public static String imei(Context context) {
        if (context == null) {
            return "";
        }
        PackageManagerWrapper pm = new PackageManagerWrapper(context);
        if (pm.hasPermission(Manifest.permission.READ_PHONE_STATE)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String deviceId = telephonyManager.getDeviceId();
            if (deviceId != null && deviceId.length() > 0 && !deviceId.contains("*")
                    && countZero(deviceId) != deviceId.length()) {
                return deviceId;
            }
        }
        return "";
    }

    public static String macAddress(Context context) {
        if (context == null) {
            return "";
        }
        PackageManagerWrapper pm = new PackageManagerWrapper(context);
        if (pm.hasPermission(Manifest.permission.ACCESS_WIFI_STATE)
                && pm.hasPermission(Manifest.permission.ACCESS_NETWORK_STATE)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                WifiInfo info = wifiManager.getConnectionInfo();
                return info.getMacAddress();
            }
        }
        return "";
    }

    private static void readPackageInfo() {
        Context context = LoggerFactory.context();
        if (context == null) {
            return;
        }

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            packageName = packageInfo.packageName;
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(TAG, "", e);
        }
    }

    private static int countZero(String string) {
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '0') {
                count++;
            }
        }
        return count;
    }

}
