package com.bobby.nesty.present;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.bobby.nesty.util.http.HttpUtil;
import com.bobby.nesty.util.http.ProgressListener;
import com.bobby.nesty.util.retrofit.MyRetrofit;
import com.bobby.nesty.util.retrofit.ServiceRetrofit;
import com.bobby.nesty.util.rxjava.EventType;
import com.bobby.nesty.util.rxjava.RxBus;
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

/**
 * Created by corous360 on 2016/10/18.
 */
public class RxPresent extends BasePresent<RxCallBack> {

    public RxPresent(RxCallBack rxCallBack) {
        super(rxCallBack);
    }

    public Subscription uploadFile(){
        final Map<String, String> map = new HashMap<>();
        final Map<String, String> map1 = new HashMap<>();
        String path = Environment.getExternalStorageDirectory().getPath();
        map1.put("file1", path + "/DCIM/Camera/-10d72371c8a50fd8.jpg");
        map1.put("file2", path + "/unit_test/zpyy.png");
        map.put("description1", "hello");
        map.put("description2", "yes");
        return HttpUtil.getInstance().uploadProgress("http://192.168.3.114:8080/LoginServlet/servlet/UploadServlet",
                map, map1).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.e("rxjavaupload", "ok");
                    }

                    @Override
                    public void onError(Throwable e) {
                        String str = e.toString();
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e("rxjavaupload", s);
                    }
                });
    }

    public Subscription downloadFile(){
        return HttpUtil.getInstance().downloadProgress("http://pic32.nipic.com/20130829/12906030_124355855000_2.png", Environment.getExternalStorageDirectory().toString() + "/unit_test/zpyy.png")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        String str = e.toString();
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e("rxjava111",s);
                    }
                });
    }

    public Subscription testGet(){
        return HttpUtil.getInstance().get("http://www.baidu.com")
                .map(new Func1<Response, String>() {
                    @Override
                    public String call(Response response) {
                        try{
                            return response.body().string();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !TextUtils.isEmpty(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        String str = e.toString();
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e("rxjava","success!!!!");
                    }
                });
    }

    public Subscription downloadTest(){
        return MyRetrofit.DownloadProgress(new ProgressListener() {
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
                        Callback.showToast("main thread");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String o) {

                    }
                });
    }

    public Subscription clickEvent(){
        return RxBus.getInstance()
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
    }

    public Subscription longClickEvent(){
        return RxBus.getInstance()
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
                });
    }
}
