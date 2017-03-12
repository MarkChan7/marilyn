package com.markchan.marilyn;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 页面翻转控件. 支持无限循环, 自动翻页, 翻页特效
 *
 * Created by Mark Chan on 2017/3/12.
 */
public class Marilyn extends RelativeLayout {

    private List<?> mData;
    private List<ImageView> mPageIndicatorViews = new ArrayList<>();
    private int[] mPageIndicatorStateRedIdArr;
    private OnPageChangeListenerWrapper mOnPageChangeListenerWrapper;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private MarilynPagerAdapter<?> mAdapter;
    private MarilynViewPager mViewPager;
    private ViewPagerScroller mScroller;
    private LinearLayout mPageIndicatorContainer;
    private long mAutoTurningTime;
    private boolean mTurning;
    private boolean mTurnable = false;
    private boolean mLoopable = true;

    private int mPageIndicatorMargin;
    private int mPageIndicatorSpace;

    private int mViewPagerMarginLeft;
    private int mViewPagerMarginRight;

    public enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
    }

    private SwitchTask mSwitchTask;

    public Marilyn(Context context) {
        this(context, null);
    }

    public Marilyn(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Marilyn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Marilyn(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Marilyn);
            mLoopable = ta.getBoolean(R.styleable.Marilyn_loopable, true);
            mViewPagerMarginLeft = (int) ta
                    .getDimension(R.styleable.Marilyn_viewPager_marginLeft, 0);
            mViewPagerMarginRight = (int) ta
                    .getDimension(R.styleable.Marilyn_viewPager_marginRight, 0);
            mPageIndicatorMargin = (int) ta.getDimension(R.styleable.Marilyn_pageIndicator_margin,
                    getContext().getResources()
                            .getDimension(R.dimen.page_indicator_container_default_margin));
            mPageIndicatorSpace = (int) ta.getDimension(R.styleable.Marilyn_pageIndicator_space,
                    getContext().getResources()
                            .getDimension(R.dimen.page_indicator_container_default_space));
            ta.recycle();
        }

        initView(context);

        initScroller();

        mSwitchTask = new SwitchTask(this);
    }

    private void initView(Context context) {
        mViewPager = new MarilynViewPager(context);
        LayoutParams lp = new LayoutParams(-1, -1);
        lp.setMargins(mViewPagerMarginLeft, 0, mViewPagerMarginRight, 0);
        addView(mViewPager, lp);

        mPageIndicatorContainer = new LinearLayout(context);
        lp = new LayoutParams(-2, -2);
        lp.setMargins(mPageIndicatorMargin, mPageIndicatorMargin, mPageIndicatorMargin,
                mPageIndicatorMargin);
        mPageIndicatorContainer.setOrientation(LinearLayout.HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        addView(mPageIndicatorContainer, lp);
    }

    private class SwitchTask implements Runnable {

        private final WeakReference<Marilyn> mMarilynRef;

        SwitchTask(Marilyn marilyn) {
            mMarilynRef = new WeakReference<>(marilyn);
        }

        @Override
        public void run() {
            Marilyn marilyn = mMarilynRef.get();
            if (marilyn != null) {
                if (marilyn.mViewPager != null && marilyn.mTurning) {
                    int page = marilyn.mViewPager.getCurrentItem() + 1;
                    marilyn.mViewPager.setCurrentItem(page);
                    marilyn.postDelayed(marilyn.mSwitchTask, marilyn.mAutoTurningTime);
                }
            }
        }
    }

    public Marilyn setPages(PagerViewHolderCreator holderCreator, List data) {
        mData = data;
        mAdapter = new MarilynPagerAdapter(holderCreator, mData);
        mViewPager.setAdapter(mAdapter, mLoopable);
        if (mPageIndicatorStateRedIdArr != null) {
            setPageIndicator(mPageIndicatorStateRedIdArr);
        }
        return this;
    }

    /**
     * 通知数据变化. 如果只是增加数据建议使用 notifyDataSetAdd()
     */
    public void notifyDataSetChanged() {
        mViewPager.getAdapter().notifyDataSetChanged();
        if (mPageIndicatorStateRedIdArr != null) {
            setPageIndicator(mPageIndicatorStateRedIdArr);
        }
    }

    /**
     * 设置底部指示器是否可见
     */
    public Marilyn setPageIndicatorVisibility(boolean visibility) {
        mPageIndicatorContainer.setVisibility(visibility ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 底部指示器资源图片
     */
    public Marilyn setPageIndicator(int[] stateResIdArr) {
        mPageIndicatorContainer.removeAllViews();
        mPageIndicatorViews.clear();
        mPageIndicatorStateRedIdArr = stateResIdArr;
        if (mData == null) {
            return this;
        }
        for (int count = 0; count < mData.size(); count++) {
            ImageView pageIndicatorImageView = new ImageView(getContext());
            pageIndicatorImageView.setPadding(mPageIndicatorSpace, 0, mPageIndicatorSpace, 0);
            if (mPageIndicatorViews.isEmpty()) {
                pageIndicatorImageView.setImageResource(stateResIdArr[1]);
            } else {
                pageIndicatorImageView.setImageResource(stateResIdArr[0]);
            }
            mPageIndicatorViews.add(pageIndicatorImageView);
            mPageIndicatorContainer.addView(pageIndicatorImageView);
        }
        mOnPageChangeListenerWrapper = new OnPageChangeListenerWrapper(mPageIndicatorViews,
                stateResIdArr);
        mViewPager.setOnPageChangeListener(mOnPageChangeListenerWrapper);
        mOnPageChangeListenerWrapper.onPageSelected(mViewPager.getRealItem());
        if (mOnPageChangeListener != null) {
            mOnPageChangeListenerWrapper.setOnPageChangeListener(mOnPageChangeListener);
        }
        return this;
    }

    /**
     * 指示器的方向
     *
     * @param align 三个方向：居左 （RelativeLayout.ALIGN_PARENT_LEFT）, 居中 （RelativeLayout.CENTER_HORIZONTAL）,
     * 居右 （RelativeLayout.ALIGN_PARENT_RIGHT）
     */
    public Marilyn setPageIndicatorAlign(PageIndicatorAlign align) {
        LayoutParams lp = (RelativeLayout.LayoutParams) mPageIndicatorContainer.getLayoutParams();
        int alignVerb;
        switch (align) {
            case ALIGN_PARENT_LEFT:
                alignVerb = RelativeLayout.ALIGN_PARENT_LEFT;
                break;
            case ALIGN_PARENT_RIGHT:
                alignVerb = RelativeLayout.ALIGN_PARENT_RIGHT;
                break;
            case CENTER_HORIZONTAL:
            default:
                alignVerb = RelativeLayout.CENTER_HORIZONTAL;
                break;
        }
        lp.addRule(alignVerb, RelativeLayout.TRUE);
        mPageIndicatorContainer.setLayoutParams(lp);
        return this;
    }

    /***
     * 是否开启了翻页
     */
    public boolean isTurning() {
        return mTurning;
    }

    /***
     * 开始翻页
     * @param autoTurningTime 自动翻页时间
     */
    public Marilyn startTurning(long autoTurningTime) {
        if (mTurning) {
            stopTurning();
        }
        mTurnable = true;
        mAutoTurningTime = autoTurningTime;
        mTurning = true;
        postDelayed(mSwitchTask, autoTurningTime);
        return this;
    }

    public void stopTurning() {
        mTurning = false;
        removeCallbacks(mSwitchTask);
    }

    /**
     * 自定义翻页动画效果
     */
    public Marilyn setPageTransformer(PageTransformer transformer) {
        mViewPager.setPageTransformer(true, transformer);
        return this;
    }

    /**
     * 设置ViewPager的滑动速度
     */
    private void initScroller() {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            mScroller = new ViewPagerScroller(mViewPager.getContext());
            scrollerField.set(mViewPager, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isManualScrollable() {
        return mViewPager.isManualScrollable();
    }

    public void setManualScrollable(boolean manualScrollable) {
        mViewPager.setManualScrollable(manualScrollable);
    }

    /**
     * 触碰控件的时候, 翻页应该停止, 离开的时候如果之前是开启了翻页的话则重新启动翻页
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_OUTSIDE) {
            if (mTurnable) { // 开始翻页
                startTurning(mAutoTurningTime);
            }
        } else if (action == MotionEvent.ACTION_DOWN) {
            if (mTurnable) { // 停止翻页
                stopTurning();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public int getCurrentItem() {
        if (mViewPager != null) {
            return mViewPager.getRealItem();
        }
        return -1;
    }

    public void setCurrentItem(int item) {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(item);
        }
    }

    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return mOnPageChangeListener;
    }

    /**
     * 设置翻页监听器
     */
    public Marilyn setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
        // 如果有默认的监听器(即是使用了默认的翻页指示器), 则把用户设置的依附到默认的上面, 否则就直接设置
        if (mOnPageChangeListenerWrapper != null) {
            mOnPageChangeListenerWrapper.setOnPageChangeListener(onPageChangeListener);
        } else {
            mViewPager.setOnPageChangeListener(onPageChangeListener);
        }
        return this;
    }

    public boolean isLoopable() {
        return mViewPager.isLoopable();
    }

    /**
     * 监听item点击事件
     */
    public Marilyn setOnItemClickListener(MarilynOnItemClickListener onItemClickListener) {
        mViewPager.setOnItemClickListener(onItemClickListener);
        return this;
    }

    /**
     * 设置ViewPager的滚动速度
     */
    public void setScrollDuration(int scrollDuration) {
        mScroller.setScrollDuration(scrollDuration);
    }

    public int getScrollDuration() {
        return mScroller.getScrollDuration();
    }

    public MarilynViewPager getViewPager() {
        return mViewPager;
    }

    public void setLoopable(boolean loopable) {
        mLoopable = loopable;
        mViewPager.setLoopable(loopable);
    }
}
