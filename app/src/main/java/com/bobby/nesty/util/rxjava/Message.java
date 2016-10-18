package com.bobby.nesty.util.rxjava;

/**
 * Created by corous360 on 2016/10/13.
 */
public class Message {
    private EventType code;
    private Object object;

    public Message(EventType code, Object o) {
        this.code = code;
        this.object = o;
    }

    public EventType getCode(){
        return code;
    }

    public Object getObject(){
        return object;
    }
}
