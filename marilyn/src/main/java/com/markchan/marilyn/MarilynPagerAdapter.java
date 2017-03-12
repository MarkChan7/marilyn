package com.markchan.marilyn;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Mark Chan on 2017/3/12.
 */
public class MarilynPagerAdapter<T> extends PagerAdapter {

    private static final int MULTIPLE_COUNT = 300;

    protected List<T> mData;
    protected PagerViewHolderCreator<T> mViewHolderCreator;
    private MarilynViewPager mViewPager;
    private boolean mLoopable = true;

    public MarilynPagerAdapter(PagerViewHolderCreator<T> viewHolderCreator, List<T> data) {
        mViewHolderCreator = viewHolderCreator;
        mData = data;
    }

    @Override
    public int getCount() {
        return mLoopable ? getRealCount() * MULTIPLE_COUNT : getRealCount();
    }

    public int toRealPosition(int position) {
        int realCount = getRealCount();
        return realCount == 0 ? 0 : position % realCount;
    }

    public int getRealCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = toRealPosition(position);
        View view = getView(realPosition, null, container);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        int position = mViewPager.getCurrentItem();
        if (position == 0) {
            position = mViewPager.getFirstItem();
        } else if (position == getCount() - 1) {
            position = mViewPager.getLastItem();
        }
        try {
            mViewPager.setCurrentItem(position, false);
        } catch (Exception ignored) {
            ;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setViewPager(MarilynViewPager viewPager) {
        mViewPager = viewPager;
    }

    public void setLoopable(boolean loopable) {
        mLoopable = loopable;
    }

    public View getView(int position, View view, ViewGroup container) {
        PagerViewHolder<T> holder;
        if (view == null) {
            holder = mViewHolderCreator.createHolder();
            view = holder.createView(container.getContext());
            view.setTag(R.id.marilyn_item_tag, holder);
        } else {
            holder = (PagerViewHolder<T>) view.getTag(R.id.marilyn_item_tag);
        }
        if (mData != null && !mData.isEmpty()) {
            holder.updateUi(container.getContext(), position, mData.get(position));
        }
        return view;
    }
}