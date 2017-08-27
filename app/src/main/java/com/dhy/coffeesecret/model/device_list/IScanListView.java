package com.dhy.coffeesecret.model.device_list;

import android.bluetooth.BluetoothDevice;

import com.clj.fastble.data.ScanResult;
import com.dhy.coffeesecret.model.base.IBaseView;

/**
 * Created by CoDeleven on 17-8-2.
 */

public interface IScanListView extends IBaseView{
    void updateDeviceList();
    void addDevice2List(ScanResult device);
    void updateDeviceRssi(BluetoothDevice device, int rssi);
    void finishActivity();

}
