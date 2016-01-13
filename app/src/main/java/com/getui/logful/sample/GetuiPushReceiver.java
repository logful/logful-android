package com.getui.logful.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
                    LoggerFactory.parseTransmission(data);
                }
                break;
            case PushConsts.GET_CLIENTID:
                String cid = bundle.getString("clientid");
                Log.v(TAG, "Getui sdk cid: " + cid);
                break;
            default:
                break;
        }
    }

}
