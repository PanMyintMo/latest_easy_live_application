package com.gawilive.main.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gawilive.common.adapter.AppAdapter;
import com.gawilive.main.R;
import com.gawilive.main.bean.RedSourceModel;

/**
 * 红积分适配器
 */
public class RedSourceAdapter extends AppAdapter<RedSourceModel> {


    public RedSourceAdapter(Context context) {
        super(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }


    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private TextView mTvRedScore;
        private TextView mTvDesc;
        private TextView mTvTime;

        private ViewHolder() {
            super(R.layout.item_red_source);
            mTvDesc = findViewById(R.id.tvDesc);
            mTvTime = findViewById(R.id.tvTime);
            mTvRedScore = findViewById(R.id.tvRedScore);
        }

        @Override
        public void onBindView(int position) {
            RedSourceModel model = getItem(position);
            mTvDesc.setText(model.getDesc());
            mTvTime.setText(model.getAddtime());
            mTvRedScore.setText(model.getStr() + model.getRedScore());
        }
    }
}