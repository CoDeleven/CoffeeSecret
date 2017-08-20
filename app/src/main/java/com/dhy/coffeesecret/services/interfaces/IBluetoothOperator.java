package com.dhy.coffeesecret.services.interfaces;

import android.bluetooth.BluetoothDevice;
import com.clj.fastble.data.ScanResult;

/**
 * Created by CoDeleven on 17-8-2.
 */

public interface IBluetoothOperator extends IBleListenerAware {
    boolean isConnected();

    BluetoothDevice getConnectedDevice();

    boolean isEnable();

    void enableBle();

    void startScanDevice();

    void stopScanDevice();

    boolean connect(ScanResult device);

    boolean connect(String address);

    String getLatestAddress();

    void disableBle();


}
