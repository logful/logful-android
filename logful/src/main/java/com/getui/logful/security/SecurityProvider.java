package com.getui.logful.security;

public interface SecurityProvider {

    byte[] password();

    byte[] salt();

}
