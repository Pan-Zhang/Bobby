package com.bobby.nesty.util.http;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
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
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

    /**
     * 构造方法，对client初始化工作
     */
    private HttpUtil(){
        mOkHttpClient = new OkHttpClient.Builder().
                connectTimeout(10*1000, TimeUnit.MILLISECONDS).
                readTimeout(10*1000, TimeUnit.MILLISECONDS).
                writeTimeout(10*1000, TimeUnit.MILLISECONDS).
                connectionPool(new ConnectionPool(10,5, TimeUnit.MINUTES)).build();
    }

    /**
     * 单例模式
     * @return this
     */
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

    /**
     * rxjava封装okhttp同步方法返回结果<Response>，虽然okhttp的execute是同步方式，但是由于rxjava默认
     * 在Schedulers.computation()调度器中执行，所以不会报错，但是该调度器的主要针对计算工作。所以，
     * 如果后面的工作涉及到io相关等建议切换调度器。
     * @param url 请求地址
     * @return 返回
     */
    public Observable<Response> get(String url) {
        final Request.Builder builder = new Request.Builder();
        builder.url(DealUrl(url)).get();
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                try{
                    subscriber.onNext(mOkHttpClient.newCall(builder.build()).execute());
                }catch (Exception e){
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * 文件下载进度使用rxjava与okhttp进行封装的方法
     * @param url  下载链接地址
     * @param srcFile 保存目标文件
     * @return 返回进度
     */
    public Observable<String> downloadProgress(String url, final String srcFile){
        final Request.Builder builder = new Request.Builder();
        builder.url(DealUrl(url)).get();
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                mOkHttpClient.newBuilder().addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(new ProgressListener() {
                                    @Override
                                    public void update(long bytesRead, long contentLength, boolean done) {
                                        subscriber.onNext(String.valueOf(100*bytesRead/contentLength));
                                    }

                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                    }
                                }, originalResponse.body())).build();
                    }}).build().newCall(builder.build()).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        InputStream is = null;
                        byte[] buf = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;
                        File desFile = new File(srcFile);
                        if (!desFile.getParentFile().exists()) {
                            desFile.getParentFile().mkdirs();
                        }
                        try {
                            is = response.body().byteStream();
                            if (!desFile.exists()) {
                                Boolean creat = desFile.createNewFile();
                            } else {
                                desFile.delete();
                            }
                            fos = new FileOutputStream(desFile);
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                            }
                            fos.flush();
                        } catch (final IOException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        } finally {
                            try {
                                if (is != null) is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                if (fos != null) fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 利用rxjava与okhttp对上传文件进度回调的监听封装
     * @param url 上传地址
     * @param mapString string参数
     * @param mapFile 文件
     * @return 返回进度
     */
    public Observable<String> uploadProgress(final String url, final Map<String, String> mapString, final Map<String, String> mapFile){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                Request.Builder builder = new Request.Builder();
                MultipartBody.Builder builder1 = new MultipartBody.Builder();
                builder1.setType(MultipartBody.FORM);
                if(mapString != null){
                    for(Map.Entry<String, String> entry : mapString.entrySet()){
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
                        post(new ProgressRequestBody(builder1.build(), new ProgressListener() {
                            @Override
                            public void update(long bytesRead, long contentLength, boolean done) {
                                subscriber.onNext(String.valueOf(100*bytesRead/contentLength));
                            }

                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                            }
                        }))
                        .build()).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if(response.isSuccessful()){
                                    subscriber.onCompleted();
                                }
                            }
                        });
            }
        });
    }

    /**
     * 普通的okhttp下载进度监听
     * @param url 下载链接
     * @param progressListener 该接口继承okhttp的Callback接口，所以进度，成功以及失败的回调都在这里完成
     */
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

    /**
     * post方式普通封装
     * @param url 地址链接
     * @param map 参数
     * @param callback 回调
     */
    public void postForm(String url, Map<String, String> map, Callback callback) {
        Request.Builder builder = new Request.Builder();
        FormBody.Builder builder1 = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder1.add(entry.getKey(), entry.getValue());
        }
        mOkHttpClient.newCall(builder.url(DealUrl(url)).post(builder1.build()).build()).enqueue(callback);
    }

    /**
     * post复杂类型封装
     * @param url 地址
     * @param map 普通参数
     * @param mapFile 上传文件参数
     * @param callback 回调
     */
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

    /**
     * 上传文件进度回调封装
     * @param url 链接
     * @param map 普通参数
     * @param mapFile 文件参数
     * @param progressListener 该接口继承okhttp的Callback接口，所以进度，成功以及失败的回调都在这里完成
     */
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

    /**
     * 扩展
     * @param url
     * @param map
     * @param mapFile
     * @param callback
     * @param progressListener
     */
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
        OkHttpClient.Builder client = mOkHttpClient.newBuilder();
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
