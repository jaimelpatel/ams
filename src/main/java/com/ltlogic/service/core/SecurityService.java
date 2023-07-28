package com.ltlogic.service.core;

public interface SecurityService {
    String findLoggedInUsername();

    void autologin(String username, String password);
}
