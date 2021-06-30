package com.seeingvoice.www.svhearing.okhttpUtil;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Date:2019/6/17
 * Time:16:12
 * auther:zyy
 */
public class OkHttpManager {
    public static OkHttpManager instence;
    private OkHttpClient mOkHttpClient;
    private Handler okHandler;
    String finalStr;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public OkHttpManager() {
        //声明Handler对指定为主线程looper,确保执行线程在主线程中。
        okHandler = new Handler(Looper.getMainLooper());
        //指定超时时间等参数
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS);
        mOkHttpClient = builder.build();
    }

    /**
     * 单例模式获取当前的实例对象，确保唯一性
     */
    public static OkHttpManager getInstence() {
        if (instence == null) {
            synchronized (OkHttpManager.class) {
                if (instence == null) {
                    instence = new OkHttpManager();
                }
            }
        }
        return instence;
    }

    /**
     * 发送get请求
     * @param url
     * @param resultCallback
     */
    public void getNet(String url, ResultCallback resultCallback) {
        Request request = new Request.Builder()
                .url(url)  //接口地址
                .method("GET", null)  //默认的也是GET,可以不设置
                .build();
        dealNet(request,resultCallback);
    }


    /**
     * 发送post请求
     * @param url
     * @param resultCallback
     * @param param
     */
    public void postNet(String url, ResultCallback resultCallback, Param... param) {
        if (param == null) {
            param = new Param[0];
        }
        FormBody.Builder formbody = new FormBody.Builder();
        for (Param p : param) {
            formbody.add(p.key, p.value);
        }
        RequestBody requestBody = formbody.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)  //传入构建好的参数
                .build();

        dealNet(request,resultCallback);
    }

    public void postJsonNet(String url, ResultCallback resultCallback, String json){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        FormBody.Builder formbody = new FormBody.Builder();

        //json为String类型的json数据
        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonObject));

//        RequestBody requestBody = formbody.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)  //传入构建好的参数
                .build();

        dealNet(request,resultCallback);
    }

    private void dealNet(final Request request, final ResultCallback resultCallback){
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
//                Log.e("WXEntryActivity",call.toString());
                Log.i("OkHttpManager", "onFailure" + call.toString());
                okHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //请求失败的时候执行的方法
                        resultCallback.onFailed(request, e);
                    }
                });
            }

            @Override
            public synchronized void onResponse(Call call, Response response) throws IOException {
                finalStr = "";
                try {
                    finalStr = response.body().string();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("OkHttpManager", "onResponse" + finalStr);
                okHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //请求成功的时候执行的方法
                        resultCallback.onSuccess(finalStr);
                    }
                });
            }
        });
    }

    /**
     * 自定义监听网络请求成功失败回调
     */
    public static abstract class ResultCallback {
        public abstract void onFailed(Request request, IOException e);

        public abstract void onSuccess(String response);
    }

    /**
     * 参数封装类
     */
    public static class Param {
        String key;
        String value;

        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
