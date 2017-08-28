package com.dhy.coffeesecret.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;

import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.bake.ReportActivity;
import com.dhy.coffeesecret.ui.common.LineSelectedActivity;
import com.dhy.coffeesecret.ui.common.SearchFragment;
import com.dhy.coffeesecret.ui.mine.adapter.HistoryLineAdapter;
import com.dhy.coffeesecret.url.UrlBake;
import com.dhy.coffeesecret.utils.FormatUtils;
import com.dhy.coffeesecret.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.jesse.nativelogger.NLogger;

import static com.dhy.coffeesecret.ui.common.DefaultViewHandler.LOADING_ERROR;
import static com.dhy.coffeesecret.ui.common.DefaultViewHandler.LOADING_SUCCESS;

/**
 * Created by CoDeleven on 17-8-26.
 */

public class HistoryReportEntranceActivity extends LineSelectedActivity{
    private static final String TAG = HistoryReportEntranceActivity.class.getSimpleName();
    private List<BakeReport> mBakeReports = new LinkedList<>();
    protected SearchFragment mSearchFragment;

    @Override
    protected RecyclerView.Adapter<?> initAdapter() {
        if(mBakeReports == null){
            fetchDataFromServer();
        }
        return new HistoryLineAdapter(this, mBakeReports, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeSearchFragment();
    }

    @Override
    public void onItemClick(Parcelable parcelable) {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra(UniExtraKey.EXTRA_BAKE_REPORT.getKey(), parcelable);
        startActivity(intent);
        removeSearchFragment();
    }

    protected void removeSearchFragment(){
        if(mSearchFragment != null){
            if(!mSearchFragment.isRemoved()){
                mSearchFragment.remove();
            }
            mSearchFragment = null;
        }
    }

    protected void fetchDataFromServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String token = application.getToken();
                    String temp = HttpUtils.getStringFromServer(UrlBake.getAll(token));
                    // NLogger.i(TAG, "刷新豆种信息：" + temp);
                    Type type = new TypeToken<Map<String, BakeReport>>() {
                    }.getType();
                    Map<String, BakeReport> bakeReports = new Gson().fromJson(temp, type);

                    // 清除上一次留下的数据
                    mBakeReports.clear();
                    // 重新添加数据
                    mBakeReports.addAll(bakeReports.values());
                    // 进行排序
                    Collections.sort(mBakeReports, new Comparator<BakeReport>() {
                        @Override
                        public int compare(BakeReport o1, BakeReport o2) {
                            return (int) (FormatUtils.date2IdWithTimestamp(o2.getDate()) - FormatUtils.date2IdWithTimestamp(o1.getDate()));
                        }
                    });
                    NLogger.i(TAG, "从服务器获取到" + bakeReports.size() + "个数据");
                    // 请求成功
                    mHandler.sendEmptyMessage(LOADING_SUCCESS);
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(LOADING_ERROR);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected SearchFragment newSearchFragment() {
        if(mSearchFragment == null){
            mSearchFragment = new HistoryReportSearchFragment();
            // List<BakeReport> bakeReportList = (List<BakeReport>)getIntent().getSerializableExtra(UniExtraKey.EXTRA_BAKE_REPORT_LIST.getKey());
            // 放着，如果没什么软用就不实现
            mSearchFragment.setOnResultClickListenr(this);
            if(mBakeReports == null){
                fetchDataFromServer();
            }
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(UniExtraKey.EXTRA_BAKE_REPORT_LIST.getKey(), new ArrayList<>(mBakeReports));
            mSearchFragment.setArguments(bundle);
        }
        return mSearchFragment;
    }

    @Override
    public void doDataHandle() {
        fetchDataFromServer();
    }
}
