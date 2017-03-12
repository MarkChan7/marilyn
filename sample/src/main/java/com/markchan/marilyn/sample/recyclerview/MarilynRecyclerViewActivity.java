package com.markchan.marilyn.sample.recyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.markchan.marilyn.Marilyn;
import com.markchan.marilyn.PagerViewHolderCreator;
import com.markchan.marilyn.sample.NetworkImageHolderView;
import com.markchan.marilyn.sample.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarilynRecyclerViewActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<String> mData = new ArrayList<>();
    private MarilynRecyclerViewAdapter mAdapter;
    private Marilyn mMarilyn;

    private List<String> mImageUrls;
    private String[] mImageUrlArr = {
            "http://img2.imgtn.bdimg.com/it/u=3093785514,1341050958&fm=21&gp=0.jpg",
            "http://img2.3lian.com/2014/f2/37/d/40.jpg",
            "http://d.3987.com/sqmy_131219/001.jpg",
            "http://img2.3lian.com/2014/f2/37/d/39.jpg",
            "http://www.8kmm.com/UploadFiles/2012/8/201208140920132659.jpg",
            "http://f.hiphotos.baidu.com/image/h%3D200/sign=1478eb74d5a20cf45990f9df460b4b0c/d058ccbf6c81800a5422e5fdb43533fa838b4779.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/09fa513d269759ee50f1971ab6fb43166c22dfba.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        init();
        initEvents();
    }

    private void initViews() {
        setContentView(R.layout.acitvity_marilyn_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        mMarilyn = (Marilyn) LayoutInflater.from(this).inflate(R.layout.item_marilyn_header, null);
        mMarilyn.setLayoutParams(
                new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600));
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    private void init() {
        initImageLoader();

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MarilynRecyclerViewAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);

        mImageUrls = Arrays.asList(mImageUrlArr);
        mMarilyn.setPages(new PagerViewHolderCreator() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, mImageUrls)
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator_normal,
                        R.drawable.ic_page_indicator_focused});

        mAdapter.addHeader(mMarilyn);
        loadData();
    }

    private void initEvents() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                showImageForEmptyUri(R.drawable.ic_placeholder)
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    private void loadData() {
        mData.add("test＝＝＝＝＝＝＝＝＝＝＝");
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMarilyn.startTurning(5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMarilyn.stopTurning();
    }

    @Override
    public void onRefresh() {
        mAdapter.addData("onRefresh  ===test========");
        mAdapter.addData("onRefresh  ===test========");
        mAdapter.addData("onRefresh  ===test========");
        mAdapter.addData("onRefresh  ===test========");
        mAdapter.addData("onRefresh  ===test========");
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
