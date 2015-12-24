package com.getui.logful.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import com.getui.logful.Logger;
import com.getui.logful.LoggerFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lqynydyxf on 15/12/22.
 */
public class ParsePassThroughData {

    private final static String TAG = ParsePassThroughData.class.getSimpleName();

    /**
     * 解析Json格式的字符串
     *
     * @param context
     * @param data
     */
    public static void parseData(Context context, String data) {
        final String MATCHER = "matcher";
        final String APP_ID = "appId";
        final String APP_KEY = "appKey";
        final String ACTION_CHAINS = "actionChains";
        final String OPEN_LOG = "openLog";
        final String UPLOAD_LOG = "upLoadLog";
        final String IS_UPLOAD = "isUpLoad";
        final String UPLOAD_LEVEL = "upLoadLevel";
        try {
            JSONObject dataJson = new JSONObject(data);
            JSONObject matcherObject = dataJson.getJSONObject(MATCHER);
            String appId = matcherObject.getString(APP_ID);
            String appKey = matcherObject.getString(APP_KEY);
            JSONObject actionObject = dataJson.getJSONObject(ACTION_CHAINS);
            boolean openLog = actionObject.getBoolean(OPEN_LOG);
            JSONObject upLoadLogObject = actionObject.getJSONObject(UPLOAD_LOG);
            boolean isUpLoad = upLoadLogObject.getBoolean(IS_UPLOAD);
            String upLoadLevel = upLoadLogObject.getString(UPLOAD_LEVEL);
            if (verifyAppIDAndAppKey(context, appId, appKey)) {
                Log.d(TAG, "verify success!");
                pushLogToServer(upLoadLevel);
            }
            Log.d(TAG, "appId = " + appId +
                    " appKey = " + appKey +
                    " oepnLog = " + Boolean.toString(openLog) +
                    " isUpLoad = " + Boolean.toString(isUpLoad) +
                    " upLoadLevel = " + upLoadLevel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过透传消息里的AppID，AppKey和本地配置的报名和AppKey的对比，进行验证
     *
     * @param context
     * @param AppID
     * @param AppKey
     * @return
     */
    public static boolean verifyAppIDAndAppKey(Context context, String AppID, String AppKey) {
        String packageName = context.getPackageName();
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            String pushAppKey = appInfo.metaData.getString("PUSH_APPKEY");
            if (pushAppKey != null && packageName.equals(AppID) && AppKey.equals(pushAppKey)) {
                Log.d(TAG, packageName + "   " + pushAppKey);
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据Json对象里日志的级别打印
     *
     * @param level
     */
    private static void pushLogToServer(String level) {
        Log.d(TAG, "pushLogToServer is doing");
        Logger logger = LoggerFactory.logger("app");
        if (level.equals("verbose")) {
            logger.verbose(TAG, "some verbose message");

        } else if (level.equals("debug")) {
            logger.debug(TAG, "some debug message");

        } else if (level.equals("info")) {
            logger.info(TAG, "some info message");

        } else if (level.equals("warn")) {
            logger.warn(TAG, "some warn message");

        } else if (level.equals("error")) {
            logger.error(TAG, "some error message");

        } else if (level.equals("exception")) {
            logger.exception(TAG, "some exception message", null);

        } else if (level.equals("fatal")) {
            logger.fatal(TAG, "some fatal message");

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "upLoadLog------");
                LoggerFactory.interruptThenSync();
            }
        }, 1000);
    }
}
