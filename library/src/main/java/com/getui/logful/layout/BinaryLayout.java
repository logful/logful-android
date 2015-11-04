package com.getui.logful.layout;

import com.getui.logful.appender.LogEvent;
import com.getui.logful.util.BytesUtil;
import com.getui.logful.util.CryptoTool;

public class BinaryLayout extends AbstractLayout {

    @Override
    public synchronized byte[] toBytes(LogEvent logEvent) {
        // Time chunk
        byte[] timeChunk = BytesUtil.longToBytes(logEvent.getTimeMillis());
        short timeLen = (short) timeChunk.length;
        byte[] timeLenChunk = BytesUtil.shortToBytes(timeLen);

        // Tag chunk
        byte[] tagChunk = CryptoTool.aesEncrypt(logEvent.getTag());
        short tagLen = (short) tagChunk.length;
        byte[] tagLenChunk = BytesUtil.shortToBytes(tagLen);

        // Message chunk
        byte[] msgChunk = CryptoTool.aesEncrypt(logEvent.getMessage());
        short msgLen = (short) msgChunk.length;
        byte[] msgLenChunk = BytesUtil.shortToBytes(msgLen);

        // Layout id chunk
        byte[] layoutIdChunk = BytesUtil.shortToBytes(logEvent.getLayoutId());
        short layoutIdLen = (short) layoutIdChunk.length;
        byte[] layoutIdLenChunk = BytesUtil.shortToBytes(layoutIdLen);

        // EOF chunk
        short eofValue = -100;
        byte[] eofChunk = BytesUtil.shortToBytes(eofValue);

        int attachmentId = logEvent.getAttachmentId();
        if (attachmentId == -1) {
            return BytesUtil.addAll(timeLenChunk, timeChunk, tagLenChunk, tagChunk, msgLenChunk, msgChunk,
                    layoutIdLenChunk, layoutIdChunk, eofChunk);
        } else {
            // Attachment id chunk
            byte[] attachmentIdChunk = BytesUtil.intToBytes(attachmentId);
            short attachmentIdLen = (short) attachmentIdChunk.length;
            byte[] attachmentIdLenChunk = BytesUtil.shortToBytes(attachmentIdLen);

            return BytesUtil.addAll(timeLenChunk, timeChunk, tagLenChunk, tagChunk, msgLenChunk, msgChunk,
                    layoutIdLenChunk, layoutIdChunk, attachmentIdLenChunk, attachmentIdChunk, eofChunk);

        }
    }
}
