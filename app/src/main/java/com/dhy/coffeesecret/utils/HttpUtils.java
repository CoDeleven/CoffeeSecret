package com.dhy.coffeesecret.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mxf on 2017/3/4.
 */


public class HttpUtils {

    private static final OkHttpClient mOkHttpClient;
    private static final Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    private static final long TIMEOUT =  4;
    private static final String CHARSET_NAME = "UTF-8";

    private static final MediaType TYPE = MediaType.parse("application/json; charset=utf-8");

    static {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public static Request getRequest(String url, Object obj) {
        String json = mGson.toJson(obj);
        RequestBody body = RequestBody.create(TYPE, json);
        Request request = new Request.Builder().url(url).post(body).build();
        return request;
    }

    public static Request getRequest(String url) {
        Request request = new Request.Builder().url(url).build();
        return request;
    }

    /**
     * 该不会开启异步线程。
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static Response execute(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }

    public static Response execute(String url, Object obj) throws IOException {
        Request request = getRequest(url, obj);
        return mOkHttpClient.newCall(request).execute();
    }


    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param responseCallback
     */
    public static void enqueue(Request request, Callback responseCallback) {
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 异步访问网络
     *
     * @param url
     * @param obj
     * @param responseCallback
     */
    public static void enqueue(String url, Object obj, Callback responseCallback) {
        Request request = getRequest(url, obj);
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     *
     * @param request
     */
    public static void enqueue(Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    public static String getStringFromServer(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = execute(request);
        if (response.isSuccessful()) {
            String responseUrl = response.body().string();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }


    public static String attachHttpGetParam(String url, String name, String value) {
        return url + "?" + name + "=" + value;
    }
}
