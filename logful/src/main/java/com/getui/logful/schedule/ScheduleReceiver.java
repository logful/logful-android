package com.getui.logful.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.getui.logful.net.TransferManager;

public class ScheduleReceiver extends BroadcastReceiver {

    private static final String TAG = "ScheduleReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO
        TransferManager.uploadLogFile();
        TransferManager.uploadCrashReport();
        TransferManager.uploadAttachment();
    }
}
