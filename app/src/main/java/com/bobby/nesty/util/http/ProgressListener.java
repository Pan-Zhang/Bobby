package com.bobby.nesty.util.http;

import okhttp3.Callback;

/**
 * 进度监听统一接口，继承Callback回调接口方便使用
 * Created by corous360 on 2016/9/20.
 */
public interface ProgressListener extends Callback{

    void update(long bytesRead, long contentLength, boolean done);

}
