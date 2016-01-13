package com.getui.logful.security;

import com.getui.logful.util.SystemConfig;
import com.getui.logful.util.UidTool;

public class DefaultSecurityProvider implements SecurityProvider {

    private byte[] passwordData;

    private byte[] saltData;

    public DefaultSecurityProvider() {
        this.passwordData = SystemConfig.appKey().getBytes();
        this.saltData = UidTool.uid().getBytes();
    }

    @Override
    public byte[] password() {
        return passwordData;
    }

    @Override
    public byte[] salt() {
        return saltData;
    }
}
