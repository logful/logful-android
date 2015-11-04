package com.getui.logful.appender;

import java.io.IOException;
import java.io.OutputStream;

import com.getui.logful.layout.Layout;
import com.getui.logful.util.LogUtil;

public class OutputStreamManager extends AbstractManager {
    private static final String TAG = "OutputStreamManager";

    private volatile OutputStream outputStream;

    protected final Layout layout;

    private long fileSize;

    protected OutputStreamManager(final String filePath, final OutputStream outputStream, final Layout layout,
            final long fileSize) {
        super(filePath);
        this.outputStream = outputStream;
        this.layout = layout;
        this.fileSize = fileSize;
    }

    @Override
    protected void releaseSub() {
        close();
    }

    protected OutputStream getOutputStream() {
        return outputStream;
    }

    protected void setOutputStream(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    protected long getFileSize() {
        return fileSize;
    }

    protected void write(final byte[] bytes) {
        write(bytes, 0, bytes.length);
    }

    protected synchronized void write(final byte[] bytes, final int offset, final int length) {
        try {
            outputStream.write(bytes, offset, length);
            // 记录写入的字节长度
            fileSize += length;
        } catch (final IOException e) {
            LogUtil.e(TAG, "", e);
        }
    }

    protected synchronized void close() {
        final OutputStream stream = outputStream;
        if (stream == System.out || stream == System.err) {
            return;
        }
        try {
            stream.close();
        } catch (final IOException e) {
            LogUtil.e(TAG, "", e);
        }
    }

    public synchronized void flush() {
        try {
            outputStream.flush();
        } catch (final IOException e) {
            LogUtil.e(TAG, "", e);
        }
    }

}
