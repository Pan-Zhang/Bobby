package com.bobby.nesty.util.rxjava;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by corous360 on 2016/10/13.
 */
public class RxBus {
    private static volatile RxBus defaultInstance;

    private final Subject<Object, Object> bus;

    public RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxBus getInstance() {
        RxBus rxBus = defaultInstance;
        if (defaultInstance == null) {
            synchronized (RxBus.class) {
                rxBus = defaultInstance;
                if (defaultInstance == null) {
                    rxBus = new RxBus();
                    defaultInstance = rxBus;
                }
            }
        }
        return rxBus;
    }

    public void post (Object o) {
        if(bus.hasObservers()){
            bus.onNext(o);
        }
    }

    public <T>Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }

    public void post(EventType code, Object o){
        if(bus.hasObservers()){
            bus.onNext(new Message(code,o));
        }
    }

    public <T> Observable<T> toObservable(final EventType code, final Class<T> eventType) {
        return bus.ofType(Message.class)
                .filter(new Func1<Message,Boolean>() {
                    @Override
                    public Boolean call(Message o) {
                        return o.getCode() == code && eventType.isInstance(o.getObject());
                    }
                }).map(new Func1<Message,Object>() {
                    @Override
                    public Object call(Message o) {
                        return o.getObject();
                    }
                }).cast(eventType);
    }
}