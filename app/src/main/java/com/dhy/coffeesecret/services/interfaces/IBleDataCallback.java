package com.dhy.coffeesecret.services.interfaces;

import com.dhy.coffeesecret.pojo.Temperature;

/**
 * Created by CoDeleven on 17-8-2.
 */

public interface IBleDataCallback {
    void notifyTemperature(Temperature temperature);
}
