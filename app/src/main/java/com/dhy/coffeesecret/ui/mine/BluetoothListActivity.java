package com.dhy.coffeesecret.ui.mine;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.services.BluetoothService;
import com.dhy.coffeesecret.ui.device.adapter.BluetoothListAdapter;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothListActivity extends AppCompatActivity implements BluetoothListAdapter.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener, BluetoothService.DeviceChangedListener, BluetoothService.ViewControllerListener {
    private static BluetoothDevice curDevice = null;
    @Bind(R.id.id_connecting_bluetooth_list)
    RecyclerView devices;
    @Bind(R.id.id_bluetooth_switch)
    SwitchButton switchButton;
    @Bind(R.id.id_back)
    ImageView back;
    ImageView tick = null;
    private BluetoothService.BluetoothOperator mBluetoothOperator;
    private ProgressBar progressCircle = null;
    private static Map<String, BluetoothDevice> canConnectDeviceMap = new HashMap<>();
    private BluetoothDevice temp;
    private View curView;
    private BluetoothListAdapter adapter;

    @OnClick(R.id.id_back)
    public void back() {
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!mBluetoothOperator.isEnable() && isChecked) {
            mBluetoothOperator.enable();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!mBluetoothOperator.isEnable()) {
                        mBluetoothOperator.enable();
                        try {
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mBluetoothOperator.startScanDevice();
                }
            }).start();
        } else {
            canConnectDeviceMap.clear();
            adapter.notifyDataSetChanged();
            devices.invalidate();

            mBluetoothOperator.disableBluetooth();

            refreshListView();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        switchButton.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_list);
        ButterKnife.bind(this);
        adapter = BluetoothListAdapter.getAdapterInstance(this);
        devices.setLayoutManager(new LinearLayoutManager(this));
        devices.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        switchButton.setOnCheckedChangeListener(null);

        if (mBluetoothOperator == null) {
            // 获取helper实例
            mBluetoothOperator = BluetoothService.BLUETOOTH_OPERATOR;
        }
        mBluetoothOperator.setDeviceChangedListener(this);
        mBluetoothOperator.setViewControllerListener(this);
        if (mBluetoothOperator.isEnable()) {
            switchButton.setChecked(true);
            mBluetoothOperator.startScanDevice();
        }
    }

    @Override
    public void notifyDeviceConnectStatus(boolean isConnected) {
    }

    @Override
    public void notifyNewDevice(BluetoothDevice device, int rssi) {
        if (!canConnectDeviceMap.containsKey(device.getAddress())) {
            adapter.addDevice(device);
            canConnectDeviceMap.put(device.getAddress(), device);
        }
        refreshListView();
        adapter.setRssi(device.getAddress(), rssi);
    }

    @Override
    public void handleViewBeforeStartRead() {
        progressCircle.setVisibility(View.GONE);
        tick.setVisibility(View.VISIBLE);
        temp = null;
        finish();
    }

    public void refreshListView() {
        adapter.notifyDataSetChanged();
        devices.invalidate();
    }

    @Override
    public void onItemClick(BluetoothDevice device, View view) {
        if (progressCircle != null) {
            progressCircle.setVisibility(View.GONE);
        }
        progressCircle = (ProgressBar) view.findViewById(R.id.circle_progress);
        progressCircle.setVisibility(View.VISIBLE);
        tick = (ImageView) view.findViewById(R.id.id_bluetooth_list_right);

        if (!mBluetoothOperator.connect(device)) {
            curView.findViewById(R.id.circle_progress).setVisibility(View.GONE);
            return;
        }else{

        }
        adapter.setLastConnectedDevice(device);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothOperator.stopScanDevice();
    }

}
