package com.getui.logful.schedule;

import com.getui.logful.net.TransferManager;
import com.getui.logful.util.LogUtil;

public class UploadScheduleTask extends AbstractTask {

    private static final String TAG = "UploadScheduleTask";

    public UploadScheduleTask(String name) {
        super(name);
    }

    @Override
    public void exec() {
        LogUtil.d(TAG, "Schedule upload task exec!");
        TransferManager.uploadLogFile();
        TransferManager.uploadAttachment();
        TransferManager.uploadCrashReport();
    }
}
