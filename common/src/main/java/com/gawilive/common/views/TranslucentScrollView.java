package com.gawilive.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 功能名称：滑动渐变scrollview
 *
 * @author wudonglin
 * @class
 * @date 2022/3/3 10:09 上午
 * @updateUser
 * @updateData
 **/
public class TranslucentScrollView extends ScrollView {
    private OnScrollChangedListener mOnScrollChangedListener;

    public TranslucentScrollView(Context context) {
        super(context);
    }

    public TranslucentScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TranslucentScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

    public interface OnScrollChangedListener {
        void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
    }
}
