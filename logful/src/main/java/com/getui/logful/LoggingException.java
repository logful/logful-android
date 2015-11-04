package com.getui.logful;

public class LoggingException extends RuntimeException {

    private static final long serialVersionUID = 6366395965071580537L;

    public LoggingException(final String message) {
        super(message);
    }

    public LoggingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public LoggingException(final Throwable cause) {
        super(cause);
    }

}
