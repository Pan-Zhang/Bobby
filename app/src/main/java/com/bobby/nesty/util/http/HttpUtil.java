package com.bobby.nesty.util.http;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by corous360 on 2016/7/7.
 */
public class HttpUtil{

    private static HttpUtil mInstance;
    private final OkHttpClient mOkHttpClient;

    public OkHttpClient getmOkHttpClient(){
        return mOkHttpClient;
    }

    private String DealUrl(String url){
        return url;
    }

    private HttpUtil(){
        mOkHttpClient = new OkHttpClient.Builder().
                connectTimeout(10*1000, TimeUnit.MILLISECONDS).
                readTimeout(10*1000, TimeUnit.MILLISECONDS).
                writeTimeout(10*1000, TimeUnit.MILLISECONDS).
                connectionPool(new ConnectionPool(10,5, TimeUnit.MINUTES)).build();
    }

    public static HttpUtil getInstance(){
        if(mInstance == null){
            synchronized (HttpUtil.class){
                if(mInstance == null){
                    mInstance = new HttpUtil();
                }
            }
        }
        return mInstance;
    }

    public void get(String url, Callback callback) {
        Request.Builder builder = new Request.Builder();
        builder.url(DealUrl(url)).get();
        mOkHttpClient.newCall(builder.build()).enqueue(callback);
    }

    public void getProgress(String url, final ProgressListener progressListener) {
        Request.Builder builder = new Request.Builder();
        builder.url(DealUrl(url)).get();
        /**
         * this is a new okhttpclient
         */
        mOkHttpClient.newBuilder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(progressListener, originalResponse.body()))
                        .build();
            }
        }).build().newCall(builder.build()).enqueue(progressListener);
    }

    public void postForm(String url, Map<String, String> map, Callback callback) {
        Request.Builder builder = new Request.Builder();
        FormBody.Builder builder1 = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder1.add(entry.getKey(), entry.getValue());
        }
        mOkHttpClient.newCall(builder.url(DealUrl(url)).post(builder1.build()).build()).enqueue(callback);
    }

    public void postMultipart(String url, Map<String, String> map, Map<String, String> mapFile, Callback callback) {
        Request.Builder builder = new Request.Builder();
        MultipartBody.Builder builder1 = new MultipartBody.Builder();
        builder1.setType(MultipartBody.FORM);
        if(map != null){
            for(Map.Entry<String, String> entry : map.entrySet()){
                builder1.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        if(mapFile != null){
            for(Map.Entry<String, String> entry : mapFile.entrySet()){
                File file = new File(entry.getValue());
                if(file.exists()){
                    builder1.addFormDataPart(entry.getKey(), file.getName(),
                            RequestBody.create(MediaType.parse("application/octet-stream"), file));
                }
            }
        }
        mOkHttpClient.newCall(builder.url(DealUrl(url)).post(builder1.build()).build()).enqueue(callback);
    }

    public void postMultipartProgress(String url, Map<String, String> map, Map<String, String> mapFile, ProgressListener progressListener) {
        Request.Builder builder = new Request.Builder();
        MultipartBody.Builder builder1 = new MultipartBody.Builder();
        builder1.setType(MultipartBody.FORM);
        if(map != null){
            for(Map.Entry<String, String> entry : map.entrySet()){
                builder1.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        if(mapFile != null){
            for(Map.Entry<String, String> entry : mapFile.entrySet()){
                File file = new File(entry.getValue());
                if(file.exists()){
                    builder1.addFormDataPart(entry.getKey(), file.getName(),
                            RequestBody.create(MediaType.parse("application/octet-stream"), file));
                }
            }
        }
        mOkHttpClient.newCall(builder.url(DealUrl(url)).
                post(new ProgressRequestBody(builder1.build(), progressListener)).build()).enqueue(progressListener);
    }

    public void postMultipartHttpEx(String url, Map<String, String> map, Map<String, String> mapFile,
                                     Callback callback, ProgressListener progressListener){
        Request.Builder builder = new Request.Builder();
        MultipartBody.Builder builder1 = new MultipartBody.Builder();
        builder1.setType(MultipartBody.FORM);
        if(map != null){
            for(Map.Entry<String, String> entry : map.entrySet()){
                builder1.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\""+entry.getKey()+"\""),
                        RequestBody.create(null, entry.getValue()));
            }
        }
        if(mapFile != null){
            for(Map.Entry<String, String> entry : mapFile.entrySet()){
                File file = new File(entry.getValue());
                if(file.exists()){
                    builder1.addPart(Headers.of(
                            "Content-Disposition",
                            "form-data; name=\""+entry.getKey()+"\"; filename=\"" + file.getName() + "\""),
                            RequestBody.create(MediaType.parse("application/octet-stream"), file));
                }
            }
        }
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(builder1.build(), progressListener);
        mOkHttpClient.newCall(builder.url(DealUrl(url)).post(progressRequestBody).build()).enqueue(callback);
    }

    /**
     * 包装OkHttpClient，用于下载文件的回调
     * @param progressListener 进度回调接口
     * @return 包装后的OkHttpClient
     */
    public OkHttpClient addProgressResponseListener(final ProgressListener progressListener){
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        //增加拦截器
        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //拦截
                Response originalResponse = chain.proceed(chain.request());

                //包装响应体并返回
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(progressListener, originalResponse.body()))
                        .build();
            }
        });
        return client.build();
    }


    /**
     * 包装OkHttpClient，用于上传文件的回调
     * @param progressListener 进度回调接口
     * @return 包装后的OkHttpClient
     */
    public OkHttpClient addProgressRequestListener(final ProgressListener progressListener){
        OkHttpClient.Builder client = mOkHttpClient.newBuilder();
        //增加拦截器
        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .method(original.method(), new ProgressRequestBody(original.body(),progressListener))
                        .build();
                return chain.proceed(request);
            }
        });
        return client.build();
    }

}
