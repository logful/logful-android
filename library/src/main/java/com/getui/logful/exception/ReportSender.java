package com.getui.logful.exception;

import android.content.Context;

public interface ReportSender {

    void send(Context context, CrashReportData crashData);

}
