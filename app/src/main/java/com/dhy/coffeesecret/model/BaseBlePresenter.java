package com.dhy.coffeesecret.model;

import android.bluetooth.BluetoothDevice;

import com.dhy.coffeesecret.pojo.BakeReportProxy;
import com.dhy.coffeesecret.pojo.Temperature;
import com.dhy.coffeesecret.services.IBleConnectionCallback;
import com.dhy.coffeesecret.services.IBleScanCallback;
import com.dhy.coffeesecret.services.IBleTemperatureCallback;
import com.dhy.coffeesecret.services.IBluetoothOperator;

/**
 * Created by CoDeleven on 17-8-2.
 */

public abstract class BaseBlePresenter implements IBasePresenter, IBleScanCallback, IBleConnectionCallback, IBleTemperatureCallback {
    protected static IBluetoothOperator mBluetoothOperator;
    protected static BakeReportProxy bakeReportProxy;
    protected static IBaseView viewOperator;
    protected static IBaseModel modelOperator;
    /**
     * 初始化蓝牙操作助手的监听
     *
     * 请将view设置在此方法之前
     */
    public void initBluetoothListener() {
        // 如果蓝牙操作助手不为空时，更改监听器
        if (BaseBlePresenter.mBluetoothOperator != null) {
            // 重新设置回调接口到本对象
            BaseBlePresenter.mBluetoothOperator.setTemperatureListener(this);
            BaseBlePresenter.mBluetoothOperator.setScanCallbackListener(this);
            BaseBlePresenter.mBluetoothOperator.setConnectionListener(this);
            // 如果初始化的时候处于连接状态那么设置文本为已连接
            if(BaseBlePresenter.mBluetoothOperator.isConnected()){
                viewOperator.updateText(0, "");
            }
        }
    }
    public void destroyBluetoothListener() {
        mBluetoothOperator.setTemperatureListener(null);
        mBluetoothOperator.setScanCallbackListener(null);
    }

    public void clearBakeReportProxy(){
        bakeReportProxy = null;
    }

    public BakeReportProxy getBakeReportProxy() {
        return bakeReportProxy;
    }

    @Override
    public void onScanning(BluetoothDevice bluetoothDevice, int rssi) {

    }

    @Override
    public void notifyTemperature(Temperature temperature) {

    }

    @Override
    public void toPreConnect(int status) {

    }

    @Override
    public void onScanningComplete(BluetoothDevice... bluetoothDevice) {

    }

    @Override
    public void toConnecting(int status) {

    }

    @Override
    public void toConnected(int status) {

    }

    @Override
    public void toDisconnected(int status) {

    }

    @Override
    public void toDisconnecting(int status) {

    }
}
