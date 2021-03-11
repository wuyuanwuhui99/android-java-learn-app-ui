package com.player.learn.utils;


import android.os.Handler;
import android.os.Message;

public class MyHander extends Handler {
    public static MyHander getInstance(){
        return new MyHander();
    }

    @Override
    public void handleMessage(Message msg) {

    }
}
