package com.bobby.nesty.util.http;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by corous360 on 2016/7/19.
 */
public abstract class HandlerJsonCallback<T> extends TypeToken<T> implements Callback {

    private Handler handler = new MyHandler(this);

    public abstract void success(T t);

    public abstract void failed(String str);

    static class MyHandler extends Handler{

        private WeakReference<okhttp3.Callback> mActivityWeakReference;

        public MyHandler(okhttp3.Callback callback) {
            mActivityWeakReference=new WeakReference<okhttp3.Callback>(callback);
        }

        @Override
        public void handleMessage(Message msg) {
            HandlerJsonCallback handlerJsonCallback = (HandlerJsonCallback) mActivityWeakReference.get();
            if(handlerJsonCallback != null){
                msg.what = 0;
                handlerJsonCallback.success(msg.obj);
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        failed(e.toString());
    }

    /**
     * [{"Title":"jjjj", "content":"kbsd"}, {"Title":"kkkk", "content":"2333"}]
     * @param call
     * @param response
     * @throws IOException
     */
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final T t = ParseJson(response.body().string());
        handler.post(new Runnable() {
            @Override
            public void run() {
                success(t);
            }
        });
    }

    public T ParseJson(String jsonString){
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonString,getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
