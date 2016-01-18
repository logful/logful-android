package com.getui.logful.config;

import com.getui.logful.appender.AsyncAppenderManager;
import com.getui.logful.schedule.ScheduleExecutor;
import com.getui.logful.util.FileUtils;
import com.getui.logful.util.LogStorage;
import com.getui.logful.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class LogfulConfigurer implements Configurer {

    private static final String TAG = "LogfulConfigurer";
    private static final String JSON_KEY_TIMESTAMP = "timestamp";
    private static final String JSON_KEY_ON = "on";
    private static final String JSON_KEY_INTERRUPT = "interrupt";
    private static final String JSON_KEY_INTERVAL = "interval";
    private static final String JSON_KEY_FREQUENCY = "frequency";

    private long timestampVal;

    private boolean onVal = true;

    private boolean interruptVal = false;

    private long intervalVal;

    private long frequencyVal;

    private static class ClassHolder {
        static LogfulConfigurer config = new LogfulConfigurer();
    }

    public static LogfulConfigurer config() {
        return ClassHolder.config;
    }

    public LogfulConfigurer() {
        load();
    }

    public void load() {
        // TODO
        File configFile = LogStorage.configFile();
        if (configFile != null && configFile.isFile()) {
            try {
                String content = FileUtils.read(configFile);
                LogUtil.d(TAG, "Read config from local: " + content);
                parse(new JSONObject(content), false, false);
            } catch (IOException e) {
                LogUtil.e(TAG, "", e);
            } catch (JSONException e) {
                LogUtil.e(TAG, "", e);
            }
        }
    }

    public void parse(JSONObject object, boolean save, boolean implement) {
        this.timestampVal = object.optLong(JSON_KEY_TIMESTAMP);
        this.onVal = object.optBoolean(JSON_KEY_ON, false);
        this.interruptVal = object.optBoolean(JSON_KEY_INTERRUPT, false);
        this.intervalVal = object.optLong(JSON_KEY_INTERVAL);
        this.frequencyVal = object.optLong(JSON_KEY_FREQUENCY);

        if (save) {
            save();
        }

        if (implement) {
            implement();
        }
    }

    public void save() {
        try {
            File configFile = LogStorage.configFile();
            if (configFile != null) {
                JSONObject object = new JSONObject();

                object.put(JSON_KEY_TIMESTAMP, timestampVal);
                object.put(JSON_KEY_ON, onVal);
                object.put(JSON_KEY_INTERRUPT, interruptVal);
                object.put(JSON_KEY_INTERVAL, intervalVal);
                object.put(JSON_KEY_FREQUENCY, frequencyVal);
                FileUtils.write(configFile, object.toString(), false);
            }
        } catch (JSONException e) {
            LogUtil.e(TAG, "", e);
        } catch (IOException e) {
            LogUtil.e(TAG, "", e);
        }
    }

    public void implement() {
        LogfulConfigurer config = LogfulConfigurer.config();
        if (config.on()) {
            // Read cache log event.
            AsyncAppenderManager.readCache();
            if (config.timestampVal != 0 && config.intervalVal != 0) {
                long remain = config.intervalVal - (System.currentTimeMillis() / 1000 - config.timestampVal);
                if (remain > 0) {
                    ScheduleExecutor.schedule(config.frequency(), config.interruptVal, remain);
                } else {
                    LogUtil.d(TAG, "Interval time arrived!");
                }
            } else {
                ScheduleExecutor.schedule(config.frequency(), false, 0);
            }
        } else {
            ScheduleExecutor.cancelAll();
            LogUtil.d(TAG, "Logful is turn off.");
        }
    }

    public void setOn(boolean on, boolean save, boolean implement) {
        this.onVal = on;
        if (save) {
            save();
        }

        if (implement) {
            implement();
        }
    }

    public void setFrequency(long frequency, boolean save, boolean implement) {
        this.frequencyVal = frequency;
        if (save) {
            save();
        }

        if (implement) {
            implement();
        }
    }

    @Override
    public long timestamp() {
        return timestampVal;
    }

    @Override
    public boolean on() {
        return onVal;
    }

    @Override
    public boolean interrupt() {
        return false;
    }

    @Override
    public long interval() {
        return intervalVal;
    }

    @Override
    public long frequency() {
        return frequencyVal;
    }
}
