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
    private Toolbar mToolbar;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bean_info);
        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        btn = (Button) findViewById(R.id.btn_curve);

        setSupportActionBar(mToolbar);

        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_beaninfo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void init() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BeanInfoActivity.this, LinesSelectedActivity.class);
                startActivity(intent);
            }
        });
    }
}
