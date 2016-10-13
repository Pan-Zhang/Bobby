package com.bobby.nesty.present;

import android.util.Log;

import com.bobby.nesty.model.GirlsBean;
import com.bobby.nesty.model.Story;
import com.bobby.nesty.util.retrofit.MyRetrofit;
import com.bobby.nesty.util.retrofit.ServiceRetrofit;
import com.bobby.nesty.util.rxjava.EventType;
import com.bobby.nesty.util.rxjava.RxBus;
import com.bobby.nesty.view.viewcontrol.TestCallback;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by corous360 on 2016/10/10.
 */
public class TestPresent extends BasePresent<TestCallback> {

    public TestPresent(TestCallback baseCallback) {
        super(baseCallback);
    }

    public Subscription loadData(){
        return MyRetrofit.getRetrofit().create(ServiceRetrofit.class)
                .getBaidu("福利", 20, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GirlsBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Callback.stopRefrech(null);
                    }

                    @Override
                    public void onNext(GirlsBean girlsBean) {
                        Callback.stopRefrech(girlsBean);
                    }
                });
    }

    public Subscription interval(){
        return Observable.interval(10, 10, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        Callback.interval();
                    }
                });
    }

    public Subscription subscribeClick(){
        return RxBus.getInstance()
                .toObservable(EventType.OnClick, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(String event) {
                        Log.d("rxjava", "receive"+event);
                        Callback.refreshTextView(event);
                    }
                });
    }

    public Subscription testDatabase(){
        return Observable.create(new Observable.OnSubscribe<List<Story>>() {
            @Override
            public void call(Subscriber<? super List<Story>> subscriber) {
                Story story = new Story();
                story.setId("1");
                story.setDate("2016-9-9");
                story.setContent("first");
                story.setTitle("f");
                add(story);

                story = new Story();
                story.setId("1");
                story.setDate("2016-9-9");
                story.setContent("second");
                story.setTitle("s");
                add(story);

                story = new Story();
                story.setId("1");
                story.setDate("2016-9-9");
                story.setContent("second");
                story.setTitle("t");
                add(story);

                subscriber.onNext(select());
            }
        })
                .flatMap(new Func1<List<Story>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<Story> storyList) {

                        return Observable.from(storyList)
                                .flatMap(new Func1<Story, Observable<String>>() {
                                    @Override
                                    public Observable<String> call(Story story) {
                                        return Observable.just(story.getTitle());
                                    }
                                });
                    }
                })
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String story) {
                        if(story.equals("s") || story.equals("t")){
                            return true;
                        }
                        return false;
                    }
                })
                .take(3)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Callback.showToast("begin");
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("UIError",e.toString());
                    }

                    @Override
                    public void onNext(String story) {
                        Callback.showToast(story);
                    }

                });
    }
}
