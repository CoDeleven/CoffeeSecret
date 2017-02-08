package com.dhy.coffeesecret.ui.device;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.interfaces.ToolbarOperation;
import com.dhy.coffeesecret.utils.FragmentTool;

public class DeviceChildActivity extends AppCompatActivity implements ToolbarOperation {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_child);

        toolbar = (Toolbar) findViewById(R.id.toolbar_device_activtiy);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);

    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void modifyToolbarTitle(String title) {
        TextView textView = (TextView) toolbar.findViewById(R.id.id_toolbar_title);
        textView.setText(title);
    }

    @Override
    public float getToolbarHeight() {
        return 112;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FragmentTool.getFragmentToolInstance(this).removeKey(this);
    }


}
