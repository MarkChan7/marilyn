package com.markchan.marilyn;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Mark Chan on 2017/3/12.
 */
public class MarilynViewPager extends ViewPager {

    private static final float SENSE = 5;

    OnPageChangeListener mOuterPageChangeListener;
    private MarilynOnItemClickListener mOnItemClickListener;
    private MarilynPagerAdapter mAdapter;

    private boolean mManualScrollable = true;
    private boolean mLoopable = true;

    private float mLastX = 0;

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

        private float mPreviousPosition = -1;

        @Override
        public void onPageSelected(int position) {
            int realPosition = mAdapter.toRealPosition(position);
            if (mPreviousPosition != realPosition) {
                mPreviousPosition = realPosition;
                if (mOuterPageChangeListener != null) {
                    mOuterPageChangeListener.onPageSelected(realPosition);
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mOuterPageChangeListener != null) {
                if (position != mAdapter.getRealCount() - 1) {
                    mOuterPageChangeListener.onPageScrolled(position,
                            positionOffset, positionOffsetPixels);
                } else {
                    if (positionOffset > .5) {
                        mOuterPageChangeListener.onPageScrolled(0, 0, 0);
                    } else {
                        mOuterPageChangeListener.onPageScrolled(position, 0, 0);
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOuterPageChangeListener != null) {
                mOuterPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };

    public MarilynViewPager(Context context) {
        this(context, null);
    }

    public MarilynViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        super.setOnPageChangeListener(mOnPageChangeListener);
    }

    public void setAdapter(MarilynPagerAdapter adapter, boolean loopable) {
        mAdapter = adapter;
        mAdapter.setLoopable(loopable);
        mAdapter.setViewPager(this);
        super.setAdapter(mAdapter);
        setCurrentItem(getFirstItem(), false);
    }

    public int getFirstItem() {
        return mLoopable ? mAdapter.getRealCount() : 0;
    }

    public int getLastItem() {
        return mAdapter.getRealCount() - 1;
    }

    public boolean isManualScrollable() {
        return mManualScrollable;
    }

    public void setManualScrollable(boolean manualScrollable) {
        this.mManualScrollable = manualScrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mManualScrollable) {
            if (mOnItemClickListener != null) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastX = ev.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float x = ev.getX();
                        if (Math.abs(mLastX - x) < SENSE) {
                            mOnItemClickListener.onItemClick((getRealItem()));
                        }
                        mLastX = 0;
                        break;
                }
            }
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mManualScrollable && super.onInterceptTouchEvent(ev);
    }

    public MarilynPagerAdapter getAdapter() {
        return mAdapter;
    }

    public int getRealItem() {
        return mAdapter != null ? mAdapter.toRealPosition(super.getCurrentItem()) : 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOuterPageChangeListener = listener;
    }

    public boolean isLoopable() {
        return mLoopable;
    }

    public void setLoopable(boolean loopable) {
        this.mLoopable = loopable;
        if (!loopable) {
            setCurrentItem(getRealItem(), false);
        }
        if (mAdapter == null) {
            return;
        }
        mAdapter.setLoopable(loopable);
        mAdapter.notifyDataSetChanged();
    }

    public void setOnItemClickListener(MarilynOnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
