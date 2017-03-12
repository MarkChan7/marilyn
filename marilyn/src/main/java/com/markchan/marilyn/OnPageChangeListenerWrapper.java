package com.markchan.marilyn;

import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import java.util.List;

/**
 * Created by Mark Chan on 2017/3/12.
 */
public class OnPageChangeListenerWrapper implements ViewPager.OnPageChangeListener {

    private List<ImageView> mPageIndicatorViews;
    private int[] mPageIndicatorStateResIdArr;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    public OnPageChangeListenerWrapper(List<ImageView> pageIndicatorViews,
            int pageIndicatorStateResIdArr[]) {
        mPageIndicatorViews = pageIndicatorViews;
        mPageIndicatorStateResIdArr = pageIndicatorStateResIdArr;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int index) {
        for (int i = 0; i < mPageIndicatorViews.size(); i++) {
            mPageIndicatorViews.get(index).setImageResource(mPageIndicatorStateResIdArr[1]);
            if (index != i) {
                mPageIndicatorViews.get(i).setImageResource(mPageIndicatorStateResIdArr[0]);
            }
        }
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(index);
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }
}
