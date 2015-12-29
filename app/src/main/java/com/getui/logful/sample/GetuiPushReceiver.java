package com.getui.logful.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.getui.logful.Logger;
import com.getui.logful.LoggerFactory;
import com.igexin.sdk.PushConsts;

public class GetuiPushReceiver extends BroadcastReceiver {

    private static final String TAG = "GetuiPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    LoggerFactory.parseTransaction(data);
                }
                break;
            case PushConsts.GET_CLIENTID:
                String cid = bundle.getString("clientid");
                LoggerFactory.bindPushSdk(cid);
                break;
            default:
                break;
        }
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
