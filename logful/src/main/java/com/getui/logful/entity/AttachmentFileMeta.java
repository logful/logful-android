package com.getui.logful.entity;

import com.getui.logful.LoggerConstants;

public class AttachmentFileMeta {

    private long id;

    private String filename;

    private int location;

    private int sequence;

    private long createTime;

    private long deleteTime;

    private int status;

    private String fileMD5;

    public AttachmentFileMeta() {
        this.id = -1;
        this.status = LoggerConstants.STATE_NORMAL;
        this.createTime = System.currentTimeMillis();
        this.location = LoggerConstants.LOCATION_EXTERNAL;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public void setFileMD5(String fileMD5) {
        this.fileMD5 = fileMD5;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(long deleteTime) {
        this.deleteTime = deleteTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
