package com.dhy.coffeesecret.model.device;

import com.dhy.coffeesecret.pojo.Temperature;

/**
 * Created by CoDeleven on 17-8-1.
 */

public interface IDeviceModel{
    void updateBeginTemperature(Temperature temperature);
    void updateLastConnectedAdd(String lastAddress);
    String getLastConnectedAddr();
}
