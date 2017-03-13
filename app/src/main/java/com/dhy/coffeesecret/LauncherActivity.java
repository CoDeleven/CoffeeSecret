package com.dhy.coffeesecret;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.dhy.coffeesecret.pojo.Global;
import com.dhy.coffeesecret.utils.SPPrivateUtils;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent intent;
                if (SPPrivateUtils.getBoolean(LauncherActivity.this, Global.IS_FIRST_TIME, true)) {
                    intent = new Intent(LauncherActivity.this, GuidanceActivity.class);
                } else {
                    intent = new Intent(LauncherActivity.this, MainActivity.class);
                }
                startActivity(intent);
                overridePendingTransition(R.anim.in_fade, R.anim.out_fade);
                finish();
            }
        }).start();
    }

    @Override
    public void onBackPressed() {

    }
}
