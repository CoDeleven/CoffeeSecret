package com.dhy.coffeesecret;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.dhy.coffeesecret.pojo.Global;
import com.dhy.coffeesecret.utils.SPPrivateUtils;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        Intent intent;
        if (SPPrivateUtils.getBoolean(LauncherActivity.this, Global.IS_FIRST_TIME, true)) {
            intent = new Intent(LauncherActivity.this, GuidanceActivity.class);
        } else if("".equals(SPPrivateUtils.getString(LauncherActivity.this, "address", ""))){
            intent = new Intent(LauncherActivity.this, FirstConnectedActivity.class);
        }else{
            intent = new Intent(LauncherActivity.this, MainActivity.class);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.in_fade, R.anim.out_fade);
        finish();

}

    @Override
    public void onBackPressed() {

    }
}
