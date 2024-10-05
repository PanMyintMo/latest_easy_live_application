package com.gawilive.main.views;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.main.bean.PayUserBean;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hjq.shape.view.ShapeTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.HtmlConfig;
import com.gawilive.common.activity.WebViewActivity;
import com.gawilive.common.bean.*;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.CommonHttpUtil;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.utils.*;
import com.gawilive.im.activity.ChatActivity;
import com.gawilive.live.activity.LiveRecordActivity;
import com.gawilive.live.activity.LuckPanRecordActivity;
import com.gawilive.live.activity.RoomManageActivity;
import com.gawilive.main.R;
import com.gawilive.main.activity.*;
import com.gawilive.main.bean.BannerBean;
import com.gawilive.main.http.MainHttpUtil;
import com.gawilive.mall.activity.*;

import okhttp3.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 新版个人中心
public class MainMeViewNewHolder extends AbsMainViewHolder {

    private FrameLayout mFlTop;
    private ImageView mBtnSetting;
    private ImageView mBtnMsg;
    private TextView mRedPoint;
    private RoundedImageView mAvatar;
    private TextView mName;
    private ShapeTextView mPersonData;
    private ImageView mImgLogo;
    private TextView mTvInputQb;
    private Banner mBanner;

    private TextView mFollowNum;

    private TextView mFansNum;

    private TextView mCollectNum;


    private boolean mPaused;

    private RelativeLayout mGroupVip;

    private List<BannerBean> mBannerList;

    private boolean mBannerNeedUpdate;

    private ImageView btn_wallet;

    private TextView mCollectionNum;
    private RelativeLayout mMGroupVip;

    private TextView mTvFjGl;
    private TextView mTvZbZx;
    private TextView mTvDj;
    private TextView mTvMrrw;
    private TextView mTvZbmx;
    private TextView mTvFxnr;
    private TextView mTvZJJL;
    private TextView mTvGxsz;
    private ImageView mBtnWallet;

    private TextView forwardProfit;

    private TextView tvWdRz;

    private TextView tvWdDj;

    private TextView tvWdSp;

    private TextView btn_vip;

    private TextView tvCoin, tvIntegral, tvBalance, tvJfCj, tvDp, tvJPXq;

    private LinearLayout llRedScore, llGreenScore;


    public MainMeViewNewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_me_new;
    }

    @Override
    public void init() {
        setStatusHeight();
        initView();
        initData();
    }

    private void initView() {
        mFlTop = findViewById(R.id.fl_top);
        mBtnSetting = findViewById(R.id.btn_setting);
        mBtnMsg = findViewById(R.id.btn_msg);
        mRedPoint = findViewById(R.id.red_point);
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        mPersonData = findViewById(R.id.personData);
        mImgLogo = findViewById(R.id.imgLogo);
        mTvInputQb = findViewById(R.id.tvInputQb);
        mBanner = findViewById(R.id.banner);
        mFansNum = findViewById(R.id.fans_num);
        mFollowNum = findViewById(R.id.follow_num);
        mCollectNum = findViewById(R.id.collection_num);
        mGroupVip = findViewById(R.id.mGroupVip);
        btn_wallet = findViewById(R.id.btn_wallet);
        mTvInputQb = findViewById(R.id.tvInputQb);
        mTvFjGl = findViewById(R.id.tvFjGl);
        mTvZbZx = findViewById(R.id.tvZbZx);
        mTvDj = findViewById(R.id.tvDj);
        mTvMrrw = findViewById(R.id.tvMrrw);
        mTvZbmx = findViewById(R.id.tvZbmx);
        mTvFxnr = findViewById(R.id.tvFxnr);
        mTvZJJL = findViewById(R.id.tvZJJL);
        mTvGxsz = findViewById(R.id.tvGxsz);
        forwardProfit = findViewById(R.id.forwardProfit);
        tvWdRz = findViewById(R.id.tvWdRz);
        tvWdDj = findViewById(R.id.tvWdDj);
        tvWdSp = findViewById(R.id.tvWdSp);
        btn_vip = findViewById(R.id.btn_vip);
        tvCoin = findViewById(R.id.tvCoin);
        tvBalance = findViewById(R.id.tvBalance);
        tvIntegral = findViewById(R.id.tvIntegral);
        tvJfCj = findViewById(R.id.tvJfCj);
        tvDp = findViewById(R.id.tvDp);
        llRedScore = findViewById(R.id.llRedScore);
        llGreenScore = findViewById(R.id.llGreenScore);


        mBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                ImgLoader.display(mContext, ((BannerBean) path).getImageUrl(), imageView);
            }
        });
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int p) {
                if (mBannerList != null) {
                    if (p >= 0 && p < mBannerList.size()) {
                        BannerBean bean = mBannerList.get(p);
                        if (bean != null) {
                            String link = bean.getLink();
                            if (!TextUtils.isEmpty(link)) {
                                WebViewActivity.forward(mContext, link, false);
                            }
                        }
                    }
                }
            }
        });

        // 进入钱包
        mTvInputQb.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, MyCoinActivity.class));
        });

        // 设置
        mBtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardSetting();
            }
        });

        // 直播明细
        mTvZbmx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.forward(mContext, CommonAppConfig.HOST + "/appapi/Detail/index");
            }
        });

        // 我的认证
        tvWdRz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.forward(mContext, HtmlConfig.AUTH);
            }
        });

        // 开通vip
        btn_vip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.forward(mContext, HtmlConfig.SHOP);
//                //  mContext.startActivity(new Intent(mContext,DrawALotteryOrRaffleActivity));
//                RouteUtil.forwardPrize(mContext);
            }
        });

        // 我的收益
        forwardProfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardProfit();
            }
        });
        // 我的等级
        tvWdDj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.forward(mContext, CommonAppConfig.HOST + "/appapi/Level/index");
            }
        });
        // 我的视频
        tvWdSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardMyVideo();
            }
        });


        // 我的粉丝
        mFansNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardFans();
            }
        });

        // 我的关注
        mFollowNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardFollow();
            }
        });

        // 我的评论
        mCollectNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonAppConfig.getInstance().isTeenagerType()) {
                    ToastUtil.show(com.gawilive.common.R.string.a_137);
                } else {
                    mContext.startActivity(new Intent(mContext, GoodsCollectActivity.class));
                }

            }
        });

        mBtnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.forward(mContext);
            }
        });

        btn_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteUtil.forwardMyCoin(mContext);
            }
        });

        // 房间管理
        mTvFjGl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardRoomManage();
            }
        });
        // 装备中心
        mTvZbZx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.forward(mContext, CommonAppConfig.HOST + "/appapi/Equipment/index");
            }
        });
        // 道具
        mTvDj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.forward(mContext, CommonAppConfig.HOST + "/appapi/Mall/index");
            }
        });
        // 每日任务
        mTvMrrw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, DailyTaskActivity.class));
            }
        });

        // 付费内容
        mTvFxnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardPayContent();
            }
        });
        // 中奖激励
        mTvZJJL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                luckPanRecord();
            }
        });
        // 个性设置
        mTvGxsz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardSetting();
            }
        });

        // 积分抽奖
        tvJfCj.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, DrawALotteryOrRaffleActivity.class));
        });

        // 个人资料
        mPersonData.setOnClickListener(v -> RouteUtil.forwardUserHome(mContext, CommonAppConfig.getInstance().getUid()));

        // 红积分来源
        llRedScore.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, RedSourceActivity.class));
        });

        // 抽奖钻石来源
        llGreenScore.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, GreenSourceActivity.class));
        });

    }

    private void initData() {
        getBanner();
    }


    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    // 获取认证信息
    private void getAuthData() {
        MainHttpUtil.getAuth(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    AuthModel model = JSONObject.parseObject(info[0], AuthModel.class);
                    if ("1".equals(model.getStatus())) {
                        mContext.startActivity(new Intent(mContext, MyProfitActivity.class));
                    } else if ("0".equals(model.getStatus())) {
                        ToastUtil.show(WordUtil.getString(R.string.mall_063));
                    } else if ("2".equals(model.getStatus())) {
                        ToastUtil.show(WordUtil.getString(R.string.mall_064));
                        WebViewActivity.forward(mContext, HtmlConfig.AUTH);
                    } else {
                        ToastUtil.show(WordUtil.getString(R.string.string_smrz_tips));
                        WebViewActivity.forward(mContext, HtmlConfig.AUTH);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowed() && mPaused) {
            loadData();

        }
        mPaused = false;


    }

    private void getBanner() {
        MainHttpUtil.getHot(1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                List<BannerBean> bannerList = JSON.parseArray(obj.getString("slide"), BannerBean.class);
                if (bannerList != null && bannerList.size() > 0) {
                    if (mBannerList == null || mBannerList.size() != bannerList.size()) {
                        mBannerNeedUpdate = true;
                    } else {
                        for (int i = 0; i < mBannerList.size(); i++) {
                            BannerBean bean = mBannerList.get(i);
                            if (bean == null || !bean.isEqual(bannerList.get(i))) {
                                mBannerNeedUpdate = true;
                                break;
                            }
                        }
                    }
                }
                mBannerList = bannerList;
                showBanner();
            }
        });


    }


    private void showBanner() {
        if (mBanner == null) {
            return;
        }
        if (mBannerList != null && mBannerList.size() > 0) {
            if (mBanner.getVisibility() != View.VISIBLE) {
                mBanner.setVisibility(View.VISIBLE);
            }
            if (mBannerNeedUpdate) {
                mBanner.update(mBannerList);
            }
        } else {
            if (mBanner.getVisibility() != View.GONE) {
                mBanner.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void loadData() {
        if (CommonAppConfig.getInstance().isLogin()) {
            if (isFirstLoadData()) {
                showData1();
                getBalance();
            }
            MainHttpUtil.getBaseInfo(mCallback);
        } else {
            if (mContext != null) {
                ((MainActivity) mContext).setCurrentPage(0);
            }
        }
    }


    private final CommonCallback<UserBean> mCallback = new CommonCallback<UserBean>() {
        @Override
        public void callback(UserBean bean) {
            queryWallet(bean);
            showData();
        }
    };

    private void showData1() {
        String userBeanJson = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
        if (TextUtils.isEmpty(userBeanJson)) {
            return;
        }
        JSONObject obj = JSON.parseObject(userBeanJson);
        UserBean u = JSON.toJavaObject(obj, UserBean.class);
        ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
        mName.setText(u.getUserNiceName());
        CommonAppConfig appConfig = CommonAppConfig.getInstance();

        mFollowNum.setText(WordUtil.getString(R.string.follow) + "\t" + StringUtil.toWan(u.getFollows()));
        mFansNum.setText(WordUtil.getString(R.string.fans) + "\t" + StringUtil.toWan(u.getFans()));
        mCollectNum.setText(WordUtil.getString(R.string.mall_393) + "\t" + StringUtil.toWan(obj.getIntValue("goods_collect_nums")));
    }

    private void showData() {
        String userBeanJson = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
        if (TextUtils.isEmpty(userBeanJson)) {
            return;
        }
        JSONObject obj = JSON.parseObject(userBeanJson);
        UserBean u = JSON.toJavaObject(obj, UserBean.class);
        ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
        mName.setText(u.getUserNiceName());
        CommonAppConfig appConfig = CommonAppConfig.getInstance();

        mFollowNum.setText(WordUtil.getString(R.string.follow) + "\t" + StringUtil.toWan(u.getFollows()));
        mFansNum.setText(WordUtil.getString(R.string.fans) + "\t" + StringUtil.toWan(u.getFans()));
        mCollectNum.setText(WordUtil.getString(R.string.mall_393) + "\t" + StringUtil.toWan(obj.getIntValue("goods_collect_nums")));

        tvIntegral.setText(BigDecimalUtils.add(TextUtils.isEmpty(u.getGreen_score()) ? "0" : u.getGreen_score(), "0", 0));
        tvBalance.setText(BigDecimalUtils.add(TextUtils.isEmpty(u.getRed_score()) ? "0" : u.getRed_score(), "0", 0));


        if (CommonAppConfig.getInstance().isTeenagerType()) {
            if (mGroupVip.getVisibility() != View.GONE) {
                mGroupVip.setVisibility(View.GONE);
            }

        } else {
            if (mGroupVip.getVisibility() != View.VISIBLE) {
                mGroupVip.setVisibility(View.VISIBLE);
            }
        }
        //  tvDp.setText("直播小店");

        // 店铺
        tvDp.setOnClickListener(v -> {
            if (u.getIsOpenShop() == 0) {

                BuyerActivity.forward(mContext);
            } else {
                SellerActivity.forward(mContext);
            }
        });

    }


    // 同步钱包信息
    private void queryWallet(UserBean bean) {
        OkHttp.getAsync(CommonAppConfig.HOST2 + "?act=findUser&phone=" + bean.getMobile(), new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == -3) { // 进行信息同步
                    Map<String, String> map = new HashMap<>();
                    map.put("phone", bean.getMobile());
                    map.put("email", TextUtils.isEmpty(bean.getUser_email()) ? bean.getMobile() : bean.getUser_email());
                    map.put("pwd", bean.getUser_pass());
                    map.put("account", bean.getMobile());
                    map.put("username", bean.getUserNiceName());
                    map.put("avatar", bean.getAvatarThumb());


                    map.put("live_user_id", bean.getId());
                    syncData(map);
                } else {
                    JsonObject dataObject = object.getAsJsonObject("data");
                    PayUserBean bean = new Gson().fromJson(dataObject.toString(), PayUserBean.class);
                    MMKVUtils.setPayUid(bean.getUid());
                    MMKVUtils.setKey(bean.getKey());
                    MMKVUtils.setPayPwd(bean.getPaypwd());
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                Log.d("查询用户信息返回数据", "出错了");
            }
        });
    }

    // 同步信息
    private void syncData(Map<String, String> map) {
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=addUser", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                syncData(map);
            }

            @Override
            public void requestFailure(Request request, IOException e) {
            }
        });
    }


    /**
     * 我的关注
     */
    private void forwardFollow() {
        FollowActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }

    /**
     * 我的粉丝
     */
    private void forwardFans() {
        FansActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }

    /**
     * 直播记录
     */
    private void forwardLiveRecord() {
        LiveRecordActivity.forward(mContext, CommonAppConfig.getInstance().getUserBean());
    }

    /**
     * 我的收益
     */
    private void forwardProfit() {
        getAuthData();
    }

    /**
     * 我的钻石
     */
    private void forwardCoin() {
        RouteUtil.forwardMyCoin(mContext);
    }

    /**
     * 设置
     */
    private void forwardSetting() {
        mContext.startActivity(new Intent(mContext, SettingActivity.class));
    }

    /**
     * 我的视频
     */
    private void forwardMyVideo() {
        mContext.startActivity(new Intent(mContext, MyVideoActivity.class));
    }

    /**
     * 房间管理
     */
    private void forwardRoomManage() {
        mContext.startActivity(new Intent(mContext, RoomManageActivity.class));
    }

    /**
     * 转盘中奖记录
     */
    private void luckPanRecord() {
        mContext.startActivity(new Intent(mContext, LuckPanRecordActivity.class));
    }

    /**
     * 付费内容
     */
    private void forwardPayContent() {
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        if (u != null) {
            if (u.getIsOpenPayContent() == 0) {
                PayContentActivity1.forward(mContext);
            } else {
                PayContentActivity2.forward(mContext);
            }
        }
    }

    // 查询余额
    private void getBalance() {
        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    tvCoin.setText(coin);


                }
            }


        });
    }
}
