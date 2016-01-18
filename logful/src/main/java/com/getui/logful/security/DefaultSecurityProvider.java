package com.getui.logful.security;

import com.getui.logful.util.SystemConfig;
import com.getui.logful.util.UidTool;

public class DefaultSecurityProvider implements SecurityProvider {

    private byte[] passwordData;

    private byte[] saltData;

    @Override
    public byte[] password() {
        if (passwordData == null) {
            this.passwordData = SystemConfig.appKey().getBytes();
        }
        return passwordData;
    }

    @Override
    public byte[] salt() {
        if (saltData == null) {
            this.saltData = UidTool.uid().getBytes();
        }
        return saltData;
    }
}
