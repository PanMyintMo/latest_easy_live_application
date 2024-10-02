package com.gawilive.phonelive;

import android.text.TextUtils;

import com.hjq.toast.ToastInterceptor;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastBlackStyle;
import com.meihu.beautylibrary.MHSDK;
import com.mob.MobSDK;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.imsdk.v2.V2TIMLogListener;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.tencent.rtmp.TXLiveBase;
//import com.umeng.commonsdk.UMConfigure;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.CommonAppContext;
import com.gawilive.common.Constants;
import com.gawilive.common.http.HttpClient;
import com.gawilive.common.utils.DecryptUtil;
import com.gawilive.common.utils.L;
import com.gawilive.common.utils.SpUtil;
import com.gawilive.im.utils.ImMessageUtil;
import com.gawilive.im.utils.ImPushUtil;
import com.tencent.ugc.TXUGCBase;


/**
 * Created by cxf on 2017/8/3.
 */

public class AppContext extends CommonAppContext {

    private boolean mBeautyInited;

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(android.R.color.white);//全局设置主题颜色
            return new ClassicsHeader(context);
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        UMConfigure.preInit(this, CommonAppConfig.getMetaDataString("UMENG_APPKEY"),
//                CommonAppConfig.PACKAGE_NAME);
        String language = SpUtil.getInstance().getStringValue(SpUtil.LANGUAGE);
        if (language.contains(Constants.CHINESE_CODE)) {
            HttpClient.getInstance().setLanguage(Constants.CHINESE_CODE);
        } else if (language.contains(Constants.AR_CODE)) {
            HttpClient.getInstance().setLanguage(Constants.AR_CODE);
        } else if (language.contains(Constants.TH_CODE)) {
            HttpClient.getInstance().setLanguage(Constants.TH_CODE);
        } else if (language.contains(Constants.MY_CODE)) {
            HttpClient.getInstance().setLanguage(Constants.MY_CODE);
        } else {
            HttpClient.getInstance().setLanguage(Constants.ENGLISH_CODE);
        }

        ToastUtils.init(this, new ToastBlackStyle(this) {

            @Override
            public int getCornerRadius() {
                return 999;
            }
        });

        // 设置 Toast 拦截器
        ToastUtils.setToastInterceptor(new ToastInterceptor());

    }

    public static void initSdk() {
        boolean isDebug = BuildConfig.DEBUG;
        L.setDeBug(isDebug);
        CommonAppContext context = CommonAppContext.getInstance();
        if (isDebug) {
            L.e("应用签名------>" + context.getAppSignature());
        }
        //腾讯云直播鉴权url
        String liveLicenceUrl = "https://license.vod2.myqcloud.com/license/v2/1325982282_1/v_cube.license";
        //腾讯云直播鉴权key
        String liveKey = "499ee482c5cffc1ae8685877c469a0a4";
        //腾讯云视频鉴权url
        String ugcLicenceUrl = "https://license.vod2.myqcloud.com/license/v2/1325982282_1/v_cube.license";
        //腾讯云视频鉴权key
        String ugcKey = "499ee482c5cffc1ae8685877c469a0a4";
        TXLiveBase.getInstance().setLicence(context, liveLicenceUrl, liveKey);

        TXUGCBase.getInstance().setLicence(context, ugcLicenceUrl, ugcKey);
        //初始化腾讯bugly
        CrashReport.initCrashReport(context);
        CrashReport.setAppVersion(context, CommonAppConfig.getInstance().getVersion());
        //初始化ShareSdk
        MobSDK.init(context, "38919dbcc620c", "10d04607915c029b977f4c50e40a085f");
        MobSDK.submitPolicyGrantResult(true);
//        MobSDK.init(context);
        //初始化极光推送
        //   ImPushUtil.getInstance().init(context);
        //初始化极光IM
        // ImMessageUtil.getInstance().init();
        //初始化友盟统计
//        UMConfigure.init(context, UMConfigure.DEVICE_TYPE_PHONE, null);


        V2TIMSDKConfig config = new V2TIMSDKConfig();
        // 指定 log 输出级别
        config.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_INFO);
        // 指定 log 监听器
        config.setLogListener(new V2TIMLogListener() {
            @Override
            public void onLog(int logLevel, String logContent) {
                // logContent 为 SDK 日志内容
            }
        });


    }

    /**
     * 初始化美狐
     */
    public void initBeautySdk(String beautyAppId, String beautyKey) {
        if (!TextUtils.isEmpty(beautyAppId) && !TextUtils.isEmpty(beautyKey)) {
            if (!mBeautyInited) {
                mBeautyInited = true;
                if (CommonAppConfig.isYunBaoApp()) {
                    beautyAppId = DecryptUtil.decrypt(beautyAppId);
                    beautyKey = DecryptUtil.decrypt(beautyKey);
                }
                MHSDK.init(this, beautyAppId, beautyKey);
                CommonAppConfig.getInstance().setMhBeautyEnable(true);
                L.e("美狐初始化----AppId--->" + beautyAppId + "---AppKey--->" + beautyKey);
            }
        } else {
            CommonAppConfig.getInstance().setMhBeautyEnable(false);
        }
    }

}
