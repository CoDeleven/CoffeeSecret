package com.dhy.coffeesecret.ui.container;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.container.fragments.LinesSelectedActivity;

public class BeanInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bean_info);

        init();

    }

    private void init() {
    }
}
