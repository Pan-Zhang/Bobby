package com.bobby.nesty;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bobby.nesty.util.http.HttpUtil;
import com.bobby.nesty.view.activity.TestActivity;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    Button uploadtest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadtest = (Button) findViewById(R.id.uploadtest);
        final Map<String, String> map = new HashMap<>();
        final Map<String, String> map1 = new HashMap<>();
        String path = Environment.getExternalStorageDirectory().getPath();
        map1.put("file1", path + "/DCIM/Camera/-10d72371c8a50fd8.jpg");
        map1.put("file2", path + "/unit_test/zpyy.png");
        map.put("description1", "hello");
        map.put("description2", "yes");
        uploadtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.getInstance().uploadProgress("http://192.168.3.114:8080/LoginServlet/servlet/UploadServlet",
                        map, map1).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                                Log.e("rxjavaupload", "ok");
                            }

                            @Override
                            public void onError(Throwable e) {
                                String str = e.toString();
                            }

                            @Override
                            public void onNext(String s) {
                                Log.e("rxjavaupload", s);
                            }
                        });
            }
        });

        startActivity(new Intent(this, TestActivity.class));
    }
}
