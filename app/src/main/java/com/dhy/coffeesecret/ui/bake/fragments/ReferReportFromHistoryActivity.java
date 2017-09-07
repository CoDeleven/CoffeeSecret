package com.dhy.coffeesecret.ui.bake.fragments;

import android.content.Intent;
import android.os.Parcelable;

import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.ui.mine.HistoryReportEntranceActivity;

/**
 * Created by CoDeleven on 17-8-26.
 */

public class ReferReportFromHistoryActivity extends HistoryReportEntranceActivity {

    @Override
    public void onItemClick(Parcelable parcelable) {
        // super.onItemClick(serializable);
        /*Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra(UniExtraKey.EXTRA_BAKE_REPORT.getKey(), serializable);
        startActivity(intent);*/
        // set Result
        Intent intent = new Intent();
        intent.putExtra(UniExtraKey.EXTRA_BAKE_REPORT.getKey(), parcelable);
        setResult(RESULT_OK, intent);
        removeSearchFragment();
        finish();
    }

}
