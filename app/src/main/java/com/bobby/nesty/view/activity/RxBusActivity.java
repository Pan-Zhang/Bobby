package com.bobby.nesty.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bobby.nesty.R;
import com.bobby.nesty.present.BasePresent;
import com.bobby.nesty.util.rxjava.EventType;
import com.bobby.nesty.util.rxjava.RxBus;
import com.bobby.nesty.view.viewcontrol.BaseCallback;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by corous360 on 2016/10/13.
 */
public class RxBusActivity extends BaseActivity<BasePresent<BaseCallback>> implements BaseCallback{
    private CompositeSubscription mCompositeSubscription;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_bus);
        rxBusObservers();
        rxBusPost();
    }
    private void rxBusPost() {
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.getInstance().post(EventType.OnClick, "click");
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                RxBus.getInstance().post(EventType.OnLongClick, "longclick");
                return true;
            }
        });
    }

    private void rxBusObservers() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(EventType.OnClick, String.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(String event) {
                        Log.d("rxjava", event);
                    }
                });
        addSubscription(subscription);
        addSubscription(RxBus.getInstance()
                .toObservable(EventType.OnLongClick, String.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(String event) {
                        Log.d("rxjava", event);
                    }
                }));
    }

    @Override
    public void setPresent() {
        present = new BasePresent<BaseCallback>(this);
    }

    @Override
    public void showToast(String mess) {

    }

    @Override
    public void showToast(int id) {

    }
}