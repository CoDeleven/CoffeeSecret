package com.dhy.coffeesecret.ui.mine;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.utils.BluetoothHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BluetoothListActivity extends AppCompatActivity implements BluetoothHelper.DeviceChangeListener, BluetoothHelper.ViewHandlerListener {

    private BluetoothHelper mHelper;
    private ListView listView;
    private BaseAdapter mAdapter = null;
    private ProgressBar progressCircle = null;
    private Map<String, BluetoothDevice> deviceMap = new HashMap<>();

    public BluetoothListActivity() {
        // 获取helper实例
        mHelper = BluetoothHelper.getNewInstance();
        // 生成适配器
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return deviceMap.size();
            }

            @Override
            public Object getItem(int position) {
                return new ArrayList<>(deviceMap.values()).get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = getLayoutInflater().inflate(R.layout.bluetooth_item_view, parent, false);
                TextView textView = (TextView) view.findViewById(R.id.text_name);
                textView.setText(((BluetoothDevice) getItem(position)).getName());

                return view;
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = BluetoothHelper.getNewInstance();
        }
        mHelper.setDeviceListener(this);
        mHelper.setViewHandlerListener(this);
        mHelper.scanBluetoothDevice();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_list);
        listView = (ListView) findViewById(R.id.id_bluetooth_list);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressCircle = (ProgressBar) view.findViewById(R.id.circle_progress);
                progressCircle.setVisibility(View.VISIBLE);

                BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
                // FragmentTransaction tx = getFragmentManager().beginTransaction();
                if (!mHelper.connectDevice(device)) {
                    Log.e("codelevex", "连接失败");
                    return;
                }
            }
        });
    }


    @Override
    public void notifyNewDevice(BluetoothDevice device) {
        if (!deviceMap.containsKey(device.getAddress())) {
            Log.e("codelevex", "我activity的实现:Device" + device.getName());
            deviceMap.put(device.getAddress(), device);
            refreshListView();
        }
    }

    @Override
    public void handleViewBeforeStartRead() {
        progressCircle.setVisibility(View.GONE);
        finish();
    }

    public void refreshListView() {
        mAdapter.notifyDataSetChanged();
        listView.invalidate();
    }
}
