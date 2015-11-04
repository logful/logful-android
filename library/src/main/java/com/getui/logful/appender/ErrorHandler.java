package com.getui.logful.appender;

public interface ErrorHandler {

    void error(String msg);

    void error(String msg, LogEvent event, Throwable throwable);

    void error(String msg, Throwable throwable);

}
