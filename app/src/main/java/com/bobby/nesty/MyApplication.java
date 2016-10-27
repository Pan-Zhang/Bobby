package com.bobby.nesty;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.bobby.nesty.util.database.DatabaseHelper;
import com.bobby.nesty.util.database.DatabaseManager;
import com.bobby.nesty.util.http.HttpUtil;
import com.bobby.nesty.util.http.OkHttp3Downloader;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by corous360 on 2016/10/10.
 */
public class MyApplication extends Application {

    public CrashHandler crashHandler;
    private List<Activity> activities;
    public static MyApplication mInstance;

    public String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();
    public final String DBPATH = "ZP";

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;


    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
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
        crashHandler = new CrashHandler(this);

        /**
         * init picasso
         */
        Picasso.setSingletonInstance(new Picasso.Builder(this)
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

    public void clearActivity() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }
}
