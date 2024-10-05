package com.gawilive.im.event;

/**
 * Created by cxf on 2018/7/20.
 * IM登录事件
 */

public class ImLoginEvent {

    private final boolean mLogin;

    public ImLoginEvent(boolean login) {
        mLogin = login;
    }


    public boolean isLogin() {
        return mLogin;
    }
}
