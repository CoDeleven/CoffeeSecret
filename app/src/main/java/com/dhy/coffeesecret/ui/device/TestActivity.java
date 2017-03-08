package com.dhy.coffeesecret.ui.device;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.utils.CacheUtils;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.dhy.coffeesecret.utils.URLs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Response;

public class TestActivity extends AppCompatActivity {
    private List<BakeReport> bakeReports;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        readDataFromCache();

    }

    private void readDataFromCache(){
        BakeReport bakeReport = ((MyApplication)getApplication()).getObjectById("0", BakeReport.class);
        Log.e("codelevex", "gg");
    }

    private void readDataFromServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = HttpUtils.execute(URLs.GET_ALL_BAKE_REPORT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    String temp = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<BakeReport>>(){}.getType();
                    bakeReports = gson.fromJson(temp, listType);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
