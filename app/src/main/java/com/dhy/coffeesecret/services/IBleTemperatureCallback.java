package com.dhy.coffeesecret.services;

import com.dhy.coffeesecret.pojo.Temperature;

/**
 * Created by CoDeleven on 17-8-2.
 */

public interface IBleTemperatureCallback {
    void notifyTemperature(Temperature temperature);
}
