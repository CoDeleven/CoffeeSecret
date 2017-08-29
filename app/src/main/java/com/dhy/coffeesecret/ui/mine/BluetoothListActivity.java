package com.dhy.coffeesecret.ui.mine;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
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

import com.clj.fastble.data.ScanResult;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.model.device_list.IScanListView;
import com.dhy.coffeesecret.model.device_list.Presenter4ScanList;
import com.dhy.coffeesecret.services.BluetoothService;
import com.dhy.coffeesecret.ui.bake.adapter.BluetoothListAdapter;
import com.dhy.coffeesecret.utils.T;
import com.kyleduo.switchbutton.SwitchButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothListActivity extends AppCompatActivity implements BluetoothListAdapter.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener, BluetoothService.ViewControllerListener, SwipeRefreshLayout.OnRefreshListener,
        IScanListView {
    public static final int DEVICE_CONNECTED = 2, CANCEL_REFRESH = -1, REFRESH = 0x77;
    private static final String TAG = BluetoothListActivity.class.getSimpleName();
    @Bind(R.id.srl)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.id_connecting_bluetooth_list)
    RecyclerView mDevicesList;
    @Bind(R.id.id_bluetooth_switch)
    SwitchButton switchButton;
    @Bind(R.id.id_back)
    ImageView back;
    @Bind(R.id.id_scan_progress)
    ProgressBar mScanProgress;

    View mCurConnectingView;
    ImageView tick = null;
    private ProgressBar progressCircle = null;
    private BluetoothListAdapter mAdapter;
    private Presenter4ScanList mPresenter;
    private Handler toastHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothProfile.STATE_DISCONNECTED:
                    T.showShort(getApplicationContext(), (String) msg.obj);
                    break;
            }
        }
    };
    private Handler mTextHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CANCEL_REFRESH:
                    refreshLayout.setRefreshing(false);
                    mScanProgress.setVisibility(View.GONE);
                    break;
                case REFRESH:
                    mScanProgress.setVisibility(View.VISIBLE);
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    if(progressCircle != null){
                        progressCircle.setVisibility(View.GONE);
                    }
                    if(tick != null){
                        tick.setVisibility(View.VISIBLE);
                    }

                    mCurConnectingView.setEnabled(true);
                    break;
                case BluetoothProfile.STATE_CONNECTING:
                    if(progressCircle != null){
                        progressCircle.setVisibility(View.VISIBLE);
                    }
                    if(tick != null){
                        tick.setVisibility(View.GONE);
                    }
                    mCurConnectingView.setEnabled(false);
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:

                    if(progressCircle != null){
                        progressCircle.setVisibility(View.INVISIBLE);
                    }
                    if(tick != null){
                        tick.setVisibility(View.GONE);
                    }
                    mCurConnectingView.setEnabled(true);
                    break;
            }
        }
    };

    @Override
    public void showWarnDialog(int index) {

    }

    /**
     * 刷新列表
     */
    @Override
    public void updateDeviceList() {
        mAdapter.notifyDataSetChanged();
        mDevicesList.invalidate();
    }

    @Override
    public void addDevice2List(ScanResult device) {
        mAdapter.addDevice(device);
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
            // 因为启动蓝牙需要一段时间，先设置为不可点击
            switchButton.setEnabled(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!mPresenter.isEnable()) {

                    }
                    mPresenter.startScan();
                    mTextHandler.sendEmptyMessage(REFRESH);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 现在成功启动了，开启按钮
                            switchButton.setEnabled(true);
                        }
                    });
                }
            }).start();

        } else {
            // 关闭蓝牙后清空adapter内的设备
            // mAdapter.clearDevices();
            // 清空可连接设备
            // canConnectDeviceMap.clear();

            // 刷新列表
            // mAdapter.notifyDataSetChanged();
            // 关闭蓝牙初始化状态
            // mAdapter.lastConnectedAddress = null;
            // 请求关闭蓝牙设备
            // mBluetoothOperator.disableBluetooth();
            // 关闭蓝牙设备后清空已连接的设备状态
            // hasConnected = null;
            mPresenter.disableBluetooth();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mPresenter.isEnable()) {

                    }
                    mPresenter.startScan();
                    // mTextHandler.sendEmptyMessage(REFRESH);
                    // mTextHandler.sendEmptyMessage(CANCEL_REFRESH);
                    mTextHandler.sendEmptyMessage(BluetoothProfile.STATE_DISCONNECTED);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 现在成功启动了，开启按钮
                            switchButton.setEnabled(true);
                        }
                    });
                }
            }).start();
            mAdapter.clearDevices();
            updateDeviceList();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_list);
        ButterKnife.bind(this);

        // 获取蓝牙操作对象
        if (mPresenter == null) {
            // 获取helper实例
            mPresenter = Presenter4ScanList.newInstance();
        }
        // 设置列表
        mAdapter = new BluetoothListAdapter(this);
        mDevicesList.setLayoutManager(new LinearLayoutManager(this));
        mDevicesList.setAdapter(mAdapter);

        mPresenter.setDeviceListAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        mPresenter.setView(this);


        // 初始化监听器到本presenter
        mPresenter.initBluetoothListener();
        // 设置发现新设备进行回调的对象
        // TODO 视图更新
        // mBluetoothOperator.setDeviceChangedListener(this);
        // 设置视图更改器，用于当蓝牙连接成功时，设置勾给相应的条目
        // mBluetoothOperator.setViewControllerListener(this);
        // 如果蓝牙已经开启
        if (mPresenter.isEnable()) {
            // 打开连接按钮
            switchButton.setChecked(true);
            // 开始扫描
            mPresenter.startScan();
            // 设置进度条可见
            mScanProgress.setVisibility(View.VISIBLE);
        } else {
            // 打开连接按钮
            switchButton.setChecked(false);
            mAdapter.clearDevices();
        }
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        switchButton.setOnCheckedChangeListener(this);
    }

    @Override
    public void onRefresh() {
        if (mPresenter != null) {
            // mPresenter.clearAddedDevices();
            // mAdapter.notifyDataSetChanged();
            mPresenter.startScan();
            // mDevicesList.invalidate();
        } else {
            mTextHandler.sendEmptyMessage(CANCEL_REFRESH);
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void updateText(int index, Object updateContent) {
        if (mTextHandler != null) {
            if (index == BluetoothProfile.STATE_DISCONNECTED || index == BluetoothProfile.STATE_CONNECTED || index == BluetoothProfile.STATE_CONNECTING) {
                if (tick == null && progressCircle == null) {
                    return;
                }
            }
            mTextHandler.sendEmptyMessage(index);
        }
    }

    @Override
    public void showToast(int index, String toastContent) {
        Message msg = new Message();
        msg.what = index;
        msg.obj = toastContent;
        toastHandler.sendMessage(msg);

    }

    @Override
    public void updateDeviceRssi(BluetoothDevice device, int rssi) {
        // 通过设备地址，更新对应设备的rssi
        mAdapter.setRssi(device.getAddress(), rssi);
        updateDeviceList();
    }

    @Override
    public void handleViewBeforeStartRead() {
    }

    @Override
    public void onItemClick(final ScanResult device, View view) {
        // 如果之前存在一个设备在连接,设置上一个视图隐藏
        if (progressCircle != null && progressCircle.isShown()) {
            progressCircle.setVisibility(View.GONE);
        }

        // 更新progressCircle和tick两个view的引用
        progressCircle = (ProgressBar) view.findViewById(R.id.circle_progress);
        // progressCircle.setVisibility(View.VISIBLE);
        tick = (ImageView) view.findViewById(R.id.id_bluetooth_list_right);
        // 设置当前正在连接的视图
        mCurConnectingView = view;
        // 当前状态是处于正在连接的状态
        mTextHandler.sendEmptyMessage(BluetoothProfile.STATE_CONNECTING);
        // 开始正式请求连接,因为连接是异步回调，不是及时消息,如果此时false，一定不能连接成功
        mPresenter.connect(device);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.clearDevices();
        // mPresenter.clearScanedDevices();
        mPresenter.stopScan();
        // 因为destroy不能及时执行，所以这里去掉了
        // mPresenter.resetBluetoothListener();
        // mTextHandler = null;
        mCurConnectingView = null;
    }

}
