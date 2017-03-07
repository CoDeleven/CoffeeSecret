package com.dhy.coffeesecret.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.device.fragments.BakeDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryLineActivity extends AppCompatActivity {
    public static final String REFER_LINE = "GET_HISTORY";
    private List<BakeReport> bakeReportList = new ArrayList<>();
    private ArrayList<Float> entries = new ArrayList<>();
    private BaseAdapter baseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return bakeReportList.size();
        }

        @Override
        public Object getItem(int position) {
            return bakeReportList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_line);
        init();
    }

    public void init() {
        //TODO 初始化操作
        Map<String, ? extends BakeReport> bakeReports = ((MyApplication) getApplication()).getBakeReports();

        bakeReportList.addAll(bakeReports.values());



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
