package com.dhy.coffeesecret.ui.cup.listener;

import android.app.Activity;
import android.view.View;

/**
 * 点击finish 当前的activity
 * Created by mxf on 2017/2/17.
 */
public class FinishListener implements View.OnClickListener {
    private Activity activity;

    public FinishListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        activity.finish();
    }
}
