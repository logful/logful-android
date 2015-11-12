package com.getui.logful.appender;

import com.getui.logful.layout.Layout;

public final class FileAppender extends AbstractOutputStreamAppender<FileManager> {

    private final long capacity;

    private final int fileFragment;

    /**
     * FileAppender.
     *
     * @param loggerName Logger name {@link com.getui.log.Logger}
     * @param layout Layout to format {@link LogEvent}
     * @param manager FileManager {@link FileManager}
     * @param ignoreExceptions Ignore exceptions
     * @param immediateFlush Immediate flush
     * @param capacity Capacity
     * @param fragment Fragment
     */
    private FileAppender(final String loggerName, final Layout layout, final FileManager manager,
            final boolean ignoreExceptions, final boolean immediateFlush, final long capacity, final int fragment) {
        super(loggerName, layout, ignoreExceptions, immediateFlush, manager);
        this.capacity = capacity;
        this.fileFragment = fragment;
    }

    public static FileAppender createAppender(final String loggerName, final String filePath, final Layout layout,
            final long capacity, final int fragment) {
        return FileAppender.createAppender(loggerName, filePath, true, false, true, true, true, 8192, layout, capacity,
                fragment);
    }

    public static FileAppender createAppender(final String loggerName, final String filePath, final boolean isAppend,
            final boolean isLocking, final boolean isBuffered, final boolean immediateFlush, final boolean ignore,
            final int bufferSize, final Layout layout, final long capacity, final int fragment) {
        final FileManager manager =
                FileManager.getFileManager(filePath, isAppend, isLocking, isBuffered, layout, bufferSize);
        if (manager == null) {
            return null;
        }
        return new FileAppender(loggerName, layout, manager, ignore, immediateFlush, capacity, fragment);
    }

    @Override
    public int fragment() {
        return fileFragment;
    }

    /**
     * 判断是否已经达到最大容量.
     * 
     * @return 是否达到最大容量
     */
    @Override
    public boolean writeable() {
        long fileSize = getManager().getFileSize();
        return fileSize < capacity;
    }
}
