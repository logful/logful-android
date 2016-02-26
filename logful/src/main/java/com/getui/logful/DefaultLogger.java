package com.getui.logful;

import com.getui.logful.appender.AsyncAppenderManager;
import com.getui.logful.appender.DefaultEvent;
import com.getui.logful.util.CaptureUtils;
import com.getui.logful.util.StringUtils;
import com.getui.logful.util.VerifyMsgLayout;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class DefaultLogger extends AbstractLogger {

    private int[] recordLogLevels;
    private String msgLayout;

    public DefaultLogger(String fileTag) {
        super(fileTag);
        this.recordLogLevels =
                new int[] {Constants.VERBOSE, Constants.DEBUG, Constants.INFO, Constants.WARN, Constants.ERROR,
                        Constants.EXCEPTION, Constants.FATAL};
    }

    @Override
    public boolean isEnabled(int level) {
        for (int logLevel : recordLogLevels) {
            if (level == logLevel) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setMsgLayout(String layout) {
        // 验证日志内容格式模板时候配置正确
        VerifyMsgLayout.verify(layout);
        this.msgLayout = layout;
    }

    @Override
    public String getMsgLayout() {
        return msgLayout;
    }

    @Override
    public void recordLogLevel(int... levels) {
        this.recordLogLevels = levels;
    }

    @Override
    public void verbose(String tag, String msg) {
        logMessage(Constants.VERBOSE, tag, msg, false);
    }

    @Override
    public void verbose(String tag, String msg, boolean capture) {
        logMessage(Constants.VERBOSE, tag, msg, capture);
    }

    @Override
    public void debug(String tag, String msg) {
        logMessage(Constants.DEBUG, tag, msg, false);
    }

    @Override
    public void debug(String tag, String msg, boolean capture) {
        logMessage(Constants.DEBUG, tag, msg, capture);
    }

    @Override
    public void info(String tag, String msg) {
        logMessage(Constants.INFO, tag, msg, false);
    }

    @Override
    public void info(String tag, String msg, boolean capture) {
        logMessage(Constants.INFO, tag, msg, capture);
    }

    @Override
    public void warn(String tag, String msg) {
        logMessage(Constants.WARN, tag, msg, false);
    }

    @Override
    public void warn(String tag, String msg, boolean capture) {
        logMessage(Constants.WARN, tag, msg, capture);
    }

    @Override
    public void error(String tag, String msg) {
        logMessage(Constants.ERROR, tag, msg, false);
    }

    @Override
    public void error(String tag, String msg, boolean capture) {
        logMessage(Constants.ERROR, tag, msg, capture);
    }

    @Override
    public void exception(String tag, String msg, Throwable throwable) {
        exception(tag, msg, throwable, false);
    }

    @Override
    public void exception(String tag, String msg, Throwable throwable, boolean capture) {
        if (StringUtils.isEmpty(tag)) {
            return;
        }
        if (throwable != null) {
            StringBuilder builder = new StringBuilder();

            if (msg.length() > 0) {
                String temp = msg + "|\n";
                builder.append(temp);
            }

            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);

            // If the exception was thrown in a background thread inside
            // AsyncTask, then the actual exception can be found with getCause
            Throwable cause = throwable;
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            final String stacktraceString = result.toString();
            printWriter.close();

            builder.append(stacktraceString);

            logMessage(Constants.EXCEPTION, tag, builder.toString(), capture);
        } else {
            logMessage(Constants.EXCEPTION, tag, msg, capture);
        }
    }

    @Override
    public void fatal(String tag, String msg) {
        logMessage(Constants.FATAL, tag, msg, false);
    }

    @Override
    public void fatal(String tag, String msg, boolean capture) {
        logMessage(Constants.FATAL, tag, msg, capture);
    }

    private void logMessage(int level, String tag, String message, boolean capture) {
        if (StringUtils.isEmpty(tag) || StringUtils.isEmpty(message)) {
            return;
        }
        if (!isEnabled(level)) {
            return;
        }

        if (capture) {
            CaptureUtils.captureThenLog(this, level, tag, message);
        } else {
            DefaultEvent event = DefaultEvent.createEvent(getName(), level, tag, message, msgLayout);
            AsyncAppenderManager.manager().append(event);
        }
    }
}
