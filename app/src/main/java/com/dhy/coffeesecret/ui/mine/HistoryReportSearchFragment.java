package com.dhy.coffeesecret.ui.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.ui.mine.adapter.LineListAdapter;
import com.dhy.coffeesecret.ui.common.SearchFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CoDeleven on 17-8-26.
 */

public class HistoryReportSearchFragment extends SearchFragment {
    private List<BakeReport> bakeReports;
    private List<BakeReport> bakeReportTemp;
    private LineListAdapter mLineListAdapter;

    @Override
    protected void initData(Bundle bundle) {
        if(bundle == null){
            throw new RuntimeException("没有数据...");
        }
        bakeReports = new ArrayList<>();
        bakeReportTemp = bundle.getParcelableArrayList(UniExtraKey.EXTRA_BAKE_REPORT_LIST.getKey());

        mLineListAdapter = new LineListAdapter(getContext(), bakeReports, getResultClickListenr());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSearchList(mLineListAdapter);
    }

    @Override
    protected void handleDataBySearchKey(final String searchKey) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (bakeReportTemp != null) {
                    bakeReports.clear();

                    for (BakeReport bakeReport : bakeReportTemp) {
                        if (bakeReport.getDate().toLowerCase().contains(searchKey)) {
                            bakeReports.add(bakeReport);
                        }
                    }

                    mLineListAdapter.notifyDataSetChanged();
                }
            }
        });
    }


}
