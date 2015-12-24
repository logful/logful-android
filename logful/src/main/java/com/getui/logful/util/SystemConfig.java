package com.getui.logful.util;

import android.content.Context;

import com.getui.logful.LoggerConstants;
import com.getui.logful.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class SystemConfig {

    private static final String TAG = "SystemConfig";

    private String baseUrl;

    private String aliasName;

    private String appKeyString;

    private String appSecretString;

    private boolean on;

    private static class ClassHolder {
        static SystemConfig config = new SystemConfig();
    }

    public static SystemConfig config() {
        return ClassHolder.config;
    }

    public SystemConfig() {
        this.on = true;
        this.aliasName = "";
    }

    /**
     * 读取配置文件.
     */
    public static void read() {
        Context context = LoggerFactory.context();
        if (context == null) {
            return;
        }
        SystemConfig config = SystemConfig.config();
        config.readProperties(context);
    }

    public static String alias() {
        SystemConfig config = SystemConfig.config();
        if (config.aliasName == null) {
            return "";
        }
        return config.aliasName;
    }

    public static String baseUrl() {
        SystemConfig config = SystemConfig.config();
        if (config.baseUrl == null || config.baseUrl.length() == 0) {
            return LoggerConstants.API_BASE_URL;
        }
        return config.baseUrl;
    }

    public static String apiUrl(String uri) {
        SystemConfig config = SystemConfig.config();
        return config.baseUrl + uri;
    }

    public static String appKey() {
        SystemConfig config = SystemConfig.config();
        if (!StringUtils.isEmpty(config.appKeyString)) {
            return config.appKeyString;
        }
        return "";
    }

    public static String appSecret() {
        SystemConfig config = SystemConfig.config();
        if (!StringUtils.isEmpty(config.appSecretString)) {
            return config.appSecretString;
        }
        return "";
    }

    public static boolean isOn() {
        SystemConfig config = SystemConfig.config();
        return config.on;
    }

    /**
     * 保存用户设置的别名.
     *
     * @param alias User alias name
     */
    public static synchronized void saveAlias(String alias) {
        if (alias == null || alias.length() == 0) {
            return;
        }
        SystemConfig config = SystemConfig.config();
        config.aliasName = alias;
    }

    public static synchronized void saveBaseUrl(String baseUrl) {
        SystemConfig config = SystemConfig.config();
        config.baseUrl = baseUrl;
    }

    public static synchronized void saveAppKey(String appKey) {
        SystemConfig config = SystemConfig.config();
        config.appKeyString = appKey;
    }

    public static synchronized void saveAppSecret(String appSecret) {
        SystemConfig config = SystemConfig.config();
        config.appSecretString = appSecret;
    }

    /**
     * 保存日志系统状态.
     *
     * @param on Log system status
     */
    public static synchronized void saveStatus(boolean on) {
        SystemConfig config = SystemConfig.config();
        if (config.on != on) {
            config.on = on;
            config.writeProperties();
        }
    }

    private void readProperties(Context context) {
        File file = new File(context.getFilesDir(), LoggerConstants.SYSTEM_CONFIG_FILE_NAME);
        if (!file.exists() || !file.isFile()) {
            return;
        }
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] keyValue = line.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];

                    if (key.equalsIgnoreCase("isOn")) {
                        if (value.equalsIgnoreCase("true")) {
                            on = true;
                        }
                        if (value.equalsIgnoreCase("false")) {
                            on = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "", e);
                }
            }
        }
    }

    private void writeProperties() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Context context = LoggerFactory.context();
                if (context == null) {
                    return;
                }

                HashMap<String, String> propertiesMap = new HashMap<String, String>();
                propertiesMap.put("isOn", on ? "true" : "false");

                File file = new File(context.getFilesDir(), LoggerConstants.SYSTEM_CONFIG_FILE_NAME);
                String temp = StringUtils.mapToPropertiesString(propertiesMap);
                try {
                    BufferedWriter out = new BufferedWriter(new FileWriter(file), 2048);
                    out.write(temp);
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "", e);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

}
