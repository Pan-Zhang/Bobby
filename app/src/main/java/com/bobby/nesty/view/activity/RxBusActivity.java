package com.bobby.nesty.view.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bobby.nesty.R;
import com.bobby.nesty.present.BasePresent;
import com.bobby.nesty.present.RxPresent;
import com.bobby.nesty.util.http.HttpUtil;
import com.bobby.nesty.util.http.ProgressListener;
import com.bobby.nesty.util.retrofit.MyRetrofit;
import com.bobby.nesty.util.retrofit.ServiceRetrofit;
import com.bobby.nesty.util.rxjava.EventType;
import com.bobby.nesty.util.rxjava.RxBus;
import com.bobby.nesty.view.viewcontrol.BaseCallback;
import com.bobby.nesty.view.viewcontrol.RxCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by corous360 on 2016/10/13.
 */
public class RxBusActivity extends BaseActivity<RxPresent> implements RxCallBack{
    private CompositeSubscription mCompositeSubscription;
    Button button;
    Button download;
    Button myget;
    Button downloadtest;
    Button uploadtest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_bus);
        rxBusPost();
        rxBusObservers();
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

        uploadtest = (Button) findViewById(R.id.uploadtest);

        uploadtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubscription(present.uploadFile());
            }
        });

        downloadtest = (Button) findViewById(R.id.downloadtest);
        downloadtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubscription(present.downloadFile());
            }
        });

        myget = (Button) findViewById(R.id.myget);
        myget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubscription(present.testGet());
            }
        });

        download = (Button) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubscription(present.downloadTest());
            }
        });
    }

    private void rxBusObservers() {

        addSubscription(present.clickEvent());

        addSubscription(present.longClickEvent());
    }

    @Override
    public void setPresent() {
        present = new RxPresent(this);
    }

    @Override
    public void showToast(String mess) {

    }

    @Override
    public void showToast(int id) {

    }
}