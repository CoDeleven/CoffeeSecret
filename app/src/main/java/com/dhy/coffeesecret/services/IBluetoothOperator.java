package com.dhy.coffeesecret.services;

import android.bluetooth.BluetoothDevice;

/**
 * Created by CoDeleven on 17-8-2.
 */

public interface IBluetoothOperator extends IBluetoothOperatorListener{
    boolean isConnected();

    BluetoothDevice getConnectedDevice();

    boolean isEnable();

    void enable();

    void startScanDevice();

    void stopScanDevice();

    boolean connect(BluetoothDevice device);

    boolean connect(String address);
    void closeBluetooth();
}
