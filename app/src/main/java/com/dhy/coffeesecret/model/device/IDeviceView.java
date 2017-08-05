package com.dhy.coffeesecret.model.device;

import com.dhy.coffeesecret.model.IBaseView;
import com.dhy.coffeesecret.pojo.Temperature;

/**
 * Created by CoDeleven on 17-8-1.
 * DeviceFragment的view操作
 */

public interface IDeviceView extends IBaseView{
    void updateTemperatureView(Temperature temperature);
}
