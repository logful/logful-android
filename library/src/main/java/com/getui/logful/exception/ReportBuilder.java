package com.getui.logful.exception;

public class ReportBuilder {

    private Thread thread;

    private Throwable throwable;

    public static ReportBuilder create(Thread thread, Throwable throwable) {
        ReportBuilder builder = new ReportBuilder();
        builder.thread = thread;
        builder.throwable = throwable;
        return builder;
    }

    public CrashReportData build() {
        return CrashReportDataFactory.createCrashData(thread, throwable);
    }

}
