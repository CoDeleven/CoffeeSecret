package com.dhy.coffeesecret;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dhy.coffeesecret.services.BluetoothService;
import com.dhy.coffeesecret.ui.device.adapter.BluetoothListAdapter;
import com.dhy.coffeesecret.utils.SPPrivateUtils;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.views.DividerDecoration;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FirstConnectedActivity extends AppCompatActivity implements BluetoothListAdapter.OnItemClickListener,
        BluetoothService.DeviceChangedListener, BluetoothService.ViewControllerListener {
    public static final int DEVICE_CONNECTING = 0, DEVICE_CONNECT_FAILED = 1, DEVICE_CONNECTED = 2;
    @Bind(R.id.id_first_bluetooth_list)
    RecyclerView recyclerView;
    @Bind(R.id.id_first_connect_status)
    TextView connectStatus;

    BluetoothService.BluetoothOperator mBluetoothOperator;
    ImageView tick = null;
    private BluetoothListAdapter mAdapter;
    private Map<String, BluetoothDevice> devices = new HashMap<>();
    private ProgressBar progressCircle = null;
    // 连接状态视图的处理器
    private Handler progressViewHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (tick != null && progressCircle != null) {
                switch (msg.what) {
                    case DEVICE_CONNECTING:
                        progressCircle.setVisibility(View.VISIBLE);
                        tick.setVisibility(View.GONE);
                        break;
                    case DEVICE_CONNECT_FAILED:
                        progressCircle.setVisibility(View.INVISIBLE);
                        tick.setVisibility(View.GONE);
                        break;
                    case DEVICE_CONNECTED:
                        progressCircle.setVisibility(View.GONE);
                        tick.setVisibility(View.VISIBLE);
                }
            }
            return false;
        }
    });
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothOperator = (BluetoothService.BluetoothOperator) service;
            mBluetoothOperator.setDeviceChangedListener(FirstConnectedActivity.this);
            mBluetoothOperator.setViewControllerListener(FirstConnectedActivity.this);
            mBluetoothOperator.startScanDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_connected);

        ButterKnife.bind(this);
        mAdapter = new BluetoothListAdapter(this);
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerDecoration(this));
        Intent intent = new Intent(getApplicationContext(), BluetoothService.class);
        getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onItemClick(BluetoothDevice device, View view) {
        BluetoothDevice hasConnectedDevice = mBluetoothOperator.getBluetoothDevice();
        if (hasConnectedDevice != null && device != null && device.getAddress().equals(hasConnectedDevice.getAddress())) {
            return;
        }
        // 如果之前存在一个设备在连接,设置上一个视图隐藏
        if (progressCircle != null && progressCircle.isShown()) {
            progressCircle.setVisibility(View.GONE);
        }

        // 更新progressCircle和tick两个view的引用
        progressCircle = (ProgressBar) view.findViewById(R.id.circle_progress);
        // progressCircle.setVisibility(View.VISIBLE);
        tick = (ImageView) view.findViewById(R.id.id_bluetooth_list_right);
        // 当前状态是处于正在连接的状态
        progressViewHandler.sendEmptyMessage(DEVICE_CONNECTING);
        // 开始正式请求连接,因为连接是异步回调，不是及时消息,如果此时false，一定不能连接成功
        if (!mBluetoothOperator.connect(device)) {
            progressViewHandler.sendEmptyMessage(DEVICE_CONNECT_FAILED);
            return;
        }
    }

    @Override
    public void notifyDeviceConnectStatus(boolean isConnected, BluetoothDevice device) {
        if (isConnected) {
            // 设置已连接设备
            mAdapter.lastConnectedAddress = device.getAddress();
            // 设置已经连接状态
            progressViewHandler.sendEmptyMessage(DEVICE_CONNECTED);
            // 保存连接设备地址到配置文件,方便启动时读取并直接连接
            // SettingTool.getConfig(this);
            // SettingTool.saveAddress(device.getAddress());
            SPPrivateUtils.put(this, "address", device.getAddress());

            // 连接成功，结束activity
            Intent intent = new Intent(this, ConnectedActivity.class);
            startActivity(intent);

        } else {
            // 确保连接设备为null
            mAdapter.lastConnectedAddress = null;
            // 设置连接失败状态
            progressViewHandler.sendEmptyMessage(DEVICE_CONNECT_FAILED);
        }

    }

    @Override
    public void notifyNewDevice(BluetoothDevice device, int rssi) {
        Log.e("codelevex", "发现新设备：" + device.getAddress());
        // 如果可连接设备里包含里新设备，则只更新rssi,而不添加至adapter
        if (!devices.containsKey(device.getAddress())) {
            mAdapter.addDevice(device);
            devices.put(device.getAddress(), device);
        }
        // 每次发现新设备刷新列表
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshListView();
            }
        });

        // 通过设备地址，更新对应设备的rssi
        mAdapter.setRssi(device.getAddress(), rssi);
    }

    @Override
    public void handleViewBeforeStartRead() {
    }

    /**
     * 刷新列表
     */
    public void refreshListView() {
        mAdapter.notifyDataSetChanged();
        recyclerView.invalidate();
    }

}
