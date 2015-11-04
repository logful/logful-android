package com.getui.logful.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.getui.logful.entity.Config;
import com.getui.logful.net.UploadSystemInfoEvent;
import com.getui.logful.net.UploadSystemInfoEvent.UploadInfoListener;
import com.getui.logful.schedule.ScheduleExecutor;

public class RemoteConfig {

    private static final String TAG = "RemoteConfig";

    private static UploadInfoListener listener = new UploadInfoListener() {
        @Override
        public void onResponse(String response) {
            Config config = Config.defaultConfig();

            try {
                JSONObject object = new JSONObject(response);
                config.setLevel(object.optInt("level"));
                config.setTargetLevel(object.optInt("targetLevel"));
                config.setShouldUpload(object.optBoolean("shouldUpload"));
                if (object.has("schedule")) {
                    JSONObject temp = object.optJSONObject("schedule");
                    config.setScheduleType(temp.optInt("scheduleType"));
                    if (temp.has("scheduleTime")) {
                        config.setScheduleTime(temp.optLong("scheduleTime"));
                    }
                    if (temp.has("scheduleArray")) {
                        JSONArray array = temp.optJSONArray("scheduleArray");
                        String[] strings = new String[array.length()];
                        for (int i = 0; i < array.length(); i++) {
                            strings[i] = array.getJSONObject(i).optString("timeString");
                        }
                        config.setScheduleArray(strings);
                    }
                }
            } catch (JSONException e) {
                LogUtil.e(TAG, "Error parse response data.");
            }

            RemoteConfig.parse(config);
        }

        @Override
        public void onFailure() {
            RemoteConfig.parse(Config.defaultConfig());
        }
    };

    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            UploadSystemInfoEvent event = new UploadSystemInfoEvent();
            event.upload(listener);
        }
    };

    public static void read() {
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private static void parse(Config config) {
        if (config.isShouldUpload()) {
            if (config.getScheduleType() == 1) {
                // Schedule at fix time.
                ScheduleExecutor.schedule(config.getScheduleArray());
            } else if (config.getScheduleType() == 2) {
                // Schedule at fix delay.
                ScheduleExecutor.schedule(config.getScheduleTime());
            }
        }
    }

}
