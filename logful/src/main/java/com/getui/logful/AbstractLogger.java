package com.getui.logful;

public abstract class AbstractLogger implements Logger {

    private String mFileTag;

    public AbstractLogger(String fileTag) {
        this.mFileTag = fileTag;
    }

    @Override
    public String getName() {
        return mFileTag;
    }

    @Override
    public void setMsgLayout(String layout) {

    }

    @Override
    public void recordLogLevel(int... levels) {

    }

    @Override
    public void verbose(String tag, String msg) {

    }

    @Override
    public void debug(String tag, String msg) {

    }

    @Override
    public void info(String tag, String msg) {

    }

    @Override
    public void warn(String tag, String msg) {

    }

    @Override
    public void error(String tag, String msg) {

    }

    @Override
    public void exception(String tag, String msg, Throwable throwable) {

    }

    @Override
    public void fatal(String tag, String msg) {

    }
}
