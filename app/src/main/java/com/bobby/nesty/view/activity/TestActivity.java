package com.bobby.nesty.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bobby.nesty.R;
import com.bobby.nesty.model.GirlsBean;
import com.bobby.nesty.present.TestPresent;
import com.bobby.nesty.util.rxjava.EventType;
import com.bobby.nesty.util.rxjava.RxBus;
import com.bobby.nesty.view.adapter.BaseRecyclerAdapter;
import com.bobby.nesty.view.adapter.HeaderAdapter;
import com.bobby.nesty.view.adapter.TestRecycleViewAdapter;
import com.bobby.nesty.view.viewcontrol.TestCallback;
import com.bobby.nesty.view.widget.ViewPagerHeader;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by corous360 on 2016/10/10.
 */
public class TestActivity extends BaseActivity<TestPresent> implements TestCallback, SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.recycleView)
    RecyclerView recyclerView;

    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.rxjavatest)
    TextView rxjavatest;

    TestRecycleViewAdapter recycleViewAdapter;
    HeaderAdapter headerAdapter;
    ViewPagerHeader viewPager;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        refreshLayout.setOnRefreshListener(this);

        recycleViewAdapter = new TestRecycleViewAdapter(this, null);
        initAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(recycleViewAdapter);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                beginRefresh();
                addSubscription(present.loadData());
            }
        }, 200);

        addSubscription(present.interval());
        addSubscription(present.subscribeClick());
//        addSubscription(present.testDatabase());
    }

    @Override
    public void setPresent() {
        present = new TestPresent(this);
    }

    private void initAdapter(){
        View view = LayoutInflater.from(this).inflate(R.layout.adapter_recycleheader_test, null);
        viewPager = (ViewPagerHeader) view.findViewById(R.id.viewpager);
        headerAdapter = new HeaderAdapter(this, null);
        viewPager.setAdapter(headerAdapter);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        ViewGroup.LayoutParams params = new RecyclerView.LayoutParams(dm.widthPixels, dm.widthPixels/2);
        view.setLayoutParams(params);
        recycleViewAdapter.setHeaderView(view);
        recycleViewAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<GirlsBean.ResultsEntity>() {
            @Override
            public void onItemClick(int position, GirlsBean.ResultsEntity data) {
                startActivity(new Intent(TestActivity.this, RxBusActivity.class));
                showToast(data.get_id());
            }
        });
    }

    @Override
    public void showToast(String mess) {
        showToastShort(mess);
    }

    @Override
    public void showToast(int id) {
        showToastShort(id);
    }

    @Override
    public void beginRefresh() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void stopRefrech(GirlsBean girlsBean) {
        if(girlsBean!=null){
            recycleViewAdapter.refresh(girlsBean);
            List<String> list = new ArrayList<String>();
            for(GirlsBean.ResultsEntity resultsEntity : girlsBean.getResults()){
                list.add(resultsEntity.getUrl());
            }
            headerAdapter.refresh(list);
        }
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void interval() {
        if(headerAdapter.getCount()!=0){
            viewPager.setCurrentItem((viewPager.getCurrentItem()+1)%headerAdapter.getCount(), true);
        }
    }

    @Override
    public void refreshTextView(String s) {
        rxjavatest.setText(s);
    }

    @Override
    public void onRefresh() {
        addSubscription(present.loadData());
    }
}
