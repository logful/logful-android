package com.getui.logful.layout;

import com.getui.logful.appender.LogEvent;

public interface Layout {

    byte[] toBytes(LogEvent event);

}
