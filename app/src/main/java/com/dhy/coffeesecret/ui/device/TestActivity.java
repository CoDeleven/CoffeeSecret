package com.dhy.coffeesecret.ui.device;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.utils.BluetoothHelper;

public class TestActivity extends AppCompatActivity {
    private EditText text1;
    private EditText text2;
    private Button send;
    private BluetoothHelper mHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        text1 = (EditText)findViewById(R.id.id_test_1);
        text2 = (EditText)findViewById(R.id.id_test_2);
        send = (Button) findViewById(R.id.id_test_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.secondChannel = "4348414e3b" + text1.getText().toString() + text2.getText().toString() + "30300a";
                mHelper.read();
            }
        });
        if(mHelper == null){
            mHelper = BluetoothHelper.getNewInstance(this);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper.scanBluetoothDevice();
    }
}
