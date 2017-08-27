package com.dhy.coffeesecret.ui.cup.fragment;

import android.content.Intent;
import android.os.Parcelable;

import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.ui.mine.HistoryReportEntranceActivity;

/**
 * Created by CoDeleven on 17-8-26.
 */

public class CupRelatedReportSelectActivity extends HistoryReportEntranceActivity{
    @Override
    public void onItemClick(Parcelable parcelable) {
        Intent intent = new Intent();
        intent.putExtra(UniExtraKey.EXTRA_BAKE_REPORT.getKey(), parcelable);
        setResult(RESULT_OK, intent);
        removeSearchFragment();
        finish();
    }
}
