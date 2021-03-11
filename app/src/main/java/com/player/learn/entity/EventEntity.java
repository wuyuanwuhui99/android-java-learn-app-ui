package com.player.learn.entity;

public class EventEntity {
    private String what;
    private Object data;

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public EventEntity(String what, Object data) {
        this.what = what;
        this.data = data;
    }
}
