package com.getui.logful.util;

import com.getui.logful.LoggerConstants;

public class SystemConfig {

    private String baseUrl;

    private String aliasName;

    private String appKeyString;

    private String appSecretString;

    private static class ClassHolder {
        static SystemConfig config = new SystemConfig();
    }

    public static SystemConfig config() {
        return ClassHolder.config;
    }

    public static String baseUrl() {
        SystemConfig config = SystemConfig.config();
        if (StringUtils.isEmpty(config.baseUrl)) {
            return LoggerConstants.API_BASE_URL;
        }
        return config.baseUrl;
    }

    public static String alias() {
        SystemConfig config = SystemConfig.config();
        if (StringUtils.isEmpty(config.aliasName)) {
            return "";
        }
        return config.aliasName;
    }


    public static String apiUrl(String uri) {
        return SystemConfig.baseUrl() + uri;
    }

    public static String appKey() {
        SystemConfig config = SystemConfig.config();
        if (StringUtils.isEmpty(config.appKeyString)) {
            return "";
        }
        return config.appKeyString;
    }

    public static String appSecret() {
        SystemConfig config = SystemConfig.config();
        if (StringUtils.isEmpty(config.appSecretString)) {
            return "";
        }
        return config.appSecretString;
    }

    public static synchronized void saveAlias(String alias) {
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

}
