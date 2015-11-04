package com.getui.logful.schedule;

import com.getui.logful.net.TransferManager;

public class UploadScheduleTask extends AbstractTask {

    public UploadScheduleTask(String name) {
        super(name);
    }

    @Override
    public void exec() {
        TransferManager.uploadLogFile();
        TransferManager.uploadCrashReport();
        TransferManager.uploadAttachment();
    }
}
