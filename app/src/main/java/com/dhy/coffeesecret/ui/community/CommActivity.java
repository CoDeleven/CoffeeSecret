package com.dhy.coffeesecret.ui.community;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.dhy.coffeesecret.R;

import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

public class CommActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm);
        getWindow().addFlags(FLAG_TRANSLUCENT_STATUS);
    }

    public void back(View view) {
        finish();
    }
}
