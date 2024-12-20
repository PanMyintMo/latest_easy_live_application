package com.gawilive.common.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

//import com.umeng.analytics.MobclickAgent;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.R;
import com.gawilive.common.dialog.NotLoginDialogFragment;
import com.gawilive.common.interfaces.LifeCycleListener;
import com.gawilive.common.utils.ClickUtil;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.common.utils.MultiLanguageUtil;
import com.gawilive.common.utils.ScreenDimenUtil;
import com.gawilive.common.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2017/8/3.
 */

public abstract class AbsActivity extends AppCompatActivity {

    protected String mTag;
    protected Context mContext;
    protected List<LifeCycleListener> mLifeCycleListeners;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag = this.getClass().getSimpleName();
        getIntentParams();
        setStatusBar();
        int layoutId = getLayoutId();
        if (layoutId > 0) {
            setContentView(layoutId);
        }
        setStatusHeight();
        mContext = this;
        mLifeCycleListeners = new ArrayList<>();
        main(savedInstanceState);
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onCreate();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Log.e("multilanguage", "attachBaseContext");
        super.attachBaseContext(MultiLanguageUtil.attachBaseContext(newBase));
        //app杀进程启动后会调用Activity attachBaseContext
        MultiLanguageUtil.getInstance().setConfiguration(newBase);

    }

    protected void getIntentParams() {

    }

    protected abstract int getLayoutId();

    protected void main(Bundle savedInstanceState) {
        main();
    }

    protected void main() {

    }

    protected boolean isStatusBarWhite() {
        return false;
    }


    protected void setTitle(String title) {
        TextView titleView = findViewById(R.id.titleView);
        if (titleView != null) {
            titleView.setText(title);
        }
    }


    public void backClick(View v) {
        if (v.getId() == R.id.btn_back) {
            onBackPressed();
        }
    }

    protected boolean canClick() {
        return ClickUtil.canClick();
    }


    /**
     * 设置透明状态栏
     */
    private void setStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (isStatusBarWhite()) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(0);
    }


    @Override
    protected void onDestroy() {
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onDestroy();
            }
            mLifeCycleListeners.clear();
            mLifeCycleListeners = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onStart();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onReStart();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onResume();
            }
        }
        //友盟统计
//        MobclickAgent.onResume(this);
        updateBrightness();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onPause();
            }
        }
        //友盟统计
//        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onStop();
            }
        }
    }

    public void addLifeCycleListener(LifeCycleListener listener) {
        if (mLifeCycleListeners != null && listener != null) {
            mLifeCycleListeners.add(listener);
        }
    }

    public void addAllLifeCycleListener(List<LifeCycleListener> listeners) {
        if (mLifeCycleListeners != null && listeners != null) {
            mLifeCycleListeners.addAll(listeners);
        }
    }

    public void removeLifeCycleListener(LifeCycleListener listener) {
        if (mLifeCycleListeners != null) {
            mLifeCycleListeners.remove(listener);
        }
    }


    public void startActivity(Class<? extends Activity> cs) {
        Intent intent = new Intent(this, cs);
        startActivity(intent);
    }

    /**
     * 设置当前应用亮度
     */
    public void updateBrightness() {
        float brightness = SpUtil.getInstance().getBrightness();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness != lp.screenBrightness) {
            lp.screenBrightness = brightness;
            window.setAttributes(lp);
        }
    }

    /**
     * 根据不同手机的状态栏设置高度
     */
    private void setStatusHeight() {
        View flTop = findViewById(R.id.fl_top);
        if (flTop == null) {
            return;
        }
        int statusBarHeight = ScreenDimenUtil.getInstance().getStatusBarHeight();
        if (statusBarHeight > DpUtil.dp2px(19)) {
            flTop.setPadding(0, statusBarHeight, 0, 0);
        }
    }

    /**
     * 未登录的弹窗
     */
    public void showNotLoginDialog() {
        NotLoginDialogFragment fragment = new NotLoginDialogFragment();
        fragment.show(getSupportFragmentManager(), "NotLoginDialogFragment");
    }


    public boolean checkLogin() {
        if (!CommonAppConfig.getInstance().isLogin()) {
            showNotLoginDialog();
            return false;
        }
        return true;
    }

}
