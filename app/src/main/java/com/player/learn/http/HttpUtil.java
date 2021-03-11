package com.player.learn.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class HttpUtil {

    private static final int TIMEOUT_IN_MILLIONS = 5000;

    /**
     * 异步的Get请求
     * @param urlStr
     * @param callBack
     */
    public static void doGetAsyn(final String urlStr, final String token, final HttpCallBackListener callBack) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject jsonObject = doGet(urlStr,token);
                    if (callBack != null) {
                        callBack.onFinish(jsonObject);
                    }
                } catch (Exception e) {
                    callBack.onError(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 异步的Get请求
     * @param urlStr
     * @param handler
     */
    public static void doGetAsyn(final String urlStr, final String token, final Handler handler) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject jsonObject = doGet(urlStr,token);
                    if (handler != null) {
                        Message message = handler.obtainMessage(1,jsonObject);
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Message message = handler.obtainMessage(2,e);
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 异步的Post请求
     * @param urlStr
     * @param params
     * @param callBack
     * @throws Exception
     */
    public static void doPostAsyn(final String urlStr, final String params,final String token,
                                  final HttpCallBackListener callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = doPost(urlStr, params,token);
                    if (callBack != null) {
                        callBack.onFinish(jsonObject);
                    }
                } catch (Exception e) {
                    callBack.onError(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * Get请求，获得返回数据
     *
     * @param urlStr
     * @return
     * @throws Exception
     */
    public static JSONObject doGet(String urlStr,String token) {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("contentType", "UTF-8");
            conn.setRequestProperty("Content-type", "application/json;charset=UTF-8");
            if(token != null && !"".equals(token))conn.setRequestProperty("Authorization",token);
            conn.setRequestProperty("Accept-Language", Locale.getDefault().toString());
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();

                BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = in.readLine()) != null){
                    buffer.append(line);
                }
                return new JSONObject(buffer.toString());
            } else {
                throw new RuntimeException(" responseCode is not 200 ... ");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.disconnect();
        }
        return null;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param param  请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     * @throws Exception
     */
    public static JSONObject doPost(String url, String param,String token) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("charset", "utf-8");
            conn.setUseCaches(false);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
            if(token != null && !"".equals(token))conn.setRequestProperty("Authorization",token);
            if (param != null && !param.trim().equals("")) {
                // 获取URLConnection对象对应的输出流
                out = new PrintWriter(conn.getOutputStream());
                // 发送请求参数
                out.print(param);
                // flush输出流的缓冲
                out.flush();
            }
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getBitmap(String path) throws IOException {

        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200){
            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        return null;
    }
}
