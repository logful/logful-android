package com.getui.logful.appender;

import com.getui.logful.LoggingException;

public class AppenderLoggingException extends LoggingException {

    private static final long serialVersionUID = 6545990597472958303L;

    /**
     * Construct an exception with a message.
     *
     * @param message The reason for the exception
     */
    public AppenderLoggingException(final String message) {
        super(message);
    }

    /**
     * Construct an exception with a message and underlying cause.
     *
     * @param message The reason for the exception
     * @param cause The underlying cause of the exception
     */
    public AppenderLoggingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct an exception with an underlying cause.
     *
     * @param cause The underlying cause of the exception
     */
    public AppenderLoggingException(final Throwable cause) {
        super(cause);
    }
}
