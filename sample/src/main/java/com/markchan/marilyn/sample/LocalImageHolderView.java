package com.markchan.marilyn.sample;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.markchan.marilyn.PagerViewHolder;

public class LocalImageHolderView implements PagerViewHolder<Integer> {

    private ImageView mImageView;

    @Override
    public View createView(Context context) {
        mImageView = new ImageView(context);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return mImageView;
    }

    @Override
    public void updateUi(Context context, int position, Integer item) {
        mImageView.setImageResource(item);
    }
}
