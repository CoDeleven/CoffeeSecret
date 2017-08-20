package com.dhy.coffeesecret.services.interfaces;

/**
 * Created by CoDeleven on 17-8-2.
 */

public interface IBleListenerAware {
    void setTemperatureListener(IBleDataCallback temperatureListener);
    void setScanCallbackListener(IBleScanCallback scanCallbackListener);
    void setConnectionListener(IBleConnCallback connectionListener);
}
