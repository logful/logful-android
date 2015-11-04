package com.getui.logful.entity;

public class MsgLayout {

    private long id;

    private String layout;

    public MsgLayout() {
        this.id = -1;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout.trim();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
