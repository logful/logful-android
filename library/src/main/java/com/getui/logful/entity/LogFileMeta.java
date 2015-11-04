package com.getui.logful.entity;

import com.getui.logful.LoggerConstants;
import com.getui.logful.appender.LogEvent;

public class LogFileMeta {

    private long id;

    /**
     * Logger 名称标签.
     */
    private String loggerName;

    /**
     * 日志文件名称.
     */
    private String filename;

    /**
     * 日志文件序号.
     */
    private int fragment;

    /**
     * 日志文件状态 1 未上传 2 待上传 3 已上传 4 已删除.
     */
    private int status;

    /**
     * 日志文件 MD5.
     */
    private String fileMD5;

    /**
     * 文件存储位置（内部或者外部存储）.
     */
    private int location;

    /**
     * 日志文件级别.
     */
    private int level;

    /**
     * 日志文件创建时间.
     */
    private long createTime;

    /**
     * 日志文件删除时间.
     */
    private long deleteTime;

    /**
     * 日志文件是否已经达到最大容量.
     */
    private boolean eof;

    public LogFileMeta() {
        this.id = -1;
        this.status = LoggerConstants.STATE_NORMAL;
        this.createTime = System.currentTimeMillis();
        this.eof = false;
        this.location = LoggerConstants.LOCATION_EXTERNAL;
    }

    public static LogFileMeta createMeta(LogEvent logEvent, String filename, int fragment) {
        LogFileMeta meta = new LogFileMeta();
        meta.setLoggerName(logEvent.getLoggerName());
        meta.setLevel(logEvent.getLevel());
        meta.setFilename(filename);
        meta.setFragment(fragment);
        return meta;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String mLoggerName) {
        this.loggerName = mLoggerName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getFragment() {
        return fragment;
    }

    public void setFragment(int fragment) {
        this.fragment = fragment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public void setFileMD5(String fileMD5) {
        this.fileMD5 = fileMD5;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(long deleteTime) {
        this.deleteTime = deleteTime;
    }

    public boolean isEof() {
        return eof;
    }

    public void setEof(boolean eof) {
        this.eof = eof;
    }

}
