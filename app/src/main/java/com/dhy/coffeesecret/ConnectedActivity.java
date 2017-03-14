package com.dhy.coffeesecret;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConnectedActivity extends AppCompatActivity {
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
