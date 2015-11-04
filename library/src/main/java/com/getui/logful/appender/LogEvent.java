package com.getui.logful.appender;

public interface LogEvent {

    int getLevel();

    int getPriority();

    String getLoggerName();

    String getTag();

    String getMessage();

    short getLayoutId();

    long getTimeMillis();

    String getDateString();

    StackTraceElement getSource();

    String getThreadName();

    Throwable getThrown();

    long getSequence();

    int getAttachmentId();
}
