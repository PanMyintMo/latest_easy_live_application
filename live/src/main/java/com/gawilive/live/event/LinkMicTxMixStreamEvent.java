package com.gawilive.live.event;

/**
 * Created by cxf on 2019/3/25.
 * 腾讯连麦的时候 主播混流
 */

public class LinkMicTxMixStreamEvent {

    private final int mType;
    private final String mToStream;

    public LinkMicTxMixStreamEvent(int type, String toStream) {
        mType = type;
        mToStream = toStream;
    }

    public int getType() {
        return mType;
    }

    public String getToStream() {
        return mToStream;
    }
}
