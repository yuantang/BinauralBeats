package com.coder.binauralbeats.activity;

/**
 * Created by TUS on 2018/3/14.
 */

public class Event {
    String key;
    Object value;
    Event(String key,Object value){
        this.key=key;
        this.value=value;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
