package com.gawilive.live.dialog;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.custom.CommonRefreshView;
import com.gawilive.common.dialog.AbsDialogFragment;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.common.utils.JsonUtil;
import com.gawilive.live.R;
import com.gawilive.live.activity.LiveActivity;
import com.gawilive.live.adapter.LuckPanRecordAdapter;
import com.gawilive.live.bean.LuckPanBean;
import com.gawilive.live.http.LiveHttpConsts;
import com.gawilive.live.http.LiveHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/8/27.
 * 中奖记录
 */

public class LuckPanRecordDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private CommonRefreshView mRefreshView;
    private LuckPanRecordAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_luck_pan_record;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(300);
        params.height = DpUtil.dp2px(320);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LuckPanBean>() {
            @Override
            public RefreshAdapter<LuckPanBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new LuckPanRecordAdapter(mContext,true);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                LiveHttpUtil.getTurnRecord(((LiveActivity) mContext).isVoiceChatRoom() ? 1 : 0, p, callback);
            }

            @Override
            public List<LuckPanBean> processData(String[] info) {
                return JsonUtil.getJsonToList(Arrays.toString(info), LuckPanBean.class);
            }

            @Override
            public void onRefreshSuccess(List<LuckPanBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<LuckPanBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LiveHttpUtil.cancel(LiveHttpConsts.GET_WIN);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_confirm) {
            dismiss();
        }
    }
}
