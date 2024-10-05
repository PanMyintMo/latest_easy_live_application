package com.gawilive.common.event;

/**
 * 登录状态改变
 */
public class LoginChangeEvent {
    private final boolean mLogin;
    private final boolean mFirstLogin;//是否是第一次登录

    public LoginChangeEvent(boolean login, boolean firstLogin) {
        mLogin = login;
        mFirstLogin = firstLogin;
    }

    public boolean isLogin() {
        return mLogin;
    }

    public boolean isFirstLogin() {
        return mFirstLogin;
    }
}
