package com.dhy.coffeesecret.model.device_list;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.dhy.coffeesecret.model.BaseBlePresenter;
import com.dhy.coffeesecret.model.IBaseView;
import com.dhy.coffeesecret.model.device.IDeviceModel;
import com.dhy.coffeesecret.utils.SettingTool;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.dhy.coffeesecret.ui.mine.BluetoothListActivity.DEVICE_CONNECTED;
import static com.dhy.coffeesecret.ui.mine.BluetoothListActivity.DEVICE_CONNECT_FAILED;
import static com.dhy.coffeesecret.ui.mine.BluetoothListActivity.REFRESH;

/**
 * Created by CoDeleven on 17-8-2.
 */

public class Presenter4ScanList extends BaseBlePresenter {
    private static final String TAG = Presenter4ScanList.class.getSimpleName();
    private static Presenter4ScanList mSelf;
    private IScanListView mScanListView;
    private IDeviceModel mDeviceModel;
    private Map<String, BluetoothDevice> mBeScanedDevice = new LinkedHashMap<>();

    public static Presenter4ScanList newInstance() {
        if (mSelf == null) {
            mSelf = new Presenter4ScanList();
        }
        return mSelf;
    }

    @Override
    public void setView(IBaseView baseView) {
        mScanListView = (IScanListView) baseView;
    }

    @Override
    public void onScanning(BluetoothDevice bluetoothDevice, int rssi) {
        String deviceAddr = bluetoothDevice.getAddress();
        // 如果不含有该键的值时
        if (!mBeScanedDevice.containsKey(deviceAddr)) {
            // 如果蓝牙设备的名字为空或者名字为null就过滤掉
            if(!(bluetoothDevice.getName() == null || bluetoothDevice.getName().equals("null"))){
                mBeScanedDevice.put(deviceAddr, bluetoothDevice);
                // 添加新的设备，没必要执行下面的骚操作了，直接结束
                mScanListView.addDevice2List(bluetoothDevice, rssi);
            }
            return;
        }
        // 更新对应设备的rssi
        mScanListView.updateDeviceRssi(bluetoothDevice, rssi);
    }

    @Override
    public void toPreConnect(int status) {

    }

    @Override
    public void onScanningComplete(BluetoothDevice... bluetoothDevice) {
        Log.d(TAG, "onScanningComplete -> 扫描完成");
        mScanListView.updateText(REFRESH, null);
    }

    @Override
    public void toConnecting(int status) {

    }

    @Override
    public void toDisconnected(int status) {
        Log.e("BluetoothListActivity", "连接失败");
        //  设置连接失败状态
        mScanListView.updateText(DEVICE_CONNECT_FAILED, null);
    }

    @Override
    public void toDisconnecting(int status) {
        toDisconnected(status);
    }

    @Override
    public void toConnected(int status) {
        // 设置已经连接状态
        mScanListView.updateText(DEVICE_CONNECTED, null);
        // 设置已连接设备
        String connectedAddr = mBluetoothOperator.getConnectedDevice().getAddress();
        // adapter.lastConnectedAddress = device.getAddress();
        // 保存连接设备地址到配置文件,方便启动时读取并直接连接
        SettingTool.saveAddress(connectedAddr);
        // 连接成功后结束并跳转回首页
        mScanListView.finishActivity();
        super.resetBluetoothListener();
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

    public BluetoothDevice getConnectedDevice() {
        return super.mBluetoothOperator.getConnectedDevice();
    }

    public void disableBluetooth(){
        super.mBluetoothOperator.closeBluetooth();
    }

    public void enableBluetooth(){
        super.mBluetoothOperator.enable();
    }

    public boolean connect(BluetoothDevice device){
        // 判断是否是相等的设备，如果是相等的设备不连接，直接返回true
        BluetoothDevice curConnectedDevice = mBluetoothOperator.getConnectedDevice();
        if(curConnectedDevice != null && device.getAddress().equals(curConnectedDevice.getAddress())){
            return true;
        }
        return super.mBluetoothOperator.connect(device);
    }

    public void clearScanedDevices(){
        mBeScanedDevice.clear();
    }
}
