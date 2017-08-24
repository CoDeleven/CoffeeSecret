package com.dhy.coffeesecret.model.device_list;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import com.clj.fastble.data.ScanResult;
import com.dhy.coffeesecret.model.BaseBlePresenter;
import com.dhy.coffeesecret.ui.device.adapter.BluetoothListAdapter;
import com.dhy.coffeesecret.utils.SettingTool;

import static com.dhy.coffeesecret.ui.mine.BluetoothListActivity.CANCEL_REFRESH;
import static com.dhy.coffeesecret.ui.mine.BluetoothListActivity.DEVICE_CONNECTED;

/**
 * Created by CoDeleven on 17-8-2.
 */

public class Presenter4ScanList extends BaseBlePresenter<IScanListView, Model4ScanList> {
    private static final String TAG = Presenter4ScanList.class.getSimpleName();
    private static Presenter4ScanList mSelf;
    private BluetoothListAdapter mAdapter;

    public void setDeviceListAdapter(BluetoothListAdapter adapter){
        this.mAdapter = adapter;

        if(mBluetoothOperator.isConnected()){
            adapter.setConnectedDevice(mBluetoothOperator.getConnectedDevice());
        }else{
            mModelOperator.removeDevice(mBluetoothOperator.getLatestAddress());
        }
        adapter.setDevices(mModelOperator.listAllAddedDevices());
    }

    public Presenter4ScanList() {
        super(Model4ScanList.newInstance());
    }

    public static Presenter4ScanList newInstance() {
        if (mSelf == null) {
            mSelf = new Presenter4ScanList();
        }
        return mSelf;
    }


    @Override
    public void onScanning(ScanResult result) {
        BluetoothDevice device = result.getDevice();
        // 如果不含有该键的值时
        if (!mModelOperator.isExisted(device.getAddress())) {
            // 如果蓝牙设备的名字为空或者名字为null就过滤掉
            if(!(device.getName() == null || device.getName().equals("null"))){
                mModelOperator.addDevice(result);
                // 添加新的设备，没必要执行下面的更新rssi骚操作了，直接结束
                // mScanListView.addDevice2List(result);
                if(mAdapter != null){
                    mAdapter.addDevice(result);
                }
            }
            return;
        }
        // 更新对应设备的rssi
        getView().updateDeviceRssi(device, result.getRssi());

    }

    @Override
    public void toPreConnect() {

    }

    @Override
    public void onScanningComplete(ScanResult... results) {
        Log.d(TAG, "onScanningComplete -> 扫描完成");
        getView().updateText(CANCEL_REFRESH, null);
    }

    @Override
    public void toConnecting() {

    }

    @Override
    public void toDisconnected() {
        Log.e("BluetoothListActivity", "连接失败");
        //  设置连接失败状态
        getView().updateText(BluetoothProfile.STATE_DISCONNECTED, null);
        getView().showToast(BluetoothProfile.STATE_DISCONNECTED, "连接失败，请重新尝试...");
        // 断开连接时，移除已经连接的设备。
        mModelOperator.removeDevice(mBluetoothOperator.getLatestAddress());
        // 告诉Adapter失去连接了，让Adapter清理已经连接的设备
        mAdapter.missConnected();
    }

    @Override
    public void toDisconnecting() {
        toDisconnected();
    }

    @Override
    public void toConnected() {
        // 设置已经连接状态
        getView().updateText(DEVICE_CONNECTED, null);
        // 保存连接设备地址到配置文件,方便启动时读取并直接连接
        SettingTool.saveAddress(mBluetoothOperator.getLatestAddress());
        super.resetBluetoothListener();
        // 连接成功后结束并跳转回首页
        getView().finishActivity();

    }

    public boolean isEnable() {
        return super.mBluetoothOperator.isEnable();
    }

    public void startScan() {
        super.mBluetoothOperator.startScanDevice();
    }

    public void stopScan(){
        super.mBluetoothOperator.stopScanDevice();
    }

    public void disableBluetooth(){
        super.mBluetoothOperator.disableBle();
    }

    public void enableBluetooth(){
        super.mBluetoothOperator.enableBle();
    }

    public boolean connect(ScanResult result){
        // 判断是否是相等的设备，如果是相等的设备不连接，直接返回true
        BluetoothDevice curConnectedDevice = mBluetoothOperator.getConnectedDevice();
        if(curConnectedDevice != null && result.getDevice().getAddress().equals(curConnectedDevice.getAddress())){
            return true;
        }
        return super.mBluetoothOperator.connect(result);
    }

    @Override
    public void toDisable() {
        mModelOperator.clearAllAddedDevices();
        mBluetoothOperator.stopScanDevice();
        getView().updateText(CANCEL_REFRESH, null);
    }

}
