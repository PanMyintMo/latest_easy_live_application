package com.gawilive.main.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.main.utils.permission.PermissionInterceptor;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.project.codeinstallsdk.CodeInstall;
import com.project.codeinstallsdk.inter.ReceiptCallBack;
import com.project.codeinstallsdk.model.Transfer;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.adapter.ViewPagerAdapter;
import com.gawilive.common.bean.ConfigBean;
import com.gawilive.common.custom.TabButtonGroup;
import com.gawilive.common.dialog.NotCancelableInputDialog;
import com.gawilive.common.event.LoginChangeEvent;
import com.gawilive.common.http.CommonHttpConsts;
import com.gawilive.common.http.CommonHttpUtil;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.http.HttpClient;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.interfaces.PermissionCallback;
import com.gawilive.common.utils.DialogUitl;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.common.utils.FloatWindowHelper;
import com.gawilive.common.utils.L;
import com.gawilive.common.utils.LocationUtil;
import com.gawilive.common.utils.PermissionUtil;
import com.gawilive.common.utils.RouteUtil;
import com.gawilive.common.utils.SpUtil;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.VersionUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.im.activity.ChatActivity;
import com.gawilive.im.event.ImUnReadCountEvent;
import com.gawilive.im.utils.ImPushUtil;
import com.gawilive.live.LiveConfig;
import com.gawilive.live.activity.LiveAnchorActivity;
import com.gawilive.live.bean.LiveBean;
import com.gawilive.live.bean.LiveConfigBean;
import com.gawilive.live.event.LiveAudienceVoiceExitEvent;
import com.gawilive.live.event.LiveAudienceVoiceOpenEvent;
import com.gawilive.live.floatwindow.FloatWindowUtil;
import com.gawilive.live.http.LiveHttpConsts;
import com.gawilive.live.http.LiveHttpUtil;
import com.gawilive.live.utils.LiveStorge;
import com.gawilive.live.views.LiveVoicePlayUtil;
import com.gawilive.main.R;
import com.gawilive.main.bean.BonusBean;
import com.gawilive.main.dialog.MainStartDialogFragment;
import com.gawilive.main.dialog.TeenagerDialogFragment;
import com.gawilive.main.http.MainHttpConsts;
import com.gawilive.main.http.MainHttpUtil;
import com.gawilive.main.interfaces.MainAppBarLayoutListener;
import com.gawilive.main.interfaces.MainStartChooseCallback;
import com.gawilive.main.presenter.CheckLivePresenter;
import com.gawilive.main.views.*;
import com.gawilive.video.activity.VideoRecordActivity;
import com.gawilive.video.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AbsActivity implements MainAppBarLayoutListener {

    private ViewGroup mRootView;
    private TabButtonGroup mTabButtonGroup;
    private ViewPager mViewPager;
    private List<FrameLayout> mViewList;
    private MainHomeViewHolder mHomeViewHolder;
    private MainActiveViewHolder mActiveViewHolder;
    private MainMallViewHolder mMallViewHolder;
    private MainMeViewNewHolder mMeViewHolder;
    private AbsMainViewHolder[] mViewHolders;
    private View mBottom;
    private int mDp70;
    private ObjectAnimator mUpAnimator;//向上动画
    private ObjectAnimator mDownAnimator;//向下动画
    private boolean mAnimating;
    private boolean mShowed = true;
    private boolean mHided;

    private CheckLivePresenter mCheckLivePresenter;
    private boolean mFristLoad;
    private long mLastClickBackTime;//上次点击back键的时间
    private AudioManager mAudioManager;
    private boolean mFirstLogin;//是否是第一次登录
    private Handler mHandler;
    private boolean mLoginChanged;
    private Runnable mTeengerTicker;
    private HttpCallback mTeenagerTimeCallback;
    private boolean mIsTeengerTick;
    public BonusViewHolder mBonusViewHolder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void main() {
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        Intent intent = getIntent();
        mFirstLogin = intent.getBooleanExtra(Constants.FIRST_LOGIN, false);
        mRootView = (ViewGroup) findViewById(R.id.rootView);
        mTabButtonGroup = (TabButtonGroup) findViewById(R.id.tab_group);
        mTabButtonGroup.setClickIntercepter(new TabButtonGroup.ClickIntercepter() {
            @Override
            public boolean needIntercept(int position) {
                if (position == 3 && !CommonAppConfig.getInstance().isLogin()) {
                    RouteUtil.forwardLogin(mContext);
                    return true;
                }
                return false;
            }
        });
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(4);
        mViewList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                loadPageData(position, true);
                if (mViewHolders != null) {
                    for (int i = 0, length = mViewHolders.length; i < length; i++) {
                        AbsMainViewHolder vh = mViewHolders[i];
                        if (vh != null) {
                            vh.setShowed(position == i);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabButtonGroup.setViewPager(mViewPager);
        mViewHolders = new AbsMainViewHolder[4];
        mDp70 = DpUtil.dp2px(70);
        mBottom = findViewById(R.id.bottom);
        mUpAnimator = ObjectAnimator.ofFloat(mBottom, "translationY", mDp70, 0);
        mDownAnimator = ObjectAnimator.ofFloat(mBottom, "translationY", 0, mDp70);
        mUpAnimator.setDuration(250);
        mDownAnimator.setDuration(250);
        mUpAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
                mShowed = true;
                mHided = false;
            }
        });
        mDownAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
                mShowed = false;
                mHided = true;
            }
        });

        EventBus.getDefault().register(this);
        checkVersion();
        requestAfterLogin();
        CommonAppConfig.getInstance().setLaunched(true);
        mFristLoad = true;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean exit = intent.getBooleanExtra(Constants.EXIT, false);
            if (exit) {
                finish();
            } else {
                if (mTabButtonGroup != null) {
                    mTabButtonGroup.btnPerformClick(0);
                }
                if (CommonAppConfig.getInstance().isTeenagerType()) {
                    startTeenagerCountdown();
                    if (mBonusViewHolder != null) {
                        mBonusViewHolder.dismiss();
                        mBonusViewHolder = null;
                    }
                } else {
                    mIsTeengerTick = false;
                    if (mHandler != null) {
                        mHandler.removeCallbacks(mTeengerTicker);
                    }
                }
            }
        }
    }

    /**
     * 登录后请求接口
     */
    private void requestAfterLogin() {
        if (CommonAppConfig.getInstance().isLogin()) {
            getAgentCode();//邀请码弹窗
            loginIM();//登录IM
            ImPushUtil.getInstance().resumePush();//登录推送
            CommonHttpUtil.updatePushId(ImPushUtil.getInstance().getPushID());//更新推送Id
            checkTeenager();
        }
    }


    public void mainClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_rank) {
            RankActivity.forward(mContext, 0);
            return;
        }
        if (!checkLogin()) {
            return;
        }
        if (i == R.id.btn_start) {
            showStartDialog();
        } else if (i == R.id.btn_search) {
            SearchActivity.forward(mContext);
        } else if (i == R.id.btn_msg) {
            ChatActivity.forward(mContext);
        } else if (i == R.id.btn_add_active) {
            startActivity(new Intent(mContext, ActivePubActivity.class));
        }
    }

    private void showStartDialog() {
        if (CommonAppConfig.getInstance().isTeenagerType()) {
            ToastUtil.show(R.string.a_137);
            return;
        }
        if (!FloatWindowHelper.checkVoice(false)) {
            return;
        }
        MainStartDialogFragment dialogFragment = new MainStartDialogFragment();
        dialogFragment.setMainStartChooseCallback(mMainStartChooseCallback);
        dialogFragment.show(getSupportFragmentManager(), "MainStartDialogFragment");
    }

    private MainStartChooseCallback mMainStartChooseCallback = new MainStartChooseCallback() {
        @Override
        public void onLiveClick() {

            PermissionUtil.request(MainActivity.this,
                    new PermissionCallback() {
                        @Override
                        public void onAllGranted() {
                            forwardLive();
                        }
                    },
                    Permission.READ_MEDIA_IMAGES,
                    Permission.READ_MEDIA_VIDEO,
                    Manifest.permission.CAMERA,
                    Permission.READ_MEDIA_AUDIO
            );


        }

        @Override
        public void onVideoClick() {
            PermissionUtil.request(MainActivity.this,
                    new PermissionCallback() {
                        @Override
                        public void onAllGranted() {
                            startActivity(new Intent(mContext, VideoRecordActivity.class));
                        }
                    },
                    Permission.READ_MEDIA_IMAGES,
                    Permission.READ_MEDIA_VIDEO,
                    Permission.CAMERA,
                    Permission.READ_MEDIA_AUDIO
            );
        }

        @Override
        public void onVoiceClick() {
            PermissionUtil.request(MainActivity.this,
                    new PermissionCallback() {
                        @Override
                        public void onAllGranted() {
                            forwardVoiceLive();
                        }
                    },
                    Permission.READ_MEDIA_IMAGES,
                    Permission.READ_MEDIA_VIDEO,
                    Permission.CAMERA,
                    Permission.READ_MEDIA_AUDIO
            );
        }
    };

    /**
     * 开启直播
     */
    private void forwardLive() {
        LiveHttpUtil.getLiveSdk(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    int haveStore = obj.getIntValue("isshop");
                    String forbidLiveTip = obj.getString("liveban_title");
                    if (CommonAppConfig.LIVE_SDK_CHANGED) {
                        int sdk = obj.getIntValue("live_sdk");
                        LiveConfigBean configBean = null;
                        if (sdk == Constants.LIVE_SDK_KSY) {
                            configBean = JSON.parseObject(obj.getString("android"), LiveConfigBean.class);
                        } else {
                            configBean = JSON.parseObject(obj.getString("android_tx"), LiveConfigBean.class);
                        }
                        LiveAnchorActivity.forward(mContext, sdk, configBean, haveStore, false, forbidLiveTip);
                    } else {
                        LiveConfigBean liveConfigBean = null;
                        if (CommonAppConfig.LIVE_SDK_USED == Constants.LIVE_SDK_KSY) {
                            liveConfigBean = LiveConfig.getDefaultKsyConfig();
                        } else if (CommonAppConfig.LIVE_SDK_USED == Constants.LIVE_SDK_TX) {
                            liveConfigBean = LiveConfig.getDefaultTxConfig();
                        }
                        LiveAnchorActivity.forward(mContext, CommonAppConfig.LIVE_SDK_USED, liveConfigBean, haveStore, false, forbidLiveTip);
                    }
                }
            }
        });
    }

    /**
     * 开启语音直播
     */
    private void forwardVoiceLive() {
        LiveHttpUtil.getLiveSdk(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    int haveStore = obj.getIntValue("isshop");
                    String forbidLiveTip = obj.getString("liveban_title");
                    if (CommonAppConfig.LIVE_SDK_CHANGED) {
                        int sdk = obj.getIntValue("live_sdk");
                        LiveConfigBean configBean = null;
                        if (sdk == Constants.LIVE_SDK_KSY) {
                            configBean = JSON.parseObject(obj.getString("android"), LiveConfigBean.class);
                        } else {
                            configBean = JSON.parseObject(obj.getString("android_tx"), LiveConfigBean.class);
                        }
                        LiveAnchorActivity.forward(mContext, sdk, configBean, haveStore, true, forbidLiveTip);
                    } else {
                        LiveConfigBean liveConfigBean = null;
                        if (CommonAppConfig.LIVE_SDK_USED == Constants.LIVE_SDK_KSY) {
                            liveConfigBean = LiveConfig.getDefaultKsyConfig();
                        } else if (CommonAppConfig.LIVE_SDK_USED == Constants.LIVE_SDK_TX) {
                            liveConfigBean = LiveConfig.getDefaultTxConfig();
                        }
                        LiveAnchorActivity.forward(mContext, CommonAppConfig.LIVE_SDK_USED, liveConfigBean, haveStore, true, forbidLiveTip);
                    }
                }
            }
        });
    }

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (configBean.getMaintainSwitch() == 1) {//开启维护
                        DialogUitl.showSimpleTipDialog(mContext, WordUtil.getString(R.string.main_maintain_notice), configBean.getMaintainTips());
                    }
                    if (!VersionUtil.isLatest(configBean.getVersion())) {
                        VersionUtil.showDialog(mContext, configBean, configBean.getDownloadApkUrl());
                    }
                }
            }
        });
    }

    /**
     * 检查codeInstall安装参数
     */
    private void getAgentCode() {
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null && configBean.getCodeInstallSwitch() == 1) {
                    CodeInstall.getInstall(new ReceiptCallBack() {
                        @Override
                        public void onResponse(Transfer transfer) {
                            String paramsData = transfer.getPbData();
                            L.e("codeInstall---onResponse---> " + paramsData);
                            if (TextUtils.isEmpty(paramsData)) {
                                checkAgent();
                            } else {
                                String agentCode = null;
                                try {
                                    agentCode = JSON.parseObject(paramsData).getString("code");
                                } catch (Exception e) {

                                }
                                if (TextUtils.isEmpty(agentCode)) {
                                    checkAgent();
                                } else {
                                    L.e("codeInstall---获取到邀请码---> " + agentCode);
                                    MainHttpUtil.setDistribut(agentCode, new HttpCallback() {
                                        @Override
                                        public void onSuccess(int code, String msg, String[] info) {
                                            L.e("codeInstall---setDistribut---> " + code + "---msg----> " + msg);
                                        }
                                    });
                                }
                            }
                        }
                    });
                } else {
                    checkAgent();
                }
            }
        });
    }


    /**
     * 检查是否显示填写邀请码的弹窗
     */
    private void checkAgent() {
        MainHttpUtil.checkAgent(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    boolean isHasAgent = obj.getIntValue("has_agent") == 1;//是否有上下级关系
                    boolean agentSwitch = obj.getIntValue("agent_switch") == 1;
                    boolean isAgentMust = obj.getIntValue("agent_must") == 1;//是否必填
                    if (!isHasAgent && agentSwitch) {
                        if (mFirstLogin || isAgentMust) {
                            showInvitationCode(isAgentMust);
                        }
                    }
                }
            }
        });
    }

    /**
     * 填写邀请码的弹窗
     */
    private void showInvitationCode(boolean inviteMust) {
        if (inviteMust) {
            NotCancelableInputDialog dialog = new NotCancelableInputDialog();
            dialog.setTitle(WordUtil.getString(R.string.main_input_invatation_code));
            dialog.setActionListener(new NotCancelableInputDialog.ActionListener() {
                @Override
                public void onConfirmClick(String content, final DialogFragment dialog) {
                    if (TextUtils.isEmpty(content)) {
                        ToastUtil.show(R.string.main_input_invatation_code);
                        return;
                    }
                    MainHttpUtil.setDistribut(content, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                                dialog.dismissAllowingStateLoss();
                            } else {
                                ToastUtil.show(msg);
                            }
                        }
                    });
                }

            });
            dialog.show(getSupportFragmentManager(), "NotCancelableInputDialog");
        } else {
            new DialogUitl.Builder(mContext)
                    .setTitle(WordUtil.getString(R.string.main_input_invatation_code))
                    .setCancelable(true)
                    .setInput(true)
                    .setBackgroundDimEnabled(true)
                    .setClickCallback(new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(final Dialog dialog, final String content) {
                            if (TextUtils.isEmpty(content)) {
                                ToastUtil.show(R.string.main_input_invatation_code);
                                return;
                            }
                            MainHttpUtil.setDistribut(content, new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    if (code == 0 && info.length > 0) {
                                        ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                                        dialog.dismiss();
                                    } else {
                                        ToastUtil.show(msg);
                                    }
                                }
                            });
                        }
                    })
                    .build()
                    .show();
        }
    }

    /**
     * 签到奖励
     */
    private void requestBonus() {
        MainHttpUtil.requestBonus(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (obj.getIntValue("bonus_switch") == 0) {
                        return;
                    }
                    int day = obj.getIntValue("bonus_day");
                    if (day <= 0) {
                        return;
                    }
                    List<BonusBean> list = JSON.parseArray(obj.getString("bonus_list"), BonusBean.class);
                    BonusViewHolder bonusViewHolder = new BonusViewHolder(mContext, mRootView);
                    bonusViewHolder.setData(list, day, obj.getString("count_day"));
                    bonusViewHolder.show();
                    mBonusViewHolder = bonusViewHolder;
                }
            }
        });
    }

    /**
     * 登录IM
     */
    private void loginIM() {
        String uid = CommonAppConfig.getInstance().getUid();
        CommonAppConfig.loginIm(this, uid);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mFristLoad) {
            mFristLoad = false;
            loadPageData(0, true);
            if (mHomeViewHolder != null) {
                mHomeViewHolder.setShowed(true);
            }
            getLocation();

        }

        if (mLoginChanged) {
            mLoginChanged = false;
            requestAfterLogin();
            if (mMallViewHolder != null) {
                mMallViewHolder.showMyShopAvatar();
            }
        }

        if (CommonAppConfig.getInstance().isTeenagerType()) {
            if (mBonusViewHolder != null) {
                mBonusViewHolder.dismiss();
                mBonusViewHolder = null;
            }
        }
    }

    /**
     * 获取所在位置
     */
    private void getLocation() {
        PermissionUtil.request(MainActivity.this,
                new PermissionCallback() {
                    @Override
                    public void onAllGranted() {
                        LocationUtil.getInstance().startLocation();
                    }
                },
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        );
    }


    @Override
    protected void onDestroy() {
        if (mTabButtonGroup != null) {
            mTabButtonGroup.cancelAnim();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        EventBus.getDefault().unregister(this);
        LiveHttpUtil.cancel(LiveHttpConsts.GET_LIVE_SDK);
        MainHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        MainHttpUtil.cancel(MainHttpConsts.REQUEST_BONUS);
        MainHttpUtil.cancel(MainHttpConsts.GET_BONUS);
        MainHttpUtil.cancel(MainHttpConsts.CHECK_AGENT);
        MainHttpUtil.cancel(MainHttpConsts.SET_DISTRIBUT);
        MainHttpUtil.cancel(MainHttpConsts.CHANGE_TEENAGER_TIME);
        if (mCheckLivePresenter != null) {
            mCheckLivePresenter.cancel();
        }
        LocationUtil.getInstance().stopLocation();
        CommonAppConfig.getInstance().setGiftListJson(null);
        CommonAppConfig.getInstance().setLaunched(false);
        LiveStorge.getInstance().clear();
        VideoStorge.getInstance().clear();
        FloatWindowUtil.getInstance().dismiss();
        LiveVoicePlayUtil.getInstance().setKeepAlive(false);
        LiveVoicePlayUtil.getInstance().release();
        super.onDestroy();
    }

    public static void forward(Context context) {
        forward(context, false);
    }

    public static void forward(Context context, boolean firstLogin) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.FIRST_LOGIN, firstLogin);
        context.startActivity(intent);
    }

    /**
     * 观看直播
     */
    public void watchLive(LiveBean liveBean, String key, int position) {
        if (!FloatWindowHelper.checkVoice(true)) {
            return;
        }
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            mCheckLivePresenter.watchLive(liveBean, key, position);
        } else {
            mCheckLivePresenter.watchLive(liveBean);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
        String unReadCount = e.getUnReadCount();
        if (!TextUtils.isEmpty(unReadCount)) {
            if (mHomeViewHolder != null) {
                mHomeViewHolder.setUnReadCount(unReadCount);
            }
            if (mActiveViewHolder != null) {
                mActiveViewHolder.setUnReadCount(unReadCount);
            }
//            if (mMeViewHolder != null) {
//                mMeViewHolder.setUnReadCount(unReadCount);
//            }
        }
    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        if (curTime - mLastClickBackTime > 2000) {
            mLastClickBackTime = curTime;
            ToastUtil.show(R.string.main_click_next_exit);
            return;
        }
        super.onBackPressed();
    }


    private void loadPageData(int position, boolean needlLoadData) {
        if (mViewHolders == null) {
            return;
        }
        AbsMainViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mHomeViewHolder = new MainHomeViewHolder(mContext, parent);
                    mHomeViewHolder.setAppBarLayoutListener(this);
                    vh = mHomeViewHolder;
                } else if (position == 1) {
                    mActiveViewHolder = new MainActiveViewHolder(mContext, parent);
                    vh = mActiveViewHolder;
                } else if (position == 2) {
                    mMallViewHolder = new MainMallViewHolder(mContext, parent);
                    vh = mMallViewHolder;
                } else if (position == 3) {
                    mMeViewHolder = new MainMeViewNewHolder(mContext, parent);
                    vh = mMeViewHolder;
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
            }
        }
        if (needlLoadData && vh != null) {
            vh.loadData();
        }
    }

    @Override
    public void onOffsetChangedDirection(boolean up) {
        if (!mAnimating) {
            if (up) {
                if (mShowed && mDownAnimator != null) {
                    mDownAnimator.start();
                }
            } else {
                if (mHided && mUpAnimator != null) {
                    mUpAnimator.start();
                }
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (mAudioManager != null) {
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE,
                            AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (mAudioManager != null) {
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_LOWER,
                            AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 观众退出语音直播间，显示悬浮窗
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveAudienceExitEvent(LiveAudienceVoiceExitEvent e) {
        final LiveBean liveBean = e.getLiveBean();
        if (liveBean == null) {
            return;
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FloatWindowUtil.getInstance()
                        .setLiveBean(liveBean)
                        .requestPermission();
            }
        }, 500);

    }


    /**
     * 点击悬浮窗，进入直播间
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveAudienceVoiceOpenEvent(LiveAudienceVoiceOpenEvent e) {
        watchLive(e.getLiveBean(), "", 0);
    }


    /**
     * 登录状态改变
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginChangeEvent(LoginChangeEvent e) {
        mLoginChanged = true;
        mFirstLogin = e.isFirstLogin();
    }


    public void setCurrentPage(int position) {
        if (mTabButtonGroup != null) {
            mTabButtonGroup.setCurPosition(position);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mViewPager != null) {
            outState.putInt("CurrentItem", mViewPager.getCurrentItem());
        }
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int savedCurrentItem = savedInstanceState.getInt("CurrentItem", 0);
        if (mViewPager != null) {
            if (savedCurrentItem != mViewPager.getCurrentItem()) {
                mViewPager.setCurrentItem(savedCurrentItem, false);
            }
        }
        if (mTabButtonGroup != null) {
            mTabButtonGroup.setCurPosition(savedCurrentItem);
        }
    }

    /**
     * 是否开启青少年模式
     */
    private void checkTeenager() {
        MainHttpUtil.checkTeenager(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        boolean isOpen = obj.getIntValue("status") == 1;
                        CommonAppConfig.getInstance().setTeenagerType(isOpen);
                        if (!isOpen) {
                            boolean isTeenagerShow = SpUtil.getInstance().getBooleanValue(SpUtil.TEENAGER_SHOW);
                            if (!isTeenagerShow) {
                                SpUtil.getInstance().setBooleanValue(SpUtil.TEENAGER_SHOW, true);
                                TeenagerDialogFragment fragment = new TeenagerDialogFragment();
                                fragment.show(getSupportFragmentManager(), "TeenagerDialogFragment");
                            }
                            requestBonus();//每日签到
                        } else {
                            int is_tip = obj.getIntValue("is_tip");
                            if (is_tip == 1) {
                                mIsTeengerTick = false;
                                RouteUtil.teenagerTip(obj.getString("tips"));
                            } else {
                                startTeenagerCountdown();
                            }
                        }
                    }

                }
            }
        });
    }

    /**
     * 启动青少年模式定时器
     */
    private void startTeenagerCountdown() {
        if (mIsTeengerTick) {
            return;
        }
        mIsTeengerTick = true;
        if (mTeengerTicker == null) {
            mTeengerTicker = new Runnable() {
                @Override
                public void run() {
                    if (mHandler == null) {
                        mHandler = new Handler();
                    }
                    long now = SystemClock.uptimeMillis();
                    if (mTeenagerTimeCallback == null) {
                        mTeenagerTimeCallback = new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code != 0) {
                                    mIsTeengerTick = false;
                                    if (mHandler != null) {
                                        mHandler.removeCallbacks(mTeengerTicker);
                                    }
                                    if (code == 10010 || code == 10011) {
                                        RouteUtil.teenagerTip(msg);
                                    }
                                }
                            }
                        };
                    }
                    MainHttpUtil.changeTeenagerTime(mTeenagerTimeCallback);
                    if (mIsTeengerTick) {
                        long next = now + (1000 - now % 1000) + 9000;
                        mHandler.postAtTime(mTeengerTicker, next);
                    }
                }
            };
        }
        mTeengerTicker.run();
    }


    public void setLanguage(String languageCode) {
        HttpClient.getInstance().setLanguage(languageCode);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
