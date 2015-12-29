package com.getui.logful.util;

import android.util.Log;

import com.getui.logful.LoggerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by lqynydyxf on 15/12/22.
 */
public class ParsePassThroughData {

    private final static String TAG = ParsePassThroughData.class.getSimpleName();

    //接受到透传消息时的时间戳
    private static long originTime = System.currentTimeMillis();

    /**
     * 解析Json格式的字符串，消息格式暂定为
     * {
     * "on": true, //日志是否打开
     * "interval": 10000000, //日志开启时间（到达时间后自动关闭）
     * "frequency": 1000 //日志上传频率（每隔多长时间自动上传日志）
     * }
     *
     * @param data
     */
    public static void parseData(String data) {

        final String ON = "on";
        final String INTERVAL = "interval";
        final String FREQUENCY = "frequency";
        try {
            JSONObject dataJson = new JSONObject(data);
            boolean openLog = dataJson.getBoolean(ON);
            final long interval = dataJson.getLong(INTERVAL);
            final long frequency = dataJson.getLong(FREQUENCY);

            Log.d(TAG, "on = " + String.valueOf(openLog) +
                    " interval = " + String.valueOf(interval) +
                    " frequency = " + String.valueOf(frequency));

            if (openLog) {
                LoggerFactory.turnOnLog();
                Log.d(TAG, String.valueOf(LoggerFactory.isOn()));
                LoggerFactory.interruptThenSync();
                loopExcute(interval, frequency);
            } else {
                LoggerFactory.turnOffLog();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据设定的时间间隔，循环执行
     *
     * @param interval
     * @param frequency
     */
    private static void loopExcute(final long interval, long frequency) {
        Log.d("intervalTimeMillis", String.valueOf(interval * 1000));
        long periodTimeMillis = frequency * 1000;
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                doWork(interval);
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(task, 1000, periodTimeMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * 需要循环执行的操作
     *
     * @param interval
     */
    private static void doWork(long interval) {
        long intervalTimeMillis = interval * 1000;
        if (System.currentTimeMillis() + 1000 - originTime < intervalTimeMillis) {
            LoggerFactory.interruptThenSync();
            Log.d("timePassBy", String.valueOf(System.currentTimeMillis() - originTime));
        } else {
            LoggerFactory.turnOffLog();
        }
    }
}
