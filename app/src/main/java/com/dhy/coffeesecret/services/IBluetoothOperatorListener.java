package com.dhy.coffeesecret.services;

/**
 * Created by CoDeleven on 17-8-2.
 */

public interface IBluetoothOperatorListener {
    void setTemperatureListener(IBleTemperatureCallback temperatureListener);
    void setScanCallbackListener(IBleScanCallback scanCallbackListener);
    void setConnectionListener(IBleConnectionCallback connectionListener);
}
