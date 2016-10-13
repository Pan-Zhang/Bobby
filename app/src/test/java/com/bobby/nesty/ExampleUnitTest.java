package com.bobby.nesty;

import android.util.Log;

import com.bobby.nesty.model.GirlsBean;

import org.junit.Test;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testzipWith(){
        Observable.just("hello","good").zipWith(Observable.just(2), new Func2<String, Integer, String>() {
            @Override
            public String call(String s, Integer s2) {
                return s+" "+String.valueOf(s2);
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                String s = e.toString();
            }

            @Override
            public void onNext(String s) {
                String s1 = s;
            }
        });
    }

    @Test
    public void testMergeWith(){
        Observable.just("a","c").mergeWith(Observable.just("str")).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                String str = s;
            }
        });
    }

    @Test
    public void testConcatWith(){
        Observable.just("a").concatWith(Observable.just("kkk")).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                String str = s;
            }
        });
    }

    @Test
    public void testflatMap(){
        Observable.just("a").flatMap(new Func1<String, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(String s) {
                return Observable.just(s.hashCode());
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                int i = integer;
            }
        });
    }

    @Test
    public void testMap(){
        Observable.just("a").map(new Func1<String, Integer>() {
            @Override
            public Integer call(String s) {
                return s.hashCode();
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                int a = integer;
            }
        });
    }

    @Test
    public void testFilter(){
        Observable.just("a","b").filter(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                if(s.equals("a")){
                    return true;
                }
                return false;
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {

            }
        });
    }

    @Test
    public void subject(){
        Subject<Object, Object> subject = new SerializedSubject<>(PublishSubject.create());

        subject.subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                String str = o.toString();
            }
        });
        subject.onNext("dongxi");

        String s = "aaa";
        Observable.just(s).doOnNext(new Action1<String>() {
            @Override
            public void call(String s) {
                String d = s;
            }
        }).subscribe();
    }
}