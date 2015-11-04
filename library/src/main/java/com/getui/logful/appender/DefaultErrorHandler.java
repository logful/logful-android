package com.getui.logful.appender;

public class DefaultErrorHandler implements ErrorHandler {

    private final Appender appender;

    public DefaultErrorHandler(final Appender appender) {
        this.appender = appender;
    }

    @Override
    public void error(String msg) {
        // TODO
    }

    @Override
    public void error(String msg, LogEvent event, Throwable throwable) {
        // TODO
    }

    @Override
    public void error(String msg, Throwable throwable) {
        // TODO
    }
}
