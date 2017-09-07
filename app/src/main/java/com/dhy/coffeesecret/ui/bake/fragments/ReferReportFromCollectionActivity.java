package com.dhy.coffeesecret.ui.bake.fragments;

import android.widget.TextView;

import com.dhy.coffeesecret.ui.common.DefaultViewHandler;
import com.dhy.coffeesecret.ui.mine.HistoryReportEntranceActivity;

/**
 * Created by CoDeleven on 17-9-7.
 */

public class ReferReportFromCollectionActivity extends HistoryReportEntranceActivity {
    @Override
    protected void initTitle(TextView title) {
        title.setText("收藏曲线");
    }

    @Override
    protected void fetchDataFromServer() {
        // 从服务端获取收藏曲线
        mHandler.sendEmptyMessage(DefaultViewHandler.NO_LOADING);
    }
}
