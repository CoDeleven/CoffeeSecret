package com.dhy.coffeesecret.ui.mine;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.dhy.coffeesecret.utils.SettingTool;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dhy.coffeesecret.services.BluetoothService.SCAN_TIME;

public class BluetoothListActivity extends AppCompatActivity implements BluetoothListAdapter.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener, BluetoothService.DeviceChangedListener, BluetoothService.ViewControllerListener, SwipeRefreshLayout.OnRefreshListener {

    public static final int DEVICE_CONNECTING = 0, DEVICE_CONNECT_FAILED = 1, DEVICE_CONNECTED = 2, REFRESH = -1;

    @Bind(R.id.srl)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.id_connecting_bluetooth_list)
    RecyclerView devices;
    @Bind(R.id.id_bluetooth_switch)
    SwitchButton switchButton;
    @Bind(R.id.id_back)
    ImageView back;
    ImageView tick = null;
    private Map<String, BluetoothDevice> canConnectDeviceMap = new HashMap<>();
    private BluetoothService.BluetoothOperator mBluetoothOperator;
    private ProgressBar progressCircle = null;
    private BluetoothListAdapter adapter;
    private BluetoothDevice hasConnected;
    // 连接状态视图的处理器
    private Handler progressViewHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == REFRESH) {
                refreshLayout.setRefreshing(false);
            }
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
                        break;
                }
            }
            return false;
        }
    });

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
            mBluetoothOperator.stopScanDevice();
            // 关闭蓝牙后清空adapter内的设备
            adapter.clearDevices();
            // 清空可连接设备
            canConnectDeviceMap.clear();

            // 刷新列表
            adapter.notifyDataSetChanged();
            devices.invalidate();
            // 关闭蓝牙初始化状态
            adapter.lastConnectedAddress = null;
            // 请求关闭蓝牙设备
            mBluetoothOperator.disableBluetooth();
            // 关闭蓝牙设备后清空已连接的设备状态
            hasConnected = null;
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
        // 设置列表
        adapter = new BluetoothListAdapter(this);
        devices.setLayoutManager(new LinearLayoutManager(this));
        devices.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        refreshLayout.setOnRefreshListener(this);

        // 获取蓝牙操作对象
        if (mBluetoothOperator == null) {
            // 获取helper实例
            mBluetoothOperator = BluetoothService.BLUETOOTH_OPERATOR;
        }

        // 设置发现新设备进行回调的对象
        mBluetoothOperator.setDeviceChangedListener(this);
        // 设置视图更改器，用于当蓝牙连接成功时，设置勾给相应的条目
        mBluetoothOperator.setViewControllerListener(this);
        mBluetoothOperator.setScanListener(new BluetoothService.ScanListener(){
            @Override
            protected void onScanStop() {
                if(progressViewHandler != null){
                    progressViewHandler.sendEmptyMessage(REFRESH);
                }
            }
        });
        // 如果蓝牙已经开启
        if (mBluetoothOperator.isEnable()) {
            // 打开连接按钮
            switchButton.setChecked(true);
            // 开始扫描
            mBluetoothOperator.startScanDevice();
            // 获取当前连接着的设备
            BluetoothDevice curDevice = mBluetoothOperator.getBluetoothDevice();
            // 如果当前连接着的设备是空的，即没连接状态
            if (curDevice != null) {
                // 获取连接着的设备地址并赋给adapter
                adapter.lastConnectedAddress = curDevice.getAddress();
                // 为本数据集合添加数据
                canConnectDeviceMap.put(curDevice.getAddress(), curDevice);
                // 为adapter添加该设备
                adapter.addDevice(curDevice);
            }
        }
    }

    @Override
    public void onRefresh() {
        if (mBluetoothOperator != null) {
            mBluetoothOperator.startScanDevice();
        } else {
            progressViewHandler.sendEmptyMessage(REFRESH);
        }
    }

    @Override
    public void notifyDeviceConnectStatus(boolean isConnected, BluetoothDevice device) {
        Log.e("BluetoothListActivity", device.getAddress() + ":" + isConnected);
        if (isConnected) {
            // 设置已连接设备
            adapter.lastConnectedAddress = device.getAddress();
            hasConnected = device;
            // 设置已经连接状态
            progressViewHandler.sendEmptyMessage(DEVICE_CONNECTED);
            // 保存连接设备地址到配置文件,方便启动时读取并直接连接
            SettingTool.saveAddress(device.getAddress());
            // 连接成功，结束activity
            finish();
        } else {
            // 确保连接设备为null
            adapter.lastConnectedAddress = null;
            // 设置连接失败状态
            progressViewHandler.sendEmptyMessage(DEVICE_CONNECT_FAILED);
            // 清理devicemap中连接的设备信息
            canConnectDeviceMap.remove(device.getAddress());
            // 清理adapter中的设备信息
            adapter.clearConnectedDevice();

            hasConnected = null;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 每次发现新设备刷新列表
                    refreshListView();
                }
            });
        }

    }

    @Override
    public void notifyNewDevice(final BluetoothDevice device, int rssi) {
        Log.e("codelevex", "发现新设备：" + device.getAddress());
        // 如果可连接设备里包含里新设备，则只更新rssi,而不添加至adapter
        if (!canConnectDeviceMap.containsKey(device.getAddress())) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.addDevice(device);
                }
            });
            canConnectDeviceMap.put(device.getAddress(), device);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 每次发现新设备刷新列表
                refreshListView();
            }
        });

        // 通过设备地址，更新对应设备的rssi
        adapter.setRssi(device.getAddress(), rssi);
    }

    @Override
    public void handleViewBeforeStartRead() {
    }

    /**
     * 刷新列表
     */
    public void refreshListView() {
        adapter.notifyDataSetChanged();
        devices.invalidate();
    }

    @Override
    public void onItemClick(final BluetoothDevice device, View view) {
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mBluetoothOperator.connect(device)) {
                    progressViewHandler.sendEmptyMessage(DEVICE_CONNECT_FAILED);
                    return;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.clearDevices();
        mBluetoothOperator.stopScanDevice();
        progressViewHandler = null;
    }


}
