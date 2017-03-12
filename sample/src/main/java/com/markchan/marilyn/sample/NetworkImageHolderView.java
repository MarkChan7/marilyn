package com.markchan.marilyn.sample;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.markchan.marilyn.PagerViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NetworkImageHolderView implements PagerViewHolder<String> {

    private ImageView mImageView;

    @Override
    public View createView(Context context) {
        mImageView = new ImageView(context);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return mImageView;
    }

    @Override
    public void updateUi(Context context, int position, String item) {
        mImageView.setImageResource(R.drawable.ic_placeholder);
        ImageLoader.getInstance().displayImage(item, mImageView);
    }
}
