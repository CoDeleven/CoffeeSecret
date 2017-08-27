package com.dhy.coffeesecret.ui.counters;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;

import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.common.SearchFragment;
import com.dhy.coffeesecret.ui.common.LineSelectedActivity;
import com.dhy.coffeesecret.ui.bake.ReportActivity;
import com.dhy.coffeesecret.ui.mine.HistoryReportSearchFragment;
import com.dhy.coffeesecret.ui.mine.adapter.HistoryLineAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.dhy.coffeesecret.ui.common.DefaultViewHandler.LOADING_SUCCESS;

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
            bakeReports = bundle.getParcelableArrayList(UniExtraKey.EXTRA_BAKE_REPORT_LIST.getKey());
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeSearchFragment();
    }

    @Override
    public void onItemClick(Parcelable parcelable) {
        Intent intent = new Intent(SelectedRelatedBeanActivity.this, ReportActivity.class);
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

    @Override
    protected SearchFragment newSearchFragment() {
        mSearchFragment = new HistoryReportSearchFragment();
        Bundle bundle = new Bundle();
        mSearchFragment.setArguments(bundle);
        mSearchFragment.setOnResultClickListenr(this);
        bundle.putParcelableArrayList(UniExtraKey.EXTRA_BAKE_REPORT_LIST.getKey(), new ArrayList<>(bakeReports));
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
