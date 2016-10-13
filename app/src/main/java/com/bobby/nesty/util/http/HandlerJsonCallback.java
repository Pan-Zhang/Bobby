package com.bobby.nesty.util.http;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by corous360 on 2016/7/19.
 */
public abstract class HandlerJsonCallback<T> extends TypeToken<T> implements Callback {

    private static Handler handler = new Handler(Looper.getMainLooper());

    public abstract void success(T t);

    public abstract void failed(String str);

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
