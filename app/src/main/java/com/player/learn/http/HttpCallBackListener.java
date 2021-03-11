package com.player.learn.http;

import org.json.JSONException;
import org.json.JSONObject;

public interface HttpCallBackListener {
    void onFinish(JSONObject response) throws JSONException;
    void onError(Exception e);
}
