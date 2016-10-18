package com.bobby.nesty.util.retrofit;

import com.bobby.nesty.util.http.HttpUtil;
import com.bobby.nesty.util.http.ProgressListener;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by corous360 on 2016/10/2.
 */
public class MyRetrofit {

    private static Retrofit retrofit;
    private final static String BASE_URL = "http://gank.io/";

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (MyRetrofit.class) {
                if (retrofit == null) {
                    retrofit =new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(HttpUtil.getInstance().getmOkHttpClient())
                            .build();
                }
            }
        }
        return retrofit;
    }

    /**
     * 文件上传进度监听
     * @param progressListener 进度回调
     * @return
     */
    public static Retrofit UploadProgress(ProgressListener progressListener){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(HttpUtil.getInstance().addProgressRequestListener(progressListener))
                .build();
    }

    /**
     * 文件下载进度监听
     * @param progressListener 进度回调
     * @return
     */
    public static Retrofit DownloadProgress(ProgressListener progressListener){
        return new Retrofit.Builder()
                .baseUrl("http://pic32.nipic.com/20130829/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(HttpUtil.getInstance().addProgressResponseListener(progressListener))
                .build();
    }
}
