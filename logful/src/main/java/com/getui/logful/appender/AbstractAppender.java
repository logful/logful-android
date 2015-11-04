package com.getui.logful.appender;

import com.getui.logful.AbstractLifeCycle;
import com.getui.logful.layout.Layout;

public abstract class AbstractAppender extends AbstractLifeCycle implements Appender {

    private ErrorHandler handler = new DefaultErrorHandler(this);

    private final String loggerName;

    private final Layout layout;

    private final boolean isIgnoreExceptions;

    private final int fragment;

    protected AbstractAppender(final String loggerName, final Layout layout, boolean ignoreExceptions) {
        this.loggerName = loggerName;
        this.layout = layout;
        this.isIgnoreExceptions = ignoreExceptions;
        this.fragment = 0;
    }

    public void error(final String msg) {
        handler.error(msg);
    }

    public void error(final String msg, final LogEvent event, final Throwable throwable) {
        handler.error(msg, event, throwable);
    }

    public void error(final String msg, final Throwable throwable) {
        handler.error(msg, throwable);
    }

    @Override
    public ErrorHandler getHandler() {
        return handler;
    }

    @Override
    public Layout getLayout() {
        return layout;
    }

    @Override
    public String getLoggerName() {
        return loggerName;
    }

    @Override
    public boolean ignoreExceptions() {
        return isIgnoreExceptions;
    }

    @Override
    public void setHandler(ErrorHandler handler) {
        if (handler == null) {
            return;
            // TODO
        }
        if (isStarted()) {
            return;
            // TODO
        }
        this.handler = handler;
    }

    @Override
    public int fragment() {
        return fragment;
    }

}
