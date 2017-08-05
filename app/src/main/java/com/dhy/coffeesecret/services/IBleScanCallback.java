package com.dhy.coffeesecret.services;

import android.bluetooth.BluetoothDevice;

/**
 * Created by CoDeleven on 17-8-2.
 */

public interface IBleScanCallback {
    void onScanning(BluetoothDevice bluetoothDevice, int rssi);
    void onScanningComplete(BluetoothDevice... bluetoothDevice);
}
