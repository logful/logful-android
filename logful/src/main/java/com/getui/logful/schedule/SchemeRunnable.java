package com.getui.logful.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class SchemeRunnable implements Runnable {

    private final ConcurrentSkipListMap<String, AbstractTask> mTaskMaps = new ConcurrentSkipListMap<String, AbstractTask>();

    public void addTask(AbstractTask task) {
        mTaskMaps.put(task.getName(), task);
    }

    public void clearTask() {
        mTaskMaps.clear();
    }

    @Override
    public void run() {
        for (Map.Entry<String, AbstractTask> entry : mTaskMaps.entrySet()) {
            AbstractTask task = entry.getValue();
            task.start();
            task.exec();
            task.finish();
        }
    }
}
