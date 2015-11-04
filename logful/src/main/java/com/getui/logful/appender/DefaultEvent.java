package com.getui.logful.appender;

import java.util.concurrent.atomic.AtomicLong;

import com.getui.logful.db.DatabaseManager;
import com.getui.logful.util.DateTimeUtil;

public class DefaultEvent extends AbstractLogEvent {

    static final AtomicLong seq = new AtomicLong(0);

    private long sequence;

    /**
     * Logger 名称.
     */
    private String loggerName;

    /**
     * 日志打印优先级.
     */
    private int priority;

    /**
     * 日志级别.
     */
    private int level;

    /**
     * 日志 tag.
     */
    private String tag;

    /**
     * 日志内容.
     */
    private String message;

    /**
     * 日志时间戳.
     */
    private final long timestamp;

    private String layoutString;

    private int attachmentId;

    protected DefaultEvent() {
        this.sequence = seq.getAndIncrement();
        this.timestamp = System.currentTimeMillis();
        this.attachmentId = -1;
    }

    public static DefaultEvent createEvent(final String loggerName, final int level, final String tag,
            final String msg, final String layoutString) {
        return createEvent(loggerName, level, tag, msg, layoutString, -1);
    }

    public static DefaultEvent createEvent(final String loggerName, final int level, final String tag,
            final String msg, final String layoutString, final int attachmentId) {
        DefaultEvent event = new DefaultEvent();
        event.loggerName = loggerName;
        event.tag = tag;
        event.message = msg;
        event.layoutString = layoutString;

        event.priority = level;
        event.level = level;

        event.attachmentId = attachmentId;
        return event;
    }

    @Override
    public long getSequence() {
        return sequence;
    }

    @Override
    public int getAttachmentId() {
        return attachmentId;
    }

    @Override
    public String getLoggerName() {
        return loggerName;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public short getLayoutId() {
        return DatabaseManager.layoutId(layoutString);
    }

    @Override
    public long getTimeMillis() {
        return timestamp;
    }

    @Override
    public String getDateString() {
        return DateTimeUtil.timeString(timestamp);
    }
}
