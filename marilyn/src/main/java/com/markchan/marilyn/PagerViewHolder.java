package com.markchan.marilyn;

import android.content.Context;
import android.view.View;

/**
 * Created by Mark Chan on 2017/3/12.
 */
public interface PagerViewHolder<T> {

    View createView(Context context);

    void updateUi(Context context, int position, T item);
}