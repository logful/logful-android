package com.getui.logful.schedule;

public abstract class AbstractTask implements ScheduleTask {

    private static final String TAG = AbstractTask.class.getSimpleName();

    private String mName;

    public AbstractTask(String name) {
        this.mName = name;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void start() {

    }

    @Override
    public void exec() {

    }

    @Override
    public void finish() {

    }
}
