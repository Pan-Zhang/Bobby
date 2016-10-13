package com.bobby.nesty.util.http;

import java.util.Map;

import okhttp3.Callback;

/**
 * Created by corous360 on 2016/9/20.
 */
public interface httpDao {

    void get(String url, Callback callback);

    void getProgress(String url, ProgressListener progressListener);

    void postForm(String url, Map<String, String> map, Callback callback);

    void postMultipart(String url, Map<String, String> map, Map<String, String> mapFile, Callback callback);

    void postMultipartProgress(String url, Map<String, String> map, Map<String, String> mapFile, ProgressListener progressListener);
}
