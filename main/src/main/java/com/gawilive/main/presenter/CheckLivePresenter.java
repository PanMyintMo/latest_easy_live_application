package com.gawilive.main.presenter;

import android.app.Dialog;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.DialogUitl;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.live.activity.LiveAudienceActivity;
import com.gawilive.live.bean.LiveBean;
import com.gawilive.live.dialog.LiveRoomCheckDialogFragment2;
import com.gawilive.live.http.LiveHttpConsts;
import com.gawilive.live.http.LiveHttpUtil;
import com.gawilive.main.R;
import com.gawilive.main.activity.TeenagerActivity;

/**
 * Created by cxf on 2017/9/29.
 */

public class CheckLivePresenter {

    private Context mContext;
    private LiveBean mLiveBean;//选中的直播间信息
    private String mKey;
    private int mPosition;
    private int mLiveType;//直播间的类型  普通 密码 门票 计时等
    private int mLiveTypeVal;//收费价格等
    private String mLiveTypeMsg;//直播间提示信息或房间密码
    private int mLiveSdk;
    private HttpCallback mCheckLiveCallback;
    private boolean mIsVoiceRoom;

    public CheckLivePresenter(Context context) {
        mContext = context;
    }


    /**
     * 观众 观看直播
     */
    public void watchLive(LiveBean bean) {
        watchLive(bean, "", 0);
    }

    /**
     * 观众 观看直播
     */
    public void watchLive(LiveBean bean, String key, int position) {
        mLiveBean = bean;
        mKey = key;
        mPosition = position;
        if (mCheckLiveCallback == null) {
            mCheckLiveCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        if (info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            mIsVoiceRoom = obj.getIntValue("live_type") == 1;
                            mLiveType = obj.getIntValue("type");
                            mLiveTypeVal = obj.getIntValue("type_val");
                            mLiveTypeMsg = obj.getString("type_msg");
                            if (mIsVoiceRoom) {
                                mLiveSdk = Constants.LIVE_SDK_TX;
                            } else {
                                if (CommonAppConfig.LIVE_SDK_CHANGED) {
                                    mLiveSdk = obj.getIntValue("live_sdk");
                                } else {
                                    mLiveSdk = CommonAppConfig.LIVE_SDK_USED;
                                }
                            }
                            if (mLiveType == Constants.LIVE_TYPE_NORMAL) {
                                forwardNormalRoom();
                            } else {
                                if (CommonAppConfig.getInstance().isTeenagerType()) {
                                    if (mLiveType == Constants.LIVE_TYPE_PAY || mLiveType == Constants.LIVE_TYPE_TIME) {
                                        new DialogUitl.Builder(mContext)
                                                .setContent(WordUtil.getString(R.string.a_137))
                                                .setCancelString(WordUtil.getString(R.string.know))
                                                .setConfrimString(WordUtil.getString(R.string.a_118))
                                                .setCancelable(true)
                                                .setBackgroundDimEnabled(true)
                                                .setClickCallback(new DialogUitl.SimpleCallback() {


                                                    @Override
                                                    public void onConfirmClick(Dialog dialog, String content) {
                                                        TeenagerActivity.forward(mContext);
                                                    }
                                                })
                                                .build()
                                                .show();

                                        return;
                                    }
                                }
                                LiveRoomCheckDialogFragment2 fragment = new LiveRoomCheckDialogFragment2();
                                if (mLiveType == Constants.LIVE_TYPE_PWD) {
                                    fragment.setLiveType(mLiveType, mLiveTypeMsg);
                                } else {
                                    fragment.setLiveType(mLiveType, String.valueOf(mLiveTypeVal));
                                }
                                fragment.setActionListener(new LiveRoomCheckDialogFragment2.ActionListener() {
                                    @Override
                                    public void onConfirmClick() {
                                        if (mLiveType == Constants.LIVE_TYPE_PWD) {
                                            forwardNormalRoom();
                                        } else {
                                            if (((AbsActivity) mContext).checkLogin()) {
                                                roomCharge();
                                            }
                                        }
                                    }
                                });
                                fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "LiveRoomCheckDialogFragment2");
                            }
                        }
                    } else {
                        ToastUtil.show(msg);
                    }
                }

                @Override
                public boolean showLoadingDialog() {
                    return true;
                }

                @Override
                public Dialog createLoadingDialog() {
                    return DialogUitl.loadingDialog(mContext);
                }
            };
        }
        LiveHttpUtil.checkLive(bean.getUid(), bean.getStream(), mCheckLiveCallback);
    }


    /**
     * 前往普通房间
     */
    private void forwardNormalRoom() {
        forwardLiveAudienceActivity();
    }


    public void roomCharge() {
        LiveHttpUtil.roomCharge(mLiveBean.getUid(), mLiveBean.getStream(), mRoomChargeCallback);
    }

    private HttpCallback mRoomChargeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                forwardLiveAudienceActivity();
            } else {
                ToastUtil.show(msg);
            }
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }

        @Override
        public Dialog createLoadingDialog() {
            return DialogUitl.loadingDialog(mContext);
        }
    };

    public void cancel() {
        LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
    }

    /**
     * 跳转到直播间
     */
    private void forwardLiveAudienceActivity() {
        if (mIsVoiceRoom) {
            LiveAudienceActivity.forward(mContext, mLiveBean, mLiveType, mLiveTypeVal, "", 0, mLiveSdk, true);
        } else {
            LiveAudienceActivity.forward(mContext, mLiveBean, mLiveType, mLiveTypeVal, mKey, mPosition, mLiveSdk, false);
        }
    }
}
