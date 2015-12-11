package com.getui.logful.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GetuiPushReceiver extends BroadcastReceiver {

    private static final String TAG = "GetuiPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    LoggerFactory.parseTransaction(data);
                }
                break;
            default:
                break;
        }
        */
    }
}
