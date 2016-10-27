package com.bobby.nesty;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bobby.nesty.util.http.HttpUtil;
import com.bobby.nesty.view.activity.TestActivity;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    Button uploadtest;
    Button menu;
    ImageView move_iv;
    FrameLayout frame_parent;
    boolean ISCLICK;
    private int containerWidth;
    private int containerHeight;
    float lastX, lastY;

    Map<String, String> map;
    Map<String, String> map1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        animationTest();
//        testUpload();
        move_iv = (ImageView) findViewById(R.id.move_iv);
        move_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestActivity.class));
            }
        });

//        finish();
    }

    private void testUpload() {
        uploadtest = (Button) findViewById(R.id.uploadtest);
        map = new HashMap<>();
        map1 = new HashMap<>();
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
    }

    private void animationTest(){
        menu = (Button) findViewById(R.id.menu);
        ObjectAnimator anim = ObjectAnimator.ofFloat(menu, "translationY", 0, 600);
        anim.setInterpolator(new BounceInterpolator());
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(menu, "rotation", 0f, 720f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim).with(anim1);
        animSet.setDuration(3000);
        animSet.start();

        move_iv = (ImageView) findViewById(R.id.move_iv);
        move_iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    ISCLICK = true;
                }
                return false;
            }
        });
        frame_parent = (FrameLayout) findViewById(R.id.frame_parent);
        frame_parent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    ISCLICK = false;
                }
                if(!ISCLICK){
                    return false;
                }
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //  不要直接用getX和getY,这两个获取的数据已经是经过处理的,容易出现图片抖动的情况
                        float distanceX = lastX - event.getRawX();
                        float distanceY = lastY - event.getRawY();

                        float nextY = move_iv.getY() - distanceY;
                        float nextX = move_iv.getX() - distanceX;

                        // 不能移出屏幕
                        if (nextY < 0) {
                            nextY = 0;
                        } else if (nextY > containerHeight - move_iv.getHeight()) {
                            nextY = containerHeight - move_iv.getHeight();
                        }
                        if (nextX < 0)
                            nextX = 0;
                        else if (nextX > containerWidth - move_iv.getWidth())
                            nextX = containerWidth - move_iv.getWidth();

                        // 属性动画移动
                        ObjectAnimator y = ObjectAnimator.ofFloat(move_iv, "y", move_iv.getY(), nextY);
                        ObjectAnimator x = ObjectAnimator.ofFloat(move_iv, "x", move_iv.getX(), nextX);

                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(x, y);
                        animatorSet.setDuration(0);
                        animatorSet.start();

                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 这里来获取容器的宽和高
//        if (hasFocus) {
//            containerHeight = frame_parent.getHeight();
//            containerWidth = frame_parent.getWidth();
//        }
    }
}
