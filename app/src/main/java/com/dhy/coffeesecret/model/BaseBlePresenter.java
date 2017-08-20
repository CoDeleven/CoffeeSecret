package com.dhy.coffeesecret.model;

import android.bluetooth.BluetoothProfile;

import com.clj.fastble.data.ScanResult;

import com.dhy.coffeesecret.pojo.Temperature;
import com.dhy.coffeesecret.services.interfaces.IBleConnCallback;
import com.dhy.coffeesecret.services.interfaces.IBleDataCallback;
import com.dhy.coffeesecret.services.interfaces.IBleScanCallback;
import com.dhy.coffeesecret.services.interfaces.IBluetoothOperator;

/**
 * Created by CoDeleven on 17-8-2.
 */

public abstract class BaseBlePresenter extends BasePresenter implements IBasePresenter, IBleScanCallback, IBleConnCallback, IBleDataCallback {
    protected static IBluetoothOperator mBluetoothOperator;
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
                mViewOperator.updateText(BluetoothProfile.STATE_CONNECTED, "");
            }else{
                mViewOperator.updateText(BluetoothProfile.STATE_DISCONNECTED, "");
            }
        }
    }
    public void resetBluetoothListener() {
        mBluetoothOperator.setTemperatureListener(null);
        mBluetoothOperator.setScanCallbackListener(null);
    }

    @Override
    public void onScanning(ScanResult result) {

    }

    @Override
    public void notifyTemperature(Temperature temperature) {

    }

    @Override
    public void toPreConnect() {

    }

    @Override
    public void onScanningComplete(ScanResult... results) {

    }

    @Override
    public void toConnecting() {

    }

    @Override
    public void toConnected() {

    }

    @Override
    public void toDisconnected() {
    }

    @Override
    public void toDisconnecting() {

    }

    public boolean isConnected(){
        return mBluetoothOperator.isConnected();
    }

    @Override
    public void toDisable() {

    }
}
