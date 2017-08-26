package com.dhy.coffeesecret.ui.device.fragments;

import android.content.Intent;

import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.ui.mine.HistoryReportEntranceActivity;

import java.io.Serializable;

/**
 * Created by CoDeleven on 17-8-26.
 */

public class ReferReportSelectedActivity extends HistoryReportEntranceActivity {

    @Override
    public void onItemClick(Serializable serializable) {
        // super.onItemClick(serializable);
        /*Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra(UniExtraKey.EXTRA_BAKE_REPORT.getKey(), serializable);
        startActivity(intent);*/
        // set Result
        Intent intent = new Intent();
        intent.putExtra(UniExtraKey.EXTRA_BAKE_REPORT.getKey(), serializable);
        setResult(RESULT_OK, intent);
        if(mSearchFragment != null){
            mSearchFragment.remove();
            mSearchFragment = null;
        }
        finish();
    }
}
