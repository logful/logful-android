package com.getui.logful.schedule;

public interface ScheduleTask {

    String getName();

    void start();

    void exec();

    void finish();
}
