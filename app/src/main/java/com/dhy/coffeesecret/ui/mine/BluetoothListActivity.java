package com.dhy.coffeesecret.ui.mine;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.model.device_list.IScanListView;
import com.dhy.coffeesecret.model.device_list.Presenter4ScanList;
import com.dhy.coffeesecret.services.BluetoothService;
import com.dhy.coffeesecret.ui.device.adapter.BluetoothListAdapter;
import com.kyleduo.switchbutton.SwitchButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothListActivity extends AppCompatActivity implements BluetoothListAdapter.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener, BluetoothService.ViewControllerListener, SwipeRefreshLayout.OnRefreshListener,
        IScanListView {
    public static final int DEVICE_CONNECTING = 0, DEVICE_CONNECT_FAILED = 1, DEVICE_CONNECTED = 2, REFRESH = -1, SCANNING_COMPLETE = -2;
    @Bind(R.id.srl)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.id_connecting_bluetooth_list)
    RecyclerView devices;
    @Bind(R.id.id_bluetooth_switch)
    SwitchButton switchButton;
    @Bind(R.id.id_back)
    ImageView back;
    @Bind(R.id.id_scan_progress)
    ProgressBar mScanProgress;


    ImageView tick = null;
    // private Map<String, BluetoothDevice> canConnectDeviceMap = new HashMap<>();
    // private BluetoothService.BluetoothOperator mBluetoothOperator;
    private ProgressBar progressCircle = null;
    private BluetoothListAdapter adapter;
    // private BluetoothDevice hasConnected;
    private Presenter4ScanList mPresenter;
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
            switch (msg.what){
                case REFRESH:
                    refreshLayout.setRefreshing(false);
                    mScanProgress.setVisibility(View.GONE);
                    break;
                case SCANNING_COMPLETE:
                    break;
            }
            return false;
        }
    });

    /**
     * 刷新列表
     */
    @Override
    public void updateDeviceList() {
        adapter.notifyDataSetChanged();
        devices.invalidate();
    }

    @Override
    public void addDevice2List(BluetoothDevice device, int rssi) {
        adapter.addDevice(device);
        updateDeviceList();
    }

    @OnClick(R.id.id_back)
    public void back() {
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!mPresenter.isEnable() && isChecked) {
            mPresenter.enableBluetooth();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!mPresenter.isEnable()) {
                        mPresenter.enableBluetooth();
                        try {
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mPresenter.startScan();
                }
            }).start();
        } else {
            // 关闭蓝牙后清空adapter内的设备
            // adapter.clearDevices();
            // 清空可连接设备
            // canConnectDeviceMap.clear();

            // 刷新列表
            // adapter.notifyDataSetChanged();
            devices.invalidate();
            // 关闭蓝牙初始化状态
            // adapter.lastConnectedAddress = null;
            // 请求关闭蓝牙设备
            // mBluetoothOperator.disableBluetooth();
            // 关闭蓝牙设备后清空已连接的设备状态
            // hasConnected = null;
            mPresenter.disableBluetooth();
            updateDeviceList();
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
        if (mPresenter == null) {
            // 获取helper实例
            mPresenter = Presenter4ScanList.newInstance();
        }
        mPresenter.setView(this);
        // 初始化监听器到本presenter
        mPresenter.initBluetoothListener();
        // 设置发现新设备进行回调的对象
        // TODO 视图更新
        // mBluetoothOperator.setDeviceChangedListener(this);
        // 设置视图更改器，用于当蓝牙连接成功时，设置勾给相应的条目
        // mBluetoothOperator.setViewControllerListener(this);
        /*mBluetoothOperator.setScanListener(new BluetoothService.ScanListener(){
            @Override
            protected void onScanStop() {
                if(progressViewHandler != null){
                    progressViewHandler.sendEmptyMessage(REFRESH);
                }
            }
        });*/
        // 如果蓝牙已经开启
        if (mPresenter.isEnable()) {
            // 打开连接按钮
            switchButton.setChecked(true);
            // 开始扫描
            mPresenter.startScan();
            // 获取当前连接着的设备
            BluetoothDevice curDevice = mPresenter.getConnectedDevice();
            // 如果当前连接着的设备是空的，即没连接状态
            if (curDevice != null) {
                // 获取连接着的设备地址并赋给adapter
                adapter.lastConnectedAddress = curDevice.getAddress();
                // 为本数据集合添加数据
                // canConnectDeviceMap.put(curDevice.getAddress(), curDevice);
                // 为adapter添加该设备
                adapter.addDevice(curDevice);
            }
        }
    }

    @Override
    public void onRefresh() {
        if (mPresenter != null) {
            mPresenter.startScan();
        } else {
            progressViewHandler.sendEmptyMessage(REFRESH);
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void updateText(int index, Object updateContent) {
        if(progressViewHandler != null){
            progressViewHandler.sendEmptyMessage(index);
        }
    }

    @Override
    public void showToast(int index, String toastContent) {

    }

    @Override
    public void updateDeviceRssi(BluetoothDevice device, int rssi) {
        // 通过设备地址，更新对应设备的rssi
        adapter.setRssi(device.getAddress(), rssi);
        updateDeviceList();
    }

    @Override
    public void handleViewBeforeStartRead() {
    }

    @Override
    public void onItemClick(final BluetoothDevice device, View view) {
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
                if (!mPresenter.connect(device)) {
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
        mPresenter.clearScanedDevices();
        mPresenter.stopScan();
        // 因为destroy不能及时执行，所以这里去掉了
        // mPresenter.destroyBluetoothListener();
        progressViewHandler = null;
    }


}
