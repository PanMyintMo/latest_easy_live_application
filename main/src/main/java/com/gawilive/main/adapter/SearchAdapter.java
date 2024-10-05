package com.gawilive.main.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.adapter.RefreshAdapter;
import com.gawilive.common.bean.LevelBean;
import com.gawilive.common.custom.MyRadioButton;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.CommonHttpUtil;
import com.gawilive.common.utils.CommonIconUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.main.R;
import com.gawilive.live.bean.SearchUserBean;

import java.util.List;

/**
 * Created by cxf on 2018/9/29.
 */

public class SearchAdapter extends RefreshAdapter<SearchUserBean> {

    private final View.OnClickListener mFollowClickListener;
    private final View.OnClickListener mClickListener;
    private final String mFollow;
    private final String mFollowing;
    private final int mFrom;
    private final String mUid;

    public SearchAdapter(Context context, int from) {
        super(context);
        mFrom = from;
        mFollow = WordUtil.getString(R.string.follow);
        mFollowing = WordUtil.getString(R.string.following);
        mFollowClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                SearchUserBean bean = (SearchUserBean) tag;
                CommonHttpUtil.setAttention(bean.getId(), null);
            }
        };
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                SearchUserBean bean = (SearchUserBean) tag;
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean, 0);
                }
            }
        };
        mUid = CommonAppConfig.getInstance().getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    public void updateItem(String id, int attention) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        for (int i = 0, size = mList.size(); i < size; i++) {
            SearchUserBean bean = mList.get(i);
            if (bean != null && id.equals(bean.getId())) {
                bean.setAttention(attention);
                notifyItemChanged(i, Constants.PAYLOAD);
                break;
            }
        }
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mSign;
        ImageView mSex;
        ImageView mLevelAnchor;
        ImageView mLevel;
        MyRadioButton mBtnFollow;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mSign = itemView.findViewById(R.id.sign);
            mSex = itemView.findViewById(R.id.sex);
            mLevelAnchor = itemView.findViewById(R.id.level_anchor);
            mLevel = itemView.findViewById(R.id.level);
            mBtnFollow = itemView.findViewById(R.id.btn_follow);
            itemView.setOnClickListener(mClickListener);
            mBtnFollow.setOnClickListener(mFollowClickListener);
        }

        void setData(SearchUserBean bean, int position, Object payload) {
            itemView.setTag(bean);
            mBtnFollow.setTag(bean);
            if (payload == null) {
                ImgLoader.displayAvatar(mContext,bean.getAvatar(), mAvatar);
                mName.setText(bean.getUserNiceName());
                mSign.setText(bean.getSignature());
                mSex.setImageResource(CommonIconUtil.getSexIcon(bean.getSex()));
                LevelBean anchorLevelBean = CommonAppConfig.getInstance().getAnchorLevel(bean.getLevelAnchor());
                if (anchorLevelBean != null) {
                    ImgLoader.display(mContext,anchorLevelBean.getThumb(), mLevelAnchor);
                }
                LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
                if (levelBean != null) {
                    ImgLoader.display(mContext,levelBean.getThumb(), mLevel);
                }
            }
            if (mUid.equals(bean.getId())) {
                if (mBtnFollow.getVisibility() == View.VISIBLE) {
                    mBtnFollow.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mBtnFollow.getVisibility() != View.VISIBLE) {
                    mBtnFollow.setVisibility(View.VISIBLE);
                }
                if (bean.getAttention() == 1) {
                    mBtnFollow.doChecked(true);
                    mBtnFollow.setText(mFollowing);
                } else {
                    mBtnFollow.doChecked(false);
                    mBtnFollow.setText(mFollow);
                }
            }
        }

    }
}
