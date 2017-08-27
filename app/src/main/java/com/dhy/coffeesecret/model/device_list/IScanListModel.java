package com.dhy.coffeesecret.model.device_list;


import android.bluetooth.BluetoothDevice;

import com.clj.fastble.data.ScanResult;

import java.util.List;

/**
 * Created by CoDeleven on 17-8-2.
 */

public interface IScanListModel{
    void addDevice(ScanResult scanResult);
    void removeDevice(String deviceAddress);
    void removeDevice(BluetoothDevice device);
    boolean isExisted(String address);
    void clearAllAddedDevices();
    List<ScanResult> listAllAddedDevices();
}
