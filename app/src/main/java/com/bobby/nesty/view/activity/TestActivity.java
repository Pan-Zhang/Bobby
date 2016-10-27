package com.bobby.nesty.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bobby.nesty.MyApplication;
import com.bobby.nesty.R;
import com.bobby.nesty.model.GirlsBean;
import com.bobby.nesty.present.TestPresent;
import com.bobby.nesty.util.Common.WeakHandler;
import com.bobby.nesty.util.rxjava.EventType;
import com.bobby.nesty.util.rxjava.RxBus;
import com.bobby.nesty.view.adapter.BaseRecyclerAdapter;
import com.bobby.nesty.view.adapter.HeaderAdapter;
import com.bobby.nesty.view.adapter.TestRecycleViewAdapter;
import com.bobby.nesty.view.viewcontrol.TestCallback;
import com.bobby.nesty.view.widget.ViewPagerHeader;
import com.squareup.leakcanary.RefWatcher;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by corous360 on 2016/10/10.
 */
public class TestActivity extends BaseActivity<TestPresent> implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.recycleView)
    RecyclerView recyclerView;

    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.rxjavatest)
    TextView rxjavatest;

    TestRecycleViewAdapter recycleViewAdapter;
    HeaderAdapter headerAdapter;
    ViewPagerHeader viewPager;

    WeakHandler handler = new WeakHandler();

    MyCallback myCallback = new MyCallback(this);

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
                myCallback.beginRefresh();
                addSubscription(present.loadData());
            }
        }, 200);

        addSubscription(present.interval());
        addSubscription(present.subscribeClick());
//        addSubscription(present.testDatabase());
    }

    @Override
    public void setPresent() {
        present = new TestPresent(myCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        refWatcher.watch(this);
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
        addSubscription(RxBus.getInstance().toObservable(EventType.RecycleadapterClick, GirlsBean.ResultsEntity.class)
                .subscribe(new Subscriber<GirlsBean.ResultsEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GirlsBean.ResultsEntity resultsEntity) {
                        startActivity(new Intent(TestActivity.this, RxBusActivity.class));
                        showToastShort(resultsEntity.get_id());
                    }
                })
        );
    }

    @Override
    public void onRefresh() {
        addSubscription(present.loadData());
    }

    private static class MyCallback implements TestCallback{

        WeakReference<Activity> activityWeakReference;

        public MyCallback(Activity activity){
            activityWeakReference = new WeakReference<Activity>(activity);
        }

        public TestActivity getActivity(){
            if(activityWeakReference.get() != null){
                return (TestActivity)activityWeakReference.get();
            }
            return null;
        }

        @Override
        public void beginRefresh() {
            if(getActivity()!=null)getActivity().refreshLayout.setRefreshing(true);
        }

        @Override
        public void stopRefrech(GirlsBean girlsBean) {
            if(getActivity()!=null){
                if(girlsBean!=null){
                    getActivity().recycleViewAdapter.refresh(girlsBean);
                    List<String> list = new ArrayList<String>();
                    for(GirlsBean.ResultsEntity resultsEntity : girlsBean.getResults()){
                        list.add(resultsEntity.getUrl());
                    }
                    getActivity().headerAdapter.refresh(list);
                }
                getActivity().refreshLayout.setRefreshing(false);
            }

        }

        @Override
        public void interval() {
            if(getActivity()!=null && getActivity().headerAdapter.getCount()!=0){
                getActivity().viewPager.setCurrentItem((getActivity().viewPager.getCurrentItem()+1)%getActivity().headerAdapter.getCount(), true);
            }
        }

        @Override
        public void refreshTextView(String s) {
            if(getActivity()!=null) getActivity().rxjavatest.setText(s);
        }

        @Override
        public void showToast(String mess) {
            if(getActivity()!=null)
                getActivity().showToastShort(mess);
        }

        @Override
        public void showToast(int id) {
            if(getActivity()!=null)
                getActivity().showToastShort(id);
        }
    }
}
