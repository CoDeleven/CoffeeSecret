package com.dhy.coffeesecret.ui.container;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.container.fragments.LinesSelectedActivity;

public class BeanInfoActivity extends AppCompatActivity {
    private ImageView mImageBack;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bean_info);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        mImageBack = (ImageView) findViewById(R.id.btn_back);
        mButton = (Button) findViewById(R.id.btn_curve);
        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_beaninfo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void init() {
        mImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BeanInfoActivity.this, LinesSelectedActivity.class);
                startActivity(intent);
            }
        });
    }
}
