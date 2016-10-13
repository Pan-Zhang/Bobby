package com.bobby.nesty;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.os.Environment;

import com.bobby.nesty.util.database.DatabaseHelper;
import com.bobby.nesty.util.database.DatabaseManager;
import com.bobby.nesty.util.http.HttpUtil;
import com.bobby.nesty.util.http.OkHttp3Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by corous360 on 2016/10/10.
 */
public class MyApplication extends Application {

    private LruCache lruCache;
    public static MyApplication mInstance;
    public CrashHandler crashHandler;
    private List<Activity> activities;

    public String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();
    public final String DBPATH = "ZP";

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * create file you need
         */
        CreateFile();

        /**
         * init database
         */
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        DatabaseManager.initializeInstance(databaseHelper);

        /**
         * catch log
         */
        crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        /**
         * init picasso
         */
        lruCache = new LruCache(this);
        Picasso.setSingletonInstance(new Picasso.Builder(this).memoryCache(lruCache)
                .downloader(new OkHttp3Downloader(HttpUtil.getInstance().getmOkHttpClient())).build());

        /**
         * others
         */
        mInstance = this;
        activities = new LinkedList<Activity>();
    }

    private void CreateFile() {
        File file = new File(SDCARD_PATH+"/"+DBPATH);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    public void addActivity(Activity activity){
        activities.add(activity);
    }

    public void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public void addBitmap(String key, Bitmap bitmap){
        lruCache.set(key, bitmap);
    }

    public void removeBitmap(String key){
        lruCache.clearKeyUri(key);
    }

    public Bitmap getBitmap(String key){
        return lruCache.get(key);
    }

    public void removeAll(){
        lruCache.clear();
    }

    public void clearActivity() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }
}
