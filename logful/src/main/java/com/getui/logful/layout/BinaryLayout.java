package com.getui.logful.layout;

import com.getui.logful.appender.LogEvent;
import com.getui.logful.util.BytesUtils;
import com.getui.logful.util.CryptoTool;

public class BinaryLayout extends AbstractLayout {

    @Override
    public synchronized byte[] toBytes(LogEvent logEvent) {
        // Time chunk
        byte[] timeChunk = BytesUtils.longToBytes(logEvent.getTimeMillis());
        short timeLen = (short) timeChunk.length;
        byte[] timeLenChunk = BytesUtils.shortToBytes(timeLen);

        // Tag chunk
        byte[] tagChunk = CryptoTool.AESEncrypt(logEvent.getTag());
        short tagLen = (short) tagChunk.length;
        byte[] tagLenChunk = BytesUtils.shortToBytes(tagLen);

        // Message chunk
        byte[] msgChunk = CryptoTool.AESEncrypt(logEvent.getMessage());
        short msgLen = (short) msgChunk.length;
        byte[] msgLenChunk = BytesUtils.shortToBytes(msgLen);

        // Layout id chunk
        byte[] layoutIdChunk = BytesUtils.shortToBytes(logEvent.getLayoutId());
        short layoutIdLen = (short) layoutIdChunk.length;
        byte[] layoutIdLenChunk = BytesUtils.shortToBytes(layoutIdLen);

        // EOF chunk
        short eofValue = -100;
        byte[] eofChunk = BytesUtils.shortToBytes(eofValue);

        int attachmentId = logEvent.getAttachmentId();
        if (attachmentId == -1) {
            return BytesUtils.addAll(timeLenChunk, timeChunk, tagLenChunk, tagChunk, msgLenChunk, msgChunk,
                    layoutIdLenChunk, layoutIdChunk, eofChunk);
        } else {
            // Attachment id chunk
            byte[] attachmentIdChunk = BytesUtils.intToBytes(attachmentId);
            short attachmentIdLen = (short) attachmentIdChunk.length;
            byte[] attachmentIdLenChunk = BytesUtils.shortToBytes(attachmentIdLen);

            return BytesUtils.addAll(timeLenChunk, timeChunk, tagLenChunk, tagChunk, msgLenChunk, msgChunk,
                    layoutIdLenChunk, layoutIdChunk, attachmentIdLenChunk, attachmentIdChunk, eofChunk);

        }
    }
}
