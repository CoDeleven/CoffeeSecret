package com.dhy.coffeesecret.ui.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dhy.coffeesecret.BaseActivity;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConnectedActivity extends BaseActivity {
    @Bind(R.id.id_first_confirm)
    Button confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.id_first_confirm)
    public void onConfirmClick(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
