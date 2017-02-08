package com.dhy.coffeesecret.ui.device;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.interfaces.ToolbarOperation;
import com.dhy.coffeesecret.ui.device.fragments.ReportFragment;
import com.dhy.coffeesecret.utils.FragmentTool;

public class DeviceChildActivity extends AppCompatActivity implements ToolbarOperation {

    public static final String[] FRAGMENT_TAG = {"bean_detail", "bean_line", "bean_search", "bean_report"};
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_child);

        toolbar = (Toolbar) findViewById(R.id.toolbar_device_activtiy);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            ReportFragment fragment = new ReportFragment();

            fragment.setToolbarOperation(this);
            FragmentTool fragmentTool = FragmentTool.getFragmentToolInstance(this);
            fragmentTool.add(R.id.id_device_child, fragment, false, FRAGMENT_TAG[3]);
        } else {
           /* FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction tx = manager.beginTransaction();
            LinesSelectedFragment lines = (LinesSelectedFragment)manager.findFragmentByTag(FRAGMENT_TAG[1]);
            SearchFragment search = (SearchFragment)manager.findFragmentByTag(FRAGMENT_TAG[2]);
            lines.setToolbarOperation(this);
            search.setToolbarOperation(this);
            FragmentTool.setCurFragment(search);
            //假设search是当前的fragment
            tx.show(lines);

            tx.show(search);
            tx.commit();*/
        }

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
