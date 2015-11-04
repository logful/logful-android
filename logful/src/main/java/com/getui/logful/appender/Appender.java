package com.getui.logful.appender;

import com.getui.logful.LifeCycle;
import com.getui.logful.layout.Layout;

public interface Appender extends LifeCycle {

    void append(LogEvent event);

    ErrorHandler getHandler();

    Layout getLayout();

    String getLoggerName();

    boolean ignoreExceptions();

    void setHandler(ErrorHandler handler);

    int fragment();

    boolean writing();

    boolean writeable();
}
