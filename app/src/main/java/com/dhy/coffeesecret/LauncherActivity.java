package com.dhy.coffeesecret;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.dhy.coffeesecret.pojo.Global;
import com.dhy.coffeesecret.pojo.SQLiteHelper;
import com.dhy.coffeesecret.utils.SPPrivateUtils;

import java.util.Timer;
import java.util.TimerTask;

public class LauncherActivity extends AppCompatActivity {

    private Handler waitMinutes2StartMainActivity = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Intent intent;
            intent = new Intent(LauncherActivity.this, MainActivity.class);
/*           if (SPPrivateUtils.getBoolean(LauncherActivity.this, Global.IS_FIRST_TIME, true)) {
               intent = new Intent(LauncherActivity.this, GuidanceActivity.class);
           } else if ("".equals(SPPrivateUtils.getString(LauncherActivity.this, "address", ""))) {
               intent = new Intent(LauncherActivity.this, FirstConnectedActivity.class);
           } else {
               intent = new Intent(LauncherActivity.this, MainActivity.class);
           }*/

            startActivity(intent);
            overridePendingTransition(R.anim.in_fade, R.anim.out_fade);
            finish();
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                waitMinutes2StartMainActivity.sendEmptyMessage(0);
            }
        }, 1500);

        //TODO 初始化数据库
        SQLiteOpenHelper helper = new SQLiteHelper(this);
        ((MyApplication) getApplicationContext()).setCountry2Continent(helper.getReadableDatabase());
        ((MyApplication)getApplicationContext()).initUnit();
        Log.d("LauncherActivity", MyApplication.tempratureUnit + "," + MyApplication.weightUnit);

    }

    @Override
    public void onBackPressed() {

    }
}
