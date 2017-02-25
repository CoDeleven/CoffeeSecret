package com.dhy.coffeesecret.utils;

import android.util.Log;

import com.dhy.coffeesecret.pojo.BakeReportImm;
import com.dhy.coffeesecret.views.BaseChart4Coffee;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by CoDeleven on 17-2-9.
 */

public class ObjectJsonConvert {
    public static String bakereport2Json(BakeReportImm imm) {
/*        BakeReportImm imm = new BakeReportImm();
        imm.setBaker("gg");
        imm.setCookedBeanWeight(320);
        imm.setDevelopRate(0.3f);
        imm.setDevice("lala");
        imm.setDevelopTime(300);
        imm.setStartTemp(160);
        imm.setEndTemp(230);
        imm.setEnvTemp(26);
        Map<Integer, Float> maps = new HashMap<Integer, Float>();
        maps.put(1, 360F);
        maps.put(2, 720F);
        imm.setRawBeanWeight(maps);
        LineData line = new LineData();
        for (int i = 0; i < 5; ++i) {
            line.addDataSet(new LineDataSet(Arrays.asList(new Entry(1, 1)), BaseChart4Coffee.Integer2StringLable(i)));
        }
        imm.lineData2Pojo(line);*/

        String jsonStr = new Gson().toJson(imm);
        return jsonStr;
    }

    private static void sendJsonData(final String jsonStr){
        final OkHttpClient okHttpClient = new OkHttpClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://10.0.2.2:8080/CoffeeSecret/bake/add";
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);
                Log.e("codelevex", "wwwwww");
                Request request = new Request.Builder().url(url).post(requestBody).build();
                try{
                    Response response = okHttpClient.newCall(request).execute();
                    if(response.isSuccessful()){
                        Log.e("codelevex", "成功获取");
                    }else{
                        Log.e("codelevex", "失败啦！！！:" + response.code());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
