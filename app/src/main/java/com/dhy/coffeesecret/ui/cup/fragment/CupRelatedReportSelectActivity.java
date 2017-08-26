package com.dhy.coffeesecret.ui.cup.fragment;

import android.content.Intent;

import com.dhy.coffeesecret.model.UniExtraKey;
import com.dhy.coffeesecret.ui.mine.HistoryReportEntranceActivity;

import java.io.Serializable;

/**
 * Created by CoDeleven on 17-8-26.
 */

public class CupRelatedReportSelectActivity extends HistoryReportEntranceActivity{
    @Override
    public void onItemClick(Serializable serializable) {
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
