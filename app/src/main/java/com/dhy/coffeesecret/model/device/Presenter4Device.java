package com.dhy.coffeesecret.model.device;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.clj.fastble.data.ScanResult;
import com.dhy.coffeesecret.model.BaseBlePresenter;
import com.dhy.coffeesecret.model.IBaseView;
import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.Temperature;
import com.dhy.coffeesecret.services.interfaces.IBluetoothOperator;
import com.dhy.coffeesecret.ui.device.DeviceFragment;

/**
 * Created by CoDeleven on 17-8-1.
 */

public class Presenter4Device extends BaseBlePresenter {
    private static final String TAG = Presenter4Device.class.getSimpleName();

    private static Presenter4Device mPresenter;
    private boolean isStart = false;

    private Presenter4Device() {
        // 初始化model
        super.mModelOperator = Model4Device.newInstance();
    }

    /**
     * 只能在DeviceFragment重新进行bakeReport的生成
     */
    public void initBakeReport(){
        super.mCurBakingProxy = new BakeReportProxy();
    }

    /**
     * 第一次调用必须调用该方法
     *
     * @param bluetoothOperator
     * @return
     */
    public static Presenter4Device newInstance(IBluetoothOperator bluetoothOperator) {
        if (BaseBlePresenter.mBluetoothOperator == null) {
            mPresenter = new Presenter4Device();
            BaseBlePresenter.mBluetoothOperator = bluetoothOperator;
        }
        return mPresenter;
    }

    @Override
    public void setView(IBaseView baseView) {
        super.setView(baseView);
        mViewOperator = baseView;
    }

    @Override
    public void notifyTemperature(Temperature temperature) {
        Log.d(TAG, "presenter4device -> notifyTemperature: 收到温度信息：" + temperature);
        if (!isStart) {
            ((Model4Device) mModelOperator).updateBeginTemperature(temperature);
            // beginTemp = temperature.getBeanTemp();
            isStart = true;
        }
        mViewOperator.updateText(DeviceFragment.UPDATE_TEMPERATURE_TEXT, temperature);
    }


    @Override
    public void onScanning(ScanResult result) {
        if (result.getDevice().getAddress().equals(((Model4Device) mModelOperator).getLastConnectedAddr())) {
            mViewOperator.showToast(DeviceFragment.AUTO_CONNECTION_TIPS, "正在自动重连...");
            // 连接蓝牙设备
            BaseBlePresenter.mBluetoothOperator.connect(result);
            // 停止扫描
            BaseBlePresenter.mBluetoothOperator.stopScanDevice();
        }
    }

    @Override
    public void toConnected() {
        // 更新连接状态
        mViewOperator.updateText(0, "");
    }

    @Override
    public void toDisconnected() {
        // 用于父类做一些基础的判断
        super.toDisconnected();
        // 更新连接状态为未连接
        mViewOperator.updateText(1, "");
    }

    @Override
    public void toDisconnecting() {
        toDisconnected();
    }

    public boolean isConnected() {
        return BaseBlePresenter.mBluetoothOperator.isConnected();
    }

    public BluetoothDevice getConnectedDevice() {
        return BaseBlePresenter.mBluetoothOperator.getConnectedDevice();
    }

    public boolean isEnable(){
        return BaseBlePresenter.mBluetoothOperator.isEnable();
    }
    public void enableBluetooth(){
        BaseBlePresenter.mBluetoothOperator.enableBle();
    }
    public void startScan(){
        BaseBlePresenter.mBluetoothOperator.startScanDevice();
    }
    public void disableBluetooth(){
        // BaseBlePresenter.mBluetoothOperator.
    }
}
