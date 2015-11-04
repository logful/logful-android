package com.getui.logful.appender;

public class AbstractLogEvent implements LogEvent {

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getLoggerName() {
        return null;
    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public short getLayoutId() {
        return 0;
    }

    @Override
    public long getTimeMillis() {
        return 0;
    }

    @Override
    public String getDateString() {
        return null;
    }

    // TODO
    @Override
    public StackTraceElement getSource() {
        return null;
    }

    @Override
    public String getThreadName() {
        return null;
    }

    @Override
    public Throwable getThrown() {
        return null;
    }

    @Override
    public long getSequence() {
        return 0;
    }

    @Override
    public int getAttachmentId() {
        return -1;
    }
}
