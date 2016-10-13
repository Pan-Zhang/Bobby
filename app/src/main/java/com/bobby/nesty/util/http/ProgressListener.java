package com.bobby.nesty.util.http;
import okhttp3.Callback;

/**
 * Created by corous360 on 2016/9/20.
 */
public interface ProgressListener extends Callback{

    void update(long bytesRead, long contentLength, boolean done);

}
