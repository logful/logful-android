package com.getui.logful.appender;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.getui.logful.layout.Layout;
import com.getui.logful.util.LogUtil;

public class FileManager extends OutputStreamManager {

    private static final FileManagerFactory FACTORY = new FileManagerFactory();

    private final boolean append;

    private final boolean locking;

    private final int bufferSize;

    protected FileManager(final String filePath, final OutputStream os, final boolean append, final boolean locking,
            final Layout layout, final int bufferSize, final long fileSize) {
        super(filePath, os, layout, fileSize);
        this.append = append;
        this.locking = locking;
        this.bufferSize = bufferSize;
    }

    public static FileManager getFileManager(final String filePath, final boolean append, boolean locking,
            final boolean bufferedIo, final Layout layout, final int bufferSize) {
        if (locking && bufferedIo) {
            locking = false;
        }
        return getManager(filePath, FACTORY, new FactoryData(append, locking, bufferedIo, bufferSize, layout));
    }

    public boolean isAppend() {
        return append;
    }

    public boolean isLocking() {
        return locking;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    private static class FactoryData {
        private final boolean append;
        private final boolean locking;
        private final boolean bufferedIO;
        private final int bufferSize;
        private final Layout layout;

        /**
         * Constructor.
         *
         * @param append Append status.
         * @param locking Locking status.
         * @param bufferedIO Buffering flag.
         * @param bufferSize Buffer size.
         */
        public FactoryData(final boolean append, final boolean locking, final boolean bufferedIO, final int bufferSize,
                final Layout layout) {
            this.append = append;
            this.locking = locking;
            this.bufferedIO = bufferedIO;
            this.bufferSize = bufferSize;
            this.layout = layout;
        }
    }

    private static class FileManagerFactory implements ManagerFactory<FileManager, FactoryData> {

        @Override
        public FileManager createManager(String filePath, FactoryData data) {
            final File file = new File(filePath);
            final File parent = file.getParentFile();
            if (null != parent && !parent.exists()) {
                boolean result = parent.mkdirs();
            }
            OutputStream outputStream;
            // Start file size.
            long fileSize = file.length();
            try {
                outputStream = new FileOutputStream(filePath, data.append);
                int bufferSize = data.bufferSize;
                if (data.bufferedIO) {
                    outputStream = new BufferedOutputStream(outputStream, bufferSize);
                } else {
                    bufferSize = -1;
                }
                return new FileManager(filePath, outputStream, data.append, data.locking, data.layout, bufferSize,
                        fileSize);
            } catch (final FileNotFoundException e) {
                LogUtil.e("FileManagerFactory", "", e);
            }
            return null;
        }
    }

}
