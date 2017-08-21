package com.dhy.coffeesecret.utils;

import android.util.Log;

import com.dhy.coffeesecret.pojo.BakeReport;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by CoDeleven on 17-2-9.
 */

public class ObjectJsonConvert {

    public static String bakereport2Json(BakeReport imm) {
        String jsonStr = new Gson().toJson(imm);
        return jsonStr;
    }

    private static void sendJsonData(final String jsonStr) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://10.0.2.2:8080/CoffeeSecret/bake/add";
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);
                Log.e("codelevex", "wwwwww");
                Request request = new Request.Builder().url(url).post(requestBody).build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.e("codelevex", "成功获取");
                    } else {
                        Log.e("codelevex", "失败啦！！！:" + response.code());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
