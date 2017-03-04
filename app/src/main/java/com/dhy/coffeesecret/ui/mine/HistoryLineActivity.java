package com.dhy.coffeesecret.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.device.fragments.BakeDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HistoryLineActivity extends AppCompatActivity {
    public static final String REFER_LINE = "GET_HISTORY";
    private static int count = 0;
    private List<BakeReport> bakeReportList = new ArrayList<>();
    private ArrayList<Float> entries = new ArrayList<>();

    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_line);
        init();
    }

    public void init() {
        //TODO 初始化操作
        /**
         * 仅用于模拟烘焙过程的数据
         */
        InputStreamReader ireader = new InputStreamReader(getResources().openRawResource(R.raw.test));
        BufferedReader br = new BufferedReader(ireader);
        try {
            String str = "";
            while ((str = br.readLine()) != null) {
                for (String temp : str.trim().split(",")) {
                    entries.add(Float.parseFloat(temp));
                    count += 5;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent();
        intent.putExtra(REFER_LINE, entries);
        Log.e("codelevex", entries.size() + "");
        setResult(BakeDialog.GET_HISTORY, intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
