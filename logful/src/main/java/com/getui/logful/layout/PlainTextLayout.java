package com.getui.logful.layout;

import java.io.UnsupportedEncodingException;

import com.getui.logful.LoggerConstants;
import com.getui.logful.appender.LogEvent;
import com.getui.logful.util.CryptoTool;
import com.getui.logful.util.LogUtil;

public class PlainTextLayout extends AbstractLayout {

    @Override
    public synchronized byte[] toBytes(LogEvent logEvent) {
        byte[] tagBytes = CryptoTool.aesEncrypt(logEvent.getTag());
        byte[] msgBytes = CryptoTool.aesEncrypt(logEvent.getMessage());

        String line = logEvent.getDateString() + "|" + logEvent.getTimeMillis();

        try {
            String tag = new String(tagBytes, "UTF-8");
            line = line + "|" + tag;

            String msg = new String(msgBytes, "UTF-8");
            line = line + "|" + msg;
        } catch (UnsupportedEncodingException e) {
            LogUtil.e("PlainTextLayout", "", e);
        }

        line = line + "|" + logEvent.getLayoutId() + "\n";

        try {
            return line.getBytes(LoggerConstants.CHARSET);
        } catch (UnsupportedEncodingException e) {
            return new byte[] {};
        }
    }
}
