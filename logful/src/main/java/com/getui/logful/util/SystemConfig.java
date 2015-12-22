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
import java.io.InputStreamReader;
import java.util.HashMap;

public class SystemConfig {

    private static final String TAG = "SystemConfig";

    private String baseUrl;

    private String aliasName;

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
        config.readAssetConfigFile(context);
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

    /**
     * 读取用户 Asset 配置文件.
     *
     * @param context {@link Context}
     */
    private void readAssetConfigFile(Context context) {
        BufferedReader bufferedReader = null;
        try {
            String filename = LoggerConstants.USER_CONFIG_ASSET_FILE_NAME;
            bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() > 0 && line.charAt(0) != '#') {
                    String[] keyValue = line.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        if (key.equalsIgnoreCase("baseUrl")) {
                            if (value.length() > 0) {
                                baseUrl = value;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            LogUtil.v(TAG, "logful.properties file not found.");
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LogUtil.v(TAG, "Close buffered reader failed.");
                }
            }
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
