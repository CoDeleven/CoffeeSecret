package com.dhy.coffeesecret.model.device;

import com.dhy.coffeesecret.model.base.IBaseView;
import com.dhy.coffeesecret.pojo.BakeReport;

/**
 * Created by CoDeleven on 17-8-1.
 * DeviceFragment的view操作
 */

public interface IDeviceView extends IBaseView{
    void goNextActivity(BakeReport referTemperatures);
}
