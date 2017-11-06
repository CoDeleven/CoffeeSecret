package com.dhy.coffeesecret;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by CoDeleven on 17-11-6.
 */

public class BaseActivity extends AppCompatActivity {
    // private static final List<? extends AppCompatActivity> ACTIVITIES = new LinkedList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ((MyApplication)getApplication()).addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MyApplication)getApplication()).removeActivity(this);
    }
}
