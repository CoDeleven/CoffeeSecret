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
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class BluetoothListActivity extends AppCompatActivity implements BluetoothHelper.DeviceChangeListener, BluetoothHelper.ViewHandlerListener, AdapterView.OnItemClickListener {

    @Bind(R.id.id_connecting_bluetooth_list)
    ListView canConnectDevice;
    @Bind(R.id.id_bluetooth_switch)
    SwitchButton switchButton;
    private BluetoothHelper mHelper;
    private BaseAdapter mAdapter = null;
    private ProgressBar progressCircle = null;
    private Map<String, BluetoothDevice> canConnectDeviceMap = new HashMap<>();

    public BluetoothListActivity() {
        // 获取helper实例
        mHelper = BluetoothHelper.getNewInstance();
        // 生成适配器
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return canConnectDeviceMap.size();
            }

            @Override
            public Object getItem(int position) {
                return new ArrayList<>(canConnectDeviceMap.values()).get(position);
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

    @OnCheckedChanged(R.id.id_bluetooth_switch)
    public void enableBluetooth(boolean isEnable) {
        if (isEnable) {
            mHelper.getmAdapter().enable();
            while(!mHelper.getEnableStatus()){
                mHelper.scanBluetoothDevice();
                Log.e("codelevex", "haha");
            }
        } else {
            canConnectDeviceMap.clear();
            mAdapter.notifyDataSetChanged();
            canConnectDevice.invalidate();

            mHelper.getmAdapter().disable();

            mHelper.close();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = BluetoothHelper.getNewInstance();
        }

        mHelper.setDeviceListener(this);
        mHelper.setViewHandlerListener(this);
        // mHelper.scanBluetoothDevice();
        if (mHelper.getEnableStatus()) {
            switchButton.setChecked(true);
            mHelper.scanBluetoothDevice();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_list);
        ButterKnife.bind(this);
        canConnectDevice.setDividerHeight(1);
        canConnectDevice.setAdapter(mAdapter);
        canConnectDevice.setOnItemClickListener(this);
    }


    @Override
    public void notifyNewDevice(BluetoothDevice device) {
        if (!canConnectDeviceMap.containsKey(device.getAddress())) {
            Log.e("codelevex", "我activity的实现:Device" + device.getName());
            canConnectDeviceMap.put(device.getAddress(), device);
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
        canConnectDevice.invalidate();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        progressCircle = (ProgressBar) view.findViewById(R.id.circle_progress);
        progressCircle.setVisibility(View.VISIBLE);

        BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
        if (!mHelper.connectDevice(device)) {
            Log.e("codelevex", "连接失败");
            return;
        }
    }
}
