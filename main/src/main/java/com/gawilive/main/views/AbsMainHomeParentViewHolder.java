package com.gawilive.main.views;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gawilive.common.adapter.ViewPagerAdapter;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.im.utils.ImMessageUtil;
import com.gawilive.main.R;
import com.gawilive.main.interfaces.MainAppBarExpandListener;
import com.gawilive.main.interfaces.MainAppBarLayoutListener;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/2/20.
 */

public abstract class AbsMainHomeParentViewHolder extends AbsMainViewHolder {

//    protected AppBarLayout mAppBarLayout;
    protected ViewPager mViewPager;
    protected MagicIndicator mIndicator;
    private TextView mRedPoint;//显示未读消息数量的红点
    protected AbsMainHomeChildViewHolder[] mViewHolders;
    private MainAppBarLayoutListener mAppBarLayoutListener;
    private MainAppBarExpandListener mAppBarExpandListener;
    private boolean mPaused;
    protected List<FrameLayout> mViewList;
    private int mAppLayoutOffestY;

    public AbsMainHomeParentViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {
//        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
//        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateListener() {
//            @Override
//            public void onStateChanged(AppBarLayout appBarLayout, int state) {
//                switch (state) {
//                    case AppBarStateListener.EXPANDED:
//                        if (mAppBarExpandListener != null) {
//                            mAppBarExpandListener.onExpand(true);
//                        }
//                        break;
//                    case AppBarStateListener.MIDDLE:
//                    case AppBarStateListener.COLLAPSED:
//                        if (mAppBarExpandListener != null) {
//                            mAppBarExpandListener.onExpand(false);
//                        }
//                        break;
//                }
//            }
//        });
//        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                onAppBarOffsetChanged(-verticalOffset);
//                if (mAppBarLayoutListener != null) {
//                    if (verticalOffset > mAppLayoutOffestY) {
//                        mAppBarLayoutListener.onOffsetChangedDirection(false);
//                    } else if (verticalOffset < mAppLayoutOffestY) {
//                        mAppBarLayoutListener.onOffsetChangedDirection(true);
//                    }
//                    mAppLayoutOffestY = verticalOffset;
//                }
//            }
//        });
        mViewList = new ArrayList<>();
        int pageCount = getPageCount();
        for (int i = 0; i < pageCount; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewHolders = new AbsMainHomeChildViewHolder[pageCount];
        mViewPager = findViewById(R.id.viewPager);
        if (pageCount > 1) {
            mViewPager.setOffscreenPageLimit(pageCount - 1);
        }
        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                loadPageData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mIndicator = findViewById(R.id.indicator);
        final String[] titles = getTitles();
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                return getIndicatorTitleView(context, titles, index);
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setXOffset(DpUtil.dp2px(5));
                linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.global));
                return linePagerIndicator;
            }
        });
        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
        mRedPoint = findViewById(R.id.red_point);
        String[] unReadCountArr = ImMessageUtil.getInstance().getAllUnReadMsgCount();
        String unReadCount =
                unReadCountArr != null && unReadCountArr.length > 0 ? unReadCountArr[0] : "0";
        setUnReadCount(unReadCount);
    }

    public void onAppBarOffsetChanged(float verticalOffset) {

    }

    /**
     * 设置AppBarLayout滑动监听
     */
    public void setAppBarLayoutListener(MainAppBarLayoutListener appBarLayoutListener) {
        mAppBarLayoutListener = appBarLayoutListener;
    }

    /**
     * 设置AppBarLayout展开监听
     */
    public void setAppBarExpandListener(MainAppBarExpandListener appBarExpandListener) {
        mAppBarExpandListener = appBarExpandListener;
    }

    @Override
    public void loadData() {
        if (mViewPager != null) {
            loadPageData(mViewPager.getCurrentItem());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowed() && mPaused) {
            loadData();
        }
        mPaused = false;
    }


    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppBarLayoutListener = null;
        mAppBarExpandListener = null;
        super.onDestroy();
    }

    public void setCurrentPage(int position) {
        if (mViewPager == null) {
            return;
        }
        if (mViewPager.getCurrentItem() == position) {
            loadPageData(position);
        } else {
            mViewPager.setCurrentItem(position, false);
        }
    }

    /**
     * 显示未读消息
     */
    public void setUnReadCount(String unReadCount) {
        if (mRedPoint != null) {
            if ("0".equals(unReadCount)) {
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mRedPoint.getVisibility() != View.VISIBLE) {
                    mRedPoint.setVisibility(View.VISIBLE);
                }
                mRedPoint.setText(unReadCount);
            }
        }
    }


    protected IPagerTitleView getIndicatorTitleView(Context context, String[] titles, final int index) {
        SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
        simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.gray1));
        simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.textColor));
        simplePagerTitleView.setText(titles[index]);
        simplePagerTitleView.setTextSize(18);
        simplePagerTitleView.getPaint().setFakeBoldText(true);
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(index);
                }
            }
        });
        return simplePagerTitleView;
    }


    protected abstract void loadPageData(int position);

    protected abstract int getPageCount();

    protected abstract String[] getTitles();


}
