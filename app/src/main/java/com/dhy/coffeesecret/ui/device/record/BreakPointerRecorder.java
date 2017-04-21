package com.dhy.coffeesecret.ui.device.record;

import com.dhy.coffeesecret.pojo.Temprature;

/**
 * Created by CoDeleven on 17-4-21.
 */

public class BreakPointerRecorder extends AbstractTimeSystem {
    private Temprature lastTemprature;

    public void record(Temprature temprature){
        if(this.lastTemprature == null || lastTemprature.getBeanTemp() > temprature.getBeanTemp()){
            this.lastTemprature = temprature;
        }
    }

}
