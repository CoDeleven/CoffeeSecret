package com.dhy.coffeesecret.ui.container;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.container.fragments.SearchFragment;
import com.dhy.coffeesecret.ui.device.LineSelectedActivity;
import com.dhy.coffeesecret.ui.device.ReportActivity;
import com.dhy.coffeesecret.ui.mine.HistoryReportSearchFragment;
import com.dhy.coffeesecret.ui.mine.adapter.HistoryLineAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.dhy.coffeesecret.ui.DefaultViewHandler.LOADING_SUCCESS;

/**
 * Created by CoDeleven on 17-8-26.
 */

public class SelectedRelatedBeanActivity extends LineSelectedActivity {
    private List<BakeReport> bakeReports;
    private SearchFragment mSearchFragment;

    @Override
    protected RecyclerView.Adapter<?> initAdapter() {
        if(bakeReports == null){
            initBatchData(getIntent().getExtras());
        }
        return new HistoryLineAdapter(this, bakeReports, this);
    }

    private void initBatchData(Bundle bundle) {
        if (bundle == null){
            throw new RuntimeException("没有数据...");
        }
        if(bakeReports == null){
            bakeReports = (List<BakeReport>) bundle.getSerializable(UniExtraKey.EXTRA_BAKE_REPORT_LIST.getKey());
        }

    }

    @Override
    public void onItemClick(Serializable serializable) {
        Intent intent = new Intent(SelectedRelatedBeanActivity.this, ReportActivity.class);
        intent.putExtra(UniExtraKey.EXTRA_BAKE_REPORT.getKey(), serializable);
        startActivity(intent);
        if(mSearchFragment != null){
            mSearchFragment.remove();
            mSearchFragment = null;
        }
    }


    @Override
    protected SearchFragment newSearchFragment() {
        mSearchFragment = new HistoryReportSearchFragment();
        Bundle bundle = new Bundle();
        mSearchFragment.setArguments(bundle);
        mSearchFragment.setOnResultClickListenr(this);
        bundle.putSerializable(UniExtraKey.EXTRA_BAKE_REPORT_LIST.getKey(), new ArrayList<>(bakeReports));
        return mSearchFragment;
    }

    protected void fetchDataFromServer(){

    }

    @Override
    public void doDataHandle() {
        fetchDataFromServer();
        mHandler.sendEmptyMessage(LOADING_SUCCESS);
    }
}
