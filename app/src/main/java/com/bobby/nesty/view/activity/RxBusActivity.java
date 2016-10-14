package com.bobby.nesty.view.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bobby.nesty.R;
import com.bobby.nesty.present.BasePresent;
import com.bobby.nesty.util.http.ProgressListener;
import com.bobby.nesty.util.retrofit.MyRetrofit;
import com.bobby.nesty.util.retrofit.ServiceRetrofit;
import com.bobby.nesty.util.rxjava.EventType;
import com.bobby.nesty.util.rxjava.RxBus;
import com.bobby.nesty.view.viewcontrol.BaseCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
public class RxBusActivity extends BaseActivity<BasePresent<BaseCallback>> implements BaseCallback{
    private CompositeSubscription mCompositeSubscription;
    Button button;
    Button download;
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

        download = (Button) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubscription(MyRetrofit.DownloadProgress(new ProgressListener() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                            }

                            @Override
                            public void update(long bytesRead, long contentLength, boolean done) {
                                Log.e("rxjava", String.valueOf(100*bytesRead/contentLength));
                            }
                        }).create(ServiceRetrofit.class).getContact1().map(new Func1<ResponseBody, String>() {
                    @Override
                    public String call(ResponseBody response) {
                        InputStream is = null;
                        byte[] buf = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;
                        File desFile = new File(Environment.getExternalStorageDirectory().toString()+"/unit_test/zpyy.png");
                        if(!desFile.getParentFile().exists()){
                            desFile.getParentFile().mkdirs();
                        }
                        try
                        {
                            is = response.byteStream();
                            if(!desFile.exists()){
                                Boolean creat = desFile.createNewFile();
                            }else{
                                desFile.delete();
                            }
                            fos = new FileOutputStream(desFile);
                            while ((len = is.read(buf)) != -1)
                            {
                                fos.write(buf, 0, len);
                            }
                            fos.flush();
                        } catch (final IOException e)
                        {
                            e.printStackTrace();
                        } finally
                        {
                            try
                            {
                                if (is != null) is.close();
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            try
                            {
                                if (fos != null) fos.close();
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        return "success";
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<String>() {

                            @Override
                            public void onStart() {
                                super.onStart();
                            }

                            @Override
                            public void onCompleted() {
                                Toast.makeText(RxBusActivity.this, "main thread", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(String o) {

                            }
                        })
                );
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